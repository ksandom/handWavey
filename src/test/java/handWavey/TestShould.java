// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package handWavey;

import handWavey.Should;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class TestShould {
    private Should should;

    @BeforeEach
    void setUp() {
        this.should = new Should(false);
    }

    @AfterEach
    void destroy() {
        this.should = null;
    }

    @Test
    public void testRawValues() {
        assertEquals(false, this.should.toFalse());
        assertEquals(false, this.should.toTrue());
        
        this.should.set(true);
        
        assertEquals(false, this.should.toFalse());
        assertEquals(true, this.should.toTrue());
        assertEquals(false, this.should.toFalse());
        assertEquals(false, this.should.toTrue());
        
        this.should.set(false);
        
        assertEquals(true, this.should.toFalse());
        assertEquals(false, this.should.toTrue());
        assertEquals(false, this.should.toFalse());
        assertEquals(false, this.should.toTrue());
   }

}
