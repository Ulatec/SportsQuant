package BaseballQuant.Util;

import BaseballQuant.Model.*;
import BaseballQuant.Model.CacheObjects.PitcherPerformanceCacheObject;
import BaseballQuant.Model.CacheObjects.PlayerPerformanceCacheObject;
import BaseballQuant.Model.CacheObjects.TeamCacheObject;
import BaseballQuant.Model.CacheObjects.TestValue;
import SportsQuant.Model.PlayerGamePerformance;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class PlayerStatFetcher {
    //PlayerDictionary playerDictionary;
    private HashMap<Integer, List<PlayerPerformanceCacheObject>> playerPerformanceCache;
    private HashMap<Integer, TestValue> pitcherPerformanceCache;
    private HashMap<Integer, List<TeamCacheObject>> teamCache;
    public PlayerStatFetcher(){
        playerPerformanceCache = new HashMap<>();
        pitcherPerformanceCache = new HashMap<>();
        teamCache = new HashMap<>();
        //playerDictionary = new PlayerDictionary();
    }
    public List<PlayerGamePerformance> getFullSeasonPlayerStatsPerGame(int playerId){
        List<PlayerGamePerformance> playerGamePerformanceList = new ArrayList<>();
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

    private void addGameStatsToList(List<PlayerGamePerformance> playerGamePerformanceList, DateFormat df, Connection.Response response) throws ParseException {
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

    public List<Double> modelOpponentPitching(int gameCount,int pitcherGameCount, MLBPlayer player, MLBGame parentGame, Date date, GameFinder gameFinder,
                                              PlayerStatFilter playerStatFilter, boolean doubleSquareRoot, boolean allowBelowZero){
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().minusHours(2);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        //System.out.println("searching for most recent games before " + date + " for player: " + player.getFirstName() + " " + player.getLastName());
        int games = 0;
        Set<MLBPlayerGamePerformance> playerGamePerformanceSet = player.getPlayerGamePerformances();
        List<MLBPlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(playerGamePerformanceSet);
        playerGamePerformanceList.sort(Comparator.comparing(MLBPlayerGamePerformance::getDate));
        List<Integer> GameIDs = new ArrayList<>();
        for(int i = playerGamePerformanceList.size()-1; i>0; i--){
            if(games>=gameCount){
                break;
            }
            if(playerGamePerformanceList.get(i).getDate().before(beforeDate) && playerGamePerformanceList.get(i).getFieldingpercentage()> 0){
                GameIDs.add(playerGamePerformanceList.get(i).getGameId());
                games++;
            }
        }
        List<Double> highRangeValues = new ArrayList<>();
        List<Double> lowRangeValues = new ArrayList<>();
        for(int i = 0; i<GameIDs.size(); i++) {
            MLBTeam team = null;
            MLBGame game = gameFinder.findGameById(GameIDs.get(i));
            //System.out.println(GameID);
            List<MLBPlayer> players = new ArrayList<>(game.getHomeMLBTeam().getFieldingPlayers());
            players.sort(Comparator.comparing(MLBPlayer::getPlayerID));
            for (int j = 0; j < players.size(); j++) {
                if (players.get(j).getPlayerID() == player.getPlayerID()) {
                    //TODO: HAD THESE BACKWARDS. BOTH WERE SET TO GET HOME TEAM.
                    team = (MLBTeam) game.getAwayMLBTeam().clone();
                    break;
                }
            }
            players = new ArrayList<>(game.getAwayMLBTeam().getFieldingPlayers());
            for (int j = 0; j < players.size(); j++) {
                if (players.get(j).getPlayerID() == player.getPlayerID()) {
                    team = (MLBTeam) game.getHomeMLBTeam().clone();
                    break;
                }
            }
            double highRange = 0.0;
            double lowRange = 0.0;


                //RUN MODEL FOR SELECTED TEAM
                List<MLBPitcher> pitchers = new ArrayList<>(team.getPitchingPlayers());
                for (int k = 0; k < pitchers.size(); k++) {
                    //for(MLBPitcher OpposingPlayer : team.getPitchingPlayers()){
                    if (k > team.getPitchingPlayers().size()) {
                        System.out.println("STOP!!");
                    }
                    MLBPitcher tempPlayer = (MLBPitcher) pitchers.get(k).clone();
                    double opposingPitcherRuns = 0.0;

                    //double opposingPlayerBlocksPerGame = playerStatFilter.getPlayerBlocksPerGameLastNGames(gameCount, OpposingPlayer, date);
                    double opposingPitcherRunsStdDev = 0.0;
                    boolean cacheFound = false;
                    PitcherPerformanceCacheObject lookupKey = new PitcherPerformanceCacheObject();
                    lookupKey.setGameid(game.getGameId());
                    lookupKey.setPlayerId(tempPlayer.getPlayerID());
                    lookupKey.setPlayerGameLookback(gameCount);
                    lookupKey.setPitcherGameLookback(pitcherGameCount);
                    if (pitcherPerformanceCache.get(lookupKey.hashCode()) != null) {
                                opposingPitcherRuns = pitcherPerformanceCache.get(lookupKey.hashCode()).getRunsGivenUpLastNGames();
                                opposingPitcherRunsStdDev = pitcherPerformanceCache.get(lookupKey.hashCode()).getRunsGivenPerGameStdDev();
                                cacheFound = true;
                    } else {
                        playerStatFilter.trimPlayerGameHistory(pitcherGameCount, tempPlayer, game.getDate());
                        opposingPitcherRuns = playerStatFilter.getPitcherGivenRunsLastNGames(pitcherGameCount, tempPlayer, game.getDate()) + playerStatFilter.getPitcherRunsGivenUpRateOfChange(pitcherGameCount, tempPlayer, game.getDate());
                        opposingPitcherRunsStdDev = playerStatFilter.getPitcherGivenRunsLastNGamesStdDev(pitcherGameCount, tempPlayer, game.getDate(), (opposingPitcherRuns / pitcherGameCount));

                        TestValue testValue = new TestValue();
                        testValue.setRunsGivenUpLastNGames(opposingPitcherRuns);
                        testValue.setRunsGivenPerGameStdDev(opposingPitcherRunsStdDev);
                        pitcherPerformanceCache.put(lookupKey.hashCode(), testValue);
                    }
//                        cacheFound = true;
//                    }
//                    if (!cacheFound) {
//                        playerStatFilter.trimPlayerGameHistory(pitcherGameCount, tempPlayer, game.getDate());
//                        opposingPitcherRuns = playerStatFilter.getPitcherGivenRunsLastNGames(pitcherGameCount, tempPlayer, game.getDate());
//                        opposingPitcherRunsStdDev = playerStatFilter.getPitcherGivenRunsLastNGamesStdDev(pitcherGameCount, tempPlayer, game.getDate(), (opposingPitcherRuns / pitcherGameCount));
//                        PitcherPerformanceCacheObject playerPerformanceCacheObject = new PitcherPerformanceCacheObject();
//                        playerPerformanceCacheObject.setPlayerId(tempPlayer.getPlayerID());
//                        playerPerformanceCacheObject.setGameid(game.getGameId());
//                        playerPerformanceCacheObject.setRunsGivenUpLastNGames(opposingPitcherRuns);
//                        playerPerformanceCacheObject.setRunsGivenPerGameStdDev(opposingPitcherRunsStdDev);
//                        playerPerformanceCacheObject.setPitcherGameLookback(pitcherGameCount);
//                        playerPerformanceCacheObject.setPlayerGameLookback(gameCount);
//                        pitcherPerformanceCache.get(tempPlayer.getPlayerID()).add(playerPerformanceCacheObject);
//                    }

                    if (doubleSquareRoot) {
                        opposingPitcherRunsStdDev = Math.sqrt(opposingPitcherRunsStdDev);
                    }

                    if (!Double.isNaN(opposingPitcherRunsStdDev)) {
                        highRange = highRange + (opposingPitcherRuns / pitcherGameCount) + opposingPitcherRunsStdDev;
                        if (allowBelowZero) {
                            lowRange = lowRange + (opposingPitcherRuns / pitcherGameCount) - opposingPitcherRunsStdDev;
                        } else {
                            if ((opposingPitcherRuns / pitcherGameCount) - opposingPitcherRunsStdDev > 0) {
                                lowRange = lowRange + (opposingPitcherRuns / pitcherGameCount) - opposingPitcherRunsStdDev;
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

            for(int i = 0; i<highRangeValues.size(); i++){
            //for (Double highDouble : highRangeValues) {
                highTotal = highTotal + highRangeValues.get(i);
            }
            double highAverage = highTotal / highRangeValues.size();
            double lowTotal = 0.0;
            for(int i = 0; i<lowRangeValues.size(); i++){
                lowTotal = lowTotal + lowRangeValues.get(i);
            }
            double lowAverage = lowTotal / lowRangeValues.size();
            List<Double> highLowList = new ArrayList<>();
            highLowList.add(highAverage);
            highLowList.add(lowAverage);
            return highLowList;
        }

        List<Double> highLowList = new ArrayList<>();
        highLowList.add(0.0);
        highLowList.add(0.0);
        return highLowList;
    }

    public List<Double> modelOpponentFielding(int gameCount, MLBPlayer player, MLBGame parentGame, Date date, GameFinder gameFinder,
                                              PlayerStatFilter playerStatFilter, boolean doubleSquareRoot, boolean allowBelowZero){
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().minusHours(2);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        //System.out.println("searching for most recent games before " + date + " for player: " + player.getFirstName() + " " + player.getLastName());
        int games = 0;
        Set<MLBPlayerGamePerformance> playerGamePerformanceSet = player.getPlayerGamePerformances();
        List<MLBPlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(playerGamePerformanceSet);
        playerGamePerformanceList.sort(Comparator.comparing(MLBPlayerGamePerformance::getDate));
        List<Integer> GameIDs = new ArrayList<>();
        for(int i = playerGamePerformanceList.size()-1; i>0; i--){
        //for(MLBPlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
            if(games>=gameCount){
                break;
            }
            if(playerGamePerformanceList.get(i).getDate().before(beforeDate) && playerGamePerformanceList.get(i).getFieldingpercentage()> 0){
                GameIDs.add(playerGamePerformanceList.get(i).getGameId());
                games++;
            }
        }
        List<Double> highRangeValues = new ArrayList<>();
        List<Double> lowRangeValues = new ArrayList<>();
        for(int i = 0; i<GameIDs.size(); i++){
            MLBTeam team = null;
            MLBGame game = gameFinder.findGameById(GameIDs.get(i));
            for(MLBPlayer potentialOpponentPlayer:  game.getHomeMLBTeam().getFieldingPlayers()){
                if(potentialOpponentPlayer.getPlayerID() == player.getPlayerID()){
                    //TODO: HAD THESE BACKWARDS. BOTH WERE SET TO GET HOME TEAM.
                    team = game.getAwayMLBTeam();
                    break;
                }
            }
            for(MLBPlayer potentialOpponentPlayer:  game.getAwayMLBTeam().getFieldingPlayers()){
                if(potentialOpponentPlayer.getPlayerID() == player.getPlayerID()){
                    team = game.getHomeMLBTeam();
                    break;
                }
            }
            double highRange = 0.0;
            double lowRange = 0.0;

            //RUN MODEL FOR SELECTED TEAM
            ArrayList<MLBPlayer> playerList = new ArrayList<>(team.getFieldingPlayers());
            for(int j = 0; j<team.getFieldingPlayers().size(); j++){
            //for(MLBPlayer OpposingPlayer : team.getFieldingPlayers()){
                MLBPlayer tempPlayer = (MLBPlayer) playerList.get(j).clone();
                double opposingPlayerFielding = 0.0;

                double opposingPlayerFieldingStdDev = 0.0;
                boolean cacheFound = false;
//                if (playerPerformanceCache.get(tempPlayer.getPlayerID()) != null) {
//                    List<PlayerPerformanceCacheObject> list = playerPerformanceCache.get(tempPlayer.getPlayerID());
//                    for (int k = 0; k < list.size(); k++) {
//                        if (list.get(k).getGameid() == game.getGameId() && list.get(k).getPlayerGameLookback() == gameCount) {
//                            opposingPlayerFielding = list.get(k).getFielding();
//                            opposingPlayerFieldingStdDev = list.get(k).getFieldingStdDev();
//                            cacheFound = true;
//                            break;
//                        }
//                    }
//                } else {
                    playerStatFilter.trimPlayerGameHistory(gameCount, tempPlayer, game.getDate());
                    opposingPlayerFielding = playerStatFilter.getFieldingLastNGames(gameCount, tempPlayer, game.getDate()) + playerStatFilter.getFieldingRateOfChange(gameCount, tempPlayer, game.getDate()) ;
                    opposingPlayerFieldingStdDev = playerStatFilter.getFieldingStdDevLastNGames(gameCount, tempPlayer, game.getDate(), opposingPlayerFielding);
                    PlayerPerformanceCacheObject playerPerformanceCacheObject = new PlayerPerformanceCacheObject();
                    playerPerformanceCacheObject.setPlayerId(tempPlayer.getPlayerID());
                    playerPerformanceCacheObject.setGameid(game.getGameId());
                    playerPerformanceCacheObject.setFielding(opposingPlayerFielding);
                    playerPerformanceCacheObject.setFieldingStdDev(opposingPlayerFieldingStdDev);
                    playerPerformanceCacheObject.setPlayerGameLookback(gameCount);
                    List<PlayerPerformanceCacheObject> list = new ArrayList<>();
                    list.add(playerPerformanceCacheObject);
//                    playerPerformanceCache.put(tempPlayer.getPlayerID(), list);
//
//                }
//
//                if(!cacheFound){
//                    playerStatFilter.trimPlayerGameHistory(gameCount, tempPlayer,game.getDate());
//                    opposingPlayerFielding = playerStatFilter.getFieldingLastNGames(gameCount, tempPlayer, game.getDate());
//                    opposingPlayerFieldingStdDev = playerStatFilter.getFieldingStdDevLastNGames(gameCount, tempPlayer, game.getDate(), opposingPlayerFielding);
//                    PlayerPerformanceCacheObject playerPerformanceCacheObject = new PlayerPerformanceCacheObject();
//                    playerPerformanceCacheObject.setPlayerId(tempPlayer.getPlayerID());
//                    playerPerformanceCacheObject.setGameid(game.getGameId());
//                    playerPerformanceCacheObject.setFielding(opposingPlayerFielding);
//                    playerPerformanceCacheObject.setFieldingStdDev(opposingPlayerFieldingStdDev);
//                    playerPerformanceCacheObject.setPlayerGameLookback(gameCount);
//                    playerPerformanceCache.get(tempPlayer.getPlayerID()).add(playerPerformanceCacheObject);
//                }


                if(doubleSquareRoot) {
                    opposingPlayerFieldingStdDev = Math.sqrt(opposingPlayerFieldingStdDev);
                }

                //double lowRunRange = (opposingPlayerFielding) - opposingPlayerFieldingStdDev;
                if(!Double.isNaN(opposingPlayerFieldingStdDev)) {
                    highRange = highRange + (opposingPlayerFielding) + opposingPlayerFieldingStdDev;
                    if(allowBelowZero){
                        lowRange = lowRange + (opposingPlayerFielding/gameCount) - opposingPlayerFieldingStdDev;
                    }else{
                        if ((opposingPlayerFielding/gameCount) - opposingPlayerFieldingStdDev > 0) {
                            lowRange = lowRange + (opposingPlayerFielding/gameCount) - opposingPlayerFieldingStdDev;
                        }
                    }
                }
            }
            highRangeValues.add(highRange);
            lowRangeValues.add(lowRange);
        }

        if(GameIDs.size()>0) {
            double highTotal = 0.0;
            for(int i = 0; i<highRangeValues.size(); i++){
                highTotal = highTotal + highRangeValues.get(i);
            }
            double lowTotal = 0.0;
            for(int i = 0; i<lowRangeValues.size(); i++){
                lowTotal = lowTotal + lowRangeValues.get(i);
            }
            List<Double> highLowList = new ArrayList<>();
            highLowList.add(highTotal / highRangeValues.size());
            highLowList.add(lowTotal / lowRangeValues.size());
            return highLowList;
        }
        return null;
    }


    public double modelOpponentTeamPitching(int gameCount, MLBPlayer player, Date date, GameFinder gameFinder){
        TeamStatFetcher teamStatFetcher = new TeamStatFetcher();
        teamStatFetcher.setGameList(gameFinder.gameList);
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(2);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        //System.out.println("searching for most recent games before " + date + " for player: " + player.getFirstName() + " " + player.getLastName());
        int games = 0;
        Set<MLBPlayerGamePerformance> playerGamePerformanceSet = player.getPlayerGamePerformances();
        List<MLBPlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(playerGamePerformanceSet);
        playerGamePerformanceList.sort(Comparator.comparing(MLBPlayerGamePerformance::getDate).reversed());
        List<Integer> GameIDs = new ArrayList<>();
        for(int i = 0; i< playerGamePerformanceList.size(); i++){
            if(games>=gameCount){
                break;
            }
            if(playerGamePerformanceList.get(i).getDate().before(beforeDate) && playerGamePerformanceList.get(i).getAtbats()> 0){
                GameIDs.add(playerGamePerformanceList.get(i).getGameId());
                games++;
            }
        }
        List<Double> values = new ArrayList<>();
        for(int GameID : GameIDs){
            MLBTeam team = null;
            MLBGame game = gameFinder.findGameById(GameID);
            //System.out.println(GameID);
            for(MLBPlayer potentialOpponentPlayer:  game.getHomeMLBTeam().getFieldingPlayers()){
                if(potentialOpponentPlayer.getPlayerID() == player.getPlayerID()){
                    //TODO: HAD THESE BACKWARDS. BOTH WERE SET TO GET HOME TEAM.
                    team = game.getAwayMLBTeam();
                    break;
                }
            }
            for(MLBPlayer potentialOpponentPlayer:  game.getAwayMLBTeam().getFieldingPlayers()){
                if(potentialOpponentPlayer.getPlayerID() == player.getPlayerID()){
                    team = game.getHomeMLBTeam();
                    break;
                }
            }
            //RUN MODEL FOR SELECTED TEAM
                //for(MLBPitcher OpposingPlayer : team.getPitchingPlayers()){
            //if (playerPerformanceCache.get(tempPlayer.getPlayerID()) != null) {
                double opponentRunsGivenUp  = teamStatFetcher.getTeamRunsGivenUpPerGame(gameCount,team,game);
                //double opposingPlayerBlocksPerGame = playerStatFilter.getPlayerBlocksPerGameLastNGames(gameCount, OpposingPlayer, date);
            values.add(opponentRunsGivenUp);

            //System.out.println("midPoint: " + (highRange + lowRange)/2);


            //System.out.println("[BlockModel][Fetch] " + gameFinder.findGameById(cleanedID));
            //totalBlocks = totalBlocks + countOpposingBlocksInGame(gameFinder.findGameById(GameID), player);
        }
        if(GameIDs.size()>0) {
            double highTotal = 0.0;

            for(int i = 0; i<values.size(); i++){
                //for (Double highDouble : highRangeValues) {
                highTotal = highTotal + values.get(i);
            }

            return highTotal / values.size();
        }
        return 0;
    }
}
