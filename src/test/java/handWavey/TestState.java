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
    private HandWaveyConfig handWaveyConfig;
    private float pi = (float)3.1415926536;

    @BeforeEach
    void setUp() {
        this.handWaveyConfig = new HandWaveyConfig("unitTest");
        handWaveyConfig.defineGeneralConfig();
        
        Config config = Config.singleton();
        
        // Override some config so that we know that the unit tests will get expected values.
        Group handSummaryManager = config.getGroup("handSummaryManager");
        handSummaryManager.getItem("debugLevel").set("2");
        handSummaryManager.getItem("rangeMethod").set("manual");
        
        Group axisOrientation = handSummaryManager.getGroup("axisOrientation");
        axisOrientation.getItem("xMultiplier").set("1");
        axisOrientation.getItem("yMultiplier").set("1");
        axisOrientation.getItem("zMultiplier").set("-1");
        
        Group physicalBoundaries = handSummaryManager.getGroup("physicalBoundaries");
        physicalBoundaries.getItem("x").set("100");
        physicalBoundaries.getItem("yMin").set("200");
        physicalBoundaries.getItem("yMax").set("400");
        physicalBoundaries.getItem("z").set("120");
        
        handSummaryManager.getItem("zoneMode").set("touchScreen");
        handSummaryManager.getItem("zoneBuffer").set("30");

        Group zones = handSummaryManager.getGroup("zones");
        Group touchScreen = zones.getGroup("touchScreen");
        Group zoneNone = touchScreen.getGroup("zoneNone");
        
        Group absolute = touchScreen.getGroup("absolute");
        absolute.getItem("threshold").set("-150");
        absolute.getItem("movingMeanBegin").set("4");
        absolute.getItem("movingMeanEnd").set("20");
        
        Group relative = touchScreen.getGroup("relative");
        relative.getItem("threshold").set("50");
        relative.getItem("movingMeanBegin").set("20");
        relative.getItem("movingMeanEnd").set("40");
        
        Group action = touchScreen.getGroup("action");
        action.getItem("threshold").set("100");
        action.getItem("movingMeanBegin").set("20");
        action.getItem("movingMeanEnd").set("20");
        

        Group touchPad = zones.getGroup("touchPad");
        Group zoneTPNone = touchPad.getGroup("zoneNone");
        
        Group active = touchPad.getGroup("active");
        active.getItem("threshold").set("-120");
        active.getItem("movingMeanBegin").set("4");
        active.getItem("movingMeanEnd").set("20");
        
        Group tpAction = touchPad.getGroup("action");
        tpAction.getItem("threshold").set("80");
        tpAction.getItem("movingMeanBegin").set("20");
        tpAction.getItem("movingMeanEnd").set("20");

        handSummaryManager.getItem("relativeSensitivity").set("0.1");
        
        
        Group gestureConfig = config.getGroup("gestureConfig");
        Group primaryHand = gestureConfig.getGroup("primaryHand");
        primaryHand.getItem("rotationSegments").set("4");
        primaryHand.getItem("rotationOffset").set("0");
        
        Group secondaryHand = gestureConfig.getGroup("secondaryHand");
        secondaryHand.getItem("rotationSegments").set("4");
        secondaryHand.getItem("rotationOffset").set("0");
            
        this.handsState = new HandsState();
    }

    @AfterEach
    void destroy() {
        this.handsState = null;
        this.handWaveyConfig = null;
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
