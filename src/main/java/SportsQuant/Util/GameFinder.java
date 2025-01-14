package SportsQuant.Util;

import BaseballQuant.Model.MLBGame;
import SportsQuant.Model.*;
import SportsQuant.Repository.GameRepository;
import net.bytebuddy.asm.Advice;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static java.lang.Thread.sleep;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

public class GameFinder {
    ChromeOptions chromeOptions;
    ChromeDriver webDriver;
    GameRepository gameRepository;
    List<Game> gameList;
    HashMap<Integer, Game> gameMap;
    public GameFinder() {
        System.setProperty("webdriver.chrome.driver", "C:/Users/Mark/Desktop/chromedriver.exe");
    }
    public GameFinder(List<Game> setGames){
        gameList = setGames;
        gameMap = new HashMap<>();
        for(Game game : setGames){
            gameMap.put(game.getGameId(),game);
        }
    }

    public void setGames(List<Game> gameList){
        this.gameList = gameList;
    }
    public void startWebDriver(){
        System.setProperty("webdriver.chrome.driver", "C:/Users/Mark/Desktop/chromedriver.exe");
        this.chromeOptions = new ChromeOptions();
        chromeOptions.setHeadless(false);
        webDriver = new ChromeDriver(chromeOptions);
    }

    public ReturnedOdds getOverUnder(LocalDate localDate, String teamName) {
        String gameID = "";
        String dateString = "";
        int day = localDate.getDayOfMonth();
        int month = localDate.getMonthValue();
        dateString = dateString + localDate.getYear();
        String monthString = "";
        if (month < 10) {
            monthString = "0" + month;
        } else {
            monthString = String.valueOf(month);
        }
        dateString = dateString + monthString;
        String dayString = "";
        if (day < 10) {
            dayString = "0" + day;
        } else {
            dayString = String.valueOf(day);
        }
        dateString = dateString + dayString;
        WebDriverWait wait = new WebDriverWait(webDriver, 20);
        while (true) {
            try {
                System.out.println(dateString);
                webDriver.get("https://www.espn.com/nba/scoreboard/_/date/" + dateString);
                WebElement events = wait.until(presenceOfElementLocated(By.id("events")));
                List<WebElement> gameElements = events.findElements(By.tagName("article"));
                for (WebElement gameElement : gameElements) {
                    if (gameElement.getText().toLowerCase().contains(teamName.toLowerCase())) {
                        System.out.println("ESPN Game ID found: " + gameElement.getAttribute("id"));
                        gameID = gameElement.getAttribute("id");
                        break;
                    }
                }
                webDriver.get("https://www.espn.com/nba/game/_/gameId/" + gameID);
                List<WebElement> oddsSelector = new ArrayList<>();
                try {
                    oddsSelector = wait.until(presenceOfAllElementsLocatedBy(By.className("odds-lines-plus-logo")));
                }catch (TimeoutException timeoutException){
                    System.out.println("UNABLE TO FIND OVERUNDER RETURNING 0.0");
                    return new ReturnedOdds();
                }
                String underOverString;
                String cleanedString;
                String lineString;
                String cleanedLineString;
                ReturnedOdds returnedOdds = new ReturnedOdds();
                //List<WebElement> oddsSelector = webDriver.findElements(By.className("odds-lines-plus-logo"));
                if(oddsSelector.get(0).findElements(By.tagName("li")).size() == 2){
                    lineString= oddsSelector.get(0).findElements(By.tagName("li")).get(0).getText();
                    underOverString= oddsSelector.get(0).findElements(By.tagName("li")).get(1).getText();
                    if(underOverString.contains("Over/Under")) {
                        cleanedString = underOverString.substring(underOverString.indexOf(" ") + 1);
                        returnedOdds.setOverUnder(Double.parseDouble(cleanedString));
                    }else{
                        System.out.println("UNABLE TO FIND OVERUNDER");
                    }
                    if(lineString.contains("Line")){
                        cleanedLineString = lineString.substring(lineString.indexOf(":") + 2);
                        returnedOdds.setSpreadLine(cleanedLineString);
                    }else{
                        System.out.println("UNABLE TO FIND LINE");
                    }

                }else{
                    underOverString= oddsSelector.get(0).findElements(By.tagName("li")).get(0).getText();
                    if(underOverString.contains("Over/Under")) {
                        cleanedString = underOverString.substring(underOverString.indexOf(" ") + 1);
                        returnedOdds.setOverUnder(Double.parseDouble(cleanedString));
                    }else{
                        System.out.println("UNABLE TO FIND OVERUNDER");
                    }
                }
                return  returnedOdds;
                //webDriver.quit();



            } catch (Exception e) {

                e.printStackTrace();
                //webDriver.quit();
            }
        }
    }

