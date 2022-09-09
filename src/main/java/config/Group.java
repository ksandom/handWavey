// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Provides a directory/folder like behaviour for configuration.

Intended to be used to group related configuration items together.
*/

package config;

import config.Dirt;
import config.Item;
import java.util.HashMap;
import java.util.regex.*;

public class Group extends Dirt { // A collection of related configuration Items.
    private HashMap<String, Item> items = new HashMap<String, Item>();
    protected HashMap<String, config.Group> groups = new HashMap<String, config.Group>();

    private HashMap<String, Item> itemTemplates = new HashMap<String, Item>();

    public Group() {
        super(true);
    }

    public Item newItem(String key, String defaultValue, String description, Boolean canHideIfUnchanged) {
        Item newItem = newItem(key, defaultValue, description);
        newItem.setCanHideIfUnchanged(canHideIfUnchanged);

        return newItem;
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
        Item result = this.items.get(key);

        if (result == null) {
            result = deriveItem(key);
        }

        return result;
    }

    private Item deriveItem(String requestedKey) {
        Item result = null;

        for (String key : this.itemTemplates.keySet()) {
            if (Pattern.matches(key, requestedKey)) {
                Item itemTemplate = this.itemTemplates.get(key);
                result = newItem(key, itemTemplate.getDefaultValue(), itemTemplate.getDescription());
                break;
            }
        }

        return result;
    }

    public void addItemTemplate(String nameRegex, String defaultValue, String description) {
        this.itemTemplates.put(nameRegex, new Item(defaultValue, description));
    }

    public boolean itemCanExist(String key) {
        return (getItem(key) != null);
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

    public HashMap _getGroups() {
        return this.groups;
    }

    public HashMap _getItems() {
        return this.items;
    }
}
