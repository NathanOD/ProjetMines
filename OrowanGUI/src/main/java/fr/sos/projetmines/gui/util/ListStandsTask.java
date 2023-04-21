package fr.sos.projetmines.gui.util;

import fr.sos.projetmines.Job;
import fr.sos.projetmines.OperationResult;
import fr.sos.projetmines.OrowanConnectionResult;
import fr.sos.projetmines.Stand;
import fr.sos.projetmines.gui.controller.OrowanController;
import fr.sos.projetmines.gui.rpc.OrowanAuthenticatorClient;
import fr.sos.projetmines.gui.rpc.StandManagementClient;
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
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ListStandsTask extends Service<Set<Stand>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListStandsTask.class);

    // ---- UserManagementClient
    /**

     The property that holds the {@link StandManagementClient} instance to use for the RPC calls.
     @return the object property for the {@link StandManagementClient}
     */
    private ObjectProperty<StandManagementClient> client = new SimpleObjectProperty<>();

    public ObjectProperty<StandManagementClient> client() {
        return client;
    }
    /**

     Gets the {@link StandManagementClient} instance to use for the RPC calls.
     @return the {@link StandManagementClient} instance
     */
    public StandManagementClient getClient() {
        return client.getValue();
    }
    /**

     Sets the {@link StandManagementClient} instance to use for the RPC calls.
     @param newClient the new {@link StandManagementClient} instance to use
     */
    public void setClient(StandManagementClient newClient) {
        this.client.set(newClient);
    }
    /**

     Alias for the {@link #client()} method.
     @return the object property for the {@link StandManagementClient}
     */
    public ObjectProperty<StandManagementClient> controller() {
        return client;
    }
    /**

     Creates a Task object that retrieves the list of stands from the server.
     @return a Task object that retrieves the list of stands from the server
     */
    @Override
    protected Task<Set<Stand>> createTask() {
        return new Task<>() {
            @Override
            protected Set<Stand> call() throws Exception {
                ManagedChannel channel = Grpc.newChannelBuilderForAddress(OrowanController.getInstance().getHost(),
                        OrowanController.getInstance().getPort() + 2, InsecureChannelCredentials.create()).build();
                try {
                    StandManagementClient client = new StandManagementClient(channel);
                    Set<Stand> stands = client.listStands();
                    if (stands.size() > 0) {
                        LOGGER.info("{} stands have been found!", stands.size());
                    } else {
                        LOGGER.warn("No stand available in the database!");
                        fireEvent(new WorkerStateEvent(this, WorkerStateEvent.WORKER_STATE_FAILED));
                    }
                    return stands;
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
}
