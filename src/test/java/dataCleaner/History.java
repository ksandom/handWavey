// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package dataCleaner;

import java.sql.Timestamp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import dataCleaner.History;

class HistoryTester extends History {
    public HistoryTester(int size, double seedValue) {
        super(size, seedValue);
    }

    public void overrideTimestamp(int offset, long newTimestamp) {
        super.overrideTimestamp(offset, newTimestamp);
    }

    public int getIndexForTimestamp(long timestamp) {
        return super.getIndexForTimestamp(timestamp);
    }

    public int getIndexForOffset(int offset) {
        return super.getIndexForOffset(offset);
    }
}

class TestHistory {
    HistoryTester history;
    Timestamp now = new Timestamp(System.currentTimeMillis());
    long nowMillis = now.getTime();
    int[] index = new int[4096];
    int size = 10;

    @BeforeEach
    void setUp() {
        this.history = new HistoryTester(this.size, 1);

        // Add some data. It should wrap around, and we should be left with values from 0-9.
        int dataCount = this.size + 2;
        for (int dataPoint = dataCount; dataPoint>-1; dataPoint--) {
            this.history.set(dataPoint);
        }

        // Get a map of the offsets to simplify our testing.
        for (int i=0; i<this.size; i++) {
            this.index[i] = this.history.getIndexForOffset(i);
            System.out.println("index(" + String.valueOf(i) + ")=" + String.valueOf(this.index[i]));
        }

        // Override the timestamps to simulate a longer period of time.
        for (int offset = 0; offset<this.size; offset++) {
            this.history.overrideTimestamp(offset, this.nowMillis - ((long)offset * 100));
        }
    }

    @AfterEach
    void destroy() {
        this.history = null;
    }

    @Test
    public void testTimeOffset() {
         assertEquals(this.index[0], this.history.getIndexForTimestamp(this.nowMillis - 0));
         assertEquals(this.index[1], this.history.getIndexForTimestamp(this.nowMillis - 100));
         assertEquals(this.index[2], this.history.getIndexForTimestamp(this.nowMillis - 200));
         assertEquals(this.index[3], this.history.getIndexForTimestamp(this.nowMillis - 300));
         assertEquals(this.index[4], this.history.getIndexForTimestamp(this.nowMillis - 400));
         assertEquals(this.index[5], this.history.getIndexForTimestamp(this.nowMillis - 500));
         assertEquals(this.index[6], this.history.getIndexForTimestamp(this.nowMillis - 600));
         assertEquals(this.index[7], this.history.getIndexForTimestamp(this.nowMillis - 700));
         assertEquals(this.index[8], this.history.getIndexForTimestamp(this.nowMillis - 800));
         assertEquals(this.index[9], this.history.getIndexForTimestamp(this.nowMillis - 900));

         // Some in-betweens.
         assertEquals(this.index[7], this.history.getIndexForTimestamp(this.nowMillis - 660));
         assertEquals(this.index[7], this.history.getIndexForTimestamp(this.nowMillis - 680));
         assertEquals(this.index[7], this.history.getIndexForTimestamp(this.nowMillis - 720));
         assertEquals(this.index[7], this.history.getIndexForTimestamp(this.nowMillis - 740));
         assertEquals(this.index[8], this.history.getIndexForTimestamp(this.nowMillis - 760));
         assertEquals(this.index[8], this.history.getIndexForTimestamp(this.nowMillis - 780));
    }

    @Test
    public void testGet() {
         assertEquals(0, this.history.get(this.nowMillis - 0));
         assertEquals(1, this.history.get(this.nowMillis - 100));
         assertEquals(2, this.history.get(this.nowMillis - 200));
         assertEquals(3, this.history.get(this.nowMillis - 300));
    }

    @Test
    public void testGetSumFrom() {
         assertEquals(0, this.history.getSumFrom(this.nowMillis - 0));
         assertEquals(1, this.history.getSumFrom(this.nowMillis - 100));
         assertEquals(3, this.history.getSumFrom(this.nowMillis - 200));
         assertEquals(6, this.history.getSumFrom(this.nowMillis - 300));
    }
}
