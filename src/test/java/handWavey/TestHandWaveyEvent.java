package handWavey;

import handWavey.Gesture;
import handWavey.HandWaveyConfig;
import handWavey.HandsState;
import mouseAndKeyboardOutput.*;
import config.Config;
import config.Group;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class TestHandWaveyEvent {
    private Gesture gesture;
    private Config config;
    private HandWaveyConfig handWaveyConfig;
    private HandWaveyEvent eventHandler;
    private Output output;
    private HandsState handsState;

    @BeforeEach
    void setUp() {
        Config.setSingletonFilename("handWaveyConfigTest");
        this.config = Config.singleton();
        this.handWaveyConfig = new HandWaveyConfig("unitTest");
        this.handWaveyConfig.defineGeneralConfig();
        this.gesture = new Gesture();
        this.output = new NullOutput();
        this.handsState = HandsState.singleton();
        this.eventHandler = new HandWaveyEvent(this.output, HandWaveyEvent.audioDisabled, this.handsState);
        this.handsState.setHandWaveyEvent(this.eventHandler);
    }

    @AfterEach
    void destroy() {
        this.gesture = null;
        this.config = null;
        this.handWaveyConfig = null;
    }

//     @Test
//     public void testSomething() {
//     }
}
