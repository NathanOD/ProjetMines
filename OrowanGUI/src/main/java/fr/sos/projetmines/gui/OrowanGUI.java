package fr.sos.projetmines.gui;

import fr.sos.projetmines.gui.controller.OrowanController;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.spi.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.Level;

public class OrowanGUI extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanGUI.class);

    public static void main(String[] args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        new OrowanController(primaryStage);

    }
}