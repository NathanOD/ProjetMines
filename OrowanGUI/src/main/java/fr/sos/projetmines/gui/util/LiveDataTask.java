package fr.sos.projetmines.gui.util;

import fr.sos.projetmines.gui.controller.OrowanController;
import fr.sos.projetmines.gui.controller.WorkerController;
import fr.sos.projetmines.gui.rpc.OrowanLiveDataClient;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import javafx.beans.property.*;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveDataTask extends Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiveDataTask.class);

    // ---- CONTROLLER
    /**

     The {@link WorkerController} associated with this task.
     */
    private final ObjectProperty<WorkerController> controller = new SimpleObjectProperty<>();
    /**

     Gets the value of the controller property.
     @return the value of the controller property
     */
    public WorkerController getController() {
        return controller.getValue();
    }
    /**

     Sets the value of the controller property.
     @param newController the new value of the controller property
     */
    public void setController(WorkerController newController) {
        this.controller.set(newController);
    }

    /**

     Returns the controller property itself.
     @return the controller property
     */
    public ObjectProperty<WorkerController> controller(){
        return controller;
    }
    /**

     Creates a new {@link Task} that starts a gRPC channel and a {@link OrowanLiveDataClient} to start receiving live data
     from the Orowan system.
     @return a new Task that starts a gRPC channel and a {@link OrowanLiveDataClient} to start receiving live data
     from the Orowan system.
     */
    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                ManagedChannel channel = Grpc.newChannelBuilderForAddress(OrowanController.getInstance().getHost(),
                        OrowanController.getInstance().getPort() + 1, InsecureChannelCredentials.create()).build();
                OrowanLiveDataClient client = new OrowanLiveDataClient(getController(), channel);
                client.startReceivingValues();
                return null;
            }
        };
    }
}
