package SportsQuant.Util;

import SportsQuant.Model.*;
import SportsQuant.Model.CacheObject.FractalRangeCacheObject;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class TeamStatFetcher {
    List<Game> gameList;
    private List<FractalRangeCacheObject> teamStatMap = new ArrayList<>();
    private List<FractalRangeCacheObject> teamPercentMap = new ArrayList<>();
    private List<FractalRangeCacheObject> opponentMap = new ArrayList<>();
    public TeamStatFetcher(){

    }

    public void setGameList(List<Game> gameList) {
        this.gameList = gameList;
    }

    public List<Game> getTeamsLastNGames(int gameCount, Team team, Date date){
        String teamName = team.getTeamName();

        //System.out.println("searching teamName: " + teamName);
        //List<MLBGame> GamesIn2021 = gameRepository.findAllByAwayTeamNameOrHomeTeamName(teamName, teamName);
        List<Game> GamesIn2021 = new ArrayList<>();
        for(int i = 0; i<gameList.size();i++){
            //for(MLBGame gameInList : gameList){
            try {
                if (gameList.get(i).getAwayTeamName().equals(teamName) || gameList.get(i).getHomeTeamName().equals(teamName)
                        || gameList.get(i).getHomeTeam().getTeamId() == team.getTeamId() || gameList.get(i).getAwayTeam().getTeamId() == team.getTeamId()) {
                    GamesIn2021.add(gameList.get(i));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        GamesIn2021.sort(Comparator.comparing(Game::getDate).reversed());
        int gamesCounted = 0;
        List<Game> listOfMostRecentGames = new ArrayList<>();
        for(int i = 0; i<GamesIn2021.size(); i++){
//        for(MLBGame game3: GamesIn2021){
            if(gamesCounted>=gameCount){
                break;
            }else{
                if(GamesIn2021.get(i).getDate().before(date)){
                    listOfMostRecentGames.add(GamesIn2021.get(i));
                    gamesCounted = gamesCounted + 1;
                }
            }
        }
        return listOfMostRecentGames;
    }


    public List<Double> getOpponentsBlocksPerGame(int gameCount, Team team, Date date){
        List<Game> games = getTeamsLastNGames(gameCount,team, date);
        List<Double> doubleList = new ArrayList<>();
        for(Game pastGame : games){
            Team opposingTeam;
            double blocksInGame = 0.0;
            if(pastGame.getAwayTeam().getTeamId() == team.getTeamId()){
                opposingTeam = pastGame.getHomeTeam();
            }else{
                opposingTeam = pastGame.getAwayTeam();
            }

            for(Player player : opposingTeam.getPlayers()){
                for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
                    if(playerGamePerformance.getGameID() == pastGame.getGameId()){
                        blocksInGame = blocksInGame + (double) playerGamePerformance.getBlocks();
                    }
                }
            }
            doubleList.add(blocksInGame);
        }
        return doubleList;
    }



public List<Double> getOpponentsTurnoversPerGame(int gameCount, Team team, Date date){
    List<Game> games = getTeamsLastNGames(gameCount,team, date);
    List<Double> doubleList = new ArrayList<>();
    for(Game pastGame : games){
        Team opposingTeam;
        double turnoversInGame = 0.0;
        if(pastGame.getAwayTeam().getTeamId() == team.getTeamId()){
            opposingTeam = pastGame.getHomeTeam();
        }else{
            opposingTeam = pastGame.getAwayTeam();
        }

        for(Player player : opposingTeam.getPlayers()){
            for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
                if(playerGamePerformance.getGameID() == pastGame.getGameId()){
                    turnoversInGame = turnoversInGame + (double) playerGamePerformance.getTurnovers();
                }
            }
        }
        doubleList.add(turnoversInGame);
    }
    return doubleList;
}

    public List<Double> getOpponentsFoulsPerGame(int gameCount, Team team, Date date){
        List<Game> games = getTeamsLastNGames(gameCount,team, date);
        List<Double> doubleList = new ArrayList<>();
        for(Game pastGame : games){
            Team opposingTeam;
            double foulsInGame = 0.0;
            if(pastGame.getAwayTeam().getTeamId() == team.getTeamId()){
                opposingTeam = pastGame.getHomeTeam();
            }else{
                opposingTeam = pastGame.getAwayTeam();
            }

            for(Player player : opposingTeam.getPlayers()){
                for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
                    if(playerGamePerformance.getGameID() == pastGame.getGameId()){
                        foulsInGame = foulsInGame + (double) playerGamePerformance.getFouls();
                    }
                }
            }
            doubleList.add(foulsInGame);
        }
        return doubleList;
    }

    public List<Double> getOpponentsStealsPerGame(int gameCount, Team team, Date date){
        List<Game> games = getTeamsLastNGames(gameCount,team, date);
        List<Double> doubleList = new ArrayList<>();
        for(Game pastGame : games){
            Team opposingTeam;
            double stealsInGame = 0.0;
            if(pastGame.getAwayTeam().getTeamId() == team.getTeamId()){
                opposingTeam = pastGame.getHomeTeam();
            }else{
                opposingTeam = pastGame.getAwayTeam();
            }

            for(Player player : opposingTeam.getPlayers()){
                for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
                    if(playerGamePerformance.getGameID() == pastGame.getGameId()){
                        stealsInGame = stealsInGame + (double) playerGamePerformance.getSteals();
                    }
                }
            }
            doubleList.add(stealsInGame);
        }
        return doubleList;
    }
    public List<Double> getOpponentsReboundsPerGame(int gameCount,  Team team, Date date,boolean defensive){
        List<Game> games = getTeamsLastNGames(gameCount,team, date);
        List<Double> doubleList = new ArrayList<>();
        for(Game pastGame : games){
            Team opposingTeam;
            double reboundsInGame = 0.0;
            if(pastGame.getAwayTeam().getTeamId() == team.getTeamId()){
                opposingTeam = pastGame.getHomeTeam();
            }else{
                opposingTeam = pastGame.getAwayTeam();
            }

            for(Player player : opposingTeam.getPlayers()){
                for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
                    if(playerGamePerformance.getGameID() == pastGame.getGameId()){
                        if(defensive) {
                            reboundsInGame = reboundsInGame + (double) playerGamePerformance.getReboundsDefensive();
                        }else{
                            reboundsInGame = reboundsInGame + (double) playerGamePerformance.getReboundsOffensive();
                        }
                    }
                }
            }
            doubleList.add(reboundsInGame);
        }
        return doubleList;
    }
    public List<Double> getTeamPointsPerGame(int gameCount, Team team, Date date){
        List<Game> games = getTeamsLastNGames(gameCount,team, date);
        List<Double> doubleList = new ArrayList<>();
        for(Game pastGame : games){
            //Team selectedTeam;
            if(pastGame.getAwayTeam().getTeamId() == team.getTeamId()){
                //selectedTeam = pastGame.getAwayTeam();
                doubleList.add((double) pastGame.getAwayPoints());
            }else {
                //selectedTeam = pastGame.getHomeTeam();
                doubleList.add((double) pastGame.getHomePoints());
            }

        }
        return doubleList;
    }
    public List<Double> getTeamThreePointPercentage(int gameCount, Team team, Date date){
        List<Game> games = getTeamsLastNGames(gameCount,team, date);
        List<Double> percentageList = new ArrayList<>();
        for(Game pastGame : games){
            Team selectedTeam;
            double threePointersMade = 0.0;
            double threePointersAttempted = 0.0;
            if(pastGame.getAwayTeam().getTeamId() == team.getTeamId()){
                selectedTeam = pastGame.getAwayTeam();
            }else{
                selectedTeam = pastGame.getHomeTeam();
            }
            for(Player player : selectedTeam.getPlayers()){
                for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
                    if(playerGamePerformance.getGameID() == pastGame.getGameId()){
                        threePointersMade = threePointersMade + (double) playerGamePerformance.getThreePointersMade();
                        threePointersAttempted = threePointersAttempted + (double) playerGamePerformance.getThreePointersAttempted();
                    }
                }
            }
            percentageList.add(threePointersMade/threePointersAttempted);
        }
        return percentageList;
    }
    public List<Double> getTeamFieldGoalsAttempted(int gameCount, Team team, Date date){
        List<Game> games = getTeamsLastNGames(gameCount,team, date);
        List<Double> fieldGoalsAttempted = new ArrayList<>();
        for(Game pastGame : games){
            Team selectedTeam;
            double attempts = 0.0;
            if(pastGame.getAwayTeam().getTeamId() == team.getTeamId()){
                selectedTeam = pastGame.getAwayTeam();
            }else{
                selectedTeam = pastGame.getHomeTeam();
            }
            List<Player> players = new ArrayList<>(selectedTeam.getPlayers());
            for(int j = 0; j<players.size(); j++){
                List<PlayerGamePerformance> performances = new ArrayList<>(players.get(j).getPlayerGamePerformances());
                for(int k = 0; k< performances.size(); k++){
                    if(performances.get(k).getGameID() == pastGame.getGameId()){
                        attempts = attempts + (double) performances.get(k).getFieldGoalsAttempted();
                    }
                }
            }
            fieldGoalsAttempted.add(attempts);
        }
        return fieldGoalsAttempted;
    }
    public List<Double> getTeamThreePointersAttempted(int gameCount, Team team, Date date){
        List<Game> games = getTeamsLastNGames(gameCount,team, date);
        List<Double> threePointersAttempted = new ArrayList<>();
        for(Game pastGame : games){
            Team selectedTeam;
            double attempts = 0.0;
            if(pastGame.getAwayTeam().getTeamId() == team.getTeamId()){
                selectedTeam = pastGame.getAwayTeam();
            }else{
                selectedTeam = pastGame.getHomeTeam();
            }
            for(Player player : selectedTeam.getPlayers()){
                for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
                    if(playerGamePerformance.getGameID() == pastGame.getGameId()){
                        attempts = attempts + (double) playerGamePerformance.getThreePointersAttempted();
                    }
                }
            }
            threePointersAttempted.add(attempts);
        }
        return threePointersAttempted;
    }
