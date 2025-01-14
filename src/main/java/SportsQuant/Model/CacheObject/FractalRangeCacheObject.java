package SportsQuant.Model.CacheObject;

public class FractalRangeCacheObject {

    private int gameId;
    private int gameCount;
    private int fractalWindow;
    private int teamId;
    private String statType;
    private double average;
    private double variance;
    private double topBridge;
    private double bottomBridge;
    private double startingLevel;

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getFractalWindow() {
        return fractalWindow;
    }

    public void setFractalWindow(int fractalWindow) {
        this.fractalWindow = fractalWindow;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getTopBridge() {
        return topBridge;
    }

    public void setTopBridge(double topBridge) {
        this.topBridge = topBridge;
    }

    public double getBottomBridge() {
        return bottomBridge;
    }

    public void setBottomBridge(double bottomBridge) {
        this.bottomBridge = bottomBridge;
    }

    public double getStartingLevel() {
        return startingLevel;
    }

    public void setStartingLevel(double startingLevel) {
        this.startingLevel = startingLevel;
    }

    public String getStatType() {
        return statType;
    }

    public void setStatType(String statType) {
        this.statType = statType;
    }

    public double getVariance() {
        return variance;
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }

    public int getGameCount() {
        return gameCount;
    }

    public void setGameCount(int gameCount) {
        this.gameCount = gameCount;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }
}
