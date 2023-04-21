package fr.sos.projetmines.gui.util;

import fr.sos.projetmines.OperationResult;
import fr.sos.projetmines.OrowanOperationResult;
import fr.sos.projetmines.gui.OrowanGUI;
import fr.sos.projetmines.gui.controller.EngineerController;
import fr.sos.projetmines.gui.controller.OrowanController;
import fr.sos.projetmines.gui.controller.WorkerController;
import fr.sos.projetmines.gui.rpc.OrowanLiveDataClient;
import fr.sos.projetmines.gui.rpc.UserManagementClient;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import javafx.beans.property.*;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ListUsersTask extends Service<Set<String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListUsersTask.class);

    // ---- CONTROLLER
    /**
     * The controller associated with this task. This property is used to ensure
     * that the controller is not garbage-collected while the service is running.
     */
    private final ObjectProperty<EngineerController> controller = new SimpleObjectProperty<>();
    /**
     * Returns the controller associated with this task.
     *
     * @return the controller associated with this task
     */
    public EngineerController getController() {
        return controller.getValue();
    }
    /**
     * Sets the controller associated with this task.
     *
     * @param newController the new controller to associate with this task
     */
    public void setController(EngineerController newController) {
        this.controller.set(newController);
    }

    /**
     * Returns the object property that contains the controller associated with this task.
     *
     * @return the object property that contains the controller associated with this task
     */
    public ObjectProperty<EngineerController> controller(){
        return controller;
    }

    /**
     * Creates and returns a new task that lists all the users available in the Orowan system.
     *
     * @return a new task that lists all the users available in the Orowan system
     */
    @Override
    protected Task<Set<String>> createTask() {
        return new Task<>() {
            @Override
            protected Set<String> call() throws Exception {
                ManagedChannel channel = Grpc.newChannelBuilderForAddress(OrowanController.getInstance().getHost(),
                        OrowanController.getInstance().getPort() + 3, InsecureChannelCredentials.create()).build();
                try {
                    UserManagementClient client = new UserManagementClient(channel);
                    return client.listUsers();
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
