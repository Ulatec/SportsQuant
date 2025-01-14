package SportsQuant.Model;

import java.util.HashMap;
import java.util.Map;

public class StupidObject {
    private double correctPct;
    private Map.Entry<String, HashMap<StatResult,Boolean>> entry;

    public double getCorrectPct() {
        return correctPct;
    }

    public void setCorrectPct(double correctPct) {
        this.correctPct = correctPct;
    }

    public Map.Entry<String, HashMap<StatResult, Boolean>> getEntry() {
        return entry;
    }

    public void setEntry(Map.Entry<String, HashMap<StatResult, Boolean>> entry) {
        this.entry = entry;
    }
}
