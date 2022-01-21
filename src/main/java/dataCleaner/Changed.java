// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Tracks whether the state of something has changed.

It can provide:
* Whether it has changed.
* The old value.
* The new value.
*/

package dataCleaner;

public class Changed {
    private String strCurrent = "";
    private int intCurrent = 0;
    
    private String strPrevious = "";
    private int intPrevious = 0;
    
    private Boolean changed = false;
    
    public Changed(String value) {
        set(value);
        set(value); // Set a secont time to remove the changed state.
    }
    
    public Changed(int value) {
        set(value);
        set(value); // Set a secont time to remove the changed state.
    }
    
    public void set(String value) {
        if (value != this.strCurrent) {
            this.strPrevious = this.strCurrent;
            this.strCurrent = value;
            this.changed = true;
        } else {
            this.strPrevious = value;
            this.changed = false;
        }
    }
    
    public void set(int value) {
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
}
