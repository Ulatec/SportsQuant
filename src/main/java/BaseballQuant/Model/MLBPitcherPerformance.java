package BaseballQuant.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class MLBPitcherPerformance {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private double inningsPitched;
    private int runsGivenUp;
    private int walksGiven;
    private int hitsGiven;
    private int gameId;
    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getInningsPitched() {
        return inningsPitched;
    }
    public void setInningsPitchedFromString(String inningsPitched) {
        Double fullInnings = Double.parseDouble(inningsPitched.substring(0,1));
        Double partialInnings = Double.parseDouble(inningsPitched.substring(2,3))/3;
        //System.out.println("innings pitched: " + (fullInnings + partialInnings));
        this.inningsPitched = (fullInnings + partialInnings);
    }
    public void setInningsPitched(double inningsPitched) {
        this.inningsPitched = inningsPitched;
    }

    public int getRunsGivenUp() {
        return runsGivenUp;
    }

    public void setRunsGivenUp(int runsGivenUp) {
        this.runsGivenUp = runsGivenUp;
    }

    public int getWalksGiven() {
        return walksGiven;
    }

    public void setWalksGiven(int walksGiven) {
        this.walksGiven = walksGiven;
    }

    public int getHitsGiven() {
        return hitsGiven;
    }

    public void setHitsGiven(int hitsGiven) {
        this.hitsGiven = hitsGiven;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
