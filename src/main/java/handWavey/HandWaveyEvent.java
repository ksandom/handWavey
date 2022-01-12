package handWavey;

import debug.Debug;
import mouseAndKeyboardOutput.*;
import audio.*;
import macro.MacroLine;
import config.Config;
import config.Group;
import debug.Debug;
import java.io.File;
import java.util.HashMap;
import java.util.List;

/* For taking the right actions when an event is triggers. */
public class HandWaveyEvent {
    public static final Boolean audioDisabled = false;
    public static final Boolean audioEnabled = true;
    
    private Output output;
    private Boolean useAudio;
    private String audioPath;
    
    private Group actionEvents;
    private Group audioEvents;
    
    private MacroLine macroLine;
    
    // TODO Make this debug level configurable.
    private Debug debug = new Debug(2, "HandWaveyEvent");
    
    public HandWaveyEvent(Output output, Boolean useAudio, HandsState handsState) {
        this.output = output;
        this.useAudio = useAudio;
       
        this.macroLine = new MacroLine(this.output, handsState);
        
        this.actionEvents = Config.singleton().getGroup("actionEvents");
        this.audioEvents = Config.singleton().getGroup("audioEvents");
        
        this.audioPath = Config.singleton().getGroup("audioConfig").getItem("pathToAudio").get() + File.separator;
    }
    
    public void triggerEvents(List<String> events) {
        this.debug.out(1, "Received " + String.valueOf(events.size()) + " events.");
        for (String eventName : events) {
            triggerEvent(eventName, "  ");
        }
    }
    
    public void triggerEvent(String eventName) {
        triggerEvent(eventName, "");
    }
    
    public void triggerEvent(String eventName, String indent) {
        this.debug.out(2, indent + "Event: " + eventName + ".");
        String macroLine = this.getEventAction(eventName);
        if (macroLine != "") {
            this.debug.out(2, indent + "  macroLine: \"" + macroLine + "\"");
            this.macroLine.runLine(macroLine);
        }
        
        if (this.useAudio) {
            String fileToPlay = this.getEventAudio(eventName);
            if (fileToPlay != "") {
                this.debug.out(2, indent + "  fileToPlay: \"" + fileToPlay + "\"");
                BackgroundSound.play(fileToPlay);
            }
        }
    }
    
    private String getEventAction(String eventName) {
        if (this.actionEvents.getItem(eventName) == null) {
            return "";
        }
        
        String action = this.actionEvents.getItem(eventName).get();
        
        return action;
    }
    
    private String getEventAudio(String eventName) {
        if (this.audioEvents.getItem(eventName) == null) {
            return "";
        }
        
        String filePath = this.audioEvents.getItem(eventName).get();
        String fullPath = "";
        
        if (filePath != "") {
            fullPath = this.audioPath + filePath;
        }
        
        return fullPath;
    }
}
