// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package mouseAndKeyboardOutput;

import mouseAndKeyboardOutput.*;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class TestPressables {
    private Pressables pressables;
    
    @BeforeEach
    void setUp() {
        this.pressables = new Pressables();
    }

    @AfterEach
    void destroy() {
        this.pressables = null;
    }

    @Test
    public void testIsValidPressable() {
        this.pressables.defineKeys();
        
        assertEquals(false, this.pressables.isValidPressable("hgfhgf"));
        assertEquals(true, this.pressables.isValidPressable("ctrl"));
    }

    @Test
    public void testGetPressableID() {
        this.pressables.defineKeys();
        
        assertEquals(Pressables.INVALID, this.pressables.getPressableID("hgfhgf"));
        assertEquals(KeyEvent.VK_CONTROL, this.pressables.getPressableID("ctrl"));
    }

    @Test
    public void testPressablesIKnow() {
        this.pressables.defineKeys();
        
        assertEquals(true, (this.pressables.getPressablesIKnow().size() > 1));
    }
}
