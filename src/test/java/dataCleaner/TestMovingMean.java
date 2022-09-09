// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package dataCleaner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import dataCleaner.MovingMean;

class TestMovingMean {
    MovingMean movingMean;

    @BeforeEach
    void setUp() {
        this.movingMean = new MovingMean(4, 1);
    }

    @AfterEach
    void destroy() {
        this.movingMean = null;
        System.gc();
    }

    @Test
    public void testSetup() {
         assertEquals(1, this.movingMean.get());
    }

    @Test
    public void testAddingValues() {
         this.movingMean.set(2);
         assertEquals(1.25, this.movingMean.get());
         this.movingMean.set(2);
         assertEquals(1.5, this.movingMean.get());
         this.movingMean.set(2);
         assertEquals(1.75, this.movingMean.get());
         this.movingMean.set(2);
         assertEquals(2, this.movingMean.get());
    }

    @Test
    public void testSeed() {
        this.movingMean.seed(3);
        assertEquals(3, this.movingMean.get());
    }

    @Test
    public void testNegatives() {
        this.movingMean.set(2);
        this.movingMean.set(-2);
        this.movingMean.set(2);
        this.movingMean.set(-2);
        assertEquals(0, this.movingMean.get());
    }

    @Test
    public void testResize() {
        this.movingMean.set(1);
        this.movingMean.set(1);
        this.movingMean.set(2);
        this.movingMean.set(2);
        assertEquals(1.5, this.movingMean.get());

        this.movingMean.resize(8);
        assertEquals(1.5, this.movingMean.get());

        // TODO Add tests that would show the weaknesses of the current implementation. Specifically around which old values get evited.
        this.movingMean.set(4);
        this.movingMean.set(4);
        this.movingMean.set(4);
        this.movingMean.set(4);
        assertEquals(2.75, this.movingMean.get());

        // This is where the current implementation really shows it's weakness. If it was 100% correct, the value would be 4 (the last 4 entries added were 4, so that would be all that is left.). But because we take the average of everything, and seed the whole array with that, it comes to 2.75. This will need to be fixed at some point for statistical accuracy. But for now this is good enough for what we need.
        this.movingMean.resize(4);
        assertEquals(2.75, this.movingMean.get());
    }
}
