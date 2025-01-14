package BaseballQuant.Util;

import BaseballQuant.Model.*;
import BaseballQuant.Repository.GameRepository;
import BaseballQuant.Repository.PitcherRepository;
import BaseballQuant.Repository.PlayerRepository;
import SportsQuant.Model.Game;
import SportsQuant.Model.PlayerGamePerformance;
import SportsQuant.Model.ReturnedOdds;
import org.apache.commons.collections.ArrayStack;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

public class GameFinder {

    GameRepository gameRepository;
    ScrapingProxy scrapingProxy;
    List<MLBGame> gameList;
    PitcherRepository pitcherRepository;
    HashMap<Integer, MLBGame> gameMap;

    public GameFinder() {

//        webDriver = new ChromeDriver(chromeOptions);
    }
    public GameFinder(List<MLBGame> setGames){
        gameList = setGames;
        gameMap = new HashMap<>();
        for(MLBGame mlbGame : setGames){
            gameMap.put(mlbGame.getGameId(),mlbGame);
        }
    }

    public void setScrapingProxy(ScrapingProxy scrapingProxy) {
        this.scrapingProxy = scrapingProxy;
    }

    public PitcherRepository getPitcherRepository() {
        return pitcherRepository;
    }

    public void setPitcherRepository(PitcherRepository pitcherRepository) {
        this.pitcherRepository = pitcherRepository;
    }

