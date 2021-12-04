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
}
