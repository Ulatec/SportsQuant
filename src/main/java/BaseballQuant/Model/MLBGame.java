package BaseballQuant.Model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class MLBGame implements Cloneable{

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private MLBTeam awayMLBTeam;
    @ManyToOne
    private MLBTeam homeMLBTeam;
    private Date date;
    private int gameId;
    private int homePoints;
    private int awayPoints;
    private String awayTeamName;
    private int awayTeamMlbId;
    private String homeTeamName;
    private int homeTeamMlbId;
    public boolean shortenedMakeUpGame;
    private String awayTeamTricode;
    private String homeTeamTricode;
    @ManyToOne
    private MLBPitcher awayStartingPitcher;
    @ManyToOne
    private MLBPitcher homeStartingPitcher;

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
        return "MLBGame{" +
                "id=" + id +
                ", date=" + date +
                ", gameId=" + gameId +
                ", homePoints=" + homePoints +
                ", awayPoints=" + awayPoints +
                ", awayTeamName='" + awayTeamName + '\'' +
                ", awayTeamMlbId=" + awayTeamMlbId +
                ", homeTeamName='" + homeTeamName + '\'' +
                ", homeTeamMlbId=" + homeTeamMlbId +
                ", shortenedMakeUpGame=" + shortenedMakeUpGame +
                ", awayTeamTricode='" + awayTeamTricode + '\'' +
                ", homeTeamTricode='" + homeTeamTricode + '\'' +
                ", awayStartingPitcher=" + awayStartingPitcher +
                ", homeStartingPitcher=" + homeStartingPitcher +
                '}';
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

    public MLBTeam getAwayMLBTeam() {
        return awayMLBTeam;
    }

    public void setAwayMLBTeam(MLBTeam awayMLBTeam) {
        this.awayMLBTeam = awayMLBTeam;
    }

    public MLBTeam getHomeMLBTeam() {
        return homeMLBTeam;
    }

    public void setHomeMLBTeam(MLBTeam homeMLBTeam) {
        this.homeMLBTeam = homeMLBTeam;
    }

    public int getAwayTeamMlbId() {
        return awayTeamMlbId;
    }

    public void setAwayTeamMlbId(int awayTeamMlbId) {
        this.awayTeamMlbId = awayTeamMlbId;
    }

    public int getHomeTeamMlbId() {
        return homeTeamMlbId;
    }

    public void setHomeTeamMlbId(int homeTeamMlbId) {
        this.homeTeamMlbId = homeTeamMlbId;
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

    public MLBPitcher getAwayStartingPitcher() {
        return awayStartingPitcher;
    }

    public void setAwayStartingPitcher(MLBPitcher awayStartingPitcher) {
        this.awayStartingPitcher = awayStartingPitcher;
    }

    public MLBPitcher getHomeStartingPitcher() {
        return homeStartingPitcher;
    }

    public void setHomeStartingPitcher(MLBPitcher homeStartingPitcher) {
        this.homeStartingPitcher = homeStartingPitcher;
    }

    public boolean isShortenedMakeUpGame() {
        return shortenedMakeUpGame;
    }

    public void setShortenedMakeUpGame(boolean shortenedMakeUpGame) {
        this.shortenedMakeUpGame = shortenedMakeUpGame;
    }


}