    public JSONObject getOverUnderWilliamHill(LocalDate localDate){
        try{
            Connection.Response response = Jsoup.connect("https://www.williamhill.com/us/nj/bet/api/v2/sports/baseball/events/schedule")
                    .method(Connection.Method.GET)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 OPR/68.0.3618.206")
                    .header("Connection", "keep-alive")
                    .ignoreContentType(true)
                    .maxBodySize(0)
                    .execute();
            return new JSONObject(response.body());
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }


    public JSONObject getMarketsCloudBet(){
        try{
            Connection.Response response = Jsoup.connect("https://www.cloudbet.com/sports-api/v6/sports/competitions/baseball-usa-mlb/events?markets=baseball.moneyline&markets=baseball.totals&markets=baseball.run_line&locale=en")
                    .method(Connection.Method.GET)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 OPR/78.0.4093.153")
                    .header("Connection", "keep-alive")
                    .header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjkwMDk1YmM2ZGM2ZDY3NzkxZDdkYTFlZWIxYTU1OWEzZDViMmM0ODYiLCJ0eXAiOiJKV1QifQ.eyJ0ZW5hbnQiOiJjbG91ZGJldCIsInV1aWQiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20va3ViZXJzaG11YmVyLXByb2QtY2ItYXV0aCIsImF1ZCI6Imt1YmVyc2htdWJlci1wcm9kLWNiLWF1dGgiLCJhdXRoX3RpbWUiOjE2MzAwMzIwMDYsInVzZXJfaWQiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJzdWIiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJpYXQiOjE2MzAwNzI3ODksImV4cCI6MTYzMDA3NjM4OSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6e30sInNpZ25faW5fcHJvdmlkZXIiOiJjdXN0b20ifX0.D9tWV403lYk9EElG09UW3NZTENUmMkquTf_8y2-eTqqsS3bHcrs3tMHXiXVy2EtPY0zfukc__3pMIQHYrga6TtSdkQjIB3qTf-Biemk9T0vtIhyZ_ClEiFTi3OoQ8FWPBN66jc_5DsRdm0FAQ23TmEwUB7oqTDKRZN6yC_xcc3ksAFoUIRB_9KrDi-ofXAr0VqejB4xKLrtpypxvXgHVs_rrxpA3hWg6FEPhVxQhI3LFEmUOZsB8Dg6j-UAKXiS8rON4f_-MCKk0vJemkBgYFhcbjI1rf_hdmoBB0G8cpXTjJmHAaXzF8xtsl4sYyeOI1GJeiFxuHJK19hMwacfkuw")
                    .header("Content-Type", "application/json")
                    .header("Referer", "https://www.cloudbet.com/en/sports/baseball/usa-mlb")
                    .header("x-platform-v2", "desktop")
                    .header("x-channel", "WEB")
                    .header("x-brand", "cloudbet")
                    .header("x-player-timezone", "America/Chicago")
                    .ignoreContentType(true)
                    .maxBodySize(0)
                    .execute();
            return new JSONObject(response.body());
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }


    public JSONObject getOverUnderCaesars(LocalDate localDate){
        LocalDateTime localDateTime = localDate.atStartOfDay();
        localDateTime = localDateTime.minusDays(1);
        System.out.println(localDateTime);
        String dateString = "";
        int day = localDateTime.getDayOfMonth();
        int month = localDateTime.getMonthValue();
        dateString = dateString + localDateTime.getYear() + "-";
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
        String tomorrowString = "";
        localDateTime = localDateTime.plusDays(1).plusHours(1);
        System.out.println(localDateTime);
        int tomorrowday = localDateTime.getDayOfMonth();
        int tomorrowmonth = localDateTime.getMonthValue();
        tomorrowString = tomorrowString + localDateTime.getYear() + "-";
        String tomorrowmonthString = "";
        if (tomorrowmonth < 10) {
            tomorrowmonthString = "0" + tomorrowmonth;
        } else {
            tomorrowmonthString = String.valueOf(tomorrowmonth);
        }
        tomorrowString = tomorrowString + tomorrowmonthString + "-";
        String tomorrowdayString = "";
        if (tomorrowday < 10) {
            tomorrowdayString = "0" + tomorrowday;
        } else {
            tomorrowdayString = String.valueOf(tomorrowday);
        }
        tomorrowString = tomorrowString + tomorrowdayString;
        System.out.println("today string: " + dateString);
        System.out.println("tomorrow string: " + tomorrowString);
        try{
            Connection.Response response = Jsoup.connect("https://sb-content.caesarscasino.com/content-service/api/v1/q/event-list?" +
                    "startTimeFrom=" + dateString + "T04%3A00%3A00Z" +
                    "&startTimeTo=" + tomorrowString + "T03%3A59%3A59Z" +
                    "&started=false&active=true" +
                    "&maxMarkets=10" +
                    "&orderMarketsBy=displayOrder" +
                    "&marketSortsIncluded=HH%2CHL%2CMR%2CWH" +
                    "&eventSortsIncluded=MTCH" +
                    "&includeChildMarkets=true" +
                    "&prioritisePrimaryMarkets=true" +
                    "&includeMedia=true" +
                    "&drilldownTagIds=62")
                    .method(Connection.Method.GET)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 OPR/68.0.3618.206")
                    .header("Connection", "keep-alive")
                    .ignoreContentType(true)
                    .maxBodySize(0)
                    .execute();
            return new JSONObject(response.body());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

//    public ReturnedOdds getOverUnder(LocalDate localDate, String teamName, int teamScore, boolean future) {
//        String gameID = "";
//        String dateString = "";
//        int day = localDate.getDayOfMonth();
//        int month = localDate.getMonthValue();
//        dateString = dateString + localDate.getYear();
//        String monthString = "";
//        if (month < 10) {
//            monthString = "0" + month;
//        } else {
//            monthString = String.valueOf(month);
//        }
//        dateString = dateString + monthString;
//        String dayString = "";
//        if (day < 10) {
//            dayString = "0" + day;
//        } else {
//            dayString = String.valueOf(day);
//        }
//        dateString = dateString + dayString;
//        WebDriverWait wait = new WebDriverWait(webDriver, 10);
//        while (true) {
//            try {
//                System.out.println(dateString);
//                webDriver.get("https://www.espn.com/mlb/scoreboard/_/date/" + dateString);
//                System.out.println("attempting URL : " + "https://www.espn.com/mlb/scoreboard/_/date/" + dateString);
//                WebElement events = wait.until(presenceOfElementLocated(By.id("events")));
//                String searchString;
//                if(teamName.toLowerCase().equals("d-backs")){
//                    searchString = "diamondbacks";
//                }else{
//                    searchString = teamName.toLowerCase();
//                }
//
//                System.out.println("looking for: " + teamName.toLowerCase());
//                List<WebElement> gameElements = events.findElements(By.tagName("article"));
//                for (WebElement gameElement : gameElements) {
//                    //System.out.println(gameElement.getText().toLowerCase());
//                    if (gameElement.getText().toLowerCase().contains(searchString)) {
//                        List<WebElement> scoreElements = gameElement.findElements(By.className("total"));
//                            for (WebElement webElement : scoreElements) {
//                                if (webElement.getText().toLowerCase().contains(String.valueOf(teamScore))) {
//                                    System.out.println("ESPN Game ID found: " + gameElement.getAttribute("id"));
//                                    gameID = gameElement.getAttribute("id");
//                                    break;
//                                }
//                            }
//                        }
//                }
//                webDriver.get("https://www.espn.com/mlb/game/_/gameId/" + gameID);
//                System.out.println("attempting URL : " + "https://www.espn.com/mlb/game/_/gameId/" + gameID);
//                List<WebElement> oddsSelector = new ArrayList<>();
//                try {
//                    oddsSelector = wait.until(presenceOfAllElementsLocatedBy(By.className("odds-lines-plus-logo")));
//                }catch (TimeoutException timeoutException){
//                    System.out.println("UNABLE TO FIND OVERUNDER RETURNING 0.0");
//                    return new ReturnedOdds();
//                }
//                String underOverString;
//                String cleanedString;
//                String lineString;
//                String cleanedLineString;
//                ReturnedOdds returnedOdds = new ReturnedOdds();
//                //List<WebElement> oddsSelector = webDriver.findElements(By.className("odds-lines-plus-logo"));
//                if(oddsSelector.get(0).findElements(By.tagName("li")).size() == 2){
//                    lineString= oddsSelector.get(0).findElements(By.tagName("li")).get(0).getText();
//                    underOverString= oddsSelector.get(0).findElements(By.tagName("li")).get(1).getText();
//                    if(underOverString.contains("Over/Under")) {
//                        cleanedString = underOverString.substring(underOverString.indexOf(" ") + 1);
//                        returnedOdds.setOverUnder(Double.parseDouble(cleanedString));
//                    }else{
//                        System.out.println("UNABLE TO FIND OVERUNDER");
//                    }
//                    if(lineString.contains("Line")){
//                        cleanedLineString = lineString.substring(lineString.indexOf(":") + 2);
//                        returnedOdds.setSpreadLine(cleanedLineString);
//                    }else{
//                        System.out.println("UNABLE TO FIND LINE");
//                    }
//
//                }else{
//                    underOverString= oddsSelector.get(0).findElements(By.tagName("li")).get(0).getText();
//                    if(underOverString.contains("Over/Under")) {
//                        cleanedString = underOverString.substring(underOverString.indexOf(" ") + 1);
//                        returnedOdds.setOverUnder(Double.parseDouble(cleanedString));
//                    }else{
//                        System.out.println("UNABLE TO FIND OVERUNDER");
//                    }
//                }
//                return  returnedOdds;
//                //webDriver.quit();
//
//
//
//            } catch (Exception e) {
//
//                e.printStackTrace();
//                //webDriver.quit();
//            }
//        }
//    }

    public GameRepository getGameRepository() {
        return gameRepository;
    }

    public void setGameRepository(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    //public void quitWebDriver() {
//        webDriver.quit();
//    }

    public Date findDateOfGame(String id) {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        while (true) {
            try {
                Connection.Response response = Jsoup.connect("https://cdn.nba.com/static/json/staticData/scheduleLeagueV2_1.json")
                        .method(Connection.Method.GET)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 OPR/68.0.3618.206")
                        .header("Host", "stats.nba.com")
                        .header("Connection", "keep-alive")
                        .header("Referer", "https://www.nba.com")
                        .header("Origin", "https://www.nba.com")
                        .ignoreContentType(true)
                        .maxBodySize(0)
                        .execute();
                //System.out.println(response.body());
                JSONArray gameArray = new JSONObject(response.body()).getJSONObject("leagueSchedule").getJSONArray("gameDates");
                //System.out.println(gameArray);
                for (Object object : gameArray) {
                    JSONObject dayObject = (JSONObject) object;
                    for (Object object2 : dayObject.getJSONArray("games")) {
                        JSONObject gameObject = (JSONObject) object2;
                        if (gameObject.getString("gameId").equals(id)) {
                            return df.parse(dayObject.getString("gameDate"));
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<MLBGame> findGamesOnDate(LocalDate localDate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        List<MLBGame> games = new ArrayList<>();
        String dateString = "";
        int day = localDate.getDayOfMonth();
        int month = localDate.getMonthValue();
        String monthString = "";
        if (day < 10) {
            monthString = "" + month;
        } else {
            monthString = String.valueOf(month);
        }
        dateString = dateString + monthString + "/";
        String dayString = "";
        if (day < 10) {
            dayString = "" + day;
        } else {
            dayString = String.valueOf(day);
        }
        dateString = dateString + dayString + "/" + localDate.getYear();
        System.out.println( dateString);
        JSONObject trackingObject = null;
        try {

            Connection.Response response = Jsoup.connect("https://statsapi.mlb.com/api/v1/schedule?language=en&sportId=1&date=" + dateString +
                    "&sortBy=gameDate&hydrate=game(content(summary,media(epg))),linescore(runners),flags,team,review")
                    .method(Connection.Method.GET)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 OPR/68.0.3618.206")
                    .header("Host", "stats.nba.com")
                    .header("Connection", "keep-alive")
                    .header("Referer", "https://www.nba.com")
                    .header("Origin", "https://www.nba.com")
                    .ignoreContentType(true)
                    .maxBodySize(0)
                    .execute();
            JSONObject jsonObject = new JSONObject(response.body());
            //System.out.println(jsonObject);
            JSONArray dateArray = jsonObject.getJSONArray("dates");
            for (Object object : dateArray) {
                JSONObject gameDateObject = (JSONObject) object;
                JSONArray gameArray = gameDateObject.getJSONArray("games");
                for(Object object2 : gameArray){
                    JSONObject gameObject = (JSONObject) object2;
                    //System.out.println(gameObject);
                //    if(gameObject.getJSONObject("teams").getJSONObject("away").has("score")) {
                        if(!isGameInBlacklist(gameObject.getInt("gamePk"))){
                            if(!gameObject.getString("seriesDescription").equals("Spring Training") &&
                                    !gameObject.getString("seriesDescription").equals("Exhibition") &&
                                    !gameObject.getString("seriesDescription").equals("All-Star Game")) {
   //                             if(gameObject.getJSONObject("status").getString("detailedState").equals("Final")) {
                                    MLBGame mlbGame = new MLBGame();
                                    mlbGame.setGameId(gameObject.getInt("gamePk"));
                                    mlbGame.setDate(df.parse(gameObject.getString("gameDate")));
                                    JSONObject linescore = gameObject.getJSONObject("linescore");
                                    //UNCOMMENT TO ENABLE SHORTENEDMAKEUPGAMES
                                    mlbGame.setShortenedMakeUpGame(linescore.getInt("scheduledInnings") == 7);
                                    JSONObject awayTeamObject = gameObject.getJSONObject("teams").getJSONObject("away");
                                    trackingObject = awayTeamObject;
                                    if(gameObject.getJSONObject("teams").getJSONObject("away").has("score")){
                                        mlbGame.setAwayPoints(awayTeamObject.getInt("score"));
                                    }
                                    mlbGame.setAwayTeamTricode(awayTeamObject.getJSONObject("team").getString("abbreviation"));
                                    mlbGame.setAwayTeamName(awayTeamObject.getJSONObject("team").getString("teamName"));
                                    mlbGame.setAwayTeamMlbId(awayTeamObject.getJSONObject("team").getInt("id"));
                                    JSONObject homeTeamObject = gameObject.getJSONObject("teams").getJSONObject("home");
                                    trackingObject = homeTeamObject;
                                    if(gameObject.getJSONObject("teams").getJSONObject("home").has("score")){
                                        mlbGame.setHomePoints(homeTeamObject.getInt("score"));
                                    }
                                    mlbGame.setHomeTeamTricode(homeTeamObject.getJSONObject("team").getString("abbreviation"));
                                    mlbGame.setHomeTeamName(homeTeamObject.getJSONObject("team").getString("teamName"));
                                    mlbGame.setHomeTeamMlbId(homeTeamObject.getJSONObject("team").getInt("id"));
                                    List<MLBPlayer> homePlayerList = getStartingLineUp(gameObject.getInt("gamePk"), true);

                                    games.add(mlbGame);
    //                            }
                            }else{
                                System.out.println("Spring training/Exhibition game. Will not import.");
                            }
                 //       }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("tracking: " + trackingObject);
            e.printStackTrace();
        }
        return games;
    }


    public List<MLBPlayer> getStartingLineUp(int GameId, boolean home){
        return null;
    }

    public List<MLBGame> findTodaysGames(LocalDate localDate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        List<MLBGame> games = new ArrayList<>();
        String dateString = "";
        int day = localDate.getDayOfMonth();
        int month = localDate.getMonthValue();
        String monthString = "";
        if (day < 10) {
            monthString = "" + month;
        } else {
            monthString = String.valueOf(month);
        }
        dateString = dateString + monthString + "/";
        String dayString = "";
        if (day < 10) {
            dayString = "" + day;
        } else {
            dayString = String.valueOf(day);
        }
        dateString = dateString + dayString + "/" + localDate.getYear();
        System.out.println( dateString);
        JSONObject trackingObject = null;
        try {

            Connection.Response response = Jsoup.connect("https://statsapi.mlb.com/api/v1/schedule?language=en&sportId=1&date=" + dateString +
                    "&sortBy=gameDate&hydrate=game(content(summary,media(epg))),linescore(runners),flags,team,review")
                    .method(Connection.Method.GET)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 OPR/68.0.3618.206")
                    .header("Host", "stats.nba.com")
                    .header("Connection", "keep-alive")
                    .header("Referer", "https://www.nba.com")
                    .header("Origin", "https://www.nba.com")
                    .ignoreContentType(true)
                    .maxBodySize(0)
                    .execute();
            JSONObject jsonObject = new JSONObject(response.body());
           //System.out.println(jsonObject);
            JSONArray dateArray = jsonObject.getJSONArray("dates");
            for (Object object : dateArray) {
                JSONObject gameDateObject = (JSONObject) object;
                JSONArray gameArray = gameDateObject.getJSONArray("games");
                for(Object object2 : gameArray){
                    JSONObject gameObject = (JSONObject) object2;
                    //System.out.println(gameObject);
                        if(!isGameInBlacklist(gameObject.getInt("gamePk"))){
                            if(!gameObject.getString("seriesDescription").equals("Spring Training")) {
                                MLBGame mlbGame = new MLBGame();
                                mlbGame.setGameId(gameObject.getInt("gamePk"));
                                JSONObject linescore = gameObject.getJSONObject("linescore");
                                mlbGame.setDate(df.parse(gameObject.getString("gameDate")));
                                mlbGame.setShortenedMakeUpGame(linescore.getInt("scheduledInnings") == 7);
                                JSONObject awayTeamObject = gameObject.getJSONObject("teams").getJSONObject("away");
                                trackingObject = awayTeamObject;
                                //mlbGame.setAwayPoints(awayTeamObject.getInt("score"));
                                mlbGame.setAwayTeamTricode(awayTeamObject.getJSONObject("team").getString("abbreviation"));
                                mlbGame.setAwayTeamName(awayTeamObject.getJSONObject("team").getString("teamName"));
                                mlbGame.setAwayTeamMlbId(awayTeamObject.getJSONObject("team").getInt("id"));
                                JSONObject homeTeamObject = gameObject.getJSONObject("teams").getJSONObject("home");
                                trackingObject = gameObject;
                                //mlbGame.setHomePoints(homeTeamObject.getInt("score"));
                                mlbGame.setHomeTeamTricode(homeTeamObject.getJSONObject("team").getString("abbreviation"));
                                mlbGame.setHomeTeamName(homeTeamObject.getJSONObject("team").getString("teamName"));
                                mlbGame.setHomeTeamMlbId(homeTeamObject.getJSONObject("team").getInt("id"));
                                List<MLBPlayer> awayPlayers = findProbableMLBPlayers(mlbGame.getGameId(),false);
                                MLBTeam awayTeam = new MLBTeam();
                                awayTeam.setTeamAbbreviation(awayTeamObject.getJSONObject("team").getString("abbreviation"));
                                awayTeam.setTeamName(awayTeamObject.getJSONObject("team").getString("teamName"));
                                awayTeam.setMlbId(awayTeamObject.getJSONObject("team").getInt("id"));
                                awayTeam.setFieldingPlayers(new HashSet<>(awayPlayers));
                                int awayPitcherId = findProbableMLBPitcherId(mlbGame.getGameId(), false);
                                if(awayPitcherId != 0) {
                                    Optional<MLBPitcher> optionalAwayMLBPitcher = pitcherRepository.findByPlayerID(awayPitcherId);
                                    if (optionalAwayMLBPitcher.isPresent()) {
                                        mlbGame.setAwayStartingPitcher(optionalAwayMLBPitcher.get());
                                        System.out.println("Pitcher " + optionalAwayMLBPitcher.get().getFullName() + " has been set.");
                                    } else {
                                        System.out.println("Pitcher does not exist with ID: " + awayPitcherId);
                                    }
                                }else{
                                    System.out.println("Pitcher for " + mlbGame.getAwayTeamName() + " is not yet known.");
                                }
                                List<MLBPlayer> homePlayers = findProbableMLBPlayers(mlbGame.getGameId(),true);
                                MLBTeam homeTeam = new MLBTeam();
                                homeTeam.setTeamAbbreviation(homeTeamObject.getJSONObject("team").getString("abbreviation"));
                                homeTeam.setTeamName(homeTeamObject.getJSONObject("team").getString("teamName"));
                                homeTeam.setMlbId(homeTeamObject.getJSONObject("team").getInt("id"));
                                homeTeam.setFieldingPlayers(new HashSet<>(homePlayers));


                                int homePitcherId = findProbableMLBPitcherId(mlbGame.getGameId(), true);
                                if(homePitcherId != 0) {
                                    Optional<MLBPitcher> optionalHomeMLBPitcher = pitcherRepository.findByPlayerID(homePitcherId);
                                    if (optionalHomeMLBPitcher.isPresent()) {
                                        mlbGame.setHomeStartingPitcher(optionalHomeMLBPitcher.get());
                                        System.out.println("Pitcher " + optionalHomeMLBPitcher.get().getFullName() + " has been set.");
                                    } else {
                                        System.out.println("Pitcher does not exist with ID: " + homePitcherId);
                                    }
                                }else{
                                    System.out.println("Pitcher for " + mlbGame.getHomeTeamName() + " is not yet known.");
                                }
//                                mlbGame.setHomeMLBTeam(homeTeam);
//                                mlbGame.setAwayMLBTeam(awayTeam);
                                games.add(mlbGame);
                            }else{
                                System.out.println("Spring training game. Will not import.");
                            }
                        }
                }
            }
        } catch (Exception e) {
            System.out.println("tracking: " + trackingObject);
            e.printStackTrace();
        }
        //System.out.println(games);
        return games;
    }

    public List<MLBPlayer> findProbableMLBPlayers(int gameId, boolean home){
        List<MLBPlayer> playerList = new ArrayList<>();
        try {

            Connection.Response response = Jsoup.connect("https://statsapi.mlb.com/api/v1/schedule?gamePk="+ gameId + "&language=en&hydrate=story,xrefId,lineups,broadcasts(all),probablePitcher(note),game(tickets)&useLatestGames=true&fields=dates,games,teams,probablePitcher,note,id,dates,games,broadcasts,type,name,homeAway,isNational,dates,games,game,tickets,ticketType,ticketLinks,dates,games,lineups,homePlayers,awayPlayers,useName,lastName,primaryPosition,abbreviation,dates,games,xrefIds,xrefId,xrefType,story")
                    .method(Connection.Method.GET)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 OPR/68.0.3618.206")
                    .header("Host", "stats.nba.com")
                    .header("Connection", "keep-alive")
                    .header("Referer", "https://www.nba.com")
                    .header("Origin", "https://www.nba.com")
                    .ignoreContentType(true)
                    .maxBodySize(0)
                    .execute();
            JSONObject jsonObject = new JSONObject(response.body());
            //System.out.println(jsonObject);
            JSONArray dateArray = jsonObject.getJSONArray("dates");
            for (Object object : dateArray) {
                JSONObject gameDateObject = (JSONObject) object;
                JSONArray gameArray = gameDateObject.getJSONArray("games");
                for (Object object2 : gameArray) {
                    JSONObject gameObject = (JSONObject) object2;
                    if(home){
                        if(gameObject.getJSONObject("lineups").has("homePlayers")){
                            JSONArray homeObject = gameObject.getJSONObject("lineups").getJSONArray("homePlayers");
                            for(Object playerObject : homeObject){
                                JSONObject player = (JSONObject) playerObject;
                                MLBPlayer mlbPlayer = new MLBPlayer();
                                mlbPlayer.setPlayerID(player.getInt("id"));
                                mlbPlayer.setFullName(player.getString("useName") + " " + player.getString("lastName"));
                                playerList.add(mlbPlayer);
                            }
                        }
                    }else{
                        if(gameObject.getJSONObject("lineups").has("awayPlayers")){
                            JSONArray homeObject = gameObject.getJSONObject("lineups").getJSONArray("awayPlayers");
                            for(Object playerObject : homeObject){
                                JSONObject player = (JSONObject) playerObject;
                                MLBPlayer mlbPlayer = new MLBPlayer();
                                mlbPlayer.setPlayerID(player.getInt("id"));
                                mlbPlayer.setFullName(player.getString("useName") + " " + player.getString("lastName"));
                                playerList.add(mlbPlayer);
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return playerList;
    }

    public int findProbableMLBPitcherId(int gameId, boolean home){
        try {

            Connection.Response response = Jsoup.connect("https://statsapi.mlb.com/api/v1/schedule?gamePk="+ gameId + "&language=en&hydrate=story,xrefId,lineups,broadcasts(all),probablePitcher(note),game(tickets)&useLatestGames=true&fields=dates,games,teams,probablePitcher,note,id,dates,games,broadcasts,type,name,homeAway,isNational,dates,games,game,tickets,ticketType,ticketLinks,dates,games,lineups,homePlayers,awayPlayers,useName,lastName,primaryPosition,abbreviation,dates,games,xrefIds,xrefId,xrefType,story")
                    .method(Connection.Method.GET)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 OPR/68.0.3618.206")
                    .header("Host", "stats.nba.com")
                    .header("Connection", "keep-alive")
                    .header("Referer", "https://www.nba.com")
                    .header("Origin", "https://www.nba.com")
                    .ignoreContentType(true)
                    .maxBodySize(0)
                    .execute();
            JSONObject jsonObject = new JSONObject(response.body());
            //System.out.println(jsonObject);
            JSONArray dateArray = jsonObject.getJSONArray("dates");
            for (Object object : dateArray) {
                JSONObject gameDateObject = (JSONObject) object;
                JSONArray gameArray = gameDateObject.getJSONArray("games");
                for (Object object2 : gameArray) {
                    JSONObject gameObject = (JSONObject) object2;
                    if(home){
                        JSONObject homeObject = gameObject.getJSONObject("teams").getJSONObject("home");
                        if(homeObject.has("probablePitcher")){
                            return homeObject.getJSONObject("probablePitcher").getInt("id");
                        }else{
                            return 0;
                        }
                    }else{
                        JSONObject awayObject = gameObject.getJSONObject("teams").getJSONObject("away");
                        if(awayObject.has("probablePitcher")){
                            return awayObject.getJSONObject("probablePitcher").getInt("id");
                        }else{
                            return 0;
                        }
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public List<MLBGame> findGamesOlderThan2021(LocalDate localDate){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        List<MLBGame> games = new ArrayList<>();
        String dateString = "";
        int day = localDate.getDayOfMonth();
        int month = localDate.getMonthValue();
        String monthString = "";
        if (day < 10) {
            monthString = "0" + month;
        } else {
            monthString = String.valueOf(month);
        }

        String dayString = "";

        if (day < 10) {
            dayString = "0" + day;
        } else {
            dayString = String.valueOf(day);
        }
        dateString = localDate.getYear() + "-" + monthString + "-" + dayString;
        try{
            Connection.Response response = Jsoup.connect("https://stats.nba.com/stats/scoreboardv3?GameDate="+ dateString + "&LeagueID=00")
                    .method(Connection.Method.GET)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 OPR/68.0.3618.206")
                    .header("Host", "stats.nba.com")
                    .header("Connection", "keep-alive")
                    .header("Referer", "https://www.nba.com")
                    .header("Origin", "https://www.nba.com")
                    .ignoreContentType(true)
                    .execute();
            JSONObject jsonObject = new JSONObject(response.body()).getJSONObject("scoreboard");
            //System.out.println(jsonObject);
            JSONArray gameArray = jsonObject.getJSONArray("games");
            //System.out.println(dateString + " " + jsonObject);
            for(Object gameObject : gameArray){
                JSONObject JSONgameDateObject = (JSONObject) gameObject;
                MLBGame newGame = new MLBGame();
                newGame.setAwayPoints(JSONgameDateObject.getJSONObject("awayTeam").getInt("score"));
                newGame.setHomePoints(JSONgameDateObject.getJSONObject("homeTeam").getInt("score"));
                newGame.setAwayTeamName(JSONgameDateObject.getJSONObject("awayTeam").getString("teamName"));
                newGame.setHomeTeamName(JSONgameDateObject.getJSONObject("homeTeam").getString("teamName"));
                newGame.setGameId(Integer.parseInt(JSONgameDateObject.getString("gameId")));
                newGame.setDate(df.parse(JSONgameDateObject.getString("gameEt")));
                //System.out.println(newGame);
//                if (!isGameInBlacklist(JSONgameDateObject.getString("gameId"))) {
//                    games.add(newGame);
//                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return games;
    }

    public List<MLBGame> findGamesOnDateFromDB(LocalDate localDate) {
        LocalDateTime localDateTimeBefore = localDate.atStartOfDay().plusHours(3);
        LocalDateTime localDateTimeAfter = localDateTimeBefore.plusDays(1);

        //System.out.println("dateBefore: " + Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()) + " dateAfter: " + Date.from(localDateAfter.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        List<MLBGame> games = new ArrayList<>();
        for(int i =0; i<gameList.size(); i++){
            if(gameList.get(i).getDate().before(Date.from(localDateTimeAfter.atZone(ZoneId.systemDefault()).toInstant())) && gameList.get(i).getDate().after(Date.from(localDateTimeBefore.atZone(ZoneId.systemDefault()).toInstant()))){
                games.add(gameList.get(i));
            }
        }

        Collections.sort(games,(o1, o2) -> o1.getDate().compareTo(o2.getDate()) * -1);
        return games;
    }

    public List<String> findTodaysGames() {
        List<String> gameList = new ArrayList<>();
        try {
            Connection.Response response = Jsoup.connect("https://cdn.nba.com/static/json/liveData/scoreboard/todaysScoreboard_00.json")
                    .method(Connection.Method.GET)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 OPR/68.0.3618.206")
                    .header("Host", "stats.nba.com")
                    .header("Connection", "keep-alive")
                    .header("Referer", "https://www.nba.com")
                    .header("Origin", "https://www.nba.com")
                    .execute();
            JSONArray jsonArray = new JSONObject(response.body()).getJSONObject("scoreboard").getJSONArray("games");
            for (Object object : jsonArray) {
                JSONObject jsonObject = (JSONObject) object;
                gameList.add(jsonObject.getString("gameId"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameList;
    }

    public void getPlayersRosterAndStatsForGame(MLBGame mlbGame) {
        int id = mlbGame.getGameId();
        System.out.println("fetching Game "+ id);
        JSONObject trackingObject = null;
        while (true) {
            String url = null;
            try {
                //System.out.println("ScrapingProxy: "+ scrapingProxy);
                //Thread.sleep((long) Math.random() * 500);

                Connection connection = Jsoup.connect("https://statsapi.mlb.com/api/v1.1/game/" + id +"/feed/live?language=en")
                        .method(Connection.Method.GET)
                        .ignoreContentType(true);
                if (scrapingProxy != null) {
                    connection.proxy(scrapingProxy.getIP(), scrapingProxy.getPort());
                }
                url = "https://statsapi.mlb.com/api/v1.1/game/" + id +"/feed/live?language=en";
                Connection.Response response = connection.execute();
                JSONObject baseObject = new JSONObject(response.body());
                JSONObject jsonObject = baseObject.getJSONObject("liveData").getJSONObject("boxscore").getJSONObject("teams");
                //AWAY TEAM
                JSONObject jsonObjectAway = jsonObject.getJSONObject("away");
                JSONObject jsonObjectHome = jsonObject.getJSONObject("home");
                //List<Player> awayPlayers = getPlayerListFromPreviousGame(jsonObject.getInt("awayTeamId"), game.getDate());
                JSONObject awayPlayerArray = jsonObjectAway.getJSONObject("players");
                JSONObject homePlayerArray = jsonObjectHome.getJSONObject("players");
                int awayPitcherId = 0;
                if(baseObject.getJSONObject("gameData").getJSONObject("probablePitchers").has("away")) {
                    awayPitcherId = baseObject.getJSONObject("gameData").getJSONObject("probablePitchers").getJSONObject("away").getInt("id");
                }
                int homePitcherId = 0;
                if(baseObject.getJSONObject("gameData").getJSONObject("probablePitchers").has("home")) {
                    homePitcherId = baseObject.getJSONObject("gameData").getJSONObject("probablePitchers").getJSONObject("home").getInt("id");
                }
                //System.out.println("away: " + awayPlayerArray);
                //System.out.println("home: " + homePlayerArray);
                MLBTeam awayTeam = new MLBTeam();
                awayTeam.setMlbId(mlbGame.getAwayTeamMlbId());
                awayTeam.setTeamName(mlbGame.getAwayTeamName());
                Set<MLBPlayer> awayPlayerSet = new HashSet<>();
                Set<MLBPitcher> awayPitcherSet = new HashSet<>();
                //System.out.println(jsonObject);
                for(String key : awayPlayerArray.keySet()){
                    JSONObject playerObject = awayPlayerArray.getJSONObject(key);
                   // System.out.println(playerObject);
                    if(playerObject.getJSONObject("stats").getJSONObject("batting").has("atBats")) {
                        MLBPlayer mlbPlayer = new MLBPlayer();
                        mlbPlayer.setPlayerID(playerObject.getJSONObject("person").getInt("id"));
                        mlbPlayer.setFullName(playerObject.getJSONObject("person").getString("fullName"));
                        MLBPlayerGamePerformance mlbPlayerGamePerformance = new MLBPlayerGamePerformance();
                        mlbPlayerGamePerformance.setAtbats(playerObject.getJSONObject("stats").getJSONObject("batting").getInt("atBats"));
                        mlbPlayerGamePerformance.setRbis(playerObject.getJSONObject("stats").getJSONObject("batting").getInt("rbi"));
                        mlbPlayerGamePerformance.setRuns(playerObject.getJSONObject("stats").getJSONObject("batting").getInt("runs"));
                        mlbPlayerGamePerformance.setWalks(playerObject.getJSONObject("stats").getJSONObject("batting").getInt("baseOnBalls"));
                        mlbPlayerGamePerformance.setStolenBases(playerObject.getJSONObject("stats").getJSONObject("batting").getInt("stolenBases"));
                        int chances = playerObject.getJSONObject("stats").getJSONObject("fielding").getInt("chances");
                        int errors = playerObject.getJSONObject("stats").getJSONObject("fielding").getInt("errors");
                        //System.out.println(playerObject.getJSONObject("stats").getJSONObject("fielding"));
                        if(chances > 0){
                            mlbPlayerGamePerformance.setFieldingpercentage(((double)chances -(double)errors)/(double) chances);
                            //System.out.println(mlbPlayer.getFullName() + " " + errors + " on " + chances + " chances.");
                        }
                        mlbPlayerGamePerformance.setGameId(mlbGame.getGameId());
                        mlbPlayerGamePerformance.setDate(mlbGame.getDate());
                        Set<MLBPlayerGamePerformance> playerGamePerformanceList = new HashSet<>();
                        playerGamePerformanceList.add(mlbPlayerGamePerformance);
                        mlbPlayer.setPlayerGamePerformances(playerGamePerformanceList);
                        awayPlayerSet.add(mlbPlayer);
                    }
                    if(playerObject.getJSONObject("stats").getJSONObject("pitching").has("inningsPitched")){
                        MLBPitcher mlbPitcher = new MLBPitcher();
                        mlbPitcher.setPlayerID(playerObject.getJSONObject("person").getInt("id"));
                        mlbPitcher.setFullName(playerObject.getJSONObject("person").getString("fullName"));
                        MLBPitcherPerformance mlbPitcherPerformance = new MLBPitcherPerformance();
                        mlbPitcherPerformance.setRunsGivenUp(playerObject.getJSONObject("stats").getJSONObject("pitching").getInt("runs"));
                        mlbPitcherPerformance.setHitsGiven(playerObject.getJSONObject("stats").getJSONObject("pitching").getInt("hits"));
                        mlbPitcherPerformance.setWalksGiven(playerObject.getJSONObject("stats").getJSONObject("pitching").getInt("baseOnBalls"));

                        mlbPitcherPerformance.setInningsPitchedFromString(playerObject.getJSONObject("stats").getJSONObject("pitching").getString("inningsPitched"));
                        mlbPitcherPerformance.setDate(mlbGame.getDate());
                        mlbPitcherPerformance.setGameId(mlbGame.getGameId());
                        Set<MLBPitcherPerformance> pitcherGamePerformanceList = new HashSet<>();
                        pitcherGamePerformanceList.add(mlbPitcherPerformance);
                        mlbPitcher.setMLBPitcherGamePerformances(pitcherGamePerformanceList);
                        awayPitcherSet.add(mlbPitcher);
                    }
                    //System.out.println("player: " + playerObject);
                }
                for(MLBPitcher mlbPitcher : awayPitcherSet){
                    if(mlbPitcher.getPlayerID() == awayPitcherId && awayPitcherId != 0){
                        mlbGame.setAwayStartingPitcher(mlbPitcher);
                    }
                }
                awayTeam.setFieldingPlayers(awayPlayerSet);
                awayTeam.setPitchingPlayers(awayPitcherSet);


                MLBTeam homeTeam = new MLBTeam();
                homeTeam.setMlbId(mlbGame.getHomeTeamMlbId());
                homeTeam.setTeamName(mlbGame.getHomeTeamName());
                Set<MLBPlayer> homePlayerSet = new HashSet<>();
                Set<MLBPitcher> homePitcherSet = new HashSet<>();
                for(String key : homePlayerArray.keySet()){
                    JSONObject playerObject = homePlayerArray.getJSONObject(key);
                    trackingObject = playerObject.getJSONObject("stats").getJSONObject("batting");
                    if(playerObject.getJSONObject("stats").getJSONObject("batting").has("atBats")) {
                        MLBPlayer mlbPlayer = new MLBPlayer();
                        mlbPlayer.setPlayerID(playerObject.getJSONObject("person").getInt("id"));
                        mlbPlayer.setFullName(playerObject.getJSONObject("person").getString("fullName"));
                        MLBPlayerGamePerformance mlbPlayerGamePerformance = new MLBPlayerGamePerformance();
                        mlbPlayerGamePerformance.setAtbats(playerObject.getJSONObject("stats").getJSONObject("batting").getInt("atBats"));
                        mlbPlayerGamePerformance.setRbis(playerObject.getJSONObject("stats").getJSONObject("batting").getInt("rbi"));
                        mlbPlayerGamePerformance.setRuns(playerObject.getJSONObject("stats").getJSONObject("batting").getInt("runs"));
                        mlbPlayerGamePerformance.setWalks(playerObject.getJSONObject("stats").getJSONObject("batting").getInt("baseOnBalls"));
                        mlbPlayerGamePerformance.setStolenBases(playerObject.getJSONObject("stats").getJSONObject("batting").getInt("stolenBases"));
                        int chances = playerObject.getJSONObject("stats").getJSONObject("fielding").getInt("chances");
                        int errors = playerObject.getJSONObject("stats").getJSONObject("fielding").getInt("errors");
                        //System.out.println(playerObject.getJSONObject("stats").getJSONObject("fielding"));
                        if(chances > 0){
                            mlbPlayerGamePerformance.setFieldingpercentage(((double)chances -(double)errors)/(double) chances);
                            //System.out.println(mlbPlayer.getFullName() + " " + errors + " on " + chances + " chances.");
                        }
                        mlbPlayerGamePerformance.setGameId(mlbGame.getGameId());
                        mlbPlayerGamePerformance.setDate(mlbGame.getDate());
                        Set<MLBPlayerGamePerformance> playerGamePerformanceList = new HashSet<>();
                        playerGamePerformanceList.add(mlbPlayerGamePerformance);
                        mlbPlayer.setPlayerGamePerformances(playerGamePerformanceList);
                        homePlayerSet.add(mlbPlayer);
                    }
                    if(playerObject.getJSONObject("stats").getJSONObject("pitching").has("inningsPitched")){
                        MLBPitcher mlbPitcher = new MLBPitcher();
                        mlbPitcher.setPlayerID(playerObject.getJSONObject("person").getInt("id"));
                        mlbPitcher.setFullName(playerObject.getJSONObject("person").getString("fullName"));
                        MLBPitcherPerformance mlbPitcherPerformance = new MLBPitcherPerformance();
                        mlbPitcherPerformance.setRunsGivenUp(playerObject.getJSONObject("stats").getJSONObject("pitching").getInt("runs"));
                        mlbPitcherPerformance.setHitsGiven(playerObject.getJSONObject("stats").getJSONObject("pitching").getInt("hits"));
                        mlbPitcherPerformance.setWalksGiven(playerObject.getJSONObject("stats").getJSONObject("pitching").getInt("baseOnBalls"));
                        mlbPitcherPerformance.setInningsPitchedFromString(playerObject.getJSONObject("stats").getJSONObject("pitching").getString("inningsPitched"));
                        mlbPitcherPerformance.setDate(mlbGame.getDate());
                        mlbPitcherPerformance.setGameId(mlbGame.getGameId());
                        Set<MLBPitcherPerformance> pitcherGamePerformanceList = new HashSet<>();
                        pitcherGamePerformanceList.add(mlbPitcherPerformance);
                        mlbPitcher.setMLBPitcherGamePerformances(pitcherGamePerformanceList);
                        homePitcherSet.add(mlbPitcher);
                    }
                    //System.out.println("player: " + playerObject);
                }
                for(MLBPitcher mlbPitcher : homePitcherSet){
                    if(mlbPitcher.getPlayerID() == homePitcherId && homePitcherId != 0){
                        mlbGame.setHomeStartingPitcher(mlbPitcher);
                    }
                }
                homeTeam.setFieldingPlayers(homePlayerSet);
                homeTeam.setPitchingPlayers(homePitcherSet);
                mlbGame.setHomeMLBTeam(homeTeam);
                mlbGame.setAwayMLBTeam(awayTeam);
               // System.out.println(homeTeam.getTeamName() + " Home Sizes::Pitchers: " + homeTeam.getPitchingPlayers().size() + " Fielders: " + homeTeam.getFieldingPlayers().size());
               // System.out.println(awayTeam.getTeamName() + " Away Sizes::Pitchers: " + awayTeam.getPitchingPlayers().size() + " Fielders: " + awayTeam.getFieldingPlayers().size());
                break;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(url);
                if (scrapingProxy != null) {
                    System.out.println("Error with proxy: " + scrapingProxy.getIP() + ":" + scrapingProxy.getPort());
                } else {
                    System.out.println("error without proxy");
                    System.out.println(trackingObject);
                    
                }
                try {
                    Thread.sleep(4000);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }
    }

    public MLBGame getTeamsPreviousGameFromDB(MLBGame game, int teamID) {
        //LocalDate JanFirst = LocalDate.of(2021, 1,1);
        //System.out.println("Game ID For Lists: " + game.getDate());
        String teamTriCode;
        if(game.getHomeTeamMlbId() == teamID){
            teamTriCode = game.getHomeTeamTricode();
        }else{
            teamTriCode = game.getAwayTeamTricode();
        }
        //System.out.println("searching teamTriCode: " + teamTriCode);
        //List<MLBGame> GamesIn2021 = gameRepository.findAllByAwayTeamNameOrHomeTeamName(teamTriCode, teamTriCode);
        List<MLBGame> GamesIn2021 = new ArrayList<>();
        int size = gameList.size();
        for(int i = 0; i< size; i++){
            try {
                if (gameList.get(i).getAwayTeamTricode().equals(teamTriCode) || gameList.get(i).getHomeTeamTricode().equals(teamTriCode) ||
                        (teamTriCode.equals("AZ") && gameList.get(i).getAwayTeamTricode().equals("ARI") ||
                                (teamTriCode.equals("AZ") && gameList.get(i).getHomeTeamTricode().equals("ARI")))) {
                    GamesIn2021.add(gameList.get(i));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        GamesIn2021.sort(Comparator.comparing(MLBGame::getDate).reversed());
        //System.out.println("Found Games: " + GamesIn2021.size());
        MLBGame selectedGame = null;
        for(int i = 0; i < GamesIn2021.size(); i++){
        //for(MLBGame game3: GamesIn2021){
            if(GamesIn2021.get(i).getDate().before(game.getDate())){

                selectedGame = GamesIn2021.get(i);
                break;
            }

        }
        return selectedGame;
    }


    public Set<MLBPlayer> getPlayerListFromPreviousGameFromDB(MLBGame game, int teamID) {
        //LocalDate JanFirst = LocalDate.of(2021, 1,1);
        //System.out.println("Game ID For Lists: " + game.getDate());
        //List<MLBGame> GamesIn2021 = new ArrayList<>();
        Predicate<MLBGame> homeTeamMatch = g -> g.getHomeTeamMlbId() == (teamID);
        Predicate<MLBGame> awayTeamMatch = g -> g.getHomeTeamMlbId() == (teamID);
        List<MLBGame> GamesIn2021 = gameList.stream()
                .parallel()
                .filter(homeTeamMatch.or(awayTeamMatch))
                .sorted(Comparator.comparing(MLBGame::getDate))
                .collect(Collectors.toList());
//        for(int i = 0; i< gameList.size(); i++){
//            try {
//                if (gameList.get(i).getAwayTeamTricode().equals(teamTriCode) || gameList.get(i).getHomeTeamTricode().equals(teamTriCode)) {
//                    GamesIn2021.add(gameList.get(i));
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
        //System.out.println("Found Games: " + GamesIn2021.size());
        MLBGame selectedGame = null;
        for(int i = GamesIn2021.size()-1; i>0;i--){
        //for(MLBGame game3: GamesIn2021){
            if(GamesIn2021.get(i).getDate().before(game.getDate())){
                selectedGame = GamesIn2021.get(i);
                break;
            }
        }
        //System.out.println("selected Game: " + selectedGame.getHomeMLBTeam().getTeamName() + " vs " + selectedGame.getAwayMLBTeam().getTeamName() + " on " + selectedGame.getDate() + " ID: " + selectedGame.getGameId());
        Set<MLBPlayer> playerList = new HashSet<>();
        if(selectedGame != null) {
            if (selectedGame.getAwayTeamMlbId() == teamID) {
                playerList = selectedGame.getAwayMLBTeam().getFieldingPlayers();
            } else {
                playerList = selectedGame.getHomeMLBTeam().getFieldingPlayers();
            }
        }else{
            System.out.println("STOP!!!");
        }
        //System.out.println(playerList);
        return playerList;
    }

    public Set<MLBPitcher> getPitcherListFromPreviousGameFromDB(MLBGame game, int teamID) {

        String teamName;
        if(game.getHomeTeamMlbId() == teamID){
            teamName = game.getHomeTeamName();
        }else{
            teamName = game.getAwayTeamName();
        }
        List<MLBGame> GamesIn2021 = new ArrayList<>();

        for(MLBGame gameInList : gameList){
            try {
                if (gameInList.getAwayTeamName().equals(teamName) || gameInList.getHomeTeamName().equals(teamName)) {
                    GamesIn2021.add(gameInList);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        GamesIn2021.sort(Comparator.comparing(MLBGame::getDate).reversed());
        //System.out.println("Found Games: " + GamesIn2021.size());
        MLBGame selectedGame = null;
        for(MLBGame game3: GamesIn2021){
            if(game3.getDate().before(game.getDate())){
//                System.out.println(" pitcher game: " +game3.getHomeMLBTeam().getTeamName() + " vs " + game3.getAwayMLBTeam().getTeamName()
//                        + " on " + game3.getDate() + " :: sizes: " + game3.getHomeMLBTeam().getPitchingPlayers().size() + " : " + game3.getAwayMLBTeam().getPitchingPlayers().size());
                selectedGame = game3;
                break;
            }

        }
        //System.out.println("pitcher selected Game: " + selectedGame.getHomeMLBTeam().getTeamName() + " vs " + selectedGame.getAwayMLBTeam().getTeamName() + " on " + selectedGame.getDate() + " ID: " + selectedGame.getGameId());
        Set<MLBPitcher> playerList = new HashSet<>();
        if(selectedGame.getAwayTeamMlbId() == teamID){
            playerList = selectedGame.getAwayMLBTeam().getPitchingPlayers();
            for(MLBPitcher pitcher : selectedGame.getAwayMLBTeam().getPitchingPlayers()){

            }
        }else{
            playerList = selectedGame.getHomeMLBTeam().getPitchingPlayers();
            for(MLBPitcher pitcher : selectedGame.getHomeMLBTeam().getPitchingPlayers()){

            }
        }

        return playerList;
    }

    public Set<MLBPitcher> buildProbablePitcherList(MLBGame game, int teamID, int gameCount, int recentAppearanceLookback, int pitcherCap){
        MLBGame gameToRun = game;
        String teamName;
        if(game.getHomeTeamMlbId() == teamID){
            teamName = game.getHomeTeamName();
        }else{
            teamName = game.getAwayTeamName();
        }
        List<MLBGame> GamesIn2021 = new ArrayList<>();
        for(MLBGame gameInList : gameList){
            try {
                if (gameInList.getAwayTeamName().equals(teamName) || gameInList.getHomeTeamName().equals(teamName)) {
                    GamesIn2021.add(gameInList);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        GamesIn2021.sort(Comparator.comparing(MLBGame::getDate).reversed());
        //System.out.println("Found Games: " + GamesIn2021.size());
        Set<MLBPitcher> pitcherSet = new HashSet<>();
        Set<MLBPitcher> startingPitchers = new HashSet<>();
        List<MLBPitcher> allPitchers = new ArrayList<>();
        HashMap<MLBPitcher, Double> playerAppearanceMap = new HashMap<>();
        MLBGame mostRecent = GamesIn2021.get(0);
        List<Integer> recentPlayerIds = new ArrayList<>();
        for(MLBPitcher recentPitcher : mostRecent.getAwayMLBTeam().getPitchingPlayers()){
            recentPlayerIds.add(recentPitcher.getPlayerID());
        }
        for(MLBPitcher recentPitcher : mostRecent.getHomeMLBTeam().getPitchingPlayers()){
            recentPlayerIds.add(recentPitcher.getPlayerID());
        }
        int scanCount = 0;
        for(MLBGame game3: GamesIn2021){
            if(scanCount<gameCount) {
                if (game3.getDate().before(game.getDate())) {
                    if (game3.getAwayTeamMlbId() == teamID) {
                        pitcherSet.addAll(game3.getAwayMLBTeam().getPitchingPlayers());
                        if(game3.getAwayStartingPitcher() != null) {
                            startingPitchers.add(game3.getAwayStartingPitcher());
                        }
                    } else {
                        pitcherSet.addAll(game3.getHomeMLBTeam().getPitchingPlayers());
                        if(game3.getHomeStartingPitcher() != null) {
                            startingPitchers.add(game3.getHomeStartingPitcher());
                        }
                    }
                    scanCount = scanCount + 1;
                }
            }
        }
        scanCount = 0;
        for(MLBGame game3: GamesIn2021){
            if(scanCount<recentAppearanceLookback) {
                if (game3.getDate().before(game.getDate())) {
                    if (game3.getAwayTeamMlbId() == teamID) {
                        allPitchers.addAll(game3.getAwayMLBTeam().getPitchingPlayers());
                    } else {
                        pitcherSet.addAll(game3.getHomeMLBTeam().getPitchingPlayers());
                        allPitchers.addAll(game3.getHomeMLBTeam().getPitchingPlayers());
                    }
                    scanCount = scanCount + 1;
                }
            }
        }
        scanCount = 0;
        for(MLBGame game3: GamesIn2021){
            if(scanCount<recentAppearanceLookback) {
                if (game3.getDate().before(game.getDate())) {
                    if (game3.getAwayTeamMlbId() == teamID) {
                        for(MLBPitcher mlbPitcher : game3.getAwayMLBTeam().getPitchingPlayers()){
                            if(playerAppearanceMap.containsKey(mlbPitcher)){
                                Double d = playerAppearanceMap.get(mlbPitcher);
                                d = d + getInningsPitcherForGame(mlbPitcher,game3);
                                playerAppearanceMap.put(mlbPitcher,d);
                            }else{
                                playerAppearanceMap.put(mlbPitcher,getInningsPitcherForGame(mlbPitcher,game3));
                            }
                        }
                    } else {
                        for(MLBPitcher mlbPitcher : game3.getHomeMLBTeam().getPitchingPlayers()){
                            if(playerAppearanceMap.containsKey(mlbPitcher)){
                                Double d = playerAppearanceMap.get(mlbPitcher);
                                d = d + getInningsPitcherForGame(mlbPitcher,game3);
                                playerAppearanceMap.put(mlbPitcher,d);
                            }else{
                                playerAppearanceMap.put(mlbPitcher,getInningsPitcherForGame(mlbPitcher,game3));
                            }
                        }
                    }
                    scanCount = scanCount + 1;
                }
            }
        }

        //pitcherSet.removeIf(mlbPitcher -> recentPlayerIds.contains(mlbPitcher.getPlayerID()));

        for(MLBPitcher startingPitcher : startingPitchers){
            int pitcherId = startingPitcher.getPlayerID();
            pitcherSet.removeIf(pitcher -> pitcher.getPlayerID() == pitcherId);
        }

        playerAppearanceMap.entrySet().removeIf(entry -> recentPlayerIds.contains(entry.getKey().getPlayerID()));

        Stream<Map.Entry<MLBPitcher,Double>> sorted =
                playerAppearanceMap.entrySet().stream()
                        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(pitcherCap);

        List<Map.Entry<MLBPitcher, Double>> entries = sorted.collect(Collectors.toList());

        //entries.removeIf(mlbPitcher -> recentPlayerIds.contains(mlbPitcher.getKey().getPlayerID()));

        System.out.println(teamName + ":" + entries);
        HashSet<MLBPitcher> newPitcherSet = new HashSet<>();
        for(Map.Entry<MLBPitcher, Double> entry : entries){
            newPitcherSet.add(entry.getKey());
        }

        //THIS IS THE ORIGINAL IMPLEMENTATION:
//        List<MLBPitcher> pitcherList = new ArrayList<MLBPitcher>(pitcherSet);
//        while(pitcherList.size()>pitcherCap){
//            pitcherList.remove(pitcherList.size() - 1);
//        }

        //System.out.println("Returning " + teamName + " Probable Pitcher List Of Size " + newPitcherSet.size() + " :: " + newPitcherSet);
        return new HashSet<>(newPitcherSet);

    }


    public double getInningsPitcherForGame(MLBPitcher mlbPitcher, MLBGame game){
        int gameIdToFind = game.getGameId();
        Set<MLBPitcherPerformance> mlbPitcherPerformances = mlbPitcher.getMLBPitcherGamePerformances();
        for(MLBPitcherPerformance mlbPitcherPerformance :mlbPitcherPerformances){
            if(mlbPitcherPerformance.getGameId() == gameIdToFind){
                return mlbPitcherPerformance.getInningsPitched();
            }
        }
        return 0.0;
    }

    public void getPlayersForGameId(MLBGame mlbGame, boolean newGames){
        int id = mlbGame.getGameId();
        while(true) {
            try {
                //System.out.println("ScrapingProxy: "+ scrapingProxy);
                //Thread.sleep((long) Math.random() * 500);
                Connection connection = Jsoup.connect("https://statsapi.mlb.com/api/v1.1/game/" + id +"/feed/live?language=en")
                        .method(Connection.Method.GET)
                        .ignoreContentType(true);
                Connection.Response response = connection.execute();
                JSONObject baseObject = new JSONObject(response.body());
                JSONObject jsonObject = baseObject.getJSONObject("liveData").getJSONObject("boxscore").getJSONObject("teams");
                //AWAY TEAM
                JSONObject jsonObjectAway = jsonObject.getJSONObject("away");
                JSONObject jsonObjectHome = jsonObject.getJSONObject("home");
                //List<Player> awayPlayers = getPlayerListFromPreviousGame(jsonObject.getInt("awayTeamId"), game.getDate());
                JSONObject awayPlayerArray = jsonObjectAway.getJSONObject("players");
                JSONObject homePlayerArray = jsonObjectHome.getJSONObject("players");
                int awayPitcherId = 0;
                if(baseObject.getJSONObject("gameData").getJSONObject("probablePitchers").has("away")) {
                    awayPitcherId = baseObject.getJSONObject("gameData").getJSONObject("probablePitchers").getJSONObject("away").getInt("id");
                }
                int homePitcherId = 0;
                if(baseObject.getJSONObject("gameData").getJSONObject("probablePitchers").has("home")) {
                    homePitcherId = baseObject.getJSONObject("gameData").getJSONObject("probablePitchers").getJSONObject("home").getInt("id");
                }
                //System.out.println("away: " + awayPlayerArray);
                //System.out.println("home: " + homePlayerArray);
                MLBTeam awayTeam = new MLBTeam();
                awayTeam.setMlbId(mlbGame.getAwayTeamMlbId());
                awayTeam.setTeamName(mlbGame.getAwayTeamName());
                Set<MLBPlayer> awayPlayerSet = new HashSet<>();
                Set<MLBPitcher> awayPitcherSet = new HashSet<>();




                break;
            } catch (Exception e) {
                e.printStackTrace();
                if(scrapingProxy != null) {
                    System.out.println("Error with proxy: " + scrapingProxy.getIP() + ":" + scrapingProxy.getPort());
                }else{
                    System.out.println("error without proxy");
                }
                try{
                    Thread.sleep(10000);
                }catch (Exception ee){
                    ee.printStackTrace();
                }
            }
        }
    }


    private Set<MLBPlayer> getPlayerListFromPreviousGame(int teamID, Date date, boolean newGames){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date beforeDate = date;
        int gameID = 0;
        if(beforeDate == null){
            beforeDate = new Date();
        }
        while(true) {
            try {
                Thread.sleep((long) (Math.random()*1750));
                Connection connection = Jsoup.connect("https://stats.nba.com/stats/teamgamelogs?DateFrom=&" +
                        "DateTo=&" +
                        "GameSegment=&" +
                        "LastNGames=0&" + "LeagueID=00&" + "Location=&" + "MeasureType=Base&" +
                        "Month=0" + "&OpponentTeamID=0" + "&Outcome=&" +
                        "PORound=0" +
                        "&PaceAdjust=N&PerMode=Totals&Period=0&PlusMinus=N&Rank=N&Season=2020-21&SeasonSegment=&" +
                        "SeasonType=Playoffs&ShotClockRange=&TeamID=" + teamID + "&VsConference=&VsDivision=")
                        .method(Connection.Method.GET)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 OPR/68.0.3618.206")
                        .header("Host", "stats.nba.com")
                        .header("Connection", "keep-alive")
                        .header("Referer", "https://www.nba.com")
                        .header("Origin", "https://www.nba.com")
                        .ignoreContentType(true);
                if(scrapingProxy != null){
                    connection.proxy(scrapingProxy.getIP(), scrapingProxy.getPort());
                }
                Connection.Response response = connection.execute();
                JSONObject jsonObject = new JSONObject(response.body());
                JSONArray jsonArray = jsonObject.getJSONArray("resultSets").getJSONObject(0).getJSONArray("rowSet");
                Connection regSeasonRequest = Jsoup.connect("https://stats.nba.com/stats/teamgamelogs?DateFrom=&" +
                        "DateTo=&" +
                        "GameSegment=&" +
                        "LastNGames=0&" + "LeagueID=00&" + "Location=&" + "MeasureType=Base&" +
                        "Month=0" + "&OpponentTeamID=0" + "&Outcome=&" + "PORound=0" +
                        "&PaceAdjust=N&PerMode=Totals&Period=0&PlusMinus=N&Rank=N&Season=2020-21&SeasonSegment=&" +
                        "SeasonType=Regular+Season&ShotClockRange=&TeamID=" + teamID + "&VsConference=&VsDivision=")
                        .method(Connection.Method.GET)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 OPR/68.0.3618.206")
                        .header("Host", "stats.nba.com")
                        .header("Connection", "keep-alive")
                        .header("Referer", "https://www.nba.com")
                        .header("Origin", "https://www.nba.com")
                        .ignoreContentType(true);
                if(scrapingProxy != null){
                    regSeasonRequest.proxy(scrapingProxy.getIP(), scrapingProxy.getPort());
                }
                Connection.Response regSeasonResponse = regSeasonRequest.execute();

                JSONObject RegSeasonJsonObject = new JSONObject(regSeasonResponse.body());
                JSONArray regSeasonArray = RegSeasonJsonObject.getJSONArray("resultSets").getJSONObject(0).getJSONArray("rowSet");
                //System.out.println(regSeasonArray);
                for (Object object : regSeasonArray) {
                    jsonArray.put(object);
                }
                LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                localDate = localDate.minusDays(1);
                beforeDate = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                Date gameDate = null;
                for (Object object : jsonArray) {
                    JSONArray game = (JSONArray) object;
                    gameDate = df.parse(game.getString(5));
                    if (gameDate.before(beforeDate)) {
                        gameID = Integer.parseInt(game.getString(4));
                        //System.out.println("selecting game ID " + gameID);
                        break;
                    }
                }
                //System.out.println(jsonArray);
                //Thread.sleep(0);
                String cleanedString = "00" + gameID;
                Connection connection1 = Jsoup.connect("https://stats.nba.com/stats/boxscoreadvancedv3?GameID=" + cleanedString + "&LeagueID=00&endPeriod=0&endRange=28800&rangeType=0&startPeriod=0&startRange=0")
                        .method(Connection.Method.GET)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 OPR/68.0.3618.206")
                        .header("Host", "stats.nba.com")
                        .header("Connection", "keep-alive")
                        .header("Referer", "https://www.nba.com")
                        .header("Origin", "https://www.nba.com")
                        .ignoreContentType(true);
                if(scrapingProxy != null){
                    connection1.proxy(scrapingProxy.getIP(), scrapingProxy.getPort());
                }
                Connection.Response response2 = connection1.execute();
                JSONObject jsonObject2 = new JSONObject(response2.body()).getJSONObject("boxScoreAdvanced");
                //System.out.println("Game selected for rosters: " + jsonObject2.getJSONObject("homeTeam").getString("teamName") + " vs " + jsonObject2.getJSONObject("awayTeam").getString("teamName"));
                JSONArray playerArray;
                if (jsonObject2.getInt("awayTeamId") == teamID) {
                    playerArray = jsonObject2.getJSONObject("awayTeam").getJSONArray("players");
                } else {
                    playerArray = jsonObject2.getJSONObject("homeTeam").getJSONArray("players");
                }
                Set<MLBPlayer> teamList = new HashSet<>();
                //extractPlayers(teamList, playerArray, gameID, newGames, gameDate);
                //AWAY TEAM
                return teamList;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public MLBGame findGameById(int id){
        if(gameMap.containsKey(id)){
            return gameMap.get(id);
        }else {

            return null;
        }
    }

    public boolean isGameInBlacklist(int id){
        List<Integer> blackList = new ArrayList<>();
        blackList.add(634675);
        blackList.add(567633);
        return blackList.contains(id);
    }

}
