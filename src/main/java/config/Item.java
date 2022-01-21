// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Tracks a single configuration item.
*/

package config;

import config.Dirt;

public class Item extends Dirt{ // An individual piece of configuration. A.K.A a setting.
    private String description;
    private String defaultValue;
    private String value;
    private String oldValue; // Memory only.
    
    public Item(String defaultValue, String description) {
        super(false);
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.oldValue = defaultValue;
    }
    
    public String get() {
        return this.value;
    }

    public String getOldValue() {
        return this.oldValue;
    }
    
    public void set(String value) {
        this.oldValue = this.value;
        this.value = value;
        
        makeDirty();
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void overrideDefault(String newValue) {
        if (this.value == this.defaultValue) {
            this.value = newValue;
            this.oldValue = newValue;
        }
        
        this.defaultValue = newValue;
    }
}
