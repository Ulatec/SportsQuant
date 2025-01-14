package SportsQuant.Threads;

import BaseballQuant.Model.ClassResult;
import BaseballQuant.Model.MLBTeam;
import SportsQuant.Model.CacheObject.CacheSettingsObject;
import SportsQuant.Model.*;
import SportsQuant.Util.*;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.io.BufferedWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

public class BackTestThread extends Thread {

    private ThreadMonitor threadMonitor;


    //private BackTestWatcher backTestWatcher;
    private List<BackTestIngestObject> backTestIngestObjects;
    private HashMap<Integer,GameOdds> gameOddsHashMap;
    private List<BackTestResult> backTestResults;
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    private final boolean moreInfo = false;
    private GameFinder gameFinder;
    private boolean newGames= false;
    private int threadNum;
    private BufferedWriter bufferedWriter;
    private GameCalculatorClass gameCalculatorClass;
    private HashMap<Integer, HashMap<Integer, ScoreModel>> gameCache;
    private HashMap<LocalDate, List<Game>> dayGameMap;
    private int testsComplete = 0;
    private boolean forward;
    PlayerStatFilter playerStatFilter = new PlayerStatFilter();
    PlayerStatFetcher playerStatFetcher = new PlayerStatFetcher();
    private int modifier = 0;
    Instant start;
    Instant now;
    double delta;
    double rate;
    long gamesCompleted;
    private final int printEveryNGames = 1;
    private final int printEveryNResults = 25;
    private static final String ANSI_ORANGE = "\033[38;5;214m";
    private double startingMoney;
    private LocalDate localDate;
    private boolean verbose = false;
    private int gamesToTest;
    //PlayerStatFilter playerStatFilter;
    TeamStatFetcher teamStatFetcher;
    HashMap<Integer, ScoreModel> scoreModelHashMap = new HashMap<>();

    public BackTestThread(List<BackTestIngestObject> backTestIngestObjects, List<Game> gameList, ThreadMonitor threadMonitor) {
        this.backTestIngestObjects = backTestIngestObjects;
        this.threadMonitor = threadMonitor;
        backTestResults = new ArrayList<>();
        List<Game> newList = new ArrayList<>();
        for(int i = 0; i< gameList.size(); i++){
        //for(Game game: gameList){
            newList.add((Game) gameList.get(i).clone());
        }
        gameFinder = new GameFinder(newList);
        gameCalculatorClass = new GameCalculatorClass();
        gameCalculatorClass.setGameFinder(gameFinder);
        gameCalculatorClass.setPlayerStatFetcher(new PlayerStatFetcher());

        teamStatFetcher = new TeamStatFetcher();
        teamStatFetcher.setGameList(newList);
        gameCalculatorClass.setTeamStatFetcher(teamStatFetcher);
        forward = false;
        gameCache = new HashMap<>();
        start = Instant.now();
        dayGameMap = new HashMap<>();
    }

    public boolean isForward() {
        return forward;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }
//    public BackTestWatcher getBackTestWatcher() {
//        return backTestWatcher;
//    }
//
//    public void setBackTestWatcher(BackTestWatcher backTestWatcher) {
//        this.backTestWatcher = backTestWatcher;
//    }
    public List<BackTestIngestObject> getBackTestIngestObjects() {
        return backTestIngestObjects;
    }

    public void setBackTestIngestObjects(List<BackTestIngestObject> backTestIngestObjects) {
        this.backTestIngestObjects = backTestIngestObjects;
    }


    public HashMap<Integer, GameOdds> getGameOddsHashMap() {
        return gameOddsHashMap;
    }

    public void setGameOddsHashMap(HashMap<Integer, GameOdds> gameOddsHashMap) {
        this.gameOddsHashMap = gameOddsHashMap;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }


    public void run(){
        System.out.println("start backtest thread");
        start = Instant.now();
        //backTestWatcher.setTotalTestsToComplete(backTestIngestObjects.size());
        for(BackTestIngestObject backTestIngestObject : backTestIngestObjects){
            BackTestResult backTestResult;
            if(verbose){
                backTestResult = runBackTestVerbose(backTestIngestObject);
            }else{
                backTestResult = runBackTest(backTestIngestObject);
            }
            backTestResults.add(backTestResult);
        }
        //gameFinder.quitWebDriver();
        threadMonitor.ingestResults(backTestResults);
        threadMonitor.threadFinished();
        //backTestWatcher.setStatus("complete");
    }

