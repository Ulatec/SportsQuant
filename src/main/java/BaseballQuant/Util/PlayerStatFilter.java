package BaseballQuant.Util;

import BaseballQuant.Model.*;
import SportsQuant.Model.Player;
import SportsQuant.Model.PlayerGamePerformance;
import com.landawn.abacus.util.stream.Stream;
import org.apache.commons.math3.stat.regression.SimpleRegression;
//import jdk.incubator.vector.DoubleVector;
//import jdk.incubator.vector.FloatVector;
//import jdk.incubator.vector.VectorSpecies;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class PlayerStatFilter {
    private double minuteThreshold = 1.0;

    public double getMinuteThreshold() {
        return minuteThreshold;
    }

    public void setMinuteThreshold(double minuteThreshold) {
        this.minuteThreshold = minuteThreshold;
    }

    public int getPlayerPointsLastNGames(int N, MLBPlayer player, Date date){
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().minusHours(2);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        int totalRuns = 0;
        int games = 0;
        List<MLBPlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(player.getMLBPlayerGamePerformances());
        for(int i = 0; i<playerGamePerformanceList.size(); i++){
            if(games>=N){
                break;
            }
            if(playerGamePerformanceList.get(i).getDate().before(beforeDate) && playerGamePerformanceList.get(i).getAtbats() > 0){
                totalRuns = totalRuns + playerGamePerformanceList.get(i).getRuns();
                games++;
            }else{
            }
        }
        return totalRuns;
    }


    public int getPitcherGivenRunsLastNGames(int N, MLBPitcher pitcher, Date date){
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(2);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        int totalRuns = 0;
        int games = 0;
        List<MLBPitcherPerformance> mlbPitcherPerformanceList = new ArrayList<>(pitcher.getMLBPitcherGamePerformances());
        mlbPitcherPerformanceList.sort(Comparator.comparing(MLBPitcherPerformance::getDate).reversed());
        for(int i = 0; i<mlbPitcherPerformanceList.size(); i++){
        //for(MLBPitcherPerformance mlbPitcherPerformance : pitcher.getMLBPitcherGamePerformances() ){
            if(games>=N){
                break;
            }
            if(mlbPitcherPerformanceList.get(i).getDate().before(beforeDate) && mlbPitcherPerformanceList.get(i).getInningsPitched() > 0){
                totalRuns = totalRuns + mlbPitcherPerformanceList.get(i).getRunsGivenUp();
                games++;
            }
        }
        return totalRuns;
    }
    public double getPlayerPointsLastNGamesStdDev(int N, MLBPlayer player, Date date){

        List<MLBPlayerGamePerformance> playerGamePerformanceList = new ArrayList<>( player.getPlayerGamePerformances());
        List<Double> temp = new ArrayList<>();
        for(int i = 0; i<playerGamePerformanceList.size(); i++){
        //for(MLBPlayerGamePerformance playerGamePerformance : playerGamePerformanceList){
            //TODO:: TEST ENABLING DATE CHECKING HERE:
            //if(playerGamePerformanceList.get(i).getDate().before(date) && playerGamePerformanceList.get(i).getAtbats()> 0) {
                temp.add((double) playerGamePerformanceList.get(i).getRuns());
           //}
        }

        double[] data = temp.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
        double playerPPG = getPlayerPointsPerGameLastNGames(N,player,date);
        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += Math.pow(data[i] - playerPPG, 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + Math.sqrt(variance));
        return Math.sqrt(variance);
    }
    public float getPlayerPointsLastNGamesStdDev(int N, MLBPlayer player, Date date, double average){

        List<MLBPlayerGamePerformance> playerGamePerformanceList = new ArrayList<>( player.getPlayerGamePerformances());
        List<Float> temp = new ArrayList<>();
        for(int i = 0; i<playerGamePerformanceList.size(); i++){
            //for(MLBPlayerGamePerformance playerGamePerformance : playerGamePerformanceList){
            //TODO:: TEST ENABLING DATE CHECKING HERE:
            //if(playerGamePerformanceList.get(i).getDate().before(date) && playerGamePerformanceList.get(i).getAtbats()> 0) {
            temp.add((float) playerGamePerformanceList.get(i).getRuns());
            //}
        }
        float[] data = Stream.of(temp).mapToFloat(Float::valueOf).toArray();
//        float[] data = temp.stream()
//                .mapToDouble(Float::doubleValue)
//                .toArray();

        float variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += Math.pow(data[i] - average, 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + Math.sqrt(variance));
        return (float) Math.sqrt(variance);
    }
    public double getFieldingStdDevLastNGames(int N, MLBPlayer mlbPlayer, Date date){

        List<MLBPlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(mlbPlayer.getMLBPlayerGamePerformances());
        List<Double> temp = new ArrayList<>();
        for(int i = 0; i<playerGamePerformanceList.size(); i++){
            //for(MLBPlayerGamePerformance playerGamePerformance : playerGamePerformanceList){
            if(playerGamePerformanceList.get(i).getDate().before(date) && playerGamePerformanceList.get(i).getFieldingpercentage()> 0) {
                temp.add((double) playerGamePerformanceList.get(i).getFieldingpercentage());
            }
        }
        double[] data = temp.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
        double average = getFieldingLastNGames(N,mlbPlayer,date);
        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += Math.pow(data[i] - average, 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + Math.sqrt(variance));
        return Math.sqrt(variance);
    }
    public double getFieldingStdDevLastNGames(int N, MLBPlayer mlbPlayer, Date date, double average){

        List<MLBPlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(mlbPlayer.getMLBPlayerGamePerformances());
        playerGamePerformanceList.sort(Comparator.comparing(MLBPlayerGamePerformance::getDate).reversed());
        List<Double> temp = new ArrayList<>();
        for(int i = 0; i<playerGamePerformanceList.size(); i++){
            //for(MLBPlayerGamePerformance playerGamePerformance : playerGamePerformanceList){
            if(playerGamePerformanceList.get(i).getDate().before(date) && playerGamePerformanceList.get(i).getFieldingpercentage()> 0) {
                temp.add((double) playerGamePerformanceList.get(i).getFieldingpercentage());
            }
        }
        double[] data = temp.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();

        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += Math.pow(data[i] - average, 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + Math.sqrt(variance));
        return Math.sqrt(variance);
    }

    public double getFieldingLastNGames(int N, MLBPlayer mlbPlayer, Date date){
        //long start = System.currentTimeMillis();
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(2);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        List<MLBPlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(mlbPlayer.getMLBPlayerGamePerformances());
        playerGamePerformanceList.sort(Comparator.comparing(MLBPlayerGamePerformance::getDate).reversed());
        List<Double> fieldingDoubles = new ArrayList<>();
        //Collections.sort(playerGamePerformanceList, Comparator.comparing(MLBPlayerGamePerformance::getDate).reversed());
        int games = 0;
        for(int i = 0; i<playerGamePerformanceList.size(); i++){
        //for(MLBPlayerGamePerformance mlbPlayerGamePerformance : mlbPlayer.getMLBPlayerGamePerformances() ){
            if(games>=N){
                break;
            }
            if(playerGamePerformanceList.get(i).getDate().before(beforeDate) && playerGamePerformanceList.get(i).getFieldingpercentage() > 0){
                fieldingDoubles.add(playerGamePerformanceList.get(i).getFieldingpercentage());
                games++;
            }
        }
        DoubleSummaryStatistics doubleSummaryStatistics = fieldingDoubles.stream().mapToDouble((x) -> x).summaryStatistics();
        //System.out.println(playerGamePerformanceList.size() + " took " + (System.currentTimeMillis() - start)  + " ms");
        return doubleSummaryStatistics.getAverage();
    }
    public double getFieldingRateOfChange(int N, MLBPlayer mlbPlayer, Date date){
        //long start = System.currentTimeMillis();
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(2);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        List<MLBPlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(mlbPlayer.getMLBPlayerGamePerformances());
        playerGamePerformanceList.sort(Comparator.comparing(MLBPlayerGamePerformance::getDate).reversed());
        List<Double> fieldingDoubles = new ArrayList<>();
        //Collections.sort(playerGamePerformanceList, Comparator.comparing(MLBPlayerGamePerformance::getDate).reversed());
        int games = 0;
        for(int i = 0; i<playerGamePerformanceList.size(); i++){
            //for(MLBPlayerGamePerformance mlbPlayerGamePerformance : mlbPlayer.getMLBPlayerGamePerformances() ){
            if(games>=N){
                break;
            }
            if(playerGamePerformanceList.get(i).getDate().before(beforeDate) && playerGamePerformanceList.get(i).getFieldingpercentage() > 0){
                fieldingDoubles.add(playerGamePerformanceList.get(i).getFieldingpercentage());
                games++;
            }
        }
        //DoubleSummaryStatistics doubleSummaryStatistics = fieldingDoubles.stream().mapToDouble((x) -> x).summaryStatistics();
        SimpleRegression simpleRegression = new SimpleRegression();
        for(int x = 0; x < fieldingDoubles.size(); x++){
            simpleRegression.addData(x,fieldingDoubles.get(x));
        }
        //System.out.println(playerGamePerformanceList.size() + " took " + (System.currentTimeMillis() - start)  + " ms");
        return simpleRegression.getSlope();
    }


    public double getPitcherGivenRunsLastNGamesStdDev(int N, MLBPitcher pitcher, Date date){

        List<MLBPitcherPerformance> playerGamePerformanceList = new ArrayList<>(pitcher.getMLBPitcherGamePerformances());
        List<Double> temp = new ArrayList<>();
        for(int i = 0; i<playerGamePerformanceList.size(); i++){
        //for(MLBPitcherPerformance playerGamePerformance : playerGamePerformanceList){
            if(playerGamePerformanceList.get(i).getDate().before(date) && playerGamePerformanceList.get(i).getInningsPitched() > 0){
                temp.add((double) playerGamePerformanceList.get(i).getRunsGivenUp());
            }
        }

        double[] data = temp.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
        double average = getPitcherGivenRunsPerGameLastNGames(N,pitcher,date);
        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += Math.pow(data[i] - average, 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + Math.sqrt(variance));
        return Math.sqrt(variance);
    }
    public float getPitcherGivenRunsLastNGamesStdDev(int N, MLBPitcher pitcher, Date date, double average){

        List<MLBPitcherPerformance> playerGamePerformanceList = new ArrayList<>(pitcher.getMLBPitcherGamePerformances());
        playerGamePerformanceList.sort(Comparator.comparing(MLBPitcherPerformance::getDate).reversed());
        List<Double> temp = new ArrayList<>();
        for(int i = 0; i<playerGamePerformanceList.size(); i++){
            //for(MLBPitcherPerformance playerGamePerformance : playerGamePerformanceList){
            if(playerGamePerformanceList.get(i).getDate().before(date) && playerGamePerformanceList.get(i).getInningsPitched() > 0){
                temp.add((double) playerGamePerformanceList.get(i).getRunsGivenUp());
            }
        }

        double[] data = temp.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
        float variance = 0;
        for (int i = 0; i < temp.size(); i++) {
            variance += (float)Math.pow(data[i] - average, 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + Math.sqrt(variance));
        return (float)Math.sqrt(variance);
    }
    public double getPitcherGivenRunsPerGameLastNGames(int N, MLBPitcher pitcher, Date date){
        int totalRuns = 0;
        int games = 0;
        List<MLBPitcherPerformance> mlbPitcherPerformanceList = new ArrayList<>(pitcher.getMLBPitcherGamePerformances());
        mlbPitcherPerformanceList.sort(Comparator.comparing(MLBPitcherPerformance::getDate).reversed());
        for(int i = 0; i<mlbPitcherPerformanceList.size(); i++){
        //for(MLBPitcherPerformance mlbPitcherPerformance : pitcher.getMLBPitcherGamePerformances()){
            if(games>=N){
                break;
            }
            //TODO:: TEST MODIFICATION. WAS NOT CHECKING PITCHING DATES PREVIOUSLY.
            if(mlbPitcherPerformanceList.get(i).getDate().before(date) && mlbPitcherPerformanceList.get(i).getInningsPitched() > 0){
                totalRuns = totalRuns + mlbPitcherPerformanceList.get(i).getRunsGivenUp();
                games++;
            }
        }
        //System.out.println("Points Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints/N);
        return (double)totalRuns/(double)N;
    }
    public float getPlayerPointsPerGameLastNGames(int N, MLBPlayer player, Date date){
        int totalRuns = 0;
        int games = 0;
        List<MLBPlayerGamePerformance> mlbPlayerGamePerformanceList = new ArrayList<>(player.getMLBPlayerGamePerformances());
        mlbPlayerGamePerformanceList.sort(Comparator.comparing(MLBPlayerGamePerformance::getDate).reversed());
       // for(MLBPlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
        for(int i = 0; i<mlbPlayerGamePerformanceList.size(); i++){
            if(games>=N){
                break;
            }
            if(mlbPlayerGamePerformanceList.get(i).getDate().before(date) && mlbPlayerGamePerformanceList.get(i).getAtbats() > 0){
                totalRuns = totalRuns + mlbPlayerGamePerformanceList.get(i).getRuns();
                games++;
            }
        }

        //System.out.println("Points Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints/N);
        return (float)totalRuns/(float)N;
    }
    public float getPitcherRunsGivenUpRateOfChange(int N, MLBPitcher pitcher, Date date){
        int totalRuns = 0;
        int games = 0;
        List<MLBPitcherPerformance> mlbPitcherPerformanceList = new ArrayList<>(pitcher.getMLBPitcherGamePerformances());
        mlbPitcherPerformanceList.sort(Comparator.comparing(MLBPitcherPerformance::getDate).reversed());
        List<Integer> valueList = new ArrayList<>();
        for(int i = 0; i<mlbPitcherPerformanceList.size(); i++){
            //for(MLBPitcherPerformance mlbPitcherPerformance : pitcher.getMLBPitcherGamePerformances()){
            if(games>=N){
                break;
            }
            //TODO:: TEST MODIFICATION. WAS NOT CHECKING PITCHING DATES PREVIOUSLY.
            if(mlbPitcherPerformanceList.get(i).getDate().before(date) && mlbPitcherPerformanceList.get(i).getInningsPitched() > 0){
                totalRuns = totalRuns + mlbPitcherPerformanceList.get(i).getRunsGivenUp();
                valueList.add(mlbPitcherPerformanceList.get(i).getRunsGivenUp());
                games++;
            }
        }


        SimpleRegression simpleRegression = new SimpleRegression();
        for(int x = 0; x < valueList.size(); x++){
            simpleRegression.addData(x,valueList.get(x));
        }

        //System.out.println("Points Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints/N);
        return (float) simpleRegression.getSlope();
    }

    public float getPlayerPointsRateOfChange(int N, MLBPlayer player, Date date){
        int totalRuns = 0;
        int games = 0;
        List<MLBPlayerGamePerformance> mlbPlayerGamePerformanceList = new ArrayList<>(player.getMLBPlayerGamePerformances());
        mlbPlayerGamePerformanceList.sort(Comparator.comparing(MLBPlayerGamePerformance::getDate).reversed());
        List<Integer> valueList = new ArrayList<>();
        // for(MLBPlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
        for(int i = 0; i<mlbPlayerGamePerformanceList.size(); i++){
            if(games>=N){
                break;
            }
            if(mlbPlayerGamePerformanceList.get(i).getDate().before(date) && mlbPlayerGamePerformanceList.get(i).getAtbats() > 0){
                totalRuns = totalRuns + mlbPlayerGamePerformanceList.get(i).getRuns();
                valueList.add(mlbPlayerGamePerformanceList.get(i).getRuns());
                games++;
            }
        }
        SimpleRegression simpleRegression = new SimpleRegression();
        for(int x = 0; x < valueList.size(); x++){
            simpleRegression.addData(x,valueList.get(x));
        }

        //System.out.println("Points Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints/N);
        return (float) simpleRegression.getSlope();
    }

    public double getPlayerStolenBasesLastNGamesStdDev(int N, MLBPlayer player, Date date){
        List<MLBPlayerGamePerformance> playerGamePerformanceList = new ArrayList<>( player.getPlayerGamePerformances());
        List<Double> temp = new ArrayList<>();
        for(int i = 0; i<playerGamePerformanceList.size(); i++){
            //for(MLBPlayerGamePerformance playerGamePerformance : playerGamePerformanceList){
            //TODO:: TEST ENABLING DATE CHECKING HERE:
            //if(playerGamePerformanceList.get(i).getDate().before(date) && playerGamePerformanceList.get(i).getAtbats()> 0) {
            temp.add((double) playerGamePerformanceList.get(i).getStolenBases());
            //}
        }
        double[] data = temp.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
        double playerStolenBasesPerGame = getPlayerStolenBasesPerGameLastNGames(N,player,date);
        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += Math.pow(data[i] - playerStolenBasesPerGame, 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + Math.sqrt(variance));
        return Math.sqrt(variance);
    }
    public double getPlayerStolenBasesLastNGamesStdDev(int N, MLBPlayer player, Date date, double average){
        List<MLBPlayerGamePerformance> playerGamePerformanceList = new ArrayList<>( player.getPlayerGamePerformances());
        List<Double> temp = new ArrayList<>();
        for(int i = 0; i<playerGamePerformanceList.size(); i++){
            //for(MLBPlayerGamePerformance playerGamePerformance : playerGamePerformanceList){
            //TODO:: TEST ENABLING DATE CHECKING HERE:
            //if(playerGamePerformanceList.get(i).getDate().before(date) && playerGamePerformanceList.get(i).getAtbats()> 0) {
            temp.add((double) playerGamePerformanceList.get(i).getStolenBases());
            //}
        }
        double[] data = temp.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += Math.pow(data[i] - average, 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + Math.sqrt(variance));
        return Math.sqrt(variance);
    }

    public strictfp double getPlayerStolenBasesPerGameLastNGames(int N, MLBPlayer player, Date date){
        int totalStolenBases = 0;
        int games = 0;
        List<MLBPlayerGamePerformance> mlbPlayerGamePerformanceList = new ArrayList<>(player.getMLBPlayerGamePerformances());
        // for(MLBPlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
        for(int i = 0; i<mlbPlayerGamePerformanceList.size(); i++){
            if(games>=N){
                break;
            }
            if(mlbPlayerGamePerformanceList.get(i).getDate().before(date) && mlbPlayerGamePerformanceList.get(i).getAtbats() > 0){
                totalStolenBases = totalStolenBases + mlbPlayerGamePerformanceList.get(i).getStolenBases();
                games++;
            }
        }

        //System.out.println("Points Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints/N);
        return (double)totalStolenBases/(double)N;
    }

    public strictfp double getPlayerStolenBasesRateOfChange(int N, MLBPlayer player, Date date){

        List<MLBPlayerGamePerformance> mlbPlayerGamePerformanceList = new ArrayList<>(player.getMLBPlayerGamePerformances());
        mlbPlayerGamePerformanceList.sort(Comparator.comparing(MLBPlayerGamePerformance::getDate).reversed());
        List<Double> temp = new ArrayList<>();

        for(int i = 0; i<mlbPlayerGamePerformanceList.size(); i++){
            //for(MLBPitcherPerformance playerGamePerformance : playerGamePerformanceList){
            if(mlbPlayerGamePerformanceList.get(i).getDate().before(date) && mlbPlayerGamePerformanceList.get(i).getAtbats() > 0){
                temp.add((double) mlbPlayerGamePerformanceList.get(i).getStolenBases());
            }
        }

        SimpleRegression simpleRegression = new SimpleRegression();
        for(int x = 0; x < temp.size(); x++){
            simpleRegression.addData(x,temp.get(x));
        }

        //System.out.println("Points Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints/N);
        return (float) simpleRegression.getSlope();
    }

    public strictfp double getPlayerWalksRateOfChange(int N, MLBPlayer player, Date date){

        List<MLBPlayerGamePerformance> mlbPlayerGamePerformanceList = new ArrayList<>(player.getMLBPlayerGamePerformances());
        mlbPlayerGamePerformanceList.sort(Comparator.comparing(MLBPlayerGamePerformance::getDate).reversed());
        List<Double> temp = new ArrayList<>();

        for(int i = 0; i<mlbPlayerGamePerformanceList.size(); i++){
            //for(MLBPitcherPerformance playerGamePerformance : playerGamePerformanceList){
            if(mlbPlayerGamePerformanceList.get(i).getDate().before(date) && mlbPlayerGamePerformanceList.get(i).getAtbats() > 0){
                temp.add((double) mlbPlayerGamePerformanceList.get(i).getWalks());
            }
        }

        SimpleRegression simpleRegression = new SimpleRegression();
        for(int x = 0; x < temp.size(); x++){
            simpleRegression.addData(x,temp.get(x));
        }

        //System.out.println("Points Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints/N);
        return (float) simpleRegression.getSlope();
    }

    public void trimPlayerGameHistory(int N, MLBPlayer player, Date date){


        LocalDateTime localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().minusHours(2);
        Date beforeDate = Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant());
        List<MLBPlayerGamePerformance> playerGamePerformances = new ArrayList<>(player.getPlayerGamePerformances());
        Set<MLBPlayerGamePerformance> cleanedPerformances = new HashSet<>();
        playerGamePerformances.sort(Comparator.comparing(MLBPlayerGamePerformance::getDate));
        int games = 0;
        for(int i = playerGamePerformances.size()-1; i>0;i--){
            if(games>=N){
                break;
            }
            if(playerGamePerformances.get(i).getDate().before(beforeDate)){
                cleanedPerformances.add(playerGamePerformances.get(i));
                games++;
            }
        }
        player.setPlayerGamePerformances(cleanedPerformances);
    }
    public void trimPlayerGameHistory(int N, MLBPitcher pitcher, Date date){
        LocalDateTime localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().minusHours(2);
        Date beforeDate = Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant());
        List<MLBPitcherPerformance> playerGamePerformances = new ArrayList<>(pitcher.getMLBPitcherGamePerformances());
        Set<MLBPitcherPerformance> cleanedPerformances = new HashSet<>();
        playerGamePerformances.sort(Comparator.comparing(MLBPitcherPerformance::getDate));

        int games = 0;
        for(int i = playerGamePerformances.size()-1; i>0;i--){
            if(games>=N){
                break;
            }
            if(playerGamePerformances.get(i).getDate().before(beforeDate)){
                cleanedPerformances.add(playerGamePerformances.get(i));
                games++;
            }
        }
        pitcher.setMLBPitcherGamePerformances(cleanedPerformances);
    }
}
