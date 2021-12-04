package dataCleaner;

public class MovingMean {
    private int size;
    private int position;
    private double[] data;

    public MovingMean(int size, double seedValue) {
        this.size = size;
        this.position = 0;

        seed(seedValue);
    }

    // Make all values the same as a starting point. This is great for transitioning from a mode that doesn't use this instance of the moving mean. That way the result is immediately at the expected, and can continue from there.
    public void seed(double value) {
        for (int i=0; i<this.size; i++) {
            this.data[i] = value;
        }
    }

    // Put in a data point.
    public void set(double value) {
        step();
        this.data[this.position] = value;
    }

    // Get out the moving mean of all data.
    public double get() {
        double total = 0;

        for (int i=0; i<this.size; i++) {
            total += this.data[i];
        }

        return total/this.size;
    }

    private void step() {
        this.position++;
        if (this.position > this.size) {
            this.position = 0;
        }
    }
}
