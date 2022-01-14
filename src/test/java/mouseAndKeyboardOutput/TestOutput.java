package mouseAndKeyboardOutput;

import mouseAndKeyboardOutput.*;
import java.awt.Dimension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class TestOutput {
    private Output output;
    
    @BeforeEach
    void setUp() {
        this.output = new NullOutput();
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
        this.output.click(3);
        assertEquals(3, this.output.getLastMouseButton());
        assertEquals(1, this.output.testInt("clicked"));
    }
    
    @Test
    public void testDoubleClick() {
        assertEquals(0, this.output.testInt("clicked"));
        this.output.doubleClick(4);
        assertEquals(4, this.output.getLastMouseButton());
        assertEquals(1, this.output.testInt("clicked"));
    }
    
    @Test
    public void testMouseDown() {
        this.output.mouseDown(4);
        assertEquals(4, this.output.getLastMouseButton());
        this.output.mouseDown(5);
        assertEquals(5, this.output.getLastMouseButton());
    }
    
    @Test
    public void testMouseUp() {
        this.output.mouseUp(4);
        assertEquals(4, this.output.getLastMouseButton());
        this.output.mouseUp(5);
        assertEquals(5, this.output.getLastMouseButton());
    }
    
    @Test
    public void testGetMouseButtonID() {
        assertEquals(5, this.output.getMouseButtonID("left"));
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
        this.output.keyDown(1);
        assertEquals(1, this.output.testInt("lastKey"));
        this.output.keyDown(3);
        assertEquals(3, this.output.testInt("lastKey"));
    }
    
    @Test
    public void testKeyUp() {
        this.output.keyUp(2);
        assertEquals(2, this.output.testInt("lastKey"));
        this.output.keyUp(7);
        assertEquals(7, this.output.testInt("lastKey"));
    }
    
    @Test
    public void testGetKeyID() {
        assertEquals(99, this.output.getKeyID("thing"));
    }
    
    @Test
    public void testGetKeysIKnow() {
        assertEquals(3, this.output.getKeysIKnow().size());
    }
}
