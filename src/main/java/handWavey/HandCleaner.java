// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
For cleaning the input hand data that comes in via HandSummary objects.
*/

package handWavey;

import config.*;
import dataCleaner.MovingMean;
import java.util.Date;

public class HandCleaner {
    private MovingMean movingMeanX = null;
    private MovingMean movingMeanY = null;
    private MovingMean movingMeanZ = null;

    private MovingMean movingMeanRoll = null;
    private MovingMean movingMeanPitch = null;
    private MovingMean movingMeanYaw = null;

    private MovingMean movingMeanFinger = null;

    private int handState = Gesture.absent;

    private Boolean absent = true;
    private Boolean isLeft = true;

    private float pi = (float)3.1415926536;

    private float openThreshold = 0;

    private double trimRoll = 0;
    private double maxChangePerSecond = 0;
    private double maxChange = 0;
    private long lastChangeTime = 0;

    private double speed = 0;
    private long lastSubmissionTime = 0;
    private double stationarySpeed = 0;

    private Boolean gesturesLocked = false;

    public HandCleaner() {
        Group ultraMotionConfig = Config.singleton().getGroup("ultraMotion");
        this.openThreshold = Float.parseFloat(ultraMotionConfig.getItem("openThreshold").get());

        Group handCleaner = Config.singleton().getGroup("handCleaner");
        int movingMeanLengthX = Integer.parseInt(handCleaner.getItem("movingMeanX").get());
        int movingMeanLengthY = Integer.parseInt(handCleaner.getItem("movingMeanY").get());
        int movingMeanLengthZ = Integer.parseInt(handCleaner.getItem("movingMeanZ").get());

        int movingMeanLengthRoll = Integer.parseInt(handCleaner.getItem("movingMeanRoll").get());
        int movingMeanLengthPitch = Integer.parseInt(handCleaner.getItem("movingMeanPitch").get());
        int movingMeanLengthYaw = Integer.parseInt(handCleaner.getItem("movingMeanYaw").get());

        int movingMeanLengthFinger = Integer.parseInt(handCleaner.getItem("movingMeanFinger").get());

        maxChangePerSecond = Double.parseDouble(handCleaner.getItem("autoTrimMaxChangePerSecond").get());
        maxChange = Double.parseDouble(handCleaner.getItem("autoTrimMaxChange").get());

        stationarySpeed = Double.parseDouble(handCleaner.getItem("stationarySpeed").get());

        lastChangeTime = timeInMilliseconds();

        movingMeanX = new MovingMean(movingMeanLengthX, 0);
        movingMeanY = new MovingMean(movingMeanLengthY, 0);
        movingMeanZ = new MovingMean(movingMeanLengthZ, 0);

        movingMeanRoll = new MovingMean(movingMeanLengthRoll, 0);
        movingMeanPitch = new MovingMean(movingMeanLengthPitch, 0);
        movingMeanYaw = new MovingMean(movingMeanLengthYaw, 0);

        movingMeanFinger = new MovingMean(movingMeanLengthFinger, 0);
    }

