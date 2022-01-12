package handWavey;

import dataCleaner.Changed;
import java.util.List;
import java.util.ArrayList;

/* For figuring out what events should be triggered when something changes. */
public class HandStateEvents {
    private Changed zoneChanged = new Changed("OOB");
    private Changed segmentChanged = new Changed(0);
    private Changed stateChanged = new Changed(Gesture.absent);
    
    private Gesture gesture;
    
    private String handLetter ="p";
    
    public HandStateEvents(Boolean isPrimary) {
        this.handLetter = (isPrimary)?"p":"s";
        this.gesture = new Gesture();
        this.gesture.generateConfig();
    }
    
    public void setZone(String zone) {
        this.zoneChanged.set(zone);
    }
    
    public void setSegment(int segment) {
        this.segmentChanged.set(segment);
    }
    
    public void setState(int state) {
        this.stateChanged.set(state);
    }
    
    public Boolean somethingChanged() {
        return (this.zoneChanged.hasChanged() || this.segmentChanged.hasChanged() || this.stateChanged.hasChanged());
    }
    
    public List<String> getEvents() {
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
        
        if (this.zoneChanged.hasChanged()){
            result.add("general-zone-" + this.handLetter + "AnyChange");
            result.add("general-zone-" + this.handLetter + this.gesture.capitalise(this.zoneChanged.fromStr()) + "-exit");
            result.add("general-zone-" + this.handLetter + this.gesture.capitalise(this.zoneChanged.toStr()) + "-enter");
        }
        
        if (this.segmentChanged.hasChanged()){
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

/*
assertEquals("combined-pActive0Open-sAny0Open", this.gesture.gestureName("active", 0, Gesture.open, "any", 0, Gesture.open));
assertEquals("combined-pNone2Closed-sAny3Open", this.gesture.gestureName("none", 2, Gesture.closed, "any", 3, Gesture.open));
assertEquals("combined-pActive0Open-sAbsent", this.gesture.gestureName("active", 0, Gesture.open, "any", 0, Gesture.absent));
assertEquals("individual-pActive0Open", this.gesture.gestureName("p", "active", 0, Gesture.open));
assertEquals("individual-sActive0Open", this.gesture.gestureName("s", "active", 0, Gesture.open));
*/
