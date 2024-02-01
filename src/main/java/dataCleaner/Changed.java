// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Tracks whether the state of something has changed.

It can provide:
* Whether it has changed.
* The old value.
* The new value.
*/

package dataCleaner;

import debug.Debug;
import java.util.Date;

public class Changed {
    private Debug debug;

    private String name = "Unnamed";

    private String strNext = "";
    private int intNext = 0;

    private String strCurrent = "";
    private int intCurrent = 0;

    private String strPrevious = "";
    private int intPrevious = 0;

    private Boolean changed = false;

    private Boolean isInt = false;
    private Boolean missmatch = false;

    private Boolean useChangeTimeout = false;
    private long changeTimeout = 0;
    private long timeoutWhen = 0;

    public Changed(String value, String name) {
        isInt = false;
        set(value);
        set(value); // Set a second time to remove the changed state.
        this.name = name;
        debug = Debug.getDebug("Changed");
    }

    public Changed(int value, String name) {
        isInt = true;
        set(value);
        set(value); // Set a second time to remove the changed state.
        this.name = name;
        debug = Debug.getDebug("Changed");
    }

    public void enableTimeout(Boolean enabled) {
        this.useChangeTimeout = enabled;
        this.endTimeout();
    }

    public void setTimeout(long timeout) {
        this.useChangeTimeout = true;
        this.changeTimeout = timeout;
    }

    public void set(String value) {
        if (isInt) {
            this.debug.out(1, "Was initialised as an int, but just received a string. This is a bug in " + name);
            missmatch = true;
        }

        if (!value.equals(this.strCurrent)) {
            if (this.timeoutPassed()) {
                // Change. And we're ready to pass it on.
                this.strPrevious = this.strCurrent;
                this.strCurrent = value;
                this.changed = true;
                this.strNext = "";
            } else {
                if (this.strNext.equals("")) {
                    // Time to set up the time out tracking.
                    this.strNext = value;
                } else {
                    if (!this.strNext.equals(value)) {
                        // We got a new value before the timeout expired. So we reset the timer.
                        this.strNext = value;
                        this.beginTimeout();
                    }
                }

                this.strPrevious = value;
                this.changed = false;
            }
        } else {
            // No change.
            this.strPrevious = value;
            this.changed = false;
        }
    }

    public void set(int value) {
        if (!isInt) {
            this.debug.out(1, "Was initialised as a string, but just received an int. This is a bug in " + name);
            missmatch = true;
        }

        if (value != this.intCurrent) {
            if (this.timeoutPassed()) {
                // Change. And we're ready to pass it on.
                this.intPrevious = this.intCurrent;
                this.intCurrent = value;
                this.changed = true;
                this.intNext = 0;
            } else {
                if (this.intNext == 0) {
                    // Time to set up the time out tracking.
                    this.intNext = value;
                } else {
                    if (this.intNext != value) {
                        // We got a new value before the timeout expired. So we reset the timer.
                        this.intNext = value;
                        this.beginTimeout();
                    }
                }

                this.intPrevious = value;
                this.changed = false;
            }
        } else {
            // No change.
            this.intPrevious = value;
            this.changed = false;
        }
    }

    public Boolean timeoutPassed() {
        if (this.useChangeTimeout) {
            if (this.timeoutWhen == 0) {
                // Time to begin the timeout.
                this.beginTimeout();
                return false;
            } else {
                if (this.timeInMilliseconds() > this.timeoutWhen) {
                    // Timeout has begun, and finished.
                    this.endTimeout();
                    return true;
                } else {
                    // Timeout has begun, and is still in progress.
                    return false;
                }
            }
        } else {
            return true;
        }
    }

    public void beginTimeout() {
        this.timeoutWhen = this.timeInMilliseconds() + this.changeTimeout;
    }

    public void endTimeout() {
        this.timeoutWhen = 0;
    }

    public Boolean hasChanged() {
        return this.changed;
    }

    public String fromStr() {
        return this.strPrevious;
    }

    public int fromInt() {
        return this.intPrevious;
    }

    public String toStr() {
        return this.strCurrent;
    }

    public int toInt() {
        return this.intCurrent;
    }

    public Boolean getMissmatch() {
        return missmatch;
    }

    private long timeInMilliseconds() {
        Date date = new Date();
        return date.getTime();
    }
}
