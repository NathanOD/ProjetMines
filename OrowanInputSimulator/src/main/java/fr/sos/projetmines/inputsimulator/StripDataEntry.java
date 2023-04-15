package fr.sos.projetmines.inputsimulator;

public class StripDataEntry {

    private final Strip strip;
    private final float inputError;
    private final int lp;
    private final int matId;
    private final float xTime;
    private final float xLoc;
    private final float entryThickness;
    private final float exitThickness;
    private final float entryTension;
    private final float exitTension;
    private final float rollForce;
    private final float forwardSlip;
    private final float mu;
    private final float torque;
    private final float averageSigma;
    private final float waterFlowRateTop;
    private final float waterFlowRateBottom;
    private final float oilFlowRateTop;
    private final float oilFlowRateBottom;
    private final float rollSpeed;


    public StripDataEntry(Strip strip, float inputError, int lp, int matId, float xTime, float xLoc,
                          float entryThickness, float exitThickness, float entryTension, float exitTension,
                          float rollForce, float forwardSlip, float mu, float torque, float averageSigma,
                          float waterFlowRateTop, float waterFlowRateBottom, float oilFlowRateTop,
                          float oilFlowRateBottom, float rollSpeed) {
        this.strip = strip;
        this.inputError = inputError;
        this.lp = lp;
        this.matId = matId;
        this.xTime = xTime;
        this.xLoc = xLoc;
        this.entryThickness = entryThickness;
        this.exitThickness = exitThickness;
        this.entryTension = entryTension;
        this.exitTension = exitTension;
        this.rollForce = rollForce;
        this.forwardSlip = forwardSlip;
        this.mu = mu;
        this.torque = torque;
        this.averageSigma = averageSigma;
        this.waterFlowRateTop = waterFlowRateTop;
        this.waterFlowRateBottom = waterFlowRateBottom;
        this.oilFlowRateTop = oilFlowRateTop;
        this.oilFlowRateBottom = oilFlowRateBottom;
        this.rollSpeed = rollSpeed;
    }

    public Strip getStrip() {
        return strip;
    }

    public float getInputError() {
        return inputError;
    }

    public int getLp() {
        return lp;
    }

    public int getMatId() {
        return matId;
    } //Equals to strip.strip_id

    public float getXTime() {
        return xTime;
    }

    public float getXLoc() {
        return xLoc;
    }

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

    public float getRollForce() {
        return rollForce;
    }

    public float getForwardSlip() {
        return forwardSlip;
    }

    public float getMu() {
        return mu;
    }

    public float getTorque() {
        return torque;
    }

    public float getAverageSigma() {
        return averageSigma;
    }

    public float getWaterFlowRateTop() {
        return waterFlowRateTop;
    }

    public float getWaterFlowRateBottom() {
        return waterFlowRateBottom;
    }

    public float getOilFlowRateTop() {
        return oilFlowRateTop;
    }

    public float getOilFlowRateBottom() {
        return oilFlowRateBottom;
    }

    public float getRollSpeed() {
        return rollSpeed;
    }
}
