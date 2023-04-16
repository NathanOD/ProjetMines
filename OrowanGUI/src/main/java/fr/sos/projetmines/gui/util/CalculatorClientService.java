package fr.sos.projetmines.gui.util;

import fr.sos.projetmines.ConnectionResult;
import fr.sos.projetmines.Job;
import fr.sos.projetmines.OrowanConnectionResult;
import fr.sos.projetmines.gui.rpc.OrowanAuthenticatorClient;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class CalculatorClientService extends Service<Job> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalculatorClientService.class);


    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();

    public String getUsername() {
        return username.getValue();
    }

    public void setUsername(String newUsername) {
        this.username.set(newUsername);
    }

    public StringProperty username() {
        return username;
    }

    public String getPassword() {
        return password.getValue();
    }

    public void setPassword(String newPassword) {
        this.password.set(newPassword);
    }

    public StringProperty password() {
        return password;
    }

    private final StringProperty host = new SimpleStringProperty();

    public String getHost() {
        return host.getValue();
    }

    public void setHost(String newHost) {
        this.host.set(newHost);
    }

    public StringProperty host() {
        return host;
    }
    private final IntegerProperty port = new SimpleIntegerProperty();

    public int getPort() {
        return port.getValue();
    }

    public void setPort(int newPort) {
        this.port.set(newPort);
    }

    public IntegerProperty port() {
        return port;
    }


    @Override
    protected Task<Job> createTask() {
        return new Task<>() {
            @Override
            protected Job call() throws Exception {
                ManagedChannel channel = Grpc.newChannelBuilderForAddress(getHost(),
                                getPort(), InsecureChannelCredentials.create()).build();
                try {
                    OrowanAuthenticatorClient client = new OrowanAuthenticatorClient(channel);
                    Optional<OrowanConnectionResult> resultOpt = client.sendConnectionRequest(getUsername(), getPassword());
                    if (resultOpt.isPresent() && resultOpt.get().getResult() == ConnectionResult.LOGIN_SUCCESSFUL) {
                        Platform.runLater(() -> {
                            LiveDataService service = new LiveDataService();
                            service.setHost(getHost());
                            service.setPort(getPort()+1);
                            service.start();
                        });
                        return resultOpt.get().getUserJob();
                    } else {
                        fireEvent(new WorkerStateEvent(this, WorkerStateEvent.WORKER_STATE_FAILED));
                        return Job.UNRECOGNIZED;
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
}
