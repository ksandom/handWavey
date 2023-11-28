// (c) 2023 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Handles turning the motion of the hand into derived motions like cursor and scrolling.
*/

package handWavey;

import dataCleaner.MovingMean;
import dataCleaner.History;
import config.*;
import debug.Debug;
import mouseAndKeyboardOutput.*;

import java.awt.Dimension;
import java.sql.Timestamp;
import java.util.HashMap;

public class Motion {

    private static Motion motion = null;

    private Debug debug;
    private OutputProtection output;

    private MovingMean movingMeanX = new MovingMean(1, 0);
    private MovingMean movingMeanY = new MovingMean(1, 0);
    private HandsState handsState;

    private History historyX;
    private History historyY;
    private History historyScroll;

    private int desktopWidth = 0;
    private int desktopHeight = 0;

    private int xOffset = 0;
    private int yOffset = 0;
    private double xMultiplier = 1;
    private double yMultiplier = 1;
    private double zMultiplier = -1;

    private double lastAbsoluteX = 0;
    private double lastAbsoluteY = 0;
    private double diffRemainderY = 0;
    private double diffRemainderX = 0;
    private double diffScrollRemainderY = 0;
    private double diffScrollRemainderX = 0;

    private int touchPadX = 0;
    private int touchPadY = 0;
    private double touchPadInputMultiplier = 5;
    private double touchPadOutputMultiplier = 1;
    private double touchPadAcceleration = 2;
    private double touchPadMaxSpeed = 20;

    private int rewindCursorTime = 300;
    private int repeatRewindCursorTime = 700;
    private long lastCursorRewind = 0;
    private int cursorLockTime = 400;
    private long cursorLock = 0;

    private double scrollInputMultiplier = 5;
    private double scrollOutputMultiplier = 1;
    private double scrollAcceleration = 2;
    private double scrollMaxSpeed = 20;

    private int rewindScrollTime = 300;

    private double relativeSensitivity = 0.1;

    private int maxChange = 200;

    private String zoneMode = "touchScreen";

    private Boolean shouldDiscardOldPosition = true;


    public static Motion singleton() {
        if (Motion.motion == null) {
            Motion.motion = new Motion();
        }

        return Motion.motion;
    }


    public Motion() {
        this.debug = Debug.getDebug("Motion");
        this.handsState = HandsState.singleton();

        reloadConfig();
    }

