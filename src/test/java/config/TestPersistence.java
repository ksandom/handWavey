// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package config;

import config.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

public class TestPersistence {
    Config config = null;

    private void fillConfig(Config config) {
        config.newItem("blah", "niet", "A description of nothing.");

        Group things = config.newGroup("things");
        things.newItem("thing1", "aValue", "A description of that value.");
        things.getItem("thing1").set("aNewValue");
        Group specificThings = things.newGroup("specific");
        Group moreSpecificThings = specificThings.newGroup("moreSpecific");
        moreSpecificThings.newItem("thing2", "anotherValue", "A description of that other value.");

        Group debug = config.newGroup("debug");
        debug.newItem("Persistence", "3", "Debug level for Persistence.");
        debug.newItem("Config", "1", "Debug level for Config.");
    }


    @BeforeEach
    void setUp() {
        // Note that this is intentionally not using the singleton config object for these tests. This is because we want to mess around with the config file locations.
        this.config = new Config("handWaveyConfigTest4");

        fillConfig(this.config);
    }

    @AfterEach
    void destroy() {
        this.config = null;
        System.gc();
    }

    @Test
    public void testTestSetup() {
        assertEquals(this.config.getGroup("things").getItem("thing1").get(), "aNewValue");
        assertEquals(this.config.getGroup("things").getItem("thing1").getOldValue(), "aValue");
        assertEquals(this.config.getGroup("things").getGroup("specific").getGroup("moreSpecific").getItem("thing2").get(), "anotherValue");
    }

    @Test
    public void testSave() {
        assertEquals(this.config.getGroup("things").getItem("thing1").get(), "aNewValue");

        this.config.save();
    }

    @Test
    public void testPersistedChanges() {
        // Take a value from the test data set.
        assertEquals("niet", this.config.getItem("blah").get());

        // Change it so that it's recognisably not our default test set.
        this.config.getItem("blah").set("house");
        assertEquals("house", this.config.getItem("blah").get());

        // Save to disk.
        this.config.save();

        // Create a new config object, and load it with the default test set.
        Config config2 = new Config("handWaveyConfigTest4");
        fillConfig(config2);

        // Load the file into the new object. We should get the changed value.
        config2.load();
        assertEquals("house", config2.getItem("blah").get());
    }

    @Test
    public void testSeparation() {
        // Test default state.
        assertEquals("aNewValue", this.config.getGroup("things").getItem("thing1").get());
        assertEquals("niet", this.config.getItem("blah").get());

        // Separate out the things group.
        this.config.addGroupToSeparate("things");

        // Set some differing values.
        this.config.getGroup("things").getItem("thing1").set("different1");
        this.config.getItem("blah").set("different2");

        // Save to disk.
        this.config.save();

        // Create a new config object, and load it with the default test set.
        Config config2 = new Config("handWaveyConfigTest4");
        config2.addGroupToSeparate("things");
        fillConfig(config2);

        // Load the file into the new object, excluding subgroup files. We should get the changed value. for "blah", but not "thing1" because "thing1" is in the group that was excluded.
        config2.load(false);
        assertEquals("aNewValue", config2.getGroup("things").getItem("thing1").get());
        assertEquals("different2", config2.getItem("blah").get());

        // Load everything.
        config2.load();
        assertEquals("different1", config2.getGroup("things").getItem("thing1").get());
        assertEquals("different2", config2.getItem("blah").get());
    }

    @Test
    public void testMissmatches() {
        // Separate out the things group.
        this.config.addGroupToSeparate("things");

        // Save to disk.
        this.config.save();

        // Check the state.
        assertEquals(false, this.config.separatesMissmatch());

        // Break it.
        this.config.addGroupToSeparate("thingseryasd");

        // Save to disk.
        this.config.save();

        // Check the state.
        assertEquals(true, this.config.separatesMissmatch());
    }

    @Test
    public void testProactiveMissmatches() {
        // Separate out the things group.
        this.config.addGroupToSeparate("things");

        // Proactively test the separates without touching the disk.
        assertEquals(false, this.config.testSeparates()); // <-- Do the test and get the result.
        assertEquals(false, this.config.separatesMissmatch()); // <-- Get the result.

        // Break it.
        this.config.addGroupToSeparate("thingseryasd");

        // Proactively test the separates without touching the disk.
        assertEquals(true, this.config.testSeparates()); // <-- Do the test and get the result.
        assertEquals(true, this.config.separatesMissmatch()); // <-- Get the result.
    }
}
