// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package config;

import config.Config;
import config.Dirt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class DirtTester extends Dirt {
    public DirtTester(Boolean state) {
        super(state);
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
        this.dirt = new DirtTester(true);
        this.config = new Config("handWaveyConfigTest.yml");
    }

    @AfterEach
    void destroy() {
        this.dirt = null;
        this.config = null;
        System.gc();
    }

    @Test
    public void testWithoutAssociation() {
        assertEquals(this.dirt.isDirty(), false);
        assertEquals(this.config.isDirty(), false);

        this.dirt.finishedStartup();
        this.dirt.makeDirty();

        assertEquals(this.dirt.isDirty(), true);
        assertEquals(this.config.isDirty(), false);
    }

    @Test
    public void testSEtupCorrectly() {
        assertNotNull(this.dirt);
        assertNotNull(this.config);
    }

    @Test
    public void testWithAssociation() {
        assertEquals(this.dirt.isDirty(), false);
        assertEquals(this.config.isDirty(), false);
        assertEquals(this.dirt.isStartingUp(), true);

        this.dirt.finishedStartup();
        this.config.finishedStartup();

        assertEquals(this.dirt.isStartingUp(), false);
        assertNull(this.dirt.getConfigManager());

        this.dirt.setConfigManager(this.config);

        assertNotNull(this.dirt.getConfigManager());

        this.dirt.makeDirty();

        assertEquals(this.dirt.isDirty(), true);
        assertEquals(this.dirt.getConfigManager().isDirty(), true);
        assertEquals(this.config.isDirty(), true);
    }
}
