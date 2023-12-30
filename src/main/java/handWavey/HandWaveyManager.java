// (c) 2022-2023 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Glues everything together.

I know this class is huge. I'm slowly pulling more and more of its functionality out into separate, testable, classes.

To quickly understand how it all fits together, take a look at sendHandSummaries() near the bottom of this file. To summarise:

* Input (currently UltraMotionInput) sends some HandSummary s to this class via the sendHandSummaries() function.
* this.handsState.figureOutStuff(); then figures out:
    * Which of each of these the hands are in and tracks them via HandStateEvents.
        * zone (none, noMove, active, action, absolute, relative)
        * segment (0-3 normally, but could be more, or less.)
        * state (open, closed, absent, out of bounds)
    * Triggers events for any changes.
* There is then some logic to determine how movement should be treated.

If you want to add configuration options, start in HandWaveyConfig.

*/

package handWavey;

import handWavey.Motion;
import handWavey.Zone;
import handWavey.HandWaveyConfig;
import config.*;
import debug.Debug;
// import mouseAndKeyboardOutput.*;
import audio.*;
import bug.ShouldComplete;

import java.util.HashMap;
import java.io.File;

/* HandWaveyManager is the glue that brings everything together. */
public final class HandWaveyManager {
    private Debug debug;
    private Config config;
    private Motion motion;
    private HandSummary[] handSummaries;
    private HashMap<String, Zone> zones = new HashMap<String, Zone>();
    private HandWaveyEvent handWaveyEvent;
    private HandsState handsState;
    private ShouldComplete shouldCompleteSFO;

    private String zoneMode = "touchScreen";

    private double zNoMoveBegin = 0;
    private double zActiveBegin = 0;
    private double zAbsoluteBegin = 0;
    private double zRelativeBegin = 0;
    private double zActionBegin = 0;

    private double zMultiplier = -1;


    public HandWaveyManager() {
        initialSetup(false);
        displayWarning();
    }

    public HandWaveyManager(Boolean loadConfig) {
        initialSetup(loadConfig);
        displayWarning();
    }

    private void initialSetup(Boolean loadConfig) {
        HandWaveyConfig handWaveyConfig = new HandWaveyConfig("handWavey");
        handWaveyConfig.defineGeneralConfig();
        new Gesture().generateConfig();



        this.config = Config.singleton();
        if (loadConfig) loadAndSaveConfigToDisk();

        Boolean useAudio = Boolean.parseBoolean(this.config.getGroup("audioConfig").getItem("useAudio").get());


        this.handsState = HandsState.singleton();
        this.motion = Motion.singleton();
        this.handWaveyEvent = new HandWaveyEvent(this.motion.getOutput(), useAudio, this.handsState, this);
        this.handsState.setHandWaveyEvent(this.handWaveyEvent);

        this.shouldCompleteSFO = new ShouldComplete("figureStuffOut");

        reloadConfig();
    }

    private void loadAndSaveConfigToDisk() {
        this.config.load(); // Load any changes that the user has made.

        String saveBackConfig = Config.singleton().getItem("saveBackConfig").get();
        if (!saveBackConfig.equals("false")) {
            this.config.save(); // Save all config to disk so that any new settings are available to the user.
        } else {
            Debug debug = new Debug(0, "HandWaveyManager-startup");
            debug.out(0, "   !!!! saveBackConfig is disabled.                                 !!!!");
            debug.out(0, "   !!!! So the config files will not be updated/repaired as needed. !!!!");
            debug.out(0, "   !!!! Make sure to set it back to true as soon as possible.       !!!!");
        }
    }

    public void reloadConfig() {
        // This function reloads, and calcuates config based on the settings currently in Config. It does not trigger a reload of the config from file.

        // Set up the debugging.
        this.debug = Debug.getDebug("HandWaveyManager");

        // Configure Z axis thresholds.
        this.zoneMode = Config.singleton().getItem("zoneMode").get();

        if (this.zoneMode.equals("touchScreen")) {
            Group touchScreen = this.config.getGroup("zones").getGroup("touchScreen");
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
        } else if (this.zoneMode.equals("touchPad")) {
            Group touchPad = this.config.getGroup("zones").getGroup("touchPad");
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
            this.debug.out(0, "Unknown zoneMode \"" + this.zoneMode + "\". This will likely cause badness.");
        }

        this.zMultiplier = motion.getZMultiplier();

        this.debug.out(1, "Moving mean configured for zones:");
        for (String key: this.zones.keySet()) {
            this.debug.out(1, "  " + key + ":  " + this.zones.get(key).toString());
        }





        // Config checks.
        checkZones();
    }

    public void displayWarning() {
        System.out.println("");
        System.out.println("Thank you for using handWavey.");

        System.out.println("");
        System.out.println("For documentation, go here: https://github.com/ksandom/handWavey/tree/main/docs");
        System.out.println("");
        System.out.println("I believe this to be safe to use long term. But I have no medical or ergonomic background to back that up.");
        System.out.println("Please use your intelligence and judgement when using this.");

        System.out.println("");
        System.out.println("If you notice any changes in discomfort, pain, stiffness, or you have any doubts whatsoever; please stop using it immediately, and seek medical advice. Only resume using it after you have medical advice saying that it's ok for you to resume.");
        System.out.println("");

        System.out.println("Nothing in this application, repo, or any related material should be understood to be medical advice.");

        System.out.println("");
        System.out.println("By using this software, you are taking responsibility for staying healthy.");
        System.out.println("");
    }

