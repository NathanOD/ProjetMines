package fr.sos.projetmines.gui.rpc;

import fr.sos.projetmines.*;
import fr.sos.projetmines.gui.controller.WorkerController;
import io.grpc.Channel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class OrowanLiveDataClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanLiveDataClient.class);

    private final OrowanLiveDataProviderGrpc.OrowanLiveDataProviderStub client; //asynchronous
    private final WorkerController controller;

    public OrowanLiveDataClient(Channel channel, WorkerController controller) {
        this.client = OrowanLiveDataProviderGrpc.newStub(channel);
        this.controller = controller;
    }

    public void startReceivingValues() {
        StandIdentifier standIdentifier = StandIdentifier.newBuilder().setStandId(3).build(); //TODO: Change stand id
        try {
            client.curvesData(standIdentifier, new StreamObserver<>() {
                @Override
                public void onNext(CurvePoint value) {
                    controller.addPointToPlot(value);
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
