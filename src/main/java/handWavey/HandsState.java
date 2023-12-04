// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Keeps track of the state of BOTH hands, and:
* generates the appropriate events as needed.
* provides information to other classes to make decisions. eg choosing the correct zone.
*/

package handWavey;

import config.*;
import dataCleaner.Changed;
import debug.Debug;
import java.util.HashMap;
import java.util.List;
import java.sql.Timestamp;

public class HandsState {
    private static HandsState handsState = null;
    private HandSummary[] handSummaries;
    private HandWaveyEvent handWaveyEvent;

    private Debug debug;

    private HandStateEvents primaryState = new HandStateEvents(true);
    private HandStateEvents secondaryState = new HandStateEvents(false);

    private double zNoMoveBegin = 0;
    private double zActiveBegin = 0;
    private double zAbsoluteBegin = 0;
    private double zRelativeBegin = 0;
    private double zActionBegin = 0;
    private double zoneBuffer = 0;

    private String zoneMode = "touchScreen";

    private String zone = "none";
    private String zoneOverride = "";
    private String chosenButton = "left";

    private float pi = (float)3.1415926536;

    private int primarySegments = 4;
    private double primaryOffset = 0;
    private double primarySegmentWidth = this.pi/2;
    private int secondarySegments = 4;
    private double secondaryOffset = 0;
    private double secondarySegmentWidth = this.pi/2;
    private int primaryMergeIntoSegment = 0;
    private int primaryMergeFrom = 0;
    private int primaryMergeTo = 0;
    private int secondaryMergeIntoSegment = 0;
    private int secondaryMergeFrom = 0;
    private int secondaryMergeTo = 0;

    private long currentFrameTime = 0;
    private long previousFrameAge = 0;
    private long sillyFrameAge = 10 * 1000; // Longer than this many milliseconds is well and truly meaningless, and could lead to interesting bugs.

    private long newHands = 0;
    private Boolean newHandsHandled = false;
    private int oldHandsTimeout = 800;
    private int cursorFreezeFirstMillis = 200;
    private Boolean newHandsCursorFreeze = false;
    private Boolean earlyUnfreeze = true;
    private String earlyUnfreezeZone = "active";

    // TODO Migrate other dimensions.
    private double zMultiplier = -1;


    public HandsState() {
        Config config = Config.singleton();

        this.debug = Debug.getDebug("HandsState");


        // Configure zone behavior.
        this.zoneMode = config.getItem("zoneMode").get();
        this.zoneBuffer = Double.parseDouble(config.getItem("zoneBuffer").get());


        // Configure Z axis thresholds.
        if (this.zoneMode.equals("touchScreen")) {
            this.debug.out(1, "Using touchScreen zoneMode.");
            Group touchScreen = config.getGroup("zones").getGroup("touchScreen");
            this.zAbsoluteBegin = Double.parseDouble(touchScreen.getGroup("absolute").getItem("threshold").get());
            this.zRelativeBegin = Double.parseDouble(touchScreen.getGroup("relative").getItem("threshold").get());
            this.zActionBegin = Double.parseDouble(touchScreen.getGroup("action").getItem("threshold").get());
        } else if (this.zoneMode.equals("touchPad")) {
            this.debug.out(1, "Using touchPad zoneMode.");
            Group touchPad = config.getGroup("zones").getGroup("touchPad");
            this.zNoMoveBegin = Double.parseDouble(touchPad.getGroup("noMove").getItem("threshold").get());
            this.zActiveBegin = Double.parseDouble(touchPad.getGroup("active").getItem("threshold").get());
            this.zActionBegin = Double.parseDouble(touchPad.getGroup("action").getItem("threshold").get());
        } else {
            // TODO This needs to produce some user feedback that the user will see. Once this runs as a service, a debug message won't be sufficient.
            this.debug.out(0, "Unknown zoneMode " + this.zoneMode + ". This will likely cause badness.");
        }

        Group axisOrientation = config.getGroup("axisOrientation");
        int configuredZMultiplier = Integer.parseInt(axisOrientation.getItem("zMultiplier").get());
        this.zMultiplier = configuredZMultiplier;



        // Configure hand gesture zones.
        Group primaryHand = config.getGroup("gestureConfig").getGroup("primaryHand");
        this.primarySegments = Integer.parseInt(primaryHand.getItem("rotationSegments").get());
        this.primarySegmentWidth = (this.pi * 2) / this.primarySegments;
        this.primaryOffset = Double.parseDouble(primaryHand.getItem("rotationOffset").get());
        this.primaryMergeIntoSegment = Integer.parseInt(primaryHand.getItem("mergeIntoSegment").get());
        this.primaryMergeFrom = Integer.parseInt(primaryHand.getItem("mergeFrom").get());
        this.primaryMergeTo = Integer.parseInt(primaryHand.getItem("mergeTo").get());

        Group secondaryHand = config.getGroup("gestureConfig").getGroup("secondaryHand");
        this.secondarySegments = Integer.parseInt(secondaryHand.getItem("rotationSegments").get());
        this.secondarySegmentWidth = (this.pi * 2) / this.secondarySegments;
        this.secondaryOffset = Double.parseDouble(secondaryHand.getItem("rotationOffset").get());
        this.secondaryMergeIntoSegment = Integer.parseInt(secondaryHand.getItem("mergeIntoSegment").get());
        this.secondaryMergeFrom = Integer.parseInt(secondaryHand.getItem("mergeFrom").get());
        this.secondaryMergeTo = Integer.parseInt(secondaryHand.getItem("mergeTo").get());


        // Configure new hands tracking.
        Group newHands = config.getGroup("dataCleaning").getGroup("newHands");
        this.oldHandsTimeout = Integer.parseInt(newHands.getItem("oldHandsTimeout").get());
        this.cursorFreezeFirstMillis = Integer.parseInt(newHands.getItem("cursorFreezeFirstMillis").get());

        this.earlyUnfreeze = Boolean.parseBoolean(newHands.getItem("earlyUnfreeze").get());
        this.earlyUnfreezeZone = newHands.getItem("earlyUnfreezeZone").get();
    }

