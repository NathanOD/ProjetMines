package fr.sos.projetmines.calculator.rpc;

import fr.sos.projetmines.CurvePoint;
import fr.sos.projetmines.FrictionCoefficient;
import fr.sos.projetmines.OrowanLiveDataProviderGrpc;
import fr.sos.projetmines.StandIdentifier;
import fr.sos.projetmines.calculator.OrowanCalculator;
import fr.sos.projetmines.calculator.model.OrowanDataOutput;
import fr.sos.projetmines.commonutils.event.OEventListener;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class OrowanLiveDataService extends OrowanLiveDataProviderGrpc.OrowanLiveDataProviderImplBase
        implements OEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanLiveDataService.class);

    /**
     * Map containing the clients subscribed to receive CurvePoint data for each stand ID.
     */
    private final Map<Integer, Set<StreamObserver<CurvePoint>>> curveDataClients;

    /**
     * Map containing the clients subscribed to receive average friction data for each stand ID.
     */
    private final Map<Integer, Set<StreamObserver<FrictionCoefficient>>> averageFrictionClients;
    /**
     * Map containing the friction coefficient data for each stand ID.
     */
    private final Map<Integer, Set<FrictionCoefficient>> frictions;

    /**
     * Initializes a new instance of the OrowanLiveDataService class.
     * Registers this instance as a listener for Orowan data input changes.
     */
    public OrowanLiveDataService() {
        OrowanCalculator.getInstance().getDataInputBroadcaster().registerListener(this);
        curveDataClients = new HashMap<>();
        averageFrictionClients = new HashMap<>();
        frictions = new HashMap<>();
    }

    /**
     * Streams the CurvePoint data to clients subscribed to receive data for the specified stand ID.
     *
     * @param request          the StandIdentifier instance containing the stand ID to receive data for
     * @param responseObserver the StreamObserver instance to receive CurvePoint data
     */
    @Override
    public void curvesData(StandIdentifier request, StreamObserver<CurvePoint> responseObserver) {
        int standId = request.getStandId();
        if (!curveDataClients.containsKey(standId)) {
            curveDataClients.put(standId, new HashSet<>());
        }
        curveDataClients.get(standId).add(responseObserver);
        LOGGER.info("A new client will receive data for the stand {}!", standId);
    }

    /**
     * Streams the average friction coefficient data to clients subscribed to receive data for the specified stand ID.
     *
     * @param request          the StandIdentifier instance containing the stand ID to receive data for
     * @param responseObserver the StreamObserver instance to receive FrictionCoefficient data
     */
    @Override
    public void averageFrictionCoefficient(StandIdentifier request, StreamObserver<FrictionCoefficient> responseObserver) {
        int standId = request.getStandId();
        if (!averageFrictionClients.containsKey(standId)) {
            averageFrictionClients.put(standId, new HashSet<>());
            if (!frictions.containsKey(standId)) {
                frictions.put(standId, new HashSet<>());
            }
        }
        averageFrictionClients.get(standId).add(responseObserver);
        LOGGER.info("A new client will receive average friction for the stand {}!", standId);
    }

    /**
     * Event handler for Orowan data input changes. Sends the data to all clients subscribed to receive data for the
     * corresponding stand ID.
     *
     * @param eventData a Map instance containing the stand ID and output data
     */
    @Override
    public void onEvent(Map<String, ?> eventData) {
        int standId = (int) eventData.get("standId");
        OrowanDataOutput output = (OrowanDataOutput) eventData.get("output");
        // Check for stand clients existence
        if (curveDataClients.containsKey(standId)) {
            sendCurvePoint(output, standId);
        }
        if (averageFrictionClients.containsKey(standId)) {
            sendAverageFrictionCoefficient(output, standId);
        }
    }

    /**
     * Sends a {@link CurvePoint} message to all registered clients for the given stand.
     *
     * @param output  the {@link OrowanDataOutput} object containing the computed data to be sent
     * @param standId the ID of the stand associated with the data
     */
    private void sendCurvePoint(OrowanDataOutput output, int standId) {
        //For each client clients
        CurvePoint point = CurvePoint.newBuilder()
                .setComputationTime(output.getComputationTime())
                .setSigma(output.getSigmaMoy())
                .setFrictionCoefficient(output.getFriction())
                .setRollSpeed(output.getRollSpeed())
                .setXTime(output.getXTime())
                .build();

        Iterator<StreamObserver<CurvePoint>> curveClients = curveDataClients.get(standId).iterator();

        while (curveClients.hasNext()) {
            StreamObserver<CurvePoint> curveDataClient = curveClients.next();
            try {
                curveDataClient.onNext(point);
            } catch (StatusRuntimeException exception) {
                curveClients.remove();
            }
        }
    }

    /**
     * Sends an average friction coefficient value to all registered clients for the given stand
     * once 5 friction coefficients have been computed.
     *
     * @param outputData the {@link OrowanDataOutput} object containing the computed data to be sent
     * @param standId    the ID of the stand associated with the data
     */
    private void sendAverageFrictionCoefficient(OrowanDataOutput outputData, int standId) {
        FrictionCoefficient frictionCoeff = FrictionCoefficient.newBuilder()
                .setFrictionCoefficient(outputData.getFriction())
                .setXTime(outputData.getXTime())
                .build();
        Set<FrictionCoefficient> coefficients = frictions.get(standId);
        coefficients.add(frictionCoeff);
        if (coefficients.size() == 5) {
            float sum = 0;
            for (FrictionCoefficient coefficient : coefficients) {
                sum += coefficient.getFrictionCoefficient();
            }
            Iterator<StreamObserver<FrictionCoefficient>> averageClients = averageFrictionClients.get(standId).iterator();
            while (averageClients.hasNext()) {
                StreamObserver<FrictionCoefficient> averageFrictionClient = averageClients.next();
                try {
                    FrictionCoefficient averageCoefficient = FrictionCoefficient.newBuilder()
                            .setFrictionCoefficient(sum / 5f)
                            .setXTime(outputData.getXTime())
                            .build();
                    averageFrictionClient.onNext(averageCoefficient);
                    float meanXTime = 0;
                    for (FrictionCoefficient coefficient : coefficients) {
                        meanXTime += coefficient.getXTime();
                    }
                    OrowanCalculator.getInstance().getDatabase().saveOrowanAverageOutput(meanXTime / 5f, sum / 5f);
                } catch (StatusRuntimeException exception) {
                    averageClients.remove();
                }
            }
            coefficients.clear();
        }
    }
}
