package SportsQuant.Model;

public class ReturnedOdds {
    private String spreadLine;
    private double overUnder;

    public String getSpreadLine() {
        return spreadLine;
    }

    public void setSpreadLine(String spreadLine) {
        this.spreadLine = spreadLine;
    }

    public double getOverUnder() {
        return overUnder;
    }

    public void setOverUnder(double overUnder) {
        this.overUnder = overUnder;
    }

    @Override
    public String toString() {
        return "ReturnedOdds{" +
                "spreadLine='" + spreadLine + '\'' +
                ", overUnder=" + overUnder +
                '}';
    }
}
