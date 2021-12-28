package handWavey;

import handWavey.HandWaveyConfig;
import config.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class TestHandSummary {
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
        handWaveyConfig.defineGeneralConfig();
        
        assertEquals("2021-11-26", Config.singleton().getItem("configFormatVersion").get());
    }
}

