package SportsQuant;


import BaseballQuant.Model.CSVGameObject;
import BaseballQuant.Model.MLBGame;
import BaseballQuant.Model.MLBGameOdds;
import SportsQuant.Model.Game;
import SportsQuant.Model.GameOdds;
import SportsQuant.Repository.GameOddsRepository;
import SportsQuant.Repository.GameRepository;
import SportsQuant.Util.CSVImporter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

//@Component
public class ImportOdds implements ApplicationRunner {
    @Autowired
    private GameOddsRepository gameOddsRepository;
    @Autowired
    private GameRepository gameRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        CSVImporter csvImporter = new CSVImporter();
        List<CSVGameObject> csvGameObjects = csvImporter.readGameDataFromCSV();
        List<Game> allGames = (List<Game>) gameRepository.findAll();
            List<GameOdds> assignedGameOdds = new ArrayList<>();
            int totalAssigned = 0;
            for(CSVGameObject csvGameObject : csvGameObjects) {
                LocalDateTime localDate = csvGameObject.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusHours(8);
                LocalDateTime localDateBefore = localDate.minusDays(1).minusHours(2);
                LocalDateTime localDateAfter = localDate.plusDays(1).plusHours(2);
                String csvAwayTriCode = csvGameObject.getAwayTeamTricode().toLowerCase();
                String csvHomeTriCode = csvGameObject.getHomeTeamTricode().toLowerCase();
                boolean found = false;
                for (Game game : allGames) {
                        //System.out.println(mlbGame);
                    if(game.getAwayTeamTricode() == null){
                        System.out.println("STOPP!!!");
                    }
                        String awayTriCode = game.getAwayTeamTricode().toLowerCase();
                        String homeTriCode = game.getHomeTeamTricode().toLowerCase();
                        LocalDateTime gameDateTime = game.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        if (gameDateTime.isAfter(localDateBefore)) {
                            if (gameDateTime.isBefore(localDateAfter)) {
                                if (awayTriCode.equals(csvAwayTriCode)) {
                                    if (homeTriCode.equals(csvHomeTriCode)) {
                                        System.out.println("Assigning " + csvGameObject + " to Game Id: " + game.getGameId());
                                        GameOdds gameOdds = new GameOdds();
                                        gameOdds.setGameId(game.getGameId());
                                        gameOdds.setAwayTeamTriCode(game.getAwayTeamTricode());
                                        gameOdds.setHomeTeamTriCode(game.getHomeTeamTricode());
                                        gameOdds.setDate(game.getDate());
                                        gameOdds.setAwayTeamSpread(csvGameObject.getAwayTeamSpread());
                                        gameOdds.setHomeTeamSpread(csvGameObject.getHomeTeamSpread());
                                        gameOdds.setAwayTeamSpreadOdds(csvGameObject.getAwayTeamSpreadOdds());
                                        gameOdds.setHomeTeamSpreadOdds(csvGameObject.getHomeTeamSpreadOdds());
                                        gameOdds.setAwayTeamMoneyLine(csvGameObject.getAwayTeamMoneyLine());
                                        gameOdds.setHomeTeamMoneyLine(csvGameObject.getHomeTeamMoneyLine());
                                        gameOdds.setOverUnder(csvGameObject.getOverUnder());
                                        assignedGameOdds.add(gameOdds);
                                        totalAssigned++;
                                        found = true;
                                    }else{
                                        System.out.println(game + " incorrect home triCode.");
                                    }
                                }
                            }
                        }
                }
                if (!found) {
                    System.out.println("stop here.");
                }
            }
            System.out.println(totalAssigned + "/" + csvGameObjects.size());
            int savedGames = 0;
            for(GameOdds gameOdds : assignedGameOdds){
                if(gameOddsRepository.findByGameId(gameOdds.getGameId()).isEmpty()){
                    gameOddsRepository.save(gameOdds);
                    savedGames++;
                }else{
                    if(gameOddsRepository.findByGameId(gameOdds.getGameId()).get().getAwayTeamMoneyLine() == 0.0){
                        GameOdds tempOdds = gameOddsRepository.findByGameId(gameOdds.getGameId()).get();
                        tempOdds.setAwayTeamMoneyLine(gameOdds.getAwayTeamMoneyLine());
                        tempOdds.setHomeTeamMoneyLine(gameOdds.getHomeTeamMoneyLine());
                        gameOddsRepository.save(tempOdds);
                    }
                }
            }
            System.out.println(" saved games: " + savedGames);
    }
}
