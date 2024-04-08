// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
This is the interface that must be implemented to create a new Output.

See AWTOutput, VNCOutput, and NullOutput for real world examples.
*/

package mouseAndKeyboardOutput;

import java.awt.Dimension;
import java.awt.Point;
import java.util.*;

public interface Output {
    abstract void info();
    abstract Dimension getDesktopResolution(); // Return width, height
    abstract void setPosition(int x, int y); // Move the cursor to a specific point on the screen.
    abstract Point getPosition(); // Get the current location of the mouse cursor.

    abstract void click(String button); // Eg "left", "middle", "right"
    abstract void doubleClick(String button); // Eg "left", "middle", "right"
    abstract void mouseDown(String button); // Eg "left", "middle", "right"
    abstract void mouseUp(String button); // Eg "left", "middle", "right"

    abstract void scroll(int amount); // Eg 1 = scroll down by 1 click. -1 = scroll up by one click.

    abstract void keyDown(String key); // Eg "ctrl", "alt", "a", "b".
    abstract void keyUp(String key); // Eg "ctrl", "alt", "a", "b".
    abstract Set<String> getKeysIKnow(); // Returns a set of strings that can be used for keyDown(), and keyUp().

    abstract int testInt(String testName); // For testing internals in unit tests. Can simply return nothing.;
}
