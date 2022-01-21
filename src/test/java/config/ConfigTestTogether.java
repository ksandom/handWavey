// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package config;

import config.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigTestTogether {
    @Test
    public void testSingleton() {
        Config.setSingletonFilename("handWaveyConfigTest2.yml");
        
        // This way is faster when dealing with multiple transactions.
        Group things = Config.singleton().newGroup("things");
        Item thing1 = things.newItem("thing1", "aValue", "A description of that value.");
        assertEquals(thing1.get(), "aValue");
        
        // This way can be done with less code, and may be acceptable when doing rarely executed tasks.
        Config.singleton().getGroup("things").getItem("thing1").set("aNewValue");
        assertEquals(Config.singleton().getGroup("things").getItem("thing1").get(), "aNewValue");
        assertEquals(Config.singleton().getGroup("things").getItem("thing1").getOldValue(), "aValue");
    }
}
