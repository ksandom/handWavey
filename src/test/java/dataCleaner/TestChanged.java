// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package dataCleaner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import dataCleaner.Changed;

class TestChanged {
    Changed changedStr = null;
    Changed changedInt = null;

    @BeforeEach
    void setUp() {
        this.changedStr = new Changed("");
        this.changedInt = new Changed(0);
    }

    @AfterEach
    void destroy() {
        this.changedStr = null;
        this.changedInt = null;
    }

    @Test
    public void testSequence() {
         // Starting state.
         assertEquals(false, this.changedStr.hasChanged());
         assertEquals(false, this.changedInt.hasChanged());
         assertEquals("", this.changedStr.toStr());
         assertEquals(0, this.changedInt.toInt());
         assertEquals("", this.changedStr.fromStr());
         assertEquals(0, this.changedInt.fromInt());


         // Change it.
         this.changedStr.set("something else");
         this.changedInt.set(1);

         assertEquals(true, this.changedStr.hasChanged());
         assertEquals(true, this.changedInt.hasChanged());
         assertEquals("something else", this.changedStr.toStr());
         assertEquals(1, this.changedInt.toInt());
         assertEquals("", this.changedStr.fromStr());
         assertEquals(0, this.changedInt.fromInt());


         // Set the same values again. We should see no change.
         this.changedStr.set("something else");
         this.changedInt.set(1);

         assertEquals(false, this.changedStr.hasChanged());
         assertEquals(false, this.changedInt.hasChanged());
         assertEquals("something else", this.changedStr.toStr());
         assertEquals(1, this.changedInt.toInt());
         assertEquals("something else", this.changedStr.fromStr());
         assertEquals(1, this.changedInt.fromInt());


         // A new change.
         this.changedStr.set("another value");
         this.changedInt.set(-1);

         assertEquals(true, this.changedStr.hasChanged());
         assertEquals(true, this.changedInt.hasChanged());
         assertEquals("another value", this.changedStr.toStr());
         assertEquals(-1, this.changedInt.toInt());
         assertEquals("something else", this.changedStr.fromStr());
         assertEquals(1, this.changedInt.fromInt());


         // Changing back do the starting values should be a normal change.
         this.changedStr.set("");
         this.changedInt.set(0);

         assertEquals(true, this.changedStr.hasChanged());
         assertEquals(true, this.changedInt.hasChanged());
         assertEquals("", this.changedStr.toStr());
         assertEquals(0, this.changedInt.toInt());
         assertEquals("another value", this.changedStr.fromStr());
         assertEquals(-1, this.changedInt.fromInt());
    }
}
