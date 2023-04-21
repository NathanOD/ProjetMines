package fr.sos.projetmines.gui.controller;

import fr.sos.projetmines.Job;
import fr.sos.projetmines.gui.util.AuthenticatorTask;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    /**

     The task responsible for authenticating the user.
     */
    private final AuthenticatorTask service;
    /**

     The text field for entering the host address.
     */
    @FXML
    private TextField hostField;
    /**

     The text field for entering the login identifier.
     */
    @FXML
    private TextField loginIdentifierField;
    /**

     The password field for entering the login password.
     */
    @FXML
    private PasswordField loginPasswordField;
    /**

     Constructor for the LoginController class.
     Initializes the AuthenticatorTask, sets the onSucceeded and onFailed events, and logs the result of the authentication.
     */
    public LoginController() {
        service = new AuthenticatorTask();
        service.setOnSucceeded(event -> {
            Job result = (Job) event.getSource().getValue();
            OrowanController.getInstance().setUserJob(result);
            OrowanController.getInstance().showChoiceScene();
            LOGGER.info("Authentication succeeded as a {}.", result.name());
        });
        service.setOnFailed(event -> LOGGER.info("Authentication failed."));
    }

    /**

     The attemptConnection method is called when the user clicks the login button.

     It validates the user input, sets the credentials and connection information for the AuthenticatorTask,

     starts the task, and logs the authentication request.
     */
    @FXML
    private void attemptConnection() {
        String identifier = loginIdentifierField.getText();
        String password = loginPasswordField.getText();
        String address = hostField.getText();
        if (address.split(":").length == 2) {
            String host = address.split(":")[0];
            int port;
            try {
                port = Integer.parseInt(address.split(":")[1]);
            } catch (NumberFormatException exception) {
                LOGGER.info("Port malformed! Please enter an integer.");
                return;
            }

            if (identifier.length() == 0) {
                LOGGER.info("Username cannot be empty!");
                return;
            }

            service.setUsername(identifier);
            service.setPassword(password);
            service.setHost(host);
            service.setPort(port);

            service.restart();
            LOGGER.info("Sent authentication request.");

        } else {
            LOGGER.info("Address malformed! Please enter an address in this format: \"host:port\".");
        }
    }
    /**

     The close method is called when the user clicks the close button.
     It cancels the AuthenticatorTask and exits the application.
     */
    @FXML
    private void close() {
        service.cancel();
        Platform.exit();
    }
}
