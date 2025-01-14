package BaseballQuant.Threads;

import BaseballQuant.Model.*;
import BaseballQuant.Model.CacheObjects.CacheSettingsObject;
import BaseballQuant.Util.*;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

public class BackTestSingleThread extends Thread{
    private BackTestTimeThreadMonitor threadMonitor;
    private List<BackTestIngestObject> backTestIngestObjects;
    private List<BackTestResult> backTestResults;
    private HashMap<Integer,MLBGameOdds> mlbGameOddsHashMap;
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_ORANGE = "\033[38;5;214m";
    private boolean moreInfo = false;
    private ScrapingProxy scrapingProxy;
    private GameFinder gameFinder;
    private boolean newGames= false;
    private int threadNum;
    private TeamStatFetcher teamStatFetcher;
    private GameCalculatorClass gameCalculatorClass;
    private final HashMap<CacheSettingsObject, HashMap<Integer, BaseballQuant.Model.ScoreModel>> gameCache;
    private HashMap<LocalDate, List<MLBGame>> dayGameMap;
    private LocalDate localDate;
    //TIME VARS//
    private Instant start;
    private int gamesCompleted;
    private Instant now;
    private long delta;
    private float rate;
    private final DecimalFormat d = new DecimalFormat("#.##");
    private final int printEveryNGames = 1;
    private final int printEveryNResults = 300;
    PlayerStatFilter playerStatFilter;
    private final boolean verbose = false;
    private int testsComplete = 0;
    private boolean forward;
    double startingMoney;
    private int modifier = 0;
    private List<BackTestIngestObject> referenceList;
    private boolean caching;

    public BackTestSingleThread(  HashMap<Integer,MLBGameOdds> mlbGameOddsHashMap, List<MLBGame> gameList,int threadNum) {
        playerStatFilter = new PlayerStatFilter();
        backTestResults = new ArrayList<>();
        gameFinder = new GameFinder(gameList);
        teamStatFetcher = new TeamStatFetcher();
        teamStatFetcher.setGameList(gameList);
        this.mlbGameOddsHashMap = mlbGameOddsHashMap;
        gameCalculatorClass = new GameCalculatorClass();
        gameCalculatorClass.setGameFinder(gameFinder);
        gameCalculatorClass.setTeamStatFetcher(teamStatFetcher);
        gameCalculatorClass.setMoreInfo(moreInfo);
        gameCalculatorClass.setPlayerStatFetcher(new PlayerStatFetcher());
        gameCache = new HashMap<>();
        dayGameMap = new HashMap<>();
        gamesCompleted = 0;
        start = Instant.now();
        forward = false;
        this.threadNum = threadNum;
    }

    public boolean isCaching() {
        return caching;
    }
    public void setCaching(boolean caching) {
        this.caching = caching;
    }
    public void setBackTestIngestObjects(List<BackTestIngestObject> backTestIngestObjects) {
        this.backTestIngestObjects = backTestIngestObjects;
    }
    public void setThreadMonitor(BackTestTimeThreadMonitor threadMonitor) {
        this.threadMonitor = threadMonitor;
    }
    public double getStartingMoney() {
        return startingMoney;
    }
    public void setStartingMoney(double startingMoney) {
        this.startingMoney = startingMoney;
    }
    public boolean isForward() {
        return forward;
    }
    public void setForward(boolean forward) {
        this.forward = forward;
    }

    public void run(){
        System.out.println("start backtest");
        start = Instant.now();
        while(true) {
            if(backTestIngestObjects != referenceList) {
                for (BackTestIngestObject backTestIngestObject : backTestIngestObjects) {
                    BackTestResult backTestResult;
                    if (verbose) {
                        backTestResult = runBackTestVerbose(backTestIngestObject);
                    } else {
                        backTestResult = runBackTest(backTestIngestObject);
                    }
                    backTestResults.add(backTestResult);
                }
                //gameFinder.quitWebDriver();
                threadMonitor.ingestResults(backTestResults);
                backTestResults = new ArrayList<>();
                threadMonitor.threadFinished();
                referenceList = backTestIngestObjects;
                gamesCompleted = 0;
                start = Instant.now();
                testsComplete = 0;
            }
        }
    }

