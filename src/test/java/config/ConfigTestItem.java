// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package config;

import config.Config;
import config.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigTestItem {
    private String defaultValue;
    private String value;
    private String description;
    private Item item;
    
    @BeforeEach
    void setUp() {
        this.defaultValue = "something";
        this.value = "something else";
        this.description = "A description of a stored item.";
        
        this.item = new Item(this.defaultValue, this.description);
    }
    
    @AfterEach
    void destroy() {
        this.item = null;
    }
    
    @Test
    public void testInitialState() {
        assertEquals(this.item.get(), this.defaultValue);
        assertNotEquals(this.item.get(), this.value);
        assertEquals(this.item.getDescription(), this.description);
        assertEquals(this.item.getDefaultValue(), this.defaultValue);
    }
    
    @Test
    public void testUpdate() {
        assertEquals(this.item.isDirty(), false);
        this.item.set(this.value);
        assertEquals(this.item.get(), this.value);
        assertEquals(this.item.isDirty(), true);
    }

    @Test
    public void testDirtyConfig() {
        Config config = new Config("handWaveyConfigTest.yml");
        assertEquals(config.isDirty(), false);
        assertEquals(this.item.isDirty(), false);
        
        this.item.setConfigManager(config);
        this.item.finishedStartup();
        config.finishedStartup();
        this.item.set(this.value);
        
        assertEquals(this.item.isDirty(), true);
        assertEquals(config.isDirty(), true);
    }

    @Test
    public void testOldValue() {
        this.item.set("thing1");
        assertEquals(this.item.get(), "thing1");
        assertEquals(this.item.getOldValue(), "something");

        this.item.set("thing2");
        assertEquals(this.item.get(), "thing2");
        assertEquals(this.item.getOldValue(), "thing1");
    }
    
    @Test void testOverrideDefault() {
        // Known state?
        assertEquals(this.defaultValue, this.item.get());
        
        // Change the default and see it reflected.
        this.item.overrideDefault("wow");
        assertEquals("wow", this.item.get());
        
        // It should work on subsequent tries.
        this.item.overrideDefault("wow2");
        assertEquals("wow2", this.item.get());
        
        // Changing from default should be reflected in the output.
        this.item.set("wow3");
        assertEquals("wow3", this.item.get());
        
        // It should not affect things once we have a non-default value.
        this.item.overrideDefault("wow4");
        assertEquals("wow3", this.item.get());
    }
    
    @Test
    public void testCanHide() {
        assertEquals(this.item.canHideIfUnchanged(), false);
        this.item.setCanHideIfUnchanged(true);
        assertEquals(this.item.canHideIfUnchanged(), true);
        this.item.setCanHideIfUnchanged(false);
        assertEquals(this.item.canHideIfUnchanged(), false);
    }
    
    @Test
    public void testCanHideByGroup() {
        Group aGroup = new Group();
        Item item1 = aGroup.newItem("item1", "a", "A description.");
        assertEquals(item1.canHideIfUnchanged(), false);
        
        Item item2 = aGroup.newItem("item2", "a", "A description.", true);
        assertEquals(item2.canHideIfUnchanged(), true);
        
        Item item3 = aGroup.newItem("item3", "a", "A description.", false);
        assertEquals(item3.canHideIfUnchanged(), false);
    }
}
