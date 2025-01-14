package SportsQuant;

import SportsQuant.Model.*;
import SportsQuant.Threads.*;
import SportsQuant.Util.CSVExporter;
import SportsQuant.Util.GameFinder;
import SportsQuant.Util.ListSplitter;

import java.time.LocalDate;
import java.util.*;

public class IndividualStatAccuracyTest extends Thread{

    HashMap<Integer, GameOdds> GameOddsHashMap;
    List<BackTestIngestObject> initialBackTestIngestObjects;
    List<Game> games;
    private double highReboundIncrement = 0.5;
    private double lowReboundIncrement = 0.5;
    private double highBlockIncrement = 0.5;
    private double lowBlockIncrement = 0.5;
    private double highStealIncrement = 0.5;
    private double lowStealIncrement = 0.5;
    private double highTurnoverIncrement = 0.5;
    private double lowTurnoverIncrement = 0.5;
    private double highFoulIncrement = 0.5;
    private double lowFoulIncrement = 0.5;
    public IndividualStatAccuracyTest(List<BackTestIngestObject> backTestIngestObjects, HashMap<Integer, GameOdds> GameOddsHashMap, List<Game> games){
        this.initialBackTestIngestObjects = backTestIngestObjects;
        this.GameOddsHashMap = GameOddsHashMap;
        this.games = games;
    }
    public void run(){

        GameFinder gameFinder = new GameFinder();
        gameFinder.setGames(games);
        int GamesToTest = 5;
        String betType = "spread";
        double numSeparation = 0.05;
        List<Double> highNums = new ArrayList<>();
        List<Double> lowNums = new ArrayList<>();
        for(double i = 0.5; i <=1; i = i + numSeparation){
            highNums.add(i);
            lowNums.add(i);
        }
        //LocalDate startDate = LocalDate.of(2018,11,2);
        LocalDate startDate = LocalDate.of(2021,2,1);
        // LocalDate startDate = LocalDate.of(2021,6,1);
        int threads = 1;
        Integer[] gameLookbackList = {30};
        Integer[] fractalWindows = {10};
        double[] pointPerBlockList = {1};
        double[] pointPerStealList = {1};
        double pointPerTurnover = 1;
        double pointPerRebound = 1;
        double pointPerFoul = 1;
        double[] highBlockFactorList = {1};
        double[] lowBlockFactorList = {1};
        double[] highStealFactorList = {1};
        double[] lowStealFactorList = {1};
        double[] highTurnoverFactorList = {1};
        double[] lowTurnoverlFactorList = {1};
        double[] highReboundFactorList = {1};
        double[] lowReboundFactorList = {1};
        double[] highFoulFactorList = {1};
        double[] lowFoulFactorList = {1};
        double[] gameTimeList = {0};
        double[] homeTeamAdvantages = {0};
        double[] pointThresholds = {0};
        boolean[] allowBelowZeros = {true};
        boolean[] doubleSquareRoots = {true};
        boolean[] totalSquareRoots = {true};
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
                                                                                        for(double highNum : highNums) {
                                                                                            for(double lowNum : lowNums) {
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
                                                                                                backTestIngestObject.setNumHigh(highNum);
                                                                                                backTestIngestObject.setNumLow(lowNum);
                                                                                                backTestIngestObjects.add(backTestIngestObject);
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
            }
        }

        StatThreadMonitor statThreadMonitor = new StatThreadMonitor(threads);
        List<List<BackTestIngestObject>> listOfLists = ListSplitter.split(backTestIngestObjects, threads);
        listOfLists.removeIf(list -> list.size() == 0);
        for(int i=0; i <listOfLists.size(); i++ ){
            IndividualStatTestThread backTestThread = new IndividualStatTestThread(listOfLists.get(i), games,  statThreadMonitor);
            backTestThread.setGameOddsHashMap(GameOddsHashMap);
            backTestThread.setThreadNum(i);
            backTestThread.setForward(false);
            backTestThread.start();
        }
        List<TreeMap<String,HashMap<StatResult,Boolean>>> listOfMaps = new ArrayList<>();
        while (statThreadMonitor.getBackTestResults() == null) {
        }
        List<TreeMap<String,HashMap<StatResult,Boolean>>> results = statThreadMonitor.getBackTestResults();
        List<TreeMap<String,HashMap<StatResult,Boolean>>> pastResults = new ArrayList<>(results);
        List<StupidObject> entriesToPrint = new ArrayList<>();
        for(TreeMap<String,HashMap<StatResult,Boolean>> listItem : pastResults){
            for(Map.Entry<String,HashMap<StatResult,Boolean>> entry : listItem.entrySet()){
                if(entry.getKey().equals("fouls")){
                    //System.out.println(entry.getKey());
                    int correct = 0;
                    for(Map.Entry<StatResult,Boolean> subEntry : entry.getValue().entrySet()){
//                        System.out.println(subEntry.getKey().getForecastedNumber() + "\t" + subEntry.getKey().getActualNumber() +
//                                "\t" + subEntry.getValue() + "\t" + subEntry.getKey().getHighNum() + "\t" + subEntry.getKey().getLowNum());
                        if(subEntry.getValue()){
                            correct = correct + 1;
                        }
                    }
                    StupidObject stupidObject = new StupidObject();
                    stupidObject.setEntry(entry);
                    stupidObject.setCorrectPct((double)correct/entry.getValue().entrySet().size());
                    entriesToPrint.add(stupidObject);
                    //System.out.println("%%" + (double)correct/entry.getValue().entrySet().size());
                }
            }
        }
        entriesToPrint.sort(Comparator.comparing(StupidObject::getCorrectPct));
        //TreeMap<Double, Map.Entry<String,HashMap<StatResult,Boolean>>> sortedMap = new TreeMap<>(entriesToPrint);
        //for(Map.Entry<Double, Map.Entry<String,HashMap<StatResult,Boolean>>> entry : sortedMap.entrySet()){
            for(StupidObject stupidObject : entriesToPrint){
                //System.out.println(entry.getKey());
                int correct = 0;
                List<Double> forecastedNumbers = new ArrayList<>();
                List<Double> actualNumbers = new ArrayList<>();
                double forecastedTotal = 0.0;
                double actualTotal = 0.0;
                double highNum = 0.0;
                double lowNum = 0.0;
                double totalpredicted = 0.0;
                double totalActual = 0.0;
                for(Map.Entry<StatResult,Boolean> subEntry : stupidObject.getEntry().getValue().entrySet()){
//                    System.out.println(subEntry.getKey().getForecastedNumber() + "\t" + subEntry.getKey().getActualNumber() +
//                            "\t" + subEntry.getValue() + "\t" + subEntry.getKey().getHighNum() + "\t" + subEntry.getKey().getLowNum());
                    if(subEntry.getValue()){
                        correct = correct + 1;
                    }
                    forecastedTotal = forecastedTotal + subEntry.getKey().getForecastedNumber();
                    actualTotal = actualTotal + subEntry.getKey().getActualNumber();
                    forecastedNumbers.add(subEntry.getKey().getForecastedNumber());
                    actualNumbers.add(subEntry.getKey().getActualNumber());
                    highNum = subEntry.getKey().getHighNum();
                    lowNum = subEntry.getKey().getLowNum();
                    totalpredicted = subEntry.getKey().getPredictedPoints();
                    totalActual = subEntry.getKey().getActualPoints();
                }
                Collections.sort(forecastedNumbers);
                Collections.sort(actualNumbers);
                double median = forecastedNumbers.get(forecastedNumbers.size()/2);
                if(forecastedNumbers.size()%2 == 0) median = (median + forecastedNumbers.get(forecastedNumbers.size()/2-1)) / 2;
                double actualMedian = actualNumbers.get(actualNumbers.size()/2);
                if(actualNumbers.size()%2 == 0) actualMedian = (actualMedian + actualNumbers.get(actualNumbers.size()/2-1)) / 2;
                System.out.println(stupidObject.getCorrectPct() + " median: " + median +  " actualMedian: " + actualMedian + " delta: " + (forecastedTotal - actualTotal) +
                        " total: " + actualTotal + " high: " + highNum + " lowNum: " + lowNum  + " actual: " + totalActual + " predicted: " + totalpredicted + " %: " + (totalActual-totalpredicted)/totalActual);

        }
//        for(BackTestIngestObject backTestIngestObject : initialBackTestIngestObjects){
//            listOfMaps.add(individualStatTestThread.runBackTest(backTestIngestObject));
//        }
//        for(HashMap<String,HashMap<StatResult,Boolean>> map : listOfMaps){
//            for(Map.Entry<String, HashMap<StatResult,Boolean>> entry : map.entrySet()){
//                System.out.println(entry.getKey());
//                int correct = 0;
//                for(Map.Entry<StatResult,Boolean> subEntry : entry.getValue().entrySet()){
//                    System.out.println(subEntry.getKey().getForecastedNumber() + "\t" + subEntry.getKey().getActualNumber() + "\t" + subEntry.getValue());
//                    if(subEntry.getValue()){
//                        correct = correct + 1;
//                    }
//                }
//                System.out.println((double)correct/entry.getValue().entrySet().size());
//            }
//        }

    }

