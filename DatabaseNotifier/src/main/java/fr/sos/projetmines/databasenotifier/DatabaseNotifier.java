package fr.sos.projetmines.databasenotifier;

import fr.sos.projetmines.commonutils.config.Configuration;
import fr.sos.projetmines.commonutils.config.ConfigurationExpectation;
import fr.sos.projetmines.commonutils.config.ConfigurationFilePathException;
import fr.sos.projetmines.commonutils.config.IntExpectation;
import fr.sos.projetmines.commonutils.event.OEventBroadcaster;
import fr.sos.projetmines.commonutils.rpc.OrowanRPCServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DatabaseNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseNotifier.class);

    private static final DatabaseNotifier INSTANCE = new DatabaseNotifier();
    private final OEventBroadcaster broadcaster;
    private boolean isUp = false;

    /**
     * Constructs the DatabaseNotifier object by initializing the OEventBroadcaster and starting an RPC server.
     */
    private DatabaseNotifier() {
        this.broadcaster = new OEventBroadcaster();
        Optional<Configuration> configOpt = getConfiguration();
        if (configOpt.isEmpty()) {
            LOGGER.info("Database notifier RPC Server will not start!");
            return;
        }
        OrowanRPCServer server = new OrowanRPCServer(configOpt.get().getIntValue("rpc-db-port"),
                "DatabaseNotifier", new EventNotifierService(broadcaster));
        new Thread(() -> {
            try {
                server.startServer();
                server.blockUntilShutdown();
            } catch (IOException exception) {
                LOGGER.error(exception.getMessage());
                isUp = false;
            } catch (InterruptedException exception) {
                isUp = false;
            }
        }).start();
        isUp = true;
    }

    /**
     * Returns the singleton instance of this class.
     * @return the singleton instance of this class
     */
    public static DatabaseNotifier getInstance() {
        return INSTANCE;
    }

    /**
     * Retrieves the configuration from the config file and returns an Optional object
     * containing the Configuration object if the configuration is valid, or an empty Optional otherwise.
     * @return an Optional object containing the Configuration object if the configuration
     * is valid, or an empty Optional otherwise
     */
    private Optional<Configuration> getConfiguration() {
        Path configPath = Path.of(System.getProperty("user.dir"), "config.properties");
        LOGGER.info("Configuration file path: {}", configPath);
        Set<ConfigurationExpectation> expectations = new HashSet<>();
        expectations.add(new IntExpectation("rpc-db-port", 0, 65535));
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
     * Returns a flag indicating whether the RPC server is up and running.
     * @return a flag indicating whether the RPC server is up and running
     */
    public boolean isUp() {
        return isUp;
    }

    /**
     * Returns the object used to broadcast events to the registered listeners.
     * @return the object used to broadcast events to the registered listeners
     */
    public OEventBroadcaster getBroadcaster() {
        return broadcaster;
    }
}
