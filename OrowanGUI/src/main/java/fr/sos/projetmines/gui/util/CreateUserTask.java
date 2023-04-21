package fr.sos.projetmines.gui.util;

import fr.sos.projetmines.*;
import fr.sos.projetmines.gui.controller.OrowanController;
import fr.sos.projetmines.gui.rpc.OrowanAuthenticatorClient;
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
import java.util.concurrent.TimeUnit;

public class CreateUserTask extends UserManagementTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserTask.class);

    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final ObjectProperty<Job> job = new SimpleObjectProperty<>();


    /**

     Returns the username of the user to be created.
     @return the username of the user to be created
     */
    public String getUsername() {
        return username.getValue();
    }
    /**

     Returns the password of the user to be created.
     @return the password of the user to be created
     */
    public String getPassword() {
        return password.getValue();
    }
    /**

     Returns the job of the user to be created.
     @return the job of the user to be created
     */
    public Job getJob() {
        return job.getValue();
    }

    /**

     Sets the username of the user to be created.
     @param newUsername the new username to set
     */
    public void setUsername(String newUsername) {
        this.username.set(newUsername);
    }
    /**

     Sets the password of the user to be created.
     @param newPassword the new password to set
     */
    public void setPassword(String newPassword) {
        this.password.set(newPassword);
    }
    /**

     Sets the job of the user to be created.
     @param newJob the new job to set
     */
    public void setJob(Job newJob) {
        this.job.set(newJob);
    }

    /**

     Returns the username property of the user to be created.
     @return the username property of the user to be created
     */
    public StringProperty username() {
        return username;
    }
    /**

     Returns the password property of the user to be created.
     @return the password property of the user to be created
     */
    public StringProperty password() {
        return password;
    }
    /**

     Returns the job property of the user to be created.
     @return the job property of the user to be created
     */
    public ObjectProperty<Job> job() {
        return job;
    }
    /**

     Defines the logging behavior in case of a successful user creation.
     */
    @Override
    protected void successLog() {
        LOGGER.info("User \"{}\" successfully created", getUsername());
    }
    /**

     Defines the logging behavior in case of a failed user creation.
     */
    @Override
    protected void failLog() {
        LOGGER.info("Failed to create the user \"{}\"", getUsername());
    }
    /**

     Sends the request to create a new user to the server through the provided client and returns the operation result if present.
     @param client the user management client used to send the request
     @return an {@link Optional} containing the operation result if present, an empty optional otherwise
     */
    @Override
    protected Optional<OrowanOperationResult> getResult(UserManagementClient client) {
        return client.createUser(getUsername(), getPassword(), getJob());
    }
}