    public BackTestResult runBackTest(BackTestIngestObject backTestIngestObject)  {
        startingMoney = 100;
        gamesToTest = backTestIngestObject.getGamesToTest();
        localDate = backTestIngestObject.getStartDate();
        int gameCount = backTestIngestObject.getPlayerGameLookBack();
        int correctPredictions = 0;
        int incorrectPredictions = 0;
        int totalGames = 0;
        int predictOver = 0;
        int predictUnder = 0;
        int actualOver = 0;
        int actualUnder = 0;
        int exactMatch = 0;
        int exactLosses = 0;
        double totalPredictedPoints = 0;
        double totalActualPoints = 0;
        int ppgCorrect = 0;
        int ppgIncorrect = 0;
        double betPercent = 0.05;
        while(totalGames < gamesToTest + 200){
            List<Game> games = getGamesFromMap(localDate);
            for(int i = 0; i < games.size(); i++) {
                Game game = games.get(i);
                ScoreModel scoreModel = (ScoreModel) getScoreModelForGame(game, backTestIngestObject, game.getDate()).clone();
                scoreModelHashMap.put(game.getGameId(), scoreModel);
                totalGames++;
            }
                localDate = localDate.minusDays(1);
        }
        totalGames = 0;
        localDate = backTestIngestObject.getStartDate();



//        HashMap<LocalDate, Double> AllPointCorrelation = new HashMap<>();
//        HashMap<LocalDate, Double> AllReboundCorrelation = new HashMap<>();
//        HashMap<LocalDate, Double> AllBlocksCorrelation = new HashMap<>();
//        HashMap<LocalDate, Double> AllStealsCorrelation = new HashMap<>();
//        HashMap<LocalDate, Double> AllTurnoversCorrelation = new HashMap<>();
//        while(totalGames < gamesToTest+100){
//            List<Game> gameBase = new ArrayList<>();
//            LocalDate tempDate = localDate;
//            while(gameBase.size() < backTestIngestObject.getFractalWindow()){
//                gameBase.addAll(getGamesFromMap(tempDate));
//                tempDate = tempDate.minusDays(1);
//            }
//            PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
//            List<Double> PointRocs = new ArrayList<>();
//            List<Double> ReboundRocs = new ArrayList<>();
//            List<Double> outcomes = new ArrayList<>();
//            List<Double> BlocksRocs = new ArrayList<>();
//            List<Double> StealsRocs = new ArrayList<>();
//            List<Double> TurnoversRocs = new ArrayList<>();
//            for(Game game : gameBase){
//                PointRocs.add(scoreModelHashMap.get(game.getGameId()).getHomePointsPerGameRoc());
//                PointRocs.add(scoreModelHashMap.get(game.getGameId()).getAwayPointsPerGameRoc());
//                ReboundRocs.add(scoreModelHashMap.get(game.getGameId()).getHomeReboundsPerGameRoc());
//                ReboundRocs.add(scoreModelHashMap.get(game.getGameId()).getAwayReboundsPerGameRoc());
//                BlocksRocs.add(scoreModelHashMap.get(game.getGameId()).getHomeReboundsPerGameRoc());
//                BlocksRocs.add(scoreModelHashMap.get(game.getGameId()).getAwayReboundsPerGameRoc());
//                StealsRocs.add(scoreModelHashMap.get(game.getGameId()).getHomeStealsPerGameRoc());
//                StealsRocs.add(scoreModelHashMap.get(game.getGameId()).getAwayStealsPerGameRoc());
//                TurnoversRocs.add(scoreModelHashMap.get(game.getGameId()).getHomeTurnoversPerGameRoc());
//                TurnoversRocs.add(scoreModelHashMap.get(game.getGameId()).getAwayTurnoversPerGameRoc());
//                if(game.getHomePoints() > game.getAwayPoints()){
//                    outcomes.add(1.0);
//                    outcomes.add((double) 0);
//                }else{
//                    outcomes.add((double) 0);
//                    outcomes.add(1.0);
//                }
//            }
//            AllPointCorrelation.put(localDate, pearsonsCorrelation.correlation(convertListToArray(PointRocs),convertListToArray(outcomes)));
//            AllReboundCorrelation.put(localDate, pearsonsCorrelation.correlation(convertListToArray(ReboundRocs),convertListToArray(outcomes)));
//            AllBlocksCorrelation.put(localDate, pearsonsCorrelation.correlation(convertListToArray(BlocksRocs),convertListToArray(outcomes)));
//            AllStealsCorrelation.put(localDate, pearsonsCorrelation.correlation(convertListToArray(StealsRocs),convertListToArray(outcomes)));
//            AllTurnoversCorrelation.put(localDate, pearsonsCorrelation.correlation(convertListToArray(TurnoversRocs),convertListToArray(outcomes)));
//            totalGames = totalGames + getGamesFromMap(localDate).size();
//
//                localDate = localDate.minusDays(1);
//
//        }
        totalGames = 0;
        localDate = backTestIngestObject.getStartDate();
        while(totalGames < gamesToTest) {
            float dailyNetMoney = 0.0f;
            List<Game> games = getGamesFromMap(localDate);

            //System.out.println(games);
            double betPerGame = startingMoney * betPercent;
            for (int i = 0; i<games.size(); i++) {
                Game game = (Game) games.get(i).clone();
                ScoreModel scoreModel = scoreModelHashMap.get(game.getGameId());


                //mergeScores(scoreModel);
                //System.out.println("Score only range: " + scoreModel.getTotalHigh() + " || " + scoreModel.getTotalLow());
//                if(backTestIngestObject.isSquareRootTotalPoints()){
//                    squareRootTotalPoints(scoreModel);
//                }
              //  StaticScoreModelUtils.convertAndAssignMadeBasketsFromPercentages(scoreModel);
              //      StaticScoreModelUtils.factorBlocksIntoScoringModel(scoreModel, backTestIngestObject.getPointsReducedPerBlock(),
               //             backTestIngestObject.getLowerBlockPointFactor(), backTestIngestObject.getHighBlockPointFactor());
                    //factorAverageBlocksIntoScoringModel(scoreModel, backTestIngestObject.getPointsReducedPerBlock());
               //     StaticScoreModelUtils.factorStealsIntoScoringModel(scoreModel, backTestIngestObject.getPointReductionPerSteal(),
               //             backTestIngestObject.getLowerStealPointFactor(), backTestIngestObject.getHighStealPointFactor());

               // StaticScoreModelUtils.factorTurnoversIntoScoringModel(scoreModel, backTestIngestObject.getPointsReducedPerTurnover(),
               //             backTestIngestObject.getLowerTurnoverPointFactor(), backTestIngestObject.getHighTurnoverPointFactor());
               // StaticScoreModelUtils.factorDefensiveReboundsIntoScoringModel(scoreModel, backTestIngestObject.getPointsReducedPerRebound(),
               //         backTestIngestObject.getLowerReboundPointFactor(), backTestIngestObject.getHighReboundPointFactor(), moreInfo);
               // StaticScoreModelUtils.factorOffensiveReboundsIntoScoringModel(scoreModel, backTestIngestObject.getHighReboundPointFactor());
               // StaticScoreModelUtils.factorFoulsIntoScoringModel(scoreModel, backTestIngestObject.getPointsReducedPerFoul(),
                //        backTestIngestObject.getLowerFoulPointFactor(), backTestIngestObject.getHighFoulPointFactor());

               // StaticScoreModelUtils.sumFieldGoalsAndFreeThrowsIntoPoints(scoreModel);
                //mergeScores(scoreModel);

                if(backTestIngestObject.getBetType().equals("moneyline")){
                    GameOdds gameOdds = gameOddsHashMap.get(game.getGameId());
                    if(gameOdds != null) {
                        //StaticScoreModelUtils.addHomeTeamAdvantage(scoreModel,backTestIngestObject.getHomeTeamAdvantage());
                        //mergeScores(scoreModel);
                        totalPredictedPoints = totalPredictedPoints + ((scoreModel.getTotalHigh() + scoreModel.getTotalLow()) / 2);
                        totalActualPoints = totalActualPoints + (game.getAwayPoints() + game.getHomePoints());
                        totalGames = totalGames + 1;
                        gamesCompleted = gamesCompleted + 1;
                        float homePredicted = (float) ((scoreModel.getHomeHighPoints() + scoreModel.getHomeLowPoints()) / 2);
                        float awayPredicted = (float) ((scoreModel.getAwayHighPoints() + scoreModel.getAwayLowPoints()) / 2);
                        //System.out.println("calculated awayPercent = " + awayPercentAboveHome);
                        float pointDifference = homePredicted - awayPredicted;
                        if(!(localDate.getDayOfMonth() < 30 && localDate.getMonthValue()==10)) {
                            //if (pointDifference <= backTestIngestObject.getPointThreshold() * -1 || pointDifference >= backTestIngestObject.getPointThreshold()) {
                            double topEndDifference = scoreModel.getHomeHighPoints() - scoreModel.getAwayHighPoints();
                            double lowEndDifference = scoreModel.getHomeLowPoints() - scoreModel.getAwayLowPoints();
                            double totalDifferential = 0.0;
                            if (topEndDifference > 0) {
                                totalDifferential = totalDifferential + topEndDifference;
                            } else {
                                totalDifferential = totalDifferential + (topEndDifference * -1);
                            }
                            if (lowEndDifference > 0) {
                                totalDifferential = totalDifferential + lowEndDifference;
                            } else {
                                totalDifferential = totalDifferential + (lowEndDifference * -1);
                            }
                            //    double homeShare = (topEndDifference + lowEndDifference) / totalDifferential;
//                            double homeImpliedPercentage = 0.5 + (homeShare*0.5); double homeImpliedPercentage = 0.5 + (homeShare*0.5);
                            //   double marketOdds = getProbability(gameOdds.getHomeTeamMoneyLine());

                            double homeTally = 0.0;
                            double awayTally = 0.0;

                            //if(scoreModel.getAwayBlockPerGame() > scoreModel.getHomeBlockPerGame()) {
                            awayTally += backTestIngestObject.getHighBlockPointFactor() * scoreModel.getAwayBlockPerGame();
//                            //}else{
                            homeTally += backTestIngestObject.getHighBlockPointFactor() * scoreModel.getHomeBlockPerGame();
//
                            awayTally += backTestIngestObject.getLowerBlockPointFactor() * scoreModel.getAwayBlocksPerGameRoc();
//                            //}else{
                           homeTally += backTestIngestObject.getLowerBlockPointFactor() * scoreModel.getHomeBlocksPerGameRoc();
//                            //}
//                            //if(scoreModel.getAwayFoulPerGame() < scoreModel.getHomeFoulPerGame()){
//                            //  awayTally += backTestIngestObject.getLowerBlockPointFactor() * scoreModel.getAwayFoulsPerGameRoc();
//                            //}else{
//                            //  homeTally += backTestIngestObject.getLowerBlockPointFactor() * scoreModel.getAwayBlocksPerGameRoc();
//                            // }
////                            if(scoreModel.getAwayStealPerGame() > scoreModel.getHomeStealPerGame()){
                            awayTally += backTestIngestObject.getHighStealPointFactor() * scoreModel.getAwayStealPerGame();
////                            }else{
                            homeTally += backTestIngestObject.getHighStealPointFactor() * scoreModel.getHomeStealPerGame();
                           awayTally += backTestIngestObject.getLowerStealPointFactor() * scoreModel.getAwayStealsPerGameRoc();
////                            }else{
                            homeTally += backTestIngestObject.getLowerStealPointFactor() * scoreModel.getHomeStealsPerGameRoc();
//
//
////                            }
////                            if(scoreModel.getAwayTurnoverPerGame() < scoreModel.getHomeTurnoverPerGame()){
////                                awayTally += backTestIngestObject.getLowerStealPointFactor();
////                            }else{
////                                homeTally += backTestIngestObject.getLowerStealPointFactor();
////                            }
////                            if(scoreModel.getAwayPointsPerGame() > scoreModel.getHomePointsPerGame()){
//                            awayTally += backTestIngestObject.getHighTurnoverPointFactor() * scoreModel.getAwayTurnoverPerGame();
////                            }else{
//                            homeTally += backTestIngestObject.getHighTurnoverPointFactor() * scoreModel.getHomeTurnoverPerGame();
//
//                            awayTally += backTestIngestObject.getLowerTurnoverPointFactor() * scoreModel.getAwayTurnoversPerGameRoc();
////                            }else{
//                            homeTally += backTestIngestObject.getLowerTurnoverPointFactor() * scoreModel.getHomeTurnoversPerGameRoc();
////                            }
////                            if(scoreModel.getAwayReboundPerGame() > scoreModel.getHomeReboundPerGame()){
//                            awayTally += backTestIngestObject.getHighReboundPointFactor() * scoreModel.getAwayReboundPerGame();
////                            }else{
//                            homeTally += backTestIngestObject.getHighReboundPointFactor() * scoreModel.getHomeReboundPerGame();
////                            }
//                            awayTally += backTestIngestObject.getLowerReboundPointFactor() * scoreModel.getAwayReboundsPerGameRoc();
////                            }else{
//                            homeTally += backTestIngestObject.getLowerReboundPointFactor() * scoreModel.getHomeReboundsPerGameRoc();
//
//                            awayTally += backTestIngestObject.getHighFoulPointFactor() * scoreModel.getAwayFoulPerGame();
//                            homeTally += backTestIngestObject.getHighFoulPointFactor() * scoreModel.getHomeFoulPerGame();
//                            awayTally += backTestIngestObject.getLowerFoulPointFactor() * scoreModel.getAwayFoulsPerGameRoc();
//                            homeTally += backTestIngestObject.getLowerFoulPointFactor() * scoreModel.getHomeFoulsPerGameRoc();
//
                           awayTally += backTestIngestObject.getHomeTeamAdvantage() * scoreModel.getAwayPointsPerGame();
                            homeTally += backTestIngestObject.getHomeTeamAdvantage() * scoreModel.getHomePointsPerGame();
//
                            awayTally += backTestIngestObject.getPointThreshold() * scoreModel.getAwayPointsPerGameRoc();
                            homeTally += backTestIngestObject.getPointThreshold() * scoreModel.getHomePointsPerGameRoc();
//
//                            awayTally += backTestIngestObject.getPointsReducedPerBlock() * scoreModel.getAwayFreeThrowsPerGame();
//                            homeTally += backTestIngestObject.getPointsReducedPerBlock() * scoreModel.getHomeFreeThrowsPerGame();
//
//                            awayTally += backTestIngestObject.getPointReductionPerSteal() * scoreModel.getAwayFreeThrowAttemptsPerGameRoc();
//                            homeTally += backTestIngestObject.getPointReductionPerSteal() * scoreModel.getHomeFreeThrowAttemptsPerGameRoc();

                            awayTally += backTestIngestObject.getPointvolweight() * scoreModel.getAwayWinProbRoc();
                            homeTally += backTestIngestObject.getPointvolweight() * scoreModel.getHomeWinProbRoc();
                            boolean home = false;
                            awayTally += backTestIngestObject.getHighFoulPointFactor() *  getProbability(gameOdds.getAwayTeamSpreadOdds());
                            homeTally += backTestIngestObject.getHighFoulPointFactor() * getProbability(gameOdds.getHomeTeamSpreadOdds());
                            if(homeTally > awayTally){
                           // if (homeWinProb > awayWinProb) {
                               // if(scoreModel.getHomeWinProbRoc() > 0 && scoreModel.getAwayWinProbRoc() < 0){

                                    //  if(scoreModel.getHomeReboundsPerGameRoc() > backTestIngestObject.getHighBlockPointFactor()) {
                                    //        if (scoreModel.getHomeStealsPerGameRoc() > backTestIngestObject.getHighStealPointFactor()) {
                                    home = true;
                                    //    System.out.println(game.getDate() + " Bet Home Moneyline " + game.getHomeTeamName() + " vs " + game.getAwayTeamName() );
                                    if (game.getHomePoints() > game.getAwayPoints()) {
                                        correctPredictions = correctPredictions + 1;
                                        //splitCorrect = splitCorrect + 1;
                                        dailyNetMoney = dailyNetMoney + (float) ((getBetAmountFromSpreadOdds(gameOdds.getHomeTeamMoneyLine(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getHomeTeamMoneyLine(), modifier) - 1)));
                                    } else {

                                        incorrectPredictions = incorrectPredictions + 1;
                                        //splitIncorrect = splitIncorrect + 1;
                                        dailyNetMoney = dailyNetMoney - (float) getBetAmountFromSpreadOdds(gameOdds.getHomeTeamMoneyLine(), betPerGame);
                                        //            }
                                        //         }
                                    }
                                }else{
                                    //                                    if (game.getHomePoints() < game.getAwayPoints()) {
                                    if (game.getHomePoints() < game.getAwayPoints()) {
                                        correctPredictions = correctPredictions + 1;
                                        //splitCorrect = splitCorrect + 1;
                                        dailyNetMoney = dailyNetMoney + (float) ((getBetAmountFromSpreadOdds(gameOdds.getAwayTeamMoneyLine(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getAwayTeamMoneyLine(), modifier) - 1)));
                                    } else {

                                        incorrectPredictions = incorrectPredictions + 1;
                                        //splitIncorrect = splitIncorrect + 1;
                                        dailyNetMoney = dailyNetMoney - (float) getBetAmountFromSpreadOdds(gameOdds.getAwayTeamMoneyLine(), betPerGame);
                                    }

                            //    }
                            }
                        }

//                        if(!home) {
//                            if (scoreModel.getAwayPointsPerGameRoc() > backTestIngestObject.getHomeTeamAdvantage()) {
//                                if (scoreModel.getAwayReboundsPerGameRoc() > backTestIngestObject.getHighBlockPointFactor()) {
//                                    //  System.out.println(game.getDate() + " Bet Home Moneyline " + game.getHomeTeamName() + " vs " + game.getAwayTeamName() );
//                                    if (game.getHomePoints() < game.getAwayPoints()) {
//                                        correctPredictions = correctPredictions + 1;
//                                        //splitCorrect = splitCorrect + 1;
//                                        dailyNetMoney = dailyNetMoney + (float) ((getBetAmountFromSpreadOdds(gameOdds.getAwayTeamMoneyLine(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getAwayTeamMoneyLine(), modifier) - 1)));
//                                    } else {
//
//                                        incorrectPredictions = incorrectPredictions + 1;
//                                        //splitIncorrect = splitIncorrect + 1;
//                                        dailyNetMoney = dailyNetMoney - (float) getBetAmountFromSpreadOdds(gameOdds.getAwayTeamMoneyLine(), betPerGame);
//                                    }
//                                }
//                            }
//                        }
                       // }
                    }
                } else if (backTestIngestObject.getBetType().equals("spread")) {
                    GameOdds gameOdds = gameOddsHashMap.get(game.getGameId());
                    if(gameOdds != null) {
                        StaticScoreModelUtils.addHomeTeamAdvantage(scoreModel, backTestIngestObject.getHomeTeamAdvantage());
                        mergeScores(scoreModel);
                        totalPredictedPoints = totalPredictedPoints + ((scoreModel.getTotalHigh() + scoreModel.getTotalLow())/2);
                        totalActualPoints = totalActualPoints + (game.getAwayPoints() + game.getHomePoints());
                        float HomePredictedPoints = (float)(scoreModel.getHomeHighPoints() + scoreModel.getHomeLowPoints()) / 2;
                        float AwayPredictedPoints = (float)(scoreModel.getAwayHighPoints() + scoreModel.getAwayLowPoints()) / 2;
                        float pointDifference = HomePredictedPoints - AwayPredictedPoints;
                        int actualDifference = game.getHomePoints() - game.getAwayPoints();
                        float pointClass =  (pointDifference - ((float)gameOdds.getHomeTeamSpread()*-1));
                        if(pointClass<= backTestIngestObject.getPointThreshold() * -1 || pointClass >= backTestIngestObject.getPointThreshold()) {
                            if (HomePredictedPoints > AwayPredictedPoints) {
                                double invertedHomeSpread = gameOdds.getHomeTeamSpread() * -1;
                                if (pointDifference >= invertedHomeSpread) {
                                    if (actualDifference > invertedHomeSpread) {
                                        correctPredictions = correctPredictions + 1;
                                        dailyNetMoney = (float) (dailyNetMoney + ((getBetAmountFromSpreadOdds(gameOdds.getHomeTeamSpreadOdds(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getHomeTeamSpreadOdds(), modifier) - 1))));
                                    } else {
                                        incorrectPredictions = incorrectPredictions + 1;
                                        dailyNetMoney = (float) (dailyNetMoney - getBetAmountFromSpreadOdds(gameOdds.getHomeTeamSpreadOdds(), betPerGame));
                                    }
                                } else {
                                    if ((actualDifference * -1) > invertedHomeSpread * -1) {
                                        dailyNetMoney = (float) (dailyNetMoney + (getBetAmountFromSpreadOdds(gameOdds.getAwayTeamSpreadOdds(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getAwayTeamSpreadOdds(), modifier) - 1)));
                                        correctPredictions = correctPredictions + 1;
                                    } else {
                                        incorrectPredictions = incorrectPredictions + 1;
                                        dailyNetMoney = dailyNetMoney - (float) getBetAmountFromSpreadOdds(gameOdds.getAwayTeamSpreadOdds(), betPerGame);
                                    }
                                }
                            } else {
                                double invertedAwaySpread = gameOdds.getAwayTeamSpread() * -1;
                                if ((pointDifference * -1) >= invertedAwaySpread) {
                                    if ((actualDifference * -1) > invertedAwaySpread) {
                                        correctPredictions = correctPredictions + 1;
                                        dailyNetMoney = (float) (dailyNetMoney + ((getBetAmountFromSpreadOdds(gameOdds.getAwayTeamSpreadOdds(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getAwayTeamSpreadOdds(), modifier) - 1))));
                                    } else {
                                        incorrectPredictions = incorrectPredictions + 1;
                                        dailyNetMoney = dailyNetMoney - (float) getBetAmountFromSpreadOdds(gameOdds.getAwayTeamSpreadOdds(), betPerGame);
                                    }
                                } else {
                                    if (actualDifference > invertedAwaySpread * -1) {
                                        correctPredictions = correctPredictions + 1;
                                        dailyNetMoney = (float) (dailyNetMoney + (getBetAmountFromSpreadOdds(gameOdds.getHomeTeamSpreadOdds(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getHomeTeamSpreadOdds(), modifier) - 1)));
                                    } else {
                                        incorrectPredictions = incorrectPredictions + 1;
                                        dailyNetMoney = (dailyNetMoney - (float) getBetAmountFromSpreadOdds(gameOdds.getHomeTeamSpreadOdds(), betPerGame));
                                    }
                                }
                            }

                        }
                        totalGames = totalGames + 1;
                        gamesCompleted = gamesCompleted + 1;
                    }
                }
                else if(backTestIngestObject.getBetType().equals("overunder")) {
                        if (gameOddsHashMap.get(game.getGameId()) != null) {
                            GameOdds gameOdds = gameOddsHashMap.get(game.getGameId());
                            //StaticScoreModelUtils.addHomeTeamAdvantage(scoreModel, backTestIngestObject.getHomeTeamAdvantage());
                            mergeScores(scoreModel);
                            totalActualPoints = totalActualPoints + (game.getHomePoints() + game.getAwayPoints());
                            double predictedPoints = (scoreModel.getTotalHigh() + scoreModel.getTotalLow()) / 2;
                            totalPredictedPoints = totalPredictedPoints + (predictedPoints);
                            int actualPoints = game.getHomePoints() + game.getAwayPoints();
                            if(game.getHomePoints() + game.getAwayPoints() != gameOdds.getOverUnder()){

                                double homeTally = 0.0;
                                double awayTally = 0.0;

                                //if(scoreModel.getAwayBlockPerGame() > scoreModel.getHomeBlockPerGame()) {
                                awayTally += backTestIngestObject.getHighBlockPointFactor() * scoreModel.getAwayBlockPerGame();
                                //}else{
                                homeTally += backTestIngestObject.getHighBlockPointFactor() * scoreModel.getHomeBlockPerGame();

                                awayTally += backTestIngestObject.getLowerBlockPointFactor() * scoreModel.getAwayBlocksPerGameRoc();
                                //}else{
                                homeTally += backTestIngestObject.getLowerBlockPointFactor() * scoreModel.getHomeBlocksPerGameRoc();
                                //}
                                //if(scoreModel.getAwayFoulPerGame() < scoreModel.getHomeFoulPerGame()){
                                //  awayTally += backTestIngestObject.getLowerBlockPointFactor() * scoreModel.getAwayFoulsPerGameRoc();
                                //}else{
                                //  homeTally += backTestIngestObject.getLowerBlockPointFactor() * scoreModel.getAwayBlocksPerGameRoc();
                                // }
//                            if(scoreModel.getAwayStealPerGame() > scoreModel.getHomeStealPerGame()){
                                awayTally += backTestIngestObject.getHighStealPointFactor() * scoreModel.getAwayStealPerGame();
//                            }else{
                                homeTally += backTestIngestObject.getHighStealPointFactor() * scoreModel.getHomeStealPerGame();
                                awayTally += backTestIngestObject.getLowerStealPointFactor() * scoreModel.getAwayStealsPerGameRoc();
//                            }else{
                                homeTally += backTestIngestObject.getLowerStealPointFactor() * scoreModel.getHomeStealsPerGameRoc();


//                            }
//                            if(scoreModel.getAwayTurnoverPerGame() < scoreModel.getHomeTurnoverPerGame()){
//                                awayTally += backTestIngestObject.getLowerStealPointFactor();
//                            }else{
//                                homeTally += backTestIngestObject.getLowerStealPointFactor();
//                            }
//                            if(scoreModel.getAwayPointsPerGame() > scoreModel.getHomePointsPerGame()){
                                awayTally += backTestIngestObject.getHighTurnoverPointFactor() * scoreModel.getAwayTurnoverPerGame();
//                            }else{
                                homeTally += backTestIngestObject.getHighTurnoverPointFactor() * scoreModel.getHomeTurnoverPerGame();

                                awayTally += backTestIngestObject.getLowerTurnoverPointFactor() * scoreModel.getAwayTurnoversPerGameRoc();
//                            }else{
                                homeTally += backTestIngestObject.getLowerTurnoverPointFactor() * scoreModel.getHomeTurnoversPerGameRoc();
//                            }
//                            if(scoreModel.getAwayReboundPerGame() > scoreModel.getHomeReboundPerGame()){
                                awayTally += backTestIngestObject.getHighReboundPointFactor() * scoreModel.getAwayReboundPerGame();
//                            }else{
                                homeTally += backTestIngestObject.getHighReboundPointFactor() * scoreModel.getHomeReboundPerGame();
//                            }
                                awayTally += backTestIngestObject.getLowerReboundPointFactor() * scoreModel.getAwayReboundsPerGameRoc();
//                            }else{
                                homeTally += backTestIngestObject.getLowerReboundPointFactor() * scoreModel.getHomeReboundsPerGameRoc();

                                awayTally += backTestIngestObject.getHighFoulPointFactor() * scoreModel.getAwayFoulPerGame();
                                homeTally += backTestIngestObject.getHighFoulPointFactor() * scoreModel.getHomeFoulPerGame();
                                awayTally += backTestIngestObject.getLowerFoulPointFactor() * scoreModel.getAwayFoulsPerGameRoc();
                                homeTally += backTestIngestObject.getLowerFoulPointFactor() * scoreModel.getHomeFoulsPerGameRoc();

                                awayTally += backTestIngestObject.getHomeTeamAdvantage() * scoreModel.getAwayPointsPerGame();
                                homeTally += backTestIngestObject.getHomeTeamAdvantage() * scoreModel.getHomePointsPerGame();

                                awayTally += backTestIngestObject.getPointThreshold() * scoreModel.getAwayPointsPerGameRoc();
                                homeTally += backTestIngestObject.getPointThreshold() * scoreModel.getHomePointsPerGameRoc();

                                awayTally += backTestIngestObject.getPointsReducedPerBlock() * scoreModel.getAwayFreeThrowsPerGame();
                                homeTally += backTestIngestObject.getPointsReducedPerBlock() * scoreModel.getHomeFreeThrowsPerGame();

                                awayTally += backTestIngestObject.getPointReductionPerSteal() * scoreModel.getAwayFreeThrowAttemptsPerGameRoc();
                                homeTally += backTestIngestObject.getPointReductionPerSteal() * scoreModel.getHomeFreeThrowAttemptsPerGameRoc();

                                awayTally += backTestIngestObject.getPointvolweight() * scoreModel.getAwayPointsScoredVol();
                                homeTally += backTestIngestObject.getPointvolweight() * scoreModel.getHomePointsScoredVol();

                                awayTally += backTestIngestObject.getGameTimeThreshold() * scoreModel.getAwayOverUnderRoc();
                                homeTally += backTestIngestObject.getGameTimeThreshold() * scoreModel.getHomeOverUnderRoc();

                              //  double pointClass = predictedPoints - gameOdds.getOverUnder();
                               // if (pointClass <= backTestIngestObject.getPointThreshold() * -1 || pointClass >= backTestIngestObject.getPointThreshold()) {
                                    if (homeTally + awayTally > 0) {
                                        actualOver = actualOver + 1;
                                        if (actualPoints > gameOdds.getOverUnder()) {
                                            correctPredictions = correctPredictions + 1;
                                            double winnings = (float) ((getBetAmountFromSpreadOdds(-115, betPerGame) * (applyWinningSpreadOdds(-115, modifier) - 1)));
                                            dailyNetMoney = dailyNetMoney + (float) winnings;
                                        } else {
                                            incorrectPredictions = incorrectPredictions + 1;
                                            double losings = (float) getBetAmountFromSpreadOdds(-115, betPerGame);
                                            dailyNetMoney = (float) (dailyNetMoney - losings);
                                        }
                                    } else {
                                        actualUnder = actualUnder + 1;
                                        if (actualPoints < gameOdds.getOverUnder()) {
                                            correctPredictions = correctPredictions + 1;
                                            double winnings = (float) ((getBetAmountFromSpreadOdds(-115, betPerGame) * (applyWinningSpreadOdds(-115, modifier) - 1)));
                                            dailyNetMoney = dailyNetMoney + (float) winnings;
                                        } else {
                                            incorrectPredictions = incorrectPredictions + 1;
                                            double losings = (float) getBetAmountFromSpreadOdds(-115, betPerGame);
                                            dailyNetMoney = (float) (dailyNetMoney - losings);
                                        }
                                    }
                                }
                           // }
                            totalGames = totalGames + 1;
                            gamesCompleted = gamesCompleted + 1;
//                            MathContext m = new MathContext(4);
//
//                            String progressString = ANSI_GREEN + "CORRECT PREDICTIONS: " + correctPredictions + " || " + "INCORRECT PREDICTIONS: " + incorrectPredictions + " (" + (double) correctPredictions / (double) (correctPredictions + incorrectPredictions) + "%) EXACT MATCH: " + exactMatch;
//                            progressString = progressString + ANSI_RESET;
//
//                            System.out.println(progressString);
//                            System.out.println(ANSI_GREEN + "SETTINGS: Lookback: " + gameCount + " ptsReduction: " + backTestIngestObject.getPointsReducedPerBlock() +
//                                    " ptsReductionPerSteal: " + backTestIngestObject.getPointReductionPerSteal() +
//                                    " ptsReducedPerTO: " + backTestIngestObject.getPointsReducedPerTurnover() +
//                                    " lowBlockFactor: " + backTestIngestObject.getLowerBlockPointFactor() + " highBlk: " + backTestIngestObject.getHighBlockPointFactor() +
//                                    " lowStl: " + backTestIngestObject.getLowerStealPointFactor() + "highStl: " + backTestIngestObject.getHighStealPointFactor() +
//                                    " dblsqrt: " + backTestIngestObject.isDoubleSquareRoot() +
//                                    " modelOpponentBlocks: " + backTestIngestObject.isModelOpponentBlocks() +
//                                    " modelOpponentSteals: " + backTestIngestObject.isModelOpponentSteals() +
//                                    " gameTimeThreshold: " + backTestIngestObject.getGameTimeThreshold() + ANSI_RESET);
//                            //System.out.println(ANSI_GREEN + "PPG CORRECT PREDICTIONS: " + ppgCorrect + " || " + "PPG INCORRECT PREDICTIONS: " + ppgIncorrect + "(" + b2.round(m).doubleValue() + "%)" + ANSI_RESET);
//                            System.out.println(ANSI_GREEN + "PREDICTIONS OVER: " + predictOver + " PREDICTIONS UNDER: " + predictUnder + " || ACTUAL OVER: " + actualOver + " ACTUAL UNDER: " + actualUnder + " || TotalPredictedPoints: " + totalPredictedPoints + " TotalActualPoints: " + totalActualPoints + " (" + ((totalPredictedPoints - totalActualPoints) / (totalActualPoints) * 100) + "%)" + ANSI_RESET);
//                            //System.out.println(ANSI_GREEN + backTestResults + ANSI_RESET);
//                            System.out.println(ANSI_GREEN + "[" + threadNum + "] " + "[" + (backTestResults.size() + 1) + "/" + backTestIngestObjects.size() + "]" + ANSI_RESET);
                        }

                }
            }
            if(forward){
                localDate = localDate.plusDays(1);
            }else{
                localDate = localDate.minusDays(1);
            }
            startingMoney = startingMoney + dailyNetMoney;
        }
        testsComplete = testsComplete + 1;
        printProgress();
        return MiscSharedUtils.buildBackTestResult(backTestIngestObject,correctPredictions, incorrectPredictions,startingMoney, totalActualPoints,totalPredictedPoints, exactMatch);
    }


