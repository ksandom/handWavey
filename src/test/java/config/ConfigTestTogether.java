package config;

import config.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigTestTogether {
    private Config config;

    @BeforeEach
    void setUp() {
        this.config = new Config("handWaveyConfigTest.yml");
    }

    @AfterEach
    void destroy() {
        this.config = null;
    }

    @Test
    public void testBasicItems() {
//         this.config
    }
}
