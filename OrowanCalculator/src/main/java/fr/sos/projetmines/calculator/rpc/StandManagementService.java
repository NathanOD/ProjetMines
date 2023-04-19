package fr.sos.projetmines.calculator.rpc;

import fr.sos.projetmines.*;
import fr.sos.projetmines.calculator.OrowanCalculator;
import fr.sos.projetmines.calculator.database.CalculatorDatabaseFacade;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandManagementService extends StandManagementGrpc.StandManagementImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandManagementService.class);

    @Override
    public void enableStand(StandEnablingRequest request, StreamObserver<OrowanOperationResult> responseObserver) {
        super.enableStand(request, responseObserver);
    }

    @Override
    public void disableStand(StandDisablingRequest request, StreamObserver<OrowanOperationResult> responseObserver) {
        super.disableStand(request, responseObserver);
    }
}
