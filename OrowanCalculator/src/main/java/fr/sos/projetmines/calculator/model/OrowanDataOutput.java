package fr.sos.projetmines.calculator.model;

public class OrowanDataOutput {

    private final int caseId;
    private final String errors;
    private final float offsetYield;
    private final float friction;
    private final float rollingTorque;
    private final float sigmaMoy;
    private final float sigmaIni;
    private final float sigmaOut;
    private final float sigmaMax;
    private final float forceError;
    private final float slipError;
    private final String hasConverged;
    private long computationTime;
    private float rollSpeed;
    private long xTime;

    public OrowanDataOutput(int caseId, String errors, float offsetYield, float friction, float rollingTorque,
                            float sigmaMoy, float sigmaIni, float sigmaOut, float sigmaMax, float forceError,
                            float slipError, String hasConverged) {
        this.caseId = caseId;
        this.errors = errors;
        this.offsetYield = offsetYield;
        this.friction = friction;
        this.rollingTorque = rollingTorque;
        this.sigmaMoy = sigmaMoy;
        this.sigmaIni = sigmaIni;
        this.sigmaOut = sigmaOut;
        this.sigmaMax = sigmaMax;
        this.forceError = forceError;
        this.slipError = slipError;
        this.hasConverged = hasConverged;
    }

    public int getCaseId() {
        return caseId;
    }

    public String getErrors() {
        return errors;
    }

    public float getOffsetYield() {
        return offsetYield;
    }

    public float getFriction() {
        return friction;
    }

    public float getRollingTorque() {
        return rollingTorque;
    }

    public float getSigmaMoy() {
        return sigmaMoy;
    }

    public float getSigmaIni() {
        return sigmaIni;
    }

    public float getSigmaOut() {
        return sigmaOut;
    }

    public float getSigmaMax() {
        return sigmaMax;
    }

    public float getForceError() {
        return forceError;
    }

    public float getSlipError() {
        return slipError;
    }

    public String getHasConverged() {
        return hasConverged;
    }

    public long getComputationTime() {
        return computationTime;
    }

    public void setComputationTime(long newComputationTime) {
        this.computationTime = newComputationTime;
    }

    public float getRollSpeed() {
        return rollSpeed;
    }

    public void setRollSpeed(float newRollSpeed) {
        this.rollSpeed = newRollSpeed;
    }

    public long getXTime() {
        return xTime;
    }

    public void setXTime(long xTime) {
        this.xTime = xTime;
    }
}
