package fr.sos.projetmines.gui.util;

import fr.sos.projetmines.UserOperationResult;
import fr.sos.projetmines.gui.rpc.UserManagementClient;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class DeleteUserTask extends UserManagementTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserTask.class);

    private final StringProperty username = new SimpleStringProperty();

    public DeleteUserTask(UserManagementClient client) {
        super(client);
    }

    public String getUsername() {
        return username.getValue();
    }

    public void setUsername(String newUsername) {
        this.username.set(newUsername);
    }

    public StringProperty username() {
        return username;
    }

    @Override
    protected void successLog() {
        LOGGER.info("User \"{}\" successfully deleted", getUsername());
    }

    @Override
    protected void failLog() {
        LOGGER.info("Failed to delete the user \"{}\"", getUsername());
    }

    @Override
    protected Optional<UserOperationResult> getResult() {
        return client.deleteUser(getUsername());
    }

}