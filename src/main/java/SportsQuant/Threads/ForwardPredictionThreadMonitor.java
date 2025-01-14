package SportsQuant.Threads;


import SportsQuant.Model.BackTestIngestObject;
import SportsQuant.Model.BackTestResult;
import SportsQuant.Model.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ForwardPredictionThreadMonitor extends Thread{
    List<BackTestResult> backTestResults;
    int numberOfThreads;
    int finishedThreads;
    List<Game> games;
    List<BackTestIngestObject> backTestIngestObjects;
    HashMap<Integer, List<BackTestResult>> resultMap;
    boolean completed;
    boolean safeToKill = false;

    public ForwardPredictionThreadMonitor(int numberOfThreads){
        this.numberOfThreads = numberOfThreads;
        finishedThreads = 0;
        backTestResults = new ArrayList<>();
        resultMap = new HashMap<>();
    }
    public void run(){
        for(int i = 0; i<numberOfThreads; i++){

        }
        while(!safeToKill) {
            //DO NOTHING
        }

        System.out.println("ThreadMonitor finished.");
    }
    public synchronized List<BackTestResult> getBackTestResults(){
        if(resultMap.entrySet().size()<numberOfThreads){
            return null;
        }else{
            safeToKill = true;
            return backTestResults;
        }
    }

    public void writeResults(){
    }
    public synchronized void threadFinished(int threadNum, List<BackTestResult> backTestResults){
        this.backTestResults.addAll(backTestResults);
        resultMap.put(threadNum, backTestResults);
        finishedThreads++;
        System.out.println(finishedThreads + " finished.");
        System.out.println(finishedThreads + ":" + numberOfThreads);
        if(finishedThreads == numberOfThreads){
            writeResults();
        }

    }

    public synchronized void setBackTestResults(List<BackTestResult> backTestResults) {
        this.backTestResults = backTestResults;
    }


    public synchronized List<Game> getGames() {
        return games;
    }

    public synchronized void setGames(List<Game> games) {
        this.games = games;
    }

    public synchronized List<BackTestIngestObject> getBackTestIngestObjects() {
        return backTestIngestObjects;
    }

    public synchronized void setBackTestIngestObjects(List<BackTestIngestObject> backTestIngestObjects) {
        this.backTestIngestObjects = backTestIngestObjects;
    }

}
