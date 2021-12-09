package dataCleaner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import dataCleaner.PointsCleaner;

class PointsCleanerTester extends PointsCleaner {
    public PointsCleanerTester (int size, double seedValue, double maxChange, int minSequential) {
        super(size, seedValue, maxChange, minSequential);
    }

    public int getPreviousPosition(int steps) {
        return super.getPreviousPosition(steps);
    }
}

class TestPointsCleaner {
    PointsCleanerTester pointsCleaner;

    @BeforeEach
    void setUp() {
        this.pointsCleaner = new PointsCleanerTester(4, 1, 2, 2);
    }

    @AfterEach
    void destroy() {
        this.pointsCleaner = null;
    }

    @Test
    public void testPreviousPositions() {
        assertEquals(3, this.pointsCleaner.getPreviousPosition(1));
        assertEquals(2, this.pointsCleaner.getPreviousPosition(2));
        assertEquals(1, this.pointsCleaner.getPreviousPosition(3));
        assertEquals(0, this.pointsCleaner.getPreviousPosition(4));
        assertEquals(3, this.pointsCleaner.getPreviousPosition(5));
    }

}
