package BaseballQuant.Threads;

import BaseballQuant.Model.*;
import BaseballQuant.Model.CacheObjects.CacheSettingsObject;
import BaseballQuant.Static.StaticConfigurations;
import BaseballQuant.Util.*;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

public class BackTestThread extends Thread {
    private ThreadMonitor threadMonitor;
    private List<BackTestIngestObject> backTestIngestObjects;
    private List<BackTestResult> backTestResults;
    private HashMap<Integer, MLBGameOdds> mlbGameOddsHashMap;
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_ORANGE = "\033[38;5;214m";
    private boolean moreInfo = false;
    private ScrapingProxy scrapingProxy;
    private GameFinder gameFinder;
    private boolean newGames = false;
    private int threadNum;
    private TeamStatFetcher teamStatFetcher;
    private GameCalculatorClass gameCalculatorClass;
    private HashMap<CacheSettingsObject, HashMap<Integer, ScoreModel>> gameCache;
    private HashMap<LocalDate, List<MLBGame>> dayGameMap;
    private HashMap<String, Double> dayPerformanceMap;
    private LocalDate localDate;
    private boolean useStaticParams;
    List<IndividualBet> betList = new ArrayList<>();
    //TIME VARS//
    private Instant start;
    private int gamesCompleted;
    private Instant now;
    private long delta;
    private float rate;
    private final DecimalFormat d = new DecimalFormat("#.##");
    private final int printEveryNGames = 1;
    private final int printEveryNResults = 250;
    PlayerStatFilter playerStatFilter;
    private final boolean verbose = true;
    private boolean forward;
    private boolean caching;
    private double startingMoney;
    private int modifier = 0;
    List<CacheSettingsObject> completedSettings = new ArrayList<>();


    public BackTestThread(List<BackTestIngestObject> backTestIngestObjects, HashMap<Integer, MLBGameOdds> mlbGameOddsHashMap, List<MLBGame> gameList,
                          ThreadMonitor threadMonitor) {
        this.backTestIngestObjects = backTestIngestObjects;
        this.threadMonitor = threadMonitor;
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
        dayPerformanceMap = new HashMap<>();
        gamesCompleted = 0;
        forward = false;
    }

    public void setUseStaticParams(boolean useStaticParams) {
        this.useStaticParams = useStaticParams;
    }

    public boolean isCaching() {
        return caching;
    }

    public void setCaching(boolean caching) {
        this.caching = caching;
    }

