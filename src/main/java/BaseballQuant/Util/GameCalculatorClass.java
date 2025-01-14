package BaseballQuant.Util;

import BaseballQuant.Model.*;
import BaseballQuant.Model.CacheObjects.OpponentCacheObject;
import BaseballQuant.Model.CacheObjects.PlayerPerformanceCacheObject;
import com.landawn.abacus.util.FloatSummaryStatistics;
import com.landawn.abacus.util.stream.Stream;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.text.SimpleDateFormat;
import java.util.*;

public class GameCalculatorClass {
    private TeamStatFetcher teamStatFetcher;
    private GameFinder gameFinder;
    private boolean moreInfo;
    private PlayerStatFetcher playerStatFetcher;
    private HashMap<Integer,List<PlayerPerformanceCacheObject>> playerPerformanceCacheObjects;
    private HashMap<Integer,List<OpponentCacheObject>> opponentPitchingCacheObjects;
    private HashMap<Integer,List<OpponentCacheObject>> opponentFieldingCacheObjects;
    boolean overUnder = false;

    public TeamStatFetcher getTeamStatFetcher() {
        return teamStatFetcher;
    }

    public void setTeamStatFetcher(TeamStatFetcher teamStatFetcher) {
        this.teamStatFetcher = teamStatFetcher;
    }

    public GameFinder getGameFinder() {
        return gameFinder;
    }

    public void setGameFinder(GameFinder gameFinder) {
        this.gameFinder = gameFinder;
    }

    public boolean isMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(boolean moreInfo) {
        this.moreInfo = moreInfo;
    }

    public PlayerStatFetcher getPlayerStatFetcher() {
        return playerStatFetcher;
    }

    public void setPlayerStatFetcher(PlayerStatFetcher playerStatFetcher) {
        this.playerStatFetcher = playerStatFetcher;
    }

    public GameCalculatorClass(){
        playerPerformanceCacheObjects = new HashMap<>();
        opponentPitchingCacheObjects = new HashMap<>();
        opponentFieldingCacheObjects = new HashMap<>();
    }

    public void setOverUnder(boolean overUnder) {
        this.overUnder = overUnder;
    }

