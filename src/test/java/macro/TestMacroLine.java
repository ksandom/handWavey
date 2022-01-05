package macro;

import macro.*;
import mouseAndKeyboardOutput.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class TestOutput {
    private MacroLine macroLine;
    private Output output;
    
    @BeforeEach
    void setUp() {
        this.output = new NullOutput();
        this.macroLine = new MacroLine(this.output);
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
}
