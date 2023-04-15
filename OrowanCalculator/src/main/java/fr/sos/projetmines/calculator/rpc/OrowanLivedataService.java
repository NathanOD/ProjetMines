package fr.sos.projetmines.calculator.rpc;

import fr.sos.projetmines.CurvePoint;
import fr.sos.projetmines.FrictionCoefficient;
import fr.sos.projetmines.OrowanLiveDataProviderGrpc;
import fr.sos.projetmines.StandIdentifier;
import fr.sos.projetmines.calculator.model.OrowanDataOutput;
import fr.sos.projetmines.calculator.util.OutputDataReceiver;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OrowanLivedataService extends OrowanLiveDataProviderGrpc.OrowanLiveDataProviderImplBase
        implements PropertyChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanLivedataService.class);
    private final Map<Integer, Set<StreamObserver<CurvePoint>>> curveDataResponses;

    public OrowanLivedataService() {
        OutputDataReceiver.getInstance().addPropertyChangeListener(this);
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
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt instanceof IndexedPropertyChangeEvent) {
            IndexedPropertyChangeEvent event = (IndexedPropertyChangeEvent) evt;
            OrowanDataOutput output = (OrowanDataOutput) evt.getNewValue();
            if (curveDataResponses.containsKey(event.getIndex())) {
                for (StreamObserver<CurvePoint> response : curveDataResponses.get(event.getIndex())) {
                    if (response != null) {
                        try {
                            CurvePoint point = CurvePoint.newBuilder()
                                    .setComputationTime(output.getComputationTime())
                                    .setSigma(output.getSigmaMoy())
                                    .setFrictionCoefficient(output.getFriction())
                                    .setRollSpeed(output.getRollSpeed())
                                    .build();
                            response.onNext(point);
                        }catch(StatusRuntimeException exception){
                        }
                    }
                }
            }
        }
    }
}

