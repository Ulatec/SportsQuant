package SportsQuant.Util;

import SportsQuant.Model.BackTestResult;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class CSVExporter {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public CSVExporter(){

    }

    public void writeCSVResults(List<BackTestResult> results){
        boolean fileWritten = false;

        results.sort(Comparator.comparing(SportsQuant.Model.BackTestResult::getEndingMoney).reversed());
        results = results.parallelStream().limit(1000000).collect(Collectors.toList());
        while(!fileWritten) {
            try {
                String betType = results.get(0).getBetType();
                String filePath;
                if (betType.equals("spread")) {
                    filePath = "NBASpreadBackTestResults.csv";
                } else if (betType.equals("overunder")) {
                    filePath = "NBAOverUnderBackTestResults.csv";
                } else if (betType.equals("moneyline")) {
                    filePath = "NBAMoneyLineBackTestResults.csv";
                } else {
                    filePath = "idk.csv";
                }

                File file = new File(filePath);
                FileWriter outputfile = new FileWriter(file);

                // create CSVWriter object filewriter object as parameter
                CSVWriter writer = new CSVWriter(outputfile);

                // adding header to csv
                String[] header = {"PlayerLookBack",
                        "highBlkPntFctr", "LwrBlkPntFctr", "highStlPntFctr", "lwrStlPntFctr", "highTOpntFctr","lwrTOpntFctr",
                        "highRbndFctr", "lowRbndFctr", "highFoulFctr", "lowFoulFctr",
                        "pointVolW", "allowBelowZero", "sqrtTotal", "PredictCorrect",
                        "PredictIncorrect",  "pntThreshold", "homeTeamAdvantage", "fractalWindow", "estimatedPts",
                        "actualPts", "gameTime", "correctPercent", "endingMoney"};
                writer.writeNext(header);
                for (BackTestResult backTestResult : results) {
                    String[] data1 = {String.valueOf(backTestResult.getPlayerGameLookBack()),
                            String.valueOf(backTestResult.getHighBlockPointFactor()), String.valueOf(backTestResult.getLowerBlockPointFactor()),  String.valueOf(backTestResult.getHighStealPointFactor()), String.valueOf(backTestResult.getLowerStealPointFactor()),
                            String.valueOf(backTestResult.getHighTurnoverPointFactor()), String.valueOf(backTestResult.getLowerTurnoverPointFactor()),
                            String.valueOf(backTestResult.getHighReboundPointFactor()), String.valueOf(backTestResult.getLowerReboundPointFactor()),
                            String.valueOf(backTestResult.getHighFoulPointFactor()), String.valueOf(backTestResult.getLowerFoulPointFactor()),
                            String.valueOf(backTestResult.getPointvolweight()), String.valueOf(backTestResult.isAllowBelowZero()), String.valueOf(backTestResult.isSquareRootTotal()),
                            String.valueOf(backTestResult.getPredictCorrect()), String.valueOf(backTestResult.getPredictIncorrect()),
                            String.valueOf(backTestResult.getPointThreshold()), String.valueOf(backTestResult.getHomeTeamAdvantage()), String.valueOf(backTestResult.getFractalWindow()), String.valueOf(backTestResult.getEstimatedPoints()), String.valueOf(backTestResult.getActualPoints()), String.valueOf(backTestResult.getGameTimeThreshold()),
                            String.valueOf(backTestResult.getCorrectPercent()), String.valueOf(backTestResult.getEndingMoney())
                    };
                    writer.writeNext(data1);
                }
                // add data to csv

                // closing writer connection
                writer.close();
                System.out.println(ANSI_GREEN + "file written" + ANSI_RESET);
                fileWritten = true;
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println(ANSI_RED + "[" + new Date() + "] Could not write file." + ANSI_RESET);
                try {
                    Thread.sleep(10000);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }
    }
    public void writeTimeAccuracyResults(List<BackTestResult> results){
        boolean fileWritten = false;

        //results.sort(Comparator.comparing(SportsQuant.Model.BackTestResult::getCorrectPercent).reversed());
        //results = results.parallelStream().limit(1000000).collect(Collectors.toList());
        while(!fileWritten) {
            try {
                String betType = results.get(0).getBetType();
                String filePath;
                filePath = "TimeAccuracyTest.csv";

                File file = new File(filePath);
                FileWriter outputfile = new FileWriter(file);

                // create CSVWriter object filewriter object as parameter
                CSVWriter writer = new CSVWriter(outputfile);

                // adding header to csv
                String[] header = {"PlayerLookBack",
                        "highBlkPntFctr", "LwrBlkPntFctr", "highStlPntFctr", "lwrStlPntFctr", "highTOpntFctr","lwrTOpntFctr",
                        "highRbndFctr", "lowRbndFctr", "highFoulFctr", "lowFoulFctr",
                        "dblSqRt", "allowBelowZero", "sqrtTotal", "PredictCorrect",
                        "PredictIncorrect",  "pntThreshold", "homeTeamAdvantage", "estimatedPts",
                        "actualPts", "gameTime", "correctPercent", "OGcorrectPct", "startDate", "endingMoney"};
                writer.writeNext(header);
                for (BackTestResult backTestResult : results) {
                    String[] data1 = {String.valueOf(backTestResult.getPlayerGameLookBack()),
                            String.valueOf(backTestResult.getHighBlockPointFactor()), String.valueOf(backTestResult.getLowerBlockPointFactor()),  String.valueOf(backTestResult.getHighStealPointFactor()), String.valueOf(backTestResult.getLowerStealPointFactor()),
                            String.valueOf(backTestResult.getHighTurnoverPointFactor()), String.valueOf(backTestResult.getLowerTurnoverPointFactor()),
                            String.valueOf(backTestResult.getHighReboundPointFactor()), String.valueOf(backTestResult.getLowerReboundPointFactor()),
                            String.valueOf(backTestResult.getHighFoulPointFactor()), String.valueOf(backTestResult.getLowerFoulPointFactor()),
                            String.valueOf(backTestResult.isDoubleSquareRoot()), String.valueOf(backTestResult.isAllowBelowZero()), String.valueOf(backTestResult.isSquareRootTotal()),
                            String.valueOf(backTestResult.getPredictCorrect()), String.valueOf(backTestResult.getPredictIncorrect()),
                            String.valueOf(backTestResult.getPointThreshold()), String.valueOf(backTestResult.getHomeTeamAdvantage()), String.valueOf(backTestResult.getEstimatedPoints()), String.valueOf(backTestResult.getActualPoints()), String.valueOf(backTestResult.getGameTimeThreshold()),
                            String.valueOf(backTestResult.getCorrectPercent()), String.valueOf(backTestResult.getOriginalResult().getCorrectPercent()), String.valueOf(backTestResult.getStartDate()), String.valueOf(backTestResult.getEndingMoney())
                    };
                    writer.writeNext(data1);
                }
                // add data to csv

                // closing writer connection
                writer.close();
                System.out.println(ANSI_GREEN + "file written" + ANSI_RESET);
                fileWritten = true;
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println(ANSI_RED + "[" + new Date() + "] Could not write file." + ANSI_RESET);
                try {
                    Thread.sleep(10000);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }
    }
    public void writeAccuracyOutputs(List<Double> lowTurnovers, List<Double> homeadvantage, List<Double> old, List<Double> a,
                                     List<Double> b, List<Double> lookback, List<Double> c){
        boolean fileWritten = false;
        //results.sort(Comparator.comparing(SportsQuant.Model.BackTestResult::getCorrectPercent).reversed());
        //results = results.parallelStream().limit(1000000).collect(Collectors.toList());
        while(!fileWritten) {
            try {
                //String betType = firstentry.getKey().getBetType();
                String filePath;
                filePath = "accuracyOutputs.csv";

                File file = new File(filePath);
                FileWriter outputfile = new FileWriter(file);

                // create CSVWriter object filewriter object as parameter
                CSVWriter writer = new CSVWriter(outputfile);

                // adding header to csv
                String[] header = {"lowTurnovers", "homeadvantage", "Old", "Averages",
                        "Correlations", "Lookback", "Outputs"};
                writer.writeNext(header);
                for(int i = 0; i<a.size(); i++) {
                        String[] data1 = { String.valueOf(lowTurnovers.get(i)), String.valueOf(homeadvantage.get(i)),
                                String.valueOf(old.get(i)),
                                String.valueOf(a.get(i)),
                                String.valueOf(b.get(i)),
                                String.valueOf(lookback.get(i)),
                                String.valueOf(c.get(i))
                        };
                        writer.writeNext(data1);

                }
                // add data to csv

                // closing writer connection
                writer.close();
                System.out.println(ANSI_GREEN + "file written" + ANSI_RESET);
                fileWritten = true;
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println(ANSI_RED + "[" + new Date() + "] Could not write file." + ANSI_RESET);
                try {
                    Thread.sleep(10000);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }
    }
    public void writeCoorelationResults(HashMap<LocalDate, HashMap<BackTestResult, BackTestResult>> map){
        boolean fileWritten = false;
        //results.sort(Comparator.comparing(SportsQuant.Model.BackTestResult::getCorrectPercent).reversed());
        //results = results.parallelStream().limit(1000000).collect(Collectors.toList());
        while(!fileWritten) {
            try {
                //String betType = firstentry.getKey().getBetType();
                String filePath;
                filePath = "TimeCorrelationTest.csv";

                File file = new File(filePath);
                FileWriter outputfile = new FileWriter(file);

                // create CSVWriter object filewriter object as parameter
                CSVWriter writer = new CSVWriter(outputfile);

                // adding header to csv
                String[] header = {"PlayerLookBack",
                        "highBlkPntFctr", "LwrBlkPntFctr", "highStlPntFctr", "lwrStlPntFctr", "highTOpntFctr","lwrTOpntFctr",
                        "highRbndFctr", "lowRbndFctr", "highFoulFctr", "lowFoulFctr",
                        "dblSqRt", "allowBelowZero", "sqrtTotal", "PredictCorrect",
                        "PredictIncorrect",  "pntThreshold", "homeTeamAdvantage", "estimatedPts",
                        "actualPts", "gameTime", "firstPct", "correctPercent", "startDate", "ForwardPct" };
                writer.writeNext(header);
                for(Map.Entry<LocalDate, HashMap<BackTestResult, BackTestResult>> entry : map.entrySet()) {
                    for (Map.Entry<BackTestResult, BackTestResult> subEntry : entry.getValue().entrySet()) {
                        String[] data1 = {String.valueOf(subEntry.getKey().getPlayerGameLookBack()),
                                String.valueOf(subEntry.getKey().getHighBlockPointFactor()), String.valueOf(subEntry.getKey().getLowerBlockPointFactor()), String.valueOf(subEntry.getKey().getHighStealPointFactor()), String.valueOf(subEntry.getKey().getLowerStealPointFactor()),
                                String.valueOf(subEntry.getKey().getHighTurnoverPointFactor()), String.valueOf(subEntry.getKey().getLowerTurnoverPointFactor()),
                                String.valueOf(subEntry.getKey().getHighReboundPointFactor()), String.valueOf(subEntry.getKey().getLowerReboundPointFactor()),
                                String.valueOf(subEntry.getKey().getHighFoulPointFactor()), String.valueOf(subEntry.getKey().getLowerFoulPointFactor()),
                                String.valueOf(subEntry.getKey().isDoubleSquareRoot()), String.valueOf(subEntry.getKey().isAllowBelowZero()), String.valueOf(subEntry.getKey().isSquareRootTotal()),
                                String.valueOf(subEntry.getKey().getPredictCorrect()), String.valueOf(subEntry.getKey().getPredictIncorrect()),
                                String.valueOf(subEntry.getKey().getPointThreshold()), String.valueOf(subEntry.getKey().getHomeTeamAdvantage()), String.valueOf(subEntry.getKey().getEstimatedPoints()), String.valueOf(subEntry.getKey().getActualPoints()), String.valueOf(subEntry.getKey().getGameTimeThreshold()),
                                String.valueOf(subEntry.getKey().getOriginalResult().getCorrectPercent()),
                                String.valueOf(subEntry.getKey().getCorrectPercent()), String.valueOf(subEntry.getKey().getStartDate()), String.valueOf(subEntry.getValue().getCorrectPercent())
                        };
                        writer.writeNext(data1);
                    }
                }
                // add data to csv

                // closing writer connection
                writer.close();
                System.out.println(ANSI_GREEN + "file written" + ANSI_RESET);
                fileWritten = true;
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println(ANSI_RED + "[" + new Date() + "] Could not write file." + ANSI_RESET);
                try {
                    Thread.sleep(10000);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }
    }
}
