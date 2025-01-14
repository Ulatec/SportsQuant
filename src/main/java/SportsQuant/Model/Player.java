package SportsQuant.Model;

import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class Player implements  Cloneable{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Unique
    private int playerID;
    private String firstName;
    private String lastName;
    @OneToMany(fetch = FetchType.EAGER)
    private Set<PlayerGamePerformance> playerGamePerformances;
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public Set<PlayerGamePerformance> getPlayerGamePerformances() {
        return playerGamePerformances;
    }

    public void setPlayerGamePerformances(Set<PlayerGamePerformance> playerGamePerformances) {
        this.playerGamePerformances = playerGamePerformances;
    }

    public PlayerGamePerformance findPlayerPerformanceByGameId(int gameId){
        for(PlayerGamePerformance playerGamePerformance : this.playerGamePerformances){
            if(playerGamePerformance.getGameID() == gameId){
                return playerGamePerformance;
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
        return "Player{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", playerID=" + playerID +
                ", playerGamePerformances=" + playerGamePerformances.size() +
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
