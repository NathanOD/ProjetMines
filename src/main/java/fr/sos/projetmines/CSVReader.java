package fr.sos.projetmines;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader {
    public void main(String[] args) {

        String csvFile = "C:/Users/hp/OneDrive/Desktop/projet IL/Fichiers/Krakov/1939351_F2.txt";
        String line = "";
        String cvsSplitBy = "; ";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {
                String[] values = line.split(cvsSplitBy);

                // Do something with the values
                for (String value : values) {
                    System.out.print(value + " ");
                }
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
