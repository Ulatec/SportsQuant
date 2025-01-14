package BaseballQuant.Repository;

import BaseballQuant.Model.MLBPitcher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface PitcherRepository extends CrudRepository<MLBPitcher, Long> {
    Optional<MLBPitcher> findByPlayerID(int id);
}