    public void tallyTeamBattingLastNGames(BackTestIngestObject backTestIngestObject, Date date, PlayerStatFilter playerStatFilter,
                                           MLBTeam safeTeam, ScoreModel scoreModel, boolean home, MLBGame unsafeGame, boolean newGames) {
        MLBGame safeGame = (MLBGame) unsafeGame.clone();
        MLBTeam team = (MLBTeam) safeTeam.clone();
        double totalPointsHome = 0;
        float highPoints = 0;
        float lowPoints = 0;
        float highRunsGivenUp = 0;
        float lowRunsGivenUp = 0;
        double highBlocks = 0;
        double lowBlocks = 0;
        double highStolenBases = 0;
        double lowStolenBases = 0;
       // boolean modelOpposingPitching = backTestIngestObject.isModelOpposingPitching();
        int gameCount = backTestIngestObject.getGameCount();
        int pitcherCount = backTestIngestObject.getPitcherGameLookback();
        boolean insufficientPitcherData = false;
//        double RGPGHigh = 0.0;
//        double RGPGLow = 0.0;
        double opponentsRunsGivenUpHigh = 0.0;
        double opponentsRunsGivenUpLow = 0.0;
        double homeAdvantage = 0.0;
        float runsGivenUpPerGameRoc = 0;
        float runsScoredPerGameRoc = 0;
        float fieldingRocTotal = 0;
        float stolenRocTotal = 0;
        float walksRocTotal = 0;
        float runsScoredPerGame = 0;
        float runsGivenUpPerGameTot = 0;
        if(home) {
             homeAdvantage = teamStatFetcher.getHomeAdvantage(gameCount, team, safeGame);
        }
        //double RGPG = teamStatFetcher.getTeamRunsGivenUpPerGame(pitcherCount,team,safeGame);
        //float RGPGStdDev = teamStatFetcher.getTeamRunsGivenUpPerGameStdDev(pitcherCount, team, safeGame);
//        List<Double> opponentsRunsGivenPerGame = teamStatFetcher.getOpposingTeamRunsGivenUpPerGame(pitcherCount,team,safeGame);
//        DoubleSummaryStatistics opponentRunsGivenPerGameStats = opponentsRunsGivenPerGame.stream().mapToDouble((x) -> x).summaryStatistics();
//        double opponentsRunsGivenPerGameStdDev = teamStatFetcher.genericStdDev(opponentsRunsGivenPerGame);
//        if(backTestIngestObject.isDoubleSquareRoot()){
//            RGPGStdDev = (float) Math.sqrt(RGPGStdDev);
//            opponentsRunsGivenPerGameStdDev = Math.sqrt(opponentsRunsGivenPerGameStdDev);
//        }
//        double RGPGHigh = RGPG + RGPGStdDev;
//        double RGPGLow = 0;
//        if(backTestIngestObject.isAllowLowEndBelowZero()){
//            RGPGLow = RGPG - RGPGStdDev;
//        }else{
//            if(RGPG - RGPGStdDev > 0){
//                RGPGLow = RGPG - RGPGStdDev;
//            }
//        }
//        double opponentsRunsGivenUpHigh = opponentRunsGivenPerGameStats.getAverage() + opponentsRunsGivenPerGameStdDev;
//        double opponentsRunsGivenUpLow = 0;
//        if(backTestIngestObject.isAllowLowEndBelowZero()){
//            opponentsRunsGivenUpLow = opponentRunsGivenPerGameStats.getAverage() - opponentsRunsGivenPerGameStdDev;
//        }else{
//            if(opponentRunsGivenPerGameStats.getAverage() - opponentsRunsGivenPerGameStdDev > 0){
//                opponentsRunsGivenUpLow = opponentRunsGivenPerGameStats.getAverage() - opponentsRunsGivenPerGameStdDev;
//            }
//        }
        List<MLBGame> cleanGames =  teamStatFetcher.getTeamsLastNGamesClean(backTestIngestObject.getGameCount(), team, safeGame);
        //double total = 0;
        HashMap<Integer, MLBPlayer> playerHashMap = new HashMap<>();
        HashMap<Integer, MLBPitcher> pitcherHashMap = new HashMap<>();
        for(MLBGame mlbGame : cleanGames){
            if(mlbGame.getAwayTeamName().equals(team.getTeamName())){
                for(MLBPlayer player : mlbGame.getAwayMLBTeam().getFieldingPlayers()){
                    playerHashMap.putIfAbsent(player.getPlayerID(), player);
                }
                for(MLBPitcher pitcher : mlbGame.getAwayMLBTeam().getPitchingPlayers()){
                    pitcherHashMap.putIfAbsent(pitcher.getPlayerID(), pitcher);
                }
            }else{
                for(MLBPlayer player : mlbGame.getHomeMLBTeam().getFieldingPlayers()){
                    playerHashMap.putIfAbsent(player.getPlayerID(), player);
                }
                for(MLBPitcher pitcher : mlbGame.getHomeMLBTeam().getPitchingPlayers()){
                    pitcherHashMap.putIfAbsent(pitcher.getPlayerID(), pitcher);
                }
            }
        }
        double averagePlayers = (double) (playerHashMap.size() + pitcherHashMap.size()) /cleanGames.size();

        List<Float> opponentPitchingModelHighs = new ArrayList<>();
        List<Float> opponentPitchingModelLows = new ArrayList<>();
        List<Double> opponentFieldingModelHighs = new ArrayList<>();
        List<Double> opponentFieldingModelLows = new ArrayList<>();
        List<Double> lowFielding = new ArrayList<>();
        List<Double> highFielding = new ArrayList<>();
        List<Double> runsGivenUp = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy/MM/dd");


        //System.out.println("playersize: " + team.getFieldingPlayers().size());
        //MLBPlayer[] players = (MLBPlayer[]) team.getFieldingPlayers().toArray();
        ArrayList<MLBPlayer> playerList = new ArrayList<>(team.getFieldingPlayers());
        playerList.sort(Comparator.comparing(MLBPlayer::getPlayerID));
        int listSize = team.getFieldingPlayers().size();

        for(int i = 0; i<listSize; i++) {

            MLBPlayer playerClone = (MLBPlayer) playerList.get(i).clone();
            playerStatFilter.trimPlayerGameHistory(gameCount, playerClone, date);
            //float playerPoints = playerStatFilter.getPlayerPointsLastNGames(gameCount, playerClone, date) + playerStatFilter.getPlayerPointsRateOfChange(gameCount,playerClone,date);;

//            if (modelOpposingPitching) {
//                List<Double> pitchingDoubles = playerStatFetcher.modelOpponentPitching(gameCount, backTestIngestObject.getPitcherGameLookback(), playerClone, safeGame, date, gameFinder, playerStatFilter, backTestIngestObject.isDoubleSquareRoot(), backTestIngestObject.isAllowLowEndBelowZero());
//                //List<Double> rangeDoubles = playerStatFetcher.modelOpposingTeamPitching(gameCount,playerClone,date,gameFinder,playerStatFilter,team,backTestIngestObject);
//
//                opponentPitchingModelHighs.add(pitchingDoubles.get(0).floatValue());
//                opponentPitchingModelLows.add(pitchingDoubles.get(1).floatValue());
//            }
            //runsGivenUp.add( playerStatFetcher.modelOpponentTeamPitching(gameCount, playerClone,date,gameFinder));

//            List<Double> fieldingDoubles = playerStatFetcher.modelOpponentFielding(gameCount, playerClone,safeGame, date, gameFinder, playerStatFilter,
//                    backTestIngestObject.isDoubleSquareRoot(), backTestIngestObject.isAllowLowEndBelowZero());
//            if(fieldingDoubles != null) {
//                opponentFieldingModelHighs.add(fieldingDoubles.get(0));
//                opponentFieldingModelLows.add(fieldingDoubles.get(1));
//            }

            double fieldingRoc = playerStatFilter.getFieldingRateOfChange(gameCount,playerClone,date);
            fieldingRocTotal += (float) fieldingRoc;
            double playerFielding = playerStatFilter.getFieldingLastNGames(gameCount,playerClone,date);
            double playerFieldingStdDev = playerStatFilter.getFieldingStdDevLastNGames(gameCount,playerClone,date,playerFielding);

            float playerPointsPerGame = playerStatFilter.getPlayerPointsPerGameLastNGames(gameCount, playerClone, date);
            if(!Double.isNaN(playerPointsPerGame)){
                runsScoredPerGame += playerPointsPerGame;
            }
            float playerPointsStdDev =  playerStatFilter.getPlayerPointsLastNGamesStdDev(gameCount, playerClone, date, playerPointsPerGame);
            runsScoredPerGameRoc += (float) playerStatFilter.getPlayerPointsRateOfChange(gameCount, playerClone, date);



            double stolenRoc = playerStatFilter.getPlayerStolenBasesRateOfChange(gameCount, playerClone, date);
            float w = (float) playerStatFilter.getPlayerWalksRateOfChange(gameCount,playerClone,date);
            if(!Double.isNaN(w)) {
                walksRocTotal += w;
            }
            stolenRocTotal += (float) stolenRoc;
            double stolenBasesPerLastNGames = playerStatFilter.getPlayerStolenBasesPerGameLastNGames(gameCount, playerClone, date);
            double stolenBasesPerLastNGamesStdDev = playerStatFilter.getPlayerStolenBasesLastNGamesStdDev(gameCount, playerClone, date,stolenBasesPerLastNGames);
//
            if(backTestIngestObject.isDoubleSquareRoot()){
             //   playerPointsStdDev = (float) Math.sqrt(playerPointsStdDev);
                stolenBasesPerLastNGamesStdDev = Math.sqrt(stolenBasesPerLastNGamesStdDev);
                playerFieldingStdDev = Math.sqrt(playerFieldingStdDev);
            }

//            if(moreInfo) {
//                String inputDates = "[";
//
//                for (MLBPlayerGamePerformance playerGamePerformance : playerClone.getPlayerGamePerformances()) {
//                    inputDates = inputDates + simpleDateFormat.format(playerGamePerformance.getDate()) + ",";
//                }
//                inputDates = inputDates + "]";
//              //  System.out.println(playerClone.getFullName() + " " + " High: " + (playerPointsPerGame + playerPointsStdDev) + " || " + "Low: " + (playerPointsPerGame - playerPointsStdDev) + "(" + playerPointsStdDev + ") " + inputDates);
//                //System.out.println("PLAYER BPG: " + playerBlocksPerGame);
//            }
//           // if(!Double.isNaN(playerPointsStdDev)) {
//           //     highPoints = highPoints + (playerPointsPerGame + playerPointsStdDev);
//                //if ((playerPointsPerGame - playerPointsStdDev) > 0) {
//           //         lowPoints = lowPoints + ((playerPointsPerGame - playerPointsStdDev));
//                //}
//           // }
//            totalPointsHome = totalPointsHome + playerPoints;
//
//            if(!Double.isNaN(stolenBasesPerLastNGamesStdDev)){
//                highStolenBases = highStolenBases + (stolenBasesPerLastNGames + stolenBasesPerLastNGamesStdDev);
//                //if(stoFlenBasesPerLastNGames - stolenBasesPerLastNGamesStdDev > 0){
//                lowStolenBases = lowStolenBases + (stolenBasesPerLastNGames - stolenBasesPerLastNGamesStdDev );
//                //}
//            }
//
//
//
//            if(!Double.isNaN(playerFieldingStdDev)) {
//                //highFielding = highFielding + (playerFielding + playerFieldingStdDev);
//                highFielding.add((playerFielding + playerFieldingStdDev));
//                if ((playerFielding - playerFieldingStdDev) > 0) {
//                    //lowFielding = lowFielding + ((playerFielding - playerFieldingStdDev));
//                    lowFielding.add((playerFielding - playerFieldingStdDev));
//                }
//            }
        }
        try {
            MLBGame priorGame = (MLBGame) gameFinder.getTeamsPreviousGameFromDB(safeGame, team.getMlbId()).clone();
//            if (!newGames) {
//                attemptToSwitchStartingPitcher(safeGame, priorGame, home);
//            }
        }catch (Exception e){
            e.printStackTrace();
        }
        MLBPitcher startingPitcher;
        if(home){
            startingPitcher = safeGame.getHomeStartingPitcher();
        }else{
            startingPitcher = safeGame.getAwayStartingPitcher();
        }

        if(startingPitcher != null){
            MLBPitcher pitcherClone = (MLBPitcher) startingPitcher.clone();
            playerStatFilter.trimPlayerGameHistory(backTestIngestObject.getPitcherGameLookback(), pitcherClone, date);
            if(pitcherClone.getMLBPitcherGamePerformances().size() < backTestIngestObject.getPitcherGameLookback()/2){
                insufficientPitcherData = true;
            }
        }else{
            insufficientPitcherData = true;
        }

        if(startingPitcher == null){
            runsGivenUpPerGameRoc = teamStatFetcher.runsGivenUpLastNGamesRateOfChange(backTestIngestObject.getPitcherGameLookback(), team, safeGame);
            float runsGivenUpPerGame = (float) (teamStatFetcher.runsGivenUpLastNGames(backTestIngestObject.getPitcherGameLookback(), team, safeGame) / (double) backTestIngestObject.getPitcherGameLookback());
            float runsGivenUpStdDev;
            runsGivenUpPerGameTot += runsGivenUpPerGame;
            if (backTestIngestObject.isDoubleSquareRoot()) {
                runsGivenUpStdDev = (float)Math.sqrt(teamStatFetcher.getRunsGivenUpLastNGamesStdDev(backTestIngestObject.getPitcherGameLookback(), team, safeGame));
            } else {
                runsGivenUpStdDev = teamStatFetcher.getRunsGivenUpLastNGamesStdDev(backTestIngestObject.getPitcherGameLookback(), team, safeGame);
            }
            if (!Double.isNaN(runsGivenUpStdDev)) {
                highRunsGivenUp = highRunsGivenUp + (runsGivenUpPerGame + runsGivenUpStdDev);
                if(backTestIngestObject.isAllowLowEndBelowZero()) {
                    lowRunsGivenUp = lowRunsGivenUp + ((runsGivenUpPerGame - runsGivenUpStdDev));
                }else{
                    if ((runsGivenUpPerGame - runsGivenUpStdDev) > 0) {
                        lowRunsGivenUp = lowRunsGivenUp + ((runsGivenUpPerGame - runsGivenUpStdDev));
                    }
                }
            }
        }else {
            runsGivenUpPerGameRoc = teamStatFetcher.runsGivenUpLastNGamesRateOfChange(backTestIngestObject.getPitcherGameLookback(), team, safeGame);
            MLBPitcher pitcherClone = (MLBPitcher) startingPitcher.clone();
            playerStatFilter.trimPlayerGameHistory(backTestIngestObject.getPitcherGameLookback(), pitcherClone, date);
            if (pitcherClone.getMLBPitcherGamePerformances().size() == 0) {
                insufficientPitcherData = true;
                float runsGivenUpPerGame = (float)(teamStatFetcher.runsGivenUpLastNGames(backTestIngestObject.getPitcherGameLookback(), team, safeGame) / (double) backTestIngestObject.getPitcherGameLookback());
                runsGivenUpPerGameTot += runsGivenUpPerGame;
                float runsGivenUpStdDev;
                if (backTestIngestObject.isDoubleSquareRoot()) {
                    runsGivenUpStdDev =(float) Math.sqrt(teamStatFetcher.getRunsGivenUpLastNGamesStdDev(backTestIngestObject.getPitcherGameLookback(), team, safeGame));
                } else {
                    runsGivenUpStdDev = teamStatFetcher.getRunsGivenUpLastNGamesStdDev(backTestIngestObject.getPitcherGameLookback(), team, safeGame);
                }
                if (!Double.isNaN(runsGivenUpStdDev)) {
                    highRunsGivenUp = highRunsGivenUp + (runsGivenUpPerGame + runsGivenUpStdDev);
                    if(backTestIngestObject.isAllowLowEndBelowZero()) {
                        lowRunsGivenUp = lowRunsGivenUp + ((runsGivenUpPerGame - runsGivenUpStdDev));
                    }else{
                        if ((runsGivenUpPerGame - runsGivenUpStdDev) > 0) {
                            lowRunsGivenUp = lowRunsGivenUp + ((runsGivenUpPerGame - runsGivenUpStdDev));
                        }
                    }
                }
            } else {
                float startingPitcherRunsGivenUpPerLastNGames =  (float)playerStatFilter.getPitcherGivenRunsPerGameLastNGames(backTestIngestObject.getPitcherGameLookback(), pitcherClone, date) ;
                runsGivenUpPerGameTot += startingPitcherRunsGivenUpPerLastNGames;
                float startingPitcherRunsGivenUpStdDev = playerStatFilter.getPitcherGivenRunsLastNGamesStdDev(backTestIngestObject.getPitcherGameLookback(), pitcherClone, date,startingPitcherRunsGivenUpPerLastNGames);
                //TODO:: NEW BULLPEN IMPLEMENTATION
                if (backTestIngestObject.isDoubleSquareRoot()) {
                    startingPitcherRunsGivenUpStdDev = (float)Math.sqrt(startingPitcherRunsGivenUpStdDev);
                }
                float startingPitcherRunsGivenUpHigh = startingPitcherRunsGivenUpPerLastNGames + startingPitcherRunsGivenUpStdDev;
                float startingPitcherRunsGivenUpLow = 0;
                if(backTestIngestObject.isAllowLowEndBelowZero()) {
                    startingPitcherRunsGivenUpLow = startingPitcherRunsGivenUpPerLastNGames - startingPitcherRunsGivenUpStdDev;
                }else{
                    if ((startingPitcherRunsGivenUpPerLastNGames - startingPitcherRunsGivenUpStdDev) > 0) {
                        startingPitcherRunsGivenUpLow = startingPitcherRunsGivenUpPerLastNGames - startingPitcherRunsGivenUpStdDev;
                    }
                }
                MLBGame mlbGame1 = (MLBGame) safeGame.clone();
                float bullpenRunsGivenUpPerLastNGames = ((float)teamStatFetcher.getBullpenRunsGivenUpLastNGames((int) backTestIngestObject.getGameCount(), team, mlbGame1) / backTestIngestObject.getGameCount());
                runsGivenUpPerGameTot += bullpenRunsGivenUpPerLastNGames;
                float bullpenRunsGivenUpPerLastNGamesStdDev = teamStatFetcher.getBullpenRunsGivenUpLastNGamesStdDev((int) backTestIngestObject.getGameCount(), team, mlbGame1);
                if (backTestIngestObject.isDoubleSquareRoot()) {
                    bullpenRunsGivenUpPerLastNGamesStdDev = (float)Math.sqrt(bullpenRunsGivenUpPerLastNGamesStdDev);
                }
                float bullpenHighRunsGivenUp = bullpenRunsGivenUpPerLastNGames + bullpenRunsGivenUpPerLastNGamesStdDev;
                float bullpenLowRunsGivenUp = 0;
                if(backTestIngestObject.isAllowLowEndBelowZero()) {
                    bullpenLowRunsGivenUp = bullpenRunsGivenUpPerLastNGames - bullpenRunsGivenUpPerLastNGamesStdDev;
                }else{
                    if ((bullpenRunsGivenUpPerLastNGames - bullpenRunsGivenUpPerLastNGamesStdDev) > 0) {
                        bullpenLowRunsGivenUp = bullpenRunsGivenUpPerLastNGames - bullpenRunsGivenUpPerLastNGamesStdDev;
                    }
                }
                //System.out.println(team.getTeamName() + " Bullpen Pitching Range of " + bullpenHighRunsGivenUp + " || " + bullpenLowRunsGivenUp);
                if (!Double.isNaN(startingPitcherRunsGivenUpHigh) && !Double.isNaN(bullpenHighRunsGivenUp)) {
                    highRunsGivenUp = startingPitcherRunsGivenUpHigh + bullpenHighRunsGivenUp;
                    lowRunsGivenUp = startingPitcherRunsGivenUpLow + bullpenLowRunsGivenUp;
                }
            }

        }


        FloatSummaryStatistics highstats = Stream.of(opponentPitchingModelHighs).mapToFloat((x) -> x).summarize();
        FloatSummaryStatistics lowstats = Stream.of(opponentPitchingModelLows).mapToFloat((x) -> x).summarize();
        DoubleSummaryStatistics highFieldingModelStats = opponentFieldingModelHighs.stream().mapToDouble((x) -> x).summaryStatistics();
        DoubleSummaryStatistics lowFieldingModelStats = opponentFieldingModelLows.stream().mapToDouble((x) -> x).summaryStatistics();
       /// DoubleSummaryStatistics RunsGivenUpStats = runsGivenUp.stream().mapToDouble((x) -> x).summaryStatistics();
        DoubleSummaryStatistics highFieldingStats = highFielding.stream().mapToDouble((x) -> x).summaryStatistics();
        DoubleSummaryStatistics lowFieldingStats = lowFielding.stream().mapToDouble((x) -> x).summaryStatistics();
        //DoubleSummaryStatistics highRunStats = highRuns.stream().mapToDouble((x) -> x).summaryStatistics();
        //DoubleSummaryStatistics lowRunStats = lowRuns.stream().mapToDouble((x) -> x).summaryStatistics();
        //System.out.println(team.getTeamName() + " Calculated Pitching Range of " + highRunsGivenUp + " || " + lowRunsGivenUp);
        //System.out.println(team.getTeamName() + " Gave up  " + runsGivenUpPerGame + " per game.");

        //System.out.println(team.getTeamName() + " Starter Pitching Range of " + startingPitcherRunsGivenUpHigh + " || " + startingPitcherRunsGivenUpLow);
        if(moreInfo){
            System.out.println("#####################################");
            if(home){
                if(safeGame.getHomeStartingPitcher() != null) {
                    System.out.println("Starting Pitcher: " + safeGame.getHomeStartingPitcher().getFullName());
                }
                System.out.println("Pitchers: " + safeGame.getHomeMLBTeam().getPitchingPlayers());
            }else{
                if(safeGame.getAwayStartingPitcher() != null) {
                    System.out.println("Starting Pitcher: " + safeGame.getAwayStartingPitcher().getFullName());
                }
                System.out.println("Pitchers: " + safeGame.getAwayMLBTeam().getPitchingPlayers());

            }
            System.out.println("High forecast: " + highPoints);
            System.out.println("High blocks: " + highBlocks);
            System.out.println("Low forecast: " + lowPoints);
            System.out.println("Low blocks: " + lowBlocks);
            System.out.println("Team PPG: " + (totalPointsHome/gameCount));
            System.out.println(team.getTeamName() + " played against pitching range of " + highstats.getAverage() + " || " + lowstats.getAverage());
            System.out.println(team.getTeamName() + " played against a fielding range of " + highFieldingModelStats.getAverage() + " || " + lowFieldingModelStats.getAverage());
            System.out.println(team.getTeamName() + " gave up " + highRunsGivenUp + " || " + lowRunsGivenUp);
        }

        if(home){
//            if(Double.isNaN(runsGivenUpPerGameRoc) || runsGivenUpPerGameRoc == 0.0){
//                System.out.println(runsGivenUpPerGameRoc);
//                runsGivenUpPerGameRoc = (float) calculateTeamRunsGivenUpRoc(backTestIngestObject,team,scoreModel,home,safeGame);
//                System.out.println(runsGivenUpPerGameRoc);
//            }
            scoreModel.setHomeAveragePriorPlayers(averagePlayers);
            scoreModel.setHomeRunsScoredVol(calculateTeamRunsScoredVol(backTestIngestObject,team,scoreModel,home,safeGame));
            scoreModel.setHomeRunsGivenUpVol(calculateTeamRunsGivenUpVol(backTestIngestObject,team,scoreModel,home,safeGame));
            scoreModel.setHomeRunsGivenUpHigh(highRunsGivenUp);
            scoreModel.setHomeRunsGivenUpLow(lowRunsGivenUp);
            scoreModel.setHomeRunsScoredPerGame(runsScoredPerGame);
            scoreModel.setHomeRunsGivenUpPerGame(calculateTeamRunsGivenUpPerGame(backTestIngestObject,team,scoreModel,true,safeGame));
            scoreModel.setHomeRunsGivenUpPerGame(runsGivenUpPerGameTot);

            scoreModel.setHomePitchingModelHigh(highstats.getAverage().floatValue());
            scoreModel.setHomeHighPoints(highPoints);
            scoreModel.setHomeLowPoints(lowPoints);
            scoreModel.setHomeHighStolenBases(highStolenBases);
            scoreModel.setHomeLowStolenBases(lowStolenBases);
            scoreModel.setHomePitchingModelLow(lowstats.getAverage().floatValue());
            scoreModel.setHomeFieldingModelHigh(highFieldingModelStats.getAverage());
            scoreModel.setHomeFieldingMoelLow(lowFieldingModelStats.getAverage());
            scoreModel.setHomeFieldingHigh(highFieldingStats.getAverage());
            scoreModel.setHomeFieldingLow(lowFieldingStats.getAverage());
            scoreModel.setHomeModelRunsGivenUpPerGameHigh(opponentsRunsGivenUpHigh);
            scoreModel.setHomeModelRunsGivenUpPerGameLow(opponentsRunsGivenUpLow);
//            scoreModel.setHomeRGPGHigh(RGPGHigh);
//            scoreModel.setHomeRGPGLow(RGPGLow);
            scoreModel.setHomeAdvantage(homeAdvantage);
           // scoreModel.setHomeRunsGivenUpPerGameRoc(calculateTeamRunsGivenUpRoc(backTestIngestObject,team,scoreModel,true,safeGame));

            scoreModel.setHomeRunsGivenUpPerGameRoc(runsGivenUpPerGameRoc);
            scoreModel.setHomeRunsScoredPerGameRoc(runsScoredPerGame);
            scoreModel.setHomeFieldingRoc(fieldingRocTotal);
            scoreModel.setHomeStolenBasesRoc(stolenRocTotal);
            scoreModel.setHomeWalkRoc(walksRocTotal);
            scoreModel.setHomeSufficientPitcherGamers(!insufficientPitcherData);
            if(!scoreModel.isInsufficientPitcherData()) {
                scoreModel.setInsufficientPitcherData(insufficientPitcherData);
            }
        }else{
//            if(Double.isNaN(runsGivenUpPerGameRoc) || runsGivenUpPerGameRoc == 0.0){
//                //System.out.println("");
//                System.out.println(runsGivenUpPerGameRoc);
//                runsGivenUpPerGameRoc = (float) calculateTeamRunsGivenUpRoc(backTestIngestObject,team,scoreModel,home,safeGame);
//                System.out.println(runsGivenUpPerGameRoc);
//            }
            scoreModel.setAwayAveragePriorPlayers(averagePlayers);
            scoreModel.setAwayRunsScoredVol(calculateTeamRunsScoredVol(backTestIngestObject,team,scoreModel,home,safeGame));
            scoreModel.setAwayRunsGivenUpVol(calculateTeamRunsGivenUpVol(backTestIngestObject,team,scoreModel,home,safeGame));
            scoreModel.setAwayRunsGivenUpHigh(highRunsGivenUp);
            scoreModel.setAwayRunsGivenUpLow(lowRunsGivenUp);
            scoreModel.setAwayRunsScoredPerGame(runsScoredPerGame);
           // scoreModel.setAwayRunsGivenUpPerGame(calculateTeamRunsGivenUpPerGame(backTestIngestObject,team,scoreModel,false,safeGame));
            scoreModel.setAwayRunsGivenUpPerGame(runsGivenUpPerGameTot);
            scoreModel.setAwayPitchingModelHigh(highstats.getAverage().floatValue());
            scoreModel.setAwayHighPoints(highPoints);
            scoreModel.setAwayLowPoints(lowPoints);
            scoreModel.setAwayHighStolenBases(highStolenBases);
            scoreModel.setAwayLowStolenBases(lowStolenBases);
            scoreModel.setAwayPitchingModelLow(lowstats.getAverage().floatValue());
            scoreModel.setAwayFieldingModelHigh(highFieldingModelStats.getAverage());
            scoreModel.setAwayFieldingModelLow(lowFieldingModelStats.getAverage());
            scoreModel.setAwayFieldingHigh(highFieldingStats.getAverage());
            scoreModel.setAwayFieldingLow(lowFieldingStats.getAverage());
            scoreModel.setAwayModelRunsGivenUpPerGameHigh(opponentsRunsGivenUpHigh);
            scoreModel.setAwayModelRunsGivenUpPerGameLow(opponentsRunsGivenUpLow);
//            scoreModel.setAwayRGPGHigh(RGPGHigh);
//            scoreModel.setAwayRGPGLow(RGPGLow);
            scoreModel.setAwayRunsGivenUpPerGameRoc(runsGivenUpPerGameRoc);

            scoreModel.setAwayRunsScoredPerGameRoc(runsScoredPerGameRoc);

            scoreModel.setAwayFieldingRoc(fieldingRocTotal);
            scoreModel.setAwayStolenBasesRoc(stolenRocTotal);
            scoreModel.setAwayWalkRoc(walksRocTotal);
            scoreModel.setAwaySufficientPitcherGamers(!insufficientPitcherData);
            if(!scoreModel.isInsufficientPitcherData()) {
                scoreModel.setInsufficientPitcherData(insufficientPitcherData);
            }
        }
    }

