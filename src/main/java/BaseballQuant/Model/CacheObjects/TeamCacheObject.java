package BaseballQuant.Model.CacheObjects;

public class TeamCacheObject {

    private double runsPerGame;
    private int playerGameLookback;
    private int gameid;
    private int teamid;


    public double getRunsPerGame() {
        return runsPerGame;
    }

    public void setRunsPerGame(double runsPerGame) {
        this.runsPerGame = runsPerGame;
    }

    public int getPlayerGameLookback() {
        return playerGameLookback;
    }

    public void setPlayerGameLookback(int playerGameLookback) {
        this.playerGameLookback = playerGameLookback;
    }

    public int getGameid() {
        return gameid;
    }

    public void setGameid(int gameid) {
        this.gameid = gameid;
    }

    public int getTeamid() {
        return teamid;
    }

    public void setTeamid(int teamid) {
        this.teamid = teamid;
    }
}
