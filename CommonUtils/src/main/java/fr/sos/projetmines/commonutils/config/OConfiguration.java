package fr.sos.projetmines.commonutils.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OConfiguration {

    /**
     * OConfiguration default logger
     */
    private final static Logger LOGGER = Logger.getLogger(OConfiguration.class.getName());

    /**
     * Path of the configuration file
     */
    private final Path configurationFilePath;

    /**
     * Path of the default configuration file
     */
    private final String defaultConfigurationFilePath;


    /**
     * Set of the values constraints
     */
    private final Set<OConfigurationExpectation> expectedValues;

    /**
     * Loaded configuration (initialized by the {@link #loadConfiguration()} method
     */
    private final Properties config;

    /**
     * @param configurationFilePath        path of the configuration file (outside jar archive)
     * @param defaultConfigurationFilePath path of the default configuration file (inside jar archive, used for copy)
     * @param expectedValues               Expected configurations for the keys
     * @throws ConfigurationFilePathException when a path is misconfigured
     */
    public OConfiguration(Path configurationFilePath, String defaultConfigurationFilePath,
                          OConfigurationExpectation... expectedValues) throws ConfigurationFilePathException {
        this(configurationFilePath, defaultConfigurationFilePath, Set.of(expectedValues));
    }

    /**
     * @param configurationFilePath        path of the configuration file (outside jar archive)
     * @param defaultConfigurationFilePath path of the default configuration file (inside jar archive, used for copy)
     * @param expectedValues               Expected configurations for the keys
     * @throws ConfigurationFilePathException when a path is misconfigured
     */
    public OConfiguration(Path configurationFilePath, String defaultConfigurationFilePath,
                          Set<OConfigurationExpectation> expectedValues) throws ConfigurationFilePathException {
        this.configurationFilePath = configurationFilePath;
        if (configurationFilePath == null) {
            throw new ConfigurationFilePathException(true);
        }
        this.defaultConfigurationFilePath = defaultConfigurationFilePath;
        if (defaultConfigurationFilePath == null || defaultConfigurationFilePath.length() == 0) {
            throw new ConfigurationFilePathException(false);
        }
        this.config = new Properties();
        this.expectedValues = expectedValues;
    }

    /**
     * Checks for the configuration file existence. Three cases are available:
     * - Configuration file does not exist: File is created and filled with the content of the default configuration file
     * - Configuration file exists but the configuration has an error: The configuration is backed up
     * and the default configuration is written into the file
     * - Configuration file exists and is well configured: does nothing
     *
     * @return whether the configuration file exists and is well configured
     * @throws IOException if the file existence could not be checked
     */
    public boolean validateConfiguration() throws IOException {
        if (!Files.exists(configurationFilePath)) {
            LOGGER.info("No configuration file found!");
            copyDefaultConfiguration();
            return false;
        }
        loadConfiguration();

        if (!validateKeys()) {
            backupMalformedConfig();
            copyDefaultConfiguration();
            return false;
        }

        LOGGER.info("The configuration is correct and loaded!");
        return true;
    }

    /**
     * Tries to load the configuration file at the provided file path
     *
     * @return whether the file could be opened
     */
    private boolean loadConfiguration() {
        try {
            InputStream configStream = new FileInputStream(configurationFilePath.toFile());
            config.load(configStream);
            configStream.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Validates each configured key by checking their conformity
     * Logs the configuration expectations for the misconfigured keys
     *
     * @return whether all keys are well configured
     */
    private boolean validateKeys() {
        assert config != null;
        boolean isValid = true;
        for (OConfigurationExpectation expectation : expectedValues) {
            String value;
            if (expectation instanceof OConcatenatedStringExpectation) {
                value = getConcatenatedValue(expectation.getKeyName().split("\\+"));
            } else {
                value = config.getProperty(expectation.getKeyName(), null);
            }
            if (!expectation.isConform(value)) {
                isValid = false;
                LOGGER.info(expectation.getConformityConditions());
            }
        }
        return isValid;
    }

    /**
     * Generates the default configuration file at the provided configuration path
     *
     * @throws IOException when the file could not be generated
     */
    private void copyDefaultConfiguration() throws IOException {
        InputStream databaseDefaultConfig = OConfiguration.class.getClassLoader()
                .getResourceAsStream(defaultConfigurationFilePath.toString());
        assert databaseDefaultConfig != null;
        Files.copy(databaseDefaultConfig, configurationFilePath, StandardCopyOption.REPLACE_EXISTING);
        databaseDefaultConfig.close();
        LOGGER.info(String.format("Generated the default configuration in \"%s\" file!",
                configurationFilePath.getFileName().toString()));
        LOGGER.info("Please edit this file before restarting this app!");
    }

    /**
     * Saves the malformed configuration into the same file name with -backup
     *
     * @throws IOException when the file could not be copied
     */
    private void backupMalformedConfig() throws IOException {
        assert configurationFilePath.toFile().exists();
        int counter = -1;
        Path cwd = Path.of(System.getProperty("user.dir"));
        String backupFileName;
        do {
            counter++;
            backupFileName = configurationFilePath.toString()
                    .replace(".p", String.format("-backup-%d.p", counter));
        } while (cwd.resolve(backupFileName).toFile().exists());

        Files.copy(configurationFilePath, cwd.resolve(backupFileName));
        LOGGER.info(String.format("Saved the malformed configuration into \"%s\"", backupFileName));
    }

    /**
     * Needs to be called after the validateConfiguration() method!
     *
     * @param key configuration key name
     * @return the value as a String
     */
    public String getStringValue(String key) {
        assert config != null;
        return config.getProperty(key);
    }

    /**
     * Needs to be called after the validateConfiguration() method!
     *
     * @param keys Name of the configuration keys to concatenate
     * @return the value as a String
     */
    public String getConcatenatedValue(String... keys) {
        assert config != null;
        return String.join("", Arrays.stream(keys)
                .map(key -> config.getProperty(key, null)).collect(Collectors.toSet()));
    }

    /**
     * Needs to be called after the validateConfiguration() method!
     * Parses to int without catching any error, please provide a configured int!
     *
     * @param key configuration key name
     * @return the value as an int
     */
    public int getIntValue(String key) {
        assert config != null;
        return Integer.parseInt(config.getProperty(key));
    }


    /**
     * Needs to be called after the validateConfiguration() method!
     * Parses to double without catching any error, please provide a configured double!
     *
     * @param key configuration key name
     * @return the value as a double
     */
    public double getDoubleValue(String key) {
        assert config != null;
        return Double.parseDouble(config.getProperty(key));
    }
}