    public void attemptToSwitchStartingPitcher(MLBGame game, MLBGame priorGame, boolean home){
        MLBTeam selectedTeam;
        if(home){
            selectedTeam = game.getHomeMLBTeam();

            MLBPitcher startingPitcher = game.getHomeStartingPitcher();
            if(startingPitcher != null){
                selectedTeam.getPitchingPlayers().add(startingPitcher);
            }
            MLBPitcher priorStartingPitcher = priorGame.getHomeStartingPitcher();
            if(priorStartingPitcher != null){
                //System.out.println("Prior starting pitcher: " + priorStartingPitcher.getFullName());
                Set<MLBPitcher> mlbPitchers = game.getHomeMLBTeam().getPitchingPlayers();
                Set<MLBPitcher> cleanPitchers = new HashSet<>(mlbPitchers);
                Iterator<MLBPitcher> iterator = cleanPitchers.iterator();
                while(iterator.hasNext()){
                    MLBPitcher pitcher = iterator.next();
                    if(pitcher.getPlayerID() == priorStartingPitcher.getPlayerID()){
                        iterator.remove();
                    }
                }
                selectedTeam.setPitchingPlayers(cleanPitchers);
            }
            game.setHomeMLBTeam(selectedTeam);
        }else{
            selectedTeam = game.getAwayMLBTeam();
            MLBPitcher startingPitcher = game.getAwayStartingPitcher();
            if(startingPitcher != null){
                selectedTeam.getPitchingPlayers().add(startingPitcher);
            }
            MLBPitcher priorStartingPitcher = priorGame.getHomeStartingPitcher();
            if(priorStartingPitcher != null){
                //System.out.println("Prior starting pitcher: " + priorStartingPitcher.getFullName());
                Set<MLBPitcher> mlbPitchers = game.getAwayMLBTeam().getPitchingPlayers();
                Set<MLBPitcher> cleanPitchers = new HashSet<>(mlbPitchers);

                Iterator<MLBPitcher> iterator = cleanPitchers.iterator();
                while(iterator.hasNext()){
                    MLBPitcher pitcher = iterator.next();
                    if(pitcher.getPlayerID() == priorStartingPitcher.getPlayerID()){
                        iterator.remove();
                    }
                }
                //mlbPitchers.removeIf(mlbPitcher -> mlbPitcher.getPlayerID() == priorStartingPitcher.getPlayerID());
                selectedTeam.setPitchingPlayers(cleanPitchers);
            }
            game.setAwayMLBTeam(selectedTeam);
        }
        //System.out.println("new pitcher list: " + selectedTeam.getPitchingPlayers());
    }
    public void calculateTestCorrelation(BackTestIngestObject backTestIngestObject, Date date, PlayerStatFilter playerStatFilter,
                                         MLBTeam safeTeam, ScoreModel scoreModel, boolean home, MLBGame unsafeGame, boolean newGames,HashMap<Integer,ScoreModel> gameCache){
        MLBTeam team = new MLBTeam();
        if(safeTeam == null){
            if(home) {
                team.setMlbId(unsafeGame.getHomeTeamMlbId());
                team.setTeamName(unsafeGame.getHomeTeamName());
                team.setTeamAbbreviation(unsafeGame.getHomeTeamTricode());
            }else{
                team.setMlbId(unsafeGame.getAwayTeamMlbId());
                team.setTeamName(unsafeGame.getAwayTeamName());
                team.setTeamAbbreviation(unsafeGame.getAwayTeamTricode());
            }
        }else{
            team = (MLBTeam) safeTeam.clone();
        }
        MLBGame safeGame = (MLBGame) unsafeGame.clone();


        List<MLBGame> lastGames = teamStatFetcher.getTeamsLastNGames(backTestIngestObject.getGameCount(), team, safeGame);
        List<Double> walkRocs = new ArrayList<>();
        List<Double> gameResult = new ArrayList<>();
        for(MLBGame mlbGame : lastGames){
            ScoreModel foundModel = gameCache.get(mlbGame.getGameId());
            if(foundModel == null){
                break;
            }
            if(mlbGame.getHomeTeamName().equals(team.getTeamName())){
                if(!Double.isNaN(foundModel.getHomeWalkRoc())) {
                    walkRocs.add(foundModel.getHomeWalkRoc());
                    if (mlbGame.getHomePoints() > mlbGame.getAwayPoints()) {
                        gameResult.add(1.0);
                    } else {
                        gameResult.add(0.0);
                    }
                }
            }else if(mlbGame.getAwayTeamName().equals(team.getTeamName())){
                if(!Double.isNaN(foundModel.getAwayWalkRoc())) {
                    walkRocs.add(foundModel.getAwayWalkRoc());
                    if (mlbGame.getAwayPoints() > mlbGame.getHomePoints()) {
                        gameResult.add(1.0);
                    } else {
                        gameResult.add(0.0);
                    }
                }
            }
        }
        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
        if(walkRocs.size()>1) {
            if (home) {
                scoreModel.setHomeTestCorrelation(pearsonsCorrelation.correlation(convertListToArray(walkRocs), convertListToArray(gameResult)));
            } else {
                scoreModel.setAwayTestCorrelation(pearsonsCorrelation.correlation(convertListToArray(walkRocs), convertListToArray(gameResult)));
            }
        }
    }
    public void calculateRunCorrelation(BackTestIngestObject backTestIngestObject, Date date, PlayerStatFilter playerStatFilter,
                                         MLBTeam safeTeam, ScoreModel scoreModel, boolean home, MLBGame unsafeGame, boolean newGames,HashMap<Integer,ScoreModel> gameCache){
        MLBGame safeGame = (MLBGame) unsafeGame.clone();
        MLBTeam team = new MLBTeam();
        if(safeTeam == null){
            if(home) {
                team.setMlbId(unsafeGame.getHomeTeamMlbId());
                team.setTeamName(unsafeGame.getHomeTeamName());
                team.setTeamAbbreviation(unsafeGame.getHomeTeamTricode());
            }else{
                team.setMlbId(unsafeGame.getAwayTeamMlbId());
                team.setTeamName(unsafeGame.getAwayTeamName());
                team.setTeamAbbreviation(unsafeGame.getAwayTeamTricode());
            }
        }else{
            team = (MLBTeam) safeTeam.clone();
        }

        List<MLBGame> lastGames = teamStatFetcher.getTeamsLastNGames(backTestIngestObject.getGameCount(), team, safeGame);
        List<Double> runRocs = new ArrayList<>();
        List<Double> gameResult = new ArrayList<>();
        for(MLBGame mlbGame : lastGames){
            ScoreModel foundModel = gameCache.get(mlbGame.getGameId());
            if(foundModel == null){
                break;
            }

            if(mlbGame.getHomeTeamName().equals(team.getTeamName())){
                if(!Double.isNaN(foundModel.getHomeRunsScoredPerGameRoc())) {
                    runRocs.add(foundModel.getHomeRunsScoredPerGameRoc());
                    if (mlbGame.getHomePoints() > mlbGame.getAwayPoints()) {
                        gameResult.add(1.0);
                    } else {
                        gameResult.add(0.0);
                    }
                }
            }else if(mlbGame.getAwayTeamName().equals(team.getTeamName())){
                if(!Double.isNaN(foundModel.getAwayRunsScoredPerGameRoc())) {
                    runRocs.add(foundModel.getAwayRunsScoredPerGameRoc());
                    if (mlbGame.getAwayPoints() > mlbGame.getHomePoints()) {
                        gameResult.add(1.0);
                    } else {
                        gameResult.add(0.0);
                    }
                }
            }
        }
        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();

        if(runRocs.size() > 1) {

            if (home) {
                scoreModel.setHomeRunCorrelation(pearsonsCorrelation.correlation(convertListToArray(runRocs), convertListToArray(gameResult)));
            } else {
                scoreModel.setAwayRunCorrelation(pearsonsCorrelation.correlation(convertListToArray(runRocs), convertListToArray(gameResult)));
            }
        }
    }
    public void calculateGivenUpCorrelation(BackTestIngestObject backTestIngestObject, Date date, PlayerStatFilter playerStatFilter,
                                        MLBTeam safeTeam, ScoreModel scoreModel, boolean home, MLBGame unsafeGame, boolean newGames,HashMap<Integer,ScoreModel> gameCache){
        MLBGame safeGame = (MLBGame) unsafeGame.clone();
        MLBTeam team = new MLBTeam();
        if(safeTeam == null){
            if(home) {
                team.setMlbId(unsafeGame.getHomeTeamMlbId());
                team.setTeamName(unsafeGame.getHomeTeamName());
                team.setTeamAbbreviation(unsafeGame.getHomeTeamTricode());
            }else{
                team.setMlbId(unsafeGame.getAwayTeamMlbId());
                team.setTeamName(unsafeGame.getAwayTeamName());
                team.setTeamAbbreviation(unsafeGame.getAwayTeamTricode());
            }
        }else{
            team = (MLBTeam) safeTeam.clone();
        }

        List<MLBGame> lastGames = teamStatFetcher.getTeamsLastNGames(backTestIngestObject.getPitcherGameLookback(), team, safeGame);
        List<Double> runRocs = new ArrayList<>();
        List<Double> gameResult = new ArrayList<>();
        for(MLBGame mlbGame : lastGames){
            ScoreModel foundModel = gameCache.get(mlbGame.getGameId());
            if(foundModel == null){
                break;
            }
            if(mlbGame.getHomeTeamName().equals(team.getTeamName())){
                if(!Double.isNaN(foundModel.getAwayRunsScoredPerGameRoc())) {
                    runRocs.add(foundModel.getAwayRunsScoredPerGameRoc());
                    if (mlbGame.getHomePoints() > mlbGame.getAwayPoints()) {
                        gameResult.add(1.0);
                    } else {
                        gameResult.add(0.0);
                    }
                }
            }else if(mlbGame.getAwayTeamName().equals(team.getTeamName())){
                if(!Double.isNaN(foundModel.getHomeRunsScoredPerGameRoc())) {
                    runRocs.add(foundModel.getHomeRunsScoredPerGameRoc());
                    if (mlbGame.getAwayPoints() > mlbGame.getHomePoints()) {
                        gameResult.add(1.0);
                    } else {
                        gameResult.add(0.0);
                    }
                }
            }
        }
        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
        if(runRocs.size() > 1 ) {
            if (home) {
                scoreModel.setHomeGivenUpCorrelation(pearsonsCorrelation.correlation(convertListToArray(runRocs), convertListToArray(gameResult)));
            } else {
                scoreModel.setAwayGivenUpCorrelation(pearsonsCorrelation.correlation(convertListToArray(runRocs), convertListToArray(gameResult)));
            }
        }
    }
    public void calculateFieldingCorrelation(BackTestIngestObject backTestIngestObject, Date date, PlayerStatFilter playerStatFilter,
                                            MLBTeam safeTeam, ScoreModel scoreModel, boolean home, MLBGame unsafeGame, boolean newGames){
        MLBGame safeGame = (MLBGame) unsafeGame.clone();
        MLBTeam team = new MLBTeam();
        if(safeTeam == null){
            if(home) {
                team.setMlbId(unsafeGame.getHomeTeamMlbId());
                team.setTeamName(unsafeGame.getHomeTeamName());
                team.setTeamAbbreviation(unsafeGame.getHomeTeamTricode());
            }else{
                team.setMlbId(unsafeGame.getAwayTeamMlbId());
                team.setTeamName(unsafeGame.getAwayTeamName());
                team.setTeamAbbreviation(unsafeGame.getAwayTeamTricode());
            }
        }else{
            team = (MLBTeam) safeTeam.clone();
        }

        List<MLBGame> lastGames = teamStatFetcher.getTeamsLastNGames(backTestIngestObject.getGameCount(), team, safeGame);
        List<Double> fieldingRocs = new ArrayList<>();
        List<Double> gameResult = new ArrayList<>();
        for(MLBGame mlbGame : lastGames){
            if(mlbGame.getHomeTeamName().equals(team.getTeamName())){
                fieldingRocs.add(scoreModel.getHomeFieldingRoc());
                if(mlbGame.getHomePoints() > mlbGame.getAwayPoints()){
                    gameResult.add(1.0);
                }else{
                    gameResult.add(0.0);
                }
            }else if(mlbGame.getAwayTeamName().equals(team.getTeamName())){
                fieldingRocs.add(scoreModel.getAwayFieldingRoc());
                if(mlbGame.getAwayPoints() > mlbGame.getHomePoints()){
                    gameResult.add(1.0);
                }else{
                    gameResult.add(0.0);
                }
            }
        }
        PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
        if(fieldingRocs.size() > 1) {
            if (home) {
                scoreModel.setHomeFieldingCorrelation(pearsonsCorrelation.correlation(convertListToArray(fieldingRocs), convertListToArray(gameResult)));
            } else {
                scoreModel.setAwayFieldingCorrelation(pearsonsCorrelation.correlation(convertListToArray(fieldingRocs), convertListToArray(gameResult)));
            }
        }
    }

