package fr.sos.projetmines.gui;

import fr.sos.projetmines.gui.controller.OrowanController;
import javafx.application.Application;
import javafx.stage.Stage;

public class GUIStarter extends Application {

    @Override
    public void start(Stage primaryStage) {
        new OrowanController(primaryStage);

    }
}
