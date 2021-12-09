package dataCleaner;

public class MovingMean {
    private int size = 0;
    private int position = 0;
    private double[] data = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

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
        if (this.position > this.size - 1) {
            this.position = 0;
        }
    }

    public void resize(int newSize) {
        /* This is not completely correct because old values will no longer decay out of view. Instead their average is spread across the entire window, and that average will fade out of view.

        To do this correctly, it would need to effectively grow the buffer as new data comes in. To do this easily while maintaining the order of the data to that it decays correctly, it would need to defrag the data back to 0 each time the resize is run. Shrink could do it in a single step, but would still need the defrag so that the correct data is truncated.

        I need this to be able to be run many times per second on low end hardware without hogging resources. Therefore the following approximation is used instead. */
        if (newSize != this.size) {
            double currentMean = get();
            this.position = 0;
            this.size = newSize;
            seed(currentMean);
        }
    }
}
