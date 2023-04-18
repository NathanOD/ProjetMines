package fr.sos.projetmines.calculator.rpc;

import fr.sos.projetmines.*;
import fr.sos.projetmines.calculator.OrowanCalculator;
import fr.sos.projetmines.calculator.database.CalculatorDatabaseFacade;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandManagementService extends StandManagementGrpc.StandManagementImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandManagementService.class);

    private final CalculatorDatabaseFacade database = OrowanCalculator.getInstance().getDatabase();

    @Override
    public void newStand(OrowanNewStand request, StreamObserver<StandCreationResult> responseObserver) {
        super.newStand(request, responseObserver);
        /*
        LOGGER.debug("Received an user creation request.");


         */
    }

    @Override
    public void deleteStand(OrowanDeleteStand request, StreamObserver<StandDeleteResult> responseObserver) {
        super.deleteStand(request, responseObserver);
    }
}
