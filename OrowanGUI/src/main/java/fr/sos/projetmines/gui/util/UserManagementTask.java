package fr.sos.projetmines.gui.util;

import fr.sos.projetmines.OperationResult;
import fr.sos.projetmines.OrowanOperationResult;
import fr.sos.projetmines.gui.rpc.UserManagementClient;
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

    // ---- UserManagementClient

    private ObjectProperty<UserManagementClient> client = new SimpleObjectProperty<>();
    public ObjectProperty<UserManagementClient> client() {
        return client;
    }
    public UserManagementClient getClient() {
        return client.getValue();
    }
    public void setClient(UserManagementClient newClient) {
        this.client.set(newClient);
    }
    public ObjectProperty<UserManagementClient> controller() {
        return client;
    }

    @Override
    protected Task<OperationResult> createTask() {
        return new Task<>() {
            @Override
            protected OperationResult call() throws Exception {
                try {
                    Optional<OrowanOperationResult> resultOpt = getResult();
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
                        getClient().getChannel().shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        LOGGER.error(e.getMessage());
                    }
                }
            }
        };
    }

    protected abstract void successLog();

    protected abstract void failLog();

    protected abstract Optional<OrowanOperationResult> getResult();

}
