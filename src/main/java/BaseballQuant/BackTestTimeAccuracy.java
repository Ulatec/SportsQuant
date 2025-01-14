package BaseballQuant;

import BaseballQuant.Model.*;
import BaseballQuant.Threads.BackTestSingleThread;
import BaseballQuant.Threads.BackTestTimeThreadMonitor;
import BaseballQuant.Util.ListSplitter;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

//@Component
public class BackTestTimeAccuracy extends Thread{

    HashMap<Integer, MLBGameOdds> mlbGameOddsHashMap;
    List<BackTestIngestObject> backTestIngestObjects;
    List<MLBGame> games;

    BackTestTimeAccuracy(List<BackTestIngestObject> backTestIngestObjects, HashMap<Integer, MLBGameOdds> mlbGameOddsHashMap, List<MLBGame> games){
        this.backTestIngestObjects = backTestIngestObjects;
        this.mlbGameOddsHashMap = mlbGameOddsHashMap;
        this.games = games;
    }

    public void run(){
        int[] lookBacksToTest = new int[]{400};
        int[] gamesToTest = new int[]{1};
        int durationOfDays = 30;
        int threads = 12;
        LocalDate originalDate = backTestIngestObjects.get(0).getStartDate();
        int runs = backTestIngestObjects.size() * lookBacksToTest.length * durationOfDays;
        //BackTestSingleThread backTestSingleThread = new BackTestSingleThread(runs,mlbGameOddsHashMap, games);
        double startingMoney = 100;
        HashMap<Integer, Double> moneyMap = new HashMap<>();
        for(int lookback : lookBacksToTest){
            moneyMap.put(lookback, startingMoney);
        }
        HashMap<TestingPair, List<BackTestResult>> resultsMap = new HashMap<>();
        List<BackTestSingleThread> threadsList = new ArrayList<>();
        for(int i =0; i<threads; i++){
            BackTestSingleThread backTestSingleThread = new BackTestSingleThread(mlbGameOddsHashMap,games, i);
            threadsList.add(backTestSingleThread);
            backTestSingleThread.start();
        }
        int daysTested = 0;
        while(daysTested<durationOfDays) {
                //List<BackTestResult> dailyResults = new ArrayList<>();
                List<BackTestIngestObject> clonedIngestObjects = new ArrayList<>();
                for (BackTestIngestObject backTestIngestObject : backTestIngestObjects) {
                    for(int lookBack : lookBacksToTest) {
                        BackTestIngestObject clone = backTestIngestObject.clone();
                        clone.setGamesToTest(lookBack);
                        clone.setStartDate(backTestIngestObject.getStartDate().plusDays((1L * daysTested)));
                        clonedIngestObjects.add(clone);
                        //BackTestResult result = backTestSingleThread.runBackTest(clone);
                        //dailyResults.add(result);
                    }
                }
                List<List<BackTestIngestObject>> listOfLists = ListSplitter.split(clonedIngestObjects, threads);
                BackTestTimeThreadMonitor backTestTimeThreadMonitor = new BackTestTimeThreadMonitor(threads);
                backTestTimeThreadMonitor.start();
                for(int i =0; i<threads; i++){
                    threadsList.get(i).setThreadMonitor(backTestTimeThreadMonitor);
                    threadsList.get(i).setBackTestIngestObjects(listOfLists.get(i));
                }
                while(backTestTimeThreadMonitor.getBackTestResults() == null){

                }
                List<BackTestResult> results = backTestTimeThreadMonitor.getBackTestResults();
                for(int lookBack : lookBacksToTest){
                    List<BackTestResult> freshResults = new ArrayList<>(results);
                    freshResults = freshResults.stream().filter(result -> result.getGamesToTest() == lookBack)
                            .sorted(Comparator.comparingDouble(BackTestResult::getEndingMoney)
                                    .reversed()).collect(Collectors.toCollection(ArrayList::new));
                    BackTestResult bestResult = freshResults.get(0);
                    ///List<BackTestResult> filtered = freshResults.stream().limit(10)
                    //        .sorted(Comparator.comparingDouble(BackTestResult::getDailyVol).reversed()).collect(Collectors.toList());
                    //BackTestResult bestResult = filtered.get(0);
                    for(int gamesDuration : gamesToTest) {
                        BackTestIngestObject backTestIngestObject = convertResultBackToIngestObject(bestResult);
                        backTestIngestObject.setStartDate(originalDate.plusDays(1 + (1L * daysTested)));
                        backTestIngestObject.setGamesToTest(gamesDuration);
                        BackTestSingleThread backTestSingleThread = new BackTestSingleThread( mlbGameOddsHashMap, games, 0);
                        backTestSingleThread.setBackTestIngestObjects(new ArrayList<>());
                        backTestSingleThread.setForward(true);
                        backTestSingleThread.setStartingMoney(moneyMap.get(lookBack));
                        BackTestResult bestBackTestPredictable = backTestSingleThread.runBackTestVerbose(backTestIngestObject);
                        moneyMap.put(lookBack,bestBackTestPredictable.getEndingMoney());
                        System.out.println(bestBackTestPredictable.getStartDate() + " tested " + bestBackTestPredictable.getCorrectPercent() + "% with a "
                                + lookBack + " lookback." + bestBackTestPredictable
                                + " Original pct was: " + bestResult.getCorrectPercent()
                                + " Original vol was: " + bestResult.getDailyVol()
                                + " Original ending Money was: " + bestResult.getEndingMoney());
                        if(bestBackTestPredictable.getPredictCorrect() + bestBackTestPredictable.getPredictIncorrect()>0) {
                            boolean entryFound = false;
                            for (Map.Entry<TestingPair, List<BackTestResult>> entry : resultsMap.entrySet()) {
                                if (entry.getKey().getGameLookBack() == lookBack && entry.getKey().getPointThreshold() == gamesDuration) {
                                    entryFound = true;
                                    entry.getValue().add(bestBackTestPredictable);
                                }
                            }
                            if (!entryFound) {
                                List<BackTestResult> list = new ArrayList<>();
                                list.add(bestBackTestPredictable);
                                TestingPair testingPair = new TestingPair();
                                testingPair.setGameLookBack(lookBack);
                                testingPair.setPointThreshold(gamesDuration);
                                resultsMap.put(testingPair, list);
                            }
                        }
                        for(Map.Entry<TestingPair, List<BackTestResult>> entry : resultsMap.entrySet()) {
                            int totalCorrect=0;
                            int totalIncorrect =0;
                            for(BackTestResult backTestResult : entry.getValue()){
                                totalIncorrect = totalIncorrect + backTestResult.getPredictIncorrect();
                                totalCorrect = totalCorrect + backTestResult.getPredictCorrect();
                            }
                            System.out.println(entry.getKey() + " : " + (double) totalCorrect / (double) (totalCorrect + totalIncorrect) + " " + entry.getValue().get(entry.getValue().size() - 1).getEndingMoney());
                        }
                    }
                }
            daysTested++;
        }

        for(Map.Entry<TestingPair, List<BackTestResult>> entry : resultsMap.entrySet()){
            int totalCorrect=0;
            int totalIncorrect =0;
            for(BackTestResult backTestResult : entry.getValue()){
                totalIncorrect = totalIncorrect + backTestResult.getPredictIncorrect();
                totalCorrect = totalCorrect + backTestResult.getPredictCorrect();
            }

            System.out.println(entry.getKey() + " : " + (double)totalCorrect/(double)(totalCorrect + totalIncorrect) + " " + entry.getValue().get(entry.getValue().size() - 1).getEndingMoney());
            for(BackTestResult result : entry.getValue()){
                System.out.println(result);
            }
        }
        //System.out.println(bestResultList);
    }


    public BackTestIngestObject convertResultBackToIngestObject(BackTestResult backTestResult){
        BackTestIngestObject backTestIngestObject = new BackTestIngestObject();
        backTestIngestObject.setAllowLowEndBelowZero(backTestResult.isAllowLowEndBelowZero());
        backTestIngestObject.setGamesToTest(backTestResult.getGamesToTest());
        backTestIngestObject.setStartDate(backTestResult.getStartDate());
        backTestIngestObject.setBetType(backTestResult.getBetType());
        backTestIngestObject.setGameCount(backTestResult.getPlayerGameLookBack());
        backTestIngestObject.setBullpenGameCount(backTestResult.getBullpenGameLookback());
        backTestIngestObject.setDoubleSquareRoot(backTestResult.isDoubleSquareRoot());
        backTestIngestObject.setSquareRootTotalPoints(backTestResult.isSquareRootTotalPoints());
        backTestIngestObject.setPitcherGameLookback(backTestResult.getPitcherGameLookback());

        backTestIngestObject.setModelOpposingFielding(backTestResult.isModelOpponentFielding());
        backTestIngestObject.setModelStolenBases(backTestResult.isModelStolenBases());
        return backTestIngestObject;
    }
}
