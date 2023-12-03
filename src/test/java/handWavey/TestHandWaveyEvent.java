// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package handWavey;

import handWavey.Gesture;
import handWavey.HandWaveyConfig;
import handWavey.HandsState;
import handWavey.HandWaveyManager;
import mouseAndKeyboardOutput.*;
import config.Config;
import config.Group;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class TestHandWaveyEvent {
    private Gesture gesture = null;
    private Config config = null;
    private HandWaveyConfig handWaveyConfig = null;
    private HandWaveyEvent handWaveyEvent = null;
    private OutputProtection output = null;
    private HandsState handsState = null;
    private HandWaveyManager handWaveyManager = null;

    @BeforeEach
    void setUp() {
        Config.setSingletonFilename("handWaveyConfigTest");
        this.handWaveyManager = new HandWaveyManager();
        this.config = Config.singleton();
        this.handWaveyConfig = new HandWaveyConfig("unitTest");
        this.handWaveyConfig.defineGeneralConfig();
        this.gesture = new Gesture();
        this.gesture.generateConfig();
        this.output = new OutputProtection(new NullOutput());
        this.handsState = HandsState.singleton();
        this.handWaveyEvent = new HandWaveyEvent(this.output, HandWaveyEvent.audioDisabled, this.handsState, this.handWaveyManager);
        this.handsState.setHandWaveyEvent(this.handWaveyEvent);
    }

    @AfterEach
    void destroy() {
        this.gesture = null;
        this.config = null;
        this.handWaveyConfig = null;
        this.handWaveyEvent = null;
        this.output = null;
        this.handsState = null;
        this.handWaveyManager = null;
    }

    @Test
    public void testTriggerEvent() {
        this.config.getGroup("actionEvents").getItem("general-state-pClosed-enter").set("click();");
        assertEquals(0, this.output.testInt("clicked"));
        this.handWaveyEvent.triggerEvent("general-state-pClosed-enter");
        assertEquals(1, this.output.testInt("clicked"));
    }
}
