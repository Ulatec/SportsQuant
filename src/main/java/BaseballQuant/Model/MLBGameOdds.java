package BaseballQuant.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
@Entity
public class MLBGameOdds {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private int draftKingsId;
    private double overUnder;
    private int gameId;
    private double oddsOver;
    private double oddsUnder;
    private Date date;
    private String awayTeamName;
    private String homeTeamName;
    private double awayTeamSpread;
    private double homeTeamSpread;
    private double awayTeamSpreadOdds;
    private double homeTeamSpreadOdds;
    private double spreadLine;
    private double awayTeamMoneyLine;
    private double homeTeamMoneyLine;
    private boolean paired;


    public MLBGameOdds(){

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDraftKingsId() {
        return draftKingsId;
    }

    public void setDraftKingsId(int draftKingsId) {
        this.draftKingsId = draftKingsId;
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

    public double getOddsOver() {
        return oddsOver;
    }

    public void setOddsOver(double oddsOver) {
        this.oddsOver = oddsOver;
    }

    public double getOddsUnder() {
        return oddsUnder;
    }

    public void setOddsUnder(double oddsUnder) {
        this.oddsUnder = oddsUnder;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    public double getAwayTeamSpread() {
        return awayTeamSpread;
    }

    public void setAwayTeamSpread(double awayTeamSpread) {
        this.awayTeamSpread = awayTeamSpread;
    }

    public double getHomeTeamSpread() {
        return homeTeamSpread;
    }

    public void setHomeTeamSpread(double homeTeamSpread) {
        this.homeTeamSpread = homeTeamSpread;
    }

    public double getSpreadLine() {
        return spreadLine;
    }

    public void setSpreadLine(double spreadLine) {
        this.spreadLine = spreadLine;
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

    public boolean isPaired() {
        return paired;
    }

    public void setPaired(boolean paired) {
        this.paired = paired;
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
}
