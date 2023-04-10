package fr.sos.projetmines.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class OrowanController implements PropertyChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanController.class);

    public OrowanController(Stage stage) {
        try {
            // Load login overview.
            ResourceBundle bundle = ResourceBundle.getBundle("bundles/strings", Locale.getDefault());

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("fr.sos.projetmines/login-scene.fxml"));
            loader.setResources(bundle);

            Scene loginScene = new Scene(loader.load());

            // Set the stage parameters
            stage.setTitle("Projet Mines x ArcelorMittal - Orowan");
            stage.setScene(loginScene);
            // Image icon = new Image(getClass().getClassLoader().getResourceAsStream("images/icon.png"));
            // stage.getIcons().add(icon);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the person overview inside the root layout.
     */
    public void showPersonOverview() {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        LOGGER.debug("Event on property: " + evt.getPropertyName());
    }
}
