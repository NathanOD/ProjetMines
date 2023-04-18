package fr.sos.projetmines.calculator.rpc;

import fr.sos.projetmines.*;
import fr.sos.projetmines.calculator.OrowanCalculator;
import fr.sos.projetmines.calculator.database.CalculatorDatabaseFacade;
import fr.sos.projetmines.calculator.util.DataFormatter;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserManagementService extends UserManagementGrpc.UserManagementImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserManagementService.class);

    private final CalculatorDatabaseFacade database = OrowanCalculator.getInstance().getDatabase();

    @Override
    public void createUser(UserCreationRequest request, StreamObserver<UserOperationResult> responseObserver) {
        LOGGER.debug("Received an user creation request.");
        byte[] salt = DataFormatter.generateSalt();
        byte[] passwordHash = DataFormatter.hashPassword(request.getPassword(), salt);

        UserOperationResult result = null;
        if (!database.checkUserExistence(request.getUsername())) {
            //If the user does not already exist then creates
            database.addUser(request.getUsername(), passwordHash, salt, request.getJob());
            result = UserOperationResult.newBuilder().setResult(OperationResult.SUCCESSFUL).build();
        } else {
            //If the user is already registered
            result = UserOperationResult.newBuilder().setResult(OperationResult.FAIL).build();
        }
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteUser(UserDeletionRequest request, StreamObserver<UserOperationResult> responseObserver) {
        UserOperationResult result = null;
        if (database.checkUserExistence(request.getUsername())) {
            //If the user exists then delete
            database.deleteUser(request.getUsername());
            result = UserOperationResult.newBuilder().setResult(OperationResult.SUCCESSFUL).build();
        } else {
            //If the user does not exist
            result = UserOperationResult.newBuilder().setResult(OperationResult.FAIL).build();
        }
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }

    @Override
    public void setUserJob(UserJobUpdateRequest request, StreamObserver<UserOperationResult> responseObserver) {
        UserOperationResult result = null;
        if (!database.checkUserExistence(request.getUsername())) {
            // If the user exists
            result = UserOperationResult.newBuilder().setResult(OperationResult.FAIL).build();
        } else {
            database.setUserJob(request.getUsername(), request.getJob());
            result = UserOperationResult.newBuilder().setResult(OperationResult.SUCCESSFUL).build();
        }
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }
}
