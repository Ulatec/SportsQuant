package SportsQuant.Repository;

import SportsQuant.Model.Player;
import SportsQuant.Model.PlayerGamePerformance;
import org.springframework.data.repository.CrudRepository;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

//@RepositoryRestResource
public interface PlayerGamePerformanceRepository extends CrudRepository<PlayerGamePerformance, Long> {
    List<PlayerGamePerformance> findAllByGameID(int id);
}
