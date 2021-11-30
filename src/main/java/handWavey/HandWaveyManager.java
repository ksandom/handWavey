package handWavey;

// import handWavey.*;
import config.*;

public class HandWaveyManager {
    public HandWaveyManager() {
        Config.setSingletonFilename("handWavey.yml");
        Config config = Config.singleton();
        
        
        Item configFormatVersion = config.newItem("configFormatVersion", "2021-11-26", "This number is incremented by the programmer whenever existing config items get changed (eg new description, default value etc) so that conflicts can be resolved.");
        configFormatVersion.set("2021-11-26"); // Update it here.

        Group ultraMotion = config.newGroup("ultraMotion");
        ultraMotion.newItem("maxHands", "2", "Maximum number of hands to track. Anything more than this setting will be discarded, and assumptions can be made faster, so it will run faster. The most recent hands above the threshold are the ones to be discarded.");
        ultraMotion.newItem("openThreshold", "1.7", "Float: When the last bone of the middle finger is above this angle, the hand is assumed to be open.");
        ultraMotion.newItem("debugLevel", "1", "Int: Sensible numbers are 0-5, where 0 is no debugging, and 5  is probably more detail than you'll ever want.");

        Group handSummary = config.newGroup("handSummary");
        handSummary.newItem("xMultiplier", "1", "Set this to -1 when you need to invert X (side to side). You'll typicall only need to do this if your device is upside down. On newer LeapSDK versions, this may become obsolete.");
        handSummary.newItem("yMultiplier", "1", "Set this to -1 when you need to invert Y (up and down). You'll typicall only need to do this if your device is upside down. On newer LeapSDK versions, this may become obsolete.");
        handSummary.newItem("yMultiplier", "1", "Set this to -1 when you need to invert Z (how far away from you your hand goes). UltraMotion takes care of this for you. So I can't currently think of a use-case for it, but am including it for completness.");
    }
}
