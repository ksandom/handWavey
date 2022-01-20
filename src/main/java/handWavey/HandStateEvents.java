package handWavey;

import dataCleaner.Changed;
import java.util.List;
import java.util.ArrayList;

import debug.Debug;

/* For figuring out what events should be triggered when something changes. */
public class HandStateEvents {
    private Debug debug;
    private Changed zoneChanged = new Changed("OOB");
    private Changed segmentChanged = new Changed(0);
    private Changed stateChanged = new Changed(Gesture.absent);
    
    private Gesture gesture;
    
    private String handLetter ="p";
    
    public HandStateEvents(Boolean isPrimary) {
        this.debug = Debug.getDebug("HandStateEvents");
        
        this.handLetter = (isPrimary)?"p":"s";
        this.gesture = new Gesture();
        this.gesture.generateConfig();
    }
    
    public void setZone(String zone) {
        this.zoneChanged.set(zone);
        
        if (this.zoneChanged.hasChanged()) {
            this.debug.out(1, this.handLetter + " hand has changed zone to " + zone);
        }
    }
    
    public void setSegment(int segment) {
        this.segmentChanged.set(segment);
        
        if (this.segmentChanged.hasChanged()) {
            this.debug.out(1, this.handLetter + " hand has changed segment to " + String.valueOf(segment));
        }
    }
    
    public void setState(int state) {
        this.stateChanged.set(state);
        
        if (this.stateChanged.hasChanged()) {
            this.debug.out(1, this.handLetter + " hand has changed state to " + String.valueOf(state));
        }
    }
    
    public Boolean freshlyAbsent() {
        return (this.stateChanged.toInt() == Gesture.absent && this.stateChanged.hasChanged());
    }
    
    public Boolean somethingChanged() {
        return (this.zoneChanged.hasChanged() || this.segmentChanged.hasChanged() || this.stateChanged.hasChanged());
    }
    
    public List<String> getEvents() { // Normal use-case.
        List<String> result = new ArrayList<String>();
        
        if (somethingChanged()) {
            result.add(this.gesture.gestureName(
                this.handLetter,
                this.zoneChanged.fromStr(),
                this.segmentChanged.fromInt(),
                this.stateChanged.fromInt()) + "-exit");
            result.add(this.gesture.gestureName(
                this.handLetter,
                this.zoneChanged.toStr(),
                this.segmentChanged.toInt(),
                this.stateChanged.toInt()) + "-enter");
        } else {
            return result; // Return quickly if nothing has changed.
        }
        
        if (this.zoneChanged.hasChanged() || reIntroduced()){
            result.add("general-zone-" + this.handLetter + "AnyChange");
            result.add("general-zone-" + this.handLetter + this.gesture.capitalise(this.zoneChanged.fromStr()) + "-exit");
            result.add("general-zone-" + this.handLetter + this.gesture.capitalise(this.zoneChanged.toStr()) + "-enter");
        }
        
        if (this.segmentChanged.hasChanged() || reIntroduced()){
            result.add("general-segment-" + this.handLetter + "AnyChange");
            result.add("general-segment-" + this.handLetter + this.segmentChanged.fromInt() + "-exit");
            result.add("general-segment-" + this.handLetter + this.segmentChanged.toInt() + "-enter");
        }
        
        if (this.stateChanged.hasChanged()){
            String fromState = this.gesture.capitalise(this.gesture.handState(this.stateChanged.fromInt()));
            String toState = this.gesture.capitalise(this.gesture.handState(this.stateChanged.toInt()));
            
            result.add("general-state-" + this.handLetter + "AnyChange");
            result.add("general-state-" + this.handLetter + fromState + "-exit");
            result.add("general-state-" + this.handLetter + toState + "-enter");
        }
        
        return result;
    }
    
    private Boolean reIntroduced() {
        return (this.stateChanged.hasChanged() && this.stateChanged.fromInt() == Gesture.absent);
    }
    
    public List<String> getCurrentEvents() { // For when events have been lost during a click freeze.
        List<String> result = new ArrayList<String>();
        
        result.add(this.gesture.gestureName(
            this.handLetter,
            this.zoneChanged.toStr(),
            this.segmentChanged.toInt(),
            this.stateChanged.toInt()) + "-enter");
        
        result.add("general-zone-" + this.handLetter + "AnyChange");
        result.add("general-zone-" + this.handLetter + this.gesture.capitalise(this.zoneChanged.toStr()) + "-enter");
        
        result.add("general-segment-" + this.handLetter + "AnyChange");
        result.add("general-segment-" + this.handLetter + this.segmentChanged.toInt() + "-enter");
        
        String fromState = this.gesture.capitalise(this.gesture.handState(this.stateChanged.fromInt()));
        String toState = this.gesture.capitalise(this.gesture.handState(this.stateChanged.toInt()));
        
        result.add("general-state-" + this.handLetter + "AnyChange");
        result.add("general-state-" + this.handLetter + toState + "-enter");
        
        return result;
    }
    
    public String getIndividualExitEvent() {
        return this.gesture.generateSingleHandGestureName(
            this.handLetter,
            this.zoneChanged.fromStr(),
            this.segmentChanged.fromInt(),
            this.stateChanged.fromInt());
    }
    
    public String getIndividualEnterEvent() {
        return this.gesture.generateSingleHandGestureName(
            this.handLetter,
            this.zoneChanged.toStr(),
            this.segmentChanged.toInt(),
            this.stateChanged.toInt());
    }
}
