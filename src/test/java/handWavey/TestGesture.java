// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package handWavey;

import handWavey.Gesture;
import handWavey.HandWaveyConfig;
import config.Config;
import config.Group;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class TestGesture {
    private Gesture gesture = null;
    private Config config = null;
    private HandWaveyConfig handWaveyConfig = null;

    @BeforeEach
    void setUp() {
        Config.setSingletonFilename("handWaveyConfigTest");
        this.config = Config.singleton();
        this.handWaveyConfig = new HandWaveyConfig("unitTest");
        this.handWaveyConfig.defineGeneralConfig();
        this.gesture = new Gesture();
    }

    @AfterEach
    void destroy() {
        this.gesture = null;
        this.config = null;
        this.handWaveyConfig = null;
    }

    @Test
    public void testGestureName() {
        assertEquals("combined-pActive0Open-sNone0Open", this.gesture.gestureName("active", 0, Gesture.open, "none", 0, Gesture.open));
        assertEquals("combined-pNone2Closed-sNone3Open", this.gesture.gestureName("none", 2, Gesture.closed, "none", 3, Gesture.open));
        assertEquals("combined-pActive0Open-sNone0Absent", this.gesture.gestureName("active", 0, Gesture.open, "none", 0, Gesture.absent));
        assertEquals("individual-pActive0Open", this.gesture.gestureName("p", "active", 0, Gesture.open));
        assertEquals("individual-sActive0Open", this.gesture.gestureName("s", "active", 0, Gesture.open));
        assertEquals("sActive0Open", this.gesture.generateSingleHandGestureName("s", "active", 0, Gesture.open));
    }

    @Test
    public void testGestureDescription() {
        assertEquals("The primary hand is in the active zone, is in segment 0, and is in the open state. And the secondary hand is in the active zone, is in segment 0, and is in the closed state.", this.gesture.gestureDescription("active", 0, Gesture.open, "active", 0, Gesture.closed));
        assertEquals("The primary hand is in the active zone, is in segment 0, and is in the open state. And the secondary hand is absent.", this.gesture.gestureDescription("active", 0, Gesture.open, "none", 0, Gesture.absent));
        assertEquals("The primary hand is in the active zone, is in segment 0, and is in the open state.", this.gesture.gestureDescription("p", "active", 0, Gesture.open));
        assertEquals("The secondary hand is in the active zone, is in segment 0, and is in the open state.", this.gesture.gestureDescription("s", "active", 0, Gesture.open));
    }

    @Test
    public void testGenerateConfig() {
        this.gesture.generateConfig();

        Group actionEvents = Config.singleton().getGroup("actionEvents");
        Group audioEvents = Config.singleton().getGroup("audioEvents");

        // All of these should exist, and should not return null.

        // Combinations of hands together.
        assertEquals("", actionEvents.getItem("combined-pActive0Open-sNone0Open-enter").get());
        assertEquals("", actionEvents.getItem("combined-pActive0Open-sNone0Open-exit").get());

        assertEquals("", audioEvents.getItem("combined-pActive0Open-sNone0Open-enter").get());
        assertEquals("", audioEvents.getItem("combined-pActive0Open-sNone0Open-exit").get());

        // Use both hands, but one is absent.
        assertEquals("", actionEvents.getItem("combined-pActive0Open-sOOB0Absent-enter").get());

        // Full gestures for a single hand.
        assertEquals("", actionEvents.getItem("individual-pActive0Open-enter").get());
        assertEquals("", actionEvents.getItem("individual-sActive0Open-enter").get());
        assertEquals("", actionEvents.getItem("individual-pNoMove0Open-exit").get());

        // A specific change in a single component.
        assertEquals("", actionEvents.getItem("general-zone-pActive-enter").get());
        assertEquals("setButton(\"left\");", actionEvents.getItem("general-segment-p0-enter").get());
        assertEquals("", actionEvents.getItem("general-state-pOpen-enter").get());
        assertEquals("", actionEvents.getItem("general-zone-sActive-enter").get());
        assertEquals("keyDown(\"ctrl\");", actionEvents.getItem("general-segment-s0-enter").get());
        assertEquals("", actionEvents.getItem("general-state-sOpen-enter").get());

        // Any change in a single component.
        assertEquals("", actionEvents.getItem("general-zone-pAnyChange").get());
        assertEquals("lockCursor();rewindCursorPosition();", actionEvents.getItem("general-segment-pAnyChange").get());
        assertEquals("", actionEvents.getItem("general-state-pAnyChange").get());
        assertEquals("", actionEvents.getItem("general-zone-sAnyChange").get());
        assertEquals("", actionEvents.getItem("general-segment-sAnyChange").get());
        assertEquals("", actionEvents.getItem("general-state-sAnyChange").get());

        // OOB and NonOOB.
        assertEquals("", actionEvents.getItem("combined-pOOB0Absent-sOOB0Absent-exit").get());
        assertEquals("", actionEvents.getItem("individual-pOOB0Absent-exit").get());

        // Special events.
        assertEquals("", actionEvents.getItem("special-newHandFreeze").get());
    }

    @Test
    public void testHandState() {
        assertEquals("OOB<0", this.gesture.handState(-1));
        assertEquals("OOB>n(3)", this.gesture.handState(10));
        assertEquals("open", this.gesture.handState(Gesture.open));
        assertEquals("closed", this.gesture.handState(Gesture.closed));
        assertEquals("absent", this.gesture.handState(Gesture.absent));
    }
}
