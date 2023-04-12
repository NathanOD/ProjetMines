package fr.sos.projetmines;

import fr.sos.projetmines.controller.OrowanController;
import fr.sos.projetmines.util.OrowanDataConverter;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class MinesProject extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinesProject.class);

    public static void main(String[] args) throws IOException {
        new OrowanDataConverter("./Orowan/Krakov/1939351_F2.txt",
                Path.of(System.getProperty("user.dir"), "/1939351_F2_output.txt"));

        launch(args);
    }

    /*
        //TODO: Check this snippet:
        CSVOutput csv = new CSVOutput(workingDir + "/output1.txt");
        csv.readCSV();

        String[][] data = csv.getData();
        for (String[] row : data) {
            for (String value : row) {
                System.out.print(value + "\t");
            }
            System.out.println();
        }
    */


    @Override
    public void start(Stage primaryStage) {
        new OrowanController(primaryStage);
    }
}