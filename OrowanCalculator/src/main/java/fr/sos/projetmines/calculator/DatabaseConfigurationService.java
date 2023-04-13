package fr.sos.projetmines.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class DatabaseConfigurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfigurationService.class);
    private static final String DEFAULT_CONFIGURATION_FILE = "database-default-configuration.properties";

    private final Path configurationFilePath;

    public DatabaseConfigurationService(Path configurationFilePath) throws IOException {
        this.configurationFilePath = configurationFilePath;
        boolean fileExisted = validateConfigurationFile();
        if (!fileExisted) {
            //failTask();
        }

        Properties databaseConfig = new Properties();
        InputStream configStream = new FileInputStream(configurationFilePath.toFile());
        databaseConfig.load(configStream);
        configStream.close();

        if (!databaseConfig.containsKey("prefix") ||
                !databaseConfig.containsKey("url") ||
                !databaseConfig.containsKey("database")) {
            LOGGER.info("Configuration is malformed!");
            saveMalformedConfig();
            copyDefaultConfiguration();
//            failTask();
        }

        String databaseAddress = databaseConfig.getProperty("prefix")
                + databaseConfig.getProperty("url")
                + databaseConfig.getProperty("database");

        if (databaseAddress.length() == 0) {
            LOGGER.error("The configured address is invalid!");
//            failTask();
        }
        LOGGER.debug("Database address: \"{}\"", databaseAddress);

        DatabaseConnection.getInstance().setDatabaseAddress(databaseAddress);

    }

    private boolean validateConfigurationFile() throws IOException {
        if (!Files.exists(configurationFilePath)) {
            LOGGER.warn("No configuration found for the database!");
            copyDefaultConfiguration();
            return false;
        } else {
            LOGGER.debug("A configuration for the database has been found!");
            return true;
        }
    }

    private void copyDefaultConfiguration() throws IOException {
        InputStream databaseDefaultConfig = DatabaseConfigurationService.class.getClassLoader()
                .getResourceAsStream(DEFAULT_CONFIGURATION_FILE);
        assert databaseDefaultConfig != null;
        Files.copy(databaseDefaultConfig, configurationFilePath, StandardCopyOption.REPLACE_EXISTING);
        databaseDefaultConfig.close();
        LOGGER.info("Generated the default configuration in \"database-configuration.properties\" file!");
    }

    private void saveMalformedConfig() throws IOException {
        int counter = 0;
        Path cwd = Path.of(System.getProperty("user.dir"));
        String backupFileName = "configuration-backup.properties." + counter;
        while (cwd.resolve(backupFileName).toFile().exists()) {
            counter++;
            backupFileName = "configuration-backup.properties." + counter;
        }

        Files.copy(configurationFilePath, cwd.resolve(backupFileName));
        LOGGER.info("Saved the malformed configuration into \"{}\"", backupFileName);
    }
}
