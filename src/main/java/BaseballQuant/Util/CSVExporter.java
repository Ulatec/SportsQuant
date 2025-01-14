package BaseballQuant.Util;

import BaseballQuant.Model.BackTestResult;
import BaseballQuant.Model.MLBGameOdds;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CSVExporter {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    public CSVExporter(){

    }

    public void writeAllGameOddsToCsv(List<MLBGameOdds> allGameOdds){
        String filePath = "GameOddsExport.csv";
        try {
            File file = new File(filePath);
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);
            String[] header = {"gameId","date","awayTeam","homeTeam","overUnder","awayTeamSpread","homeTeamSpread","awayTeamSpreadOdds","homeTeamSpreadOdds","awayTeamMoneyLine", "homeTeamMoneyLine"};
            writer.writeNext(header);
            for(MLBGameOdds mlbGameOdds : allGameOdds){
                String[] data1 = {String.valueOf(mlbGameOdds.getGameId()),String.valueOf(mlbGameOdds.getDate()),String.valueOf(mlbGameOdds.getAwayTeamName()),
                String.valueOf(mlbGameOdds.getHomeTeamName()),String.valueOf(mlbGameOdds.getOverUnder()), String.valueOf(mlbGameOdds.getAwayTeamSpread()),String.valueOf(mlbGameOdds.getHomeTeamSpread()),
                String.valueOf(mlbGameOdds.getAwayTeamSpreadOdds()),String.valueOf(mlbGameOdds.getHomeTeamSpreadOdds()),String.valueOf(mlbGameOdds.getAwayTeamMoneyLine()),String.valueOf(mlbGameOdds.getHomeTeamMoneyLine())};
                writer.writeNext(data1);
            }
            writer.close();
            System.out.println("file written");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void writeCSVResults(List<BackTestResult> results){
        boolean fileWritten = false;

        results.sort(Comparator.comparing(BackTestResult::getEndingMoney).reversed());
        results = results.parallelStream().limit(50000).collect(Collectors.toList());
        String spread = results.get(0).getBetType();
        String filePath = "MLBOverUnderBackTestResults2.csv";
        if(spread.equals("spread")){
            filePath = "MLBSpreadBackTestResults.csv";
        }else if(spread.equals("moneyline")){
            filePath = "MLBMoneyLineBackTestResults.csv";
        }
        while(!fileWritten) {
            try {
                File file = new File(filePath);
                FileWriter outputfile = new FileWriter(file);

                // create CSVWriter object filewriter object as parameter
                CSVWriter writer = new CSVWriter(outputfile);

                // adding header to csv
                String[] header = {"PlayerLookBack", "PitcherLookBack", "BullpenLookback",
                         "SquareRootTotal", "DoubleSquareRoot",
                        "allowBelow0",

                        "enable1", "enable2","enable3","enable4","enable5","enable6",
                        "enable7", "enable8","enable9","enable10","enable11","enable12",
                        "enable13", "enable14", "enable15",

                        "flip1", "flip2", "flip3", "flip4", "flip5", "flip6",

                        "homeGivenUpFlip","awayGivenUpFlip","homeScoredFlip","awayScoredFlip",
                        "homeFieldFlip", "awayFieldFlip", "homeStolenFlip", "awayStolenFlip",
                        "d1","d2","d3","d4","d5","d6","d7","d8","d9","d10","d11","d12","deez",
                       "PredictOver", "PredictUnder",
                        "PredictCorrect", "PredictIncorrect", "actualOver", "actualUnder",
                         "estimatedPts", "actualPts", "correctPercent", "dailyVol", "rsquare" , "endingMoney","betSize"};
                writer.writeNext(header);
                for (BackTestResult backTestResult : results) {
                    if (backTestResult.getPredictCorrect() + backTestResult.getPredictIncorrect() > 0) {

                        String[] data1 = {String.valueOf(backTestResult.getPlayerGameLookBack()), String.valueOf(backTestResult.getPitcherGameLookback()),
                                String.valueOf(backTestResult.getBullpenGameLookback()),  String.valueOf(backTestResult.isSquareRootTotalPoints()),
                                String.valueOf(backTestResult.isDoubleSquareRoot()), String.valueOf(backTestResult.isAllowLowEndBelowZero()),

                                String.valueOf(backTestResult.isEnable1()), String.valueOf(backTestResult.isEnable2()),
                                String.valueOf(backTestResult.isEnable3()), String.valueOf(backTestResult.isEnable4()),
                                String.valueOf(backTestResult.isEnable5()), String.valueOf(backTestResult.isEnable6()),
                                String.valueOf(backTestResult.isEnable7()), String.valueOf(backTestResult.isEnable8()),
                                String.valueOf(backTestResult.isEnable9()), String.valueOf(backTestResult.isEnable10()),
                                String.valueOf(backTestResult.isEnable11()), String.valueOf(backTestResult.isEnable12()),
                                String.valueOf(backTestResult.isEnable13()), String.valueOf(backTestResult.isEnable14()),
                                String.valueOf(backTestResult.isEnable15()),

                                String.valueOf(backTestResult.isFlip1()), String.valueOf(backTestResult.isFlip2()),
                                String.valueOf(backTestResult.isFlip3()), String.valueOf(backTestResult.isFlip4()),
                                String.valueOf(backTestResult.isFlip5()), String.valueOf(backTestResult.isFlip6()),

                                String.valueOf(backTestResult.isHomeRunsGivenUpRocFlip()), String.valueOf(backTestResult.isAwayRunsGivenUpRocFlip()),
                                String.valueOf(backTestResult.isHomeRunsScoredRocFlip()), String.valueOf(backTestResult.isAwayRunsScoredocFlip()),

                                String.valueOf(backTestResult.isHomeFieldingRocFlip()), String.valueOf(backTestResult.isAwayFieldingRocFlip()),
                                String.valueOf(backTestResult.isHomeStolenBasesRocFlip()), String.valueOf(backTestResult.isAwayStolenBasesRocFlip()),

                                String.valueOf(backTestResult.getD1()),String.valueOf(backTestResult.getD2()),String.valueOf(backTestResult.getD3()),
                                String.valueOf(backTestResult.getD4()),String.valueOf(backTestResult.getD5()),String.valueOf(backTestResult.getD6()),
                                String.valueOf(backTestResult.getD7()), String.valueOf(backTestResult.getD8()), String.valueOf(backTestResult.getD9()),
                                String.valueOf(backTestResult.getD10()), String.valueOf(backTestResult.getD11()),String.valueOf(backTestResult.getD12()),
                                String.valueOf(backTestResult.getDeez()),
                                String.valueOf(backTestResult.getPredictOver()), String.valueOf(backTestResult.getPredictUnder()),
                                String.valueOf(backTestResult.getPredictCorrect()), String.valueOf(backTestResult.getPredictIncorrect()),
                                String.valueOf(backTestResult.getActualOver()), String.valueOf(backTestResult.getActualUnder()),
                                String.valueOf(backTestResult.getEstimatedPoints()), String.valueOf(backTestResult.getActualPoints()),
                                String.valueOf(backTestResult.getCorrectPercent()), String.valueOf(backTestResult.getDailyVol()),
                                String.valueOf(backTestResult.getrSquared()), String.valueOf(backTestResult.getEndingMoney()), String.valueOf(backTestResult.getBetSize())
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
                try{
                    Thread.sleep(10000);
                }catch (Exception ee){
                    ee.printStackTrace();
                }
            }
        }
    }
}
