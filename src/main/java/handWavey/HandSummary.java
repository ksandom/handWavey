// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
A concise representation of the state of a hand. It only contains what we actually need.
*/

package handWavey;

import java.sql.Timestamp;
import java.lang.Math.*;

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

    private Boolean handOpen = true;
    private Boolean isLeft = true;

    private Boolean valid = true;
    private Boolean oob = false;

    private Config config;

    private long lastUpdate = 0;
    private long oldHandsTimeout = 400;
    private long introducedTime = 0;
    private long eventFreezeFirstMillis = 1200;

    private String xMap = "x";
    private String yMap = "y";
    private String zMap = "z";

    private double xOffset = 0;
    private double yOffset = 0;
    private double zOffset = 0;

    private String rollRotationMap = "roll";
    private String pitchRotationMap = "pitch";
    private String yawRotationMap = "yaw";

    private double rollRotationOffset = 0;
    private double pitchRotationOffset = 0;
    private double yawRotationOffset = 0;

    private final double upper = Math.PI;
    private final double lower = Math.PI * -1;
    private final double oneRotation = Math.PI * 2;


    public HandSummary(int id){
        this.config = Config.singleton();
        this.id = id;

        this.oldHandsTimeout = Long.parseLong(this.config.getGroup("dataCleaning").getGroup("newHands").getItem("oldHandsTimeout").get());
        this.eventFreezeFirstMillis = Long.parseLong(this.config.getGroup("dataCleaning").getGroup("newHands").getItem("eventFreezeFirstMillis").get());

        this.xMap = this.config.getGroup("physicalBoundaries").getGroup("map").getItem("x").get();
        this.yMap = this.config.getGroup("physicalBoundaries").getGroup("map").getItem("y").get();
        this.zMap = this.config.getGroup("physicalBoundaries").getGroup("map").getItem("z").get();

        this.xOffset = Long.parseLong(this.config.getGroup("physicalBoundaries").getGroup("inputOffsets").getItem("x").get());
        this.yOffset = Long.parseLong(this.config.getGroup("physicalBoundaries").getGroup("inputOffsets").getItem("y").get());
        this.zOffset = Long.parseLong(this.config.getGroup("physicalBoundaries").getGroup("inputOffsets").getItem("z").get());

        this.rollRotationMap = this.config.getGroup("physicalBoundaries").getGroup("rotationMap").getItem("roll").get();
        this.pitchRotationMap = this.config.getGroup("physicalBoundaries").getGroup("rotationMap").getItem("pitch").get();
        this.yawRotationMap = this.config.getGroup("physicalBoundaries").getGroup("rotationMap").getItem("yaw").get();

        this.rollRotationOffset = Double.parseDouble(this.config.getGroup("physicalBoundaries").getGroup("inputRotationOffsets").getItem("roll").get());
        this.pitchRotationOffset = Double.parseDouble(this.config.getGroup("physicalBoundaries").getGroup("inputRotationOffsets").getItem("pitch").get());
        this.yawRotationOffset = Double.parseDouble(this.config.getGroup("physicalBoundaries").getGroup("inputRotationOffsets").getItem("yaw").get());
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
        double mappedX = getMapped(this.xMap, x, y, z);
        double mappedY = getMapped(this.yMap, x, y, z);
        double mappedZ = getMapped(this.zMap, x, y, z);

        if (this.handX != mappedX || this.handY != mappedY || this.handZ != mappedZ) markUpdated();

        this.handX = mappedX;
        this.handY = mappedY;
        this.handZ = mappedZ;
    }

    private double getMapped(String axis, double x, double y, double z) {
        if (axis.equals("x")) {
            return x + xOffset;
        } else if (axis.equals("y")) {
            return y + yOffset;
        } else {
            return z + zOffset;
        }
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

        this.handRoll = getRotationMapped(this.rollRotationMap, roll, pitch, yaw);
        this.handPitch = getRotationMapped(this.pitchRotationMap, roll, pitch, yaw);
        this.handYaw = getRotationMapped(this.yawRotationMap, roll, pitch, yaw);
    }

    private double getRotationMapped(String axis, double roll, double pitch, double yaw) {
        if (axis.equals("roll")) {
            return protectAngle(roll + rollRotationOffset);
        } else if (axis.equals("pitch")) {
            return protectAngle(pitch + pitchRotationOffset);
        } else {
            return protectAngle(yaw + yawRotationOffset);
        }
    }

    private double protectAngle(double angle) {
        double result = angle;

        while (result > this.upper) {
            result = this.lower - (result - this.oneRotation);
        }

        while (result < this.lower) {
            result += this.oneRotation;
        }

        return result;
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
        if (!this.handOpen.equals(state)) markUpdated();
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
        if (!this.oob.equals(value)) markUpdated();
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
