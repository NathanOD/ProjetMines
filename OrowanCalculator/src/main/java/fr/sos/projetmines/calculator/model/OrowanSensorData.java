package fr.sos.projetmines.calculator.model;

import fr.sos.projetmines.calculator.OrowanCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class OrowanSensorData {

    private static final Map<String, OrowanInputDataRange> constraints
            = OrowanCalculator.getInstance().getDataFormatter().getInputDataRanges();

    private final int standId;
    private final int lp;
    private final float xLoc;
    private final float entryThickness;
    private final float exitThickness;
    private final float entryTension;
    private final float exitTension;
    private final float workRollDiameter;
    private final float youngModulus;
    private final float averageSigma;
    private final float mu;
    private final float rollForce;
    private final float forwardSlip;
    private final float xTime;

    private final float rollSpeed;


    public OrowanSensorData(int standId, int lp, float xLoc, float entryThickness, float exitThickness, float entryTension,
                            float exitTension, float workRollDiameter, float youngModulus, float averageSigma,
                            float mu, float rollForce, float forwardSlip, float xTime, float rollSpeed) {
        this.standId = standId;
        this.lp = lp;
        this.xLoc = xLoc;
        this.entryThickness = entryThickness;
        this.exitThickness = exitThickness;
        this.entryTension = entryTension;
        this.exitTension = exitTension;
        this.workRollDiameter = workRollDiameter;
        this.youngModulus = youngModulus;
        this.averageSigma = averageSigma;
        this.mu = mu;
        this.rollForce = rollForce;
        this.forwardSlip = forwardSlip;
        this.xTime = xTime;
        this.rollSpeed = rollSpeed;
    }

    public int getStandId() {
        return standId;
    }

    public int getLp() {
        return lp;
    }

    public float getXLoc(){ return xLoc; }

    public float getEntryThickness() {
        return entryThickness;
    }

    public float getExitThickness() {
        return exitThickness;
    }

    public float getEntryTension() {
        return entryTension;
    }

    public float getExitTension() {
        return exitTension;
    }

    public float getWorkRollDiameter() {
        return workRollDiameter;
    }

    public float getYoungModulus() {
        return youngModulus;
    }

    public float getAverageSigma() {
        return averageSigma;
    }

    public float getMu() {
        return mu;
    }

    public float getRollForce() {
        return rollForce;
    }

    public float getForwardSlip() {
        return forwardSlip;
    }

    public float getXTime() {
        return xTime;
    }

    public float getRollSpeed() {
        return rollSpeed;
    }

    public boolean respectsConstraints(){
        return constraints.get("x_loc").isValid(xLoc) &&
                constraints.get("entry_thickness").isValid(entryThickness) &&
                constraints.get("exit_thickness").isValid(exitThickness) &&
                constraints.get("entry_tension").isValid(entryTension) &&
                constraints.get("exit_tension").isValid(exitTension) &&
                constraints.get("roll_force").isValid(rollForce) &&
                constraints.get("forward_slip").isValid(forwardSlip) &&
                constraints.get("work_roll_diameter").isValid(workRollDiameter) &&
                constraints.get("young_modulus").isValid(youngModulus) &&
                constraints.get("mu").isValid(mu);
    }
}
