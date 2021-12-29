package handWavey;

import handWavey.Gesture;
import config.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class TestGesture {
    private Gesture gesture;
    private Config config;

    @BeforeEach
    void setUp() {
        this.gesture = new Gesture();
        this.config = new Config("handWaveyConfigTest.yml");

    }

    @AfterEach
    void destroy() {
        this.gesture = null;
        this.config = null;
    }

    @Test
    public void testGestureName() {
        assertEquals("active-p0Open-any-s0Open", this.gesture.generateGestureName("active", 0, Gesture.open,  "any", 0, Gesture.open));
        assertEquals("active-p0Closed-any-s0Open", this.gesture.generateGestureName("active", 0, Gesture.closed,  "any", 0, Gesture.open));
        assertEquals("active-p0Closed", this.gesture.generateGestureName("active", 0, Gesture.closed,  "any", 0, Gesture.absent));
    }

}