    public BackTestResult runBackTestVerbose(BackTestIngestObject backTestIngestObject)  {
        startingMoney = 100;
        int gamesToTest = backTestIngestObject.getGamesToTest();
        localDate = backTestIngestObject.getStartDate();
        int gameCount = backTestIngestObject.getPlayerGameLookBack();
        int correctPredictions = 0;
        int incorrectPredictions = 0;
        int totalGames = 0;
        int predictOver = 0;
        int predictUnder = 0;
        int actualOver = 0;
        int actualUnder = 0;
        int exactMatch = 0;
        double totalPredictedPoints = 0;
        double totalActualPoints = 0;
        int ppgCorrect = 0;
        int ppgIncorrect = 0;
        double betPercent = 0.10;
        while(totalGames < gamesToTest) {
            float dailyNetMoney = 0.0f;
            List<Game> games = getGamesFromMap(localDate);

            //System.out.println(games);
            double betPerGame = startingMoney * betPercent;
            for (int i = 0; i<games.size(); i++) {
                Game game = (Game) games.get(i).clone();
                ScoreModel scoreModel = (ScoreModel) getScoreModelForGame(game,backTestIngestObject, game.getDate()).clone();


                mergeScores(scoreModel);
                StaticScoreModelUtils.convertAndAssignMadeBasketsFromPercentages(scoreModel);
                if(backTestIngestObject.isModelOpponentBlocks()){
                    StaticScoreModelUtils.factorBlocksIntoScoringModel(scoreModel, backTestIngestObject.getPointsReducedPerBlock(),
                            backTestIngestObject.getLowerBlockPointFactor(), backTestIngestObject.getHighBlockPointFactor());
                    //factorAverageBlocksIntoScoringModel(scoreModel, backTestIngestObject.getPointsReducedPerBlock());
                }
                if(backTestIngestObject.isModelOpponentSteals()){
                    StaticScoreModelUtils.factorStealsIntoScoringModel(scoreModel, backTestIngestObject.getPointReductionPerSteal(),
                            backTestIngestObject.getLowerStealPointFactor(), backTestIngestObject.getHighStealPointFactor());
                }
                if(backTestIngestObject.isModelOpponentTurnovers()){
                    StaticScoreModelUtils.factorTurnoversIntoScoringModel(scoreModel, backTestIngestObject.getPointsReducedPerTurnover(),
                            backTestIngestObject.getLowerTurnoverPointFactor(), backTestIngestObject.getHighTurnoverPointFactor());
                }
                StaticScoreModelUtils.factorDefensiveReboundsIntoScoringModel(scoreModel, backTestIngestObject.getPointsReducedPerRebound(),
                        backTestIngestObject.getLowerReboundPointFactor(), backTestIngestObject.getHighReboundPointFactor(), moreInfo);
                StaticScoreModelUtils.factorOffensiveReboundsIntoScoringModel(scoreModel, backTestIngestObject.getHighReboundPointFactor());
                StaticScoreModelUtils.factorFoulsIntoScoringModel(scoreModel, backTestIngestObject.getPointsReducedPerFoul(),
                        backTestIngestObject.getLowerFoulPointFactor(), backTestIngestObject.getHighFoulPointFactor());

                StaticScoreModelUtils.sumFieldGoalsAndFreeThrowsIntoPoints(scoreModel);
                System.out.println(game.getHomeTeamName() + " vs " + game.getAwayTeamName() + " " + game.getDate());
                if(backTestIngestObject.getBetType().equals("overunder")){
                    if(gameOddsHashMap.get(game.getGameId()) != null) {
                        GameOdds gameOdds = gameOddsHashMap.get(game.getGameId());
                        StaticScoreModelUtils.addHomeTeamAdvantage(scoreModel, backTestIngestObject.getHomeTeamAdvantage());
                        mergeScores(scoreModel);

                        totalActualPoints = totalActualPoints + (game.getHomePoints() + game.getAwayPoints());
                        double predictedPoints = (scoreModel.getTotalHigh() + scoreModel.getTotalLow()) / 2;
                        totalPredictedPoints = totalPredictedPoints + ((scoreModel.getTotalHigh() + scoreModel.getTotalLow()) / 2);
                        System.out.println("O/U:" + gameOdds.getOverUnder());
                        System.out.println("Actual Score: " + (game.getHomePoints() + game.getAwayPoints()));

                        System.out.println("predicted points : " + predictedPoints);
                        int actualPoints = game.getHomePoints() + game.getAwayPoints();
                        if(game.getHomePoints() + game.getAwayPoints() != gameOdds.getOverUnder()){
                            double pointClass = predictedPoints - gameOdds.getOverUnder();
                            if (pointClass <= backTestIngestObject.getPointThreshold() * -1 || pointClass >= backTestIngestObject.getPointThreshold()) {
                                if (predictedPoints > gameOdds.getOverUnder()) {
                                    if (actualPoints > gameOdds.getOverUnder()) {
                                        actualOver = actualOver + 1;
                                        correctPredictions = correctPredictions + 1;
                                        double winnings = (float) ((getBetAmountFromSpreadOdds(-105, betPerGame) * (applyWinningSpreadOdds(-105, modifier) - 1)));
                                        dailyNetMoney = dailyNetMoney + (float) winnings;
                                    } else {
                                        actualUnder = actualUnder + 1;
                                        incorrectPredictions = incorrectPredictions + 1;
                                        double losings = (float) getBetAmountFromSpreadOdds(-105, betPerGame);
                                        dailyNetMoney = (float) (dailyNetMoney - losings);
                                    }
                                } else {
                                    if (actualPoints < gameOdds.getOverUnder()) {
                                        actualUnder = actualUnder + 1;
                                        correctPredictions = correctPredictions + 1;
                                        double winnings = (float) ((getBetAmountFromSpreadOdds(-105, betPerGame) * (applyWinningSpreadOdds(-105, modifier) - 1)));
                                        dailyNetMoney = dailyNetMoney + (float) winnings;
                                    } else {
                                        actualOver = actualOver + 1;
                                        incorrectPredictions = incorrectPredictions + 1;
                                        double losings = (float) getBetAmountFromSpreadOdds(-105, betPerGame);
                                        dailyNetMoney = (float) (dailyNetMoney - losings);
                                    }
                                }
                            }
                        }
                        totalGames = totalGames + 1;
                    }
                }
                else if (backTestIngestObject.getBetType().equals("spread")) {
                    GameOdds gameOdds = gameOddsHashMap.get(game.getGameId());
                    if(gameOdds != null) {
                        if (gameOdds.getAwayTeamSpreadOdds() != 0 && gameOdds.getHomeTeamSpreadOdds() != 0) {
                            StaticScoreModelUtils.addHomeTeamAdvantage(scoreModel, backTestIngestObject.getHomeTeamAdvantage());
                            mergeScores(scoreModel);
                            //totalPredictedPoints = totalPredictedPoints + (safeScoreModel.getHomePPG() + safeScoreModel.getAwayPPG());
                            totalPredictedPoints = totalPredictedPoints + ((scoreModel.getTotalHigh() + scoreModel.getTotalLow())/2);
                            totalActualPoints = totalActualPoints + (game.getAwayPoints() + game.getHomePoints());
                            float HomePredictedPoints = (float)(scoreModel.getHomeHighPoints() + scoreModel.getHomeLowPoints()) / 2;
                            float AwayPredictedPoints = (float)(scoreModel.getAwayHighPoints() + scoreModel.getAwayLowPoints()) / 2;
                            float pointDifference = HomePredictedPoints - AwayPredictedPoints;
                            double pointClass = pointDifference - (gameOdds.getHomeTeamSpread()*-1);
                            System.out.println("(Spread) Home Predicted: " + HomePredictedPoints);
                            System.out.println("(Spread) Away Predicted: " + AwayPredictedPoints);
                            System.out.println("Home spread = " + game.getHomeTeamName() + " " + gameOdds.getHomeTeamSpread());
                            boolean result = false;
                            int actualDifference = game.getHomePoints() - game.getAwayPoints();


                            if(pointClass<= backTestIngestObject.getPointThreshold() * -1 || pointClass >= backTestIngestObject.getPointThreshold()) {
                                System.out.println("pointClass = " + pointClass);
                                if (HomePredictedPoints > AwayPredictedPoints) {
                                    double invertedHomeSpread = gameOdds.getHomeTeamSpread() * -1;
                                    if (pointDifference >= invertedHomeSpread) {
                                        System.out.println("Selecting " + game.getHomeTeamName() + " @ " + gameOdds.getHomeTeamSpread());
                                        System.out.println(game.getHomeTeamName() + " is predicted to win by " + pointDifference + " points");
                                        System.out.println(game.getHomeTeamName() + " spread is " + gameOdds.getHomeTeamSpread());
                                        if (game.getHomePoints() - game.getAwayPoints() > invertedHomeSpread) {
                                            result = true;
                                            correctPredictions = correctPredictions + 1;
                                            //splitCorrect = splitCorrect + 1;
                                            double winnings = (float) ((getBetAmountFromSpreadOdds(gameOdds.getHomeTeamSpreadOdds(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getHomeTeamSpreadOdds(), modifier) - 1)));
                                            dailyNetMoney = dailyNetMoney + (float)winnings;
                                            System.out.println("This is a win. Odds were " + gameOdds.getHomeTeamSpreadOdds() + " Current Balance is " + startingMoney + "Add $" + winnings);
                                        } else {
                                            result = false;
                                            incorrectPredictions = incorrectPredictions + 1;
                                            //splitIncorrect = splitIncorrect + 1;
                                            double losings = (float) getBetAmountFromSpreadOdds(gameOdds.getHomeTeamSpreadOdds(), betPerGame);
                                            dailyNetMoney = (float) (dailyNetMoney - losings);
                                            System.out.println("This is a loss. Odds were " + gameOdds.getHomeTeamSpreadOdds() + " Lose $" + losings);
                                        }
                                    } else {
                                        System.out.println("Selecting " + game.getAwayTeamName() + " @ " + gameOdds.getAwayTeamSpread());
                                        System.out.println(game.getAwayTeamName() + " is predicted to win by " + (pointDifference * -1) + " points");
                                        System.out.println(game.getAwayTeamName() + " spread is " + gameOdds.getAwayTeamSpread());
                                        if (game.getAwayPoints() - game.getHomePoints() > invertedHomeSpread * -1) {
                                            result = true;
                                            double winnings = (float) ((getBetAmountFromSpreadOdds(gameOdds.getAwayTeamSpreadOdds(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getAwayTeamSpreadOdds(), modifier) - 1)));

                                            dailyNetMoney = (float) (dailyNetMoney + winnings);
                                            correctPredictions = correctPredictions + 1;
                                            //splitCorrect = splitCorrect + 1;
                                            System.out.println("This is a win. Odds were " + gameOdds.getAwayTeamSpreadOdds() + " Current Balance is " + startingMoney + "Add $" + ((betPerGame * (applyWinningSpreadOdds(gameOdds.getAwayTeamSpreadOdds(), modifier) - 1))));

                                        } else {
                                            result = false;
                                            incorrectPredictions = incorrectPredictions + 1;
                                            //splitIncorrect = splitIncorrect + 1;
                                            double losings = (float) getBetAmountFromSpreadOdds(gameOdds.getAwayTeamSpreadOdds(), betPerGame);
                                            dailyNetMoney = (float) (dailyNetMoney - losings);
                                            System.out.println("This is a loss. Odds were " + gameOdds.getAwayTeamSpreadOdds() + " Lose $" + losings);
                                        }
                                    }
                                } else {
                                    double inverted = (pointDifference * -1);
                                    double invertedAwaySpread = gameOdds.getAwayTeamSpread() * -1;
                                    if (inverted >= invertedAwaySpread) {
                                        System.out.println("Selecting " + game.getAwayTeamName() + " @ " + gameOdds.getAwayTeamSpread());
                                        System.out.println(game.getAwayTeamName() + " is predicted to win by " + (pointDifference) + " points");
                                        System.out.println(game.getAwayTeamName() + " spread is " + gameOdds.getAwayTeamSpread());
                                        if (game.getAwayPoints() - game.getHomePoints() > invertedAwaySpread) {
                                            result = true;
                                            correctPredictions = correctPredictions + 1;
                                            //splitCorrect = splitCorrect + 1;
                                            double winnings = (float) ((getBetAmountFromSpreadOdds(gameOdds.getAwayTeamSpreadOdds(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getAwayTeamSpreadOdds(), modifier) - 1)));

                                            dailyNetMoney = dailyNetMoney + (float)winnings;
                                            System.out.println("This is a win. Odds were " + gameOdds.getAwayTeamSpreadOdds() + " Current Balance is " + startingMoney + "Add $" + winnings);
                                        } else {
                                            result = false;

                                            incorrectPredictions = incorrectPredictions + 1;
                                            double losings = (float) getBetAmountFromSpreadOdds(gameOdds.getAwayTeamSpreadOdds(), betPerGame);
                                            //splitIncorrect = splitIncorrect + 1;
                                            dailyNetMoney = (float) (dailyNetMoney - losings);
                                            System.out.println("This is a loss. Odds were " + gameOdds.getAwayTeamSpreadOdds() + " Lose $" + losings);

                                        }
                                    } else {
                                        System.out.println("Selecting " + game.getHomeTeamName() + " @ " + gameOdds.getHomeTeamSpreadOdds());
                                        System.out.println(game.getHomeTeamName() + " is predicted to win by " + pointDifference + " points");
                                        System.out.println(game.getHomeTeamName() + " spread is " + gameOdds.getHomeTeamSpread());
                                        if (game.getHomePoints() - game.getAwayPoints() > invertedAwaySpread * -1) {
                                            result = true;
                                            correctPredictions = correctPredictions + 1;
                                            //splitCorrect = splitCorrect + 1;
                                            double winnings = (float) ((getBetAmountFromSpreadOdds(gameOdds.getHomeTeamSpreadOdds(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getHomeTeamSpreadOdds(), modifier) - 1)));

                                            dailyNetMoney = dailyNetMoney + (float) winnings;
                                            System.out.println("This is a win. Odds were " + gameOdds.getHomeTeamSpreadOdds() + " Current Balance is " + startingMoney + "Add $" + winnings);

                                        } else {
                                            result = false;
                                            double losings = (float) getBetAmountFromSpreadOdds(gameOdds.getHomeTeamSpreadOdds(), betPerGame);

                                            dailyNetMoney = (float) (dailyNetMoney - losings);
                                            incorrectPredictions = incorrectPredictions + 1;
                                            //splitIncorrect = splitIncorrect + 1;
                                            System.out.println("This is a loss. Odds were " + gameOdds.getHomeTeamSpreadOdds() + " Lose $" + losings);

                                        }

                                    }

                                }
                            }
                        }
                        totalGames = totalGames + 1;
                    }
                    System.out.println(game.getHomeTeamName() + " scored " + game.getHomePoints());
                    System.out.println(game.getAwayTeamName() + " scored " + game.getAwayPoints());
                    //totalGames = totalGames + 1;
                    if(correctPredictions + incorrectPredictions > 0) {
                        BigDecimal b1 = new BigDecimal(((double) correctPredictions / (double) (correctPredictions + incorrectPredictions)) * 100);
                        //BigDecimal b2 = new BigDecimal(((double)ppgCorrect / (double)(ppgCorrect + ppgIncorrect)) * 100);
                        MathContext m = new MathContext(4);
                        System.out.println(ANSI_GREEN + "$" + (startingMoney + dailyNetMoney) + ANSI_RESET);
                        String progressString = ANSI_GREEN + "CORRECT PREDICTIONS: " + correctPredictions + " || " + "INCORRECT PREDICTIONS: " + incorrectPredictions + " (" + b1.round(m).doubleValue() + "%) EXACT MATCH: " + exactMatch;
                        System.out.println(progressString);
                        System.out.println(ANSI_GREEN + "SETTINGS: Lookback: " + gameCount + " ptsReduction: " + backTestIngestObject.getPointsReducedPerBlock() +
                                " ptsReductionPerSteal: " + backTestIngestObject.getPointReductionPerSteal() +
                                " ptsReducedPerTO: " + backTestIngestObject.getPointsReducedPerTurnover() +
                                " lowBlockFactor: " + backTestIngestObject.getLowerBlockPointFactor() +
                                " dblsqrt: " + backTestIngestObject.isDoubleSquareRoot() + " rplcBlksWBPG: " + backTestIngestObject.isReplaceLowBlocksWithBPG() +
                                " blockExp: " + backTestIngestObject.getBlockExponent() +
                                " modelOpponentBlocks: " + backTestIngestObject.isModelOpponentBlocks() +
                                " modelOpponentSteals: " + backTestIngestObject.isModelOpponentSteals() +
                                " gameTimeThreshold: " + backTestIngestObject.getGameTimeThreshold() + ANSI_RESET);
                        //System.out.println(ANSI_GREEN + "PPG CORRECT PREDICTIONS: " + ppgCorrect + " || " + "PPG INCORRECT PREDICTIONS: " + ppgIncorrect + "(" + b2.round(m).doubleValue() + "%)" + ANSI_RESET);
                        System.out.println(ANSI_GREEN + "PREDICTIONS OVER: " + predictOver + " PREDICTIONS UNDER: " + predictUnder + " || ACTUAL OVER: " + actualOver + " ACTUAL UNDER: " + actualUnder + " || TotalPredictedPoints: " + totalPredictedPoints + " TotalActualPoints: " + totalActualPoints + " (" + ((totalPredictedPoints - totalActualPoints) / (totalActualPoints) * 100) + "%)" + ANSI_RESET);
                        //System.out.println(ANSI_GREEN + backTestResults + ANSI_RESET);
                        System.out.println(ANSI_GREEN + "[" + threadNum + "] " + "[" + (backTestResults.size() + 1) + "/" + backTestIngestObjects.size() + "]" + ANSI_RESET);
                    }
                }
                gamesCompleted = gamesCompleted + 1;
                printProgress();
            }
            if(forward){
                localDate = localDate.plusDays(1);
            }else{
                localDate = localDate.minusDays(1);
            }
            startingMoney = startingMoney + dailyNetMoney;
        }


        return MiscSharedUtils.buildBackTestResult(backTestIngestObject,correctPredictions, incorrectPredictions,startingMoney, totalActualPoints,totalPredictedPoints, exactMatch);
    }



