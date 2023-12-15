// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Protects the outputs/user interfaces from the user/macros doing stupid things like pressing the same key twice without releasing it in-between.

To use this, simply create an instance of this class, and assign it an Output. Eg:
OutputProtection output = new OutputProtection(new AWTOutput());
*/

package mouseAndKeyboardOutput;

import debug.Debug;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.util.*;

public class OutputProtection {
    private Debug debug;
    private Output output;
    private Protector buttonProtector;
    private Protector keyProtector;

    public OutputProtection(Output output) {
        this.debug = Debug.getDebug("OutputProtection");
        this.output = output;

        this.buttonProtector = new Protector();
        this.keyProtector = new Protector();
    }

    public void info() {
        System.out.println("Output protection active for:");
        this.output.info();
    }

    public Dimension getDesktopResolution() {
        return this.output.getDesktopResolution();
    }

    public void setPosition(int x, int y) {
        this.output.setPosition(x, y);
    }

    public void click(String button) {
        this.output.click(button);
    }

    public void doubleClick(String button) {
        this.output.doubleClick(button);
    }

    public void mouseDown(String button) {
        if (this.buttonProtector.isUp(button)) {
            this.buttonProtector.setDown(button);
            this.output.mouseDown(button);
        } else {
            this.debug.out(1, "Mouse button " + button + " was already down. Not doing it again.");
        }
    }

    public void mouseUp(String button) {
        if (this.buttonProtector.isDown(button)) {
            this.buttonProtector.setUp(button);
            this.output.mouseUp(button);
        } else {
            this.debug.out(1, "Mouse button " + button + " was already up. Not doing it again.");
        }
    }

    public void releaseButtons() {
        for (String button : this.buttonProtector.getDownItems()) {
            mouseUp(button);
        }
    }

    public void scroll(int amount) {
        this.output.scroll(amount);
    }


    public void keyDown(String key) {
        if (this.keyProtector.isUp(key)) {
            this.keyProtector.setDown(key);
            this.output.keyDown(key);
        } else {
            this.debug.out(1, "Keyboard key " + key + " was already down. Not doing it again.");
        }
    }

    public void keyUp(String key) {
        if (this.keyProtector.isDown(key)) {
            this.keyProtector.setUp(key);
            this.output.keyUp(key);
        } else {
            this.debug.out(1, "Keyboard key " + key + " was already up. Not doing it again.");
        }
    }

    public void releaseKeys() {
        for (String key : this.keyProtector.getDownItems()) {
            keyUp(key);
        }
    }

    public Set<String> getKeysIKnow() {
        return this.output.getKeysIKnow();
    }


    public int testInt(String testName) {
        return this.output.testInt(testName);
    }
}
