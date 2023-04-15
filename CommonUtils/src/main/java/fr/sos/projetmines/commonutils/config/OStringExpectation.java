package fr.sos.projetmines.commonutils.config;

public class OStringExpectation extends OConfigurationExpectation{

    /**
     * Minimal length of the string
     */
    protected final int minimalLength;

    /**
     * Maximal length of the string
     */
    protected final int maximalLength;

    /**
     * @param keyName       name of the configuration key
     * @param minimalLength minimal acceptable string length. Set 0 to remove the constraint
     * @param maximalLength maximal acceptable string length. Set 0 to remove the constraint
     */
    public OStringExpectation(String keyName, int minimalLength, int maximalLength) {
        super(keyName);
        this.minimalLength = minimalLength;
        this.maximalLength = maximalLength <= 0 ? Integer.MAX_VALUE : maximalLength;
    }

    @Override
    public boolean isConform(String comparable) {
        return comparable != null
                && comparable.length() >= minimalLength
                && comparable.length() <= maximalLength;
    }

    @Override
    public String getConformityConditions() {
        return String.format("Expected key \"%s\" is invalid! Its value must be existing%s%s.",
                keyName,
                minimalLength > 0 ? String.format(", longer than %d characters", minimalLength) : "",
                maximalLength != Integer.MAX_VALUE ? String.format(", shorter than %d characters", maximalLength) : ""
        );
    }
}
