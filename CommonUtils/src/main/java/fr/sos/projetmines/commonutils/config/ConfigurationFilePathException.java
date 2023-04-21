package fr.sos.projetmines.commonutils.config;

public class ConfigurationFilePathException extends Exception {

    /**
     * @param configFile defines whether the configuration file path or default configuration file path is null
     *                   Set to true for the configuration file, to false for the default configuration file
     */
    public ConfigurationFilePathException(boolean configFile) {
        super(String.format("Provided file path for%s configuration is null or empty! Couldn't load the configuration",
                configFile ? "" : " default"));
    }
}