    public GameRepository getGameRepository() {
        return gameRepository;
    }

    public void setGameRepository(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public void quitWebDriver() {
        webDriver.quit();
    }

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

    public List<Game> findGamesOnDate(LocalDate localDate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        List<Game> games = new ArrayList<>();
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
        dateString = dateString + dayString + "/" + localDate.getYear() + " 12:00:00 AM";
        //System.out.println( dateString);
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
            JSONObject jsonObject = new JSONObject(response.body());
            JSONArray dateArray = jsonObject.getJSONObject("leagueSchedule").getJSONArray("gameDates");
            for (Object object : dateArray) {
                JSONObject gameDateObject = (JSONObject) object;
                if (gameDateObject.getString("gameDate").equals(dateString)) {
                    System.out.println("Date found.");
                    JSONArray gameArray = gameDateObject.getJSONArray("games");
                    System.out.println(gameArray);
                    for (Object gameObject : gameArray) {
                        JSONObject JSONgameDateObject = (JSONObject) gameObject;
                        Game newGame = new Game();
                        newGame.setAwayPoints(JSONgameDateObject.getJSONObject("awayTeam").getInt("score"));
                        newGame.setHomePoints(JSONgameDateObject.getJSONObject("homeTeam").getInt("score"));
                        newGame.setAwayTeamName(JSONgameDateObject.getJSONObject("awayTeam").getString("teamName"));
                        newGame.setHomeTeamName(JSONgameDateObject.getJSONObject("homeTeam").getString("teamName"));
                        newGame.setAwayTeamTricode(JSONgameDateObject.getJSONObject("awayTeam").getString("teamTricode"));
                        newGame.setHomeTeamTricode(JSONgameDateObject.getJSONObject("homeTeam").getString("teamTricode"));
                        newGame.setGameId(Integer.parseInt(JSONgameDateObject.getString("gameId")));
                        newGame.setDate(df.parse(JSONgameDateObject.getString("gameDateTimeEst")));
                        //System.out.println(newGame);
                        if (!isGameInBlacklist(JSONgameDateObject.getString("gameId"))) {
                            games.add(newGame);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return games;
    }
    public List<Game> findGamesOlderThan2021(LocalDate localDate){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        List<Game> games = new ArrayList<>();
        String dateString = "";
        int day = localDate.getDayOfMonth();
        int month = localDate.getMonthValue();
        String monthString = "";
        if (day < 10) {
            monthString = "" + month;
        } else {
            monthString = String.valueOf(month);
        }

        String dayString = "";

        if (day < 10) {
            dayString = "" + day;
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
            JSONArray gameArray = jsonObject.getJSONArray("games");
            System.out.println(dateString + " " + jsonObject);
            for(Object gameObject : gameArray){
                JSONObject JSONgameDateObject = (JSONObject) gameObject;
                    Game newGame = new Game();
                    newGame.setAwayPoints(JSONgameDateObject.getJSONObject("awayTeam").getInt("score"));
                    newGame.setHomePoints(JSONgameDateObject.getJSONObject("homeTeam").getInt("score"));
                    newGame.setAwayTeamName(JSONgameDateObject.getJSONObject("awayTeam").getString("teamName"));
                    newGame.setHomeTeamName(JSONgameDateObject.getJSONObject("homeTeam").getString("teamName"));
                    newGame.setAwayTeamTricode(JSONgameDateObject.getJSONObject("awayTeam").getString("teamTricode"));
                    newGame.setHomeTeamTricode(JSONgameDateObject.getJSONObject("homeTeam").getString("teamTricode"));
                    newGame.setGameId(Integer.parseInt(JSONgameDateObject.getString("gameId")));
                    newGame.setDate(df.parse(JSONgameDateObject.getString("gameEt")));
                    //System.out.println(newGame);
                    if (!isGameInBlacklist(JSONgameDateObject.getString("gameId")) &&
                            !JSONgameDateObject.getString("gameLabel").contains("All-Star") && !JSONgameDateObject.getString("gameLabel").contains("Rising")
                            && !JSONgameDateObject.getString("gameLabel").contains("Preseason")) {
                        games.add(newGame);
                    }

            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return games;
    }

    public List<Game> findGamesOnDateFromDB(LocalDate localDate) {
        LocalDateTime localDateTimeBefore = localDate.atStartOfDay().plusHours(3);
        LocalDateTime localDateTimeAfter = localDateTimeBefore.plusDays(1);

        //System.out.println("dateBefore: " + Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()) + " dateAfter: " + Date.from(localDateAfter.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        List<Game> games = new ArrayList<>();
        for(int i =0; i<gameList.size(); i++){
            if(gameList.get(i).getDate().before(Date.from(localDateTimeAfter.atZone(ZoneId.systemDefault()).toInstant())) && gameList.get(i).getDate().after(Date.from(localDateTimeBefore.atZone(ZoneId.systemDefault()).toInstant()))){
                games.add(gameList.get(i));
            }
        }

        Collections.sort(games,(o1, o2) -> o1.getDate().compareTo(o2.getDate()) * -1);
        return games;
    }

    public List<Game> findTodaysGames() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        List<Game> gameList = new ArrayList<>();
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
                Game game = new Game();
                game.setGameId(Integer.parseInt(jsonObject.getString("gameId")));
                game.setAwayTeamTricode(jsonObject.getJSONObject("awayTeam").getString("teamTricode"));
                Team awayTeam = new Team();
                //awayTeam.setPlayers(awayPlayerList);
                awayTeam.setTeamId(jsonObject.getJSONObject("awayTeam").getInt("teamId"));
                awayTeam.setPlayers(new HashSet<>());
                game.setAwayTeam(awayTeam);
                Team homeTeam = new Team();
                //awayTeam.setPlayers(awayPlayerList);
                homeTeam.setTeamId(jsonObject.getJSONObject("homeTeam").getInt("teamId"));
                homeTeam.setPlayers(new HashSet<>());
                game.setHomeTeam(homeTeam);
                game.setHomeTeamTricode(jsonObject.getJSONObject("homeTeam").getString("teamTricode"));
                game.setDate(df.parse(jsonObject.getString("gameEt")));
                gameList.add(game);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameList;
    }

    public void getPlayersRosterAndStatsForGame(Game game) {
        int id = game.getGameId();
        //System.out.println("fetching Game "+ id);
        String cleanedString = "00" + id;
        while (true) {
            try {
                //System.out.println("ScrapingProxy: "+ scrapingProxy);
                //Thread.sleep((long) Math.random() * 500);

                Connection connection = Jsoup.connect("https://stats.nba.com/stats/boxscoretraditionalv3?GameID=" + cleanedString + "&LeagueID=00&endPeriod=0&endRange=28800&rangeType=0&startPeriod=0&startRange=0")
                        .method(Connection.Method.GET)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 OPR/68.0.3618.206")
                        .header("Host", "stats.nba.com")
                        .header("Connection", "keep-alive")
                        .header("Cache-Control", "no-cache")
                        .header("Pragma", "no-cache")
                        .header("Accept", "*/*")
                        .header("DNT", "1")
                        .header("Referer", "https://www.nba.com")
                        .header("Origin", "https://www.nba.com")
                        .ignoreContentType(true);
                Connection.Response response = connection.execute();
                JSONObject jsonObject = new JSONObject(response.body()).getJSONObject("boxScoreTraditional");
                //AWAY TEAM
                JSONArray jsonArrayAway = jsonObject.getJSONObject("awayTeam").getJSONArray("players");
                //List<Player> awayPlayers = getPlayerListFromPreviousGame(jsonObject.getInt("awayTeamId"), game.getDate());
                Set<Player> awayPlayerList = new HashSet<>();
                extractPlayers(awayPlayerList, jsonArrayAway, id, false,game.getDate());
                Team awayTeam = new Team();
                awayTeam.setPlayers(awayPlayerList);
                awayTeam.setTeamId(jsonObject.getInt("awayTeamId"));
                if (jsonObject.getJSONObject("awayTeam").get("teamName") != JSONObject.NULL) {
                    awayTeam.setTeamName(jsonObject.getJSONObject("awayTeam").getString("teamName"));

                }
                game.setAwayTeam(awayTeam);
                //HOME TEAM
                JSONArray jsonArrayHome = jsonObject.getJSONObject("homeTeam").getJSONArray("players");
                //List<Player> homePlayers = getPlayerListFromPreviousGame(jsonObject.getInt("homeTeamId"), game.getDate());
                Set<Player> homePlayerList = new HashSet<>();
                extractPlayers(homePlayerList, jsonArrayHome, id, false,game.getDate());
                Team homeTeam = new Team();
                homeTeam.setPlayers(homePlayerList);
                homeTeam.setTeamId(jsonObject.getInt("homeTeamId"));
                if (jsonObject.getJSONObject("homeTeam").get("teamName") != JSONObject.NULL) {
                    homeTeam.setTeamName(jsonObject.getJSONObject("homeTeam").getString("teamName"));
                }
                game.setHomeTeam(homeTeam);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    sleep(10000);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }
    }

    public Set<Player> getPlayerListFromPreviousGameFromDB(Game game, int teamID) {
        //LocalDate JanFirst = LocalDate.of(2021, 1,1);
        //System.out.println("Game ID For Lists: " + game.getDate());
        String teamName;
        if(game.getHomeTeam().getTeamId() == teamID){
            teamName = game.getHomeTeamName();
        }else{
            teamName = game.getAwayTeamName();
        }
        LocalDate start = game.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusDays(10);
        LocalDate stop = game.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        //Date startDate = Date.from(start.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        //Date stopDate = Date.from(stop.atStartOfDay().minusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        //System.out.println("searching teamName: " + teamName);
        //List<Game> GamesIn2021 = gameRepository.findAllByAwayTeamTricodeOrHomeTeamTricode(teamName, teamName);
        //List<Game> GamesIn2021Test = gameRepository.findAllByAwayTeamTricodeOrHomeTeamTricode(teamName, teamName);

        if(teamName == null){
            System.out.println("STOP!");
        }
        List<Game> GamesIn2021 = new ArrayList<>();
        for(Game gameInList : gameList){
            try {
                if(teamName.equals("Trailblazers")){
                    if (gameInList.getAwayTeamName().equals("Trailblazers") || gameInList.getHomeTeamName().equals("Trailblazers")
                    || gameInList.getAwayTeamName().equals("Trail Blazers") || gameInList.getHomeTeamName().equals("Trail Blazers")) {
                        GamesIn2021.add(gameInList);
                    }
                }else
                    if(gameInList.getAwayTeamName() != null && gameInList.getHomeTeamName() != null) {
                        if (gameInList.getAwayTeamName().equals(teamName) || gameInList.getHomeTeamName().equals(teamName)) {
                            GamesIn2021.add(gameInList);
                        }
                    }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //System.out.println("Games before: " + stopDate + " and after " + startDate + " :: " + GamesIn2021);
//        List<Game> teamGames = new ArrayList<>();
//        for(Game game2: GamesIn2021){
//            if(game2.getHomeTeam().getTeamId() == teamID|| game2.getAwayTeam().getTeamId() == teamID){
//                teamGames.add(game2);
//                //System.out.println(game2.getGameId());
//            }
//        }
        GamesIn2021.sort(Comparator.comparing(Game::getDate).reversed());
        //System.out.println("Found Games: " + GamesIn2021.size());
        Game selectedGame = null;
        for(Game game3: GamesIn2021){
            if(game3.getDate().before(game.getDate())){
//                System.out.println(game3.getHomeTeam().getTeamName() + " vs " + game3.getAwayTeam().getTeamName()
//                        + " on " + game3.getDate() + " :: sizes: " + game3.getHomeTeam().getFieldingPlayers().size() + " : " + game3.getAwayTeam().getFieldingPlayers().size());
                selectedGame = game3;
                break;
            }

        }
        if(selectedGame == null){
            System.out.println("STOP HERE>");
        }
        //System.out.println("selected Game: " + selectedGame.getHomeTeam().getTeamName() + " vs " + selectedGame.getAwayTeam().getTeamName() + " on " + selectedGame.getDate() + " ID: " + selectedGame.getGameId());
        Set<Player> playerList = new HashSet<>();
        try {
            if (selectedGame.getAwayTeam().getTeamId() == teamID) {
                playerList = selectedGame.getAwayTeam().getPlayers();
            } else {
                playerList = selectedGame.getHomeTeam().getPlayers();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return playerList;
    }

    public void getPlayersForGameId(Game game, boolean newGames){
        int id = game.getGameId();
        String cleanedString = "00" + id;
        System.out.println(cleanedString);

        while(true) {
            try {
                //System.out.println("ScrapingProxy: "+ scrapingProxy);
                //Thread.sleep((long) Math.random() * 500);
                sleep(5000);
                Connection connection = Jsoup.connect("https://stats.nba.com/stats/boxscoreadvancedv3?GameID=" + cleanedString + "&LeagueID=00&endPeriod=0&endRange=28800&rangeType=0&startPeriod=0&startRange=0")
                        .method(Connection.Method.GET)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 OPR/68.0.3618.206")
                        .header("Host", "stats.nba.com")
                        .header("Connection", "keep-alive")
                        .header("Cache-Control", "no-cache")
                        .header("Pragma", "no-cache")
                        .header("Accept", "*/*")
                        .header("DNT", "1")
                        .header("Referer", "https://www.nba.com")
                        .header("Origin", "https://www.nba.com")
                        .ignoreContentType(true);
                Connection.Response response = connection.execute();
                JSONObject jsonObject = new JSONObject(response.body()).getJSONObject("boxScoreAdvanced");
                //AWAY TEAM
                JSONArray jsonArrayAway = jsonObject.getJSONObject("awayTeam").getJSONArray("players");
                //Set<Player> awayPlayers = getPlayerListFromPreviousGame(jsonObject.getInt("awayTeamId"), game.getDate(), newGames);
                //extractPlayers(awayPlayerList, jsonArrayAway);
                Team awayTeam = new Team();
                //awayTeam.setPlayers(awayPlayers);
                awayTeam.setTeamId(jsonObject.getInt("awayTeamId"));
                awayTeam.setTeamName(game.getAwayTeamName());
//                if (jsonObject.getJSONObject("awayTeam").get("teamName") != JSONObject.NULL) {
//                    awayTeam.setTeamName(jsonObject.getJSONObject("awayTeam").getString("teamName"));
//                }
                game.setAwayTeam(awayTeam);
                //HOME TEAM
                JSONArray jsonArrayHome = jsonObject.getJSONObject("homeTeam").getJSONArray("players");
                //Set<Player> homePlayers = getPlayerListFromPreviousGame(jsonObject.getInt("homeTeamId"), game.getDate(), newGames);
                //extractPlayers(awayPlayerList, jsonArrayHome);
                Team homeTeam = new Team();
               // homeTeam.setPlayers(homePlayers);
                homeTeam.setTeamId(jsonObject.getInt("homeTeamId"));
                homeTeam.setTeamName(game.getHomeTeamName());
//                if (jsonObject.getJSONObject("homeTeam").get("teamName") != JSONObject.NULL) {
//                    homeTeam.setTeamName(jsonObject.getJSONObject("homeTeam").getString("teamName"));
//                }
                game.setHomeTeam(homeTeam);
                break;
            } catch (Exception e) {
                e.printStackTrace();

                try{
                    sleep(10000);
                }catch (Exception ee){
                    ee.printStackTrace();
                }
            }
        }
    }

    private void extractPlayers(Set<Player> homePlayerList, JSONArray jsonArrayHome, int id, boolean newGames, Date gameDate) {
        for(Object object : jsonArrayHome){
            JSONObject playerObject = (JSONObject) object;
            //System.out.println(playerObject);
            //System.out.println(playerObject.getString("firstName") + " " + playerObject.getString("familyName"));
            Player player = new Player();
            player.setPlayerGamePerformances(new HashSet<>());
            player.setFirstName(playerObject.getString("firstName"));
            player.setLastName(playerObject.getString("familyName"));
            player.setPlayerID(playerObject.getInt("personId"));
            if(!newGames) {
                //System.out.println(playerObject.getJSONObject("statistics"));
                //System.out.println("points found: " +playerObject.getJSONObject("statistics").getInt("points") );
                PlayerGamePerformance playerGamePerformance = new PlayerGamePerformance();
                playerGamePerformance.setGameID(id);
                playerGamePerformance.setDate(gameDate);
                playerGamePerformance.setPoints(playerObject.getJSONObject("statistics").getInt("points"));
                playerGamePerformance.setBlocks(playerObject.getJSONObject("statistics").getInt("blocks"));
                playerGamePerformance.setSteals(playerObject.getJSONObject("statistics").getInt("steals"));
                playerGamePerformance.setRebounds(playerObject.getJSONObject("statistics").getInt("reboundsTotal"));
                playerGamePerformance.setFouls(playerObject.getJSONObject("statistics").getInt("foulsPersonal"));
                playerGamePerformance.setTurnovers(playerObject.getJSONObject("statistics").getInt("turnovers"));
                playerGamePerformance.setFieldGoalsAttempted(playerObject.getJSONObject("statistics").getInt("fieldGoalsAttempted"));
                playerGamePerformance.setFieldGoalsMade(playerObject.getJSONObject("statistics").getInt("fieldGoalsMade"));
                playerGamePerformance.setFieldGoalPercentage(playerObject.getJSONObject("statistics").getDouble("fieldGoalsPercentage"));
                playerGamePerformance.setFreeThrowsAttempted(playerObject.getJSONObject("statistics").getInt("freeThrowsAttempted"));
                playerGamePerformance.setFreeThrowsMade(playerObject.getJSONObject("statistics").getInt("freeThrowsMade"));
                playerGamePerformance.setFreeThrowsPercentage(playerObject.getJSONObject("statistics").getDouble("freeThrowsPercentage"));
                playerGamePerformance.setThreePointersAttempted(playerObject.getJSONObject("statistics").getInt("threePointersAttempted"));
                playerGamePerformance.setThreePointersMade(playerObject.getJSONObject("statistics").getInt("threePointersMade"));
                playerGamePerformance.setThreePointerPercentage(playerObject.getJSONObject("statistics").getDouble("threePointersPercentage"));
                playerGamePerformance.setReboundsDefensive(playerObject.getJSONObject("statistics").getInt("reboundsDefensive"));
                playerGamePerformance.setReboundsOffensive(playerObject.getJSONObject("statistics").getInt("reboundsOffensive"));
                String minutesString = playerObject.getJSONObject("statistics").getString("minutes");
                if(!minutesString.equals("")) {
                    String minutes = minutesString.substring(0, minutesString.indexOf(":"));
                    String seconds = minutesString.substring(minutesString.indexOf(":") + 1);
                    //System.out.println("Minutes: " + minutes + " seconds: " + seconds);
                    double fractionalMinute = Double.parseDouble(seconds)/60;
                    double fullMinutes = Double.parseDouble(minutes);
                    playerGamePerformance.setMinutes(fullMinutes + fractionalMinute);
                }
//                playerGamePerformance.setMinutes();
                //System.out.println(playerGamePerformance.getPoints());
                player.getPlayerGamePerformances().add(playerGamePerformance);
            }
            homePlayerList.add(player);
        }


    }

    private Set<Player> getPlayerListFromPreviousGame(int teamID, Date date, boolean newGames){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date beforeDate = date;
        int gameID = 0;
        if(beforeDate == null){
            beforeDate = new Date();
        }
        while(true) {
            try {
                sleep((long) (Math.random()*1750));
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
                Connection.Response response2 = connection1.execute();
                JSONObject jsonObject2 = new JSONObject(response2.body()).getJSONObject("boxScoreAdvanced");
                //System.out.println("Game selected for rosters: " + jsonObject2.getJSONObject("homeTeam").getString("teamName") + " vs " + jsonObject2.getJSONObject("awayTeam").getString("teamName"));
                JSONArray playerArray;
                if (jsonObject2.getInt("awayTeamId") == teamID) {
                    playerArray = jsonObject2.getJSONObject("awayTeam").getJSONArray("players");
                } else {
                    playerArray = jsonObject2.getJSONObject("homeTeam").getJSONArray("players");
                }
                Set<Player> teamList = new HashSet<>();
                extractPlayers(teamList, playerArray, gameID, newGames, gameDate);
                //AWAY TEAM
                return teamList;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Game findGameById(int id){
        if(gameMap.get(id) != null){
            return gameMap.get(id);
        }

        return null;
    }

    public boolean isGameInBlacklist(String id){
        if(id.equals("0031900001")){
            return true;
        }else return id.equals("0032000001");
    }

}
