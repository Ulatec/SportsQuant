package SportsQuant.Threads;


import SportsQuant.Model.*;
import SportsQuant.Model.CacheObject.CacheSettingsObject;
import SportsQuant.Model.CacheObject.StatCacheSettingsObject;
import SportsQuant.Util.*;

import java.io.BufferedWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

public class IndividualStatTestThread extends Thread {
    private StatThreadMonitor threadMonitor;
    //private BackTestWatcher backTestWatcher;
    private List<BackTestIngestObject> backTestIngestObjects;
    private HashMap<Integer, GameOdds> gameOddsHashMap;
    private List<TreeMap<String, HashMap<StatResult,Boolean>>> backTestResults;
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\033[31;1;4m";
    public static final String ANSI_RESET = "\u001B[0m";
    private final boolean moreInfo = false;
    private GameFinder gameFinder;
    private boolean newGames = false;
    private int threadNum;
    private BufferedWriter bufferedWriter;
    private GameCalculatorClass gameCalculatorClass;
    private HashMap<Integer, HashMap<Integer, ScoreModel>> gameCache;
    private HashMap<LocalDate, List<Game>> dayGameMap;
    private int testsComplete = 0;
    private boolean forward;
    PlayerStatFilter playerStatFilter = new PlayerStatFilter();
    PlayerStatFetcher playerStatFetcher = new PlayerStatFetcher();
    TeamStatFetcher teamStatFetcher = new TeamStatFetcher();
    private int modifier = 0;
    Instant start;
    Instant now;
    double delta;
    double rate;
    long gamesCompleted;
    private final int printEveryNGames = 1;
    private final int printEveryNResults = 5000;
    private static final String ANSI_ORANGE = "\033[38;5;214m";
    private double startingMoney;
    private List<BackTestIngestObject> referenceList;
    private final DecimalFormat d = new DecimalFormat("#.##");
    private LocalDate localDate;
    private double betPercent;
    private double confidence;
    private boolean recording = false;

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public double getBetPercent() {
        return betPercent;
    }

    public void setBetPercent(double betPercent) {
        this.betPercent = betPercent;
    }

    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {
        this.recording = recording;
    }
//PlayerStatFilter playerStatFilter;

    public IndividualStatTestThread(List<BackTestIngestObject> backTestIngestObjects, List<Game> gameList, StatThreadMonitor threadMonitor) {
        this.gameOddsHashMap = gameOddsHashMap;
        this.threadNum = threadNum;
        this.threadMonitor = threadMonitor;
        this.backTestIngestObjects = backTestIngestObjects;
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
        teamStatFetcher.setGameList(gameList);
        forward = false;
        gameCache = new HashMap<>();
        start = Instant.now();
        dayGameMap = new HashMap<>();
    }

    public List<BackTestIngestObject> getBackTestIngestObjects() {
        return backTestIngestObjects;
    }

    public void setBackTestIngestObjects(List<BackTestIngestObject> backTestIngestObjects) {
        this.backTestIngestObjects = backTestIngestObjects;
    }

    public boolean isForward() {
        return forward;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
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
    public void setStartingMoney(double startingMoney) {
        this.startingMoney = startingMoney;
    }
    public synchronized void setThreadMonitor(StatThreadMonitor threadMonitor) {
        this.threadMonitor = threadMonitor;
    }
    public void run() {
        System.out.println("start backtest");
        start = Instant.now();

        while(true) {
            if(backTestIngestObjects != referenceList) {
                for (BackTestIngestObject backTestIngestObject : backTestIngestObjects) {
                    TreeMap<String, HashMap<StatResult,Boolean>> result;
                    result = runBackTest(backTestIngestObject);
                    backTestResults.add(result);
                }
                //gameFinder.quitWebDriver();
                //threadMonitor.ingestResults(backTestResults);
                System.out.println("thread " + threadNum + " is finished");
                threadMonitor.threadFinished(threadNum, backTestResults);
                backTestResults = new ArrayList<>();

                referenceList = backTestIngestObjects;
                gamesCompleted = 0;
                start = Instant.now();
                testsComplete = 0;

            }
        }
    }

    public TreeMap<String, HashMap<StatResult,Boolean>> runBackTest(BackTestIngestObject backTestIngestObject) {
        startingMoney = 100;
        playerStatFilter.setMinuteThreshold(backTestIngestObject.getGameTimeThreshold());
        int gamesToTest = backTestIngestObject.getGamesToTest();
        LocalDate startDate = backTestIngestObject.getStartDate();
        localDate = backTestIngestObject.getStartDate();
        int gameCount = backTestIngestObject.getPlayerGameLookBack();
        int correctPredictions = 0;
        int incorrectPredictions = 0;
        int totalGames = 0;
        double spreadModification = 0;
        double spreadOddsModification = 0;
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
        HashMap<StatResult, Boolean> freeThrowAttemptMap = new HashMap<>();
        HashMap<StatResult, Boolean> threePointAttemptMap = new HashMap<>();
        HashMap<StatResult, Boolean> fieldGoalAttemptMap = new HashMap<>();
        HashMap<StatResult, Boolean> foulMap = new HashMap<>();
        HashMap<StatResult, Boolean> defensiveReboundMap = new HashMap<>();
        HashMap<StatResult, Boolean> offensiveReboundMap = new HashMap<>();
        HashMap<StatResult, Boolean> blockMap = new HashMap<>();
        HashMap<StatResult, Boolean> stealMap = new HashMap<>();
        HashMap<StatResult, Boolean> turnoverMap = new HashMap<>();
        HashMap<StatResult, Boolean> freeThrowMap = new HashMap<>();
        HashMap<StatResult, Boolean> threePointPctMap = new HashMap<>();
        HashMap<StatResult, Boolean> freeThrowPctMap = new HashMap<>();
        HashMap<StatResult, Boolean> fieldGoalPctMap = new HashMap<>();
        String statType = "threePointAttempts";
            while (totalGames < gamesToTest) {
                float dailyNetMoney = 0.0f;
                List<Game> games = getGamesFromMap(localDate);

                //System.out.println(games);
                double betPerGame = startingMoney * betPercent;
                for (int i = 0; i < games.size(); i++) {
                    Game game = (Game) games.get(i).clone();
                    ScoreModel scoreModel = (ScoreModel) getScoreModelForGame(game, backTestIngestObject, game.getDate()).clone();

                    Double threePointAttempts = 0.0;
                    Double fieldGoalAttempts = 0.0;
                    Double fouls = 0.0;
                    Double dreb = 0.0;
                    Double oreb = 0.0;
                    Double blocks = 0.0;
                    Double steals = 0.0;
                    Double turnovers = 0.0;
                    Double freethrows = 0.0;
                    Double threePointMade = 0.0;
                    Double freeThrowMade = 0.0;
                    Double freeThrowAttempt = 0.0;
                    Double fieldGoalsMade = 0.0;
                    List<Player> players = new ArrayList<>();
                    players.addAll(game.getAwayTeam().getPlayers());
                    players.addAll(game.getHomeTeam().getPlayers());
                    for(Player player : players){
                        for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
                            if (playerGamePerformance.getGameID() == game.getGameId()) {
                                threePointAttempts = threePointAttempts + (double) playerGamePerformance.getThreePointersAttempted();
                                fieldGoalAttempts = fieldGoalAttempts + (double) playerGamePerformance.getFieldGoalsAttempted();
                                fouls = fouls + (double) playerGamePerformance.getFouls();
                                dreb = dreb + (double) playerGamePerformance.getReboundsDefensive();
                                blocks = blocks + (double) playerGamePerformance.getBlocks();
                                steals = steals + (double) playerGamePerformance.getSteals();
                                turnovers = turnovers + (double) playerGamePerformance.getTurnovers();
                                oreb = oreb + (double) playerGamePerformance.getReboundsOffensive();
                                freethrows = freethrows + (double) playerGamePerformance.getFreeThrowsAttempted();
                                threePointMade = threePointMade + (double) playerGamePerformance.getThreePointersMade();
                                freeThrowAttempt = freeThrowAttempt + (double) playerGamePerformance.getFreeThrowsAttempted();
                                freeThrowMade = freeThrowMade + (double) playerGamePerformance.getFreeThrowsMade();
                                fieldGoalsMade = fieldGoalsMade + (double) playerGamePerformance.getFieldGoalsMade();
                            }
                        }
                    }

                    mergeScores(scoreModel);
                    StaticScoreModelUtils.convertAndAssignMadeBasketsFromPercentages(scoreModel);
                    StaticScoreModelUtils.factorBlocksIntoScoringModel(scoreModel, backTestIngestObject.getPointsReducedPerBlock(), backTestIngestObject.getLowerBlockPointFactor(), backTestIngestObject.getHighBlockPointFactor());
                    StaticScoreModelUtils.factorStealsIntoScoringModel(scoreModel, backTestIngestObject.getPointReductionPerSteal(), backTestIngestObject.getLowerStealPointFactor(), backTestIngestObject.getHighStealPointFactor());
                    StaticScoreModelUtils.factorTurnoversIntoScoringModel(scoreModel, backTestIngestObject.getPointsReducedPerTurnover(), backTestIngestObject.getLowerTurnoverPointFactor(), backTestIngestObject.getHighTurnoverPointFactor());
                    StaticScoreModelUtils.factorDefensiveReboundsIntoScoringModel(scoreModel, backTestIngestObject.getPointsReducedPerRebound(), backTestIngestObject.getLowerReboundPointFactor(), backTestIngestObject.getHighReboundPointFactor(), moreInfo);
                    StaticScoreModelUtils.factorOffensiveReboundsIntoScoringModel(scoreModel, backTestIngestObject.getHighReboundPointFactor());
                    StaticScoreModelUtils.factorFoulsIntoScoringModel(scoreModel, backTestIngestObject.getPointsReducedPerFoul(), backTestIngestObject.getLowerFoulPointFactor(), backTestIngestObject.getHighFoulPointFactor());

                    StaticScoreModelUtils.sumFieldGoalsAndFreeThrowsIntoPoints(scoreModel);
                    boolean result = false;
                    if (backTestIngestObject.getBetType().equals("moneyline")) {
                        GameOdds gameOdds = gameOddsHashMap.get(game.getGameId());
                        if (gameOdds != null) {
                            mergeScores(scoreModel);
                            totalPredictedPoints = totalPredictedPoints + ((scoreModel.getTotalHigh() + scoreModel.getTotalLow()) / 2);
                            totalActualPoints = totalActualPoints + (game.getAwayPoints() + game.getHomePoints());
                            totalGames = totalGames + 1;
                            gamesCompleted = gamesCompleted + 1;
                            double homePredicted = ((scoreModel.getHomeHighPoints() + scoreModel.getHomeLowPoints()) / 2);
                            double awayPredicted = ((scoreModel.getAwayHighPoints() + scoreModel.getAwayLowPoints()) / 2);
                            //System.out.println("calculated awayPercent = " + awayPercentAboveHome);
                            double pointDifference = homePredicted - awayPredicted;
                            if (pointDifference <= backTestIngestObject.getPointThreshold() * -1 || pointDifference >= backTestIngestObject.getPointThreshold()) {
                                if (homePredicted > awayPredicted) {
                                    //System.out.println("Implied Away Odds: " + impliedAwayOdds);
                                    if (game.getHomePoints() > game.getAwayPoints()) {
                                        correctPredictions = correctPredictions + 1;
                                        //splitCorrect = splitCorrect + 1;
                                        dailyNetMoney = dailyNetMoney + (float) ((getBetAmountFromSpreadOdds(gameOdds.getHomeTeamMoneyLine(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getHomeTeamMoneyLine(), modifier) - 1)));
                                    } else {
                                        incorrectPredictions = incorrectPredictions + 1;
                                        //splitIncorrect = splitIncorrect + 1;
                                        dailyNetMoney = dailyNetMoney - (float) getBetAmountFromSpreadOdds(gameOdds.getHomeTeamMoneyLine(), betPerGame);
                                    }
                                } else {
                                    if (game.getAwayPoints() > game.getHomePoints()) {
                                        correctPredictions = correctPredictions + 1;
                                        //splitCorrect = splitCorrect + 1;
                                        dailyNetMoney = dailyNetMoney + (float) ((getBetAmountFromSpreadOdds(gameOdds.getAwayTeamMoneyLine(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getAwayTeamMoneyLine(), modifier) - 1)));
                                    } else {
                                        incorrectPredictions = incorrectPredictions + 1;
                                        //splitIncorrect = splitIncorrect + 1;
                                        dailyNetMoney = dailyNetMoney - (float) getBetAmountFromSpreadOdds(gameOdds.getAwayTeamMoneyLine(), betPerGame);
                                    }
                                }
                            }
                        }
                    } else if (backTestIngestObject.getBetType().equals("overunder")) {
                        if (gameOddsHashMap.get(game.getGameId()) != null) {

                            GameOdds gameOdds = gameOddsHashMap.get(game.getGameId());
                            //StaticScoreModelUtils.addHomeTeamAdvantage(scoreModel, backTestIngestObject.getHomeTeamAdvantage());
                            mergeScores(scoreModel);

                            totalActualPoints = totalActualPoints + (game.getHomePoints() + game.getAwayPoints());
                            double predictedPoints = (scoreModel.getTotalHigh() + scoreModel.getTotalLow()) / 2;
                            totalPredictedPoints = totalPredictedPoints + (predictedPoints);
//                            System.out.println("O/U:" + gameOdds.getOverUnder());
//                            System.out.println("Actual Score: " + (game.getHomePoints() + game.getAwayPoints()));
//
//                            System.out.println("predicted points : " + predictedPoints);
                            int actualPoints = game.getHomePoints() + game.getAwayPoints();
                            if (game.getHomePoints() + game.getAwayPoints() != gameOdds.getOverUnder()) {
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
//                            totalGames = totalGames + 1;
                        }
                    } else if (backTestIngestObject.getBetType().equals("spread")) {
                        GameOdds gameOdds = gameOddsHashMap.get(game.getGameId());
                        if (gameOdds != null) {
                            StaticScoreModelUtils.addHomeTeamAdvantage(scoreModel, backTestIngestObject.getHomeTeamAdvantage());
                            mergeScores(scoreModel);
                            totalPredictedPoints = totalPredictedPoints + ((scoreModel.getTotalHigh() + scoreModel.getTotalLow()) / 2);
                            //totalPredictedPoints = totalPredictedPoints + (scoreModel.getAwayPointsPerGame() + scoreModel.getHomePointsPerGame());
                            totalActualPoints = totalActualPoints + (game.getAwayPoints() + game.getHomePoints());
                            float HomePredictedPoints = (float) (scoreModel.getHomeHighPoints() + scoreModel.getHomeLowPoints()) / 2;
                            float AwayPredictedPoints = (float) (scoreModel.getAwayHighPoints() + scoreModel.getAwayLowPoints()) / 2;
                            System.out.println("(Spread) Home Predicted: " + HomePredictedPoints);
                            System.out.println("(Spread) Away Predicted: " + AwayPredictedPoints);
                            System.out.println("Home spread = " + game.getHomeTeamName() + " " + gameOdds.getHomeTeamSpread());
                            //float HomePredictedPoints = (float) scoreModel.getHomePointsPerGame();
                            //float AwayPredictedPoints = (float) scoreModel.getAwayPointsPerGame();
                            float pointDifference = HomePredictedPoints - AwayPredictedPoints;
                            int actualDifference = game.getHomePoints() - game.getAwayPoints();
                            float pointClass = (pointDifference - ((float) gameOdds.getHomeTeamSpread()* -1));
                            //if (pointClass <= backTestIngestObject.getPointThreshold() * -1 || pointClass >= backTestIngestObject.getPointThreshold()) {
                                if (HomePredictedPoints > AwayPredictedPoints) {
                                    double invertedHomeSpread = getModifiedSpread(gameOdds.getHomeTeamSpread(), spreadModification) * -1;
                                    if (pointDifference >= invertedHomeSpread) {
                                        double spreadOdds = getModifiedSpread(gameOdds.getHomeTeamSpreadOdds(), spreadOddsModification);
                                        if(spreadOdds != actualDifference && spreadOdds != actualDifference*-1) {
                                            if (actualDifference > invertedHomeSpread) {
                                                result = true;
                                                correctPredictions = correctPredictions + 1;
                                                dailyNetMoney = (float) (dailyNetMoney + ((getBetAmountFromSpreadOdds(spreadOdds, betPerGame) * (applyWinningSpreadOdds(spreadOdds, modifier) - 1))));

                                                //dailyNetMoney = (float) (dailyNetMoney + ((getBetAmountFromSpreadOdds(gameOdds.getHomeTeamSpreadOdds(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getHomeTeamSpreadOdds(), modifier) - 1))));
                                            } else {
                                                incorrectPredictions = incorrectPredictions + 1;
                                                dailyNetMoney = (float) (dailyNetMoney - getBetAmountFromSpreadOdds(spreadOdds, betPerGame));
                                                //dailyNetMoney = (float) (dailyNetMoney - getBetAmountFromSpreadOdds(gameOdds.getHomeTeamSpreadOdds(), betPerGame));
                                            }
                                        }
                                    } else {
                                        double spreadOdds = getModifiedSpread(gameOdds.getAwayTeamSpreadOdds(), spreadOddsModification);
                                        if(spreadOdds != actualDifference && spreadOdds != actualDifference*-1) {
                                            if ((actualDifference * -1) > invertedHomeSpread * -1) {
                                                result = true;
                                                dailyNetMoney = (float) (dailyNetMoney + (getBetAmountFromSpreadOdds(spreadOdds, betPerGame) * (applyWinningSpreadOdds(spreadOdds, modifier) - 1)));

                                                //dailyNetMoney = (float) (dailyNetMoney + (getBetAmountFromSpreadOdds(gameOdds.getAwayTeamSpreadOdds(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getAwayTeamSpreadOdds(), modifier) - 1)));
                                                correctPredictions = correctPredictions + 1;
                                            } else {
                                                incorrectPredictions = incorrectPredictions + 1;

                                                dailyNetMoney = dailyNetMoney - (float) getBetAmountFromSpreadOdds(spreadOdds, betPerGame);

                                                //dailyNetMoney = dailyNetMoney - (float) getBetAmountFromSpreadOdds(gameOdds.getAwayTeamSpreadOdds(), betPerGame);
                                            }
                                        }
                                    }
                                } else {
                                    double invertedAwaySpread = getModifiedSpread(gameOdds.getAwayTeamSpread(), spreadModification) * -1;
                                    if ((pointDifference * -1) >= invertedAwaySpread) {
                                        double spreadOdds = getModifiedSpread(gameOdds.getAwayTeamSpreadOdds(), spreadOddsModification);
                                        if(spreadOdds != actualDifference && spreadOdds != actualDifference*-1) {
                                            if ((actualDifference * -1) > invertedAwaySpread) {
                                                result = true;
                                                correctPredictions = correctPredictions + 1;
                                                dailyNetMoney = (float) (dailyNetMoney + ((getBetAmountFromSpreadOdds(spreadOdds, betPerGame) * (applyWinningSpreadOdds(spreadOdds, modifier) - 1))));
                                                //dailyNetMoney = (float) (dailyNetMoney + ((getBetAmountFromSpreadOdds(gameOdds.getAwayTeamSpreadOdds(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getAwayTeamSpreadOdds(), modifier) - 1))));
                                            } else {
                                                incorrectPredictions = incorrectPredictions + 1;
                                                dailyNetMoney = dailyNetMoney - (float) getBetAmountFromSpreadOdds(spreadOdds, betPerGame);
                                                //dailyNetMoney = dailyNetMoney - (float) getBetAmountFromSpreadOdds(gameOdds.getAwayTeamSpreadOdds(), betPerGame);
                                            }
                                        }
                                    } else {
                                        double spreadOdds = getModifiedSpread(gameOdds.getHomeTeamSpreadOdds(), spreadOddsModification);
                                        if(spreadOdds != actualDifference && spreadOdds != actualDifference*-1) {
                                            if (actualDifference > invertedAwaySpread * -1) {
                                                result = true;
                                                correctPredictions = correctPredictions + 1;
                                                dailyNetMoney = (float) (dailyNetMoney + (getBetAmountFromSpreadOdds(spreadOdds, betPerGame) * (applyWinningSpreadOdds(spreadOdds, modifier) - 1)));
                                                //dailyNetMoney = (float) (dailyNetMoney + (getBetAmountFromSpreadOdds(gameOdds.getHomeTeamSpreadOdds(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getHomeTeamSpreadOdds(), modifier) - 1)));
                                            } else {
                                                incorrectPredictions = incorrectPredictions + 1;
                                                dailyNetMoney = (dailyNetMoney - (float) getBetAmountFromSpreadOdds(spreadOdds, betPerGame));

                                                //dailyNetMoney = (dailyNetMoney - (float) getBetAmountFromSpreadOdds(gameOdds.getHomeTeamSpreadOdds(), betPerGame));
                                            }
                                        }
                                    }
                                }

                            //}
                            totalGames = totalGames + 1;
                        }

                    }
                    if(correctPredictions + incorrectPredictions > 0) {
                        BigDecimal b1 = new BigDecimal(((double) correctPredictions / (double) (correctPredictions + incorrectPredictions)) * 100);
                        //BigDecimal b2 = new BigDecimal(((double)ppgCorrect / (double)(ppgCorrect + ppgIncorrect)) * 100);
                        MathContext m = new MathContext(4);
                        System.out.println(ANSI_GREEN + "$" + (startingMoney + dailyNetMoney) + ANSI_RESET);
                        System.out.println(ANSI_GREEN + backTestIngestObject.getNumHigh() + "/" + backTestIngestObject.getNumLow() + ANSI_RESET);
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
                    if(!Double.isNaN(scoreModel.getHomeHighPoints()) && !Double.isNaN(scoreModel.getAwayHighPoints())) {
                        double forecastedThreePointAttempts = ((scoreModel.getAwayHighThreePointAttempts() + scoreModel.getAwayLowThreePointAttempts()) / 2) + ((scoreModel.getHomeHighThreePointAttempts() + scoreModel.getHomeLowThreePointAttempts()) / 2);
                        double forecastedFieldGoalAttempts = ((scoreModel.getAwayHighFieldGoalAttempts() + scoreModel.getAwayLowFieldGoalAttempts()) / 2) + ((scoreModel.getHomeHighFieldGoalAttempts() + scoreModel.getHomeLowFieldGoalAttempts()) / 2);
                        double forcastedFouls = ((scoreModel.getAwayHighFouls() + scoreModel.getAwayLowFouls()) / 2) + ((scoreModel.getHomeHighFouls() + scoreModel.getHomeLowFouls()) / 2);
                        double forcastedDefensiveRebounds = ((scoreModel.getAwayHighDefensiveRebounds() + scoreModel.getAwayLowDefensiveRebounds()) / 2) + ((scoreModel.getHomeHighDefensiveRebounds() + scoreModel.getHomeLowDefensiveRebounds()) / 2);
                        double forcastedOffensiveRebounds = ((scoreModel.getAwayHighOffensiveRebounds() + scoreModel.getAwayLowOffensiveRebounds()) / 2) + ((scoreModel.getHomeHighOffensiveRebounds() + scoreModel.getHomeLowOffensiveRebounds()) / 2);
                        double forcastedBlocks = ((scoreModel.getAwayHighBlocks() + scoreModel.getAwayLowBlocks()) / 2) + ((scoreModel.getHomeHighBlocks() + scoreModel.getHomeLowBlocks()) / 2);
                        double forcastedSteals = ((scoreModel.getAwayHighSteals() + scoreModel.getAwayLowSteals()) / 2) + ((scoreModel.getHomeHighSteals() + scoreModel.getHomeLowSteals()) / 2);
                        double forcastedTurnovers = ((scoreModel.getAwayHighTurnovers() + scoreModel.getAwayLowTurnovers()) / 2) + ((scoreModel.getHomeHighTurnovers() + scoreModel.getHomeLowTurnovers()) / 2);
                        double forecastedFreeThrows = ((scoreModel.getAwayHighFreeThrowAttempts() + scoreModel.getAwayLowFreeThrowAttempts()) / 2) + ((scoreModel.getHomeHighFreeThrowAttempts() + scoreModel.getHomeLowFreeThrowAttempts()) / 2);
                        double forecastedThreePointPercentage = ((((scoreModel.getAwayHighThreePointPercentage() + scoreModel.getAwayLowThreePointPercentage()) / 2) + ((scoreModel.getHomeHighThreePointPercentage() + scoreModel.getHomeLowThreePointPercentage()) / 2))/2);
                        double forecastedFreeThrowPercentage = ((((scoreModel.getAwayHighFreeThrowPercentage() + scoreModel.getAwayLowFreeThrowPercentage()) / 2) + ((scoreModel.getHomeHighFreeThrowPercentage() + scoreModel.getHomeLowFreeThrowPercentage()) / 2))/2);
                        double forecastedFieldGoalPercentage = ((((scoreModel.getAwayHighFieldGoalPercentage() + scoreModel.getAwayLowFieldGoalPercentage()) / 2) + ((scoreModel.getHomeHighFieldGoalPercentage() + scoreModel.getHomeLowFieldGoalPercentage()) / 2))/2);
                        StatResult statResultThreePointer = new StatResult();
                        statResultThreePointer.setStatType("threePointAttempt");
                        statResultThreePointer.setForecastedNumber(forecastedThreePointAttempts);
                        statResultThreePointer.setActualNumber(threePointAttempts);
                        statResultThreePointer.setHighNum(backTestIngestObject.getNumHigh());
                        statResultThreePointer.setLowNum(backTestIngestObject.getNumLow());
                        StatResult statResultFieldGoalAttempts = new StatResult();
                        statResultFieldGoalAttempts.setStatType("fieldGoalAttempt");
                        statResultFieldGoalAttempts.setForecastedNumber(forecastedFieldGoalAttempts);
                        statResultFieldGoalAttempts.setActualNumber(fieldGoalAttempts);
                        statResultFieldGoalAttempts.setHighNum(backTestIngestObject.getNumHigh());
                        statResultFieldGoalAttempts.setLowNum(backTestIngestObject.getNumLow());
                        StatResult statResultFouls = new StatResult();
                        statResultFouls.setStatType("fouls");
                        statResultFouls.setForecastedNumber(forcastedFouls);
                        statResultFouls.setActualNumber(fouls);
                        statResultFouls.setHighNum(backTestIngestObject.getNumHigh());
                        statResultFouls.setLowNum(backTestIngestObject.getNumLow());
                        statResultFouls.setActualPoints(totalActualPoints);
                        statResultFouls.setPredictedPoints(totalPredictedPoints);
                        StatResult statResultDefensiveRebounds = new StatResult();
                        statResultDefensiveRebounds.setStatType("defensiveRebounds");
                        statResultDefensiveRebounds.setForecastedNumber(forcastedDefensiveRebounds);
                        statResultDefensiveRebounds.setActualNumber(dreb);
                        statResultDefensiveRebounds.setHighNum(backTestIngestObject.getNumHigh());
                        statResultDefensiveRebounds.setLowNum(backTestIngestObject.getNumLow());
                        StatResult statResultOffensiveRebounds = new StatResult();
                        statResultOffensiveRebounds.setStatType("offensiveRebounds");
                        statResultOffensiveRebounds.setForecastedNumber(forcastedOffensiveRebounds);
                        statResultOffensiveRebounds.setActualNumber(oreb);
                        statResultOffensiveRebounds.setHighNum(backTestIngestObject.getNumHigh());
                        statResultOffensiveRebounds.setLowNum(backTestIngestObject.getNumLow());
                        StatResult statResultBlocks = new StatResult();
                        statResultBlocks.setStatType("blocks");
                        statResultBlocks.setForecastedNumber(forcastedBlocks);
                        statResultBlocks.setActualNumber(blocks);
                        statResultBlocks.setHighNum(backTestIngestObject.getNumHigh());
                        statResultBlocks.setLowNum(backTestIngestObject.getNumLow());
                        StatResult statResultSteals = new StatResult();
                        statResultSteals.setStatType("steals");
                        statResultSteals.setForecastedNumber(forcastedSteals);
                        statResultSteals.setActualNumber(steals);
                        statResultSteals.setHighNum(backTestIngestObject.getNumHigh());
                        statResultSteals.setLowNum(backTestIngestObject.getNumLow());
                        statResultSteals.setActualPoints(totalActualPoints);
                        statResultSteals.setPredictedPoints(totalPredictedPoints);
                        StatResult statResultTurnovers = new StatResult();
                        statResultTurnovers.setStatType("turnovers");
                        statResultTurnovers.setForecastedNumber(forcastedTurnovers);
                        statResultTurnovers.setActualNumber(turnovers);
                        statResultTurnovers.setHighNum(backTestIngestObject.getNumHigh());
                        statResultTurnovers.setLowNum(backTestIngestObject.getNumLow());
                        StatResult statResultFreeThrows = new StatResult();
                        statResultFreeThrows.setStatType("freeThrows");
                        statResultFreeThrows.setForecastedNumber(forecastedFreeThrows);
                        statResultFreeThrows.setActualNumber(freethrows);
                        statResultFreeThrows.setHighNum(backTestIngestObject.getNumHigh());
                        statResultFreeThrows.setLowNum(backTestIngestObject.getNumLow());
                        StatResult statResultThreePointPct = new StatResult();
                        statResultThreePointPct.setStatType("threePointPercent");
                        statResultThreePointPct.setForecastedNumber(forecastedThreePointPercentage);
                        statResultThreePointPct.setActualNumber(threePointMade/threePointAttempts);
                        statResultThreePointPct.setHighNum(backTestIngestObject.getNumHigh());
                        statResultThreePointPct.setLowNum(backTestIngestObject.getNumLow());
                        StatResult statResultFreeThrowPct = new StatResult();
                        statResultFreeThrowPct.setStatType("freeThrowPct");
                        statResultFreeThrowPct.setForecastedNumber(forecastedFreeThrowPercentage);
                        statResultFreeThrowPct.setActualNumber(freeThrowMade/freeThrowAttempt);
                        statResultFreeThrowPct.setHighNum(backTestIngestObject.getNumHigh());
                        statResultFreeThrowPct.setLowNum(backTestIngestObject.getNumLow());
                        statResultFreeThrowPct.setActualPoints(totalActualPoints);
                        statResultFreeThrowPct.setPredictedPoints(totalPredictedPoints);
                        StatResult statResultFieldGoalPct = new StatResult();
                        statResultFieldGoalPct.setStatType("fieldGoalPct");
                        statResultFieldGoalPct.setForecastedNumber(forecastedFieldGoalPercentage);
                        statResultFieldGoalPct.setActualNumber(fieldGoalsMade/fieldGoalAttempts);
                        statResultFieldGoalPct.setHighNum(backTestIngestObject.getNumHigh());
                        statResultFieldGoalPct.setLowNum(backTestIngestObject.getNumLow());
                        threePointAttemptMap.put(statResultThreePointer, result);
                        fieldGoalAttemptMap.put(statResultFieldGoalAttempts, result);
                        foulMap.put(statResultFouls, result);
                        defensiveReboundMap.put(statResultDefensiveRebounds, result);
                        offensiveReboundMap.put(statResultOffensiveRebounds, result);
                        blockMap.put(statResultBlocks, result);
                        stealMap.put(statResultSteals, result);
                        turnoverMap.put(statResultTurnovers,result);
                        freeThrowMap.put(statResultFreeThrows,result);
                        threePointPctMap.put(statResultThreePointPct, result);
                        freeThrowPctMap.put(statResultFreeThrowPct, result);
                        fieldGoalPctMap.put(statResultFieldGoalPct, result);
                    }
                    gamesCompleted = gamesCompleted + 1;
                    System.out.println(gamesCompleted);
                }
                if (forward) {
                    localDate = localDate.plusDays(1);
                } else {
                    localDate = localDate.minusDays(1);
                }
                startingMoney = startingMoney + dailyNetMoney;
        }
        TreeMap<String, HashMap<StatResult, Boolean>> outputHashMap = new TreeMap<>();
            outputHashMap.put("threePointAttempt", threePointAttemptMap);
            outputHashMap.put("fieldGoalAttempt", fieldGoalAttemptMap);
            outputHashMap.put("fouls", foulMap);
        outputHashMap.put("defensiveRebounds", defensiveReboundMap);
        outputHashMap.put("blocks", blockMap);
        outputHashMap.put("steals", stealMap);
        outputHashMap.put("turnovers", turnoverMap);
        outputHashMap.put("offensiveRebounds", offensiveReboundMap);
        outputHashMap.put("freethrows", freeThrowMap);
        outputHashMap.put("threePointPct", threePointPctMap);
        outputHashMap.put("freeThrowPct", freeThrowPctMap);
        outputHashMap.put("fieldGoalPct", fieldGoalPctMap);
        System.out.println("predicted: " + totalPredictedPoints);
        System.out.println("actual: " + totalActualPoints);
        System.out.println("%: " + (double) totalPredictedPoints/totalActualPoints);
        testsComplete = testsComplete + 1;
        //printProgress();
        BackTestResult backTestResult = new BackTestResult();
        backTestResult.setPlayerGameLookBack(gameCount);
        backTestResult.setDoubleSquareRoot(backTestIngestObject.isDoubleSquareRoot());
        backTestResult.setLowerBlockPointFactor(backTestIngestObject.getLowerBlockPointFactor());
        backTestResult.setPointsReducedPerBlock(backTestIngestObject.getPointsReducedPerBlock());
        backTestResult.setPointsReducedPerRebound(backTestIngestObject.getPointsReducedPerRebound());
        backTestResult.setPointsReducedPerFoul(backTestIngestObject.getPointsReducedPerFoul());
        backTestResult.setCorrectPercent(((double) correctPredictions / (double) (correctPredictions + incorrectPredictions)) * 100);
        backTestResult.setPredictCorrect(correctPredictions);
        backTestResult.setPredictIncorrect(incorrectPredictions);
        backTestResult.setPredictOver(predictOver);
        backTestResult.setPredictUnder(predictUnder);
        backTestResult.setActualOver(actualOver);
        backTestResult.setActualUnder(actualUnder);
        backTestResult.setActualPoints(totalActualPoints);
        backTestResult.setExactResults(exactMatch);
        backTestResult.setExactLosses(exactLosses);
        backTestResult.setEstimatedPoints(totalPredictedPoints);
        backTestResult.setBlockExponent(backTestIngestObject.getBlockExponent());
        backTestResult.setModelOpponentBlocks(backTestIngestObject.isModelOpponentBlocks());
        backTestResult.setFactorPostBlock(backTestIngestObject.isFactorPostBlocks());
        backTestResult.setPointsReducedPerSteal(backTestIngestObject.getPointReductionPerSteal());
        backTestResult.setModelOpponentSteals(backTestIngestObject.isModelOpponentSteals());
        backTestResult.setPpgCorrect(ppgCorrect);
        backTestResult.setPpgIncorrect(ppgIncorrect);
        backTestResult.setGameTimeThreshold(backTestIngestObject.getGameTimeThreshold());
        backTestResult.setPointsReducedPerTurnover(backTestIngestObject.getPointsReducedPerTurnover());
        backTestResult.setDayLookbackCap(backTestIngestObject.getDayLookbackCap());
        backTestResult.setBetType(backTestIngestObject.getBetType());
        backTestResult.setHighBlockPointFactor(backTestIngestObject.getHighBlockPointFactor());
        backTestResult.setHighStealPointFactor(backTestIngestObject.getHighStealPointFactor());
        backTestResult.setLowerStealPointFactor(backTestIngestObject.getLowerStealPointFactor());
        backTestResult.setHighTurnoverPointFactor(backTestIngestObject.getHighTurnoverPointFactor());
        backTestResult.setLowerTurnoverPointFactor(backTestIngestObject.getLowerTurnoverPointFactor());
        backTestResult.setHighReboundPointFactor(backTestIngestObject.getHighReboundPointFactor());
        backTestResult.setLowerReboundPointFactor(backTestIngestObject.getLowerReboundPointFactor());
        backTestResult.setHighFoulPointFactor(backTestIngestObject.getHighFoulPointFactor());
        backTestResult.setLowerFoulPointFactor(backTestIngestObject.getLowerFoulPointFactor());
        backTestResult.setPointThreshold(backTestIngestObject.getPointThreshold());
        backTestResult.setAllowBelowZero(backTestIngestObject.isAllowBelowZero());
        backTestResult.setSquareRootTotal(backTestIngestObject.isSquareRootTotalPoints());
        backTestResult.setStartDate(backTestIngestObject.getStartDate());
        backTestResult.setGamesToTest(backTestIngestObject.getGamesToTest());
        backTestResult.setHomeTeamAdvantage(backTestIngestObject.getHomeTeamAdvantage());
        backTestResult.setModelOpponentTurnovers(backTestIngestObject.isModelOpponentTurnovers());
        backTestResult.setTestingPair(backTestIngestObject.getTestingPair());
        backTestResult.setOriginalResult(backTestIngestObject.getOriginalResult());
        backTestResult.setFractalWindow(backTestIngestObject.getFractalWindow());
        backTestResult.setEndingMoney(startingMoney);
        double delta =totalPredictedPoints - totalActualPoints;
        if(totalPredictedPoints - totalActualPoints < 0){
            delta = delta * -1;
        }
        backTestResult.setPointDelta(delta);
        if(testsComplete % printEveryNResults == 0) {
            now = Instant.now();
            delta = Duration.between(start, now).toMillis();
            rate = ((float) gamesCompleted / delta) * 1000;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ANSI_ORANGE).append("Games per second: ").append(rate).append("\n");
            stringBuilder.append("[").append(threadNum).append("] [").append(testsComplete).append("/").append(backTestIngestObjects.size()).append("]").append(startDate).append("]").append(ANSI_RESET);
            System.out.println(stringBuilder);
        }

        return outputHashMap;
    }

    public BackTestResult runBackTestVerbose(BackTestIngestObject backTestIngestObject) {
        playerStatFilter.setMinuteThreshold(backTestIngestObject.getGameTimeThreshold());
        localDate = backTestIngestObject.getStartDate();
        int gameCount = backTestIngestObject.getPlayerGameLookBack();
        int correctPredictions = 0;
        int incorrectPredictions = 0;
        int totalGames = 0;
        double spreadModification = 0;
        double spreadOddsModification = 0;
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

        if(getGamesFromMap(localDate).size() != 0) {
            float dailyNetMoney = 0.0f;
            List<Game> games = getGamesFromMap(localDate);

            //System.out.println(games);
            double betPerGame = startingMoney * betPercent;
            for (int i = 0; i < games.size(); i++) {
                Game game = (Game) games.get(i).clone();
                ScoreModel scoreModel = (ScoreModel) getScoreModelForGame(game, backTestIngestObject, game.getDate()).clone();

                //DYNAMIC UTILS
                mergeScores(scoreModel);
                //System.out.println("Score only range: " + scoreModel.getTotalHigh() + " || " + scoreModel.getTotalLow());
                if(backTestIngestObject.isSquareRootTotalPoints()){
                    squareRootTotalPoints(scoreModel);
                }
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

                if (backTestIngestObject.getBetType().equals("moneyline")) {
                    GameOdds gameOdds = gameOddsHashMap.get(game.getGameId());
                    if (gameOdds != null) {
                        mergeScores(scoreModel);
                        totalPredictedPoints = totalPredictedPoints + ((scoreModel.getTotalHigh() + scoreModel.getTotalLow()) / 2);
                        totalActualPoints = totalActualPoints + (game.getAwayPoints() + game.getHomePoints());
                        totalGames = totalGames + 1;
                        gamesCompleted = gamesCompleted + 1;
                        double homePredicted = ((scoreModel.getHomeHighPoints() + scoreModel.getHomeLowPoints()) / 2);
                        double awayPredicted = ((scoreModel.getAwayHighPoints() + scoreModel.getAwayLowPoints()) / 2);
                        //System.out.println("calculated awayPercent = " + awayPercentAboveHome);
                        double pointDifference = homePredicted - awayPredicted;
                        if (pointDifference <= backTestIngestObject.getPointThreshold() * -1 || pointDifference >= backTestIngestObject.getPointThreshold()) {
                            if (homePredicted > awayPredicted) {
                                //System.out.println("Implied Away Odds: " + impliedAwayOdds);
                                if (game.getHomePoints() > game.getAwayPoints()) {
                                    correctPredictions = correctPredictions + 1;
                                    //splitCorrect = splitCorrect + 1;
                                    dailyNetMoney = dailyNetMoney + (float) ((getBetAmountFromSpreadOdds(gameOdds.getHomeTeamMoneyLine(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getHomeTeamMoneyLine(), modifier) - 1)));
                                } else {
                                    incorrectPredictions = incorrectPredictions + 1;
                                    //splitIncorrect = splitIncorrect + 1;
                                    dailyNetMoney = dailyNetMoney - (float) getBetAmountFromSpreadOdds(gameOdds.getHomeTeamMoneyLine(), betPerGame);
                                }
                            } else {
                                if (game.getAwayPoints() > game.getHomePoints()) {
                                    correctPredictions = correctPredictions + 1;
                                    //splitCorrect = splitCorrect + 1;
                                    dailyNetMoney = dailyNetMoney + (float) ((getBetAmountFromSpreadOdds(gameOdds.getAwayTeamMoneyLine(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getAwayTeamMoneyLine(), modifier) - 1)));
                                } else {
                                    incorrectPredictions = incorrectPredictions + 1;
                                    //splitIncorrect = splitIncorrect + 1;
                                    dailyNetMoney = dailyNetMoney - (float) getBetAmountFromSpreadOdds(gameOdds.getAwayTeamMoneyLine(), betPerGame);
                                }
                            }
                        }
                        if ((correctPredictions + incorrectPredictions) > 0) {
                            float pct = ((float) correctPredictions / (float) (correctPredictions + incorrectPredictions)) * 100;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(ANSI_GREEN).append("$").append(startingMoney + dailyNetMoney).append(ANSI_RESET);
                            System.out.println(stringBuilder);
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(ANSI_GREEN).append("CORRECT PREDICTIONS: ").append(correctPredictions).append(" || ").append("INCORRECT PREDICTIONS: ")
                                    .append(incorrectPredictions).append(" (").append(d.format(pct)).append("%) EXACT MATCH: ").append(exactMatch)
//                                .append(" short: ").append(d.format(shortenedpct)).append(" Scorrect: ").append(shortenedCorrect).append(" Sincorrect: ").append(shortenedIncorrect)
//                                .append(" Inverted: ").append(d.format(invertedpct)).append("%").append( " InvertedOver: ").append(invertedOver)
//                                .append(" InvertedUnder: ").append(invertedUnder).append(" PredictOver: ").append(invertedPredictOver).append(" PredictUnder: ").append(invertedPredictUnder)
                                    .append(ANSI_RESET);
                            System.out.println(stringBuilder);
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(ANSI_GREEN).append("SETTINGS: Lookback: ").append(gameCount).append(" dblSquareRoot: ").append(backTestIngestObject.isDoubleSquareRoot()).append(" modelOpposingPitching: ").append(" PitchingDiffAdjustment: ").append(1).append("\n");
                            //stringBuilder.append(" Splits: ").append(splitPercentages).append("\n");
                            //stringBuilder.append("PPG CORRECT PREDICTIONS: ").append(ppgCorrect).append(" || ").append("PPG INCORRECT PREDICTIONS: ").append(ppgIncorrect).append("(").append(d.format(ppgpct)).append("%)").append("\n");
                            stringBuilder.append("PREDICTIONS OVER: ").append(predictOver).append(" PREDICTIONS UNDER: ")
                                    .append(predictUnder).append(" || ACTUAL OVER: ").append(actualOver).append(" ACTUAL UNDER: ")
                                    .append(actualUnder).append(" || TotalPredictedPoints: ").append(totalPredictedPoints).append(" TotalActualPoints: ")
                                    .append(totalActualPoints).append(" (").append((totalPredictedPoints - totalActualPoints) / (totalActualPoints) * 100)
                                    .append("%)").append(ANSI_RESET);
                            System.out.println(stringBuilder);
                        }
                    }
                }
                else if (backTestIngestObject.getBetType().equals("overunder")) {
                    if (gameOddsHashMap.get(game.getGameId()) != null) {
                        System.out.println(game.getHomeTeam().getTeamName() + " vs " + game.getAwayTeam().getTeamName() + game.getDate());
                        GameOdds gameOdds = gameOddsHashMap.get(game.getGameId());
                        StaticScoreModelUtils.addHomeTeamAdvantage(scoreModel, backTestIngestObject.getHomeTeamAdvantage());
                        mergeScores(scoreModel);
                        System.out.println("O/U:" + gameOdds.getOverUnder());
                        System.out.println("Actual Score: " + (game.getHomePoints() + game.getAwayPoints()));
                        totalActualPoints = totalActualPoints + (game.getHomePoints() + game.getAwayPoints());
                        double predictedPoints = (scoreModel.getTotalHigh() + scoreModel.getTotalLow()) / 2;
                        System.out.println("predicted points : " + predictedPoints);
                        int actualPoints = game.getHomePoints() + game.getAwayPoints();
                        if(game.getHomePoints() + game.getAwayPoints() != gameOdds.getOverUnder()){
                            double pointClass = predictedPoints - gameOdds.getOverUnder();
                            System.out.println("pointclass=" + pointClass);
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
                        totalPredictedPoints = totalPredictedPoints + ((scoreModel.getTotalHigh() + scoreModel.getTotalLow()) / 2);
                        totalGames = totalGames + 1;
                        //if (correctPredictions + incorrectPredictions > 0) {
                           // BigDecimal b1 = new BigDecimal(((double) correctPredictions / (double) (correctPredictions + incorrectPredictions)) * 100);
                           // BigDecimal b2 = new BigDecimal(((double) ppgCorrect / (double) (ppgCorrect + ppgIncorrect)) * 100);
                            MathContext m = new MathContext(4);

                            String progressString = ANSI_GREEN + "CORRECT PREDICTIONS: " + correctPredictions + " || " + "INCORRECT PREDICTIONS: " + incorrectPredictions + " (" + (double) correctPredictions / (double) (correctPredictions + incorrectPredictions) + "%) EXACT MATCH: " + exactMatch;
                            progressString = progressString + ANSI_RESET;

                            System.out.println(progressString);
                            System.out.println(ANSI_GREEN + "SETTINGS: Lookback: " + gameCount + " ptsReduction: " + backTestIngestObject.getPointsReducedPerBlock() +
                                    " ptsReductionPerSteal: " + backTestIngestObject.getPointReductionPerSteal() +
                                    " ptsReducedPerTO: " + backTestIngestObject.getPointsReducedPerTurnover() +
                                    " lowBlockFactor: " + backTestIngestObject.getLowerBlockPointFactor() + " highBlk: " + backTestIngestObject.getHighBlockPointFactor() +
                                    " lowStl: " + backTestIngestObject.getLowerStealPointFactor() + "highStl: " + backTestIngestObject.getHighStealPointFactor() +
                                    " dblsqrt: " + backTestIngestObject.isDoubleSquareRoot() +
                                    " modelOpponentBlocks: " + backTestIngestObject.isModelOpponentBlocks() +
                                    " modelOpponentSteals: " + backTestIngestObject.isModelOpponentSteals() +
                                    " gameTimeThreshold: " + backTestIngestObject.getGameTimeThreshold() + ANSI_RESET);
                            //System.out.println(ANSI_GREEN + "PPG CORRECT PREDICTIONS: " + ppgCorrect + " || " + "PPG INCORRECT PREDICTIONS: " + ppgIncorrect + "(" + b2.round(m).doubleValue() + "%)" + ANSI_RESET);
                            System.out.println(ANSI_GREEN + "PREDICTIONS OVER: " + predictOver + " PREDICTIONS UNDER: " + predictUnder + " || ACTUAL OVER: " + actualOver + " ACTUAL UNDER: " + actualUnder + " || TotalPredictedPoints: " + totalPredictedPoints + " TotalActualPoints: " + totalActualPoints + " (" + ((totalPredictedPoints - totalActualPoints) / (totalActualPoints) * 100) + "%)" + ANSI_RESET);
                            //System.out.println(ANSI_GREEN + backTestResults + ANSI_RESET);
                            System.out.println(ANSI_GREEN + "[" + threadNum + "] " + "[" + (backTestResults.size() + 1) + "/" + backTestIngestObjects.size() + "]" + ANSI_RESET);
                       // }
                    }
                } else if (backTestIngestObject.getBetType().equals("spread")) {
                    GameOdds gameOdds = gameOddsHashMap.get(game.getGameId());
                    if (gameOdds != null) {
                        if (gameOdds.getAwayTeamSpreadOdds() != 0 && gameOdds.getHomeTeamSpreadOdds() != 0) {
                            StaticScoreModelUtils.addHomeTeamAdvantage(scoreModel, backTestIngestObject.getHomeTeamAdvantage());
                            mergeScores(scoreModel);
                            //totalPredictedPoints = totalPredictedPoints + (scoreModel.getAwayPointsPerGame() + scoreModel.getHomePointsPerGame());
                            totalPredictedPoints = totalPredictedPoints + ((scoreModel.getTotalHigh() + scoreModel.getTotalLow()) / 2);
                            totalActualPoints = totalActualPoints + (game.getAwayPoints() + game.getHomePoints());
                            float HomePredictedPoints = (float) (scoreModel.getHomeHighPoints() + scoreModel.getHomeLowPoints()) / 2;
                            float AwayPredictedPoints = (float) (scoreModel.getAwayHighPoints() + scoreModel.getAwayLowPoints()) / 2;
//                            float HomePredictedPoints = (float) scoreModel.getHomePointsPerGame();
//                            float AwayPredictedPoints = (float) scoreModel.getAwayPointsPerGame();
                            float pointDifference = HomePredictedPoints - AwayPredictedPoints;
                            double actualDifference = game.getHomePoints() - game.getAwayPoints();
                            float pointClass = (pointDifference - ((float) gameOdds.getHomeTeamSpread()* -1));
                            System.out.println("(Spread) Home Predicted: " + HomePredictedPoints);
                            System.out.println("(Spread) Away Predicted: " + AwayPredictedPoints);
                            boolean result = false;
                            System.out.println("pointClass = " + pointClass);
                            if (pointClass <= backTestIngestObject.getPointThreshold() * -1 || pointClass >= backTestIngestObject.getPointThreshold()) {
                                if (HomePredictedPoints > AwayPredictedPoints) {
                                    double invertedHomeSpread = getModifiedSpread(gameOdds.getHomeTeamSpread(),spreadModification) * -1;
                                    if (pointDifference >= invertedHomeSpread) {
                                        System.out.println("Selecting " + game.getHomeTeamName() + " @ " + gameOdds.getHomeTeamSpread());
                                        System.out.println(game.getHomeTeamName() + " is predicted to win by " + pointDifference + " points");
                                        System.out.println(game.getHomeTeamName() + " spread is " + (invertedHomeSpread * -1));
                                        System.out.println(game.getHomeTeamName() + " Original Spread was " + gameOdds.getHomeTeamSpread());
                                        double spreadOdds = getModifiedSpread(gameOdds.getHomeTeamSpreadOdds(),spreadOddsModification);
                                        if(actualDifference == spreadOdds || actualDifference == spreadOdds * -1){
                                            System.out.println(" Exact Match. ");
                                        }else if (actualDifference > invertedHomeSpread) {
                                            result = true;
                                            correctPredictions = correctPredictions + 1;
                                            //splitCorrect = splitCorrect + 1;
                                            double winnings = (float) ((getBetAmountFromSpreadOddsWithConfidenceFactor(confidence,spreadOdds, betPerGame) * (applyWinningSpreadOdds(spreadOdds, modifier) - 1)));
                                            dailyNetMoney = dailyNetMoney + (float) winnings;
                                            System.out.println("This is a win. Odds were " + spreadOdds + " Current Balance is " + startingMoney + "Add $" + winnings);
                                        } else {
                                            result = false;
                                            incorrectPredictions = incorrectPredictions + 1;
                                            //splitIncorrect = splitIncorrect + 1;
                                            double losings = (float) getBetAmountFromSpreadOddsWithConfidenceFactor(confidence,spreadOdds, betPerGame);
                                            dailyNetMoney = (float) (dailyNetMoney - losings);
                                            System.out.println("This is a loss. Odds were " + spreadOdds + " Lose $" + losings);
                                        }
                                    } else {
                                        System.out.println("Selecting " + game.getAwayTeamName() + " @ " + gameOdds.getAwayTeamSpread());
                                        System.out.println(game.getAwayTeamName() + " is predicted to win by " + (pointDifference) + " points");
                                        System.out.println(game.getAwayTeamName() + " spread is " + (invertedHomeSpread));
                                        System.out.println(game.getAwayTeamName() + " Original Spread was " + gameOdds.getAwayTeamSpread());
                                        double spreadOdds = getModifiedSpread(gameOdds.getAwayTeamSpreadOdds(),spreadOddsModification);
                                        if(actualDifference == spreadOdds || actualDifference == spreadOdds * -1){
                                            System.out.println(" Exact Match. ");
                                        } else if ((actualDifference*-1) > invertedHomeSpread * -1) {
                                            result = true;
                                            double winnings = (float) ((getBetAmountFromSpreadOddsWithConfidenceFactor(confidence,spreadOdds, betPerGame) * (applyWinningSpreadOdds(spreadOdds, modifier) - 1)));

                                            dailyNetMoney = (float) (dailyNetMoney + winnings);
                                            correctPredictions = correctPredictions + 1;
                                            //splitCorrect = splitCorrect + 1;
                                            System.out.println("This is a win. Odds were " + spreadOdds + " Current Balance is " + startingMoney + "Add $" + winnings);

                                        } else {
                                            result = false;
                                            incorrectPredictions = incorrectPredictions + 1;
                                            //splitIncorrect = splitIncorrect + 1;
                                            double losings = (float) getBetAmountFromSpreadOddsWithConfidenceFactor(confidence,spreadOdds, betPerGame);
                                            dailyNetMoney = (float) (dailyNetMoney - losings);
                                            System.out.println("This is a loss. Odds were " + spreadOdds + " Lose $" + losings);
                                        }
                                    }
                                } else {
                                    double inverted = (pointDifference * -1);
                                    double invertedAwaySpread =  getModifiedSpread(gameOdds.getAwayTeamSpread(),spreadModification) * -1;
                                    if (inverted >= invertedAwaySpread) {
                                        System.out.println("Selecting " + game.getAwayTeamName() + " @ " + gameOdds.getAwayTeamSpread());
                                        System.out.println(game.getAwayTeamName() + " is predicted to win by " + (pointDifference) + " points");
                                        System.out.println(game.getAwayTeamName() + " spread is " + (invertedAwaySpread * -1));
                                        System.out.println(game.getAwayTeamName() + " Original Spread was " + gameOdds.getAwayTeamSpread());
                                        double spreadOdds = getModifiedSpreadOdds(gameOdds.getAwayTeamSpreadOdds(), spreadOddsModification);
                                        if(actualDifference == spreadOdds || actualDifference == spreadOdds * -1){
                                            System.out.println(" Exact Match. ");
                                        } else
                                        if (game.getAwayPoints() - game.getHomePoints() > invertedAwaySpread) {
                                            result = true;
                                            correctPredictions = correctPredictions + 1;
                                            //splitCorrect = splitCorrect + 1;
                                            double winnings = (float) ((getBetAmountFromSpreadOddsWithConfidenceFactor(confidence,spreadOdds, betPerGame) * (applyWinningSpreadOdds(spreadOdds, modifier) - 1)));

                                            dailyNetMoney = dailyNetMoney + (float) winnings;
                                            System.out.println("This is a win. Odds were " + spreadOdds + " Current Balance is " + startingMoney + "Add $" + winnings);
                                        } else {
                                            result = false;

                                            incorrectPredictions = incorrectPredictions + 1;
                                            double losings = (float) getBetAmountFromSpreadOddsWithConfidenceFactor(confidence,spreadOdds, betPerGame);
                                            //splitIncorrect = splitIncorrect + 1;
                                            dailyNetMoney = (float) (dailyNetMoney - losings);
                                            System.out.println("This is a loss. Odds were " + spreadOdds + " Lose $" + losings);

                                        }
                                    } else {
                                        System.out.println("Selecting " + game.getHomeTeamName() + " @ " + gameOdds.getHomeTeamSpreadOdds());
                                        System.out.println(game.getHomeTeamName() + " is predicted to win by " + pointDifference + " points");
                                        System.out.println(game.getHomeTeamName() + " spread is " + (invertedAwaySpread));
                                        System.out.println(game.getHomeTeamName() + " Original Spread was " + gameOdds.getHomeTeamSpread());
                                        double spreadOdds = getModifiedSpreadOdds(gameOdds.getHomeTeamSpreadOdds(), spreadOddsModification);
                                        if(actualDifference == spreadOdds || actualDifference == spreadOdds * -1){
                                            System.out.println(" Exact Match. ");
                                        }else
                                        if (actualDifference > invertedAwaySpread * -1) {
                                            result = true;
                                            correctPredictions = correctPredictions + 1;
                                            //splitCorrect = splitCorrect + 1;
                                            double winnings = (float) ((getBetAmountFromSpreadOddsWithConfidenceFactor(confidence,spreadOdds, betPerGame) * (applyWinningSpreadOdds(spreadOdds, modifier) - 1)));
                                            dailyNetMoney = dailyNetMoney + (float) winnings;
                                            System.out.println("This is a win. Odds were " + spreadOdds + " Current Balance is " + startingMoney + "Add $" + winnings);

                                        } else {
                                            result = false;
                                            double losings = (float) getBetAmountFromSpreadOddsWithConfidenceFactor(confidence,spreadOdds, betPerGame);

                                            dailyNetMoney = (float) (dailyNetMoney - losings);
                                            incorrectPredictions = incorrectPredictions + 1;
                                            //splitIncorrect = splitIncorrect + 1;
                                            System.out.println("This is a loss. Odds were " + spreadOdds + " Lose $" + losings);

                                        }

                                    }

                                }
                            }
                        }
                        totalGames = totalGames + 1;

                        System.out.println(game.getHomeTeamName() + " spread was " + gameOdds.getHomeTeamSpread());
                        System.out.println(game.getHomeTeamName() + " scored " + game.getHomePoints());
                        System.out.println(game.getAwayTeamName() + " scored " + game.getAwayPoints());
                        String activeColor;
                        if(recording){
                            activeColor = ANSI_GREEN;
                        }else{
                            activeColor = ANSI_RED;
                        }
                        if (correctPredictions + incorrectPredictions > 0) {
                            BigDecimal b1 = new BigDecimal(((double) correctPredictions / (double) (correctPredictions + incorrectPredictions)) * 100);
                            //BigDecimal b2 = new BigDecimal(((double)ppgCorrect / (double)(ppgCorrect + ppgIncorrect)) * 100);
                            MathContext m = new MathContext(4);
                            System.out.println(activeColor + "$" + (startingMoney + dailyNetMoney) + ANSI_RESET);
                            String progressString = activeColor + "CORRECT PREDICTIONS: " + correctPredictions + " || " + "INCORRECT PREDICTIONS: " + incorrectPredictions + " (" + b1.round(m).doubleValue() + "%) EXACT MATCH: " + exactMatch;
                            System.out.println(progressString);
                            System.out.println(activeColor + "SETTINGS: Lookback: " + gameCount + " ptsReduction: " + backTestIngestObject.getPointsReducedPerBlock() +
                                    " ptsReductionPerSteal: " + backTestIngestObject.getPointReductionPerSteal() +
                                    " ptsReducedPerTO: " + backTestIngestObject.getPointsReducedPerTurnover() +
                                    " lowBlockFactor: " + backTestIngestObject.getLowerBlockPointFactor() +
                                    " dblsqrt: " + backTestIngestObject.isDoubleSquareRoot() + " rplcBlksWBPG: " + backTestIngestObject.isReplaceLowBlocksWithBPG() +
                                    " blockExp: " + backTestIngestObject.getBlockExponent() +
                                    " modelOpponentBlocks: " + backTestIngestObject.isModelOpponentBlocks() +
                                    " modelOpponentSteals: " + backTestIngestObject.isModelOpponentSteals() +
                                    " gameTimeThreshold: " + backTestIngestObject.getGameTimeThreshold() + ANSI_RESET);
                            //System.out.println(ANSI_GREEN + "PPG CORRECT PREDICTIONS: " + ppgCorrect + " || " + "PPG INCORRECT PREDICTIONS: " + ppgIncorrect + "(" + b2.round(m).doubleValue() + "%)" + ANSI_RESET);
                            //System.out.println(ANSI_GREEN + "PREDICTIONS OVER: " + predictOver + " PREDICTIONS UNDER: " + predictUnder + " || ACTUAL OVER: " + actualOver + " ACTUAL UNDER: " + actualUnder + " || TotalPredictedPoints: " + totalPredictedPoints + " TotalActualPoints: " + totalActualPoints + " (" + ((totalPredictedPoints - totalActualPoints) / (totalActualPoints) * 100) + "%)" + ANSI_RESET);
                            //System.out.println(ANSI_GREEN + backTestResults + ANSI_RESET);
                            System.out.println(activeColor + "[" + threadNum + "] " + "[" + (backTestResults.size() + 1) + "/" + backTestIngestObjects.size() + "]" + ANSI_RESET);
                        }
                    }
                }
                now = Instant.now();
                delta = Duration.between(start, now).toMillis();
                rate = ((float) gamesCompleted / delta) * 1000;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(ANSI_ORANGE).append("Games per second: ").append(rate).append("\n");
                stringBuilder.append("[").append(threadNum).append("] [").append(testsComplete + 1).append("/").append(backTestIngestObjects.size()).append("]").append(ANSI_RESET);
                System.out.println(stringBuilder);
                System.gc();
            }
            if (forward) {
                localDate = localDate.plusDays(1);
            } else {
                localDate = localDate.minusDays(1);
            }
            startingMoney = startingMoney + dailyNetMoney;
        }

        printProgress();
        BackTestResult backTestResult = new BackTestResult();
        backTestResult.setPlayerGameLookBack(gameCount);
        backTestResult.setDoubleSquareRoot(backTestIngestObject.isDoubleSquareRoot());
        backTestResult.setLowerBlockPointFactor(backTestIngestObject.getLowerBlockPointFactor());
        backTestResult.setPointsReducedPerBlock(backTestIngestObject.getPointsReducedPerBlock());
        backTestResult.setPointsReducedPerRebound(backTestIngestObject.getPointsReducedPerRebound());
        backTestResult.setPointsReducedPerFoul(backTestIngestObject.getPointsReducedPerFoul());
        backTestResult.setCorrectPercent(((double) correctPredictions / (double) (correctPredictions + incorrectPredictions)) * 100);
        backTestResult.setPredictCorrect(correctPredictions);
        backTestResult.setPredictIncorrect(incorrectPredictions);
        backTestResult.setPredictOver(predictOver);
        backTestResult.setPredictUnder(predictUnder);
        backTestResult.setActualOver(actualOver);
        backTestResult.setActualUnder(actualUnder);
        backTestResult.setActualPoints(totalActualPoints);
        backTestResult.setExactResults(exactMatch);
        backTestResult.setExactLosses(exactLosses);
        backTestResult.setEstimatedPoints(totalPredictedPoints);
        backTestResult.setBlockExponent(backTestIngestObject.getBlockExponent());
        backTestResult.setModelOpponentBlocks(backTestIngestObject.isModelOpponentBlocks());
        backTestResult.setFactorPostBlock(backTestIngestObject.isFactorPostBlocks());
        backTestResult.setPointsReducedPerSteal(backTestIngestObject.getPointReductionPerSteal());
        backTestResult.setModelOpponentSteals(backTestIngestObject.isModelOpponentSteals());
        backTestResult.setPpgCorrect(ppgCorrect);
        backTestResult.setPpgIncorrect(ppgIncorrect);
        backTestResult.setGameTimeThreshold(backTestIngestObject.getGameTimeThreshold());
        backTestResult.setPointsReducedPerTurnover(backTestIngestObject.getPointsReducedPerTurnover());
        backTestResult.setDayLookbackCap(backTestIngestObject.getDayLookbackCap());
        backTestResult.setBetType(backTestIngestObject.getBetType());
        backTestResult.setHighBlockPointFactor(backTestIngestObject.getHighBlockPointFactor());
        backTestResult.setHighStealPointFactor(backTestIngestObject.getHighStealPointFactor());
        backTestResult.setLowerStealPointFactor(backTestIngestObject.getLowerStealPointFactor());
        backTestResult.setHighTurnoverPointFactor(backTestIngestObject.getHighTurnoverPointFactor());
        backTestResult.setLowerTurnoverPointFactor(backTestIngestObject.getLowerTurnoverPointFactor());
        backTestResult.setHighReboundPointFactor(backTestIngestObject.getHighReboundPointFactor());
        backTestResult.setLowerReboundPointFactor(backTestIngestObject.getLowerReboundPointFactor());
        backTestResult.setHighFoulPointFactor(backTestIngestObject.getHighFoulPointFactor());
        backTestResult.setLowerFoulPointFactor(backTestIngestObject.getLowerFoulPointFactor());
        backTestResult.setEndingMoney(startingMoney);
        backTestResult.setHomeTeamAdvantage(backTestIngestObject.getHomeTeamAdvantage());
        backTestResult.setSquareRootTotal(backTestIngestObject.isSquareRootTotalPoints());
        backTestResult.setModelOpponentTurnovers(backTestIngestObject.isModelOpponentTurnovers());
        backTestResult.setStartDate(backTestIngestObject.getStartDate());
        backTestResult.setPointThreshold(backTestIngestObject.getPointThreshold());
        backTestResult.setGamesToTest(backTestIngestObject.getGamesToTest());
        backTestResult.setTestingPair(backTestResult.getTestingPair());
        backTestResult.setOriginalResult(backTestIngestObject.getOriginalResult());
        backTestResult.setAllowBelowZero(backTestIngestObject.isAllowBelowZero());
        backTestResult.setFractalWindow(backTestIngestObject.getFractalWindow());
        return backTestResult;
    }


    public static void mergeScores(ScoreModel scoreModel) {
        scoreModel.setTotalHigh(scoreModel.getHomeHighPoints() + scoreModel.getAwayHighPoints());
        scoreModel.setTotalLow(scoreModel.getHomeLowPoints() + scoreModel.getAwayLowPoints());
    }

    public ScoreModel getScoreModelForGame(Game game, BackTestIngestObject backTestIngestObject, Date date) {
        StatCacheSettingsObject compareObject = new StatCacheSettingsObject();
        compareObject.setGameTimeThreshold(backTestIngestObject.getGameTimeThreshold());
        compareObject.setPlayerGameLookback(backTestIngestObject.getPlayerGameLookBack());
        compareObject.setDoubleSquareRoot(backTestIngestObject.isDoubleSquareRoot());
        compareObject.setAllowBelowZero(backTestIngestObject.isAllowBelowZero());
        compareObject.setNumHigh(backTestIngestObject.getNumHigh());
        compareObject.setNumLow(backTestIngestObject.getNumLow());
        int hashCode = compareObject.hashCode();
        if(gameCache.get(hashCode) != null){
            if(gameCache.get(hashCode).get(game.getGameId()) != null){
                return (ScoreModel) gameCache.get(hashCode).get(game.getGameId()).clone();
            }else{
                ScoreModel scoreModel = new ScoreModel();
                HashMap<Integer,ScoreModel> gameMap = gameCache.get(hashCode);
                Team awayTeam = (Team) game.getAwayTeam().clone();
                awayTeam.setPlayers(gameFinder.getPlayerListFromPreviousGameFromDB(game, game.getAwayTeam().getTeamId()));
                Team homeTeam = (Team) game.getHomeTeam().clone();
                homeTeam.setPlayers(gameFinder.getPlayerListFromPreviousGameFromDB(game, game.getHomeTeam().getTeamId()));
            //    gameCalculatorClass.experimentalTallying(backTestIngestObject, game, teamStatFetcher, date, awayTeam, scoreModel, false, false, backTestIngestObject.getNumHigh(), backTestIngestObject.getNumLow());
            //    gameCalculatorClass.experimentalTallying(backTestIngestObject, game,  teamStatFetcher, date, homeTeam, scoreModel, true, false, backTestIngestObject.getNumHigh(), backTestIngestObject.getNumLow());
//            gameCalculatorClass.tallyTeamLastNGames(backTestIngestObject, date, playerStatFilter,
//                    awayTeam, scoreModel, false,false, backTestIngestObject.getDayLookbackCap(), backTestIngestObject.getGameTimeThreshold(), moreInfo);
//            gameCalculatorClass.tallyTeamLastNGames(backTestIngestObject, date, playerStatFilter,
//                    homeTeam, scoreModel,false, true, backTestIngestObject.getDayLookbackCap(),backTestIngestObject.getGameTimeThreshold(), moreInfo);
                gameMap.put(game.getGameId(), scoreModel);
                return (ScoreModel) scoreModel.clone();
            }
        } else{
            CacheSettingsObject cacheSettingsObject = new CacheSettingsObject();
            cacheSettingsObject.setPlayerGameLookback(backTestIngestObject.getPlayerGameLookBack());
            cacheSettingsObject.setDoubleSquareRoot(backTestIngestObject.isDoubleSquareRoot());
            cacheSettingsObject.setAllowBelowZero(backTestIngestObject.isAllowBelowZero());
            cacheSettingsObject.setGameTimeThreshold(backTestIngestObject.getGameTimeThreshold());
            HashMap<Integer, ScoreModel> gameMap = new HashMap<>();
            Team awayTeam = (Team) game.getAwayTeam().clone();
            awayTeam.setPlayers(gameFinder.getPlayerListFromPreviousGameFromDB(game, game.getAwayTeam().getTeamId()));
            Team homeTeam = (Team) game.getHomeTeam().clone();
            homeTeam.setPlayers(gameFinder.getPlayerListFromPreviousGameFromDB(game, game.getHomeTeam().getTeamId()));
            ScoreModel scoreModel = new ScoreModel();
        //    gameCalculatorClass.experimentalTallying(backTestIngestObject,game,  teamStatFetcher, date, awayTeam, scoreModel, false, false, backTestIngestObject.getNumHigh(), backTestIngestObject.getNumLow());
        //    gameCalculatorClass.experimentalTallying(backTestIngestObject,  game, teamStatFetcher, date, homeTeam, scoreModel, true, false, backTestIngestObject.getNumHigh(), backTestIngestObject.getNumLow());
//            gameCalculatorClass.tallyTeamLastNGames(backTestIngestObject, date, playerStatFilter,
//                    awayTeam, scoreModel, false,false, backTestIngestObject.getDayLookbackCap(), backTestIngestObject.getGameTimeThreshold(), moreInfo);
//            gameCalculatorClass.tallyTeamLastNGames(backTestIngestObject, date, playerStatFilter,
//                    homeTeam, scoreModel,false, true, backTestIngestObject.getDayLookbackCap(),backTestIngestObject.getGameTimeThreshold(), moreInfo);

            gameMap.put(game.getGameId(), scoreModel);
            gameCache.put(cacheSettingsObject.hashCode(), gameMap);
            return (ScoreModel) scoreModel.clone();
        }
    }

    private GameOdds getGameOddsFromCache(int gameId) {
        if (gameOddsHashMap.get(gameId) != null) {
            return gameOddsHashMap.get(gameId);
        } else {
            return null;
        }
    }

    private List<Game> getGamesFromMap(LocalDate localDate) {
        if (dayGameMap.get(localDate) != null) {
            return dayGameMap.get(localDate);
        } else {
            List<Game> games = gameFinder.findGamesOnDateFromDB(localDate);
            dayGameMap.put(localDate, games);
            return games;
        }
    }


    public double getDailyVol(List<Double> splits) {
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

    public void printProgress() {
        if ((testsComplete + 1) % printEveryNResults == 0) {
            now = Instant.now();
            delta = Duration.between(start, now).toMillis();
            rate = ((float) gamesCompleted / delta) * 1000;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ANSI_ORANGE).append("Games per second: ").append(rate).append("\n");
            stringBuilder.append("[").append(threadNum).append("] [").append(testsComplete + 1).append("/").append(backTestIngestObjects.size()).append("]").append(ANSI_RESET);
            System.out.println(stringBuilder);
            System.gc();
        }
    }
    public double getModifiedSpreadOdds(double spreadOdds, double modifier){
        return spreadOdds  + modifier;
    }

    public double getModifiedSpread(double originalPointSpread, double modifier){
        return originalPointSpread  + modifier;
    }

    public double getBetAmountFromSpreadOddsWithConfidenceFactor(double confidenceFactor, double spreadOdds, double target) {
        double decimalPayOut = applyWinningSpreadOdds(spreadOdds, 0);
//        double temp = (target*(1+(((confidenceFactor - 60)*25)/100)));
        // double temp = (target*(1+(((confidenceFactor)*2)/100)));
            return target / decimalPayOut;

    }

    public double getBetAmountFromSpreadOdds(double spreadOdds, double target) {
        double decimalPayOut = applyWinningSpreadOdds(spreadOdds, 0);
        if (decimalPayOut >= 1) {
            return target / decimalPayOut;
        } else {
            return target / decimalPayOut;
        }
    }

    public double applyWinningSpreadOdds(double spreadOdds, double modifier) {
        if (spreadOdds >= 100 || spreadOdds <= -100) {
            if (spreadOdds < 0) {
                return 1 - (100 / (spreadOdds));
            } else {
                return 1 + (spreadOdds / 100);
            }
        } else {
            return 1 + (spreadOdds);
        }
    }
    public void squareRootTotalPoints(ScoreModel scoreModel){
        //double midPointDistanceToLower = ((scoreModel.getTotalHigh() + scoreModel.getTotalLow()) / 2) - scoreModel.getTotalLow();
        double awayMidPointDistance = ((scoreModel.getAwayHighPoints() + scoreModel.getAwayLowPoints()) / 2) - scoreModel.getAwayLowPoints();
        double awayAmountToAddBack = Math.sqrt(awayMidPointDistance);
        //System.out.println("Amount to add back: " + awayAmountToAddBack + " Distance Mid to low=" + awayMidPointDistance);
        scoreModel.setAwayHighPoints(scoreModel.getAwayHighPoints() - (1 * awayAmountToAddBack));
        scoreModel.setAwayLowPoints(scoreModel.getAwayLowPoints() + (1 * awayAmountToAddBack));
        //System.out.println("new Away High " + scoreModel.getAwayHighPoints() + " : new Low " + scoreModel.getAwayLowPoints());
        double homeMidPointDistance = ((scoreModel.getHomeHighPoints() + scoreModel.getHomeLowPoints()) / 2) - scoreModel.getHomeLowPoints();
        double homeAmountToAddBack = Math.sqrt(homeMidPointDistance);
        //System.out.println("Amount to add back: " + homeAmountToAddBack + " Distance Mid to low=" + homeMidPointDistance);
        scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() - (1 * homeAmountToAddBack));
        scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() + (1 * homeAmountToAddBack));
        //System.out.println("new Home High " + scoreModel.getHomeHighPoints() + " : new Low " + scoreModel.getHomeLowPoints());

    }

}
