package fr.sos.projetmines.calculator.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrowanInputDataRange {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrowanInputDataRange.class);

    private final String constraintName;
    private float minValue, maxValue;

    public OrowanInputDataRange(String constraintName, float minValue, float maxValue) {
        this.constraintName = constraintName;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public boolean isValid(float value){
        boolean bool = value >= minValue && value <= maxValue;
        if(!bool){
            LOGGER.debug("Constraint {} not respected ({} <= x <= {}): {}", constraintName, minValue, maxValue, value);
        }
        return bool;
    }
}
