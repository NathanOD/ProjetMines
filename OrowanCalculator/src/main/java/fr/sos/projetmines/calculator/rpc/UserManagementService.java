package fr.sos.projetmines.calculator.rpc;

import fr.sos.projetmines.*;
import fr.sos.projetmines.calculator.OrowanCalculator;
import fr.sos.projetmines.calculator.database.CalculatorDatabaseFacade;
import fr.sos.projetmines.calculator.util.DataFormatter;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;


public class UserManagementService extends UserManagementGrpc.UserManagementImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserManagementService.class);

    private final CalculatorDatabaseFacade database = OrowanCalculator.getInstance().getDatabase();
    /**

     Creates a new user with the given credentials and adds them to the database.
     @param request The request containing the user's information.
     @param responseObserver The response observer used to send the result of the operation back to the client.
     */
    @Override
    public void createUser(UserCreationRequest request, StreamObserver<OrowanOperationResult> responseObserver) {
        LOGGER.debug("Received an user creation request.");
        byte[] salt = DataFormatter.generateSalt();
        byte[] passwordHash = DataFormatter.hashPassword(request.getPassword(), salt);

        OrowanOperationResult result = null;
        if (!database.checkUserExistence(request.getUsername())) {
            //If the user does not already exist then creates
            database.addUser(request.getUsername(), passwordHash, salt, request.getJob());
            result = OrowanOperationResult.newBuilder().setResult(OperationResult.SUCCESSFUL).build();
        } else {
            //If the user is already registered
            result = OrowanOperationResult.newBuilder().setResult(OperationResult.FAIL).build();
        }
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }
    /**

     Deletes an existing user from the database.
     @param request The request containing the name of the user to be deleted.
     @param responseObserver The response observer used to send the result of the operation back to the client.
     */
    @Override
    public void deleteUser(UserDeletionRequest request, StreamObserver<OrowanOperationResult> responseObserver) {
        OrowanOperationResult result = null;
        if (database.deleteUser(request.getUsername())) {
            //If the user exists then delete
            result = OrowanOperationResult.newBuilder().setResult(OperationResult.SUCCESSFUL).build();
        } else {
            //If the user does not exist
            result = OrowanOperationResult.newBuilder().setResult(OperationResult.FAIL).build();
        }
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }
    /**

     Updates the job of an existing user in the database.
     @param request The request containing the name of the user and their new job.
     @param responseObserver The response observer used to send the result of the operation back to the client.
     */
    @Override
    public void setUserJob(UserJobUpdateRequest request, StreamObserver<OrowanOperationResult> responseObserver) {
        OrowanOperationResult result = null;
        boolean success = database.setUserJob(request.getUsername(), request.getJob());
        if (!success) {
            // If the user exists
            result = OrowanOperationResult.newBuilder().setResult(OperationResult.FAIL).build();
        } else {
            result = OrowanOperationResult.newBuilder().setResult(OperationResult.SUCCESSFUL).build();
        }
        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }
    /**

     Retrieves a list of all the users in the database and sends their usernames to the client.
     @param request The request object, not used in this method.
     @param responseObserver The response observer used to send the list of users back to the client.
     */
    @Override
    public void listUsers(ListUsersRequest request, StreamObserver<OrowanUserCredentials> responseObserver) {
        Optional<Set<String>> usernamesOpt = database.getUsers();
        if (usernamesOpt.isEmpty()) {
            responseObserver.onCompleted();
            return;
        }
        for (String username : usernamesOpt.get()) {
            OrowanUserCredentials credentials = OrowanUserCredentials.newBuilder().setUsername(username).build();
            responseObserver.onNext(credentials);
        }
        responseObserver.onCompleted();
    }
}
