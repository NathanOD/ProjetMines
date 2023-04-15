package fr.sos.projetmines.commonutils.config;

public class OIntExpectation extends OConfigurationExpectation {


    /**
     * Minimal value of the integer
     */
    private final int minimalValue;

    /**
     * Maximal value of the integer
     */
    private final int maximalValue;

    /**
     * @param keyName      name of the configuration key
     * @param minimalValue minimal acceptable value. Set {@link Integer#MIN_VALUE} to remove the constraint
     * @param maximalValue maximal acceptable value. Set {@link Integer#MAX_VALUE} to remove the constraint
     */
    public OIntExpectation(String keyName, int minimalValue, int maximalValue) {
        super(keyName);
        this.minimalValue = minimalValue;
        this.maximalValue = maximalValue;
    }

    @Override
    public boolean isConform(String comparable) {
        if (comparable == null || comparable.length() == 0)
            return false;

        int number;
        try {
            number = Integer.parseInt(comparable);
        } catch (NumberFormatException exception) {
            return false;
        }
        return number >= minimalValue && number <= maximalValue;
    }

    @Override
    public String getConformityConditions() {
        return String.format("Expected key \"%s\" is invalid! Its value needs to be an integer%s%s.",
                keyName,
                minimalValue != Integer.MIN_VALUE ? String.format(", greater than %s", minimalValue) : "",
                maximalValue != Integer.MAX_VALUE ? String.format(", lesser than %s", maximalValue) : ""
        );
    }
}
