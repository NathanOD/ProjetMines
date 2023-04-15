package fr.sos.projetmines.calculator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

public class CalculatorConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalculatorConfiguration.class);
    private static final String DEFAULT_CONFIGURATION_FILE = "default-config.properties";

    private final Path configurationFilePath;

    public CalculatorConfiguration(Path configurationFilePath) {
        this.configurationFilePath = configurationFilePath;
    }

    public Optional<Properties> verifyConfig() throws IOException {
        if (!validateConfigurationFile()) {
            return Optional.empty();
        }

        Properties config = new Properties();
        InputStream configStream = new FileInputStream(configurationFilePath.toFile());
        config.load(configStream);
        configStream.close();

        Set<String> configurationKeys = Set.of("db-prefix", "db-url", "db-name", "db-username", "db-password",
                "rpc-minimal-port", "rpc-db-url", "rpc-db-port", "orowan-exe");
        boolean containsAllKeys = config.keySet().containsAll(configurationKeys);
        if (!containsAllKeys) {
            LOGGER.info("Configuration is malformed!");
            saveMalformedConfig();
            copyDefaultConfiguration();
            return Optional.empty();
        }
        if(!checkAddressAndUsername(config)){
            return Optional.empty();
        }

        try {
            Integer.parseInt(config.getProperty("rpc-db-port"));
        } catch (NumberFormatException exception) {
            LOGGER.info("The configured port is invalid! Please provide an integer value.");
            return Optional.empty();
        }

        return Optional.of(config);
    }

    private boolean checkAddressAndUsername(Properties config){
        String databaseAddress = config.getProperty("db-prefix") + config.getProperty("db-url")
                + config.getProperty("db-name");

        if (databaseAddress.length() == 0) {
            LOGGER.error("The configured address is empty!");
            return false;
        } else if (config.getProperty("db-username").length() == 0) {
            LOGGER.error("The configured username is empty!");
            return false;
        }
        LOGGER.debug("Database address: \"{}\"", databaseAddress);
        return true;
    }

    private boolean validateConfigurationFile() throws IOException {
        if (!Files.exists(configurationFilePath)) {
            LOGGER.warn("No server configuration found!");
            copyDefaultConfiguration();
            return false;
        } else {
            LOGGER.debug("A configuration has been found!");
            return true;
        }
    }

    private void copyDefaultConfiguration() throws IOException {
        InputStream databaseDefaultConfig = CalculatorConfiguration.class.getClassLoader()
                .getResourceAsStream(DEFAULT_CONFIGURATION_FILE);
        assert databaseDefaultConfig != null;
        Files.copy(databaseDefaultConfig, configurationFilePath, StandardCopyOption.REPLACE_EXISTING);
        databaseDefaultConfig.close();
        LOGGER.info("Generated the default configuration in \"{}\" file!", configurationFilePath.getFileName());
        LOGGER.info("Please edit this file before restarting this app!");
    }

    private void saveMalformedConfig() throws IOException {
        int counter = 0;
        Path cwd = Path.of(System.getProperty("user.dir"));
        String backupFileName = "config-backup.properties." + counter;
        while (cwd.resolve(backupFileName).toFile().exists()) {
            counter++;
            backupFileName = "config-backup.properties." + counter;
        }

        Files.copy(configurationFilePath, cwd.resolve(backupFileName));
        LOGGER.info("Saved the malformed configuration into \"{}\"", backupFileName);
    }
}
