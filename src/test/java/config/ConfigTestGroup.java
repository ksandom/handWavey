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
    private Config config;
    private Group group;

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
        assertTrue(things instanceof Group);
    }

    @Test
    public void testNewGroup() {
        this.group.newGroup("things");
        Group things = this.group.getGroup("things");
        assertTrue(things instanceof Group);
    }

    @Test
    public void testNewItem() {
        this.group.newItem("thing1", "aValue", "Description of thing1");
        Item thing1 = this.group.getItem("thing1");
        assertTrue(thing1 instanceof Item);
        assertEquals(thing1.get(), "aValue");
    }

    @Test
    public void testInlineChange() {
        this.group.getItem("colour").set("square");

        assertEquals(this.group.getItem("colour").get(), "square");
        assertEquals(this.group.getItem("speed").get(), "ludicrous");
    }
}