    public void calculateTestCorrelationRoc(BackTestIngestObject backTestIngestObject, Date date, PlayerStatFilter playerStatFilter,
                                         MLBTeam safeTeam, ScoreModel scoreModel, boolean home, MLBGame unsafeGame, boolean newGames, HashMap<Integer,ScoreModel> gameCache){
        MLBGame safeGame = (MLBGame) unsafeGame.clone();
        MLBTeam team = new MLBTeam();
        if(safeTeam == null){
            if(home) {
                team.setMlbId(unsafeGame.getHomeTeamMlbId());
                team.setTeamName(unsafeGame.getHomeTeamName());
                team.setTeamAbbreviation(unsafeGame.getHomeTeamTricode());
            }else{
                team.setMlbId(unsafeGame.getAwayTeamMlbId());
                team.setTeamName(unsafeGame.getAwayTeamName());
                team.setTeamAbbreviation(unsafeGame.getAwayTeamTricode());
            }
        }else{
            team = (MLBTeam) safeTeam.clone();
        }

        List<MLBGame> lastGames = teamStatFetcher.getTeamsLastNGamesClean(backTestIngestObject.getGameCount(), team, safeGame);

        List<Double> walkRocs = new ArrayList<>();
        for(MLBGame mlbGame : lastGames){
            ScoreModel foundModel = gameCache.get(mlbGame.getGameId());
            if(foundModel == null){
                break;
            }
            if(mlbGame.getHomeTeamName().equals(team.getTeamName())){
                walkRocs.add(foundModel.getHomeTestCorrelation());
            } else if(mlbGame.getAwayTeamName().equals(team.getTeamName())){
                walkRocs.add(foundModel.getAwayTestCorrelation());
            }
        }
        SimpleRegression simpleRegression = new SimpleRegression();
        for(int i = 0; i < walkRocs.size(); i++){
            simpleRegression.addData(i,walkRocs.get(i));
        }
        double slope = simpleRegression.getSlope();
        if(simpleRegression.getN() != 0){
            if(home) {
                scoreModel.setHomeTestCorrelationRoc(slope);
            }else{
                scoreModel.setAwayTestCorrelationRoc(slope);
            }
        }
    }

