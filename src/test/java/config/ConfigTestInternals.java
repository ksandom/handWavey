package config;

import config.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;


class ConfigTester extends Config {
    public ConfigTester(String fileName) {
        super(fileName);
    }
    
    public String getBestPath() {
        return super.getBestPath();
    }
}

public class ConfigTestInternals {
    String fileName;
    ConfigTester ct;
    
    @BeforeEach
    void setUp() {
        this.fileName = "handWaveyConfigTest.yml";
        this.ct = new ConfigTester(this.fileName);
    }
    
    @AfterEach
    void destroy() {
        this.fileName = null;
        this.ct = null;
    }
    
    @Test
    public void testBestPath() {
        assertNotNull(this.ct.getBestPath());
    }
    
    @Test
    public void testFullPath() {
        assertNotNull(this.ct.getFullPath(this.fileName));
    }
    
    @Test
    public void testFullPathValue() {
        assertEquals(this.ct.getFullPath(this.fileName), this.ct.getBestPath() + this.fileName);
    }
    
    @Test
    public void testDirty() {
        assertEquals(this.ct.isDirty(), false);
        this.ct.makeDirty();
        assertEquals(this.ct.isDirty(), true);
    }
}