    public static void mergeScores(ScoreModel scoreModel){
        scoreModel.setTotalHigh(scoreModel.getHomeHighPoints() + scoreModel.getAwayHighPoints());
        scoreModel.setTotalLow(scoreModel.getHomeLowPoints() + scoreModel.getAwayLowPoints());
    }

    public ScoreModel getScoreModelForGame(Game game, BackTestIngestObject backTestIngestObject, Date date) {
        CacheSettingsObject compareObject = new CacheSettingsObject();
        compareObject.setGameTimeThreshold(backTestIngestObject.getGameTimeThreshold());
        compareObject.setPlayerGameLookback(backTestIngestObject.getPlayerGameLookBack());
        compareObject.setDoubleSquareRoot(backTestIngestObject.isDoubleSquareRoot());
        compareObject.setAllowBelowZero(backTestIngestObject.isAllowBelowZero());

        compareObject.setFractalWindow(backTestIngestObject.getFractalWindow());
        int hashCode = compareObject.hashCode();
        if(gameCache.get(hashCode) != null){
            if(gameCache.get(hashCode).get(game.getGameId()) != null){
                return gameCache.get(hashCode).get(game.getGameId());
            }else{
                ScoreModel scoreModel = new ScoreModel();
                HashMap<Integer,ScoreModel> gameMap = gameCache.get(hashCode);
                Team awayTeam = (Team) game.getAwayTeam().clone();
                awayTeam.setPlayers(gameFinder.getPlayerListFromPreviousGameFromDB(game, game.getAwayTeam().getTeamId()));
                Team homeTeam = (Team) game.getHomeTeam().clone();
                homeTeam.setPlayers(gameFinder.getPlayerListFromPreviousGameFromDB(game, game.getHomeTeam().getTeamId()));
        //        gameCalculatorClass.experimentalTallying(backTestIngestObject, game, teamStatFetcher, date, awayTeam, scoreModel, false, false, 0, 0);
       //         gameCalculatorClass.experimentalTallying(backTestIngestObject, game, teamStatFetcher, date, homeTeam, scoreModel, true, false, 0, 0);

                gameCalculatorClass.tallyTeamLastNGames(backTestIngestObject, date, playerStatFilter,
                        awayTeam, scoreModel, false,false, backTestIngestObject.getDayLookbackCap(), backTestIngestObject.getGameTimeThreshold(), moreInfo);
                gameCalculatorClass.tallyTeamLastNGames(backTestIngestObject, date, playerStatFilter,
                        homeTeam, scoreModel,false, true, backTestIngestObject.getDayLookbackCap(),backTestIngestObject.getGameTimeThreshold(), moreInfo);
                gameMap.put(game.getGameId(), scoreModel);
                return scoreModel;
            }
        } else{
            CacheSettingsObject cacheSettingsObject = new CacheSettingsObject();
            cacheSettingsObject.setPlayerGameLookback(backTestIngestObject.getPlayerGameLookBack());
            cacheSettingsObject.setDoubleSquareRoot(backTestIngestObject.isDoubleSquareRoot());
            cacheSettingsObject.setAllowBelowZero(backTestIngestObject.isAllowBelowZero());
            cacheSettingsObject.setGameTimeThreshold(backTestIngestObject.getGameTimeThreshold());
            cacheSettingsObject.setFractalWindow(backTestIngestObject.getFractalWindow());
            HashMap<Integer, ScoreModel> gameMap = new HashMap<>();
            Team awayTeam = new Team();
            if (game.getAwayTeam() == null) {
                awayTeam.setTeamId(game.getAwayTeam().getTeamId());
                awayTeam.setTeamName(game.getAwayTeamName());
                awayTeam.setTeamAbbreviation(game.getAwayTeamTricode());
             //   awayId = game.getAwayTeam().getTeamId();
            }else{
                awayTeam = (Team) game.getAwayTeam().clone();
                awayTeam = (Team) game.getAwayTeam().clone();
             //   awayId = game.getAwayTeam().getTeamId();
            }
            Team homeTeam = new Team();
            if (game.getHomeTeam() == null) {
                homeTeam.setTeamId(game.getHomeTeam().getTeamId());
                homeTeam.setTeamName(game.getHomeTeamName());
                homeTeam.setTeamAbbreviation(game.getHomeTeamTricode());
             //   homeId = game.getHomeTeam().getTeamId();
            }else{
                homeTeam = (Team) game.getHomeTeam().clone();
                homeTeam = (Team) game.getHomeTeam().clone();
            //    homeId = game.getHomeTeam().getTeamId();
            }


            awayTeam.setPlayers(gameFinder.getPlayerListFromPreviousGameFromDB(game, awayTeam.getTeamId()));

           // Team homeTeam = (Team) game.getHomeTeam().clone();

            homeTeam.setPlayers(gameFinder.getPlayerListFromPreviousGameFromDB(game, homeTeam.getTeamId()));

            ScoreModel scoreModel = new ScoreModel();
       //     gameCalculatorClass.experimentalTallying(backTestIngestObject, game, teamStatFetcher, date, awayTeam, scoreModel, false, false, 0, 0);
       //     gameCalculatorClass.experimentalTallying(backTestIngestObject, game, teamStatFetcher, date, homeTeam, scoreModel, true, false, 0, 0);
            gameCalculatorClass.tallyTeamLastNGames(backTestIngestObject, date, playerStatFilter,
                    awayTeam, scoreModel, false,false, backTestIngestObject.getDayLookbackCap(), backTestIngestObject.getGameTimeThreshold(), moreInfo);
            gameCalculatorClass.tallyTeamLastNGames(backTestIngestObject, date, playerStatFilter,
                    homeTeam, scoreModel,false, true, backTestIngestObject.getDayLookbackCap(),backTestIngestObject.getGameTimeThreshold(), moreInfo);
            gameMap.put(game.getGameId(), scoreModel);
            gameCache.put(cacheSettingsObject.hashCode(), gameMap);
            return scoreModel;
        }
    }

