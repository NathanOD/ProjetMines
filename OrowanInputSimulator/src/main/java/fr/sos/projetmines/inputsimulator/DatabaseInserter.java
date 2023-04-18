package fr.sos.projetmines.inputsimulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


public class DatabaseInserter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInserter.class);

    private final Set<Strip> strips;

    public DatabaseInserter() {
        this.strips = new HashSet<>();
    }

    public void startInsertion(int standId, String localInputFilePath) throws IOException {
        InputStream inputCsv = getClass().getClassLoader().getResourceAsStream(localInputFilePath);
        if (inputCsv == null) {
            LOGGER.error("Cannot access the file at the provided the path \"{}\"!", localInputFilePath);
            return;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputCsv));
        double lastTime = 0;

        // On se connecte à la base de données
        InputSimulatorDatabaseConnection dbConnection = InputSimulatorDatabaseConnection.getInstance();
        dbConnection.setDatabaseAddress("jdbc:h2:tcp://localhost/C:/Users/steve/Documents/Cours/FIG172/2IA/Projet IL/db/orowan;IGNORECASE=TRUE;AUTO_SERVER=true;AUTO_RECONNECT=TRUE");
        dbConnection.setUsername("simulator");
        dbConnection.setPassword("simulator");
        dbConnection.connect();

        // On ouvre le fichier CSV
        bufferedReader.readLine(); // Skip header line
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] columns = line.split("\t+");

            int[] integerValues = new int[2];
            float[] doubleValues = new float[columns.length - 3];
            for (int i = 0; i < 2; i++)
                integerValues[i] = Integer.parseInt(columns[i]);
            for (int i = 0; i < columns.length - 3; i++) {
                try {
                    doubleValues[i] = Float.parseFloat(columns[i + 2]);
                } catch (NumberFormatException e) {
                    LOGGER.warn("The value \"{}\" is not parsable to double !", columns[i]);
                }
            }

            Strip strip = getStripById(integerValues[1]).orElse(new Strip(integerValues[1], standId, doubleValues[8],
                    doubleValues[9], doubleValues[10], doubleValues[11], doubleValues[12]));
            StripDataEntry entry = new StripDataEntry(strip, doubleValues[16], integerValues[0], integerValues[1],
                    doubleValues[0], doubleValues[1], doubleValues[2], doubleValues[3], doubleValues[4], doubleValues[5],
                    doubleValues[6], doubleValues[7], doubleValues[13], doubleValues[14], doubleValues[15], doubleValues[17],
                    doubleValues[18], doubleValues[19], doubleValues[20], doubleValues[21]);

            if (getStripById(1).isEmpty()) {
                dbConnection.insertData(entry, true);
                strips.add(strip);
            } else {
                dbConnection.insertData(entry, false);
            }

            LOGGER.debug("Added a value to the database");
            // On attend avant de passer à l'entrée suivante
            try {
                Thread.sleep((long) ((doubleValues[0] - lastTime) * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lastTime = doubleValues[0];
        }
        dbConnection.closeConnection();
        bufferedReader.close();
    }

    private Optional<Strip> getStripById(int stripId) {
        return strips.stream().filter(strip -> strip.getStripId() == stripId).findAny();
    }
}
