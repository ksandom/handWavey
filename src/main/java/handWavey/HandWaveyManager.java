package handWavey;

import handWavey.Zone;
import config.*;
import dataCleaner.MovingMean;
import debug.Debug;
import java.awt.Dimension;
import mouseAndKeyboardOutput.*;
import java.util.HashMap;
import audio.*;
import java.io.File;

public class HandWaveyManager {
    private HandSummary[] handSummaries;
    private HashMap<String, Zone> zones = new HashMap<String, Zone>();
    private MovingMean movingMeanX = new MovingMean(1, 0);
    private MovingMean movingMeanY = new MovingMean(1, 0);
    private Debug debug;
    private GenericOutput output;
    private HandsState handsState;
        
    private HashMap<String, String> eventSounds = new HashMap<String, String>();
    private String audioPath;
    
    private String zoneMode = "touchScreen";
    
    private int desktopWidth = 0;
    private int desktopHeight = 0;
    
    private int xOffset = 0;
    private int yOffset = 0;
    private double xMultiplier = 1;
    private double yMultiplier = 1;
    private double zMultiplier = -1;
    
    private double zNoMoveBegin = 0;
    private double zActiveBegin = 0;
    private double zAbsoluteBegin = 0;
    private double zRelativeBegin = 0;
    private double zActionBegin = 0;
    
    private double lastAbsoluteX = 0;
    private double lastAbsoluteY = 0;
    private double diffRemainderY = 0;
    private double diffRemainderX = 0;
    private double diffScrollRemainderY = 0;
    private double diffScrollRemainderX = 0;
    
    private int touchPadX = 0;
    private int touchPadY = 0;
    private double touchPadInputMultiplier = 5;
    private double touchPadOutputMultiplier = 1;
    private double touchPadAcceleration = 2;
    
    private double scrollInputMultiplier = 5;
    private double scrollOutputMultiplier = 1;
    private double scrollAcceleration = 2;
    
    private double relativeSensitivity = 0.1;
    
    private Boolean shouldDiscardOldPosition = true;
    
