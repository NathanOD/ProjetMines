package fr.sos.projetmines.inputsimulator;

public class Strip {
    private final int stripId;
    private final float workRollDiameter;
    private final float rolledLength;
    private final float youngModulus;
    private final float backupRollDiameter;
    private final float backupRolledLength;

    public Strip(int stripId, float workRollDiameter, float rolledLength, float youndModulus, float backupRollDiameter, float backupRolledLength) {
        this.stripId = stripId;
        this.workRollDiameter = workRollDiameter;
        this.rolledLength = rolledLength;
        this.youngModulus = youndModulus;
        this.backupRollDiameter = backupRollDiameter;
        this.backupRolledLength = backupRolledLength;
    }

    public int getStripId() {
        return stripId;
    }

    public float getWorkRollDiameter() {
        return workRollDiameter;
    }

    public float getRolledLength() {
        return rolledLength;
    }

    public float getYoungModulus() {
        return youngModulus;
    }

    public float getBackupRollDiameter() {
        return backupRollDiameter;
    }

    public float getBackupRolledLength() {
        return backupRolledLength;
    }
}
