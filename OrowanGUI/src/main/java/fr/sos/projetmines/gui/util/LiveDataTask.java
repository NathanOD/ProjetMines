package fr.sos.projetmines.gui.util;

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

    // ---- HOST
    private final StringProperty host = new SimpleStringProperty("localhost");
    public String getHost() {
        return host.getValue();
    }
    public void setHost(String newHost) {
        this.host.set(newHost);
    }
    public StringProperty host() {
        return host;
    }

    // ---- PORT
    private final IntegerProperty port = new SimpleIntegerProperty(32765);
    public int getPort() {
        return port.getValue();
    }
    public void setPort(int newPort) {
        this.port.set(newPort);
    }
    public IntegerProperty port() {
        return port;
    }

    // ---- CONTROLLER

    private final ObjectProperty<WorkerController> controller = new SimpleObjectProperty<>();
    public WorkerController getController() {
        return controller.getValue();
    }
    public void setController(WorkerController newController) {
        this.controller.set(newController);
    }
    public ObjectProperty<WorkerController> controller(){
        return controller;
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                ManagedChannel channel = Grpc.newChannelBuilderForAddress(getHost(),
                        getPort(), InsecureChannelCredentials.create()).build();
                OrowanLiveDataClient client = new OrowanLiveDataClient(channel, getController());
                client.startReceivingValues();
                return null;
            }
        };
    }
}
