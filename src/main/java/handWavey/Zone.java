package handWavey;

public class Zone {
    private double zBegin;
    private double zEnd;
    private double zSpan;
    
    private int movingMeanBegin;
    private int movingMeanEnd;
    private int movingMeanSpan;
    
    public Zone(double zBegin, double zEnd, int movingMeanBegin, int movingMeanEnd) {
        this.zBegin = zBegin;
        this.zEnd = zEnd;
        this.zSpan = this.zEnd - this.zBegin;
        
        this.movingMeanBegin = movingMeanBegin;
        this.movingMeanEnd = movingMeanEnd;
        this.movingMeanSpan = this.movingMeanEnd - this.movingMeanBegin;
    }
    
    public double getDepthPercent(double z) {
        if (z < zBegin) z = zBegin;
        if (z > zEnd) z = zEnd;
        
        double rebased = z - this.zBegin;
        
        return rebased / this.zSpan;
    }
    
    public int getMovingMeanWidth(double z) {
        double scaledValue = movingMeanSpan * getDepthPercent(z);
        int movingMeanWidth = (int) Math.round(scaledValue + movingMeanBegin);
        
        return movingMeanWidth;
    }
    
    public String toString() {
        return "(" + String.valueOf(this.zBegin) + ", " + String.valueOf(this.zEnd) + ", " + String.valueOf(this.zSpan) + ") " +
            "(" + String.valueOf(this.movingMeanBegin) + ", " + String.valueOf(this.movingMeanEnd) + ", " + String.valueOf(this.movingMeanSpan) + ") ";
    }
}
