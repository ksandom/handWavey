// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Gets all information about hand positions from the UltraMotion/LeapMotion device and sends the information that we need via the HandSummary class to the HandWaveyManager via the sendHandSummaries function.

This is used for receiving data from the UltraMotion/LeapMotion device for tracking hand positions.
*/

package ultraMotion;

import config.Config;
import config.*;
import com.leapmotion.leap.*;
import handWavey.HandSummary;
import handWavey.UltraMotionManager;
import debug.Debug;
import java.util.Arrays;

public class UltraMotionInput extends Listener {
    private UltraMotionManager ultraMotionManager = null;
    private Debug debug;
    private HandSummary[] handSummaries = {null, null, null, null, null, null, null, null, null, null};
    private int lastHandCount = 0;
    private int maxHands = 2;
    private float openThreshold = 0;
    private float pi = (float)3.1415926536;

    private double maxHeight = 500;
    private double minHeight = 150;
    private double maxCAtMaxHeight = 210;
    private double maxCAtMinHeight = 130;
    private double heightRatio = 1;
    private double heightDiff = 350;
    private double cDiff = 80;

    private Boolean activeHandsExist = false;

    private int fingerToUse = 3;
    private Bone.Type boneToUse = null;

    public UltraMotionInput () {
        this.boneToUse = Bone.Type.values()[Bone.Type.values().length-1];

        this.debug = Debug.getDebug("UltraMotionInput");

        Group ultraMotionConfig = Config.singleton().getGroup("ultraMotion");
        this.openThreshold = Float.parseFloat(ultraMotionConfig.getItem("openThreshold").get());
        this.maxHands = Integer.parseInt(ultraMotionConfig.getItem("maxHands").get());
        this.fingerToUse = Integer.parseInt(ultraMotionConfig.getItem("openFinger").get());

        this.debug.out(1, "Finger to use for open/closed state: " + String.valueOf(this.fingerToUse));
        this.debug.out(1, "Open/closed threshold: " + String.valueOf(this.openThreshold));

        // Configure the cone of silence for ignoring input from outside the reliable cone.
        Group conOfSilence = ultraMotionConfig.getGroup("coneOfSilence");
        this.maxHeight = Double.parseDouble(conOfSilence.getItem("maxHeight").get());
        this.minHeight = Double.parseDouble(conOfSilence.getItem("minHeight").get());
        this.maxCAtMaxHeight = Double.parseDouble(conOfSilence.getItem("maxCAtMaxHeight").get());
        this.maxCAtMinHeight = Double.parseDouble(conOfSilence.getItem("maxCAtMinHeight").get());

        // Pre-calculate some of the isInRange calculations to reduce work later on.
        this.heightDiff = this.maxHeight - this.minHeight;
        this.cDiff = this.maxCAtMaxHeight - this.maxCAtMinHeight;
        this.heightRatio = this.heightDiff / this.cDiff;
    }

    public Boolean isInRange(double x, double y, double z) {
        Boolean result = false;

        if (y < this.minHeight || y > this.maxHeight  ) {
            result = false;
        } else {
            // Get radius from the center of the cone at the current height.
            double c = Math.pow(Math.pow(x, 2) + Math.pow(z, 2), 0.5);

            // Get the useable height.
            double height = y - this.minHeight;

            // Find the maximum allowable radius at the current height.
            double maxC = height / this.heightRatio + this.maxCAtMinHeight;

            result = (c <= maxC);
        }

        return result;
    }

    public void setMaxHands(int maxHands) {
        // TODO This assumption is simply because I haven't put time into how to dynamically add more entries into the handSummaries array. If that is solved, this limit can be removed. But for now, 10 hands seems like way more than is ever going to be used... Famous last words.
        if (maxHands > 10) {
            maxHands = 10;
        }

        this.maxHands = maxHands;
        emptyHands();
    }

    public void setOpenThreshold(float openThreshold) {
        this.openThreshold = openThreshold;
    }

    private void emptyHands() {
        // This just simplifies the code later down since we only need to check for one absense.
        for (int i = 0; i<this.maxHands; i++) {
            this.handSummaries[i] = null;
        }

        this.activeHandsExist = false;
    }

    public void setUltraMotionManager(UltraMotionManager ultraMotionManager) {
        this.ultraMotionManager = ultraMotionManager;
    }

    public void onInit(Controller controller) {
        this.debug.out(0, "Initialised.");
    }

    public void onConnect(Controller controller) {
        this.debug.out(0, "Connected.");
    }

    public void onDisconnect(Controller controller) {
        this.debug.out(0, "Disconnected.");

        if (this.ultraMotionManager != null) {
            this.ultraMotionManager.exit();
        }
    }

    public void onExit(Controller controller) {
        this.debug.out(0, "Exited.");
    }

