package fr.sos.projetmines.gui.util;

import fr.sos.projetmines.Job;
import fr.sos.projetmines.OrowanOperationResult;
import fr.sos.projetmines.gui.rpc.UserManagementClient;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UpdateUserJobTask extends UserManagementTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserTask.class);
    private final StringProperty username = new SimpleStringProperty();
    private final ObjectProperty<Job> job = new SimpleObjectProperty<>();
    /**
     * Get the username of the user being updated.
     * @return the username
     */
    public String getUsername() {
        return username.getValue();
    }
    /**
     * Set the username of the user being updated.
     * @param newUsername the new username
     */
    public void setUsername(String newUsername) {
        this.username.set(newUsername);
    }
    /**
     * Get the new job for the user being updated.
     * @return the job
     */
    public Job getJob() {
        return job.getValue();
    }
    /**
     * Set the new job for the user being updated.
     * @param newJob the new job
     */
    public void setJob(Job newJob) {
        this.job.set(newJob);
    }
    /**
     * Get the username property.
     * @return the username property
     */
    public StringProperty username() {
        return username;
    }
    /**
     * Get the job property.
     * @return the job property
     */
    public ObjectProperty<Job> job() {
        return job;
    }
    /**
     * Logs a message indicating that the user was successfully updated.
     */
    @Override
    protected void successLog() {
        LOGGER.info("User \"{}\" successfully updated", getUsername());
    }
    /**
     * Logs a message indicating that updating the user failed.
     */
    @Override
    protected void failLog() {
        LOGGER.info("Failed to udpdate the user \"{}\"", getUsername());
    }
    /**
     * Sends an update user job request to the UserManagementClient and returns the result.
     * @param client the UserManagementClient to use
     * @return an Optional containing the OrowanOperationResult if the request was successful, or empty if it failed
     */
    @Override
    protected Optional<OrowanOperationResult> getResult(UserManagementClient client) {
        return client.updateUserJob(getUsername(), getJob());
    }

}




