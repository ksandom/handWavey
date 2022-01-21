// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Provides a directory/folder like behaviour for configuration.

Intended to be used to group related configuration items together.
*/

package config;

import config.Dirt;
import config.Item;
import java.util.HashMap;

public class Group extends Dirt { // A collection of related configuration Items.
    private HashMap<String, Item> items = new HashMap<String, Item>();
    private HashMap<String, config.Group> groups = new HashMap<String, config.Group>();

    public Group() {
        super(true);
    }
    
    public Item newItem(String key, String defaultValue, String description) {
        // TODO Check if the item already exists and raise an exception if it does.

        this.items.put(key, new Item(defaultValue, description));
        makeDirty();
        return getItem(key);
    }

    public void put(String key, Item item) {
        this.items.put(key, item);
        
        makeDirty();
    }
    
    public Item getItem(String key) {
        return this.items.get(key);
    }

    public Group newGroup(String key) {
        // TODO Check if the group already exists and raise an exception if it does.

        this.groups.put(key, new Group());
        makeDirty();
        return getGroup(key);
    }

    public Group getGroup(String key) {
        return this.groups.get(key);
    }

    public void finishedStartup() {
        super.finishedStartup();

        for (Item item : this.items.values()) {
            item.finishedStartup();
        }
    }
}
