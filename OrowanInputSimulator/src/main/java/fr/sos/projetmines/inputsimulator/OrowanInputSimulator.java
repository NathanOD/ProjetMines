package fr.sos.projetmines.inputsimulator;

import java.io.IOException;

public class OrowanInputSimulator {

    public static void main(String[] args) {
        int standId = 3;
        String inputFilePath = String.format("./Krakov/1939351_F%d.txt", standId);
        DatabaseInserter databaseInserter = new DatabaseInserter();

        InputSimulatorDatabaseConnection dbConnection = InputSimulatorDatabaseConnection.getInstance();
        dbConnection.setDatabaseAddress("jdbc:h2:tcp://localhost/C:/Users/steve/Documents/Cours/FIG172/2IA/Projet IL/db/orowan;IGNORECASE=TRUE;AUTO_SERVER=true;AUTO_RECONNECT=TRUE");
        dbConnection.setUsername("simulator");
        dbConnection.setPassword("simulator");
        dbConnection.connect();

        try {
            InputSimulatorDatabaseConnection.getInstance().insertStand(standId);
            databaseInserter.startInsertion(standId, inputFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*
        DataFormatter dFormat = new DataFormatter();
        DataFormatter.formatToFile();
        */
    }
}
