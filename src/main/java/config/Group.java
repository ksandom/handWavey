package config;

import config.Dirt;
import config.Item;
import java.util.HashMap;

class Group extends Dirt { // A collection of related configuration Items.
    private HashMap<String, Item> items = new HashMap<String, Item>();
    
    public Group() {
        super(true);
    }
    
    public void put(String key, Item item) {
        this.items.put(key, item);
        
        makeDirty();
    }
    
    public String get(String key) {
        return this.items.get(key).get();
    }
}
