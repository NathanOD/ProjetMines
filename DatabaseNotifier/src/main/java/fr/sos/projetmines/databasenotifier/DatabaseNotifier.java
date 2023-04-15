package fr.sos.projetmines.databasenotifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

public class DatabaseNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseNotifier.class);

    public static void main(String[] args) {
        Path configPath = Path.of(System.getProperty("user.dir"), "./config.properties");
        try {
            Optional<Properties> configOpt = new RPCConfiguration(configPath).verifyConfig();
            if (configOpt.isPresent()) {
                Properties config = configOpt.get();

                configureTrigger(config);

                RPCServer server = new RPCServer(Integer.parseInt(config.getProperty("rpc-db-port")));
                server.startServer();
                server.blockUntilShutdown();
            }
        } catch (IOException e) {
            LOGGER.error("Unable to reach the configuration file! ({})", configPath.toAbsolutePath());
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private static void configureTrigger(Properties config) {
        try {
            String databaseAddress = config.getProperty("db-prefix") + config.getProperty("db-url")
                    + config.getProperty("db-name");

            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection(databaseAddress, config.getProperty("db-username"),
                    config.getProperty("db-password"));
            /*Statement stat = conn.createStatement();
            stat.execute("CREATE TRIGGER INSERTION AFTER INSERT ON input_orowan FOR EACH ROW " +
                    "CALL \"fr.sos.projetmines.databasenotifier.DatabaseTrigger\" ");*/
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