    public void calculateRunScoredVolRoc(BackTestIngestObject backTestIngestObject, Date date, PlayerStatFilter playerStatFilter,
                                            MLBTeam safeTeam, ScoreModel scoreModel, boolean home, MLBGame unsafeGame, boolean newGames, HashMap<Integer,ScoreModel> gameCache){
        MLBGame safeGame = (MLBGame) unsafeGame.clone();
        MLBTeam team = new MLBTeam();
        if(safeTeam == null){
            if(home) {
                team.setMlbId(unsafeGame.getHomeTeamMlbId());
                team.setTeamName(unsafeGame.getHomeTeamName());
                team.setTeamAbbreviation(unsafeGame.getHomeTeamTricode());
            }else{
                team.setMlbId(unsafeGame.getAwayTeamMlbId());
                team.setTeamName(unsafeGame.getAwayTeamName());
                team.setTeamAbbreviation(unsafeGame.getAwayTeamTricode());
            }
        }else{
            team = (MLBTeam) safeTeam.clone();
        }

        List<MLBGame> lastGames = teamStatFetcher.getTeamsLastNGamesClean(backTestIngestObject.getGameCount(), team, safeGame);

        List<Double> runRocs = new ArrayList<>();
        for(MLBGame mlbGame : lastGames){
            ScoreModel foundModel = gameCache.get(mlbGame.getGameId());
            if(foundModel == null){
                break;
            }
            if(mlbGame.getHomeTeamName().equals(team.getTeamName())){
                runRocs.add(foundModel.getHomeRunsScoredVol());
            } else if(mlbGame.getAwayTeamName().equals(team.getTeamName())){
                runRocs.add(foundModel.getAwayRunsScoredVol());
            }
        }
        SimpleRegression simpleRegression = new SimpleRegression();
        for(int i = 0; i < runRocs.size(); i++){
            simpleRegression.addData(i,runRocs.get(i));
        }
        if(simpleRegression.getN() != 0) {
            if(home) {
                scoreModel.setHomeRunsScoredVolRoc(simpleRegression.getSlope());
            }else{
                scoreModel.setAwayRunsScoredVolRoc(simpleRegression.getSlope());
            }
        }
    }
    public void calculateRunGivenUpVolRoc(BackTestIngestObject backTestIngestObject, Date date, PlayerStatFilter playerStatFilter,
                                         MLBTeam safeTeam, ScoreModel scoreModel, boolean home, MLBGame unsafeGame, boolean newGames, HashMap<Integer,ScoreModel> gameCache){
        MLBGame safeGame = (MLBGame) unsafeGame.clone();
        MLBTeam team = new MLBTeam();
        if(safeTeam == null){
            if(home) {
                team.setMlbId(unsafeGame.getHomeTeamMlbId());
                team.setTeamName(unsafeGame.getHomeTeamName());
                team.setTeamAbbreviation(unsafeGame.getHomeTeamTricode());
            }else{
                team.setMlbId(unsafeGame.getAwayTeamMlbId());
                team.setTeamName(unsafeGame.getAwayTeamName());
                team.setTeamAbbreviation(unsafeGame.getAwayTeamTricode());
            }
        }else{
            team = (MLBTeam) safeTeam.clone();
        }

        List<MLBGame> lastGames = teamStatFetcher.getTeamsLastNGamesClean(backTestIngestObject.getGameCount(), team, safeGame);

        List<Double> runRocs = new ArrayList<>();
        for(MLBGame mlbGame : lastGames){
            ScoreModel foundModel = gameCache.get(mlbGame.getGameId());
            if(foundModel == null){
                break;
            }
            if(mlbGame.getHomeTeamName().equals(team.getTeamName())){
                runRocs.add(foundModel.getHomeRunsGivenUpVol());
            } else if(mlbGame.getAwayTeamName().equals(team.getTeamName())){
                runRocs.add(foundModel.getAwayRunsGivenUpVol());
            }
        }
        SimpleRegression simpleRegression = new SimpleRegression();
        for(int i = 0; i < runRocs.size(); i++){
            simpleRegression.addData(i,runRocs.get(i));
        }
        if(simpleRegression.getN() != 0) {
            if(home) {
                scoreModel.setHomeRunsGivenUpVolRoc(simpleRegression.getSlope());
            }else{
                scoreModel.setAwayRunsGivenUpVolRoc(simpleRegression.getSlope());
            }
        }
    }

