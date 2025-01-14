package SportsQuant.Model;

public class StatResult {
    private String statType;
    private double forecastedNumber;
    private double actualNumber;
    private double highNum;
    private double lowNum;
    private double predictedPoints;
    private double actualPoints;

    public String getStatType() {
        return statType;
    }

    public void setStatType(String statType) {
        this.statType = statType;
    }

    public double getForecastedNumber() {
        return forecastedNumber;
    }

    public void setForecastedNumber(double forecastedNumber) {
        this.forecastedNumber = forecastedNumber;
    }

    public double getActualNumber() {
        return actualNumber;
    }

    public void setActualNumber(double actualNumber) {
        this.actualNumber = actualNumber;
    }

    public double getHighNum() {
        return highNum;
    }

    public void setHighNum(double highNum) {
        this.highNum = highNum;
    }

    public double getLowNum() {
        return lowNum;
    }

    public void setLowNum(double lowNum) {
        this.lowNum = lowNum;
    }

    public double getPredictedPoints() {
        return predictedPoints;
    }

    public void setPredictedPoints(double predictedPoints) {
        this.predictedPoints = predictedPoints;
    }

    public double getActualPoints() {
        return actualPoints;
    }

    public void setActualPoints(double actualPoints) {
        this.actualPoints = actualPoints;
    }

    @Override
    public String toString() {
        return "StatResult{" +
                "statType='" + statType + '\'' +
                ", forecastedNumber=" + forecastedNumber +
                ", actualNumber=" + actualNumber +
                '}';
    }
}
