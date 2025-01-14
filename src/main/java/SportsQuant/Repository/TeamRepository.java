package SportsQuant.Repository;

import SportsQuant.Model.Player;
import SportsQuant.Model.Team;
import org.springframework.data.repository.CrudRepository;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

//@RepositoryRestResource
public interface TeamRepository extends CrudRepository<Team, Long> {
    List<Team> findByTeamId(int id);
}
