package mouseAndKeyboardOutput;

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
import java.util.HashMap;

public class GenericOutput implements Output {
    private GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private GraphicsDevice[] gs = this.ge.getScreenDevices();
    private Robot robot = null;
    private int button = 0;
    private int downButton = 0;
    private String[] keysIKnow = {"ctrl", "alt", "shift"};
    
    private static final int invalid = -1;
    
    private HashMap<String, Integer> keys = new HashMap<String, Integer>();
    private HashMap<String, Integer> buttons = new HashMap<String, Integer>();
    
    public GenericOutput() {
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
    }
    
    private void defineKey(String keyName, int keyCode) {
        this.keys.put(keyName, keyCode);
    }
    
    private void defineButtons() {
        //
        // Add mouse buttons here:
        //
        
        // TODO From: https://docs.oracle.com/javase/7/docs/api/java/awt/event/KeyEvent.html
        defineButton("left", InputEvent.BUTTON1_MASK);
        defineButton("middle", InputEvent.BUTTON2_MASK);
        defineButton("right", InputEvent.BUTTON3_MASK);
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
        mouseDown(button);
        mouseUp(button);
    }
    
    public void doubleClick(int button) {
        if (isValid(button)) {
            click(button);
            click(button);
        }
    }
    
    public void mouseDown(int button) {
        if (isValid(button)) {
            try {
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
        this.robot.mouseWheel(amount);
    }
    
    private Boolean isValid(int value) {
        return (value != -1);
    }
    
    public int getMouseButtonID(String buttonName) {
        int result = this.invalid;
        
        if (this.buttons.containsKey(buttonName)) {
            result = this.buttons.get(buttonName);
        }
        return result;
    }
    
    public int getKeyID(String keyName) {
        int result = this.invalid;
        
        if (this.keys.containsKey(keyName)) {
            result = this.keys.get(keyName);
        }
        return result;
    }
    
    public void keyDown(int key) {
        if (isValid(key)) {
            try {
                this.robot.keyPress(key);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void keyUp(int key) {
        if (isValid(key)) {
            try {
                this.robot.keyRelease(key);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
    
    public String[] getKeysIKnow() {
        return this.keysIKnow;
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
