// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package debug;

import debug.Debug;
import config.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class DebugTester extends Debug {
    public DebugTester(int level, String message) {
        super(level, message);
    }

    public String getDebugText(int level, String message) {
        return super.getDebugText(level, message);
    }

    public Boolean shouldOutput(int level) {
        return super.shouldOutput(level);
    }
}

public class DebugTest {
    private DebugTester debug;

    @BeforeEach
    void setUp() {
        this.debug = new DebugTester(3, "Unit tests");
    }

    @AfterEach
    void destroy() {
        this.debug = null;
    }

    @Test
    public void testOutput() {
        assertEquals("Debug 3 (Unit tests): Blah.", this.debug.getDebugText(3, "Blah."));
    }

    @Test
    public void testWhen() {
        assertEquals(true, this.debug.shouldOutput(2));
        assertEquals(true, this.debug.shouldOutput(3));
        assertEquals(false, this.debug.shouldOutput(4));

        this.debug.setLevel(1);

        assertEquals(true, this.debug.shouldOutput(0));
        assertEquals(true, this.debug.shouldOutput(1));
        assertEquals(false, this.debug.shouldOutput(2));

        this.debug.setLevel(0);

        assertEquals(true, this.debug.shouldOutput(0));
        assertEquals(false, this.debug.shouldOutput(1));
        assertEquals(false, this.debug.shouldOutput(2));
    }

    @Test
    public void testContextBasedSetup() {
        // Optimal: Debugging should be set up as configured.
        
        Config.singleton().newGroup("debug").newItem("aContext3", "2", "A test value.");
        
        Debug debug = Debug.getDebug("aContext3");
        assertEquals("Debug 1 (aContext3): Blah.", debug.getDebugText(1, "Blah."));
        assertNotNull(debug);
        assertEquals(2, debug.getLevel());
    }

    @Test
    public void testContextBasedSetupWithoutConfigItem() {
        // Sub-optimal: Config item is missing. Should gracefully return a working object so that debugging can be done.
        Config.singleton().newGroup("debug");
        
        Debug debug = Debug.getDebug("aContext2");
        assertEquals("Debug 1 (aContext2): Blah.", debug.getDebugText(1, "Blah."));
        assertNotNull(debug);
        assertEquals(1, debug.getLevel());
    }

    @Test
    public void testContextBasedSetupWithoutAnyConfig() {
        // Sub-optimal: Config item, and debug group are missing. Should gracefully return a working object so that debugging can be done.
        Debug debug = Debug.getDebug("aContext1");
        assertEquals("Debug 1 (aContext1): Blah.", debug.getDebugText(1, "Blah."));
        assertNotNull(debug);
        assertEquals(1, debug.getLevel());
    }
}
