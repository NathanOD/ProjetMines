package fr.sos.projetmines.calculator.rpc;

import fr.sos.projetmines.*;
import fr.sos.projetmines.calculator.util.CalculatorDatabaseConnection;
import fr.sos.projetmines.calculator.util.DataFormatter;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserManagementService extends UserManagementGrpc.UserManagementImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserManagementService.class);

    @Override
    public void createUser(OrowanNewUserCredentials request, StreamObserver<UserCreationResult> responseObserver) {
        String providedUsername = request.getUsername();
        String providedPassword = request.getPassword();
        Job providedJob = request.getJob();
        byte[] salt = DataFormatter.generateSalt();
        byte[] passwordHash = DataFormatter.hashPassword(providedPassword, salt);

        CalculatorDatabaseConnection database = CalculatorDatabaseConnection.getInstance();
        if (!database.checkUserExistance(providedUsername)) {
            database.addUser(providedUsername, passwordHash, salt, providedJob);
            UserCreationResult result = UserCreationResult.newBuilder()
                    .setResult(UserCreationResult.CreationResult.CREATION_SUCCESSFUL).build();
            responseObserver.onNext(result);
            responseObserver.onCompleted();
        } else {
            UserCreationResult result = UserCreationResult.newBuilder()
                    .setResult(UserCreationResult.CreationResult.CREATION_FAILED).build();
            responseObserver.onNext(result);
            responseObserver.onCompleted();
        }
    }


    @Override
    public void deleteUser(OrowanDeletedUserCredentials request, StreamObserver<UserDeleteResult> responseObserver) {
        String providedUsername = request.getUsername();

        CalculatorDatabaseConnection database = CalculatorDatabaseConnection.getInstance();
        if (database.checkUserExistance(providedUsername)) {
            database.deleteUser(providedUsername);
            UserDeleteResult result = UserDeleteResult.newBuilder()
                    .setResult(UserDeleteResult.DeleteResult.DELETE_SUCCESSFUL).build();
            responseObserver.onNext(result);
            responseObserver.onCompleted();
        } else {
            UserDeleteResult result = UserDeleteResult.newBuilder()
                    .setResult(UserDeleteResult.DeleteResult.DELETE_FAILED).build();
            responseObserver.onNext(result);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void newAdmin(OrowanNewAdminCredentials request, StreamObserver<NewAdminResult> responseObserver) {
        String providedAdminName = request.getUsername();
        CalculatorDatabaseConnection database = CalculatorDatabaseConnection.getInstance();

        if (!database.checkUserExistance(providedAdminName)) {
            NewAdminResult result = NewAdminResult.newBuilder()
                    .setResult(NewAdminResult.AdminResult.ADMIN_FAILED).build();
            responseObserver.onNext(result);
            responseObserver.onCompleted();
        } else {
            database.setJob(providedAdminName, Job.PROCESS_ENGINEER);
            NewAdminResult result = NewAdminResult.newBuilder()
                    .setResult(NewAdminResult.AdminResult.ADMIN_SUCCESSFUL).build();
            responseObserver.onNext(result);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void newWorker(OrowanNewWorkerCredentials request, StreamObserver<NewWorkerResult> responseObserver) {
        String providedWorkerName = request.getUsername();
        CalculatorDatabaseConnection database = CalculatorDatabaseConnection.getInstance();

        if (!database.checkUserExistance(providedWorkerName)) {
            NewWorkerResult result = NewWorkerResult.newBuilder()
                    .setResult(NewWorkerResult.WorkerResult.WORKER_FAILED).build();
            responseObserver.onNext(result);
            responseObserver.onCompleted();
        } else {
            database.setJob(providedWorkerName, Job.WORKER);
            NewWorkerResult result = NewWorkerResult.newBuilder()
                    .setResult(NewWorkerResult.WorkerResult.WORKER_SUCCESSFUL).build();
            responseObserver.onNext(result);
            responseObserver.onCompleted();
        }
    }
}
