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
    private Gesture gesture;
    private Config config;
    private HandWaveyConfig handWaveyConfig;

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
        assertEquals("combined-pActive0Open-sAny0Open", this.gesture.gestureName("active", 0, Gesture.open, "any", 0, Gesture.open));
        assertEquals("combined-pNone2Closed-sAny3Open", this.gesture.gestureName("none", 2, Gesture.closed, "any", 3, Gesture.open));
        assertEquals("combined-pActive0Open-sAbsent", this.gesture.gestureName("active", 0, Gesture.open, "any", 0, Gesture.absent));
        assertEquals("individual-pActive0Open", this.gesture.gestureName("p", "active", 0, Gesture.open));
        assertEquals("individual-sActive0Open", this.gesture.gestureName("s", "active", 0, Gesture.open));
    }
    
    @Test
    public void testGestureDescription() {
        assertEquals("The primary hand is in the active zone, is in segment 0, and is in the open state. And the secondary hand is in the any zone, is in segment 0, and is in the closed state.", this.gesture.gestureDescription("active", 0, Gesture.open, "any", 0, Gesture.closed));
        assertEquals("The primary hand is in the active zone, is in segment 0, and is in the open state. And the secondary hand is absent.", this.gesture.gestureDescription("active", 0, Gesture.open, "any", 0, Gesture.absent));
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
        assertEquals("", actionEvents.getItem("combined-pActive0Open-sAny0Open-enter").get());
        assertEquals("", actionEvents.getItem("combined-pActive0Open-sAny0Open-exit").get());
        
        assertEquals("", audioEvents.getItem("combined-pActive0Open-sAny0Open-enter").get());
        assertEquals("", audioEvents.getItem("combined-pActive0Open-sAny0Open-exit").get());

        // Use both hands, but one is absent.
        assertEquals("", actionEvents.getItem("combined-pActive0Open-sAbsent-enter").get());
        
        // Full gestures for a single hand.
        assertEquals("", actionEvents.getItem("individual-pActive0Open-enter").get());
        assertEquals("", actionEvents.getItem("individual-sActive0Open-enter").get());
        
        // A specific change in a single component.
        assertEquals("", actionEvents.getItem("general-zone-pActive-enter").get());
        assertEquals("setButton(\"left\");", actionEvents.getItem("general-segment-p0-enter").get());
        assertEquals("rewind();freeze();mouseUp();", actionEvents.getItem("general-state-pOpen-enter").get());
        assertEquals("", actionEvents.getItem("general-zone-sActive-enter").get());
        assertEquals("", actionEvents.getItem("general-segment-s0-enter").get());
        assertEquals("", actionEvents.getItem("general-state-sOpen-enter").get());
        
        // Any change in a single component.
        assertEquals("", actionEvents.getItem("general-zone-pAnyChange").get());
        assertEquals("", actionEvents.getItem("general-segment-pAnyChange").get());
        assertEquals("", actionEvents.getItem("general-state-pAnyChange").get());
        assertEquals("", actionEvents.getItem("general-zone-sAnyChange").get());
        assertEquals("", actionEvents.getItem("general-segment-sAnyChange").get());
        assertEquals("", actionEvents.getItem("general-state-sAnyChange").get());
    }
}
