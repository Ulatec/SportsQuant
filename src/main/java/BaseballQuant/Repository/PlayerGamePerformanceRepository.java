package BaseballQuant.Repository;

import BaseballQuant.Model.MLBPlayerGamePerformance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
@RepositoryRestResource
public interface PlayerGamePerformanceRepository extends CrudRepository<MLBPlayerGamePerformance, Long> {
    List<MLBPlayerGamePerformance> findAllByGameId(int id);
}
