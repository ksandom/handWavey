// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
For identifying what events should be generated for a given set of characteristics.

Also generates the config for those events.
*/

package handWavey;

import config.*;
import java.util.Locale;
import java.util.HashMap;
import java.sql.Timestamp;
import com.google.common.base.Joiner;

public class Gesture {

    private String[] zones = {"OOB", "nonOOB", "none", "noMove", "active", "action", "absolute", "relative"};

    // States.
    private int stateCount = 3;
    private String[] states = new String[this.stateCount];
    public static final int open = 0;
    public static final int closed = 1;
    public static final int absent = 2;
    public static final int entering = 4;
    public static final int exiting = 5;

    // Segments.
    private int maximumSegments = 20;

    // Config.
    Group actionEvents;
    Group audioEvents;

    public Gesture() {
        this.states[this.open] = "open";
        this.states[this.closed] = "closed";
        this.states[this.absent] = "absent";

        this.actionEvents = Config.singleton().getGroup("actionEvents");
        this.audioEvents = Config.singleton().getGroup("audioEvents");
    }

    public void generateConfig() {
        // Generate all combinations of events available for configuration.
        generateChangeEventsConfig();
        generateGeneralEventsConfig();
        generateCombinedEventsConfig();

        setDefaults();
    }

    private void setDefaults() {
        // Double click when entering the action zone.
        overrideDefault(
            "general-zone-pAction-enter",
            "lockCursor();rewindCursorPosition();doubleClick();",
            "metalDing07.wav");

        // Normal click behavior.
        overrideDefault(
            "general-state-pClosed-enter",
            "lockCursor();rewindCursorPosition();mouseDown();",
            "metalDing07.wav");
        overrideDefault(
            "general-state-pClosed-exit",
            "rewindCursorPosition();rewindScroll();releaseButtons();unlockCursor();",
            "metalDing08.wav");

        // Set buttons.
        overrideDefault(
            "general-segment-p0-enter",
            "setButton(\"left\");",
            "");
        overrideDefault(
            "general-segment-p1-enter",
            "setButton(\"right\");",
            "");
        overrideDefault(
            "general-segment-p2-enter",
            "setButton(\"middle\");overrideZone(\"scroll\");",
            "");
        overrideDefault(
            "general-segment-p2-exit",
            "releaseZone();",
            "");

        // Set keys.
        overrideDefault(
            "general-segment-s0-enter",
            "keyDown(\"ctrl\");",
            "");
        overrideDefault(
            "general-segment-s0-exit",
            "keyUp(\"ctrl\");",
            "");
        overrideDefault(
            "general-segment-s1-enter",
            "keyDown(\"alt\");",
            "");
        overrideDefault(
            "general-segment-s1-exit",
            "keyUp(\"alt\");",
            "");
        overrideDefault(
            "general-segment-s2-enter",
            "keyDown(\"shift\");",
            "");
        overrideDefault(
            "general-segment-s2-exit",
            "keyUp(\"shift\");",
            "");
        overrideDefault(
            "general-zone-sActive-exit",
            "releaseKeys();",
            "");

        // Stabilise gesture changes.
        overrideDefault(
            "general-segment-pAnyChange",
            "lockCursor();rewindCursorPosition();",
            "");
        overrideDefault(
            "special-newHandUnfreezeEvent",
            "",
            "");
        overrideDefault(
            "general-state-pAbsent-enter",
            "setButton(\"left\");releaseButtons();releaseKeys();releaseZone();",
            "");

        // General auido feedback.
        this.audioEvents.getItem("general-zone-pAnyChange").overrideDefault("metalDing01.wav");
        this.audioEvents.getItem("general-zone-sAnyChange").overrideDefault("metalDing03.wav");
        this.audioEvents.getItem("general-segment-pAnyChange").overrideDefault("metalDing02.wav");
        this.audioEvents.getItem("general-segment-sAnyChange").overrideDefault("metalDing04.wav");
    }

    private void overrideDefault(String eventName, String actionLine, String audioNotification) {
        this.actionEvents.getItem(eventName).overrideDefault(actionLine);
        this.audioEvents.getItem(eventName).overrideDefault(audioNotification);
    }

