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

/*
All of the same tests from TestOutput should pass with the same when using OutputProtection.
*/
class TestOutputProtection {
    private OutputProtection output;
    
    @BeforeEach
    void setUp() {
        this.output = new OutputProtection(new NullOutput());
    }

    @AfterEach
    void destroy() {
        this.output = null;
    }

    // TODO Find a better way of testing this.
//     @Test
//     public void testMouseDown() {
//         this.output.mouseDown("left");
//         assertEquals(InputEvent.BUTTON1_MASK, this.output.testInt("lastMouseButton"));
//         this.output.mouseDown("right");
//         assertEquals(InputEvent.BUTTON3_MASK, this.output.testInt("lastMouseButton"));
//         this.output.mouseDown("left");
//         
//         // The button is already down. So the protection should stop it from going down again.
//         assertEquals(InputEvent.BUTTON3_MASK, this.output.testInt("lastMouseButton"));
//     }
    
    @Test
    public void testMouseUp() {
        this.output.mouseDown("left");
        assertEquals(InputEvent.BUTTON1_MASK, this.output.testInt("lastMouseButton"));
        this.output.mouseUp("left");
        assertEquals(InputEvent.BUTTON1_MASK, this.output.testInt("lastMouseButton"));
        
        // We have not yet pressed down the right button. So it should not press.
        this.output.mouseUp("right");
        assertEquals(InputEvent.BUTTON1_MASK, this.output.testInt("lastMouseButton"));
    }
    
    // TODO Find a better way of testing this.
//     @Test
//     public void testKeyDown() {
//         this.output.keyDown("ctrl");
//         assertEquals(KeyEvent.VK_CONTROL, this.output.testInt("lastKey"));
//         this.output.keyDown("alt");
//         assertEquals(KeyEvent.VK_ALT, this.output.testInt("lastKey"));
//         
//         // We have already pressed the ctrl key, so it should not get pressed again.
//         this.output.keyDown("ctrl");
//         assertEquals(KeyEvent.VK_ALT, this.output.testInt("lastKey"));
//     }
    
    @Test
    public void testKeyUp() {
        this.output.keyDown("ctrl");
        assertEquals(KeyEvent.VK_CONTROL, this.output.testInt("lastKey"));
        this.output.keyUp("ctrl");
        assertEquals(KeyEvent.VK_CONTROL, this.output.testInt("lastKey"));
        
        // We have not yet pressed down the alt key, so it should not get pressed.
        this.output.keyUp("alt");
        assertEquals(KeyEvent.VK_CONTROL, this.output.testInt("lastKey"));
    }
}