    public double calculateTeamRunsScoredRoc(BackTestIngestObject backTestIngestObject,
                                            MLBTeam safeTeam, ScoreModel scoreModel, boolean home, MLBGame unsafeGame){
        MLBGame safeGame = (MLBGame) unsafeGame.clone();
        MLBTeam team = new MLBTeam();
        if(safeTeam == null){
            if(home) {
                team.setMlbId(unsafeGame.getHomeTeamMlbId());
                team.setTeamName(unsafeGame.getHomeTeamName());
                team.setTeamAbbreviation(unsafeGame.getHomeTeamTricode());
            }else{
                team.setMlbId(unsafeGame.getAwayTeamMlbId());
                team.setTeamName(unsafeGame.getAwayTeamName());
                team.setTeamAbbreviation(unsafeGame.getAwayTeamTricode());
            }
        }else{
            team = (MLBTeam) safeTeam.clone();
        }

        List<MLBGame> lastGames = teamStatFetcher.getTeamsLastNGamesClean(backTestIngestObject.getGameCount(), team, safeGame);

        List<Integer> runsScored = new ArrayList<>();
        for(MLBGame mlbGame : lastGames){
            if(mlbGame.getHomeTeamName().equals(team.getTeamName())){
                runsScored.add(mlbGame.getHomePoints());
            } else if(mlbGame.getAwayTeamName().equals(team.getTeamName())){
                runsScored.add(mlbGame.getAwayPoints());
            }
        }
        SimpleRegression simpleRegression = new SimpleRegression();
        for(int i = 0; i < runsScored.size(); i++){
            simpleRegression.addData(i,runsScored.get(i));
        }
        double slope = simpleRegression.getSlope();
        return slope;
    }

    public double calculateTeamRunsScoredVol(BackTestIngestObject backTestIngestObject,
                                             MLBTeam safeTeam, ScoreModel scoreModel, boolean home, MLBGame unsafeGame){
        MLBGame safeGame = (MLBGame) unsafeGame.clone();
        MLBTeam team = new MLBTeam();
        if(safeTeam == null){
            if(home) {
                team.setMlbId(unsafeGame.getHomeTeamMlbId());
                team.setTeamName(unsafeGame.getHomeTeamName());
                team.setTeamAbbreviation(unsafeGame.getHomeTeamTricode());
            }else{
                team.setMlbId(unsafeGame.getAwayTeamMlbId());
                team.setTeamName(unsafeGame.getAwayTeamName());
                team.setTeamAbbreviation(unsafeGame.getAwayTeamTricode());
            }
        }else{
            team = (MLBTeam) safeTeam.clone();
        }

        List<MLBGame> lastGames = teamStatFetcher.getTeamsLastNGamesClean(backTestIngestObject.getGameCount(), team, safeGame);

        List<Integer> runsScored = new ArrayList<>();

        for(MLBGame mlbGame : lastGames){
            if(mlbGame.getHomeTeamName().equals(team.getTeamName())){
                runsScored.add(mlbGame.getHomePoints());
            } else if(mlbGame.getAwayTeamName().equals(team.getTeamName())){
                runsScored.add(mlbGame.getAwayPoints());
            }
        }
        List<Integer> diffs = new ArrayList<>();
        int total = 0;
        for(int i = 1; i < runsScored.size(); i++){
            diffs.add(runsScored.get(i) - runsScored.get(i-1));
            total += runsScored.get(i);
        }

        double variance = 0;
        int size = diffs.size();
        int oldSize = diffs.size();
        for (int i = 0; i < size; i++) {
            variance += Math.pow(diffs.get(i) - (double) total / oldSize, 2);
        }
        variance /= oldSize - 1;
        return variance;
    }

