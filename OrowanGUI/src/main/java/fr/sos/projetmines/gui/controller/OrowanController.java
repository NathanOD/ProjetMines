package fr.sos.projetmines.gui.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class OrowanController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanController.class);
    private static OrowanController instance;
    private final Stage stage;

    public static OrowanController getInstance(){
        return instance;
    }

    private String host;
    private int port;


    public OrowanController(Stage stage) {
        if (instance == null){
            instance = this;
        }
        this.stage = stage;
        try {
            // Load login overview.
            ResourceBundle bundle = ResourceBundle.getBundle("bundles/strings", Locale.getDefault());

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("fr.sos.projetmines.gui/login-scene.fxml"));
            loader.setResources(bundle);
            Scene loginScene = new Scene(loader.load());

            // Set the stage parameters
            stage.setTitle("Projet Mines x ArcelorMittal - Orowan");
            stage.setScene(loginScene);
            /*
            Image icon = new Image(getClass().getClassLoader().getResourceAsStream("images/icon.png"));
            stage.getIcons().add(icon);
            */
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showWorkerScene() {
        showScene("worker-scene.fxml");
    }

    public void showProcessEngineerScene() {
        showScene("engineer-scene.fxml");
    }

    private void showScene(String sceneFileName) {
        ResourceBundle bundle = ResourceBundle.getBundle("bundles/strings", Locale.getDefault());

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("fr.sos.projetmines.gui/" + sceneFileName));
        loader.setResources(bundle);
        try {
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException exception) {
            LOGGER.error(exception.getMessage());
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
