package mouseAndKeyboardOutput;

import java.awt.Dimension;
import java.util.*;

public interface Output {
    abstract void info();
    abstract Dimension getDesktopResolution();
    abstract void setPosition(int x, int y);
    
    abstract void click(String button);
    abstract void doubleClick(String button);
    abstract void mouseDown(String button);
    abstract void mouseUp(String button);
    //abstract int getLastMouseButton();
    //abstract int getMouseButtonID(String buttonName);
    
    abstract void scroll(int amount);
    
    abstract void keyDown(String key);
    abstract void keyUp(String key);
    //abstract int getKeyID(String keyName);
    abstract Set<String> getKeysIKnow();
    
    abstract int testInt(String testName); // For testing internals in unit tests. Can simply return nothing.;
}
