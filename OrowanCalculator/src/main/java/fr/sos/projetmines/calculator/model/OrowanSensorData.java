package fr.sos.projetmines.calculator.model;

public class OrowanSensorData {

    private final int standId;
    private final int lp;
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
    private final long xTime;


    public OrowanSensorData(int standId, int lp, float entryThickness, float exitThickness, float entryTension,
                            float exitTension, float workRollDiameter, float youngModulus, float averageSigma,
                            float mu, float rollForce, float forwardSlip, long xTime) {
        this.standId = standId;
        this.lp = lp;
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
    }

    public int getStandId() {
        return standId;
    }

    public int getLp() {
        return lp;
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

    public long getXTime() {
        return xTime;
    }
}
