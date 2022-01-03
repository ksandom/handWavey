package mouseAndKeyboardOutput;

import java.awt.Dimension;

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
    
    public void click(int button) {
        this.clicked = true;
        this.lastButton = button;
    }
    
    public void doubleClick(int button) {
        this.clicked = true;
        this.lastButton = button;
    }
    
    public void mouseDown(int button) {
        this.lastButton = button;
    }
    
    public void mouseUp(int button) {
        this.lastButton = button;
    }
    
    public int getLastMouseButton() {
        return this.lastButton;
    }
    
    public int getMouseButtonID(String buttonName) {
        return 5;
    }
    
    
    public void scroll(int amount) {
        this.scroll += amount;
    }
    
    
    public void keyDown(int key) {
        this.lastKey = key;
    }
    
    public void keyUp(int key) {
        this.lastKey = key;
    }
    
    public int getKeyID(String keyName) {
        return 99;
    }
    
    public String[] getKeysIKnow() {
        return new String[] {"a", "b", "c"};
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
        }
        
        return result;
    }
}
