package BaseballQuant.Model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class MLBPlayerGamePerformance {


    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    public int atbats;
    public int rbis;
    public int runs;
    public int walks;
    public int stolenBases;
    public Date date;
    public int gameId;
    public double fieldingpercentage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public int getAtbats() {
        return atbats;
    }

    public void setAtbats(int atbats) {
        this.atbats = atbats;
    }

    public int getRbis() {
        return rbis;
    }

    public void setRbis(int rbis) {
        this.rbis = rbis;
    }

    public int getRuns() {
        return runs;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getWalks() {
        return walks;
    }

    public void setWalks(int walks) {
        this.walks = walks;
    }

    @Override
    public String toString() {
        return "MLBPlayerGamePerformance{" +
                "id=" + id +
                ", atbats=" + atbats +
                ", rbis=" + rbis +
                ", runs=" + runs +
                ", walks=" + walks +
                ", date=" + date +
                '}';
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public double getFieldingpercentage() {
        return fieldingpercentage;
    }

    public void setFieldingpercentage(double fieldingpercentage) {
        this.fieldingpercentage = fieldingpercentage;
    }

    public int getStolenBases() {
        return stolenBases;
    }

    public void setStolenBases(int stolenBases) {
        this.stolenBases = stolenBases;
    }

}
