package BaseballQuant.Repository;

import BaseballQuant.Model.MLBGame;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Date;
import java.util.List;
import java.util.Optional;
@RepositoryRestResource
public interface GameRepository extends CrudRepository<MLBGame, Long> {

    Optional<MLBGame> findByGameId(int id);
    List<MLBGame> findAllByDateBeforeAndDateAfter(Date date1, Date date2);
    List<MLBGame> findAllByDateBeforeAndDateAfterAndAwayTeamNameOrDateBeforeAndDateAfterAndHomeTeamName(Date date1, Date date2, String teamName, Date date3, Date date4, String teamName2);
    List<MLBGame> findAllByDateAfter(Date date1);
    List<MLBGame> findAllByAwayTeamNameOrHomeTeamName(String teamName1, String teamName2);
}
