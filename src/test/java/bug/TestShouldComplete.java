// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package bug;

import bug.ShouldComplete;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

public class TestShouldComplete {
    private ShouldComplete shouldComplete = null;
    
    @BeforeEach
    void setUp() {
        this.shouldComplete = new ShouldComplete("A test context");
    }
    
    @AfterEach
    void destroy() {
        this.shouldComplete = null;
    }
    
    @Test
    public void testAValidSequence() {
        this.shouldComplete.start("A successful thing;");
        this.shouldComplete.finish();
        
        assertEquals(true, this.shouldComplete.start("A another thing;"));
    }
    
    @Test
    public void testInvalidSequence() {
        this.shouldComplete.start("An unsuccessful thing;");
        
        assertEquals(false, this.shouldComplete.start("A another thing;"));
    }
    
    @Test
    public void testFailedString() {
        this.shouldComplete.start("A successful thing;");
        this.shouldComplete.finish();
        this.shouldComplete.start("An unsuccessful thing;");
        
        assertEquals(false, this.shouldComplete.start("A another thing;"));
    }
    
    @Test
    public void testSequence() {
        // We start a successful operation. Nothing has failed yet.
        assertEquals(true, this.shouldComplete.start("A successful thing;"));
        assertEquals("", this.shouldComplete.getUnfinishedOperation());
        this.shouldComplete.finish();
        assertEquals("", this.shouldComplete.getUnfinishedOperation());
        
        // We start an unsuccessful operation. Nothing has failed yet.
        assertEquals(true, this.shouldComplete.start("An unsuccessful thing;"));
        assertEquals("", this.shouldComplete.getUnfinishedOperation());
        
        // We start a new operation and detect the previous failure.
        assertEquals(false, this.shouldComplete.start("A another thing;"));
        assertEquals("An unsuccessful thing;", this.shouldComplete.getUnfinishedOperation());
        
        // Upon completion, the unfinished operation should clear.
        this.shouldComplete.finish();
        assertEquals("", this.shouldComplete.getUnfinishedOperation());
        
    }
}
