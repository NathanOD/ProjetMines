package fr.sos.projetmines.gui.util;

import fr.sos.projetmines.OrowanOperationResult;
import fr.sos.projetmines.gui.rpc.UserManagementClient;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class DeleteUserTask extends UserManagementTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserTask.class);


    private final StringProperty username = new SimpleStringProperty();

    /**
     * Returns the username of the user to be deleted.
     *
     * @return The username of the user to be deleted.
     */
    public String getUsername() {
        return username.getValue();
    }

    /**
     * Sets the username of the user to be deleted.
     *
     * @param newUsername The new username of the user to be deleted.
     */

    public void setUsername(String newUsername) {
        this.username.set(newUsername);
    }

    /**
     * Returns the StringProperty representing the username of the user to be deleted.
     *
     * @return The StringProperty representing the username of the user to be deleted.
     */
    public StringProperty username() {
        return username;
    }

    /**
     * Logs a success message after the user has been successfully deleted.
     */
    @Override
    protected void successLog() {
        LOGGER.info("User \"{}\" successfully deleted", getUsername());
    }

    /**
     * Logs a failure message if the user could not be deleted.
     */
    @Override
    protected void failLog() {
        LOGGER.info("Failed to delete the user \"{}\"", getUsername());
    }

    /**
     * Sends a request to the UserManagementClient to delete the user and returns the OrowanOperationResult, if any.
     *
     * @param client The UserManagementClient used to delete the user.
     * @return An Optional of {@link OrowanOperationResult} representing the result of the operation.
     */
    @Override
    protected Optional<OrowanOperationResult> getResult(UserManagementClient client) {
        return client.deleteUser(getUsername());
    }

}