    public void reloadConfig() {
        // This function reloads, and calcuates config based on the settings currently in Config. It does not trigger a reload of the config from file.

        // Use the chosen output.
        String chosenOutput = Config.singleton().getGroup("output").getItem("device").get();
        this.selectOutput(chosenOutput);

        // Set up maxChange.
        Group dataCleaning = Config.singleton().getGroup("dataCleaning");
        this.maxChange = Integer.parseInt(dataCleaning.getItem("maxChange").get());


        // Get configured multipliers.
        Group axisOrientation = Config.singleton().getGroup("axisOrientation");
        int configuredXMultiplier = Integer.parseInt(axisOrientation.getItem("xMultiplier").get());
        int configuredYMultiplier = Integer.parseInt(axisOrientation.getItem("yMultiplier").get());
        int configuredZMultiplier = Integer.parseInt(axisOrientation.getItem("zMultiplier").get());
        this.zMultiplier = configuredZMultiplier;


        // Get the total desktop resolution.
        if (this.debug.getLevel() >= 2) {
            this.getOutput().info();
        }

        Dimension desktopResolution = this.getOutput().getDesktopResolution();
        this.desktopWidth = desktopResolution.width;
        this.desktopHeight = desktopResolution.height;
        double desktopAspectRatio = this.desktopWidth/this.desktopHeight;

        // Set initial locaiton for touchPad based zoning.
        float defaultX = this.desktopWidth /3;
        float defaultY = this.desktopHeight /3;
        this.touchPadX = (int) Math.round(defaultX);
        this.touchPadY = (int) Math.round(defaultY);

        // Figure out how to best fit the desktop into the physical space.
        // TODO This could be abstracted out into testable code.
        Group physicalBoundaries = Config.singleton().getGroup("physicalBoundaries");
        int pX = Integer.parseInt(physicalBoundaries.getItem("x").get());
        int pXDiff = pX * 2;
        int pYMin = Integer.parseInt(physicalBoundaries.getItem("yMin").get());
        int pYMax = Integer.parseInt(physicalBoundaries.getItem("yMax").get());
        int pYDiff = pYMax - pYMin;
        double physicalAspectRatio = pXDiff / pYDiff;

        this.xOffset = pX;
        this.yOffset = pYMin * -1;

        this.zoneMode = Config.singleton().getItem("zoneMode").get();
        if (this.zoneMode == "touchPad") {
            this.debug.out(1, "touchPad zone mode only needs simple multipliers.");
            this.xMultiplier = configuredXMultiplier;
            this.yMultiplier = configuredYMultiplier;
        } else {
            if (desktopAspectRatio > physicalAspectRatio) { // desktop is wider
                this.debug.out(1, "Desktop is wider than the cone. Optimising the usable cone for that.");
                this.yMultiplier = configuredYMultiplier * (this.desktopHeight/pYDiff);
                this.xMultiplier = configuredXMultiplier * (this.desktopHeight/pYDiff);
            } else { // desktop is narrower
                this.debug.out(1, "The cone is wider than the desktop. Optimising the usable cone for that.");
                this.xMultiplier = configuredXMultiplier * (this.desktopWidth/pXDiff);
                this.yMultiplier = configuredYMultiplier * (this.desktopWidth/pXDiff);
            }
        }

        this.debug.out(1, "xMultiplier: " + String.valueOf(this.xMultiplier));
        this.debug.out(1, "yMultiplier: " + String.valueOf(this.yMultiplier));
        this.debug.out(1, "zMultiplier: " + String.valueOf(this.zMultiplier));


        // Get relative sensitivity.
        this.relativeSensitivity = Double.parseDouble(Config.singleton().getItem("relativeSensitivity").get());


        // Load touchpad mode config.
        Group touchPadConfig = Config.singleton().getGroup("touchPad");
        this.touchPadInputMultiplier = Double.parseDouble(touchPadConfig.getItem("inputMultiplier").get());
        this.touchPadOutputMultiplier = Double.parseDouble(touchPadConfig.getItem("outputMultiplier").get());
        this.touchPadAcceleration = Double.parseDouble(touchPadConfig.getItem("acceleration").get());
        this.touchPadMaxSpeed = Double.parseDouble(touchPadConfig.getItem("maxSpeed").get());


        // Load click config.
        Group click = Config.singleton().getGroup("click");
        this.rewindCursorTime = Integer.parseInt(click.getItem("rewindCursorTime").get());
        this.repeatRewindCursorTime = Integer.parseInt(click.getItem("repeatRewindCursorTime").get());
        int cursorHistorySize = Integer.parseInt(click.getItem("historySize").get());
        this.historyX = new History(cursorHistorySize, 0);
        this.historyY = new History(cursorHistorySize, 0);


        // Load scroll config.
        Group scrollConfig = Config.singleton().getGroup("scroll");
        this.scrollInputMultiplier = Double.parseDouble(scrollConfig.getItem("inputMultiplier").get());
        this.scrollOutputMultiplier = Double.parseDouble(scrollConfig.getItem("outputMultiplier").get());
        this.scrollAcceleration = Double.parseDouble(scrollConfig.getItem("acceleration").get());
        this.scrollMaxSpeed = Double.parseDouble(scrollConfig.getItem("maxSpeed").get());
        this.rewindScrollTime = Integer.parseInt(scrollConfig.getItem("rewindScrollTime").get());
        int scrollHistorySize = Integer.parseInt(scrollConfig.getItem("historySize").get());
        this.historyScroll = new History(scrollHistorySize, 0);
    }

    public double getZMultiplier() {
        return this.zMultiplier;
    }

    public void selectOutput(String chosenOutput) {
        switch (chosenOutput) {
            case "AWT":
                setOutput(new OutputProtection(new AWTOutput()));
                break;
            case "Null":
                setOutput(new OutputProtection(new NullOutput()));
                break;
            case "VNC":
                setOutput(new OutputProtection(new VNCOutput(chosenOutput)));
                break;
        }
    }

    public void setOutput(OutputProtection output) {
        this.output = output;
    }

    public OutputProtection getOutput() {
        return this.output;
    }

    private void moveMouse(int x, int y) {
        if (x < 0) x = 0;
        if (x > this.desktopWidth) x = this.desktopWidth-1;

        if (y < 0) y = 0;
        if (y > this.desktopHeight) y = this.desktopHeight-1;

        this.historyX.set(x);
        this.historyY.set(y);

        this.output.setPosition(x, y);
    }

