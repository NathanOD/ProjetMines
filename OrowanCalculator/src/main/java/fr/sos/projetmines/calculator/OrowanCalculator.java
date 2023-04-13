package fr.sos.projetmines.calculator;

import java.nio.file.Path;

public class OrowanCalculator {

    public static void main(String[] args) {
        new OrowanDataConverter("./Orowan/Krakov/1939351_F2.txt",
                Path.of(System.getProperty("user.dir"), "/1939351_F2_output.txt"));
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
    }
}
