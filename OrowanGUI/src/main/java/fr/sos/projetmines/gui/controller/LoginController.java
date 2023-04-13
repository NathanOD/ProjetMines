package fr.sos.projetmines.gui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField loginIdentifierField;

    @FXML
    private PasswordField loginPasswordField;

    @FXML
    public void attemptConnection(){
        //Identifier : loginIdentifierField.getText()
        //Password : loginPasswordField.getText()

    }

    public void closeWindow(){
        Platform.exit();
    }
}
