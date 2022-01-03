package handWavey;

import mouseAndKeyboardOutput.*;
import audio.*;
import config.Config;
import config.Group;
import debug.Debug;
import java.util.HashMap;
import java.io.File;

public class HandWaveyEvent {
    public static final Boolean audioDisabled = false;
    public static final Boolean audioEnabled = true;
    
    private Output output;
    private Boolean useAudio;
    
    private HashMap<String, String> actionCache = new HashMap<String, String>();
    private HashMap<String, String> audioCache = new HashMap<String, String>();
    private String audioPath;
    
    private Group actionEvents;
    private Group audioEvents;
    
    private Debug debug = new Debug(1, "HandWaveyEvent");
    
    public HandWaveyEvent(Output output, Boolean useAudio) {
        this.output = output;
        this.useAudio = useAudio;
        
        this.actionEvents = Config.singleton().getGroup("actionEvents");
        this.audioEvents = Config.singleton().getGroup("audioEvents");
        
        this.audioPath = Config.singleton().getGroup("audioConfig").getItem("pathToAudio").get() + File.separator;
    }
    
    public void triggerEvent(String eventName) {
        if (!eventIsCached(eventName)) {
            cacheEvent(eventName);
        }
        
        // TODO Action.
        
        if (this.useAudio) {
            BackgroundSound.play(this.audioCache.get(eventName));
        }
    }
    
    private Boolean eventIsCached(String eventName) {
        return (this.actionCache.containsKey(eventName) && this.audioCache.containsKey(eventName));
    }
    
    private void cacheEvent(String eventName) {
        cacheAction(eventName);
        cacheAudio(eventName);
    }
    
    private void cacheAction(String eventName) {
        this.debug.out(3, "Load eventName " + eventName);
        String action = this.actionEvents.getItem(eventName).get();
        
        this.audioCache.put(eventName, action);
        this.debug.out(1, "Loaded action for event " + eventName + ".");
    }
    
    private void cacheAudio(String eventName) {
        this.debug.out(3, "Load eventName " + eventName);
        String filePath = this.audioEvents.getItem(eventName).get();
        String fullPath = "";
        
        if (filePath != "") {
            fullPath = this.audioPath + filePath;
        }
        
        this.audioCache.put(eventName, filePath);
        this.debug.out(1, "Loaded " + fullPath + " for event " + eventName + ".");
    }
}