    public BackTestResult runBackTest(BackTestIngestObject backTestIngestObject){

//        System.out.println(backTestResult);

        return  null;
    }
    public BackTestResult runBackTestVerbose(BackTestIngestObject backTestIngestObject){
        return  null;
    }



    public static void mergeScores(ScoreModel scoreModel){
        scoreModel.setTotalHigh(scoreModel.getHomeHighPoints() + scoreModel.getAwayHighPoints());
        scoreModel.setTotalLow(scoreModel.getHomeLowPoints() + scoreModel.getAwayLowPoints());
    }

    public void addHomeTeamAdvantage(ScoreModel scoreModel, double homeTeamAdvantageLow, double homeTeamAdvantageHigh, boolean shortGame){
        homeTeamAdvantageLow = homeTeamAdvantageHigh;
        if(shortGame){
            scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() + (homeTeamAdvantageHigh * (7.0/9.0)));
            scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() + (homeTeamAdvantageLow * (7.0/9.0)));
        }else{
            scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() + (homeTeamAdvantageHigh));
            scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() + (homeTeamAdvantageLow));
        }

    }

    private void addStaticHomeTeamAdvantage(ScoreModel safeScoreModel, double homeAdvantageHigh, boolean shortenedMakeUpGame) {
//        if(shortenedMakeUpGame){
//            safeScoreModel.setHomePPG((float) (safeScoreModel.getHomePPG() + (homeAdvantageHigh * (7.0/9.0))));
//        }else{
//            safeScoreModel.setHomePPG((float) (safeScoreModel.getHomePPG() + (homeAdvantageHigh)));
 //       }
    }
    public void addStolenBasesToModel(ScoreModel scoreModel,  double highStolenBaseFactor, double lowStolenBaseFactor){
        scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() + (scoreModel.getHomeHighStolenBases() * highStolenBaseFactor));
        scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() + (scoreModel.getHomeLowStolenBases() * lowStolenBaseFactor));
        scoreModel.setAwayHighPoints(scoreModel.getAwayHighPoints() + (scoreModel.getAwayHighStolenBases() * highStolenBaseFactor));
        scoreModel.setAwayLowPoints(scoreModel.getAwayLowPoints() + (scoreModel.getAwayLowStolenBases() * lowStolenBaseFactor));
    }
