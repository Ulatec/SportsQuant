package SportsQuant;

import SportsQuant.Model.*;
import SportsQuant.Threads.ForwardPredictionThread;
import SportsQuant.Threads.ForwardPredictionThreadMonitor;
import SportsQuant.Util.CSVExporter;
import SportsQuant.Util.GameFinder;
import SportsQuant.Util.ListSplitter;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class CorrelationTesting extends Thread{

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
    public CorrelationTesting(List<BackTestIngestObject> backTestIngestObjects, HashMap<Integer, GameOdds> GameOddsHashMap, List<Game> games){
        this.initialBackTestIngestObjects = backTestIngestObjects;
        this.GameOddsHashMap = GameOddsHashMap;
        this.games = games;
    }
    public void run(){
        GameFinder gameFinder = new GameFinder();
        gameFinder.setGames(games);
        int[] lookBacksToTest = new int[]{50};
        double[] pointThresholds = new double[]{0};
        double[] betPercents = new double[]{0.10};
        //int durationOfDays = 960;
        int durationOfDays = 1;
        int threads = 16;
        LocalDate originalDate = initialBackTestIngestObjects.get(0).getStartDate();
        //int runs = initialBackTestIngestObjects.size() * lookBacksToTest.length * durationOfDays;
        //BackTestSingleThread backTestSingleThread = new BackTestSingleThread(runs,mlbGameOddsHashMap, games);
        double startingMoney = 2000;
        HashMap<TestingPair, List<Double>> PercentageMap = new HashMap<>();
        HashMap<TestingPair, Double> moneyMap = new HashMap<>();
        HashMap<LocalDate, HashMap<BackTestResult, BackTestResult>> resultMap = new HashMap<>();
        for(int lookback : lookBacksToTest){
            for(double pointThreshold : pointThresholds) {
                for(double betPercent : betPercents) {
                    TestingPair testingPair = new TestingPair();
                    testingPair.setGameLookBack(lookback);
                    testingPair.setPointThreshold(pointThreshold);
                    testingPair.setBetPercent(betPercent);
                    moneyMap.put(testingPair, startingMoney);
                    PercentageMap.put(testingPair,new ArrayList<>());
                }
            }
        }

        HashMap<TestingPair, List<BackTestResult>> resultsMap = new HashMap<>();
        List<ForwardPredictionThread> threadsList = new ArrayList<>();
        for(int i =0; i<threads; i++){
            ForwardPredictionThread forwardPredictionThread = new ForwardPredictionThread(GameOddsHashMap,games, i);
            threadsList.add(forwardPredictionThread);
            forwardPredictionThread.start();
        }
        int daysTested = 0;
        while(daysTested<durationOfDays){
            LocalDate testDate = originalDate.minusDays(daysTested);
        if(gameFinder.findGamesOnDateFromDB(testDate.plusDays(1)).size() > 0) {
            List<BackTestIngestObject> objectsToTest = new ArrayList<>();
//                if(daysTested>0){
//                    for(Map.Entry<TestingPair, List<BackTestResult>> entry : resultsMap.entrySet()){
//                        //List<BackTestIngestObject> objectsToAddToList = new ArrayList<>();
//                        BackTestResult mostRecentResult = entry.getValue().get(entry.getValue().size() -1);
//                        objectsToTest = getNewIngestObjectSet(mostRecentResult,originalDate);
//                    }
//                }else{
            LocalDate correlationWindow = testDate;
            int correlationGameCount = 150;
            int gamesCounted = 0;
            while (gamesCounted <= correlationGameCount) {
                int games = getGameCountOnDate(gameFinder, correlationWindow);
                correlationWindow = correlationWindow.minusDays(1);
                gamesCounted = gamesCounted + games;
            }
            objectsToTest = initialBackTestIngestObjects;
            //}
            List<BackTestIngestObject> clonedIngestObjects = new ArrayList<>();
            for (BackTestIngestObject backTestIngestObject : objectsToTest) {
                for (int lookBack : lookBacksToTest) {
                    for (double pointThreshold : pointThresholds) {
                        BackTestIngestObject clone = (BackTestIngestObject) backTestIngestObject.clone();
                        clone.setGamesToTest(lookBack);
                        clone.setPointThreshold(pointThreshold);
                        clone.setStartDate(correlationWindow);
                        clonedIngestObjects.add(clone);
                    }
                    //BackTestResult result = backTestSingleThread.runBackTest(clone);
                    //dailyResults.add(result);
                }
            }
            List<List<BackTestIngestObject>> listOfLists = ListSplitter.split(clonedIngestObjects, threads);
            listOfLists.removeIf(list -> list.size() == 0);
            ForwardPredictionThreadMonitor forwardPredictionThreadMonitor = new ForwardPredictionThreadMonitor(listOfLists.size());
            forwardPredictionThreadMonitor.start();
            for (int i = 0; i < listOfLists.size(); i++) {
                threadsList.get(i).setForward(false);
                threadsList.get(i).setThreadMonitor(forwardPredictionThreadMonitor);
                threadsList.get(i).setBackTestIngestObjects(listOfLists.get(i));
            }
            while (forwardPredictionThreadMonitor.getBackTestResults() == null) {

            }
            List<BackTestResult> results = forwardPredictionThreadMonitor.getBackTestResults();
            List<BackTestResult> pastResults = new ArrayList<>(results);
            List<BackTestIngestObject> forwardIngestObjects = new ArrayList<>();
            for (BackTestResult pastResult : pastResults) {
                BackTestIngestObject backTestIngestObject = convertResultBackToIngestObject(pastResult);
                backTestIngestObject.setStartDate(correlationWindow);
                backTestIngestObject.setGamesToTest(25);
                backTestIngestObject.setPointThreshold(0);
                forwardIngestObjects.add(backTestIngestObject);
            }
            List<List<BackTestIngestObject>> listOfNewLists = ListSplitter.split(forwardIngestObjects, threads);
            listOfNewLists.removeIf(list -> list.size() == 0);

            ForwardPredictionThreadMonitor correlationPredictionThreadMonitor = new ForwardPredictionThreadMonitor(listOfNewLists.size());
            correlationPredictionThreadMonitor.start();
            for (int i = 0; i < listOfNewLists.size(); i++) {
                threadsList.get(i).setForward(true);
                threadsList.get(i).setThreadMonitor(correlationPredictionThreadMonitor);
                threadsList.get(i).setBackTestIngestObjects(listOfNewLists.get(i));
            }
            while (correlationPredictionThreadMonitor.getBackTestResults() == null) {
            }
            List<BackTestResult> correlationResults = correlationPredictionThreadMonitor.getBackTestResults();

            List<BackTestResult> results1 = new ArrayList<>(correlationResults);
            List<BackTestIngestObject> testDateIngestObjects = new ArrayList<>();
            for (BackTestResult correlationResult : results1) {
                BackTestIngestObject backTestIngestObject = convertResultBackToIngestObject(correlationResult);
                backTestIngestObject.setStartDate(testDate.plusDays(1));
                backTestIngestObject.setGamesToTest(1);
                backTestIngestObject.setPointThreshold(0);
                testDateIngestObjects.add(backTestIngestObject);
            }
            List<List<BackTestIngestObject>> listOfFinalLists = ListSplitter.split(testDateIngestObjects, threads);
            listOfNewLists.removeIf(list -> list.size() == 0);
            ForwardPredictionThreadMonitor finalPredictionThreadMonitor = new ForwardPredictionThreadMonitor(listOfNewLists.size());
            finalPredictionThreadMonitor.start();
            for (int i = 0; i < listOfFinalLists.size(); i++) {
                threadsList.get(i).setForward(true);
                threadsList.get(i).setThreadMonitor(finalPredictionThreadMonitor);
                threadsList.get(i).setBackTestIngestObjects(listOfFinalLists.get(i));
            }
            while (finalPredictionThreadMonitor.getBackTestResults() == null) {

            }
            List<BackTestResult> finalResults = finalPredictionThreadMonitor.getBackTestResults();

            List<BackTestResult> finalResultsSafe = new ArrayList<>(finalResults);
            for (BackTestResult backTestResult : finalResultsSafe) {
                if (resultMap.get(testDate.plusDays(1)) != null) {
                    resultMap.get(testDate.plusDays(1)).put(backTestResult.getOriginalResult(), backTestResult);
                } else {
                    HashMap<BackTestResult, BackTestResult> entry = new HashMap<>();
                    entry.put(backTestResult.getOriginalResult(), backTestResult);
                    resultMap.put(testDate.plusDays(1), entry);
                }
            }
            daysTested = daysTested + 1;
        }
//            for (int lookBack : lookBacksToTest) {
//                for (double pointThreshold : pointThresholds) {
//                    for (double betPercent : betPercents) {
//                        try {
//
//                            pastResults = pastResults.parallelStream().filter(result -> result.getGamesToTest() == lookBack)
//                                    .filter(result -> result.getPointThreshold() == pointThreshold)
////                                        .filter(result -> result.getPointDelta() < result.getActualPoints() * 1.1 &&
////                                                result.getPointDelta() > result.getActualPoints() * 0.9)
//                                    .sorted(Comparator.comparingDouble(BackTestResult::getCorrectPercent)
//                                            .reversed()).collect(Collectors.toCollection(ArrayList::new));
//                            BackTestResult highestPercent = pastResults.get(0);
//                            List<BackTestResult> filtered = pastResults.parallelStream().filter(result -> result.getCorrectPercent() == highestPercent.getCorrectPercent())
//                                    .sorted(Comparator.comparingDouble(BackTestResult::getPointDelta)).collect(Collectors.toList());
//                            List<BackTestIngestObject> forwardIngestObjects = new ArrayList<>();
//                            for (BackTestResult pastResult : pastResults) {
//                                BackTestIngestObject backTestIngestObject = convertResultBackToIngestObject(pastResult);
//                                backTestIngestObject.setStartDate(originalDate.plusDays(1));
//                                backTestIngestObject.setGamesToTest(1);
//                                backTestIngestObject.setPointThreshold(pointThreshold);
//                                forwardIngestObjects.add(backTestIngestObject);
//                            }
//
//
//
//                            double[] a = old.stream().mapToDouble(Double::doubleValue).toArray();
//                            double[] b = future.stream().mapToDouble(Double::doubleValue).toArray();
//                            double correlation = new PearsonsCorrelation().correlation(a, b);
//                            System.out.println("Correlation is " + correlation);
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
        }
        for(Map.Entry<TestingPair, List<BackTestResult>> entry : resultsMap.entrySet()){
            HashMap<String, HashMap<Double,Integer>> parameterCountMap = new HashMap<>();
            parameterCountMap.put("highBlock", new HashMap<>());
            parameterCountMap.put("lowBlock", new HashMap<>());
            int totalCorrect=0;
            int totalIncorrect =0;
            for(BackTestResult backTestResult : entry.getValue()){
                totalIncorrect = totalIncorrect + backTestResult.getPredictIncorrect();
                totalCorrect = totalCorrect + backTestResult.getPredictCorrect();
            }

            System.out.println(entry.getKey() + " : " + (double)totalCorrect/(double)(totalCorrect + totalIncorrect) + " " + entry.getValue().get(entry.getValue().size() - 1).getEndingMoney());
            for(BackTestResult result : entry.getValue()){
                if(parameterCountMap.get("highBlock").get(result.getHighBlockPointFactor()) != null){
                    parameterCountMap.get("highBlock").put(result.getHighBlockPointFactor(),parameterCountMap.get("highBlock").get(result.getHighBlockPointFactor()) + 1);
                }else{
                    parameterCountMap.get("highBlock").put(result.getHighBlockPointFactor(),1);
                }
                if(parameterCountMap.get("lowBlock").get(result.getLowerBlockPointFactor()) != null){
                    parameterCountMap.get("lowBlock").put(result.getLowerBlockPointFactor(),parameterCountMap.get("lowBlock").get(result.getLowerBlockPointFactor()) + 1);
                }else{
                    parameterCountMap.get("lowBlock").put(result.getLowerBlockPointFactor(),1);
                }

                System.out.println(result);
            }
            System.out.println("##############################################");
            // System.out.println(parameterCountMap);
            CSVExporter csvExporter = new CSVExporter();
            csvExporter.writeTimeAccuracyResults(entry.getValue());
        }
        CSVExporter csvExporter = new CSVExporter();
        csvExporter.writeCoorelationResults(resultMap);
        //System.out.println(bestResultList);
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
