package handWavey;

// import handWavey.*;
import config.*;
import debug.Debug;
import java.awt.Dimension;
import mouseAndKeyboardOutput.*;

public class HandWaveyManager {
    private HandSummary[] handSummaries;
    private Debug debug;
    private GenericOutput output;
    private int desktopWidth = 0;
    private int desktopHeight = 0;
    
    private int xOffset = 0;
    private int yOffset = 0;
    private double xMultiplier = 1;
    private double yMultiplier = 1;
    
    public HandWaveyManager() {
        Config.setSingletonFilename("handWavey.yml");
        Config config = Config.singleton();
        
        
        Item configFormatVersion = config.newItem(
            "configFormatVersion",
            "2021-11-26",
            "This number is incremented by the programmer whenever existing config items get changed (eg new description, default value etc) so that conflicts can be resolved.");
        configFormatVersion.set("2021-11-26"); // Update it here.

        Group ultraMotion = config.newGroup("ultraMotion");
        ultraMotion.newItem(
            "maxHands",
            "2",
            "Maximum number of hands to track. Anything more than this setting will be discarded, and assumptions can be made faster, so it will run faster. The most recent hands above the threshold are the ones to be discarded.");
        ultraMotion.newItem(
            "openThreshold",
            "1.7",
            "Float: When the last bone of the middle finger is less than this angle, the hand is assumed to be open.");
        ultraMotion.newItem(
            "debugLevel",
            "1",
            "Int: Sensible numbers are 0-5, where 0 is no debugging, and 5  is probably more detail than you'll ever want.");

        Group handSummaryManager = config.newGroup("handSummaryManager");
        handSummaryManager.newItem(
            "debugLevel",
            "2",
            "Int: Sensible numbers are 0-5, where 0 is no debugging, and 5  is probably more detail than you'll ever want.");
        handSummaryManager.newItem(
            "rangeMethod",
            "manual",
            "How the range of possible hand positions is configured. Current possible values are: manual.");
        
        Group axisOrientation = handSummaryManager.newGroup("axisOrientation");
        axisOrientation.newItem(
            "xMultiplier",
            "1",
            "Set this to -1 when you need to invert X (side to side). You'll typicall only need to do this if your device is upside down. On newer LeapSDK versions, this may become obsolete.");
        axisOrientation.newItem(
            "yMultiplier",
            "1",
            "Set this to -1 when you need to invert Y (up and down). You'll typicall only need to do this if your device is upside down. On newer LeapSDK versions, this may become obsolete.");
        axisOrientation.newItem(
            "zMultiplier",
            "1",
            "Set this to -1 when you need to invert Z (how far away from you your hand goes). UltraMotion takes care of this for you. So I can't currently think of a use-case for it, but am including it for completness.");
        
        Group physicalBoundaries = handSummaryManager.newGroup("physicalBoundaries");
        physicalBoundaries.newItem(
            "x",
            "100",
            "+ and - this value horizontally from the center of the visible cone above the device.");
        physicalBoundaries.newItem(
            "yMin",
            "200",
            "Minimum value of height above the device.");
        physicalBoundaries.newItem(
            "yMax",
            "400",
            "Maximum value of height above the device.");
        physicalBoundaries.newItem(
            "z",
            "120",
            "+ and - this value in depth from the center of the visible cone above the device.");
        
        Group zoneThresholds = handSummaryManager.newGroup("zoneThresholds");
        physicalBoundaries.newItem(
            "absolute",
            "50",
            "Less than ___ denotes the beginning of the absolute zone.");
        physicalBoundaries.newItem(
            "relative",
            "-50",
            "Less than ___ denotes the beginning of the relative zone.");
        physicalBoundaries.newItem(
            "action",
            "-100",
            "Less than ___ denotes the beginning of the action zone.");

        handSummaryManager.newItem(
            "relativeSensitivity",
            "0.1",
            "How sensitive is the relative zone compared to the absolute zone? Decimal between 0 and 1.");
        
        this.output = new GenericOutput();
        
        reloadConfig();
    }
    
