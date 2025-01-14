package SportsQuant.Model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Game implements Cloneable{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Team awayTeam;
    @ManyToOne
    private Team homeTeam;
    private Date date;
    private int gameId;
    private double overUnder;
    private double homeSpread;
    private double awaySpread;
    private int homePoints;
    private int awayPoints;
    private String awayTeamName;
    private String homeTeamName;
    private String awayTeamTricode;
    private String homeTeamTricode;
    @Transient
    private GameOdds gameOdds;

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int id) {
        this.gameId = id;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", awayTeam=" + awayTeam +
                ", homeTeam=" + homeTeam +
                ", date=" + date +
                ", gameId=" + gameId +
                ", overUnder=" + overUnder +
                ", homeSpread=" + homeSpread +
                ", awaySpread=" + awaySpread +
                ", homePoints=" + homePoints +
                ", awayPoints=" + awayPoints +
                ", awayTeamName='" + awayTeamName + '\'' +
                ", homeTeamName='" + homeTeamName + '\'' +
                ", awayTeamTriCode='" + awayTeamTricode + '\'' +
                ", homeTeamTriCode='" + homeTeamTricode + '\'' +
                '}';
    }

    public double getOverUnder() {
        return overUnder;
    }

    public void setOverUnder(double overUnder) {
        this.overUnder = overUnder;
    }

    public int getHomePoints() {
        return homePoints;
    }

    public void setHomePoints(int homePoints) {
        this.homePoints = homePoints;
    }

    public int getAwayPoints() {
        return awayPoints;
    }

    public void setAwayPoints(int awayPoints) {
        this.awayPoints = awayPoints;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new Error("Something impossible just happened");
        }
    }

    public double getHomeSpread() {
        return homeSpread;
    }

    public void setHomeSpread(double homeSpread) {
        this.homeSpread = homeSpread;
    }

    public double getAwaySpread() {
        return awaySpread;
    }

    public void setAwaySpread(double awaySpread) {
        this.awaySpread = awaySpread;
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

    public GameOdds getGameOdds() {
        return gameOdds;
    }

    public void setGameOdds(GameOdds gameOdds) {
        this.gameOdds = gameOdds;
    }
}