    public void updateHand(HandSummary handSummary) {
        // Check which hand we have.
        Boolean isLeftNow = handSummary.handIsLeft();
        if (isLeftNow.equals(isLeft)) {
            resetAutoTrim();
        }
        isLeft = isLeftNow;

        // Calculate relative finger to hand positions.
        double relativeFingerPitch = mangleAngle(handSummary.getFingerAngle()) + handSummary.getHandPitch();
        double fingerDifference = Math.abs(relativeFingerPitch);

        if (!absent) {
            // Normal flow.

            // Collect initial state for later distance calculations.
            double xStart = movingMeanX.get();
            double yStart = movingMeanY.get();

            // Update the values.
            movingMeanX.set(handSummary.getHandX());
            movingMeanY.set(handSummary.getHandY());
            movingMeanZ.set(handSummary.getHandZ());

            if (!this.gesturesLocked) {
                movingMeanRoll.set(handSummary.getHandRoll());
                movingMeanPitch.set(handSummary.getHandPitch());
                movingMeanYaw.set(handSummary.getHandYaw());

                movingMeanFinger.set(fingerDifference);
            }

            // Do the speed calculations.
            long now = timeInMilliseconds();
            long elapsed = now - lastSubmissionTime;
            lastSubmissionTime = now;

            double xChange = movingMeanX.get() - xStart;
            double yChange = movingMeanY.get() - yStart;
            double cChange = Math.pow((Math.pow(xChange, 2) + Math.pow(yChange, 2)), 0.5);

            speed = cChange / elapsed * 100F;
        } else {
            // No longer absent. Let's reset the means.

            // Reset all values to the current ones. We don't want old data.
            absent = false;
            movingMeanX.seed(handSummary.getHandX());
            movingMeanY.seed(handSummary.getHandY());
            movingMeanZ.seed(handSummary.getHandZ());

            movingMeanRoll.seed(handSummary.getHandRoll());
            movingMeanPitch.seed(handSummary.getHandPitch());
            movingMeanYaw.seed(handSummary.getHandYaw());

            movingMeanFinger.seed(fingerDifference);

            resetAutoTrim();

            // Prepare for the speed calculations in the next iteration.
            lastSubmissionTime = timeInMilliseconds();
            speed = 0;

            gesturesLocked = false;
        }

        // Figure out whether the hand is open or closed.
        Boolean handOpen = (movingMeanFinger.get() < openThreshold);
        this.handState = (handOpen)?Gesture.open:Gesture.closed;
    }

    private double mangleAngle(double angle) {
        // Moves the center by 180 degrees. I didn't get rotation working reliably.
        // TODO Revisit whether getting rotation working reliably would be a better solution. There must be no jump when rotating past the boundary where an individual angle loops.

        int sign = (angle < 0)?-1:1;
        double invertedValue = this.pi - Math.abs(angle);

        return invertedValue * (double)sign;
    }

    public void setAbsent() {
        absent = true;
    }

    public int getState() {
        return handState;
    }

    public double getHandX() {
        return movingMeanX.get();
    }

    public double getHandY() {
        return movingMeanY.get();
    }

    public double getHandZ() {
        return movingMeanZ.get();
    }

    public double getHandRoll() {
        return movingMeanRoll.get() + trimRoll;
    }

    public double getHandPitch() {
        return movingMeanPitch.get();
    }

    public double getHandYaw() {
        return movingMeanYaw.get();
    }

    public double getFingerAngle() {
        return movingMeanFinger.get();
    }

    private long timeInMilliseconds() {
        Date date = new Date();
        return date.getTime();
    }

    public void autoTrim(double distance) {
        if (maxChangePerSecond == 0) return;

        long now = this.timeInMilliseconds();
        long elapsed = now - lastChangeTime;
        lastChangeTime = timeInMilliseconds();
        double seconds = elapsed / 1000F;

        double distancePerSecond = distance / seconds;

        // Check that it's not happening too fast.
        if (Math.abs(distancePerSecond) > maxChangePerSecond) {
            if (distance >= 0) {
                distance = maxChangePerSecond * seconds;
            } else {
                distance = maxChangePerSecond * seconds * -1;
            }
        }

        // Are we using the right hand?
        if (!isLeft) distance *= -1;

        // Apply the change.
        trimRoll += distance;

        // Check for OOB.
        if (trimRoll > maxChange) {
            trimRoll = maxChange;
        }
        if (trimRoll * -1 > maxChange) {
            trimRoll = maxChange * -1;
        }
    }

    private void resetAutoTrim() {
        trimRoll = 0;
        lastChangeTime = timeInMilliseconds();
    }

    public double getSpeed() {
        return speed;
    }

    public Boolean isStationary() {
        if (stationarySpeed < 0) return true; // Setting stationarySpeed to -1 effectively disables this feature.
        return (speed < stationarySpeed);
    }

    public void setGestureLock(Boolean desiredState) {
        this.gesturesLocked = desiredState;
    }
}