    public double getDailyVol(List<Double> splits) {
        splits.removeIf(number -> number.isNaN());
        List<Double> differentials = new ArrayList<>();
        for(int i = 0; i<splits.size(); i++){
            if(i+1< splits.size()){
                differentials.add((splits.get(i+1) - splits.get(i))/splits.get(i+1));
            }
        }
        DoubleSummaryStatistics summaryStatistics = differentials.stream().mapToDouble((x) -> x).summaryStatistics();
        double[] data = differentials.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();

        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += Math.pow(data[i] - summaryStatistics.getAverage(), 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + Math.sqrt(variance));
        return Math.sqrt(variance) * Math.sqrt(365);
    }
    public BackTestIngestObject convertResultBackToIngestObject(BackTestResult backTestResult){
        BackTestIngestObject backTestIngestObject = new BackTestIngestObject();
        backTestIngestObject.setAllowBelowZero(backTestResult.isAllowBelowZero());
        backTestIngestObject.setGamesToTest(backTestResult.getGamesToTest());
        backTestIngestObject.setStartDate(backTestResult.getStartDate());
//        backTestIngestObject.setHomeAdvantageHigh(backTestResult.getHomeAdvantageHigh());
//        backTestIngestObject.setHomeAdvantageLow(backTestResult.getHomeAdvantageLow());

        backTestIngestObject.setBetType(backTestResult.getBetType());
        backTestIngestObject.setPlayerGameLookBack(backTestResult.getPlayerGameLookBack());
        backTestIngestObject.setDoubleSquareRoot(backTestResult.isDoubleSquareRoot());
        backTestIngestObject.setHighBlockPointFactor(backTestResult.getHighBlockPointFactor());
        backTestIngestObject.setLowerBlockPointFactor(backTestResult.getLowerBlockPointFactor());
        backTestIngestObject.setHighStealPointFactor(backTestResult.getHighStealPointFactor());
        backTestIngestObject.setLowerStealPointFactor(backTestResult.getLowerStealPointFactor());
        backTestIngestObject.setHighTurnoverPointFactor(backTestResult.getHighTurnoverPointFactor());
        backTestIngestObject.setLowerTurnoverPointFactor(backTestResult.getLowerTurnoverPointFactor());
        backTestIngestObject.setPointReductionPerSteal(backTestResult.getPointsReducedPerSteal());
        backTestIngestObject.setPointsReducedPerBlock(backTestResult.getPointsReducedPerBlock());
        backTestIngestObject.setPointsReducedPerTurnover(backTestResult.getPointsReducedPerTurnover());
        backTestIngestObject.setPointsReducedPerRebound(backTestResult.getPointsReducedPerRebound());
        backTestIngestObject.setPointsReducedPerFoul(backTestResult.getPointsReducedPerFoul());
        backTestIngestObject.setHighReboundPointFactor(backTestResult.getHighReboundPointFactor());
        backTestIngestObject.setLowerReboundPointFactor(backTestResult.getLowerReboundPointFactor());
        backTestIngestObject.setHighFoulPointFactor(backTestResult.getHighFoulPointFactor());
        backTestIngestObject.setLowerFoulPointFactor(backTestResult.getLowerFoulPointFactor());
        backTestIngestObject.setModelOpponentTurnovers(backTestResult.isModelOpponentTurnovers());
        backTestIngestObject.setModelOpponentSteals(backTestResult.isModelOpponentSteals());
        backTestIngestObject.setModelOpponentBlocks(backTestResult.isModelOpponentBlocks());
        backTestIngestObject.setGameTimeThreshold(backTestResult.getGameTimeThreshold());
        backTestIngestObject.setPointThreshold(backTestResult.getPointThreshold());
        backTestIngestObject.setHomeTeamAdvantage(backTestResult.getHomeTeamAdvantage());
        backTestIngestObject.setSquareRootTotalPoints(backTestResult.isSquareRootTotal());
        backTestIngestObject.setTestingPair(backTestResult.getTestingPair());
        backTestIngestObject.setOriginalResult(backTestResult);
        return backTestIngestObject;
    }
    public BackTestIngestObject findBestResultForGivenLocalDate(BackTestResult backTestResult, LocalDate localDate){
        ForwardPredictionThread backTestSingleThread = new ForwardPredictionThread(GameOddsHashMap, games, 0);
        backTestSingleThread.setBackTestIngestObjects(new ArrayList<>());
        backTestSingleThread.setForward(false);
        backTestSingleThread.setStartingMoney(100);
        backTestSingleThread.setConfidence(0);
        backTestSingleThread.setBetPercent(0.02);
        BackTestIngestObject initialIngestObject = convertResultBackToIngestObject(backTestResult);
        BackTestResult bestBackTestPredictable = backTestSingleThread.runBackTestVerbose(convertResultBackToIngestObject(backTestResult));
        BackTestResult bestResult = bestBackTestPredictable;
        double upperBounds = 5.0;
        double lowerBounds = -5.0;
        double minIncrement = 0.05;
        double startingIncrement = 1;
        //Target Optimal High Block
        double currentIncrement = startingIncrement;
        while(currentIncrement > minIncrement){
            BackTestIngestObject upperTestObject = ((BackTestIngestObject) initialIngestObject.clone());
            upperTestObject.setHighBlockPointFactor(upperTestObject.getHighBlockPointFactor() + currentIncrement);
            BackTestIngestObject lowerTestObject = ((BackTestIngestObject) initialIngestObject.clone());
            lowerTestObject.setHighBlockPointFactor(upperTestObject.getHighBlockPointFactor() - currentIncrement);
            BackTestResult upperResult = backTestSingleThread.runBackTest(upperTestObject);
            BackTestResult lowerResult = backTestSingleThread.runBackTest(lowerTestObject);
        }


        return null;
    }

