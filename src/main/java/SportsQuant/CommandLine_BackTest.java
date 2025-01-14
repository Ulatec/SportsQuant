package SportsQuant;

import SportsQuant.Model.*;
import SportsQuant.Repository.*;
import SportsQuant.Threads.*;
import SportsQuant.Util.ListSplitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
public class CommandLine_BackTest implements ApplicationRunner {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private PlayerGamePerformanceRepository playerGamePerformanceRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameOddsRepository gameOddsRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //BackTestSet backTestSet = buildBackTestSet();
        System.out.println("backtest");
        boolean accuracyTesting = false;
        boolean correlationTesting = false;
        boolean statTesting = false;
        List<Game> games = (List<Game>) gameRepository.findAll();
        List<GameOdds> gameOdds = (List<GameOdds>) gameOddsRepository.findAll();
        HashMap<Integer, GameOdds> gameOddsHashMap = new HashMap<>();
        for(GameOdds gameOdds1 : gameOdds){
            gameOddsHashMap.put(gameOdds1.getGameId(), gameOdds1);
        }
        for(Game game : games){
            for(GameOdds gameOdds1: gameOdds){
                if(gameOdds1.getGameId() == game.getGameId()){
                    game.setGameOdds(gameOdds1);
                }
            }
        }
        int GamesToTest = 550;
        String betType = "moneyline";
        //LocalDate startDate = LocalDate.of(2018,11,2);
        LocalDate startDate = LocalDate.of(2025,1,2);
        // LocalDate startDate = LocalDate.of(2021,6,1);
        int threads = 16;

        double[] listOfPowerFactors = {1};

        //TODO:: SPREAD TESTS
//        Integer[] gameLookbackList = {4,6,8};
//        double[] pointPerBlockList = {2};
//        double[] pointPerStealList = {2};
//
//        double[] highBlockFactorList = {0,0.5,1,1.5,2,2.5,3};
//        double[] lowBlockFactorList = {0,0.5,1,1.5,2,2.5,3};
//        double[] highStealFactorList = {0,0.5,1,1.5,2,2.5,3};
//        double[] lowStealFactorList = {0,0.5,1,1.5,2,2.5,3};
//        double[] gameTimeList = {0,0.5,1};
//        double[] homeTeamAdvantages = {-1,0,1};
//        double[] pointThresholds = {4,6,8,10,12};

        //TODO: MONEYLINE

