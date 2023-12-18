// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
For cleaning the input hand data that comes in via HandSummary objects.
*/

package handWavey;

import config.*;
import dataCleaner.MovingMean;

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

    private float pi = (float)3.1415926536;

    private float openThreshold = 0;

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

        movingMeanX = new MovingMean(movingMeanLengthX, 0);
        movingMeanY = new MovingMean(movingMeanLengthY, 0);
        movingMeanZ = new MovingMean(movingMeanLengthZ, 0);

        movingMeanRoll = new MovingMean(movingMeanLengthRoll, 0);
        movingMeanPitch = new MovingMean(movingMeanLengthPitch, 0);
        movingMeanYaw = new MovingMean(movingMeanLengthYaw, 0);

        movingMeanFinger = new MovingMean(movingMeanLengthFinger, 0);
    }

    public void updateHand(HandSummary handSummary) {
        if (!absent) {
            // Normal flow.
            movingMeanX.set(handSummary.getHandX());
            movingMeanY.set(handSummary.getHandY());
            movingMeanZ.set(handSummary.getHandZ());

            movingMeanRoll.set(handSummary.getHandRoll());
            movingMeanPitch.set(handSummary.getHandPitch());
            movingMeanYaw.set(handSummary.getHandYaw());

            movingMeanFinger.set(handSummary.getFingerAngle());
        } else {
            // No longer absent. Let's reset the means.
            absent = false;
            movingMeanX.seed(handSummary.getHandX());
            movingMeanY.seed(handSummary.getHandY());
            movingMeanZ.seed(handSummary.getHandZ());

            movingMeanRoll.seed(handSummary.getHandRoll());
            movingMeanPitch.seed(handSummary.getHandPitch());
            movingMeanYaw.seed(handSummary.getHandYaw());

            movingMeanFinger.seed(handSummary.getFingerAngle());
        }

        // Figure out whether the hand is open or closed.
        double relativeFingerPitch = mangleAngle(movingMeanFinger.get()) + movingMeanPitch.get();
        double fingerDifference = Math.abs(relativeFingerPitch);
        Boolean handOpen = (fingerDifference < openThreshold);
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
        return movingMeanRoll.get();
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
}
