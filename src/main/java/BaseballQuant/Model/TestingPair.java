package BaseballQuant.Model;

public class TestingPair {

    int gameLookBack;
    double pointThreshold;

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
                ", gamesToTest=" + pointThreshold +
                '}';
    }
}
