package BaseballQuant.Model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Set;

@Entity
public class MLBPlayer implements  Cloneable{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    private int playerID;
    private String fullName;
    @Fetch(FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER)
    private Set<MLBPlayerGamePerformance> MLBPlayerGamePerformances;


    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public Set<MLBPlayerGamePerformance> getPlayerGamePerformances() {
        return MLBPlayerGamePerformances;
    }

    public void setPlayerGamePerformances(Set<MLBPlayerGamePerformance> MLBPlayerGamePerformances) {
        this.MLBPlayerGamePerformances = MLBPlayerGamePerformances;
    }

    public MLBPlayerGamePerformance findPlayerPerformanceByGameId(int gameId){
        for(MLBPlayerGamePerformance MLBPlayerGamePerformance : this.MLBPlayerGamePerformances){
            if(MLBPlayerGamePerformance.getGameId() == gameId){
                return MLBPlayerGamePerformance;
            }
        }
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MLBPlayer{" +
                "id=" + id +
                ", playerID=" + playerID +
                ", fullName='" + fullName + '\'' +
                ", MLBPlayerGamePerformances=" + MLBPlayerGamePerformances.size() +
                '}';
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Set<MLBPlayerGamePerformance> getMLBPlayerGamePerformances() {
        return MLBPlayerGamePerformances;
    }

    public void setMLBPlayerGamePerformances(Set<MLBPlayerGamePerformance> MLBPlayerGamePerformances) {
        this.MLBPlayerGamePerformances = MLBPlayerGamePerformances;
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
}
