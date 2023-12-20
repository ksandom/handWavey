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

public class Changed {
    private Debug debug;

    private String name = "Unnamed";

    private String strCurrent = "";
    private int intCurrent = 0;

    private String strPrevious = "";
    private int intPrevious = 0;

    private Boolean changed = false;

    private Boolean isInt = false;
    private Boolean missmatch = false;

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

    public void set(String value) {
        if (isInt) {
            this.debug.out(1, "Was initialised as an int, but just received a string. This is a bug in " + name);
            missmatch = true;
        }

        if (!value.equals(this.strCurrent)) {
            this.strPrevious = this.strCurrent;
            this.strCurrent = value;
            this.changed = true;
        } else {
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
            this.intPrevious = this.intCurrent;
            this.intCurrent = value;
            this.changed = true;
        } else {
            this.intPrevious = value;
            this.changed = false;
        }
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
}
