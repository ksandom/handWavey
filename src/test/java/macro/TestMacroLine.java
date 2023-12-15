// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package macro;

import macro.*;
import mouseAndKeyboardOutput.*;
import handWavey.HandsState;
import handWavey.HandWaveyManager;
import handWavey.HandWaveyEvent;
import config.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class TestMacro {
    private MacroLine macroLine = null;
    private OutputProtection output = null;
    private HandsState handsState = null;
    private HandWaveyManager handWaveyManager = null;
    private HandWaveyEvent handWaveyEvent = null;

    @BeforeEach
    void setUp() {
        this.handWaveyManager = new HandWaveyManager(false);

        Config.singleton().getGroup("debug").getItem("HandWaveyEvent").set("5");

        this.handsState = HandsState.singleton();
        this.output = new OutputProtection(new NullOutput());
        this.handWaveyEvent = new HandWaveyEvent(this.output, HandWaveyEvent.audioDisabled, this.handsState, this.handWaveyManager);
        this.macroLine = new MacroLine(this.output, this.handsState, this.handWaveyManager, this.handWaveyEvent);
    }

    @AfterEach
    void destroy() {
        this.macroLine = null;
        this.output = null;
        this.handsState = null;
        this.handWaveyEvent = null;
        this.handWaveyManager = null;
    }

    @Test
    public void testGetKeysIKnow() {
        this.macroLine.runLine("moveMouse(\"122\", \"221\");");
        assertEquals(122, this.output.testInt("posX"));
        assertEquals(221, this.output.testInt("posY"));
    }

    @Test
    public void testEmptyParameters() {
        this.macroLine.runLine("click();");
        assertEquals(1, this.output.testInt("clicked"));
    }

    @Test
    public void testConfigExists() {
        assertNotNull(Config.singleton().getGroup("actionEvents").itemCanExist("custom-1"));
    }

    @Test
    public void testSlots() {
        Group actionEvents = Config.singleton().getGroup("actionEvents");
        actionEvents.getItem("custom-1").set("moveMouse(\"101\", \"201\");");
        actionEvents.getItem("custom-2").set("moveMouse(\"102\", \"202\");");
        actionEvents.getItem("custom-3").set("moveMouse(\"103\", \"203\");");

        System.out.println("Got value: '" + actionEvents.getItem("custom-1").get() + "'");

        this.macroLine.runLine("doSlot(\"5\", \"custom-1\");");
        assertEquals(101, this.output.testInt("posX"));
        assertEquals(201, this.output.testInt("posY"));

        this.macroLine.runLine("setSlot(\"5\", \"custom-2\");");
        this.macroLine.runLine("setSlot(\"6\", \"custom-3\");");
        assertEquals(101, this.output.testInt("posX"));
        assertEquals(201, this.output.testInt("posY"));

        this.macroLine.runLine("doSlot(\"5\", \"custom-1\");");
        assertEquals(102, this.output.testInt("posX"));
        assertEquals(202, this.output.testInt("posY"));

        this.macroLine.runLine("doSlot(\"6\", \"custom-1\");");
        assertEquals(103, this.output.testInt("posX"));
        assertEquals(203, this.output.testInt("posY"));
    }
}
