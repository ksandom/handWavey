// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package config;

import config.Config;
import config.Item;
import config.Group;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigTestGroup {
    private Config config = null;
    private Group group = null;

    @BeforeEach
    void setUp() {
        this.config = new Config("handWaveyConfigTest.yml");
        this.group = new Group();
        this.group.newItem("colour", "black", "Chosen colour.");
        this.group.newItem("speed", "ludicrous", "Perceived speed.");
        this.group.newItem("shape", "square", "Designed shape");
    }

    @AfterEach
    void destroy() {
        this.config = null;
        this.group = null;
        System.gc();
    }

    @Test
    public void testSetup() {
        assertEquals(this.group.isDirty(), false);

        this.group.newItem("colour2", "blue", "Another colour for some reason.");
        this.group.newItem("inverseSpeed", "0.02", "Travel, but inside-out.");
        this.group.finishedStartup();
        assertEquals(this.group.isDirty(), false);

        this.group.newItem("size", "huuuuuge", "Like, how big is it?");
        assertEquals(this.group.isDirty(), true);
    }

    @Test
    public void testReturnedValues() {
        assertEquals(this.group.getItem("colour").get(), "black");
        assertEquals(this.group.getItem("speed").get(), "ludicrous");
        assertEquals(this.group.getItem("shape").get(), "square");
    }

    @Test
    public void testSubGroup() {
        Group things = this.group.newGroup("things");
        assertNotNull(things);
    }

    @Test
    public void testGetSubGroup() {
        this.group.newGroup("things");
        Group things = this.group.getGroup("things");
        assertNotNull(things);
    }

    @Test
    public void testNewItem() {
        this.group.newItem("thing1", "aValue", "Description of thing1");
        Item thing1 = this.group.getItem("thing1");
        assertNotNull(thing1);
        assertEquals(thing1.get(), "aValue");
    }

    @Test
    public void testInlineChange() {
        this.group.getItem("colour").set("square");

        assertEquals(this.group.getItem("colour").get(), "square");
        assertEquals(this.group.getItem("speed").get(), "ludicrous");
    }

    @Test
    public void testTemplate() {
        // Items don't exist, and we haven't created a template for them.
        assertEquals(this.group.getItem("notYet"), null);
        assertEquals(this.group.itemCanExist("notYet"), false);

        // Create templates.
        this.group.addItemTemplate("^.*Now$", "a balloon", "A lovely description of the config item.");
        this.group.addItemTemplate("^then.*$", "a giraffe", "Another lovely description of the config item.");

        // This should still not exist because it doesn't match the regex.
        assertEquals(this.group.getItem("notYet"), null);
        assertEquals(this.group.itemCanExist("notYet"), false);
        assertEquals(this.group.itemCanExist("notYet"), false); // This should still be false.


        // This should match the first template only.
        assertEquals(this.group.getItem("untilNow").get(), "a balloon");
        assertEquals(this.group.getItem("untilNow").getDescription(), "A lovely description of the config item.");

        // A separate match, because the previous test will have created an item, and we want to know that it works on a non-existant item.
        assertEquals(this.group.itemCanExist("justNow"), true);
        assertEquals(this.group.itemCanExist("justNow"), true); // And on an item that should exist now.


        // This should match the second template only.
        assertEquals(this.group.getItem("thenItWillWork").get(), "a giraffe");
        assertEquals(this.group.getItem("thenItWillWork").getDescription(), "Another lovely description of the config item.");

        // A separate match, because the previous test will have created an item, and we want to know that it works on a non-existant item.
        assertEquals(this.group.itemCanExist("thenAnother"), true);
        assertEquals(this.group.itemCanExist("thenAnother"), true); // And on an item that should exist now.
    }
}
