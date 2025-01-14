package SportsQuant.Model;

public class TestingPair {

    int gameLookBack;
    double pointThreshold;
    double betPercent;

    public int getGameLookBack() {
        return gameLookBack;
    }

    public void setGameLookBack(int gameLookBack) {
        this.gameLookBack = gameLookBack;
    }

    public double getPointThreshold() {
        return pointThreshold;
    }

    public void setPointThreshold(double pointThreshold) {
        this.pointThreshold = pointThreshold;
    }

    @Override
    public String toString() {
        return "TestingPair{" +
                "gameLookBack=" + gameLookBack +
                ", pointThreshold=" + pointThreshold +
                ", betPercent=" + betPercent +
                '}';
    }

    public double getBetPercent() {
        return betPercent;
    }

    public void setBetPercent(double betPercent) {
        this.betPercent = betPercent;
    }
}
