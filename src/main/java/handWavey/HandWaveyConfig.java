package handWavey;

import java.io.File;
import config.*;
import debug.Debug;

public class HandWaveyConfig {
    private Debug debug;
    private Config config;
    private String fileName = "handWavey";
    
    public HandWaveyConfig(String fileName) {
        this.fileName = fileName;
        Config.setSingletonFilename(this.fileName + ".yml");
        this.config = Config.singleton();
    }
    
    public void destroy() {
        // Remove any files managed by this class. This is intended for unit testing, but may later be useful for things like uninstall.
        // TODO Delete all files for fileName.
    }
    
    public void defineGeneralConfig() {
        Item configFormatVersion = this.config.newItem(
            "configFormatVersion",
            "2021-11-26",
            "This number is incremented by the programmer whenever existing config items get changed (eg new description, default value etc) so that conflicts can be resolved.");
        configFormatVersion.set("2021-11-26"); // Update it here.

        this.config.newItem(
            "output",
            "AWTOutput",
            "[AWTOutput, NullOutput]: Which method to use to control the mouse and keyboard. Default is AWTOutput, which will be the best setting in most situations. NullOutput is there purely for testing.");
        
        Group debug = this.config.newGroup("debug");
        debug.newItem(
            "HandWaveyManager",
            "1",
            "Int: Sensible numbers are 0-5, where 0 is no debugging, and 5 is probably more detail than you'll ever want. HandWaveyManager is the glue that brings everything together. If you're having trouble working out what to debug, start here.");
        debug.newItem(
            "UltraMotionInput",
            "1",
            "Int: Sensible numbers are 0-5, where 0 is no debugging, and 5 is probably more detail than you'll ever want. This option tunes the UltraMotion input method.");
        debug.newItem(
            "HandWaveyEvent",
            "0",
            "Int: Sensible numbers are 0-5, where 0 is no debugging, and 5 is probably more detail than you'll ever want. HandWaveyEvent is for taking the right actions when an event is triggers. If you're not sure if your eventActions or eventAudio is not behaving correctly, this is the place to look.");
        debug.newItem(
            "HandsState",
            "1",
            "Int: Sensible numbers are 0-5, where 0 is no debugging, and 5 is probably more detail than you'll ever want. HandsState tracks what gesture the hands are currently making, and triggers events based on changes.");
        debug.newItem(
            "MacroLine",
            "1",
            "Int: Sensible numbers are 0-5, where 0 is no debugging, and 5 is probably more detail than you'll ever want. MacroLine process commands that have come directly from events.");
        debug.newItem(
            "OutputProtection",
            "1",
            "Int: Sensible numbers are 0-5, where 0 is no debugging, and 5 is probably more detail than you'll ever want. OutputProtection makes sure that we are doing sensible things with the mouse and keyboard. Eg we aren't calling mouseUp on the same button without doing a mouseDown in between.");
        debug.newItem(
            "AWTOutput",
            "1",
            "Int: Sensible numbers are 0-5, where 0 is no debugging, and 5 is probably more detail than you'll ever want. AWTOutput is the default way to control the mouse and keyboard of a machine.");
        
        Group newHands = this.config.newGroup("newHands");
        newHands.newItem(
            "cursorFreezeFirstMillis",
            "200",
            "Int (milliseconds): While the hand enters into the view of the UltraMotion device, the data is erratic. Therefore we want to ignore the first few moments until the data has stabilised. This wait time is measured in milliseconds, and stops the cursor from moving.");
        newHands.newItem(
            "clickFreezeFirstMillis",
            "1200",
            "Int (milliseconds): While the hand enters into the view of the UltraMotion device, the data is erratic. Therefore we want to ignore the first few moments until the data has stabilised. This wait time is measured in milliseconds, and stops accidental clicks.");
        newHands.newItem(
            "oldHandsTimeout",
            "400",
            "Int (milliseconds): If we haven't had any update in this amount of time, any new frame that we receive is deemed to be new. Under normal, active operation, we receive 10s of frames per seciond. Ie <100 milliseconds.");
        
        Group ultraMotion = this.config.newGroup("ultraMotion");
        ultraMotion.newItem(
            "maxHands",
            "2",
            "Maximum number of hands to track. Anything more than this setting will be discarded, and assumptions can be made faster, so it will run faster. The most recent hands above the threshold are the ones to be discarded.");
        ultraMotion.newItem(
            "openThreshold",
            "1.8",
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

        Group handSummaryManager = this.config.newGroup("handSummaryManager");
        handSummaryManager.newItem(
            "maxChange",
            "30",
            "If the difference between the current input position and the previous input position is larger than this number, ignore it, and reset the state so that subsequent input makes sense. This is usually caused by going OOB on one side of the usable cone, and re-entering on the other side of the cone. When this number is too high, errors can slip through that cause the mouse cursor to jump. When it's too low, the cursor will regularly stop when you move your hand too fast. This symptom should not be confused with a hang due to something like garbage collection.");
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
        scroll.newItem(
            "movingMeanBegin",
            "1",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        scroll.newItem(
            "movingMeanEnd",
            "1",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");


        Group touchPad = zones.newGroup("touchPad");
        Group zoneTPNone = touchPad.newGroup("zoneNone");
        // None currently doesn't require any config. Its group is here solely for completeness.
        
        Group noMove = touchPad.newGroup("noMove");
        noMove.newItem(
            "threshold",
            "-150",
            "Z greater than this value denotes the beginning of the noMove zone.");
        
        Group active = touchPad.newGroup("active");
        active.newItem(
            "threshold",
            "-50",
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
            "1",
            "Input is pretty small. Make it a bit bigger.");
        touchPadConfig.newItem(
            "outputMultiplier",
            "2",
            "Input is pretty small. Make it a bit bigger.");
        touchPadConfig.newItem(
            "acceleration",
            "200",
            "Small change in output moves the pointer very precisely. A larger movement moves the pointer much more drastically.");
        touchPadConfig.newItem(
            "maxSpeed",
            "25",
            "Maximum speed per second.");
        
        
        Group clickConfig = handSummaryManager.newGroup("click");
        clickConfig.newItem(
            "rewindCursorTime",
            "300",
            "int milliseconds. When we do a clicking motion, we move in a way that disrupts the cursor. The idea of this setting is to get a position that is just before we started doing the gesture. The default should be pretty close for most people, but if you find that the cursor is still disrupted by the gesture, increase this number. If it rewinds to a time well before you began the gesture, then decrease this number.");
        clickConfig.newItem(
            "repeatRewindCursorTime",
            "1100",
            "int milliseconds. When the rewind is triggered within this many milliseconds from the last rewind, use the same cursor position as last time. This is so that a mouse up uses the same position as a mouse down.");
        clickConfig.newItem(
            "historySize",
            "400",
            "int <4096. How many samples to keep. We only need enough to rewind by what ever amount of time is defined in rewindCursorTime. Eg: If we get 5-30 frames per second, 40 should be plenty to cater to rewind times up to 1000 milliseconds.");
        clickConfig.newItem(
            "cursorLockTime",
            "600",
            "int milliseconds <4096. How long to stop the cursor from moving after a mouse down event. This is to make it easy to click on something without dragging it. Making this shorter will make drags feel more responsive. Making it longer will make it easier to click when you are having trouble completing a click quickly.");


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
            "180",
            "Small change in output moves the pointer very precisely. A larger movement moves the pointer much more drastically.");
        scrollConfig.newItem(
            "maxSpeed",
            "35",
            "Maximum speed per second.");
        scrollConfig.newItem(
            "rewindScrollTime",
            "300",
            "int milliseconds. When we do a middle clicking motion while doing the scrolling gesture, it's easy to accidentally scroll. The idea of this setting is to get a position that is just before we started doing the gesture. The default should be pretty close for most people, but if you find that the scroll is still disrupted by the gesture, increase this number. If it rewinds to a time well before you began the gesture, then decrease this number.");
        scrollConfig.newItem(
            "historySize",
            "40",
            "int <4096. How many samples to keep. We only need enough to rewind by what ever amount of time is defined in rewindCursorTime. Eg: If we get 5-30 frames per second, 40 should be plenty to cater to rewind times up to 1000 milliseconds.");
        
        
        Group actionEvents = this.config.newGroup("actionEvents"); // Entirely generated in Gesture.
        actionEvents.newItem(
            "newHandFreeze",
            "",
            "When a new primary hand is introduced, the cursor and the ability to click the mouse or press keys, is disabled while the device stabilises.");
        actionEvents.newItem(
            "newHandUnfreezeCursor",
            "",
            "When the time has expired for the Cursor freeze after a new primary hand is introduced.");
        actionEvents.newItem(
            "newHandUnfreezeClick",
            "",
            "When the time has expired for the Click freeze after a new primary hand is introduced.");
        
        Group audioConfig = this.config.newGroup("audioConfig");
        audioConfig.newItem(
            "pathToAudio",
            "audio" + File.separator + "clips",
            "Where are all of the audio clips stored.");
        
        Group audioEvents = this.config.newGroup("audioEvents");
        audioEvents.newItem(
            "newHandFreeze",
            "",
            "When a new primary hand is introduced, the cursor and the ability to click the mouse or press keys, is disabled while the device stabilises.");
        audioEvents.newItem(
            "newHandUnfreezeCursor",
            "",
            "When the time has expired for the Cursor freeze after a new primary hand is introduced.");
        audioEvents.newItem(
            "newHandUnfreezeClick",
            "",
            "When the time has expired for the Click freeze after a new primary hand is introduced.");
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
        
        Group gestureConfig = this.config.newGroup("gestureConfig");
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
    }
}