//    public SimpleHighLowPair getTeamStatRescaledRangeNew(Game game, int gameCount, int fractalWindow, Team team, Date date, String statType, double numHigh, double numLow, boolean home){
//        List<Game> games = getTeamsLastNGames(90, team, date);
//        List<Double> rawData = new ArrayList<>();
//        HashMap<Integer, Double> gameIdDoubleHashMap = new HashMap<>();
//            for (Game pastGame : games) {
//                boolean meetsCriteria = false;
//                Team selectedTeam = null;
//                double attempts = 0.0;
//                if (pastGame.getAwayTeam().getTeamId() == team.getTeamId()) {
//                    //if (!home) {
//                    selectedTeam = pastGame.getAwayTeam();
//                    //meetsCriteria = true;
//                    //}
//                } else {
//                    //if (home) {
//                    selectedTeam = pastGame.getHomeTeam();
//                    //meetsCriteria = true;
//                    //}
//                }
//                //if (meetsCriteria) {
//                for (Player player : selectedTeam.getPlayers()) {
//                    for (PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()) {
//                        if (playerGamePerformance.getGameID() == pastGame.getGameId()) {
//                            switch (statType) {
//                                case "fouls":
//                                    attempts = attempts + (double) playerGamePerformance.getFouls();
//                                    break;
//                                case "turnovers":
//                                    attempts = attempts + (double) playerGamePerformance.getTurnovers();
//                                    break;
//                                case "defensiveRebounds":
//                                    attempts = attempts + (double) playerGamePerformance.getReboundsDefensive();
//                                    break;
//                                case "offensiveRebounds":
//                                    attempts = attempts + (double) playerGamePerformance.getReboundsOffensive();
//                                    break;
//                                case "blocks":
//                                    attempts = attempts + (double) playerGamePerformance.getBlocks();
//                                    break;
//                                case "steals":
//                                    attempts = attempts + (double) playerGamePerformance.getSteals();
//                                    break;
//                                case "threePointAttempt":
//                                    attempts = attempts + (double) playerGamePerformance.getThreePointersAttempted();
//                                    break;
//                                case "fieldGoalAttempt":
//                                    attempts = attempts + (double) playerGamePerformance.getFieldGoalsAttempted();
//                                    break;
//                                case "freeThrowAttempt":
//                                    attempts = attempts + (double) playerGamePerformance.getFreeThrowsAttempted();
//                                    break;
//                            }
//
//                        }
//                    }
//                }
//                gameIdDoubleHashMap.put(pastGame.getGameId(), attempts);
//                rawData.add(attempts);
//                //}
//            }
//            List<Double> convertedData = convertListOfDoublesToPercentageChanges(rawData);
//
//
//        return new SimpleHighLowPair(topEndOfRange, lowEndOfRange);
//    }








    public SimpleHighLowPair getTeamStatRescaledRange(Game game, int gameCount, int fractalWindow, Team team, Date date, String statType, double numHigh, double numLow, boolean home){
        List<Game> games = getTeamsLastNGames(90, team, date);
        List<Double> rawData = new ArrayList<>();
        HashMap<Integer, Double> gameIdDoubleHashMap = new HashMap<>();
        FractalRangeCacheObject fractalRangeCacheObject = null;
        for(FractalRangeCacheObject entry : teamStatMap){
            if(entry.getGameId() == game.getGameId() && entry.getFractalWindow() == fractalWindow && entry.getStatType().equals(statType) && entry.getGameCount() == gameCount && entry.getTeamId() == team.getTeamId()){
                fractalRangeCacheObject = entry;
            }
        }
        if(fractalRangeCacheObject == null) {
            fractalRangeCacheObject = new FractalRangeCacheObject();
            for (Game pastGame : games) {
                boolean meetsCriteria = false;
                Team selectedTeam = null;
                double attempts = 0.0;
                if (pastGame.getAwayTeam().getTeamId() == team.getTeamId()) {
                    //if (!home) {
                        selectedTeam = pastGame.getAwayTeam();
                        //meetsCriteria = true;
                    //}
                } else {
                    //if (home) {
                        selectedTeam = pastGame.getHomeTeam();
                        //meetsCriteria = true;
                    //}
                }
                //if (meetsCriteria) {
                    for (Player player : selectedTeam.getPlayers()) {
                        for (PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()) {
                            if (playerGamePerformance.getGameID() == pastGame.getGameId()) {
                                switch (statType) {

                                    case "fouls":
                                        attempts = attempts + (double) playerGamePerformance.getFouls();
                                        break;
                                    case "turnovers":
                                        attempts = attempts + (double) playerGamePerformance.getTurnovers();
                                        break;
                                    case "defensiveRebounds":
                                        attempts = attempts + (double) playerGamePerformance.getReboundsDefensive();
                                        break;
                                    case "offensiveRebounds":
                                        attempts = attempts + (double) playerGamePerformance.getReboundsOffensive();
                                        break;
                                    case "blocks":
                                        attempts = attempts + (double) playerGamePerformance.getBlocks();
                                        break;
                                    case "steals":
                                        attempts = attempts + (double) playerGamePerformance.getSteals();
                                        break;
                                    case "threePointAttempt":
                                        attempts = attempts + (double) playerGamePerformance.getThreePointersAttempted();
                                        break;
                                    case "fieldGoalAttempt":
                                        attempts = attempts + (double) playerGamePerformance.getFieldGoalsAttempted();
                                        break;
                                    case "freeThrowAttempt":
                                        attempts = attempts + (double) playerGamePerformance.getFreeThrowsAttempted();
                                        break;
                                }

                            }
                        }
                    }
                    gameIdDoubleHashMap.put(pastGame.getGameId(), attempts);
                    rawData.add(attempts);
                //}
            }
            List<Double> convertedData = convertListOfDoublesToPercentageChanges(rawData);
            List<Double> rescaledRanges = new ArrayList<>();
            //int numberOfWindows = fractalWindow;
            int numberOfWindowsCompleted = 0;
            LocalDateTime localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            while (numberOfWindowsCompleted < fractalWindow) {
                Date beforeDate = Date.from(localDate.atZone(ZoneId.systemDefault()).minusHours(10).toInstant());
                Date afterDate = Date.from(localDate.atZone(ZoneId.systemDefault()).plusHours(10).toInstant());
                boolean newDate = false;

                for (int i = 0; i < games.size(); i++) {
                    Date gameDate = games.get(i).getDate();
                    if (gameDate.before(afterDate) && gameDate.after(beforeDate)) {
                        newDate = true;
                        break;
                    }
                }
                if (newDate) {
                    List<Double> threePointersAttempted = new ArrayList<>();
                    for (Game pastGame : games) {
                        if (threePointersAttempted.size() >= gameCount) {
                            break;
                        }
                        Date gameDate = pastGame.getDate();
                        if (gameDate.before(afterDate)) {
                            if (pastGame.getAwayTeam().getTeamId() == team.getTeamId()) {
                                if (!home) {
                                    threePointersAttempted.add(gameIdDoubleHashMap.get(pastGame.getGameId()));
                                }
                            } else {
                                if (home) {
                                    threePointersAttempted.add(gameIdDoubleHashMap.get(pastGame.getGameId()));
                                }
                            }

                        }

                    }
                    DoubleSummaryStatistics doubleSummaryStatistics = threePointersAttempted.stream().mapToDouble(x -> x).summaryStatistics();
                    List<Double> meanAdjustedList = new ArrayList<>();
                    for (Double num : threePointersAttempted) {
                        meanAdjustedList.add(num - doubleSummaryStatistics.getAverage());
                    }

                    List<Double> cumulativeDeviationSeries = new ArrayList<>();
                    double accumulator = 0.0;
                    for (Double num : meanAdjustedList) {
                        accumulator = accumulator + num;
                        cumulativeDeviationSeries.add(accumulator);
                    }
                    double max = Collections.max(cumulativeDeviationSeries);
                    double min = Collections.min(cumulativeDeviationSeries);


                    double[] data = threePointersAttempted.stream()
                            .mapToDouble(Double::doubleValue)
                            .toArray();
                    double perGame = threePointersAttempted.stream().mapToDouble((x) -> x).summaryStatistics().getAverage();
                    double variance = 0;
                    for (int i = 0; i < data.length; i++) {
                        variance += Math.pow(data[i] - perGame, 2);
                    }
                    variance /= data.length;
                    rescaledRanges.add((max - min) / Math.sqrt(variance));
                    numberOfWindowsCompleted++;
                }
                localDate = localDate.minusDays(1);
            }
            List<Double> mostRecentPeriod = new ArrayList<>();
            for (int i = 0; i < gameCount; i++) {
                mostRecentPeriod.add(convertedData.get(i));
            }
            double[] data = mostRecentPeriod.stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray();
            double perGame = mostRecentPeriod.stream().mapToDouble((x) -> x).summaryStatistics().getAverage();
            double variance = 0;
            for (int i = 0; i < data.length; i++) {
                variance += Math.pow(data[i] - perGame, 2);
            }
            variance /= data.length;
            double slope = (rawData.get(0) - rawData.get(gameCount - 1)) / (gameCount - 1);
            double maxAccumulator = -10000000.0;
            for (int i = 0; i < gameCount; i++) {
                double temp = rawData.get((gameCount - 1) - i) - (rawData.get(gameCount - 1) + (slope * i));
                if (temp > maxAccumulator) {
                    maxAccumulator = temp;
                }
            }
            double minAccumulator = 10000000.0;
            for (int i = 0; i < gameCount; i++) {
                double temp = rawData.get((gameCount - 1) - i) - (rawData.get(gameCount - 1) + (slope * i));
                if (temp < minAccumulator) {
                    minAccumulator = temp;
                }
            }
            DoubleSummaryStatistics rescaledRangeStats = rescaledRanges.stream().mapToDouble(x -> x).summaryStatistics();
            double average = rescaledRangeStats.getAverage();
            double topBridge = (maxAccumulator / (maxAccumulator - minAccumulator));
            double lowBridge = (minAccumulator / (maxAccumulator - minAccumulator));
            fractalRangeCacheObject.setFractalWindow(fractalWindow);
            fractalRangeCacheObject.setAverage(average);
            fractalRangeCacheObject.setVariance(variance);
            fractalRangeCacheObject.setTopBridge(topBridge);
            fractalRangeCacheObject.setBottomBridge(lowBridge);
            fractalRangeCacheObject.setGameId(game.getGameId());
            fractalRangeCacheObject.setStatType(statType);
            fractalRangeCacheObject.setStartingLevel(rawData.get(0));
            fractalRangeCacheObject.setGameCount(gameCount);
            fractalRangeCacheObject.setTeamId(team.getTeamId());
            teamStatMap.add(fractalRangeCacheObject);
        }
        double topEndOfRange = 0.0;
        double lowEndOfRange = 0.0;
        double highPowerFactor = 0.0;
        double lowPowerFactor = 0.0;
        switch(statType){
            case "fouls":
                highPowerFactor = 1;
                lowPowerFactor = 1;
                break;
            case "steals":
                highPowerFactor = 1;
                lowPowerFactor = 1;
                break;
            case "turnovers":
                highPowerFactor = 1;
                lowPowerFactor = 1;
                break;
            case "blocks":
                highPowerFactor = 1;
                lowPowerFactor = 1;
                break;
            case "defensiveRebounds":
                highPowerFactor = 1;
                lowPowerFactor = 1;
                break;
            case "offensiveRebounds":
                highPowerFactor = 1;
                lowPowerFactor = 1;
                break;
            case "freeThrowAttempt":
                highPowerFactor = 1;
                lowPowerFactor = 1;
                break;
            case "fieldGoalAttempt":
                highPowerFactor = 1;
                lowPowerFactor = 1;
                break;
            case "threePointAttempt":
                highPowerFactor = 1;
                lowPowerFactor = 1;
                break;
            default:
               highPowerFactor = 1;
               lowPowerFactor = 1;
        }
        double highTest = (Math.pow((fractalRangeCacheObject.getStartingLevel() * fractalRangeCacheObject.getAverage() * Math.sqrt(fractalRangeCacheObject.getVariance())),highPowerFactor) * fractalRangeCacheObject.getTopBridge());
        topEndOfRange = fractalRangeCacheObject.getStartingLevel() + Math.sqrt(highTest);

        double test = Math.pow((fractalRangeCacheObject.getStartingLevel() * fractalRangeCacheObject.getAverage() * Math.sqrt(fractalRangeCacheObject.getVariance())),lowPowerFactor) * fractalRangeCacheObject.getBottomBridge();
        if(test < 0){
            test = test * -1;
            test = Math.sqrt(test);
        }

        lowEndOfRange = fractalRangeCacheObject.getStartingLevel() - (test);


//        topEndOfRange = fractalRangeCacheObject.getStartingLevel() +
//                (Math.pow((fractalRangeCacheObject.getStartingLevel() * fractalRangeCacheObject.getAverage() * Math.sqrt(fractalRangeCacheObject.getVariance())),highPowerFactor) * fractalRangeCacheObject.getTopBridge());
//        lowEndOfRange = fractalRangeCacheObject.getStartingLevel() +
//                (Math.pow((fractalRangeCacheObject.getStartingLevel() * fractalRangeCacheObject.getAverage() * Math.sqrt(fractalRangeCacheObject.getVariance())),lowPowerFactor) * fractalRangeCacheObject.getBottomBridge());
        if(lowEndOfRange<0) {
            lowEndOfRange = 0;
        }

        return new SimpleHighLowPair(topEndOfRange, lowEndOfRange);
    }


    public SimpleHighLowPair getTeamStatPercentageRescaledRange(Game game, int gameCount, int fractalWindow, Team team, Date date, String statType, double numHigh, double numLow, boolean home){
        List<Game> games = getTeamsLastNGames(90, team, date);
        List<Double> rawData = new ArrayList<>();
        HashMap<Integer, Double> gameIdDoubleHashMap = new HashMap<>();
        FractalRangeCacheObject fractalRangeCacheObject = null;
        for(FractalRangeCacheObject entry : teamPercentMap){
            if(entry.getGameId() == game.getGameId() && entry.getFractalWindow() == fractalWindow && entry.getStatType().equals(statType) && entry.getGameCount() == gameCount && entry.getTeamId() == team.getTeamId()){
                fractalRangeCacheObject = entry;
            }
        }
        if(fractalRangeCacheObject == null) {
            fractalRangeCacheObject = new FractalRangeCacheObject();
            for (Game pastGame : games) {
                Team selectedTeam = null;
                boolean meetsCriteria = false;
                double attempts = 0.0;
                double successes = 0.0;
                if (pastGame.getAwayTeam().getTeamId() == team.getTeamId()) {
                    //if (!home) {
                        selectedTeam = pastGame.getAwayTeam();
                        //meetsCriteria = true;
                    //}
                } else {
                    //if (home) {
                        selectedTeam = pastGame.getHomeTeam();
                        //meetsCriteria = true;
                    //}
                }
                //if (meetsCriteria) {
                    for (Player player : selectedTeam.getPlayers()) {
                        for (PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()) {
                            if (playerGamePerformance.getGameID() == pastGame.getGameId()) {
                                switch (statType) {
                                    case "threePointPercentage":
                                        attempts = attempts + (double) playerGamePerformance.getThreePointersAttempted();
                                        successes = successes + (double) playerGamePerformance.getThreePointersMade();
                                        break;
                                    case "freeThrowPercentage":
                                        attempts = attempts + (double) playerGamePerformance.getFreeThrowsAttempted();
                                        successes = successes + (double) playerGamePerformance.getFreeThrowsMade();
                                        break;
                                    case "fieldGoalPercentage":
                                        attempts = attempts + (double) playerGamePerformance.getFieldGoalsAttempted();
                                        successes = successes + (double) playerGamePerformance.getFieldGoalsMade();
                                        break;
                                }

                            }
                        }
                    }
                    gameIdDoubleHashMap.put(pastGame.getGameId(), successes / attempts);
                    rawData.add(successes / attempts);
                //}
            }
            List<Double> convertedData = convertListOfDoublesToPercentageChanges(rawData);
            List<Double> rescaledRanges = new ArrayList<>();
            int numberOfWindows = fractalWindow;
            int numberOfWindowsCompleted = 0;
            LocalDateTime localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            while (numberOfWindowsCompleted < numberOfWindows) {
                Date beforeDate = Date.from(localDate.atZone(ZoneId.systemDefault()).minusHours(10).toInstant());
                Date afterDate = Date.from(localDate.atZone(ZoneId.systemDefault()).plusHours(10).toInstant());
                boolean newDate = false;

                for (int i = 0; i < games.size(); i++) {
                    Date gameDate = games.get(i).getDate();
                    if (gameDate.before(afterDate) && gameDate.after(beforeDate)) {
                        newDate = true;
                        break;
                    }
                }
                if (newDate) {
                    List<Double> threePointersAttempted = new ArrayList<>();
                    for (Game pastGame : games) {
                        if (threePointersAttempted.size() >= gameCount) {
                            break;
                        }
                        Date gameDate = pastGame.getDate();
                        if (gameDate.before(afterDate)) {
                            if (pastGame.getAwayTeam().getTeamId() == team.getTeamId()) {
                                if (!home) {
                                    threePointersAttempted.add(gameIdDoubleHashMap.get(pastGame.getGameId()));
                                }
                            } else {
                                if (home) {
                                    threePointersAttempted.add(gameIdDoubleHashMap.get(pastGame.getGameId()));
                                }
                            }

                        }

                    }
                    DoubleSummaryStatistics doubleSummaryStatistics = threePointersAttempted.stream().mapToDouble(x -> x).summaryStatistics();
                    List<Double> meanAdjustedList = new ArrayList<>();
                    for (Double num : threePointersAttempted) {
                        meanAdjustedList.add(num - doubleSummaryStatistics.getAverage());
                    }

                    List<Double> cumulativeDeviationSeries = new ArrayList<>();
                    double accumulator = 0.0;
                    for (Double num : meanAdjustedList) {
                        accumulator = accumulator + num;
                        cumulativeDeviationSeries.add(accumulator);
                    }
                    double max = Collections.max(cumulativeDeviationSeries);
                    double min = Collections.min(cumulativeDeviationSeries);


                    double[] data = threePointersAttempted.stream()
                            .mapToDouble(Double::doubleValue)
                            .toArray();
                    double perGame = threePointersAttempted.stream().mapToDouble((x) -> x).summaryStatistics().getAverage();
                    double variance = 0;
                    for (int i = 0; i < data.length; i++) {
                        variance += Math.pow(data[i] - perGame, 2);
                    }
                    variance /= data.length;
                    rescaledRanges.add((max - min) / Math.sqrt(variance));
                    numberOfWindowsCompleted++;
                }
                localDate = localDate.minusDays(1);
            }
            List<Double> mostRecentPeriod = new ArrayList<>();
            for (int i = 0; i < gameCount; i++) {
                mostRecentPeriod.add(rawData.get(i));
            }
            double[] data = mostRecentPeriod.stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray();
            double perGame = mostRecentPeriod.stream().mapToDouble((x) -> x).summaryStatistics().getAverage();
            double variance = 0;
            for (int i = 0; i < data.length; i++) {
                variance += Math.pow(data[i] - perGame, 2);
            }
            variance /= data.length;
            double slope = (rawData.get(0) - rawData.get(gameCount - 1)) / (gameCount - 1);
            double maxAccumulator = -10000000.0;
            for (int i = 0; i < gameCount; i++) {
                double temp = rawData.get((gameCount - 1) - i) - (rawData.get(gameCount - 1) + (slope * i));
                if (temp > maxAccumulator) {
                    maxAccumulator = temp;
                }
            }
            double minAccumulator = 10000000.0;
            for (int i = 0; i < gameCount; i++) {
                double temp = rawData.get((gameCount - 1) - i) - (rawData.get(gameCount - 1) + (slope * i));
                if (temp < minAccumulator) {
                    minAccumulator = temp;
                }
            }
            DoubleSummaryStatistics rescaledRangeStats = rescaledRanges.stream().mapToDouble(x -> x).summaryStatistics();
            double average = rescaledRangeStats.getAverage();
            double topBridge = (maxAccumulator / (maxAccumulator - minAccumulator));
            double lowBridge = (minAccumulator / (maxAccumulator - minAccumulator));
            fractalRangeCacheObject.setFractalWindow(fractalWindow);
            fractalRangeCacheObject.setAverage(average);
            fractalRangeCacheObject.setVariance(variance);
            fractalRangeCacheObject.setTopBridge(topBridge);
            fractalRangeCacheObject.setBottomBridge(lowBridge);
            fractalRangeCacheObject.setGameId(game.getGameId());
            fractalRangeCacheObject.setStatType(statType);
            fractalRangeCacheObject.setStartingLevel(rawData.get(0));
            fractalRangeCacheObject.setGameCount(gameCount);
            fractalRangeCacheObject.setTeamId(team.getTeamId());
            teamPercentMap.add(fractalRangeCacheObject);
        }
        double topEndOfRange = 0.0;
        double lowEndOfRange = 0.0;
        double highPowerFactor = 0.0;
        double lowPowerFactor = 0.0;
        double highTest = 0.0;
        double test = 0.0;
        switch(statType) {
            case "freeThrowPercentage":
                highPowerFactor = 1;
                lowPowerFactor = 1;
                highTest = (Math.pow((fractalRangeCacheObject.getStartingLevel() * fractalRangeCacheObject.getAverage() * Math.sqrt(fractalRangeCacheObject.getVariance())),highPowerFactor) * fractalRangeCacheObject.getTopBridge());
                topEndOfRange = fractalRangeCacheObject.getStartingLevel() + highTest;
                test = Math.pow((fractalRangeCacheObject.getStartingLevel() * fractalRangeCacheObject.getAverage() * Math.sqrt(fractalRangeCacheObject.getVariance())),lowPowerFactor) * fractalRangeCacheObject.getBottomBridge();
                if(test < 0){
                    test = test * -1;
                }
                lowEndOfRange = fractalRangeCacheObject.getStartingLevel() - test;
                if(topEndOfRange>1){
                    topEndOfRange = 1;
                }
                if(lowEndOfRange<0){
                    lowEndOfRange = 0.0;
                }
                break;
            case "fieldGoalPercentage":
                highPowerFactor = 1;
                lowPowerFactor = 1;
                highTest = (Math.pow((fractalRangeCacheObject.getStartingLevel() * fractalRangeCacheObject.getAverage() * Math.sqrt(fractalRangeCacheObject.getVariance())),highPowerFactor) * fractalRangeCacheObject.getTopBridge());
                topEndOfRange = fractalRangeCacheObject.getStartingLevel() + Math.sqrt(highTest);
                test = Math.pow((fractalRangeCacheObject.getStartingLevel() * fractalRangeCacheObject.getAverage() * Math.sqrt(fractalRangeCacheObject.getVariance())),lowPowerFactor) * fractalRangeCacheObject.getBottomBridge();
                if(test < 0){
                    test = Math.sqrt(test * -1);
                }
                lowEndOfRange = fractalRangeCacheObject.getStartingLevel() - test;
                if(topEndOfRange>1){
                    topEndOfRange = 1;
                }
                if(lowEndOfRange<0){
                    lowEndOfRange = 0.0;
                }
                break;
            case "threePointPercentage":
                highPowerFactor = 1;
                lowPowerFactor = 1;
                highTest = (Math.pow((fractalRangeCacheObject.getStartingLevel() * fractalRangeCacheObject.getAverage() * Math.sqrt(fractalRangeCacheObject.getVariance())),highPowerFactor) * fractalRangeCacheObject.getTopBridge());
                topEndOfRange = fractalRangeCacheObject.getStartingLevel() + Math.sqrt(highTest);
                test = Math.pow((fractalRangeCacheObject.getStartingLevel() * fractalRangeCacheObject.getAverage() * Math.sqrt(fractalRangeCacheObject.getVariance())),lowPowerFactor) * fractalRangeCacheObject.getBottomBridge();
                if(test < 0){
                    test = Math.sqrt(test * -1);
                }
                lowEndOfRange = fractalRangeCacheObject.getStartingLevel() - test;
                if(topEndOfRange>1){
                    topEndOfRange = 1;
                }
                if(lowEndOfRange<0){
                    lowEndOfRange = 0.0;
                }
                break;
            default:
                break;
        }
//        System.out.println("top end of Range = " + topEndOfRange);
//        System.out.println("low end of Range = " + lowEndOfRange);

        return new SimpleHighLowPair(topEndOfRange, lowEndOfRange);
    }
    public SimpleHighLowPair getOpponentStatRescaledRange(Game game, int gameCount, int fractalWindow, Team team, Date date, String statType, double numHigh, double numLow, boolean home){

        boolean opponentHomeStatus = !home;
        List<Double> allData = new ArrayList<>();
        List<Game> games = getTeamsLastNGames(gameCount, team, date);
        List<Double> rescaledRanges = new ArrayList<>();
        FractalRangeCacheObject fractalRangeCacheObject = null;
        for(FractalRangeCacheObject entry : opponentMap){
            if(entry.getGameId() == game.getGameId() && entry.getFractalWindow() == fractalWindow && entry.getStatType().equals(statType) && entry.getGameCount() == gameCount && entry.getTeamId() == team.getTeamId()){
                fractalRangeCacheObject = entry;
            }
        }
        if(fractalRangeCacheObject == null) {
            fractalRangeCacheObject = new FractalRangeCacheObject();
            for (Game pastGame : games) {
                List<Double> rawData = new ArrayList<>();
                HashMap<Integer, Double> gameIdDoubleHashMap = new HashMap<>();
                Team opposingTeam;
                if (pastGame.getAwayTeam().getTeamId() == team.getTeamId()) {
                    opposingTeam = pastGame.getHomeTeam();
                } else {
                    opposingTeam = pastGame.getAwayTeam();
                }
                List<Game> opposingTeamGames = getTeamsLastNGames(gameCount, opposingTeam, pastGame.getDate());
                for (Game opposingTeamPastGame : opposingTeamGames) {
                    Team opposingTeamPast;
                    double attempts = 0.0;
                    if (opposingTeamPastGame.getAwayTeam().getTeamId() == team.getTeamId()) {
                        opposingTeamPast = opposingTeamPastGame.getAwayTeam();
                    } else {
                        opposingTeamPast = opposingTeamPastGame.getHomeTeam();
                    }
                    for (Player player : opposingTeamPast.getPlayers()) {
                        for (PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()) {
                            if (playerGamePerformance.getGameID() == opposingTeamPastGame.getGameId()) {
                                switch (statType) {
                                    case "defensiveRebounds":
                                        attempts = attempts + (double) playerGamePerformance.getReboundsDefensive();
                                        break;
                                    case "offensiveRebounds":
                                        attempts = attempts + (double) playerGamePerformance.getReboundsOffensive();
                                        break;
                                    case "blocks":
                                        attempts = attempts + (double) playerGamePerformance.getBlocks();
                                        break;
                                    case "steals":
                                        attempts = attempts + (double) playerGamePerformance.getBlocks();
                                        break;
                                    case "fouls":
                                        attempts = attempts + (double) playerGamePerformance.getFouls();
                                        break;
                                    case "turnovers":
                                        attempts = attempts + (double) playerGamePerformance.getTurnovers();
                                        break;
                                }

                            }
                        }
                    }
                    gameIdDoubleHashMap.put(opposingTeamPastGame.getGameId(), attempts);
                    rawData.add(attempts);
                    allData.add(attempts);
                }


                int numberOfWindows = fractalWindow;
                //int numberOfWindowsCompleted = 0;
                LocalDateTime localDate = pastGame.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                //while (numberOfWindowsCompleted < numberOfWindows) {
                Date beforeDate = Date.from(localDate.atZone(ZoneId.systemDefault()).minusHours(10).toInstant());
                Date afterDate = Date.from(localDate.atZone(ZoneId.systemDefault()).plusHours(10).toInstant());
                boolean newDate = false;

//                for (int i = 0; i < games.size(); i++) {
//                    Date gameDate = opposingTeamGames.get(i).getDate();
//                    if (gameDate.before(afterDate) && gameDate.after(beforeDate)) {
//                        newDate = true;
//                        break;
//                    }
//                }
                //if (newDate) {
                List<Double> threePointersAttempted = new ArrayList<>();
                for (Game opposingTeamGame : opposingTeamGames) {
                    if (threePointersAttempted.size() >= gameCount) {
                        break;
                    }
                    Date gameDate = opposingTeamGame.getDate();
                    if (gameDate.before(afterDate)) {

                        threePointersAttempted.add(gameIdDoubleHashMap.get(opposingTeamGame.getGameId()));
                    }

                }
                DoubleSummaryStatistics doubleSummaryStatistics = threePointersAttempted.stream().mapToDouble(x -> x).summaryStatistics();
                List<Double> meanAdjustedList = new ArrayList<>();
                for (Double num : threePointersAttempted) {
                    meanAdjustedList.add(num - doubleSummaryStatistics.getAverage());
                }

                List<Double> cumulativeDeviationSeries = new ArrayList<>();
                double accumulator = 0.0;
                for (Double num : meanAdjustedList) {
                    accumulator = accumulator + num;
                    cumulativeDeviationSeries.add(accumulator);
                }
                double max = Collections.max(cumulativeDeviationSeries);
                double min = Collections.min(cumulativeDeviationSeries);


                double[] data = threePointersAttempted.stream()
                        .mapToDouble(Double::doubleValue)
                        .toArray();
                double perGame = threePointersAttempted.stream().mapToDouble((x) -> x).summaryStatistics().getAverage();
                double variance = 0;
                for (int i = 0; i < data.length; i++) {
                    variance += Math.pow(data[i] - perGame, 2);
                }
                variance /= data.length;
                rescaledRanges.add((max - min) / Math.sqrt(variance));
                // numberOfWindowsCompleted++;
                //}
                //localDate = localDate.minusDays(1);
                //}
            }
            List<Double> convertedData = convertListOfDoublesToPercentageChanges(allData);
            List<Double> mostRecentPeriod = new ArrayList<>();
            for (int i = 0; i < gameCount; i++) {
                mostRecentPeriod.add(convertedData.get(i));
            }
            double[] data = mostRecentPeriod.stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray();
            double perGame = mostRecentPeriod.stream().mapToDouble((x) -> x).summaryStatistics().getAverage();
            double variance = 0;
            for (int i = 0; i < data.length; i++) {
                variance += Math.pow(data[i] - perGame, 2);
            }
            variance /= data.length;
            double slope = (allData.get(0) - allData.get(gameCount - 1)) / (gameCount - 1);
            double maxAccumulator = -10000000.0;
            for (int i = 0; i < gameCount; i++) {
                double temp = allData.get((gameCount - 1) - i) - (allData.get(gameCount - 1) + (slope * i));
                if (temp > maxAccumulator) {
                    maxAccumulator = temp;
                }
            }
            double minAccumulator = 10000000.0;
            for (int i = 0; i < gameCount; i++) {
                double temp = allData.get((gameCount - 1) - i) - (allData.get(gameCount - 1) + (slope * i));
                if (temp < minAccumulator) {
                    minAccumulator = temp;
                }
            }
            DoubleSummaryStatistics rescaledRangeStats = rescaledRanges.stream().mapToDouble(x -> x).summaryStatistics();
            double average = rescaledRangeStats.getAverage();
            double topBridge = (maxAccumulator / (maxAccumulator - minAccumulator));
            double lowBridge = (minAccumulator / (maxAccumulator - minAccumulator));
            fractalRangeCacheObject.setFractalWindow(fractalWindow);
            fractalRangeCacheObject.setAverage(average);
            fractalRangeCacheObject.setVariance(variance);
            fractalRangeCacheObject.setTopBridge(topBridge);
            fractalRangeCacheObject.setBottomBridge(lowBridge);
            fractalRangeCacheObject.setGameId(game.getGameId());
            fractalRangeCacheObject.setStatType(statType);
            fractalRangeCacheObject.setStartingLevel(allData.get(0));
            fractalRangeCacheObject.setGameCount(gameCount);
            fractalRangeCacheObject.setTeamId(team.getTeamId());
            opponentMap.add(fractalRangeCacheObject);
        }
        double lowEndOfRange = 0.0;
        double topEndOfRange = 0.0;
        double highPowerFactor = 0.0;
        double lowPowerFactor = 0.0;
        switch(statType){
            case "fouls":
                highPowerFactor = 1;
                lowPowerFactor = 1;
                break;
            case "steals":
                highPowerFactor = 1;
                lowPowerFactor = 1;
                break;
            case "turnovers":
                highPowerFactor = 1;
                lowPowerFactor = 1;
                break;
            case "blocks":
                highPowerFactor = 1;
                lowPowerFactor = 1;
                break;
            case "defensiveRebounds":
                highPowerFactor = 1;
                lowPowerFactor = 1;
                break;
            case "offensiveRebounds":
                highPowerFactor = 1;
                lowPowerFactor = 1;
                break;
            case "freeThrowAttempt":
                highPowerFactor = 1;
                lowPowerFactor = 1;
                break;
            case "fieldGoalAttempt":
                highPowerFactor = 1;
                lowPowerFactor = 1;
                break;
            case "threePointAttempt":
                highPowerFactor = 1;
                lowPowerFactor = 1;
                break;
            default:
                highPowerFactor = 1;
                lowPowerFactor = 1;
        }
        double highTest = (Math.pow((fractalRangeCacheObject.getStartingLevel() * fractalRangeCacheObject.getAverage() * Math.sqrt(fractalRangeCacheObject.getVariance())),highPowerFactor) * fractalRangeCacheObject.getTopBridge());
        topEndOfRange = fractalRangeCacheObject.getStartingLevel() + Math.sqrt(highTest);

        double test = Math.pow((fractalRangeCacheObject.getStartingLevel() * fractalRangeCacheObject.getAverage() * Math.sqrt(fractalRangeCacheObject.getVariance())),lowPowerFactor) * fractalRangeCacheObject.getBottomBridge();
        if(test < 0){
            test = test * -1;
            test = Math.sqrt(test);
        }

        lowEndOfRange = fractalRangeCacheObject.getStartingLevel() - (test);


        if(lowEndOfRange<0){
            lowEndOfRange = 0;
        }
        if(Double.isNaN(topEndOfRange) || Double.isNaN(lowEndOfRange)){
            System.out.println("STOP HERE.");
        }

//        System.out.println("top end of Range = " + topEndOfRange);
//        System.out.println("low end of Range = " + lowEndOfRange);

        return new SimpleHighLowPair(topEndOfRange, lowEndOfRange);
    }



    public double getLogVariance(List<Double> barList, int dayOffset, int length){
        if(dayOffset + length < barList.size()) {
            List<Double> returnsBetweenBars = new ArrayList<>();
            for (int i = 0; i < length - 1; i++) {
                //  if(dayOffset + length + i + 1 < barList.size()) {
                double a = barList.get(dayOffset + i);
                if(dayOffset + i + 1>= barList.size()){
                    //System.out.println("stop!");
                }
                double b = barList.get(dayOffset + i + 1);

                double d = Math.log(a / b);

                //double d = (barList.get(barList.size() - dayOffset - i).getClose() - barList.get(barList.size() - dayOffset - i - 1).getClose())/barList.get(barList.size() - dayOffset - i - 1).getClose();

                //double d = (Math.log(barList.get(barList.size() - dayOffset - i).getClose()) - Math.log(barList.get(barList.size() - dayOffset - i - 1).getClose()));
                returnsBetweenBars.add(d);
                // }
            }
            DoubleSummaryStatistics doubleSummaryStatistics = returnsBetweenBars.stream().mapToDouble(x -> x).summaryStatistics();
            double variance = 0;
            for (int i = 0; i < returnsBetweenBars.size(); i++) {
                variance += Math.pow(returnsBetweenBars.get(i) - doubleSummaryStatistics.getAverage(), 2);
            }
            variance /= returnsBetweenBars.size();
            //System.out.println("variance: " + (variance * (252/length)/12) + " RV: " + (Math.sqrt(variance) * Math.sqrt(365)));
            if(Double.isNaN(100* (Math.sqrt(variance) * Math.sqrt(365)))){
                //System.out.println("stop!");
            }
            return 100* (Math.sqrt(variance) * Math.sqrt(365));
        }
        return 0.0;
    }

