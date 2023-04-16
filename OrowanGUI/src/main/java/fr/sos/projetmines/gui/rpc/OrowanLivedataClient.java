package fr.sos.projetmines.gui.rpc;

import fr.sos.projetmines.*;
import fr.sos.projetmines.gui.controller.OrowanController;
import io.grpc.Channel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrowanLivedataClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanLivedataClient.class);

    private final OrowanLiveDataProviderGrpc.OrowanLiveDataProviderStub client; //asynchronous
    private final Channel channel;
    public  List<CurvePoint> curvePoints = new ArrayList<>();

    public OrowanLivedataClient(Channel channel) {
        this.channel = channel;
        client = OrowanLiveDataProviderGrpc.newStub(channel);
    }

    public void startReceivingValues() {
        StandIdentifier standIdentifier = StandIdentifier.newBuilder().setStandId(3).build(); //TODO: Change stand id
        try {
            client.curvesData(standIdentifier, new StreamObserver<CurvePoint>() {
                @Override
                public void onNext(CurvePoint value) {
                    LOGGER.info("NEW POINT RECEIVED!!!");
                    curvePoints.add(value);
                    //OrowanController.getInstance().updatePlot(curvePoints);
                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onCompleted() {

                }
            });
        } catch (StatusRuntimeException e) {
            LOGGER.warn("RPC Failed: {}", e.getStatus());
        }
    }
}
