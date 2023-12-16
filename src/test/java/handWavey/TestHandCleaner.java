// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package handWavey;

import handWavey.HandSummary;
import handWavey.HandCleaner;
import config.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class TestHandCleaner {
    private HandCleaner handCleaner = null;
    private HandSummary handSummary = null;
    private HandWaveyConfig handWaveyConfig = null;
    private Config config = null;

    @BeforeEach
    void setUp() {
        this.handWaveyConfig = new HandWaveyConfig("unitTest");
        this.handWaveyConfig.defineGeneralConfig();
        this.config = Config.singleton();

        this.handCleaner = new HandCleaner();

        this.handSummary = new HandSummary(42);
        this.handSummary.setHandPosition(10, 200, 20);
        this.handSummary.setHandAngles(0.1, 0, -0.1);
        this.handSummary.setArmAngles(0.2, 0, -0.2);
        this.handSummary.setFingerAngle(0.5);
        this.handSummary.setHandIsLeft(false);
    }

    @AfterEach
    void destroy() {
        this.handCleaner = null;
        this.handSummary = null;
        this.config = null;
        this.handWaveyConfig = null;
    }

    @Test
    public void testEmptyState() {
        assertEquals(this.handCleaner.getHandX(), 0);
        assertEquals(this.handCleaner.getHandY(), 0);
        assertEquals(this.handCleaner.getHandZ(), 0);
        assertEquals(this.handCleaner.getHandRoll(), 0);
        assertEquals(this.handCleaner.getHandPitch(), 0);
        assertEquals(this.handCleaner.getHandYaw(), 0);
        assertEquals(this.handCleaner.getState(), Gesture.absent);
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

        assertEquals(this.handSummary.getFingerAngle(), 0.5);
   }

    @Test
    public void testFirstState() {
        this.handCleaner.updateHand(this.handSummary);

        assertEquals(this.handCleaner.getHandX(), 10);
        assertEquals(this.handCleaner.getHandY(), 200);
        assertEquals(this.handCleaner.getHandZ(), 20);
        assertEquals(this.handCleaner.getHandRoll(), 0.1);
        assertEquals(this.handCleaner.getHandPitch(), 0);
        assertEquals(this.handCleaner.getHandYaw(), -0.1);
        assertEquals(this.handCleaner.getState(), Gesture.closed);
    }

    @Test
    public void testSecondState() {
        this.handCleaner.updateHand(this.handSummary);

        HandSummary handSummary = new HandSummary(43);
        handSummary.setHandPosition(0, 0, 0);
        handSummary.setHandAngles(0, 0, 0);
        handSummary.setArmAngles(0, 0, 0);
        handSummary.setFingerAngle(0);
        handSummary.setHandIsLeft(false);

        this.handCleaner.updateHand(handSummary);

        assertEquals(this.handCleaner.getHandX(), 7.5);
        assertEquals(this.handCleaner.getHandY(), 150);
        assertEquals(this.handCleaner.getHandZ(), 15);
        assertEquals(this.handCleaner.getHandRoll(), 0.075, 0.001);
        assertEquals(this.handCleaner.getHandPitch(), 0);
        assertEquals(this.handCleaner.getHandYaw(), -0.075, 0.001);
        assertEquals(this.handCleaner.getState(), Gesture.closed);
    }
}
