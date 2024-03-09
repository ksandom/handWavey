// (c) 2023 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Tracks whether a boolean value has consistently been an expected value within a specified time range.

It can provide:
* Has it been consistently the expected value yet?
*/

package dataCleaner;

// import debug.Debug;
import java.util.Date;

public class Consistently {
    Boolean initialised = false;

    String name = "Unnamed";

    Boolean expectedValue = false;
    long minMilliseconds = 0;
    Boolean useMax = false;
    long maxMilliseconds = 0;

    long firstMatchingTick = 0;

    long overrideTime = 0;

    public Consistently(Boolean expectedValue, long minMilliseconds, String name) {
        this.expectedValue = expectedValue;
        this.minMilliseconds = minMilliseconds;
        this.name = name;
    }

    // If an upper limit is wanted. This is where to set it.
    public void setMax(long maxMilliseconds) {
        this.maxMilliseconds = maxMilliseconds;
        this.useMax = true;
    }

    // To be called every time we have new data.
    public void tick(Boolean value) {
        if (value == this.expectedValue) {
            if (this.firstMatchingTick == 0) {
                this.firstMatchingTick = timeInMilliseconds();
            }
        } else {
            reset();
        }

        this.initialised = true;
    }

    // Does the current state match our criteria?
    // Note that we don't need to call this to track anything. All of that is done with tick. So we only need to call this when we want to know something.
    public Boolean isConsistent() {
        if (!this.initialised) {
            return false;
        }

        if (this.firstMatchingTick == 0) { // TODO check this.
            return false;
        }

        long now = timeInMilliseconds();
        long duration = now - this.firstMatchingTick;

        if (duration < this.minMilliseconds) {
            return false;
        }

        if (this.useMax) {
            if (duration > this.maxMilliseconds) {
                return false;
            }
        }

        return true;
    }

    // Regardless of whether we match, stop it so that the next tick starts from scratch.
    public void reset() {
        this.firstMatchingTick = 0;
    }

    // Get the current time in milliseconds.
    private long timeInMilliseconds() {
        if (this.overrideTime != 0) {
            return this.overrideTime;
        }

        Date date = new Date();
        return date.getTime();
    }

    // This is purely for unit testing.
    public void overrideTime(long overrideTime) {
        this.overrideTime = overrideTime;
    }

}
