package handWavey;

import config.*;
import debug.Debug;

public class HandsState {
    private Debug debug;
    
    private double zNoMoveBegin = 0;
    private double zActiveBegin = 0;
    private double zAbsoluteBegin = 0;
    private double zRelativeBegin = 0;
    private double zActionBegin = 0;
    private double zoneBuffer = 0;
    
    private String zoneMode = "touchScreen";
    
    private String zone = "none";
    private String oldZone = "none";
    
    private Boolean zoneMouseDown = false;
    private Boolean gestureMouseDown = false;
    private Boolean resultMouseDownDown = false;
    private Boolean resultMouseDownUp = false;
    
    private Boolean isNew = false;
    
    public HandsState() {
        Config config = Config.singleton();
        Group handSummaryManager = config.getGroup("handSummaryManager");
        
        // TODO Give this class its own debug level?
        int debugLevel = Integer.parseInt(handSummaryManager.getItem("debugLevel").get());
        this.debug = new Debug(debugLevel, "HandsState");

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
            this.isNew = true;
            this.oldZone = this.zone;
            
            switch(newZone) {
                case "none":
                    this.zoneMouseDown = false;
                    break;
                case "noMove":
                    this.zoneMouseDown = false;
                    break;
                case "active":
                    this.zoneMouseDown = false;
                    break;
                case "absolute":
                    this.zoneMouseDown = false;
                    break;
                case "relative":
                    this.zoneMouseDown = false;
                    break;
                case "action":
                    this.zoneMouseDown = true;
                    break;
            }
            
            this.zone = newZone;
            this.debug.out(1, "Entered zone " + newZone + "  at depth " + String.valueOf(Math.round(handZ)));
        } else {
            this.isNew = false;
        }
        
        return this.zone;
    }
    
    public Boolean zoneIsNew() {
        return this.isNew;
    }
    
    public String getOldZone() {
        return this.oldZone;
    }
    
    private Boolean combinedMouseDown() {
        return ((this.zoneMouseDown || this.gestureMouseDown) && this.zone != "none");
    }
    
    public Boolean shouldMouseDown() {
        Boolean combined = combinedMouseDown();
        if (combined != this.resultMouseDownDown) {
            this.resultMouseDownDown = combined;
            return combined;
        } else {
            return false;
        }
    }
    
    public Boolean shouldMouseUp() {
        Boolean combined = combinedMouseDown();
        if (combined != this.resultMouseDownUp) {
            this.resultMouseDownUp = combined;
            return !combined;
        } else {
            return false;
        }
    }
    
    public void setHandClosed(Boolean handClosed) {
        this.gestureMouseDown = handClosed;
    }
}

