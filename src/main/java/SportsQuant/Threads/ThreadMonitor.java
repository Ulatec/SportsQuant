package SportsQuant.Threads;

import SportsQuant.Model.BackTestResult;
import SportsQuant.Util.CSVExporter;

import java.util.ArrayList;
import java.util.List;

public class ThreadMonitor extends Thread{
    List<BackTestResult> backTestResults;
    int numberOfThreads;
    int finishedThreads;
    public ThreadMonitor(int numberOfThreads){
        this.numberOfThreads = numberOfThreads;
        finishedThreads = 0;
        backTestResults = new ArrayList<>();
    }
    public void run(){
        while(finishedThreads<numberOfThreads) {
            //DO NOTHING
        }
        System.out.println("ThreadMonitor finished.");
    }
    public void writeResults(){
        CSVExporter csvExporter = new CSVExporter();
        csvExporter.writeCSVResults(backTestResults);
    }
    public void ingestResults(List<BackTestResult> backTestResults){
        this.backTestResults.addAll(backTestResults);
    }
    public void threadFinished(){

        finishedThreads = finishedThreads + 1;
        System.out.println(finishedThreads + " finished.");
        System.out.println(finishedThreads + ":" + numberOfThreads);
        if(finishedThreads == numberOfThreads){
            writeResults();
        }
    }

}