    private GameOdds getGameOddsFromCache(int gameId){
        if(gameOddsHashMap.get(gameId) != null){
            return  gameOddsHashMap.get(gameId);
        }else{
            return null;
        }
    }
    private List<Game> getGamesFromMap(LocalDate localDate){
        if(dayGameMap.get(localDate)!= null){
            return dayGameMap.get(localDate);
        }else{
            List<Game> games = gameFinder.findGamesOnDateFromDB(localDate);
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

    public void printProgress(){
        if((testsComplete + 1) % printEveryNResults == 0) {
            now = Instant.now();
            delta = Duration.between(start, now).toMillis();
            rate = ((float) gamesCompleted / delta) * 1000;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ANSI_ORANGE).append("Games per second: ").append(rate).append("\n");
            stringBuilder.append("[").append(threadNum).append("] [").append(testsComplete + 1).append("/").append(backTestIngestObjects.size()).append("]").append(ANSI_RESET);
            System.out.println(stringBuilder);
        }
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
    public static double getProbability(double spreadOdds){
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
    public double[] convertListToArray(List<Double> originalList){
        double[] newArray = new double[originalList.size()];
        int size = originalList.size();
        for(int i = 0; i < size; i++){
            newArray[i] = originalList.get(i);
        }
        return newArray;
    }
}
