// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package dataCleaner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import dataCleaner.Consistently;

class TestConsistently {
    Consistently consistentlySomething = null;

    @BeforeEach
    void setUp() {
        this.consistentlySomething = new Consistently(true, 100, "Unit test");
        this.consistentlySomething.overrideTime(1000);
    }

    @AfterEach
    void destroy() {
        this.consistentlySomething = null;
    }

    @Test
    public void testBasicMin() {
         // Set up the initial state.
         this.consistentlySomething.tick(true);

         // Test true, but not enough time passed.
         this.consistentlySomething.overrideTime(1010);
         this.consistentlySomething.tick(true);
         assertEquals(false, this.consistentlySomething.isConsistent());

         // Test true, and enough time passed.
         this.consistentlySomething.overrideTime(1110);
         this.consistentlySomething.tick(true);
         assertEquals(true, this.consistentlySomething.isConsistent());

         // Test still true after more time.
         this.consistentlySomething.overrideTime(2000);
         this.consistentlySomething.tick(true);
         assertEquals(true, this.consistentlySomething.isConsistent());
    }

    @Test
    public void testMinMax() {
         // Set up the initial state.
         this.consistentlySomething.tick(true);
         this.consistentlySomething.setMax(200);

         // Test true, but not enough time passed.
         this.consistentlySomething.overrideTime(1010);
         this.consistentlySomething.tick(true);
         assertEquals(false, this.consistentlySomething.isConsistent());

         // Test true, and enough time passed.
         this.consistentlySomething.overrideTime(1110);
         this.consistentlySomething.tick(true);
         assertEquals(true, this.consistentlySomething.isConsistent());

         // Test true, and enough time passed.
         this.consistentlySomething.overrideTime(2000);
         this.consistentlySomething.tick(true);
         assertEquals(false, this.consistentlySomething.isConsistent());
    }

    @Test
    public void testBroken() {
         // Set up the initial state.
         this.consistentlySomething.tick(true);

         // Test true, but not enough time passed.
         this.consistentlySomething.overrideTime(1010);
         this.consistentlySomething.tick(true);
         assertEquals(false, this.consistentlySomething.isConsistent());

         // Test false, and enough time passed.
         this.consistentlySomething.overrideTime(1110);
         this.consistentlySomething.tick(false);
         assertEquals(false, this.consistentlySomething.isConsistent());

         // Test true, not enough time.
         this.consistentlySomething.overrideTime(1120);
         this.consistentlySomething.tick(true);
         assertEquals(false, this.consistentlySomething.isConsistent());

         // Test true, enough time.
         this.consistentlySomething.overrideTime(1220);
         this.consistentlySomething.tick(true);
         assertEquals(true, this.consistentlySomething.isConsistent());
    }
}
