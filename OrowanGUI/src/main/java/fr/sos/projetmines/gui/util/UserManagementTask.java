package fr.sos.projetmines.gui.util;

import fr.sos.projetmines.*;
import fr.sos.projetmines.gui.controller.OrowanController;
import fr.sos.projetmines.gui.rpc.OrowanAuthenticatorClient;
import fr.sos.projetmines.gui.rpc.UserManagementClient;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public abstract class UserManagementTask extends Service<OperationResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserManagementTask.class);
    /**

     Creates a new task for user management operations. The task creates a new gRPC channel to connect to the server,
     and it uses the {@link #getResult(UserManagementClient)} method to perform the user management operation.
     @return A new {@link Task} object that can be used to perform the user management operation.
     */
    @Override
    protected Task<OperationResult> createTask() {
        return new Task<>() {
            @Override
            protected OperationResult call() throws Exception {
                ManagedChannel channel = Grpc.newChannelBuilderForAddress(OrowanController.getInstance().getHost(),
                        OrowanController.getInstance().getPort() + 3, InsecureChannelCredentials.create()).build();
                try {
                    UserManagementClient client = new UserManagementClient(channel);
                    Optional<OrowanOperationResult> resultOpt = getResult(client);
                    if (resultOpt.isPresent() && resultOpt.get().getResult() == OperationResult.SUCCESSFUL) {
                        successLog();
                        return resultOpt.get().getResult();
                    } else {
                        failLog();
                        fireEvent(new WorkerStateEvent(this, WorkerStateEvent.WORKER_STATE_FAILED));
                        return OperationResult.FAIL;
                    }
                } finally {
                    try {
                        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        LOGGER.error(e.getMessage());
                    }
                }
            }
        };
    }
    /**

     This method is called when the user management operation succeeded, and it should log a message indicating the
     success.
     */
    protected abstract void successLog();
    /**

     This method is called when the user management operation failed, and it should log a message indicating the
     failure.
     */
    protected abstract void failLog();

    /**

     This method should be implemented to return an optional {@link OrowanOperationResult} object, which contains the
     result of the user management operation.
     @param client The {@link UserManagementClient} object used to perform the operation.
     @return An optional {@link OrowanOperationResult} object.
     */
    protected abstract Optional<OrowanOperationResult> getResult(UserManagementClient client);

}