        Integer[] gameLookbackList = {13};
        Integer[] fractalWindows = {300};
        double[] pointPerBlockList = {0};
        double[] pointPerStealList = {-0.001};
        double pointPerTurnover = 1;
        double pointPerRebound = 1;
        double pointPerFoul = 1;
        double[] highBlockFactorList = {0};
        double[] lowBlockFactorList = {-0.01,-0.05,-0.001,-0.005,-0.1,0.01,0.05,0.005,0.001,0.1};
        double[] highStealFactorList = {0};
        double[] lowStealFactorList = {-0.01,-0.05,-0.001,-0.005,-0.1,0.01,0.05,0.005,0.001,0.1};
        double[] highTurnoverFactorList = {0};
        double[] lowTurnoverlFactorList = {0};
        double[] highReboundFactorList = {0};
        double[] lowReboundFactorList = {0};
        double[] highFoulFactorList = {-0.01,-0.05,-0.001,-0.005,-0.1,0.01,0.05,0.005,0.001,0.1};
        double[] lowFoulFactorList = {0};
        double[] gameTimeList = {0};
        double[] homeTeamAdvantages = {-0.01,-0.05,-0.001,-0.005,-0.1,0.01,0.05,0.005,0.001,0.1};
        double[] pointThresholds = {-0.01,-0.05,-0.001,-0.005,-0.1,0.01,0.05,0.005,0.001,0.1};
        boolean[] allowBelowZeros = {true};
        boolean[] doubleSquareRoots = {true};
        boolean[] totalSquareRoots = {true};
        double[] pointvol = {-0.01,-0.05,-0.001,-0.005,-0.1,0.01,0.05,0.005,0.001,0.1};


//        Integer[] gameLookbackList = {11,};
//        Integer[] fractalWindows = {300};
//        double[] pointPerBlockList = {0.05};
//        double[] pointPerStealList = {-0.05};
//        double pointPerTurnover = 1;
//        double pointPerRebound = 1;
//        double pointPerFoul = 1;
//        double[] highBlockFactorList = {0.85};
//        double[] lowBlockFactorList = {0.1};
//        double[] highStealFactorList = {0.2};
//        double[] lowStealFactorList = {0.7};
//        double[] highTurnoverFactorList = {-0.9};
//        double[] lowTurnoverlFactorList = {-0.8};
//        double[] highReboundFactorList = {-0.25};
//        double[] lowReboundFactorList = {0.6};
//        double[] highFoulFactorList = {-0.55};
//        double[] lowFoulFactorList = {0.88};
//        double[] gameTimeList = {0};
//        double[] homeTeamAdvantages = {0.27};
//        double[] pointThresholds = {-0.01,};
//        boolean[] allowBelowZeros = {true};
//        boolean[] doubleSquareRoots = {true};
//        boolean[] totalSquareRoots = {true};
//        double[] pointvol = {0};

//        Integer[] gameLookbackList = {7};
//        double[] pointPerBlockList = {2};
//        double[] pointPerStealList = {2};
//        double[] highBlockFactorList = {0.5};
//        double[] lowBlockFactorList = {3.25};
//        double[] highStealFactorList = {3.75};
//        double[] lowStealFactorList = {0.5};
//        double[] gameTimeList = {0};
//        double[] homeTeamAdvantages = {0};
//        double[] pointThresholds = {0};
//        boolean[] allowBelowZeros = {false};
//        boolean[] doubleSquareRoots = {true};
//        boolean[] totalSquareRoots = {true};


