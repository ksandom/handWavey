package handWavey;

// import handWavey.*;
import config.*;

public class HandWaveyManager {
    private Config config = new Config("handWavey.yml");

    public HandWaveyManager() {
        Item configFormatVersion = this.config.newItem("configFormatVersion", "2021-11-26", "This number is incremented by the programmer whenever existing config items get changed (eg new description, default value etc) so that conflicts can be resolved.");
        configFormatVersion.set("2021-11-26"); // Update it here.

        Group ultraMotion = this.config.newGroup("ultraMotion");
        ultraMotion.newItem("maxHands", "2", "Maximum number of hands to track. Anything more than this setting will be discarded, and assumptions can be made faster, so it will run faster. The most recent hands above the threshold are the ones to be discarded.");
    }

    public Config getConfig() {
        return this.config;
    }
}
