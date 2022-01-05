package handWavey;

import config.*;
import debug.Debug;
import java.util.HashMap;
import java.sql.Timestamp;

/* TODO
* HandsState should track which gesture is active, and whether it has changed (therefore whether to trigger an action.)
    * Each of the general changes.
    * Combined
        * Full (both available hands).
        * Individual.
* HandWaveyManager should receive that information, and trigger an Event handler/interpreter to send the actions to the appropriate place.
* Migrate existing events to the new set-up.
* Migrate hard-coded stuff to the new set-up.
*/

public class Gesture {
    private Debug debug;
    private String[] zones = {"any", "notOOB", "OOB", "none", "noMove", "active", "action", "absolute", "relative"};
    
    // States.
    private int stateCount = 4;
    private String[] states = new String[this.stateCount];
    public static final int open = 0;
    public static final int closed = 1;
    public static final int any = 2;
    public static final int absent = 3;
    public static final int entering = 4;
    public static final int exiting = 5;
    
    // Segments.
    private int maximumSegments = 10;
    
    // Config.
    Group actionEvents;
    Group audioEvents;
    
    public Gesture() {
        this.states[this.open] = "open";
        this.states[this.closed] = "closed";
        this.states[this.any] = "any";
        this.states[this.absent] = "absent";
        
        this.actionEvents = Config.singleton().getGroup("actionEvents");
        this.audioEvents = Config.singleton().getGroup("audioEvents");
    }
    
    public void generateConfig() {
        // Generate all combinations of events available for configuration.
        generateChangeEventsConfig();
        generateGeneralEventsConfig();
        generateCombinedEventsConfig();
    }
    
    private void generateChangeEventsConfig() {
        String[] handLetters = new String[] {"p", "s"};
        String[] eventTypes = {"zone", "segment", "state"};
        
        for (String handLetter : handLetters) {
            String hand = hand(handLetter);
            
            for (String eventType : eventTypes) {
                String name = "general-" + eventType + "-" + handLetter + "AnyChange";
                String description = "Triggered when the " + hand + " hand changes " + eventType + ".";
                
                this.actionEvents.newItem(name, "", description);
                this.audioEvents.newItem(name, "", description);
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
        // Combined events.
        for (int primaryState = 0; primaryState < stateCount-1; primaryState++) {
            for ( String primaryZone :  zones ) {
                for (int primarySegment = -1; primarySegment < this.maximumSegments; primarySegment++) {
                
                    for (int secondaryState = 0; secondaryState < stateCount; secondaryState++) {
                        if (secondaryState == this.absent) {
                            assembleCombinedEvent(primaryZone, primarySegment, primaryState, "OOB", 0, this.absent);
                            assembleIndividualHandEvent("p", primaryZone, primarySegment, primaryState);
                            assembleIndividualHandEvent("s", primaryZone, primarySegment, primaryState);
                        } else {
                            for ( String secondaryZone :  zones ) {
                                for (int secondarySegment = -1; secondarySegment < this.maximumSegments; secondarySegment++) {
                                    assembleCombinedEvent(primaryZone, primarySegment, primaryState, secondaryZone, secondarySegment, secondaryState);
                                }
                            }
                        }
                    }
                    
                }
            }
        }
    }
    
    private void assembleGeneralEvent(String name, String handLetter, String when) {
        String triggerDescription = "General event: When the " + hand(handLetter) + " hand is " + when;
        addActionConfig(name, triggerDescription, Gesture.entering);
        addActionConfig(name, triggerDescription, Gesture.exiting);
        addAudioFeedbackConfig(name, triggerDescription, Gesture.entering);
        addAudioFeedbackConfig(name, triggerDescription, Gesture.exiting);
    }
    
    private void assembleCombinedEvent(String primaryZone, int primarySegment, int primaryHandState, String secondaryZone, int secondarySegment, int secondaryHandState) {
        String name = "combined-" + generateGestureName(primaryZone, primarySegment, primaryHandState, secondaryZone, secondarySegment, secondaryHandState);
        String identifierExplanation = generateGestureDescription(primaryZone, primarySegment, primaryHandState, secondaryZone, secondarySegment, secondaryHandState);
        
        String triggerDescription = "When the the hands are in the following state: " + identifierExplanation + ".";
        
        // Full event.
        addConfigItems(name, triggerDescription);
    }
    
    private void assembleIndividualHandEvent(String handLetter, String zone, int segment, int handState) {
        String name = "individual-" + generateSingleHandGestureName(handLetter, zone, segment, handState);
        String identifierExplanation = generateSingleHandGestureDescription(handLetter, zone, segment, handState);
        
        String triggerDescription = "When the the hands are in the following state: " + identifierExplanation + ".";
        addConfigItems(name, triggerDescription);
    }
    
    private void addConfigItems(String name, String triggerDescription) {
        addActionConfig(name, triggerDescription, Gesture.entering);
        addActionConfig(name, triggerDescription, Gesture.exiting);
        addAudioFeedbackConfig(name, triggerDescription, Gesture.entering);
        addAudioFeedbackConfig(name, triggerDescription, Gesture.exiting);
    }
    
    private String handState(int handState) {
        return this.states[handState];
    }
    
    private String capitalise(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
    
    private String hand(String handLetter) {
        return (handLetter == "p")?"primary":"secondary";
    }
    
    public String generateGestureName(String primaryZone, int primarySegment, int primaryHandState, String secondaryZone, int secondarySegment, int secondaryHandState) {
        String result = "";
        String primaryHand = generateSingleHandGestureName("p", primaryZone, primarySegment, primaryHandState);
        String secondaryHand = "";
        
        if (secondaryHandState == this.absent) {
            result = primaryHand + "-sAbsent";
        } else {
            secondaryHand = generateSingleHandGestureName("s", secondaryZone, secondarySegment, secondaryHandState);
            result = primaryHand + "-" + secondaryHand;
        }
        
        return result;
    }
    
    public String generateSingleHandGestureName(String letter, String zone, int segment, int handState) {
        // eg pActive0Closed
        return letter + capitalise(zone) + String.valueOf(segment) + capitalise(handState(handState));
    }
    
    public String generateGestureDescription(String primaryZone, int primarySegment, int primaryHandState, String secondaryZone, int secondarySegment, int secondaryHandState) {
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
        
        this.actionEvents.newItem(fullName, "", description);
    }
    
    private void addAudioFeedbackConfig(String name, String whenDescription, int direction) {
        String directionName = (direction == Gesture.entering)?"enter":"exit";
        String directionString = (direction == Gesture.entering)?"entering":"exiting";
        String fullName = name + "-" + directionName;
        String description = "Sound to play when " + directionString + " this state: " + whenDescription;
        
        this.audioEvents.newItem(fullName, "", description);
    }
}
