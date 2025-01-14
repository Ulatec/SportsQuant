package SportsQuant.Threads;

import SportsQuant.Model.BackTestIngestObject;
import SportsQuant.Model.BackTestResult;
import SportsQuant.Model.Game;
import SportsQuant.Model.StatResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class StatThreadMonitor {
    List<TreeMap<String, HashMap<StatResult,Boolean>>> backTestResults;
    int numberOfThreads;
    int finishedThreads;
    List<Game> games;
    List<BackTestIngestObject> backTestIngestObjects;
    HashMap<Integer, List<TreeMap<String, HashMap<StatResult,Boolean>>>> resultMap;
    boolean completed;
    boolean safeToKill = false;

    public StatThreadMonitor(int numberOfThreads){
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
    public synchronized List<TreeMap<String, HashMap<StatResult,Boolean>>> getBackTestResults(){
        if(resultMap.entrySet().size()<numberOfThreads){
            return null;
        }else{
            safeToKill = true;
            return backTestResults;
        }
    }

    public void writeResults(){
    }
    public synchronized void threadFinished(int threadNum, List<TreeMap<String, HashMap<StatResult,Boolean>>> backTestResults){
        this.backTestResults.addAll(backTestResults);
        resultMap.put(threadNum, backTestResults);
        finishedThreads++;
        System.out.println(finishedThreads + " finished.");
        System.out.println(finishedThreads + ":" + numberOfThreads);
        if(finishedThreads == numberOfThreads){
            writeResults();
        }

    }

    public synchronized void setBackTestResults(List<TreeMap<String, HashMap<StatResult,Boolean>>> backTestResults) {
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
