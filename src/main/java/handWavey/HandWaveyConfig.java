// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
All HandWavey configuration is either defined here, or in Gesture.generateConfig().
*/

package handWavey;

import java.io.File;
import config.*;

public class HandWaveyConfig {
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
        this.config.addGroupToSeparate("handCleaner");
        this.config.addGroupToSeparate("tap");
        this.config.addGroupToSeparate("macros");

        // Build up general config.
        Item configFormatVersion = this.config.newItem(
            "configFormatVersion",
            "2021-11-26",
            "This number is incremented by the programmer whenever existing config items get changed (eg new description, default value etc) so that conflicts can be resolved.");
        configFormatVersion.set("2021-11-26"); // Update it here.

        this.config.newItem(
            "saveBackConfig",
            "true",
            "[true, false] Save back config after loading it. This has the effect of cleaning up the configuration files, and reflecting changes with new versions in the config file. The only time you'll want to turn this off while developing a config that you may want to share around. Ie if you make a mistake, it won't get lost. Increase debugging on Persistance to at least 1, and pay attention to the debug output to spot any mistakes that you've made. \"true\" == save back. \"false\" == don't save back.");

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
            "Motion",
            "1",
            "Int: Sensible numbers are 0-5, where 0 is no debugging, and 5 is probably more detail than you'll ever want. Motion takes hand movements and converts them into output movements like moving the mouse cursor, or scrolling.");
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
        debug.addItemTemplate(
            "bug.ShouldComplete.MacroCore.instruction-.*",
            "0",
            "Int: Sensible numbers are 0-2, where 0 will only tell you when a bug has been detected. 1 tells you what has been started, and 2 tells you what has completed as well (this is probably redundant, since level 0 still tells you on the next round when something hasn't finished.) Generally you'll want to keep this at 0. But if want to see that something is even being attempted, this will help. This entry is for the individual macro instructions at nesting level denoted at the end of this setting name.");
        debug.addItemTemplate(
            "bug.ShouldComplete.MacroLine.line-.*",
            "0",
            "Int: Sensible numbers are 0-2, where 0 will only tell you when a bug has been detected. 1 tells you what has been started, and 2 tells you what has completed as well (this is probably redundant, since level 0 still tells you on the next round when something hasn't finished.) Generally you'll want to keep this at 0. But if want to see that something is even being attempted, this will help. This entry is for the individual macro instructions at nesting level denoted at the end of this setting name.");
        debug.addItemTemplate(
            "BackgroundSound",
            "1",
            "Int: Sensible numbers are 0-1, where 0 will not tell anything. And 1 tells you when ever the maxCount has been exceeded.");
        debug.addItemTemplate(
            "Changed",
            "1",
            "Int: Sensible numbers are 0-1, where 0 will not tell anything. And 1 tells you when a data type has changed from what it was initialised as.");

        Group dataCleaning = this.config.newGroup("dataCleaning");
        dataCleaning.newItem(
            "maxChange",
            "20",
            "If the difference between the current input position and the previous input position is larger than this number, ignore it, and reset the state so that subsequent input makes sense. This is usually caused by going OOB on one side of the usable cone, and re-entering on the other side of the cone. When this number is too high, errors can slip through that cause the mouse cursor to jump. When it's too low, the cursor will regularly stop when you move your hand too fast. This symptom should not be confused with a hang due to something like garbage collection.");
        dataCleaning.newItem(
            "minFrameGapSeconds",
            "0.002",
            "The minimum gap between frames in seconds. When the gap is smaller than this number, a debug message will be produced like \"Skipping frame that is only 0.001 seconds old.\", and the frame will not contribute to the cursor moving. If this number is too high, then too many frames will get skipped, and the cursor won't move as much as desired. If this number is too low, then pointer acceleration can be erratic when frame timings get erratic (Common during high CPU load.)");

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

        Group changeTimeouts = dataCleaning.newGroup("changeTimeouts");
        String defaultChangeTimeout = "20";
        Group primaryHandTimeouts = changeTimeouts.newGroup("primaryHand");
        primaryHandTimeouts.newItem(
            "zoneChanged",
            "0",
            "Int (milliseconds): The amount of time that a change in zone must be stable before it gets reported. You want a number that is small enough to not be noticed by a human. But large enough to catch errors that can cause random clicks/states.");
        primaryHandTimeouts.newItem(
            "OOBChanged",
            defaultChangeTimeout,
            "Int (milliseconds): The amount of time that a change in OOB (hands out of bounds/viewable area) must be stable before it gets reported. You want a number that is small enough to not be noticed by a human. But large enough to catch errors that can cause random clicks/states.");
        primaryHandTimeouts.newItem(
            "segmentChanged",
            defaultChangeTimeout,
            "Int (milliseconds): The amount of time that a change in segment must be stable before it gets reported. You want a number that is small enough to not be noticed by a human. But large enough to catch errors that can cause random clicks/states.");
        primaryHandTimeouts.newItem(
            "stateChanged",
            defaultChangeTimeout,
            "Int (milliseconds): The amount of time that a change in state (open/closed/absent) must be stable before it gets reported. You want a number that is small enough to not be noticed by a human. But large enough to catch errors that can cause random clicks/states.");
        primaryHandTimeouts.newItem(
            "stationaryChanged",
            defaultChangeTimeout,
            "Int (milliseconds): The amount of time that a change in whether the hand is stationary, or not, must be stable before it gets reported. You want a number that is small enough to not be noticed by a human. But large enough to catch errors that can cause random clicks/states.");
        primaryHandTimeouts.newItem(
            "zStationaryChanged",
            defaultChangeTimeout,
            "Int (milliseconds): The amount of time that a change in whether the hand is stationary on the z axis, or not, must be stable before it gets reported. You want a number that is small enough to not be noticed by a human. But large enough to catch errors that can cause random clicks/states.");
        // primaryHandTimeouts.newItem(
        //     "tapChanged",
        //     defaultChangeTimeout,
        //     "Int (milliseconds): The amount of time that a change in whether the hand has tapped, or not, must be stable before it gets reported. You want a number that is small enough to not be noticed by a human. But large enough to catch errors that can cause random clicks/states.");

        Group secondaryHandTimeouts = changeTimeouts.newGroup("secondaryHand");
        secondaryHandTimeouts.newItem(
            "zoneChanged",
            "0",
            "Int (milliseconds): The amount of time that a change in zone must be stable before it gets reported. You want a number that is small enough to not be noticed by a human. But large enough to catch errors that can cause random clicks/states.");
        secondaryHandTimeouts.newItem(
            "OOBChanged",
            defaultChangeTimeout,
            "Int (milliseconds): The amount of time that a change in OOB (hands out of bounds/viewable area) must be stable before it gets reported. You want a number that is small enough to not be noticed by a human. But large enough to catch errors that can cause random clicks/states.");
        secondaryHandTimeouts.newItem(
            "segmentChanged",
            defaultChangeTimeout,
            "Int (milliseconds): The amount of time that a change in segment must be stable before it gets reported. You want a number that is small enough to not be noticed by a human. But large enough to catch errors that can cause random clicks/states.");
        secondaryHandTimeouts.newItem(
            "stateChanged",
            defaultChangeTimeout,
            "Int (milliseconds): The amount of time that a change in state (open/closed/absent) must be stable before it gets reported. You want a number that is small enough to not be noticed by a human. But large enough to catch errors that can cause random clicks/states.");
        secondaryHandTimeouts.newItem(
            "stationaryChanged",
            defaultChangeTimeout,
            "Int (milliseconds): The amount of time that a change in whether the hand is stationary, or not, must be stable before it gets reported. You want a number that is small enough to not be noticed by a human. But large enough to catch errors that can cause random clicks/states.");
        secondaryHandTimeouts.newItem(
            "zStationaryChanged",
            defaultChangeTimeout,
            "Int (milliseconds): The amount of time that a change in whether the hand is stationary on the z axis, or not, must be stable before it gets reported. You want a number that is small enough to not be noticed by a human. But large enough to catch errors that can cause random clicks/states.");
        // secondaryHandTimeouts.newItem(
        //     "tapChanged",
        //     defaultChangeTimeout,
        //     "Int (milliseconds): The amount of time that a change in whether the hand has tapped, or not, must be stable before it gets reported. You want a number that is small enough to not be noticed by a human. But large enough to catch errors that can cause random clicks/states.");

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
        Group pbMap = physicalBoundaries.newGroup("map");
        pbMap.newItem(
            "x",
            "x",
            "Which axis from the device the x input is mapped to in software. This is useful when you want to rotate the input by 90 degrees.");
        pbMap.newItem(
            "y",
            "y",
            "Which axis from the device the y input is mapped to in software. This is useful when you want to rotate the input by 90 degrees.");
        pbMap.newItem(
            "z",
            "z",
            "Which axis from the device the z input is mapped to in software. This is useful when you want to rotate the input by 90 degrees.");
        Group pbOffets = physicalBoundaries.newGroup("inputOffsets");
        pbOffets.newItem(
            "x",
            "0",
            "How much to change the device's x axis by. Normally you won't want to change this from 0. But it's useful if you want to want to do something like swapping y with x or z.");
        pbOffets.newItem(
            "y",
            "0",
            "How much to change the device's y axis by. Normally you won't want to change this from 0. But it's useful if you want to want to do something like swapping y with x or z.");
        pbOffets.newItem(
            "z",
            "0",
            "How much to change the device's z axis by. Normally you won't want to change this from 0. But it's useful if you want to want to do something like swapping y with x or z.");
        Group pbRotMap = physicalBoundaries.newGroup("rotationMap");
        pbRotMap.newItem(
            "roll",
            "roll",
            "Which rotation axis from the device the roll input is mapped to in software. This is useful when you want to rotate the input by 90 degrees.");
        pbRotMap.newItem(
            "pitch",
            "pitch",
            "Which rotation axis from the device the pitch input is mapped to in software. This is useful when you want to rotate the input by 90 degrees.");
        pbRotMap.newItem(
            "yaw",
            "yaw",
            "Which rotation axis from the device the yaw input is mapped to in software. This is useful when you want to rotate the input by 90 degrees.");

        this.config.newItem(
            "zoneMode",
            "touchPad",
            "(touchScreen, touchPad). What type of device the zones approximate. The names are not an exact comparison, but should at least give an idea of how they work.");
        Group pbRotOffets = physicalBoundaries.newGroup("inputRotationOffsets");
        pbRotOffets.newItem(
            "roll",
            "0",
            "How much to change the device's roll axis by. Normally you won't want to change this from 0. But it's useful if you want to rotate the device into an unusual position.");
        pbRotOffets.newItem(
            "pitch",
            "0",
            "How much to change the device's pitch axis by. Normally you won't want to change this from 0. But it's useful if you want to rotate the device into an unusual position.");
        pbRotOffets.newItem(
            "yaw",
            "0",
            "How much to change the device's yaw axis by. Normally you won't want to change this from 0. But it's useful if you want to rotate the device into an unusual position.");

        this.config.newItem(
            "zoneBuffer",
            "30",
            "Once a zone is entered, how far beyond the threshold must the hand retreat before the zone is considered exited?");

        Group zones = this.config.newGroup("zones");
        Group touchScreen = zones.newGroup("touchScreen");
        touchScreen.newGroup("zoneNone");
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
            "70",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        scroll.newItem(
            "movingMeanEnd",
            "70",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");


        Group touchPad = zones.newGroup("touchPad");
        touchPad.newGroup("zoneNone");
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
            "15",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        active.newItem(
            "movingMeanEnd",
            "15",
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
            "70",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        tpScroll.newItem(
            "movingMeanEnd",
            "70",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");


        Group touchPadConfig = this.config.newGroup("touchPad");
        touchPadConfig.newItem(
            "unAcceleratedBaseMultiplier",
            "1.5",
            "Decimal: How much to speed up unaccelerated movement. 1 is no change, 0.5 is half as fast, 2 is twice as fast. Think of this as how fast you want to cursor to move normally.");
        touchPadConfig.newItem(
            "acceleratedBaseMultiplier",
            "1.7",
            "Decimal: How much to speed up accelerated movement. 1 is no change, 0.5 is half as fast, 2 is twice as fast. Think of this as how fast you want to cursor to move when acceleration is active.");
        touchPadConfig.newItem(
            "accelerationExponent",
            "1.3",
            "Decimal: How much to increase the power of the acceleration. 1 is no change, 0.5 is half as fast, 2 is twice as fast. Think of this as how power the acceleration is.");
        touchPadConfig.newItem(
            "accelerationThreshold",
            "14",
            "Decimal: The speed that the hand must move faster than for acceleration to apply.");
        touchPadConfig.newItem(
            "maxSpeed",
            "500",
            "Maximum speed per second.");


        Group clickConfig = this.config.newGroup("click");
        clickConfig.newItem(
            "rewindCursorTime",
            "200",
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
            "100",
            "int milliseconds. When exiting a scroll, it's easy to accidentally scroll. Often this is done simply from the leapmotion error. The idea of this setting is to get a position that is just before we started doing the gesture. The default should be pretty close for most people, but if you find that the scroll is still disrupted by the gesture, increase this number. If it rewinds to a time well before you began the gesture, then decrease this number.");
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
            "recalibrateSegments();setSlot(\"250\", \"custom-recalibrateSegments\");",
            "When the time has expired for the Cursor freeze after a new primary hand is introduced.");
        actionEvents.newItem(
            "special-newHandUnfreezeEvent",
            "",
            "When the time has expired for the Event freeze after a new primary hand is introduced. This event is triggered.");
        actionEvents.newItem(
            "special-primaryMoving",
            "movingProtection-enable();",
            "When the primary hand starts moving.");
        actionEvents.newItem(
            "special-primaryStationary",
            "movingProtection-disable();",
            "When the primary hand starts moving.");
        actionEvents.newItem(
            "special-secondaryMoving",
            "movingProtectionSecondary-enable();",
            "When the secondary hand starts moving.");
        actionEvents.newItem(
            "special-secondaryStationary",
            "movingProtectionSecondary-disable();",
            "When the secondary hand starts moving.");
        actionEvents.newItem(
            "special-primaryZMoving",
            "",
            "When the primary hand starts moving on the z axis.");
        actionEvents.newItem(
            "special-primaryZStationary",
            "setSlot(\"250\", \"custom-noop\");",
            "When the primary hand starts moving on the z axis.");
        actionEvents.newItem(
            "special-secondaryZMoving",
            "",
            "When the secondary hand starts moving on the z axis.");
        actionEvents.newItem(
            "special-secondaryZStationary",
            "",
            "When the secondary hand starts moving on the z axis.");
        this.generateCustomConfig(actionEvents);

        Group audioConfig = this.config.newGroup("audioConfig");
        audioConfig.newItem(
            "useAudio",
            "true",
            "[true, false]: Audio notifications give the user feedback about what gestures they are making, and make it much easier to use handWavey. If you are getting more notifications than you'd like, you should first try using a configuration that has less notifications from the examples. But if you want audio notifications gone entirely, you can set this to false.");
        audioConfig.newItem(
            "pathToAudio",
            "audio" + File.separator + "clips",
            "Where are all of the audio clips stored.");
        audioConfig.newItem(
            "maxCount",
            "8",
            "How many audio clips can play concurrently.");

        Group audioEvents = this.config.newGroup("audioEvents");
        audioEvents.newItem(
            "special-newHandFreeze",
            "",
            "When a new primary hand is introduced, the cursor and the ability to click the mouse or press keys, is disabled while the device stabilises.");
        audioEvents.newItem(
            "special-newHandUnfreezeCursor",
            "recalibrateSegments();",
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
        audioEvents.addItemTemplate("^custom-[0-9]+", "", "When this custom event that a user can trigger in a gestureLayout. It is intended to be used with slots, and can be read about in createADynamicGestureLayout.md.");
        audioEvents.newItem(
            "special-primaryMoving",
            "",
            "When the primary hand starts moving.");
        audioEvents.newItem(
            "special-primaryStationary",
            "",
            "When the primary hand starts moving.");
        audioEvents.newItem(
            "special-secondaryMoving",
            "",
            "When the secondary hand starts moving.");
        audioEvents.newItem(
            "special-secondaryStationary",
            "",
            "When the secondary hand starts moving.");


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
            "mergeIntoSegment",
            "1",
            "Merge unused segments into this segment. This has the effect of not causing an unnecessary segment change event when accidentally moving into an unused segment.");
        primaryHand.newItem(
            "mergeFrom",
            "2",
            "Merge between this value, and mergeTo, into mergeIntoSegment.");
        primaryHand.newItem(
            "mergeTo",
            "3",
            "Merge between this value, and mergeTo, into mergeIntoSegment.");
        Group secondaryHand = gestureConfig.newGroup("secondaryHand");
        secondaryHand.newItem(
            "rotationSegments",
            "4",
            "When you rotate your hand; it enters different segments. Increasing the number of segments increases the number of things you can do with your hand. Decreasing the number of segments makes it easier to be precise. Remember that some segments are hard for a human hand to reach, so you need to keep that in mind when choosing this number. It is expected that some segments will be unused for this reason. Don't hurt yourself.");
        secondaryHand.newItem(
            "mergeIntoSegment",
            "1",
            "Merge unused segments into this segment. This has the effect of not causing an unnecessary segment change event when accidentally moving into an unused segment.");
        secondaryHand.newItem(
            "mergeFrom",
            "2",
            "Merge between this value, and mergeTo, into mergeIntoSegment.");
        secondaryHand.newItem(
            "mergeTo",
            "3",
            "Merge between this value, and mergeTo, into mergeIntoSegment.");

        Group handCleaner = this.config.newGroup("handCleaner");
        handCleaner.newItem(
            "movingMeanX",
            "4",
            "The moving mean length for the X axis. >0. 1 effectively disables the moving mean. A larger number is more effective at removing noise, at the expense of responsiveness.");
        handCleaner.newItem(
            "movingMeanY",
            "4",
            "The moving mean length for the Y axis. >0. 1 effectively disables the moving mean. A larger number is more effective at removing noise, at the expense of responsiveness.");
        handCleaner.newItem(
            "movingMeanZ",
            "4",
            "The moving mean length for the Z axis. >0. 1 effectively disables the moving mean. A larger number is more effective at removing noise, at the expense of responsiveness.");
        handCleaner.newItem(
            "movingMeanRoll",
            "4",
            "The moving mean length for the roll axis. >0. 1 effectively disables the moving mean. A larger number is more effective at removing noise, at the expense of responsiveness.");
        handCleaner.newItem(
            "movingMeanPitch",
            "4",
            "The moving mean length for the pitch axis. >0. 1 effectively disables the moving mean. A larger number is more effective at removing noise, at the expense of responsiveness.");
        handCleaner.newItem(
            "movingMeanYaw",
            "4",
            "The moving mean length for the yaw axis. >0. 1 effectively disables the moving mean. A larger number is more effective at removing noise, at the expense of responsiveness.");
        handCleaner.newItem(
            "movingMeanFinger",
            "4",
            "The moving mean length for the finger (used for whether the hand is open or closed). >0. 1 effectively disables the moving mean. A larger number is more effective at removing noise, at the expense of responsiveness.");
        handCleaner.newItem(
            "autoTrimMaxChangePerSecond",
            "0.15",
            "The maximum change (in radians) that the auto-trim can apply per second. It is applied proportionally based on the duration since the last sample. The goal of auto-trim is to adjust to changes in the resting position of the hand so that segments still feel intuitive to the user despite the user not being consistent. Setting this to 0 disables autoTrim.");
        handCleaner.newItem(
            "autoTrimMaxChange",
            "1.57",
            "The maximum change (in radians) that the auto-trim can apply in total. Setting this too small will limit how much auto-trim can help you. Setting it too large could lead to confusing behavior. The goal of auto-trim is to adjust to changes in the resting position of the hand so that segments still feel intuitive to the user despite the user not being consistent.");
        handCleaner.newItem(
            "stationarySpeed",
            "22",
            "The speed, below which, the hand is considered stationary, and segment/state changes will be allowed. This is called speedLock. Setting this to -1 disables the speedLock. Change the debug level for HandsState to at least 2 to see the live speeds when the lock engages and disengages. You'll need stationarySpeed to be set to something positive for this to work. I suggest starting around 5-10.");

        Group tap = this.config.newGroup("tap");
        tap.newItem(
            "tapSpeed",
            "10",
            "The speed of the Z axis (away from you), above which, the hand is considered to be performing a tap. Setting this to -1 disables the tap gesture. You'll need tapSpeed to be set to something positive for this to work. I suggest starting around 5-10.");
        tap.newItem(
            "samplesToWaitNegative",
            "5",
            "Number of samples in the negative direction to wait until allowing another tap.");
        tap.newItem(
            "samplesToWaitPositive",
            "5",
            "Number of samples in the positive direction to wait until allowing a tap.");

        Group macros = this.config.newGroup("macros");
        generateMacroConfig(macros);
    }

    private void generateMacroConfig(Group macrosGroup) {
        macrosGroup.addItemTemplate(".*", "", "A user-defined macro.");


        macrosGroup.newItem(
            "prepForClick",
            "cancelAllDelayedDos();lockCursor();rewindCursorPosition();lockTaps(\"primary\");",
            "Prepare for a click.");

        macrosGroup.newItem(
            "prepForDelayedClick",
            "cancelAllDelayedDos();lockCursor();rewindCursorPosition(\"150\");lockTaps(\"primary\");",
            "Prepare for a delayed click.");

        macrosGroup.newItem(
            "mDownAmbiguous",
            "delayedDo(\"do-mDownAmbiguous\", \"150\");",
            "Mouse down - Ambiguous, with delay. To be called by the event.");

        macrosGroup.newItem(
            "do-mDownAmbiguous",
            "prepForClick();mouseDown();",
            "Mouse down - Ambiguous.");

        macrosGroup.newItem(
            "mUpAmbiguous",
            "cancelAllDelayedDos();rewindCursorPosition();releaseButtons();unlockCursor();unlockTaps(\"primary\");",
            "Mouse up - General.");

        macrosGroup.newItem(
            "mDownLeft",
            "delayedDo(\"do-mDownLeft\", \"150\");",
            "Mouse down - Left, with delay. To be called by the event.");

        macrosGroup.newItem(
            "do-mDownLeft",
            "setButton(\"left\");do-mDownAmbiguous();",
            "Mouse down - Left.");

        macrosGroup.newItem(
            "mDownRight",
            "delayedDo(\"do-mDownRight\", \"150\");",
            "Mouse down - Right, with delay. To be called by the event.");

        macrosGroup.newItem(
            "do-mDownRight",
            "setButton(\"right\");do-mDownAmbiguous();",
            "Mouse down - Right.");

        macrosGroup.newItem(
            "mDownMiddle",
            "delayedDo(\"do-mDownMiddle\", \"150\");",
            "Mouse down - Middle, with delay. To be called by the event.");

        macrosGroup.newItem(
            "do-mDownMiddle",
            "setButton(\"middle\");do-mDownAmbiguous();",
            "Mouse down - Middle.");

        macrosGroup.newItem(
            "mUpLeft",
            "mUpAmbiguous();",
            "Mouse up - Left.");

        macrosGroup.newItem(
            "mUpRight",
            "mUpAmbiguous();",
            "Mouse up - Right.");

        macrosGroup.newItem(
            "mUpMiddle",
            "mUpAmbiguous();",
            "Mouse up - Middle.");

        macrosGroup.newItem(
            "allowWheelClicks",
            "setSlot(\"0\", \"closedSlot0-action-enter\");setSlot(\"1\", \"closedSlot1-action-enter\");setSlot(\"10\", \"closedSlot0-action-exit\");setSlot(\"11\", \"closedSlot1-action-exit\");",
            "Define the actions to be performed when clicking with a closed hand.");

        macrosGroup.newItem(
            "disallowWheelClicks",
            "setSlot(\"0\", \"\");setSlot(\"1\", \"\");setSlot(\"10\", \"\");setSlot(\"11\", \"\");",
            "Mouse up - Middle.");

        macrosGroup.newItem(
            "noHands",
            "cancelAllDelayedDos();setButton(\"left\");releaseButtons();releaseKeys();releaseZone();unlockTaps(\"primary\");",
            "To be triggered when the primary hand is no longer present.");

        macrosGroup.newItem(
            "simple-ambiguousClick",
            "delayedDo(\"do-simple-ambiguousClick\", \"150\");",
            "Perform an ambiguous click. Intended to be called by a tap. This includes a delay that can be cancelled.");

        macrosGroup.newItem(
            "do-simple-ambiguousClick",
            "prepForDelayedClick();click();unlockCursor();",
            "Do a simple click without specifying the button. It's intended for this to have been done before getting to this point. Either by abstracting it out, or by the gestureLayout setting it.");

        macrosGroup.newItem(
            "simple-leftClick",
            "delayedDo(\"do-simple-leftClick\", \"150\");",
            "Perform a left click. Intended to be called by a tap. This includes a delay that can be cancelled.");

        macrosGroup.newItem(
            "do-simple-leftClick",
            "setButton(\"left\");do-simple-ambiguousClick();",
            "Do the actual work of the left click from a tap. Intended to be called by simple-leftClick();");

        macrosGroup.newItem(
            "simple-rightClick",
            "delayedDo(\"do-simple-rightClick\", \"150\");",
            "Perform a right click. Intended to be called by a tap. This includes a delay that can be cancelled.");

        macrosGroup.newItem(
            "do-simple-rightClick",
            "setButton(\"right\");do-simple-ambiguousClick();",
            "Do the actual work of the right click from a tap. Intended to be called by simple-rightClick();");

        macrosGroup.newItem(
            "simple-middleClick",
            "delayedDo(\"do-simple-middleClick\", \"150\");",
            "Perform a middle click. Intended to be called by a tap. This includes a delay that can be cancelled.");

        macrosGroup.newItem(
            "do-simple-middleClick",
            "setButton(\"middle\");do-simple-ambiguousClick();",
            "Do the actual work of the middle click from a tap. Intended to be called by simple-middleClick();");

        macrosGroup.newItem(
            "simple-trippleLeftClick",
            "delayedDo(\"do-simple-trippleLeftClick\", \"150\");",
            "Perform a complete tripple-click.");

        macrosGroup.newItem(
            "do-simple-trippleLeftClick",
            "cancelAllDelayedDos();setButton(\"left\");lockCursor();rewindCursorPosition(\"150\");click();click();click();unlockCursor();",
            "Do the work of a simple tripple click.");

        macrosGroup.newItem(
            "stabliseSegment",
            "lockCursor();rewindCursorPosition();",
            "Reduce noise caused by the hand rotating.");

        macrosGroup.newItem(
            "yankScroll-enter",
            "cancelAllDelayedDos();lockCursor();allowWheelClicks();setSlot(\"3\", \"do-scroll\");lockTaps(\"primary\");unlockTaps(\"primary\" , \"800\");delayedDo(\"do-earlyScroll\", \"900\");delayedDo(\"do-scroll\", \"1500\");",
            "Yank scrolling is the grab to scroll, where you need to yank it to get it started. The -enter macro gets it set up.");

        macrosGroup.newItem(
            "yankScroll-exit",
            "cancelAllDelayedDos();unlockCursor();allowWheelClicks();setSlot(\"3\", \"\");rewindCursorPosition();releaseZone();unlockCursor();unlockTaps(\"primary\", \"800\");",
            "Yank scrolling is the grab to scroll, where you need to yank it to get it started. The -exit macro puts it away.");

        macrosGroup.newItem(
            "do-mDoubleClick-left",
            "lockCursor();rewindCursorPosition();releaseButtons();setButton(\"left\");doubleClick();",
            "Perform a double left click right now.");

        macrosGroup.newItem(
            "do-mDoubleClickHold-left",
            "lockCursor();rewindCursorPosition();releaseButtons();setButton(\"left\");click();mouseDown();",
            "Perform a double left click hold right now.");

        macrosGroup.newItem(
            "do-mTrippleClick-left",
            "lockCursor();rewindCursorPosition();releaseButtons();setButton(\"left\");doubleClick();click();",
            "Perform a double left click right now.");

        macrosGroup.newItem(
            "do-mTrippleClickHold-left",
            "lockCursor();rewindCursorPosition();releaseButtons();setButton(\"left\");doubleClick();mouseDown();",
            "Perform a tripple left click hold right now.");

        macrosGroup.newItem(
            "do-earlyScroll",
            "rewindCursorPosition();overrideZone(\"scroll\");",
            "");

        macrosGroup.newItem(
            "undo-earlyScroll",
            "releaseZone();",
            "");

        macrosGroup.newItem(
            "do-scroll",
            "cancelAllDelayedDos();rewindCursorPosition();overrideZone(\"scroll\");setSlot(\"3\", \"\");delayedDo(\"disallowWheelClicks\", \"150\");lockTaps(\"primary\");",
            "");

        macrosGroup.newItem(
            "movingProtection-enable",
            "doSlot(\"3\", \"\");simpleMovingProtection-enable();",
            "Enable protections against accidental gestures from erratic data while the hand is moving quickly.");

        macrosGroup.newItem(
            "movingProtection-disable",
            "simpleMovingProtection-disable();",
            "Disable protections against accidental gestures from erratic data while the hand is moving quickly.");

        macrosGroup.newItem(
            "movingProtectionSecondary-enable",
            "lockGestures(\"secondary\");lockTaps(\"secondary\");cancelAllDelayedDos();",
            "Enable protections against accidental gestures from erratic data while the hand is moving quickly.");

        macrosGroup.newItem(
            "movingProtectionSecondary-disable",
            "unlockGestures(\"secondary\");unlockTaps(\"secondary\", \"150\");",
            "Disable protections against accidental gestures from erratic data while the hand is moving quickly.");

        macrosGroup.newItem(
            "simpleMovingProtection-enable",
            "lockGestures(\"primary\");lockTaps(\"primary\");cancelAllDelayedDos();",
            "Enable moving protection. For most gestureLayouts, you'll want movingProtection-enable.");

        macrosGroup.newItem(
            "simpleMovingProtection-disable",
            "unlockGestures(\"primary\");unlockTaps(\"primary\", \"150\");",
            "Disable moving protection. For most gestureLayouts, you'll want movingProtection-disable.");

        macrosGroup.newItem(
            "prep-sharedScroll-slot",
            "undo-earlyScroll();cancelAllDelayedDos();setSlot(\"3\", \"\");rewindCursorPosition();lockTaps(\"primary\");",
            "Preparations to be done before running an overrideable slot for sharedScroll functionality.");

        macrosGroup.newItem(
            "finish-sharedScroll-slot-withoutCancel",
            "rewindCursorPosition();rewindScroll();unlockTaps(\"primary\", \"800\");",
            "What has to be done after running an overrideable slot for sharedScroll functionality.");

        macrosGroup.newItem(
            "finish-sharedScroll-slot",
            "cancelAllDelayedDos();finish-sharedScroll-slot-withoutCancel();",
            "What has to be done after running an overrideable slot for sharedScroll functionality.");

        macrosGroup.newItem(
            "closedSlot0-enter",
            "doSlot(\"0\", \"\");",
            "What happens when a closedSlot0 gesture is performed. Intended to be called by the event. Please override the closedSlot0-overrideable-enter instead if possible.");

        macrosGroup.newItem(
            "closedSlot0-exit",
            "doSlot(\"10\", \"\");",
            "What happens when a closedSlot0 gesture is finished. Intended to be called by the event. Please override the closedSlot0-overrideable-exit instead if possible.");

        macrosGroup.newItem(
            "closedSlot1-enter",
            "doSlot(\"1\", \"\");",
            "What happens when a closedSlot1 gesture is performed. Intended to be called by the event. Please override the closedSlot1-overrideable-enter instead if possible.");

        macrosGroup.newItem(
            "closedSlot1-exit",
            "doSlot(\"11\", \"\");",
            "What happens when a closedSlot1 gesture is finished. Intended to be called by the event. Please override the closedSlot1-overrideable-enter instead if possible.");

        macrosGroup.newItem(
            "closedSlot0-action-enter",
            "delayedDo(\"do-closedSlot0-action-enter\", \"150\");",
            "When the closed gestures get enabled. This is one of the macros that gets allocated.");

        macrosGroup.newItem(
            "do-closedSlot0-action-enter",
            "prep-sharedScroll-slot();closedSlot0-overrideable-enter();",
            "When the closed gestures get enabled. This gets triggered via a delay for stability.");

        macrosGroup.newItem(
            "closedSlot0-action-exit",
            "finish-sharedScroll-slot-withoutCancel();closedSlot0-overrideable-exit();",
            "When the closed gestures get enabled. This is one of the macros that gets allocated.");

        macrosGroup.newItem(
            "closedSlot1-action-enter",
            "delayedDo(\"do-closedSlot1-action-enter\", \"150\");",
            "When the closed gestures get enabled. This is one of the macros that gets allocated.");

        macrosGroup.newItem(
            "do-closedSlot1-action-enter",
            "prep-sharedScroll-slot();closedSlot1-overrideable-enter();",
            "When the closed gestures get enabled. This gets triggered via a delay for stability.");

        macrosGroup.newItem(
            "closedSlot1-action-exit",
            "finish-sharedScroll-slot-withoutCancel();closedSlot1-overrideable-exit();",
            "When the closed gestures get enabled. This is one of the macros that gets allocated.");

        macrosGroup.newItem(
            "closedSlot0-overrideable-enter",
            "setButton(\"middle\");click();",
            "Overrideable action to be performed when the closedSlot0 gesture is performed.");

        macrosGroup.newItem(
            "closedSlot0-overrideable-exit",
            "",
            "Overrideable action to be performed when the closedSlot0 gesture is finished.");

        macrosGroup.newItem(
            "closedSlot1-overrideable-enter",
            "setButton(\"left\");doubleClick();",
            "Overrideable action to be performed when the closedSlot1 gesture is performed.");

        macrosGroup.newItem(
            "closedSlot1-overrideable-exit",
            "",
            "Overrideable action to be performed when the closedSlot1 gesture is finished.");
    }

    private void generateCustomConfig(Group customGroup) {
        /*
        This is for custom events that are typically triggered by slots.

        When updating the defaults, be sure to run ./generateCustomTable.sh in docs/user/howTo to update https://github.com/ksandom/handWavey/blob/main/docs/user/howTo/createADynamicGestureLayout.md#custom-events .
        Also make sure that you get the expected results.
        */


        // Create the template.
        customGroup.addItemTemplate("custom-.*", "", "A custom event that a user can trigger in a gestureLayout. It is intended to be used with slots, and can be read about in createADynamicGestureLayout.md.");

        // Set the default values.
        customGroup.getItem("custom-noOp").overrideDefault("debug(\"0\", \"No action is currently assigned to this slot.\");"); // Do nothing. Useful to have a blank slot that can sometimes be used for other things.
        customGroup.getItem("custom-releaseAll").overrideDefault("rewindCursorPosition();rewindScroll();releaseButtons();releaseKeys();unlockCursor();"); // Release all buttons and keys. Useful for getting the keyboard and mouse into a known state.

        customGroup.getItem("custom-mouseDown-left").overrideDefault("setButton(\"left\");lockCursor();rewindCursorPosition();mouseDown();"); // Press down the left mouse button.
        customGroup.getItem("custom-mouseDown-right").overrideDefault("setButton(\"right\");lockCursor();rewindCursorPosition();mouseDown();"); // Press down the right mouse button.
        customGroup.getItem("custom-mouseDown-middle").overrideDefault("setButton(\"middle\");lockCursor();rewindCursorPosition();mouseDown();"); // Press down the middle mouse button.

        // Tap-specific clicks.
        customGroup.getItem("custom-tap-left").overrideDefault("setButton(\"left\");lockCursor();rewindCursorPosition();mouseDown();mouseUp();unlockCursor();"); // Tap: Click left mouse button.
        customGroup.getItem("custom-tap-right").overrideDefault("setButton(\"right\");lockCursor();rewindCursorPosition();mouseDown();mouseUp();unlockCursor();"); // Tap: Click right mouse button.
        customGroup.getItem("custom-tap-middle").overrideDefault("setButton(\"middle\");lockCursor();rewindCursorPosition();mouseDown();mouseUp();unlockCursor();"); // Tap: Click middle mouse button.

        customGroup.getItem("custom-click-left").overrideDefault("setButton(\"left\");lockCursor();rewindCursorPosition();mouseDown();mouseUp();unlockCursor();"); // Press down the left mouse button.
        customGroup.getItem("custom-click-right").overrideDefault("setButton(\"right\");lockCursor();rewindCursorPosition();mouseDown();mouseUp();unlockCursor();"); // Press down the right mouse button.
        customGroup.getItem("custom-click-middle").overrideDefault("setButton(\"middle\");lockCursor();rewindCursorPosition();mouseDown();mouseUp();unlockCursor();"); // Press down the middle mouse button.

        customGroup.getItem("custom-releaseZone").overrideDefault("rewindCursorPosition();releaseZone();unlockCursor();"); // Release and zone overrides. This is typically used at the end of overriding the zone for something like scrolling.
        customGroup.getItem("custom-override-scroll").overrideDefault("rewindCursorPosition();overrideZone(\"scroll\");releaseKeys();"); // Override the zone to scroll. This has the effect that any movement of the hand causes scroll movement instead of mouse cursor movement.
        customGroup.getItem("custom-override-ctrl+scroll").overrideDefault("rewindCursorPosition();keyDown(\"ctrl\");overrideZone(\"scroll\");"); // Press the CTRL key down, and override the zone to scroll. Often this is used for zooming.

        customGroup.getItem("custom-doubleClick-hold").overrideDefault("lockCursor();rewindCursorPosition();releaseButtons();setButton(\"left\");click();mouseDown();"); // Double click the left button, without lifting the finger at the end of the second click. This is useful for doing things like drag-selecting by word rather than by character.
        customGroup.getItem("custom-trippleClick-hold").overrideDefault("lockCursor();rewindCursorPosition();releaseButtons();setButton(\"left\");doubleClick();mouseDown();"); // Tripple click the left button, without lifting the finger at the end of the second click. This is useful for doing things like drag-selecting by line rather than by character.
        customGroup.getItem("custom-doubleClick").overrideDefault("lockCursor();rewindCursorPosition();releaseButtons();setButton(\"left\");doubleClick();"); // Double click the left button.
        customGroup.getItem("custom-trippleClick").overrideDefault("lockCursor();rewindCursorPosition();releaseButtons();setButton(\"left\");doubleClick();click();"); // Tripple click the left button.
        customGroup.getItem("custom-alt+mouseDown-left").overrideDefault("keyDown(\"alt\");setButton(\"left\");lockCursor();rewindCursorPosition();mouseDown();keyUp(\"alt\");"); // Press the ALT key, then hold down the left button.
        customGroup.getItem("custom-alt+mouseDown-right").overrideDefault("keyDown(\"alt\");setButton(\"right\");lockCursor();rewindCursorPosition();mouseDown();keyUp(\"alt\");"); // Press the ALT key, then hold down the right button.
        customGroup.getItem("custom-alt+mouseDown-middle").overrideDefault("keyDown(\"alt\");setButton(\"middle\");lockCursor();rewindCursorPosition();mouseDown();keyUp(\"alt\");"); // Press the ALT key, then hold down the middle button.
        customGroup.getItem("custom-ctrl+mouseDown-left").overrideDefault("keyDown(\"ctrl\");setButton(\"left\");lockCursor();rewindCursorPosition();mouseDown();keyUp(\"ctrl\");"); // Press the CTRL key, then hold down the left button.
        customGroup.getItem("custom-ctrl+mouseDown-right").overrideDefault("keyDown(\"ctrl\");setButton(\"right\");lockCursor();rewindCursorPosition();mouseDown();keyUp(\"ctrl\");"); // Press the CTRL key, then hold down the right button.
        customGroup.getItem("custom-ctrl+mouseDown-middle").overrideDefault("keyDown(\"ctrl\");setButton(\"middle\");lockCursor();rewindCursorPosition();mouseDown();keyUp(\"ctrl\");"); // Press the CTRL key, then hold down the middle button.
        customGroup.getItem("custom-ctrl+c").overrideDefault("keyDown(\"ctrl\");keyDown(\"c\");keyUp(\"c\");keyUp(\"ctrl\");"); // CTRL + c. Typically used for copying a selection.
        customGroup.getItem("custom-ctrl+v").overrideDefault("keyDown(\"ctrl\");keyDown(\"v\");keyUp(\"v\");keyUp(\"ctrl\");"); // CTRL + v. Typically used for pasting.
        customGroup.getItem("custom-ctrl+x").overrideDefault("keyDown(\"ctrl\");keyDown(\"x\");keyUp(\"x\");keyUp(\"ctrl\");"); // CTRL + x. Typically used for cutting a selection.
        customGroup.getItem("custom-delete").overrideDefault("keyDown(\"delete\");keyUp(\"delete\");"); // Press and release the delete key.
        customGroup.getItem("custom-ctrl+z").overrideDefault("keyDown(\"ctrl\");keyDown(\"z\");keyUp(\"z\");keyUp(\"ctrl\");"); // CTRL + z. Typically used for undo.
        customGroup.getItem("custom-ctrl+shift+z").overrideDefault("keyDown(\"ctrl\");keyDown(\"shift\");keyDown(\"z\");keyUp(\"z\");keyUp(\"shift\");keyUp(\"ctrl\");"); // CTRL + Shift z. Typically used for re-doing an undone task.

        customGroup.getItem("custom-recalibrate").overrideDefault("setSlot(\"250\", \"custom-noop\");recalibrateSegments();"); // Recalibrate the segments once after entry.
    }
}
