// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

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
    HandsState handsState = null;
    private HandWaveyConfig handWaveyConfig = null;
    private float pi = (float)3.1415926536;

    @BeforeEach
    void setUp() {
        this.handWaveyConfig = new HandWaveyConfig("unitTest");
        handWaveyConfig.defineGeneralConfig();

        Config config = Config.singleton();

        // Override some config so that we know that the unit tests will get expected values.
        config.getGroup("debug").getItem("HandWaveyManager").set("2");

        Group axisOrientation = config.getGroup("axisOrientation");
        axisOrientation.getItem("xMultiplier").set("1");
        axisOrientation.getItem("yMultiplier").set("1");
        axisOrientation.getItem("zMultiplier").set("-1");

        Group physicalBoundaries = config.getGroup("physicalBoundaries");
        physicalBoundaries.getItem("x").set("100");
        physicalBoundaries.getItem("yMin").set("200");
        physicalBoundaries.getItem("yMax").set("400");
        physicalBoundaries.getItem("z").set("120");

        config.getItem("zoneMode").set("touchScreen");
        config.getItem("zoneBuffer").set("30");

        Group zones = config.getGroup("zones");
        Group touchScreen = zones.getGroup("touchScreen");
        // Group zoneNone = touchScreen.getGroup("zoneNone");

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
        // Group zoneTPNone = touchPad.getGroup("zoneNone");

        Group active = touchPad.getGroup("active");
        active.getItem("threshold").set("-120");
        active.getItem("movingMeanBegin").set("4");
        active.getItem("movingMeanEnd").set("20");

        Group tpAction = touchPad.getGroup("action");
        tpAction.getItem("threshold").set("80");
        tpAction.getItem("movingMeanBegin").set("20");
        tpAction.getItem("movingMeanEnd").set("20");

        config.getItem("relativeSensitivity").set("0.1");


        Group gestureConfig = config.getGroup("gestureConfig");
        Group primaryHand = gestureConfig.getGroup("primaryHand");
        primaryHand.getItem("rotationSegments").set("4");

        Group secondaryHand = gestureConfig.getGroup("secondaryHand");
        secondaryHand.getItem("rotationSegments").set("4");

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

        assertEquals("none", this.handsState.getZone(-151));
        assertEquals("none", this.handsState.getZone(-150));
        assertEquals("absolute", this.handsState.getZone(-149));
        assertEquals("absolute", this.handsState.getZone(10));
        assertEquals("absolute", this.handsState.getZone(50));
        assertEquals("relative", this.handsState.getZone(51));
        assertEquals("relative", this.handsState.getZone(100));
        assertEquals("action", this.handsState.getZone(101));
        assertEquals("action", this.handsState.getZone(102));
        assertEquals("action", this.handsState.getZone(99));
        assertEquals("relative", this.handsState.getZone(70));
    }

    @Test
    public void testZoneOverride() {
        /* In this test we test a sequence of getZone()s by changing the z axis, and then testing the results. We override the zone to scroll part way through then test again. Then we dissallow the override as if we are using the secondary hand.

        * We should get different zones depending on where are are on the Z axis.
        * We should get scroll regardless of Z while in the override.
        * We should not get the override when it is dissallowed.
        */

        assertEquals("none", this.handsState.getZone(-182));
        assertEquals("absolute", this.handsState.getZone(-149));
        assertEquals("relative", this.handsState.getZone(51));
        assertEquals("action", this.handsState.getZone(101));

        this.handsState.overrideZone("scroll");

        assertEquals("scroll", this.handsState.getZone(-182));
        assertEquals("scroll", this.handsState.getZone(-149));
        assertEquals("scroll", this.handsState.getZone(51));
        assertEquals("scroll", this.handsState.getZone(101));

        assertEquals("none", this.handsState.getZone(-182, false));
        assertEquals("absolute", this.handsState.getZone(-149, false));
        assertEquals("relative", this.handsState.getZone(51, false));
        assertEquals("action", this.handsState.getZone(101, false));

        assertEquals("scroll", this.handsState.getZone(-151));

        this.handsState.releaseZone();

        assertEquals("none", this.handsState.getZone(-182));
        assertEquals("absolute", this.handsState.getZone(-149));
        assertEquals("relative", this.handsState.getZone(51));
        assertEquals("action", this.handsState.getZone(101));

        assertEquals("none", this.handsState.getZone(-182, false));
        assertEquals("absolute", this.handsState.getZone(-149, false));
        assertEquals("relative", this.handsState.getZone(51, false));
        assertEquals("action", this.handsState.getZone(101, false));
}

    @Test
    public void testEraticZones() {
        /* This is a clone of the testZones test, but with slightly more eratic data.

        * There should be no broken assumptions from skipping a zone. It should simply do the right thing based on where we are now.
        */

        assertEquals("none", this.handsState.getZone(-151));
        assertEquals("absolute", this.handsState.getZone(-149));
        assertEquals("action", this.handsState.getZone(101));
        assertEquals("action", this.handsState.getZone(99));
        assertEquals("relative", this.handsState.getZone(70));
        assertEquals("relative", this.handsState.getZone(70));
    }

    @Test
    public void testHandSegments() {
        Boolean primary = true;
        // Boolean secondary = false;
        Boolean left = true;
        Boolean right = false;

        double handTop = 0;
        double handRight = pi * -0.5;
        double handBottom = pi;
        double handLeft = pi * 0.5;

        assertEquals(0, this.handsState.getHandSegment(handTop, primary, right));
        assertEquals(1, this.handsState.getHandSegment(handRight, primary, right));
        assertEquals(2, this.handsState.getHandSegment(handBottom, primary, right));
        assertEquals(3, this.handsState.getHandSegment(pi * -1.5, primary, right)); // Alternative value;
        assertEquals(3, this.handsState.getHandSegment(handLeft, primary, right));

        assertEquals(0, this.handsState.getHandSegment(handTop, primary, left));
        assertEquals(3, this.handsState.getHandSegment(handRight, primary, left));
        assertEquals(2, this.handsState.getHandSegment(handBottom, primary, left));
        assertEquals(2, this.handsState.getHandSegment(pi * -1, primary, left)); // Alternative value.
        assertEquals(1, this.handsState.getHandSegment(handLeft, primary, left));
    }

    @Test
    public void testHandSegmentMerge() {
        Boolean primary = true;
        // Boolean secondary = false;
        Boolean left = true;
        Boolean right = false;

        double handTop = 0;
        double handRight = pi * -0.5;
        double handBottom = pi;
        double handLeft = pi * 0.5;

        // Merge some segments together.
        Group primaryHand = Config.singleton().getGroup("gestureConfig").getGroup("primaryHand");
        primaryHand.getItem("mergeIntoSegment").set("5"); // This is OOB, but I've set it to this to make it obvious that it's working.
        primaryHand.getItem("mergeFrom").set("2");
        primaryHand.getItem("mergeTo").set("3");
        this.handsState = new HandsState();

        assertEquals(0, this.handsState.getHandSegment(handTop, primary, right));
        assertEquals(1, this.handsState.getHandSegment(handRight, primary, right));
        assertEquals(5, this.handsState.getHandSegment(handBottom, primary, right));
        assertEquals(5, this.handsState.getHandSegment(pi * -1.5, primary, right)); // Alternative value;
        assertEquals(5, this.handsState.getHandSegment(handLeft, primary, right));

        assertEquals(0, this.handsState.getHandSegment(handTop, primary, left));
        assertEquals(5, this.handsState.getHandSegment(handRight, primary, left));
        assertEquals(5, this.handsState.getHandSegment(handBottom, primary, left));
        assertEquals(5, this.handsState.getHandSegment(pi * -1, primary, left)); // Alternative value.
        assertEquals(1, this.handsState.getHandSegment(handLeft, primary, left));
    }
}
