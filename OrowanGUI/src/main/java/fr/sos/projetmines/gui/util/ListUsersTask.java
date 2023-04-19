package fr.sos.projetmines.gui.util;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ListUsersTask extends Service<Set<String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListUsersTask.class);

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

    // ---- CONTROLLER

    private final ObjectProperty<EngineerController> controller = new SimpleObjectProperty<>();
    public EngineerController getController() {
        return controller.getValue();
    }
    public void setController(EngineerController newController) {
        this.controller.set(newController);
    }
    public ObjectProperty<EngineerController> controller(){
        return controller;
    }

    @Override
    protected Task<Set<String>> createTask() {
        return new Task<>() {
            @Override
            protected Set<String> call() throws Exception {
                return getClient().listUsers();
            }
        };
    }
}
