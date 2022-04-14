// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
All HandWavey configuration is either defined here, or in Gesture.generateConfig().
*/

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
        Config.setSingletonFilename(this.fileName);
        this.config = Config.singleton();
    }
    
    public void destroy() {
        // Remove any files managed by this class. This is intended for unit testing, but may later be useful for things like uninstall.
        // TODO Delete all files for fileName.
    }
    
    public void defineGeneralConfig() {
        // Add groups to separate out into separate files.
        this.config.addGroupToSeparate("debug");
        this.config.addGroupToSeparate("output");
        this.config.addGroupToSeparate("dataCleaning");
        this.config.addGroupToSeparate("ultraMotion");
        this.config.addGroupToSeparate("axisOrientation");
        this.config.addGroupToSeparate("physicalBoundaries");
        this.config.addGroupToSeparate("zones");
        this.config.addGroupToSeparate("touchPad");
        this.config.addGroupToSeparate("click");
        this.config.addGroupToSeparate("scroll");
        this.config.addGroupToSeparate("actionEvents");
        this.config.addGroupToSeparate("audioConfig");
        this.config.addGroupToSeparate("audioEvents");
        this.config.addGroupToSeparate("gestureConfig");
        
        // Build up general config.
        Item configFormatVersion = this.config.newItem(
            "configFormatVersion",
            "2021-11-26",
            "This number is incremented by the programmer whenever existing config items get changed (eg new description, default value etc) so that conflicts can be resolved.");
        configFormatVersion.set("2021-11-26"); // Update it here.
        Item saveBackConfig = this.config.newItem(
            "saveBackConfig",
            "true",
            "[true, false] Save back config after loading it. This has the effect of cleaning up the configuration files, and reflecting changes with new versions in the config file. The only time you'll want to turn this off while developing a config that you may want to share around. Ie if you make a mistake, it won't get lost. Increase debugging on Persistance to at least 1, and pay attention to the debug output to spot any mistakes that you've made. \"true\" == save back. \"false\" == don't save back.");
        configFormatVersion.set("2021-11-26"); // Update it here.

        Group output = this.config.newGroup("output");
        output.newItem(
            "device",
            "AWT",
            "[AWT, VNC, Null]: Which method to use to control the mouse and keyboard. Default is AWTOutput, which will be the best setting in most situations. VNC gives you a method of controlling a separate computer, and needs to be configured in the config group. NullOutput is there purely for testing.");
        Group vnc = output.newGroup("VNC");
        vnc.newItem(
            "host",
            "127.0.0.1",
            "The hostname to connect to.");
        vnc.newItem(
            "port",
            "5900",
            "When you start the VNC server, you'll get either a port number (eg 5901), or a desktop number (eg :1). If it's a desktop number, it should be added to 5900 to get the number that you want here. Eg :1 + 5900 = 5901.");
        vnc.newItem( // TODO Find a better way of doing this. See VNCOutput.java for more info.
            "password",
            "",
            "WARNING: This is currently stored in plain text. Please take that into account when assessing the security of your setup. It's worth having the VNC server only listening where it's needed, and in addition having the firewall configured to block it externally.");
        
        Group debug = this.config.newGroup("debug");
        debug.newItem(
            "HandWaveyManager",
            "0",
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
        debug.newItem(
            "Pressables",
            "1",
            "Int: Sensible numbers are 0-5, where 0 is no debugging, and 5 is probably more detail than you'll ever want. Pressables is a class for tracking which keys/buttons we can press and how that translates into the protocol that controls the mouse and keyboard. If there are keys/buttons that you want to use that aren't supported, this is the place to start.");
        debug.newItem(
            "VNCOutput",
            "1",
            "Int: Sensible numbers are 0-5, where 0 is no debugging, and 5 is probably more detail than you'll ever want. VNCOutput is a way to control the mouse and keyboard of a separate machine.");
        debug.newItem(
            "Persistence",
            "1",
            "Int: Sensible numbers are 0-5, where 0 is no debugging, and 5 is probably more detail than you'll ever want. Persistence is responsible for saving and loading configuration.");
        debug.newItem(
            "Config",
            "0",
            "Int: Sensible numbers are 0-5, where 0 is no debugging, and 5 is probably more detail than you'll ever want. Config is responsible for managing configuration. It will be rare that you'll need to do anything with this. But it's here for if you do need to. Not that this setting it not applied until the first configuration file has been loaded. Before that it defaults to level 0 and there is no debugging that isn't already displayed at level 0.");
        debug.newItem(
            "HandsState",
            "1",
            "Int: Sensible numbers are 0-5, where 0 is no debugging, and 5 is probably more detail than you'll ever want. HandsState tracks what gesture the hands are currently making, and triggers events based on changes.");
        debug.newItem(
            "HandStateEvents",
            "0",
            "Int: Sensible numbers are 0-5, where 0 is no debugging, and 5 is probably more detail than you'll ever want. If you want information about events, you should first check out HandWaveyEvent. If you're not sure why those events are being triggered, then this class will tell you about the state changes that are generating those events. Normally users won't need this information.");
        debug.newItem(
            "bug.ShouldComplete/BackgroundSound/play",
            "0",
            "Int: Sensible numbers are 0-2, where 0 will only tell you when a bug has been detected. 1 tells you what has been started, and 2 tells you what has completed as well (this is probably redundant, since level 0 still tells you on the next round when something hasn't finished.) Generally you'll want to keep this at 0. But if want to see that something is even being attempted, this will help. This entry is for playing audio.");
        debug.newItem(
            "bug.ShouldComplete/HandWaveyEvent/event",
            "0",
            "Int: Sensible numbers are 0-2, where 0 will only tell you when a bug has been detected. 1 tells you what has been started, and 2 tells you what has completed as well (this is probably redundant, since level 0 still tells you on the next round when something hasn't finished.) Generally you'll want to keep this at 0. But if want to see that something is even being attempted, this will help. This entry is for hand specific events.");
        debug.newItem(
            "bug.ShouldComplete/figureStuffOut",
            "0",
            "Int: Sensible numbers are 0-2, where 0 will only tell you when a bug has been detected. 1 tells you what has been started, and 2 tells you what has completed as well (this is probably redundant, since level 0 still tells you on the next round when something hasn't finished.) Generally you'll want to keep this at 0. But if want to see that something is even being attempted, this will help. This entry is for the figureStuffOut function in HandWaveyManager.");
        debug.newItem(
            "bug.ShouldComplete/MacroCore/instruction",
            "0",
            "Int: Sensible numbers are 0-2, where 0 will only tell you when a bug has been detected. 1 tells you what has been started, and 2 tells you what has completed as well (this is probably redundant, since level 0 still tells you on the next round when something hasn't finished.) Generally you'll want to keep this at 0. But if want to see that something is even being attempted, this will help. This entry is for the individual macro instructions.");
        debug.newItem(
            "bug.ShouldComplete/MacroLine/line",
            "0",
            "Int: Sensible numbers are 0-2, where 0 will only tell you when a bug has been detected. 1 tells you what has been started, and 2 tells you what has completed as well (this is probably redundant, since level 0 still tells you on the next round when something hasn't finished.) Generally you'll want to keep this at 0. But if want to see that something is even being attempted, this will help. This entry is for macro lines.");
        
        Group dataCleaning = this.config.newGroup("dataCleaning");
        dataCleaning.newItem(
            "maxChange",
            "30",
            "If the difference between the current input position and the previous input position is larger than this number, ignore it, and reset the state so that subsequent input makes sense. This is usually caused by going OOB on one side of the usable cone, and re-entering on the other side of the cone. When this number is too high, errors can slip through that cause the mouse cursor to jump. When it's too low, the cursor will regularly stop when you move your hand too fast. This symptom should not be confused with a hang due to something like garbage collection.");
        
        Group newHands = dataCleaning.newGroup("newHands");
        newHands.newItem(
            "earlyUnfreeze",
            "true",
            "Boolean (true|false): While the hand enters into the view of the UltraMotion device, the data is erratic. Therefore we want to ignore the first few moments until the data has stabilised. But if the hand gets to a location that we know is stable, we can assume that all is well, and unlock the cursor. Set to false if you are getting erratic behavior, yet the debug output is telling you that \"The hand seems ready.\"");
        newHands.newItem(
            "earlyUnfreezeZone",
            "active",
            "String (noMove, active, action): While the hand enters into the view of the UltraMotion device, the data is erratic. Therefore we want to ignore the first few moments until the data has stabilised. But if the hand gets to a location that we know is stable, we can assume that all is well, and unlock the cursor. Set this to the zone where the hand is most above the sensor. Most of fthe time, you'll want this to be \"active\".");
        newHands.newItem(
            "cursorFreezeFirstMillis",
            "200",
            "Int (milliseconds): While the hand enters into the view of the UltraMotion device, the data is erratic. Therefore we want to ignore the first few moments until the data has stabilised. This wait time is measured in milliseconds, and stops the cursor from moving.");
        newHands.newItem(
            "eventFreezeFirstMillis",
            "400",
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
            "Float: When the last bone of the openFinger is less than this angle, the hand is assumed to be open.");
        ultraMotion.newItem(
            "openFinger",
            "3",
            "Int: Which finger to use to determine that the hand is open or closed. If you are having incorrect open/closed state, this is the first thing to check. Have a look at the \"Leap Motion Diagnostic Visualizer\" to see what every finger is doing when you see the incorrect behavior. On the devices that I've tested so far, finger 1 and 2 (pointing and index) are regularly in the incorrect state, while other fingers seem to be fine.");
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

        Group axisOrientation = this.config.newGroup("axisOrientation");
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
        
        Group physicalBoundaries = this.config.newGroup("physicalBoundaries");
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

        this.config.newItem(
            "zoneMode",
            "touchPad",
            "(touchScreen, touchPad). What type of device the zones approximate. The names are not an exact comparison, but should at least give an idea of how they work.");

        this.config.newItem(
            "zoneBuffer",
            "30",
            "Once a zone is entered, how far beyond the threshold must the hand retreat before the zone is considered exited?");

        Group zones = this.config.newGroup("zones");
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
        
        
        Group touchPadConfig = this.config.newGroup("touchPad");
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
        
        
        Group clickConfig = this.config.newGroup("click");
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


        Group scrollConfig = this.config.newGroup("scroll");
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
            "200",
            "int milliseconds. When we do a middle clicking motion while doing the scrolling gesture, it's easy to accidentally scroll. The idea of this setting is to get a position that is just before we started doing the gesture. The default should be pretty close for most people, but if you find that the scroll is still disrupted by the gesture, increase this number. If it rewinds to a time well before you began the gesture, then decrease this number.");
        scrollConfig.newItem(
            "historySize",
            "40",
            "int <4096. How many samples to keep. We only need enough to rewind by what ever amount of time is defined in rewindCursorTime. Eg: If we get 5-30 frames per second, 40 should be plenty to cater to rewind times up to 1000 milliseconds.");
        
        
        Group actionEvents = this.config.newGroup("actionEvents"); // Entirely generated in Gesture.
        actionEvents.newItem(
            "special-newHandFreeze",
            "",
            "When a new primary hand is introduced, the cursor and the ability to click the mouse or press keys, is disabled while the device stabilises.");
        actionEvents.newItem(
            "special-newHandUnfreezeCursor",
            "",
            "When the time has expired for the Cursor freeze after a new primary hand is introduced.");
        actionEvents.newItem(
            "special-newHandUnfreezeEvent",
            "",
            "When the time has expired for the Event freeze after a new primary hand is introduced. This event is triggered.");
        
        Group audioConfig = this.config.newGroup("audioConfig");
        audioConfig.newItem(
            "useAudio",
            "true",
            "[true, false]: Audio notifications give the user feedback about what gestures they are making, and make it much easier to use handWavey. If you are getting more notifications than you'd like, you should first try using a configuration that has less notifications from the examples. But if you want audio notifications gone entirely, you can set this to false.");
        audioConfig.newItem(
            "pathToAudio",
            "audio" + File.separator + "clips",
            "Where are all of the audio clips stored.");
        
        Group audioEvents = this.config.newGroup("audioEvents");
        audioEvents.newItem(
            "special-newHandFreeze",
            "",
            "When a new primary hand is introduced, the cursor and the ability to click the mouse or press keys, is disabled while the device stabilises.");
        audioEvents.newItem(
            "special-newHandUnfreezeCursor",
            "",
            "When the time has expired for the Cursor freeze after a new primary hand is introduced.");
        audioEvents.newItem(
            "special-newHandUnfreezeEvent",
            "",
            "When the time has expired for the Event freeze after a new primary hand is introduced.");
        audioEvents.newItem(
            "imposterHand-replace",
            "",
            "When we detect that a hand ID is not what we expect, but are able to replace it and continue.");
        audioEvents.newItem(
            "imposterHand-discard",
            "",
            "When we detect that a hand ID is not what we expect, but not are able to replace it and continue.");
        audioEvents.newItem(
            "bug",
            "coocoo1.wav",
            "When a bug is detected, play this sound.");
        
        
        this.config.newItem(
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
        primaryHand.newItem(
            "mergeIntoSegment",
            "0",
            "Merge unused segments into this segment. This has the effect of not causing an unnecessary segment change event when accidentally moving into an unused segment.");
        primaryHand.newItem(
            "mergeFrom",
            "0",
            "Merge between this value, and mergeTo, into mergeIntoSegment.");
        primaryHand.newItem(
            "mergeTo",
            "0",
            "Merge between this value, and mergeTo, into mergeIntoSegment.");
        Group secondaryHand = gestureConfig.newGroup("secondaryHand");
        secondaryHand.newItem(
            "rotationSegments",
            "4",
            "When you rotate your hand; it enters different segments. Increasing the number of segments increases the number of things you can do with your hand. Decreasing the number of segments makes it easier to be precise. Remember that some segments are hard for a human hand to reach, so you need to keep that in mind when choosing this number. It is expected that some segments will be unused for this reason. Don't hurt yourself.");
        secondaryHand.newItem(
            "rotationOffset",
            "0",
            "In radians. Adjust where the segments are slightly to cater to your hand's natural bias.");
        secondaryHand.newItem(
            "mergeIntoSegment",
            "0",
            "Merge unused segments into this segment. This has the effect of not causing an unnecessary segment change event when accidentally moving into an unused segment.");
        secondaryHand.newItem(
            "mergeFrom",
            "0",
            "Merge between this value, and mergeTo, into mergeIntoSegment.");
        secondaryHand.newItem(
            "mergeTo",
            "0",
            "Merge between this value, and mergeTo, into mergeIntoSegment.");
    }
}
