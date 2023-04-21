package fr.sos.projetmines.calculator.rpc;

import fr.sos.projetmines.*;
import fr.sos.projetmines.calculator.OrowanCalculator;
import fr.sos.projetmines.calculator.database.CalculatorDatabaseFacade;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;

public class StandManagementService extends StandManagementGrpc.StandManagementImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandManagementService.class);
    private final CalculatorDatabaseFacade database = OrowanCalculator.getInstance().getDatabase();
    /**
     * Enables the stand with the given ID.
     *
     * @param request The request containing the ID of the stand to enable.
     * @param responseObserver The observer for the response.
     */
    @Override
    public void enableStand(StandEnablingRequest request, StreamObserver<OrowanOperationResult> responseObserver) {
        OrowanOperationResult result = null;
        boolean success = database.setStandState(request.getStandId() , true);
        if (!success) {
            // If the user exists
            result = OrowanOperationResult.newBuilder().setResult(OperationResult.FAIL).build();
            LOGGER.warn("Stand {} could not be enabled!", request.getStandId());
        } else {
            result = OrowanOperationResult.newBuilder().setResult(OperationResult.SUCCESSFUL).build();
            LOGGER.info("Stand {} has been enabled!", request.getStandId());
        }
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }
    /**
     * Disables the stand with the given ID.
     *
     * @param request The request containing the ID of the stand to disable.
     * @param responseObserver The observer for the response.
     */
    @Override
    public void disableStand(StandDisablingRequest request, StreamObserver<OrowanOperationResult> responseObserver) {
        OrowanOperationResult result = null;
        boolean success = database.setStandState(request.getStandId() , false);
        if (!success) {
            // If the user exists
            result = OrowanOperationResult.newBuilder().setResult(OperationResult.FAIL).build();
            LOGGER.warn("Stand {} could not be disabled!", request.getStandId());
        } else {
            result = OrowanOperationResult.newBuilder().setResult(OperationResult.SUCCESSFUL).build();
            LOGGER.info("Stand {} has been disabled!", request.getStandId());
        }
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }
    /**
     * Lists all the stands.
     *
     * @param request The request (unused in this method).
     * @param responseObserver The observer for the response, which will receive each stand in the database.
     */
    @Override
    public void listStands(StandListRequest request, StreamObserver<Stand> responseObserver) {
        Optional<Set<Stand>> standsOpt = database.getStands();
        if (standsOpt.isEmpty()) {
            responseObserver.onCompleted();
            return;
        }
        for (Stand stand : database.getStands().get()) {
            responseObserver.onNext(stand);
        }
        responseObserver.onCompleted();
    }
}
