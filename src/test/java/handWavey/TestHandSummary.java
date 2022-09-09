// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package handWavey;

import handWavey.HandSummary;
import config.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class TestHandSummary {
    private HandSummary handSummary;
    private HandWaveyConfig handWaveyConfig;
    private Config config;

    @BeforeEach
    void setUp() {
        this.handWaveyConfig = new HandWaveyConfig("unitTest");
        this.handWaveyConfig.defineGeneralConfig();
        this.config = Config.singleton();

        this.handSummary = new HandSummary(42);
        this.handSummary.setHandPosition(10, 200, 20);
        this.handSummary.setHandAngles(0.1, 0, -0.1);
        this.handSummary.setArmAngles(0.2, 0, -0.2);
        this.handSummary.setHandOpen(false);
        this.handSummary.setHandIsLeft(false);
    }

    @AfterEach
    void destroy() {
        this.handSummary = null;
        this.config = null;
        this.handWaveyConfig = null;
        System.gc();
    }

    @Test
    public void testRawValues() {
        assertEquals(this.handSummary.getHandX(), 10);
        assertEquals(this.handSummary.getHandY(), 200);
        assertEquals(this.handSummary.getHandZ(), 20);

        assertEquals(this.handSummary.getHandRoll(), 0.1);
        assertEquals(this.handSummary.getHandPitch(), 0);
        assertEquals(this.handSummary.getHandYaw(), -0.1);

        assertEquals(this.handSummary.getArmRoll(), 0.2);
        assertEquals(this.handSummary.getArmPitch(), 0);
        assertEquals(this.handSummary.getArmYaw(), -0.2);

        assertEquals(this.handSummary.handIsOpen(), false);
   }

    @Test
    public void testID() {
        assertEquals(this.handSummary.getID(), 42);
    }

    @Test
    public void testValid() {
        assertEquals(this.handSummary.isValid(), true);
        this.handSummary.setOOB(true);
        assertEquals(this.handSummary.isValid(), false);
        this.handSummary.setOOB(false);
        assertEquals(this.handSummary.isValid(), true);
        this.handSummary.markInvalid();
        assertEquals(this.handSummary.isValid(), false);
    }

    @Test
    public void testLeft() {
        assertEquals(this.handSummary.handIsLeft(), false);
        this.handSummary.setHandIsLeft(true);
        assertEquals(this.handSummary.handIsLeft(), true);
        this.handSummary.setHandIsLeft(false);
        assertEquals(this.handSummary.handIsLeft(), false);
    }

    @Test
    public void testReMap() {
        // Change the config.
        this.config.getGroup("physicalBoundaries").getGroup("map").getItem("x").set("y");
        this.config.getGroup("physicalBoundaries").getGroup("map").getItem("y").set("z");
        this.config.getGroup("physicalBoundaries").getGroup("map").getItem("z").set("x");

        // Create a new object with that new config.
        this.handSummary = new HandSummary(43);

        // Set some data. We expect this to go to new locations.
        this.handSummary.setHandPosition(10, 20, 30); // X, Y, Z => Y, Z, X

        assertEquals(this.handSummary.getHandX(), 20);
        assertEquals(this.handSummary.getHandY(), 30);
        assertEquals(this.handSummary.getHandZ(), 10);
    }

    @Test
    public void testOffsets() {
        // Change the config.
        this.config.getGroup("physicalBoundaries").getGroup("inputOffsets").getItem("x").set("1");
        this.config.getGroup("physicalBoundaries").getGroup("inputOffsets").getItem("y").set("2");
        this.config.getGroup("physicalBoundaries").getGroup("inputOffsets").getItem("z").set("3");

        // Create a new object with that new config.
        this.handSummary = new HandSummary(43);

        // Set some data. We expect these to change by the correct offsets.
        this.handSummary.setHandPosition(110, 120, 130);

        assertEquals(this.handSummary.getHandX(), 111);
        assertEquals(this.handSummary.getHandY(), 122);
        assertEquals(this.handSummary.getHandZ(), 133);
    }

    @Test
    public void testRotationReMap() {
        // Change the config.
        this.config.getGroup("physicalBoundaries").getGroup("rotationMap").getItem("roll").set("pitch");
        this.config.getGroup("physicalBoundaries").getGroup("rotationMap").getItem("pitch").set("yaw");
        this.config.getGroup("physicalBoundaries").getGroup("rotationMap").getItem("yaw").set("roll");

        // Create a new object with that new config.
        this.handSummary = new HandSummary(44);

        // Set some data. We expect this to go to new locations.
        this.handSummary.setHandAngles(2.10, 2.20, 2.30); // roll, pitch, yaw => pitch, yaw, roll

        assertEquals(this.handSummary.getHandRoll(), 2.20);
        assertEquals(this.handSummary.getHandPitch(), 2.30);
        assertEquals(this.handSummary.getHandYaw(), 2.10);
    }

    @Test
    public void testRotationOffsets() {
        // Change the config.
        this.config.getGroup("physicalBoundaries").getGroup("inputRotationOffsets").getItem("roll").set("0.01");
        this.config.getGroup("physicalBoundaries").getGroup("inputRotationOffsets").getItem("pitch").set("0.02");
        this.config.getGroup("physicalBoundaries").getGroup("inputRotationOffsets").getItem("yaw").set("0.03");

        // Create a new object with that new config.
        this.handSummary = new HandSummary(45);

        // Set some data. We expect this to go to new locations.
        this.handSummary.setHandAngles(2.10, 2.20, 2.30);

        // TODO There's got to be a better way to round to a specific number of decimal places?!
        assertEquals((double)Math.round(this.handSummary.getHandRoll() * 100)/100, 2.11);
        assertEquals((double)Math.round(this.handSummary.getHandPitch() * 100)/100, 2.22);
        assertEquals((double)Math.round(this.handSummary.getHandYaw() * 100)/100, 2.33);
    }

    @Test
    public void testOOBRotationOffsets() {
        // Change the config.
        this.config.getGroup("physicalBoundaries").getGroup("inputRotationOffsets").getItem("roll").set("2");
        this.config.getGroup("physicalBoundaries").getGroup("inputRotationOffsets").getItem("pitch").set("3");
        this.config.getGroup("physicalBoundaries").getGroup("inputRotationOffsets").getItem("yaw").set("-7");

        // Create a new object with that new config.
        this.handSummary = new HandSummary(46);

        // Set some data. We expect this to go to new locations.
        this.handSummary.setHandAngles(3.10, 3.20, 3.30);

        // TODO There's got to be a better way to round to a specific number of decimal places?!
        assertEquals((double)Math.round(this.handSummary.getHandRoll() * 100)/100, -1.96);
        assertEquals((double)Math.round(this.handSummary.getHandPitch() * 100)/100, -3.06);
        assertEquals((double)Math.round(this.handSummary.getHandYaw() * 100)/100, 2.58);
    }
}
