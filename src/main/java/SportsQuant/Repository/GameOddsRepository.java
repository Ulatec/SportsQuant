package SportsQuant.Repository;

import SportsQuant.Model.Game;
import SportsQuant.Model.GameOdds;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GameOddsRepository  extends CrudRepository<GameOdds, Long> {
        Optional<GameOdds> findByGameId(int id);
}