    public double calculateTeamRunsGivenUpPerGame(BackTestIngestObject backTestIngestObject,
                                             MLBTeam safeTeam, ScoreModel scoreModel, boolean home, MLBGame unsafeGame){
        MLBGame safeGame = (MLBGame) unsafeGame.clone();
        MLBTeam team = new MLBTeam();
        if(safeTeam == null){
            if(home) {
                team.setMlbId(unsafeGame.getHomeTeamMlbId());
                team.setTeamName(unsafeGame.getHomeTeamName());
                team.setTeamAbbreviation(unsafeGame.getHomeTeamTricode());
            }else{
                team.setMlbId(unsafeGame.getAwayTeamMlbId());
                team.setTeamName(unsafeGame.getAwayTeamName());
                team.setTeamAbbreviation(unsafeGame.getAwayTeamTricode());
            }
        }else{
            team = (MLBTeam) safeTeam.clone();
        }

        List<MLBGame> lastGames = teamStatFetcher.getTeamsLastNGamesClean(backTestIngestObject.getGameCount(), team, safeGame);

        List<Integer> runsScored = new ArrayList<>();
        double total = 0.0;
        for(MLBGame mlbGame : lastGames){
            if(mlbGame.getHomeTeamName().equals(team.getTeamName())){
                runsScored.add(mlbGame.getHomePoints());
                total += mlbGame.getAwayPoints();
            } else if(mlbGame.getAwayTeamName().equals(team.getTeamName())){
                runsScored.add(mlbGame.getAwayPoints());
                total += mlbGame.getHomePoints();
            }
        }

        return total/runsScored.size();
    }
    public double calculateTeamRunsGivenUpVol(BackTestIngestObject backTestIngestObject,
                                             MLBTeam safeTeam, ScoreModel scoreModel, boolean home, MLBGame unsafeGame){
        MLBGame safeGame = (MLBGame) unsafeGame.clone();
        MLBTeam team = new MLBTeam();
        if(safeTeam == null){
            if(home) {
                team.setMlbId(unsafeGame.getHomeTeamMlbId());
                team.setTeamName(unsafeGame.getHomeTeamName());
                team.setTeamAbbreviation(unsafeGame.getHomeTeamTricode());
            }else{
                team.setMlbId(unsafeGame.getAwayTeamMlbId());
                team.setTeamName(unsafeGame.getAwayTeamName());
                team.setTeamAbbreviation(unsafeGame.getAwayTeamTricode());
            }
        }else{
            team = (MLBTeam) safeTeam.clone();
        }

        List<MLBGame> lastGames = teamStatFetcher.getTeamsLastNGamesClean(backTestIngestObject.getGameCount(), team, safeGame);

        List<Integer> runsScored = new ArrayList<>();

        for(MLBGame mlbGame : lastGames){
            if(mlbGame.getHomeTeamName().equals(team.getTeamName())){
                runsScored.add(mlbGame.getAwayPoints());
            } else if(mlbGame.getAwayTeamName().equals(team.getTeamName())){
                runsScored.add(mlbGame.getHomePoints());
            }
        }
        List<Integer> diffs = new ArrayList<>();
        int total = 0;
        for(int i = 1; i < runsScored.size(); i++){
            diffs.add(runsScored.get(i) - runsScored.get(i-1));
            total += runsScored.get(i);
        }

        double variance = 0;
        int size = diffs.size();
        int oldSize = diffs.size();
        for (int i = 0; i < size; i++) {
            variance += Math.pow(diffs.get(i) - (double) total / oldSize, 2);
        }
        variance /= oldSize - 1;
        return variance;
    }


    public double calculateTeamRunsGivenUpRoc(BackTestIngestObject backTestIngestObject,
                                           MLBTeam safeTeam, ScoreModel scoreModel, boolean home, MLBGame unsafeGame){
        MLBGame safeGame = (MLBGame) unsafeGame.clone();
        MLBTeam team = new MLBTeam();
        if(safeTeam == null){
            if(home) {
                team.setMlbId(unsafeGame.getHomeTeamMlbId());
                team.setTeamName(unsafeGame.getHomeTeamName());
                team.setTeamAbbreviation(unsafeGame.getHomeTeamTricode());
            }else{
                team.setMlbId(unsafeGame.getAwayTeamMlbId());
                team.setTeamName(unsafeGame.getAwayTeamName());
                team.setTeamAbbreviation(unsafeGame.getAwayTeamTricode());
            }
        }else{
            team = (MLBTeam) safeTeam.clone();
        }

        List<MLBGame> lastGames = teamStatFetcher.getTeamsLastNGamesClean(backTestIngestObject.getPitcherGameLookback(), team, safeGame);

        List<Integer> runsScored = new ArrayList<>();
        for(MLBGame mlbGame : lastGames){
            if(mlbGame.getHomeTeamName().equals(team.getTeamName())){
                runsScored.add(mlbGame.getAwayPoints());
            } else if(mlbGame.getAwayTeamName().equals(team.getTeamName())){
                runsScored.add(mlbGame.getHomePoints());
            }
        }
        SimpleRegression simpleRegression = new SimpleRegression();
        for(int i = 0; i < runsScored.size(); i++){
            simpleRegression.addData(i,runsScored.get(i));
        }
        double slope = simpleRegression.getSlope();
        return slope;
    }

    public void calculateTestCorrelationRocBool(BackTestIngestObject backTestIngestObject, Date date, PlayerStatFilter playerStatFilter,
                                                 MLBTeam safeTeam, ScoreModel scoreModel, boolean home, MLBGame unsafeGame, boolean newGames, HashMap<Integer,ScoreModel> gameCache){

        MLBGame safeGame = (MLBGame) unsafeGame.clone();
        MLBTeam team = new MLBTeam();
        if(safeTeam == null){
            if(home) {
                team.setMlbId(unsafeGame.getHomeTeamMlbId());
                team.setTeamName(unsafeGame.getHomeTeamName());
                team.setTeamAbbreviation(unsafeGame.getHomeTeamTricode());
            }else{
                team.setMlbId(unsafeGame.getAwayTeamMlbId());
                team.setTeamName(unsafeGame.getAwayTeamName());
                team.setTeamAbbreviation(unsafeGame.getAwayTeamTricode());
            }
        }else{
            team = (MLBTeam) safeTeam.clone();
        }

        List<MLBGame> lastGames = teamStatFetcher.getTeamsLastNGamesClean(backTestIngestObject.getGameCount(), team, safeGame);

        double slope;
        if(safeGame.getHomeMLBTeam().getTeamName().equals(team.getTeamName())){
            slope  = gameCache.get(safeGame.getGameId()).getHomeTestCorrelationRoc() + (gameCache.get(safeGame.getGameId()).getHomeGivenUpCorrelationRoc()) ;
        }else{
            slope  = gameCache.get(safeGame.getGameId()).getAwayTestCorrelationRoc() + (gameCache.get(safeGame.getGameId()).getAwayGivenUpCorrelationRoc());
        }
        double test;
        double test2;
        if(lastGames.size() != 0) {
            if (gameCache.get(lastGames.get(0).getGameId()) != null) {
                ScoreModel scoreModel1 = gameCache.get(lastGames.get(0).getGameId());
                if (lastGames.get(0).getHomeTeamName().equals(team.getTeamName())) {
                    test = scoreModel1.getHomeTestCorrelationRoc();
                    test2 = scoreModel1.getHomeGivenUpCorrelationRoc();
                } else {
                    test = scoreModel1.getAwayTestCorrelationRoc();
                    test2 = scoreModel1.getAwayGivenUpCorrelationRoc();
                }
                // if(simpleRegression.getN() != 0){
                if (home) {
                    //    scoreModel.setHomeTestCorrelationRoc(slope);
                    scoreModel.setTestHomeBoolean((test + (test2) > 0 && slope < 0));
                } else {
                    //   scoreModel.setAwayTestCorrelationRoc(slope);
                    scoreModel.setTestAwayBoolean((test + (test2) < 0 && slope > 0));
                }
            }
        }
        //   }
    }
    public void calculateAveragePlayersRoc(BackTestIngestObject backTestIngestObject, Date date, PlayerStatFilter playerStatFilter,
                                                MLBTeam safeTeam, ScoreModel scoreModel, boolean home, MLBGame unsafeGame, boolean newGames, HashMap<Integer,ScoreModel> gameCache){

        MLBGame safeGame = (MLBGame) unsafeGame.clone();
        MLBTeam team = new MLBTeam();
        if(safeTeam == null){
            if(home) {
                team.setMlbId(unsafeGame.getHomeTeamMlbId());
                team.setTeamName(unsafeGame.getHomeTeamName());
                team.setTeamAbbreviation(unsafeGame.getHomeTeamTricode());
            }else{
                team.setMlbId(unsafeGame.getAwayTeamMlbId());
                team.setTeamName(unsafeGame.getAwayTeamName());
                team.setTeamAbbreviation(unsafeGame.getAwayTeamTricode());
            }
        }else{
            team = (MLBTeam) safeTeam.clone();
        }

        List<MLBGame> lastGames = teamStatFetcher.getTeamsLastNGamesClean(backTestIngestObject.getGameCount(), team, safeGame);

        List<Double> runRocs = new ArrayList<>();
        for(MLBGame mlbGame : lastGames){
            ScoreModel foundModel = gameCache.get(mlbGame.getGameId());
            if(foundModel == null){
                break;
            }
            if(mlbGame.getHomeTeamName().equals(team.getTeamName())){
                runRocs.add(foundModel.getHomeAveragePriorPlayers());
            } else if(mlbGame.getAwayTeamName().equals(team.getTeamName())){
                runRocs.add(foundModel.getAwayAveragePriorPlayers());
            }
        }
        SimpleRegression simpleRegression = new SimpleRegression();
        for(int i = 0; i < runRocs.size(); i++){
            simpleRegression.addData(i,runRocs.get(i));
        }
        if(simpleRegression.getN() != 0) {
            if(home) {
                scoreModel.setHomeAveragePriorPlayersRoc(simpleRegression.getSlope());
            }else{
                scoreModel.setAwayAveragePriorPlayersRoc(simpleRegression.getSlope());
            }
        }
        //   }
    }

