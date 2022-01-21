// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Provides individually configurable leveled debug for different classes.

There are two ways of calling this:


## Config provided level

`Debug debug = Debug.getDebug("configProvidedLevel");`

The debug level is then defined in

`Config.singleton().getGroup("debug").newItem("configProvidedLevel", "1", "Description of the setting.")`

The advantage of this method is that all debug options can be placed together.

## Hard-coded level

`Debug debug = new Debug(1, "hardCodedLevel");`

This method gives you simplicity if you just need to get a little output right now.

*/

package debug;

import config.*;

public class Debug {
    private static int errorLevel = 1; // The level that we set if config can't be found.
    private int level = -1;
    private String context;

    public Debug(int defaultLevel, String context) {
        this.context = context;
        this.setLevel(defaultLevel);
    }
    
    /* Returns a fully configured debug object solely from the context.
    The context is assumed to match debug/context in Config. */
    public static Debug getDebug(String context) {
        Group debugGroup = Config.singleton().getGroup("debug");
        
        if (debugGroup == null) {
            Debug debug = new Debug(Debug.errorLevel, context);
            debug.out(0, "Could not find the \"debug\" config group. This is likely a bug in the application. Defaulting the debug level to " + Debug.errorLevel + ".");
            return debug;
        }
        
        Item debugConfigItem = debugGroup.getItem(context);
        
        if (debugConfigItem == null) {
            Debug debug = new Debug(Debug.errorLevel, context);
            debug.out(0, "Could not find the config item for context \"" + context + "\" in the \"debug\" config group. This is likely a bug in the application. Defaulting the debug level to " + Debug.errorLevel + ".");
            return debug;
        }
        
        int level = Integer.parseInt(debugConfigItem.get());
        return new Debug(level, context);
    }

    public void setLevel(int level) {
        if ((level > 0) || (this.level > 0)) {
            if (this.level == -1) {
                System.out.println("Debug   (" + this.context + ")  debug level set to " + String.valueOf(level) + ".");
            } else {
                System.out.println("Debug   (" + this.context + ")  debug level change from " + String.valueOf(this.level) + " to " + String.valueOf(level) + ".");
            }
        }
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }

    protected String getDebugText(int level, String message) {
        return "Debug " + String.valueOf(level) + " (" + this.context + "): " + message;
    }

    protected Boolean shouldOutput(int level) {
        return (level <= this.level);
    }
    
    public void out(int level, String message) {
        // The logic of this function is abstracted out to make it easier to test.
        if (shouldOutput(level)) {
            System.out.println(getDebugText(level, message));
        }
    }
}
