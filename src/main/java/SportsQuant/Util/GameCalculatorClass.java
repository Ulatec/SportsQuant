package SportsQuant.Util;

import BaseballQuant.Model.MLBGame;
import BaseballQuant.Model.MLBTeam;
import SportsQuant.Model.*;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class GameCalculatorClass {
    private PlayerStatFetcher playerStatFetcher;
    private TeamStatFetcher teamStatFetcher;
    private boolean moreInfo;
    private GameFinder gameFinder;
    public GameCalculatorClass(){

    }

    public boolean isMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(boolean moreInfo) {
        this.moreInfo = moreInfo;
    }

    public GameFinder getGameFinder() {
        return gameFinder;
    }

    public void setGameFinder(GameFinder gameFinder) {
        this.gameFinder = gameFinder;
    }

    public PlayerStatFetcher getPlayerStatFetcher() {
        return playerStatFetcher;
    }

    public void setPlayerStatFetcher(PlayerStatFetcher playerStatFetcher) {
        this.playerStatFetcher = playerStatFetcher;
    }
    public void setTeamStatFetcher(TeamStatFetcher teamStatFetcher) {
        this.teamStatFetcher = teamStatFetcher;
    }

    public void tallyTeamLastNGames(BackTestIngestObject backTestIngestObject, Date date, PlayerStatFilter playerStatFilter,
                                    Team team, ScoreModel scoreModel, boolean replaceLowBlocksWithBPG, boolean home, int dayLookbackCap, double gameTimeThreshold, boolean moreInfo) {

        int gameCount = backTestIngestObject.getPlayerGameLookBack();
        double totalPointsHome = 0;
        double highPoints = 0;
        double lowPoints = 0;
        double highBlocks = 0;
        double lowBlocks = 0;
        List<Double> blockScoreModelHighs = new ArrayList<>();
        List<Double> blockScoreModelLows = new ArrayList<>();
        List<Double> stealScoreModelHighs = new ArrayList<>();
        List<Double> stealScoreModelLows = new ArrayList<>();
        List<Double> turnoverScoreModelHighs = new ArrayList<>();
        List<Double> turnoverScoreModelLows = new ArrayList<>();
        List<Double> reboundScoreModelHighs = new ArrayList<>();
        List<Double> reboundScoreModelLows = new ArrayList<>();
        List<Double> foulsScoreModelHighs = new ArrayList<>();
        List<Double> foulsScoreModelLows = new ArrayList<>();
        double highSteals = 0;
        double lowSteals = 0;
        double highTurnovers = 0;
        double lowTurnovers = 0;
        double highRebounds = 0;
        double lowRebounds = 0;
        double highFouls = 0;
        double lowFouls = 0;
        double PointsPerGame = 0.0;
        double BlocksPerGame = 0.0;
        double StealsPerGame = 0.0;
        double ReboundsPerGame = 0.0;
        double TurnoversPerGame = 0.0;
        double FoulsPerGame = 0.0;
        double FreeThrowsPerGame = 0.0;
        double pointsRoc = 0.0;
        double blocksRoc = 0.0;
        double reboundsRoc = 0.0;
        double stealsRoc = 0.0;
        double turnoversRoc = 0.0;
        double foulsRoc = 0.0;
        double freethrowpctRoc = 0.0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy/MM/dd");
        //System.out.println("playersize: " + team.getFieldingPlayers().size());
        for(Player player : team.getPlayers()) {
            Player playerClone = (Player) player.clone();

            playerStatFilter.trimPlayerGameHistory(gameCount, playerClone, date, gameTimeThreshold);

            //TODO:: Modeling test using rate of change of player performance;

            double playerPoints = playerStatFilter.getForecastedPlayerPointsWithDerivative(gameCount, playerClone, date, dayLookbackCap, gameTimeThreshold);
            double playerPointsRoc = playerStatFilter.getPointsRateOfChange(gameCount, playerClone, date);
            pointsRoc += playerPointsRoc;
            double playerBlocksRoc = playerStatFilter.getBlocksRateOfChange(gameCount, playerClone, date);
            blocksRoc += playerBlocksRoc;
            double playerReboundsRoc = playerStatFilter.getReboundsRateOfChange(gameCount, playerClone, date);
            reboundsRoc += playerReboundsRoc;
            double playerStealsRoc = playerStatFilter.getStealsRateOfChange(gameCount, playerClone, date);
            stealsRoc += playerStealsRoc;
            double playerTurnoversRoc = playerStatFilter.getTurnoversRateOfChange(gameCount, playerClone, date);
            turnoversRoc += playerTurnoversRoc;
            double playerFoulsRoc = playerStatFilter.getFoulsRateOfChange(gameCount, playerClone, date);
            foulsRoc += playerFoulsRoc;
            double playerfreethrowpctroc = playerStatFilter.getFreeThrowsRateOfChange(gameCount, playerClone, date);
            freethrowpctRoc += playerfreethrowpctroc;

            //int playerPoints = playerStatFilter.getPlayerPointsLastNGames(gameCount, playerClone, date, dayLookbackCap, gameTimeThreshold);
            PerformanceReturnObject performanceReturnObject = playerStatFetcher.modelOpponentStats(gameCount, playerClone, date, gameFinder,
                    playerStatFilter, gameTimeThreshold, dayLookbackCap, backTestIngestObject.isDoubleSquareRoot(), backTestIngestObject.isAllowBelowZero());
            if(performanceReturnObject != null) {
                blockScoreModelHighs.add(performanceReturnObject.getOpponentBlocksHigh());
                blockScoreModelLows.add(performanceReturnObject.getOpponentBlocksLow());
                stealScoreModelHighs.add(performanceReturnObject.getOpponentStealsHigh());
                stealScoreModelLows.add(performanceReturnObject.getOpponentStealsLow());
                turnoverScoreModelHighs.add(performanceReturnObject.getOpponentTurnoversHigh());
                turnoverScoreModelLows.add(performanceReturnObject.getOpponentTurnoversLow());
                reboundScoreModelHighs.add(performanceReturnObject.getOpponentTurnoversHigh());
                reboundScoreModelLows.add(performanceReturnObject.getOpponentTurnoversLow());
                foulsScoreModelHighs.add(performanceReturnObject.getOpponentFoulHigh());
                foulsScoreModelLows.add(performanceReturnObject.getOpponentFoulLow());
            }

            //TODO:: MODELING TEST
            //if(playerPoints>0) {
//            double playerPointsPerGame = playerStatFilter.getForecastedPlayerPointsWithDerivative(gameCount, playerClone, date,  dayLookbackCap, gameTimeThreshold);
//            double playerBlocksPerGame = playerStatFilter.getForecastedPlayerBlocksWithDerivative(gameCount, playerClone, date, dayLookbackCap, gameTimeThreshold);
//            double playerStealsPerGame = playerStatFilter.getForecastedPlayerStealsWithDerivative(gameCount, playerClone, date, dayLookbackCap, gameTimeThreshold);
//            double playerReboundsPerGame = playerStatFilter.getForecastedPlayerReboundsWithDerivative(gameCount, playerClone, date,dayLookbackCap, gameTimeThreshold);
//            double playerTurnoversPerGame = playerStatFilter.getForecastedPlayerTurnoversWithDerivative(gameCount, playerClone, date,dayLookbackCap, gameTimeThreshold);
//            double playerFoulsPerGame = playerStatFilter.getForecastedPlayerFoulsWithDerivative(gameCount,playerClone,date,dayLookbackCap, gameTimeThreshold);


            //double playerFieldGoalsPerGame = playerStatFilter.getPlayerFieldGoalAttemptsPerGameLastNGames(gameCount,playerClone,date, gameTimeThreshold);
            double playerPointsPerGame = playerStatFilter.getPlayerPointsPerGameLastNGames(gameCount, playerClone, date,  gameTimeThreshold);
            PointsPerGame+=playerPointsPerGame;
            double playerBlocksPerGame = playerStatFilter.getPlayerBlocksPerGameLastNGames(gameCount, playerClone, date,  gameTimeThreshold);
            BlocksPerGame += playerBlocksPerGame;
            double playerStealsPerGame = playerStatFilter.getPlayerStealsPerGameLastNGames(gameCount, playerClone, date, gameTimeThreshold);
            StealsPerGame += playerStealsPerGame;
            double playerReboundsPerGame = playerStatFilter.getPlayerReboundsPerGameLastNGames(gameCount, playerClone, date, gameTimeThreshold);
            ReboundsPerGame += playerReboundsPerGame;
            double playerTurnoversPerGame = playerStatFilter.getPlayerTurnoversPerGameLastNGames(gameCount, playerClone, date, gameTimeThreshold);
            TurnoversPerGame += playerTurnoversPerGame;
            double playerFoulsPerGame = playerStatFilter.getPlayerFoulsPerGameLastNGames(gameCount,playerClone,date, gameTimeThreshold);
            FoulsPerGame += playerFoulsPerGame;
            double playerFreeThrowsPerGame = playerStatFilter.getPlayerFreeThrowsPerGameLastNGames(gameCount,playerClone,date, gameTimeThreshold);
            FreeThrowsPerGame += playerFreeThrowsPerGame;

            double playerPointsStdDev;
            double playerBlocksStdDev;
            double playerStealsStdDev;
            double playerTurnoversStdDev;
            double playerReboundsStdDev;
            double playerFoulsStdDev;
                if (backTestIngestObject.isDoubleSquareRoot()) {
                    playerPointsStdDev = Math.sqrt(playerStatFilter.getPlayerPointsLastNGamesStdDev(gameCount, playerClone, date, gameTimeThreshold));
                    playerBlocksStdDev = Math.sqrt(playerStatFilter.getPlayerBlocksLastNGamesStdDev(gameCount, playerClone, date, gameTimeThreshold));
                    playerStealsStdDev = Math.sqrt(playerStatFilter.getPlayerStealsLastNGamesStdDev(gameCount, playerClone, date, gameTimeThreshold));
                    playerTurnoversStdDev = Math.sqrt(playerStatFilter.getPlayerTurnoversLastNGamesStdDev(gameCount, playerClone, date, gameTimeThreshold));
                    playerReboundsStdDev = Math.sqrt(playerStatFilter.getPlayerReboundsLastNGamesStdDev(gameCount, playerClone, date, gameTimeThreshold));
                    playerFoulsStdDev = Math.sqrt(playerStatFilter.getPlayerFoulsLastNGamesStdDev(gameCount, playerClone, date, gameTimeThreshold));
                } else {
                    playerPointsStdDev = playerStatFilter.getPlayerPointsLastNGamesStdDev(gameCount, playerClone, date, gameTimeThreshold);
                    playerBlocksStdDev = playerStatFilter.getPlayerBlocksLastNGamesStdDev(gameCount, playerClone, date, gameTimeThreshold);
                    playerStealsStdDev = playerStatFilter.getPlayerStealsLastNGamesStdDev(gameCount, playerClone, date, gameTimeThreshold);
                    playerTurnoversStdDev = playerStatFilter.getPlayerTurnoversLastNGamesStdDev(gameCount, playerClone, date, gameTimeThreshold);
                    playerReboundsStdDev = playerStatFilter.getPlayerReboundsLastNGamesStdDev(gameCount, playerClone, date, gameTimeThreshold);
                    playerFoulsStdDev = playerStatFilter.getPlayerFoulsLastNGamesStdDev(gameCount, playerClone, date, gameTimeThreshold);
                }

                //double playerPointsStdDev = playerStatFilter.getPlayerPointsLastNGamesStdDev(gameCount, player, date)/2;
                if (moreInfo) {
                    String inputDates = "[";
                    for (PlayerGamePerformance playerGamePerformance : playerClone.getPlayerGamePerformances()) {
                        inputDates = inputDates + simpleDateFormat.format(playerGamePerformance.getDate()) + ",";
                    }
                    inputDates = inputDates + "]";
                    System.out.println(playerClone.getFirstName() + " " + playerClone.getLastName() + " High: " + (playerPointsPerGame + playerPointsStdDev) + " || " + "Low: " + (playerPointsPerGame - playerPointsStdDev) + "(" + playerPointsStdDev + ") BLOCKS:: High: " + (playerBlocksPerGame + playerBlocksStdDev) + " || " + (playerBlocksPerGame - playerBlocksStdDev) + inputDates);
                    //System.out.println("PLAYER BPG: " + playerBlocksPerGame);
                }

                if (!Double.isNaN(playerPointsStdDev)) {
                    highPoints = highPoints + (playerPointsPerGame + playerPointsStdDev);
//                    if (backTestIngestObject.isAllowBelowZero()) {
                        lowPoints = lowPoints + ((playerPointsPerGame - playerPointsStdDev));
//                    } else {
//                        if ((playerPointsPerGame - playerPointsStdDev) > 0) {
//                            lowPoints = lowPoints + ((playerPointsPerGame - playerPointsStdDev));
//                        }
//                    }
                }

                if (!Double.isNaN(playerStealsStdDev)) {
                    highSteals = highSteals + (playerStealsPerGame + playerStealsStdDev);
                    if (backTestIngestObject.isAllowBelowZero()) {
                        lowSteals = lowSteals + ((playerStealsPerGame - playerStealsStdDev));
                    } else {
                        if ((playerStealsPerGame - playerStealsStdDev) > 0) {
                            lowSteals = lowSteals + ((playerPointsPerGame - playerPointsStdDev));
                        }
                    }
                }

                if (!Double.isNaN(playerBlocksStdDev)) {
                    highBlocks = highBlocks + (playerBlocksPerGame + playerBlocksStdDev);
                    if (backTestIngestObject.isAllowBelowZero()) {
                        lowBlocks = lowBlocks + (((playerBlocksPerGame - playerBlocksStdDev)));
                    } else {
                        if ((playerBlocksPerGame - playerBlocksStdDev) > 0) {
                            lowBlocks = lowBlocks + (((playerBlocksPerGame - playerBlocksStdDev)));
                        }
                    }
                }
            if (!Double.isNaN(playerTurnoversStdDev)) {
                highTurnovers = highTurnovers + (playerTurnoversPerGame + playerTurnoversStdDev);
                if (backTestIngestObject.isAllowBelowZero()) {
                    lowTurnovers = lowTurnovers + (((playerTurnoversPerGame - playerTurnoversStdDev)));
                } else {
                    if ((playerTurnoversPerGame - playerTurnoversStdDev) > 0) {
                        lowTurnovers = lowTurnovers + (((playerTurnoversPerGame - playerTurnoversStdDev)));
                    }
                }
            }
            if (!Double.isNaN(playerReboundsStdDev)) {
                highRebounds = highRebounds + (playerReboundsPerGame + playerReboundsStdDev);
                if (backTestIngestObject.isAllowBelowZero()) {
                    lowRebounds = lowRebounds + (((playerReboundsPerGame - playerReboundsStdDev)));
                } else {
                    if ((playerReboundsPerGame - playerReboundsStdDev) > 0) {
                        lowRebounds = lowRebounds + (((playerReboundsPerGame - playerReboundsStdDev)));
                    }
                }
            }
            if (!Double.isNaN(playerFoulsStdDev)) {
                highFouls = highFouls + (playerFoulsPerGame + playerFoulsStdDev);
                if (backTestIngestObject.isAllowBelowZero()) {
                    lowFouls = lowFouls + (((playerFoulsPerGame - playerFoulsStdDev)));
                } else {
                    if ((playerFoulsPerGame - playerFoulsStdDev) > 0) {
                        lowFouls = lowFouls + (((playerFoulsPerGame - playerFoulsStdDev)));
                    }
                }
            }
           //     totalTurnovers = totalTurnovers + playerTurnovers;
                totalPointsHome = totalPointsHome + playerPoints;
             //   totalBlocks = totalBlocks + playerBlocks;
            //    totalSteals = totalSteals + playerSteals;
           // }
        }


        //DoubleSummaryStatistics stats = opponentBlocks.stream().mapToDouble((x) -> x).summaryStatistics();
        //DoubleSummaryStatistics stealStats = opponentSteals.stream().mapToDouble((x) -> x).summaryStatistics();
        DoubleSummaryStatistics highstats = blockScoreModelHighs.stream().mapToDouble((x) -> x).summaryStatistics();
        DoubleSummaryStatistics highStealStats = stealScoreModelHighs.stream().mapToDouble((x) -> x).summaryStatistics();
        DoubleSummaryStatistics lowStealStats = stealScoreModelLows.stream().mapToDouble((x) -> x).summaryStatistics();
        DoubleSummaryStatistics highTurnoverStats = turnoverScoreModelHighs.stream().mapToDouble((x) -> x).summaryStatistics();
        DoubleSummaryStatistics lowTurnoverStats = turnoverScoreModelLows.stream().mapToDouble((x) -> x).summaryStatistics();
        DoubleSummaryStatistics highReboundStats = reboundScoreModelHighs.stream().mapToDouble((x) -> x).summaryStatistics();
        DoubleSummaryStatistics lowReboundStats = reboundScoreModelLows.stream().mapToDouble((x) -> x).summaryStatistics();
        DoubleSummaryStatistics highFoulsStats = foulsScoreModelHighs.stream().mapToDouble((x) -> x).summaryStatistics();
        DoubleSummaryStatistics lowFoulsStats = foulsScoreModelLows.stream().mapToDouble((x) -> x).summaryStatistics();
        DoubleSummaryStatistics lowstats = blockScoreModelLows.stream().mapToDouble((x) -> x).summaryStatistics();
        //System.out.println(team.getTeamName() + " played against BPG of " + stats.getAverage());
        if(moreInfo){
            System.out.println("#####################################");
            System.out.println("High forecast: " + highPoints);
            System.out.println("High blocks: " + highBlocks);

            System.out.println("Low forecast: " + lowPoints);
            System.out.println("Low blocks: " + lowBlocks);
            //System.out.println("Team BPG: " + (double)totalBlocks/(double)gameCount);
            System.out.println("Team PPG: " + (totalPointsHome/gameCount));
            //System.out.println("Team SPG: " + ((double)totalSteals/(double)gameCount));
            System.out.println(team.getTeamName() + " played against a block range of " + highstats.getAverage() + " || " + lowstats.getAverage());
            System.out.println(team.getTeamName() + " played against a steal range of " + highStealStats.getAverage() + " || " + lowStealStats.getAverage());

        }
        if(home){
            scoreModel.setHomeHighTurnovers(highTurnovers);
            scoreModel.setHomeLowTurnovers(lowTurnovers);
            scoreModel.setHomeHighSteals(highSteals);
            scoreModel.setHomeLowSteals(lowSteals);
            scoreModel.setHomeHighTurnovers(highTurnovers);
            scoreModel.setHomeLowTurnovers(lowTurnovers);
            scoreModel.setHomeHighRebounds(highRebounds);
            scoreModel.setHomeLowRebounds(lowRebounds);
            scoreModel.setHomeHighFouls(highFouls);
            scoreModel.setHomeLowFouls(lowFouls);
            scoreModel.setHomeStealScoringModelHigh(highStealStats.getAverage());
            scoreModel.setHomeStealScoringModelLow(lowStealStats.getAverage());
            scoreModel.setHomeTurnoverScoringModelHigh(highTurnoverStats.getAverage());
            scoreModel.setHomeTurnoverScoringModelLow(lowTurnoverStats.getAverage());
            scoreModel.setHomeOffensiveReboundScoringModelHigh(highReboundStats.getAverage());
            scoreModel.setHomeOffensiveReboundScoringModelLow(lowReboundStats.getAverage());
            scoreModel.setHomeFoulScoringModelHigh(highFoulsStats.getAverage());
            scoreModel.setHomeFoulScoringModelLow(lowFoulsStats.getAverage());
            scoreModel.setHomeBlockScoringModelHigh(highstats.getAverage());
            scoreModel.setHomeBlockScoringModelLow(lowstats.getAverage());
            scoreModel.setHomeHighBlocks(highBlocks);
            scoreModel.setHomeHighPoints(highPoints);
            scoreModel.setHomeLowPoints(lowPoints);
            scoreModel.setHomeLowBlocks(lowBlocks);
            scoreModel.setHomePointsPerGame(PointsPerGame);
            scoreModel.setHomeReboundPerGame(ReboundsPerGame);
            scoreModel.setHomeStealPerGame(StealsPerGame);
            scoreModel.setHomeTurnoverPerGame(TurnoversPerGame);
            scoreModel.setHomeFoulPerGame(FoulsPerGame);
            scoreModel.setHomeBlockPerGame(BlocksPerGame);
            scoreModel.setHomePointsPerGameRoc(pointsRoc);
            scoreModel.setHomeBlocksPerGameRoc(blocksRoc);
            scoreModel.setHomeReboundsPerGameRoc(reboundsRoc);
            scoreModel.setHomeStealsPerGameRoc(stealsRoc);
            scoreModel.setHomeTurnoversPerGameRoc(turnoversRoc);
            scoreModel.setHomeFoulsPerGameRoc(foulsRoc);
            scoreModel.setHomeFreeThrowAttemptsPerGameRoc(freethrowpctRoc);
            scoreModel.setHomeFreeThrowsPerGame(FreeThrowsPerGame);
            scoreModel.setHomePointsScoredVol(calculateTeamPointsScoredVol(backTestIngestObject,team,scoreModel,true,date));
            scoreModel.setHomePointsScoredVol(calculateTeamOverUnderRoc(backTestIngestObject,team,date));
            scoreModel.setHomeWinProbRoc(calculateTeamWinProbRoc(backTestIngestObject,team,date));
        }else{
            scoreModel.setAwayHighTurnovers(highTurnovers);
            scoreModel.setAwayLowTurnovers(lowTurnovers);
            scoreModel.setAwayHighSteals(highSteals);
            scoreModel.setAwayLowSteals(lowSteals);
            scoreModel.setAwayHighTurnovers(highTurnovers);
            scoreModel.setAwayLowTurnovers(lowTurnovers);
            scoreModel.setAwayHighRebounds(highRebounds);
            scoreModel.setAwayLowRebounds(lowRebounds);
            scoreModel.setAwayHighFouls(highFouls);
            scoreModel.setAwayLowFouls(lowFouls);
            scoreModel.setAwayStealScoringModelHigh(highStealStats.getAverage());
            scoreModel.setAwayStealScoringModelLow(lowStealStats.getAverage());
            scoreModel.setAwayTurnoverScoringModelHigh(highTurnoverStats.getAverage());
            scoreModel.setAwayTurnoverScoringModelLow(lowTurnoverStats.getAverage());
            scoreModel.setAwayOffensiveReboundScoringModelHigh(highReboundStats.getAverage());
            scoreModel.setAwayOffensiveReboundScoringModelHigh(lowReboundStats.getAverage());
            scoreModel.setAwayFoulScoringModelHigh(highFoulsStats.getAverage());
            scoreModel.setAwayFoulScoringModelLow(lowFoulsStats.getAverage());
            scoreModel.setAwayBlockScoringModelHigh(highstats.getAverage());
            scoreModel.setAwayBlockScoringModelLow(lowstats.getAverage());
            scoreModel.setAwayHighBlocks(highBlocks);
            scoreModel.setAwayHighPoints(highPoints);
            scoreModel.setAwayLowPoints(lowPoints);
            scoreModel.setAwayLowBlocks(lowBlocks);
            scoreModel.setAwayPointsPerGame(PointsPerGame);
            scoreModel.setAwayReboundPerGame(ReboundsPerGame);
            scoreModel.setAwayStealPerGame(StealsPerGame);
            scoreModel.setAwayTurnoverPerGame(TurnoversPerGame);
            scoreModel.setAwayFoulPerGame(FoulsPerGame);
            scoreModel.setAwayBlockPerGame(BlocksPerGame);
            scoreModel.setAwayPointsPerGameRoc(pointsRoc);
            scoreModel.setAwayBlocksPerGameRoc(blocksRoc);
            scoreModel.setAwayReboundsPerGameRoc(reboundsRoc);
            scoreModel.setAwayStealsPerGameRoc(stealsRoc);
            scoreModel.setAwayTurnoversPerGameRoc(turnoversRoc);
            scoreModel.setAwayFoulsPerGameRoc(foulsRoc);
            scoreModel.setAwayFreeThrowAttemptsPerGameRoc(freethrowpctRoc);
            scoreModel.setAwayFreeThrowsPerGame(FreeThrowsPerGame);
            scoreModel.setAwayPointsScoredVol(calculateTeamPointsScoredVol(backTestIngestObject,team,scoreModel,false,date));
            scoreModel.setAwayPointsScoredVol(calculateTeamOverUnderRoc(backTestIngestObject,team,date));
            scoreModel.setAwayWinProbRoc(calculateTeamWinProbRoc(backTestIngestObject,team,date));
        }
//        System.out.println("Upside: " + (highPoints - (totalPointsHome/gameCount))/(totalPointsHome/gameCount)+ "%") ;
//        System.out.println("Downside: " + (lowPoints - (totalPointsHome/gameCount))/(totalPointsHome/gameCount)+ "%");
    }
    public double calculateTeamOverUnderRoc(BackTestIngestObject backTestIngestObject,
                                               Team safeTeam,  Date date){
 List<Game> lastGames = teamStatFetcher.getTeamsLastNGames(backTestIngestObject.getGamesToTest(), safeTeam, date);





        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(2);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        //List<PlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(player.getPlayerGamePerformances());
        //playerGamePerformanceList.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        List<Double> fieldingDoubles = new ArrayList<>();
        //Collections.sort(playerGamePerformanceList, Comparator.comparing(MLBPlayerGamePerformance::getDate).reversed());
        int games = 0;
        for(int i = 0; i<lastGames.size(); i++){
            //for(MLBPlayerGamePerformance mlbPlayerGamePerformance : mlbPlayer.getMLBPlayerGamePerformances() ){
            if(games>=backTestIngestObject.getPlayerGameLookBack()){
                break;
            }
            //if(playerGamePerformanceList.get(i).getDate().before(beforeDate) && playerGamePerformanceList.get(i).getFieldingpercentage() > 0){
            if(lastGames.get(i).getGameOdds() != null) {
                fieldingDoubles.add((double) lastGames.get(i).getGameOdds().getOverUnder());
                games++;
            }
            //}
        }
        //DoubleSummaryStatistics doubleSummaryStatistics = fieldingDoubles.stream().mapToDouble((x) -> x).summaryStatistics();
        SimpleRegression simpleRegression = new SimpleRegression();
        for(int x = 0; x < fieldingDoubles.size(); x++){
            simpleRegression.addData(x,fieldingDoubles.get(x));
        }
        //System.out.println(playerGamePerformanceList.size() + " took " + (System.currentTimeMillis() - start)  + " ms");
        return simpleRegression.getSlope();

    }
    public double calculateTeamWinProbRoc(BackTestIngestObject backTestIngestObject,
                                            Team safeTeam,  Date date){
        List<Game> lastGames = teamStatFetcher.getTeamsLastNGames(backTestIngestObject.getGamesToTest(), safeTeam, date);





        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(2);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        //List<PlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(player.getPlayerGamePerformances());
        //playerGamePerformanceList.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        List<Double> fieldingDoubles = new ArrayList<>();
        //Collections.sort(playerGamePerformanceList, Comparator.comparing(MLBPlayerGamePerformance::getDate).reversed());
        int games = 0;
        for(int i = 0; i<lastGames.size(); i++){
            //for(MLBPlayerGamePerformance mlbPlayerGamePerformance : mlbPlayer.getMLBPlayerGamePerformances() ){
            if(games>=backTestIngestObject.getPlayerGameLookBack()){
                break;
            }
            //if(playerGamePerformanceList.get(i).getDate().before(beforeDate) && playerGamePerformanceList.get(i).getFieldingpercentage() > 0){
            if(lastGames.get(i).getGameOdds() != null) {
                if(lastGames.get(i).getAwayTeam().getTeamId() == safeTeam.getTeamId()){
                    fieldingDoubles.add(getProbability( lastGames.get(i).getGameOdds().getAwayTeamMoneyLine()));
                    games++;
                } else if(lastGames.get(i).getHomeTeam().getTeamId() == safeTeam.getTeamId()){
                    fieldingDoubles.add(getProbability( lastGames.get(i).getGameOdds().getHomeTeamMoneyLine()));
                    games++;
                }

            }
            //}
        }
        //DoubleSummaryStatistics doubleSummaryStatistics = fieldingDoubles.stream().mapToDouble((x) -> x).summaryStatistics();
        SimpleRegression simpleRegression = new SimpleRegression();
        for(int x = 0; x < fieldingDoubles.size(); x++){
            simpleRegression.addData(x,fieldingDoubles.get(x));
        }
        //System.out.println(playerGamePerformanceList.size() + " took " + (System.currentTimeMillis() - start)  + " ms");
        return simpleRegression.getSlope();

    }


    public double calculateTeamPointsScoredVol(BackTestIngestObject backTestIngestObject,
                                             Team safeTeam, ScoreModel scoreModel, boolean home, Date date){
//        if(safeTeam == null){
//            if(home) {
//                team.setTeamId(unsafeGame.ge());
//                team.setTeamName(unsafeGame.getHomeTeamName());
//                team.setTeamAbbreviation(unsafeGame.getHomeTeamTricode());
//            }else{
//                team.setTeamId(unsafeGame.getAwayTeamMlbId());
//                team.setTeamName(unsafeGame.getAwayTeamName());
//                team.setTeamAbbreviation(unsafeGame.getAwayTeamTricode());
//            }
//        }else{
//            team = (MLBTeam) safeTeam.clone();
//        }

        List<Game> lastGames = teamStatFetcher.getTeamsLastNGames(backTestIngestObject.getGamesToTest(), safeTeam, date);





        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(2);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        //List<PlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(player.getPlayerGamePerformances());
        //playerGamePerformanceList.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        List<Double> fieldingDoubles = new ArrayList<>();
        //Collections.sort(playerGamePerformanceList, Comparator.comparing(MLBPlayerGamePerformance::getDate).reversed());
        int games = 0;
        for(int i = 0; i<lastGames.size(); i++){
            //for(MLBPlayerGamePerformance mlbPlayerGamePerformance : mlbPlayer.getMLBPlayerGamePerformances() ){
            if(games>=backTestIngestObject.getPlayerGameLookBack()){
                break;
            }
            //if(playerGamePerformanceList.get(i).getDate().before(beforeDate) && playerGamePerformanceList.get(i).getFieldingpercentage() > 0){
            if(lastGames.get(i).getGameOdds() != null) {
                fieldingDoubles.add((double) lastGames.get(i).getGameOdds().getOverUnder());
                games++;
            }
            //}
        }
        //DoubleSummaryStatistics doubleSummaryStatistics = fieldingDoubles.stream().mapToDouble((x) -> x).summaryStatistics();
        SimpleRegression simpleRegression = new SimpleRegression();
        for(int x = 0; x < fieldingDoubles.size(); x++){
            simpleRegression.addData(x,fieldingDoubles.get(x));
        }
        //System.out.println(playerGamePerformanceList.size() + " took " + (System.currentTimeMillis() - start)  + " ms");
        return simpleRegression.getSlope();


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
}
