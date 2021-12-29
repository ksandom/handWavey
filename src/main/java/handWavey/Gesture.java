package handWavey;

import config.*;
import debug.Debug;
import java.util.HashMap;
import java.sql.Timestamp;

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
    
    // Segments.
    private int maximumSegments = 10;
    
    public Gesture() {
        this.states[this.open] = "open";
        this.states[this.closed] = "closed";
        this.states[this.any] = "any";
        this.states[this.absent] = "absent";
    }
    
    public void generateConfig() {
        // Generate all combinations of events up for configuration.
        
        for (int primaryState = 0; primaryState < stateCount-1; primaryState++) {
            for ( String primaryZone :  zones ) {
                for (int primarySegment = -1; primarySegment < this.maximumSegments; primarySegment++) {
                
                    for (int secondaryState = 0; secondaryState < stateCount; secondaryState++) {
                        if (secondaryState == this.absent) {
                            assembleEvent(primaryZone, primarySegment, primaryState, "OOB", 0, this.absent);
                        } else {
                            for ( String secondaryZone :  zones ) {
                                for (int secondarySegment = -1; secondarySegment < this.maximumSegments; secondarySegment++) {
                                    assembleEvent(primaryZone, primarySegment, primaryState, secondaryZone, secondarySegment, secondaryState);
                                }
                            }
                        }
                    }
                    
                }
            }
        }
    }
    
    private void assembleEvent(String primaryZone, int primarySegment, int primaryHandState, String secondaryZone, int secondarySegment, int secondaryHandState) {
        String name = generateGestureName(primaryZone, primarySegment, primaryHandState, secondaryZone, secondarySegment, secondaryHandState);
        String identifierExplanation = generateGestureDescription(primaryZone, primarySegment, primaryHandState, secondaryZone, secondarySegment, secondaryHandState);
        
        String triggerDescription = "When the the hands are in the following state: " + identifierExplanation + ".";
        
        /* TODO
        // * Finish code for generating the description.
        // * Write tests for public functions.
        // * should generateConfig() also generate shorter events?
        // * Create event actions (enter, exit).
        // * Create event audio feedback (enter, exit).
        // * HandsState should track which gesture is active, and whether it has changed (therefore whether to trigger an action.)
        // * HandWaveyManager should receive that information, and trigger an Event handler/interpreter to send the actions to the appropriate place.
        */
    }
    
    private String handState(int handState) {
        return this.states[handState];
    }
    
    private String capitalise(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
    
    public String generateGestureName(String primaryZone, int primarySegment, int primaryHandState, String secondaryZone, int secondarySegment, int secondaryHandState) {
        String result = "";
        String primaryHand = generateSingleHandGestureName("p", primaryZone, primarySegment, primaryHandState);
        String secondaryHand = "";
        
        if (secondaryHandState == this.absent) {
            result = primaryHand;
        } else {
            secondaryHand = generateSingleHandGestureName("s", secondaryZone, secondarySegment, secondaryHandState);
            result = primaryHand + "-" + secondaryHand;
        }
        
        return result;
    }
    
    private String generateSingleHandGestureName(String letter, String zone, int segment, int handState) {
        return zone + "-" + letter + String.valueOf(segment) + capitalise(handState(handState));
    }
    
    public String generateGestureDescription(String primaryZone, int primarySegment, int primaryHandState, String secondaryZone, int secondarySegment, int secondaryHandState) {
        // TODO This is old code. All of it needs to be re-written.
        String pSegment = String.valueOf(primarySegment);
        String sSegment = String.valueOf(secondarySegment);
        
        return primaryZone + "-p" + pSegment + "-s" + secondarySegment;
    }
    
    private String generateSingleHandGestureDescription(String letter, String zone, int segment, int handState) {
        // TODO This is effectively a stub from the name function. All of it needs to be re-written.
        
        String pSegment = String.valueOf(segment);
        
        return zone + "-" + letter + String.valueOf(segment) + capitalise(handState(handState));
    }
}