    private void generateChangeEventsConfig() {
        String[] handLetters = new String[] {"p", "s"};
        String[] eventTypes = {"zone", "segment", "state"};

        for (String handLetter : handLetters) {
            String hand = hand(handLetter);

            for (String eventType : eventTypes) {
                String name = "general-" + eventType + "-" + handLetter + "AnyChange";
                String description = "Triggered when the " + hand + " hand changes " + eventType + ".";

                this.actionEvents.newItem(name, "", description, true);
                this.audioEvents.newItem(name, "", description, true);
            }
        }
    }

    private void generateGeneralEventsConfig() {
        // General events per hand for simple configurations.
        String[] handLetters = new String[] {"p", "s"};

        for (String handLetter : handLetters) {
            // Zones.
            for (String zone : this.zones) {
                String name = "general-zone-" + handLetter + capitalise(zone);
                String when = "in the " + zone + " zone.";
                assembleGeneralEvent(name, handLetter, when);
            }

            // Segments.
            for (int segment=0; segment< this.maximumSegments; segment ++) {
                String segmentString = String.valueOf(segment);
                String name = "general-segment-" + handLetter + segmentString;
                String when = "in the " + segmentString+ " segment.";
                assembleGeneralEvent(name, handLetter, when);
            }

            // States.
            for (int state=0; state< this.states.length; state ++) {
                String stateString = handState(state);
                String name = "general-state-" + handLetter + capitalise(stateString);
                String when = "in the " + stateString+ " state.";
                assembleGeneralEvent(name, handLetter, when);
            }
        }
    }

    private void generateCombinedEventsConfig() {
        // Combined events using state, zone, and segment.

        // Examples
        // individual-pActive0Open-enter
        // combined-pNoMove1Closed-sOOB0Absent-exit

        String[] upperZones = this.zones;

        for (int i=0; i<upperZones.length; i++) {
            upperZones[i] = capitalise(upperZones[i]);
        }

        Joiner joiner = Joiner.on("|").skipNulls();
        String zonesRegex = "(" + joiner.join(upperZones) + ")";


        String direction = "-(enter|exit)";
        String basicHand = zonesRegex + "[0-9]*(Open|Closed|Absent)";

        // individual-sAction11Closed-enter
        String individual = "individual-(p|s)" + basicHand + direction;
        String combined = "combined-p" + basicHand + "-s" + basicHand + direction;

        this.actionEvents.addItemTemplate(individual, "", "Action to take when this event is triggered. This event matches what one hand is doing regardless of what the other hand is doing. See https://github.com/ksandom/handWavey/blob/main/docs/user/configuration/howEventNamingWorks.md");
        this.audioEvents.addItemTemplate(individual, "", "Audio to play when this event is triggered. This event matches what one hand is doing regardless of what the other hand is doing. See https://github.com/ksandom/handWavey/blob/main/docs/user/configuration/howEventNamingWorks.md");

        this.actionEvents.addItemTemplate(combined, "", "Action to take when this event is triggered. This hand matches what both hands are doing together. See https://github.com/ksandom/handWavey/blob/main/docs/user/configuration/howEventNamingWorks.md");
        this.audioEvents.addItemTemplate(combined, "", "Audio to play when this event is triggered. This hand matches what both hands are doing together. See https://github.com/ksandom/handWavey/blob/main/docs/user/configuration/howEventNamingWorks.md");
    }

    private void assembleGeneralEvent(String name, String handLetter, String when) {
        String triggerDescription = "General event: When the " + hand(handLetter) + " hand is " + when;
        addActionConfig(name, triggerDescription, Gesture.entering);
        addActionConfig(name, triggerDescription, Gesture.exiting);
        addAudioFeedbackConfig(name, triggerDescription, Gesture.entering);
        addAudioFeedbackConfig(name, triggerDescription, Gesture.exiting);
    }

    private void addConfigItems(String name, String triggerDescription) {
        addActionConfig(name, triggerDescription, Gesture.entering);
        addActionConfig(name, triggerDescription, Gesture.exiting);
        addAudioFeedbackConfig(name, triggerDescription, Gesture.entering);
        addAudioFeedbackConfig(name, triggerDescription, Gesture.exiting);
    }

