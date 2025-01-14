package BaseballQuant.Threads;



import BaseballQuant.Model.*;
import BaseballQuant.Util.*;
import SportsQuant.Model.GameResult;
import SportsQuant.Model.ReturnedOdds;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class TodaysGamesThread extends Thread {
    private List<MLBGameOdds> mlbGameOdds;
    private GameFinder gameFinder;
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    private boolean moreInfo = true;
    private List<MLBGame> gamesToRun;
    private List<GameResult> gameResults;
    private TeamStatFetcher teamStatFetcher;
    private GameCalculatorClass gameCalculatorClass;
    private List<BackTestIngestObject> backTestIngestObjects;
    private DecimalFormat d = new DecimalFormat("#.##");

    public TodaysGamesThread( List<GameResult> gameResults, List<MLBGame> gameList, List<MLBGame> gamesToRun){
        this.gamesToRun = gamesToRun;
        gameFinder = new GameFinder(gameList);
        //gameFinder.startWebDriver();
        this.gameResults = gameResults;
        teamStatFetcher = new TeamStatFetcher();
        teamStatFetcher.setGameList(gameList);
        gameCalculatorClass = new GameCalculatorClass();
        gameCalculatorClass.setGameFinder(gameFinder);
        gameCalculatorClass.setTeamStatFetcher(teamStatFetcher);
        gameCalculatorClass.setMoreInfo(moreInfo);
        gameCalculatorClass.setPlayerStatFetcher(new PlayerStatFetcher());
    }
    @Override
    public void run(){
        runBackTest();
    }

    public List<MLBGameOdds> getMlbGameOdds() {
        return mlbGameOdds;
    }

    public List<BackTestIngestObject> getBackTestIngestObjects() {
        return backTestIngestObjects;
    }

    public void setBackTestIngestObjects(List<BackTestIngestObject> backTestIngestObjects) {
        this.backTestIngestObjects = backTestIngestObjects;
    }

    public void setMlbGameOdds(List<MLBGameOdds> mlbGameOdds) {
        this.mlbGameOdds = mlbGameOdds;
    }

    public void runBackTest() {
        System.out.println("start backtest");
        //playerStatFilter = new PlayerStatFilter();
        //playerStatFilter.setMinuteThreshold(backTestIngestObject.getGameTimeThreshold());
        Instant start = Instant.now();
        int days = 60;
        int correctPredictions = 0;
        int incorrectPredictions = 0;
        int totalGames = 0;
        int daysCompleted = 0;
        int noDecision = 0;
        int predictOver = 0;
        int predictUnder = 0;
        int actualOver = 0;
        int actualUnder = 0;
        int exactMatch = 0;
        int exactLosses = 0;
        int greaterThanXpctCorrect = 0;
        int greaterThanXpctIncorrect = 0;
        double totalPredictedPoints = 0;
        double totalActualPoints = 0;
        int ppgCorrect = 0;
        int ppgIncorrect = 0;
        double percentPerBet = 0.05;
        //PlayerDictionary playerDictionary = new PlayerDictionary();

        //gameFinder.setScrapingProxy(scrapingProxy);
        //gameFinder.setGameRepository(gameRepository);
        //120games


        //System.out.println(games);
        PlayerStatFetcher playerStatFetcher = new PlayerStatFetcher();
        gameCalculatorClass.setPlayerStatFetcher(playerStatFetcher);
        for (BackTestIngestObject backTestIngestObject : backTestIngestObjects) {
            for (MLBGame game : gamesToRun) {
                MLBGame safeGame = (MLBGame) game.clone();
                //boolean shortenedGame = game.isShortenedMakeUpGame();
                LocalDate localDate = game.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Date date = game.getDate();
                ReturnedOdds returnedOdds;


                System.out.println(safeGame);
                //gameFinder.getPlayersForGameId(game, true);
                ScoreModel scoreModel = new ScoreModel();
                PlayerStatFilter playerStatFilter = new PlayerStatFilter();
                //playerStatFilter.setMinuteThreshold(backTestIngestObject.getGameTimeThreshold());
                //System.out.println(game);
                MLBTeam awayTeam = new MLBTeam();
                awayTeam.setMlbId(safeGame.getAwayTeamMlbId());
                awayTeam.setTeamName(safeGame.getAwayTeamName());
                awayTeam.setTeamAbbreviation(safeGame.getAwayTeamTricode());
                Set<MLBPlayer> awayProbableTeam = gameFinder.getPlayerListFromPreviousGameFromDB(safeGame, safeGame.getAwayTeamMlbId());
                awayTeam.setFieldingPlayers(awayProbableTeam);
                safeGame.setAwayMLBTeam(awayTeam);

                //gameFinder.getPlayerListFromPreviousGameFromDB(game,awayTeam.getTeamId());


                MLBTeam homeTeam = new MLBTeam();
                homeTeam.setMlbId(safeGame.getHomeTeamMlbId());
                homeTeam.setTeamName(safeGame.getHomeTeamName());
                homeTeam.setTeamAbbreviation(safeGame.getHomeTeamTricode());
                Set<MLBPlayer> homeProbableTeam = gameFinder.getPlayerListFromPreviousGameFromDB(safeGame, safeGame.getHomeTeamMlbId());
                homeTeam.setFieldingPlayers(homeProbableTeam);

                safeGame.setHomeMLBTeam(homeTeam);
                if (backTestIngestObject.getBetType().equals("overunder")) {
                    gameCalculatorClass.setOverUnder(true);
                }
                gameCalculatorClass.tallyTeamBattingLastNGames(backTestIngestObject, date, playerStatFilter, awayTeam, scoreModel, false, safeGame, true);
                if (backTestIngestObject.getBetType().equals("overunder")) {
                    gameCalculatorClass.setOverUnder(true);
                }
                gameCalculatorClass.tallyTeamBattingLastNGames(backTestIngestObject, date, playerStatFilter, homeTeam, scoreModel, true, safeGame, true);

                mergeScores(scoreModel);


                mergeScores(scoreModel);
                if (backTestIngestObject.isSquareRootTotalPoints()) {
                    StaticScoreModelUtils.squareRootTotal(scoreModel);

                }
                if (game.isShortenedMakeUpGame()) {
                    StaticScoreModelUtils.adjustForShortenedGame(scoreModel);
                }
//                if (backTestIngestObject.isModelOpposingPitching()) {
//                    StaticScoreModelUtils.factorOpponentPitchingIntoScoringModel(scoreModel, game, 1,
//                            1, 1, moreInfo);
//                }
                if (backTestIngestObject.isModelOpposingFielding()) {
                    StaticScoreModelUtils.factorOpponentFieldingIntoScoringModel(scoreModel, game,
                            1, 1, moreInfo);
                }
                if (backTestIngestObject.isModelStolenBases()) {
                    StaticScoreModelUtils.addStolenBasesToModel(scoreModel, 1, 1);
                }

                mergeScores(scoreModel);

                if (backTestIngestObject.getBetType().equals("moneyline")) {
                    MLBGameOdds mlbGameOddsSingle = null;
                    StaticScoreModelUtils.addHomeTeamAdvantage(scoreModel, 1, 1, safeGame.isShortenedMakeUpGame());
                    addStaticHomeTeamAdvantage(scoreModel, 1, safeGame.isShortenedMakeUpGame());
                    mergeScores(scoreModel);
                    //factorStaticOpponentPitchingIntoScoringModel(scoreModel, backTestIngestObject.getHighRunFactor());

                    float homePredicted = (float) ((scoreModel.getHomeHighPoints() + scoreModel.getHomeLowPoints()) / 2);
                    float awayPredicted = (float) ((scoreModel.getAwayHighPoints() + scoreModel.getAwayLowPoints()) / 2);
                    totalPredictedPoints = totalPredictedPoints + (homePredicted + awayPredicted);
                    for (MLBGameOdds mlbGameOdds : mlbGameOdds) {
                        if (mlbGameOdds.getGameId() == safeGame.getGameId()) {
                            mlbGameOddsSingle = mlbGameOdds;
                        }
                    }
                    double pointDifference = homePredicted - awayPredicted;
                    String moneyLineResultString = "";
                    System.out.println(game.getHomeTeamName() + " vs " + game.getAwayTeamName() + game.getDate());
                    System.out.println("(ML) Away Predicted: " + awayPredicted);
                    System.out.println("(ML) Home Predicted: " + homePredicted);
                    System.out.println("Home RunsGivenUp Roc: " + scoreModel.getHomeRunsGivenUpPerGameRoc());
                    System.out.println("Away RunsGivenUp Roc: " + scoreModel.getAwayRunsGivenUpPerGameRoc());
                    System.out.println("Home RunsScoredRoc Roc: " + scoreModel.getHomeRunsScoredPerGameRoc());
                    System.out.println("Away RunsScoredRoc Roc: " + scoreModel.getAwayRunsScoredPerGameRoc());
                    //}
                }
                if (backTestIngestObject.getBetType().equals("spread")) {
                    StaticScoreModelUtils.addHomeTeamAdvantage(scoreModel, 1, 1, game.isShortenedMakeUpGame());
                    addStaticHomeTeamAdvantage(scoreModel, 1, game.isShortenedMakeUpGame());
                   // StaticScoreModelUtils.factorStaticOpponentPitchingIntoScoringModel(scoreModel, game, 1, 1, moreInfo);
                    mergeScores(scoreModel);
                    System.out.println("Score only range: " + scoreModel.getTotalHigh() + " || " + scoreModel.getTotalLow());
                    System.out.println("Away only range: " + scoreModel.getAwayHighPoints() + " || " + scoreModel.getAwayLowPoints());
                    System.out.println("Home only range: " + scoreModel.getHomeHighPoints() + " || " + scoreModel.getHomeLowPoints());
                    System.out.println(safeGame.getHomeMLBTeam().getTeamName() + " vs " + safeGame.getAwayMLBTeam().getTeamName() + safeGame.getDate());
                    MLBGameOdds mlbGameOddsSingle = null;
                    for (MLBGameOdds mlbGameOdds : mlbGameOdds) {
                        if (mlbGameOdds.getGameId() == safeGame.getGameId()) {
                            mlbGameOddsSingle = mlbGameOdds;
                        }
                    }
                    if (mlbGameOddsSingle != null) {
                        StringBuilder stringBuilder = new StringBuilder();
                        double awaySpread = mlbGameOddsSingle.getAwayTeamSpread();
                        double homeSpread = mlbGameOddsSingle.getHomeTeamSpread();
                        double awaySpreadOdds = mlbGameOddsSingle.getAwayTeamSpreadOdds();
                        double homeSpreadOdds = mlbGameOddsSingle.getHomeTeamSpreadOdds();

                        String result = null;

                    }
                }
                if (backTestIngestObject.getBetType().equals("overunder")) {
                    double overUnder = 0.0;
                    MLBGameOdds mlbGameOdds1 = null;
                    for (MLBGameOdds mlbGameOdds : mlbGameOdds) {
                        if (mlbGameOdds.getGameId() == safeGame.getGameId()) {
                            overUnder = mlbGameOdds.getOverUnder();
                            mlbGameOdds1 = mlbGameOdds;
                        }
                    }

                    double upperPct = (scoreModel.getTotalHigh() - overUnder) / ((scoreModel.getTotalHigh() - overUnder) + (overUnder - scoreModel.getTotalLow()));
                    double lowerPct = (overUnder - scoreModel.getTotalLow()) / ((scoreModel.getTotalHigh() - overUnder) + (overUnder - scoreModel.getTotalLow()));
                    StringBuilder stringBuilder;
                    stringBuilder = new StringBuilder();
                    System.out.println("Score only range: " + scoreModel.getTotalHigh() + " || " + scoreModel.getTotalLow());
                    System.out.println("Away only range: " + scoreModel.getAwayHighPoints() + " || " + scoreModel.getAwayLowPoints());
                    System.out.println("Home only range: " + scoreModel.getHomeHighPoints() + " || " + scoreModel.getHomeLowPoints());
                    System.out.println(game.getHomeTeamName() + " vs " + game.getAwayTeamName() + game.getDate());
                    stringBuilder.append("Game High: ").append(scoreModel.getTotalHigh()).append(" : ").append(upperPct * 100).append("%").append("\n");
                    stringBuilder.append("Game Low: ").append(scoreModel.getTotalLow()).append(" : ").append(lowerPct * 100).append("%").append("\n");
                    stringBuilder.append("O/U:").append(overUnder).append("\n");
                    stringBuilder.append("Actual Score: ").append((game.getHomePoints() + game.getAwayPoints())).append("\n");
                    stringBuilder.append(game.getHomeTeamName()).append(":").append(game.getHomePoints()).append("\n");
                    stringBuilder.append(game.getAwayTeamName()).append(":").append(game.getAwayPoints());
                    stringBuilder.append("homeRuns RoC" + scoreModel.getHomeRunsScoredPerGameRoc());
                    System.out.println(stringBuilder);
                    totalActualPoints = totalActualPoints + (safeGame.getHomePoints() + safeGame.getAwayPoints());
//                        if (upperPct > lowerPct) {
//                            System.out.println("PREDICT OVER ON " + safeGame.getHomeTeamName() + " vs " + safeGame.getAwayTeamName());
//                        } else {
//                            System.out.println("PREDICT LOWER ON " + safeGame.getHomeTeamName() + " vs " + safeGame.getAwayTeamName());
//                        }
                    System.out.println("Estimated Points: " + ((scoreModel.getTotalHigh() + scoreModel.getTotalLow()) / 2));
                    totalPredictedPoints = totalPredictedPoints + ((scoreModel.getTotalHigh() + scoreModel.getTotalLow()) / 2);
                    totalGames = totalGames + 1;
                    totalActualPoints = totalActualPoints + (game.getAwayPoints() + game.getHomePoints());

                    totalGames = totalGames + 1;

                    Instant now = Instant.now();
                    long delta = Duration.between(start, now).toMillis();
                    double rate = ((double) totalGames / (double) delta) * 1000;
                    double perMinute = rate * 60;
                    System.out.println("Games per Minute: " + perMinute);
                }
            }

            BackTestResult backTestResult = new BackTestResult();
            backTestResult.setPlayerGameLookBack(backTestIngestObject.getGameCount());
            backTestResult.setDoubleSquareRoot(backTestIngestObject.isDoubleSquareRoot());
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
//            backTestResult.setPpgCorrect(ppgCorrect);
//            backTestResult.setPpgIncorrect(ppgIncorrect);
//            backTestResult.setModelOpponentPitching(backTestIngestObject.isModelOpposingPitching());
            backTestResult.setPitcherGameLookback(backTestIngestObject.getPitcherGameLookback());

            System.out.println(backTestResult);
        }
    }



    public static void mergeScores(ScoreModel scoreModel){
        scoreModel.setTotalHigh(scoreModel.getHomeHighPoints() + scoreModel.getAwayHighPoints());
        scoreModel.setTotalLow(scoreModel.getHomeLowPoints() + scoreModel.getAwayLowPoints());
    }
    public void addStolenBasesToModel(ScoreModel scoreModel, MLBGame game, double highStolenBaseFactor, double lowStolenBaseFactor){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(game.getHomeTeamName()).append(" steal range: " ).append(scoreModel.getHomeHighStolenBases()).append(" || ").append(scoreModel.getHomeLowStolenBases());
        System.out.println(stringBuilder);
        stringBuilder = new StringBuilder();
        stringBuilder.append(game.getAwayTeamName()).append(" steal range: " ).append(scoreModel.getAwayHighStolenBases()).append(" || ").append(scoreModel.getAwayLowStolenBases());
        System.out.println(stringBuilder);
        scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() + (scoreModel.getHomeHighStolenBases() * highStolenBaseFactor));
        scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() + (scoreModel.getHomeLowStolenBases() * lowStolenBaseFactor));
        scoreModel.setAwayHighPoints(scoreModel.getAwayHighPoints() + (scoreModel.getAwayHighStolenBases() * highStolenBaseFactor));
        scoreModel.setAwayLowPoints(scoreModel.getAwayLowPoints() + (scoreModel.getAwayLowStolenBases() * lowStolenBaseFactor));
    }

    public void factorOpponentPitchingIntoScoringModel(ScoreModel scoreModel, MLBGame game, double runDifferentialFactor, double highRunFactor, double lowRunFactor){
        if(moreInfo) {
            System.out.println("Before pitching Differential Range : " + (scoreModel.getHomeHighPoints() + scoreModel.getAwayHighPoints()) + " || " + (scoreModel.getHomeLowPoints() + scoreModel.getAwayLowPoints()));
            //BLOCKS FORECASTED BY HOME TEAM
            System.out.println("Home Pitching Range: " + scoreModel.getHomeRunsGivenUpHigh() + " || " + scoreModel.getHomeRunsGivenUpLow());
            System.out.println("This compares to Away Team's (" + game.getAwayMLBTeam().getTeamName() + ") prior scoring against pitching range of: " + scoreModel.getAwayPitchingModelHigh() + " || " + scoreModel.getAwayPitchingModelLow());
            //BLOCKS FORECASTED BY AWAY TEAM
            //double awayBlockMidPoint = (scoreModel.getAwayHighBlocks() + scoreModel.getAwayLowBlocks())/2;
            System.out.println("Away Pitching Range: " + scoreModel.getAwayRunsGivenUpHigh() + " || " + scoreModel.getAwayRunsGivenUpLow());
            System.out.println("This compares to Home Team's (" + game.getHomeMLBTeam().getTeamName() + ")prior scoring against pitching range of: " + scoreModel.getHomePitchingModelHigh() + " || " + scoreModel.getHomePitchingModelLow());
        }
        //FORECASTED DIFFERENCE IN BLOCKS BY HOME TEAM
        double homeBlockDifferentialHigh = scoreModel.getHomePitchingModelHigh() - scoreModel.getAwayRunsGivenUpHigh();
        double homeBlockDifferentialLow = scoreModel.getHomePitchingModelLow() - scoreModel.getAwayRunsGivenUpLow();
        System.out.println("Home Team expects to play against a pitching range differential of: " + homeBlockDifferentialHigh + " || " + homeBlockDifferentialLow);
        double awayBlockDifferentialHigh = scoreModel.getAwayPitchingModelHigh() - scoreModel.getHomeRunsGivenUpHigh();
        double awayBlockDifferentialLow = scoreModel.getAwayPitchingModelLow() - scoreModel.getHomeRunsGivenUpLow();
        System.out.println("Away Team expects to play against a pitching range differential of: " + awayBlockDifferentialHigh + " || " + awayBlockDifferentialLow);

        //double awayBlockMidPointDifferenceToModel = awayBlockMidPoint - scoreModel.getHomeBlockScoringModel();
        //System.out.println("Forecasted Change Of Blocks to Away Team: " + awayBlockMidPointDifferenceToModel);
        scoreModel.setAwayHighPoints(scoreModel.getAwayHighPoints() - ((homeBlockDifferentialHigh * runDifferentialFactor) * highRunFactor));
        scoreModel.setAwayLowPoints(scoreModel.getAwayLowPoints() - ((homeBlockDifferentialLow * runDifferentialFactor) * lowRunFactor));
        scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() - ((awayBlockDifferentialHigh * runDifferentialFactor) * highRunFactor));
        scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() - ((awayBlockDifferentialLow * runDifferentialFactor) * lowRunFactor));
        System.out.println("After pitching Differential Range : " + (scoreModel.getHomeHighPoints() + scoreModel.getAwayHighPoints()) + " || " + (scoreModel.getHomeLowPoints() + scoreModel.getAwayLowPoints()));
    }

    public void factorOpponentFieldingIntoScoringModel(ScoreModel scoreModel, MLBGame game, double highFieldingFactor, double lowFieldingFactor){
        StringBuilder stringBuilder = new StringBuilder();
        if(moreInfo) {
            System.out.println("Before fielding Differential Range : " + (scoreModel.getHomeHighPoints() + scoreModel.getAwayHighPoints()) + " || " + (scoreModel.getHomeLowPoints() + scoreModel.getAwayLowPoints()));
            System.out.println("Before Away Fielding Differential Range : " + (scoreModel.getAwayHighPoints()) + " || " + (scoreModel.getAwayLowPoints()));
            //BLOCKS FORECASTED BY HOME TEAM
            System.out.println("Home Fielding Range: " + scoreModel.getHomeRunsGivenUpHigh() + " || " + scoreModel.getHomeRunsGivenUpLow());
            System.out.println("This compares to Away Team's (" + game.getAwayMLBTeam().getTeamName() + ") prior scoring against fielding range of: " + scoreModel.getAwayPitchingModelHigh() + " || " + scoreModel.getAwayPitchingModelLow());
            //BLOCKS FORECASTED BY AWAY TEAM
            //double awayBlockMidPoint = (scoreModel.getAwayHighBlocks() + scoreModel.getAwayLowBlocks())/2;
            System.out.println("Away Fielding Range: " + scoreModel.getAwayRunsGivenUpHigh() + " || " + scoreModel.getAwayRunsGivenUpLow());
            System.out.println("This compares to Home Team's (" + game.getHomeMLBTeam().getTeamName() + ")prior scoring against fielding range of: " + scoreModel.getHomePitchingModelHigh() + " || " + scoreModel.getHomePitchingModelLow());
        }
        //FORECASTED DIFFERENCE IN BLOCKS BY HOME TEAM
        double homeFieldingDifferentialHigh = scoreModel.getHomeFieldingModelHigh() - scoreModel.getAwayFieldingHigh();
        double homeFieldingDifferentialLow = scoreModel.getHomeFieldingMoelLow() - scoreModel.getAwayFieldingLow();
        stringBuilder.append("Home Team expects to play against a fielding range differential of: ").append(homeFieldingDifferentialHigh).append(" || ").append(homeFieldingDifferentialLow).append("\n");
        double awayFieldingDifferentialHigh = scoreModel.getAwayFieldingModelHigh() - scoreModel.getHomeFieldingHigh();
        double awayFieldingDifferentialLow = scoreModel.getAwayFieldingModelLow() - scoreModel.getHomeFieldingLow();
        stringBuilder.append("Away Team expects to play against a fielding range differential of: ").append(awayFieldingDifferentialHigh).append(" || ").append(awayFieldingDifferentialLow).append("\n");

        //double awayBlockMidPointDifferenceToModel = awayBlockMidPoint - scoreModel.getHomeBlockScoringModel();
        //System.out.println("Forecasted Change Of Blocks to Away Team: " + awayBlockMidPointDifferenceToModel);
        double test = (homeFieldingDifferentialLow * lowFieldingFactor);
        scoreModel.setAwayHighPoints(scoreModel.getAwayHighPoints() - test);
        scoreModel.setAwayLowPoints(scoreModel.getAwayLowPoints() - ((homeFieldingDifferentialHigh * highFieldingFactor)));
        scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() - ((awayFieldingDifferentialLow * lowFieldingFactor)));
        scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() - ((awayFieldingDifferentialHigh * highFieldingFactor)));
        //System.out.println("After Away Fielding Differential Range : " + (scoreModel.getAwayHighPoints()) + " || " + (scoreModel.getAwayLowPoints()));
        //System.out.println("After Home Fielding Differential Range : " + (scoreModel.getHomeHighPoints()) + " || " + (scoreModel.getHomeHighPoints()));
        stringBuilder.append("After fielding Differential Range : ").append(scoreModel.getHomeHighPoints() + scoreModel.getAwayHighPoints()).append(" || ").append(scoreModel.getHomeLowPoints() + scoreModel.getAwayLowPoints()).append("\n");
        System.out.println(stringBuilder);
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

    public double convertDecimalToAmerican(double spreadOdds){
        System.out.println("OG spread odds: " + spreadOdds);
        if(spreadOdds>=100 || spreadOdds <=-100){
            return spreadOdds;
        }else {
            double temp;
            if (spreadOdds < 2) {
                temp = -100 / (spreadOdds - 1);
            } else {
                temp = (spreadOdds - 1) * 100;
            }
            return temp;
        }
    }

    public double getProbability(double spreadOdds){
        System.out.println("OG spread odds: " + spreadOdds);
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


    private void addStaticHomeTeamAdvantage(ScoreModel safeScoreModel, double homeAdvantageHigh, boolean shortenedMakeUpGame) {
//        if(shortenedMakeUpGame){
//            safeScoreModel.setHomePPG((float) (safeScoreModel.getHomePPG() + (homeAdvantageHigh * (7.0/9.0))));
//        }else{
//            safeScoreModel.setHomePPG((float) (safeScoreModel.getHomePPG() + (homeAdvantageHigh)));
//        }
    }
//    public void factorStaticOpponentPitchingIntoScoringModel(ScoreModel scoreModel, double highRunFactor){
//        float homeRunsGivenDifferential = (float) (scoreModel.getHomeModelRunsGivenUpPerGame() - scoreModel.getAwayRGPG());
//        double awayRunsGivenDifferential = scoreModel.getAwayModelRunsGivenUpPerGameHigh() - scoreModel.getHomeRGPG();
//        scoreModel.setAwayPPG((float) (scoreModel.getAwayPPG() - ((homeRunsGivenDifferential * 1) * highRunFactor)));
//        scoreModel.setHomePPG((float) (scoreModel.getHomePPG() - ((awayRunsGivenDifferential * 1) * highRunFactor)));
//    }
}
