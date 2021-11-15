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
    private Item item1;
    private Item item2;
    private Item item3;
    
    @BeforeEach
    void setUp() {
        this.config = new Config("handWaveyConfigTest.yml");
        this.group = new Group();
        this.item1 = new Item("Chosen colour.", "black");
        this.item2 = new Item("Perceived speed.", "ludicrous");
        this.item3 = new Item("Designed shape", "square");
    }
    
    @AfterEach
    void destroy() {
        this.config = null;
    }
    
    @Test
    public void testSetup() {
        assertEquals(this.group.isDirty(), false);
        
        this.group.put("colour", this.item1);
        this.group.put("speed", this.item2);
        this.group.finishedStartup();
        assertEquals(this.group.isDirty(), false);
        
        this.group.put("shape", this.item3);
        assertEquals(this.group.isDirty(), true);
    }
    
    @Test
    public void testReturnedValues() {
        this.group.put("colour", this.item1);
        this.group.put("speed", this.item2);
        this.group.put("shape", this.item3);
        
        assertEquals(this.group.get("colour"), "black");
        assertEquals(this.group.get("speed"), "ludicrous");
        assertEquals(this.group.get("shape"), "square");
    }
    
    @Test
    public void testOverwrite() {
        this.group.put("colour", this.item1);
        this.group.put("speed", this.item2);
        this.group.put("colour", this.item3);
        
        assertEquals(this.group.get("colour"), "square");
        assertEquals(this.group.get("speed"), "ludicrous");
    }
}
