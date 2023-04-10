package fr.sos.projetmines;

import fr.sos.projetmines.controller.OrowanController;
import fr.sos.projetmines.util.OrowanDataConverter;
import javafx.application.Application;
import javafx.stage.Stage;

public class MinesProject extends Application {

    public static void main(String[] args) {
        String workingDir = System.getProperty("user.dir");

        new OrowanDataConverter("./Orowan/Krakov/1939351_F2.txt",
                workingDir + "/1939351_F2_output.txt");
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