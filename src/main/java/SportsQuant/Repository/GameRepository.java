package SportsQuant.Repository;

import SportsQuant.Model.Game;
import SportsQuant.Model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface GameRepository extends CrudRepository<Game, Long> {
    Optional<Game> findByGameId(int id);
    List<Game> findAllByDateBeforeAndDateAfter(Date date1, Date date2);
}
