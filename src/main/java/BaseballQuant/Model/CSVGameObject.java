package BaseballQuant.Model;

import java.util.Date;

public class CSVGameObject {
    public int gameId;
    public Date date;
    private String awayTeamTricode;
    private String homeTeamTricode;
    private String awayTeamName;
    private String homeTeamName;
    private String awayPitcherName;
    private String homePitcherName;
    private double awayTeamMoneyLine;
    private double homeTeamMoneyLine;
    private double awayTeamSpread;
    private double homeTeamSpread;
    private double awayTeamSpreadOdds;
    private double homeTeamSpreadOdds;
    private double overUnder;

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getAwayTeamTricode() {
        return awayTeamTricode;
    }

    public void setAwayTeamTricode(String awayTeamTricode) {
        this.awayTeamTricode = awayTeamTricode;
    }

    public String getHomeTeamTricode() {
        return homeTeamTricode;
    }

    public void setHomeTeamTricode(String homeTeamTricode) {
        this.homeTeamTricode = homeTeamTricode;
    }

    public String getAwayPitcherName() {
        return awayPitcherName;
    }

    public void setAwayPitcherName(String awayPitcherName) {
        this.awayPitcherName = awayPitcherName;
    }

    public String getHomePitcherName() {
        return homePitcherName;
    }

    public void setHomePitcherName(String homePitcherName) {
        this.homePitcherName = homePitcherName;
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

    public double getOverUnder() {
        return overUnder;
    }

    public void setOverUnder(double overUnder) {
        this.overUnder = overUnder;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "CSVGameObject{" +
                "gameId=" + gameId +
                ", date=" + date +
                ", awayTeamTricode='" + awayTeamTricode + '\'' +
                ", homeTeamTricode='" + homeTeamTricode + '\'' +
                ", awayPitcherName='" + awayPitcherName + '\'' +
                ", homePitcherName='" + homePitcherName + '\'' +
                ", awayTeamSpread=" + awayTeamSpread +
                ", homeTeamSpread=" + homeTeamSpread +
                ", awayTeamSpreadOdds=" + awayTeamSpreadOdds +
                ", homeTeamSpreadOdds=" + homeTeamSpreadOdds +
                ", overUnder=" + overUnder +
                '}';
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
}
