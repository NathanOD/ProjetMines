package fr.sos.projetmines.calculator.model;

public class OrowanDataOutput {

     private final int caseId ;
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

     public OrowanDataOutput(int caseId, String errors, float offset_yield, float friction, float rolling_Torque,
                             float sigma_moy, float sigma_ini, float sigma_out, float sigma_max, float force_error,
                             float slip_error, String has_converged) {
          this.caseId = caseId;
          this.errors = errors;
          this.offsetYield = offset_yield;
          this.friction = friction;
          this.rollingTorque = rolling_Torque;
          this.sigmaMoy = sigma_moy;
          this.sigmaIni = sigma_ini;
          this.sigmaOut = sigma_out;
          this.sigmaMax = sigma_max;
          this.forceError = force_error;
          this.slipError = slip_error;
          this.hasConverged = has_converged;
     }

     public int getCaseId() {
          return caseId;
     }

     public String getErrors() { return errors; }

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

     public void setComputationTime(long newComputationTime){
          this.computationTime = newComputationTime;
     }

     public long getRollSpeed() {
          return computationTime;
     }

     public void setRollSpeed(float newRollSpeed){
          this.rollSpeed = newRollSpeed;
     }
}