    public HandWaveyManager() {
        Config.setSingletonFilename("handWavey.yml");
        Config config = Config.singleton();
        
        
        Item configFormatVersion = config.newItem(
            "configFormatVersion",
            "2021-11-26",
            "This number is incremented by the programmer whenever existing config items get changed (eg new description, default value etc) so that conflicts can be resolved.");
        configFormatVersion.set("2021-11-26"); // Update it here.

        Group ultraMotion = config.newGroup("ultraMotion");
        ultraMotion.newItem(
            "debugLevel",
            "1",
            "Int: Sensible numbers are 0-5, where 0 is no debugging, and 5  is probably more detail than you'll ever want.");
        ultraMotion.newItem(
            "maxHands",
            "2",
            "Maximum number of hands to track. Anything more than this setting will be discarded, and assumptions can be made faster, so it will run faster. The most recent hands above the threshold are the ones to be discarded.");
        ultraMotion.newItem(
            "openThreshold",
            "1.7",
            "Float: When the last bone of the middle finger is less than this angle, the hand is assumed to be open.");
        Group coneOfSilence = ultraMotion.newGroup("coneOfSilence");
        coneOfSilence.newItem(
            "maxHeight",
            "500",
            "Beyond this height, the hand is considered too far away from the sensor to be useable, and is thus discarded.");
        coneOfSilence.newItem(
            "minHeight",
            "150",
            "Beyond this height, the hand is considered too far away from the sensor to be useable, and is thus discarded.");
        coneOfSilence.newItem(
            "maxCAtMaxHeight",
            "260",
            "When the hand is at min max height, how far from the center of the cone can the hand be horizontally before it it considered to be too unreliable.");
        coneOfSilence.newItem(
            "maxCAtMinHeight",
            "160",
            "When the hand is at the min height, how far from the center of the cone can the hand be horizontally before it it considered to be too unreliable.");

        Group handSummaryManager = config.newGroup("handSummaryManager");
        handSummaryManager.newItem(
            "debugLevel",
            "1",
            "Int: Sensible numbers are 0-5, where 0 is no debugging, and 5  is probably more detail than you'll ever want.");
        handSummaryManager.newItem(
            "rangeMethod",
            "manual",
            "How the range of possible hand positions is configured. Current possible values are: manual.");
        
        Group axisOrientation = handSummaryManager.newGroup("axisOrientation");
        axisOrientation.newItem(
            "xMultiplier",
            "1",
            "Set this to -1 when you need to invert X (side to side). You'll typicall only need to do this if your device is upside down. On newer LeapSDK versions, this may become obsolete.");
        axisOrientation.newItem(
            "yMultiplier",
            "1",
            "Set this to -1 when you need to invert Y (up and down). You'll typicall only need to do this if your device is upside down. On newer LeapSDK versions, this may become obsolete.");
        axisOrientation.newItem(
            "zMultiplier",
            "-1",
            "Set this to -1 when you need to invert Z (how far away from you your hand goes). UltraMotion takes care of this for you. So I can't currently think of a use-case for it, but am including it for completeness.");
        
        Group physicalBoundaries = handSummaryManager.newGroup("physicalBoundaries");
        physicalBoundaries.newItem(
            "x",
            "200",
            "+ and - this value horizontally from the center of the visible cone above the device.");
        physicalBoundaries.newItem(
            "yMin",
            "200",
            "Minimum value of height above the device.");
        physicalBoundaries.newItem(
            "yMax",
            "400",
            "Maximum value of height above the device.");
        physicalBoundaries.newItem(
            "z",
            "120",
            "+ and - this value in depth from the center of the visible cone above the device.");

        handSummaryManager.newItem(
            "zoneMode",
            "touchPad",
            "(touchScreen, touchPad). What type of device the zones approximate. The names are not an exact comparison, but should at least give an idea of how they work.");

        handSummaryManager.newItem(
            "zoneBuffer",
            "30",
            "Once a zone is entered, how far beyond the threshold must the hand retreat before the zone is considered exited?");

        Group zones = handSummaryManager.newGroup("zones");
        Group touchScreen = zones.newGroup("touchScreen");
        Group zoneNone = touchScreen.newGroup("zoneNone");
        // None currently doesn't require any config. Its group is here solely for completeness.
        
        Group absolute = touchScreen.newGroup("absolute");
        absolute.newItem(
            "threshold",
            "-120",
            "Z greater than this value denotes the beginning of the absolute zone.");
        absolute.newItem(
            "movingMeanBegin",
            "4",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        absolute.newItem(
            "movingMeanEnd",
            "20",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        
        Group relative = touchScreen.newGroup("relative");
        relative.newItem(
            "threshold",
            "0",
            "Z greater than this value denotes the beginning of the relative zone.");
        relative.newItem(
            "movingMeanBegin",
            "20",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        relative.newItem(
            "movingMeanEnd",
            "40",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        
        Group action = touchScreen.newGroup("action");
        action.newItem(
            "threshold",
            "80",
            "Z greater than this value denotes the beginning of the action zone.");
        action.newItem(
            "movingMeanBegin",
            "20",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        action.newItem(
            "movingMeanEnd",
            "20",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        
        Group scroll = touchScreen.newGroup("scroll");
        action.newItem(
            "movingMeanBegin",
            "1",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        action.newItem(
            "movingMeanEnd",
            "1",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        

        Group touchPad = zones.newGroup("touchPad");
        Group zoneTPNone = touchPad.newGroup("zoneNone");
        // None currently doesn't require any config. Its group is here solely for completeness.
        
        Group noMove = touchPad.newGroup("noMove");
        noMove.newItem(
            "threshold",
            "-190",
            "Z greater than this value denotes the beginning of the noMove zone.");
        
        Group active = touchPad.newGroup("active");
        active.newItem(
            "threshold",
            "-100",
            "Z greater than this value denotes the beginning of the active zone.");
        active.newItem(
            "movingMeanBegin",
            "4",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        active.newItem(
            "movingMeanEnd",
            "4",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        
        Group tpAction = touchPad.newGroup("action");
        tpAction.newItem(
            "threshold",
            "100",
            "Z greater than this value denotes the beginning of the action zone.");
        tpAction.newItem(
            "movingMeanBegin",
            "20",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        tpAction.newItem(
            "movingMeanEnd",
            "20",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        
        
        Group tpScroll = touchPad.newGroup("scroll");
        tpScroll.newItem(
            "movingMeanBegin",
            "1",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        tpScroll.newItem(
            "movingMeanEnd",
            "1",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        
        
        Group touchPadConfig = handSummaryManager.newGroup("touchPad");
        touchPadConfig.newItem(
            "inputMultiplier",
            "0.9",
            "Input is pretty small. Make it a bit bigger.");
        touchPadConfig.newItem(
            "outputMultiplier",
            "2",
            "Input is pretty small. Make it a bit bigger.");
        touchPadConfig.newItem(
            "acceleration",
            "2.3",
            "Small change in output moves the pointer very precisely. A larger movement moves the pointer much more drastically.");
        
        
        Group scrollConfig = handSummaryManager.newGroup("scroll");
        scrollConfig.newItem(
            "inputMultiplier",
            "1",
            "Input is pretty small. Make it a bit bigger.");
        scrollConfig.newItem(
            "outputMultiplier",
            "0.003",
            "Input is pretty small. Make it a bit bigger.");
        scrollConfig.newItem(
            "acceleration",
            "1.8",
            "Small change in output moves the pointer very precisely. A larger movement moves the pointer much more drastically.");
        
        
        Group audioConfig = config.newGroup("audioConfig");
        audioConfig.newItem(
            "pathToAudio",
            "audio" + File.separator + "clips",
            "Where are all of the audio clips stored.");
        
        Group audioEvents = config.newGroup("audioEvents");
        audioEvents.newItem(
            "zone-none",
            "metalDing01.wav",
            "Sound to play when the hand moves into the none zone.");
        
        audioEvents.newItem(
            "zone-noMove",
            "metalDing02.wav",
            "Sound to play when the hand moves into the noMove zone.");
        
        audioEvents.newItem(
            "zone-active",
            "metalDing03.wav",
            "Sound to play when the hand moves into the active zone.");
        
        audioEvents.newItem(
            "zone-action",
            "metalDing04.wav",
            "Sound to play when the hand moves into the action zone.");
        
        audioEvents.newItem(
            "zone-absolute",
            "metalDing02.wav",
            "Sound to play when the hand moves into the absolute zone.");
        
        audioEvents.newItem(
            "zone-relative",
            "metalDing03.wav",
            "Sound to play when the hand moves into the relative zone.");
        
        audioEvents.newItem(
            "zone-scroll",
            "metalDing05.wav",
            "Sound to play when the hand switches to the scroll zone.");
        
        audioEvents.newItem(
            "mouse-down",
            "metalDing07.wav",
            "Sound to play when the mouse is clicked.");
        
        audioEvents.newItem(
            "mouse-up",
            "metalDing08.wav",
            "Sound to play when the mouse button is released.");
        
        audioEvents.newItem(
            "imposterHand-replace",
            "",
            "When we detect that a hand ID is not what we expect, but are able to replace it and continue.");
        
        audioEvents.newItem(
            "imposterHand-discard",
            "",
            "When we detect that a hand ID is not what we expect, but not are able to replace it and continue.");
        
        
        handSummaryManager.newItem(
            "relativeSensitivity",
            "0.15",
            "How sensitive is the relative zone compared to the absolute zone? Decimal between 0 and 1.");
        
        Group gestureConfig = config.newGroup("gestureConfig");
        Group primaryHand = gestureConfig.newGroup("primaryHand");
        primaryHand.newItem(
            "rotationSegments",
            "4",
            "When you rotate your hand; it enters different segments. Increasing the number of segments increases the number of things you can do with your hand. Decreasing the number of segments makes it easier to be precise. Remember that some segments are hard for a human hand to reach, so you need to keep that in mind when choosing this number. It is expected that some segments will be unused for this reason. Don't hurt yourself.");
        primaryHand.newItem(
            "rotationOffset",
            "0",
            "In radians. Adjust where the segments are slightly to cater to your hand's natural bias.");
        Group secondaryHand = gestureConfig.newGroup("secondaryHand");
        secondaryHand.newItem(
            "rotationSegments",
            "4",
            "When you rotate your hand; it enters different segments. Increasing the number of segments increases the number of things you can do with your hand. Decreasing the number of segments makes it easier to be precise. Remember that some segments are hard for a human hand to reach, so you need to keep that in mind when choosing this number. It is expected that some segments will be unused for this reason. Don't hurt yourself.");
        secondaryHand.newItem(
            "rotationOffset",
            "0",
            "In radians. Adjust where the segments are slightly to cater to your hand's natural bias.");
        
        this.output = new GenericOutput();
        this.handsState = new HandsState();
        
        reloadConfig();
    }
    
    public void reloadConfig() {
        // This function reloads, and calcuates config based on the settings currently in Config. It does not trigger a reload of the config from file.
        Group handSummaryManager = Config.singleton().getGroup("handSummaryManager");
        
        
        // Set up the debugging.
        int debugLevel = Integer.parseInt(handSummaryManager.getItem("debugLevel").get());
        this.debug = new Debug(debugLevel, "HandWaveyManager");
        
        
        // Get configured multipliers.
        Group axisOrientation = handSummaryManager.getGroup("axisOrientation");
        int configuredXMultiplier = Integer.parseInt(axisOrientation.getItem("xMultiplier").get());
        int configuredYMultiplier = Integer.parseInt(axisOrientation.getItem("yMultiplier").get());
        int configuredZMultiplier = Integer.parseInt(axisOrientation.getItem("zMultiplier").get());
        this.zMultiplier = configuredZMultiplier;
        
        
        // Get the total desktop resolution.
        this.output.info();
        Dimension desktopResolution = this.output.getDesktopResolution();
        this.desktopWidth = desktopResolution.width;
        this.desktopHeight = desktopResolution.height;
        double desktopAspectRatio = this.desktopWidth/this.desktopHeight;
        
        // Set initial locaiton for touchPad based zoning.
        this.touchPadX = (int) Math.round(this.desktopWidth /3);
        this.touchPadY = (int) Math.round(this.desktopHeight /3);
        
        
        // Figure out how to best fit the desktop into the physical space.
        // TODO This could be abstracted out into testable code.
        Group physicalBoundaries = handSummaryManager.getGroup("physicalBoundaries");
        int pX = Integer.parseInt(physicalBoundaries.getItem("x").get());
        int pXDiff = pX * 2;
        int pYMin = Integer.parseInt(physicalBoundaries.getItem("yMin").get());
        int pYMax = Integer.parseInt(physicalBoundaries.getItem("yMax").get());
        int pYDiff = pYMax - pYMin;
        double physicalAspectRatio = pXDiff / pYDiff;
        
        this.xOffset = pX;
        this.yOffset = pYMin * -1;
        
        if (desktopAspectRatio > physicalAspectRatio) { // desktop is wider
            this.debug.out(1, "Desktop is wider than the cone. Optimising the usable cone for that.");
            this.yMultiplier = configuredYMultiplier * (this.desktopHeight/pYDiff);
            this.xMultiplier = configuredXMultiplier * (this.desktopHeight/pYDiff);
        } else { // desktop is narrower
            this.debug.out(1, "The cone is wider than the desktop. Optimising the usable cone for that.");
            this.xMultiplier = configuredXMultiplier * (this.desktopWidth/pXDiff);
            this.yMultiplier = configuredYMultiplier * (this.desktopWidth/pXDiff);
        }
        
        
        // Configure Z axis thresholds.
        this.zoneMode = handSummaryManager.getItem("zoneMode").get();
        if (this.zoneMode == "touchScreen") {
            Group touchScreen = handSummaryManager.getGroup("zones").getGroup("touchScreen");
            this.zAbsoluteBegin = Double.parseDouble(touchScreen.getGroup("absolute").getItem("threshold").get());
            this.zRelativeBegin = Double.parseDouble(touchScreen.getGroup("relative").getItem("threshold").get());
            this.zActionBegin = Double.parseDouble(touchScreen.getGroup("action").getItem("threshold").get());
            
            this.zones.put("none", new Zone(-999, this.zAbsoluteBegin, 1, 1));
            this.zones.put("absolute", new Zone(
                this.zAbsoluteBegin, this.zRelativeBegin,
                Integer.parseInt(touchScreen.getGroup("absolute").getItem("movingMeanBegin").get()),
                Integer.parseInt(touchScreen.getGroup("absolute").getItem("movingMeanEnd").get())));
            this.zones.put("relative", new Zone(
                this.zRelativeBegin, this.zActionBegin,
                Integer.parseInt(touchScreen.getGroup("relative").getItem("movingMeanBegin").get()),
                Integer.parseInt(touchScreen.getGroup("relative").getItem("movingMeanEnd").get())));
            this.zones.put("action", new Zone(
                this.zActionBegin, this.zActionBegin+50,
                Integer.parseInt(touchScreen.getGroup("action").getItem("movingMeanBegin").get()),
                Integer.parseInt(touchScreen.getGroup("action").getItem("movingMeanEnd").get())));
        } else if (this.zoneMode == "touchPad") {
            Group touchPad = handSummaryManager.getGroup("zones").getGroup("touchPad");
            this.zNoMoveBegin = Double.parseDouble(touchPad.getGroup("noMove").getItem("threshold").get());
            this.zActiveBegin = Double.parseDouble(touchPad.getGroup("active").getItem("threshold").get());
            this.zActionBegin = Double.parseDouble(touchPad.getGroup("action").getItem("threshold").get());
            
            this.zones.put("none", new Zone(-999, this.zNoMoveBegin, 1, 1));
            this.zones.put("noMove", new Zone(this.zNoMoveBegin, this.zActiveBegin, 1, 1));
            this.zones.put("active", new Zone(
                this.zActiveBegin, this.zActionBegin,
                Integer.parseInt(touchPad.getGroup("active").getItem("movingMeanBegin").get()),
                Integer.parseInt(touchPad.getGroup("active").getItem("movingMeanEnd").get())));
            this.zones.put("action", new Zone(
                this.zActionBegin, this.zActionBegin+50,
                Integer.parseInt(touchPad.getGroup("action").getItem("movingMeanBegin").get()),
                Integer.parseInt(touchPad.getGroup("action").getItem("movingMeanEnd").get())));
            this.zones.put("scroll", new Zone(
                9900, 9999,
                Integer.parseInt(touchPad.getGroup("active").getItem("movingMeanBegin").get()),
                Integer.parseInt(touchPad.getGroup("active").getItem("movingMeanEnd").get())));
        } else {
            // TODO This needs to produce some user feedback that the user will see. Once this runs as a service, a debug message won't be sufficient.
            this.debug.out(0, "Unknown zoneMode " + this.zoneMode + ". This will likely cause badness.");
        }
        
        this.debug.out(1, "Moving mean configured for zones:");
        for (String key: this.zones.keySet()) {
            this.debug.out(1, "  " + key + ":  " + this.zones.get(key).toString());
        }
        
        // Get relative sensitivity.
        this.relativeSensitivity = Double.parseDouble(handSummaryManager.getItem("relativeSensitivity").get());
        
        
        // Get Audio path.
        this.audioPath = Config.singleton().getGroup("audioConfig").getItem("pathToAudio").get() + File.separator;;
        
        
        // Get event sounds.
        loadEventSoundFromConfig("zone-none");
        loadEventSoundFromConfig("zone-noMove");
        loadEventSoundFromConfig("zone-active");
        loadEventSoundFromConfig("zone-action");
        
        loadEventSoundFromConfig("zone-absolute");
        loadEventSoundFromConfig("zone-relative");
        
        loadEventSoundFromConfig("zone-scroll");
        
        loadEventSoundFromConfig("mouse-down");
        loadEventSoundFromConfig("mouse-up");
        
        loadEventSoundFromConfig("imposterHand-replace");
        loadEventSoundFromConfig("imposterHand-discard");
        
        
        // Load touchpad mode config.
        Group touchPadConfig = handSummaryManager.getGroup("touchPad");
        this.touchPadInputMultiplier = Double.parseDouble(touchPadConfig.getItem("inputMultiplier").get());
        this.touchPadOutputMultiplier = Double.parseDouble(touchPadConfig.getItem("outputMultiplier").get());
        this.touchPadAcceleration = Double.parseDouble(touchPadConfig.getItem("acceleration").get());
        
        
        // Load scroll config.
        Group scrollConfig = handSummaryManager.getGroup("scroll");
        this.scrollInputMultiplier = Double.parseDouble(scrollConfig.getItem("inputMultiplier").get());
        this.scrollOutputMultiplier = Double.parseDouble(scrollConfig.getItem("outputMultiplier").get());
        this.scrollAcceleration = Double.parseDouble(scrollConfig.getItem("acceleration").get());
        
        // Config checks.
        checkZones();
    }
    
    private void checkZones() {
        // Compare every zone to every other zone to make sure that there are no unusable zones.
        
        Group handSummaryManager = Config.singleton().getGroup("handSummaryManager");
        double zoneBuffer = Double.parseDouble(handSummaryManager.getItem("zoneBuffer").get());
        
        for (String outerKey: this.zones.keySet()) {
            Zone outerZone = this.zones.get(outerKey);
            for (String innerKey: this.zones.keySet()) {
                if (outerKey != innerKey) {
                    Zone innerZone = this.zones.get(innerKey);
                    
                    double diff = Math.abs(innerZone.getBegin() - outerZone.getBegin());
                    
                    if (diff <= zoneBuffer) {
                        this.debug.out(0, "Warning: There does not appear to be enough usable space between zones " + outerKey + " and " + innerKey + ". Either reduce the zoneBuffer, or increase the distance between these two zones.");
                    }
                }
            }
        }
    }
    
    private void loadEventSoundFromConfig(String eventID) {
        this.debug.out(3, "Load eventID " + eventID);
        Group audioEvents = Config.singleton().getGroup("audioEvents");
        String filePath = audioEvents.getItem(eventID).get();
        
        this.eventSounds.put(eventID, filePath);
    }
    
    private void moveMouse(int x, int y) {
        if (x < 0) x = 0;
        if (x > this.desktopWidth) x = this.desktopWidth-1;
        
        if (y < 0) y = 0;
        if (y > this.desktopHeight) y = this.desktopHeight-1;
        
        this.output.setPosition(x, y);
    }
    
    private int coordToDesktopIntX(double xCoord) {
        return (int) Math.round((xCoord + this.xOffset) * this.xMultiplier);
    }
    
    private int coordToDesktopIntY(double yCoord) {
        return this.desktopHeight - (int) Math.round((yCoord + this.yOffset) * this.yMultiplier);
    }
    
    public void discardOldPosition() {
        this.shouldDiscardOldPosition = true;
    }
    
    private void moveMouseTouchPadFromCoordinates(double xCoord, double yCoord) {
        // Calculate how far the hand has moved since the last iteration.
        double xCoordDiff = 0;
        double yCoordDiff = 0;
        
        if (!this.shouldDiscardOldPosition) {
            xCoordDiff = xCoord - this.lastAbsoluteX + this.diffRemainderX;
            yCoordDiff = yCoord - this.lastAbsoluteY + this.diffRemainderY;
        } else {
            this.shouldDiscardOldPosition = false;
        }
        
        // Calculate our acceleration.
        double accelerationThreshold = 1;
        double angularDiff = Math.pow((Math.pow(xCoordDiff, 2) + Math.pow(yCoordDiff, 2)), 0.5);
        
        double accelerationMultiplier = accelerationThreshold;
        if (angularDiff > accelerationThreshold) {
            accelerationMultiplier = angularDiff * this.touchPadAcceleration;
        }
        
        // Bring everything together to calcuate how far we should move the cursor.
        double xInput = xCoordDiff * this.touchPadInputMultiplier;
        int diffX = (int) Math.round(xInput * accelerationMultiplier * this.touchPadOutputMultiplier);
        double yInput = yCoordDiff * this.touchPadInputMultiplier;
        int diffY = (int) Math.round(yInput * accelerationMultiplier * this.touchPadOutputMultiplier);
        
        // Apply the changes.
        this.touchPadX = this.touchPadX + diffX;
        this.touchPadY = this.touchPadY - diffY;
        
        // Carry over anything that happened, but didn't result in a movement. This means that we can make use of the finer movements without having to move the acceleration and multipliers to extremes.
        this.diffRemainderX = (diffX == 0)?xCoordDiff:0;
        this.diffRemainderY = (diffY == 0)?yCoordDiff:0;
        
        // Not catching OOB here makes the mouse feel sticky on the edges.
        if (this.touchPadX < 0) this.touchPadX = 0;
        if (this.touchPadY < 0) this.touchPadY = 0;
        if (this.touchPadX > this.desktopWidth) this.touchPadX = this.desktopWidth;
        if (this.touchPadY > this.desktopHeight) this.touchPadY = this.desktopHeight;
        
        // Track where we are now so that differences make sense on the next round.
        this.lastAbsoluteX = xCoord;
        this.lastAbsoluteY = yCoord;
        
        moveMouse(this.touchPadX, this.touchPadY);
    }
    
    private void scrollFromCoordinates(double xCoord, double yCoord) {
        // Reset touchPad motion since anything from before the scroll will be meaningless.
        this.diffRemainderX = 0;
        this.diffRemainderY = 0;
        
        // Calculate how far the hand has moved since the last iteration.
        double xCoordDiff = 0;
        double yCoordDiff = 0;
        
        if (!this.shouldDiscardOldPosition) {
            xCoordDiff = xCoord - this.lastAbsoluteX + this.diffScrollRemainderX;
            yCoordDiff = yCoord - this.lastAbsoluteY + this.diffScrollRemainderY;
        } else {
            this.shouldDiscardOldPosition = false;
        }
        
        // Calculate our acceleration.
        double accelerationThreshold = 1;
        double angularDiff = Math.pow((Math.pow(xCoordDiff, 2) + Math.pow(yCoordDiff, 2)), 0.5);
        
        double accelerationMultiplier = accelerationThreshold;
        if (angularDiff > accelerationThreshold) {
            accelerationMultiplier = angularDiff * this.scrollAcceleration;
        }
        
        // Bring everything together to calcuate how far we should move the cursor.
        double xInput = xCoordDiff * this.scrollInputMultiplier;
        double yInput = yCoordDiff * this.scrollInputMultiplier;
        
        int diffX = 0;
        int diffY = 0;
        
        if (this.scrollAcceleration > 1) {
            diffX = (int) Math.round(xInput * accelerationMultiplier * this.scrollOutputMultiplier);
            diffY = (int) Math.round(yInput * accelerationMultiplier * this.scrollOutputMultiplier);
        } else {
            diffX = (int) Math.round(xInput * this.scrollOutputMultiplier);
            diffY = (int) Math.round(yInput * this.scrollOutputMultiplier);
        }
        
        // Apply the changes.
        this.output.scroll(diffY);
        
        // Carry over anything that happened, but didn't result in a movement. This means that we can make use of the finer movements without having to move the acceleration and multipliers to extremes.
        this.diffScrollRemainderX = (diffX == 0)?xCoordDiff:0;
        this.diffScrollRemainderY = (diffY == 0)?yCoordDiff:0;
        
        // Track where we are now so that differences make sense on the next round.
        this.lastAbsoluteX = xCoord;
        this.lastAbsoluteY = yCoord;
    }
    
    private void touchPadNone(double xCoord, double yCoord) {
        // This is needed, because otherwise we end up back where we started every time we lift and re-apply.
        this.lastAbsoluteX = xCoord;
        this.lastAbsoluteY = yCoord;
    }
    
    private void moveMouseAbsoluteFromCoordinates(double xCoord, double yCoord) {
        int calculatedX = coordToDesktopIntX(xCoord);
        int calculatedY = coordToDesktopIntY(yCoord);
        
        this.lastAbsoluteX = xCoord;
        this.lastAbsoluteY = yCoord;
        
        moveMouse(calculatedX, calculatedY);
    }
    
    private void moveMouseRelativeFromCoordinates(double xCoord, double yCoord) {
        double xDiff = xCoord - this.lastAbsoluteX;
        double yDiff = yCoord - this.lastAbsoluteY;
        
        int calculatedX = (int) Math.round(coordToDesktopIntX(this.lastAbsoluteX + (xDiff * this.relativeSensitivity)));
        int calculatedY = (int) Math.round(coordToDesktopIntY(this.lastAbsoluteY + (yDiff * this.relativeSensitivity)));
        
        moveMouse(calculatedX, calculatedY);
    }
    
    private void updateMovingMeans(String zone, double handZ) {
        this.movingMeanX.set(this.handSummaries[0].getHandX());
        this.movingMeanY.set(this.handSummaries[0].getHandY());
        this.movingMeanX.resize(this.zones.get(zone).getMovingMeanWidth(handZ));
        this.movingMeanY.resize(this.zones.get(zone).getMovingMeanWidth(handZ));
    }
    
    public void triggerEvent(String eventID) {
        String fileName = this.eventSounds.get(eventID);
        
        if (fileName != "") {
            String fullPath = this.audioPath + fileName;
            this.debug.out(1, "Triggering event " + eventID + " File: " + fullPath);
            
            BackgroundSound.play(fullPath);
        }
    }
    
    private Boolean secondaryHandIsActive() {
        return ((this.handSummaries.length > 1) && (this.handSummaries[1] != null) && (this.handSummaries[1].isValid()));
    }
    
    private String coordsToString(double x, double y) {
        return String.valueOf(Math.round(x)) + ", " + String.valueOf(Math.round(y));
    }
    
    private void handleKeysDowns() {
        for (String key : this.output.getKeysIKnow()) {
            if (this.handsState.shouldKeyDown(key)) {
                this.debug.out(1, "Key down: " + key);
                this.output.keyDown(this.output.getKeyID(key));
            }
        }
    }
    
    private void handleKeyUps() {
        for (String key : this.output.getKeysIKnow()) {
            if (this.handsState.shouldKeyUp(key)) {
                this.debug.out(1, "Key up:   " + key);
                this.output.keyUp(this.output.getKeyID(key));
            }
        }
    }
    
    /* TODO
    
    * Fix jumping when out of range.
    * Position history.
        * On mouse down/up, use the position from a moment in time ago.
        * On middle down/up, replay scroll position since a moment in time ago. Leave it there.
    * Config based mapping to actions.
    * VNC for initial compatibility with wayland?
    * Config to/from disk.
    * Synth audio feedback for when close to zone boundaries.
    * Occasional freeze:
        * Usually sound stops. But other stuff still works.
        * Check whether threads are lingering.
        * Check GC.
    
    */
    
    // This is where everything gets glued together.
    public void sendHandSummaries(HandSummary[] handSummaries) {
        this.handSummaries = handSummaries;
        
        Double handZ = this.handSummaries[0].getHandZ() * this.zMultiplier;
        String zone = this.handsState.getZone(handZ);
        this.handsState.setHandClosed(!this.handSummaries[0].handIsOpen());
        
        this.handsState.derivePrimaryHandSegment(this.handSummaries[0].getHandRoll(), this.handSummaries[0].handIsLeft());
        if (secondaryHandIsActive()) {
            this.handsState.deriveSecondaryHandSegment(this.handSummaries[1].getHandRoll(), this.handSummaries[1].handIsLeft());
        } else {
            this.handsState.noSecondaryHand();
        }
        
        // Figure out the current state of of gestures.
        this.handsState.figureOutStuff();
        
        // This should happen before any potential de-stabilisation has happened.
        if (this.handsState.shouldMouseUp() == true) {
            this.debug.out(1, "Mouse down at " + coordsToString(this.movingMeanX.get(), this.movingMeanY.get()));
            this.output.mouseUp(this.output.getLastMouseButton());
            triggerEvent("mouse-up");
        }
        
        handleKeyUps();

        // Move the mouse cursor.
        if ((zone == "none") || (zone == "noMove")) {
            if (this.zoneMode == "touchPad") {
                updateMovingMeans(zone, handZ);
                touchPadNone(this.movingMeanX.get(), this.movingMeanY.get());
            }
        } else if (zone == "active") {
            updateMovingMeans(zone, handZ);
            moveMouseTouchPadFromCoordinates(this.movingMeanX.get(), this.movingMeanY.get());
        } else if (zone == "absolute") {
            updateMovingMeans(zone, handZ);
            moveMouseAbsoluteFromCoordinates(this.movingMeanX.get(), this.movingMeanY.get());
        } else if (zone == "relative") {
            updateMovingMeans(zone, handZ);
            moveMouseRelativeFromCoordinates(this.movingMeanX.get(), this.movingMeanY.get());
        } else if (zone == "action") {
        } else if (zone == "scroll") {
            updateMovingMeans(zone, handZ);
            scrollFromCoordinates(this.movingMeanX.get(), this.movingMeanY.get());
        } else {
            this.debug.out(3, "A hand was detected, but it outside of any zones. z=" + String.valueOf(handZ));
        }
        
        // This should happen after any potential stabilisation has happened.
        if (this.handsState.shouldMouseDown() == true) {
            String button = this.handsState.whichMouseButton();
            this.debug.out(1, "Mouse down (" + button + ") at " + coordsToString(this.movingMeanX.get(), this.movingMeanY.get()));
            this.output.mouseDown(this.output.getMouseButtonID(button));
            triggerEvent("mouse-down");
        }
        
        handleKeysDowns();
        
        // Audio events.
        if (this.handsState.zoneIsNew()) {
            String eventID = "zone-" + zone;
            triggerEvent(eventID);
        }
    }
}
