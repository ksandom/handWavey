package config;

import config.Dirt;

class Item extends Dirt{ // An individual piece of configuratoin. A.K.A a setting.
    private String description;
    private String defaultValue;
    private String value;
    
    public Item(String description, String defaultValue) {
        super(false);
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }
    
    public String get() {
        return this.value;
    }
    
    public void set(String value) {
        this.value = value;
        
        makeDirty();
    }
    
    public String getDescription() {
        return this.description;
    }
}
