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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OrowanLiveDataService extends OrowanLiveDataProviderGrpc.OrowanLiveDataProviderImplBase
        implements OEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanLiveDataService.class);
    private final Map<Integer, Set<StreamObserver<CurvePoint>>> curveDataResponses;

    public OrowanLiveDataService() {
        OrowanCalculator.getInstance().getDataInputBroadcaster().registerListener(this);
        curveDataResponses = new HashMap<>();
    }

    @Override
    public void curvesData(StandIdentifier request, StreamObserver<CurvePoint> responseObserver) {
        int standId = request.getStandId();
        if (!curveDataResponses.containsKey(standId)) {
            curveDataResponses.put(standId, new HashSet<>());
        }
        curveDataResponses.get(standId).add(responseObserver);
        LOGGER.info("A new client will receive data for the stand {}!", standId);
    }

    @Override
    public void averageFrictionCoefficient(StandIdentifier request, StreamObserver<FrictionCoefficient> responseObserver) {

    }

    @Override
    public void onEvent(Map<String, ?> eventData) {
        int standId = (int) eventData.get("standId");
        OrowanDataOutput output = (OrowanDataOutput) eventData.get("output");
        //Check for stand clients existence
        if (curveDataResponses.containsKey(standId)) {
            //For each client clients
            for (StreamObserver<CurvePoint> response : curveDataResponses.get(standId)) {
                try {
                    // Send the new CurvePoint
                    CurvePoint point = CurvePoint.newBuilder()
                            .setComputationTime(output.getComputationTime())
                            .setSigma(output.getSigmaMoy())
                            .setFrictionCoefficient(output.getFriction())
                            .setRollSpeed(output.getRollSpeed())
                            .setXTime(output.getXTime())
                            .build();
                    response.onNext(point);
                } catch (StatusRuntimeException | NullPointerException exception) {
                    // Remove the connection from the set if an error occures
                    curveDataResponses.get(standId).remove(response);
                }
            }
        }
    }
}

