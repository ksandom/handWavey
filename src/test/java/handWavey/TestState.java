package handWavey;

import handWavey.HandsState;
import handWavey.*;
import config.Config;
import config.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;


// TODO We need to handle the case of the hand no longer being seen while a mouse down state is present. The mouse should be released so that the desktop returns to being usable.


class TestState {
    HandsState handsState;
    private float pi = (float)3.1415926536;

    @BeforeEach
    void setUp() {
        Config.setSingletonFilename("handWaveyConfigTest.yml");
        Config config = Config.singleton();

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
            "-1",
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
        
        handSummaryManager.newItem(
            "zoneMode",
            "touchScreen",
            "(touchscreen, touchPad). What type of device the zones approximate. The names are not an exact comparison, but should at least give an idea of how they work.");

        handSummaryManager.newItem(
            "zoneBuffer",
            "30",
            "Once a zone is entered, how far beyond the threshold must the hand retreat before the zone is considered exited?");

        Group zones = handSummaryManager.newGroup("zones");
        Group touchScreen = zones.newGroup("touchScreen");
        Group zoneNone = touchScreen.newGroup("zoneNone");
        // None currently doesn't require any config. Its group is here solely for completeness.
        
        Group absolute = touchScreen.newGroup("absolute");
        absolute.newItem(
            "threshold",
            "-150",
            "Z greater than this value denotes the beginning of the absolute zone.");
        absolute.newItem(
            "movingMeanBegin",
            "4",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        absolute.newItem(
            "movingMeanEnd",
            "20",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        
        Group relative = touchScreen.newGroup("relative");
        relative.newItem(
            "threshold",
            "50",
            "Z greater than this value denotes the beginning of the relative zone.");
        relative.newItem(
            "movingMeanBegin",
            "20",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        relative.newItem(
            "movingMeanEnd",
            "40",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        
        Group action = touchScreen.newGroup("action");
        action.newItem(
            "threshold",
            "100",
            "Z greater than this value denotes the beginning of the action zone.");
        action.newItem(
            "movingMeanBegin",
            "20",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        action.newItem(
            "movingMeanEnd",
            "20",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        

        Group touchPad = zones.newGroup("touchPad");
        Group zoneTPNone = touchPad.newGroup("zoneNone");
        // None currently doesn't require any config. Its group is here solely for completeness.
        
        Group active = touchPad.newGroup("active");
        active.newItem(
            "threshold",
            "-120",
            "Z greater than this value denotes the beginning of the active zone.");
        active.newItem(
            "movingMeanBegin",
            "4",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        active.newItem(
            "movingMeanEnd",
            "20",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        
        Group tpAction = touchPad.newGroup("action");
        tpAction.newItem(
            "threshold",
            "80",
            "Z greater than this value denotes the beginning of the action zone.");
        tpAction.newItem(
            "movingMeanBegin",
            "20",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");
        tpAction.newItem(
            "movingMeanEnd",
            "20",
            "int 1-4096. A moving mean is applied to the data stream to make it more steady. This variable defined how many samples are used in the mean. More == smoother, but less responsive. It's currently possible to go up to 4096, although 50 is probably a lot. 1 effectively == disabled. The \"begin\" portion when your hand enters the zone.");

        handSummaryManager.newItem(
            "relativeSensitivity",
            "0.1",
            "How sensitive is the relative zone compared to the absolute zone? Decimal between 0 and 1.");
        
        Group gestureConfig = config.newGroup("gestureConfig");
        Group primaryHand = gestureConfig.newGroup("primaryHand");
        primaryHand.newItem(
            "rotationSegments",
            "4",
            "When you rotate your hand; it enters different segments. Increasing the number of segments increases the number of things you can do with your hand. Decreasing the number of segments makes it easier to be precise. Remember that some segments are hard for a human hand to reach, so you need to keep that in mind when choosing this number. It is expected that some segments will be unused for this reason. Don't hurt yourself.");
        primaryHand.newItem(
            "rotationOffset",
            "0",
            "In radians. Adjust where the segments are slightly to cater to your hand's natural bias.");
        Group secondaryHand = gestureConfig.newGroup("secondaryHand");
        secondaryHand.newItem(
            "rotationSegments",
            "4",
            "When you rotate your hand; it enters different segments. Increasing the number of segments increases the number of things you can do with your hand. Decreasing the number of segments makes it easier to be precise. Remember that some segments are hard for a human hand to reach, so you need to keep that in mind when choosing this number. It is expected that some segments will be unused for this reason. Don't hurt yourself.");
        secondaryHand.newItem(
            "rotationOffset",
            "0",
            "In radians. Adjust where the segments are slightly to cater to your hand's natural bias.");
            
        this.handsState = new HandsState();
    }

    @AfterEach
    void destroy() {
        this.handsState = null;
    }

    @Test
    public void testZones() {
        /* In this test we test a sequence of getZone()s by changing the z axis, and then testing the results.
        
        * We should get different zones depending on where are are on the Z axis.
        * We should get mouse down and up events when entering and exiting the action zone.
        */
        
        this.handsState.figureOutMouseButtons();
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("none", this.handsState.getZone(-151));
        
        this.handsState.figureOutMouseButtons();
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("none", this.handsState.getZone(-150));
        assertEquals(false, this.handsState.zoneIsNew());
        
        this.handsState.figureOutMouseButtons();
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("absolute", this.handsState.getZone(-149));
        assertEquals(true, this.handsState.zoneIsNew());
        assertEquals("none", this.handsState.getOldZone());
        
        this.handsState.figureOutMouseButtons();
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("absolute", this.handsState.getZone(10));
        assertEquals(false, this.handsState.zoneIsNew());
        assertEquals("none", this.handsState.getOldZone());
        
        this.handsState.figureOutMouseButtons();
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("absolute", this.handsState.getZone(50));
        assertEquals(false, this.handsState.zoneIsNew());
        assertEquals("none", this.handsState.getOldZone());
        
        this.handsState.figureOutMouseButtons();
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("relative", this.handsState.getZone(51));
        assertEquals(true, this.handsState.zoneIsNew());
        assertEquals("absolute", this.handsState.getOldZone());
        
        this.handsState.figureOutMouseButtons();
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("relative", this.handsState.getZone(100));
        assertEquals(false, this.handsState.zoneIsNew());
        assertEquals("absolute", this.handsState.getOldZone());
        
        this.handsState.figureOutMouseButtons();
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("action", this.handsState.getZone(101));
        assertEquals(true, this.handsState.zoneIsNew());
        assertEquals("relative", this.handsState.getOldZone());
        
        this.handsState.figureOutMouseButtons();
        assertEquals(true, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("action", this.handsState.getZone(102));
        assertEquals(false, this.handsState.zoneIsNew());
        assertEquals("relative", this.handsState.getOldZone());
        
        this.handsState.figureOutMouseButtons();
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("action", this.handsState.getZone(103));
        assertEquals(false, this.handsState.zoneIsNew());
        assertEquals("relative", this.handsState.getOldZone());
        
        this.handsState.figureOutMouseButtons();
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("action", this.handsState.getZone(99));
        assertEquals(false, this.handsState.zoneIsNew());
        assertEquals("relative", this.handsState.getOldZone());
        
        this.handsState.figureOutMouseButtons();
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("relative", this.handsState.getZone(70));
        assertEquals(true, this.handsState.zoneIsNew());
        assertEquals("action", this.handsState.getOldZone());
        
        this.handsState.figureOutMouseButtons();
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(true, this.handsState.shouldMouseUp());
        
        assertEquals("relative", this.handsState.getZone(70));
        assertEquals(false, this.handsState.zoneIsNew());
        assertEquals("action", this.handsState.getOldZone());
        
        this.handsState.figureOutMouseButtons();
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
    }

    @Test
    public void testEraticZones() {
        /* This is a clone of the testZones test, but with slightly more eratic data.
        
        * There should be no broken assumptions from skipping a zone. It should simply do the right thing based on where we are now.
        */
        
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("none", this.handsState.getZone(-151));
        
        this.handsState.figureOutMouseButtons();
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("absolute", this.handsState.getZone(-149));
        
        this.handsState.figureOutMouseButtons();
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("action", this.handsState.getZone(101));
        
        this.handsState.figureOutMouseButtons();
        assertEquals(true, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("action", this.handsState.getZone(99));
        
        this.handsState.figureOutMouseButtons();
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("relative", this.handsState.getZone(70));
        
        this.handsState.figureOutMouseButtons();
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(true, this.handsState.shouldMouseUp());
        
        assertEquals("relative", this.handsState.getZone(70));
        
        this.handsState.figureOutMouseButtons();
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
    }
    
    @Test
    public void testHandClosedGesture() {
        /* Here we want to test that the states work correctly with the hand being open and closed.
        */
        
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("none", this.handsState.getZone(-151));
        this.handsState.setHandClosed(false);
        this.handsState.figureOutMouseButtons();
        
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("absolute", this.handsState.getZone(-149));
        this.handsState.setHandClosed(true);
        this.handsState.figureOutMouseButtons();
        
        assertEquals(true, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("absolute", this.handsState.getZone(-149));
        this.handsState.setHandClosed(true);
        this.handsState.figureOutMouseButtons();
        
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("absolute", this.handsState.getZone(-149));
        this.handsState.setHandClosed(false);
        this.handsState.figureOutMouseButtons();
        
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(true, this.handsState.shouldMouseUp());
        
        assertEquals("absolute", this.handsState.getZone(-149));
        this.handsState.setHandClosed(false);
        this.handsState.figureOutMouseButtons();
        
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
    }
    
    @Test
    public void testHandClosedGestureWithZones() {
        /* Here we want to test that the states work correctly with the hand being open and closed while moving through zones.
        */
        
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("none", this.handsState.getZone(-151));
        this.handsState.setHandClosed(false);
        this.handsState.figureOutMouseButtons();
        
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("absolute", this.handsState.getZone(-149));
        this.handsState.setHandClosed(true);
        this.handsState.figureOutMouseButtons();
        
        assertEquals(true, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("action", this.handsState.getZone(101));
        this.handsState.setHandClosed(true);
        this.handsState.figureOutMouseButtons();
        
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("action", this.handsState.getZone(99));
        this.handsState.setHandClosed(true);
        this.handsState.figureOutMouseButtons();
        
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("relative", this.handsState.getZone(70));
        this.handsState.setHandClosed(true);
        this.handsState.figureOutMouseButtons();
        
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
        
        assertEquals("relative", this.handsState.getZone(70));
        this.handsState.setHandClosed(false);
        this.handsState.figureOutMouseButtons();
        
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(true, this.handsState.shouldMouseUp());
        
        assertEquals("relative", this.handsState.getZone(70));
        this.handsState.setHandClosed(false);
        this.handsState.figureOutMouseButtons();
        
        assertEquals(false, this.handsState.shouldMouseDown());
        assertEquals(false, this.handsState.shouldMouseUp());
    }
    
    @Test
    public void testHandSegments() {
        Boolean primary = true;
        Boolean secondary = false;
        Boolean left = true;
        Boolean right = false;
        
        double handTop = 0;
        double handRight = pi*-0.5;
        double handBottom = pi;
        double handLeft = pi*0.5;
        
        assertEquals(0, this.handsState.getHandSegment(handTop, primary, right));
        assertEquals(1, this.handsState.getHandSegment(handRight, primary, right));
        assertEquals(2, this.handsState.getHandSegment(handBottom, primary, right));
        assertEquals(3, this.handsState.getHandSegment(pi*-1.5, primary, right)); // Alternative value;
        assertEquals(3, this.handsState.getHandSegment(handLeft, primary, right));
        
        assertEquals(0, this.handsState.getHandSegment(handTop, primary, left));
        assertEquals(3, this.handsState.getHandSegment(handRight, primary, left));
        assertEquals(2, this.handsState.getHandSegment(handBottom, primary, left));
        assertEquals(2, this.handsState.getHandSegment(pi*-1, primary, left)); // Alternative value.
        assertEquals(1, this.handsState.getHandSegment(handLeft, primary, left));
    }
    
    @Test
    public void testHandSegmentButtons() {
        Boolean primary = true;
        Boolean secondary = false;
        Boolean left = true;
        Boolean right = false;
        
        double handTop = 0;
        double handRight = pi*-0.5;
        double handBottom = pi;
        double handLeft = pi*0.5;
        
        this.handsState.derivePrimaryHandSegment(handTop, left);
        assertEquals("left", this.handsState.whichMouseButton());
        
        this.handsState.derivePrimaryHandSegment(handLeft, left);
        assertEquals("right", this.handsState.whichMouseButton());
        
        this.handsState.derivePrimaryHandSegment(handBottom, left);
        assertEquals("middle", this.handsState.whichMouseButton());
        
        this.handsState.derivePrimaryHandSegment(handRight, left);
        assertEquals("left", this.handsState.whichMouseButton());
        
        
        this.handsState.derivePrimaryHandSegment(handTop, right);
        assertEquals("left", this.handsState.whichMouseButton());
        
        this.handsState.derivePrimaryHandSegment(handLeft, right);
        assertEquals("left", this.handsState.whichMouseButton());
        
        this.handsState.derivePrimaryHandSegment(handBottom, right);
        assertEquals("middle", this.handsState.whichMouseButton());
        
        this.handsState.derivePrimaryHandSegment(handRight, right);
        assertEquals("right", this.handsState.whichMouseButton());
    }
}