    public static HandsState singleton() {
        if (HandsState.handsState == null) {
            HandsState.handsState = new HandsState();
        }

        return HandsState.handsState;
    }

    public void setHandWaveyEvent(HandWaveyEvent handWaveyEvent) {
        this.handWaveyEvent = handWaveyEvent;
    }


    public void setHandSummaries(HandSummary[] handSummaries) {
        this.handSummaries = handSummaries;
        notifyGotFrame();
    }

    public void figureOutStuff() {
        Boolean shouldUpdatePrimary = false;
        Boolean shouldUpdateSecondary = (secondaryHandIsActiveOrChanged());
        Boolean primaryAbsent = (this.handSummaries[0] == null);
        Double primaryHandZ;

        if (primaryAbsent) {
            this.primaryState.setState(Gesture.absent);
            this.primaryState.setZone("OOB");
            this.primaryState.setSegment(0);
        } else {
            shouldUpdatePrimary = (!this.handSummaries[0].handIsNew());

            if (!shouldUpdatePrimary && this.earlyUnfreeze) {
                // Check if the hand is already in the active area. If so, let's just enable it.
                primaryHandZ = this.handSummaries[0].getHandZ() * this.zMultiplier;
                if (this.handsState.getZone(primaryHandZ).equals(this.earlyUnfreezeZone)) {
                    shouldUpdatePrimary = true;
                    this.handSummaries[0].clearNewHand();
                    this.debug.out(1, "The hand seems ready. Skipping the rest of the cursorFreezeFirstMillis timeout.");
                    releaseCursorFreeze();
                }
            }
        }

        if (shouldUpdatePrimary) {
            primaryHandZ = this.handSummaries[0].getHandZ() * this.zMultiplier;

            String zone = this.handsState.getZone(primaryHandZ);
            if (!zone.equals(this.zoneOverride)) {
                this.primaryState.setZone(zone);
            }

            this.primaryState.setSegment(getHandSegment(true, this.handSummaries[0]));
            this.primaryState.setState(getHandState(this.handSummaries[0]));
        }

        if (this.handSummaries[1] == null || !this.handSummaries[1].isValid()) {
            this.secondaryState.setState(Gesture.absent);
            this.secondaryState.setZone("OOB");
            this.secondaryState.setSegment(0);
        } else if (shouldUpdateSecondary) {
            Double secondaryHandZ = this.handSummaries[1].getHandZ() * this.zMultiplier;
            this.secondaryState.setZone(this.handsState.getZone(secondaryHandZ, false));
            this.secondaryState.setSegment(getHandSegment(false, this.handSummaries[1]));
        } else if (this.handSummaries[1] != null) {
            this.secondaryState.setState(getHandState(this.handSummaries[1]));
        }


        if ((shouldUpdatePrimary || primaryAbsent) || shouldUpdateSecondary) {
            Boolean anythingChanged = (this.primaryState.somethingChanged() || this.secondaryState.somethingChanged());
            if (anythingChanged) {
                String pStateEnter = this.primaryState.getIndividualEnterEvent();
                String sStateEnter = this.secondaryState.getIndividualEnterEvent();

                this.debug.out(1, "Current state: " + pStateEnter + ", " + sStateEnter);

                // Derive events to be triggered.
                this.primaryState.deriveEvents();
                this.secondaryState.deriveEvents();


                // exit events.
                String primaryStateExit = primaryState.getIndividualExitEvent();
                String secondaryStateExit = secondaryState.getIndividualExitEvent();

                this.handWaveyEvent.triggerEvent("combined-" + primaryStateExit + "-" + secondaryStateExit + "-exit");

                Boolean pNonOOBExit = primaryState.nonOOBExit();
                Boolean sNonOOBExit = secondaryState.nonOOBExit();

                String pNonOOBStateExit = primaryState.getNonOOBExitEvent();
                String sNonOOBStateExit = primaryState.getNonOOBExitEvent();

                if (pNonOOBExit) {
                    this.handWaveyEvent.triggerEvent("combined-" + pNonOOBStateExit + "-" + secondaryStateExit + "-exit");
                }

                if (sNonOOBExit) {
                    this.handWaveyEvent.triggerEvent("combined-" + primaryStateExit + "-" + sNonOOBStateExit + "-exit");
                }

                if (pNonOOBExit && sNonOOBExit) {
                    this.handWaveyEvent.triggerEvent("combined-" + pNonOOBStateExit + "-" + sNonOOBStateExit + "-exit");
                }

                this.handWaveyEvent.triggerEvents(this.secondaryState.getExitEvents());
                this.handWaveyEvent.triggerEvents(this.primaryState.getExitEvents());


                // anyChange events.
                List<String> anyChangeEvents = this.primaryState.getAnyChangeEvents();
                this.handWaveyEvent.triggerEvents(anyChangeEvents);
                if (secondaryHandIsActiveOrChanged()) {
                    this.handWaveyEvent.triggerEvents(this.secondaryState.getAnyChangeEvents());
                }


                // enter events.
                this.handWaveyEvent.triggerEvents(this.primaryState.getEnterEvents());
                this.handWaveyEvent.triggerEvents(this.secondaryState.getEnterEvents());

                this.handWaveyEvent.triggerEvent("combined-" + pStateEnter + "-" + sStateEnter + "-enter");

                Boolean pNonOOBEnter = primaryState.nonOOBEnter();
                Boolean sNonOOBEnter = secondaryState.nonOOBEnter();

                String pNonOOBStateEnter = primaryState.getNonOOBEnterEvent();
                String sNonOOBStateEnter = secondaryState.getNonOOBEnterEvent();

                if (pNonOOBEnter) {
                    this.handWaveyEvent.triggerEvent("combined-" + pNonOOBStateEnter + "-" + sNonOOBStateEnter + "-enter");
                }

                if (sNonOOBEnter) {
                    this.handWaveyEvent.triggerEvent("combined-" + pNonOOBEnter + "-" + sNonOOBStateEnter + "-enter");
                }

                if (pNonOOBEnter && sNonOOBEnter) {
                    this.handWaveyEvent.triggerEvent("combined-" + pNonOOBStateEnter + "-" + sNonOOBStateEnter + "-enter");
                }
            }
        }
    }

