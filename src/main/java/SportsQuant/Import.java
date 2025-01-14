package SportsQuant;

import SportsQuant.Model.*;
import SportsQuant.Repository.*;
import SportsQuant.Util.GameFinder;
import ch.qos.logback.core.util.Loader;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

//@Component
public class Import implements ApplicationRunner {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GameOddsRepository gameOddsRepository;
    @Autowired
    private PlayerGamePerformanceRepository playerGamePerformanceRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        LocalDate localDate = LocalDate.of(2025, 1,2);
        //gameService.findGameByGameId("0022000255");
//        Optional<Game> gameOptional2 = gameRepository.findByGameId("0042000206");
//        System.out.println("homeSize: " + gameOptional2.get().getHomeTeam().getFieldingPlayers().size());
        //List<Team> teams = teamRepository.findByTeamId(1610612744);
        //List<Player> players = teamRepository.findByTeamId(1610612744).get(0).getFieldingPlayers();
//        for(Team team : teams){
//            System.out.println(team.getFieldingPlayers().size());
//        }
//        DraftKingsGameFinder draftKingsGameFinder = new DraftKingsGameFinder();
//        draftKingsGameFinder.setGameRepository(gameRepository);
//        draftKingsGameFinder.setGameOddsRepository(gameOddsRepository);
//        draftKingsGameFinder.findGames();
        int days = 700;
        int completedDates = 0;
        GameFinder gameFinder = new GameFinder();
        while(completedDates<days){
            int gamesSAved = 0;
            LocalDate before2021Season = LocalDate.of(2022,4,30);
            System.out.println(localDate);
            List<Game> gameList = new ArrayList<>();
            if(localDate.equals(LocalDate.now()) ) {
                gameList = gameFinder.findTodaysGames();
            }else{
                gameList = gameFinder.findGamesOlderThan2021(localDate);
            }
            //System.out.println(gameList);
            for(Game game : gameList) {
                if (String.valueOf(game.getGameId()).startsWith("1")) {
                    System.out.println("This is a preseason game. Skip.");
                } else {
                    if(!localDate.equals(LocalDate.now()) && !localDate.equals(LocalDate.now().plusDays(1))) {
                        gameFinder.getPlayersRosterAndStatsForGame(game);
                        //game.setHomeTeam(null);
                        //game.setAwayTeam(null);
                        System.out.println(game);
                        linkPlayersToDatabase(game);
                    }
                    Optional<Game> optionalGame = gameRepository.findByGameId(game.getGameId());
                    if (optionalGame.isEmpty()) {
                    //    if (!localDate.equals(LocalDate.now()) && !localDate.equals(LocalDate.now().plusDays(1))) {
                            for (Player player : game.getHomeTeam().getPlayers()) {
                                playerGamePerformanceRepository.saveAll(player.getPlayerGamePerformances());
                            }
                            for (Player player : game.getAwayTeam().getPlayers()) {
                                playerGamePerformanceRepository.saveAll(player.getPlayerGamePerformances());
                            }

                        playerRepository.saveAll(game.getAwayTeam().getPlayers());
                        playerRepository.saveAll(game.getHomeTeam().getPlayers());
                        teamRepository.save(game.getAwayTeam());
                        teamRepository.save(game.getHomeTeam());
                   //     }
                        game.setAwayTeamName(convertNbaTriCode(game.getAwayTeamTricode()));
                        game.setHomeTeamName(convertNbaTriCode(game.getHomeTeamTricode()));
                        gameRepository.save(game);

                    } else {
                        game.setGameId(optionalGame.get().getGameId());
                        game.setId(optionalGame.get().getId());
                     //   if (!localDate.equals(LocalDate.now()) && !localDate.equals(LocalDate.now().plusDays(1))){
                            for (Player player : game.getHomeTeam().getPlayers()) {
                                playerGamePerformanceRepository.saveAll(player.getPlayerGamePerformances());
                            }
                        for (Player player : game.getAwayTeam().getPlayers()) {
                            playerGamePerformanceRepository.saveAll(player.getPlayerGamePerformances());
                        }
                        playerRepository.saveAll(game.getAwayTeam().getPlayers());
                        playerRepository.saveAll(game.getHomeTeam().getPlayers());
                        teamRepository.save(game.getAwayTeam());
                        teamRepository.save(game.getHomeTeam());
                    //    }
                        game.setAwayTeamName(convertNbaTriCode(game.getAwayTeamTricode()));
                        game.setHomeTeamName(convertNbaTriCode(game.getHomeTeamTricode()));

                        gameRepository.save(game);
                    }
                    gamesSAved++;
                    Optional<Game> gameOptional = gameRepository.findByGameId(game.getGameId());

//                    System.out.println("Readback: " + gameOptional.get().getHomeTeam().getTeamName() + " vs " + gameOptional.get().getAwayTeam().getTeamName() +
//                            " HomePlayers: " + gameOptional.get().getHomeTeam().getPlayers().size() + " AwayPlayers: " + gameOptional.get().getAwayTeam().getPlayers().size());
//                    System.out.println("PlayerPerformances: " + playerGamePerformanceRepository.findAllByGameID(game.getGameId()));

                }
            }
            if(gamesSAved > 0) {
                List<CaesarsObject> caesarsObjectList = new ArrayList<>();
                Connection.Response response = Jsoup.connect("https://www.sportsbookreview.com/betting-odds/" +
                                "nba-basketball/pointspread/full-game/?date=" + convertLocalDate(localDate))
                        .method(Connection.Method.GET)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 OPR/78.0.4093.153")
                        .header("Connection", "keep-alive")
                        .header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjkwMDk1YmM2ZGM2ZDY3NzkxZDdkYTFlZWIxYTU1OWEzZDViMmM0ODYiLCJ0eXAiOiJKV1QifQ.eyJ0ZW5hbnQiOiJjbG91ZGJldCIsInV1aWQiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20va3ViZXJzaG11YmVyLXByb2QtY2ItYXV0aCIsImF1ZCI6Imt1YmVyc2htdWJlci1wcm9kLWNiLWF1dGgiLCJhdXRoX3RpbWUiOjE2MzAwMzIwMDYsInVzZXJfaWQiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJzdWIiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJpYXQiOjE2MzAwNzI3ODksImV4cCI6MTYzMDA3NjM4OSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6e30sInNpZ25faW5fcHJvdmlkZXIiOiJjdXN0b20ifX0.D9tWV403lYk9EElG09UW3NZTENUmMkquTf_8y2-eTqqsS3bHcrs3tMHXiXVy2EtPY0zfukc__3pMIQHYrga6TtSdkQjIB3qTf-Biemk9T0vtIhyZ_ClEiFTi3OoQ8FWPBN66jc_5DsRdm0FAQ23TmEwUB7oqTDKRZN6yC_xcc3ksAFoUIRB_9KrDi-ofXAr0VqejB4xKLrtpypxvXgHVs_rrxpA3hWg6FEPhVxQhI3LFEmUOZsB8Dg6j-UAKXiS8rON4f_-MCKk0vJemkBgYFhcbjI1rf_hdmoBB0G8cpXTjJmHAaXzF8xtsl4sYyeOI1GJeiFxuHJK19hMwacfkuw")
                        .header("Content-Type", "application/json")
                        .header("x-player-timezone", "America/Chicago")
                        .ignoreContentType(true)
                        .maxBodySize(0)
                        .execute();
                Document document = (Document) response.parse();
                Element element = document.getElementById("section-nba").getElementById("tbody-nba");
                Elements gameElements = element.select("div[class=d-flex flex-row flex-nowrap border position-relative mt-0 GameRows_eventMarketGridContainer__GuplK GameRows_neverWrap__gnQNO]");
                for (Element element1 : gameElements) {
                    CaesarsObject caesarsObject = new CaesarsObject();
                    Elements test = element1.getElementsByClass("d-flex align-items-center overflow-hidden fs-9 GameRows_gradientContainer__ZajIf");
                    String tricode1;
                    String awayPitcher = "";
                    Element teamElement = test.get(0);
                    if (teamElement.childrenSize() > 1) {
                        tricode1 = test.get(0).getElementsByTag("a").get(0).getElementsByTag("b").get(0).text();
                        awayPitcher = test.get(0).child(2).text().replace(" (R)", "").replace(" (L)", "");
                    } else {
                        tricode1 = test.get(0).getElementsByTag("a").text();
                    }

                    String tricode2;
                    Element teamElement2 = test.get(1);
                    String homePitcher = "";
                    if (teamElement2.childrenSize() > 1) {
                        tricode2 = test.get(1).getElementsByTag("a").get(0).getElementsByTag("b").get(0).text();
                        homePitcher = test.get(1).child(2).text().replace(" (R)", "").replace(" (L)", "");
                    } else {
                        tricode2 = test.get(1).getElementsByTag("a").text();
                    }
                    if (tricode1.equals("Washington")) {
                        tricode1 = "WAS";
                    }
                    if (tricode2.equals("Washington")) {
                        tricode2 = "WAS";
                    }
                    caesarsObject.setAwayTeamName(convertSportsBookNames(tricode1));
                    caesarsObject.setHomeTeamName(convertSportsBookNames(tricode2));
                    caesarsObject.setAwayPitcherName(awayPitcher);
                    caesarsObject.setHomePitcherName(homePitcher);

                    //get odds
                    Elements test2 = element1.getElementsByClass("d-flex align-items-center text-center OddsCells_oddsNumber__u3rsp OddsCells_compact__cawia");

                    //away odds
                    String unParsedAwaySpread = test2.get(4).getElementsByTag("span").get(1).text();
                    if (unParsedAwaySpread.equals("-") || unParsedAwaySpread.equals("")) {
                        unParsedAwaySpread = test2.get(6).getElementsByTag("span").get(1).text();
                    }
                    String unParsedAwayOdds = test2.get(4).getElementsByTag("span").get(2).text();
                    if (unParsedAwayOdds.equals("-") || unParsedAwayOdds.equals("")) {
                        unParsedAwayOdds = test2.get(6).getElementsByTag("span").get(2).text();
                    }
                    String unParsedHomeSpread = test2.get(5).getElementsByTag("span").get(1).text();
                    if (unParsedHomeSpread.equals("-") || unParsedHomeSpread.equals("")) {
                        unParsedHomeSpread = test2.get(7).getElementsByTag("span").get(1).text();
                    }
                    String unParsedHomeOdds = test2.get(5).getElementsByTag("span").get(2).text();
                    if (unParsedHomeOdds.equals("-") || unParsedHomeOdds.equals("")) {
                        unParsedHomeOdds = test2.get(7).getElementsByTag("span").get(2).text();
                    }
                    if (!unParsedHomeSpread.equals("") && !unParsedAwaySpread.equals("")) {
                        if(unParsedHomeSpread.equals("PK")){
                            unParsedHomeSpread = "+0";
                        }
                        if(unParsedAwaySpread.equals("PK")){
                            unParsedAwaySpread = "+0";
                        }
                        caesarsObject.setAwayTeamSpread(Double.parseDouble(unParsedAwaySpread));
                        caesarsObject.setAwayTeamSpreadOdds(Double.parseDouble(unParsedAwayOdds));
                        caesarsObject.setHomeTeamSpread(Double.parseDouble(unParsedHomeSpread));
                        caesarsObject.setHomeTeamSpreadOdds(Double.parseDouble(unParsedHomeOdds));
                        System.out.println(tricode1);


                        caesarsObjectList.add(caesarsObject);
                    }
                }

                //MONEYLINE
                Connection.Response responseML = Jsoup.connect("https://www.sportsbookreview.com/betting-odds/" +
                                "nba-basketball/money-line/full-game/?date=" + convertLocalDate(localDate))
                        .method(Connection.Method.GET)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 OPR/78.0.4093.153")
                        .header("Connection", "keep-alive")
                        .header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjkwMDk1YmM2ZGM2ZDY3NzkxZDdkYTFlZWIxYTU1OWEzZDViMmM0ODYiLCJ0eXAiOiJKV1QifQ.eyJ0ZW5hbnQiOiJjbG91ZGJldCIsInV1aWQiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20va3ViZXJzaG11YmVyLXByb2QtY2ItYXV0aCIsImF1ZCI6Imt1YmVyc2htdWJlci1wcm9kLWNiLWF1dGgiLCJhdXRoX3RpbWUiOjE2MzAwMzIwMDYsInVzZXJfaWQiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJzdWIiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJpYXQiOjE2MzAwNzI3ODksImV4cCI6MTYzMDA3NjM4OSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6e30sInNpZ25faW5fcHJvdmlkZXIiOiJjdXN0b20ifX0.D9tWV403lYk9EElG09UW3NZTENUmMkquTf_8y2-eTqqsS3bHcrs3tMHXiXVy2EtPY0zfukc__3pMIQHYrga6TtSdkQjIB3qTf-Biemk9T0vtIhyZ_ClEiFTi3OoQ8FWPBN66jc_5DsRdm0FAQ23TmEwUB7oqTDKRZN6yC_xcc3ksAFoUIRB_9KrDi-ofXAr0VqejB4xKLrtpypxvXgHVs_rrxpA3hWg6FEPhVxQhI3LFEmUOZsB8Dg6j-UAKXiS8rON4f_-MCKk0vJemkBgYFhcbjI1rf_hdmoBB0G8cpXTjJmHAaXzF8xtsl4sYyeOI1GJeiFxuHJK19hMwacfkuw")
                        .header("Content-Type", "application/json")
                        .header("x-player-timezone", "America/Chicago")
                        .ignoreContentType(true)
                        .maxBodySize(0)
                        .execute();
                Document documentML = (Document) responseML.parse();
                Element elementML = documentML.getElementById("section-nba").getElementById("tbody-nba");
                Elements gameElementsML = elementML.select("div[class=d-flex flex-row flex-nowrap border position-relative mt-0 GameRows_eventMarketGridContainer__GuplK GameRows_neverWrap__gnQNO]");
                for (Element element1ML : gameElementsML) {
                    Elements test = element1ML.getElementsByClass("d-flex align-items-center overflow-hidden fs-9 GameRows_gradientContainer__ZajIf");
                    String tricode1;
                    String awayPitcher = "";
                    Element teamElement = test.get(0);
                    if (teamElement.childrenSize() > 1) {
                        tricode1 = test.get(0).getElementsByTag("a").get(0).getElementsByTag("b").get(0).text();
                        awayPitcher = test.get(0).child(2).text().replace(" (R)", "").replace(" (L)", "");
                    } else {
                        tricode1 = test.get(0).getElementsByTag("a").text();
                    }

                    String tricode2;
                    Element teamElement2 = test.get(1);
                    String homePitcher = "";
                    if (teamElement2.childrenSize() > 1) {
                        tricode2 = test.get(1).getElementsByTag("a").get(0).getElementsByTag("b").get(0).text();
                        homePitcher = test.get(1).child(2).text().replace(" (R)", "").replace(" (L)", "");
                    } else {
                        tricode2 = test.get(1).getElementsByTag("a").text();
                    }
                    if (tricode1.equals("Washington")) {
                        tricode1 = "WAS";
                    }
                    if (tricode2.equals("Washington")) {
                        tricode2 = "WAS";
                    }
                    Elements test2 = element1ML.getElementsByClass("d-flex align-items-center text-center OddsCells_oddsNumber__u3rsp OddsCells_compact__cawia");

                    //away odds
                    String unParsedAwayMoneyLine = test2.get(4).getElementsByTag("span").get(2).text();
                    if (unParsedAwayMoneyLine.equals("-") || unParsedAwayMoneyLine.equals("")) {
                        unParsedAwayMoneyLine = test2.get(6).getElementsByTag("span").get(2).text();
                    }
                    // String unParsedAwayOdds = test2.get(0).getElementsByTag("span").get(2).text();

                    String unParsedHomeMoneyLine = test2.get(5).getElementsByTag("span").get(2).text();
                    if (unParsedHomeMoneyLine.equals("-") || unParsedHomeMoneyLine.equals("")) {
                        unParsedHomeMoneyLine = test2.get(7).getElementsByTag("span").get(2).text();
                    }
                    System.out.println(unParsedHomeMoneyLine);
                    Iterator<CaesarsObject> iterator = caesarsObjectList.iterator();
                    while(iterator.hasNext()){
                        CaesarsObject caesarsObject = iterator.next();
                        if(caesarsObject.getHomeTeamTriCode() != null && caesarsObject.getAwayTeamTriCode() != null) {
                            if (caesarsObject.getAwayTeamTriCode().equals("EST") || caesarsObject.getAwayTeamTriCode().equals("WST") || caesarsObject.getHomeTeamTriCode().equals("EST") ||
                                    caesarsObject.getHomeTeamTriCode().equals("WST")) {
                                iterator.remove();
                            }
                        }
                    }
                    for (CaesarsObject caesarsObject : caesarsObjectList) {
                        if(caesarsObject.getHomeTeamName() != null) {
                            if (caesarsObject.getHomeTeamName().equals(convertSportsBookNames(tricode2))) {
                                caesarsObject.setAwayTeamMoneyLine(Double.parseDouble(unParsedAwayMoneyLine));
                                caesarsObject.setHomeTeamMoneyLine(Double.parseDouble(unParsedHomeMoneyLine));
                            }
                        }
                    }
                    //String unParsedHomeOdds = test2.get(1).getElementsByTag("span").get(2).text();
                }
                Connection.Response responseOU = Jsoup.connect("https://www.sportsbookreview.com/betting-odds/" +
                                "nba-basketball/totals/full-game/?date=" + convertLocalDate(localDate))
                        .method(Connection.Method.GET)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 OPR/78.0.4093.153")
                        .header("Connection", "keep-alive")
                        .header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjkwMDk1YmM2ZGM2ZDY3NzkxZDdkYTFlZWIxYTU1OWEzZDViMmM0ODYiLCJ0eXAiOiJKV1QifQ.eyJ0ZW5hbnQiOiJjbG91ZGJldCIsInV1aWQiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20va3ViZXJzaG11YmVyLXByb2QtY2ItYXV0aCIsImF1ZCI6Imt1YmVyc2htdWJlci1wcm9kLWNiLWF1dGgiLCJhdXRoX3RpbWUiOjE2MzAwMzIwMDYsInVzZXJfaWQiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJzdWIiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJpYXQiOjE2MzAwNzI3ODksImV4cCI6MTYzMDA3NjM4OSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6e30sInNpZ25faW5fcHJvdmlkZXIiOiJjdXN0b20ifX0.D9tWV403lYk9EElG09UW3NZTENUmMkquTf_8y2-eTqqsS3bHcrs3tMHXiXVy2EtPY0zfukc__3pMIQHYrga6TtSdkQjIB3qTf-Biemk9T0vtIhyZ_ClEiFTi3OoQ8FWPBN66jc_5DsRdm0FAQ23TmEwUB7oqTDKRZN6yC_xcc3ksAFoUIRB_9KrDi-ofXAr0VqejB4xKLrtpypxvXgHVs_rrxpA3hWg6FEPhVxQhI3LFEmUOZsB8Dg6j-UAKXiS8rON4f_-MCKk0vJemkBgYFhcbjI1rf_hdmoBB0G8cpXTjJmHAaXzF8xtsl4sYyeOI1GJeiFxuHJK19hMwacfkuw")
                        .header("Content-Type", "application/json")
                        .header("x-player-timezone", "America/Chicago")
                        .ignoreContentType(true)
                        .maxBodySize(0)
                        .execute();
                Document documentOU = (Document) responseOU.parse();
                Element elementOU = documentOU.getElementById("section-nba").getElementById("tbody-nba");
                Elements gameElementsOU = elementOU.select("div[class=d-flex flex-row flex-nowrap border position-relative mt-0 GameRows_eventMarketGridContainer__GuplK GameRows_neverWrap__gnQNO]");
                for (Element element1OU : gameElementsOU) {
                    Elements test = element1OU.getElementsByClass("d-flex align-items-center overflow-hidden fs-9 GameRows_gradientContainer__ZajIf");
                    String tricode1;
                    String awayPitcher = "";
                    Element teamElement = test.get(0);
                    if (teamElement.childrenSize() > 1) {
                        tricode1 = test.get(0).getElementsByTag("a").get(0).getElementsByTag("b").get(0).text();
                        awayPitcher = test.get(0).child(2).text().replace(" (R)", "").replace(" (L)", "");
                    } else {
                        tricode1 = test.get(0).getElementsByTag("a").text();
                    }

                    String tricode2;
                    Element teamElement2 = test.get(1);
                    String homePitcher = "";
                    if (teamElement2.childrenSize() > 1) {
                        tricode2 = test.get(1).getElementsByTag("a").get(0).getElementsByTag("b").get(0).text();
                        homePitcher = test.get(1).child(2).text().replace(" (R)", "").replace(" (L)", "");
                    } else {
                        tricode2 = test.get(1).getElementsByTag("a").text();
                    }
                    if (tricode1.equals("Washington")) {
                        tricode1 = "WAS";
                    }
                    if (tricode2.equals("Washington")) {
                        tricode2 = "WAS";
                    }
                    Elements test2 = element1OU.getElementsByClass("d-flex align-items-center text-center OddsCells_oddsNumber__u3rsp OddsCells_compact__cawia");

                    //away odds
                    String unParseOverUnder = test2.get(4).getElementsByTag("span").get(1).text();
                    if (unParseOverUnder.equals("-") || unParseOverUnder.equals("")) {
                        unParseOverUnder = test2.get(6).getElementsByTag("span").get(1).text();
                    }
                    String unParsedOverOdds = test2.get(4).getElementsByTag("span").get(2).text();
                    if (unParsedOverOdds.equals("-") || unParsedOverOdds.equals("")) {
                        unParsedOverOdds = test2.get(6).getElementsByTag("span").get(2).text();
                    }
                    String unParsedUnderOdds = test2.get(5).getElementsByTag("span").get(2).text();
                    if (unParsedUnderOdds.equals("-") || unParsedUnderOdds.equals("")) {
                        unParsedUnderOdds = test2.get(7).getElementsByTag("span").get(2).text();
                    }
                    //  String unParsedHomeMoneyLine = test2.get(1).getElementsByTag("span").get(1).text();
                    System.out.println(unParsedUnderOdds);

                    for (CaesarsObject caesarsObject : caesarsObjectList) {
                        if(caesarsObject.getHomeTeamName() != null) {
                            if (caesarsObject.getHomeTeamName().equals(convertSportsBookNames(tricode2))) {
                                if (!unParseOverUnder.isEmpty()) {
                                    caesarsObject.setOverUnder(Double.parseDouble(unParseOverUnder));
                                    caesarsObject.setOddsOver(Double.parseDouble(unParsedOverOdds));
                                    caesarsObject.setOddsUnder(Double.parseDouble(unParsedUnderOdds));
                                }
                            }
                        }
                    }
                    //String unParsedHomeOdds = test2.get(1).getElementsByTag("span").get(2).text();
                }

                for(Game game : gameList) {
                    for (CaesarsObject caesarsObject : caesarsObjectList) {
                        if(caesarsObject.getHomeTeamName() != null) {
                            if (caesarsObject.getHomeTeamName().contains(game.getHomeTeamName()) && caesarsObject.getAwayTeamName().contains(game.getAwayTeamName())) {
                                GameOdds gameOdds = new GameOdds();
                                gameOdds.setGameId(game.getGameId());
                                gameOdds.setDate(game.getDate());
                                gameOdds.setOverUnder(caesarsObject.getOverUnder());
                                gameOdds.setAwayTeamSpread(caesarsObject.getAwayTeamSpread());
                                gameOdds.setHomeTeamSpread(caesarsObject.getHomeTeamSpread());
                                gameOdds.setAwayTeamSpreadOdds(caesarsObject.getAwayTeamSpreadOdds());
                                gameOdds.setHomeTeamSpreadOdds(caesarsObject.getHomeTeamSpreadOdds());
                                gameOdds.setHomeTeamTriCode(game.getHomeTeamTricode());
                                gameOdds.setAwayTeamTriCode(game.getAwayTeamTricode());
                                gameOdds.setHomeTeamMoneyLine(caesarsObject.getHomeTeamMoneyLine());
                                gameOdds.setAwayTeamMoneyLine(caesarsObject.getAwayTeamMoneyLine());
                                if (gameOddsRepository.findByGameId(gameOdds.getGameId()).isEmpty()) {
                                    System.out.println("saving odds.");
                                    gameOddsRepository.save(gameOdds);
                                } else {
                                    GameOdds gameOdds1 = gameOddsRepository.findByGameId(gameOdds.getGameId()).get();
                                    gameOdds1.setDate(gameOdds.getDate());
                                    gameOdds1.setOverUnder(caesarsObject.getOverUnder());
                                    gameOdds1.setAwayTeamSpread(caesarsObject.getAwayTeamSpread());
                                    gameOdds1.setHomeTeamSpread(caesarsObject.getHomeTeamSpread());
                                    gameOdds1.setAwayTeamSpreadOdds(caesarsObject.getAwayTeamSpreadOdds());
                                    gameOdds1.setHomeTeamSpreadOdds(caesarsObject.getHomeTeamSpreadOdds());
                                    gameOdds1.setHomeTeamTriCode(game.getHomeTeamTricode());
                                    gameOdds1.setAwayTeamTriCode(game.getAwayTeamTricode());
                                    gameOdds1.setHomeTeamMoneyLine(caesarsObject.getHomeTeamMoneyLine());
                                    gameOdds1.setAwayTeamMoneyLine(caesarsObject.getAwayTeamMoneyLine());
                                    System.out.println("saving odds.");
                                    gameOddsRepository.save(gameOdds1);
                                    break;
                                }
                            }
                        }
                    }
                }
                //gameFinder.setGameRepository(gameRepository);
                //gameFinder.findGamesOnDateFromDB(localDate);


            }
            localDate = localDate.minusDays(1);
            completedDates = completedDates + 1;
        }

    }

    public void linkPlayersToDatabase(Game game){
        for(Player player : game.getHomeTeam().getPlayers()){
            bind(player);
        }
        for(Player player : game.getAwayTeam().getPlayers()){
            bind(player);
        }
    }

    private void bind(Player player) {
        if(player.getId() == null){
            Optional<Player> playerOptional = playerRepository.findByPlayerID(player.getPlayerID());
            playerOptional.ifPresent(value -> player.setId(value.getId()));
            if(playerOptional.isPresent()){
                player.setId(playerOptional.get().getId());
                Set<PlayerGamePerformance> playerGamePerformanceList = playerOptional.get().getPlayerGamePerformances();
                playerGamePerformanceList.addAll(player.getPlayerGamePerformances());
                player.setPlayerGamePerformances(playerGamePerformanceList);
            }
        }
    }
    public String convertSportsBookNames(String tricode) {
        return switch (tricode) {
            case "L.A. Clippers" -> "Clippers";
            case "Oklahoma City" -> "Thunder";
            case "Denver" -> "Nuggets";
            case "Golden State" -> "Warriors";
            case "Utah" -> "Jazz";
            case "Phoenix" -> "Suns";
            case "San Antonio" -> "Spurs";
            case "Minnesota" -> "Timberwolves";
            case "Memphis" -> "Grizzlies";
            case "Dallas" -> "Mavericks";
            case "Washington" -> "Wizards";
            case "WAS" -> "Wizards";
            case "Charlotte" -> "Hornets";
            case "Philadelphia" -> "76ers";
            case "Orlando" -> "Magic";
            case "Cleveland" -> "Cavaliers";
            case "Atlanta" -> "Hawks";
            case "Brooklyn" -> "Nets";
            case "New York" -> "Knicks";
            case "Detroit" -> "Pistons";
            case "Sacramento" -> "Kings";
            case "L.A. Lakers" -> "Lakers";
            case "Miami" -> "Heat";
            case "Houston" -> "Rockets";
            case "Chicago" -> "Bulls";
            case "Portland" -> "Trailblazers";
            case "Toronto" -> "Raptors";
            case "Milwaukee" -> "Bucks";
            case "Indiana" -> "Pacers";
            case "New Orleans" -> "Pelicans";
            case "Boston" -> "Celtics";
            default -> null;
        };
    }
    public String convertNbaTriCode(String tricode) {
        return switch (tricode) {
            case "LAC" -> "Clippers";
            case "OKC" -> "Thunder";
            case "DEN" -> "Nuggets";
            case "GSW" -> "Warriors";
            case "UTA" -> "Jazz";
            case "PHX" -> "Suns";
            case "SAS" -> "Spurs";
            case "MIN" -> "Timberwolves";
            case "MEM" -> "Grizzlies";
            case "DAL" -> "Mavericks";
            case "WAS" -> "Wizards";
            case "CHA" -> "Hornets";
            case "PHI" -> "76ers";
            case "ORL" -> "Magic";
            case "CLE" -> "Cavaliers";
            case "ATL" -> "Hawks";
            case "BKN" -> "Nets";
            case "NYK" -> "Knicks";
            case "DET" -> "Pistons";
            case "SAC" -> "Kings";
            case "LAL" -> "Lakers";
            case "MIA" -> "Heat";
            case "HOU" -> "Rockets";
            case "CHI" -> "Bulls";
            case "POR" -> "Trailblazers";
            case "TOR" -> "Raptors";
            case "MIL" -> "Bucks";
            case "IND" -> "Pacers";
            case "NOP" -> "Pelicans";
            case "BOS" -> "Celtics";
            default -> null;
        };
    }
    public String convertLocalDate(LocalDate localDate){
        String dateString = "";
        int day = localDate.getDayOfMonth();
        int month = localDate.getMonthValue();
        dateString = dateString + localDate.getYear() + "-";
        String monthString = "";
        if (month < 10) {
            monthString = "0" + month;
        } else {
            monthString = String.valueOf(month);
        }
        dateString = dateString + monthString + "-";
        String dayString = "";
        if (day < 10) {
            dayString = "0" + day;
        } else {
            dayString = String.valueOf(day);
        }
        dateString = dateString + dayString;
        return dateString;
    }
}
