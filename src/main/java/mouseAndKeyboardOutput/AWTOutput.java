// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Provides the AWT output option by implementing the Output interface.

This is the everyday interface that should work with most everyday user interfaces.
*/

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
import java.awt.MouseInfo;
import java.awt.Point;
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
    private int downButton = Pressables.INVALID;

    private Pressables buttons = new Pressables();
    private Pressables keys = new Pressables();

    public AWTOutput() {
        this.debug = Debug.getDebug("AWTOutput");

        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        this.keys.defineKeys();
        this.buttons.defineButtons();

        this.button = getMouseButtonID("left");
        this.downButton = this.button;
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

    public Point getPosition() {

        return MouseInfo.getPointerInfo().getLocation();
    }

    public void click(String button) {
        if (isValidButton(button)) {
            this.debug.out(1, "Click with button: " + button);
            mouseDown(button);
            mouseUp(button);
        }
    }

    public void doubleClick(String button) {
        if (isValidButton(button)) {
            this.debug.out(1, "Doubleclick with button: " + button);
            click(button);
            click(button);
        }
    }

    public void mouseDown(String button) {
        if (isValidButton(button)) {
            mouseUp(button);
            try {
                this.button = getMouseButtonID(button);
                this.debug.out(1, "MouseDown with button: " + button + "/" + String.valueOf(this.button));
                this.downButton = this.button;
                this.robot.mousePress(this.button);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public void mouseUp(String button) {
        if (this.downButton != Pressables.INVALID) {
            try {
                this.debug.out(1, "MouseUp with button ID: " + String.valueOf(this.downButton) + "(" + button + "/" + String.valueOf(getMouseButtonID(button)) + " requested.)");
                this.robot.mouseRelease(this.downButton);
                // this.robot.mouseRelease(button); // TODO Restore the ability to press multiple buttons at a time.
                this.downButton = Pressables.INVALID;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public int getLastMouseButton() {
        return this.button;
    }

    public void scroll(int amount) {
        int debugLevel = (amount == 0)?2:1;

        if (Math.abs(amount) > 1) {
            this.debug.out(0, "Disallowed scroll by: " + String.valueOf(amount));
        } else {
            this.debug.out(debugLevel, "Scroll by: " + String.valueOf(amount));
            this.robot.mouseWheel(amount);
        }
    }

    private Boolean isValidButton(String value) {
        return this.buttons.isValidPressable(value);
    }

    private Boolean isValidKey(String value) {
        return this.keys.isValidPressable(value);
    }

    private int getMouseButtonID(String buttonName) {
        return this.buttons.getPressableID(buttonName);
    }

    private int getKeyID(String keyName) {
        return this.keys.getPressableID(keyName);
    }

    public void keyDown(String key) {
        if (isValidKey(key)) {
            try {
                this.debug.out(1, "KeyDown: " + key);
                this.robot.keyPress(getKeyID(key));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public void keyUp(String key) {
        if (isValidKey(key)) {
            try {
                this.debug.out(1, "KeyUp: " + key);
                this.robot.keyRelease(getKeyID(key));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public Set<String> getKeysIKnow() {
        return this.keys.getPressablesIKnow();
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