    private int getHandState(HandSummary handSummary) {
        if (handSummary.isValid() == false) {
            return Gesture.absent;
        }
        if (handSummary.handIsOpen()) {
            return Gesture.open;
        }

        return Gesture.closed;
    }

    public Boolean secondaryHandIsActive() {
        if (this.handSummaries.length < 2) return false;
        if (this.handSummaries[1] == null) return false;
        if (this.handSummaries[1].handIsNew()) return false;
        return (this.handSummaries[1].isValid());
    }

    public Boolean secondaryHandIsActiveOrChanged() {
        Boolean result = false;
        if (secondaryHandIsActive() || this.secondaryState.freshlyAbsent()) {
            result = true;
        }

        return result;
    }

    public int getHandSegment(Boolean isPrimary, HandSummary handSummary) {
        return getHandSegment(handSummary.getHandRoll(), isPrimary, handSummary.handIsLeft());
    }

    public int getHandSegment(double handRoll, Boolean isPrimary, Boolean isLeft) {
        // Flip the direction depending on the hand.
        int directionalMultiplier = (isLeft)?1:-1;
        double handedRoll = handRoll * directionalMultiplier;

        // Get the current segmentCount and segmentWidth.
        int segmentCount = (isPrimary)?this.primarySegments:this.secondarySegments;
        double segmentWidth = (isPrimary)?this.primarySegmentWidth:this.secondarySegmentWidth;
        int mergeIntoSegment = (isPrimary)?this.primaryMergeIntoSegment:this.secondaryMergeIntoSegment;
        int mergeFrom = (isPrimary)?this.primaryMergeFrom:this.secondaryMergeFrom;
        int mergeTo = (isPrimary)?this.primaryMergeTo:this.secondaryMergeTo;

        // Take care of, moving the range into the positive, move 0 into the center of segment 0, and add the user-specified offset.
        double userOffset = (isPrimary)?this.primaryOffset:this.secondaryOffset;
        double offsetRoll = handedRoll + this.pi + (segmentWidth/2) - userOffset;

        // Work out which segment we are in, then rotate it back because we rotated to make the entire range positive.
        int segmentNumber = (int) Math.floor(((double)offsetRoll / (double)segmentWidth) - ((double)segmentCount / 2));
        while (segmentNumber < 0) {
            segmentNumber += segmentCount;
        }
        while (segmentNumber > segmentCount - 1) {
            segmentNumber -= segmentCount;
        }

        // Work out if this is a merged segment.
        if (segmentNumber >= mergeFrom && segmentNumber <= mergeTo) {
            segmentNumber = mergeIntoSegment;
        }

        return segmentNumber;
    }

