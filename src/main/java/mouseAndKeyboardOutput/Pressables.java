// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Pressables is a class for tracking which keys/buttons we can press and how that translates into the protocol that controls the mouse and keyboard. If there are keys/buttons that you want to use that aren't supported, this is the place to start.

It assumes the use of KeyEvent and MouseEvent java classes.
*/

package mouseAndKeyboardOutput;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.*;

import debug.Debug;

public class Pressables {
    public static final int INVALID = -1;
    private HashMap<String, Integer> pressables = new HashMap<String, Integer>();
    private Debug debug;
    
    public Pressables() {
        this.debug = Debug.getDebug("Pressables");
    }
    
    public void defineKeys() {
        //
        // Add keys here:
        //
        
        // From: https://docs.oracle.com/javase/7/docs/api/java/awt/event/KeyEvent.html
        definePressable("ctrl", KeyEvent.VK_CONTROL);
        definePressable("alt", KeyEvent.VK_ALT);
        definePressable("shift", KeyEvent.VK_SHIFT);
        
        this.debug.out(1, "Defined keyboard keys: " + this.pressables.keySet().toString());
    }
    
    public void defineButtons() {
        //
        // Add mouse buttons here:
        //
        
        // From: https://docs.oracle.com/javase/7/docs/api/java/awt/event/MouseEvent.html
        definePressable("left", InputEvent.BUTTON1_MASK);
        definePressable("middle", InputEvent.BUTTON2_MASK);
        definePressable("right", InputEvent.BUTTON3_MASK);
        
        this.debug.out(1, "Defined mouse buttons: " + this.pressables.keySet().toString());
    }
    
    private void definePressable(String pressableName, int pressableCode) {
        this.pressables.put(pressableName, pressableCode);
    }
    
    public Boolean isValidPressable(String value) {
        return this.pressables.containsKey(value);
    }
    
    public int getPressableID(String keyName) {
        int result = Pressables.INVALID;
        
        if (this.pressables.containsKey(keyName)) {
            result = this.pressables.get(keyName);
        }
        return result;
    }
    
    public Set<String> getPressablesIKnow() {
        return this.pressables.keySet();
    }
}