        //TODO:: OVERUNDER
//        Integer[] gameLookbackList = {7,9,11,13};
//        Integer[] fractalWindows = {300};
//        double[] pointPerBlockList = {0.25,-0.25,-0.75,0.75};
//        double[] pointPerStealList = {0.25,-0.25,-0.75,0.75};
//        double pointPerTurnover = 1;
//        double pointPerRebound = 1;
//        double pointPerFoul = 1;
//        double[] highBlockFactorList = {0.25,};
//        double[] lowBlockFactorList = {-0.25};
//        double[] highStealFactorList = {-0.75};
//        double[] lowStealFactorList = {0.25,-0.25,-0.75,0.75};
//        double[] highTurnoverFactorList = {0.25,-0.25,-0.75,0.75};
//        double[] lowTurnoverlFactorList = {0.25,-0.25,-0.75,0.75};
//        double[] highReboundFactorList = {0};
//        double[] lowReboundFactorList = {0};
//        double[] highFoulFactorList = {0};
//        double[] lowFoulFactorList = {0};
//        double[] gameTimeList = {,-0.05,-0.01,0.01,-0.005,0.005,0.01,0.05};
//        double[] homeTeamAdvantages = {0};
//        double[] pointThresholds = {-0.05,-0.01,0.01,-0.005,0.005,0.01,0.05,};
//        boolean[] allowBelowZeros = {true};
//        boolean[] doubleSquareRoots = {true};
//        boolean[] totalSquareRoots = {true};
//        double[] pointvol = {0};


//        Integer[] gameLookbackList = {5,6,7,8,9,10};
//        double[] pointPerBlockList = {0,1,2,3};
//        double[] pointPerStealList = {0,1,2,3};
//
//        double[] highBlockFactorList = {0,1,2,3,4,5};
//        double[] lowBlockFactorList = {0,1,2,3,4,5};
//        double[] highStealFactorList = {0,2,4,6,8};
//        double[] lowStealFactorList = {0,2,4,6,8};
//        double[] gameTimeList = {0.5,1,1.5,2};
//        double[] pointThresholds = {4,5,6,7,8};
//        boolean[] allowBelowZeros = {true};
//        boolean[] doubleSquareRoots = {false};
//        boolean[] totalSquareRoots = {true};
        //boolean[] modelBlocks = new boolean[] {false, true};
        //double[] blockExponentsToTest = new double[]{1,1.1,1.2};
        List<BackTestIngestObject> backTestIngestObjects = new ArrayList<>();
        //List<BackTestResult> backTestResults = new ArrayList<>();
        for(int gameCounts : gameLookbackList) {
            for (double pointReductionPerBlock : pointPerBlockList) {
                for (double pointReductionPerSteal : pointPerStealList) {
                    for (boolean squareRootTotalPoints : totalSquareRoots) {
                        for (double lowBlockFactor : lowBlockFactorList) {
                            for (double highBlockFactor : highBlockFactorList) {
                                for (boolean doubleSquareRoot : doubleSquareRoots) {
                                    for (double lowStealFactor : lowStealFactorList) {
                                        for (double highStealFactor : highStealFactorList) {
                                            for (double gameTimeThreshold : gameTimeList) {
                                                for(double pointThreshold : pointThresholds) {
                                                    for(boolean allowBelowZero : allowBelowZeros) {
                                                        for(double homeTeamAdvantage : homeTeamAdvantages) {
                                                            for(double highTurnover : highTurnoverFactorList) {
                                                                for(double lowTurnover : lowTurnoverlFactorList) {
                                                                    for(double highRebound : highReboundFactorList) {
                                                                        for(double lowRebound : lowReboundFactorList) {
                                                                            for(double highFoul : highFoulFactorList) {
                                                                                for(double lowFoul : lowFoulFactorList) {
                                                                                    for(Integer fractalWindow : fractalWindows) {
                                                                                        for(double pointvolw : pointvol) {


                                                                                            BackTestIngestObject backTestIngestObject = new BackTestIngestObject();
                                                                                            backTestIngestObject.setPlayerGameLookBack(gameCounts);
                                                                                            backTestIngestObject.setPointsReducedPerBlock(pointReductionPerBlock);
                                                                                            backTestIngestObject.setPointsReducedPerTurnover(pointPerTurnover);
                                                                                            backTestIngestObject.setPointsReducedPerRebound(pointPerRebound);
                                                                                            backTestIngestObject.setPointsReducedPerFoul(pointPerFoul);
                                                                                            backTestIngestObject.setDoubleSquareRoot(doubleSquareRoot);
                                                                                            backTestIngestObject.setModelOpponentBlocks(true);
                                                                                            backTestIngestObject.setSquareRootTotalPoints(squareRootTotalPoints);
                                                                                            backTestIngestObject.setModelOpponentSteals(true);
                                                                                            backTestIngestObject.setModelOpponentTurnovers(true);
                                                                                            backTestIngestObject.setPointReductionPerSteal(pointReductionPerSteal);
                                                                                            backTestIngestObject.setGameTimeThreshold(gameTimeThreshold);
                                                                                            backTestIngestObject.setBetType(betType);
                                                                                            backTestIngestObject.setStartDate(startDate);
                                                                                            backTestIngestObject.setHighBlockPointFactor(highBlockFactor);
                                                                                            backTestIngestObject.setLowerBlockPointFactor(lowBlockFactor);
                                                                                            backTestIngestObject.setHighStealPointFactor(highStealFactor);
                                                                                            backTestIngestObject.setLowerStealPointFactor(lowStealFactor);
                                                                                            backTestIngestObject.setHighTurnoverPointFactor(highTurnover);
                                                                                            backTestIngestObject.setLowerTurnoverPointFactor(lowTurnover);
                                                                                            backTestIngestObject.setHighReboundPointFactor(highRebound);
                                                                                            backTestIngestObject.setLowerReboundPointFactor(lowRebound);
                                                                                            backTestIngestObject.setHighFoulPointFactor(highFoul);
                                                                                            backTestIngestObject.setLowerFoulPointFactor(lowFoul);
                                                                                            backTestIngestObject.setGamesToTest(GamesToTest);
                                                                                            backTestIngestObject.setPointThreshold(pointThreshold);
                                                                                            backTestIngestObject.setAllowBelowZero(allowBelowZero);
                                                                                            backTestIngestObject.setHomeTeamAdvantage(homeTeamAdvantage);
                                                                                            backTestIngestObject.setFractalWindow(fractalWindow);
                                                                                            backTestIngestObject.setPointvolweight(pointvolw);
                                                                                            //if(isBackTestIngestObjectWithinBounds(backTestIngestObject)) {
                                                                                            backTestIngestObjects.add(backTestIngestObject);
                                                                                        }
                                                                                        //}
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
        if(statTesting){
            IndividualStatAccuracyTest individualStatAccuracyTest = new IndividualStatAccuracyTest(backTestIngestObjects, gameOddsHashMap, games);
            individualStatAccuracyTest.start();
        }
        else if(correlationTesting){
            CorrelationTesting correlationTesting1 = new CorrelationTesting(backTestIngestObjects, gameOddsHashMap, games);
            correlationTesting1.start();
        }
        //List<BackTestWatcher> backTestWatchers = new ArrayList<>();
        else if(accuracyTesting){
            BackTestTimeAccuracy backTestTimeAccuracy = new BackTestTimeAccuracy(backTestIngestObjects, gameOddsHashMap, games);
            backTestTimeAccuracy.start();
        }else{

            //System.out.println("Tests to Run: " + backTestIngestObjects.size());

            List<List<BackTestIngestObject>> listOfLists = ListSplitter.split(backTestIngestObjects, threads);
            listOfLists.removeIf(backTestIngestObjectList -> backTestIngestObjectList.size() == 0);
            ThreadMonitor threadMonitor = new ThreadMonitor(listOfLists.size());
            for(int i=0; i <listOfLists.size(); i++ ){
                BackTestThread backTestThread = new BackTestThread(listOfLists.get(i), games,  threadMonitor);
                backTestThread.setThreadNum(i);
                backTestThread.setForward(false);
                backTestThread.setGameOddsHashMap(gameOddsHashMap);
                backTestThread.start();
            }
            threadMonitor.start();
        }
    }

    public boolean isBackTestIngestObjectWithinBounds(BackTestIngestObject backTestIngestObject){
        if(backTestIngestObject.getHighBlockPointFactor() + backTestIngestObject.getLowerBlockPointFactor() <= 3) {
            return false;
        }
        if(backTestIngestObject.getHighBlockPointFactor() + backTestIngestObject.getLowerBlockPointFactor() >= 4) {
            return false;
        }
        if(backTestIngestObject.getHighFoulPointFactor() + backTestIngestObject.getLowerFoulPointFactor() >= 1) {
            return false;
        }
        if(backTestIngestObject.getHighTurnoverPointFactor() + backTestIngestObject.getLowerTurnoverPointFactor() >= 4) {
            return false;
        }
        if(backTestIngestObject.getHighTurnoverPointFactor() + backTestIngestObject.getLowerTurnoverPointFactor() <= -4) {
            return false;
        }

        return true;
    }

    public static BackTestIngestObject gettingPermanentSettings(){
        BackTestIngestObject backTestIngestObject = new BackTestIngestObject();
        backTestIngestObject.setFactorPostBlocks(false);
        backTestIngestObject.setPlayerGameLookBack(11);
       // backTestIngestObject.setGameCount(11);
        backTestIngestObject.setModelOpponentBlocks(true);
        backTestIngestObject.setDoubleSquareRoot(true);
        backTestIngestObject.setPointsReducedPerBlock(3.5);
        backTestIngestObject.setSquareRootTotalPoints(true);
        backTestIngestObject.setBlockExponent(1);
        backTestIngestObject.setLowerBlockPointFactor(0.25);
        backTestIngestObject.setReplaceLowBlocksWithBPG(false);
        backTestIngestObject.setGameTimeThreshold(0);
        backTestIngestObject.setPointReductionPerSteal(0.75);
        backTestIngestObject.setModelOpponentSteals(true);
        return backTestIngestObject;
    }
}
