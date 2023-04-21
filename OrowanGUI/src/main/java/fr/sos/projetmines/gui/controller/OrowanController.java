package fr.sos.projetmines.gui.controller;

import fr.sos.projetmines.Job;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class OrowanController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanController.class);

    // -- Application constants

    public static OrowanController getInstance() {
        return instance;
    }

    private static final String TITLE_BASE = "Projet Mines x ArcelorMittal - Orowan";
    private static OrowanController instance;
    private final Stage stage;

    // --- Util variables
    private String host;
    private int port;
    private Job job;
    private int standId;

    // ---

    public OrowanController(Stage stage) {
        if (instance == null) {
            instance = this;
        }
        this.stage = stage;
        showLoginScene();
        stage.getIcons().add(new Image(OrowanController.class.getClassLoader().getResourceAsStream("icon.png")));
        stage.show();
    }


    public void showChoiceScene() {
        stage.setTitle(TITLE_BASE + " - Interface selection");
        showScene("choice-scene.fxml");
    }

    public void showWorkerScene() {
        stage.setTitle(TITLE_BASE + " - Stand n°" + standId);
        showScene("worker-scene.fxml");
    }

    public void showProcessEngineerScene() {
        stage.setTitle(TITLE_BASE + " - Administration panel");
        showScene("engineer-scene.fxml");
    }

    public void showLoginScene() {
        stage.setTitle(TITLE_BASE + " - Connection");
        showScene("login-scene.fxml");
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

    public Job getUserJob() {
        return job;
    }

    public void setUserJob(Job newJob) {
        this.job = newJob;
    }

    public int getStandId() {
        return standId;
    }

    public void setStandId(int newStandId) {
        this.standId = newStandId;
    }
}
