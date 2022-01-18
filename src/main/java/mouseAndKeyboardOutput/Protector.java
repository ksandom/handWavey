package mouseAndKeyboardOutput;

import debug.Debug;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.util.*;

public class Protector {
    private Debug debug;
    private Output output;
    
    private HashMap<String, Boolean> things = new HashMap<String, Boolean>();
    
    public Protector(Debug debug, Output output) {
        this.debug = debug;
        this.output = output;
    }
    
    public Boolean setDown(String name) {
        Boolean result = false;
        
        if (isUp(name)) {
            this.things.put(name, true);
            result = true;
        }
        
        return result;
    }
    
    public Boolean setUp(String name) {
        Boolean result = true;
        
        if (isUp(name)) {
            this.things.remove(name);
            result = false;
        }
        
        return result;
    }
    
    public Boolean isDown(String name) {
        return this.things.containsKey(name);
    }

    public Boolean isUp(String name) {
        return (!this.things.containsKey(name));
    }
    
    public Set<String> getDownItems() {
        return this.things.keySet();
    }
}
