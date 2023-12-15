// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package config;

import config.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;


class ConfigTester extends Config {
    public ConfigTester(String fileName) {
        super(fileName);
    }

    public String getBestPath() {
        return super.getBestPath();
    }
}

public class ConfigTestInternals {
    String directoryName= "";
    ConfigTester ct = null;

    @BeforeEach
    void setUp() {
        this.directoryName = "handWaveyConfigTest";
        this.ct = new ConfigTester(this.directoryName);
    }

    @AfterEach
    void destroy() {
        this.directoryName = null;
        this.ct = null;
    }

    @Test
    public void testBestPath() {
        assertNotNull(this.ct.getBestPath());
    }

    @Test
    public void testFullPath() {
        assertNotNull(this.ct.getFullPath(this.directoryName));
    }

    @Test
    public void testFullPathValue() {
        assertEquals(this.ct.getBestPath() + this.directoryName + File.separator, this.ct.getFullPath(this.directoryName));
    }

    @Test
    public void testDirty() {
        assertEquals(false, this.ct.isDirty());
        this.ct.finishedStartup();
        this.ct.makeDirty();
        assertEquals(true, this.ct.isDirty());
    }
}
