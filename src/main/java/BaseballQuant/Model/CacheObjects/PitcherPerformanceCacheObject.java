package BaseballQuant.Model.CacheObjects;

import java.util.Objects;

public class PitcherPerformanceCacheObject {
    private int playerGameLookback;
    private int pitcherGameLookback;
    private int playerId;
    private int gameid;

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

    public int getPlayerGameLookback() {
        return playerGameLookback;
    }

    public void setPlayerGameLookback(int playerGameLookback) {
        this.playerGameLookback = playerGameLookback;
    }

    public void setPitcherGameLookback(int pitcherGameLookback) {
        this.pitcherGameLookback = pitcherGameLookback;
    }

    public int getPitcherGameLookback() {
        return pitcherGameLookback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PitcherPerformanceCacheObject that = (PitcherPerformanceCacheObject) o;
        return playerGameLookback == that.playerGameLookback && pitcherGameLookback == that.pitcherGameLookback && playerId == that.playerId && gameid == that.gameid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerGameLookback, pitcherGameLookback, playerId, gameid);
    }
}
