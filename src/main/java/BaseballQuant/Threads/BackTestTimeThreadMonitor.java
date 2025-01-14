package BaseballQuant.Threads;

import BaseballQuant.Model.BackTestIngestObject;
import BaseballQuant.Model.BackTestResult;
import BaseballQuant.Model.MLBGame;
import BaseballQuant.Util.CSVExporter;

import java.util.ArrayList;
import java.util.List;

public class BackTestTimeThreadMonitor extends Thread{
    List<BackTestResult> backTestResults;
    int numberOfThreads;
    int finishedThreads;
    List<MLBGame> games;
    List<BackTestIngestObject> backTestIngestObjects;
    boolean completed;
    boolean safeToKill = false;
    public BackTestTimeThreadMonitor(int numberOfThreads){
        this.numberOfThreads = numberOfThreads;
        finishedThreads = 0;
        backTestResults = new ArrayList<>();
    }
    public void run(){
        for(int i = 0; i<numberOfThreads; i++){

        }
        while(!safeToKill) {
            //DO NOTHING
        }

        System.out.println("ThreadMonitor finished.");
    }
    public List<BackTestResult> getBackTestResults(){
        if(finishedThreads<numberOfThreads){
            return null;
        }else{
            safeToKill = true;
            return backTestResults;
        }
    }

    public void writeResults(){
//        for(BackTestResult backTestResult : backTestResults) {
//            System.out.println(backTestResult);
//        }
//        CSVExporter csvExporter = new CSVExporter();
//        csvExporter.writeCSVResults(backTestResults);
    }
    public void ingestResults(List<BackTestResult> backTestResults){
        this.backTestResults.addAll(backTestResults);
    }
    public void threadFinished(){

        finishedThreads = finishedThreads + 1;
        //System.out.println(finishedThreads + " finished.");
        //System.out.println(finishedThreads + ":" + numberOfThreads);
        if(finishedThreads == numberOfThreads){
            writeResults();
        }
    }

    public void setBackTestResults(List<BackTestResult> backTestResults) {
        this.backTestResults = backTestResults;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public int getFinishedThreads() {
        return finishedThreads;
    }

    public void setFinishedThreads(int finishedThreads) {
        this.finishedThreads = finishedThreads;
    }

    public List<MLBGame> getGames() {
        return games;
    }

    public void setGames(List<MLBGame> games) {
        this.games = games;
    }

    public List<BackTestIngestObject> getBackTestIngestObjects() {
        return backTestIngestObjects;
    }

    public void setBackTestIngestObjects(List<BackTestIngestObject> backTestIngestObjects) {
        this.backTestIngestObjects = backTestIngestObjects;
    }

    public boolean isSafeToKill() {
        return safeToKill;
    }

    public void setSafeToKill(boolean safeToKill) {
        this.safeToKill = safeToKill;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }


}
