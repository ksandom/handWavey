package debug;

import debug.Debug;
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
        assertEquals(this.debug.getDebugText(3, "Blah."), "Debug 3 (Unit tests): Blah.");
    }

    @Test
    public void testWhen() {
        assertEquals(this.debug.shouldOutput(2), true);
        assertEquals(this.debug.shouldOutput(3), true);
        assertEquals(this.debug.shouldOutput(4), false);

        this.debug.setLevel(1);

        assertEquals(this.debug.shouldOutput(0), true);
        assertEquals(this.debug.shouldOutput(1), true);
        assertEquals(this.debug.shouldOutput(2), false);

        this.debug.setLevel(0);

        assertEquals(this.debug.shouldOutput(0), true);
        assertEquals(this.debug.shouldOutput(1), false);
        assertEquals(this.debug.shouldOutput(2), false);
    }
}