//    public void factorStaticOpponentPitchingIntoScoringModel(ScoreModel scoreModel, double highRunFactor){
//        float homeRunsGivenDifferential = (float) (scoreModel.getHomeModelRunsGivenUpPerGame() - scoreModel.getAwayRGPG());
//        double awayRunsGivenDifferential = scoreModel.getAwayModelRunsGivenUpPerGameHigh() - scoreModel.getHomeRGPG();
//        scoreModel.setAwayPPG((float) (scoreModel.getAwayPPG() - ((homeRunsGivenDifferential * 1) * highRunFactor)));
//        scoreModel.setHomePPG((float) (scoreModel.getHomePPG() - ((awayRunsGivenDifferential * 1) * highRunFactor)));
//    }
    public void factorOpponentPitchingIntoScoringModel(ScoreModel scoreModel, MLBGame game, double runDifferentialFactor, double highRunFactor, double lowRunFactor){
        //FORECASTED DIFFERENCE IN BLOCKS BY HOME TEAM
        float homeBlockDifferentialHigh = scoreModel.getHomePitchingModelHigh() - scoreModel.getAwayRunsGivenUpHigh();
        float homeBlockDifferentialLow = scoreModel.getHomePitchingModelLow() - scoreModel.getAwayRunsGivenUpLow();

        double awayBlockDifferentialHigh = scoreModel.getAwayPitchingModelHigh() - scoreModel.getHomeRunsGivenUpHigh();
        double awayBlockDifferentialLow = scoreModel.getAwayPitchingModelLow() - scoreModel.getHomeRunsGivenUpLow();

        if(moreInfo) {
            StringBuilder stringBuilder = new StringBuilder();
            System.out.println("Before pitching Differential Range : " + (scoreModel.getHomeHighPoints() + scoreModel.getAwayHighPoints()) + " || " + (scoreModel.getHomeLowPoints() + scoreModel.getAwayLowPoints()));
            //BLOCKS FORECASTED BY HOME TEAM
            System.out.println("Home Pitching Range: " + scoreModel.getHomeRunsGivenUpHigh() + " || " + scoreModel.getHomeRunsGivenUpLow());
            System.out.println("This compares to Away Team's (" + game.getAwayMLBTeam().getTeamName() + ") prior scoring against pitching range of: " + scoreModel.getAwayPitchingModelHigh() + " || " + scoreModel.getAwayPitchingModelLow());
            //BLOCKS FORECASTED BY AWAY TEAM
            //double awayBlockMidPoint = (scoreModel.getAwayHighBlocks() + scoreModel.getAwayLowBlocks())/2;
            System.out.println("Away Pitching Range: " + scoreModel.getAwayRunsGivenUpHigh() + " || " + scoreModel.getAwayRunsGivenUpLow());
            System.out.println("This compares to Home Team's (" + game.getHomeMLBTeam().getTeamName() + ")prior scoring against pitching range of: " + scoreModel.getHomePitchingModelHigh() + " || " + scoreModel.getHomePitchingModelLow());
            stringBuilder.append("Home Team expects to play against a pitching range differential of: ").append(homeBlockDifferentialHigh).append(" || ").append(homeBlockDifferentialLow);
            stringBuilder.append("Away Team expects to play against a pitching range differential of: ").append(awayBlockDifferentialHigh).append(" || ").append(awayBlockDifferentialLow);
        }
        scoreModel.setAwayHighPoints(scoreModel.getAwayHighPoints() - ((homeBlockDifferentialHigh * runDifferentialFactor) * highRunFactor));
        scoreModel.setAwayLowPoints(scoreModel.getAwayLowPoints() - ((homeBlockDifferentialLow * runDifferentialFactor) * lowRunFactor));
        scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() - ((awayBlockDifferentialHigh * runDifferentialFactor) * highRunFactor));
        scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() - ((awayBlockDifferentialLow * runDifferentialFactor) * lowRunFactor));
    }

    public void factorOpponentFieldingIntoScoringModel(ScoreModel scoreModel, MLBGame game, double highFieldingFactor, double lowFieldingFactor){
        //FORECASTED DIFFERENCE IN BLOCKS BY HOME TEAM
        double homeFieldingDifferentialHigh = scoreModel.getHomeFieldingModelHigh() - scoreModel.getAwayFieldingHigh();
        double homeFieldingDifferentialLow = scoreModel.getHomeFieldingMoelLow() - scoreModel.getAwayFieldingLow();
        if(moreInfo) {
            StringBuilder stringBuilder = new StringBuilder();
            System.out.println("Before fielding Differential Range : " + (scoreModel.getHomeHighPoints() + scoreModel.getAwayHighPoints()) + " || " + (scoreModel.getHomeLowPoints() + scoreModel.getAwayLowPoints()));
            System.out.println("Before Away Fielding Differential Range : " + (scoreModel.getAwayHighPoints()) + " || " + (scoreModel.getAwayLowPoints()));
            //BLOCKS FORECASTED BY HOME TEAM
            System.out.println("Home Fielding Range: " + scoreModel.getHomeRunsGivenUpHigh() + " || " + scoreModel.getHomeRunsGivenUpLow());
            System.out.println("This compares to Away Team's (" + game.getAwayMLBTeam().getTeamName() + ") prior scoring against fielding range of: " + scoreModel.getAwayPitchingModelHigh() + " || " + scoreModel.getAwayPitchingModelLow());
            //BLOCKS FORECASTED BY AWAY TEAM
            //double awayBlockMidPoint = (scoreModel.getAwayHighBlocks() + scoreModel.getAwayLowBlocks())/2;
            System.out.println("Away Fielding Range: " + scoreModel.getAwayRunsGivenUpHigh() + " || " + scoreModel.getAwayRunsGivenUpLow());
            System.out.println("This compares to Home Team's (" + game.getHomeMLBTeam().getTeamName() + ")prior scoring against fielding range of: " + scoreModel.getHomePitchingModelHigh() + " || " + scoreModel.getHomePitchingModelLow());
            stringBuilder.append("Home Team expects to play against a fielding range differential of: ").append(homeFieldingDifferentialHigh).append(" || ").append(homeFieldingDifferentialLow).append("\n");
            stringBuilder.append("Away Team expects to play against a fielding range differential of: ").append(scoreModel.getAwayFieldingModelHigh() - scoreModel.getHomeFieldingHigh()).append(" || ").append(scoreModel.getAwayFieldingModelLow() - scoreModel.getHomeFieldingLow()).append("\n");
        }
        scoreModel.setAwayHighPoints(scoreModel.getAwayHighPoints() - (homeFieldingDifferentialLow * lowFieldingFactor));
        scoreModel.setAwayLowPoints(scoreModel.getAwayLowPoints() - ((homeFieldingDifferentialHigh * highFieldingFactor)));
        scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() - (((scoreModel.getAwayFieldingModelLow() - scoreModel.getHomeFieldingLow()) * lowFieldingFactor)));
        scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() - (((scoreModel.getAwayFieldingModelHigh() - scoreModel.getHomeFieldingHigh()) * highFieldingFactor)));
    }



    public void adjustForShortenedGame(ScoreModel scoreModel){
        scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() * (7.0/9.0));
        scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() * (7.0/9.0));
        scoreModel.setAwayHighPoints(scoreModel.getAwayHighPoints() * (7.0/9.0));
        scoreModel.setAwayLowPoints(scoreModel.getAwayLowPoints() * (7.0/9.0));
        scoreModel.setHomePitchingModelHigh(scoreModel.getHomePitchingModelHigh() * (float)(7.0/9.0));
        scoreModel.setHomePitchingModelLow(scoreModel.getHomePitchingModelLow() * (float)(7.0/9.0));
        scoreModel.setAwayPitchingModelHigh(scoreModel.getAwayPitchingModelHigh() * (float)(7.0/9.0));
        scoreModel.setAwayPitchingModelLow(scoreModel.getAwayPitchingModelLow() * (float)(7.0/9.0));
        scoreModel.setHomeRunsGivenUpHigh(scoreModel.getHomeRunsGivenUpHigh() * (float)(7.0/9.0));
        scoreModel.setHomeRunsGivenUpLow(scoreModel.getHomeRunsGivenUpLow() * (float)(7.0/9.0));
        scoreModel.setAwayRunsGivenUpHigh(scoreModel.getAwayRunsGivenUpHigh() * (float)(7.0/9.0));
        scoreModel.setAwayRunsGivenUpLow(scoreModel.getAwayRunsGivenUpLow() * (float)(7.0/9.0));
        scoreModel.setHomeFieldingModelHigh(scoreModel.getHomeFieldingModelHigh() * (7.0/9.0));
        scoreModel.setHomeFieldingMoelLow(scoreModel.getHomeFieldingMoelLow() * (7.0/9.0));
        scoreModel.setAwayFieldingModelHigh(scoreModel.getAwayFieldingModelHigh() * (7.0/9.0));
        scoreModel.setAwayFieldingModelLow(scoreModel.getAwayFieldingModelLow() * (7.0/9.0));
        scoreModel.setHomeFieldingHigh(scoreModel.getHomeFieldingHigh() * (7.0/9.0));
        scoreModel.setHomeFieldingLow(scoreModel.getHomeFieldingLow() * (7.0/9.0));
        scoreModel.setAwayFieldingHigh(scoreModel.getAwayFieldingHigh() * (7.0/9.0));
        scoreModel.setAwayFieldingLow(scoreModel.getAwayFieldingLow() * (7.0/9.0));
        scoreModel.setAwayHighStolenBases(scoreModel.getAwayHighStolenBases() * (7.0/9.0));
        scoreModel.setAwayLowStolenBases(scoreModel.getAwayLowStolenBases() * (7.0/9.0));
        scoreModel.setHomeHighStolenBases(scoreModel.getHomeHighStolenBases() * (7.0/9.0));
        scoreModel.setHomeLowStolenBases(scoreModel.getHomeLowStolenBases() * (7.0/9.0));
