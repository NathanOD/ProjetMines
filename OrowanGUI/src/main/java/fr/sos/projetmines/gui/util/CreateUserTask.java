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

    public CreateUserTask(UserManagementClient client){
        super(client);
    }


    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final ObjectProperty<Job> job = new SimpleObjectProperty<>();



    public String getUsername() {
        return username.getValue();
    }

    public String getPassword() {
        return password.getValue();
    }

    public Job getJob() {
        return job.getValue();
    }


    public void setUsername(String newUsername) {
        this.username.set(newUsername);
    }

    public void setPassword(String newPassword) {
        this.password.set(newPassword);
    }

    public void setJob(Job newJob) {
        this.job.set(newJob);
    }


    public StringProperty username() {
        return username;
    }

    public StringProperty password() {
        return password;
    }

    public ObjectProperty<Job> job() {
        return job;
    }

    @Override
    protected void successLog() {
        LOGGER.info("User \"{}\" successfully created", getUsername());
    }

    @Override
    protected void failLog() {
        LOGGER.info("Failed to create the user \"{}\"", getUsername());
    }

    @Override
    protected Optional<UserOperationResult> getResult() {
        return client.createUser(getUsername(), getPassword(), getJob());
    }
}
