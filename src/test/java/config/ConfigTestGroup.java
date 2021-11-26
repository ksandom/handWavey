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
        this.group.newItem("colour", "Chosen colour.", "black");
        this.group.newItem("speed", "Perceived speed.", "ludicrous");
        this.group.newItem("shape", "Designed shape", "square");
    }
    
    @AfterEach
    void destroy() {
        this.config = null;
    }
    
    @Test
    public void testSetup() {
        assertEquals(this.group.isDirty(), false);
        
        this.group.newItem("colour2", "Another colour for some reason.", "blue");
        this.group.newItem("inverseSpeed", "Travel, but inside-out.", "0.02");
        this.group.finishedStartup();
        assertEquals(this.group.isDirty(), false);
        
        this.group.newItem("size", "Like, how big is it?", "huuuuuge");
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
        this.group.newItem("thing1", "Description of thing1", "aValue");
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