    public void onFrame(Controller controller) {
        // Get the most recent frame and report some basic information
        Frame frame = controller.frame();
        this.debug.out(2, "Frame id: " + frame.id()
                         + ", timestamp: " + frame.timestamp()
                         + ", hands: " + frame.hands().count());

        int handCount = frame.hands().count();
        int handNumber = 0;

        // No hands? Let's clean our state.
        if (handCount == 0) {
            emptyHands();
        } else {
            // All hands are invalid. Let's get rid of them to so that we are ready for the next hands.
            if (allHandsAreInvalid() == true) {
                if (this.activeHandsExist == true) {
                    this.debug.out(1, "All hands are marked as invalid. Discarding so that we are ready for fresh hands.");
                    emptyHands();
                    sendHandSummaries();
                }
            }
        }

        //Get hands
        for(Hand hand : frame.hands()) {
            if (handNumber < this.maxHands) {
                Vector handPosition = hand.palmPosition();

                if (isInRange(handPosition.getX(), handPosition.getY(), handPosition.getZ())) {
                    // If we aren't tracking this hand yet. Let's do so.
                    if (this.handSummaries[handNumber] == null) {
                        assignNewHand(handNumber, hand.id());
                    }

                    this.handSummaries[handNumber].setOOB(false);
                } else {
                    if (this.handSummaries[handNumber] == null || !this.handSummaries[handNumber].isValid()) {
                        continue;
                    }

                    this.debug.out(1, "Hand " + String.valueOf(handNumber) + " is OOB.");
                    this.handSummaries[handNumber].setOOB(true);
                }

                if (this.handSummaries[handNumber].getID() != hand.id()) {
                    if (handCount == 1) {
                        // If this is the only hand, we can simply substitute it, and get on with it.
                        imposterReplace(handNumber, hand.id(), "But it was the only hand that we are tracking.");
                    } else if (!this.handSummaries[handNumber].isValid()) {
                        // If the original hand was OOB, replacing it is probably the right thing to do. This is likely to create a ripple for later hands, which brings us to the next condition.
                        imposterReplace(handNumber, hand.id(), "But it was OOB/invalid.");
                    } else if (handNumber == handCount - 1) {
                        // If there are no hands after this one, we can safely replace it.
                        imposterReplace(handNumber, hand.id(), "But it was the last hand that we are tracking.");
                    } else {
                        // For other cases, just mark it as invalid. This is now likely to interact with the second condition on the next iteration. So this will need to be re-thought out if it becomes a problem.
                        // If this does become a problem, it will look like:
                        // * A hand gets replaced with the wrong hand.
                        // * Hand ordering gets stuffed up when a hand dissapears and reappears.
                        // TODO Revisit this to make it more durable.

                        this.handSummaries[handNumber].markInvalid();
                        this.debug.out(0, "Discarding hand " + String.valueOf(this.handSummaries[handNumber].getID()) + " != " + String.valueOf(hand.id()) + " in position " + String.valueOf(handNumber));
                        this.ultraMotionManager.getHandWaveyManager().triggerEvent("imposterHand-discard");
                    }
                }

                this.handSummaries[handNumber].setHandPosition(
                    handPosition.getX(),
                    handPosition.getY(),
                    handPosition.getZ()
                    );

                Vector handNormal = hand.palmNormal();
                Vector handDirection = hand.direction();
                this.handSummaries[handNumber].setHandAngles(
                    handNormal.roll(),
                    handDirection.pitch(),
                    handDirection.yaw()
                    );

                Vector armDirection = hand.arm().direction();
                this.handSummaries[handNumber].setArmAngles(
                    armDirection.roll(),
                    armDirection.pitch(),
                    armDirection.yaw()
                    );

                this.handSummaries[handNumber].setHandIsLeft(hand.isLeft());

                Float middleDistalBonePitch = hand.fingers().get(this.fingerToUse).bone(this.boneToUse).direction().pitch();
                Float relativeFingerPitch = mangleAngle(middleDistalBonePitch) + handDirection.pitch();
                Float fingerDifference = Math.abs(relativeFingerPitch);
                Boolean handOpen = (fingerDifference < openThreshold);

                this.handSummaries[handNumber].setHandOpen(handOpen);
            } else {
                // If we have more hands than we are configured to handle, let's just stop processing the extra hands.
                break;
            }

            handNumber++;
        }

        if (this.handSummaries[0].isValid()) {
            sendHandSummaries();
        } else {
            this.ultraMotionManager.getHandWaveyManager().discardOldPosition();
        }

        this.lastHandCount = handCount;
    }

    private void sendHandSummaries() {
        this.ultraMotionManager.getHandWaveyManager().sendHandSummaries(this.handSummaries);
    }

    private void imposterReplace(int handPosition, int handID, String why) {
        this.debug.out(0, "Hand " + String.valueOf(this.handSummaries[handPosition].getID()) + " != " + String.valueOf(handID) + " in position " + String.valueOf(handPosition) + ". " + why + " So simply replacing it.");
        assignNewHand(handPosition, handID);
        this.ultraMotionManager.getHandWaveyManager().triggerEvent("imposterHand-replace");
    }

    private void assignNewHand(int handPosition, int handID) {
        this.debug.out(1, "New hand: " + String.valueOf(handID) + " assigned to position " + String.valueOf(handPosition));
        this.handSummaries[handPosition] = new HandSummary(handID);
        this.activeHandsExist = true;
    }

    private Boolean allHandsAreInvalid() {
        Boolean result = true;

        for (int handPosition = 0; handPosition < this.handSummaries.length; handPosition++ ) {
            if (this.handSummaries[handPosition] != null) {
                if (this.handSummaries[handPosition].isValid() == true) {
                    result = false;
                    break;
                }
            } else break;
        }

        return result;
    }

    private float mangleAngle(float angle) {
        // Moves the center by 180 degrees. I didn't get rotation working reliably.
        // TODO Revisit whether getting rotation working reliably would be a better solution. There must be no jump when rotating past the boundary where an individual angle loops.

        int sign = (angle < 0)?-1:1;
        float invertedValue = this.pi - Math.abs(angle);

        return invertedValue * (float)sign;
    }

    private float fixCenter(float value) {
        return combineAngles(value, this.pi);
    }

    private float combineAngles(float value, float rotateAmount) {
        // TODO There must be a better way to do this, but it will do for now.

        float combined = value + rotateAmount + this.pi;
        combined = combined % (this.pi * 2);
        combined = combined - this.pi;

        return combined;
    }
}
