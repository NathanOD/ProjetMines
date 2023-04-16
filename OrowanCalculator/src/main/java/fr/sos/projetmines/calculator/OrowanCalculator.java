package fr.sos.projetmines.calculator;

import fr.sos.projetmines.calculator.database.CalculatorDatabaseFacade;
import fr.sos.projetmines.calculator.model.OrowanDataOutput;
import fr.sos.projetmines.calculator.rpc.CalculatorServers;
import fr.sos.projetmines.calculator.rpc.DatabaseNotifierClient;
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
    //-----------

    private OrowanCalculator() {
        instance = this;
        this.dataInputBroadcaster = new OEventBroadcaster();
        Optional<Configuration> configOpt = getConfiguration();
        if (configOpt.isEmpty()) {
            this.config = null;
            this.database = null;
            return;
        }
        this.config = configOpt.get();

        Optional<CalculatorDatabaseFacade> databaseOpt = getDatabaseConnection();
        if (databaseOpt.isEmpty()) {
            this.database = null;
            return;
        }
        this.database = databaseOpt.get();

        startRPCConnections();
    }

    public static OrowanCalculator getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        new OrowanCalculator();
    }

    private static void simulateData() {
        Random random = new Random();

        new Thread(() -> {
            while (true) {
                OrowanDataOutput output = new OrowanDataOutput(0, "VOID", 0, 0, 0,
                        0, 0, 0, 0, 0, 0, "YES");
                output.setComputationTime(204);
                output.setRollSpeed(0.54f);
                Map<String, Object> data = new HashMap<>();
                data.put("standId", 3);
                data.put("output", output);
                OrowanCalculator.getInstance().getDataInputBroadcaster().broadcast(data);
                try {
                    Thread.sleep(195L + random.nextInt(10));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

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

    private void startRPCConnections() {
        CalculatorServers servers = new CalculatorServers(config.getIntValue("rpc-minimal-port"));
        DatabaseNotifierClient client = new DatabaseNotifierClient();
        client.startListeningForUpdates();

        try {
            servers.startServers();
            servers.blockUntilShutdown();
        } catch (IOException | InterruptedException e) {
            LOGGER.error(e.getMessage());
        }

    }

    public Configuration getConfig() {
        return config;
    }

    public CalculatorDatabaseFacade getDatabase() {
        return database;
    }

    public OEventBroadcaster getDataInputBroadcaster() {
        return dataInputBroadcaster;
    }
}
