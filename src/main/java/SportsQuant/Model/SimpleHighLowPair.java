package SportsQuant.Model;

public class SimpleHighLowPair {
    private double high;
    private double low;

    public SimpleHighLowPair(double high, double low) {
        this.high = high;
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }
}
