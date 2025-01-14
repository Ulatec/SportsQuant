package SportsQuant.Model;

public class CaesarsObject {

    private String awayTeamName;
    private String homeTeamName;
    private String awayTeamTriCode;
    private String homeTeamTriCode;
    private String awayPitcherName;
    private String homePitcherName;
    private String eventTitle;
    private double awayTeamSpread;
    private double homeTeamSpread;
    private double awayTeamSpreadOdds;
    private double homeTeamSpreadOdds;
    private double awayTeamMoneyLine;
    private double homeTeamMoneyLine;
    private double overUnder;
    private double oddsOver;
    private double oddsUnder;


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

    public double getOverUnder() {
        return overUnder;
    }

    public void setOverUnder(double overUnder) {
        this.overUnder = overUnder;
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

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
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

    @Override
    public String toString() {
        return "CaesarsObject{" +
                "awayTeamName='" + awayTeamName + '\'' +
                ", homeTeamName='" + homeTeamName + '\'' +
                ", awayPitcherName='" + awayPitcherName + '\'' +
                ", homePitcherName='" + homePitcherName + '\'' +
                ", eventTitle='" + eventTitle + '\'' +
                ", awayTeamSpread=" + awayTeamSpread +
                ", homeTeamSpread=" + homeTeamSpread +
                ", awayTeamSpreadOdds=" + awayTeamSpreadOdds +
                ", homeTeamSpreadOdds=" + homeTeamSpreadOdds +
                ", overUnder=" + overUnder +
                '}';
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

    public void setHomeTeamTriCode(String homeTeamTriCode) {
        this.homeTeamTriCode = homeTeamTriCode;
    }
}
