package fr.sos.projetmines.inputsimulator;

import java.io.IOException;

public class OrowanInputSimulator {

    public static void main(String[] args) {
        String inputFilePath = "./Krakov/1939351_F3.txt";
        DatabaseInserter databaseInserter = new DatabaseInserter();
        try {
            databaseInserter.startInsertion(3, inputFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*
        DataFormatter dFormat = new DataFormatter();
        DataFormatter.formatToFile();
        */
    }
}