    public void calculateRunCorrelationRocBool(BackTestIngestObject backTestIngestObject, Date date, PlayerStatFilter playerStatFilter,
                                                MLBTeam safeTeam, ScoreModel scoreModel, boolean home, MLBGame unsafeGame, boolean newGames, HashMap<Integer,ScoreModel> gameCache){

        MLBGame safeGame = (MLBGame) unsafeGame.clone();
        MLBTeam team = new MLBTeam();
        if(safeTeam == null){
            if(home) {
                team.setMlbId(unsafeGame.getHomeTeamMlbId());
                team.setTeamName(unsafeGame.getHomeTeamName());
                team.setTeamAbbreviation(unsafeGame.getHomeTeamTricode());
            }else{
                team.setMlbId(unsafeGame.getAwayTeamMlbId());
                team.setTeamName(unsafeGame.getAwayTeamName());
                team.setTeamAbbreviation(unsafeGame.getAwayTeamTricode());
            }
        }else{
            team = (MLBTeam) safeTeam.clone();
        }

        List<MLBGame> lastGames = teamStatFetcher.getTeamsLastNGamesClean(backTestIngestObject.getGameCount(), team, safeGame);

        double slope;
        if(safeGame.getHomeMLBTeam().getTeamName().equals(team.getTeamName())){
            slope  = gameCache.get(safeGame.getGameId()).getHomeRunCorrelationRoc();
        }else{
            slope  = gameCache.get(safeGame.getGameId()).getAwayRunCorrelationRoc();
        }
        double test;
        if(lastGames.size() != 0) {
            if (gameCache.get(lastGames.get(0).getGameId()) != null) {
                ScoreModel scoreModel1 = gameCache.get(lastGames.get(0).getGameId());
                if (lastGames.get(0).getHomeTeamName().equals(team.getTeamName())) {
                    test = scoreModel1.getHomeRunCorrelationRoc();
                } else {
                    test = scoreModel1.getAwayRunCorrelationRoc();
                }
                // if(simpleRegression.getN() != 0){
                if (home) {
                    //    scoreModel.setHomeTestCorrelationRoc(slope);
                    scoreModel.setRunHomeBoolean((test < 0 && slope > 0));
                } else {
                    //   scoreModel.setAwayTestCorrelationRoc(slope);
                    scoreModel.setRunAwayBoolean((test > 0 && slope < 0));
                }
            }
        }
        //   }
    }

    public void calculateRunCorrelationRoc(BackTestIngestObject backTestIngestObject, Date date, PlayerStatFilter playerStatFilter,
                                        MLBTeam safeTeam, ScoreModel scoreModel, boolean home, MLBGame unsafeGame, boolean newGames, HashMap<Integer,ScoreModel> gameCache){
        MLBGame safeGame = (MLBGame) unsafeGame.clone();
        MLBTeam team = new MLBTeam();
        if(safeTeam == null){
            if(home) {
                team.setMlbId(unsafeGame.getHomeTeamMlbId());
                team.setTeamName(unsafeGame.getHomeTeamName());
                team.setTeamAbbreviation(unsafeGame.getHomeTeamTricode());
            }else{
                team.setMlbId(unsafeGame.getAwayTeamMlbId());
                team.setTeamName(unsafeGame.getAwayTeamName());
                team.setTeamAbbreviation(unsafeGame.getAwayTeamTricode());
            }
        }else{
            team = (MLBTeam) safeTeam.clone();
        }

        List<MLBGame> lastGames = teamStatFetcher.getTeamsLastNGamesClean(backTestIngestObject.getGameCount(), team, safeGame);

        List<Double> runRocs = new ArrayList<>();
        for(MLBGame mlbGame : lastGames){
            ScoreModel foundModel = gameCache.get(mlbGame.getGameId());
            if(foundModel == null){
                break;
            }
            if(mlbGame.getHomeTeamName().equals(team.getTeamName())){
                runRocs.add(foundModel.getHomeRunCorrelation());
            } else if(mlbGame.getAwayTeamName().equals(team.getTeamName())){
                runRocs.add(foundModel.getAwayRunCorrelation());
            }
        }
        SimpleRegression simpleRegression = new SimpleRegression();
        for(int i = 0; i < runRocs.size(); i++){
            simpleRegression.addData(i,runRocs.get(i));
        }
        if(simpleRegression.getN() != 0) {
            if(home) {
                scoreModel.setHomeRunCorrelationRoc(simpleRegression.getSlope());
            }else{
                scoreModel.setAwayRunCorrelationRoc(simpleRegression.getSlope());
            }
        }
    }

    public void calculateGivenUpCorrelationRoc(BackTestIngestObject backTestIngestObject, Date date, PlayerStatFilter playerStatFilter,
                                            MLBTeam safeTeam, ScoreModel scoreModel, boolean home, MLBGame unsafeGame, boolean newGames, HashMap<Integer,ScoreModel> gameCache){
        MLBGame safeGame = (MLBGame) unsafeGame.clone();
        MLBTeam team = new MLBTeam();
        if(safeTeam == null){
            if(home) {
                team.setMlbId(unsafeGame.getHomeTeamMlbId());
                team.setTeamName(unsafeGame.getHomeTeamName());
                team.setTeamAbbreviation(unsafeGame.getHomeTeamTricode());
            }else{
                team.setMlbId(unsafeGame.getAwayTeamMlbId());
                team.setTeamName(unsafeGame.getAwayTeamName());
                team.setTeamAbbreviation(unsafeGame.getAwayTeamTricode());
            }
        }else{
            team = (MLBTeam) safeTeam.clone();
        }

        List<MLBGame> lastGames = teamStatFetcher.getTeamsLastNGamesClean(backTestIngestObject.getPitcherGameLookback(), team, safeGame);

        List<Double> runRocs = new ArrayList<>();
        for(MLBGame mlbGame : lastGames){
            ScoreModel foundModel = gameCache.get(mlbGame.getGameId());
            if(foundModel == null){
                break;
            }
            if(mlbGame.getHomeTeamName().equals(team.getTeamName())){
                runRocs.add(foundModel.getHomeGivenUpCorrelation());
            }
            else if(mlbGame.getAwayTeamName().equals(team.getTeamName())){
                runRocs.add(foundModel.getAwayGivenUpCorrelation());
            }
        }
        SimpleRegression simpleRegression = new SimpleRegression();
        for(int i = 0; i < runRocs.size(); i++){
            simpleRegression.addData(i,runRocs.get(i));
        }
        if(simpleRegression.getN() != 0) {
            if(home) {
                scoreModel.setHomeGivenUpCorrelationRoc(simpleRegression.getSlope());
            }else{
                scoreModel.setAwayGivenUpCorrelationRoc(simpleRegression.getSlope());
            }
        }
    }
    public void calculateFieldingCorrelationRoc(BackTestIngestObject backTestIngestObject, Date date, PlayerStatFilter playerStatFilter,
                                               MLBTeam safeTeam, ScoreModel scoreModel, boolean home, MLBGame unsafeGame, boolean newGames, HashMap<Integer,ScoreModel> gameCache){
        MLBGame safeGame = (MLBGame) unsafeGame.clone();
        MLBTeam team = new MLBTeam();
        if(safeTeam == null){
            if(home) {
                team.setMlbId(unsafeGame.getHomeTeamMlbId());
                team.setTeamName(unsafeGame.getHomeTeamName());
                team.setTeamAbbreviation(unsafeGame.getHomeTeamTricode());
            }else{
                team.setMlbId(unsafeGame.getAwayTeamMlbId());
                team.setTeamName(unsafeGame.getAwayTeamName());
                team.setTeamAbbreviation(unsafeGame.getAwayTeamTricode());
            }
        }else{
            team = (MLBTeam) safeTeam.clone();
        }

        List<MLBGame> lastGames = teamStatFetcher.getTeamsLastNGamesClean(backTestIngestObject.getGameCount(), team, safeGame);

        List<Double> fieldingRocs = new ArrayList<>();
        for(MLBGame mlbGame : lastGames){
            ScoreModel foundModel = gameCache.get(mlbGame.getGameId());
            if(foundModel == null){
                break;
            }
            if(mlbGame.getHomeTeamName().equals(team.getTeamName())){
                fieldingRocs.add(foundModel.getHomeFieldingRoc());
            }
            else if(mlbGame.getAwayTeamName().equals(team.getTeamName())){
                fieldingRocs.add(foundModel.getAwayFieldingRoc());
            }
        }
        SimpleRegression simpleRegression = new SimpleRegression();
        for(int i = 0; i < fieldingRocs.size(); i++){
            simpleRegression.addData(i,fieldingRocs.get(i));
        }
        if(simpleRegression.getN() != 0) {
            if(home) {
                scoreModel.setHomeFieldingCorrelationRoc(simpleRegression.getSlope());
            }else{
                scoreModel.setAwayFieldingCorrelationRoc(simpleRegression.getSlope());
            }
        }
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
