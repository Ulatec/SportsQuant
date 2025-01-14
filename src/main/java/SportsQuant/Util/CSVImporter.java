package SportsQuant.Util;

import BaseballQuant.Model.CSVGameObject;
import com.opencsv.CSVReader;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CSVImporter {

    public List<CSVGameObject> readGameDataFromCSV(){
        List<CSVGameObject> csvGameObjects = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(Paths.get("C:/Users/Mark/Desktop/nba odds 2022.csv"))) {

            CSVReader csvReader = new CSVReader(reader);
            csvReader.skip(1);

            //Iterator<VINEntry> myUserIterator = csvToBean.iterator();
            String[] nextRecord;
            int i = 0;
            Instant start = Instant.now();
            CSVGameObject csvGameObject = null;
            while ((nextRecord = csvReader.readNext()) != null) {
                if(i% 2 ==0){

                    csvGameObject = new CSVGameObject();
                    StringBuilder stringBuilder = new StringBuilder();
                    String dateString = nextRecord[0];
                    LocalDate localDate;
                    if(dateString.length() > 3){
                        localDate = LocalDate.of(2021, Integer.parseInt(dateString.substring(0, 2)), Integer.parseInt(dateString.substring(2)));
                    }else {
                        localDate = LocalDate.of(2022, Integer.parseInt(dateString.substring(0, 1)), Integer.parseInt(dateString.substring(1)));
                    }
                    stringBuilder.append(localDate);
                    String homeOrAway = nextRecord[2];
                    if(homeOrAway.equals("V")){
                        stringBuilder.append(" Away ");
                    }else{
                        stringBuilder.append(" Home ");
                    }
                    String teamName = nextRecord[3];
                    String triCode = convertNameToTriCode(teamName);
                    stringBuilder.append(triCode);
                    stringBuilder.append(" ");
                    stringBuilder.append(" ");
                    try {
                        double close = Double.parseDouble(nextRecord[10]);
                        if(close > 150){
                            csvGameObject.setOverUnder(close);
                        }else{
                            csvGameObject.setAwayTeamSpread(close * -1);
                            csvGameObject.setHomeTeamSpread(close);
                        }
                    }catch (Exception e){
                        csvGameObject.setAwayTeamSpread(0);
                        csvGameObject.setHomeTeamSpread(0);
                    }

                    double moneyLine = Double.parseDouble(nextRecord[11]);
                    stringBuilder.append(" ");
                    //System.out.println(stringBuilder);
                    csvGameObject.setAwayTeamMoneyLine(moneyLine);
                    csvGameObject.setAwayTeamSpreadOdds(-108);
                    csvGameObject.setAwayTeamTricode(triCode);
                    csvGameObject.setDate(Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
                }else{
                    StringBuilder stringBuilder = new StringBuilder();
                    String dateString = nextRecord[0];
                    LocalDate localDate;
                    if(dateString.length() > 3){
                        localDate = LocalDate.of(2021, Integer.parseInt(dateString.substring(0, 2)), Integer.parseInt(dateString.substring(2)));
                    }else {
                        localDate = LocalDate.of(2022, Integer.parseInt(dateString.substring(0, 1)), Integer.parseInt(dateString.substring(1)));
                    }
                    stringBuilder.append(localDate);
                    String homeOrAway = nextRecord[2];
                    if(homeOrAway.equals("V")){
                        stringBuilder.append(" Away ");
                    }else{
                        stringBuilder.append(" Home ");
                    }
                    String teamName = nextRecord[3];
                    String triCode = convertNameToTriCode(teamName);
                    stringBuilder.append(triCode);
                    stringBuilder.append(" ");
                    stringBuilder.append(" ");
                    try {
                        double close = Double.parseDouble(nextRecord[10]);
                        if(close > 150){
                            csvGameObject.setOverUnder(close);
                        }else{
                            csvGameObject.setAwayTeamSpread(close);
                            csvGameObject.setHomeTeamSpread(close * -1);
                        }
                    }catch (Exception e){
                        csvGameObject.setAwayTeamSpread(0);
                        csvGameObject.setHomeTeamSpread(0);
                    }
                    double moneyLine = Double.parseDouble(nextRecord[11]);

                    stringBuilder.append(" ");
                    //System.out.println(stringBuilder);
                    csvGameObject.setHomeTeamMoneyLine(moneyLine);
                    csvGameObject.setHomeTeamSpreadOdds(-108);
                    csvGameObject.setHomeTeamTricode(triCode);
                    csvGameObject.setDate(Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
                    csvGameObjects.add(csvGameObject);
                }

                //sleep(5000);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(CSVGameObject csvGameObject : csvGameObjects){
            System.out.println(csvGameObject);
        }

        return csvGameObjects;
    }

    public List<CSVGameObject> readGameOddsExport(){
        List<CSVGameObject> csvGameObjects = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(Paths.get("C:/Users/Mark/Desktop/GameOddsExport.csv"))) {

            CSVReader csvReader = new CSVReader(reader);
            csvReader.skip(1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
            //Iterator<VINEntry> myUserIterator = csvToBean.iterator();
            String[] nextRecord;
            int i = 0;
            Instant start = Instant.now();
            CSVGameObject csvGameObject = null;
            while ((nextRecord = csvReader.readNext()) != null) {
                csvGameObject = new CSVGameObject();
                StringBuilder stringBuilder = new StringBuilder();
                int gameId = Integer.parseInt(nextRecord[0]);
                Date parsedDate = sdf.parse(nextRecord[1]);
                String awayTeam = nextRecord[2];
                String homeTeam = nextRecord[3];
                double overunder = Double.parseDouble(nextRecord[4]);
                double awayTeamSpread = Double.parseDouble(nextRecord[5]);
                double homeTeamSpread = Double.parseDouble(nextRecord[6]);
                double awayTeamSpreadOdds = Double.parseDouble(nextRecord[7]);
                double homeTeamSpreadOdds = Double.parseDouble(nextRecord[8]);
                double awayTeamMoneyLine = Double.parseDouble(nextRecord[9]);
                double homeTeamMoneyLine = Double.parseDouble(nextRecord[10]);
                csvGameObject.setGameId(gameId);
                csvGameObject.setAwayTeamName(awayTeam);
                csvGameObject.setHomeTeamName(homeTeam);
                csvGameObject.setAwayTeamMoneyLine(awayTeamMoneyLine);
                csvGameObject.setAwayTeamSpread(awayTeamSpread);
                csvGameObject.setAwayTeamSpreadOdds(awayTeamSpreadOdds);
                csvGameObject.setHomeTeamMoneyLine(homeTeamMoneyLine);
                csvGameObject.setHomeTeamSpread(homeTeamSpread);
                csvGameObject.setHomeTeamSpreadOdds(homeTeamSpreadOdds);
                csvGameObject.setOverUnder(overunder);
                csvGameObject.setDate(parsedDate);
                csvGameObjects.add(csvGameObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(CSVGameObject csvGameObject : csvGameObjects){
            System.out.println(csvGameObject);
        }

        return csvGameObjects;
    }


    public String convertNameToTriCode(String name){
        return switch (name) {
            case "GoldenState" -> "GSW";
            case "Brooklyn" -> "BKN";
            case "LAClippers" -> "LAC";
            case "LALakers" -> "LAL";
            case "Charlotte" -> "CHA";
            case "Cleveland" -> "CLE";
            case "NewYork" -> "NYK";
            case "Indiana" -> "IND";
            case "Miami" -> "MIA";
            case "Orlando" -> "ORL";
            case "Washington" -> "WAS";
            case "Philadelphia" -> "PHI";
            case "Milwaukee" -> "MIL";
            case "Boston" -> "BOS";
            case "NewOrleans" -> "NOP";
            case "Toronto" -> "TOR";
            case "Atlanta" -> "ATL";
            case "Chicago" -> "CHI";
            case "SanAntonio" -> "SAS";
            case "Memphis" -> "MEM";
            case "Detroit" -> "DET";
            case "Minnesota" -> "MIN";
            case "Sacramento" -> "SAC";
            case "Denver" -> "DEN";
            case "Utah" -> "UTA";
            case "Portland" -> "POR";
            case "Dallas" -> "DAL";
            case "Phoenix" -> "PHX";
            case "OklahomaCity" -> "OKC";
            case "Houston" -> "HOU";
            default -> null;
        };
    }
}
