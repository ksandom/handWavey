// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package handWavey;

import handWavey.HandWaveyConfig;
import config.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class TestHandWaveyConfig {
    private HandWaveyConfig handWaveyConfig;

    @BeforeEach
    void setUp() {
        this.handWaveyConfig = new HandWaveyConfig("unitTest");
    }

    @AfterEach
    void destroy() {
        this.handWaveyConfig = null;
    }

    @Test
    public void testID() {
        this.handWaveyConfig.defineGeneralConfig();
        
        assertEquals("2021-11-26", Config.singleton().getItem("configFormatVersion").get());
    }
}