    public String handState(int handState) {
        String result = "";

        if (handState < 0) {
            return "OOB<0";
        } else if (handState >= this.states.length) {
            return "OOB>n(" + String.valueOf(this.states.length) + ")";
        }

        result = this.states[handState];

        return result;
    }

    public String capitalise(String value) {
        // TODO Gracefully handle 0 length strings.
        return value.substring(0, 1).toUpperCase(Locale.getDefault()) + value.substring(1);
    }

    private String hand(String handLetter) {
        return (handLetter.equals("p"))?"primary":"secondary";
    }

    private String generateGestureName(String primaryZone, int primarySegment, int primaryHandState, String secondaryZone, int secondarySegment, int secondaryHandState) {
        String result = "";

        String primaryHand = generateSingleHandGestureName("p", primaryZone, primarySegment, primaryHandState);
        String secondaryHand = "";

        secondaryHand = generateSingleHandGestureName("s", secondaryZone, secondarySegment, secondaryHandState);
        result = primaryHand + "-" + secondaryHand;

        return result;
    }

    public String generateSingleHandGestureName(String letter, String zone, int segmentIn, int handStateIn) {
        int segment = segmentIn;
        int handState = handStateIn;

        if (zone.equals("OOB")) {
            handState = this.absent;
            segment = 0;
        }

        // eg pActive0Closed
        return letter + capitalise(zone) + String.valueOf(segment) + capitalise(handState(handState));
    }

    private String generateGestureDescription(String primaryZone, int primarySegment, int primaryHandState, String secondaryZone, int secondarySegment, int secondaryHandState) {
        String result = "";
        String primary = generateSingleHandGestureDescription("p", primaryZone, primarySegment, primaryHandState);

        if (secondaryHandState != this.absent) {
            String secondary = generateSingleHandGestureDescription("s", secondaryZone, secondarySegment, secondaryHandState);
            result = capitalise(primary) + " And " + secondary;
        } else {
            result = capitalise(primary) + " And the secondary hand is absent.";
        }

        return result;
    }

    private String generateSingleHandGestureDescription(String letter, String zone, int segmentValue, int handState) {
        String segment = String.valueOf(segmentValue);
        String whichHand = hand(letter);
        String state = handState(handState);

        // Eg: the primary hand is in the active zone, is in segment 0, and is in open state.
        return "the " + whichHand + " hand is in the " + zone + " zone, is in segment " + segment + ", and is in the " + state + " state.";
    }

    private void addActionConfig(String name, String whenDescription, int direction) {
        String directionName = (direction == Gesture.entering)?"enter":"exit";
        String directionString = (direction == Gesture.entering)?"entering":"exiting";
        String fullName = name + "-" + directionName;
        String description = "Action to take when " + directionString + " this state: " + whenDescription;

        this.actionEvents.newItem(fullName, "", description, true);
    }

    private void addAudioFeedbackConfig(String name, String whenDescription, int direction) {
        String directionName = (direction == Gesture.entering)?"enter":"exit";
        String directionString = (direction == Gesture.entering)?"entering":"exiting";
        String fullName = name + "-" + directionName;
        String description = "Sound to play when " + directionString + " this state: " + whenDescription;

        this.audioEvents.newItem(fullName, "", description, true);
    }


    // Externally facing naming.
    public String gestureName(String primaryZone, int primarySegment, int primaryHandState, String secondaryZone, int secondarySegment, int secondaryHandState) {
        return "combined-" + generateGestureName(primaryZone, primarySegment, primaryHandState, secondaryZone, secondarySegment, secondaryHandState);
    }

    public String gestureName(String letter, String zone, int segment, int handState) {
        return "individual-" + generateSingleHandGestureName(letter, zone, segment, handState);
    }

    // Externally facing descriptions.
    public String gestureDescription(String primaryZone, int primarySegment, int primaryHandState, String secondaryZone, int secondarySegment, int secondaryHandState) {
        return capitalise(generateGestureDescription(primaryZone, primarySegment, primaryHandState, secondaryZone, secondarySegment, secondaryHandState));
    }

    public String gestureDescription(String letter, String zone, int segment, int handState) {
        return capitalise(generateSingleHandGestureDescription(letter, zone, segment, handState));
    }
}
