package BaseballQuant.Model.CacheObjects;

import java.util.Objects;

public class CacheSettingsObject {
    private int playerGameLookback;
    private int pitcherGameLookback;
    private int bullpenLookback;
    private boolean doubleSquareRoot;

    public int getPlayerGameLookback() {
        return playerGameLookback;
    }

    public void setPlayerGameLookback(int playerGameLookback) {
        this.playerGameLookback = playerGameLookback;
    }

    public int getPitcherGameLookback() {
        return pitcherGameLookback;
    }

    public void setPitcherGameLookback(int pitcherGameLookback) {
        this.pitcherGameLookback = pitcherGameLookback;
    }

    public int getBullpenLookback() {
        return bullpenLookback;
    }

    public void setBullpenLookback(int bullpenLookback) {
        this.bullpenLookback = bullpenLookback;
    }

    public boolean isDoubleSquareRoot() {
        return doubleSquareRoot;
    }

    public void setDoubleSquareRoot(boolean doubleSquareRoot) {
        this.doubleSquareRoot = doubleSquareRoot;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheSettingsObject that = (CacheSettingsObject) o;
        return playerGameLookback == that.playerGameLookback && pitcherGameLookback == that.pitcherGameLookback && bullpenLookback == that.bullpenLookback && doubleSquareRoot == that.doubleSquareRoot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerGameLookback, pitcherGameLookback, bullpenLookback, doubleSquareRoot);
    }
}
