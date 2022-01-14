package mouseAndKeyboardOutput;

import debug.Debug;
import java.io.IOException;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Point;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.DisplayMode;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.*;

/* AWTOutput is the default way to control the mouse and keyboard of a machine. */
public class AWTOutput implements Output {
    private Debug debug;
    
    private GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private GraphicsDevice[] gs = this.ge.getScreenDevices();
    private Robot robot = null;
    private int button = 0;
    private int downButton = 0;
    private static final int invalid = -1;
    
    private HashMap<String, Integer> keys = new HashMap<String, Integer>();
    private HashMap<String, Integer> buttons = new HashMap<String, Integer>();
    
    public AWTOutput() {
        this.debug = Debug.getDebug("AWTOutput");
        
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        
        defineKeys();
        defineButtons();
        
        this.button = getMouseButtonID("left");
        this.downButton = this.button;
    }

    private void defineKeys() {
        //
        // Add keys here:
        //
        
        // From: https://docs.oracle.com/javase/7/docs/api/java/awt/event/KeyEvent.html
        defineKey("ctrl", KeyEvent.VK_CONTROL);
        defineKey("alt", KeyEvent.VK_ALT);
        defineKey("shift", KeyEvent.VK_SHIFT);
        
        this.debug.out(1, "Defined keyboard keys: " + this.keys.keySet().toString());
    }
    
    private void defineKey(String keyName, int keyCode) {
        this.keys.put(keyName, keyCode);
    }
    
    private void defineButtons() {
        //
        // Add mouse buttons here:
        //
        
        // TODO From: https://docs.oracle.com/javase/7/docs/api/java/awt/event/MouseEvent.html
        defineButton("left", InputEvent.BUTTON1_MASK);
        defineButton("middle", InputEvent.BUTTON2_MASK);
        defineButton("right", InputEvent.BUTTON3_MASK);
        
        this.debug.out(1, "Defined mouse buttons: " + this.buttons.keySet().toString());
    }
    
    private void defineButton(String buttonName, int buttonCode) {
        this.buttons.put(buttonName, buttonCode);
    }
    
    public Dimension getDesktopResolution() {
        int x = 0;
        int y = 0;
        int w = 0;
        int h = 0;
        
        for (GraphicsDevice device: this.gs) {
            GraphicsConfiguration[] configuration = device.getConfigurations();
            Rectangle screenBounds = configuration[0].getBounds();
            
            if (screenBounds.x > x) {
                x = screenBounds.x;
                w = x + screenBounds.width;
            }
            
            if (screenBounds.y > y) {
                y = screenBounds.y;
                h = y + screenBounds.height;
            }
            
            if (w == 0) {
                w = screenBounds.width;
            }
            
            if (h == 0) {
                h = screenBounds.height;
            }
        }
        
        return new Dimension(w, h);
    }
    
    public void info() {
        System.out.println("Info.");
        
        Dimension desktopResolution = getDesktopResolution();
        System.out.println("Desktop resolution: " + desktopResolution.width + " " + desktopResolution.height);
        
        Rectangle rectangle = this.ge.getMaximumWindowBounds();
        System.out.println("Primary display: " + rectangle.toString());
        
        Point centerPoint =    this.ge.getCenterPoint();
        System.out.println("    Center: " + centerPoint.toString());
        
        System.out.println("");
        
        GraphicsDevice[] gs = this.ge.getScreenDevices();
        int length = gs.length;
        String len = String.valueOf(length);
        System.out.println("Number of devices: " + len);
        
        for (int i = 0; i < gs.length; i ++) {
            String id = gs[i].getIDstring();
            System.out.println("    ID: " + id);
            
            DisplayMode dm = gs[i].getDisplayMode();
            System.out.println("        Mode via DisplayMode: " + String.valueOf(dm.getWidth()) + " " + String.valueOf(dm.getHeight()));
            
            GraphicsConfiguration[] gc = gs[i].getConfigurations();
            Rectangle gcBounds = gc[0].getBounds();
            System.out.println("        Rectangle(0): " + gcBounds.toString());
            
            System.out.println("");
        }
    }
    
    public void setPosition(int x, int y) {
        this.robot.mouseMove(x, y);
    }
    
    public void click(int button) {
        if (isValid(button)) {
            this.debug.out(1, "Click with button ID: " + String.valueOf(button));
            mouseDown(button);
            mouseUp(button);
        }
    }
    
    public void doubleClick(int button) {
        if (isValid(button)) {
            this.debug.out(1, "Doubleclick with button ID: " + String.valueOf(button));
            click(button);
            click(button);
        }
    }
    
    public void mouseDown(int button) {
        if (isValid(button)) {
            try {
                this.debug.out(1, "MouseDown with button ID: " + String.valueOf(button));
                this.robot.mousePress(button);
                this.button = button;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void mouseUp(int button) {
        if (isValid(button)) {
            try {
                this.debug.out(1, "MouseUp with button ID: " + String.valueOf(this.downButton) + "(" + String.valueOf(button) + " requested.)");
                this.robot.mouseRelease(this.downButton);
                // this.robot.mouseRelease(button); // TODO Restore the ability to press multiple buttons at a time.
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
    
    public int getLastMouseButton() {
        return this.button;
    }
    
    public void scroll(int amount) {
        this.debug.out(1, "Scroll by: " + String.valueOf(amount));
        this.robot.mouseWheel(amount);
    }
    
    private Boolean isValid(int value) {
        return (value != AWTOutput.invalid);
    }
    
    public int getMouseButtonID(String buttonName) {
        int result = AWTOutput.invalid;
        
        if (this.buttons.containsKey(buttonName)) {
            result = this.buttons.get(buttonName);
        }
        return result;
    }
    
    public int getKeyID(String keyName) {
        int result = AWTOutput.invalid;
        
        if (this.keys.containsKey(keyName)) {
            result = this.keys.get(keyName);
        }
        return result;
    }
    
    public void keyDown(int key) {
        if (isValid(key)) {
            try {
                this.debug.out(1, "KeyDown with key ID: " + String.valueOf(key));
                this.robot.keyPress(key);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void keyUp(int key) {
        if (isValid(key)) {
            try {
                this.debug.out(1, "KeyUp with key ID: " + String.valueOf(key));
                this.robot.keyRelease(key);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
    
    public Set<String> getKeysIKnow() {
        return this.keys.keySet();
    }
    
    public void sleep(int microseconds) {
        try {
            Thread.sleep(microseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public int testInt(String testName) {
        int result = 0;
        
        switch (testName) {
            case "unused":
                break;
        }
        
        return result;
    }
}