//        scoreModel.setHomePPG(scoreModel.getHomePPG() * (float)(7.0/9.0));
//        scoreModel.setAwayPPG(scoreModel.getAwayPPG() * (float)(7.0/9.0));
    }


    public double getProbability(double spreadOdds){
        double temp;
        if(spreadOdds>=100 || spreadOdds<=-100) {
            if (spreadOdds < 0) {
                spreadOdds = spreadOdds * -1;
                temp = (spreadOdds/(spreadOdds + 100));
            } else {
                temp = (100/(spreadOdds + 100));
            }
        }else{
            return (1/spreadOdds);
        }
        return temp;
    }

    public double getBetAmountFromSpreadOdds(double spreadOdds, double target){
        double decimalPayOut = applyWinningSpreadOdds(spreadOdds,0);
        if(decimalPayOut>=1){
            return target/decimalPayOut;
        }else{
            return target/decimalPayOut;
        }
    }
    public double applyWinningSpreadOdds(double spreadOdds, double modifier){

        if(spreadOdds>=100 || spreadOdds<=-100) {
            if (spreadOdds < 0) {
                return  1 - (100 / (spreadOdds));
            } else {
                return 1 + (spreadOdds / 100);
            }
        }else{
            return 1+(spreadOdds);
        }
    }


    public ScoreModel getScoreModelForGame(MLBGame game, BackTestIngestObject backTestIngestObject, Date date){
        ScoreModel scoreModel = new ScoreModel();
        ScoreModel safeScoreModel = new ScoreModel();
        boolean cacheSettingsFound = false;

        Iterator<Map.Entry<CacheSettingsObject, HashMap<Integer, BaseballQuant.Model.ScoreModel>>> it = gameCache.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<CacheSettingsObject, HashMap<Integer, BaseballQuant.Model.ScoreModel>> entry = it.next();
            if(entry.getKey().getBullpenLookback() == backTestIngestObject.getBullpenGameCount() &&
                    entry.getKey().getPlayerGameLookback() == backTestIngestObject.getGameCount() &&
                    entry.getKey().getPitcherGameLookback() == backTestIngestObject.getPitcherGameLookback() &&
                    entry.getKey().isDoubleSquareRoot() == backTestIngestObject.isDoubleSquareRoot()
                    //entry.getKey().isAllowBelowZero() == backTestIngestObject.isAllowLowEndBelowZero()
            ){
                cacheSettingsFound = true;
                if(entry.getValue().get(game.getGameId()) != null){
                    scoreModel = entry.getValue().get(game.getGameId());
                    mergeScores(scoreModel);
                    safeScoreModel = (ScoreModel) scoreModel.clone();
                    return safeScoreModel;
                }
            }
        }
        if(!cacheSettingsFound){
            //System.out.println("cache & settings object not found.");
            CacheSettingsObject cacheSettingsObject = new CacheSettingsObject();
            cacheSettingsObject.setBullpenLookback(backTestIngestObject.getBullpenGameCount());
            cacheSettingsObject.setPitcherGameLookback(backTestIngestObject.getPitcherGameLookback());
            cacheSettingsObject.setPlayerGameLookback(backTestIngestObject.getGameCount());
            cacheSettingsObject.setDoubleSquareRoot(backTestIngestObject.isDoubleSquareRoot());
            //cacheSettingsObject.setAllowBelowZero(backTestIngestObject.isAllowLowEndBelowZero());
            HashMap<Integer, ScoreModel> gameMap = new HashMap<>();
            MLBTeam awayTeam = (MLBTeam) game.getAwayMLBTeam().clone();
            awayTeam.setFieldingPlayers(gameFinder.getPlayerListFromPreviousGameFromDB(game, game.getAwayMLBTeam().getMlbId()));
            MLBTeam homeTeam = (MLBTeam) game.getHomeMLBTeam().clone();
            homeTeam.setFieldingPlayers(gameFinder.getPlayerListFromPreviousGameFromDB(game, game.getHomeMLBTeam().getMlbId()));
            gameCalculatorClass.tallyTeamBattingLastNGames(backTestIngestObject, date, playerStatFilter, awayTeam, scoreModel, false, game, false);
            gameCalculatorClass.tallyTeamBattingLastNGames(backTestIngestObject, date, playerStatFilter, homeTeam, scoreModel, true, game, false);
            mergeScores(scoreModel);
            safeScoreModel = (ScoreModel) scoreModel.clone();
            gameMap.put(game.getGameId(), (ScoreModel) scoreModel.clone());
            gameCache.put(cacheSettingsObject, gameMap);
        }else{
            HashMap<Integer, ScoreModel> gameMap = null;
            for (Map.Entry<CacheSettingsObject, HashMap<Integer, ScoreModel>> entry : gameCache.entrySet()) {
                if (entry.getKey().getBullpenLookback() == backTestIngestObject.getBullpenGameCount() &&
                        entry.getKey().getPlayerGameLookback() == backTestIngestObject.getGameCount() &&
                        entry.getKey().getPitcherGameLookback() == backTestIngestObject.getPitcherGameLookback() &&
                        entry.getKey().isDoubleSquareRoot() == backTestIngestObject.isDoubleSquareRoot()
                   //     entry.getKey().isAllowBelowZero() == backTestIngestObject.isAllowLowEndBelowZero()
                ) {
                    gameMap = entry.getValue();
                    break;
                }
            }
            MLBTeam awayTeam = (MLBTeam) game.getAwayMLBTeam().clone();
            awayTeam.setFieldingPlayers(gameFinder.getPlayerListFromPreviousGameFromDB(game, game.getAwayMLBTeam().getMlbId()));
            MLBTeam homeTeam = (MLBTeam) game.getHomeMLBTeam().clone();
            homeTeam.setFieldingPlayers(gameFinder.getPlayerListFromPreviousGameFromDB(game, game.getHomeMLBTeam().getMlbId()));
            gameCalculatorClass.tallyTeamBattingLastNGames(backTestIngestObject, date, playerStatFilter, awayTeam, scoreModel, false, game, false);
            gameCalculatorClass.tallyTeamBattingLastNGames(backTestIngestObject, date, playerStatFilter, homeTeam, scoreModel, true, game, false);
            mergeScores(scoreModel);
            safeScoreModel = (ScoreModel) scoreModel.clone();
            gameMap.put(game.getGameId(), (ScoreModel) scoreModel.clone());
        }
        return safeScoreModel;
    }

    private MLBGameOdds getGameOddsFromCache(int gameId){
        if(mlbGameOddsHashMap.get(gameId) != null){
            return  mlbGameOddsHashMap.get(gameId);
        }else{
            return null;
        }
    }
    private List<MLBGame> getGamesFromMap(LocalDate localDate){
        if(dayGameMap.get(localDate)!= null){
            return dayGameMap.get(localDate);
        }else{
            List<MLBGame> games = gameFinder.findGamesOnDateFromDB(localDate);
            dayGameMap.put(localDate, games);
            return games;
        }
    }


    public double getDailyVol(List<Double> splits){
        splits.removeIf(number -> number.isNaN());

        DoubleSummaryStatistics summaryStatistics = splits.stream().mapToDouble((x) -> x).summaryStatistics();
        double[] data = splits.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();

        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += Math.pow(data[i] - summaryStatistics.getAverage(), 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + Math.sqrt(variance));
        return Math.sqrt(variance);
    }

    private void squareRootTotal(ScoreModel safeScoreModel){
        float awayAmountToAddBack = (float)Math.sqrt(((safeScoreModel.getAwayHighPoints() + safeScoreModel.getAwayLowPoints()) / 2) - safeScoreModel.getAwayLowPoints());
        safeScoreModel.setAwayHighPoints(safeScoreModel.getAwayHighPoints() -  awayAmountToAddBack);
        safeScoreModel.setAwayLowPoints(safeScoreModel.getAwayLowPoints() + awayAmountToAddBack);
        float homeAmountToAddBack = (float)Math.sqrt(((safeScoreModel.getHomeHighPoints() + safeScoreModel.getHomeLowPoints()) / 2) - safeScoreModel.getHomeLowPoints());
        safeScoreModel.setHomeHighPoints(safeScoreModel.getHomeHighPoints() - homeAmountToAddBack);
        safeScoreModel.setHomeLowPoints(safeScoreModel.getHomeLowPoints() + homeAmountToAddBack);
    }

}
