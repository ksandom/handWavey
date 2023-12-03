// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

package handWavey;

import handWavey.HandWaveyConfig;
import config.Config;
import config.Group;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class TestHandWaveyConfig {
    private HandWaveyConfig handWaveyConfig = null;

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

    @Test
    public void testCofigTemplates() {
        this.handWaveyConfig.defineGeneralConfig();

        assertNotNull(Config.singleton().getGroup("debug").itemCanExist("bug.ShouldComplete/MacroLine/line-0"));
    }

    @Test
    public void testThingsThatBroke() {
        Group debug = Config.singleton().getGroup("debug");
        debug.addItemTemplate(
            "bug.ShouldComplete.MacroCore.instruction-.*",
            "0",
            "Int: Sensible numbers are 0-2, where 0 will only tell you when a bug has been detected. 1 tells you what has been started, and 2 tells you what has completed as well (this is probably redundant, since level 0 still tells you on the next round when something hasn't finished.) Generally you'll want to keep this at 0. But if want to see that something is even being attempted, this will help. This entry is for the individual macro instructions at nesting level denoted at the end of this setting name.");
        debug.addItemTemplate(
            "bug.ShouldComplete.MacroLine.line-.*",
            "0",
            "Int: Sensible numbers are 0-2, where 0 will only tell you when a bug has been detected. 1 tells you what has been started, and 2 tells you what has completed as well (this is probably redundant, since level 0 still tells you on the next round when something hasn't finished.) Generally you'll want to keep this at 0. But if want to see that something is even being attempted, this will help. This entry is for the individual macro instructions at nesting level denoted at the end of this setting name.");

        assertEquals(debug.itemCanExist("bug.ShouldComplete/MacroCore/instruction-9"), true);
        assertNotNull(debug.getItem("bug.ShouldComplete/MacroCore/instruction-8"));
    }
}

