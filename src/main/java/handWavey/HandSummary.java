// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
A concise representation of the state of a hand. It only contains what we actually need.
*/

package handWavey;

import java.sql.Timestamp;

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

    private Boolean handOpen;
    private Boolean isLeft;

    private Boolean valid = true;
    private Boolean oob = false;
    
    private Config config;
    
    private long lastUpdate = 0;
    private long oldHandsTimeout = 400;
    private long introducedTime = 0;
    private long eventFreezeFirstMillis = 1200;

    public HandSummary(int id){
        this.config = Config.singleton();
        this.id = id;
        
        this.oldHandsTimeout = Long.parseLong(this.config.getGroup("dataCleaning").getGroup("newHands").getItem("oldHandsTimeout").get());
        this.eventFreezeFirstMillis = Long.parseLong(this.config.getGroup("dataCleaning").getGroup("newHands").getItem("eventFreezeFirstMillis").get());
    }

    public String toString() {
        return "Hand XYZ: " +
            String.valueOf(this.handX) + ", " +
            String.valueOf(this.handY) + ", " +
            String.valueOf(this.handZ) + ".  " +
            "Hand Roll Pitch Yaw: " +
            String.valueOf(this.handRoll) + ", " +
            String.valueOf(this.handPitch) + ", " +
            String.valueOf(this.handYaw) + ".  " +
            "Arm Roll Pitch Yaw: " +
            String.valueOf(this.armRoll) + ", " +
            String.valueOf(this.armPitch) + ", " +
            String.valueOf(this.armYaw) + ".  " +
            "handOpen: " +
            String.valueOf(this.handOpen) + ". " +
            "valid: " +
            String.valueOf(this.valid) + ". ";
    }

    public int getID() {
        return this.id;
    }

    public void setHandPosition(double x, double y, double z) {
        if (this.handX != x || this.handY != y || this.handZ != z) markUpdated();
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
        if (this.handRoll != roll || this.handPitch != pitch || this.handYaw != yaw) markUpdated();
        this.handRoll = roll;
        this.handPitch = pitch;
        this.handYaw = yaw;
    }
    
    public void setHandIsLeft(Boolean isLeft) {
        this.isLeft = isLeft;
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
        if (this.armRoll != roll ||  this.armPitch != pitch || this.armYaw != yaw) markUpdated();
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

    public void setHandOpen(Boolean state) {
        if (this.handOpen != state) markUpdated();
        this.handOpen = state;
    }

    public Boolean handIsOpen() {
        return this.handOpen;
    }
    
    public Boolean handIsLeft() {
        return this.isLeft;
    }

    public Boolean isValid() {
        return (this.valid && (this.oob == false) && !frameIsOld());
    }

    public void markInvalid() {
        this.valid = false;
    }
    
    public void setOOB(Boolean value) {
        if (this.oob != value) markUpdated();
        this.oob = value;
    }
    
    private long now() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return now.getTime();
    }
    
    private Boolean frameIsOld() {
        Long lastFrameAge = now() - this.lastUpdate;
        
        return (lastFrameAge > this.oldHandsTimeout);
    }
    
    public Boolean handIsNew() {
        Long handAge = now() - this.introducedTime;
        
        return (handAge < this.eventFreezeFirstMillis);
    }
    
    public void clearNewHand() {
        this.introducedTime = now() - this.eventFreezeFirstMillis - 1;
    }
    
    private void markUpdated() {
        long now = now();
        
        if (frameIsOld()) {
            this.introducedTime = now;
        }
        
        this.lastUpdate = now;
    }
}
