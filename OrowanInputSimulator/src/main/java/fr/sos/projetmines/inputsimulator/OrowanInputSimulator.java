package fr.sos.projetmines.inputsimulator;

import fr.sos.projetmines.commonutils.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class OrowanInputSimulator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanInputSimulator.class);

    private final InputSimulatorDatabaseConnection database;

    private static final OrowanInputSimulator INSTANCE = new OrowanInputSimulator();
    /**

     Returns the singleton instance of {@link OrowanInputSimulator}.

     */
    public static OrowanInputSimulator getInstance(){
        return INSTANCE;
    }
    /**

     Creates an instance of {@link OrowanInputSimulator} and initializes the database connection

     and input file insertion threads.
     */
    public OrowanInputSimulator() {
        Optional<Configuration> configOpt = getConfiguration();
        if (configOpt.isEmpty()) {
            this.database = null;
            return;
        }
        Configuration config = configOpt.get();

        database = new InputSimulatorDatabaseConnection("jdbc:h2:" + config.getStringValue("db-url") + config.getStringValue("db-name")
                + config.getStringValue("db-properties"), config.getStringValue("db-username"), config.getStringValue("db-password"));
        database.connect();

        String[] files = config.getStringValue("input-files").split(",");
        for (String path : files) {
            String[] contents = path.split(":");
            int standId = Integer.parseInt(contents[1]);
            String inputFilePath = String.format(contents[0], standId);
            DatabaseInserter databaseInserter = new DatabaseInserter();
            new Thread(() -> {
                try {
                    database.insertStand(standId);
                    databaseInserter.startInsertion(standId, inputFilePath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    /**
     * Retrieves the configuration from the system's config.properties file and validates it against a set of expectations.
     * If the configuration is valid, returns an Optional object containing the Configuration object.
     * Otherwise, returns an empty Optional object.
     * @return an Optional object containing the Configuration object if the configuration is valid, or an empty Optional object otherwise.
     */
    private Optional<Configuration> getConfiguration() {
        Path configPath = Path.of(System.getProperty("user.dir"), "config.properties");
        Set<ConfigurationExpectation> expectations = new HashSet<>();
        expectations.add(new StringExpectation("db-url", 2, 0));
        expectations.add(new StringExpectation("db-name", 1, 0));
        expectations.add(new StringExpectation("db-properties", 0, 0));
        expectations.add(new ConcatenatedStringExpectation(3, 0, "db-url", "db-name"));
        expectations.add(new StringExpectation("db-username", 1, 0));
        expectations.add(new StringExpectation("db-password", 0, 0));
        expectations.add(new StringExpectation("input-files", 2, 0));
        try {
            Configuration config = new Configuration(configPath, "default-configuration.properties", expectations);
            boolean validated = config.validateConfiguration();
            if (!validated) {
                return Optional.empty();
            }
            return Optional.of(config);
        } catch (IOException ioException) {
            LOGGER.error("Could not interact with the configuration");
        } catch (ConfigurationFilePathException e) {
            LOGGER.error(e.getMessage());
        }
        return Optional.empty();
    }

    public InputSimulatorDatabaseConnection getDatabase(){
        return database;
    }

    public static void main(String[] args) {
    }
}
