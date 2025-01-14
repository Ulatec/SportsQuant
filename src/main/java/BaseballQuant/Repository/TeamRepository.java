package BaseballQuant.Repository;

import BaseballQuant.Model.MLBTeam;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface TeamRepository extends CrudRepository<MLBTeam, Long> {
    List<MLBTeam> findByMlbId(int id);
}
