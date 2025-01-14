package SportsQuant.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class GameOdds implements Cloneable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private int draftKingsId;
    private double overUnder;
    private int gameId;
    private Date date;
    private String awayTeamTriCode;
    private String homeTeamTriCode;
    private double awayTeamSpread;
    private double homeTeamSpread;
    private double awayTeamSpreadOdds;
    private double homeTeamSpreadOdds;
    private double awayTeamMoneyLine;
    private double homeTeamMoneyLine;
    private boolean paired;


    public GameOdds() {
        paired = false;
    }

    public double getOverUnder() {
        return overUnder;
    }

    public void setOverUnder(double overUnder) {
        this.overUnder = overUnder;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getDraftKingsId() {
        return draftKingsId;
    }

    public void setDraftKingsId(int draftKingsId) {
        this.draftKingsId = draftKingsId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAwayTeamTriCode() {
        return awayTeamTriCode;
    }

    public void setAwayTeamTriCode(String awayTeamTriCode) {
        this.awayTeamTriCode = awayTeamTriCode;
    }

    public String getHomeTeamTriCode() {
        return homeTeamTriCode;
    }

    public void setHomeTeamTriCode(String team2Name) {
        this.homeTeamTriCode = team2Name;
    }

    public double getAwayTeamSpread() {
        return awayTeamSpread;
    }

    public void setAwayTeamSpread(double team1Spread) {
        this.awayTeamSpread = team1Spread;
    }

    public double getHomeTeamSpread() {
        return homeTeamSpread;
    }

    public void setHomeTeamSpread(double team2Spread) {
        this.homeTeamSpread = team2Spread;
    }

    public boolean isPaired() {
        return paired;
    }

    public void setPaired(boolean paired) {
        this.paired = paired;
    }

    public double getAwayTeamMoneyLine() {
        return awayTeamMoneyLine;
    }

    public void setAwayTeamMoneyLine(double awayTeamMoneyLine) {
        this.awayTeamMoneyLine = awayTeamMoneyLine;
    }

    public double getHomeTeamMoneyLine() {
        return homeTeamMoneyLine;
    }

    public void setHomeTeamMoneyLine(double homeTeamMoneyLine) {
        this.homeTeamMoneyLine = homeTeamMoneyLine;
    }

    @Override
    public String toString() {
        return "GameOdds{" +
                "id=" + id +
                ", draftKingsId=" + draftKingsId +
                ", overUnder=" + overUnder +
                ", gameId=" + gameId +
                ", date=" + date +
                ", awayTeamTriCode='" + awayTeamTriCode + '\'' +
                ", homeTeamTriCode='" + homeTeamTriCode + '\'' +
                ", awayTeamSpread=" + awayTeamSpread +
                ", homeTeamSpread=" + homeTeamSpread +
                ", awayTeamSpreadOdds=" + awayTeamSpreadOdds +
                ", homeTeamSpreadOdds=" + homeTeamSpreadOdds +
                ", awayTeamMoneyLine=" + awayTeamMoneyLine +
                ", homeTeamMoneyLine=" + homeTeamMoneyLine +
                ", paired=" + paired +
                '}';
    }

    public double getAwayTeamSpreadOdds() {
        return awayTeamSpreadOdds;
    }

    public void setAwayTeamSpreadOdds(double awayTeamSpreadOdds) {
        this.awayTeamSpreadOdds = awayTeamSpreadOdds;
    }

    public double getHomeTeamSpreadOdds() {
        return homeTeamSpreadOdds;
    }

    public void setHomeTeamSpreadOdds(double homeTeamSpreadOdds) {
        this.homeTeamSpreadOdds = homeTeamSpreadOdds;
    }

    @Override
    public GameOdds clone() {
        try {
            GameOdds clone = (GameOdds) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
