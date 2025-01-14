package BaseballQuant.Util;

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
        try (Reader reader = Files.newBufferedReader(Paths.get("C:/Users/ulate/Desktop/mlb-odds-2016.csv"))) {

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
                        localDate = LocalDate.of(2016, Integer.parseInt(dateString.substring(0, 2)), Integer.parseInt(dateString.substring(2)));
                    }else {
                        localDate = LocalDate.of(2016, Integer.parseInt(dateString.substring(0, 1)), Integer.parseInt(dateString.substring(1)));
                    }
                    stringBuilder.append(localDate);
                    String homeOrAway = nextRecord[2];
                    if(homeOrAway.equals("V")){
                        stringBuilder.append(" Away ");
                    }else{
                        stringBuilder.append(" Home ");
                    }
                    String teamTriCode = nextRecord[3];
                    stringBuilder.append(teamTriCode);
                    stringBuilder.append(" ");
                    String pitcherName = nextRecord[4].replace("-L","").replace("--", "").replace(" ", "").substring(2);
                    stringBuilder.append(pitcherName);
                    stringBuilder.append(" ");
                    double moneyLine = Double.parseDouble(nextRecord[16]);
                    double runLine = Double.parseDouble(nextRecord[17]);
                    stringBuilder.append(runLine);
                    stringBuilder.append(" ");
                    double runLineOdds = Double.parseDouble(nextRecord[18]);
                    stringBuilder.append(runLineOdds);
                    stringBuilder.append(" ");
                    //System.out.println(stringBuilder);
                    csvGameObject.setAwayTeamMoneyLine(moneyLine);
                    csvGameObject.setAwayTeamSpread(runLine);
                    csvGameObject.setAwayTeamSpreadOdds(runLineOdds);
                    csvGameObject.setAwayTeamTricode(teamTriCode);
                    csvGameObject.setAwayPitcherName(pitcherName);
                    csvGameObject.setOverUnder(Double.parseDouble(nextRecord[21]));
                    csvGameObject.setDate(Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
                }else{
                    StringBuilder stringBuilder = new StringBuilder();
                    String dateString = nextRecord[0];
                    LocalDate localDate;
                    if(dateString.length() > 3){
                        localDate = LocalDate.of(2016, Integer.parseInt(dateString.substring(0, 2)), Integer.parseInt(dateString.substring(2)));
                    }else {
                        localDate = LocalDate.of(2016, Integer.parseInt(dateString.substring(0, 1)), Integer.parseInt(dateString.substring(1)));
                    }
                    stringBuilder.append(localDate);
                    String homeOrAway = nextRecord[2];
                    if(homeOrAway.equals("V")){
                        stringBuilder.append(" Away ");
                    }else{
                        stringBuilder.append(" Home ");
                    }
                    String teamTriCode = nextRecord[3];
                    stringBuilder.append(teamTriCode);
                    stringBuilder.append(" ");
                    String pitcherName = nextRecord[4].replace("-L","").replace("--", "").substring(2);
                    stringBuilder.append(pitcherName);
                    stringBuilder.append(" ");
                    double moneyLine = Double.parseDouble(nextRecord[16]);
                    double runLine = Double.parseDouble(nextRecord[17]);
                    stringBuilder.append(runLine);
                    stringBuilder.append(" ");
                    double runLineOdds = Double.parseDouble(nextRecord[18]);
                    stringBuilder.append(runLineOdds);
                    stringBuilder.append(" ");
                    //System.out.println(stringBuilder);
                    csvGameObject.setHomeTeamMoneyLine(moneyLine);
                    csvGameObject.setHomeTeamSpread(runLine);
                    csvGameObject.setHomeTeamSpreadOdds(runLineOdds);
                    csvGameObject.setHomeTeamTricode(teamTriCode);
                    csvGameObject.setHomePitcherName(pitcherName);
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

}
