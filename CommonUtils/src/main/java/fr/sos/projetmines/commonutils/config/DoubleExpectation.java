package fr.sos.projetmines.commonutils.config;

public class DoubleExpectation extends ConfigurationExpectation {


    /**
     * Minimal value of the double
     */
    private final double minimalValue;

    /**
     * Maximal value of the double
     */
    private final double maximalValue;

    /**
     * @param keyName      name of the configuration key
     * @param minimalValue minimal acceptable value. Set {@link Integer#MIN_VALUE} to remove the constraint
     * @param maximalValue maximal acceptable value. Set {@link Integer#MAX_VALUE} to remove the constraint
     */
    public DoubleExpectation(String keyName, double minimalValue, double maximalValue) {
        super(keyName);
        this.minimalValue = minimalValue;
        this.maximalValue = maximalValue;
    }

    /**
     * The configured value as String is parsed to int and checked depending on the fixed conditions
     */
    @Override
    public boolean isConform(String comparable) {
        if (comparable == null || comparable.length() == 0)
            return false;

        double number;
        try {
            number = Double.parseDouble(comparable);
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
