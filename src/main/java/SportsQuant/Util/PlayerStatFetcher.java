package SportsQuant.Util;

import SportsQuant.Model.*;
import SportsQuant.Model.CacheObject.PlayerPerformanceCacheObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class PlayerStatFetcher {
    private HashMap<Integer, HashMap<Integer,PlayerPerformanceCacheObject>> playerPerformanceCache;
    private HashMap<Integer, HashMap<Integer,PlayerPerformanceCacheObject>> playerBlockCache;
    private HashMap<Integer, HashMap<Integer,PlayerPerformanceCacheObject>> playerTurnoverCache;
    //PlayerDictionary playerDictionary;
    public PlayerStatFetcher(){
        playerPerformanceCache = new HashMap<>();
        playerBlockCache = new HashMap<>();
        playerTurnoverCache = new HashMap<>();
        //playerDictionary = new PlayerDictionary();
    }
    public Set<PlayerGamePerformance> getFullSeasonPlayerStatsPerGame(int playerId){
        Set<PlayerGamePerformance> playerGamePerformanceList = new HashSet<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Connection.Response playoffResponse = requestPlayerData(playerId, true);
            addGameStatsToList(playerGamePerformanceList, df, playoffResponse);

            Connection.Response response = requestPlayerData(playerId, false);
            addGameStatsToList(playerGamePerformanceList, df, response);
        }catch (Exception e){
            e.printStackTrace();
        }
        return playerGamePerformanceList;
    }

    private void addGameStatsToList(Set<PlayerGamePerformance> playerGamePerformanceList, DateFormat df, Connection.Response response) throws ParseException {
        JSONArray playerGameArray = new JSONObject(response.body()).getJSONArray("resultSets").getJSONObject(0).getJSONArray("rowSet");
        for(Object object : playerGameArray){
            JSONArray jsonArray = (JSONArray) object;
            PlayerGamePerformance playerGamePerformance = new PlayerGamePerformance();
            playerGamePerformance.setGameID(jsonArray.getInt(7));
            playerGamePerformance.setPoints(jsonArray.getInt(31));
            playerGamePerformance.setDate(df.parse(jsonArray.getString(8)));
            playerGamePerformance.setAssists(jsonArray.getInt(24));
            playerGamePerformance.setRebounds(jsonArray.getInt(23));
            playerGamePerformance.setBlocks(jsonArray.getInt(27));
            playerGamePerformance.setMinutes(jsonArray.getDouble(11));
            playerGamePerformanceList.add(playerGamePerformance);
        }
    }


    private Connection.Response requestPlayerData(int playerId, boolean playoffs){
        String seasonString = "Regular+Season";
        if(playoffs){
            seasonString="Playoffs";
        }
        while(true) {
            try {
                Thread.sleep(400);
                Connection.Response response = Jsoup.connect("https://stats.nba.com/stats/playergamelogs?DateFrom=&" +
                        "DateTo=&" +
                        "GameSegment=&" + "LastNGames=0" + "&LeagueID=00" + "&Location=&" + "MeasureType=Base&" + "Month=0&" +
                        "OpponentTeamID=0&" + "Outcome=&" + "PORound=0&" + "PaceAdjust=N&" + "PerMode=Totals&" + "Period=0&" + "PlayerID=" + playerId + "&" +
                        "PlusMinus=N&" + "Rank=N&" + "Season=2020-21&" +
                        "SeasonSegment=&" +
                        "SeasonType=" + seasonString + "&" +
                        "ShotClockRange=&" +
                        "VsConference=" +
                        "&VsDivision=")
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 OPR/68.0.3618.206")
                        .header("Host", "stats.nba.com")
                        .header("Connection", "keep-alive")
                        .header("Referer", "https://www.nba.com")
                        .header("Origin", "https://www.nba.com")
                        .method(Connection.Method.GET)
                        .ignoreContentType(true)
                        .execute();
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int getOpponentTurnoversForPlayerModel(int gameCount, Player player, Date date, GameFinder gameFinder, double gameTimeThreshold){
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        localDate = localDate.minusDays(0);
        Date beforeDate = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        int games = 0;
        List<PlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(player.getPlayerGamePerformances());
        playerGamePerformanceList.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        List<Integer> GameIDs = new ArrayList<>();
        for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
            if(games>=gameCount){
                break;
            }
            if(playerGamePerformance.getDate().before(beforeDate) && playerGamePerformance.getMinutes()>= gameTimeThreshold){
                GameIDs.add(playerGamePerformance.getGameID());
                games++;
            }
        }
        int totalTurnovers = 0;
        for(int GameID : GameIDs){
            //System.out.println("[BlockModel][Fetch] " + gameFinder.findGameById(cleanedID));
            totalTurnovers = totalTurnovers + countOpposingTurnoversInGame(gameFinder.findGameById(GameID), player);
        }
        return totalTurnovers;
    }
    public int getOpponentBlocksForPlayerModel(int gameCount, Player player, Date date, GameFinder gameFinder, double gameTimeThreshold){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        localDate = localDate.minusDays(0);
        Date beforeDate = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        //System.out.println("searching for most recent games before " + date + " for player: " + player.getFirstName() + " " + player.getLastName());
        int games = 0;
        List<PlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(player.getPlayerGamePerformances());
        playerGamePerformanceList.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        List<Integer> GameIDs = new ArrayList<>();
        for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
            if(games>=gameCount){
                break;
            }
            if(playerGamePerformance.getDate().before(beforeDate) && playerGamePerformance.getMinutes()>= gameTimeThreshold){
                GameIDs.add(playerGamePerformance.getGameID());
//                System.out.println("[BlockModel] GameDate: " + simpleDateFormat.format(playerGamePerformance.getDate()) +
//                        " PGPID: " + playerGamePerformance.getId() + " Pts: " + playerGamePerformance.getPoints() + " gameID: " + playerGamePerformance.getGameID());
                games++;
            }else{
                //System.out.println(playerGamePerformance.getDate() + " is not before " + beforeDate);
            }
        }
        int totalBlocks = 0;
        for(int GameID : GameIDs){

            //System.out.println("[BlockModel][Fetch] " + gameFinder.findGameById(cleanedID));
            totalBlocks = totalBlocks + countOpposingBlocksInGame(gameFinder.findGameById(GameID), player);
        }

        //System.out.println("Total Blocks For Model of " + player.getFirstName() + " " + player.getLastName() + ": " + totalBlocks);
        return totalBlocks;
    }

    public List<Double> modelOpponentBlocks(int gameCount, Player player, Date date, GameFinder gameFinder, PlayerStatFilter playerStatFilter,
                                            double gameTimeThreshold, int dayLookbackCap, boolean doubleSquareRoot, boolean allowBelowZero){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(6);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        //System.out.println("searching for most recent games before " + date + " for player: " + player.getFirstName() + " " + player.getLastName());
        int games = 0;
        List<PlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(player.getPlayerGamePerformances());
        playerGamePerformanceList.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        List<Integer> GameIDs = new ArrayList<>();

        for(int i = 0; i<playerGamePerformanceList.size(); i++){
            if(games>=gameCount){
                break;
            }
            if(playerGamePerformanceList.get(i).getDate().before(beforeDate)
                    //&& playerGamePerformanceList.get(i).getMinutes()>= gameTimeThreshold
            ) {
                GameIDs.add(playerGamePerformanceList.get(i).getGameID());
//                System.out.println("[BlockModel] GameDate: " + simpleDateFormat.format(playerGamePerformance.getDate()) +
//                        " PGPID: " + playerGamePerformance.getId() + " Pts: " + playerGamePerformance.getPoints() + " gameID: " + playerGamePerformance.getGameID());
                games++;
            }else{
                //System.out.println(playerGamePerformance.getDate() + " is not before " + beforeDate);
            }
        }
        List<Double> highRangeValues = new ArrayList<>();
        List<Double> lowRangeValues = new ArrayList<>();
        for(int gameIdIndex = 0; gameIdIndex< GameIDs.size(); gameIdIndex ++){
        //for(int GameID : GameIDs){
            Team team = null;
            Game game = gameFinder.findGameById(GameIDs.get(gameIdIndex));
            if(game == null){
                System.out.println(GameIDs.get(gameIdIndex) + " is missing");
            }
            List<Player> players = new ArrayList<>(game.getHomeTeam().getPlayers());
            for(int i = 0; i<players.size(); i++){
                if (players.get(i).getPlayerID() == player.getPlayerID()) {
                    team = game.getAwayTeam();
                    break;
                }
            }
            if(team == null) {
                players = new ArrayList<>(game.getAwayTeam().getPlayers());
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getPlayerID() == player.getPlayerID()) {
                        team = game.getHomeTeam();
                        break;
                    }
                }
            }
            double highRange = 0.0;
            double lowRange = 0.0;

            //RUN MODEL FOR SELECTED TEAM
            List<Player> playerList = new ArrayList<>(team.getPlayers());
            for(int i = 0; i<playerList.size(); i++){
            //for(Player OpposingPlayer : team.getPlayers()){
                Player tempPlayer = (Player) playerList.get(i).clone();
                double opposingPlayerBlocks = 0.0;

                //double opposingPlayerBlocksPerGame = playerStatFilter.getPlayerBlocksPerGameLastNGames(gameCount, OpposingPlayer, date);
                double opposingPlayerBlocksStdDev =0.0;
                if (playerBlockCache.get(tempPlayer.getPlayerID()) != null) {
                    if(playerBlockCache.get(tempPlayer.getPlayerID()).get(game.getGameId()) != null){
                            opposingPlayerBlocks = playerBlockCache.get(tempPlayer.getPlayerID()).get(game.getGameId()).getBlocksPerGame();
                            opposingPlayerBlocksStdDev = playerBlockCache.get(tempPlayer.getPlayerID()).get(game.getGameId()).getBlocksPerGameStdDev();
                    }else {
                        playerStatFilter.trimPlayerGameHistory(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                        opposingPlayerBlocks = playerStatFilter.getPlayerBlocksPerGameLastNGames(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                        opposingPlayerBlocksStdDev = playerStatFilter.getPlayerBlocksLastNGamesStdDev(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                        PlayerPerformanceCacheObject playerPerformanceCacheObject = new PlayerPerformanceCacheObject();
                        playerPerformanceCacheObject.setPlayerId(tempPlayer.getPlayerID());
                        playerPerformanceCacheObject.setGameid(game.getGameId());
                        playerPerformanceCacheObject.setBlocksPerGame((double) opposingPlayerBlocks);
                        playerPerformanceCacheObject.setBlocksPerGameStdDev(opposingPlayerBlocksStdDev);
                        playerPerformanceCacheObject.setPlayerGameLookback(gameCount);
                        playerPerformanceCacheObject.setGameTimeThreshold(gameTimeThreshold);
                        playerBlockCache.get(tempPlayer.getPlayerID()).put(game.getGameId(), playerPerformanceCacheObject);
                    }
                } else{
                    playerStatFilter.trimPlayerGameHistory(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                    opposingPlayerBlocks = playerStatFilter.getPlayerBlocksPerGameLastNGames(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                    opposingPlayerBlocksStdDev = playerStatFilter.getPlayerBlocksLastNGamesStdDev(gameCount, tempPlayer, game.getDate(),  gameTimeThreshold);
                    PlayerPerformanceCacheObject playerPerformanceCacheObject = new PlayerPerformanceCacheObject();
                    playerPerformanceCacheObject.setPlayerId(tempPlayer.getPlayerID());
                    playerPerformanceCacheObject.setGameid(game.getGameId());
                    playerPerformanceCacheObject.setBlocksPerGame((double)opposingPlayerBlocks);
                    playerPerformanceCacheObject.setBlocksPerGameStdDev(opposingPlayerBlocksStdDev);
                    playerPerformanceCacheObject.setPlayerGameLookback(gameCount);
                    playerPerformanceCacheObject.setGameTimeThreshold(gameTimeThreshold);
                    HashMap<Integer, PlayerPerformanceCacheObject> map = new HashMap<>();
                    map.put(game.getGameId(), playerPerformanceCacheObject);
                    playerBlockCache.put(tempPlayer.getPlayerID(), map);
                }


                if(doubleSquareRoot){
                    opposingPlayerBlocksStdDev = Math.sqrt(opposingPlayerBlocksStdDev);
                }
                if(!Double.isNaN(opposingPlayerBlocksStdDev)) {
                    highRange = highRange + (opposingPlayerBlocks) + opposingPlayerBlocksStdDev;
                    if (allowBelowZero) {
                        lowRange = lowRange + (opposingPlayerBlocks) - opposingPlayerBlocksStdDev;
                    } else {
                        if ((opposingPlayerBlocks) - opposingPlayerBlocksStdDev > 0) {
                            lowRange = lowRange + (opposingPlayerBlocks) - opposingPlayerBlocksStdDev;
                        }
                    }

                }
            }
            //System.out.println("midPoint: " + (highRange + lowRange)/2);
            highRangeValues.add(highRange);
            lowRangeValues.add(lowRange);


            //System.out.println("[BlockModel][Fetch] " + gameFinder.findGameById(cleanedID));
            //totalBlocks = totalBlocks + countOpposingBlocksInGame(gameFinder.findGameById(GameID), player);
        }
        if(GameIDs.size()>0) {
            double highTotal = 0.0;
            for(int i  = 0; i < highRangeValues.size(); i++){
                highTotal = highTotal + highRangeValues.get(i);
            }
            //for (Double highDouble : highRangeValues) {

            //}
            double highAverage = highTotal / highRangeValues.size();
            double lowTotal = 0.0;
            for(int i  = 0; i < lowRangeValues.size(); i++){
                //for (Double lowDouble : lowRangeValues) {
                lowTotal = lowTotal + lowRangeValues.get(i);
            }
            double lowAverage = lowTotal / lowRangeValues.size();
            List<Double> highLowList = new ArrayList<>();
            highLowList.add(highAverage);
            highLowList.add(lowAverage);
            return highLowList;
        }
        return null;
        //System.out.println("Total Blocks For Model of " + player.getFirstName() + " " + player.getLastName() + ": " + totalBlocks);

    }

    public PerformanceReturnObject modelOpponentStats(int gameCount, Player player, Date date, GameFinder gameFinder,
                                                       PlayerStatFilter playerStatFilter, double gameTimeThreshold, int dayLookbackCap, boolean doubleSquareRoot, boolean allowBelowZero){
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(6);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        int games = 0;
        List<PlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(player.getPlayerGamePerformances());
        playerGamePerformanceList.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        List<Integer> GameIDs = new ArrayList<>();

        for(int i = 0; i<playerGamePerformanceList.size(); i++){
            if(games>=gameCount){
                break;
            }
            if(playerGamePerformanceList.get(i).getDate().before(beforeDate)
                //&& playerGamePerformanceList.get(i).getMinutes()>= gameTimeThreshold
            ) {
                GameIDs.add(playerGamePerformanceList.get(i).getGameID());
                games++;
            }
        }
        List<Double> highStealRangeValues = new ArrayList<>();
        List<Double> lowStealRangeValues = new ArrayList<>();
        List<Double> highBlockRangeValues = new ArrayList<>();
        List<Double> lowBlockRangeValues = new ArrayList<>();
        List<Double> highTurnoverRangeValues = new ArrayList<>();
        List<Double> lowTurnoverRangeValues = new ArrayList<>();
        List<Double> highReboundRangeValues = new ArrayList<>();
        List<Double> lowReboundRangeValues = new ArrayList<>();
        List<Double> highFoulRangeValues = new ArrayList<>();
        List<Double> lowFoulRangeValues = new ArrayList<>();
        for(int gameIdIndex = 0; gameIdIndex< GameIDs.size(); gameIdIndex ++){
            Team team = null;
            Game game = gameFinder.findGameById(GameIDs.get(gameIdIndex));
            if(game == null){
                System.out.println("");
            }
            List<Player> players = new ArrayList<>(game.getHomeTeam().getPlayers());
            for(int i = 0; i<players.size(); i++){
                if (players.get(i).getPlayerID() == player.getPlayerID()) {
                    team = game.getAwayTeam();
                    break;
                }
            }
            if(team == null) {
                players = new ArrayList<>(game.getAwayTeam().getPlayers());
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getPlayerID() == player.getPlayerID()) {
                        team = game.getHomeTeam();
                        break;
                    }
                }
            }
            double highStealRange = 0.0;
            double lowStealRange = 0.0;
            double highBlockRange = 0.0;
            double lowBlockRange = 0.0;
            double highTurnoverRange = 0.0;
            double lowTurnoverRange = 0.0;
            double highReboundRange = 0.0;
            double lowReboundRange = 0.0;
            double highFoulRange = 0.0;
            double lowFoulRange = 0.0;
            //RUN MODEL FOR SELECTED TEAM
            List<Player> playerList = new ArrayList<>(team.getPlayers());
            for(int i = 0; i<playerList.size(); i++) {
                Player tempPlayer = (Player) playerList.get(i).clone();
                double opposingPlayerSteals = 0.0;
                double opposingPlayerStealsStdDev = 0.0;
                double opposingPlayerBlocks = 0.0;
                double opposingPlayerBlocksStdDev =0.0;
                double opposingPlayerTurnovers = 0.0;
                double opposingPlayerTurnoversStdDev = 0.0;
                double opposingPlayerRebounds = 0.0;
                double opposingPlayerReboundsStdDev = 0.0;
                double opposingPlayerFouls = 0.0;
                double opposingPlayerFoulsStdDev = 0.0;
                if (playerPerformanceCache.get(tempPlayer.getPlayerID()) != null) {
                    if(playerPerformanceCache.get(tempPlayer.getPlayerID()).get(game.getGameId()) != null){
                        opposingPlayerSteals = playerPerformanceCache.get(tempPlayer.getPlayerID()).get(game.getGameId()).getStealsPerGame();
                        opposingPlayerStealsStdDev = playerPerformanceCache.get(tempPlayer.getPlayerID()).get(game.getGameId()).getStealsPerGameStdDev();
                        opposingPlayerBlocks = playerPerformanceCache.get(tempPlayer.getPlayerID()).get(game.getGameId()).getBlocksPerGame();
                        opposingPlayerBlocksStdDev = playerPerformanceCache.get(tempPlayer.getPlayerID()).get(game.getGameId()).getBlocksPerGameStdDev();
                        opposingPlayerTurnovers = playerPerformanceCache.get(tempPlayer.getPlayerID()).get(game.getGameId()).getTurnoversPerGame();
                        opposingPlayerTurnoversStdDev = playerPerformanceCache.get(tempPlayer.getPlayerID()).get(game.getGameId()).getTurnoversPerGameStdDev();
                        opposingPlayerRebounds = playerPerformanceCache.get(tempPlayer.getPlayerID()).get(game.getGameId()).getReboundsPerGame();
                        opposingPlayerReboundsStdDev = playerPerformanceCache.get(tempPlayer.getPlayerID()).get(game.getGameId()).getReboundsPerGameStdDev();
                        opposingPlayerFouls = playerPerformanceCache.get(tempPlayer.getPlayerID()).get(game.getGameId()).getFoulsPerGame();
                        opposingPlayerFoulsStdDev = playerPerformanceCache.get(tempPlayer.getPlayerID()).get(game.getGameId()).getFoulsPerGameStdDev();
                    }else{
                        playerStatFilter.trimPlayerGameHistory(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                        opposingPlayerSteals = playerStatFilter.getPlayerStealsPerGameLastNGames(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                        opposingPlayerStealsStdDev = playerStatFilter.getPlayerStealsLastNGamesStdDev(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                        opposingPlayerBlocks = playerStatFilter.getPlayerBlocksPerGameLastNGames(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                        opposingPlayerBlocksStdDev = playerStatFilter.getPlayerBlocksLastNGamesStdDev(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                        opposingPlayerTurnovers = playerStatFilter.getPlayerTurnoversPerGameLastNGames(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                        opposingPlayerTurnoversStdDev = playerStatFilter.getPlayerTurnoversLastNGamesStdDev(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                        opposingPlayerRebounds = playerStatFilter.getPlayerTurnoversPerGameLastNGames(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                        opposingPlayerReboundsStdDev = playerStatFilter.getPlayerTurnoversLastNGamesStdDev(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                        opposingPlayerFouls = playerStatFilter.getPlayerFoulsPerGameLastNGames(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                        opposingPlayerFoulsStdDev = playerStatFilter.getPlayerFoulsLastNGamesStdDev(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                        PlayerPerformanceCacheObject playerPerformanceCacheObject = new PlayerPerformanceCacheObject();
                        playerPerformanceCacheObject.setPlayerId(tempPlayer.getPlayerID());
                        playerPerformanceCacheObject.setGameid(game.getGameId());
                        playerPerformanceCacheObject.setStealsPerGame((double) opposingPlayerSteals);
                        playerPerformanceCacheObject.setStealsPerGameStdDev(opposingPlayerStealsStdDev);
                        playerPerformanceCacheObject.setBlocksPerGame(opposingPlayerBlocks);
                        playerPerformanceCacheObject.setBlocksPerGameStdDev(opposingPlayerBlocksStdDev);
                        playerPerformanceCacheObject.setTurnoversPerGame(opposingPlayerTurnovers);
                        playerPerformanceCacheObject.setTurnoversPerGameStdDev(opposingPlayerTurnoversStdDev);
                        playerPerformanceCacheObject.setReboundsPerGame(opposingPlayerRebounds);
                        playerPerformanceCacheObject.setReboundsPerGameStdDev(opposingPlayerReboundsStdDev);
                        playerPerformanceCacheObject.setFoulsPerGame(opposingPlayerFouls);
                        playerPerformanceCacheObject.setFoulsPerGameStdDev(opposingPlayerFoulsStdDev);
                        playerPerformanceCacheObject.setPlayerGameLookback(gameCount);
                        playerPerformanceCacheObject.setGameTimeThreshold(gameTimeThreshold);
                        playerPerformanceCache.get(tempPlayer.getPlayerID()).put(game.getGameId(),playerPerformanceCacheObject);
                    }
                } else {
                    playerStatFilter.trimPlayerGameHistory(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                    opposingPlayerSteals = playerStatFilter.getPlayerStealsPerGameLastNGames(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                    opposingPlayerStealsStdDev = playerStatFilter.getPlayerStealsLastNGamesStdDev(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                    opposingPlayerBlocks = playerStatFilter.getPlayerBlocksPerGameLastNGames(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                    opposingPlayerBlocksStdDev = playerStatFilter.getPlayerBlocksLastNGamesStdDev(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                    opposingPlayerTurnovers = playerStatFilter.getPlayerTurnoversPerGameLastNGames(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                    opposingPlayerTurnoversStdDev = playerStatFilter.getPlayerTurnoversLastNGamesStdDev(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                    opposingPlayerRebounds = playerStatFilter.getPlayerTurnoversPerGameLastNGames(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                    opposingPlayerReboundsStdDev = playerStatFilter.getPlayerTurnoversLastNGamesStdDev(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                    PlayerPerformanceCacheObject playerPerformanceCacheObject = new PlayerPerformanceCacheObject();
                    playerPerformanceCacheObject.setPlayerId(tempPlayer.getPlayerID());
                    playerPerformanceCacheObject.setGameid(game.getGameId());
                    playerPerformanceCacheObject.setStealsPerGame((double) opposingPlayerSteals);
                    playerPerformanceCacheObject.setStealsPerGameStdDev(opposingPlayerStealsStdDev);
                    playerPerformanceCacheObject.setBlocksPerGame(opposingPlayerBlocks);
                    playerPerformanceCacheObject.setBlocksPerGameStdDev(opposingPlayerBlocksStdDev);
                    playerPerformanceCacheObject.setTurnoversPerGame(opposingPlayerTurnovers);
                    playerPerformanceCacheObject.setTurnoversPerGameStdDev(opposingPlayerTurnoversStdDev);
                    playerPerformanceCacheObject.setReboundsPerGame(opposingPlayerRebounds);
                    playerPerformanceCacheObject.setReboundsPerGameStdDev(opposingPlayerReboundsStdDev);
                    playerPerformanceCacheObject.setFoulsPerGame(opposingPlayerFouls);
                    playerPerformanceCacheObject.setFoulsPerGameStdDev(opposingPlayerFoulsStdDev);
                    playerPerformanceCacheObject.setPlayerGameLookback(gameCount);
                    playerPerformanceCacheObject.setGameTimeThreshold(gameTimeThreshold);
                    HashMap<Integer, PlayerPerformanceCacheObject> map = new HashMap<>();
                    map.put(game.getGameId(), playerPerformanceCacheObject);
                    playerPerformanceCache.put(tempPlayer.getPlayerID(), map);
                }
                if (doubleSquareRoot) {
                    opposingPlayerStealsStdDev = Math.sqrt(opposingPlayerStealsStdDev);
                    opposingPlayerTurnoversStdDev = Math.sqrt(opposingPlayerTurnoversStdDev);
                    opposingPlayerBlocksStdDev = Math.sqrt(opposingPlayerBlocksStdDev);
                    opposingPlayerReboundsStdDev = Math.sqrt(opposingPlayerReboundsStdDev);
                    opposingPlayerFoulsStdDev = Math.sqrt(opposingPlayerFoulsStdDev);
                }
                if (!Double.isNaN(opposingPlayerStealsStdDev)) {
                    highStealRange = highStealRange + (opposingPlayerSteals) + opposingPlayerStealsStdDev;
                    if (allowBelowZero) {
                        lowStealRange = lowStealRange + (opposingPlayerSteals) - opposingPlayerStealsStdDev;
                    } else {
                        if ((opposingPlayerSteals) - opposingPlayerStealsStdDev > 0) {
                            lowStealRange = lowStealRange + (opposingPlayerSteals) - opposingPlayerStealsStdDev;
                        }
                    }
                }
                if (!Double.isNaN(opposingPlayerTurnoversStdDev)) {
                    highTurnoverRange = highTurnoverRange + (opposingPlayerTurnovers) + opposingPlayerTurnoversStdDev;
                    if (allowBelowZero) {
                        lowTurnoverRange = lowTurnoverRange + (opposingPlayerTurnovers) - opposingPlayerTurnoversStdDev;
                    } else {
                        if ((opposingPlayerTurnovers) - opposingPlayerTurnoversStdDev > 0) {
                            lowTurnoverRange = lowTurnoverRange + (opposingPlayerTurnovers) - opposingPlayerTurnoversStdDev;
                        }
                    }
                }
                if(!Double.isNaN(opposingPlayerBlocksStdDev)) {
                    highBlockRange = highBlockRange + (opposingPlayerBlocks) + opposingPlayerBlocksStdDev;
                    if (allowBelowZero) {
                        lowBlockRange = lowBlockRange + (opposingPlayerBlocks) - opposingPlayerBlocksStdDev;
                    } else {
                        if ((opposingPlayerBlocks) - opposingPlayerBlocksStdDev > 0) {
                            lowBlockRange = lowBlockRange + (opposingPlayerBlocks) - opposingPlayerBlocksStdDev;
                        }
                    }
                }
                if(!Double.isNaN(opposingPlayerReboundsStdDev)) {
                    highReboundRange = highReboundRange + (opposingPlayerRebounds) + opposingPlayerReboundsStdDev;
                    if (allowBelowZero) {
                        lowReboundRange = lowReboundRange + (opposingPlayerRebounds) - opposingPlayerReboundsStdDev;
                    } else {
                        if ((opposingPlayerRebounds) - opposingPlayerReboundsStdDev > 0) {
                            lowReboundRange = lowReboundRange + (opposingPlayerRebounds) - opposingPlayerReboundsStdDev;
                        }
                    }
                }
                if(!Double.isNaN(opposingPlayerFoulsStdDev)) {
                    highFoulRange = highFoulRange + (opposingPlayerFouls) + opposingPlayerFoulsStdDev;
                    if (allowBelowZero) {
                        lowFoulRange = lowFoulRange + (opposingPlayerFouls) - opposingPlayerFoulsStdDev;
                    } else {
                        if ((opposingPlayerFouls) - opposingPlayerFoulsStdDev > 0) {
                            lowFoulRange = lowFoulRange + (opposingPlayerFouls) - opposingPlayerFoulsStdDev;
                        }
                    }
                }

            }
            highStealRangeValues.add(highStealRange);
            lowStealRangeValues.add(lowStealRange);
            highBlockRangeValues.add(highBlockRange);
            lowBlockRangeValues.add(lowBlockRange);
            highTurnoverRangeValues.add(highTurnoverRange);
            lowTurnoverRangeValues.add(lowTurnoverRange);
            highReboundRangeValues.add(highReboundRange);
            lowReboundRangeValues.add(lowReboundRange);
            highFoulRangeValues.add(highFoulRange);
            lowFoulRangeValues.add(lowFoulRange);
        }
        if(GameIDs.size()>0) {
            PerformanceReturnObject performanceReturnObject = new PerformanceReturnObject();
            double highStealTotal = 0.0;
            for(int i  = 0; i < highStealRangeValues.size(); i++){
                highStealTotal = highStealTotal + highStealRangeValues.get(i);
            }
            performanceReturnObject.setOpponentStealsHigh(highStealTotal / highStealRangeValues.size());
            double lowStealTotal = 0.0;
            for(int i  = 0; i < lowStealRangeValues.size(); i++){
                //for (Double lowDouble : lowRangeValues) {
                lowStealTotal = lowStealTotal + lowStealRangeValues.get(i);
            }
            performanceReturnObject.setOpponentStealsLow(lowStealTotal / lowStealRangeValues.size());
            double highBlockTotal = 0.0;
            for(int i  = 0; i < highBlockRangeValues.size(); i++){
                highBlockTotal = highBlockTotal + highBlockRangeValues.get(i);
            }
            performanceReturnObject.setOpponentBlocksHigh(highBlockTotal / highBlockRangeValues.size());
            double lowBlockTotal = 0.0;
            for(int i  = 0; i < lowBlockRangeValues.size(); i++){
                lowBlockTotal = lowBlockTotal + lowBlockRangeValues.get(i);
            }
            performanceReturnObject.setOpponentBlocksLow(lowBlockTotal / lowBlockRangeValues.size());
            double highTurnoverTotal = 0.0;
            for(int i  = 0; i < highTurnoverRangeValues.size(); i++){
                highTurnoverTotal = highTurnoverTotal + highTurnoverRangeValues.get(i);
            }
            performanceReturnObject.setOpponentTurnoversHigh(highTurnoverTotal / highTurnoverRangeValues.size());
            double lowTurnoverTotal = 0.0;
            for(int i  = 0; i < lowTurnoverRangeValues.size(); i++){
                lowTurnoverTotal = lowTurnoverTotal + lowTurnoverRangeValues.get(i);
            }
            performanceReturnObject.setOpponentTurnoversLow(lowTurnoverTotal / lowTurnoverRangeValues.size());
            double highReboundTotal = 0.0;
            for(int i  = 0; i < highReboundRangeValues.size(); i++){
                highReboundTotal = highReboundTotal + highReboundRangeValues.get(i);
            }
            performanceReturnObject.setOpponentReboundHigh(highReboundTotal / highReboundRangeValues.size());
            double lowReboundTotal = 0.0;
            for(int i  = 0; i < lowReboundRangeValues.size(); i++){
                lowReboundTotal = lowReboundTotal + lowReboundRangeValues.get(i);
            }
            performanceReturnObject.setOpponentReboundLow(lowReboundTotal / lowReboundRangeValues.size());
            double highFoulTotal = 0.0;
            for(int i  = 0; i < highFoulRangeValues.size(); i++){
                highFoulTotal = highFoulTotal + highFoulRangeValues.get(i);
            }
            performanceReturnObject.setOpponentFoulHigh(highFoulTotal / highFoulRangeValues.size());
            double lowFoulTotal = 0.0;
            for(int i  = 0; i < lowFoulRangeValues.size(); i++){
                lowFoulTotal = lowFoulTotal + lowFoulRangeValues.get(i);
            }
            performanceReturnObject.setOpponentFoulHigh(lowFoulTotal / lowFoulRangeValues.size());



            return performanceReturnObject;
        }
        return null;
    }



    public List<Double> modelOpponentSteals(int gameCount, Player player, Date date, GameFinder gameFinder,
                                            PlayerStatFilter playerStatFilter, double gameTimeThreshold, int dayLookbackCap, boolean doubleSquareRoot, boolean allowBelowZero){
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(6);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        int games = 0;
        List<PlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(player.getPlayerGamePerformances());
        playerGamePerformanceList.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        List<Integer> GameIDs = new ArrayList<>();

        for(int i = 0; i<playerGamePerformanceList.size(); i++){
            if(games>=gameCount){
                break;
            }
            if(playerGamePerformanceList.get(i).getDate().before(beforeDate)
                //&& playerGamePerformanceList.get(i).getMinutes()>= gameTimeThreshold
            ) {
                GameIDs.add(playerGamePerformanceList.get(i).getGameID());
                games++;
            }
        }
        List<Double> highRangeValues = new ArrayList<>();
        List<Double> lowRangeValues = new ArrayList<>();
        for(int gameIdIndex = 0; gameIdIndex< GameIDs.size(); gameIdIndex ++){
            Team team = null;
            Game game = gameFinder.findGameById(GameIDs.get(gameIdIndex));
            List<Player> players = new ArrayList<>(game.getHomeTeam().getPlayers());
            for(int i = 0; i<players.size(); i++){
                if (players.get(i).getPlayerID() == player.getPlayerID()) {
                    team = game.getAwayTeam();
                    break;
                }
            }
            if(team == null) {
                players = new ArrayList<>(game.getAwayTeam().getPlayers());
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getPlayerID() == player.getPlayerID()) {
                        team = game.getHomeTeam();
                        break;
                    }
                }
            }
            double highRange = 0.0;
            double lowRange = 0.0;

            //RUN MODEL FOR SELECTED TEAM
            List<Player> playerList = new ArrayList<>(team.getPlayers());
            for(int i = 0; i<playerList.size(); i++) {
                Player tempPlayer = (Player) playerList.get(i).clone();
                double opposingPlayerSteals = 0.0;

                double opposingPlayerStealsStdDev = 0.0;

                if (playerPerformanceCache.get(tempPlayer.getPlayerID()) != null) {
                    if(playerPerformanceCache.get(tempPlayer.getPlayerID()).get(game.getGameId()) != null){
                        opposingPlayerSteals = playerPerformanceCache.get(tempPlayer.getPlayerID()).get(game.getGameId()).getStealsPerGame();
                        opposingPlayerStealsStdDev = playerPerformanceCache.get(tempPlayer.getPlayerID()).get(game.getGameId()).getStealsPerGameStdDev();
                    }else{
                        playerStatFilter.trimPlayerGameHistory(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                        opposingPlayerSteals = playerStatFilter.getPlayerStealsPerGameLastNGames(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                        opposingPlayerStealsStdDev = playerStatFilter.getPlayerStealsLastNGamesStdDev(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                        PlayerPerformanceCacheObject playerPerformanceCacheObject = new PlayerPerformanceCacheObject();
                        playerPerformanceCacheObject.setPlayerId(tempPlayer.getPlayerID());
                        playerPerformanceCacheObject.setGameid(game.getGameId());
                        playerPerformanceCacheObject.setStealsPerGame((double) opposingPlayerSteals);
                        playerPerformanceCacheObject.setStealsPerGameStdDev(opposingPlayerStealsStdDev);
                        playerPerformanceCacheObject.setPlayerGameLookback(gameCount);
                        playerPerformanceCacheObject.setGameTimeThreshold(gameTimeThreshold);
                        playerPerformanceCache.get(tempPlayer.getPlayerID()).put(game.getGameId(),playerPerformanceCacheObject);
                    }
                } else {
                    playerStatFilter.trimPlayerGameHistory(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                    opposingPlayerSteals = playerStatFilter.getPlayerStealsPerGameLastNGames(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                    opposingPlayerStealsStdDev = playerStatFilter.getPlayerStealsLastNGamesStdDev(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                    PlayerPerformanceCacheObject playerPerformanceCacheObject = new PlayerPerformanceCacheObject();
                    playerPerformanceCacheObject.setPlayerId(tempPlayer.getPlayerID());
                    playerPerformanceCacheObject.setGameid(game.getGameId());
                    playerPerformanceCacheObject.setStealsPerGame((double) opposingPlayerSteals);
                    playerPerformanceCacheObject.setStealsPerGameStdDev(opposingPlayerStealsStdDev);
                    playerPerformanceCacheObject.setPlayerGameLookback(gameCount);
                    playerPerformanceCacheObject.setGameTimeThreshold(gameTimeThreshold);
                    HashMap<Integer, PlayerPerformanceCacheObject> map = new HashMap<>();
                    map.put(game.getGameId(), playerPerformanceCacheObject);
                    playerPerformanceCache.put(tempPlayer.getPlayerID(), map);
                }

                if (doubleSquareRoot) {
                    opposingPlayerStealsStdDev = Math.sqrt(opposingPlayerStealsStdDev);

                }
                if (!Double.isNaN(opposingPlayerStealsStdDev)) {
                    highRange = highRange + (opposingPlayerSteals) + opposingPlayerStealsStdDev;
                    if (allowBelowZero) {
                        lowRange = lowRange + (opposingPlayerSteals) - opposingPlayerStealsStdDev;
                    } else {
                        if ((opposingPlayerSteals) - opposingPlayerStealsStdDev > 0) {
                            lowRange = lowRange + (opposingPlayerSteals) - opposingPlayerStealsStdDev;
                        }
                    }
                }


            }
            highRangeValues.add(highRange);
            lowRangeValues.add(lowRange);

        }
        if(GameIDs.size()>0) {
            double highTotal = 0.0;
            for(int i  = 0; i < highRangeValues.size(); i++){
                highTotal = highTotal + highRangeValues.get(i);
            }
            //for (Double highDouble : highRangeValues) {

            //}
            double highAverage = highTotal / highRangeValues.size();
            double lowTotal = 0.0;
            for(int i  = 0; i < lowRangeValues.size(); i++){
            //for (Double lowDouble : lowRangeValues) {
                lowTotal = lowTotal + lowRangeValues.get(i);
            }
            double lowAverage = lowTotal / lowRangeValues.size();
            List<Double> highLowList = new ArrayList<>();
            highLowList.add(highAverage);
            highLowList.add(lowAverage);
            return highLowList;
        }
        return null;
    }
    public List<Double> modelOpponentTurnovers(int gameCount, Player player, Date date, GameFinder gameFinder,
                                               PlayerStatFilter playerStatFilter, double gameTimeThreshold, int dayLookbackCap, boolean doubleSquareRoot, boolean allowBelowZero){
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(6);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        int games = 0;
        List<PlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(player.getPlayerGamePerformances());
        playerGamePerformanceList.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        List<Integer> GameIDs = new ArrayList<>();
        for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
            if(games>=gameCount){
                break;
            }
            if(playerGamePerformance.getDate().before(beforeDate) && playerGamePerformance.getMinutes()>= gameTimeThreshold){
                GameIDs.add(playerGamePerformance.getGameID());

                games++;
            }
        }
        List<Double> highRangeValues = new ArrayList<>();
        List<Double> lowRangeValues = new ArrayList<>();
        for(int GameID : GameIDs){
            Team team = null;
            Game game = gameFinder.findGameById(GameID);
            for(Player potentialOpponentPlayer:  game.getHomeTeam().getPlayers()){
                //TODO:: ORIGINALLY BOTH WERE SET TO GETHOMETEAM()
                if(potentialOpponentPlayer.getPlayerID() == player.getPlayerID()){
                    team = game.getAwayTeam();
                    break;
                }
            }
            for(Player potentialOpponentPlayer:  game.getAwayTeam().getPlayers()){
                if(potentialOpponentPlayer.getPlayerID() == player.getPlayerID()){
                    team = game.getHomeTeam();
                    break;
                }
            }
            double highRange = 0.0;
            double lowRange = 0.0;
            //RUN MODEL FOR SELECTED TEAM
            List<Player> playerList = new ArrayList<>(team.getPlayers());
            for(int i = 0; i<playerList.size(); i++) {
                Player tempPlayer = (Player) playerList.get(i).clone();
                double opposingPlayerTurnovers = 0.0;

                double opposingPlayerTurnoversStdDev = 0.0;

                //double opposingPlayerSteals = playerStatFilter.getPlayerStealsLastNGames(gameCount, tempPlayer, game.getDate(), dayLookbackCap);
                //double opposingPlayerBlocksPerGame = playerStatFilter.getPlayerBlocksPerGameLastNGames(gameCount, OpposingPlayer, date);
                // double opposingPlayerStealsStdDev = playerStatFilter.getPlayerStealsLastNGamesStdDev(gameCount, tempPlayer, game.getDate());


                if (playerTurnoverCache.get(tempPlayer.getPlayerID()) != null) {
                    if(playerTurnoverCache.get(tempPlayer.getPlayerID()).get(game.getGameId()) != null){
                            opposingPlayerTurnovers = playerTurnoverCache.get(tempPlayer.getPlayerID()).get(game.getGameId()).getTurnoversPerGame();
                            opposingPlayerTurnoversStdDev = playerTurnoverCache.get(tempPlayer.getPlayerID()).get(game.getGameId()).getTurnoversPerGameStdDev();
                    }else{
                        playerStatFilter.trimPlayerGameHistory(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                        opposingPlayerTurnovers = playerStatFilter.getPlayerTurnoversPerGameLastNGames(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                        opposingPlayerTurnoversStdDev = playerStatFilter.getPlayerTurnoversLastNGamesStdDev(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                        PlayerPerformanceCacheObject playerPerformanceCacheObject = new PlayerPerformanceCacheObject();
                        playerPerformanceCacheObject.setPlayerId(tempPlayer.getPlayerID());
                        playerPerformanceCacheObject.setGameid(game.getGameId());
                        playerPerformanceCacheObject.setTurnoversPerGame((double) opposingPlayerTurnovers);
                        playerPerformanceCacheObject.setTurnoversPerGameStdDev(opposingPlayerTurnoversStdDev);
                        playerPerformanceCacheObject.setPlayerGameLookback(gameCount);
                        playerPerformanceCacheObject.setGameTimeThreshold(gameTimeThreshold);
                        playerTurnoverCache.get(tempPlayer.getPlayerID()).put(game.getGameId(),playerPerformanceCacheObject);
                    }
                } else {
                    playerStatFilter.trimPlayerGameHistory(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                    opposingPlayerTurnovers = playerStatFilter.getPlayerTurnoversPerGameLastNGames(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                    opposingPlayerTurnoversStdDev = playerStatFilter.getPlayerTurnoversLastNGamesStdDev(gameCount, tempPlayer, game.getDate(), gameTimeThreshold);
                    PlayerPerformanceCacheObject playerPerformanceCacheObject = new PlayerPerformanceCacheObject();
                    playerPerformanceCacheObject.setPlayerId(tempPlayer.getPlayerID());
                    playerPerformanceCacheObject.setGameid(game.getGameId());
                    playerPerformanceCacheObject.setTurnoversPerGame((double) opposingPlayerTurnovers);
                    playerPerformanceCacheObject.setTurnoversPerGameStdDev(opposingPlayerTurnoversStdDev);
                    playerPerformanceCacheObject.setPlayerGameLookback(gameCount);
                    playerPerformanceCacheObject.setGameTimeThreshold(gameTimeThreshold);
                    HashMap<Integer, PlayerPerformanceCacheObject> map = new HashMap<>();
                    map.put(game.getGameId(), playerPerformanceCacheObject);
                    playerTurnoverCache.put(tempPlayer.getPlayerID(), map);

                }

                if (doubleSquareRoot) {
                    opposingPlayerTurnoversStdDev = Math.sqrt(opposingPlayerTurnoversStdDev);
                }
                if (!Double.isNaN(opposingPlayerTurnoversStdDev)) {
                    highRange = highRange + (opposingPlayerTurnovers) + opposingPlayerTurnoversStdDev;
                    if (allowBelowZero) {
                        lowRange = lowRange + (opposingPlayerTurnovers) - opposingPlayerTurnoversStdDev;
                    } else {
                        if ((opposingPlayerTurnovers) - opposingPlayerTurnoversStdDev > 0) {
                            lowRange = lowRange + (opposingPlayerTurnovers) - opposingPlayerTurnoversStdDev;
                        }
                    }
                }


            }
            highRangeValues.add(highRange);
            lowRangeValues.add(lowRange);
        }
        if(GameIDs.size()>0) {
            double highTotal = 0.0;
            for(int i  = 0; i < highRangeValues.size(); i++){
                highTotal = highTotal + highRangeValues.get(i);
            }
            //for (Double highDouble : highRangeValues) {

            //}
            double highAverage = highTotal / highRangeValues.size();
            double lowTotal = 0.0;
            for(int i  = 0; i < lowRangeValues.size(); i++){
                //for (Double lowDouble : lowRangeValues) {
                lowTotal = lowTotal + lowRangeValues.get(i);
            }
            double lowAverage = lowTotal / lowRangeValues.size();
            List<Double> highLowList = new ArrayList<>();
            highLowList.add(highAverage);
            highLowList.add(lowAverage);
            return highLowList;
        }
        return null;
    }


    public int getOpponentStealsForPlayerModel(int gameCount, Player player, Date date, GameFinder gameFinder, double gameTimeThreshold){
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(6);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        //System.out.println("searching for most recent games before " + date + " for player: " + player.getFirstName() + " " + player.getLastName());
        int games = 0;
        List<PlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(player.getPlayerGamePerformances());
        playerGamePerformanceList.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        List<Integer> GameIDs = new ArrayList<>();
        for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
            if(games>=gameCount){
                break;
            }
            if(playerGamePerformance.getDate().before(beforeDate) && playerGamePerformance.getMinutes()>= gameTimeThreshold){
                GameIDs.add(playerGamePerformance.getGameID());
//                System.out.println("[BlockModel] GameDate: " + simpleDateFormat.format(playerGamePerformance.getDate()) +
//                        " PGPID: " + playerGamePerformance.getId() + " Pts: " + playerGamePerformance.getPoints() + " gameID: " + playerGamePerformance.getGameID());
                games++;
            }else{
                //System.out.println(playerGamePerformance.getDate() + " is not before " + beforeDate);
            }
        }
        int totalSteals = 0;
        for(int GameID : GameIDs){

            //System.out.println("[BlockModel][Fetch] " + gameFinder.findGameById(cleanedID));
            totalSteals = totalSteals + countOpposingStealsInGame(gameFinder.findGameById(GameID), player);
        }

        //System.out.println("Total Blocks For Model of " + player.getFirstName() + " " + player.getLastName() + ": " + totalBlocks);
        return totalSteals;
    }


    public int countOpposingStealsInGame(Game game, Player player){
        int totalSteals = 0;
        boolean home = false;
        for(Player gamePlayer : game.getHomeTeam().getPlayers()){
            if(gamePlayer.getPlayerID() == player.getPlayerID()){
                home = true;
                break;
            }
        }

        if(home){
            for(Player player2 : game.getAwayTeam().getPlayers()){
                PlayerGamePerformance playerGamePerformance = player2.findPlayerPerformanceByGameId(game.getGameId());
                if(playerGamePerformance != null){
                    totalSteals = totalSteals + playerGamePerformance.getSteals();
                    //System.out.println("[BlockModel][Fetch] " + player2.getFirstName() + " " + player2.getLastName() + " blocks: " + playerGamePerformance.getBlocks());
                }
            }
        }else{
            for(Player player2 : game.getHomeTeam().getPlayers()){
                PlayerGamePerformance playerGamePerformance = player2.findPlayerPerformanceByGameId(game.getGameId());
                if(playerGamePerformance != null){
                    totalSteals = totalSteals + playerGamePerformance.getSteals();
                    //System.out.println("[BlockModel][Fetch] " + player2.getFirstName() + " " + player2.getLastName() + " blocks: " + playerGamePerformance.getBlocks());
                }
            }
        }

        return totalSteals;
    }



    public int countOpposingBlocksInGame(Game game, Player player){
        int totalBlocks = 0;
        boolean home = false;
        for(Player gamePlayer : game.getHomeTeam().getPlayers()){
            if(gamePlayer.getPlayerID() == player.getPlayerID()){
                home = true;
                break;
            }
        }

        if(home){
            for(Player player2 : game.getAwayTeam().getPlayers()){
                PlayerGamePerformance playerGamePerformance = player2.findPlayerPerformanceByGameId(game.getGameId());
                if(playerGamePerformance != null){
                    totalBlocks = totalBlocks + playerGamePerformance.getBlocks();
                    //System.out.println("[BlockModel][Fetch] " + player2.getFirstName() + " " + player2.getLastName() + " blocks: " + playerGamePerformance.getBlocks());
                }
            }
        }else{
            for(Player player2 : game.getHomeTeam().getPlayers()){
                PlayerGamePerformance playerGamePerformance = player2.findPlayerPerformanceByGameId(game.getGameId());
                if(playerGamePerformance != null){
                    totalBlocks = totalBlocks + playerGamePerformance.getBlocks();
                    //System.out.println("[BlockModel][Fetch] " + player2.getFirstName() + " " + player2.getLastName() + " blocks: " + playerGamePerformance.getBlocks());
                }
            }
        }

        return totalBlocks;
    }
    public int countOpposingTurnoversInGame(Game game, Player player){
        int totalTurnovers = 0;
        boolean home = false;
        for(Player gamePlayer : game.getHomeTeam().getPlayers()){
            if(gamePlayer.getPlayerID() == player.getPlayerID()){
                home = true;
                break;
            }
        }

        if(home){
            for(Player player2 : game.getAwayTeam().getPlayers()){
                PlayerGamePerformance playerGamePerformance = player2.findPlayerPerformanceByGameId(game.getGameId());
                if(playerGamePerformance != null){
                    totalTurnovers = totalTurnovers + playerGamePerformance.getTurnovers();
                    //System.out.println("[BlockModel][Fetch] " + player2.getFirstName() + " " + player2.getLastName() + " blocks: " + playerGamePerformance.getBlocks());
                }
            }
        }else{
            for(Player player2 : game.getHomeTeam().getPlayers()){
                PlayerGamePerformance playerGamePerformance = player2.findPlayerPerformanceByGameId(game.getGameId());
                if(playerGamePerformance != null){
                    totalTurnovers = totalTurnovers + playerGamePerformance.getTurnovers();
                    //System.out.println("[BlockModel][Fetch] " + player2.getFirstName() + " " + player2.getLastName() + " blocks: " + playerGamePerformance.getBlocks());
                }
            }
        }

        return totalTurnovers;
    }

}
