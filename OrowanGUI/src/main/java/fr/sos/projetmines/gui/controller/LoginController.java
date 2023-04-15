package fr.sos.projetmines.gui.controller;

import fr.sos.projetmines.Job;
import fr.sos.projetmines.gui.util.CalculatorClientService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField hostField;

    @FXML
    private TextField loginIdentifierField;

    @FXML
    private PasswordField loginPasswordField;

    @FXML
    public void attemptConnection() {
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
            CalculatorClientService service = new CalculatorClientService();
            service.setUsername(identifier);
            service.setPassword(password);
            service.setHost(host);
            service.setPort(port);
            service.start();
            service.setOnSucceeded(event -> {
                Job result = (Job) event.getSource().getValue();
                if (result == Job.PROCESS_ENGINEER) {
                    OrowanController.getInstance().showProcessEngineerScene();
                } else {
                    OrowanController.getInstance().showWorkerScene();
                }
            });
        } else {
            LOGGER.info("Address malformed! Please enter an adress in this format: \"host:port\".");
        }
    }

    public void closeWindow() {
        Platform.exit();
    }
}
