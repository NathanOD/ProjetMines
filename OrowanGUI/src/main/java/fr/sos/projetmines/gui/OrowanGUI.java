package fr.sos.projetmines.gui;

import fr.sos.projetmines.gui.controller.OrowanController;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrowanGUI {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanGUI.class);

    public static void main(String[] args) {
        Application.launch(GUIStarter.class, args);
    }

}