    public List<BackTestIngestObject> getNewIngestObjectSet(BackTestResult backTestResult,LocalDate originalDate){
        Integer[] gameLookbackList = {6};
        double[] pointPerBlockList = {2};
        double[] pointPerStealList = {2};
        double pointPerTurnover = 2;
        double pointPerRebound = 2;
        double pointPerFoul = 1;
        double[] highBlockFactorList = {backTestResult.getHighBlockPointFactor() - highBlockIncrement,backTestResult.getHighBlockPointFactor(),backTestResult.getHighBlockPointFactor() + highBlockIncrement};
        double[] lowBlockFactorList = {backTestResult.getLowerBlockPointFactor() - lowBlockIncrement,backTestResult.getLowerBlockPointFactor(),backTestResult.getLowerBlockPointFactor() + lowBlockIncrement};
        double[] highStealFactorList = {backTestResult.getHighStealPointFactor() - highStealIncrement,backTestResult.getHighStealPointFactor(),backTestResult.getHighStealPointFactor() + highStealIncrement};
        double[] lowStealFactorList = {backTestResult.getLowerStealPointFactor() - lowStealIncrement,backTestResult.getLowerStealPointFactor(),backTestResult.getLowerStealPointFactor() + lowStealIncrement};
        double[] highTurnoverFactorList = {backTestResult.getHighTurnoverPointFactor() - highTurnoverIncrement,backTestResult.getHighTurnoverPointFactor(),backTestResult.getHighTurnoverPointFactor() + highTurnoverIncrement};
        double[] lowTurnoverlFactorList = {backTestResult.getLowerTurnoverPointFactor() - lowTurnoverIncrement,backTestResult.getLowerTurnoverPointFactor(),backTestResult.getLowerTurnoverPointFactor() + lowTurnoverIncrement};
        double[] highReboundFactorList = {backTestResult.getHighReboundPointFactor() - highReboundIncrement,backTestResult.getHighReboundPointFactor(),backTestResult.getHighReboundPointFactor() + highReboundIncrement};
        double[] lowReboundFactorList = {backTestResult.getLowerReboundPointFactor() - lowReboundIncrement,backTestResult.getLowerReboundPointFactor(),backTestResult.getLowerReboundPointFactor() + lowReboundIncrement};
        double[] highFoulFactorList = {backTestResult.getHighFoulPointFactor() - highFoulIncrement,backTestResult.getHighFoulPointFactor(),backTestResult.getHighFoulPointFactor() + highFoulIncrement};
        double[] lowFoulFactorList = {backTestResult.getLowerFoulPointFactor() - lowFoulIncrement,backTestResult.getLowerFoulPointFactor(),backTestResult.getLowerFoulPointFactor() + lowFoulIncrement};
        double[] gameTimeList = {0};
        double[] homeTeamAdvantages = {-2,-1,0};
        double[] pointThresholds = {0};
        boolean[] allowBelowZeros = {true};
        boolean[] doubleSquareRoots = {true};
        boolean[] totalSquareRoots = {true};
        List<BackTestIngestObject> backTestIngestObjects = new ArrayList<>();
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
                                                for (double pointThreshold : pointThresholds) {
                                                    for (boolean allowBelowZero : allowBelowZeros) {
                                                        for (double homeTeamAdvantage : homeTeamAdvantages) {
                                                            for (double highTurnover : highTurnoverFactorList) {
                                                                for (double lowTurnover : lowTurnoverlFactorList) {
                                                                    for (double highRebound : highReboundFactorList) {
                                                                        for (double lowRebound : lowReboundFactorList) {
                                                                            for (double highFoul : highFoulFactorList) {
                                                                                for (double lowFoul : lowFoulFactorList) {
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
                                                                                    backTestIngestObject.setBetType(backTestResult.getBetType());
                                                                                    backTestIngestObject.setStartDate(originalDate);
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
                                                                                    backTestIngestObject.setGamesToTest(backTestResult.getGamesToTest());
                                                                                    backTestIngestObject.setPointThreshold(pointThreshold);
                                                                                    backTestIngestObject.setAllowBelowZero(allowBelowZero);
                                                                                    backTestIngestObject.setHomeTeamAdvantage(homeTeamAdvantage);
                                                                                    backTestIngestObject.setTestingPair(backTestResult.getTestingPair());
                                                                                    backTestIngestObjects.add(backTestIngestObject);
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
        return backTestIngestObjects;
    }
    public int getGameCountOnDate(GameFinder gameFinder, LocalDate localDate){
        return  gameFinder.findGamesOnDateFromDB(localDate).size();
    }
}
