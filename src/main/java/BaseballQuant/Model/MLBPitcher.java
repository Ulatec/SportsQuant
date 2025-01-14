package BaseballQuant.Model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Set;

@Entity
public class MLBPitcher implements Cloneable{

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private int playerID;
    private String fullName;
    @Fetch(FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER)
    private Set<MLBPitcherPerformance> MLBPitcherGamePerformances;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public Set<MLBPitcherPerformance> getMLBPitcherGamePerformances() {
        return MLBPitcherGamePerformances;
    }

    public void setMLBPitcherGamePerformances(Set<MLBPitcherPerformance> MLBPitcherGamePerformances) {
        this.MLBPitcherGamePerformances = MLBPitcherGamePerformances;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MLBPitcher{" +
                "id=" + id +
                ", playerID=" + playerID +
                ", fullName='" + fullName + '\'' +
                ", MLBPitcherGamePerformances=" + MLBPitcherGamePerformances.size() +
                '}';
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
