// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Provides the Null output option by implementing the Output interface.

This isn't intended for use by users. It's instead intended to assist with testing.
*/

package mouseAndKeyboardOutput;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.util.*;

public class NullOutput implements Output {
    private int x = 0;
    private int y = 0;

    private Boolean clicked = false;

    private int lastButton = 0;
    private int lastKey = 0;

    private int scroll = 0;

    public void info() {
        System.out.println("Null output device. Intended for unit testing.");
    }

    public Dimension getDesktopResolution() {
        return new Dimension(123, 321);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void click(String button) {
        this.clicked = true;
        this.lastButton = getMouseButtonID(button);
    }

    public void doubleClick(String button) {
        this.clicked = true;
        this.lastButton = getMouseButtonID(button);
    }

    public void mouseDown(String button) {
        this.lastButton = getMouseButtonID(button);
    }

    public void mouseUp(String button) {
        this.lastButton = getMouseButtonID(button);
    }

    private int getMouseButtonID(String buttonName) {
        int result = 0;
        switch (buttonName) {
            case "left":
                result = InputEvent.BUTTON1_MASK;
                break;
            case "middle":
                result = InputEvent.BUTTON2_MASK;
                break;
            case "right":
                result = InputEvent.BUTTON3_MASK;
                break;
            default:
                break;
        }

        return result;
    }


    public void scroll(int amount) {
        this.scroll += amount;
    }


    public void keyDown(String key) {
        this.lastKey = getKeyID(key);
    }

    public void keyUp(String key) {
        this.lastKey = getKeyID(key);
    }

    private int getKeyID(String keyName) {
        int result = 0;
        switch (keyName) {
            case "ctrl":
                result = KeyEvent.VK_CONTROL;
                break;
            case"alt":
                result = KeyEvent.VK_ALT;
                break;
            case "shift":
                result = KeyEvent.VK_SHIFT;
                break;
            default:
                break;
        }

        return result;
    }

    public Set<String> getKeysIKnow() {
        Set<String> keys = new HashSet<String>();
        keys.add("a");
        keys.add("b");
        keys.add("c");

        return keys;
    }


    public int testInt(String testName) {
        int result = 0;

        switch (testName) {
            case "posX":
                result = this.x;
                break;
            case "posY":
                result = this.y;
                break;
            case "clicked":
                result = (this.clicked)?1:0;
                break;
            case "scroll":
                result = this.scroll;
                break;
            case "lastKey":
                result = this.lastKey;
                break;
            case "lastMouseButton":
                result = this.lastButton;
                break;
            default:
                break;
        }

        return result;
    }
}