    private void checkZones() {
        // Compare every zone to every other zone to make sure that there are no unusable zones.

        double zoneBuffer = Double.parseDouble(this.config.getItem("zoneBuffer").get());

        for (String outerKey: this.zones.keySet()) {
            Zone outerZone = this.zones.get(outerKey);
            for (String innerKey: this.zones.keySet()) {
                if (!outerKey.equals(innerKey)) {
                    Zone innerZone = this.zones.get(innerKey);

                    double diff = Math.abs(innerZone.getBegin() - outerZone.getBegin());

                    if (diff <= zoneBuffer) {
                        this.debug.out(0, "Warning: There does not appear to be enough usable space between zones " + outerKey + " and " + innerKey + ". Either reduce the zoneBuffer, or increase the distance between these two zones.");
                    }
                }
            }
        }
    }

    // Make available to other classes.
    public void rewindScroll() {
        motion.rewindScroll();
    }

    public void rewindCursorPosition() {
        motion.rewindCursorPosition();
    }

    public void setCursorLock() {
        motion.setCursorLock();
    }

    public void releaseCursorLock() {
        motion.releaseCursorLock();
    }

    public void discardOldPosition() {
        motion.discardOldPosition();
    }

    public void recalibrateSegments() {
        handsState.recalibrateSegments();
    }

    public void lockGestures(String hand) {
        handsState.lockGestures(hand);
    }

    public void unlockGestures(String hand) {
        handsState.unlockGestures(hand);
    }

    public void lockTaps(String hand, long time) {
        handsState.lockTaps(hand, time);
    }

    public void unlockTaps(String hand, long time) {
        handsState.unlockTaps(hand, time);
    }


    public void triggerEvent(String eventID) {
        this.handWaveyEvent.triggerEvent(eventID);
    }

    /* TODO

    * Data cleaning:
        * How many frames for a mouse event to be acted on?
        * Adjust timings?
    * Make audio feedback for hands left/right hand aware.
    * How to build the final asset?
    * Documentation/automation:
        * Configuring.
            * touchPad acceleration.
            * scroll acceleration.
            * touchPad vs touchScreen.
    * Synth audio feedback for when close to zone boundaries.
    * Occasional freeze:
        * Usually sound stops. But other stuff still works.
        * Check whether threads are lingering.
        * Check GC.
    */

    // This is where everything gets glued together.
    public void sendHandSummaries(HandSummary[] handSummaries) {
        if (!this.shouldCompleteSFO.start("na")) {
            this.handWaveyEvent.triggerAudioOnly("bug");
        }

        this.handsState.setHandSummaries(handSummaries);

        this.handSummaries = handSummaries;

        this.handsState.figureOutStuff();

        this.shouldCompleteSFO.finish();

        if (this.handSummaries[0] == null) return;

        Double handZ = this.handSummaries[0].getHandZ() * this.zMultiplier;
        Boolean allowOverride = (!this.handSummaries[0].handIsNew());
        String zone = this.handsState.getZone(handZ, allowOverride);

        // Move the mouse cursor.
        if (!motion.cusorIsLocked()) {
            if ((zone.equals("none")) || (zone.equals("noMove")) || this.handsState.newHandsCursorFreeze() == true) {
                if (this.zoneMode.equals("touchPad")) {
                    motion.updateMovingMeans(zone, handZ, this.handSummaries, this.zones);
                    motion.touchPadNone();
                }
            } else if (zone.equals("active")) {
                motion.updateMovingMeans(zone, handZ, this.handSummaries, this.zones);
                motion.moveMouseTouchPadFromCoordinates();
            } else if (zone.equals("absolute")) {
                motion.updateMovingMeans(zone, handZ, this.handSummaries, this.zones);
                motion.moveMouseAbsoluteFromCoordinates();
            } else if (zone.equals("relative")) {
                motion.updateMovingMeans(zone, handZ, this.handSummaries, this.zones);
                motion.moveMouseRelativeFromCoordinates();
            } else if (zone.equals("action")) {
                motion.updateMovingMeans(zone, handZ, this.handSummaries, this.zones);
                motion.touchPadNone();
            } else if (zone.equals("scroll")) { // TODO This isn't getting triggered even though it should match.
                motion.updateMovingMeans(zone, handZ, this.handSummaries, this.zones);
                motion.scrollFromCoordinates();
            } else {
                this.debug.out(3, "A hand was detected, but it outside of any zones. z=" + String.valueOf(handZ));
            }
        } else {
            // Stop the cursor from jumping around during the beginning of a mouse-down event.
            if (this.zoneMode.equals("touchPad")) {
                motion.updateMovingMeans(zone, handZ, this.handSummaries, this.zones);
                motion.touchPadNone();
            }
        }
    }
}
