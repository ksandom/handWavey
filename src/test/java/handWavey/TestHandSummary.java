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
}
