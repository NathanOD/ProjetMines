package fr.sos.projetmines.service;

import fr.sos.projetmines.MinesProject;
import fr.sos.projetmines.util.DatabaseConnection;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class DatabaseConfigurationService extends Service<Boolean> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfigurationService.class);
    private static final String DEFAULT_CONFIGURATION_FILE = "database-default-configuration.properties";

    private final ObjectProperty<Path> configurationFile = new SimpleObjectProperty<>(this, "databaseConfigPath");

    public Path getConfigurationPath() {
        return configurationFile.getValue();
    }

    public void setConfigurationPath(Path filePath) {
        configurationFile.set(filePath);
    }

    public final ObjectProperty<Path> configurationProperty() {
        return configurationFile;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                boolean fileExisted = validateConfigurationFile();
                if(!fileExisted){
                    failed();
                    return false;
                }

                Properties databaseConfig = new Properties();
                InputStream configStream = new FileInputStream(getConfigurationPath().toFile());
                databaseConfig.load(configStream);
                configStream.close();

                if (!databaseConfig.containsKey("driver") ||
                        !databaseConfig.containsKey("prefix") ||
                        !databaseConfig.containsKey("url") ||
                        !databaseConfig.containsKey("database")) {
                    LOGGER.info("Configuration is malformed!");
                    saveMalformedConfig();
                    copyDefaultConfiguration();
                    failed();
                    return false;
                }

                String driver = databaseConfig.getProperty("driver");
                String databaseAddress = databaseConfig.getProperty("prefix")
                        + databaseConfig.getProperty("url")
                        + databaseConfig.getProperty("database");
                LOGGER.debug("Found driver: \"{}\"", driver);
                LOGGER.debug("Database address: \"{}\"", databaseAddress);

                DatabaseConnection.getInstance().configureConnection(driver, databaseAddress);
                return true;
            }
        };
    }

    private boolean validateConfigurationFile() throws IOException {
        if (!Files.exists(getConfigurationPath())) {
            LOGGER.warn("No configuration found for the database!");
            copyDefaultConfiguration();
            return false;
        } else {
            LOGGER.debug("A configuration for the database has been found!");
            return true;
        }
    }

    private void copyDefaultConfiguration() throws IOException {
        InputStream databaseDefaultConfig = MinesProject.class.getClassLoader()
                .getResourceAsStream(DEFAULT_CONFIGURATION_FILE);
        assert databaseDefaultConfig != null;
        Files.copy(databaseDefaultConfig, getConfigurationPath(), StandardCopyOption.REPLACE_EXISTING);
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

        Files.copy(getConfigurationPath(), cwd.resolve(backupFileName));
        LOGGER.info("Saved the malformed configuration into \"{}\"", backupFileName);
    }
}
