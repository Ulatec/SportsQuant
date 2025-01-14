package SportsQuant.Threads;

import BaseballQuant.Model.MLBGame;
import BaseballQuant.Model.MLBGameOdds;
import BaseballQuant.Util.TeamStatFetcher;
import SportsQuant.Model.*;
import SportsQuant.Repository.GameRepository;
import SportsQuant.Util.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class TodaysGamesThread extends Thread{
    private HashMap<Integer,GameOdds> gameOddsHashMap;
    private GameRepository gameRepository;
    private GameFinder gameFinder;
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    private List<Game> gamesToRun;
    private boolean moreInfo = false;
    private List<GameResult> gameResults;
    private GameCalculatorClass gameCalculatorClass;
    private List<BackTestIngestObject> backTestIngestObjects;
    private DecimalFormat d = new DecimalFormat("#.##");

    public TodaysGamesThread(List<GameResult> gameResults, List<Game> gameList, List<Game> gamesToRun){
        this.gamesToRun = gamesToRun;
        gameFinder = new GameFinder(gameList);
        //gameFinder.startWebDriver();
        this.gameResults = gameResults;
        gameCalculatorClass = new GameCalculatorClass();
        gameCalculatorClass.setGameFinder(gameFinder);
        gameCalculatorClass.setMoreInfo(moreInfo);
        gameCalculatorClass.setPlayerStatFetcher(new PlayerStatFetcher());
    }
    @Override
    public void run(){
        runBackTest();
    }
    public List<GameResult> getGameResults() {
        return gameResults;
    }

    public HashMap<Integer, GameOdds> getGameOddsHashMap() {
        return gameOddsHashMap;
    }

    public void setGameOddsHashMap(HashMap<Integer, GameOdds> gameOddsHashMap) {
        this.gameOddsHashMap = gameOddsHashMap;
    }

    public List<BackTestIngestObject> getBackTestIngestObjects() {
        return backTestIngestObjects;
    }

    public void setBackTestIngestObjects(List<BackTestIngestObject> backTestIngestObjects) {
        this.backTestIngestObjects = backTestIngestObjects;
    }

    public void setGameResults(List<GameResult> gameResults) {
        this.gameResults = gameResults;
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
        //PlayerDictionary playerDictionary = new PlayerDictionary();


        PlayerStatFetcher playerStatFetcher = new PlayerStatFetcher();
        gameCalculatorClass.setPlayerStatFetcher(playerStatFetcher);
        for (BackTestIngestObject backTestIngestObject : backTestIngestObjects) {
            for (Game game : gamesToRun) {
                Game safeGame = (Game) game.clone();
                LocalDate localDate = safeGame.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                //System.out.println(games);
                Date date = safeGame.getDate();

                //System.out.println(game.getGameId());
                gameFinder.getPlayersForGameId(safeGame, true);

                PlayerStatFilter playerStatFilter = new PlayerStatFilter();
                playerStatFilter.setMinuteThreshold(backTestIngestObject.getGameTimeThreshold());
                //System.out.println(game);
                Team awayTeam = safeGame.getAwayTeam();
                Set<Player> awayProbableTeam = gameFinder.getPlayerListFromPreviousGameFromDB(safeGame, safeGame.getAwayTeam().getTeamId());
                awayTeam.setPlayers(awayProbableTeam);
                //gameFinder.getPlayerListFromPreviousGameFromDB(game,awayTeam.getTeamId());
                ScoreModel scoreModel = new ScoreModel();
              //  gameCalculatorClass.tallyTeamLastNGames(backTestIngestObject, date, playerStatFilter,
             //           awayTeam, scoreModel, false, false, backTestIngestObject.getDayLookbackCap(), backTestIngestObject.getGameTimeThreshold(), false);
                Team homeTeam = safeGame.getHomeTeam();
                Set<Player> homeProbableTeam = gameFinder.getPlayerListFromPreviousGameFromDB(safeGame, safeGame.getHomeTeam().getTeamId());
                homeTeam.setPlayers(homeProbableTeam);
             //   gameCalculatorClass.tallyTeamLastNGames(backTestIngestObject, date, playerStatFilter,
             //           homeTeam, scoreModel, false, true, backTestIngestObject.getDayLookbackCap(), backTestIngestObject.getGameTimeThreshold(), false);


                mergeScores(scoreModel);
                //System.out.println("Score only range: " + scoreModel.getTotalHigh() + " || " + scoreModel.getTotalLow());
                if (backTestIngestObject.isSquareRootTotalPoints()) {
                    squareRootTotalPoints(scoreModel);
                }

                if (backTestIngestObject.isModelOpponentBlocks()) {
                    StaticScoreModelUtils.factorBlocksIntoScoringModel(scoreModel, backTestIngestObject.getPointsReducedPerBlock(),
                            backTestIngestObject.getLowerBlockPointFactor(), backTestIngestObject.getHighBlockPointFactor());
                    //factorAverageBlocksIntoScoringModel(scoreModel, backTestIngestObject.getPointsReducedPerBlock());
                }
                if (backTestIngestObject.isModelOpponentSteals()) {
                    StaticScoreModelUtils.factorStealsIntoScoringModel(scoreModel, backTestIngestObject.getPointReductionPerSteal(),
                            backTestIngestObject.getLowerStealPointFactor(), backTestIngestObject.getHighStealPointFactor());
                }
                if(backTestIngestObject.isModelOpponentTurnovers()){
                    StaticScoreModelUtils.factorTurnoversIntoScoringModel(scoreModel, backTestIngestObject.getPointsReducedPerTurnover(),
                            backTestIngestObject.getLowerTurnoverPointFactor(), backTestIngestObject.getHighTurnoverPointFactor());
                }

                mergeScores(scoreModel);


                System.out.println(safeGame.getHomeTeam().getTeamName() + " vs " + safeGame.getAwayTeam().getTeamName() + safeGame.getDate());
//                double overUnder;
//
//                double upperPct = (scoreModel.getTotalHigh() - overUnder) / ((scoreModel.getTotalHigh() - overUnder) + (overUnder - scoreModel.getTotalLow()));
//                double lowerPct = (overUnder - scoreModel.getTotalLow()) / ((scoreModel.getTotalHigh() - overUnder) + (overUnder - scoreModel.getTotalLow()));
//                System.out.println("Game High: " + scoreModel.getTotalHigh() + " : " + (upperPct * 100) + "%");
//                System.out.println("Game Low: " + scoreModel.getTotalLow() + " : " + (lowerPct * 100) + "%");
//                System.out.println("O/U:" + overUnder);
//                System.out.println("Actual Score: " + (safeGame.getHomePoints() + safeGame.getAwayPoints()));
//                totalActualPoints = totalActualPoints + (safeGame.getHomePoints() + safeGame.getAwayPoints());
//                if (upperPct > lowerPct) {
//                    System.out.println("PREDICT OVER ON " + safeGame.getHomeTeamName() + " vs " + safeGame.getAwayTeamName());
//                } else {
//                    System.out.println("PREDICT LOWER ON " + safeGame.getHomeTeamName() + " vs " + safeGame.getAwayTeamName());
//                }

                totalPredictedPoints = totalPredictedPoints + ((scoreModel.getTotalHigh() + scoreModel.getTotalLow()) / 2);
                totalGames = totalGames + 1;
//            BigDecimal b1 = new BigDecimal(((double) correctPredictions / (double) (correctPredictions + incorrectPredictions)) * 100);
//            MathContext m = new MathContext(4);
//            double awaySpread = game.getAwaySpread();
//            double homeSpread = game.getHomeSpread();
                if (backTestIngestObject.getBetType().equals("spread")) {
                    if (gameOddsHashMap.get(safeGame.getGameId()) != null) {
                        GameOdds gameOddsSingle = gameOddsHashMap.get(safeGame.getGameId());
                        StaticScoreModelUtils.addHomeTeamAdvantage(scoreModel, backTestIngestObject.getHomeTeamAdvantage());
                        mergeScores(scoreModel);
                        //totalPredictedPoints = totalPredictedPoints + (safeScoreModel.getHomePPG() + safeScoreModel.getAwayPPG());
                        totalPredictedPoints = totalPredictedPoints + ((scoreModel.getTotalHigh() + scoreModel.getTotalLow()) / 2);
                        totalActualPoints = totalActualPoints + (safeGame.getAwayPoints() + safeGame.getHomePoints());
                        float HomePredictedPoints = (float) (scoreModel.getHomeHighPoints() + scoreModel.getHomeLowPoints()) / 2;
                        float AwayPredictedPoints = (float) (scoreModel.getAwayHighPoints() + scoreModel.getAwayLowPoints()) / 2;
                        float pointDifference = HomePredictedPoints - AwayPredictedPoints;
                        double awaySpread = gameOddsSingle.getAwayTeamSpread();
                        double homeSpread = gameOddsSingle.getHomeTeamSpread();
                        double awaySpreadOdds = gameOddsSingle.getAwayTeamSpreadOdds();
                        double homeSpreadOdds = gameOddsSingle.getHomeTeamSpreadOdds();
                        double pointClass = pointDifference - gameOddsSingle.getHomeTeamSpread();
                        System.out.println("(Spread) Home Predicted: " + HomePredictedPoints);
                        System.out.println("(Spread) Away Predicted: " + AwayPredictedPoints);
                        String result = null;
                        if (pointClass <= backTestIngestObject.getPointThreshold() * -1 || pointClass >= backTestIngestObject.getPointThreshold()) {
                            System.out.println("pointClass = " + pointClass);
                            if (HomePredictedPoints > AwayPredictedPoints) {
                                System.out.println(safeGame.getHomeTeamName() + " is predicted to win by " + pointDifference + " points");
                                System.out.println(safeGame.getHomeTeamName() + " spread is " + homeSpread);
                                double invertedHomeSpread = homeSpread * -1;
                                if (pointDifference >= invertedHomeSpread) {
                                    result = safeGame.getHomeTeamName() + " " + homeSpread + " " + d.format(convertDecimalToAmerican(homeSpreadOdds));
                                    System.out.println("Selecting " + safeGame.getHomeTeamName() + ". Odds are " + convertDecimalToAmerican(homeSpreadOdds) + ".");
                                    //startingMoney = startingMoney + (((startingMoney * percentPerBet) *(applyWinningSpreadOdds(homeSpreadOdds)-1)));
                                } else {
                                    result = safeGame.getAwayTeamName() + " " + awaySpread + " " + d.format(convertDecimalToAmerican(awaySpreadOdds));
                                    System.out.println("Selecting " + safeGame.getAwayTeamName() + ". Odds are " + convertDecimalToAmerican(awaySpreadOdds) + ".");
                                    //startingMoney = startingMoney + (((startingMoney * percentPerBet) *(applyWinningSpreadOdds(homeSpreadOdds)-1)));
                                }
                            } else {
                                System.out.println(safeGame.getAwayTeamName() + " is predicted to win by " + (pointDifference * -1) + " points");
                                System.out.println(safeGame.getAwayTeamName() + " spread is " + awaySpread);
                                double inverted = (pointDifference * -1);
                                double invertedAwaySpread = awaySpread * -1;
                                if (inverted >= invertedAwaySpread) {
                                    result = safeGame.getAwayTeamName() + " " + awaySpread + " " + d.format(convertDecimalToAmerican(awaySpreadOdds));
                                    System.out.println("Selecting " + safeGame.getAwayTeamName() + ". Odds are " + convertDecimalToAmerican(awaySpreadOdds) + ".");
                                } else {
                                    result = safeGame.getHomeTeamName() + " " + homeSpread + " " + d.format(convertDecimalToAmerican(homeSpreadOdds));
                                    System.out.println("Selecting " + safeGame.getHomeTeamName() + ". Odds are " + convertDecimalToAmerican(homeSpreadOdds) + ".");
                                }
                            }
                        }
                        for (GameResult gameResult : gameResults) {
                            if (gameResult.getGameId() == safeGame.getGameId()) {
                                gameResult.setAwayPredictedPoints(AwayPredictedPoints);
                                gameResult.setHomePredictedPoints(HomePredictedPoints);
                                gameResult.setSpread_result(result);
                            }
                        }

                    }

                    //System.out.println(ANSI_GREEN + "PPG CORRECT PREDICTIONS: " + ppgCorrect + " || " + "PPG INCORRECT PREDICTIONS: " + ppgIncorrect + "(" + b2.round(m).doubleValue() + "%)" + ANSI_RESET);
                    //System.out.println(ANSI_GREEN + "PREDICTIONS OVER: " + predictOver + " PREDICTIONS UNDER: " + predictUnder + " || ACTUAL OVER: " + actualOver + " ACTUAL UNDER: " + actualUnder + " || TotalPredictedPoints: " + totalPredictedPoints + " TotalActualPoints: " + totalActualPoints + " (" + ((totalPredictedPoints - totalActualPoints)/(totalActualPoints)*100) + "%)" + ANSI_RESET);
                    //System.out.println(ANSI_GREEN + backTestResults + ANSI_RESET);
                    //System.out.println(ANSI_GREEN + "[" + threadNum + "] " + "[" + (backTestResults.size() + 1) + "/" + backTestIngestObjects.size() + "]" + ANSI_RESET);

                    Instant now = Instant.now();
                    long delta = Duration.between(start, now).toMillis();
                    double rate = ((double) totalGames / (double) delta) * 1000;
                    double perMinute = rate * 60;
                }

            }
        }


}



    public static void mergeScores(ScoreModel scoreModel){
        scoreModel.setTotalHigh(scoreModel.getHomeHighPoints() + scoreModel.getAwayHighPoints());
        scoreModel.setTotalLow(scoreModel.getHomeLowPoints() + scoreModel.getAwayLowPoints());
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
}
