package fr.sos.projetmines.gui.util;

import fr.sos.projetmines.Job;
import fr.sos.projetmines.OperationResult;
import fr.sos.projetmines.OrowanConnectionResult;
import fr.sos.projetmines.gui.controller.OrowanController;
import fr.sos.projetmines.gui.rpc.OrowanAuthenticatorClient;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
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

public class AuthenticatorTask extends Service<Job> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticatorTask.class);

    /**

     The username property of the user to be authenticated.
     */
    private final StringProperty username = new SimpleStringProperty();
    /**

     The password property of the user to be authenticated.
     */
    private final StringProperty password = new SimpleStringProperty();
    /**

     The host property of the remote Orowan server.
     */
    private final StringProperty host = new SimpleStringProperty();
    /**

     The port property of the remote Orowan server.
     */
    private final IntegerProperty port = new SimpleIntegerProperty();
    /**

     Returns the username property of the user to be authenticated.
     @return the username property
     */
    public String getUsername() {
        return username.getValue();
    }
    /**

     Sets the username property of the user to be authenticated.
     @param newUsername the new username
     */
    public void setUsername(String newUsername) {
        this.username.set(newUsername);
    }
    /**

     Returns the username property of the user to be authenticated as a JavaFX property.
     @return the username property
     */
    public StringProperty username() {
        return username;
    }
    /**

     Returns the password property of the user to be authenticated.
     @return the password property
     */
    public String getPassword() {
        return password.getValue();
    }
    /**

     Sets the password property of the user to be authenticated.
     @param newPassword the new password
     */
    public void setPassword(String newPassword) {
        this.password.set(newPassword);
    }
    /**

     Returns the password property of the user to be authenticated as a JavaFX property.
     @return the password property
     */
    public StringProperty password() {
        return password;
    }
    /**

     Returns the host property of the remote Orowan server.
     @return the host property
     */
    public String getHost() {
        return host.getValue();
    }
    /**

     Sets the host property of the remote Orowan server.
     @param newHost the new host
     */
    public void setHost(String newHost) {
        this.host.set(newHost);
    }

    /**

     Returns the host property of the remote Orowan server as a JavaFX property.
     @return the host property
     */
    public StringProperty host() {
        return host;
    }
    /**

     Returns the port property of the remote Orowan server.
     @return the port property
     */
    public int getPort() {
        return port.getValue();
    }
    /**

     Sets the port property of the remote Orowan server.
     @param newPort the new port
     */
    public void setPort(int newPort) {
        this.port.set(newPort);
    }
    /**

     Returns the port property of the remote Orowan server as a JavaFX property.
     @return the port property
     */
    public IntegerProperty port() {
        return port;
    }

    /**

     Creates a new task that authenticates the user with the remote Orowan server and returns a job to be executed.
     @return a new authentication task
     */
    @Override
    protected Task<Job> createTask() {
        return new Task<>() {
            @Override
            protected Job call() throws Exception {
                ManagedChannel channel = Grpc.newChannelBuilderForAddress(getHost(), getPort(), InsecureChannelCredentials.create()).build();
                try {
                    OrowanAuthenticatorClient client = new OrowanAuthenticatorClient(channel);
                    Optional<OrowanConnectionResult> resultOpt = client.sendConnectionRequest(getUsername(), getPassword());
                    if (resultOpt.isPresent() && resultOpt.get().getResult() == OperationResult.SUCCESSFUL) {
                        OrowanController.getInstance().setHost(getHost());
                        OrowanController.getInstance().setPort(getPort());
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
