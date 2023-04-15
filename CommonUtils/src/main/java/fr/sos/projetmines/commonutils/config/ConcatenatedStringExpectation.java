package fr.sos.projetmines.commonutils.config;

public class ConcatenatedStringExpectation extends StringExpectation {


    /**
     * @param minimalLength minimal acceptable concatenated string length. Set 0 to remove the constraint
     * @param maximalLength maximal acceptable concatenated string length. Set 0 to remove the constraint
     * @param keyNames      names of the configuration keys to concatenate
     */
    public ConcatenatedStringExpectation(int minimalLength, int maximalLength, String... keyNames) {
        super(String.join("+", keyNames), minimalLength, maximalLength);
    }

    /**
     * All the provided strings are concatenated before
     * @param comparables Strings to concatenate
     * @return whether the concatenated strings comply with the constraints
     */
    public boolean isConform(String... comparables) {
        return isConform(String.join("", comparables));
    }

    @Override
    public String getConformityConditions() {
        return String.format("Expected concatenated keys \"%s\" is invalid! Its value must be existing%s%s.",
                keyName,
                minimalLength > 0 ? String.format(", longer than %d characters", minimalLength) : "",
                maximalLength != Integer.MAX_VALUE ? String.format(", shorter than %d characters", maximalLength) : ""
        );
    }
}
