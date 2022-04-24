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
        definePressable("delete", KeyEvent. VK_DELETE);
        definePressable("tab", KeyEvent. VK_TAB);
        definePressable("escape", KeyEvent. VK_ESCAPE);
        
        definePressable("home", KeyEvent. VK_HOME);
        definePressable("end", KeyEvent. VK_END);
        definePressable("pageUp", KeyEvent. VK_PAGE_UP);
        definePressable("pageDown", KeyEvent. VK_PAGE_DOWN);
        
        definePressable("up", KeyEvent.VK_UP);
        definePressable("down", KeyEvent.VK_DOWN);
        definePressable("left", KeyEvent.VK_LEFT);
        definePressable("right", KeyEvent.VK_RIGHT);
        
        definePressable("printscreen", KeyEvent.VK_PRINTSCREEN);
        
        definePressable("f1", KeyEvent.VK_F1);
        definePressable("f2", KeyEvent.VK_F2);
        definePressable("f3", KeyEvent.VK_F3);
        definePressable("f4", KeyEvent.VK_F4);
        definePressable("f5", KeyEvent.VK_F5);
        definePressable("f6", KeyEvent.VK_F6);
        definePressable("f7", KeyEvent.VK_F7);
        definePressable("f8", KeyEvent.VK_F8);
        definePressable("f9", KeyEvent.VK_F9);
        definePressable("f10", KeyEvent.VK_F10);
        definePressable("f11", KeyEvent.VK_F11);
        definePressable("f12", KeyEvent.VK_F12);
        definePressable("f13", KeyEvent.VK_F13);
        definePressable("f14", KeyEvent.VK_F14);
        definePressable("f15", KeyEvent.VK_F15);
        definePressable("f16", KeyEvent.VK_F16);
        definePressable("f17", KeyEvent.VK_F17);
        definePressable("f18", KeyEvent.VK_F18);
        definePressable("f19", KeyEvent.VK_F19);
        definePressable("f20", KeyEvent.VK_F20);
        definePressable("f21", KeyEvent.VK_F21);
        definePressable("f22", KeyEvent.VK_F22);
        definePressable("f23", KeyEvent.VK_F23);
        definePressable("f24", KeyEvent.VK_F24);
        
        definePressable("=", KeyEvent. VK_EQUALS);
        definePressable("+", KeyEvent. VK_PLUS);
        definePressable("-", KeyEvent. VK_MINUS);
        definePressable(";", KeyEvent. VK_SEMICOLON);
        definePressable(";", KeyEvent. VK_SEMICOLON);
        
        definePressable("0", KeyEvent.VK_0);
        definePressable("1", KeyEvent.VK_1);
        definePressable("2", KeyEvent.VK_2);
        definePressable("3", KeyEvent.VK_3);
        definePressable("4", KeyEvent.VK_4);
        definePressable("5", KeyEvent.VK_5);
        definePressable("6", KeyEvent.VK_6);
        definePressable("7", KeyEvent.VK_7);
        definePressable("8", KeyEvent.VK_8);
        definePressable("9", KeyEvent.VK_9);
        
        definePressable("a", KeyEvent.VK_A);
        definePressable("b", KeyEvent.VK_B);
        definePressable("c", KeyEvent.VK_C);
        definePressable("d", KeyEvent.VK_D);
        definePressable("e", KeyEvent.VK_E);
        definePressable("f", KeyEvent.VK_F);
        definePressable("g", KeyEvent.VK_G);
        definePressable("h", KeyEvent.VK_H);
        definePressable("i", KeyEvent.VK_I);
        definePressable("j", KeyEvent.VK_J);
        definePressable("k", KeyEvent.VK_K);
        definePressable("l", KeyEvent.VK_L);
        definePressable("m", KeyEvent.VK_M);
        definePressable("n", KeyEvent.VK_N);
        definePressable("o", KeyEvent.VK_O);
        definePressable("p", KeyEvent.VK_P);
        definePressable("q", KeyEvent.VK_Q);
        definePressable("r", KeyEvent.VK_R);
        definePressable("s", KeyEvent.VK_S);
        definePressable("t", KeyEvent.VK_T);
        definePressable("u", KeyEvent.VK_U);
        definePressable("v", KeyEvent.VK_V);
        definePressable("w", KeyEvent.VK_W);
        definePressable("x", KeyEvent.VK_X);
        definePressable("y", KeyEvent.VK_Y);
        definePressable("z", KeyEvent.VK_Z);
        
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
