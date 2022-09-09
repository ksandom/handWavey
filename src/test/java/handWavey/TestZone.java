// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package handWavey;

import handWavey.Zone;
import handWavey.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;


// TODO We need to handle the case of the hand no longer being seen while a mouse down state is present. The mouse should be released so that the desktop returns to being usable.


class TestZone {
    Zone zone;

    @BeforeEach
    void setUp() {
        this.zone = new Zone(-150, 50, 1, 11);
    }

    @AfterEach
    void destroy() {
        this.zone = null;
        System.gc();
    }

    @Test
    public void testPosition() {
        assertEquals(0, this.zone.getDepthPercent(-180));
        assertEquals(0, this.zone.getDepthPercent(-150));
        assertEquals(0.1, this.zone.getDepthPercent(-130));
        assertEquals(0.5, this.zone.getDepthPercent(-50));
        assertEquals(1, this.zone.getDepthPercent(50));
        assertEquals(1, this.zone.getDepthPercent(150));
    }

    @Test
    public void testMovingMeanWidth() {
        assertEquals(1, this.zone.getMovingMeanWidth(-150));
        assertEquals(2, this.zone.getMovingMeanWidth(-130));
        assertEquals(2, this.zone.getMovingMeanWidth(-121));
        assertEquals(3, this.zone.getMovingMeanWidth(-120));
        assertEquals(3, this.zone.getMovingMeanWidth(-119));
        assertEquals(10, this.zone.getMovingMeanWidth(30));
        assertEquals(11, this.zone.getMovingMeanWidth(49));
        assertEquals(11, this.zone.getMovingMeanWidth(50));
        assertEquals(11, this.zone.getMovingMeanWidth(51));
    }
}
