// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
For cleaning the input hand data that comes in via HandSummary objects.
*/

package handWavey;

import config.*;
import debug.Debug;
import dataCleaner.MovingMean;
import dataCleaner.Consistently;
import java.util.Date;

public class HandCleaner {
    private Debug debug;

    String name = "Unknown";

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
    private double zSpeed = 0;
    private long lastSubmissionTime = 0;
    private double stationarySpeed = 0;

    private Boolean gesturesLocked = false;
    private Boolean tapsLocked = false;
    private long tapUnlockTime = 0;

    private double tapSpeed = 0;
    private Consistently consistentZPreTap = null;
    private Consistently consistentZTap = null;
    private Consistently consistentZPostTap = null;
    private Consistently consistentZOverflow = null;

    private int tapProgress = 0;

    private Boolean moveBeforeTaps = true;
    private Boolean hasMoved = false;
    private Boolean firstStationary = false;
    private Boolean moveBeforeTimedout = false;
    private long moveBeforeTimeout = 1500;
    private long minHandAge = 150;
    private long handIntroTime = -1;

    public HandCleaner(String name) {
        this.name = name;

        this.debug = Debug.getDebug("HandCleaner");

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

        Group tap = Config.singleton().getGroup("tap");
        tapSpeed = Double.parseDouble(tap.getItem("tapSpeed").get());

        long preTapTime = Integer.parseInt(tap.getItem("preTapTime").get());
        long tapMinTime = Integer.parseInt(tap.getItem("tapMinTime").get());
        long tapMaxTime = Integer.parseInt(tap.getItem("tapMaxTime").get());
        long tapOverflow = tapMaxTime - tapMinTime;
        long postTapTime = Integer.parseInt(tap.getItem("postTapTime").get());

        this.consistentZPreTap = new Consistently(true, preTapTime, "z pre tap");
        this.consistentZTap = new Consistently(true, tapMinTime, "z tap");
        this.consistentZOverflow = new Consistently(true, tapOverflow, "z tap overflow");
        this.consistentZPostTap = new Consistently(true, postTapTime, "z post tap");

        moveBeforeTaps = Boolean.parseBoolean(tap.getItem("moveBeforeTaps").get());

        moveBeforeTimeout = Long.parseLong(handCleaner.getItem("moveBeforeTimeout").get());
        minHandAge = Long.parseLong(handCleaner.getItem("minHandAge").get());

        if (stationarySpeed < 0) {
            if (moveBeforeTaps) {
                this.debug.out(0, "WARNING: Invalid config. moveBeforeTaps is enabled, but stationarySpeed is less than 0 meaning that the movement will never be detected to enable the taps.");
            }
        }

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
            double zStart = movingMeanZ.get();

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
            double zChange = movingMeanZ.get() - zStart;
            double cChange = Math.pow((Math.pow(xChange, 2) + Math.pow(yChange, 2)), 0.5);

            speed = cChange / elapsed * 100F;
            zSpeed = zChange / elapsed * 100F;

            if (handIntroTime < 0) {
                handIntroTime = timeInMilliseconds();
            }

            long handAge = handAge();

            if (handAge > minHandAge)
            {
                if (!isStationary()) {
                    if (!hasMoved) {
                        this.debug.out(1, this.name + " has moved since the hand was introduced.");
                    }

                    hasMoved = true;
                } else {
                    if (hasMoved) {
                        if (!firstStationary) {
                            this.debug.out(1, this.name + " has moved and then stopped since the hand was introduced.");
                            firstStationary = true;
                        }
                    }
                }
            }

            if (handAge > moveBeforeTimeout) {
                if (!moveBeforeTimedout) {
                    if (!hasMoved) {
                        this.debug.out(1, "moveBeforeTimeout(" + String.valueOf(moveBeforeTimeout) + ") for " + this.name + " has elapsed before movement has been detected. So actions will now be allowed anyway.");
                    }

                    moveBeforeTimedout = true;
                }
            }

        } else {
            // No longer absent. Let's reset everything.

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
            resetTap();

            // Prepare for the speed calculations in the next iteration.
            lastSubmissionTime = timeInMilliseconds();
            speed = 0;
            zSpeed = 0;

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
        hasMoved = false;
        firstStationary = false;
        handIntroTime = -1;
        moveBeforeTimedout = false;
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

    private long handAge() {
        return timeInMilliseconds() - handIntroTime;
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

    public void resetAutoTrim() {
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

    public Boolean isZStationary() {
        return (Math.abs(zSpeed) < tapSpeed);
    }

    public void setGestureLock(Boolean desiredState) {
        this.gesturesLocked = desiredState;
        this.showGestureLocks();
    }

    public void showGestureLocks() {
        if (this.gesturesLocked) {
            this.debug.out(1, this.name + " gestures are locked.");
        } else {
            this.debug.out(1, this.name + " gestures are not locked.");
        }
    }

    public void setTapLock(Boolean desiredState, long time) {
        if (time == 0) {
            this.tapsLocked = desiredState;
            this.tapUnlockTime = 0;
        } else {
            this.tapUnlockTime = timeInMilliseconds() + time;
            this.tapsLocked = true;
        }

        if (this.tapsLocked) {
            resetTap();
        }

        this.showTapLocks();
    }

    private Boolean tapsAreLocked() {
        if (tapUnlockTime != 0) {
            if (timeInMilliseconds() > this.tapUnlockTime) {
                this.tapsLocked = false;
                this.tapUnlockTime = 0;

                this.showTapLocks();
            }
        }

        return this.tapsLocked;
    }

    public void showTapLocks() {
        if (this.tapsLocked) {
            long now = timeInMilliseconds();
            if (this.tapUnlockTime == 0) {
                this.debug.out(1, this.name + " taps are locked with no timeout.");
            } else {
                String timeUntilUnlock = String.valueOf(this.tapUnlockTime - now);
                this.debug.out(1, this.name + " taps are locked for another " + timeUntilUnlock + " milliseconds (" + String.valueOf(this.tapUnlockTime) + "-" + String.valueOf(now) + ").");
            }
        } else {
            this.debug.out(1, this.name + " taps are not locked locked.");
        }
    }

    private Boolean isRetracting() {
        return (zSpeed > 0);
    }

    private Boolean moveBeforeTapSatisfied() {
        // If moveBeforeTaps is enabled, we should only return true if the cursor has moved.
        // If moveBeforeTaps is disabled, we should always return true;

        if (moveBeforeTimedout) {
            return true;
        }

        if (moveBeforeTaps) { // Primary flow.
            return hasMoved;
        }

        return true;
    }

    public Boolean isDoingATap(String zone) {
        // Taps are disabled. Don't spend any more time on it.
        if (tapSpeed < 0) return false;

        if (!moveBeforeTapSatisfied()) return false;

        // Hand is absent.
        if (absent) {
            resetTap();
            return false;
        }

        // Tap lock.
        if (tapsAreLocked()) {
            return false;
        }

        // We're not in the active zone.
        if (zone.equals("noMove")) {
            resetTap();
            return false;
        }

        // If the hand is moving, we are busy doing something else.
        if (!isStationary()) {
            resetTap();
            return false;
        }

        // A basic state machine for figuring out if a tap is happening.
        Boolean tapAction = (!isRetracting() && !isZStationary());
        String output = "false";
        if (tapAction) output = "true";
        this.consistentZPreTap.tick(isStationary());
        this.consistentZTap.tick(tapAction);
        this.consistentZOverflow.tick(tapAction);
        this.consistentZPostTap.tick(isStationary());

        switch (tapProgress) {
            case 0:
                if (this.consistentZPreTap.isConsistent()) {
                    this.debug.out(1, "Tap: Ready");
                    this.consistentZTap.reset();
                    tapProgress += 1;
                }
            case 1:
                if (this.consistentZTap.isConsistent()) {
                    tapProgress += 1;
                    this.debug.out(1, "Tap: motion");
                    this.consistentZOverflow.reset();
                }
                break;
            case 2:
                if (this.consistentZOverflow.isConsistent()) {
                    this.debug.out(1, "Tap: overflow");
                    resetTap();
                }

                if (!isStationary()) {
                    this.debug.out(1, "Tap: moving");
                    this.consistentZPostTap.reset();
                }

                if (this.consistentZPostTap.isConsistent()) {
                    this.debug.out(1, "Tap: complete");
                    resetTap();
                    return true;
                }
            default:
                resetTap();
        }

        return false;
    }

    private void resetTap() {
        tapProgress = 0;

        this.consistentZPreTap.reset();
        this.consistentZTap.reset();
        this.consistentZOverflow.reset();
        this.consistentZPostTap.reset();
    }
}
