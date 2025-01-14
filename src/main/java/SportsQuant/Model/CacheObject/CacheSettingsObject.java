package SportsQuant.Model.CacheObject;

import java.util.Objects;

public class CacheSettingsObject {
    private int playerGameLookback;
    private int fractalWindow;
    private double gameTimeThreshold;
    private boolean doubleSquareRoot;
    private boolean allowBelowZero;

    public int getPlayerGameLookback() {
        return playerGameLookback;
    }

    public void setPlayerGameLookback(int playerGameLookback) {
        this.playerGameLookback = playerGameLookback;
    }


    public boolean isDoubleSquareRoot() {
        return doubleSquareRoot;
    }

    public void setDoubleSquareRoot(boolean doubleSquareRoot) {
        this.doubleSquareRoot = doubleSquareRoot;
    }

    public boolean isAllowBelowZero() {
        return allowBelowZero;
    }

    public void setAllowBelowZero(boolean allowBelowZero) {
        this.allowBelowZero = allowBelowZero;
    }

    public double getGameTimeThreshold() {
        return gameTimeThreshold;
    }

    public void setGameTimeThreshold(double gameTimeThreshold) {
        this.gameTimeThreshold = gameTimeThreshold;
    }

    public int getFractalWindow() {
        return fractalWindow;
    }

    public void setFractalWindow(int fractalWindow) {
        this.fractalWindow = fractalWindow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheSettingsObject that = (CacheSettingsObject) o;
        return playerGameLookback == that.playerGameLookback && fractalWindow == that.fractalWindow && Double.compare(that.gameTimeThreshold, gameTimeThreshold) == 0 && doubleSquareRoot == that.doubleSquareRoot && allowBelowZero == that.allowBelowZero;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerGameLookback, fractalWindow, gameTimeThreshold, doubleSquareRoot, allowBelowZero);
    }
}
