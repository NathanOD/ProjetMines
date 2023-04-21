package fr.sos.projetmines.calculator;

import fr.sos.projetmines.calculator.database.CalculatorDatabaseFacade;
import fr.sos.projetmines.calculator.model.OrowanDataOutput;
import fr.sos.projetmines.calculator.rpc.CalculatorServers;
import fr.sos.projetmines.calculator.rpc.DatabaseNotifierClient;
import fr.sos.projetmines.calculator.util.DataFormatter;
import fr.sos.projetmines.commonutils.config.*;
import fr.sos.projetmines.commonutils.event.OEventBroadcaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class OrowanCalculator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanCalculator.class);

    private static OrowanCalculator instance;

    //----------- PUBLICLY ACCESSIBLE VARIABLES (THROUGH GETTERS)
    private final Configuration config;
    private final CalculatorDatabaseFacade database;
    private final OEventBroadcaster dataInputBroadcaster;

    private final DataFormatter dataFormatter;
    //-----------

    /**
     *Constructs a new instance of the OrowanCalculator class. Initializes the required components
     * and starts the RPC connections.
     */
    private OrowanCalculator() {
        instance = this;
        this.dataInputBroadcaster = new OEventBroadcaster();
        Optional<Configuration> configOpt = getConfiguration();
        if (configOpt.isEmpty()) {
            this.config = null;
            this.database = null;
            this.dataFormatter = null;
            return;
        }
        this.config = configOpt.get();

        Optional<CalculatorDatabaseFacade> databaseOpt = getDatabaseConnection();
        if (databaseOpt.isEmpty()) {
            this.database = null;
            this.dataFormatter = null;
            return;
        }
        this.database = databaseOpt.get();

        this.dataFormatter = new DataFormatter();
        dataFormatter.initializeInputRanges();
        //simulateData();
        startRPCConnections();
    }

    /**
     * Gets the singleton instance of the OrowanCalculator.
     * @return The singleton instance of the OrowanCalculator.
     */
    public static OrowanCalculator getInstance() {
        return instance;
    }

    /**
     * The main method for the OrowanCalculator. Constructs a new instance of the OrowanCalculator.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        new OrowanCalculator();
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
        expectations.add(new IntExpectation("rpc-minimal-port", 0, 65535));
        expectations.add(new StringExpectation("rpc-db-url", 2, 0));
        expectations.add(new IntExpectation("rpc-db-port", 0, 65535));
        expectations.add(new StringExpectation("orowan-exe", 5, 0));
        try {
            Configuration config = new Configuration(configPath, "default-config.properties", expectations);
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

    /**
     * Gets a connection to the database using the configuration's "db-url", "db-name", "db-properties", "db-username", and "db-password" values.
     * It concatenates the "db-url", "db-name", and "db-properties" values to form the address.
     * It creates a new CalculatorDatabaseFacade object with the address, username, and password.
     * It attempts to connect to the database and returns an empty Optional if the connection fails.
     * Otherwise, it returns an Optional containing the CalculatorDatabaseFacade object.
     * @return an Optional containing a CalculatorDatabaseFacade object if the connection was successful, otherwise an empty Optional.
     */
    private Optional<CalculatorDatabaseFacade> getDatabaseConnection() {
        String address = "jdbc:h2:" + config.getConcatenatedValue("db-url", "db-name", "db-properties");
        String username = config.getStringValue("db-username");
        String password = config.getStringValue("db-password");
        CalculatorDatabaseFacade database = new CalculatorDatabaseFacade(address, username, password);
        boolean databaseConnected = database.connect();
        if (!databaseConnected) {
            return Optional.empty();
        }
        return Optional.of(database);
    }

    /**
     * Starts the remote procedure call (RPC) connections and blocks until shutdown.
     * It creates a CalculatorServers object based on the configuration's "rpc-minimal-port" value and starts the servers.
     * It also creates a DatabaseNotifierClient object and starts listening for updates in a new thread.
     * The method blocks until the servers are shutdown.
     */
    private void startRPCConnections() {
        CalculatorServers servers = new CalculatorServers(config.getIntValue("rpc-minimal-port"));
        DatabaseNotifierClient client = new DatabaseNotifierClient();
        new Thread(client::startListeningForUpdates).start();

        try {
            servers.startServers();
            servers.blockUntilShutdown();
        } catch (IOException | InterruptedException e) {
            LOGGER.error(e.getMessage());
        }

    }

    /**
     *Retrieves the configuration object.
     * @return An instance of the Configuration class that contains the details of the application's configuration.
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * Retrieves the database connection object.
     * @return An instance of the CalculatorDatabaseFacade class that provides access to the underlying database.
     */
    public CalculatorDatabaseFacade getDatabase() {
        return database;
    }

    /**
     * Retrieves the data input broadcaster object.
     * @return An instance of the OEventBroadcaster class that broadcasts incoming data inputs to all registered listeners.
     */
    public OEventBroadcaster getDataInputBroadcaster() {
        return dataInputBroadcaster;
    }

    /**
     * Retrieves the data formatter object.
     * @return An instance of the DataFormatter class that formats data inputs for display purposes.
     */
    public DataFormatter getDataFormatter() {
        return dataFormatter;
    }

}
