package fr.sos.projetmines.gui.rpc;

import fr.sos.projetmines.*;
import fr.sos.projetmines.gui.controller.OrowanController;
import fr.sos.projetmines.gui.controller.WorkerController;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrowanLiveDataClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanLiveDataClient.class);

    private final OrowanLiveDataProviderGrpc.OrowanLiveDataProviderStub client; //asynchronous
    private final WorkerController controller;
    /**

     Constructor method for OrowanLiveDataClient class.
     @param controller the WorkerController instance associated with this client
     @param channel the channel used to establish a connection to the gRPC server
     */
    public OrowanLiveDataClient(WorkerController controller, Channel channel) {
        this.client = OrowanLiveDataProviderGrpc.newStub(channel);
        this.controller = controller;
    }

    /**

     Starts receiving real-time data points from the OrowanLiveDataProvider service for a given stand identifier.
     */

    public void startReceivingValues() {
        StandIdentifier standIdentifier = StandIdentifier.newBuilder()
                .setStandId(OrowanController.getInstance().getStandId()).build(); //TODO: Change stand id
        LOGGER.info("Waiting for stand {} data...", standIdentifier.getStandId());
        try {
            client.curvesData(standIdentifier, new StreamObserver<>() {
                @Override
                public void onNext(CurvePoint value) {
                    controller.plotNormalPoint(value);
                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onCompleted() {

                }
            });

            client.averageFrictionCoefficient(standIdentifier, new StreamObserver<>() {
                @Override
                public void onNext(FrictionCoefficient value) {
                    controller.plotAveragePoint(value);
                }

                @Override
                public void onError(Throwable t) {}

                @Override
                public void onCompleted() {}
            });
        } catch (StatusRuntimeException e) {
            LOGGER.warn("RPC Failed: {}", e.getStatus());
        }
    }
}
