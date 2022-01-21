// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package macro;

import macro.*;
import mouseAndKeyboardOutput.*;
import handWavey.HandsState;
import handWavey.HandWaveyManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class TestOutput {
    private MacroLine macroLine;
    private OutputProtection output;
    private HandsState handsState;
    private HandWaveyManager handWaveyManager;
    
    @BeforeEach
    void setUp() {
        this.output = new OutputProtection(new NullOutput());
        this.handsState = HandsState.singleton();
        this.handWaveyManager = new HandWaveyManager();
        this.macroLine = new MacroLine(this.output, this.handsState, this.handWaveyManager);
    }

    @AfterEach
    void destroy() {
        this.macroLine = null;
        this.output = null;
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
}