//    public double getHurst(List<Bar> bars, int dayOffset, int length){
//        if(dayOffset + length < bars.size()) {
//            double high = -1000000.0;
//            double low = 1000000.0;
//            for(int i = dayOffset; i < dayOffset + length; i++){
//                if(bars.get(i).getHigh() > high){
//                    high = bars.get(i).getHigh();
//                }
//                if(bars.get(i).getLow() < low){
//                    low = bars.get(i).getLow();
//                }
//            }
//            double a = Math.log(high - low);
//            double b = Math.log(bars.get(dayOffset).getAverageTrueRange());
//
//            return (a -  b )/ Math.log(length);
//        }else{
//            return 0.0;
//        }
//    }

    public double stdDev(List<Double> bars, int dayOffset, int length){
        if(dayOffset + length < bars.size()) {
            List<Double> prices = new ArrayList<>();

            for (int i = dayOffset; i < length + dayOffset; i++) {
                prices.add(bars.get(i));
            }
            DoubleSummaryStatistics doubleSummaryStatistics = prices.stream().mapToDouble(x -> x).summaryStatistics();
            double variance = 0;
            for (int i = 0; i < prices.size(); i++) {
                variance += Math.pow(prices.get(i) - doubleSummaryStatistics.getAverage(), 2);
            }
            variance /= prices.size();
            //System.out.println("Std Dev Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + Math.sqrt(variance));
            return Math.sqrt(variance);
        }else{
            return 0.0;
        }
    }

    public List<Double> convertListOfDoublesToPercentageChanges(List<Double> inputs){
        List<Double> percentageChanges = new ArrayList<>();
        for(int i = 1; i<inputs.size();i++){
            if(!Double.isInfinite(inputs.get(i)/inputs.get(i -1)) && inputs.get(i -1) != 0) {
                percentageChanges.add((inputs.get(i)/inputs.get(i -1)) - 1);
            }
        }
//        for(int i = 1; i<inputs.size();i++){
//            if(!Double.isInfinite(inputs.get(i)/inputs.get(i -1)) && inputs.get(i -1) != 0) {
//                percentageChanges.add(inputs.get(i)/inputs.get(i -1));
//            }
//        }
        return percentageChanges;
    }


    public double getGenericStdDev(List<Double> originalList){

        double[] data = originalList.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
        double perGame = originalList.stream().mapToDouble((x) -> x).summaryStatistics().getAverage();
        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += Math.pow(data[i] - perGame, 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + Math.sqrt(variance));
        return Math.sqrt(variance);
    }

}
