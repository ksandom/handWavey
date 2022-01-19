package handWavey;

import config.*;
import dataCleaner.Changed;
import debug.Debug;
import java.util.HashMap;
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
    private String oldZone = "none";
    private String zoneOverride = "";
    private String chosenButton = "left";
    
    private float pi = (float)3.1415926536;
    
    private int primarySegments = 4;
    private double primaryOffset = 0;
    private double primarySegmentWidth = this.pi/2;
    private int secondarySegments = 4;
    private double secondaryOffset = 0;
    private double secondarySegmentWidth = this.pi/2;
    
    private int primarySegment = 0;
    private int secondarySegment = 0;
    
    private long currentFrameTime = 0;
    private long previousFrameAge = 0;
    private long sillyFrameAge = 10 * 1000; // Longer than this many milliseconds is well and truly meaningless, and could lead to interesting bugs.
    
    private long segmentChangeTime = 0;
    
    private long newHands = 0;
    private Boolean newHandsHandled = false;
    private int oldHandsTimeout = 800;
    private int cursorFreezeFirstMillis = 200;
    private Boolean newHandsCursorFreeze = false;
    private int clickFreezeFirstMillis = 500;
    private Boolean newHandsClickFreeze = false;
    
    // TODO Migrate other dimensions.
    private double zMultiplier = -1;

    
    private Boolean isNew = false;
    
    public HandsState() {
        Config config = Config.singleton();
        Group handSummaryManager = config.getGroup("handSummaryManager");
        
        this.debug = Debug.getDebug("HandsState");
        
        
        // Configure zone behavior.
        this.zoneMode = handSummaryManager.getItem("zoneMode").get();
        this.zoneBuffer = Double.parseDouble(handSummaryManager.getItem("zoneBuffer").get());
        
        
        // Configure Z axis thresholds.
        if (this.zoneMode == "touchScreen") {
            Group touchScreen = handSummaryManager.getGroup("zones").getGroup("touchScreen");
            this.zAbsoluteBegin = Double.parseDouble(touchScreen.getGroup("absolute").getItem("threshold").get());
            this.zRelativeBegin = Double.parseDouble(touchScreen.getGroup("relative").getItem("threshold").get());
            this.zActionBegin = Double.parseDouble(touchScreen.getGroup("action").getItem("threshold").get());
        } else if (this.zoneMode == "touchPad") {
            Group touchPad = handSummaryManager.getGroup("zones").getGroup("touchPad");
            this.zNoMoveBegin = Double.parseDouble(touchPad.getGroup("noMove").getItem("threshold").get());
            this.zActiveBegin = Double.parseDouble(touchPad.getGroup("active").getItem("threshold").get());
            this.zActionBegin = Double.parseDouble(touchPad.getGroup("action").getItem("threshold").get());
        } else {
            // TODO This needs to produce some user feedback that the user will see. Once this runs as a service, a debug message won't be sufficient.
            this.debug.out(0, "Unknown zoneMode " + this.zoneMode + ". This will likely cause badness.");
        }
        
        Group axisOrientation = handSummaryManager.getGroup("axisOrientation");
        int configuredZMultiplier = Integer.parseInt(axisOrientation.getItem("zMultiplier").get());
        this.zMultiplier = configuredZMultiplier;

        
        
        // Configure hand gesture zones.
        Group primaryHand = config.getGroup("gestureConfig").getGroup("primaryHand");
        this.primarySegments = Integer.parseInt(primaryHand.getItem("rotationSegments").get());
        this.primarySegmentWidth = (this.pi * 2) / this.primarySegments;
        this.primaryOffset = Double.parseDouble(primaryHand.getItem("rotationOffset").get());
        
        Group secondaryHand = config.getGroup("gestureConfig").getGroup("secondaryHand");
        this.secondarySegments = Integer.parseInt(secondaryHand.getItem("rotationSegments").get());
        this.secondarySegmentWidth = (this.pi * 2) / this.secondarySegments;
        this.secondaryOffset = Double.parseDouble(secondaryHand.getItem("rotationOffset").get());
        
        
        // Configure new hands tracking.
        Group newHands = config.getGroup("newHands");
        this.oldHandsTimeout = Integer.parseInt(newHands.getItem("oldHandsTimeout").get());
        this.cursorFreezeFirstMillis = Integer.parseInt(newHands.getItem("cursorFreezeFirstMillis").get());
        this.clickFreezeFirstMillis = Integer.parseInt(newHands.getItem("clickFreezeFirstMillis").get());
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
        Double primaryHandZ = this.handSummaries[0].getHandZ() * this.zMultiplier;
        this.primaryState.setZone(this.handsState.getZone(primaryHandZ));
        
        this.primaryState.setSegment(getHandSegment(true, this.handSummaries[0]));
        this.primaryState.setState(getHandState(this.handSummaries[0]));

        if (secondaryHandIsActive()) {
            Double secondaryHandZ = this.handSummaries[1].getHandZ() * this.zMultiplier;
            this.secondaryState.setZone(this.handsState.getZone(secondaryHandZ));
            this.secondaryState.setSegment(getHandSegment(true, this.handSummaries[1]));
        }
        if (this.handSummaries[1] != null) {
            this.secondaryState.setState(getHandState(this.handSummaries[1]));
        } else {
            this.secondaryState.setState(Gesture.absent);
        }
        
        if (this.primaryState.somethingChanged() || this.secondaryState.somethingChanged()) {
            this.handWaveyEvent.triggerEvents(this.primaryState.getEvents());
            this.handWaveyEvent.triggerEvents(this.secondaryState.getEvents());
            
            this.handWaveyEvent.triggerEvent("combined-" + primaryState.getIndividualEnterEvent() + "-" + secondaryState.getIndividualEnterEvent() + "-enter");
            this.handWaveyEvent.triggerEvent("combined-" + primaryState.getIndividualExitEvent() + "-" + secondaryState.getIndividualExitEvent() + "-enter");
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
        return ((this.handSummaries.length > 1) && (this.handSummaries[1] != null) && (this.handSummaries[1].isValid()));
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
        
        // Take care of, moving the range into the positive, move 0 into the center of segment 0, and add the user-specified offset.
        double userOffset = (isPrimary)?this.primaryOffset:this.secondaryOffset;
        double offsetRoll = 0;
        if (isLeft) {
            offsetRoll = handedRoll + this.pi + (segmentWidth/2) - userOffset;
        } else {
            offsetRoll = handedRoll + this.pi + (segmentWidth/2) + userOffset;
        }
        
        // Work out which segment we are in, then rotate it back because we rotated to make the entire range positive.
        int segmentNumber = (int) Math.floor((offsetRoll / segmentWidth) - (segmentCount / 2));
        while (segmentNumber < 0) {
            segmentNumber += segmentCount;
        }
        
        return segmentNumber;
    }
    
    public void noSecondaryHand() {
        this.secondarySegment = -1;
    }
    
    private String deriveZone(double handZ) {
        String zone = "Unknown";
        
        if (this.zoneMode == "touchScreen") {
            if (handZ > this.zActionBegin) {
                zone = "action";
            } else if (handZ > this.zRelativeBegin) {
                zone = "relative";
            } else if (handZ > this.zAbsoluteBegin) {
                zone = "absolute";
            } else {
                zone = "none";
            }
        } else if (this.zoneMode == "touchPad") {
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
        String newZone = deriveZone(handZ);
        String bufferZone = deriveZone(handZ + this.zoneBuffer);
        
        if ((bufferZone == newZone) && (newZone != this.zone)) {
            this.zone = newZone;
        }
        
        if (this.zoneOverride != "") {
            return this.zoneOverride;
        }
        
        return this.zone;
    }
    
    public void overrideZone(String zone) {
        this.zoneOverride = zone;
        this.debug.out(1, "Zone override: " + this.zoneOverride);
    }
    
    public void releaseZone() {
        this.debug.out(1, "Zone release from: " + this.zoneOverride);
        this.zoneOverride = "";
    }
    
    public String whichMouseButton() {
        return this.chosenButton;
    }
    
    public void setMouseButton(String button) {
        this.chosenButton = button;
        this.debug.out(1, "Set mouse button to: " + button);
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
                this.newHandsClickFreeze = true;
                this.debug.out(1, "Hand is new. Triggering cursor and click freeze.");
                this.handWaveyEvent.triggerEvent("newHandFreeze");
            } else {
                long elapsedTime = now - this.newHands;
                if (this.newHandsCursorFreeze == true) {
                    if (elapsedTime > this.cursorFreezeFirstMillis) {
                        this.newHandsCursorFreeze = false;
                        this.debug.out(1, "Releasing newHand cursor freeze.");
                        this.handWaveyEvent.triggerEvent("newHandUnfreezeCursor");
                    }
                }
                
                if (this.newHandsClickFreeze == true) {
                    if (elapsedTime > this.cursorFreezeFirstMillis) {
                        this.newHandsClickFreeze = false;
                        this.debug.out(1, "Releasing newHand click freeze.");
                        this.handWaveyEvent.triggerEvent("newHandUnfreezeClick");
                    }
                }
            }
        }
        
        // We really don't need to track anything that long.
        if (this.previousFrameAge > this.sillyFrameAge) {
            this.previousFrameAge = this.sillyFrameAge;
        }
    }
    
    public Boolean newHandsCursorFreeze() {
        return newHandsCursorFreeze;
    }
    
    public Boolean newHandsClickFreeze() {
        return newHandsClickFreeze;
    }
    
    public long getPreviousFrameAge() {
        return this.previousFrameAge;
    }
}

