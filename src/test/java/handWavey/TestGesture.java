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
        assertEquals("pActive0Open-sAny0Open", this.gesture.generateGestureName("active", 0, Gesture.open, "any", 0, Gesture.open));
        assertEquals("pActive0Closed-sAny0Open", this.gesture.generateGestureName("active", 0, Gesture.closed, "any", 0, Gesture.open));
        assertEquals("pActive0Closed", this.gesture.generateGestureName("active", 0, Gesture.closed,  "any", 0, Gesture.absent));
    }
    
    @Test
    public void testGestureDescription() {
        assertEquals("The primary hand is in the active zone, is in segment 0, and is in the open state. And the secondary hand is in the any zone, is in segment 0, and is in the closed state.", this.gesture.generateGestureDescription("active", 0, Gesture.open, "any", 0, Gesture.closed));
        assertEquals("The primary hand is in the active zone, is in segment 0, and is in the open state.", this.gesture.generateGestureDescription("active", 0, Gesture.open, "any", 0, Gesture.absent));
    }

    @Test
    public void testGenerateConfig() {
        this.gesture.generateConfig();
        
        Group actionEvents = Config.singleton().getGroup("actionEvents");
        Group audioEvents = Config.singleton().getGroup("audioEvents");
        
        // All of these should exist, and should not return null.
        assertEquals("", actionEvents.getItem("combined-pActive0Open-sAny0Open-enter").get());
        assertEquals("", actionEvents.getItem("combined-pActive0Open-sAny0Open-exit").get());
        
        assertEquals("", audioEvents.getItem("combined-pActive0Open-sAny0Open-enter").get());
        assertEquals("", audioEvents.getItem("combined-pActive0Open-sAny0Open-exit").get());
        
        assertEquals("", actionEvents.getItem("combined-pActive0Open-enter").get());
        
        assertEquals("", actionEvents.getItem("general-zone-pActive-enter").get());
        assertEquals("", actionEvents.getItem("general-segment-p0-enter").get());
        assertEquals("", actionEvents.getItem("general-state-pOpen-enter").get());
    }
}
