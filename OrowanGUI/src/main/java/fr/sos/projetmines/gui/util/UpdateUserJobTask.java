package fr.sos.projetmines.gui.util;

import fr.sos.projetmines.Job;
import fr.sos.projetmines.UserOperationResult;
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
    public UpdateUserJobTask(UserManagementClient client) {
        super(client);
    }

    public String getUsername() {
        return username.getValue();
    }

    public void setUsername(String newUsername) {
        this.username.set(newUsername);
    }

    public Job getJob() {
        return job.getValue();
    }

    public void setJob(Job newJob) {
        this.job.set(newJob);
    }

    public StringProperty username() {
        return username;
    }

    public ObjectProperty<Job> job() {
        return job;
    }

    @Override
    protected void successLog() {
        LOGGER.info("User \"{}\" successfully updated", getUsername());
    }

    @Override
    protected void failLog() {
        LOGGER.info("Failed to udpdate the user \"{}\"", getUsername());
    }

    @Override
    protected Optional<UserOperationResult> getResult() {
        return client.updateUserJob(getUsername(), getJob());
    }

}




