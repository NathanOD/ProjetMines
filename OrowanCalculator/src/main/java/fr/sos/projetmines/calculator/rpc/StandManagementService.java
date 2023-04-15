package fr.sos.projetmines.calculator.rpc;

import fr.sos.projetmines.*;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandManagementService extends StandManagementGrpc.StandManagementImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandManagementService.class);

    @Override
    public void newStand(OrowanNewStand request, StreamObserver<StandCreationResult> responseObserver) {
        super.newStand(request, responseObserver);
        /*
        int providedStandid = request.getID();

         */
    }

    @Override
    public void deleteStand(OrowanDeleteStand request, StreamObserver<StandDeleteResult> responseObserver) {
        super.deleteStand(request, responseObserver);
    }
}