    public boolean isForward() {
        return forward;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public void setScrapingProxy(ScrapingProxy scrapingProxy) {
        this.scrapingProxy = scrapingProxy;
    }

    public void setGameCache(HashMap<CacheSettingsObject, HashMap<Integer, ScoreModel>> gameCache) {
        this.gameCache = (HashMap<CacheSettingsObject, HashMap<Integer, ScoreModel>>) gameCache.clone();
    }

    public HashMap<CacheSettingsObject, HashMap<Integer, ScoreModel>> getGameCache() {
        return gameCache;
    }

    public void run() {
        System.out.println("start backtest");
        if (useStaticParams) {
            double cap = backTestIngestObjects.get(0).getGamesToTest();
            LocalDate startDate = backTestIngestObjects.get(0).getStartDate();
            backTestIngestObjects = StaticConfigurations.backTestIngestObjectsList();
            for (BackTestIngestObject backTestIngestObject : backTestIngestObjects) {
                backTestIngestObject.setGamesToTest(cap);
                backTestIngestObject.setStartDate(startDate);
                backTestIngestObject.setModelStolenBases(true);
                //   backTestIngestObject.setModelOpposingPitching(true);
                backTestIngestObject.setModelOpposingFielding(true);
                backTestIngestObject.setDoubleSquareRoot(false);
                backTestIngestObject.setSquareRootTotalPoints(false);
                backTestIngestObject.setAllowLowEndBelowZero(true);
            }
        }
        start = Instant.now();
        for (BackTestIngestObject backTestIngestObject : backTestIngestObjects) {
            BackTestResult backTestResult;
            if (verbose) {
                backTestResult = runBackTestVerbose(backTestIngestObject);
            } else {
                backTestResult = runBackTest(backTestIngestObject);
            }
            backTestResults.add(backTestResult);
        }
        if (caching) {
            //    threadMonitor.ingestGameCache(gameCache);
        }
        //gameFinder.quitWebDriver();
        threadMonitor.ingestResults(backTestResults);
        threadMonitor.threadFinished();
    }

    public BackTestResult runBackTest(BackTestIngestObject backTestIngestObject) {

        CacheSettingsObject cacheSettingsObject = new CacheSettingsObject();
        cacheSettingsObject.setBullpenLookback(backTestIngestObject.getBullpenGameCount());
        cacheSettingsObject.setPitcherGameLookback(backTestIngestObject.getPitcherGameLookback());
        cacheSettingsObject.setPlayerGameLookback(backTestIngestObject.getGameCount());
        cacheSettingsObject.setDoubleSquareRoot(backTestIngestObject.isDoubleSquareRoot());

        boolean alreadyExists = false;
        for (CacheSettingsObject object : completedSettings) {
            if (object.hashCode() == cacheSettingsObject.hashCode()) {
                alreadyExists = true;
            }
        }
        HashMap<Integer, ScoreModel> mapToUse = null;
        if (alreadyExists) {
            Iterator<Map.Entry<CacheSettingsObject, HashMap<Integer, ScoreModel>>> it = gameCache.entrySet().iterator();
            Map.Entry<CacheSettingsObject, HashMap<Integer, ScoreModel>> entry = it.next();
            if (entry.getKey().getBullpenLookback() == backTestIngestObject.getBullpenGameCount() &&
                    entry.getKey().getPlayerGameLookback() == backTestIngestObject.getGameCount() &&
                    entry.getKey().getPitcherGameLookback() == backTestIngestObject.getPitcherGameLookback() &&
                    entry.getKey().isDoubleSquareRoot() == backTestIngestObject.isDoubleSquareRoot()
            ) {
                mapToUse = entry.getValue();
            }
        }

        startingMoney = 100;
        double gameCap = backTestIngestObject.getGamesToTest();
        localDate = backTestIngestObject.getStartDate();
        int gameCount = backTestIngestObject.getGameCount();
        int correctPredictions = 0;
        int incorrectPredictions = 0;
        int totalGames = 0;
        int daysCompleted = 0;
        int predictOver = 0;
        int predictUnder = 0;
        int actualOver = 0;
        int actualUnder = 0;
        int exactMatch = 0;
        double totalPredictedPoints = 0;
        double totalActualPoints = 0;
        //   int ppgCorrect = 0;
        //   int ppgIncorrect = 0;
        int splitCorrect = 0;
        int splitIncorrect = 0;
        double percentPerBet = 0.07;
        List<Double> splitPercentages = new ArrayList<>();
        while (totalGames < gameCap) {
            //  float dailyNetMoney = 0;
            List<MLBGame> games = getGamesFromMap(localDate);
            //  float betPerGame = (float) (startingMoney * percentPerBet);
            for (int i = 0; i < games.size(); i++) {
                MLBGame game = (MLBGame) games.get(i).clone();

                if (game.getAwayStartingPitcher() != null && game.getHomeStartingPitcher() != null) {

                    ScoreModel safeScoreModel = getScoreModelForGame(game, backTestIngestObject, game.getDate(), mapToUse);
                    mergeScores(safeScoreModel);
                    totalGames++;
                }
            }
            if (forward) {
                localDate = localDate.plusDays(1);
            } else {
                localDate = localDate.minusDays(1);
            }
        }
        localDate = backTestIngestObject.getStartDate();
        totalGames = 0;
        while (totalGames < gameCap) {
            //   float dailyNetMoney = 0;
            List<MLBGame> games = getGamesFromMap(localDate);
            //   float betPerGame = (float) (startingMoney * percentPerBet);
            for (int i = 0; i < games.size(); i++) {
                MLBGame game = (MLBGame) games.get(i).clone();
                if (game.getAwayStartingPitcher() != null && game.getHomeStartingPitcher() != null) {

                    calculateAllTestCorrelations(game, backTestIngestObject, game.getDate(), mapToUse);
                    totalGames++;
                }
            }
            if (forward) {
                localDate = localDate.plusDays(1);
            } else {
                localDate = localDate.minusDays(1);
            }
        }
        localDate = backTestIngestObject.getStartDate();
        totalGames = 0;

        while (totalGames < gameCap) {
            //   float dailyNetMoney = 0;
            List<MLBGame> games = getGamesFromMap(localDate);
            //    float betPerGame = (float) (startingMoney * percentPerBet);
            for (int i = 0; i < games.size(); i++) {
                MLBGame game = (MLBGame) games.get(i).clone();
                if (game.getAwayStartingPitcher() != null && game.getHomeStartingPitcher() != null) {

                    calculateAllTestCorrelationRocs(game, backTestIngestObject, game.getDate(), mapToUse);
                    totalGames++;
                }
            }
            if (forward) {
                localDate = localDate.plusDays(1);
            } else {
                localDate = localDate.minusDays(1);
            }
        }
        localDate = backTestIngestObject.getStartDate();
        totalGames = 0;

        while (totalGames < gameCap) {
            //    float dailyNetMoney = 0;
            List<MLBGame> games = getGamesFromMap(localDate);
            //    float betPerGame = (float) (startingMoney * percentPerBet);
            for (int i = 0; i < games.size(); i++) {
                MLBGame game = (MLBGame) games.get(i).clone();
                if (game.getAwayStartingPitcher() != null && game.getHomeStartingPitcher() != null) {

                    calculateAllTestCorrelationRocBools(game, backTestIngestObject, game.getDate());
                    totalGames++;
                }
            }
            if (forward) {
                localDate = localDate.plusDays(1);
            } else {
                localDate = localDate.minusDays(1);
            }
        }
        localDate = backTestIngestObject.getStartDate();
        totalGames = 0;


        //PlayerDictionary playerDictionary = new PlayerDictionary();
        while (totalGames < gameCap) {
            float dailyNetMoney = 0;
            List<MLBGame> games = getGamesFromMap(localDate);
            float betPerGame = (float) (startingMoney * percentPerBet);
            for (int i = 0; i < games.size(); i++) {
                MLBGame game = (MLBGame) games.get(i).clone();
                if (game.getAwayStartingPitcher() != null && game.getHomeStartingPitcher() != null) {

                    ScoreModel safeScoreModel = getScoreModelForGame(game, backTestIngestObject, game.getDate(), mapToUse);
                    mergeScores(safeScoreModel);
                    if (backTestIngestObject.isSquareRootTotalPoints()) {
                        StaticScoreModelUtils.squareRootTotal(safeScoreModel);
                    }
                    mergeScores(safeScoreModel);
                    if (game.isShortenedMakeUpGame()) {
                        StaticScoreModelUtils.adjustForShortenedGame(safeScoreModel);
                    }
//                    if (backTestIngestObject.isModelOpposingPitching()) {
//                        StaticScoreModelUtils.factorOpponentPitchingIntoScoringModel(safeScoreModel, game, 1,
//                                1, 1, moreInfo);
//                    }
                    if (backTestIngestObject.isModelOpposingFielding()) {
                        StaticScoreModelUtils.factorOpponentFieldingIntoScoringModel(safeScoreModel, game,
                                1, 1, moreInfo);
                    }
                    if (backTestIngestObject.isModelStolenBases()) {
                        StaticScoreModelUtils.addStolenBasesToModel(safeScoreModel, 1, 1);
                    }

                    mergeScores(safeScoreModel);
                    //StringBuilder stringBuilder = new StringBuilder();
                    if (backTestIngestObject.getBetType().equals("moneyline")) {
                        MLBGameOdds gameOdds = mlbGameOddsHashMap.get(game.getGameId());
                        if (gameOdds != null) {

                            if (!safeScoreModel.isInsufficientPitcherData()) {
                                //StaticScoreModelUtils.addDynamicHomeTeamAdvantage(safeScoreModel,  safeScoreModel.getHomeAdvantage(),  game.isShortenedMakeUpGame());
                                StaticScoreModelUtils.addHomeTeamAdvantage(safeScoreModel, 1, 1, game.isShortenedMakeUpGame());
                                addStaticHomeTeamAdvantage(safeScoreModel, 1, game.isShortenedMakeUpGame());
                                //StaticScoreModelUtils.factorStaticOpponentPitchingIntoScoringModel(safeScoreModel, game, backTestIngestObject.getLowRunFactor(), backTestIngestObject.getHighRunFactor(), moreInfo);
                                mergeScores(safeScoreModel);
                                totalPredictedPoints = totalPredictedPoints + ((safeScoreModel.getTotalHigh() + safeScoreModel.getTotalLow()) / 2);
                                if (Double.isNaN(totalPredictedPoints)) {
                                    System.out.println("STOP HERE");
                                }
                                totalActualPoints = totalActualPoints + (game.getAwayPoints() + game.getHomePoints());
                                totalGames = totalGames + 1;
                                gamesCompleted = gamesCompleted + 1;
                                double homePredicted = ((safeScoreModel.getHomeHighPoints() + safeScoreModel.getHomeLowPoints()) / 2);
                                double awayPredicted = ((safeScoreModel.getAwayHighPoints() + safeScoreModel.getAwayLowPoints()) / 2);
                                //System.out.println("calculated awayPercent = " + awayPercentAboveHome);
                                double pointDifference = homePredicted - awayPredicted;

                                if (pointDifference <= 0 * -1 || pointDifference >= 0) {


                                    double tally = 0;

                                    tally += (safeScoreModel.getHomeRunsScoredPerGameRoc() - safeScoreModel.getAwayRunsScoredPerGameRoc());
                                    tally += (safeScoreModel.getHomeRunsGivenUpPerGameRoc() - safeScoreModel.getAwayRunsGivenUpPerGameRoc());
                                   // tally += safeScoreModel.getHomeFieldingRoc() - safeScoreModel.getAwayFieldingRoc();
                                  //  tally += safeScoreModel.getHomeStolenBasesRoc() - safeScoreModel.getAwayStolenBasesRoc();
                                    if(tally >0) {
                                        if (game.getHomePoints() > game.getAwayPoints()) {
                                            correctPredictions = correctPredictions + 1;
                                            splitCorrect = splitCorrect + 1;
                                            dailyNetMoney = dailyNetMoney + (float) ((getBetAmountFromSpreadOdds(gameOdds.getHomeTeamMoneyLine(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getHomeTeamMoneyLine(), modifier) - 1)));
                                        } else {
                                            incorrectPredictions = incorrectPredictions + 1;
                                            splitIncorrect = splitIncorrect + 1;
                                            dailyNetMoney = dailyNetMoney - (float) getBetAmountFromSpreadOdds(gameOdds.getHomeTeamMoneyLine(), betPerGame);
                                        }
                                    }else{
                                        if (game.getHomePoints() < game.getAwayPoints()) {
                                            correctPredictions = correctPredictions + 1;
                                            splitCorrect = splitCorrect + 1;
                                            dailyNetMoney = dailyNetMoney + (float) ((getBetAmountFromSpreadOdds(gameOdds.getAwayTeamMoneyLine(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getAwayTeamMoneyLine(), modifier) - 1)));
                                        } else {
                                            incorrectPredictions = incorrectPredictions + 1;
                                            splitIncorrect = splitIncorrect + 1;
                                            dailyNetMoney = dailyNetMoney - (float) getBetAmountFromSpreadOdds(gameOdds.getAwayTeamMoneyLine(), betPerGame);
                                        }
                                    }

////                                                }
////
////                                            }
////                                        }


//                                    }

//                                        if ((safeScoreModel.getHomeRunsGivenUpPerGameRoc() < backTestIngestObject.getHomeRunsGivenUpRocThreshold() && safeScoreModel.getAwayRunsGivenUpPerGameRoc() > -1 * backTestIngestObject.getAwayRunsGivenUpRocThreshold())) {
//                                            if (safeScoreModel.getHomeRunsScoredPerGameRoc() > -1 * backTestIngestObject.getHomeRunsScoredRocThreshold() && safeScoreModel.getAwayRunsScoredPerGameRoc() < backTestIngestObject.getAwayRunsScoredocThreshold()) {
//                                                //System.out.println("Implied Away Odds: " + impliedAwayOdds);
//
//                                            }
//                                        }


//                                else {
//                                        boolean homeRunsGivenUp = false;
//                                        if (backTestIngestObject.isAwayRunsGivenUpRocFlip()) {
//                                            homeRunsGivenUp = safeScoreModel.getAwayRunsGivenUpPerGameRoc() > backTestIngestObject.getAwayRunsGivenUpRocThreshold();
//                                        } else {
//                                            homeRunsGivenUp = safeScoreModel.getAwayRunsGivenUpPerGameRoc() < backTestIngestObject.getAwayRunsGivenUpRocThreshold();
//                                        }
//                                        if (homeRunsGivenUp) {
//                                            boolean homeRunsScored = false;
//                                            if (backTestIngestObject.isAwayRunsScoredocFlip()) {
//                                                homeRunsScored = safeScoreModel.getAwayRunsScoredPerGameRoc() < backTestIngestObject.getAwayRunsScoredocThreshold();
//                                            } else {
//                                                homeRunsScored = safeScoreModel.getAwayRunsScoredPerGameRoc() > backTestIngestObject.getAwayRunsScoredocThreshold();
//                                            }
//                                            if (homeRunsScored) {
//                                                boolean homeFielding = false;
//                                                if (backTestIngestObject.isAwayFieldingRocFlip()) {
//                                                    homeFielding = safeScoreModel.getAwayFieldingRoc() < backTestIngestObject.getAwayFieldingRocThreshold();
//                                                } else {
//                                                    homeFielding = safeScoreModel.getAwayFieldingRoc() > backTestIngestObject.getAwayFieldingRocThreshold();
//                                                }
//                                                if (homeFielding) {
////                                                    boolean homeStolenBases = false;
////                                                    if (backTestIngestObject.isAwayStolenBasesRocFlip()) {
////                                                        homeStolenBases = safeScoreModel.getAwayStolenBasesRoc() < backTestIngestObject.getAwayStolenBasesRocThreshold();
////                                                    } else {
////                                                        homeStolenBases = safeScoreModel.getAwayStolenBasesRoc() > backTestIngestObject.getAwayStolenBasesRocThreshold();
////                                                    }
////                                                    if (homeStolenBases) {
                                    //   System.out.println("Selecting " + game.getAwayTeamName() + " @ " + gameOdds.getAwayTeamMoneyLine());

                                }
                            }
                        }
                    } else if (backTestIngestObject.getBetType().equals("spread")) {
                        if (mlbGameOddsHashMap.get(game.getGameId()) != null) {
                            MLBGameOdds mlbGameOdds = mlbGameOddsHashMap.get(game.getGameId());
                            if (mlbGameOdds.getAwayTeamSpreadOdds() != 0 && mlbGameOdds.getHomeTeamSpreadOdds() != 0) {
                                StaticScoreModelUtils.addHomeTeamAdvantage(safeScoreModel, 1, 1, game.isShortenedMakeUpGame());
                                addStaticHomeTeamAdvantage(safeScoreModel, 1, game.isShortenedMakeUpGame());
                                // StaticScoreModelUtils.factorStaticOpponentPitchingIntoScoringModel(safeScoreModel, game, 1, 1, moreInfo);
                                mergeScores(safeScoreModel);
                                totalPredictedPoints = totalPredictedPoints + (safeScoreModel.getTotalHigh() + safeScoreModel.getTotalLow());
                                totalActualPoints = totalActualPoints + (game.getAwayPoints() + game.getHomePoints());
                                float HomePredictedPoints = (float) (safeScoreModel.getHomeHighPoints() + safeScoreModel.getHomeLowPoints()) / 2;
                                float AwayPredictedPoints = (float) (safeScoreModel.getAwayHighPoints() + safeScoreModel.getAwayLowPoints()) / 2;
                                float pointDifference = HomePredictedPoints - AwayPredictedPoints;
                                int pointClass = (int) pointDifference;
//                                System.out.println("Score only range: " + safeScoreModel.getTotalHigh() + " || " + safeScoreModel.getTotalLow());
//                                System.out.println("Away only range: " + safeScoreModel.getAwayHighPoints() + " || " + safeScoreModel.getAwayLowPoints());
//                                System.out.println("Home only range: " + safeScoreModel.getHomeHighPoints() + " || " + safeScoreModel.getHomeLowPoints());
//                                System.out.println(game.getHomeMLBTeam().getTeamName() + " vs " + game.getAwayMLBTeam().getTeamName() + game.getDate());
//                                System.out.println("(ML) Away Predicted: " + AwayPredictedPoints);
//                                System.out.println("(ML) Home Predicted: " + HomePredictedPoints);
//                                System.out.println("(ML) Away Actual: " + game.getAwayPoints());
//                                System.out.println("(ML) Home Actual: " + game.getHomePoints());
//                                System.out.println("Home RunsGivenUp Roc: " + safeScoreModel.getHomeRunsGivenUpPerGameRoc());
//                                System.out.println("Away RunsGivenUp Roc: " + safeScoreModel.getAwayRunsGivenUpPerGameRoc());
//                                System.out.println("Home RunsScoredRoc Roc: " + safeScoreModel.getHomeRunsScoredPerGameRoc());
//                                System.out.println("Away RunsScoredRoc Roc: " + safeScoreModel.getAwayRunsScoredPerGameRoc());
                                //   if(pointClass<=backTestIngestObject.getPointThreshold()*-1 || pointClass>=backTestIngestObject.getPointThreshold()) {

                                //  }
                            }
                            totalGames = totalGames + 1;
                            gamesCompleted = gamesCompleted + 1;
                        }
                    } else if (backTestIngestObject.getBetType().equals("overunder")) {
                        MLBGameOdds gameOdds = getGameOddsFromCache(game.getGameId());
                        if (gameOdds != null) {
                            // StaticScoreModelUtils.addHomeTeamAdvantage(safeScoreModel, backTestIngestObject.getHomeAdvantageLow(), backTestIngestObject.getHomeAdvantageHigh(),  game.isShortenedMakeUpGame());
                            // addStaticHomeTeamAdvantage(safeScoreModel, backTestIngestObject.getHomeAdvantageHigh(),  game.isShortenedMakeUpGame());
                            // StaticScoreModelUtils.factorStaticOpponentPitchingIntoScoringModel(safeScoreModel, game, backTestIngestObject.getLowRunFactor(), backTestIngestObject.getHighRunFactor(), moreInfo);
                            mergeScores(safeScoreModel);
                            double total = (safeScoreModel.getTotalHigh() + safeScoreModel.getTotalLow()) / 2;
                            totalPredictedPoints = totalPredictedPoints + (total);
                            totalActualPoints = totalActualPoints + (game.getAwayPoints() + game.getHomePoints());

                            totalGames = totalGames + 1;
                            gamesCompleted = gamesCompleted + 1;
                            double overUnder = gameOdds.getOverUnder();
                            float upperPct = (float) (safeScoreModel.getTotalHigh() - overUnder) / (float) ((safeScoreModel.getTotalHigh() - overUnder) + (overUnder - safeScoreModel.getTotalLow()));
                            float lowerPct = (float) (overUnder - safeScoreModel.getTotalLow()) / (float) ((safeScoreModel.getTotalHigh() - overUnder) + (overUnder - safeScoreModel.getTotalLow()));
                            double midPoint = ((safeScoreModel.getTotalHigh() + safeScoreModel.getTotalLow()) / 2);
//                            StringBuilder stringBuilder;
//                            stringBuilder = new StringBuilder();
//                                                            System.out.println("Score only range: " + safeScoreModel.getTotalHigh() + " || " + safeScoreModel.getTotalLow());
//                                System.out.println("Away only range: " + safeScoreModel.getAwayHighPoints() + " || " + safeScoreModel.getAwayLowPoints());
//                                System.out.println("Home only range: " + safeScoreModel.getHomeHighPoints() + " || " + safeScoreModel.getHomeLowPoints());
//                                System.out.println(game.getHomeMLBTeam().getTeamName() + " vs " + game.getAwayMLBTeam().getTeamName() + game.getDate());
//                            stringBuilder.append("Game High: ").append(safeScoreModel.getTotalHigh()).append(" : ").append(upperPct * 100).append("%").append("\n");
//                            stringBuilder.append("Game Low: ").append(safeScoreModel.getTotalLow()).append(" : ").append(lowerPct * 100).append("%").append("\n");
//                            stringBuilder.append("O/U:").append(overUnder).append("\n");
//                            stringBuilder.append("Actual Score: ").append((game.getHomePoints() + game.getAwayPoints())).append("\n");
//                            stringBuilder.append(game.getHomeMLBTeam().getTeamName()).append(":").append(game.getHomePoints()).append("\n");
//                            stringBuilder.append(game.getAwayMLBTeam().getTeamName()).append(":").append(game.getAwayPoints());
//                            stringBuilder.append("homeRuns RoC" +safeScoreModel.getHomeRunsScoredPerGameRoc());
//                            System.out.println(stringBuilder);
                            double homeRunsPerGameRoc = 0;
                            double awayRunsPerGameRoc = 0;
                            double homeGivenPerGameRoc = 0;
                            double awayGivenPerGameRoc = 0;
                            boolean homeRunsRocFlip = false;
                            boolean awayRunsRocFlip = false;
                            boolean homeGivenRocFlip = false;
                            boolean awayGivenRocFlip = false;

                            // if(gameOdds.getOverUnder() == 8.5 || gameOdds.getOverUnder() == 7.5 || gameOdds.getOverUnder() == 9.5 || gameOdds.getOverUnder() == 10 || gameOdds.getOverUnder() == 10.5) {
                            if (overUnder == (float) (game.getAwayPoints() + game.getHomePoints())) {
                                exactMatch = exactMatch + 1;
                            } else {
                                boolean homeRunsRoc = false;
                                if (homeRunsRocFlip) {
                                    homeRunsRoc = safeScoreModel.getHomeRunsScoredPerGameRoc() < homeRunsPerGameRoc;
                                } else {
                                    homeRunsRoc = safeScoreModel.getHomeRunsScoredPerGameRoc() > homeRunsPerGameRoc;
                                }
                                if (homeRunsRoc) {
                                    boolean awayRunsRoc = false;
                                    if (awayRunsRocFlip) {
                                        awayRunsRoc = safeScoreModel.getAwayRunsScoredPerGameRoc() < awayRunsPerGameRoc;
                                    } else {
                                        awayRunsRoc = safeScoreModel.getAwayRunsScoredPerGameRoc() > awayRunsPerGameRoc;
                                    }
                                    if (awayRunsRoc) {
//                                        boolean homeRunsGiven = false;
//                                        if(homeGivenRocFlip){
//                                            homeRunsGiven = safeScoreModel.getHomeRunsGivenUpPerGameRoc() < homeGivenPerGameRoc;
//                                        }else{
//                                            homeRunsGiven = safeScoreModel.getHomeRunsGivenUpPerGameRoc() > homeGivenPerGameRoc;
//                                        }
//                                        if(homeRunsGiven){
//                                            boolean awayRunsGiven = false;
//                                            if(awayGivenRocFlip){
//                                                awayRunsGiven = safeScoreModel.getAwayRunsGivenUpPerGameRoc() < awayGivenPerGameRoc;
//                                            }else{
//                                                awayRunsGiven = safeScoreModel.getAwayRunsGivenUpPerGameRoc() > awayGivenPerGameRoc;
//                                            }
//                                            if(awayRunsGiven){
                                        predictOver++;
                                        //  predictOver = predictOver + 1;
                                        //   System.out.println("Select Predict Over " + gameOdds.getOddsOver());
                                        if ((game.getAwayPoints() + game.getHomePoints()) > overUnder) {
                                            correctPredictions = correctPredictions + 1;
                                            actualOver = actualOver + 1;
                                            dailyNetMoney = dailyNetMoney + (float) ((betPerGame * (applyWinningSpreadOdds(gameOdds.getOddsOver(), modifier) - 1)));
                                        } else {
                                            incorrectPredictions = incorrectPredictions + 1;
                                            actualUnder = actualUnder + 1;
                                            dailyNetMoney = dailyNetMoney - betPerGame;
//                                                }
//                                            }
                                        }
                                    }
                                }
                                //    }
                            }


//                                 else if(safeScoreModel.getHomeRunsScoredPerGameRoc() > backTestIngestObject.getHomeRunsScoredRocThreshold() && safeScoreModel.getAwayRunsScoredPerGameRoc() > backTestIngestObject.getAwayRunsScoredocThreshold() &&
//                                        safeScoreModel.getHomeRunsGivenUpPerGameRoc() > backTestIngestObject.getHomeRunsGivenUpRocThreshold() && safeScoreModel.getAwayRunsGivenUpPerGameRoc() > backTestIngestObject.getAwayRunsGivenUpRocThreshold() &&
//                                        (safeScoreModel.getTotalHigh() + safeScoreModel.getTotalLow()) / 2 > overUnder && (safeScoreModel.getHomeWalkRoc()>0)) {
//                                         predictUnder++;
//                                     //   System.out.println("Select Predict Under " + gameOdds.getOddsUnder());
//                                            if ((game.getAwayPoints() + game.getHomePoints()) < overUnder) {
//                                                correctPredictions = correctPredictions + 1;
//                                                actualUnder = actualUnder + 1;
//                                                dailyNetMoney = dailyNetMoney + (float) ((betPerGame * (applyWinningSpreadOdds(gameOdds.getOddsUnder(), modifier) - 1)));
//                                            } else {
//                                                incorrectPredictions = incorrectPredictions + 1;
//                                                actualOver = actualOver + 1;
//                                                dailyNetMoney = dailyNetMoney - betPerGame;
//                                            }
//                                        }


                        }
                    }
//                    if(gamesCompleted % 30 == 0) {
//                        if (splitCorrect + splitIncorrect > 0) {
//                            splitPercentages.add(Double.parseDouble(d.format((float) splitCorrect / (splitCorrect + splitIncorrect))));
//                            splitCorrect = 0;
//                            splitIncorrect = 0;
//                        }
//                    }
                }
            }

            daysCompleted = daysCompleted + 1;
            if (forward) {
                localDate = localDate.plusDays(1);
            } else {
                localDate = localDate.minusDays(1);
            }
            startingMoney = startingMoney + dailyNetMoney;

        }

        //double dailyVol = getDailyVol(splitPercentages);
        BackTestResult backTestResult = new BackTestResult();
        backTestResult.setPlayerGameLookBack(gameCount);
        backTestResult.setDoubleSquareRoot(backTestIngestObject.isDoubleSquareRoot());
        backTestResult.setCorrectPercent(((float) correctPredictions / (correctPredictions + incorrectPredictions)) * 100);
        backTestResult.setPredictCorrect(correctPredictions);
        backTestResult.setPredictIncorrect(incorrectPredictions);
        backTestResult.setPredictOver(predictOver);
        backTestResult.setPredictUnder(predictUnder);
        backTestResult.setActualOver(actualOver);
        backTestResult.setActualUnder(actualUnder);
        backTestResult.setActualPoints(totalActualPoints);
        backTestResult.setExactResults(exactMatch);
        backTestResult.setEstimatedPoints(totalPredictedPoints);
        backTestResult.setPitcherGameLookback(backTestIngestObject.getPitcherGameLookback());
        backTestResult.setRunsGivenUpDifferential(1);
        backTestResult.setSquareRootTotalPoints(backTestIngestObject.isSquareRootTotalPoints());
        backTestResult.setBullpenGameLookback(backTestIngestObject.getBullpenGameCount());

        backTestResult.setBetType(backTestIngestObject.getBetType());
        backTestResult.setAllowLowEndBelowZero(backTestIngestObject.isAllowLowEndBelowZero());
        // backTestResult.setDailyVol(dailyVol);
        backTestResult.setEndingMoney(startingMoney);

        backTestResult.setHomeRunsGivenUpRocFlip(backTestIngestObject.isHomeRunsGivenUpRocFlip());
        backTestResult.setAwayRunsGivenUpRocFlip(backTestIngestObject.isAwayRunsGivenUpRocFlip());
        backTestResult.setHomeRunsScoredRocFlip(backTestIngestObject.isHomeRunsScoredRocFlip());
        backTestResult.setAwayRunsScoredocFlip(backTestIngestObject.isAwayRunsScoredocFlip());

        backTestResult.setHomeFieldingRocFlip(backTestIngestObject.isHomeFieldingRocFlip());
        backTestResult.setAwayFieldingRocFlip(backTestIngestObject.isAwayFieldingRocFlip());
        backTestResult.setHomeStolenBasesRocFlip(backTestIngestObject.isHomeStolenBasesRocFlip());
        backTestResult.setAwayStolenBasesRocFlip(backTestIngestObject.isAwayStolenBasesRocFlip());


//        System.out.println(backTestResult);
        printProgress();

        return backTestResult;
    }


    public BackTestResult runBackTestVerbose(BackTestIngestObject backTestIngestObject) {
//        double testMetric1 = 0.0;
//        double testMetric2 = 0.0;
//        double testMetric3 = 0.0;
//        double testMetric4 = 0.0;
//        double testMetric5 = 0.0;
//        double testMetric6 = 0.0;
//        HashMap<Integer, ClassResult> classResultHashMap = new HashMap<>();
//        HashMap<Integer, GenericTwoInteger> PitcherResultMap = new HashMap();
        if (!useStaticParams) {
            betList = new ArrayList<>();
        } else {
            gameCache = new HashMap<>();
        }
        CacheSettingsObject cacheSettingsObject = new CacheSettingsObject();
        cacheSettingsObject.setBullpenLookback(backTestIngestObject.getBullpenGameCount());
        cacheSettingsObject.setPitcherGameLookback(backTestIngestObject.getPitcherGameLookback());
        cacheSettingsObject.setPlayerGameLookback(backTestIngestObject.getGameCount());
        cacheSettingsObject.setDoubleSquareRoot(backTestIngestObject.isDoubleSquareRoot());

        boolean alreadyExists = false;
        for (CacheSettingsObject object : completedSettings) {
            if (object.hashCode() == cacheSettingsObject.hashCode()) {
                alreadyExists = true;
            }
        }
        HashMap<Integer, ScoreModel> mapToUse = null;
        if (alreadyExists) {
            Iterator<Map.Entry<CacheSettingsObject, HashMap<Integer, ScoreModel>>> it = gameCache.entrySet().iterator();
            Map.Entry<CacheSettingsObject, HashMap<Integer, ScoreModel>> entry = it.next();
            if (entry.getKey().getBullpenLookback() == backTestIngestObject.getBullpenGameCount() &&
                    entry.getKey().getPlayerGameLookback() == backTestIngestObject.getGameCount() &&
                    entry.getKey().getPitcherGameLookback() == backTestIngestObject.getPitcherGameLookback() &&
                    entry.getKey().isDoubleSquareRoot() == backTestIngestObject.isDoubleSquareRoot()
            ) {
                mapToUse = entry.getValue();
            }
        }


        double gameCap = backTestIngestObject.getGamesToTest();
        localDate = backTestIngestObject.getStartDate();
        int gameCount = backTestIngestObject.getGameCount();
        int correctPredictions = 0;
        int incorrectPredictions = 0;
        int totalGames = 0;
        int daysCompleted = 0;
        int predictOver = 0;
        int predictUnder = 0;
        int actualOver = 0;
        int actualUnder = 0;
        int exactMatch = 0;
        double totalPredictedPoints = 0;
        double totalActualPoints = 0;
        double percentPerBet = backTestIngestObject.getBetSize();
        //PlayerDictionary playerDictionary = new PlayerDictionary();
     //   List<Double> splitPercentages = new ArrayList<>();
        //   int splitCorrect = 0;
        //  int splitIncorrect = 0;
        double startingMoney = 100;
        //gameFinder.setScrapingProxy(scrapingProxy);
        //gameFinder.setGameRepository(gameRepository);
        //120games
        while (totalGames < gameCap + 750) {
            //lfloat dailyNetMoney = 0;
            List<MLBGame> games = getGamesFromMap(localDate);
            // float betPerGame = (float) (startingMoney * percentPerBet);
            for (int i = 0; i < games.size(); i++) {
                MLBGame game = (MLBGame) games.get(i);
                if (game.getAwayStartingPitcher() != null && game.getHomeStartingPitcher() != null) {
                    ScoreModel safeScoreModel = getScoreModelForGame(game, backTestIngestObject, game.getDate(), mapToUse);
                    mergeScores(safeScoreModel);
                    totalGames++;
                }
            }
            if (forward) {
                localDate = localDate.plusDays(1);
            } else {
                localDate = localDate.minusDays(1);
            }
        }
        localDate = backTestIngestObject.getStartDate();
        totalGames = 0;
        while (totalGames < gameCap + 750) {
            // float dailyNetMoney = 0;
            List<MLBGame> games = getGamesFromMap(localDate);
            //  float betPerGame = (float) (startingMoney * percentPerBet);
            for (int i = 0; i < games.size(); i++) {
                MLBGame game = (MLBGame) games.get(i);
                if (game.getAwayStartingPitcher() != null && game.getHomeStartingPitcher() != null) {

                    calculateAllTestCorrelations(game, backTestIngestObject, game.getDate(), mapToUse);
                    totalGames++;
                }
            }
            if (forward) {
                localDate = localDate.plusDays(1);
            } else {
                localDate = localDate.minusDays(1);
            }
        }
        localDate = backTestIngestObject.getStartDate();
        totalGames = 0;
        while (totalGames < gameCap + 750) {
            //  float dailyNetMoney = 0;
            List<MLBGame> games = getGamesFromMap(localDate);
            //   float betPerGame = (float) (startingMoney * percentPerBet);
            for (int i = 0; i < games.size(); i++) {
                MLBGame game = (MLBGame) games.get(i);
                if (game.getAwayStartingPitcher() != null && game.getHomeStartingPitcher() != null) {

                    calculateAllTestCorrelationRocs(game, backTestIngestObject, game.getDate(), mapToUse);
                    totalGames++;
                }
            }
            if (forward) {
                localDate = localDate.plusDays(1);
            } else {
                localDate = localDate.minusDays(1);
            }
        }
        localDate = backTestIngestObject.getStartDate();
        totalGames = 0;
        while(totalGames < gameCap + 750) {
            float dailyNetMoney = 0;
            List<MLBGame> games = getGamesFromMap(localDate);
            float betPerGame = (float) (startingMoney * percentPerBet);
            for (int i = 0; i < games.size(); i++) {
                MLBGame game = (MLBGame) games.get(i).clone();
                if (game.getAwayStartingPitcher() != null && game.getHomeStartingPitcher() != null) {

                    calculateAllTestCorrelationRocBools(game,backTestIngestObject,game.getDate());
                    totalGames ++;
                }
            }
            if(forward){
                localDate = localDate.plusDays(1);
            }else{
                localDate = localDate.minusDays(1);
            }
        }
        localDate = backTestIngestObject.getStartDate();
        totalGames = 0;

        while (totalGames < gameCap) {
            float dailyNetMoney = 0;
            List<MLBGame> games = getGamesFromMap(localDate);

            for (MLBGame safeGame : games) {
                float betPerGame = (float) (startingMoney * percentPerBet);
                //if (game.getAwayStartingPitcher() != null && game.getHomeStartingPitcher() != null) {

                ScoreModel safeScoreModel = getScoreModelForGame(safeGame, backTestIngestObject, safeGame.getDate(), mapToUse);
                mergeScores(safeScoreModel);
                if (backTestIngestObject.isSquareRootTotalPoints()) {
                    StaticScoreModelUtils.squareRootTotal(safeScoreModel);
                }

                mergeScores(safeScoreModel);
                if (safeGame.isShortenedMakeUpGame()) {
                    StaticScoreModelUtils.adjustForShortenedGame(safeScoreModel);
                }
//                    if (backTestIngestObject.isModelOpposingPitching()) {
//                        StaticScoreModelUtils.factorOpponentPitchingIntoScoringModel(safeScoreModel, safeGame, 1,
//                                1, 1, moreInfo);
//                    }
                if (backTestIngestObject.isModelOpposingFielding()) {
                    StaticScoreModelUtils.factorOpponentFieldingIntoScoringModel(safeScoreModel, safeGame,
                            1, 1, moreInfo);
                }
                if (backTestIngestObject.isModelStolenBases()) {
                    StaticScoreModelUtils.addStolenBasesToModel(safeScoreModel, 1, 1);
                }

                mergeScores(safeScoreModel);
//                    System.out.println("Score only range: " + safeScoreModel.getTotalHigh() + " || " + safeScoreModel.getTotalLow());
//                    System.out.println("Away only range: " + safeScoreModel.getAwayHighPoints() + " || " + safeScoreModel.getAwayLowPoints());
//                    System.out.println("Home only range: " + safeScoreModel.getHomeHighPoints() + " || " + safeScoreModel.getHomeLowPoints());
            //        System.out.println(safeGame.getHomeTeamName() + " vs " + safeGame.getAwayTeamName() + safeGame.getDate());
                //     StringBuilder stringBuilder = new StringBuilder();
                     if(backTestIngestObject.getBetType().equals("moneyline")) {
                         MLBGameOdds gameOdds = mlbGameOddsHashMap.get(safeGame.getGameId());
                         if (gameOdds != null) {

                             totalPredictedPoints = totalPredictedPoints + ((safeScoreModel.getTotalHigh() + safeScoreModel.getTotalLow()) / 2);
                             totalGames = totalGames + 1;
                             gamesCompleted = gamesCompleted + 1;
                             double tally = 0;

                            // tally += (safeScoreModel.getHomeRunsScoredVolRoc() - safeScoreModel.getAwayRunsScoredVolRoc());
                           //  tally += (-safeScoreModel.getHomeRunsGivenUpVolRoc() + safeScoreModel.getAwayRunsGivenUpVolRoc());
                         //    tally += (-safeScoreModel.getHomeRunsGivenUpVolRoc() + safeScoreModel.getAwayRunsGivenUpVolRoc());

                           //  tally += (safeScoreModel.getHomeRunsScoredPerGameRoc() - safeScoreModel.getAwayRunsScoredPerGameRoc());
                           //  tally += (safeScoreModel.getVol() - safeScoreModel.getAwayAveragePriorPlayersRoc());
                          //    tally += (safeScoreModel.getHomeFieldingRoc() - safeScoreModel.getAwayFieldingRoc());
                          //     tally += (safeScoreModel.getHomeStolenBasesRoc() - safeScoreModel.getAwayStolenBasesRoc());
                          //     tally += (safeScoreModel.getHomeWalkRoc() - safeScoreModel.getAwayWalkRoc());

                             if(safeScoreModel.testHomeBoolean  && safeScoreModel.getAwayTestCorrelationRoc() > 0 && safeScoreModel.getAwayGivenUpCorrelationRoc() > 0) {
                                 if (safeGame.getHomePoints() > safeGame.getAwayPoints()) {
                                     correctPredictions = correctPredictions + 1;
                                  //   splitCorrect = splitCorrect + 1;
                                     dailyNetMoney = dailyNetMoney + (float) ((getBetAmountFromSpreadOdds(gameOdds.getHomeTeamMoneyLine(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getHomeTeamMoneyLine(), modifier) - 1)));
                                 } else {
                                     incorrectPredictions = incorrectPredictions + 1;
                                  //   splitIncorrect = splitIncorrect + 1;
                                     dailyNetMoney = dailyNetMoney - (float) getBetAmountFromSpreadOdds(gameOdds.getHomeTeamMoneyLine(), betPerGame);
                                 }
                             }
                             else{
//                                 if (safeGame.getHomePoints() < safeGame.getAwayPoints()) {
//                                     correctPredictions = correctPredictions + 1;
//                                 //    splitCorrect = splitCorrect + 1;
//                                     dailyNetMoney = dailyNetMoney + (float) ((getBetAmountFromSpreadOdds(gameOdds.getAwayTeamMoneyLine(), betPerGame) * (applyWinningSpreadOdds(gameOdds.getAwayTeamMoneyLine(), modifier) - 1)));
//                                 } else {
//                                     incorrectPredictions = incorrectPredictions + 1;
//                                 //    splitIncorrect = splitIncorrect + 1;
//                                     dailyNetMoney = dailyNetMoney - (float) getBetAmountFromSpreadOdds(gameOdds.getAwayTeamMoneyLine(), betPerGame);
//                                 }
                             }
                         }
                     }
                    if(backTestIngestObject.getBetType().equals("spread")){
                        MLBGameOdds mlbGameOdds = mlbGameOddsHashMap.get(safeGame.getGameId());
                       if(mlbGameOdds != null) {
                           float homePredicted = (float) ((safeScoreModel.getHomeHighPoints() + safeScoreModel.getHomeLowPoints()) / 2);
                           float awayPredicted = (float) ((safeScoreModel.getAwayHighPoints() + safeScoreModel.getAwayLowPoints()) / 2);
//                            System.out.println("(ML) Away Predicted: " + awayPredicted);
//                            System.out.println("(ML) Home Predicted: " + homePredicted);
//                            System.out.println("(ML) Away Actual: " + game.getAwayPoints());
//                            System.out.println("(ML) Home Actual: " + game.getHomePoints());
//                            System.out.println("Home RunsGivenUp Roc: " + safeScoreModel.getHomeRunsGivenUpPerGameRoc());
//                            System.out.println("Away RunsGivenUp Roc: " + safeScoreModel.getAwayRunsGivenUpPerGameRoc());
//                            System.out.println("Home RunsScoredRoc Roc: " + safeScoreModel.getHomeRunsScoredPerGameRoc());
//                            System.out.println("Away RunsScoredRoc Roc: " + safeScoreModel.getAwayRunsScoredPerGameRoc());
                           totalPredictedPoints = totalPredictedPoints + ((safeScoreModel.getTotalHigh() + safeScoreModel.getTotalLow()) / 2);
                           totalGames = totalGames + 1;
                           gamesCompleted = gamesCompleted + 1;
                           double pointDifference = homePredicted - awayPredicted;


                           int pointClass = (int) pointDifference;

                           boolean result = false;

                           boolean homeRunCorrelRocFlip = backTestIngestObject.isHomeRunsGivenUpRocFlip();
                           boolean homeRunCorrelFlip = backTestIngestObject.isAwayRunsGivenUpRocFlip();
                           boolean homeRunRocFlip = backTestIngestObject.isHomeRunsScoredRocFlip();
                           boolean homeRunGivenCorrelRocFlip = backTestIngestObject.isAwayRunsScoredocFlip();
                           boolean homeRunGivenCorrelFlip = backTestIngestObject.isHomeFieldingRocFlip();
                           boolean homeRunGivenRocFlip = backTestIngestObject.isAwayFieldingRocFlip();
                           boolean homeFieldingCorrelRocFlip = backTestIngestObject.isHomeStolenBasesRocFlip();
                           boolean homeFieldingCorrelFlip = backTestIngestObject.isAwayStolenBasesRocFlip();
                           boolean homeFieldingRocFlip = backTestIngestObject.isAllowLowEndBelowZero();
                           boolean e1 = backTestIngestObject.isEnable1();
                           boolean e2 = backTestIngestObject.isEnable2();
                           boolean e3 = backTestIngestObject.isEnable3();
                           boolean e4 = backTestIngestObject.isEnable4();
                           boolean e5 = backTestIngestObject.isEnable5();
                           boolean e6 = backTestIngestObject.isEnable6();
                           boolean e7 = backTestIngestObject.isEnable7();
                           boolean e8 = backTestIngestObject.isEnable8();
                           boolean e9 = backTestIngestObject.isEnable9();
                           boolean e10 = backTestIngestObject.isEnable10();
                           boolean e11 = backTestIngestObject.isEnable11();
                           boolean e12 = backTestIngestObject.isEnable12();
                           boolean e13 = backTestIngestObject.isEnable13();
                           boolean e14 = backTestIngestObject.isEnable14();
                           boolean e15 = backTestIngestObject.isEnable15();

                           boolean flip1 = backTestIngestObject.isFlip1();
                           boolean flip2 = backTestIngestObject.isFlip2();
                           boolean flip3 = backTestIngestObject.isFlip3();
                           boolean flip4 = backTestIngestObject.isFlip4();
                           boolean flip5 = backTestIngestObject.isFlip5();
                           boolean flip6 = backTestIngestObject.isFlip6();

                           boolean homeBet = false;


                           double homeTally = 0;
                           double awayTally = 0;
                           if (e1) {
                               if (!flip1) {
                                   if (safeScoreModel.getHomeRunCorrelationRoc() > 0) {
                                       homeTally += backTestIngestObject.getD1();
                                   }
                                   if (safeScoreModel.getAwayRunCorrelationRoc() > 0) {
                                       awayTally += backTestIngestObject.getD1();
                                   }
                               } else {
                                   if (safeScoreModel.getHomeRunCorrelationRoc() < 0) {
                                       homeTally += backTestIngestObject.getD1();
                                   }
                                   if (safeScoreModel.getAwayRunCorrelationRoc() < 0) {
                                       awayTally += backTestIngestObject.getD1();
                                   }
                               }
                           }
                           if (e2) {
                               if (!flip2) {
                                   if (safeScoreModel.getHomeRunCorrelation() > 0) {
                                       homeTally += backTestIngestObject.getD2();
                                   }
                                   if (safeScoreModel.getAwayRunCorrelation() > 0) {
                                       awayTally += backTestIngestObject.getD2();
                                   }
                               } else {
                                   if (safeScoreModel.getHomeRunCorrelation() < 0) {
                                       homeTally += backTestIngestObject.getD2();
                                   }
                                   if (safeScoreModel.getAwayRunCorrelation() < 0) {
                                       awayTally += backTestIngestObject.getD2();
                                   }
                               }
                           }
                           if (e3) {
                               if (!flip3) {
                                   if (safeScoreModel.getHomeRunsScoredVolRoc() < 0) {
                                       homeTally += backTestIngestObject.getD3();
                                   }
                                   if (safeScoreModel.getAwayRunsScoredVolRoc() < 0) {
                                       awayTally += backTestIngestObject.getD3();
                                   }
                               } else {
                                   if (safeScoreModel.getHomeRunsScoredVolRoc() > 0) {
                                       homeTally += backTestIngestObject.getD3();
                                   }
                                   if (safeScoreModel.getAwayRunsScoredVolRoc() > 0) {
                                       awayTally += backTestIngestObject.getD3();
                                   }
                               }
                           }
                           if (e4) {
                               if (!flip4) {
                                   if (safeScoreModel.getHomeRunsScoredPerGameRoc() > 0) {
                                       homeTally += backTestIngestObject.getD4();
                                   }
                                   if (safeScoreModel.getAwayRunsScoredPerGameRoc() > 0) {
                                       awayTally += backTestIngestObject.getD4();
                                   }
                               } else {
                                   if (safeScoreModel.getHomeRunsScoredPerGameRoc() < 0) {
                                       homeTally += backTestIngestObject.getD4();
                                   }
                                   if (safeScoreModel.getAwayRunsScoredPerGameRoc() < 0) {
                                       awayTally += backTestIngestObject.getD4();
                                   }
                               }
                           }
                           if (e5) {
                               if (!flip5) {
                                   if (safeScoreModel.getHomeAveragePriorPlayersRoc() < 0) {
                                       homeTally += backTestIngestObject.getD5();
                                   }
                                   if (safeScoreModel.getAwayAveragePriorPlayersRoc() < 0) {
                                       awayTally += backTestIngestObject.getD5();
                                   }
                               } else {
                                   if (safeScoreModel.getHomeAveragePriorPlayersRoc() > 0) {
                                       homeTally += backTestIngestObject.getD5();
                                   }
                                   if (safeScoreModel.getAwayAveragePriorPlayersRoc() > 0) {
                                       awayTally += backTestIngestObject.getD5();
                                   }
                               }
                           }
                           if (e6) {
                               if (!flip6) {
                                   if (safeScoreModel.getHomeWalkRoc() > 0) {
                                       homeTally += backTestIngestObject.getD6();
                                   }
                                   if (safeScoreModel.getAwayWalkRoc() > 0) {
                                       awayTally += backTestIngestObject.getD6();
                                   }
                               } else {
                                   if (safeScoreModel.getHomeWalkRoc() < 0) {
                                       homeTally += backTestIngestObject.getD6();
                                   }
                                   if (safeScoreModel.getAwayWalkRoc() < 0) {
                                       awayTally += backTestIngestObject.getD6();
                                   }
                               }
                           }
                           if (e7) {
                               if (!backTestIngestObject.isHomeRunsGivenUpRocFlip()) {
                                   if (safeScoreModel.getHomeStolenBasesRoc() > 0) {
                                       homeTally += backTestIngestObject.getD7();
                                   }
                                   if (safeScoreModel.getAwayStolenBasesRoc() > 0) {
                                       awayTally += backTestIngestObject.getD7();
                                   }
                               } else {
                                   if (safeScoreModel.getHomeStolenBasesRoc() < 0) {
                                       homeTally += backTestIngestObject.getD7();
                                   }
                                   if (safeScoreModel.getAwayStolenBasesRoc() < 0) {
                                       awayTally += backTestIngestObject.getD7();
                                   }
                               }
                           }
                           if (e8) {
                               if (!backTestIngestObject.isAwayRunsGivenUpRocFlip()) {
                                   if (safeScoreModel.getHomeFieldingRoc() > 0) {
                                       homeTally += backTestIngestObject.getD8();
                                   }
                                   if (safeScoreModel.getAwayFieldingRoc() > 0) {
                                       awayTally += backTestIngestObject.getD8();
                                   }
                               } else {
                                   if (safeScoreModel.getHomeFieldingRoc() < 0) {
                                       homeTally += backTestIngestObject.getD8();
                                   }
                                   if (safeScoreModel.getAwayFieldingRoc() < 0) {
                                       awayTally += backTestIngestObject.getD8();
                                   }
                               }
                           }
                           if (e9) {
                               if (!backTestIngestObject.isHomeRunsScoredRocFlip()) {
                                   if (safeScoreModel.getHomeGivenUpCorrelation() < 0) {
                                       homeTally += backTestIngestObject.getD9();
                                   }
                                   if (safeScoreModel.getAwayGivenUpCorrelation() < 0) {
                                       awayTally += backTestIngestObject.getD9();
                                   }
                               } else {
                                   if (safeScoreModel.getHomeGivenUpCorrelation() > 0) {
                                       homeTally += backTestIngestObject.getD9();
                                   }
                                   if (safeScoreModel.getAwayGivenUpCorrelation() > 0) {
                                       awayTally += backTestIngestObject.getD9();
                                   }
                               }
                           }

                           if (e10) {
                               if (!backTestIngestObject.isAwayRunsScoredocFlip()) {
                                   if (safeScoreModel.getHomeGivenUpCorrelationRoc() < 0) {
                                       homeTally += backTestIngestObject.getD10();
                                   }
                                   if (safeScoreModel.getAwayGivenUpCorrelationRoc() < 0) {
                                       awayTally += backTestIngestObject.getD10();
                                   }
                               } else {
                                   if (safeScoreModel.getHomeGivenUpCorrelationRoc() > 0) {
                                       homeTally += backTestIngestObject.getD10();
                                   }
                                   if (safeScoreModel.getAwayGivenUpCorrelationRoc() > 0) {
                                       awayTally += backTestIngestObject.getD10();
                                   }
                               }
                           }
                           if (e11) {
                               if (!backTestIngestObject.isHomeFieldingRocFlip()) {
                                   if (safeScoreModel.getHomeRunsScoredVol() < safeScoreModel.getAwayRunsScoredVol()) {
                                       homeTally += backTestIngestObject.getD11();
                                   } else {
                                       awayTally += backTestIngestObject.getD11();
                                   }
                               } else {
                                   if (safeScoreModel.getHomeRunsScoredVol() > safeScoreModel.getAwayRunsScoredVol()) {
                                       homeTally += backTestIngestObject.getD11();
                                   } else {
                                       awayTally += backTestIngestObject.getD11();
                                   }
                               }
                           }
                           if (e12) {
                               if (!backTestIngestObject.isAwayFieldingRocFlip()) {
                                   if (safeScoreModel.getHomeRunsGivenUpVol() < safeScoreModel.getAwayRunsGivenUpVol()) {
                                       homeTally += backTestIngestObject.getD12();
                                   } else {
                                       awayTally += backTestIngestObject.getD12();
                                   }
                               } else {
                                   if (safeScoreModel.getHomeRunsGivenUpVol() > safeScoreModel.getAwayRunsGivenUpVol()) {
                                       homeTally += backTestIngestObject.getD12();
                                   } else {
                                       awayTally += backTestIngestObject.getD12();
                                   }
                               }
                           }

                           boolean homerunCorrelRoc = false;

                           if (awayTally > backTestIngestObject.getDeez() && homeTally < awayTally) {
                               double test = 1;
                               System.out.println("Selecting Away Spread. (" + safeGame.getAwayTeamName() + ")");
                               // homeBet = true;
                               if (safeGame.getHomePoints() < safeGame.getAwayPoints() + mlbGameOdds.getAwayTeamSpread()) {
                                   result = true;
                                   correctPredictions = correctPredictions + 1;
                                   //    splitCorrect = splitCorrect + 1;
                                   betList.add(new IndividualBet(true, mlbGameOdds.getAwayTeamSpreadOdds(), "spread", safeGame.getDate(), percentPerBet * test, startingMoney));
                                   double winnings = (float) ((getBetAmountFromSpreadOdds(mlbGameOdds.getAwayTeamSpreadOdds(), (betPerGame * test)) * (applyWinningSpreadOdds(mlbGameOdds.getAwayTeamSpreadOdds(), modifier) - 1)));
                                   System.out.println("Winnings of " + winnings + " from a base of " + getBetAmountFromSpreadOdds(mlbGameOdds.getAwayTeamSpreadOdds(), (betPerGame * test)) + " odds were: " + mlbGameOdds.getAwayTeamSpreadOdds() + " target: " + (betPerGame * test));
                                   dailyNetMoney = (float) (dailyNetMoney + winnings);
                               } else if (safeGame.getHomePoints() > safeGame.getAwayPoints() + mlbGameOdds.getAwayTeamSpread()) {
                                   result = false;
                                   incorrectPredictions = incorrectPredictions + 1;
                                   //    splitIncorrect = splitIncorrect + 1;
                                   double losings = (float) getBetAmountFromSpreadOdds(mlbGameOdds.getAwayTeamSpreadOdds(), (betPerGame * test));
                                   betList.add(new IndividualBet(false, mlbGameOdds.getAwayTeamSpreadOdds(), "Spread", safeGame.getDate(), percentPerBet * test, startingMoney));
                                   System.out.println("Losings of " + losings + " from a base of " + getBetAmountFromSpreadOdds(mlbGameOdds.getAwayTeamSpreadOdds(), (betPerGame * test)) + " odds were: " + mlbGameOdds.getAwayTeamSpreadOdds() + " target: " + (betPerGame * test));
                                   dailyNetMoney = (float) (dailyNetMoney - losings);
                               } else {
                                   //        System.out.println("??");
                               }
                           }




//                            if (mlbGameOdds.getAwayTeamSpreadOdds() != 0 && mlbGameOdds.getHomeTeamSpreadOdds() != 0) {
//                                StaticScoreModelUtils.addHomeTeamAdvantage(safeScoreModel, 1, 1,  safeGame.isShortenedMakeUpGame());
//                                addStaticHomeTeamAdvantage(safeScoreModel, 1,  safeGame.isShortenedMakeUpGame());
//                                StaticScoreModelUtils.factorStaticOpponentPitchingIntoScoringModel(safeScoreModel, safeGame, 1, 1, moreInfo);
//                                mergeScores(safeScoreModel);
//                                totalPredictedPoints = totalPredictedPoints + (safeScoreModel.getTotalHigh() + safeScoreModel.getTotalLow());
//                                totalActualPoints = totalActualPoints + (safeGame.getAwayPoints() + safeGame.getHomePoints());
//                                float HomePredictedPoints = (float)(safeScoreModel.getHomeHighPoints() + safeScoreModel.getHomeLowPoints()) / 2;
//                                float AwayPredictedPoints = (float)(safeScoreModel.getAwayHighPoints() + safeScoreModel.getAwayLowPoints()) / 2;
////                                float HomePredictedPoints = safeScoreModel.getHomePPG();
////                                float AwayPredictedPoints = safeScoreModel.getAwayPPG();
//                                float pointDifference = HomePredictedPoints - AwayPredictedPoints;
//
//                                stringBuilder = new StringBuilder();
//                                stringBuilder.append(safeGame.getHomeTeamName()).append(" predicted: ").append(HomePredictedPoints).append("\n");
//                                stringBuilder.append(safeGame.getAwayTeamName()).append(" predicted: ").append(AwayPredictedPoints).append("\n");
//                                stringBuilder.append("Actual Score: ").append((safeGame.getHomePoints() + safeGame.getAwayPoints())).append("\n");
//                                stringBuilder.append(safeGame.getHomeTeamName()).append(": ").append(safeGame.getHomePoints()).append("\n");
//                                stringBuilder.append(safeGame.getAwayTeamName()).append(": ").append(safeGame.getAwayPoints()).append("\n");
//                                System.out.println(stringBuilder);
//                                //float realDifference = game.getHomePoints() - game.getAwayPoints();
////                                if (realDifference == mlbGameOdds.getAwayTeamSpread() || realDifference == mlbGameOdds.getHomeTeamSpread()) {
////                                    exactMatch = exactMatch + 1;
////                                } else
//                                int pointClass = (int) pointDifference;
//
//                                boolean result = false;
//
//                                if(pointClass<=-1) {
//
                                }
                                // }
                            }
//
//
//
//                            totalGames = totalGames + 1;
//                            gamesCompleted = gamesCompleted + 1;
//                            if(gamesCompleted % printEveryNGames == 0) {
//                                int totalPredictions = correctPredictions + incorrectPredictions;
//                                if (totalPredictions > 0) {
//                                    float pct = ((float) correctPredictions / (float) totalPredictions) * 100;
//                                    stringBuilder.append(ANSI_GREEN).append("$").append(startingMoney + dailyNetMoney).append(ANSI_RESET);
//                                    System.out.println(stringBuilder);
//                                    stringBuilder = new StringBuilder();
//                                    //stringBuilder.append("$").append(startingMoney + dailyNetMoney).append("\n");
//                                    stringBuilder.append(ANSI_GREEN).append("CORRECT PREDICTIONS: ").append(correctPredictions).append(" || ").append("INCORRECT PREDICTIONS: ").append(incorrectPredictions).append(" (").append(d.format(pct)).append(")\n");
////                                stringBuilder = new StringBuilder();
////                                stringBuilder.append(ANSI_GREEN).append("SETTINGS: Lookback: ").append(gameCount).append(" pitcherLookbk: ").append(backTestIngestObject.getPitcherGameLookback()).append(" dblSquareRoot: ").append(backTestIngestObject.isDoubleSquareRoot()).append(" modelOpposingPitching: ").append(backTestIngestObject.isModelOpposingPitching()).append(" PitchingDiffAdjustment: ").append(backTestIngestObject.getRunsGivenUpDifferential()).append(ANSI_RESET);
////                                System.out.println(stringBuilder);
//
//                                    //stringBuilder.append(" Splits: ").append(splitPercentages).append("\n");
//
//                                    stringBuilder.append("[").append(threadNum).append("] [").append(backTestResults.size() + 1).append("/").append(backTestIngestObjects.size()).append("]").append(ANSI_RESET);
//                                    System.out.println(stringBuilder);
//                                }
//                            }
//                        }
//
//                    }
                    if(backTestIngestObject.getBetType().equals("overunder")){
                        MLBGameOdds gameOdds = getGameOddsFromCache(safeGame.getGameId());
                        if(gameOdds != null) {
                            totalPredictedPoints = totalPredictedPoints + ((safeScoreModel.getTotalHigh() + safeScoreModel.getTotalLow()) / 2);
                            totalGames = totalGames + 1;
                            gamesCompleted = gamesCompleted + 1;
                            double overUnder = gameOdds.getOverUnder();
                            float upperPct = (float) (safeScoreModel.getTotalHigh() - overUnder) / (float) ((safeScoreModel.getTotalHigh() - overUnder) + (overUnder - safeScoreModel.getTotalLow()));
                            float lowerPct = (float) (overUnder - safeScoreModel.getTotalLow()) / (float) ((safeScoreModel.getTotalHigh() - overUnder) + (overUnder - safeScoreModel.getTotalLow()));
                            double midPoint = ((safeScoreModel.getTotalHigh() + safeScoreModel.getTotalLow()) / 2);
                            //double predictedPoints = ((safeScoreModel.getAwayHighPoints() + safeScoreModel.getAwayLowPoints())/2) + ((safeScoreModel.getHomeHighPoints() + safeScoreModel.getHomeLowPoints())/2);
//                            stringBuilder = new StringBuilder();
//                            stringBuilder.append("Game High: ").append(safeScoreModel.getTotalHigh()).append(" : ").append(upperPct * 100).append("%").append("\n");
//                            stringBuilder.append("Game Low: ").append(safeScoreModel.getTotalLow()).append(" : ").append(lowerPct * 100).append("%").append("\n");
//                            stringBuilder.append("O/U:").append(overUnder).append("\n");
//                            stringBuilder.append("Actual Score: ").append((safeGame.getHomePoints() + safeGame.getAwayPoints())).append("\n");
                            //stringBuilder.append(safeGame.getHomeTeamName()).append(":").append(safeGame.getHomePoints()).append("\n");
                            // stringBuilder.append(safeGame.getAwayMLBTeam().getTeamName()).append(":").append(safeGame.getAwayPoints());
                            //  System.out.println(stringBuilder);
                            totalActualPoints = totalActualPoints + (safeGame.getHomePoints() + safeGame.getAwayPoints());


                            //if(safeScoreModel.getAwayRunsScoredPerGameRoc() + safeScoreModel.getHomeRunsScoredPerGameRoc() < backTestIngestObject.getBullpenGameCount()*-0.01
                            //        && gameOdds.getOverUnder() < (safeScoreModel.getAwayRunsScoredPerGame() + safeScoreModel.getHomeRunsScoredPerGame())){
//                                if(safeScoreModel.getAwayRunsGivenUpPerGame() < 0.005 && safeScoreModel.getAwayRunsScoredPerGameRoc() < 0 && safeScoreModel.getAwayRunsGivenUpVolRoc <0){
                            //                    boolean awayRunCorrelRocFlip = backTestIngestObject.isHomeRunsGivenUpRocFlip();
                            boolean homeRunCorrelRocFlip = false;
                            boolean homeRunCorrelFlip = true;
                            boolean e1 = true;
                            boolean e2 = true;
                            boolean e3 = true;
                            boolean e4 = true;
                            boolean e5 = false;
                            boolean e6 = false;
                            boolean e7 = false;
                            boolean e8 = true;

                            boolean flip1 = true;
                            boolean flip2 =  true;
                            boolean flip3 =  true;
                            boolean flip4 =  true;
                            boolean flip5 = false;
                            boolean flip6 = false;
                            double homeTally = 0;
                            double awayTally = 0;
                            boolean awayrunCorrelRoc = false;

                            boolean runsScoredVolRoc = false;
                            boolean underBet = false;
                            if (e1) {
                                if (flip1) {
                                    runsScoredVolRoc = safeScoreModel.getAwayRunsScoredVolRoc() + safeScoreModel.getHomeRunsScoredVolRoc() < 0;
                                } else {
                                    runsScoredVolRoc = safeScoreModel.getAwayRunsScoredVolRoc() + safeScoreModel.getHomeRunsScoredVolRoc() > 0;
                                }
                            } else {
                                runsScoredVolRoc = true;
                            }
                            if (runsScoredVolRoc) {
                                boolean awayrunRoc = false;
                                if (e2) {
                                    if (flip2) {
                                        awayrunRoc = safeScoreModel.getAwayRunsScoredPerGameRoc() + safeScoreModel.getHomeRunsScoredPerGameRoc() > 0;
                                    } else {
                                        awayrunRoc = safeScoreModel.getAwayRunsScoredPerGameRoc() + safeScoreModel.getHomeRunsScoredPerGameRoc() < 0;
                                    }
                                } else {
                                    awayrunRoc = true;
                                }
                                if (awayrunRoc) {
                                    boolean runGivenRoc = false;
                                    if (e3) {
                                        if (flip3) {
                                            runGivenRoc = safeScoreModel.getAwayRunsGivenUpPerGameRoc() + safeScoreModel.getHomeRunsGivenUpPerGameRoc() > 0;
                                        } else {
                                            runGivenRoc = safeScoreModel.getAwayRunsGivenUpPerGameRoc() + safeScoreModel.getHomeRunsGivenUpPerGameRoc() < 0;
                                        }
                                    } else {
                                        runGivenRoc = true;
                                    }
                                    if (runGivenRoc) {
                                        boolean runsGivenUpVolRoc = false;
                                        if (e4) {
                                            if (flip4) {
                                                runsGivenUpVolRoc = safeScoreModel.getAwayRunsGivenUpVolRoc() + safeScoreModel.getHomeRunsGivenUpVolRoc() < 0;
                                            } else {
                                                runsGivenUpVolRoc = safeScoreModel.getAwayRunsGivenUpVolRoc() + safeScoreModel.getHomeRunsGivenUpVolRoc() > 0;
                                            }
                                        } else {
                                            runsGivenUpVolRoc = true;
                                        }
                                        if (runsGivenUpVolRoc) {
                                            boolean test2 = false;
                                            if (e5) {
                                                if (flip5) {
                                                    test2 = safeScoreModel.getAwayAveragePriorPlayersRoc() + safeScoreModel.getHomeAveragePriorPlayersRoc() < 0;
                                                } else {
                                                    test2 = safeScoreModel.getAwayAveragePriorPlayersRoc() + safeScoreModel.getHomeAveragePriorPlayersRoc() > 0;
                                                }
                                            } else {
                                                test2 = true;
                                            }
                                            if (test2) {
                                                boolean test3 = false;
                                                if (e6) {
                                                    if (flip6) {
                                                        test3 = safeScoreModel.getAwayFieldingRoc() + safeScoreModel.getHomeFieldingRoc() < 0;
                                                    } else {
                                                        test3 = safeScoreModel.getAwayFieldingRoc() + safeScoreModel.getHomeFieldingRoc() > 0;
                                                    }
                                                } else {
                                                    test3 = true;
                                                }
                                                if (test3) {
                                                    boolean test4 = false;
                                                    if (e7) {
                                                        if (homeRunCorrelRocFlip) {
                                                            test4 = safeScoreModel.getAwayWalkRoc() + safeScoreModel.getHomeWalkRoc() < 0;
                                                        } else {
                                                            test4 = safeScoreModel.getAwayWalkRoc() + safeScoreModel.getHomeWalkRoc() > 0;
                                                        }
                                                    } else {
                                                        test4 = true;
                                                    }
                                                    if (test4) {
                                                        boolean test5 = false;
                                                        if (e8) {
                                                            if (homeRunCorrelFlip) {
                                                                test5 = safeScoreModel.getAwayStolenBasesRoc() + safeScoreModel.getHomeStolenBasesRoc() < 0;
                                                            } else {
                                                                test5 = safeScoreModel.getAwayStolenBasesRoc() + safeScoreModel.getHomeStolenBasesRoc() > 0;
                                                            }
                                                        } else {
                                                            test5 = true;
                                                        }
                                                        if (test5) {
                                                            if(gameOdds.getOddsUnder() == 0){
                                                                gameOdds.setOddsUnder(-110);
                                                                gameOdds.setOddsOver(-110);
                                                            }
                                                            underBet = true;
                                                                 System.out.println("selecting under");
                                                            if (safeGame.getAwayPoints() + safeGame.getHomePoints() == gameOdds.getOverUnder()) {
                                                                //    System.out.println("exact");
                                                            } else if (safeGame.getAwayPoints() + safeGame.getHomePoints() < gameOdds.getOverUnder()) {
                                                                //   result = true;
                                                                double test = 1;
                                                                correctPredictions = correctPredictions + 1;
                                                                //  splitCorrect = splitCorrect + 1;
                                                                double winnings = (float) ((getBetAmountFromSpreadOdds(gameOdds.getOddsUnder(), (betPerGame * test)) * (applyWinningSpreadOdds(gameOdds.getOddsUnder(), modifier) - 1)));
                                                                        System.out.println("Winnings of " + winnings + " from a base of " + getBetAmountFromSpreadOdds(gameOdds.getOddsUnder(), (betPerGame * test)) + " odds were: " + gameOdds.getOddsUnder() + " target: " + (betPerGame * test));
                                                                dailyNetMoney = (float) (dailyNetMoney + winnings);
                                                                betList.add(new IndividualBet(true, gameOdds.getOddsUnder(), "moneyline", safeGame.getDate(), percentPerBet * test, startingMoney));
                                                            } else {
                                                                //   result = false;
                                                                double test = 1;
                                                                incorrectPredictions = incorrectPredictions + 1;
                                                                //    splitIncorrect = splitIncorrect + 1;
                                                                double losings = (float) getBetAmountFromSpreadOdds(gameOdds.getOddsUnder(), (betPerGame * test));
                                                                //testMetric1 += safeScoreModel.getAwayRunsGivenUpVol();
                                                                //      testMetric2 += safeScoreModel.getHomeRunsGivenUpVol();
                                                                        System.out.println("Losings of " + losings + " from a base of " + getBetAmountFromSpreadOdds(gameOdds.getOddsUnder(), (betPerGame * test)) + " odds were: " + gameOdds.getOddsUnder() + " target: " + (betPerGame * test));
                                                                dailyNetMoney = (float) (dailyNetMoney - losings);
                                                                betList.add(new IndividualBet(false, gameOdds.getOddsUnder(), "moneyline", safeGame.getDate(), percentPerBet * test, startingMoney));

                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }





                            if(!underBet){

                                boolean overRunCorrelRocFlip = backTestIngestObject.isHomeRunsGivenUpRocFlip();
                                boolean overRunCorrelFlip = backTestIngestObject.isAwayRunsGivenUpRocFlip();

                                boolean oe1 = true;
                                boolean oe2 = backTestIngestObject.isEnable2();
                                boolean oe3 = backTestIngestObject.isEnable3();
                                boolean oe4 = backTestIngestObject.isEnable4();
                                boolean oe5 = backTestIngestObject.isEnable5();
                                boolean oe6 = backTestIngestObject.isEnable6();
                                boolean oe7 = backTestIngestObject.isEnable7();
                                boolean oe8 = backTestIngestObject.isEnable8();

                                boolean oflip1 = backTestIngestObject.isFlip1();
                                boolean oflip2 = backTestIngestObject.isFlip2();
                                boolean oflip3 = backTestIngestObject.isFlip3();
                                boolean oflip4 = backTestIngestObject.isFlip4();
                                boolean oflip5 = backTestIngestObject.isFlip5();
                                boolean oflip6 = backTestIngestObject.isFlip6();
                                if (oe1) {
                                    if (oflip1) {
                                        runsScoredVolRoc = safeScoreModel.getAwayRunsScoredVolRoc() + safeScoreModel.getHomeRunsScoredVolRoc() < 0;
                                    } else {
                                        runsScoredVolRoc = safeScoreModel.getAwayRunsScoredVolRoc() + safeScoreModel.getHomeRunsScoredVolRoc() > 0;
                                    }
                                } else {
                                    runsScoredVolRoc = true;
                                }
                                if (runsScoredVolRoc) {
                                    boolean awayrunRoc = false;
                                    if (oe2) {
                                        if (oflip2) {
                                            awayrunRoc = safeScoreModel.getAwayRunsScoredPerGameRoc() + safeScoreModel.getHomeRunsScoredPerGameRoc() > 0;
                                        } else {
                                            awayrunRoc = safeScoreModel.getAwayRunsScoredPerGameRoc() + safeScoreModel.getHomeRunsScoredPerGameRoc() < 0;
                                        }
                                    } else {
                                        awayrunRoc = true;
                                    }
                                    if (awayrunRoc) {
                                        boolean runGivenRoc = false;
                                        if (oe3) {
                                            if (oflip3) {
                                                runGivenRoc = safeScoreModel.getAwayRunsGivenUpPerGameRoc() + safeScoreModel.getHomeRunsGivenUpPerGameRoc() > 0;
                                            } else {
                                                runGivenRoc = safeScoreModel.getAwayRunsGivenUpPerGameRoc() + safeScoreModel.getHomeRunsGivenUpPerGameRoc() < 0;
                                            }
                                        } else {
                                            runGivenRoc = true;
                                        }
                                        if (runGivenRoc) {
                                            boolean runsGivenUpVolRoc = false;
                                            if (oe4) {
                                                if (oflip4) {
                                                    runsGivenUpVolRoc = safeScoreModel.getAwayRunsGivenUpVolRoc() + safeScoreModel.getHomeRunsGivenUpVolRoc() < 0;
                                                } else {
                                                    runsGivenUpVolRoc = safeScoreModel.getAwayRunsGivenUpVolRoc() + safeScoreModel.getHomeRunsGivenUpVolRoc() > 0;
                                                }
                                            } else {
                                                runsGivenUpVolRoc = true;
                                            }
                                            if (runsGivenUpVolRoc) {
                                                boolean test2 = false;
                                                if (oe5) {
                                                    if (oflip5) {
                                                        test2 = safeScoreModel.getAwayAveragePriorPlayersRoc() + safeScoreModel.getHomeAveragePriorPlayersRoc() < 0;
                                                    } else {
                                                        test2 = safeScoreModel.getAwayAveragePriorPlayersRoc() + safeScoreModel.getHomeAveragePriorPlayersRoc() > 0;
                                                    }
                                                } else {
                                                    test2 = true;
                                                }
                                                if (test2) {
                                                    boolean test3 = false;
                                                    if (oe6) {
                                                        if (oflip6) {
                                                            test3 = safeScoreModel.getAwayFieldingRoc() + safeScoreModel.getHomeFieldingRoc() < 0;
                                                        } else {
                                                            test3 = safeScoreModel.getAwayFieldingRoc() + safeScoreModel.getHomeFieldingRoc() > 0;
                                                        }
                                                    } else {
                                                        test3 = true;
                                                    }
                                                    if (test3) {
                                                        boolean test4 = false;
                                                        if (oe7) {
                                                            if (overRunCorrelRocFlip) {
                                                                test4 = safeScoreModel.getAwayWalkRoc() + safeScoreModel.getHomeWalkRoc() < 0;
                                                            } else {
                                                                test4 = safeScoreModel.getAwayWalkRoc() + safeScoreModel.getHomeWalkRoc() > 0;
                                                            }
                                                        } else {
                                                            test4 = true;
                                                        }
                                                        if (test4) {
                                                            boolean test5 = false;
                                                            if (oe8) {
                                                                if (overRunCorrelFlip) {
                                                                    test5 = safeScoreModel.getAwayStolenBasesRoc() + safeScoreModel.getHomeStolenBasesRoc() < 0;
                                                                } else {
                                                                    test5 = safeScoreModel.getAwayStolenBasesRoc() + safeScoreModel.getHomeStolenBasesRoc() > 0;
                                                                }
                                                            } else {
                                                                test5 = true;
                                                            }
                                                            if (test5) {
                                                                if(gameOdds.getOddsUnder() == 0){
                                                                    gameOdds.setOddsUnder(-110);
                                                                    gameOdds.setOddsOver(-110);
                                                                }

                                System.out.println("selecting over" + gameOdds.getOverUnder());
                                if (safeGame.getAwayPoints() + safeGame.getHomePoints() == gameOdds.getOverUnder()) {
                                    //   System.out.println("exact");
                                } else if (safeGame.getAwayPoints() + safeGame.getHomePoints() > gameOdds.getOverUnder()) {
                                  //  result = true;
                                    double test = 1;
                                    correctPredictions = correctPredictions + 1;
                                 //   testMetric1 += gameOdds.getOverUnder();
                                 //   testMetric3 += safeScoreModel.getAwayRunsScoredPerGameRoc();
                                 //   testMetric5 += safeScoreModel.getAwayRunsGivenUpVolRoc();
                                    //  splitCorrect = splitCorrect + 1;
                                    double winnings = (float) ((getBetAmountFromSpreadOdds(gameOdds.getOddsOver(), (betPerGame * test)) * (applyWinningSpreadOdds(gameOdds.getOddsOver(), modifier) - 1)));
                                    System.out.println("Winnings of " + winnings + " from a base of " + getBetAmountFromSpreadOdds(gameOdds.getOddsOver(), (betPerGame * test)) + " odds were: " + gameOdds.getOddsOver() + " target: " + (betPerGame * test));
                                    dailyNetMoney = (float) (dailyNetMoney + winnings);
                                    betList.add(new IndividualBet(true, gameOdds.getOddsOver(), "moneyline", safeGame.getDate(), percentPerBet * test, startingMoney));
                                } else {
                                 //   result = false;
                                    double test = 1;
                                    incorrectPredictions = incorrectPredictions + 1;
                                    //    splitIncorrect = splitIncorrect + 1;
                                    double losings = (float) getBetAmountFromSpreadOdds(gameOdds.getOddsOver(), (betPerGame * test));

                                //    testMetric2 += gameOdds.getOverUnder();
                                //    testMetric4 += safeScoreModel.getAwayRunsScoredPerGameRoc();
                                //    testMetric6 += safeScoreModel.getAwayRunsGivenUpVolRoc();
                                    System.out.println("Losings of " + losings + " from a base of " + getBetAmountFromSpreadOdds(gameOdds.getOddsOver(), (betPerGame * test)) + " odds were: " + gameOdds.getOddsOver() + " target: " + (betPerGame * test));
                                    dailyNetMoney = (float) (dailyNetMoney - losings);
                                    betList.add(new IndividualBet(false, gameOdds.getOddsOver(), "moneyline", safeGame.getDate(), percentPerBet * test, startingMoney));

                                }
                            }


                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
//                            if (safeScoreModel.getAwayRunsScoredPerGameRoc() < 0 && safeScoreModel.getHomeRunsScoredPerGameRoc() < 0 && gameOdds.getOverUnder() >= 10) {
//                                if (safeScoreModel.getAwayAveragePriorPlayersRoc() > 0 && safeScoreModel.getHomeAveragePriorPlayersRoc() > 0) {
//                                    System.out.println("selecting over");
//                                    if (safeGame.getAwayPoints() + safeGame.getHomePoints() == gameOdds.getOverUnder()) {
//                                        System.out.println("exact");
//                                    } else if (safeGame.getAwayPoints() + safeGame.getHomePoints() < gameOdds.getOverUnder()) {
//                                        result = true;
//                                        double test = 1;
//                                        correctPredictions = correctPredictions + 1;
//                                        //  splitCorrect = splitCorrect + 1;
//                                        double winnings = (float) ((getBetAmountFromSpreadOdds(gameOdds.getOddsUnder(), (betPerGame * test)) * (applyWinningSpreadOdds(gameOdds.getOddsUnder(), modifier) - 1)));
//                                        System.out.println("Winnings of " + winnings + " from a base of " + getBetAmountFromSpreadOdds(gameOdds.getOddsUnder(), (betPerGame * test)) + " odds were: " + gameOdds.getOddsUnder() + " target: " + (betPerGame * test));
//                                        dailyNetMoney = (float) (dailyNetMoney + winnings);
//                                        betList.add(new IndividualBet(true, gameOdds.getOddsUnder(), "moneyline", safeGame.getDate(), percentPerBet * test, startingMoney));
//                                    } else {
//                                        result = false;
//                                        double test = 1;
//                                        incorrectPredictions = incorrectPredictions + 1;
//                                        //    splitIncorrect = splitIncorrect + 1;
//                                        double losings = (float) getBetAmountFromSpreadOdds(gameOdds.getOddsUnder(), (betPerGame * test));
//                                        testMetric1 += safeScoreModel.getAwayRunsGivenUpVol();
//                                        testMetric2 += safeScoreModel.getHomeRunsGivenUpVol();
//                                        System.out.println("Losings of " + losings + " from a base of " + getBetAmountFromSpreadOdds(gameOdds.getOddsUnder(), (betPerGame * test)) + " odds were: " + gameOdds.getOddsUnder() + " target: " + (betPerGame * test));
//                                        dailyNetMoney = (float) (dailyNetMoney - losings);
//                                        betList.add(new IndividualBet(false, gameOdds.getOddsUnder(), "moneyline", safeGame.getDate(), percentPerBet * test, startingMoney));
//
//                                    }
//                                }
//                            }
                        }
                        }



//                        if ((correctPredictions + incorrectPredictions) > 0) {
//                            float pct = ((float) correctPredictions / (float) (correctPredictions + incorrectPredictions)) * 100;
//                            float invertedpct = 0;
//                            float shortenedpct = 0;
//                            if(invertedCorrect + invertedIncorrect> 0){
//                                invertedpct = ((float) invertedCorrect / (float) (invertedCorrect + invertedIncorrect) * 100);
//                            }
//                            if(shortenedCorrect + shortenedIncorrect> 0){
//                                shortenedpct = ((float) shortenedCorrect / (float) (shortenedCorrect + shortenedIncorrect) * 100);
//                            }
//                            float ppgpct = (float) ppgCorrect / (float) (ppgCorrect + ppgIncorrect) * 100;
//                            //MathContext m = new MathContext(4);
//                            stringBuilder = new StringBuilder();
//                            stringBuilder.append(ANSI_GREEN).append("$").append(startingMoney).append(ANSI_RESET);
//                            //System.out.println(stringBuilder);
//                         //  float pct = ((float) correctPredictions / (float) correctPredictions + incorrectPredictions) * 100;
//                          //  System.out.println("\n");
//                            stringBuilder.append(ANSI_GREEN).append("$").append(startingMoney + dailyNetMoney).append(ANSI_RESET);
//                          //  System.out.println(stringBuilder);
////                            stringBuilder = new StringBuilder();
////
//                            stringBuilder.append(ANSI_GREEN).append("CORRECT PREDICTIONS: ").append(correctPredictions).append(" || ").append("INCORRECT PREDICTIONS: ")
//                                    .append(incorrectPredictions).append(" (").append(d.format(pct)).append("%) EXACT MATCH: ").append(exactMatch)
//                                                 .append(" short: ").append(d.format(shortenedpct)).append(" Scorrect: ").append(shortenedCorrect).append(" Sincorrect: ").append(shortenedIncorrect);
//
//                            //                   .append(" Inverted: ").append(d.format(invertedpct)).append("%").append( " InvertedOver: ").append(invertedOver)
//                            //        .append(" InvertedUnder: ").append(invertedUnder).append(" PredictOver: ").append(invertedPredictOver).append(" PredictUnder: ").append(invertedPredictUnder).append(ANSI_RESET);
//                        //    System.out.println(stringBuilder);
//                         //   stringBuilder = new StringBuilder();
//                            //stringBuilder.append(ANSI_GREEN).append("SETTINGS: Lookback: ").append(gameCount).append(" pitcherLookbk: ").append(backTestIngestObject.getPitcherGameLookback()).append(" dblSquareRoot: ").append(backTestIngestObject.isDoubleSquareRoot()).append(" modelOpposingPitching: ").append(backTestIngestObject.isModelOpposingPitching()).append(" PitchingDiffAdjustment: ").append(backTestIngestObject.getRunsGivenUpDifferential()).append(ANSI_RESET).append("\n");
////                            stringBuilder.append(ANSI_GREEN).append(" Splits: ").append(splitPercentages).append("\n");
////                            stringBuilder.append("PPG CORRECT PREDICTIONS: ").append(ppgCorrect).append(" || ").append("PPG INCORRECT PREDICTIONS: ").append(ppgIncorrect).append("(").append(d.format(ppgpct)).append("%)").append("\n");
////                            stringBuilder.append("PREDICTIONS OVER: ").append(predictOver).append(" PREDICTIONS UNDER: ")
////                                    .append(predictUnder).append(" || ACTUAL OVER: ").append(actualOver).append(" ACTUAL UNDER: ")
////                                    .append(actualUnder).append(" || TotalPredictedPoints: ").append(totalPredictedPoints).append(" TotalActualPoints: ")
////                                    .append(totalActualPoints).append(" (").append((totalPredictedPoints - totalActualPoints) / (totalActualPoints) * 100)
////                                    .append("%)").append(ANSI_RESET);
//                          //  System.out.println(stringBuilder);
//                        }
////                        stringBuilder = new StringBuilder();
////                        stringBuilder.append(ANSI_GREEN + "[").append(threadNum).append("] ").append("[").append(backTestResults.size() + 1).append("/").append(backTestIngestObjects.size()).append("]").append(ANSI_RESET);
////                        System.out.println(stringBuilder);
//                    }
//                    if(gamesCompleted % printEveryNGames == 500) {
//                        now = Instant.now();
//                        delta = Duration.between(start, now).toMillis();
//                        rate = ((float) gamesCompleted / delta) * 1000 * 60;
//                        stringBuilder = new StringBuilder();
//                        stringBuilder.append(ANSI_ORANGE).append("Games per Minute: ").append(rate).append(ANSI_RESET);
//                        System.out.println(stringBuilder);
//                    }

            }
//            if((splitCorrect + splitIncorrect) >0) {
//                splitPercentages.add(Double.parseDouble(d.format((float) splitCorrect / (splitCorrect + splitIncorrect))));
//            }
//            splitCorrect = 0;
//            splitIncorrect = 0;
            daysCompleted= daysCompleted + 1;
            if(forward){
                localDate = localDate.plusDays(1);
            }else{
                localDate = localDate.minusDays(1);
            }
            startingMoney = startingMoney + dailyNetMoney;
        }
//        betList.sort(Comparator.comparing(IndividualBet::getDate));
//        for(IndividualBet individualBet : betList){
//            System.out.println(individualBet.getDate() + "\t" + individualBet.getType() + "\t" + individualBet.getOdds() + "\t" + individualBet.isSuccess() + "\t" + individualBet.getBetSize());
//        }
      //  System.out.println("Predict Home: " + predictHome);
       // System.out.println("Predict Away: " + predictAway);
//        for(Map.Entry<Integer, ClassResult> entry : classResultHashMap.entrySet()){
//            System.out.println("[" + entry.getKey() + "] [" + entry.getValue().getCount() + "] [" + (double)entry.getValue().getClassCorrect()/(double)(entry.getValue().getClassCorrect() + entry.getValue().getClassIncorrect()) + "]");
//        }
//        for(Map.Entry<Integer, GenericTwoInteger> entry : PitcherResultMap.entrySet()){
//            System.out.println("[Pitcher Data] ["  + entry.getKey() + "] [" + entry.getValue().getPitcherCorrect() + " , " + entry.getValue().getPitcherIncorrect() + " , " + (double)entry.getValue().getPitcherCorrect()/(double)(entry.getValue().getPitcherCorrect() + entry.getValue().getPitcherIncorrect()) + "]");
//        }

        //System.out.println("Final Splits: " + splitPercentages);
        //double dailyVol = getDailyVol(splitPercentages);
        BackTestResult backTestResult = new BackTestResult();
        if(correctPredictions > 0) {
            backTestResult.setPlayerGameLookBack(gameCount);
            backTestResult.setDoubleSquareRoot(backTestIngestObject.isDoubleSquareRoot());
            backTestResult.setCorrectPercent(((float) correctPredictions / (correctPredictions + incorrectPredictions)) * 100);
            backTestResult.setPredictCorrect(correctPredictions);
            backTestResult.setPredictIncorrect(incorrectPredictions);
            backTestResult.setPredictOver(predictOver);
            backTestResult.setPredictUnder(predictUnder);
            backTestResult.setActualOver(actualOver);
            backTestResult.setActualUnder(actualUnder);
            backTestResult.setActualPoints(totalActualPoints);
            backTestResult.setExactResults(exactMatch);
            backTestResult.setEstimatedPoints(totalPredictedPoints);
            backTestResult.setPitcherGameLookback(backTestIngestObject.getPitcherGameLookback());
            backTestResult.setRunsGivenUpDifferential(1);
            backTestResult.setSquareRootTotalPoints(backTestIngestObject.isSquareRootTotalPoints());
            backTestResult.setBullpenGameLookback(backTestIngestObject.getBullpenGameCount());
            backTestResult.setD1(backTestIngestObject.getD1());
            backTestResult.setD2(backTestIngestObject.getD2());
            backTestResult.setD3(backTestIngestObject.getD3());
            backTestResult.setD4(backTestIngestObject.getD4());
            backTestResult.setD5(backTestIngestObject.getD5());
            backTestResult.setD6(backTestIngestObject.getD6());
            backTestResult.setD7(backTestIngestObject.getD7());
            backTestResult.setD8(backTestIngestObject.getD8());
            backTestResult.setD9(backTestIngestObject.getD9());
            backTestResult.setD10(backTestIngestObject.getD10());
            backTestResult.setD11(backTestIngestObject.getD11());
            backTestResult.setD12(backTestIngestObject.getD12());
            backTestResult.setDeez(backTestIngestObject.getDeez());
            backTestResult.setBetType(backTestIngestObject.getBetType());
            backTestResult.setAllowLowEndBelowZero(backTestIngestObject.isAllowLowEndBelowZero());
             double dailyVol = getDailyVol(betList);

            SimpleRegression simpleRegression = new SimpleRegression();
            List<Double> baseDoubles = new ArrayList<>();
            List<Double> moneyValues = new ArrayList<>();
            for (int i = 0; i < betList.size(); i++) {
               baseDoubles.add((double) betList.get(i).getDate().getTime());
               moneyValues.add(betList.get(i).getEndingMoney());
            }
            double x = simpleRegression.getRSquare();

            eExponentialRegression reg = new eExponentialRegression((ArrayList<Double>) baseDoubles, (ArrayList<Double>) moneyValues);
          //  eExponentialRegression shortReg = new eExponentialRegression((ArrayList<Double>) converted, (ArrayList<Double>) shortDollarValues);
            double r2 = reg.getR2();
//            System.out.println("R2 = " + r2);
//            System.out.println("testMetric1 = " + testMetric1/(backTestResult.getPredictCorrect()));
//            System.out.println("testMetric2 = " + testMetric2/(backTestResult.getPredictIncorrect()));
//            System.out.println("testMetric3 = " + testMetric3/(backTestResult.getPredictCorrect()));
//            System.out.println("testMetric4 = " + testMetric4/(backTestResult.getPredictIncorrect()));
//            System.out.println("testMetric5 = " + testMetric5/(backTestResult.getPredictCorrect()));
//            System.out.println("testMetric6 = " + testMetric6/(backTestResult.getPredictIncorrect()));
            //double r2x = shortReg.getR2();
            //configurationTest.setReturnRsquared(r2);
            backTestResult.setDailyVol(dailyVol);
            backTestResult.setrSquared(r2);
            backTestResult.setEndingMoney(startingMoney);

            backTestResult.setHomeRunsGivenUpRocFlip(backTestIngestObject.isHomeRunsGivenUpRocFlip());
            backTestResult.setAwayRunsGivenUpRocFlip(backTestIngestObject.isAwayRunsGivenUpRocFlip());
            backTestResult.setHomeRunsScoredRocFlip(backTestIngestObject.isHomeRunsScoredRocFlip());
            backTestResult.setAwayRunsScoredocFlip(backTestIngestObject.isAwayRunsScoredocFlip());

            backTestResult.setHomeFieldingRocFlip(backTestIngestObject.isHomeFieldingRocFlip());
            backTestResult.setAwayFieldingRocFlip(backTestIngestObject.isAwayFieldingRocFlip());
            backTestResult.setHomeStolenBasesRocFlip(backTestIngestObject.isHomeStolenBasesRocFlip());
            backTestResult.setAwayStolenBasesRocFlip(backTestIngestObject.isAwayStolenBasesRocFlip());

            backTestResult.setEnable1(backTestIngestObject.isEnable1());
            backTestResult.setEnable2(backTestIngestObject.isEnable2());
            backTestResult.setEnable3(backTestIngestObject.isEnable3());
            backTestResult.setEnable4(backTestIngestObject.isEnable4());
            backTestResult.setEnable5(backTestIngestObject.isEnable5());
            backTestResult.setEnable6(backTestIngestObject.isEnable6());
            backTestResult.setEnable7(backTestIngestObject.isEnable7());
            backTestResult.setEnable8(backTestIngestObject.isEnable8());
            backTestResult.setEnable9(backTestIngestObject.isEnable9());
            backTestResult.setEnable10(backTestIngestObject.isEnable10());
            backTestResult.setEnable11(backTestIngestObject.isEnable11());
            backTestResult.setEnable12(backTestIngestObject.isEnable12());
            backTestResult.setEnable13(backTestIngestObject.isEnable13());
            backTestResult.setEnable14(backTestIngestObject.isEnable14());
            backTestResult.setEnable15(backTestIngestObject.isEnable15());

            backTestResult.setFlip1(backTestIngestObject.isFlip1());
            backTestResult.setFlip2(backTestIngestObject.isFlip2());
            backTestResult.setFlip3(backTestIngestObject.isFlip3());
            backTestResult.setFlip4(backTestIngestObject.isFlip4());
            backTestResult.setFlip5(backTestIngestObject.isFlip5());
            backTestResult.setFlip6(backTestIngestObject.isFlip6());

            backTestResult.setBetSize(backTestIngestObject.getBetSize());
        }


        if(!alreadyExists){
            completedSettings.add(cacheSettingsObject);
        }

//        System.out.println(backTestResult);

            if(backTestResults.size() % 10000 == 0) {
                now = Instant.now();
                delta = Duration.between(start, now).toMillis();
                rate = ((float) gamesCompleted / delta) * 1000;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(ANSI_ORANGE).append("Games per Second: ").append(rate).append("\n");
                stringBuilder.append("[").append(threadNum).append("] [").append(backTestResults.size() + 1).append("/").append(backTestIngestObjects.size()).append("]").append(ANSI_RESET);
                System.out.println(stringBuilder);
            //    System.gc();
            }

        return  backTestResult;
    }



    public static void mergeScores(ScoreModel scoreModel){
        scoreModel.setTotalHigh(scoreModel.getHomeHighPoints() + scoreModel.getAwayHighPoints());
        scoreModel.setTotalLow(scoreModel.getHomeLowPoints() + scoreModel.getAwayLowPoints());
    }



    private void addStaticHomeTeamAdvantage(ScoreModel safeScoreModel, double homeAdvantageHigh, boolean shortenedMakeUpGame) {
//        if(shortenedMakeUpGame){
//            safeScoreModel.setHomePPG((float) (safeScoreModel.getHomePPG() + (homeAdvantageHigh * (7.0/9.0))));
//        }else{
//            safeScoreModel.setHomePPG((float) (safeScoreModel.getHomePPG() + (homeAdvantageHigh)));
//        }
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
    public void calculateAllTestCorrelations(MLBGame game, BackTestIngestObject backTestIngestObject, Date date, HashMap<Integer, ScoreModel> mapToUse){
        if(mapToUse != null && !mapToUse.get(game.getGameId()).isCorrelationsComplete()){
            ScoreModel scoreModel = mapToUse.get(game.getGameId());
            gameCalculatorClass.calculateTestCorrelation(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), scoreModel, true, game, false, mapToUse);
            gameCalculatorClass.calculateRunCorrelation(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), scoreModel, true, game, false, mapToUse);
            gameCalculatorClass.calculateGivenUpCorrelation(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), scoreModel, true, game, false, mapToUse);
            /// gameCalculatorClass.calculateFieldingCorrelation(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), entry.getValue().get(game.getGameId()), true, game, false);

            gameCalculatorClass.calculateTestCorrelation(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), scoreModel, false, game, false, mapToUse);
            gameCalculatorClass.calculateRunCorrelation(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), scoreModel, false, game, false, mapToUse);
            gameCalculatorClass.calculateGivenUpCorrelation(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), scoreModel, false, game, false, mapToUse);
            //  gameCalculatorClass.calculateFieldingCorrelation(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), entry.getValue().get(game.getGameId()), false, game, false);
            scoreModel.setCorrelationsComplete(true);
        }else if(mapToUse == null){
            Iterator<Map.Entry<CacheSettingsObject, HashMap<Integer, ScoreModel>>> it = gameCache.entrySet().iterator();

            while (it.hasNext() && !useStaticParams) {
                Map.Entry<CacheSettingsObject, HashMap<Integer, ScoreModel>> entry = it.next();
                if (entry.getKey().getBullpenLookback() == backTestIngestObject.getBullpenGameCount() &&
                        entry.getKey().getPlayerGameLookback() == backTestIngestObject.getGameCount() &&
                        entry.getKey().getPitcherGameLookback() == backTestIngestObject.getPitcherGameLookback() &&
                        entry.getKey().isDoubleSquareRoot() == backTestIngestObject.isDoubleSquareRoot()
                ) {
                    if (entry.getValue().get(game.getGameId()) != null) {
                        if (!entry.getValue().get(game.getGameId()).isCorrelationsComplete()) {
                            gameCalculatorClass.calculateTestCorrelation(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), entry.getValue().get(game.getGameId()), true, game, false, entry.getValue());
                            gameCalculatorClass.calculateRunCorrelation(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), entry.getValue().get(game.getGameId()), true, game, false, entry.getValue());
                            gameCalculatorClass.calculateGivenUpCorrelation(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), entry.getValue().get(game.getGameId()), true, game, false, entry.getValue());
                            /// gameCalculatorClass.calculateFieldingCorrelation(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), entry.getValue().get(game.getGameId()), true, game, false);

                            gameCalculatorClass.calculateTestCorrelation(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), entry.getValue().get(game.getGameId()), false, game, false, entry.getValue());
                            gameCalculatorClass.calculateRunCorrelation(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), entry.getValue().get(game.getGameId()), false, game, false, entry.getValue());
                            gameCalculatorClass.calculateGivenUpCorrelation(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), entry.getValue().get(game.getGameId()), false, game, false, entry.getValue());

                            //  gameCalculatorClass.calculateFieldingCorrelation(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), entry.getValue().get(game.getGameId()), false, game, false);
                            entry.getValue().get(game.getGameId()).setCorrelationsComplete(true);
                        }
                    }
                }
            }
        }
    }
    public void calculateAllTestCorrelationRocs(MLBGame game, BackTestIngestObject backTestIngestObject, Date date, HashMap<Integer, ScoreModel> mapToUse){
        if(mapToUse != null && !mapToUse.get(game.getGameId()).isCorrelationRocsComplete()){
            ScoreModel scoreModel = mapToUse.get(game.getGameId());
            gameCalculatorClass.calculateTestCorrelationRoc(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), scoreModel, true, game, false, mapToUse);
            gameCalculatorClass.calculateRunScoredVolRoc(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), scoreModel, true, game, false, mapToUse);
            gameCalculatorClass.calculateRunGivenUpVolRoc(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), scoreModel, true, game, false, mapToUse);

            gameCalculatorClass.calculateRunCorrelationRoc(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), scoreModel, true, game, false, mapToUse);
            gameCalculatorClass.calculateGivenUpCorrelationRoc(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), scoreModel, true, game, false,mapToUse);
            gameCalculatorClass.calculateAveragePlayersRoc(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), scoreModel, true, game, false, mapToUse);

            // gameCalculatorClass.calculateFieldingCorrelationRoc(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), entry.getValue().get(game.getGameId()), true, game, false, entry.getValue());

            gameCalculatorClass.calculateTestCorrelationRoc(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), scoreModel, false, game, false,mapToUse);
            gameCalculatorClass.calculateRunScoredVolRoc(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), scoreModel, false, game, false, mapToUse);
            gameCalculatorClass.calculateRunGivenUpVolRoc(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), scoreModel, false, game, false, mapToUse);

            gameCalculatorClass.calculateRunCorrelationRoc(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), scoreModel, false, game, false, mapToUse);
            gameCalculatorClass.calculateGivenUpCorrelationRoc(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), scoreModel, false, game, false,mapToUse);
            gameCalculatorClass.calculateAveragePlayersRoc(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), scoreModel, false, game, false, mapToUse);

            //  gameCalculatorClass.calculateFieldingCorrelationRoc(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), entry.getValue().get(game.getGameId()), false, game, false, entry.getValue());
            scoreModel.setCorrelationRocsComplete(true);
        }else if(mapToUse == null) {
            Iterator<Map.Entry<CacheSettingsObject, HashMap<Integer, ScoreModel>>> it = gameCache.entrySet().iterator();

            while (it.hasNext() && !useStaticParams) {
                Map.Entry<CacheSettingsObject, HashMap<Integer, ScoreModel>> entry = it.next();
                if (entry.getKey().getBullpenLookback() == backTestIngestObject.getBullpenGameCount() &&
                        entry.getKey().getPlayerGameLookback() == backTestIngestObject.getGameCount() &&
                        entry.getKey().getPitcherGameLookback() == backTestIngestObject.getPitcherGameLookback() &&
                        entry.getKey().isDoubleSquareRoot() == backTestIngestObject.isDoubleSquareRoot()
                ) {
                    if (entry.getValue().get(game.getGameId()) != null) {
                        if (!entry.getValue().get(game.getGameId()).isCorrelationRocsComplete()) {
                            gameCalculatorClass.calculateTestCorrelationRoc(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), entry.getValue().get(game.getGameId()), true, game, false, entry.getValue());
                            gameCalculatorClass.calculateRunScoredVolRoc(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), entry.getValue().get(game.getGameId()), true, game, false, entry.getValue());
                            gameCalculatorClass.calculateRunGivenUpVolRoc(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), entry.getValue().get(game.getGameId()), true, game, false, entry.getValue());

                            gameCalculatorClass.calculateRunCorrelationRoc(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), entry.getValue().get(game.getGameId()), true, game, false, entry.getValue());
                            gameCalculatorClass.calculateGivenUpCorrelationRoc(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), entry.getValue().get(game.getGameId()), true, game, false, entry.getValue());
                            gameCalculatorClass.calculateAveragePlayersRoc(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), entry.getValue().get(game.getGameId()), true, game, false, entry.getValue());

                            // gameCalculatorClass.calculateFieldingCorrelationRoc(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), entry.getValue().get(game.getGameId()), true, game, false, entry.getValue());

                            gameCalculatorClass.calculateTestCorrelationRoc(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), entry.getValue().get(game.getGameId()), false, game, false, entry.getValue());
                            gameCalculatorClass.calculateRunScoredVolRoc(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), entry.getValue().get(game.getGameId()), false, game, false, entry.getValue());
                            gameCalculatorClass.calculateRunGivenUpVolRoc(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), entry.getValue().get(game.getGameId()), false, game, false, entry.getValue());

                            gameCalculatorClass.calculateRunCorrelationRoc(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), entry.getValue().get(game.getGameId()), false, game, false, entry.getValue());
                            gameCalculatorClass.calculateGivenUpCorrelationRoc(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), entry.getValue().get(game.getGameId()), false, game, false, entry.getValue());
                            gameCalculatorClass.calculateAveragePlayersRoc(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), entry.getValue().get(game.getGameId()), false, game, false, entry.getValue());

                            //  gameCalculatorClass.calculateFieldingCorrelationRoc(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), entry.getValue().get(game.getGameId()), false, game, false, entry.getValue());
                            entry.getValue().get(game.getGameId()).setCorrelationRocsComplete(true);
                        }
                    }
                }
            }
        }
    }

    public void calculateAllTestCorrelationRocBools(MLBGame game, BackTestIngestObject backTestIngestObject, Date date){
        Iterator<Map.Entry<CacheSettingsObject, HashMap<Integer, ScoreModel>>> it = gameCache.entrySet().iterator();

        while(it.hasNext() && !useStaticParams) {
            Map.Entry<CacheSettingsObject, HashMap<Integer, ScoreModel>> entry = it.next();
            if (entry.getKey().getBullpenLookback() == backTestIngestObject.getBullpenGameCount() &&
                    entry.getKey().getPlayerGameLookback() == backTestIngestObject.getGameCount() &&
                    entry.getKey().getPitcherGameLookback() == backTestIngestObject.getPitcherGameLookback() &&
                    entry.getKey().isDoubleSquareRoot() == backTestIngestObject.isDoubleSquareRoot()
            ){
                if(entry.getValue().get(game.getGameId()) != null ){
                    if(!entry.getValue().get(game.getGameId()).isCorrelationRocBoolsComplete()) {
                           gameCalculatorClass.calculateTestCorrelationRocBool(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), entry.getValue().get(game.getGameId()), true, game, false, entry.getValue());
                        gameCalculatorClass.calculateRunCorrelationRocBool(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), entry.getValue().get(game.getGameId()), true, game, false, entry.getValue());
                     //   gameCalculatorClass.calculateTeamRunsScoredRoc(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), entry.getValue().get(game.getGameId()), true, game, false);
                        // gameCalculatorClass.calculateFieldingCorrelationRoc(backTestIngestObject, date, playerStatFilter, game.getHomeMLBTeam(), entry.getValue().get(game.getGameId()), true, game, false, entry.getValue());

                           gameCalculatorClass.calculateTestCorrelationRocBool(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), entry.getValue().get(game.getGameId()), false, game, false, entry.getValue());
                        gameCalculatorClass.calculateRunCorrelationRocBool(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), entry.getValue().get(game.getGameId()), false, game, false, entry.getValue());
                       // gameCalculatorClass.calculateTeamRunsScoredRoc(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), entry.getValue().get(game.getGameId()), false, game, false);
                        //  gameCalculatorClass.calculateFieldingCorrelationRoc(backTestIngestObject, date, playerStatFilter, game.getAwayMLBTeam(), entry.getValue().get(game.getGameId()), false, game, false, entry.getValue());
                        entry.getValue().get(game.getGameId()).setCorrelationRocBoolsComplete(true);
                    }
                }
            }
        }
    }

    public ScoreModel getScoreModelForGame(MLBGame game, BackTestIngestObject backTestIngestObject, Date date, HashMap<Integer, ScoreModel> mapToUse){




        if(mapToUse != null){
//            scoreModel = mapToUse.get(game.getGameId());
//            mergeScores(scoreModel);
//            safeScoreModel = (ScoreModel) scoreModel.clone();
            return  mapToUse.get(game.getGameId());
        }

        boolean cacheSettingsFound = false;
        ScoreModel scoreModel = new ScoreModel();
        ScoreModel safeScoreModel;

        Iterator<Map.Entry<CacheSettingsObject, HashMap<Integer, ScoreModel>>> it = gameCache.entrySet().iterator();
//        Object[] list = gameCache.entrySet().toArray();
//        for(int i = 0; i<list.length; i++){
//            Map.Entry<CacheSettingsObject, HashMap<Integer, BaseballQuant.Model.ScoreModel>> entry = (Map.Entry<CacheSettingsObject, HashMap<Integer, ScoreModel>>) list[i];
//            if(entry.getKey().getBullpenLookback() == backTestIngestObject.getBullpenGameCount() &&
//                    entry.getKey().getPlayerGameLookback() == backTestIngestObject.getGameCount() &&
//                    entry.getKey().getPitcherGameLookback() == backTestIngestObject.getPitcherGameLookback() &&
//                    entry.getKey().isDoubleSquareRoot() == backTestIngestObject.isDoubleSquareRoot() &&
//                    entry.getKey().isAllowBelowZero() == backTestIngestObject.isAllowLowEndBelowZero()
//            ){
//                cacheSettingsFound = true;
//                if(entry.getValue().get(game.getGameId()) != null){
//                    scoreModel = entry.getValue().get(game.getGameId());
//                    mergeScores(scoreModel);
//                    safeScoreModel = (ScoreModel) scoreModel.clone();
//                    return safeScoreModel;
//                }
//            }
//        }
        while(it.hasNext() && !useStaticParams){
            Map.Entry<CacheSettingsObject, HashMap<Integer, ScoreModel>> entry = it.next();
            if(entry.getKey().getBullpenLookback() == backTestIngestObject.getBullpenGameCount() &&
                    entry.getKey().getPlayerGameLookback() == backTestIngestObject.getGameCount() &&
                    entry.getKey().getPitcherGameLookback() == backTestIngestObject.getPitcherGameLookback() &&
                    entry.getKey().isDoubleSquareRoot() == backTestIngestObject.isDoubleSquareRoot()
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
        if(!cacheSettingsFound && !useStaticParams){
            //System.out.println("cache & settings object not found.");
            CacheSettingsObject cacheSettingsObject = new CacheSettingsObject();
            cacheSettingsObject.setBullpenLookback(backTestIngestObject.getBullpenGameCount());
            cacheSettingsObject.setPitcherGameLookback(backTestIngestObject.getPitcherGameLookback());
            cacheSettingsObject.setPlayerGameLookback(backTestIngestObject.getGameCount());
            cacheSettingsObject.setDoubleSquareRoot(backTestIngestObject.isDoubleSquareRoot());
            HashMap<Integer, ScoreModel> gameMap = new HashMap<>();
            int awayId = 0;
            MLBTeam awayTeam = new MLBTeam();
            if (game.getAwayMLBTeam() == null) {
                awayTeam.setMlbId(game.getAwayTeamMlbId());
                awayTeam.setTeamName(game.getAwayTeamName());
                awayTeam.setTeamAbbreviation(game.getAwayTeamTricode());
                awayId = game.getAwayTeamMlbId();
            }else{
                awayTeam = (MLBTeam) game.getAwayMLBTeam().clone();
                awayId = game.getAwayMLBTeam().getMlbId();
            }

            int homeId = 0;
            MLBTeam homeTeam = new MLBTeam();
            if (game.getAwayMLBTeam() == null) {
                homeTeam.setMlbId(game.getHomeTeamMlbId());
                homeTeam.setTeamName(game.getHomeTeamName());
                homeTeam.setTeamAbbreviation(game.getHomeTeamTricode());
                homeId = game.getHomeTeamMlbId();
            }else{
                homeTeam = (MLBTeam) game.getHomeMLBTeam().clone();
                homeId = game.getHomeMLBTeam().getMlbId();
            }

            //MLBTeam awayTeam = (MLBTeam) game.getAwayMLBTeam().clone();


            awayTeam.setFieldingPlayers(gameFinder.getPlayerListFromPreviousGameFromDB(game, awayId));
//            MLBTeam homeTeam = (MLBTeam) game.getHomeMLBTeam().clone();
            homeTeam.setFieldingPlayers(gameFinder.getPlayerListFromPreviousGameFromDB(game, homeId));

            gameCalculatorClass.tallyTeamBattingLastNGames(backTestIngestObject, date, playerStatFilter, awayTeam, scoreModel, false, game, false);
            if(backTestIngestObject.getBetType().equals("overunder")){
                gameCalculatorClass.setOverUnder(true);
            }

            gameCalculatorClass.tallyTeamBattingLastNGames(backTestIngestObject, date, playerStatFilter, homeTeam, scoreModel, true, game, false);
            if(backTestIngestObject.getBetType().equals("overunder")){
                gameCalculatorClass.setOverUnder(true);
            }
            mergeScores(scoreModel);
            safeScoreModel = (ScoreModel) scoreModel.clone();
            gameMap.put(game.getGameId(), (ScoreModel) scoreModel.clone());
            gameCache.put(cacheSettingsObject, gameMap);
        }else{
            HashMap<Integer, ScoreModel> gameMap = null;
            Iterator<Map.Entry<CacheSettingsObject, HashMap<Integer, ScoreModel>>> iterator = gameCache.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<CacheSettingsObject, HashMap<Integer, ScoreModel>> entry = iterator.next();
                if (entry.getKey().getBullpenLookback() == backTestIngestObject.getBullpenGameCount() &&
                        entry.getKey().getPlayerGameLookback() == backTestIngestObject.getGameCount() &&
                        entry.getKey().getPitcherGameLookback() == backTestIngestObject.getPitcherGameLookback() &&
                        entry.getKey().isDoubleSquareRoot() == backTestIngestObject.isDoubleSquareRoot()
                ) {
                    gameMap = entry.getValue();
                    break;
                }
            }


            int awayId = 0;
            MLBTeam awayTeam = new MLBTeam();
            if (game.getAwayMLBTeam() == null) {
                awayTeam.setMlbId(game.getAwayTeamMlbId());
                awayTeam.setTeamName(game.getAwayTeamName());
                awayTeam.setTeamAbbreviation(game.getAwayTeamTricode());
                awayId = game.getAwayTeamMlbId();

            }else{
                awayTeam = (MLBTeam) game.getAwayMLBTeam().clone();
                awayId = game.getAwayMLBTeam().getMlbId();
            }

            int homeId = 0;
            MLBTeam homeTeam = new MLBTeam();
            if (game.getAwayMLBTeam() == null) {
                homeTeam.setMlbId(game.getHomeTeamMlbId());
                homeTeam.setTeamName(game.getHomeTeamName());
                homeTeam.setTeamAbbreviation(game.getHomeTeamTricode());
                homeId = game.getHomeTeamMlbId();
            }else{
                homeTeam = (MLBTeam) game.getHomeMLBTeam().clone();
                homeId = game.getHomeMLBTeam().getMlbId();
            }

            awayTeam.setFieldingPlayers(gameFinder.getPlayerListFromPreviousGameFromDB(game, awayId));

            homeTeam.setFieldingPlayers(gameFinder.getPlayerListFromPreviousGameFromDB(game, homeId));

            gameCalculatorClass.tallyTeamBattingLastNGames(backTestIngestObject, date, playerStatFilter, awayTeam, scoreModel, false, game, false);
            if(backTestIngestObject.getBetType().equals("overunder")){
                gameCalculatorClass.setOverUnder(true);
            }
            gameCalculatorClass.tallyTeamBattingLastNGames(backTestIngestObject, date, playerStatFilter, homeTeam, scoreModel, true, game, false);
            if(backTestIngestObject.getBetType().equals("overunder")){
                gameCalculatorClass.setOverUnder(true);
            }
            mergeScores(scoreModel);
            safeScoreModel = (ScoreModel) scoreModel.clone();
            if(!useStaticParams){
                gameMap.put(game.getGameId(), (ScoreModel) scoreModel.clone());
            }
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


    public double getDailyVol(List<IndividualBet> bets){

        List<Double> returnsBetweenBets = new ArrayList<>();
        double sum = 0.0;
        for(int i =1 ; i < bets.size(); i++){
            double d = Math.log(bets.get(i).getEndingMoney() / bets.get(i -1).getEndingMoney() );
            returnsBetweenBets.add(d);
            sum += d;
        }

        double variance = 0;
        int size = returnsBetweenBets.size();
        for (int i = 0; i < size; i++) {
            variance += Math.pow(returnsBetweenBets.get(i) - sum / size, 2);
        }
        variance /= size - 1;
        //double old = 100 * (Math.sqrt(variance) * Math.sqrt(252));
        //System.out.println("Std Dev Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + Math.sqrt(variance));
        return Math.sqrt(variance);
    }

    public void printProgress(){
        if((backTestResults.size()+1) % printEveryNResults == 0) {
            now = Instant.now();
            delta = Duration.between(start, now).toMillis();
            rate = ((float) gamesCompleted / delta) * 1000;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ANSI_ORANGE).append("Games per second: ").append(rate).append("\n");
            stringBuilder.append("[").append(threadNum).append("] [").append(backTestResults.size() + 1).append("/").append(backTestIngestObjects.size()).append("]").append(ANSI_RESET);
            System.out.println(stringBuilder);
        }
    }


}