    private String deriveZone(double handZ) {
        String zone = "Unknown";

        if (this.zoneMode.equals("touchScreen")) {
            if (handZ > this.zActionBegin) {
                zone = "action";
            } else if (handZ > this.zRelativeBegin) {
                zone = "relative";
            } else if (handZ > this.zAbsoluteBegin) {
                zone = "absolute";
            } else {
                zone = "none";
            }
        } else if (this.zoneMode.equals("touchPad")) {
            if (handZ > this.zActionBegin) {
                zone = "action";
            } else if (handZ > this.zActiveBegin) {
                zone = "active";
            } else if (handZ > this.zNoMoveBegin) {
                zone = "noMove";
            } else {
                zone = "none";
            }
        }

        return zone;
    }

    public String getZone(double handZ) {
        return getZone(handZ, true);
    }

    public String getZone(double handZ, Boolean allowOverride) {
        String newZone = deriveZone(handZ);
        String bufferZone = deriveZone(handZ + this.zoneBuffer);

        if ((bufferZone.equals(newZone)) && !newZone.equals(this.zone)) {
            this.zone = newZone;
        }

        if ((allowOverride && !this.zoneOverride.equals(""))) {
            return this.zoneOverride;
        }

        return this.zone;
    }

    public void overrideZone(String zone) {
        this.zoneOverride = zone;
        this.debug.out(2, "Zone override: " + this.zoneOverride);
    }

    public void releaseZone() {
        this.debug.out(2, "Zone release from: " + this.zoneOverride);
        this.zoneOverride = "";
    }

    public String whichMouseButton() {
        return this.chosenButton;
    }

    public void setMouseButton(String button) {
        this.chosenButton = button;
        this.debug.out(2, "Set mouse button to: " + button);
    }

    private long getNow() {
        return new Timestamp(System.currentTimeMillis()).getTime();
    }

    public void notifyGotFrame() {
        long now = getNow();

        this.previousFrameAge = now - this.currentFrameTime;
        this.currentFrameTime = now;

        if (this.previousFrameAge > this.oldHandsTimeout) {
            // Old hands.
            this.newHandsHandled = false;
        } else {
            if (this.newHandsHandled == false) {
                this.newHandsHandled = true;
                this.newHands = now;
                this.newHandsCursorFreeze = true;
                this.debug.out(2, "Hand is new. Triggering cursor freeze.");
                this.handWaveyEvent.triggerEvent("special-newHandFreeze");
            } else {
                long elapsedTime = now - this.newHands;
                if (this.newHandsCursorFreeze == true) {
                    if (elapsedTime > this.cursorFreezeFirstMillis) {
                        releaseCursorFreeze();
                    }
                }
            }
        }

        // We really don't need to track anything that long.
        if (this.previousFrameAge > this.sillyFrameAge) {
            this.previousFrameAge = this.sillyFrameAge;
        }
    }

    private void releaseCursorFreeze() {
        this.newHandsCursorFreeze = false;
        this.debug.out(1, "Releasing newHand cursor freeze.");
        this.handWaveyEvent.triggerEvent("special-newHandUnfreezeCursor");
    }

    public Boolean newHandsCursorFreeze() {
        return this.newHandsCursorFreeze;
    }

    public long getPreviousFrameAge() {
        return this.previousFrameAge;
    }
}

