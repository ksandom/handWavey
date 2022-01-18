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

    @Test
    public void testGetDesktopResolution() {
        Dimension dim = this.output.getDesktopResolution();
        assertEquals(123, dim.width);
        assertEquals(321, dim.height);
    }
    
    @Test
    public void testSetPosition() {
        this.output.setPosition(456, 654);
        assertEquals(456, this.output.testInt("posX"));
        assertEquals(654, this.output.testInt("posY"));
    }
    
    @Test
    public void testClick() {
        assertEquals(0, this.output.testInt("clicked"));
        this.output.click("left");
        assertEquals(InputEvent.BUTTON1_MASK, this.output.testInt("lastMouseButton"));
        assertEquals(1, this.output.testInt("clicked"));
    }
    
    @Test
    public void testDoubleClick() {
        assertEquals(0, this.output.testInt("clicked"));
        this.output.doubleClick("middle");
        assertEquals(InputEvent.BUTTON2_MASK, this.output.testInt("lastMouseButton"));
        assertEquals(1, this.output.testInt("clicked"));
    }
    
    @Test
    public void testMouseDown() {
        this.output.mouseDown("left");
        assertEquals(InputEvent.BUTTON1_MASK, this.output.testInt("lastMouseButton"));
        this.output.mouseDown("right");
        assertEquals(InputEvent.BUTTON3_MASK, this.output.testInt("lastMouseButton"));
    }
    
    @Test
    public void testMouseUp() {
        this.output.mouseUp("left");
        assertEquals(0, this.output.testInt("lastMouseButton"));
        this.output.mouseDown("left");
        assertEquals(InputEvent.BUTTON1_MASK, this.output.testInt("lastMouseButton"));
        this.output.mouseUp("left");
        assertEquals(InputEvent.BUTTON1_MASK, this.output.testInt("lastMouseButton"));
        this.output.mouseDown("right");
        assertEquals(InputEvent.BUTTON3_MASK, this.output.testInt("lastMouseButton"));
        this.output.mouseUp("right");
        assertEquals(InputEvent.BUTTON3_MASK, this.output.testInt("lastMouseButton"));
    }
    
    @Test
    public void testScroll() {
        assertEquals(0, this.output.testInt("scroll"));
        this.output.scroll(1);
        assertEquals(1, this.output.testInt("scroll"));
        this.output.scroll(3);
        assertEquals(4, this.output.testInt("scroll"));
        this.output.scroll(-2);
        assertEquals(2, this.output.testInt("scroll"));
    }
    
    @Test
    public void testKeyDown() {
        this.output.keyDown("ctrl");
        assertEquals(KeyEvent.VK_CONTROL, this.output.testInt("lastKey"));
        this.output.keyDown("alt");
        assertEquals(KeyEvent.VK_ALT, this.output.testInt("lastKey"));
    }
    
    @Test
    public void testKeyUp() {
        this.output.keyDown("ctrl");
        assertEquals(KeyEvent.VK_CONTROL, this.output.testInt("lastKey"));
        this.output.keyUp("ctrl");
        assertEquals(KeyEvent.VK_CONTROL, this.output.testInt("lastKey"));
        this.output.keyDown("alt");
        assertEquals(KeyEvent.VK_ALT, this.output.testInt("lastKey"));
        this.output.keyUp("alt");
        assertEquals(KeyEvent.VK_ALT, this.output.testInt("lastKey"));
    }
    
    @Test
    public void testGetKeysIKnow() {
        assertEquals(3, this.output.getKeysIKnow().size());
    }
}
