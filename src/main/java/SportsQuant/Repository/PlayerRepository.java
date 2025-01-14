package SportsQuant.Repository;

import SportsQuant.Model.Player;
import org.springframework.data.repository.CrudRepository;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

//@RepositoryRestResource
public interface PlayerRepository extends CrudRepository<Player, Long> {
    Optional<Player> findByPlayerID(int id);
}
