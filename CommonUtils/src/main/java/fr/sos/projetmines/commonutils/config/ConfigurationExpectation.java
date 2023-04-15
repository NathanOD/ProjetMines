package fr.sos.projetmines.commonutils.config;

public abstract class ConfigurationExpectation {

    /**
     * Configured key name
     */
    protected final String keyName;

    /**
     * @param key name of the key
     */
    protected ConfigurationExpectation(String key) {
        this.keyName = key;
    }

    /**
     * @return the expected key name
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * @param comparable String value to test (May be parsed)
     * @return whether the value is conformed to the specified constraints
     */
    public abstract boolean isConform(String comparable);

    /**
     * @return the confirmity conditions as a human-readable string
     */
    public abstract String getConformityConditions();
}