    public void reloadConfig() {
        // This function reloads, and calcuates config based on the settings currently in Config. It does not trigger a reload of the config from file.
        Group handSummaryManager = Config.singleton().getGroup("handSummaryManager");
        
        
        // Set up the debugging.
        int debugLevel = Integer.parseInt(handSummaryManager.getItem("debugLevel").get());
        this.debug = new Debug(debugLevel, "HandWaveyManager");
        
        
        // Get configured multipliers.
        Group axisOrientation = handSummaryManager.getGroup("axisOrientation");
        int configuredXMultiplier = Integer.parseInt(axisOrientation.getItem("xMultiplier").get());
        int configuredYMultiplier = Integer.parseInt(axisOrientation.getItem("yMultiplier").get());
        int configuredZMultiplier = Integer.parseInt(axisOrientation.getItem("zMultiplier").get());
        
        
        // Get the total desktop resolution.
        this.output.info();
        Dimension desktopResolution = this.output.getDesktopResolution();
        this.desktopWidth = desktopResolution.width;
        this.desktopHeight = desktopResolution.height;
        double desktopAspectRatio = this.desktopWidth/this.desktopHeight;
        
        
        // Figure out how to best fit the desktop into the physical space.
        // TODO This could be abstracted out into testable code.
        Group physicalBoundaries = handSummaryManager.getGroup("physicalBoundaries");
        int pX = Integer.parseInt(physicalBoundaries.getItem("x").get());
        int pXDiff = pX * 2;
        int pYMin = Integer.parseInt(physicalBoundaries.getItem("yMin").get());
        int pYMax = Integer.parseInt(physicalBoundaries.getItem("yMax").get());
        int pYDiff = pYMax - pYMin;
        double physicalAspectRatio = pXDiff / pYDiff;
        
        this.xOffset = pX;
        this.yOffset = pYMin * -1;
        
        if (desktopAspectRatio > physicalAspectRatio) { // desktop is wider
            this.debug.out(1, "Desktop is wider than the cone. Optimising for that.");
            this.xMultiplier = configuredXMultiplier * (this.desktopWidth/pXDiff);
            this.yMultiplier = configuredYMultiplier * (this.desktopWidth/pXDiff) ;
        } else { // desktop is narrower
            this.debug.out(1, "The cone is wider than the desktop. Optimising for that.");
            this.yMultiplier = configuredYMultiplier * (this.desktopHeight/pYDiff);
            this.xMultiplier = configuredXMultiplier * (this.desktopHeight/pYDiff);
        }
    }
    
    private void moveMouse(int x, int y) {
        if (x < 0) x = 0;
        if (x > this.desktopWidth) x = this.desktopWidth-1;
        
        if (y < 0) y = 0;
        if (y > this.desktopHeight) y = this.desktopHeight-1;
        
        this.output.setPosition(x, y);
    }
    
    private void moveMouseAbsoluteFromCoordinates(double xCoord, double yCoord) {
        int calculatedX = (int) Math.round((xCoord + this.xOffset) * this.xMultiplier);
        int calculatedY = this.desktopHeight - (int) Math.round((yCoord + this.yOffset) * this.yMultiplier);
        
        this.debug.out(2, String.valueOf(xCoord) + " " +
            String.valueOf(yCoord) + " " +
            String.valueOf(calculatedX) + " " +
            String.valueOf(calculatedY) + " m:" +
            String.valueOf(this.xMultiplier) + " " +
            String.valueOf(this.yMultiplier) + " o:" +
            String.valueOf(this.xOffset) + " " +
            String.valueOf(this.yOffset));
        
        moveMouse(calculatedX, calculatedY);
    }
    
    public void sendHandSummaries(HandSummary[] handSummaries) {
        this.handSummaries = handSummaries;
        
        moveMouseAbsoluteFromCoordinates(this.handSummaries[0].getHandX(), this.handSummaries[0].getHandY());
        //this.debug.out(2, this.handSummaries[0].toString());
    }
}
