package BaseballQuant.Threads;

import BaseballQuant.Model.BackTestResult;
import BaseballQuant.Model.CacheObjects.CacheSettingsObject;
import BaseballQuant.Model.ScoreModel;
import BaseballQuant.Util.CSVExporter;

import java.util.*;

public class ThreadMonitor extends Thread{
    List<BackTestResult> backTestResults;
    int numberOfThreads;
    int finishedThreads;
    boolean cacheBuilding = false;
    boolean safeToKill = false;
    private final HashMap<Integer, HashMap<Integer, ScoreModel>> mergedCache = new HashMap<>();
    public ThreadMonitor(int numberOfThreads){
        this.numberOfThreads = numberOfThreads;
        finishedThreads = 0;
        backTestResults = new ArrayList<>();
    }

    public boolean isCacheBuilding() {
        return cacheBuilding;
    }

    public void setCacheBuilding(boolean cacheBuilding) {
        this.cacheBuilding = cacheBuilding;
    }

    public void run(){
        while(finishedThreads<numberOfThreads) {
            //DO NOTHING
        }

        while(!safeToKill & cacheBuilding) {
            //DO NOTHING
        }
        System.out.println("ThreadMonitor finished.");
//        for(BackTestResult backTestResult : backTestResults) {
//            System.out.println(backTestResult);
//        }
//        CSVExporter csvExporter = new CSVExporter();
//        csvExporter.writeCSVResults(backTestResults);
    }

    public void writeResults(){
//        for(BackTestResult backTestResult : backTestResults) {
//            System.out.println(backTestResult);
//        }
        CSVExporter csvExporter = new CSVExporter();
        csvExporter.writeCSVResults(backTestResults);
    }
    public HashMap<CacheSettingsObject, HashMap<Integer, ScoreModel>> getGameCache(){
   //     if(finishedThreads<numberOfThreads){
            return null;
//        }else{
//            safeToKill = true;
//            return mergedCache;
//        }
    }
    public void ingestGameCache(HashMap<CacheSettingsObject, HashMap<Integer, BaseballQuant.Model.ScoreModel>> gameCache){
//        Object[] list = gameCache.entrySet().toArray();
//        for(int i = 0; i<list.length; i++) {
//            boolean cacheSettingsFound = false;
//            Map.Entry<CacheSettingsObject, HashMap<Integer, BaseballQuant.Model.ScoreModel>> newEntry = (Map.Entry<CacheSettingsObject, HashMap<Integer, ScoreModel>>) list[i];
//            Iterator<Map.Entry<Integer, HashMap<Integer, ScoreModel>>> iterator = mergedCache.entrySet().iterator();
//            CacheSettingsObject temp = new CacheSettingsObject();
//            temp.setAllowBelowZero(newEntry.getKey().isAllowBelowZero());
//            temp.setDoubleSquareRoot(newEntry.getKey().isDoubleSquareRoot());
//            temp.setPitcherGameLookback(newEntry.getKey().getPitcherGameLookback());
//            temp.setPlayerGameLookback(newEntry.getKey().getPlayerGameLookback());
//            temp.setBullpenLookback(newEntry.getKey().getBullpenLookback());
//            if(gameCache.get(temp.hashCode()) != null) {
//                cacheSettingsFound = true;
//            }
//
//            while (iterator.hasNext()) {
//                Map.Entry<Integer, HashMap<Integer, ScoreModel>> mergedEntry = iterator.next();
//                if(mergedEntry.getKey().getBullpenLookback() == newEntry.getKey().getBullpenLookback() &&
//                        mergedEntry.getKey().getPlayerGameLookback() == newEntry.getKey().getPlayerGameLookback() &&
//                        mergedEntry.getKey().getPitcherGameLookback() == newEntry.getKey().getPitcherGameLookback() &&
//                        mergedEntry.getKey().isDoubleSquareRoot() == newEntry.getKey().isDoubleSquareRoot() &&
//                        mergedEntry.getKey().isAllowBelowZero() == newEntry.getKey().isAllowBelowZero()
//                ){
//                    cacheSettingsFound = true;
//                    for(Map.Entry<Integer, ScoreModel> GameEntry : newEntry.getValue().entrySet() ){
//                        mergedEntry.getValue().computeIfAbsent(GameEntry.getKey(), k -> GameEntry.getValue());
//                    }
//                }
//            }
//            if(!cacheSettingsFound){
//                CacheSettingsObject cacheSettingsObject = new CacheSettingsObject();
//                cacheSettingsObject.setDoubleSquareRoot(newEntry.getKey().isDoubleSquareRoot());
//                cacheSettingsObject.setPitcherGameLookback(newEntry.getKey().getPitcherGameLookback());
//                cacheSettingsObject.setPlayerGameLookback(newEntry.getKey().getPlayerGameLookback());
//                cacheSettingsObject.setBullpenLookback(newEntry.getKey().getBullpenLookback());
//                cacheSettingsObject.setAllowBelowZero(newEntry.getKey().isAllowBelowZero());
//                mergedCache.put(cacheSettingsObject.hashCode(),newEntry.getValue());
//            }
//        }
    }

    public void ingestResults(List<BackTestResult> backTestResults){
        this.backTestResults.addAll(backTestResults);
    }
    public void threadFinished(){

        finishedThreads = finishedThreads + 1;
        System.out.println(finishedThreads + " finished.");
        System.out.println(finishedThreads + ":" + numberOfThreads);
        if(finishedThreads == numberOfThreads && !cacheBuilding){
            writeResults();
        }
    }


}
