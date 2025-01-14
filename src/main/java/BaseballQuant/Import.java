package BaseballQuant;

import BaseballQuant.Model.*;
import BaseballQuant.Repository.*;
import BaseballQuant.Util.GameFinder;
import org.apache.commons.lang3.StringUtils;
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
    private PitcherRepository pitcherRepository;
    @Autowired
    private PlayerGamePerformanceRepository playerGamePerformanceRepository;
    @Autowired
    private PitcherPerformanceRepository pitcherPerformanceRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private MLBGameOddsRepository gameOddsRepository;

//    @Autowired
//    private GameOddsRepository gameOddsRepository;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        LocalDate localDate = LocalDate.of(2024, 9,24);
        LocalDate endDate = LocalDate.of(2021,6,27);
        GameFinder gameFinder = new GameFinder((List<MLBGame>) gameRepository.findAll());
        int completedDates = 0;

//
//        List<MLBPlayerGamePerformance> playerGamePerformanceList = (List<MLBPlayerGamePerformance>) playerGamePerformanceRepository.findAll();
//        playerGamePerformanceList.removeIf(performance -> performance.getMlbGame() != null );
//        int games = 0 ;
//        for(MLBPlayerGamePerformance performance : playerGamePerformanceList){
//            int gameId = performance.getGameId();
//            MLBGame game = gameFinder.findGameById(gameId);
//
//            performance.setMlbGame(game);
//            playerGamePerformanceRepository.save(performance);
//            games ++;
//            System.out.println(games + "/" +playerGamePerformanceList.size());
//        }



        while(localDate.isAfter(endDate)) {
            List<MLBGame> games = gameFinder.findGamesOnDate(localDate);
            for (MLBGame mlbGame : games) {
                gameFinder.getPlayersRosterAndStatsForGame(mlbGame);
            }
            int gamesSAved = 0;
            for (MLBGame mlbGame : games) {

                linkPlayersToDatabase(mlbGame);
                int id = mlbGame.getGameId();
                Optional<MLBGame> mlbGameOptional = gameRepository.findByGameId(id);
                System.out.println(id);
                gamesSAved++;
                if (mlbGameOptional.isEmpty()) {
                    saveGameData(mlbGame);

                }else{
                    mlbGame.setId(mlbGameOptional.get().getId());
                    saveGameData(mlbGame);
                    System.out.println(mlbGame.getGameId() + " is already in DB complete.");


                }
                Optional<MLBGame> gameOptional = gameRepository.findByGameId(mlbGame.getGameId());

                //System.out.println("Readback: " + gameOptional.get().getHomeMLBTeam().getTeamName() + " vs " + gameOptional.get().getAwayMLBTeam().getTeamName() +
                //        " HomePlayers: " + gameOptional.get().getHomeMLBTeam().getFieldingPlayers() + " AwayPlayers: " + gameOptional.get().getAwayMLBTeam().getFieldingPlayers());
                //System.out.println("PlayerPerformances: " + playerGamePerformanceRepository.findAllByGameId(mlbGame.getGameId()));
                //System.out.println("PitcherPerformances: " + pitcherPerformanceRepository.findAllByGameId(mlbGame.getGameId()));
                //System.out.println(gameOptional.get());
            }
            if(gamesSAved > 0){
            List<CaesarsObject> caesarsObjectList = new ArrayList<>();
            Connection.Response response = Jsoup.connect("https://www.sportsbookreview.com/betting-odds/" +
                            "mlb-baseball/pointspread/full-game/?date=" +convertLocalDate(localDate))
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
            Element element = document.getElementById("section-mlb").getElementById("tbody-mlb");
            Elements gameElements = element.select("div[class=d-flex flex-row flex-nowrap border position-relative mt-0 GameRows_eventMarketGridContainer__GuplK GameRows_neverWrap__gnQNO]");
            for(Element element1 : gameElements){
                CaesarsObject caesarsObject = new CaesarsObject();
                Elements test = element1.getElementsByClass("d-flex align-items-center overflow-hidden fs-9 GameRows_gradientContainer__ZajIf");
                String tricode1;
                String awayPitcher = "";
                Element teamElement = test.get(0);
                if(teamElement.childrenSize() > 1){
                    tricode1 = test.get(0).getElementsByTag("a").get(0).getElementsByTag("b").get(0).text();
                    awayPitcher = test.get(0).child(2).text().replace(" (R)", "").replace(" (L)", "");
                }else{
                    tricode1 = test.get(0).getElementsByTag("a").text();
                }

                String tricode2;
                Element teamElement2 = test.get(1);
                String homePitcher = "";
                if(teamElement2.childrenSize() > 1){
                    tricode2 = test.get(1).getElementsByTag("a").get(0).getElementsByTag("b").get(0).text();
                    homePitcher = test.get(1).child(2).text().replace(" (R)", "").replace(" (L)", "");
                }else{
                    tricode2 = test.get(1).getElementsByTag("a").text();
                }
                if(tricode1.equals("Washington")){
                    tricode1 = "WAS";
                }
                if(tricode2.equals("Washington")){
                    tricode2 = "WAS";
                }
                caesarsObject.setAwayTeamName(convertTriCode(tricode1));
                caesarsObject.setHomeTeamName(convertTriCode(tricode2));
                caesarsObject.setAwayPitcherName(awayPitcher);
                caesarsObject.setHomePitcherName(homePitcher);

                //get odds
                Elements test2 = element1.getElementsByClass("d-flex align-items-center text-center OddsCells_oddsNumber__u3rsp OddsCells_compact__cawia");

                //away odds
                String unParsedAwaySpread = test2.get(4).getElementsByTag("span").get(1).text();
                if(unParsedAwaySpread.equals("-") || unParsedAwaySpread.equals("")){
                    unParsedAwaySpread = test2.get(6).getElementsByTag("span").get(1).text();
                }
                String unParsedAwayOdds = test2.get(4).getElementsByTag("span").get(2).text();
                if(unParsedAwayOdds.equals("-") || unParsedAwayOdds.equals("")){
                    unParsedAwayOdds = test2.get(6).getElementsByTag("span").get(2).text();
                }
                String unParsedHomeSpread = test2.get(5).getElementsByTag("span").get(1).text();
                if(unParsedHomeSpread.equals("-") || unParsedHomeSpread.equals("")){
                    unParsedHomeSpread = test2.get(7).getElementsByTag("span").get(1).text();
                }
                String unParsedHomeOdds = test2.get(5).getElementsByTag("span").get(2).text();
                if(unParsedHomeOdds.equals("-") || unParsedHomeOdds.equals("")){
                    unParsedHomeOdds = test2.get(7).getElementsByTag("span").get(2).text();
                }
                if(!unParsedHomeSpread.equals("") && !unParsedAwaySpread.equals("")) {
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
                            "mlb-baseball/?date=" +convertLocalDate(localDate))
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
            Element elementML = documentML.getElementById("section-mlb").getElementById("tbody-mlb");
            Elements gameElementsML = elementML.select("div[class=d-flex flex-row flex-nowrap border position-relative mt-0 GameRows_eventMarketGridContainer__GuplK GameRows_neverWrap__gnQNO]");
            for(Element element1ML : gameElementsML){
                Elements test = element1ML.getElementsByClass("d-flex align-items-center overflow-hidden fs-9 GameRows_gradientContainer__ZajIf");
                String tricode1;
                String awayPitcher = "";
                Element teamElement = test.get(0);
                if(teamElement.childrenSize() > 1){
                    tricode1 = test.get(0).getElementsByTag("a").get(0).getElementsByTag("b").get(0).text();
                    awayPitcher = test.get(0).child(2).text().replace(" (R)", "").replace(" (L)", "");
                }else{
                    tricode1 = test.get(0).getElementsByTag("a").text();
                }

                String tricode2;
                Element teamElement2 = test.get(1);
                String homePitcher = "";
                if(teamElement2.childrenSize() > 1){
                    tricode2 = test.get(1).getElementsByTag("a").get(0).getElementsByTag("b").get(0).text();
                    homePitcher = test.get(1).child(2).text().replace(" (R)", "").replace(" (L)", "");
                }else{
                    tricode2 = test.get(1).getElementsByTag("a").text();
                }
                if(tricode1.equals("Washington")){
                    tricode1 = "WAS";
                }
                if(tricode2.equals("Washington")){
                    tricode2 = "WAS";
                }
                Elements test2 = element1ML.getElementsByClass("d-flex align-items-center text-center OddsCells_oddsNumber__u3rsp OddsCells_compact__cawia");

                //away odds
                String unParsedAwayMoneyLine = test2.get(4).getElementsByTag("span").get(2).text();
                if(unParsedAwayMoneyLine.equals("-") || unParsedAwayMoneyLine.equals("")){
                    unParsedAwayMoneyLine = test2.get(6).getElementsByTag("span").get(2).text();
                }
                // String unParsedAwayOdds = test2.get(0).getElementsByTag("span").get(2).text();

                String unParsedHomeMoneyLine = test2.get(5).getElementsByTag("span").get(2).text();
                if(unParsedHomeMoneyLine.equals("-") || unParsedHomeMoneyLine.equals("")){
                    unParsedHomeMoneyLine = test2.get(7).getElementsByTag("span").get(2).text();
                }
                System.out.println(unParsedHomeMoneyLine);

                for(CaesarsObject caesarsObject : caesarsObjectList){
                    if(caesarsObject.getHomeTeamName().equals(convertTriCode(tricode2)) && caesarsObject.getHomePitcherName().equals(homePitcher)){
                        caesarsObject.setAwayTeamMoneyLine(Double.parseDouble(unParsedAwayMoneyLine));
                        caesarsObject.setHomeTeamMoneyLine(Double.parseDouble(unParsedHomeMoneyLine));
                    }
                }
                //String unParsedHomeOdds = test2.get(1).getElementsByTag("span").get(2).text();
            }
            Connection.Response responseOU = Jsoup.connect("https://www.sportsbookreview.com/betting-odds/" +
                            "mlb-baseball/totals/full-game/?date=" +convertLocalDate(localDate))
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
            Element elementOU = documentOU.getElementById("section-mlb").getElementById("tbody-mlb");
            Elements gameElementsOU = elementOU.select("div[class=d-flex flex-row flex-nowrap border position-relative mt-0 GameRows_eventMarketGridContainer__GuplK GameRows_neverWrap__gnQNO]");
            for(Element element1OU : gameElementsOU){
                Elements test = element1OU.getElementsByClass("d-flex align-items-center overflow-hidden fs-9 GameRows_gradientContainer__ZajIf");
                String tricode1;
                String awayPitcher = "";
                Element teamElement = test.get(0);
                if(teamElement.childrenSize() > 1){
                    tricode1 = test.get(0).getElementsByTag("a").get(0).getElementsByTag("b").get(0).text();
                    awayPitcher = test.get(0).child(2).text().replace(" (R)", "").replace(" (L)", "");
                }else{
                    tricode1 = test.get(0).getElementsByTag("a").text();
                }

                String tricode2;
                Element teamElement2 = test.get(1);
                String homePitcher = "";
                if(teamElement2.childrenSize() > 1){
                    tricode2 = test.get(1).getElementsByTag("a").get(0).getElementsByTag("b").get(0).text();
                    homePitcher = test.get(1).child(2).text().replace(" (R)", "").replace(" (L)", "");
                }else{
                    tricode2 = test.get(1).getElementsByTag("a").text();
                }
                if(tricode1.equals("Washington")){
                    tricode1 = "WAS";
                }
                if(tricode2.equals("Washington")){
                    tricode2 = "WAS";
                }
                Elements test2 = element1OU.getElementsByClass("d-flex align-items-center text-center OddsCells_oddsNumber__u3rsp OddsCells_compact__cawia");

                //away odds
                String unParseOverUnder = test2.get(4).getElementsByTag("span").get(1).text();
                if(unParseOverUnder.equals("-")|| unParseOverUnder.equals("")){
                    unParseOverUnder = test2.get(6).getElementsByTag("span").get(1).text();
                }
                String unParsedOverOdds = test2.get(4).getElementsByTag("span").get(2).text();
                if(unParsedOverOdds.equals("-")|| unParsedOverOdds.equals("")){
                    unParsedOverOdds = test2.get(6).getElementsByTag("span").get(2).text();
                }
                String unParsedUnderOdds = test2.get(5).getElementsByTag("span").get(2).text();
                if(unParsedUnderOdds.equals("-") || unParsedUnderOdds.equals("")){
                    unParsedUnderOdds = test2.get(7).getElementsByTag("span").get(2).text();
                }
                //  String unParsedHomeMoneyLine = test2.get(1).getElementsByTag("span").get(1).text();
                System.out.println(unParsedUnderOdds);

                for(CaesarsObject caesarsObject : caesarsObjectList){
                    if(caesarsObject.getHomeTeamName().equals(convertTriCode(tricode2)) && caesarsObject.getHomePitcherName().equals(homePitcher)){
                        if(!unParseOverUnder.isEmpty()) {
                            caesarsObject.setOverUnder(Double.parseDouble(unParseOverUnder));
                            caesarsObject.setOddsOver(Double.parseDouble(unParsedOverOdds));
                            caesarsObject.setOddsUnder(Double.parseDouble(unParsedUnderOdds));
                        }
                    }
                }
                //String unParsedHomeOdds = test2.get(1).getElementsByTag("span").get(2).text();
            }

            for(MLBGame mlbGame : games){
                String homeTeamName = mlbGame.getHomeTeamName();
                if(homeTeamName.equals("D-backs")){
                    homeTeamName = "Diamondbacks";
                }
                String awayTeamName = mlbGame.getAwayTeamName();
                if(awayTeamName.equals("D-backs")){
                    awayTeamName = "Diamondbacks";
                }
                if(mlbGame.getHomeStartingPitcher() != null) {
                    String homeTeamPitcher = mlbGame.getHomeStartingPitcher().getFullName();
                    List<String> nameStrings = Arrays.asList(StringUtils.stripAccents(homeTeamPitcher).replace("Jr.", "").split(" "));
                    String pitcherLastName = nameStrings.get(nameStrings.size() - 1).replace(".", "");
                    System.out.println("pitcher lastName: " + pitcherLastName);
                    for (CaesarsObject caesarsObject : caesarsObjectList) {
                        if (caesarsObject.getHomeTeamName() != null && caesarsObject.getHomePitcherName() != null && caesarsObject.getOverUnder() != 0.0){
                            if(caesarsObject.getHomePitcherName().toLowerCase(Locale.ROOT).contains("l m jr")){
                                caesarsObject.setHomePitcherName("Lance McCullers Jr");
                            }
                            String cleanPitcherName = StringUtils.stripAccents(pitcherLastName).replace(".", "").toLowerCase().replace("jr.", "");;
                            String cleanCaesarsPitcherName = StringUtils.stripAccents(caesarsObject.getHomePitcherName()).toLowerCase().replace(".", "").replace("jr.", "");
                            if (cleanCaesarsPitcherName.contains(cleanPitcherName.toLowerCase()) &&  (caesarsObject.getHomeTeamName().toLowerCase().contains(homeTeamName.toLowerCase()) || (caesarsObject.getHomeTeamName().contains("Guardians") && homeTeamName.toLowerCase().contains("indians") )) ){
                                MLBGameOdds mlbGameOdds = new MLBGameOdds();
                                mlbGameOdds.setGameId(mlbGame.getGameId());
                                mlbGameOdds.setDate(mlbGame.getDate());
                                mlbGameOdds.setOverUnder(caesarsObject.getOverUnder());
                                mlbGameOdds.setAwayTeamSpread(caesarsObject.getAwayTeamSpread());
                                mlbGameOdds.setHomeTeamSpread(caesarsObject.getHomeTeamSpread());
                                mlbGameOdds.setAwayTeamSpreadOdds(caesarsObject.getAwayTeamSpreadOdds());
                                mlbGameOdds.setHomeTeamSpreadOdds(caesarsObject.getHomeTeamSpreadOdds());
                                mlbGameOdds.setHomeTeamName(mlbGame.getHomeTeamName());
                                mlbGameOdds.setAwayTeamName(mlbGame.getAwayTeamName());
                                mlbGameOdds.setHomeTeamMoneyLine(caesarsObject.getHomeTeamMoneyLine());
                                mlbGameOdds.setAwayTeamMoneyLine(caesarsObject.getAwayTeamMoneyLine());
                                mlbGameOdds.setOverUnder(caesarsObject.getOverUnder());
                                mlbGameOdds.setOddsOver(caesarsObject.getOddsOver());
                                mlbGameOdds.setOddsUnder(caesarsObject.getOddsUnder());
                                if (gameOddsRepository.findByGameId(mlbGameOdds.getGameId()).isEmpty()) {
                                    System.out.println("saving odds.");
                                    gameOddsRepository.save(mlbGameOdds);
                                }else{
                                    MLBGameOdds mlbGameOdds1 = gameOddsRepository.findByGameId(mlbGameOdds.getGameId()).get();
                                    mlbGameOdds1.setDate(mlbGame.getDate());
                                    mlbGameOdds1.setOverUnder(caesarsObject.getOverUnder());
                                    mlbGameOdds1.setAwayTeamSpread(caesarsObject.getAwayTeamSpread());
                                    mlbGameOdds1.setHomeTeamSpread(caesarsObject.getHomeTeamSpread());
                                    mlbGameOdds1.setAwayTeamSpreadOdds(caesarsObject.getAwayTeamSpreadOdds());
                                    mlbGameOdds1.setHomeTeamSpreadOdds(caesarsObject.getHomeTeamSpreadOdds());
                                    mlbGameOdds1.setHomeTeamName(mlbGame.getHomeTeamName());
                                    mlbGameOdds1.setAwayTeamName(mlbGame.getAwayTeamName());
                                    mlbGameOdds1.setHomeTeamMoneyLine(caesarsObject.getHomeTeamMoneyLine());
                                    mlbGameOdds1.setAwayTeamMoneyLine(caesarsObject.getAwayTeamMoneyLine());
                                    mlbGameOdds1.setOverUnder(caesarsObject.getOverUnder());
                                    mlbGameOdds1.setOddsOver(caesarsObject.getOddsOver());
                                    mlbGameOdds1.setOddsUnder(caesarsObject.getOddsUnder());
                                    gameOddsRepository.save(mlbGameOdds1);
                                    break;
                                }
                            }
                        }else{
//                        String cleanCaesarsTitle = StringUtils.stripAccents(caesarsObject.getEventTitle()).toLowerCase();
//                        if(cleanCaesarsTitle.contains(mlbGame.getHomeTeamName().toLowerCase()) && cleanCaesarsTitle.contains(mlbGame.getAwayTeamName().toLowerCase()) ){
//                            MLBGameOdds mlbGameOdds = new MLBGameOdds();
//                            mlbGameOdds.setGameId(mlbGame.getGameId());
//                            mlbGameOdds.setDate(mlbGame.getDate());
//                            mlbGameOdds.setOverUnder(caesarsObject.getOverUnder());
//                            mlbGameOdds.setAwayTeamSpread(caesarsObject.getAwayTeamSpread());
//                            mlbGameOdds.setHomeTeamSpread(caesarsObject.getHomeTeamSpread());
//                            mlbGameOdds.setAwayTeamSpreadOdds(caesarsObject.getAwayTeamSpreadOdds());
//                            mlbGameOdds.setHomeTeamSpreadOdds(caesarsObject.getHomeTeamSpreadOdds());
//                            mlbGameOdds.setHomeTeamName(mlbGame.getHomeTeamName());
//                            mlbGameOdds.setAwayTeamName(mlbGame.getAwayTeamName());
//                            if (mlbGameOddsRepository.findByGameId(mlbGameOdds.getGameId()).isEmpty()) {
//                                System.out.println("saving odds.");
//                                mlbGameOddsRepository.save(mlbGameOdds);
//                                break;
//                            }else{
//                                MLBGameOdds mlbGameOdds1 = mlbGameOddsRepository.findByGameId(mlbGameOdds.getGameId()).get();
//                                mlbGameOdds1.setDate(mlbGame.getDate());
//                                mlbGameOdds1.setOverUnder(caesarsObject.getOverUnder());
//                                mlbGameOdds1.setAwayTeamSpread(caesarsObject.getAwayTeamSpread());
//                                mlbGameOdds1.setHomeTeamSpread(caesarsObject.getHomeTeamSpread());
//                                mlbGameOdds1.setAwayTeamSpreadOdds(caesarsObject.getAwayTeamSpreadOdds());
//                                mlbGameOdds1.setHomeTeamSpreadOdds(caesarsObject.getHomeTeamSpreadOdds());
//                                mlbGameOdds1.setHomeTeamName(mlbGame.getHomeTeamName());
//                                mlbGameOdds1.setAwayTeamName(mlbGame.getAwayTeamName());
//                                mlbGameOddsRepository.save(mlbGameOdds1);
//                                break;
//                            }
//                        }
                        }
                    }
                }else if(mlbGame.getAwayStartingPitcher() != null){
                    String awayTeamPitcher = mlbGame.getAwayStartingPitcher().getFullName();
                    List<String> nameStrings = Arrays.asList(StringUtils.stripAccents(awayTeamPitcher).split(" "));
                    String pitcherLastName = nameStrings.get(nameStrings.size() - 1);
                    System.out.println("pitcher lastName: " + pitcherLastName);
                    for (BaseballQuant.Model.CaesarsObject caesarsObject : caesarsObjectList) {
                        if (caesarsObject.getAwayPitcherName() != null && caesarsObject.getAwayTeamName() != null) {
                            if(caesarsObject.getHomePitcherName().toLowerCase(Locale.ROOT).contains("l m jr")){
                                caesarsObject.setAwayPitcherName("Lance McCullers Jr");
                            }
                            String cleanPitcherName = StringUtils.stripAccents(pitcherLastName).replace(".", "").toLowerCase().replace("jr.", "");;
                            String cleanCaesarsPitcherName = StringUtils.stripAccents(caesarsObject.getAwayPitcherName()).toLowerCase().replace(".", "").replace("jr.", "");
                            if (cleanCaesarsPitcherName.contains(cleanPitcherName.toLowerCase()) && caesarsObject.getAwayTeamName().toLowerCase().contains(awayTeamName.toLowerCase())) {
                                MLBGameOdds mlbGameOdds = new MLBGameOdds();
                                mlbGameOdds.setGameId(mlbGame.getGameId());
                                mlbGameOdds.setDate(mlbGame.getDate());
                                mlbGameOdds.setOverUnder(caesarsObject.getOverUnder());
                                mlbGameOdds.setAwayTeamSpread(caesarsObject.getAwayTeamSpread());
                                mlbGameOdds.setHomeTeamSpread(caesarsObject.getHomeTeamSpread());
                                mlbGameOdds.setAwayTeamSpreadOdds(caesarsObject.getAwayTeamSpreadOdds());
                                mlbGameOdds.setHomeTeamSpreadOdds(caesarsObject.getHomeTeamSpreadOdds());
                                mlbGameOdds.setHomeTeamName(mlbGame.getHomeTeamName());
                                mlbGameOdds.setAwayTeamName(mlbGame.getAwayTeamName());
                                mlbGameOdds.setHomeTeamMoneyLine(caesarsObject.getHomeTeamMoneyLine());
                                mlbGameOdds.setAwayTeamMoneyLine(caesarsObject.getAwayTeamMoneyLine());
                                mlbGameOdds.setOverUnder(caesarsObject.getOverUnder());
                                mlbGameOdds.setOddsOver(caesarsObject.getOddsOver());
                                mlbGameOdds.setOddsUnder(caesarsObject.getOddsUnder());
                                if (gameOddsRepository.findByGameId(mlbGameOdds.getGameId()).isEmpty()) {
                                    System.out.println("saving odds.");
                                    gameOddsRepository.save(mlbGameOdds);
                                    break;
                                }else{
                                    MLBGameOdds mlbGameOdds1 = gameOddsRepository.findByGameId(mlbGameOdds.getGameId()).get();
                                    mlbGameOdds1.setDate(mlbGame.getDate());
                                    mlbGameOdds1.setOverUnder(caesarsObject.getOverUnder());
                                    mlbGameOdds1.setAwayTeamSpread(caesarsObject.getAwayTeamSpread());
                                    mlbGameOdds1.setHomeTeamSpread(caesarsObject.getHomeTeamSpread());
                                    mlbGameOdds1.setAwayTeamSpreadOdds(caesarsObject.getAwayTeamSpreadOdds());
                                    mlbGameOdds1.setHomeTeamSpreadOdds(caesarsObject.getHomeTeamSpreadOdds());
                                    mlbGameOdds1.setHomeTeamName(mlbGame.getHomeTeamName());
                                    mlbGameOdds1.setAwayTeamName(mlbGame.getAwayTeamName());
                                    mlbGameOdds1.setHomeTeamMoneyLine(caesarsObject.getHomeTeamMoneyLine());
                                    mlbGameOdds1.setAwayTeamMoneyLine(caesarsObject.getAwayTeamMoneyLine());
                                    mlbGameOdds1.setOverUnder(caesarsObject.getOverUnder());
                                    mlbGameOdds1.setOddsOver(caesarsObject.getOddsOver());
                                    mlbGameOdds1.setOddsUnder(caesarsObject.getOddsUnder());
                                    gameOddsRepository.save(mlbGameOdds1);
                                    break;
                                }
                            }
                        }else{
                            String cleanCaesarsTitle = StringUtils.stripAccents(caesarsObject.getEventTitle()).toLowerCase();
                            if(cleanCaesarsTitle.contains(mlbGame.getHomeTeamName().toLowerCase()) && cleanCaesarsTitle.contains(mlbGame.getAwayTeamName().toLowerCase()) ){
                                MLBGameOdds mlbGameOdds = new MLBGameOdds();
                                mlbGameOdds.setGameId(mlbGame.getGameId());
                                mlbGameOdds.setDate(mlbGame.getDate());
                                mlbGameOdds.setOverUnder(caesarsObject.getOverUnder());
                                mlbGameOdds.setAwayTeamSpread(caesarsObject.getAwayTeamSpread());
                                mlbGameOdds.setHomeTeamSpread(caesarsObject.getHomeTeamSpread());
                                mlbGameOdds.setAwayTeamSpreadOdds(caesarsObject.getAwayTeamSpreadOdds());
                                mlbGameOdds.setHomeTeamSpreadOdds(caesarsObject.getHomeTeamSpreadOdds());
                                mlbGameOdds.setHomeTeamName(mlbGame.getHomeTeamName());
                                mlbGameOdds.setAwayTeamName(mlbGame.getAwayTeamName());
                                mlbGameOdds.setHomeTeamMoneyLine(caesarsObject.getHomeTeamMoneyLine());
                                mlbGameOdds.setAwayTeamMoneyLine(caesarsObject.getAwayTeamMoneyLine());
                                mlbGameOdds.setOverUnder(caesarsObject.getOverUnder());
                                mlbGameOdds.setOddsOver(caesarsObject.getOddsOver());
                                mlbGameOdds.setOddsUnder(caesarsObject.getOddsUnder());
                                if (gameOddsRepository.findByGameId(mlbGameOdds.getGameId()).isEmpty()) {
                                    System.out.println("saving odds.");
                                    gameOddsRepository.save(mlbGameOdds);
                                    break;
                                }else{
                                    MLBGameOdds mlbGameOdds1 = gameOddsRepository.findByGameId(mlbGameOdds.getGameId()).get();
                                    mlbGameOdds1.setDate(mlbGame.getDate());
                                    mlbGameOdds1.setOverUnder(caesarsObject.getOverUnder());
                                    mlbGameOdds1.setAwayTeamSpread(caesarsObject.getAwayTeamSpread());
                                    mlbGameOdds1.setHomeTeamSpread(caesarsObject.getHomeTeamSpread());
                                    mlbGameOdds1.setAwayTeamSpreadOdds(caesarsObject.getAwayTeamSpreadOdds());
                                    mlbGameOdds1.setHomeTeamSpreadOdds(caesarsObject.getHomeTeamSpreadOdds());
                                    mlbGameOdds1.setHomeTeamName(mlbGame.getHomeTeamName());
                                    mlbGameOdds1.setAwayTeamName(mlbGame.getAwayTeamName());
                                    mlbGameOdds1.setHomeTeamMoneyLine(caesarsObject.getHomeTeamMoneyLine());
                                    mlbGameOdds1.setAwayTeamMoneyLine(caesarsObject.getAwayTeamMoneyLine());
                                    mlbGameOdds1.setOverUnder(caesarsObject.getOverUnder());
                                    mlbGameOdds1.setOddsOver(caesarsObject.getOddsOver());
                                    mlbGameOdds1.setOddsUnder(caesarsObject.getOddsUnder());
                                    gameOddsRepository.save(mlbGameOdds1);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            }



            localDate = localDate.minusDays(1);
        }
    }

    private void saveGameData(MLBGame mlbGame) {

        for (MLBPlayer player : mlbGame.getHomeMLBTeam().getFieldingPlayers()) {

            playerGamePerformanceRepository.saveAll(player.getPlayerGamePerformances());
            //playerGamePerformanceRepository.saveAll(player.getPlayerGamePerformances());
        }
        for (MLBPlayer player : mlbGame.getAwayMLBTeam().getFieldingPlayers()) {

            playerGamePerformanceRepository.saveAll(player.getPlayerGamePerformances());
        }
        for (MLBPitcher pitcher : mlbGame.getHomeMLBTeam().getPitchingPlayers()) {

            pitcherPerformanceRepository.saveAll(pitcher.getMLBPitcherGamePerformances());
        }
        for (MLBPitcher pitcher : mlbGame.getAwayMLBTeam().getPitchingPlayers()) {
            pitcherPerformanceRepository.saveAll(pitcher.getMLBPitcherGamePerformances());
        }
        //playerGamePerformanceRepository.saveAll(list);
        playerRepository.saveAll(mlbGame.getAwayMLBTeam().getFieldingPlayers());
        playerRepository.saveAll(mlbGame.getHomeMLBTeam().getFieldingPlayers());
        pitcherRepository.saveAll(mlbGame.getAwayMLBTeam().getPitchingPlayers());
        pitcherRepository.saveAll(mlbGame.getHomeMLBTeam().getPitchingPlayers());
        teamRepository.save(mlbGame.getAwayMLBTeam());
        teamRepository.save(mlbGame.getHomeMLBTeam());
        mlbGame.setAwayTeamName(mlbGame.getAwayMLBTeam().getTeamName());
        mlbGame.setHomeTeamName(mlbGame.getHomeMLBTeam().getTeamName());

        gameRepository.save(mlbGame);



    }

    public void linkPlayersToDatabase(MLBGame MLBGame){
        for(MLBPlayer MLBPlayer : MLBGame.getHomeMLBTeam().getFieldingPlayers()){
            bind(MLBPlayer);
        }
        for(MLBPitcher MLBPitcher : MLBGame.getHomeMLBTeam().getPitchingPlayers()){
            bind(MLBPitcher);
        }
        for(MLBPlayer MLBPlayer : MLBGame.getAwayMLBTeam().getFieldingPlayers()){
            bind(MLBPlayer);
        }
        for(MLBPitcher MLBPitcher : MLBGame.getAwayMLBTeam().getPitchingPlayers()){
            bind(MLBPitcher);
        }
    }

    private void bind(MLBPlayer MLBPlayer) {
        if(MLBPlayer.getId() == null){
            Optional<MLBPlayer> playerOptional = playerRepository.findByPlayerID(MLBPlayer.getPlayerID());
            playerOptional.ifPresent(value -> MLBPlayer.setId(value.getId()));
            if(playerOptional.isPresent()){
                MLBPlayer.setId(playerOptional.get().getId());
                Set<MLBPlayerGamePerformance> MLBPlayerGamePerformanceList = playerOptional.get().getPlayerGamePerformances();
                MLBPlayerGamePerformanceList.addAll(MLBPlayer.getPlayerGamePerformances());
                MLBPlayer.setPlayerGamePerformances(MLBPlayerGamePerformanceList);
            }
        }
    }
    private void bind(MLBPitcher MLBPitcher) {
        if(MLBPitcher.getId() == null){
            Optional<MLBPitcher> playerOptional = pitcherRepository.findByPlayerID(MLBPitcher.getPlayerID());
            playerOptional.ifPresent(value -> MLBPitcher.setId(value.getId()));
            if(playerOptional.isPresent()){
                MLBPitcher.setId(playerOptional.get().getId());
                Set<MLBPitcherPerformance> MLBPlayerGamePerformanceList = playerOptional.get().getMLBPitcherGamePerformances();
                MLBPlayerGamePerformanceList.addAll(MLBPitcher.getMLBPitcherGamePerformances());
                MLBPitcher.setMLBPitcherGamePerformances(MLBPlayerGamePerformanceList);
            }
        }
    }
    public String convertTriCode(String tricode){
        return switch (tricode) {
            case "LAA" -> "Angels";
            case "LA Angels" -> "Angels";
            case "CWS" -> "White Sox";
            case "CHW" -> "White Sox";
            case "HOU" -> "Astros";
            case "OAK" -> "Athletics";
            case "Oakland" -> "Athletics";
            case "MIN" -> "Twins";
            case "MIL" -> "Brewers";
            case "TB" -> "Rays";
            case "MIA" -> "Marlins";
            case "Miami" -> "Marlins";
            case "SF" -> "Giants";
            case "SEA" -> "Mariners";
            case "Seattle" -> "Mariners";
            case "LAD" -> "Dodgers";
            case "LA Dodgers" -> "Dodgers";
            case "AZ" -> "D-backs";
            case "ARI" -> "D-backs";
            case "SD" -> "Padres";
            case "BAL" -> "Orioles";
            case "BOS" -> "Red Sox";
            case "Boston" -> "Red Sox";
            case "PIT" -> "Pirates";
            case "CHC" -> "Cubs";
            case "Chi. Cubs" -> "Cubs";
            case "ATL" -> "Braves";
            case "PHI" -> "Phillies";
            case "STL" -> "Cardinals";
            case "CIN" -> "Reds";
            case "TOR" -> "Blue Jays";
            case "Toronto" -> "Blue Jays";
            case "NYY" -> "Yankees";
            case "CLE" -> "Guardians";
            case "Cleveland" -> "Guardians";
            case "DET" -> "Tigers";
            case "Detroit" -> "Tigers";
            case "TEX" -> "Rangers";
            case "KC" -> "Royals";
            case "COL" -> "Rockies";
            case "Colorado" -> "Rockies";
            case "NYM" -> "Mets";
            case "WSH" -> "Nationals";
            case "WAS" -> "Nationals";
            case "Washington" -> "Nationals";
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
