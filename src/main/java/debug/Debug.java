package debug;

public class Debug {
    private int level = -1;
    private String context;

    public Debug(int defaultLevel, String context) {
        this.context = context;
        this.setLevel(defaultLevel);
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
