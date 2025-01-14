package SportsQuant.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class PlayerGamePerformance {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    public int points;
    public double minutes;
    public int rebounds;
    public int reboundsOffensive;
    public int reboundsDefensive;
    public int assists;
    public int gameID;
    public int fouls;
    public int turnovers;
    public Date date;
    public int blocks;
    public int steals;
    public int fieldGoalsAttempted;
    public int fieldGoalsMade;
    public double fieldGoalPercentage;

    public int threePointersMade;
    public int threePointersAttempted;
    public double threePointerPercentage;

    public int freeThrowsMade;
    public int freeThrowsAttempted;
    public double freeThrowsPercentage;


    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public double getMinutes() {
        return minutes;
    }

    public void setMinutes(double minutes) {
        this.minutes = minutes;
    }

    public int getRebounds() {
        return rebounds;
    }

    public void setRebounds(int rebounds) {
        this.rebounds = rebounds;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getBlocks() {
        return blocks;
    }

    public void setBlocks(int blocks) {
        this.blocks = blocks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "PlayerGamePerformance{" +
                "points=" + points +
                ", blocks=" + blocks +
                ", steals=" + steals +
                ", minutes=" + minutes +
                ", rebounds=" + rebounds +
                ", assists=" + assists +
                ", gameID=" + gameID +
                ", date=" + date +
                '}';
    }

    public int getSteals() {
        return steals;
    }

    public void setSteals(int steals) {
        this.steals = steals;
    }

    public int getFouls() {
        return fouls;
    }

    public void setFouls(int fouls) {
        this.fouls = fouls;
    }

    public int getTurnovers() {
        return turnovers;
    }

    public void setTurnovers(int turnovers) {
        this.turnovers = turnovers;
    }

    public int getReboundsOffensive() {
        return reboundsOffensive;
    }

    public void setReboundsOffensive(int reboundsOffensive) {
        this.reboundsOffensive = reboundsOffensive;
    }

    public int getReboundsDefensive() {
        return reboundsDefensive;
    }

    public void setReboundsDefensive(int reboundsDefensive) {
        this.reboundsDefensive = reboundsDefensive;
    }

    public int getFieldGoalsAttempted() {
        return fieldGoalsAttempted;
    }

    public void setFieldGoalsAttempted(int fieldGoalsAttempted) {
        this.fieldGoalsAttempted = fieldGoalsAttempted;
    }

    public int getFieldGoalsMade() {
        return fieldGoalsMade;
    }

    public void setFieldGoalsMade(int fieldGoalsMade) {
        this.fieldGoalsMade = fieldGoalsMade;
    }

    public double getFieldGoalPercentage() {
        return fieldGoalPercentage;
    }

    public void setFieldGoalPercentage(double fieldGoalPercentage) {
        this.fieldGoalPercentage = fieldGoalPercentage;
    }

    public int getThreePointersMade() {
        return threePointersMade;
    }

    public void setThreePointersMade(int threePointersMade) {
        this.threePointersMade = threePointersMade;
    }

    public int getThreePointersAttempted() {
        return threePointersAttempted;
    }

    public void setThreePointersAttempted(int threePointersAttempted) {
        this.threePointersAttempted = threePointersAttempted;
    }

    public double getThreePointerPercentage() {
        return threePointerPercentage;
    }

    public void setThreePointerPercentage(double threePointerPercentage) {
        this.threePointerPercentage = threePointerPercentage;
    }

    public int getFreeThrowsMade() {
        return freeThrowsMade;
    }

    public void setFreeThrowsMade(int freeThrowsMade) {
        this.freeThrowsMade = freeThrowsMade;
    }

    public int getFreeThrowsAttempted() {
        return freeThrowsAttempted;
    }

    public void setFreeThrowsAttempted(int freeThrowsAttempted) {
        this.freeThrowsAttempted = freeThrowsAttempted;
    }

    public double getFreeThrowsPercentage() {
        return freeThrowsPercentage;
    }

    public void setFreeThrowsPercentage(double freeThrowsPercentage) {
        this.freeThrowsPercentage = freeThrowsPercentage;
    }
}
