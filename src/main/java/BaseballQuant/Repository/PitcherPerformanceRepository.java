package BaseballQuant.Repository;

import BaseballQuant.Model.MLBPitcherPerformance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface PitcherPerformanceRepository extends CrudRepository<MLBPitcherPerformance, Long> {
    List<MLBPitcherPerformance> findAllByGameId(int id);
}
