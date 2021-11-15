package config;

import config.Config;
import config.Dirt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class DirtTester extends Dirt {
    public DirtTester() {
        super(false);
    }
    
    public void makeDirty() {
        super.makeDirty();
    }
}

public class TestDirt {
    private DirtTester dirt;
    private Config config;
    
    @BeforeEach
    void setUp() {
        this.dirt = new DirtTester();
        this.config = new Config("handWaveyConfigTest.yml");
    }
    
    @AfterEach
    void destroy() {
        this.dirt = null;
        this.config = null;
    }
    
    @Test
    public void testWithoutAssociation() {
        assertEquals(this.dirt.isDirty(), false);
        assertEquals(this.config.isDirty(), false);
        
        this.dirt.makeDirty();
        
        assertEquals(this.dirt.isDirty(), true);
        assertEquals(this.config.isDirty(), false);
    }
    
    @Test
    public void testWithAssociation() {
        assertEquals(this.dirt.isDirty(), false);
        assertEquals(this.config.isDirty(), false);
        
        this.dirt.setConfigManager(this.config);
        this.dirt.makeDirty();
        
        assertEquals(this.dirt.isDirty(), true);
        assertEquals(this.config.isDirty(), true);
    }
}
