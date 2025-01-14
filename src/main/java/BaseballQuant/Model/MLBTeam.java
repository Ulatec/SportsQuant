package BaseballQuant.Model;

import javax.persistence.*;
import java.util.Set;

@Entity
public class MLBTeam implements Cloneable{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<MLBPlayer> fieldingPlayers;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<MLBPitcher> pitchingPlayers;
//    @ManyToMany(fetch = FetchType.EAGER)
//    private Set<MLBPlayer> startingLineUp;
    private String teamName;
    private String teamAbbreviation;
    private int mlbId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<MLBPlayer> getFieldingPlayers() {
        return fieldingPlayers;
    }

    public void setFieldingPlayers(Set<MLBPlayer> MLBPlayers) {
        this.fieldingPlayers = MLBPlayers;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamAbbreviation() {
        return teamAbbreviation;
    }

    public void setTeamAbbreviation(String teamAbbreviation) {
        this.teamAbbreviation = teamAbbreviation;
    }

    @Override
    public String toString() {
        return "Team{" +
                "players=" + fieldingPlayers +
                ", teamName='" + teamName + '\'' +
                ", teamAbbreviation='" + teamAbbreviation + '\'' +
                '}';
    }

    public int getMlbId() {
        return mlbId;
    }

    public void setMlbId(int mlbId) {
        this.mlbId = mlbId;
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

//    public Set<MLBPlayer> getStartingLineUp() {
//        return startingLineUp;
//    }
//
//    public void setStartingLineUp(Set<MLBPlayer> startingLineUp) {
//        this.startingLineUp = startingLineUp;
//    }

    public Set<MLBPitcher> getPitchingPlayers() {
        return pitchingPlayers;
    }

    public void setPitchingPlayers(Set<MLBPitcher> pitchingPlayers) {
        this.pitchingPlayers = pitchingPlayers;
    }
}
