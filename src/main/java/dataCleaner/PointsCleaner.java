package dataCleaner;

public class PointsCleaner {
    private int size;
    private int position;
    private double[] data = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private double[] assumedAata = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private double[] difference = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private double[] absoluteDifference = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private Boolean[] allowed = {false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};

    private double maxChange; // How much can a given point change from the previous one without arousing suspicion.
    private int minSequential; // The minimum number of points deviating from the previous trend to change the trend.

    public PointsCleaner(int size, double seedValue, double maxChange, int minSequential) {
        this.size = size;
        this.position = 0;

        this.maxChange = maxChange;
        this.minSequential = minSequential;

        seed(seedValue);
    }

    // Make all values the same as a starting point. This is great for transitioning from a mode that doesn't use this instance of the PointsCleaner. That way the result is immediately at the expected, and can continue from there.
    public void seed(double value) {
        for (int i=0; i<this.size; i++) {
            this.data[i] = value;
            this.assumedAata[i] = value;
            this.difference[i] = 0;
            this.absoluteDifference[i] = 0;
            this.allowed[i] = true;
        }
    }

    // Put in a data point.
    public void set(double value) {
        step();
        this.data[this.position] = value;
        this.difference[this.position] = value - this.data[getPreviousPosition(1)];
        this.absoluteDifference[this.position] = Math.abs(this.data[this.position]); // TODO Test this

        // TODO Write grouping stuff
    }

    protected int getPreviousPosition(int steps) {
        int previousPosition = this.position - (steps % this.size);
        if (previousPosition < 0) {
            previousPosition += this.size;
        }

        return previousPosition;
    }

    private void step() {
        this.position++;
        if (this.position > this.size - 1) {
            this.position = 0;
        }
    }
}
