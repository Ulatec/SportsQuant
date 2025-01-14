package BaseballQuant.Repository;

import BaseballQuant.Model.MLBPlayer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;
@RepositoryRestResource
public interface PlayerRepository extends CrudRepository<MLBPlayer, Long> {
    Optional<MLBPlayer> findByPlayerID(int id);
}
