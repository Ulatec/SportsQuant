package SportsQuant;



import SportsQuant.Model.*;
import SportsQuant.Threads.ForwardPredictionThread;
import SportsQuant.Threads.ForwardPredictionThreadMonitor;
import SportsQuant.Util.CSVExporter;
import SportsQuant.Util.GameFinder;
import SportsQuant.Util.ListSplitter;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class BackTestTimeAccuracy extends Thread{

    HashMap<Integer, GameOdds> GameOddsHashMap;
    List<BackTestIngestObject> initialBackTestIngestObjects;
    List<Game> games;
    private int playerLookBackIncrement = 1;
    private double highReboundIncrement = 1;
    private double lowReboundIncrement = 1;
    private double highBlockIncrement = 1;
    private double lowBlockIncrement = 1;
    private double highStealIncrement = 1;
    private double lowStealIncrement = 1;
    private double highTurnoverIncrement = 1;
    private double lowTurnoverIncrement = 1;
    private double highFoulIncrement = 1;
    private double lowFoulIncrement = 1;
    private double homeAdvantageIncrement = 1;

    private double lookbackCorrelation = 0.0;
    private double highReboundCorrelation = 0.0;
    private double lowReboundCorrelation = 0.0;
    private double highBlockCorrelation = 0.0;
    private double lowBlockCorrelation = 0.0;
    private double highStealCorrelation = 0.0;
    private double lowStealCorrelation = 0.0;
    private double highTurnoverCorrelation = 0.0;
    private double lowTurnoverCorrelation = 0.0;
    private double highFoulCorrelation = 0.0;
    private double lowFoulCorrelation = 0.0;
    private double homeAdvantageCorrelation = 0.0;

    private String mostRecentAdjustment = null;
    private List<String> blackList = null;
    List<String> antiStuckStrings = null;
    boolean stuck = false;
    private HashMap<String, Integer> modificationCountMap;
    public BackTestTimeAccuracy(List<BackTestIngestObject> backTestIngestObjects, HashMap<Integer, GameOdds> GameOddsHashMap, List<Game> games){
        this.initialBackTestIngestObjects = backTestIngestObjects;
        this.GameOddsHashMap = GameOddsHashMap;
        this.games = games;
    }

    public void run(){
        List<Double> correlationList = new ArrayList<>();
        correlationList.add(lookbackCorrelation);
        correlationList.add(highReboundCorrelation);
        correlationList.add(lowReboundCorrelation);
        correlationList.add(highBlockCorrelation);
        correlationList.add(lowBlockCorrelation);
        correlationList.add(highStealCorrelation);
        correlationList.add(lowStealCorrelation);
        correlationList.add(highTurnoverCorrelation);
        correlationList.add(lowTurnoverCorrelation);
        correlationList.add(highFoulCorrelation);
        correlationList.add(lowFoulCorrelation);
        correlationList.add(homeAdvantageCorrelation);
        GameFinder gameFinder = new GameFinder();
        gameFinder.setGames(games);
        int[] lookBacksToTest = new int[]{50};
        double[] pointThresholds = new double[]{0};
        double[] betPercents = new double[]{0.10};
        int durationOfDays = 900;
        //int durationOfDays = 53;
        int threads = 12;
        double correlationThreshold= -1;
        double averageThreshold = 0;
        LocalDate originalDate = initialBackTestIngestObjects.get(0).getStartDate();
        int runs = initialBackTestIngestObjects.size() * lookBacksToTest.length * durationOfDays;
        //BackTestSingleThread backTestSingleThread = new BackTestSingleThread(runs,mlbGameOddsHashMap, games);
        double startingMoney = 2000;
        HashMap<TestingPair, List<Double>> PercentageMap = new HashMap<>();
        HashMap<TestingPair, Double> moneyMap = new HashMap<>();
        HashMap<LocalDate, HashMap<BackTestResult, BackTestResult>> resultMap = new HashMap<>();
        List<Double> originalCoorelations = new ArrayList<>();
        List<Double> oldAverages = new ArrayList<>();
        List<Double> originalAverages = new ArrayList<>();
        List<Double> selectedAverage = new ArrayList<>();
        List<Double> selectedLowTurnovers = new ArrayList<>();
        List<Double> selectedLowRebounds = new ArrayList<>();
        List<Double> selectedHomeTeamAdvantage = new ArrayList();
        List<Double> selectedLookback = new ArrayList<>();
        List<Double> outputs = new ArrayList<>();
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

        while(daysTested<durationOfDays) {
            List<BackTestIngestObject> modifiedIngestList = null;
            LocalDate testDate = originalDate.plusDays(1 + (1L * daysTested));
            boolean inverse = false;
            boolean correlationComplete = false;
            boolean averageComplete = false;
            List<Game> gamesToVerify = gameFinder.findGamesOnDateFromDB(testDate);
            boolean safeToRunDay = true;
            for(Game game : gamesToVerify){
                if(GameOddsHashMap.get(game.getGameId()) == null){
                    safeToRunDay = false;
                }
            }
                //List<BackTestResult> dailyResults = new ArrayList<>();
            if(gameFinder.findGamesOnDateFromDB(testDate).size() > 0 && safeToRunDay) {
                mostRecentAdjustment = null;
                double oldToNewCorrelation = 0.0;
                List<BackTestResult> savedCorrelationResults = new ArrayList<>();
                List<BackTestResult> rollBackList = null;
                modificationCountMap = new HashMap<>();
                modificationCountMap.put("lookback", 0);
                modificationCountMap.put("highBlock", 0);
                modificationCountMap.put("lowBlock", 0);
                modificationCountMap.put("highSteal", 0);
                modificationCountMap.put("lowSteal", 0);
                modificationCountMap.put("highTurnover", 0);
                modificationCountMap.put("lowTurnover", 0);
                modificationCountMap.put("highRebound", 0);
                modificationCountMap.put("lowRebound", 0);
                modificationCountMap.put("highFoul", 0);
                modificationCountMap.put("lowFoul", 0);
                modificationCountMap.put("homeAdvantage", 0);
                Double bestAverage = 0.0;
                double bestCorrelation = -1;
                //double highestCorrelation = 1;
                boolean rollbackFlag = false;
                boolean increaseComplete = false;
                boolean decreaseComplete = false;
                double endingCorrelation = 0.0;
                double endingOldAverage = 0.0;
                double endingAverage = 0.0;
                int rollbackCount = 0;
                while(true) {
                    //inverse = bestCorrelation>0.5;
                    LocalDate correlationWindow = testDate.minusDays(1);
                    int correlationGameCount = 25;
                    int gamesCounted = 0;
                    while (gamesCounted <= correlationGameCount) {
                        int games = getGameCountOnDate(gameFinder, correlationWindow);
                        correlationWindow = correlationWindow.minusDays(1);
                        gamesCounted = gamesCounted + games;
                    }
                    List<BackTestIngestObject> objectsToTest = new ArrayList<>();

                    if(modifiedIngestList !=null){
                        if(rollbackFlag){
                            objectsToTest = runCorrelations(rollBackList);
                        }else {
                            objectsToTest = modifiedIngestList;
                        }
                    }else{
                        objectsToTest = initialBackTestIngestObjects;
                    }

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
                    //List<List<BackTestIngestObject>> listOfAlteredLists = new ArrayList<>();
//                    List<BackTestIngestObject> increasedList = adjustRanges(clonedIngestObjects,true);
//                    List<BackTestIngestObject> decreasedList = adjustRanges(clonedIngestObjects,false);
//                    for(BackTestIngestObject backTestIngestObject : increasedList){
//                        for (int lookBack : lookBacksToTest) {
//                            backTestIngestObject.setGamesToTest(lookBack);
//                        }
//                    }
//                    for(BackTestIngestObject backTestIngestObject : decreasedList){
//                        for (int lookBack : lookBacksToTest) {
//                            backTestIngestObject.setGamesToTest(lookBack);
//                        }
//                    }
                    //listOfAlteredLists.add(clonedIngestObjects);
                   // listOfAlteredLists.add(increasedList);
                   // listOfAlteredLists.add(decreasedList);
                    //for(List<BackTestIngestObject> newList : listOfAlteredLists) {
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
                            backTestIngestObject.setGamesToTest(correlationGameCount);
                            backTestIngestObject.setPointThreshold(pastResult.getPointThreshold());
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
                        List<Double> old = new ArrayList<>();
                        List<Double> future = new ArrayList<>();
                        for (BackTestResult backTestResult : correlationResults) {
                            old.add(backTestResult.getOriginalResult().getCorrectPercent());
                            future.add(backTestResult.getCorrectPercent());
                        }

                        double[] a = old.stream().mapToDouble(Double::doubleValue).toArray();
                        double[] b = future.stream().mapToDouble(Double::doubleValue).toArray();
                        double oldAverage = old.stream().mapToDouble(Double::doubleValue).summaryStatistics().getAverage();
                        double forwardAverage = future.stream().mapToDouble(Double::doubleValue).summaryStatistics().getAverage();
                        double correlation = new PearsonsCorrelation().correlation(a, b);
                        System.out.println("Completed Correlation is " + correlation);
                        System.out.println("forward average = " + forwardAverage);
                        System.out.println("old average = " + oldAverage);
                        oldToNewCorrelation = correlation;


                        if (correlation > correlationThreshold) {
                            correlationComplete = true;
                        } else {
                            correlationComplete = false;
                        }
                        if (forwardAverage > averageThreshold) {
                            averageComplete = true;
                        } else {
                            averageComplete = false;
                        }


//                    if(){
//                        originalAverages.add(forwardAverage);
//                        originalCoorelations.add(correlation);
//                        setCorrelations(correlationResults);
//                        modifiedIngestList = forwardIngestObjects;
//                        break;
//                    }
                       //if(correlation>0.2){
                        //    endingCorrelation = correlation;
                       //     endingAverage = forwardAverage;
                        //    endingOldAverage = oldAverage;
                        //    setCorrelations(correlationResults);
                        //    modifiedIngestList = forwardIngestObjects;
                         //   savedCorrelationResults = correlationResults;
                        //    break;
                        //}else{
//                            if(correlation>bestCorrelation){
//                                endingCorrelation = correlation;
//                                endingAverage = forwardAverage;
//                                endingOldAverage = oldAverage;
//                                setCorrelations(correlationResults);
//                                modifiedIngestList = forwardIngestObjects;
//                                savedCorrelationResults = correlationResults;
//                            }
                        //}


                        if (correlation > correlationThreshold && forwardAverage > averageThreshold) {
                            endingCorrelation = correlation;
                            endingAverage = forwardAverage;
                            endingOldAverage = oldAverage;
                            setCorrelations(correlationResults);
                            modifiedIngestList = forwardIngestObjects;
                            savedCorrelationResults = correlationResults;
                            break;
                        }

                        if (!correlationComplete) {
                            if (correlation <= bestCorrelation) {
                                if (rollBackList != null) {
                                    if (blackList.size() > 10) {
                                        if (rollbackCount > 3) {
                                            stuck = true;
                                            System.out.println("stuck=true");
                                        }
                                        blackList = new ArrayList<>();
                                        //modifiedIngestList = runCorrelations(correlationResults);
                                        bestCorrelation = -1;
                                        rollbackFlag = true;
                                        System.out.println("Rollback reset.");
                                        rollbackCount = rollbackCount + 1;
                                        //blackList.add(mostRecentAdjustment);
                                        mostRecentAdjustment = null;

                                    } else {

                                        blackList.add(mostRecentAdjustment);
                                        rollbackFlag = true;
                                    }
                                    System.out.println("Rolling back. Next test should not modify " + mostRecentAdjustment);
                                } else {
                                    modifiedIngestList = runCorrelations(correlationResults);
                                }
                            } else {
                                rollBackList = correlationResults;
                                rollbackFlag = false;
                                blackList = new ArrayList<>();
                                bestCorrelation = correlation;
                                //highestCorrelation = correlation;
                                modifiedIngestList = runCorrelations(correlationResults);
                                stuck = false;
                            }

                        } else if (!averageComplete) {
                            if (forwardAverage <= bestAverage) {
                                if (rollBackList != null) {
                                    if (blackList.size() > 10) {
                                        blackList = new ArrayList<>();
                                        //modifiedIngestList = runCorrelations(correlationResults);
                                        bestAverage = 0.0;
                                        rollbackFlag = false;
                                        System.out.println("Rollback reset.");
                                        mostRecentAdjustment = null;
                                    } else {
                                        blackList.add(mostRecentAdjustment);
                                        rollbackFlag = true;
                                    }
                                    System.out.println("Rolling back. Next test should not modify " + mostRecentAdjustment);


                                    //modifiedIngestList = rollBackList;
                                    //modifiedIngestList = runCorrelations(rollBackList);
                                } else {
                                    modifiedIngestList = runCorrelations(correlationResults);
                                }
                            } else {
                                antiStuckStrings = new ArrayList<>();
                                rollBackList = correlationResults;
                                rollbackFlag = false;
                                blackList = new ArrayList<>();
                                bestAverage = forwardAverage;
                                modifiedIngestList = runCorrelations(correlationResults);
                            }
                        }
                    //}
//                    if(Double.isNaN(correlation)){
//                        modifiedIngestList = null;
//                    }else {
//                        modifiedIngestList = runCorrelations(correlationResults);
//                    }
                }

                //List<BackTestIngestObject> modifiedIngestList = initialBackTestIngestObjects;
//                for(BackTestIngestObject backTestIngestObject : modifiedIngestList){
//                    backTestIngestObject.setStartDate(testDate.minusDays(1));
//                    backTestIngestObject.setGamesToTest(800);
//                }
//                List<List<BackTestIngestObject>> listOfLists = ListSplitter.split(modifiedIngestList, threads);
//                listOfLists.removeIf(list -> list.size() == 0);
//                ForwardPredictionThreadMonitor forwardPredictionThreadMonitor = new ForwardPredictionThreadMonitor(listOfLists.size());
//                forwardPredictionThreadMonitor.start();
//                for (int i = 0; i < listOfLists.size(); i++) {
//                    threadsList.get(i).setForward(false);
//                    threadsList.get(i).setThreadMonitor(forwardPredictionThreadMonitor);
//                    threadsList.get(i).setBackTestIngestObjects(listOfLists.get(i));
//                }
//                while (forwardPredictionThreadMonitor.getBackTestResults() == null) {}
//                List<BackTestResult> results = forwardPredictionThreadMonitor.getBackTestResults();
                List<BackTestResult> pastResults = new ArrayList<>(savedCorrelationResults);
                for (int lookBack : lookBacksToTest) {
                    for (double pointThreshold : pointThresholds) {
                        for (double betPercent : betPercents) {
                            try {

                                pastResults = pastResults.parallelStream()
                                        .sorted(Comparator.comparingDouble(BackTestResult::getCorrectPercent)
                                                .reversed()).collect(Collectors.toCollection(ArrayList::new));
                                HashSet<Integer> lookback = new HashSet<>();
                                HashSet<Double> highBlock = new HashSet<>();
                                HashSet<Double> lowBlock = new HashSet<>();
                                HashSet<Double> highSteal = new HashSet<>();
                                HashSet<Double> lowSteal = new HashSet<>();
                                HashSet<Double> highTurnover = new HashSet<>();
                                HashSet<Double> lowTurnover = new HashSet<>();
                                HashSet<Double> highRebound = new HashSet<>();
                                HashSet<Double> lowRebound = new HashSet<>();
                                HashSet<Double> highFoul = new HashSet<>();
                                HashSet<Double> lowFoul = new HashSet<>();
                                HashSet<Double> homeAdvantage = new HashSet<>();
                                DescriptiveStatistics lookbackStats = new DescriptiveStatistics();
                                DescriptiveStatistics highBlockStats = new DescriptiveStatistics();
                                DescriptiveStatistics lowBlockStats = new DescriptiveStatistics();
                                DescriptiveStatistics highStealStats = new DescriptiveStatistics();
                                DescriptiveStatistics lowStealStats = new DescriptiveStatistics();
                                DescriptiveStatistics highTurnoverStats = new DescriptiveStatistics();
                                DescriptiveStatistics lowTurnoverStats = new DescriptiveStatistics();
                                DescriptiveStatistics highReboundStats = new DescriptiveStatistics();
                                DescriptiveStatistics lowReboundStats = new DescriptiveStatistics();
                                DescriptiveStatistics highFoulStats = new DescriptiveStatistics();
                                DescriptiveStatistics lowFoulStats = new DescriptiveStatistics();
                                DescriptiveStatistics homeAdvantageStats = new DescriptiveStatistics();
                                for(BackTestResult result : pastResults){
                                    lookback.add(result.getPlayerGameLookBack());
                                    highBlock.add(result.getHighBlockPointFactor());
                                    lowBlock.add(result.getLowerBlockPointFactor());
                                    highSteal.add(result.getHighStealPointFactor());
                                    lowSteal.add(result.getLowerStealPointFactor());
                                    highTurnover.add(result.getHighStealPointFactor());
                                    lowTurnover.add(result.getLowerStealPointFactor());
                                    highRebound.add(result.getHighReboundPointFactor());
                                    lowRebound.add(result.getLowerReboundPointFactor());
                                    highFoul.add(result.getHighFoulPointFactor());
                                    lowFoul.add(result.getLowerFoulPointFactor());
                                    homeAdvantage.add(result.getHomeTeamAdvantage());
                                }
                                for(Integer num : lookback){
                                    lookbackStats.addValue(num);
                                }
                                for(Double num : highBlock){
                                    highBlockStats.addValue(num);
                                }
                                for(Double num : lowBlock){
                                    lowBlockStats.addValue(num);
                                }
                                for(Double num : highSteal){
                                    highStealStats.addValue(num);
                                }
                                for(Double num : lowSteal){
                                    lowStealStats.addValue(num);
                                }
                                for(Double num : highTurnover){
                                    highTurnoverStats.addValue(num);
                                }
                                for(Double num : lowTurnover){
                                    lowTurnoverStats.addValue(num);
                                }
                                for(Double num : highRebound){
                                    highReboundStats.addValue(num);
                                }
                                for(Double num : lowRebound){
                                    lowReboundStats.addValue(num);
                                }
                                for(Double num : highFoul){
                                    highFoulStats.addValue(num);
                                }
                                for(Double num : lowFoul){
                                    lowFoulStats.addValue(num);
                                }
                                for(Double num : homeAdvantage){
                                    homeAdvantageStats.addValue(num);
                                }
                                Integer lookbackValue;
                                Double highblockValue = 0.0;
                                Double lowblockValue = 0.0;
                                Double highStealValue = 0.0;
                                Double lowStealValue = 0.0;
                                Double highTurnoverValue = 0.0;
                                Double lowTurnoverValue = 0.0;
                                Double highReboundValue = 0.0;
                                Double lowReboundValue = 0.0;
                                Double highFoulValue = 0.0;
                                Double lowFoulValue = 0.0;
                                Double homeAdvantageValue = 0.0;
                                if(endingCorrelation<-1){
                                    lookbackCorrelation = lookbackCorrelation * -1;
                                    highBlockCorrelation = highBlockCorrelation * -1;
                                    lowBlockCorrelation = lowBlockCorrelation * -1;
                                    highStealCorrelation = highStealCorrelation * -1;
                                    lowStealCorrelation = lowStealCorrelation * -1;
                                    highTurnoverCorrelation = highTurnoverCorrelation * -1;
                                    lowTurnoverCorrelation = lowTurnoverCorrelation* -1;
                                    highReboundCorrelation = highReboundCorrelation * -1;
                                    lowReboundCorrelation = lowReboundCorrelation * -1;
                                    highFoulCorrelation = highFoulCorrelation * -1;
                                    lowFoulCorrelation= lowFoulCorrelation * -1;
                                    homeAdvantageCorrelation = homeAdvantageCorrelation * -1;
                                }
                                double correlationRequirement = 0.10;
                                    if (lookbackCorrelation < 0) {
                                        lookbackValue = pastResults.parallelStream().min(Comparator.comparing(BackTestResult::getPlayerGameLookBack)).get().getPlayerGameLookBack();
                                    } else {
                                        lookbackValue = pastResults.parallelStream().max(Comparator.comparing(BackTestResult::getPlayerGameLookBack)).get().getPlayerGameLookBack();
                                    }

                                if(highBlockCorrelation>correlationRequirement || highBlockCorrelation<correlationRequirement*-1) {
                                    if (highBlockCorrelation < 0) {
                                        highblockValue = pastResults.parallelStream().min(Comparator.comparing(BackTestResult::getHighBlockPointFactor)).get().getHighBlockPointFactor();
                                    } else {
                                        highblockValue = pastResults.parallelStream().max(Comparator.comparing(BackTestResult::getHighBlockPointFactor)).get().getHighBlockPointFactor();
                                    }
                                }else{
                                    highblockValue = highBlockStats.getPercentile(50);
                                }
                                if(lowBlockCorrelation>correlationRequirement || lowBlockCorrelation<correlationRequirement*-1) {
                                    if (lowBlockCorrelation < 0) {
                                        lowblockValue = pastResults.parallelStream().min(Comparator.comparing(BackTestResult::getLowerBlockPointFactor)).get().getLowerBlockPointFactor();
                                    } else {
                                        lowblockValue = pastResults.parallelStream().max(Comparator.comparing(BackTestResult::getLowerBlockPointFactor)).get().getLowerBlockPointFactor();
                                    }
                                }else{
                                    lowblockValue = lowBlockStats.getPercentile(50);
                                }
                                if(highStealCorrelation>correlationRequirement || highStealCorrelation<correlationRequirement*-1) {
                                    if (highStealCorrelation < 0) {
                                        highStealValue = pastResults.parallelStream().min(Comparator.comparing(BackTestResult::getHighStealPointFactor)).get().getHighStealPointFactor();
                                    } else {
                                        highStealValue = pastResults.parallelStream().max(Comparator.comparing(BackTestResult::getHighStealPointFactor)).get().getHighStealPointFactor();
                                    }
                                }else{
                                    highStealValue = highStealStats.getPercentile(50);
                                }
                                if(lowStealCorrelation>correlationRequirement || lowStealCorrelation<correlationRequirement*-1) {
                                    if (lowStealCorrelation < 0) {
                                        lowStealValue = pastResults.parallelStream().min(Comparator.comparing(BackTestResult::getLowerStealPointFactor)).get().getLowerStealPointFactor();
                                    } else {
                                        lowStealValue = pastResults.parallelStream().max(Comparator.comparing(BackTestResult::getLowerStealPointFactor)).get().getLowerStealPointFactor();
                                    }
                                }else{
                                    lowStealValue = lowStealStats.getPercentile(50);
                                }
                                if(highTurnoverCorrelation>correlationRequirement || highTurnoverCorrelation<correlationRequirement*-1) {
                                    if (highTurnoverCorrelation < 0) {
                                        highTurnoverValue = pastResults.parallelStream().min(Comparator.comparing(BackTestResult::getHighTurnoverPointFactor)).get().getHighTurnoverPointFactor();
                                    } else {
                                        highTurnoverValue = pastResults.parallelStream().max(Comparator.comparing(BackTestResult::getHighTurnoverPointFactor)).get().getHighTurnoverPointFactor();
                                    }
                                }else{
                                    highTurnoverValue = highTurnoverStats.getPercentile(50);
                                }
                                if(lowTurnoverCorrelation>correlationRequirement || lowTurnoverCorrelation<correlationRequirement*-1) {
                                    if (lowTurnoverCorrelation < 0) {
                                        lowTurnoverValue = pastResults.parallelStream().min(Comparator.comparing(BackTestResult::getLowerTurnoverPointFactor)).get().getLowerTurnoverPointFactor();
                                    } else {
                                        lowTurnoverValue = pastResults.parallelStream().max(Comparator.comparing(BackTestResult::getLowerTurnoverPointFactor)).get().getLowerTurnoverPointFactor();
                                    }
                                }else{
                                    lowTurnoverValue = lowTurnoverStats.getPercentile(50);
                                }
                                if(highReboundCorrelation>correlationRequirement || highReboundCorrelation<correlationRequirement*-1) {
                                    if (highReboundCorrelation < 0) {
                                        highReboundValue = pastResults.parallelStream().min(Comparator.comparing(BackTestResult::getHighReboundPointFactor)).get().getHighReboundPointFactor();
                                    } else {
                                        highReboundValue = pastResults.parallelStream().max(Comparator.comparing(BackTestResult::getHighReboundPointFactor)).get().getHighReboundPointFactor();
                                    }
                                }else{
                                    highReboundValue = highReboundStats.getPercentile(50);
                                }
                                if(lowReboundCorrelation>correlationRequirement || lowReboundCorrelation<correlationRequirement*-1) {
                                    if (lowReboundCorrelation < 0) {
                                        lowReboundValue = pastResults.parallelStream().min(Comparator.comparing(BackTestResult::getLowerReboundPointFactor)).get().getLowerReboundPointFactor();
                                    } else {
                                        lowReboundValue = pastResults.parallelStream().max(Comparator.comparing(BackTestResult::getLowerReboundPointFactor)).get().getLowerReboundPointFactor();
                                    }
                                }else{
                                    lowReboundValue = lowReboundStats.getPercentile(50);
                                }
                                if(highFoulCorrelation>correlationRequirement || highFoulCorrelation<correlationRequirement*-1) {
                                    if (highFoulCorrelation < 0) {
                                        highFoulValue = pastResults.parallelStream().min(Comparator.comparing(BackTestResult::getHighFoulPointFactor)).get().getHighFoulPointFactor();
                                    } else {
                                        highFoulValue = pastResults.parallelStream().max(Comparator.comparing(BackTestResult::getHighFoulPointFactor)).get().getHighFoulPointFactor();
                                    }
                                }else{
                                    highFoulValue = highFoulStats.getPercentile(50);
                                }
                                if(lowFoulCorrelation>correlationRequirement || lowFoulCorrelation<correlationRequirement*-1) {
                                    if (lowFoulCorrelation < 0) {
                                        lowFoulValue = pastResults.parallelStream().min(Comparator.comparing(BackTestResult::getLowerFoulPointFactor)).get().getLowerFoulPointFactor();
                                    } else {
                                        lowFoulValue = pastResults.parallelStream().max(Comparator.comparing(BackTestResult::getLowerFoulPointFactor)).get().getLowerFoulPointFactor();
                                    }
                                }else{
                                    lowFoulValue = lowFoulStats.getPercentile(50);
                                }
                                if(homeAdvantageCorrelation>correlationRequirement || homeAdvantageCorrelation<correlationRequirement*-1) {
                                    if (homeAdvantageCorrelation < 0) {
                                        homeAdvantageValue = pastResults.parallelStream().min(Comparator.comparing(BackTestResult::getHomeTeamAdvantage)).get().getHomeTeamAdvantage();
                                    } else {
                                        homeAdvantageValue = pastResults.parallelStream().max(Comparator.comparing(BackTestResult::getHomeTeamAdvantage)).get().getHomeTeamAdvantage();
                                    }
                                }else{
                                    homeAdvantageValue = homeAdvantageStats.getPercentile(50);
                                }

                                Double finalHighblockValue = highblockValue;Double finalLowblockValue = lowblockValue;Double finalHighStealValue = highStealValue;Double finalLowStealValue = lowStealValue;Double finalHighTurnoverValue = highTurnoverValue;Double finalLowTurnoverValue = lowTurnoverValue;Double finalHighReboundValue = highReboundValue;Double finalLowReboundValue = lowReboundValue;Double finalHighFoulValue = highFoulValue;Double finalLowFoulValue = lowFoulValue;Double finalHomeAdvantageValue = homeAdvantageValue;
                                pastResults = pastResults.parallelStream()
                                        .filter(result -> result.getPlayerGameLookBack() == lookbackValue)
                                        .filter(result -> result.getHighBlockPointFactor() == finalHighblockValue)
                                        .filter(result -> result.getLowerBlockPointFactor() == finalLowblockValue)
                                        .filter(result -> result.getHighStealPointFactor() == finalHighStealValue)
                                        .filter(result -> result.getLowerStealPointFactor() == finalLowStealValue)
                                        .filter(result -> result.getHighTurnoverPointFactor() == finalHighTurnoverValue)
                                        .filter(result -> result.getLowerTurnoverPointFactor() == finalLowTurnoverValue)
                                        .filter(result -> result.getHighReboundPointFactor() == finalHighReboundValue)
                                        .filter(result -> result.getLowerReboundPointFactor() == finalLowReboundValue)
                                        .filter(result -> result.getHighFoulPointFactor() == finalHighFoulValue)
                                        .filter(result -> result.getLowerFoulPointFactor() == finalLowFoulValue)
                                        .filter(result -> result.getHomeTeamAdvantage() == finalHomeAdvantageValue)
                                        .collect(Collectors.toCollection(ArrayList::new));
                                BackTestResult highestPercent;
                                if(pastResults.size()==0){
                                    highestPercent = new BackTestResult();
                                    highestPercent.setBetType(savedCorrelationResults.get(0).getBetType());
                                    highestPercent.setPointThreshold(savedCorrelationResults.get(0).getPointThreshold());
                                    highestPercent.setPlayerGameLookBack(savedCorrelationResults.get(0).getPlayerGameLookBack());
                                    highestPercent.setDoubleSquareRoot(savedCorrelationResults.get(0).isDoubleSquareRoot());
                                    highestPercent.setAllowBelowZero(savedCorrelationResults.get(0).isAllowBelowZero());
                                    highestPercent.setHighBlockPointFactor(highblockValue);
                                    highestPercent.setLowerBlockPointFactor(lowblockValue);
                                    highestPercent.setHighStealPointFactor(highStealValue);
                                    highestPercent.setLowerStealPointFactor(lowStealValue);
                                    highestPercent.setHighTurnoverPointFactor(highTurnoverValue);
                                    highestPercent.setLowerTurnoverPointFactor(lowTurnoverValue);
                                    highestPercent.setPointsReducedPerSteal(1);
                                    highestPercent.setPointsReducedPerBlock(1);
                                    highestPercent.setPointsReducedPerTurnover(1);
                                    highestPercent.setPointsReducedPerRebound(1);
                                    highestPercent.setPointsReducedPerFoul(1);
                                    highestPercent.setHighReboundPointFactor(highReboundValue);
                                    highestPercent.setLowerReboundPointFactor(lowReboundValue);
                                    highestPercent.setHighFoulPointFactor(highFoulValue);
                                    highestPercent.setLowerFoulPointFactor(lowFoulValue);
                                    highestPercent.setModelOpponentTurnovers(true);
                                    highestPercent.setModelOpponentSteals(true);
                                    highestPercent.setModelOpponentBlocks(true);
                                    highestPercent.setGameTimeThreshold(0);
                                    highestPercent.setPointThreshold(0);
                                    highestPercent.setHomeTeamAdvantage(homeAdvantageValue);
                                    highestPercent.setSquareRootTotal(savedCorrelationResults.get(0).isSquareRootTotal());
                                    highestPercent.setFractalWindow(savedCorrelationResults.get(0).getFractalWindow());
                                    highestPercent.setOriginalResult(new BackTestResult());
                                }else{
                                    highestPercent = pastResults.get(0);
                                }



//                                List<BackTestResult> filtered = pastResults.parallelStream().filter(result -> result.getCorrectPercent() == highestPercent.getCorrectPercent())
//                                        .sorted(Comparator.comparingDouble(BackTestResult::getPointDelta)).collect(Collectors.toList());
//                                BackTestResult bestResult = filtered.get(0);
                                TestingPair key = null;
                                for (Map.Entry<TestingPair, Double> entry : moneyMap.entrySet()) {
                                    if (entry.getKey().getGameLookBack() == lookBack && entry.getKey().getPointThreshold() == pointThreshold &&
                                            entry.getKey().getBetPercent() == betPercent) {
                                        key = entry.getKey();
                                    }
                                }
                                TestingPair percentKey = null;
                                for (Map.Entry<TestingPair, List<Double>> entry : PercentageMap.entrySet()) {
                                    if (entry.getKey().getGameLookBack() == lookBack && entry.getKey().getPointThreshold() == pointThreshold &&
                                            entry.getKey().getBetPercent() == betPercent) {
                                        percentKey = entry.getKey();
                                    }
                                }
                                PercentageMap.get(percentKey).add(highestPercent.getCorrectPercent());
                                DoubleSummaryStatistics stats = PercentageMap.get(percentKey).stream().mapToDouble((x) -> x).summaryStatistics();
                                BackTestIngestObject backTestIngestObject = convertResultBackToIngestObject(highestPercent);
                                backTestIngestObject.setStartDate(testDate);
                                backTestIngestObject.setGamesToTest(1);
                                backTestIngestObject.setPointThreshold(pointThreshold);
                                ForwardPredictionThread backTestSingleThread = new ForwardPredictionThread(GameOddsHashMap, games, 0);
                                backTestSingleThread.setRecording(endingCorrelation > -1);
                                backTestSingleThread.setBackTestIngestObjects(new ArrayList<>());
                                backTestSingleThread.setForward(true);
                                backTestSingleThread.setStartingMoney(moneyMap.get(key));
                                backTestSingleThread.setConfidence(highestPercent.getCorrectPercent() - stats.getAverage());
                                backTestSingleThread.setBetPercent(betPercent);
                                BackTestResult bestBackTestPredictable = backTestSingleThread.runBackTestVerbose(backTestIngestObject);



                                if(!Double.isNaN(bestBackTestPredictable.getCorrectPercent())) {
                                    outputs.add(bestBackTestPredictable.getCorrectPercent());
                                }
                                if(!Double.isNaN(highestPercent.getCorrectPercent())) {
                                    selectedAverage.add(highestPercent.getCorrectPercent());
                                }
                                if(!Double.isNaN(highestPercent.getLowerTurnoverPointFactor())) {
                                    selectedLowTurnovers.add(highestPercent.getLowerTurnoverPointFactor());
                                }
                                if(!Double.isNaN(highestPercent.getLowerReboundPointFactor())) {
                                    selectedLowRebounds.add(highestPercent.getLowerReboundPointFactor());
                                }
                                if(!Double.isNaN(highestPercent.getHomeTeamAdvantage())) {
                                    selectedHomeTeamAdvantage.add(highestPercent.getHomeTeamAdvantage());
                                }
                                if(!Double.isNaN(highestPercent.getPlayerGameLookBack())) {
                                    selectedLookback.add((double) highestPercent.getPlayerGameLookBack());
                                }
                                if(!Double.isNaN(endingAverage)){
                                    originalAverages.add(endingAverage);
                                }
                                if(!Double.isNaN(endingCorrelation)){
                                    originalCoorelations.add(endingCorrelation);
                                }
                                if(!Double.isNaN(endingOldAverage)){
                                    oldAverages.add(endingOldAverage);
                                }

                                System.out.println(bestBackTestPredictable.getStartDate() + " tested " + bestBackTestPredictable.getCorrectPercent() + "% with a "
                                        + lookBack + " lookback." + bestBackTestPredictable
                                        + " Original pct was: " + highestPercent.getCorrectPercent()
                                        + " Original vol was: " + highestPercent.getDailyVol()
                                        + " Original ending Money was: " + highestPercent.getEndingMoney() + " confidence factor was: " + (highestPercent.getCorrectPercent() - stats.getAverage()));
                                if(originalAverages.size()>1 && originalCoorelations.size()>1 && selectedAverage.size()>1 &&outputs.size()>1) {
                                    double[] a = originalAverages.stream().mapToDouble(Double::doubleValue).toArray();
                                    double[] b = originalCoorelations.stream().mapToDouble(Double::doubleValue).toArray();
                                    double[] c = outputs.stream().mapToDouble(Double::doubleValue).toArray();
                                    double[] d = selectedAverage.stream().mapToDouble(Double::doubleValue).toArray();
                                    double[] e = selectedLowRebounds.stream().mapToDouble(Double::doubleValue).toArray();
                                    double[] f = selectedHomeTeamAdvantage.stream().mapToDouble(Double::doubleValue).toArray();
                                    double[] g = selectedLookback.stream().mapToDouble(Double::doubleValue).toArray();
                                    double[] h = oldAverages.stream().mapToDouble(Double::doubleValue).toArray();
                                    double[] i = selectedLowTurnovers.stream().mapToDouble(Double::doubleValue).toArray();
                                    double oldAverageCorrelation = new PearsonsCorrelation().correlation(h,c);
                                    double avgCorrelation = new PearsonsCorrelation().correlation(a, c);
                                    double oldCorrelation = new PearsonsCorrelation().correlation(b, c);
                                    double selectedCorrelation = new PearsonsCorrelation().correlation(d,c);
                                    double lowTurnoverCorrelation = new PearsonsCorrelation().correlation(i,c);
                                    double lowReboundCorrelation = new PearsonsCorrelation().correlation(e,c);
                                    double homeSelectionCorrelation = new PearsonsCorrelation().correlation(f,c);
                                    double lookbackCorrelation = new PearsonsCorrelation().correlation(g,c);
                                    System.out.println("oldAverages Correlation = " + oldAverageCorrelation);
                                    System.out.println("OriginalAverages Correlation = " + avgCorrelation);
                                    System.out.println("OldToNew Correlation = " + oldCorrelation);
                                    System.out.println("selected Correlation = " + selectedCorrelation);
                                    System.out.println("lowTurnover Correlation = " + lowTurnoverCorrelation);
                                    System.out.println("lowRebound Correlation = " + lowReboundCorrelation);
                                    System.out.println("homeSelection Correlation = " + homeSelectionCorrelation);
                                    System.out.println("lookback Correlation = " + lookbackCorrelation);
                                }
                                if(endingCorrelation > -1) {
                                    if (key != null) {
                                        moneyMap.put(key, bestBackTestPredictable.getEndingMoney());
                                    } else {
                                        TestingPair testingPair = new TestingPair();
                                        testingPair.setGameLookBack(lookBack);
                                        testingPair.setPointThreshold(pointThreshold);
                                        testingPair.setBetPercent(betPercent);
                                        moneyMap.put(testingPair, bestBackTestPredictable.getEndingMoney());
                                    }
                                    if (bestBackTestPredictable.getPredictCorrect() + bestBackTestPredictable.getPredictIncorrect() > 0) {
                                        boolean entryFound = false;
                                        for (Map.Entry<TestingPair, List<BackTestResult>> entry : resultsMap.entrySet()) {
                                            if (entry.getKey().getGameLookBack() == lookBack && entry.getKey().getPointThreshold() == pointThreshold
                                                    && entry.getKey().getBetPercent() == betPercent) {
                                                entryFound = true;
                                                entry.getValue().add(bestBackTestPredictable);
//                                            for(BackTestResult result : entry.getValue()){
//                                                System.out.println(result);
//                                            }

                                            }

                                        }
                                        if (!entryFound) {
                                            List<BackTestResult> list = new ArrayList<>();
                                            list.add(bestBackTestPredictable);
                                            TestingPair testingPair = new TestingPair();
                                            testingPair.setGameLookBack(lookBack);
                                            testingPair.setPointThreshold(pointThreshold);
                                            testingPair.setBetPercent(betPercent);
                                            resultsMap.put(testingPair, list);
                                        }

                                    }
                                    for (Map.Entry<TestingPair, List<BackTestResult>> entry : resultsMap.entrySet()) {
                                        int totalCorrect = 0;
                                        int totalIncorrect = 0;
                                        List<Double> moneyValues = new ArrayList<>();
                                        for (BackTestResult backTestResult : entry.getValue()) {
                                            totalIncorrect = totalIncorrect + backTestResult.getPredictIncorrect();
                                            totalCorrect = totalCorrect + backTestResult.getPredictCorrect();
                                            moneyValues.add(backTestResult.getEndingMoney());
                                        }
                                        DecimalFormat formatter = new DecimalFormat("$#,###.00");
                                        formatter.setMaximumIntegerDigits(13);
                                        // System.out.println();
                                        double vol = getDailyVol(moneyValues);
                                        System.out.println(entry.getKey() + " : " + totalCorrect + "-" + totalIncorrect + " " + (double) totalCorrect / (double) (totalCorrect + totalIncorrect) + " " + formatter.format(entry.getValue().get(entry.getValue().size() - 1).getEndingMoney()) + " : vol: " + vol);
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            daysTested++;
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
            csvExporter.writeAccuracyOutputs(selectedLowTurnovers, selectedHomeTeamAdvantage,
                    oldAverages, originalAverages,originalCoorelations, selectedLookback, outputs);
        }

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
        backTestIngestObject.setFractalWindow(backTestResult.getFractalWindow());
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

    public List<BackTestIngestObject> getNewIngestObjectSet(List<Integer> lookbackList, List<Double> highBlockList, List<Double> lowBlockList, List<Double>highStealList,
                                                            List<Double> lowStealList, List<Double> highTurnoverList, List<Double> lowTurnoverList,
                                                            List<Double> highReboundList, List<Double> lowReboundList, List<Double> highFoulList,
                                                            List<Double> lowFoulList, List<Double> homeAdvantageList, LocalDate testDate){
        String betType = "spread";
        int[] gameLookbackList = lookbackList.stream().mapToInt(Integer::intValue).toArray();
        double[] pointPerBlockList = {1};
        double[] pointPerStealList = {1};
        double pointPerTurnover = 1;
        double pointPerRebound = 1;
        double pointPerFoul = 1;
        double[] highBlockFactorList = highBlockList.stream().mapToDouble(Double::doubleValue).toArray();
        double[] lowBlockFactorList = lowBlockList.stream().mapToDouble(Double::doubleValue).toArray();
        double[] highStealFactorList = highStealList.stream().mapToDouble(Double::doubleValue).toArray();
        double[] lowStealFactorList = lowStealList.stream().mapToDouble(Double::doubleValue).toArray();
        double[] highTurnoverFactorList = highTurnoverList.stream().mapToDouble(Double::doubleValue).toArray();
        double[] lowTurnoverlFactorList = lowTurnoverList.stream().mapToDouble(Double::doubleValue).toArray();
        double[] highReboundFactorList = highReboundList.stream().mapToDouble(Double::doubleValue).toArray();
        double[] lowReboundFactorList = lowReboundList.stream().mapToDouble(Double::doubleValue).toArray();
        double[] highFoulFactorList = highFoulList.stream().mapToDouble(Double::doubleValue).toArray();
        double[] lowFoulFactorList = lowFoulList.stream().mapToDouble(Double::doubleValue).toArray();
        double[] gameTimeList = {0};
        double[] homeTeamAdvantages = homeAdvantageList.stream().mapToDouble(Double::doubleValue).toArray();
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
                                                                                    backTestIngestObject.setBetType(betType);
                                                                                    backTestIngestObject.setStartDate(testDate);
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
                                                                                    //backTestIngestObject.setGamesToTest(backTestResult.getGamesToTest());
                                                                                    backTestIngestObject.setPointThreshold(pointThreshold);
                                                                                    backTestIngestObject.setAllowBelowZero(allowBelowZero);
                                                                                    backTestIngestObject.setHomeTeamAdvantage(homeTeamAdvantage);
                                                                                    //backTestIngestObject.setTestingPair(backTestResult.getTestingPair());
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

    public List<BackTestIngestObject> runCorrelations(List<BackTestResult> results){
        List<Integer> lookbacks = new ArrayList<>();
        List<Double> highBlocks = new ArrayList<>();
        List<Double> lowBlocks = new ArrayList<>();
        List<Double> highSteals = new ArrayList<>();
        List<Double> lowSteals = new ArrayList<>();
        List<Double> highTurnovers = new ArrayList<>();
        List<Double> lowTurnovers = new ArrayList<>();
        List<Double> highRebounds = new ArrayList<>();
        List<Double> lowRebounds = new ArrayList<>();
        List<Double> highFouls = new ArrayList<>();
        List<Double> lowFouls = new ArrayList<>();
        List<Double> homeAdvantages = new ArrayList<>();
        List<Double> percentResults = new ArrayList<>();
        for(BackTestResult result : results){
            lookbacks.add(result.getPlayerGameLookBack());
            highBlocks.add(result.getHighBlockPointFactor());
            lowBlocks.add(result.getLowerBlockPointFactor());
            highSteals.add(result.getHighStealPointFactor());
            lowSteals.add(result.getLowerStealPointFactor());
            highTurnovers.add(result.getHighTurnoverPointFactor());
            lowTurnovers.add(result.getLowerTurnoverPointFactor());
            highRebounds.add(result.getHighReboundPointFactor());
            lowRebounds.add(result.getLowerReboundPointFactor());
            highFouls.add(result.getHighFoulPointFactor());
            lowFouls.add(result.getLowerFoulPointFactor());
            homeAdvantages.add(result.getHomeTeamAdvantage());
            percentResults.add(result.getCorrectPercent());
        }
        double lookbackCorrelation = new PearsonsCorrelation().correlation(lookbacks.stream().mapToDouble(Integer::intValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("Lookback Correlation = " + lookbackCorrelation);
        double highBlockCorrelation = new PearsonsCorrelation().correlation(highBlocks.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("High Block Correlation = " + highBlockCorrelation);

        double lowBlockCorrelation = new PearsonsCorrelation().correlation(lowBlocks.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("Low Block Correlation = " + lowBlockCorrelation);

        double highStealCorrelation = new PearsonsCorrelation().correlation(highSteals.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("High Steal Correlation = " + highStealCorrelation);

        double lowStealCorrelation = new PearsonsCorrelation().correlation(lowSteals.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("Low Steal Correlation = " + lowStealCorrelation);

        double highTurnoverCorrelation = new PearsonsCorrelation().correlation(highTurnovers.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("high Turnover Correlation = " + highTurnoverCorrelation);

        double lowTurnoverCorrelation = new PearsonsCorrelation().correlation(lowTurnovers.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("low Turnover Correlation = " + lowTurnoverCorrelation);

        double highReboundCorrelation = new PearsonsCorrelation().correlation(highRebounds.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("high Rebounds Correlation = " + highReboundCorrelation);

        double lowReboundCorrelation = new PearsonsCorrelation().correlation(lowRebounds.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("low Rebounds Correlation = " + lowReboundCorrelation);

        double highFoulCorrelation = new PearsonsCorrelation().correlation(highFouls.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("high Fouls Correlation = " + highFoulCorrelation);
        double lowFoulCorrelation = new PearsonsCorrelation().correlation(lowFouls.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("low Fouls Correlation = " + lowFoulCorrelation);
        double homeAdvantageCorrelation = new PearsonsCorrelation().correlation(homeAdvantages.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("Home Advantage Correlation = " + homeAdvantageCorrelation);
        HashMap<String, Double> allCorrelations = new HashMap<>();
        //if(stuck) {
            allCorrelations.put("lookback", lookbackCorrelation);
        //}
        //allCorrelations.put("highBlock", highBlockCorrelation);
        //allCorrelations.put("lowBlock", lowBlockCorrelation);
        //allCorrelations.put("highSteal", highStealCorrelation);
        //allCorrelations.put("lowSteal", lowStealCorrelation);
        //allCorrelations.put("highTurnover", highTurnoverCorrelation);
        //allCorrelations.put("lowTurnover", lowTurnoverCorrelation);
        allCorrelations.put("highRebound", highReboundCorrelation);
        //allCorrelations.put("lowRebound", lowReboundCorrelation);
        //allCorrelations.put("highFoul", highFoulCorrelation);
        //allCorrelations.put("lowFoul", lowFoulCorrelation);
        allCorrelations.put("homeAdvantage", homeAdvantageCorrelation);
        HashMap<String,Double> safeMap = new HashMap<>();
        for(Map.Entry<String,Double> entry : allCorrelations.entrySet()){
            safeMap.put(entry.getKey(), entry.getValue());
        }
        HashSet<Integer> lookback = new HashSet<>();
        HashSet<Double> highBlock = new HashSet<>();
        HashSet<Double> lowBlock = new HashSet<>();
        HashSet<Double> highSteal = new HashSet<>();
        HashSet<Double> lowSteal = new HashSet<>();
        HashSet<Double> highTurnover = new HashSet<>();
        HashSet<Double> lowTurnover = new HashSet<>();
        HashSet<Double> highRebound = new HashSet<>();
        HashSet<Double> lowRebound = new HashSet<>();
        HashSet<Double> highFoul = new HashSet<>();
        HashSet<Double> lowFoul = new HashSet<>();
        HashSet<Double> homeAdvantage = new HashSet<>();
        for(BackTestResult result : results){
            lookback.add(result.getPlayerGameLookBack());
            highBlock.add(result.getHighBlockPointFactor());
            lowBlock.add(result.getLowerBlockPointFactor());
            highSteal.add(result.getHighStealPointFactor());
            lowSteal.add(result.getLowerStealPointFactor());
            highTurnover.add(result.getHighTurnoverPointFactor());
            lowTurnover.add(result.getLowerTurnoverPointFactor());
            highRebound.add(result.getHighReboundPointFactor());
            lowRebound.add(result.getLowerReboundPointFactor());
            highFoul.add(result.getHighFoulPointFactor());
            lowFoul.add(result.getLowerFoulPointFactor());
            homeAdvantage.add(result.getHomeTeamAdvantage());
        }




        List<Map.Entry<String, Double>> test = new ArrayList<>(allCorrelations.entrySet());
        for(Map.Entry<String,Double> entry : test){
            if(entry.getValue() < 0){
                entry.setValue(entry.getValue() * -1);
            }
        }
        Collections.sort(test,(o1, o2) -> o1.getValue().compareTo(o2.getValue()) * -1);
        List<Integer> modificationCountList = new ArrayList<>();
        for(Map.Entry<String, Integer> entry: modificationCountMap.entrySet()){
            modificationCountList.add(entry.getValue());
        }
        List<Map.Entry<String,Double>> listOfEntriesToAdjust = new ArrayList<>();
        if(stuck){
            listOfEntriesToAdjust.add(new AbstractMap.SimpleEntry<String, Double>("lookback", 0.0));
        }
        for(Map.Entry<String,Double> entry : test){
            int existingValue = modificationCountMap.get(entry.getKey());
            //if(existingValue < (Collections.min(modificationCountList) + 5)){
            //if(existingValue < 5){
                if(listOfEntriesToAdjust.size()<1) {
                    if(!entry.getKey().equals(mostRecentAdjustment)) {
                        if(!blackList.contains(entry.getKey())) {
                            //if(!antiStuckStrings.contains(entry.getKey())) {
                                modificationCountMap.put(entry.getKey(), existingValue + 1);
                                Map.Entry<String, Double> newEntry =
                                        new AbstractMap.SimpleEntry<String, Double>(entry.getKey(), safeMap.get(entry.getKey()));
                                listOfEntriesToAdjust.add(newEntry);
                            //}
                        }
                    }
                }
            //}
        }
        if(listOfEntriesToAdjust.size() == 0){
            for(Map.Entry<String,Double> entry : test) {
                if(listOfEntriesToAdjust.size()<1) {
                    Map.Entry<String, Double> newEntry =
                            new AbstractMap.SimpleEntry<String, Double>(entry.getKey(), safeMap.get(entry.getKey()));
                    listOfEntriesToAdjust.add(newEntry);
                }
            }
        }

        for(Map.Entry<String, Double> entry : listOfEntriesToAdjust) {
            System.out.println("Strongest Correlation is " + entry.getKey() + " " + entry.getValue());
        }
        int maxCount = 3;
        for(Map.Entry<String, Double> adjustment : listOfEntriesToAdjust) {

            switch (adjustment.getKey()) {
                case "lookback":
                    if(stuck){
                        lookback.add(Collections.max(lookback) + (playerLookBackIncrement));
                        lookback.add(Collections.min(lookback) - (playerLookBackIncrement));
                    }else {
                        if (adjustment.getValue() < 0) {
                            if (!(Collections.min(lookback) <= 2)) {
                                if (lookback.size() >= 2) {
                                    lookback.remove(Collections.max(lookback));

                                    lookback.add(Collections.min(lookback) - (playerLookBackIncrement));
                                    System.out.println("new range is " + lookback);
                                }
                            }
                        } else {
                            if (lookback.size() >= 2) {
                                lookback.remove(Collections.min(lookback));
                            }
                            //highBlock.add(Collections.max(highBlock) + (highBlockIncrement * (adjustment.getValue()*2)));
                            lookback.add(Collections.max(lookback) + (playerLookBackIncrement));
                            System.out.println("new range is " + lookback);
                        }
                    }
                    break;
                case "highBlock":
                    if (adjustment.getValue() < 0) {
                        if(highBlock.size()>=maxCount) {
                            highBlock.remove(Collections.max(highBlock));
                        }
                        highBlock.add(Collections.min(highBlock) - (highBlockIncrement));
                        System.out.println("new range is " + highBlock);
                    } else {
                        if(highBlock.size()>=maxCount) {
                            highBlock.remove(Collections.min(highBlock));
                        }
                        //highBlock.add(Collections.max(highBlock) + (highBlockIncrement * (adjustment.getValue()*2)));
                        highBlock.add(Collections.max(highBlock) + (highBlockIncrement));
                        System.out.println("new range is " + highBlock);
                    }
                    break;
                case "lowBlock":
                    if (adjustment.getValue() < 0) {
                        if(lowBlock.size()>=maxCount) {
                            lowBlock.remove(Collections.max(lowBlock));
                        }
                        //lowBlock.add(Collections.min(lowBlock) - (lowBlockIncrement * (adjustment.getValue()*2) * -1));
                        lowBlock.add(Collections.min(lowBlock) - (lowBlockIncrement ));
                        System.out.println("new range is " + lowBlock);
                    } else {
                        if(lowBlock.size()>=maxCount) {
                            lowBlock.remove(Collections.min(lowBlock));
                        }
                        //lowBlock.add(Collections.max(lowBlock) + (lowBlockIncrement * (adjustment.getValue()*2)));
                        lowBlock.add(Collections.max(lowBlock) + (lowBlockIncrement));
                        System.out.println("new range is " + lowBlock);
                    }
                    break;
                case "highSteal":
                    if (adjustment.getValue() < 0) {
                        if(highSteal.size()>=maxCount) {
                            highSteal.remove(Collections.max(highSteal));
                        }
                        //highSteal.add(Collections.min(highSteal) - (highStealIncrement * (adjustment.getValue()*2) * -1));
                        highSteal.add(Collections.min(highSteal) - (highStealIncrement));
                        System.out.println("new range is " + highSteal);
                    } else {
                        if(highSteal.size()>=maxCount) {
                            highSteal.remove(Collections.min(highSteal));
                        }
                        //highSteal.add(Collections.max(highSteal) + (highStealIncrement * (adjustment.getValue()*2)));
                        highSteal.add(Collections.max(highSteal) + (highStealIncrement));
                        System.out.println("new range is " + highSteal);
                    }
                    break;
                case "lowSteal":
                    if (adjustment.getValue() < 0) {
                        if(lowSteal.size()>=maxCount) {
                            lowSteal.remove(Collections.max(lowSteal));
                        }
                        //lowSteal.add(Collections.min(lowSteal) - (lowStealIncrement * (adjustment.getValue()*2) * -1));
                        lowSteal.add(Collections.min(lowSteal) - (lowStealIncrement));
                        System.out.println("new range is " + lowSteal);
                    } else {
                        if(lowSteal.size()>=maxCount) {
                            lowSteal.remove(Collections.min(lowSteal));
                        }
                        //lowSteal.add(Collections.max(lowSteal) + (lowStealIncrement * (adjustment.getValue()*2)));
                        lowSteal.add(Collections.max(lowSteal) + (lowStealIncrement));
                        System.out.println("new range is " + lowSteal);
                    }
                    break;
                case "highTurnover":
                    if (adjustment.getValue() < 0) {
                        if(highTurnover.size()>=maxCount) {
                            highTurnover.remove(Collections.max(highTurnover));
                        }
                        //highTurnover.add(Collections.min(highTurnover) - (highTurnoverIncrement * (adjustment.getValue()*2) * -1));
                        highTurnover.add(Collections.min(highTurnover) - (highTurnoverIncrement));
                        System.out.println("new range is " + highTurnover);
                    } else {
                        if(highTurnover.size()>=maxCount) {
                            highTurnover.remove(Collections.min(highTurnover));
                        }
                        //highTurnover.add(Collections.max(highTurnover) + (highTurnoverIncrement * (adjustment.getValue()*2)));
                        highTurnover.add(Collections.max(highTurnover) + (highTurnoverIncrement));
                        System.out.println("new range is " + highTurnover);
                    }
                    break;
                case "lowTurnover":
                    if (adjustment.getValue() < 0) {
                        //if(!(Collections.min(lowTurnover)<=lowTurnoverBoundary)) {
                            if (lowTurnover.size() >= maxCount) {
                                lowTurnover.remove(Collections.max(lowTurnover));
                            }
                            //lowTurnover.add(Collections.min(lowTurnover) - (lowTurnoverIncrement * (adjustment.getValue()*2) * -1));
                            lowTurnover.add(Collections.min(lowTurnover) - (lowTurnoverIncrement));
                            System.out.println("new range is " + lowTurnover);
                        //}
                    } else {
                        if(lowTurnover.size()>=maxCount) {
                            lowTurnover.remove(Collections.min(lowTurnover));
                        }
                        //lowTurnover.add(Collections.max(lowTurnover) + (lowTurnoverIncrement * (adjustment.getValue()*2)));
                        lowTurnover.add(Collections.max(lowTurnover) + (lowTurnoverIncrement));
                        System.out.println("new range is " + lowTurnover);
                    }
                    break;
                case "highRebound":
                    if (adjustment.getValue() < 0) {
                        if(highRebound.size()>=maxCount) {
                            highRebound.remove(Collections.max(highRebound));
                        }
                        //highRebound.add(Collections.min(highRebound) - (highReboundIncrement * (adjustment.getValue()*2) * -1));
                        highRebound.add(Collections.min(highRebound) - (highReboundIncrement));
                        System.out.println("new range is " + highRebound);
                    } else {
                        if(highRebound.size()>=maxCount) {
                            highRebound.remove(Collections.min(highRebound));
                        }
                        //highRebound.add(Collections.max(highRebound) + (highReboundIncrement * (adjustment.getValue()*2)));
                        highRebound.add(Collections.max(highRebound) + (highReboundIncrement));
                        System.out.println("new range is " + highRebound);
                    }
                    break;
                case "lowRebound":
                    if (adjustment.getValue() < 0) {
                        if(lowRebound.size()>=maxCount) {
                            lowRebound.remove(Collections.max(lowRebound));
                        }
                        //lowRebound.add(Collections.min(lowRebound) - (lowReboundIncrement * (adjustment.getValue()*2) * -1));
                        lowRebound.add(Collections.min(lowRebound) - (lowReboundIncrement));
                        System.out.println("new range is " + lowRebound);
                    } else {
                        if(lowRebound.size()>=maxCount) {
                            lowRebound.remove(Collections.min(lowRebound));
                        }
                        //lowRebound.add(Collections.max(lowRebound) + (lowReboundIncrement * (adjustment.getValue()*2)));
                        lowRebound.add(Collections.max(lowRebound) + (lowReboundIncrement));
                        System.out.println("new range is " + lowRebound);
                    }
                    break;
                case "highFoul":
                    if (adjustment.getValue() < 0) {
                        if(highFoul.size()>=maxCount) {
                            highFoul.remove(Collections.max(highFoul));
                        }
                       //highFoul.add(Collections.min(highFoul) - (highFoulIncrement * (adjustment.getValue()*2) * -1));
                        highFoul.add(Collections.min(highFoul) - (highFoulIncrement));
                        System.out.println("new range is " + highFoul);
                    } else {
                        if(highFoul.size()>=maxCount) {
                            highFoul.remove(Collections.min(highFoul));
                        }
                        //highFoul.add(Collections.max(highFoul) + (highFoulIncrement * (adjustment.getValue()*2)));
                        highFoul.add(Collections.max(highFoul) + (highFoulIncrement));
                        System.out.println("new range is " + highFoul);
                    }
                    break;
                case "lowFoul":
                    if (adjustment.getValue() < 0) {
                        if(lowFoul.size()>=maxCount) {
                            lowFoul.remove(Collections.max(lowFoul));
                        }
                        //lowFoul.add(Collections.min(lowFoul) - (lowFoulIncrement * (adjustment.getValue()*2) * -1));
                        lowFoul.add(Collections.min(lowFoul) - (lowFoulIncrement));
                        System.out.println("new range is " + lowFoul);
                    } else {
                        if(lowFoul.size()>=maxCount) {
                            lowFoul.remove(Collections.min(lowFoul));
                        }
                        //lowFoul.add(Collections.max(lowFoul) + (lowFoulIncrement * (adjustment.getValue()*2)));
                        lowFoul.add(Collections.max(lowFoul) + (lowFoulIncrement));
                        System.out.println("new range is " + lowFoul);
                    }
                    break;
                case "homeAdvantage":
                        if (adjustment.getValue() <= 0 ) {
                            //if(!(Collections.min(homeAdvantage)<=homeLowBoundary)) {
                                if (homeAdvantage.size() >= maxCount) {
                                    homeAdvantage.remove(Collections.max(homeAdvantage));
                                }
                                //lowFoul.add(Collections.min(lowFoul) - (lowFoulIncrement * (adjustment.getValue()*2) * -1));
                                homeAdvantage.add(Collections.min(homeAdvantage) - (homeAdvantageIncrement));
                                System.out.println("new range is " + homeAdvantage);
                            //}
                        } else {
                            if (homeAdvantage.size() >= maxCount) {
                                homeAdvantage.remove(Collections.min(homeAdvantage));
                            }
                            //lowFoul.add(Collections.max(lowFoul) + (lowFoulIncrement * (adjustment.getValue()*2)));
                            homeAdvantage.add(Collections.max(homeAdvantage) + (homeAdvantageIncrement));
                            System.out.println("new range is " + homeAdvantage);
                        }
                        break;

            }
            mostRecentAdjustment = adjustment.getKey();
        }

        return getNewIngestObjectSet(new ArrayList<>(lookback), new ArrayList<>(highBlock),new ArrayList<>(lowBlock),new ArrayList<>(highSteal), new ArrayList<>(lowSteal),
                    new ArrayList<>(highTurnover), new ArrayList<>(lowTurnover), new ArrayList<>(highRebound), new ArrayList<>(lowRebound),
                new ArrayList<>(highFoul), new ArrayList<>(lowFoul), new ArrayList<>(homeAdvantage), results.get(0).getStartDate());

    }

    public List<BackTestIngestObject> adjustRanges(List<BackTestIngestObject> originalObjects, boolean increase){

        HashSet<Integer> lookback = new HashSet<>();
        HashSet<Double> highBlock = new HashSet<>();
        HashSet<Double> lowBlock = new HashSet<>();
        HashSet<Double> highSteal = new HashSet<>();
        HashSet<Double> lowSteal = new HashSet<>();
        HashSet<Double> highTurnover = new HashSet<>();
        HashSet<Double> lowTurnover = new HashSet<>();
        HashSet<Double> highRebound = new HashSet<>();
        HashSet<Double> lowRebound = new HashSet<>();
        HashSet<Double> highFoul = new HashSet<>();
        HashSet<Double> lowFoul = new HashSet<>();
        HashSet<Double> homeAdvantage = new HashSet<>();
        for(BackTestIngestObject result : originalObjects){
            lookback.add(result.getPlayerGameLookBack());
            highBlock.add(result.getHighBlockPointFactor());
            lowBlock.add(result.getLowerBlockPointFactor());
            highSteal.add(result.getHighStealPointFactor());
            lowSteal.add(result.getLowerStealPointFactor());
            highTurnover.add(result.getHighTurnoverPointFactor());
            lowTurnover.add(result.getLowerTurnoverPointFactor());
            highRebound.add(result.getHighReboundPointFactor());
            lowRebound.add(result.getLowerReboundPointFactor());
            highFoul.add(result.getHighFoulPointFactor());
            lowFoul.add(result.getLowerFoulPointFactor());
            homeAdvantage.add(result.getHomeTeamAdvantage());
        }

        if(increase){
            highBlock.remove(Collections.min(highBlock));
            highBlock.add(Collections.max(highBlock) + highBlockIncrement);
            lowBlock.remove(Collections.min(lowBlock));
            lowBlock.add(Collections.max(lowBlock) + highBlockIncrement);
            highSteal.remove(Collections.min(highSteal));
            highSteal.add(Collections.max(highSteal) + highBlockIncrement);
            lowSteal.remove(Collections.min(lowSteal));
            lowSteal.add(Collections.max(lowSteal) + highBlockIncrement);
            highTurnover.remove(Collections.min(highTurnover));
            highTurnover.add(Collections.max(highTurnover) + highBlockIncrement);
            lowTurnover.remove(Collections.min(lowTurnover));
            lowTurnover.add(Collections.max(lowTurnover) + highBlockIncrement);
            highRebound.remove(Collections.min(highRebound));
            highRebound.add(Collections.max(highRebound) + highBlockIncrement);
            lowRebound.remove(Collections.min(lowRebound));
            lowRebound.add(Collections.max(lowRebound) + highBlockIncrement);
            highFoul.remove(Collections.min(highFoul));
            highFoul.add(Collections.max(highFoul) + highBlockIncrement);
            lowFoul.remove(Collections.min(lowFoul));
            lowFoul.add(Collections.max(lowFoul) + highBlockIncrement);
        }else{
            highBlock.remove(Collections.max(highBlock));
            highBlock.add(Collections.min(highBlock) - highBlockIncrement);
            lowBlock.remove(Collections.max(lowBlock));
            lowBlock.add(Collections.min(lowBlock) - highBlockIncrement);
            highSteal.remove(Collections.max(highSteal));
            highSteal.add(Collections.min(highSteal) - highBlockIncrement);
            lowSteal.remove(Collections.max(lowSteal));
            lowSteal.add(Collections.min(lowSteal) - highBlockIncrement);
            highTurnover.remove(Collections.max(highTurnover));
            highTurnover.add(Collections.min(highTurnover) - highBlockIncrement);
            lowTurnover.remove(Collections.max(lowTurnover));
            lowTurnover.add(Collections.min(lowTurnover) - highBlockIncrement);
            highRebound.remove(Collections.max(highRebound));
            highRebound.add(Collections.min(highRebound) - highBlockIncrement);
            lowRebound.remove(Collections.max(lowRebound));
            lowRebound.add(Collections.min(lowRebound) - highBlockIncrement);
            highFoul.remove(Collections.max(highFoul));
            highFoul.add(Collections.min(highFoul) - highBlockIncrement);
            lowFoul.remove(Collections.max(lowFoul));
            lowFoul.add(Collections.min(lowFoul) - highBlockIncrement);
        }


        return getNewIngestObjectSet(new ArrayList<>(lookback), new ArrayList<>(highBlock),new ArrayList<>(lowBlock),new ArrayList<>(highSteal), new ArrayList<>(lowSteal),
                new ArrayList<>(highTurnover), new ArrayList<>(lowTurnover), new ArrayList<>(highRebound), new ArrayList<>(lowRebound),
                new ArrayList<>(highFoul), new ArrayList<>(lowFoul), new ArrayList<>(homeAdvantage), originalObjects.get(0).getStartDate());

    }





    public void setCorrelations(List<BackTestResult> results) {
        List<Integer> lookbacks = new ArrayList<>();
        List<Double> highBlocks = new ArrayList<>();
        List<Double> lowBlocks = new ArrayList<>();
        List<Double> highSteals = new ArrayList<>();
        List<Double> lowSteals = new ArrayList<>();
        List<Double> highTurnovers = new ArrayList<>();
        List<Double> lowTurnovers = new ArrayList<>();
        List<Double> highRebounds = new ArrayList<>();
        List<Double> lowRebounds = new ArrayList<>();
        List<Double> highFouls = new ArrayList<>();
        List<Double> lowFouls = new ArrayList<>();
        List<Double> homeAdvantages = new ArrayList<>();
        List<Double> percentResults = new ArrayList<>();
        for (BackTestResult result : results) {
            lookbacks.add(result.getPlayerGameLookBack());
            highBlocks.add(result.getHighBlockPointFactor());
            lowBlocks.add(result.getLowerBlockPointFactor());
            highSteals.add(result.getHighStealPointFactor());
            lowSteals.add(result.getLowerStealPointFactor());
            highTurnovers.add(result.getHighTurnoverPointFactor());
            lowTurnovers.add(result.getLowerTurnoverPointFactor());
            highRebounds.add(result.getHighReboundPointFactor());
            lowRebounds.add(result.getLowerReboundPointFactor());
            highFouls.add(result.getHighFoulPointFactor());
            lowFouls.add(result.getLowerFoulPointFactor());
            homeAdvantages.add(result.getHomeTeamAdvantage());
            percentResults.add(result.getCorrectPercent());
        }
        double lookbackCorrelation = new PearsonsCorrelation().correlation(lookbacks.stream().mapToDouble(Integer::intValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        this.lookbackCorrelation = lookbackCorrelation;
        System.out.println("Lookback Correlation = " + lookbackCorrelation);
        double highBlockCorrelation = new PearsonsCorrelation().correlation(highBlocks.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("High Block Correlation = " + highBlockCorrelation);
        this.highBlockCorrelation = highBlockCorrelation;
        double lowBlockCorrelation = new PearsonsCorrelation().correlation(lowBlocks.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("Low Block Correlation = " + lowBlockCorrelation);
        this.lowBlockCorrelation = lowBlockCorrelation;
        double highStealCorrelation = new PearsonsCorrelation().correlation(highSteals.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("High Steal Correlation = " + highStealCorrelation);
        this.highStealCorrelation = highStealCorrelation;
        double lowStealCorrelation = new PearsonsCorrelation().correlation(lowSteals.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("Low Steal Correlation = " + lowStealCorrelation);
        this.lowStealCorrelation = lowStealCorrelation;
        double highTurnoverCorrelation = new PearsonsCorrelation().correlation(highTurnovers.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("high Turnover Correlation = " + highTurnoverCorrelation);
        this.highTurnoverCorrelation = highTurnoverCorrelation;
        double lowTurnoverCorrelation = new PearsonsCorrelation().correlation(lowTurnovers.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("low Turnover Correlation = " + lowTurnoverCorrelation);
        this.lowTurnoverCorrelation = lowTurnoverCorrelation;
        double highReboundCorrelation = new PearsonsCorrelation().correlation(highRebounds.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("high Rebounds Correlation = " + highReboundCorrelation);
        this.highReboundCorrelation = highReboundCorrelation;
        double lowReboundCorrelation = new PearsonsCorrelation().correlation(lowRebounds.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("low Rebounds Correlation = " + lowReboundCorrelation);
        this.lowReboundCorrelation = lowReboundCorrelation;
        double highFoulCorrelation = new PearsonsCorrelation().correlation(highFouls.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("high Fouls Correlation = " + highFoulCorrelation);
        this.highFoulCorrelation = highFoulCorrelation;
        double lowFoulCorrelation = new PearsonsCorrelation().correlation(lowFouls.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("low Fouls Correlation = " + lowFoulCorrelation);
        this.lowFoulCorrelation = lowFoulCorrelation;
        double homeAdvantageCorrelation = new PearsonsCorrelation().correlation(homeAdvantages.stream().mapToDouble(Double::doubleValue).toArray(),
                percentResults.stream().mapToDouble(Double::doubleValue).toArray());
        System.out.println("Home Advantage Correlation = " + homeAdvantageCorrelation);
        this.homeAdvantageCorrelation = homeAdvantageCorrelation;
    }
}