    public void rewindCursorPosition() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        long nowMillis = now.getTime();
        long rewindTime = 0;
        long timeSinceLastRewind = nowMillis - this.lastCursorRewind;

        if (timeSinceLastRewind < this.repeatRewindCursorTime) {
            rewindTime = this.lastCursorRewind;
        } else {
            rewindTime = nowMillis - this.rewindCursorTime;
        }

        int earlierX = (int) Math.round(this.historyX.get(rewindTime));
        int earlierY = (int) Math.round(this.historyY.get(rewindTime));

        this.debug.out(1, "Rewind cursor position by " + this.rewindCursorTime + " milliseconds to around " + String.valueOf(rewindTime) + ", " + String.valueOf(earlierX) + "," + String.valueOf(earlierY) + " for mouse down/up event.");

        // Only apply when we have real values.
        if (earlierX != 0 && earlierY != 0) { // TODO Use a better test of not being in the start-up state.
            // Move the mouse cursor.
            this.output.setPosition(earlierX, earlierY);

            // Remove any accumulating movement.
            this.diffRemainderX = 0;
            this.diffRemainderY = 0;

            // Update the persistent position so that any changes happen from here.
            this.touchPadX = earlierX;
            this.touchPadY = earlierY;

            this.lastCursorRewind = rewindTime;
        }
    }

    public void rewindScroll() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        long nowMillis = now.getTime();
        long rewindTime = nowMillis - this.rewindScrollTime;

        int earlierScroll = (int) Math.round(this.historyScroll.getSumFrom(rewindTime));

        this.debug.out(1, "Rewind scroll position by " + this.rewindScrollTime + " milliseconds to around " + String.valueOf(rewindTime) + ", " + String.valueOf(earlierScroll) + " for mouse down/up event.");

        // Discard any lingering movement since it is now irrelevant.
        this.diffScrollRemainderX = 0;
        this.diffScrollRemainderY = 0;

        // Perform the action.
        this.output.scroll(earlierScroll * -1);
    }

    private long getNow() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return now.getTime();
    }

    public void setCursorLock() {
        long now = getNow();
        this.cursorLock = now + this.cursorLockTime;
        this.debug.out(1, "Cursor locked at " + String.valueOf(now) + " for " + String.valueOf(this.cursorLockTime) + " milliseconds until " + String.valueOf(this.cursorLock) + ".");
    }

    public Boolean cusorIsLocked() {
        if (this.cursorLock > 0) {
            return (getNow() < this.cursorLock);
        } else {
            return false;
        }
    }

    public void releaseCursorLock() {
        // Figure out what feedback we are going to give.
        if (this.cursorLock > 0) {
            if (cusorIsLocked()) {
                this.debug.out(1, "Cursor explicitly unlocked.");
            } else {
                this.debug.out(1, "Cursor would have been explicitly unlocked, but the cursorLockTime had already expired.");
            }
        } else {
            this.debug.out(1, "Cursor would have been explicitly unlocked. But was already unlocked.");
        }

        // Do the actual work.
        this.cursorLock = 0;
    }

    private int coordToDesktopIntX(double xCoord) {
        return (int) Math.round((xCoord + this.xOffset) * this.xMultiplier);
    }

    private int coordToDesktopIntY(double yCoord) {
        return this.desktopHeight - (int) Math.round((yCoord + this.yOffset) * this.yMultiplier);
    }

    public void discardOldPosition() {
        this.shouldDiscardOldPosition = true;
    }

    public void moveMouseTouchPadFromCoordinates() {
        moveMouseTouchPadFromCoordinates(this.movingMeanX.get(), this.movingMeanY.get());
    }

    private void moveMouseTouchPadFromCoordinates(double xCoord, double yCoord) {
        // Calculate how far the hand has moved since the last iteration.
        double xCoordDiff = 0;
        double yCoordDiff = 0;

        if (!this.shouldDiscardOldPosition) {
            xCoordDiff = xCoord - this.lastAbsoluteX + this.diffRemainderX;
            yCoordDiff = yCoord - this.lastAbsoluteY + this.diffRemainderY;
        } else {
            this.shouldDiscardOldPosition = false;
        }

        // Calculate the distance we have moved regardless of direction.
        double angularDiff = Math.pow((Math.pow(xCoordDiff, 2) + Math.pow(yCoordDiff, 2)), 0.5);

        // Apply maxChange.
        if (angularDiff > this.maxChange) {
            this.debug.out(1, "maxChange has been hit (" + String.valueOf(angularDiff) + " > " + String.valueOf(maxChange) + "), and this frame has been filtered. If this movement was legitimate, consider increasing the maxChange value in the config.");
            this.lastAbsoluteX = xCoord;
            this.lastAbsoluteY = yCoord;
            return;
        }

        // Get time component.
        long previousFrameAge = this.handsState.getPreviousFrameAge();

        // Calculate our acceleration.
        double accelerationThreshold = 1;
        double accelerationMultiplier = accelerationThreshold;
        double angularSpeed = angularDiff * previousFrameAge / 1000 * this.touchPadAcceleration;
        if (angularSpeed > accelerationThreshold) {
            if (angularSpeed > this.touchPadMaxSpeed) {
                this.debug.out(2, "Limited touchPad speed (" + String.valueOf(angularSpeed) + ") to maxSpeed (" + String.valueOf(this.touchPadMaxSpeed) + ")");
                angularSpeed = this.touchPadMaxSpeed;
            }
            accelerationMultiplier = angularSpeed;
        }

        // Bring everything together to calcuate how far we should move the cursor.
        double xInput = xCoordDiff * this.touchPadInputMultiplier;
        int diffX = (int) Math.round((xInput * accelerationMultiplier) * this.touchPadOutputMultiplier);
        double yInput = yCoordDiff * this.touchPadInputMultiplier;
        int diffY = (int) Math.round((yInput * accelerationMultiplier) * this.touchPadOutputMultiplier);

        // Apply the changes.
        this.touchPadX = this.touchPadX + (diffX * (int)Math.round(this.xMultiplier));
        this.touchPadY = this.touchPadY - (diffY * (int)Math.round(this.yMultiplier));

        // Carry over anything that happened, but didn't result in a movement. This means that we can make use of the finer movements without having to move the acceleration and multipliers to extremes.
        this.diffRemainderX = (diffX == 0)?xCoordDiff:0;
        this.diffRemainderY = (diffY == 0)?yCoordDiff:0;

        // Not catching OOB here makes the mouse feel sticky on the edges.
        if (this.touchPadX < 0) this.touchPadX = 0;
        if (this.touchPadY < 0) this.touchPadY = 0;
        if (this.touchPadX > this.desktopWidth) this.touchPadX = this.desktopWidth;
        if (this.touchPadY > this.desktopHeight) this.touchPadY = this.desktopHeight;

        // Track where we are now so that differences make sense on the next round.
        this.lastAbsoluteX = xCoord;
        this.lastAbsoluteY = yCoord;

        moveMouse(this.touchPadX, this.touchPadY);
    }

    public void scrollFromCoordinates() {
        scrollFromCoordinates(this.movingMeanX.get(), this.movingMeanY.get());
    }

    private void scrollFromCoordinates(double xCoord, double yCoord) {
        // Reset touchPad motion since anything from before the scroll will be meaningless.
        this.diffRemainderX = 0;
        this.diffRemainderY = 0;

        // Calculate how far the hand has moved since the last iteration.
        double xCoordDiff = 0;
        double yCoordDiff = 0;

        if (!this.shouldDiscardOldPosition) {
            xCoordDiff = xCoord - this.lastAbsoluteX + this.diffScrollRemainderX;
            yCoordDiff = yCoord - this.lastAbsoluteY + this.diffScrollRemainderY;
        } else {
            this.shouldDiscardOldPosition = false;
        }

        // Calculate the distance we have moved regardless of direction.
        //double angularDiff = Math.pow((Math.pow(xCoordDiff, 2) + Math.pow(yCoordDiff, 2)), 0.5);

        // Apply maxChange.
        if (yCoordDiff > this.maxChange) {
            this.debug.out(1, "maxChange has been hit (" + String.valueOf(yCoordDiff) + " > " + String.valueOf(maxChange) + "), and this frame has been filtered. If this movement was legitimate, consider increasing the maxChange value in the config.");
            this.lastAbsoluteX = xCoord;
            this.lastAbsoluteY = yCoord;
            return;
        }

        // Get time component.
        long previousFrameAge = this.handsState.getPreviousFrameAge();

        // Calculate our acceleration.
        double accelerationThreshold = 1;
        double accelerationMultiplier = accelerationThreshold;
        double ySpeed = Math.abs(yCoordDiff * previousFrameAge / 1000 * this.scrollAcceleration);
        if (ySpeed > accelerationThreshold) {
            if (ySpeed > this.scrollMaxSpeed) {
                this.debug.out(1, "Limited scroll speed (" + String.valueOf(ySpeed) + ") to maxSpeed (" + String.valueOf(this.scrollMaxSpeed) + ")");
                ySpeed = this.scrollMaxSpeed;
            }
            accelerationMultiplier = ySpeed;
        }

        // Bring everything together to calcuate how far we should move the cursor.
        double xInput = xCoordDiff * this.scrollInputMultiplier * (int)Math.round(this.xMultiplier);
        double yInput = yCoordDiff * this.scrollInputMultiplier * (int)Math.round(this.yMultiplier);

        int diffX = 0;
        int diffY = 0;

        if (this.scrollAcceleration > 1) {
            diffX = (int) Math.round(xInput * accelerationMultiplier * this.scrollOutputMultiplier);
            diffY = (int) Math.round(yInput * accelerationMultiplier * this.scrollOutputMultiplier);
        } else {
            diffX = (int) Math.round(xInput * this.scrollOutputMultiplier);
            diffY = (int) Math.round(yInput * this.scrollOutputMultiplier);
        }

        // Record changes.
        this.historyScroll.set(diffY);

        // Apply the changes.
        this.output.scroll(diffY);

        // Carry over anything that happened, but didn't result in a movement. This means that we can make use of the finer movements without having to move the acceleration and multipliers to extremes.
        this.diffScrollRemainderX = (diffX == 0)?xCoordDiff:0;
        this.diffScrollRemainderY = (diffY == 0)?yCoordDiff:0;

        // Track where we are now so that differences make sense on the next round.
        this.lastAbsoluteX = xCoord;
        this.lastAbsoluteY = yCoord;
    }

    public void touchPadNone() {
        touchPadNone(this.movingMeanX.get(), this.movingMeanY.get());
    }

    private void touchPadNone(double xCoord, double yCoord) {
        // This is needed, because otherwise we end up back where we started every time we lift and re-apply.
        this.lastAbsoluteX = xCoord;
        this.lastAbsoluteY = yCoord;
    }

    public void moveMouseAbsoluteFromCoordinates() {
        moveMouseAbsoluteFromCoordinates(this.movingMeanX.get(), this.movingMeanY.get());
    }

    private void moveMouseAbsoluteFromCoordinates(double xCoord, double yCoord) {
        int calculatedX = coordToDesktopIntX(xCoord);
        int calculatedY = coordToDesktopIntY(yCoord);

        this.lastAbsoluteX = xCoord;
        this.lastAbsoluteY = yCoord;

        moveMouse(calculatedX, calculatedY);
    }

    public void moveMouseRelativeFromCoordinates() {
        moveMouseRelativeFromCoordinates(this.movingMeanX.get(), this.movingMeanY.get());
    }

    private void moveMouseRelativeFromCoordinates(double xCoord, double yCoord) {
        double xDiff = xCoord - this.lastAbsoluteX;
        double yDiff = yCoord - this.lastAbsoluteY;

        double changeX = this.lastAbsoluteX + (xDiff * this.relativeSensitivity);
        double changeY = this.lastAbsoluteY + (yDiff * this.relativeSensitivity);
        int calculatedX = coordToDesktopIntX(changeX);
        int calculatedY = coordToDesktopIntY(changeY);

        moveMouse(calculatedX, calculatedY);
    }

    public void updateMovingMeans(String zone, double handZ, HandSummary[] handSummaries, HashMap<String, Zone> zones) {
        // TODO This has been changed from private to public to aid in the abstraction. But maybe this should belong somewhere else, or the code that calls it should be moved in here.
        this.movingMeanX.set(handSummaries[0].getHandX());
        this.movingMeanY.set(handSummaries[0].getHandY());
        this.movingMeanX.resize(zones.get(zone).getMovingMeanWidth(handZ));
        this.movingMeanY.resize(zones.get(zone).getMovingMeanWidth(handZ));
    }
}
