package SportsQuant.Model.CacheObject;

public class PlayerPerformanceCacheObject {

    public double blocksPerGame;
    public double blocksPerGameStdDev;
    public double stealsPerGame;
    public double stealsPerGameStdDev;
    public double turnoversPerGame;
    public double turnoversPerGameStdDev;
    public double reboundsPerGame;
    public double reboundsPerGameStdDev;
    public double foulsPerGame;
    public double foulsPerGameStdDev;

    private int playerGameLookback;
    private int playerId;
    private int gameid;
    private double gameTimeThreshold;

    public double getBlocksPerGame() {
        return blocksPerGame;
    }

    public void setBlocksPerGame(double blocksPerGame) {
        this.blocksPerGame = blocksPerGame;
    }

    public double getBlocksPerGameStdDev() {
        return blocksPerGameStdDev;
    }

    public void setBlocksPerGameStdDev(double blocksPerGameStdDev) {
        this.blocksPerGameStdDev = blocksPerGameStdDev;
    }

    public double getStealsPerGame() {
        return stealsPerGame;
    }

    public void setStealsPerGame(double stealsPerGame) {
        this.stealsPerGame = stealsPerGame;
    }

    public double getStealsPerGameStdDev() {
        return stealsPerGameStdDev;
    }

    public void setStealsPerGameStdDev(double stealsPerGameStdDev) {
        this.stealsPerGameStdDev = stealsPerGameStdDev;
    }

    public int getPlayerGameLookback() {
        return playerGameLookback;
    }

    public void setPlayerGameLookback(int playerGameLookback) {
        this.playerGameLookback = playerGameLookback;
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

    public double getGameTimeThreshold() {
        return gameTimeThreshold;
    }

    public void setGameTimeThreshold(double gameTimeThreshold) {
        this.gameTimeThreshold = gameTimeThreshold;
    }

    public double getTurnoversPerGame() {
        return turnoversPerGame;
    }

    public void setTurnoversPerGame(double turnoversPerGame) {
        this.turnoversPerGame = turnoversPerGame;
    }

    public double getTurnoversPerGameStdDev() {
        return turnoversPerGameStdDev;
    }

    public void setTurnoversPerGameStdDev(double turnoversPerGameStdDev) {
        this.turnoversPerGameStdDev = turnoversPerGameStdDev;
    }

    public double getReboundsPerGame() {
        return reboundsPerGame;
    }

    public void setReboundsPerGame(double reboundsPerGame) {
        this.reboundsPerGame = reboundsPerGame;
    }

    public double getReboundsPerGameStdDev() {
        return reboundsPerGameStdDev;
    }

    public void setReboundsPerGameStdDev(double reboundsPerGameStdDev) {
        this.reboundsPerGameStdDev = reboundsPerGameStdDev;
    }

    public double getFoulsPerGame() {
        return foulsPerGame;
    }

    public void setFoulsPerGame(double foulsPerGame) {
        this.foulsPerGame = foulsPerGame;
    }

    public double getFoulsPerGameStdDev() {
        return foulsPerGameStdDev;
    }

    public void setFoulsPerGameStdDev(double foulsPerGameStdDev) {
        this.foulsPerGameStdDev = foulsPerGameStdDev;
    }
}
