// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package mouseAndKeyboardOutput;

import mouseAndKeyboardOutput.*;
import debug.Debug;

import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class TestProtector {
    private Protector protector = null;

    @BeforeEach
    void setUp() {
        this.protector = new Protector();
    }

    @AfterEach
    void destroy() {
        this.protector = null;
    }

    @Test
    public void testSingleSequence() {
        assertEquals(false, this.protector.isDown("thing"));
        assertEquals(true, this.protector.isUp("thing"));

        this.protector.setDown("thing");
        assertEquals(true, this.protector.isDown("thing"));
        assertEquals(false, this.protector.isUp("thing"));

        this.protector.setUp("thing");
        assertEquals(false, this.protector.isDown("thing"));
        assertEquals(true, this.protector.isUp("thing"));
    }

    @Test
    public void testMultipleSequence() {
        assertEquals(false, this.protector.isDown("thing1"));
        assertEquals(false, this.protector.isDown("thing2"));
        assertEquals(false, this.protector.isDown("thing3"));

        this.protector.setDown("thing1");
        assertEquals(true, this.protector.isDown("thing1"));
        assertEquals(false, this.protector.isDown("thing2"));
        assertEquals(false, this.protector.isDown("thing3"));

        this.protector.setDown("thing2");
        assertEquals(true, this.protector.isDown("thing1"));
        assertEquals(true, this.protector.isDown("thing2"));
        assertEquals(false, this.protector.isDown("thing3"));

        assertEquals(2, this.protector.getDownItems().size());
    }
}
