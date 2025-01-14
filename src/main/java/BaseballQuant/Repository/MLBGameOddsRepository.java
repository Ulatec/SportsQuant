package BaseballQuant.Repository;

import BaseballQuant.Model.MLBGame;
import BaseballQuant.Model.MLBGameOdds;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;
@RepositoryRestResource
public interface MLBGameOddsRepository extends CrudRepository<MLBGameOdds, Long> {
    Optional<MLBGameOdds> findByGameId(int id);
}
