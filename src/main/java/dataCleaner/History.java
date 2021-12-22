package dataCleaner;

import java.util.Arrays;
import java.sql.Timestamp;

public class History {
    private int size = 0;
    private int position = 0;
    private long[] timeStamp = new long[4096];
    private double[] data = new double[4096];
    
    public History (int size, double seedValue) {
        this.size = size;
        this.position = 0;
        
        seed(seedValue);
    }

    // Make all values the same as a starting point. This is great for transitioning from a mode that doesn't use this instance of the moving mean. That way the result is immediately at the expected, and can continue from there.
    public void seed(double value) {
        // A long time ago.
        Timestamp beginningOfTime = new Timestamp(1970, 1, 1, 1, 1, 1, 1);
        
        Arrays.fill(this.timeStamp, 0, this.size, beginningOfTime.getTime());
        Arrays.fill(this.data, 0, this.size, value);
    }
    
    public void set(double value) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        
        step();
        this.data[this.position] = value;
        this.timeStamp[this.position] = now.getTime();
    }
    
    private void step() {
        this.position++;
        if (this.position > this.size - 1) {
            this.position = 0;
        }
    }
    
    protected int getIndexForOffset(int offset) {
        int result = this.position - offset;
        while (result < 0) result += this.size;
        
        return result;
    }
    
    private long getTimestampForOffset(int offset) {
        return this.timeStamp[this.getIndexForOffset(offset)];
    }
    
    private int getIndexForTimeOffset(long timeOffset) {
        long timestamp = new Timestamp(System.currentTimeMillis()).getTime() - timeOffset;
        return getIndexForTimestamp(timestamp);
    }
    
    protected int getIndexForTimestamp(long timestamp) {
        int offset = findOffsetForTimestamp(timestamp, this.size - 1, 0, 0);
        int index = getIndexForOffset(offset);
        System.out.println(offset);
        System.out.println(index);
        return index;
    }
    
    private int findOffsetForTimestamp(long timestamp, int start, int stop, int step) {
        long startTimestamp = this.getTimestampForOffset(start);
        long stopTimestamp = this.getTimestampForOffset(stop);
        
        long timeRange = stopTimestamp - startTimestamp;
        long goal = timestamp - startTimestamp;
        int indexRange = start - stop;
        double positionPercent = 0.5 ; //= goal / timeRange;
        
        int guessPosition = (int)Math.round(indexRange * positionPercent);
        int guess = indexRange - guessPosition + stop;
        long guessTimestamp = this.getTimestampForOffset(guess);
        
        System.out.println(String.valueOf(step) + ": ts=" + String.valueOf(timestamp) + ", gts=" + String.valueOf(guessTimestamp) + ", start=" + String.valueOf(start) + ", guess=" + String.valueOf(guess) + ", stop=" + String.valueOf(stop) + ", startTS=" + String.valueOf(startTimestamp) + ", stopTS=" + String.valueOf(stopTimestamp) + ", guessPos=" + String.valueOf(guessPosition) + ", timeRange=" + String.valueOf(timeRange) + ", goal=" + String.valueOf(goal) + ", indexRange=" + String.valueOf(indexRange) + ", positionPercent=" + String.valueOf(positionPercent));
        
        if (timestamp >= stopTimestamp) {
            System.out.println("outside stop=" + String.valueOf(stop));
            return stop; // It's younger than our youngest entry. Use that.
        }
        if (timestamp <= startTimestamp) {
            System.out.println("outside start=" + String.valueOf(start));
            return start; // It's older than our oldest entry. Use that.
        }
        if (start == stop) {
            System.out.println("start==stop=" + String.valueOf(stop));
            return start; // There's nothing left to test. Just return what we have.
        }
        
        long highGuessDiff = this.getTimestampForOffset(guess-1) - timestamp;
        long lowGuessDiff = timestamp - this.getTimestampForOffset(guess+1);
        
        
        
        if (highGuessDiff < lowGuessDiff) {
            return findOffsetForTimestamp(timestamp, guess, stop, step + 1);
        } else {
            return findOffsetForTimestamp(timestamp, start, guess, step + 1);
        }
    }
    
    // This is purely used for unit testing.
    protected void overrideTimestamp(int offset, long newTimestamp) {
        this.timeStamp[getIndexForOffset(offset)] = newTimestamp;
    }
}
