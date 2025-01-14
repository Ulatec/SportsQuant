package BaseballQuant.Model.CacheObjects;

public class PlayerPerformanceCacheObject {

    private double runsPerGame;
    private double runsPerGameStdDev;
    private double fielding;
    private double fieldingStdDev;
    private int playerGameLookback;
    private int playerId;
    private int gameid;

    public double getRunsPerGame() {
        return runsPerGame;
    }

    public void setRunsPerGame(double runsPerGame) {
        this.runsPerGame = runsPerGame;
    }

    public double getRunsPerGameStdDev() {
        return runsPerGameStdDev;
    }

    public void setRunsPerGameStdDev(double runsPerGameStdDev) {
        this.runsPerGameStdDev = runsPerGameStdDev;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getGameid() {
        return gameid;
    }

    public void setGameid(int gameid) {
        this.gameid = gameid;
    }

    public double getFielding() {
        return fielding;
    }

    public void setFielding(double fielding) {
        this.fielding = fielding;
    }

    public double getFieldingStdDev() {
        return fieldingStdDev;
    }

    public void setFieldingStdDev(double fieldingStdDev) {
        this.fieldingStdDev = fieldingStdDev;
    }

    public int getPlayerGameLookback() {
        return playerGameLookback;
    }

    public void setPlayerGameLookback(int playerGameLookback) {
        this.playerGameLookback = playerGameLookback;
    }
}
