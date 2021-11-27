package handWavey;

import handWavey.*;
import config.*;

public class HandSummary {
    private int id;

    private double handX;
    private double handY;
    private double handZ;

    private double handRoll;
    private double handPitch;
    private double handYaw;

    private double armRoll;
    private double armPitch;
    private double armYaw;

    private Boolean handClosed;

    private Boolean valid = true;

    public HandSummary(int id){
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public void setHandPosition(double x, double y, double z) {
        this.handX = x;
        this.handY = y;
        this.handZ = z;
    }

    public double getHandX() {
        return this.handX;
    }

    public double getHandY() {
        return this.handY;
    }

    public double getHandZ() {
        return this.handZ;
    }

    public void setHandAngles(double roll, double pitch, double yaw) {
        this.handRoll = roll;
        this.handPitch = pitch;
        this.handYaw = yaw;
    }

    public double getHandRoll() {
        return this.handRoll;
    }

    public double getHandPitch() {
        return this.handPitch;
    }

    public double getHandYaw() {
        return this.handYaw;
    }

    public void setArmAngles(double roll, double pitch, double yaw) {
        this.armRoll = roll;
        this.armPitch = pitch;
        this.armYaw = yaw;
    }

    public double getArmRoll() {
        return this.armRoll;
    }

    public double getArmPitch() {
        return this.armPitch;
    }

    public double getArmYaw() {
        return this.armYaw;
    }

    public void setHandClosed(Boolean state) {
        this.handClosed = state;
    }

    public Boolean handIsClosed() {
        return this.handClosed;
    }

    public Boolean isValid() {
        return this.valid;
    }

    public void markInvalid() {
        this.valid = false;
    }
}
