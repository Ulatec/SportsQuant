package BaseballQuant.Model.CacheObjects;

import java.util.ArrayList;
import java.util.List;

public class OpponentCacheObject {
    private double highRange;
    private double lowRange;
    private int gameId;

    public double getHighRange() {
        return highRange;
    }

    public void setHighRange(double highRange) {
        this.highRange = highRange;
    }

    public double getLowRange() {
        return lowRange;
    }

    public void setLowRange(double lowRange) {
        this.lowRange = lowRange;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
    public List<Double> getDoubles(){
        List<Double> list = new ArrayList<>();
        list.add(highRange);
        list.add(lowRange);
        return list;
    }
}
