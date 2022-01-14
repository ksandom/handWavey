package mouseAndKeyboardOutput;

import java.awt.Dimension;
import java.util.*;

public interface Output {
    abstract void info();
    abstract Dimension getDesktopResolution();
    abstract void setPosition(int x, int y);
    
    abstract void click(int button);
    abstract void doubleClick(int button);
    abstract void mouseDown(int button);
    abstract void mouseUp(int button);
    abstract int getLastMouseButton();
    abstract int getMouseButtonID(String buttonName);
    
    abstract void scroll(int amount);
    
    abstract void keyDown(int key);
    abstract void keyUp(int key);
    abstract int getKeyID(String keyName);
    abstract Set<String> getKeysIKnow();
    
    abstract int testInt(String testName); // For testing internals in unit tests. Can simply return nothing.;
}
