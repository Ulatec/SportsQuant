package SportsQuant.Util;

import BaseballQuant.Model.MLBPitcher;
import BaseballQuant.Model.MLBPitcherPerformance;
import BaseballQuant.Model.MLBPlayer;
import BaseballQuant.Model.MLBPlayerGamePerformance;
import SportsQuant.Model.Game;
import SportsQuant.Model.Player;
import SportsQuant.Model.PlayerGamePerformance;
import com.landawn.abacus.util.BigIntegerSummaryStatistics;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.text.SimpleDateFormat;
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

    public int getPlayerStealsLastNGames(int N, Player player, Date date, double gameTimeThreshold){
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(6);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        int totalSteals = 0;
        int games = 0;
        for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()) {
            if (games >= N) {
                break;
            }
            if (playerGamePerformance.getDate().before(beforeDate) && playerGamePerformance.getMinutes() >= gameTimeThreshold) {
                totalSteals = totalSteals + playerGamePerformance.getSteals();
                //System.out.println("GameDate: " + simpleDateFormat.format(playerGamePerformance.getDate()));
                games++;
            }


        }
        //System.out.println("Total Points For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints);
        return totalSteals;
    }

    public int getPlayerTurnoversLastNGames(int N, Player player, Date date, int dayLookbackCap, double gameTimeThreshold){
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(6);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date afterDate = Date.from(localDateTime.minusDays(dayLookbackCap).atZone(ZoneId.systemDefault()).toInstant());
        int totalTurnovers = 0;
        int games = 0;
        for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
            if(games>=N){
                break;
            }
            if(playerGamePerformance.getDate().before(beforeDate) && playerGamePerformance.getMinutes()>= gameTimeThreshold){
                if (dayLookbackCap > 0) {
                    if (playerGamePerformance.getDate().after(afterDate)) {
                        totalTurnovers = totalTurnovers + playerGamePerformance.getTurnovers();
                        games++;
                    }
                }else{
                    totalTurnovers = totalTurnovers + playerGamePerformance.getTurnovers();
                    //System.out.println("GameDate: " + simpleDateFormat.format(playerGamePerformance.getDate()));
                    games++;
                }
            }
        }
        //System.out.println("Total Points For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints);
        return totalTurnovers;
    }

    public int getPlayerBlocksLastNGames(int N, Player player, Date date, int dayLookbackCap, double gameTimeThreshold){
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(6);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date afterDate = Date.from(localDateTime.minusDays(dayLookbackCap).atZone(ZoneId.systemDefault()).toInstant());
        //System.out.println("looking for blocks on " + player.getFirstName() + " " + player.getLastName() + " before " + date);
        int totalBlocks = 0;
        int games = 0;
        for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
            if(games>=N){
                break;
            }
            if(playerGamePerformance.getDate().before(beforeDate) && playerGamePerformance.getMinutes()>= gameTimeThreshold) {
                if (dayLookbackCap > 0) {
                    if (playerGamePerformance.getDate().after(afterDate)) {
                        totalBlocks = totalBlocks + playerGamePerformance.getBlocks();
                        games++;
                    }
                } else {
                    totalBlocks = totalBlocks + playerGamePerformance.getBlocks();
                    //System.out.println("GameDate: " + simpleDateFormat.format(playerGamePerformance.getDate()));
                    games++;
                }
            }

        }
        //System.out.println("Total Points For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints);
        return totalBlocks;
    }
    public double getForecastedPlayerReboundsWithDerivative(int N, Player player, Date date, int dayLookbackCap, double gameTimeThreshold){
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(6);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date afterDate = Date.from(localDateTime.minusDays(dayLookbackCap).atZone(ZoneId.systemDefault()).toInstant());
        int totalRebounds = 0;
        int games = 0;
        List<Integer> integers = new ArrayList<>();
        List<PlayerGamePerformance> performances = new ArrayList<>(player.getPlayerGamePerformances());
        performances.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        for(int i = 0; i< performances.size(); i++){
            //for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances() ){
            if(games>=N){
                break;
            }
            if(performances.get(i).getDate().before(beforeDate) && performances.get(i).getMinutes()>= gameTimeThreshold){
                if(dayLookbackCap > 0){
                    if(performances.get(i).getDate().after(afterDate)){
                        totalRebounds = totalRebounds + performances.get(i).getRebounds();
                        integers.add(performances.get(i).getRebounds());
                        games++;
                    }
                }else{
                    totalRebounds = totalRebounds + performances.get(i).getRebounds();
                    integers.add(performances.get(i).getRebounds());
                    games++;
                }

            }
        }
        List<Integer> differentials = new ArrayList<>();
        for(int i = 0; i<integers.size(); i++){
            if(i+1< integers.size()){
                differentials.add(integers.get(i+1) - integers.get(i));
            }
        }
        IntSummaryStatistics intSummaryStatistics = differentials.stream().mapToInt(x -> x).summaryStatistics();
        //double forcastedPoints = ((double)totalPoints/games) + intSummaryStatistics.getAverage();
        if(integers.size()>0) {
            return  (double)integers.get(0) + intSummaryStatistics.getAverage();
        }
        return 0.0;
    }


    public double getForecastedPlayerTurnoversWithDerivative(int N, Player player, Date date, int dayLookbackCap, double gameTimeThreshold){
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(6);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date afterDate = Date.from(localDateTime.minusDays(dayLookbackCap).atZone(ZoneId.systemDefault()).toInstant());
        int totalTurnovers = 0;
        int games = 0;
        List<Integer> integers = new ArrayList<>();
        List<PlayerGamePerformance> performances = new ArrayList<>(player.getPlayerGamePerformances());
        performances.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        for(int i = 0; i< performances.size(); i++){
            //for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances() ){
            if(games>=N){
                break;
            }
            if(performances.get(i).getDate().before(beforeDate) && performances.get(i).getMinutes()>= gameTimeThreshold){
                if(dayLookbackCap > 0){
                    if(performances.get(i).getDate().after(afterDate)){
                        totalTurnovers = totalTurnovers + performances.get(i).getTurnovers();
                        integers.add(performances.get(i).getTurnovers());
                        games++;
                    }
                }else{
                    totalTurnovers = totalTurnovers + performances.get(i).getTurnovers();
                    integers.add(performances.get(i).getTurnovers());
                    games++;
                }

            }
        }
        List<Integer> differentials = new ArrayList<>();
        for(int i = 0; i<integers.size(); i++){
            if(i+1< integers.size()){
                differentials.add(integers.get(i+1) - integers.get(i));
            }
        }
        IntSummaryStatistics intSummaryStatistics = differentials.stream().mapToInt(x -> x).summaryStatistics();
        //double forcastedPoints = ((double)totalPoints/games) + intSummaryStatistics.getAverage();
        if(integers.size()>0) {
            return  (double)integers.get(0) + intSummaryStatistics.getAverage();
        }
        return 0.0;
    }

    public double getForecastedPlayerFoulsWithDerivative(int N, Player player, Date date, int dayLookbackCap, double gameTimeThreshold){
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(6);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date afterDate = Date.from(localDateTime.minusDays(dayLookbackCap).atZone(ZoneId.systemDefault()).toInstant());
        int totalFouls = 0;
        int games = 0;
        List<Integer> integers = new ArrayList<>();
        List<PlayerGamePerformance> performances = new ArrayList<>(player.getPlayerGamePerformances());
        performances.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        for(int i = 0; i< performances.size(); i++){
            //for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances() ){
            if(games>=N){
                break;
            }
            if(performances.get(i).getDate().before(beforeDate) && performances.get(i).getMinutes()>= gameTimeThreshold){
                if(dayLookbackCap > 0){
                    if(performances.get(i).getDate().after(afterDate)){
                        totalFouls = totalFouls + performances.get(i).getFouls();
                        integers.add(performances.get(i).getFouls());
                        games++;
                    }
                }else{
                    totalFouls = totalFouls + performances.get(i).getFouls();
                    integers.add(performances.get(i).getFouls());
                    games++;
                }

            }
        }
        List<Integer> differentials = new ArrayList<>();
        for(int i = 0; i<integers.size(); i++){
            if(i+1< integers.size()){
                differentials.add(integers.get(i+1) - integers.get(i));
            }
        }
        IntSummaryStatistics intSummaryStatistics = differentials.stream().mapToInt(x -> x).summaryStatistics();
        //double forcastedPoints = ((double)totalPoints/games) + intSummaryStatistics.getAverage();
        if(integers.size()>0) {
            return  (double)integers.get(0) + intSummaryStatistics.getAverage();
        }
        return 0.0;
    }

    public double getForecastedPlayerStealsWithDerivative(int N, Player player, Date date, int dayLookbackCap, double gameTimeThreshold){
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(6);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date afterDate = Date.from(localDateTime.minusDays(dayLookbackCap).atZone(ZoneId.systemDefault()).toInstant());
        int totalSteals = 0;
        int games = 0;
        List<Integer> integers = new ArrayList<>();
        List<PlayerGamePerformance> performances = new ArrayList<>(player.getPlayerGamePerformances());
        performances.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        for(int i = 0; i< performances.size(); i++){
            //for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances() ){
            if(games>=N){
                break;
            }
            if(performances.get(i).getDate().before(beforeDate) && performances.get(i).getMinutes()>= gameTimeThreshold){
                if(dayLookbackCap > 0){
                    if(performances.get(i).getDate().after(afterDate)){
                        totalSteals = totalSteals + performances.get(i).getSteals();
                        integers.add(performances.get(i).getSteals());
                        games++;
                    }
                }else{
                    totalSteals = totalSteals + performances.get(i).getSteals();
                    integers.add(performances.get(i).getSteals());
                    games++;
                }

            }
        }
        List<Integer> differentials = new ArrayList<>();
        for(int i = 0; i<integers.size(); i++){
            if(i+1< integers.size()){
                differentials.add(integers.get(i+1) - integers.get(i));
            }
        }
        IntSummaryStatistics intSummaryStatistics = differentials.stream().mapToInt(x -> x).summaryStatistics();
        //double forcastedPoints = ((double)totalPoints/games) + intSummaryStatistics.getAverage();
        if(integers.size()>0) {
            return  (double)integers.get(0) + intSummaryStatistics.getAverage();
        }
        return 0.0;
    }
    public double getForecastedPlayerBlocksWithDerivative(int N, Player player, Date date, int dayLookbackCap, double gameTimeThreshold){
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(6);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date afterDate = Date.from(localDateTime.minusDays(dayLookbackCap).atZone(ZoneId.systemDefault()).toInstant());
        int totalBlocks = 0;
        int games = 0;
        List<Integer> integers = new ArrayList<>();
        List<PlayerGamePerformance> performances = new ArrayList<>(player.getPlayerGamePerformances());
        performances.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        for(int i = 0; i< performances.size(); i++){
            //for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances() ){
            if(games>=N){
                break;
            }
            if(performances.get(i).getDate().before(beforeDate) && performances.get(i).getMinutes()>= gameTimeThreshold){
                if(dayLookbackCap > 0){
                    if(performances.get(i).getDate().after(afterDate)){
                        totalBlocks = totalBlocks + performances.get(i).getBlocks();
                        integers.add(performances.get(i).getBlocks());
                        games++;
                    }
                }else{
                    totalBlocks = totalBlocks + performances.get(i).getBlocks();
                    integers.add(performances.get(i).getBlocks());
                    games++;
                }

            }
        }
        List<Integer> differentials = new ArrayList<>();
        for(int i = 0; i<integers.size(); i++){
            if(i+1< integers.size()){
                differentials.add(integers.get(i+1) - integers.get(i));
            }
        }
        IntSummaryStatistics intSummaryStatistics = differentials.stream().mapToInt(x -> x).summaryStatistics();
        //double forcastedPoints = ((double)totalPoints/games) + intSummaryStatistics.getAverage();
        if(integers.size()>0) {
            return  (double)integers.get(0) + intSummaryStatistics.getAverage();
        }
        return 0.0;
    }

    public double getForecastedPlayerPointsWithDerivative(int N, Player player, Date date, int dayLookbackCap, double gameTimeThreshold){
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(6);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date afterDate = Date.from(localDateTime.minusDays(dayLookbackCap).atZone(ZoneId.systemDefault()).toInstant());
        int totalPoints = 0;
        int games = 0;
        List<Integer> integers = new ArrayList<>();
        List<PlayerGamePerformance> performances = new ArrayList<>(player.getPlayerGamePerformances());
        performances.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        for(int i = 0; i< performances.size(); i++){
        //for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances() ){
            if(games>=N){
                break;
            }
            if(performances.get(i).getDate().before(beforeDate) && performances.get(i).getMinutes()>= gameTimeThreshold){
                if(dayLookbackCap > 0){
                    if(performances.get(i).getDate().after(afterDate)){
                        totalPoints = totalPoints + performances.get(i).getPoints();
                        integers.add(performances.get(i).getPoints());
                        games++;
                    }
                }else{
                    totalPoints = totalPoints + performances.get(i).getPoints();
                    integers.add(performances.get(i).getPoints());
                    games++;
                }

            }
        }
        List<Integer> differentials = new ArrayList<>();
        for(int i = 0; i<integers.size(); i++){
            if(i+1< integers.size()){
                differentials.add(integers.get(i+1) - integers.get(i));
            }
        }
        IntSummaryStatistics intSummaryStatistics = differentials.stream().mapToInt(x -> x).summaryStatistics();
        //double forcastedPoints = ((double)totalPoints/games) + intSummaryStatistics.getAverage();
        if(integers.size()>0) {
            return  (double)integers.get(0) + intSummaryStatistics.getAverage();
        }
        return 0.0;
    }

    public int getPlayerPointsLastNGames(int N, Player player, Date date, int dayLookbackCap, double gameTimeThreshold){
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(6);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date afterDate = Date.from(localDateTime.minusDays(dayLookbackCap).atZone(ZoneId.systemDefault()).toInstant());
        int totalPoints = 0;
        int games = 0;
        for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances() ){
            if(games>=N){
                break;
            }
            if(playerGamePerformance.getDate().before(beforeDate) && playerGamePerformance.getMinutes()>= gameTimeThreshold){
                if(dayLookbackCap > 0){
                    if(playerGamePerformance.getDate().after(afterDate)){
                        totalPoints = totalPoints + playerGamePerformance.getPoints();
                        games++;
                    }
                }else{
                    totalPoints = totalPoints + playerGamePerformance.getPoints();
                    games++;
                }

            }else{
                //System.out.println(playerGamePerformance.getDate() + " is not before " + beforeDate);
            }
        }
        //System.out.println("Total Points For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints);
        return totalPoints;
    }

    public double getStdDevFromBlockList(int N, Player player, Date date, double gameTimeThreshold){

        Set<PlayerGamePerformance> playerGamePerformanceList = player.getPlayerGamePerformances();
        List<Integer> temp = new ArrayList<>();
        for(PlayerGamePerformance playerGamePerformance : playerGamePerformanceList){
            if(playerGamePerformance.getDate().before(date) && playerGamePerformance.getMinutes()>= gameTimeThreshold) {
                temp.add(playerGamePerformance.getBlocks());
            }
        }
        //IntSummaryStatistics stats = temp.stream().mapToInt((x) -> x).summaryStatistics();
        //System.out.println("size for stdDev calc: " + playerGamePerformanceList.size());
        //DoubleSummaryStatistics doubleSummaryStatistics = temp.stream().mapToDouble((x) -> x).summaryStatistics();
        //System.out.println("Total Blocks in stdDevCalc: " + stats.getSum());
        int[] data = temp.stream()
                .mapToInt(Integer::intValue)
                .toArray();

        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += Math.pow(data[i] - getPlayerBlocksPerGameLastNGamesDebug(N,player,date), 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + Math.sqrt(variance));
        return Math.sqrt(variance);
    }



    public double getPlayerBlocksLastNGamesStdDev(int N, Player player, Date date, double gameTimeThreshold){

        Set<PlayerGamePerformance> playerGamePerformanceList = player.getPlayerGamePerformances();
        List<Integer> temp = new ArrayList<>();
        for(PlayerGamePerformance playerGamePerformance : playerGamePerformanceList){
            if(playerGamePerformance.getDate().before(date) && playerGamePerformance.getMinutes()>= gameTimeThreshold) {
                temp.add(playerGamePerformance.getBlocks());
            }
        }
        int[] data = temp.stream()
                .mapToInt(Integer::intValue)
                .toArray();

        double perGame = getPlayerBlocksPerGameLastNGames(N,player,date, gameTimeThreshold);
        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += Math.pow(data[i] - perGame, 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + Math.sqrt(variance));
        return Math.sqrt(variance);
    }
    public double getPlayerStealsLastNGamesStdDev(int N, Player player, Date date, double gameTimeThreshold){

        Set<PlayerGamePerformance> playerGamePerformanceList = player.getPlayerGamePerformances();
        List<Integer> temp = new ArrayList<>();
        for(PlayerGamePerformance playerGamePerformance : playerGamePerformanceList){
            if(playerGamePerformance.getDate().before(date) && playerGamePerformance.getMinutes()> gameTimeThreshold) {
                temp.add(playerGamePerformance.getSteals());
            }
        }
        int[] data = temp.stream()
                .mapToInt(Integer::intValue)
                .toArray();

        double perGame = getPlayerStealsPerGameLastNGames(N,player,date, gameTimeThreshold);
        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += Math.pow(data[i] - perGame, 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + Math.sqrt(variance));
        return Math.sqrt(variance);
    }
    public double getPlayerTurnoversLastNGamesStdDev(int N, Player player, Date date, double gameTimeThreshold){

        List<PlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(player.getPlayerGamePerformances());
        List<Integer> temp = new ArrayList<>();
        for(PlayerGamePerformance playerGamePerformance : playerGamePerformanceList){
            if(playerGamePerformance.getDate().before(date) && playerGamePerformance.getMinutes()> gameTimeThreshold) {
            temp.add(playerGamePerformance.getTurnovers());
            }
        }
        int[] data = temp.stream()
                .mapToInt(Integer::intValue)
                .toArray();

        double perGame = getPlayerTurnoversPerGameLastNGames(N,player,date, gameTimeThreshold);
        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += Math.pow(data[i] - perGame, 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + Math.sqrt(variance));
        return Math.sqrt(variance);
    }
    public double getPlayerPointsLastNGamesStdDev(int N, Player player, Date date, double gameTimeThreshold){

        Set<PlayerGamePerformance> playerGamePerformanceList = player.getPlayerGamePerformances();
        List<Double> temp = new ArrayList<>();
        for(PlayerGamePerformance playerGamePerformance : playerGamePerformanceList){
            if(playerGamePerformance.getDate().before(date) && playerGamePerformance.getMinutes()> gameTimeThreshold) {
                temp.add((double) playerGamePerformance.getPoints());
           }
        }

        double[] data = temp.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
        double perGame = getPlayerPointsPerGameLastNGames(N,player,date, gameTimeThreshold);
        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += Math.pow(data[i] - perGame, 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + Math.sqrt(variance));
        return Math.sqrt(variance);
    }

    public double getPlayerFoulsLastNGamesStdDev(int N, Player player, Date date, double gameTimeThreshold){

        Set<PlayerGamePerformance> playerGamePerformanceList = player.getPlayerGamePerformances();
        List<Double> temp = new ArrayList<>();
        for(PlayerGamePerformance playerGamePerformance : playerGamePerformanceList){
            if(playerGamePerformance.getDate().before(date) && playerGamePerformance.getMinutes()> gameTimeThreshold) {
                temp.add((double) playerGamePerformance.getFouls());
            }
        }

        double[] data = temp.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
        double perGame = getPlayerFoulsPerGameLastNGames(N,player,date, gameTimeThreshold);
        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += Math.pow(data[i] - perGame, 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + Math.sqrt(variance));
        return Math.sqrt(variance);
    }


    public double getPlayerReboundsLastNGamesStdDev(int N, Player player, Date date, double gameTimeThreshold){

        Set<PlayerGamePerformance> playerGamePerformanceList = player.getPlayerGamePerformances();
        List<Double> temp = new ArrayList<>();
        for(PlayerGamePerformance playerGamePerformance : playerGamePerformanceList){
            if(playerGamePerformance.getDate().before(date) && playerGamePerformance.getMinutes()> gameTimeThreshold) {
                temp.add((double) playerGamePerformance.getRebounds());
            }
        }

        double[] data = temp.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
        double perGame = getPlayerReboundsPerGameLastNGames(N,player,date, gameTimeThreshold);
        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += Math.pow(data[i] - perGame, 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + Math.sqrt(variance));
        return Math.sqrt(variance);
    }


    public double getPlayerBlocksPerGameLastNGames(int N, Player player, Date date, double gameTimeThreshold){
        int totalBlocks = 0;
        int games = 0;
        for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
            if(games>=N){
                break;
            }
            if(playerGamePerformance.getDate().before(date) && playerGamePerformance.getMinutes()>= gameTimeThreshold){
                totalBlocks = totalBlocks + playerGamePerformance.getBlocks();
                games++;
            }
        }
        //System.out.println("size for PBPG calc: " + games + " and " + totalBlocks + " blocks");
        //System.out.println("Points Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints/N);
        //return (double)totalBlocks/N;
        return (double)totalBlocks/games;
    }
    public double getPlayerBlocksPerGameLastNGamesDebug(int N, Player player, Date date){
        int totalBlocks = 0;
        int games = 0;
//        Date oldestGame = null;
//        double smallestMinutes = 0.0;
        for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
            if(games>=N){
                break;
            }
            if(playerGamePerformance.getDate().before(date) && playerGamePerformance.getMinutes()>= minuteThreshold){
                totalBlocks = totalBlocks + playerGamePerformance.getBlocks();
                games++;
//                if(oldestGame == null){
//                    oldestGame = playerGamePerformance.getDate();
//                }
//                if(date.before(oldestGame)){
//                    oldestGame = playerGamePerformance.getDate();
//                }
//                if(smallestMinutes == 0.0){
//                    smallestMinutes = playerGamePerformance.getMinutes();
//                }
//                if(smallestMinutes > playerGamePerformance.getMinutes()){
//                    smallestMinutes = playerGamePerformance.getMinutes();
//                }
            }
        }
//        if(games != N) {
//            System.out.println("Oldest game in performances: " + oldestGame + " and totalGames=" + games + " smallestTime: " + smallestMinutes);
//        }
        //System.out.println("size for PBPG calc: " + games + " and " + totalBlocks + " blocks");
        //System.out.println("Points Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints/N);
        //return (double)totalBlocks/N;
        return (double)totalBlocks/games;
    }
    public double getPlayerStealsPerGameLastNGames(int N, Player player, Date date, double gameTimeThreshold){
        int totalSteals = 0;
        int games = 0;
        for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
            if(games>=N){
                break;
            }
            if(playerGamePerformance.getDate().before(date) && playerGamePerformance.getMinutes()>= gameTimeThreshold){
                totalSteals = totalSteals + playerGamePerformance.getSteals();
                games++;
            }
        }
        //System.out.println("Points Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints/N);
        return (double)totalSteals/games;
    }
    public double getPlayerFieldGoalAttemptsPerGameLastNGames(int N, Player player, Date date, double gameTimeThreshold){
        int fieldGoalAttempts = 0;
        int games = 0;
        for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
            if(games>=N){
                break;
            }
            if(playerGamePerformance.getDate().before(date) && playerGamePerformance.getMinutes()>= gameTimeThreshold){
                fieldGoalAttempts = fieldGoalAttempts + playerGamePerformance.getFieldGoalsAttempted();
                games++;
            }
        }
        //System.out.println("Points Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints/N);
        return (double)fieldGoalAttempts/games;
    }
    public double getPlayerTurnoversPerGameLastNGames(int N, Player player, Date date, double gameTimeThreshold){
        int totalTurnovers = 0;
        int games = 0;
        for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
            if(games>=N){
                break;
            }
            if(playerGamePerformance.getDate().before(date) && playerGamePerformance.getMinutes()>= gameTimeThreshold){
                totalTurnovers = totalTurnovers + playerGamePerformance.getTurnovers();
                games++;
            }
        }
        //System.out.println("Points Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints/N);
        //return (double)totalTurnovers/N;
        return (double)totalTurnovers/games;
    }

    public double getPlayerReboundsPerGameLastNGames(int N, Player player, Date date, double gameTimeThreshold){
        int totalRebounds = 0;
        int games = 0;
        for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
            if(games>=N){
                break;
            }
            if(playerGamePerformance.getDate().before(date) && playerGamePerformance.getMinutes()>= gameTimeThreshold){
                totalRebounds = totalRebounds + playerGamePerformance.getRebounds();
                games++;
            }
        }

        //System.out.println("Points Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints/N);
        if(N>0) {
            return (double) totalRebounds / (double)games;
        }else{
            return 0;
        }
    }
    public double getPlayerFreeThrowsPerGameLastNGames(int N, Player player, Date date, double gameTimeThreshold){
        int totalRebounds = 0;
        int games = 0;
        for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
            if(games>=N){
                break;
            }
            if(playerGamePerformance.getDate().before(date) && playerGamePerformance.getMinutes()>= gameTimeThreshold){
                totalRebounds = totalRebounds + playerGamePerformance.getFreeThrowsAttempted();
                games++;
            }
        }

        //System.out.println("Points Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints/N);
        if(N>0) {
            return (double) totalRebounds / (double)games;
        }else{
            return 0;
        }
    }

    public double getPlayerFoulsPerGameLastNGames(int N, Player player, Date date, double gameTimeThreshold){
        int totalFouls = 0;
        int games = 0;
        for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
            if(games>=N){
                break;
            }
            if(playerGamePerformance.getDate().before(date) && playerGamePerformance.getMinutes()>= gameTimeThreshold){
                totalFouls = totalFouls + playerGamePerformance.getFouls();
                games++;
            }
        }

        //System.out.println("Points Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints/N);
        if(N>0) {
            return (double) totalFouls / (double) games;
        }else{
            return 0;
        }
    }



    public double getPlayerPointsPerGameLastNGames(int N, Player player, Date date, double gameTimeThreshold){
        int totalPoints = 0;
        int games = 0;
        for(PlayerGamePerformance playerGamePerformance : player.getPlayerGamePerformances()){
            if(games>=N){
                break;
            }
            if(playerGamePerformance.getDate().before(date) && playerGamePerformance.getMinutes()>= gameTimeThreshold){
                totalPoints = totalPoints + playerGamePerformance.getPoints();
                games++;
            }
        }

        //System.out.println("Points Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints/N);
        if(N>0) {
            return (double) totalPoints / (double) games;
        }else{
            return 0;
        }
    }
//    public void trimPlayerGameHistory(int N, Player player, Date date){
//        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//        localDate = localDate.minusDays(0);
//        Date beforeDate = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
//        List<PlayerGamePerformance> playerGamePerformances = player.getPlayerGamePerformances();
//        List<PlayerGamePerformance> cleanedPerformances = new ArrayList<>();
//        playerGamePerformances.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
//
//        int games = 0;
//        for(PlayerGamePerformance playerGamePerformance : playerGamePerformances){
//            if(games>=N){
//                break;
//            }
//            if(playerGamePerformance.getDate().before(beforeDate)){
//                cleanedPerformances.add(playerGamePerformance);
//                games++;
//            }
//        }
//        player.setPlayerGamePerformances(cleanedPerformances);
//    }

    public void trimPlayerGameHistory(int N, Player player, Date date, double gameTimeThreshold){
        LocalDateTime localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().minusHours(2);
        Date beforeDate = Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant());
        List<PlayerGamePerformance> playerGamePerformances = new ArrayList<>(player.getPlayerGamePerformances());
        Set<PlayerGamePerformance> cleanedPerformances = new HashSet<>();
        playerGamePerformances.sort(Comparator.comparing(PlayerGamePerformance::getDate));

        int games = 0;
        for(int i = playerGamePerformances.size()-1; i>0;i--){
            if(games>=N){
                break;
            }
            if(playerGamePerformances.get(i).getDate().before(beforeDate)
                && playerGamePerformances.get(i).getMinutes() >= gameTimeThreshold
            ) {
                cleanedPerformances.add(playerGamePerformances.get(i));
                games++;
            }
        }
        player.setPlayerGamePerformances(cleanedPerformances);
    }
    public double getPointsRateOfChange(int N, Player player, Date date){
        //long start = System.currentTimeMillis();
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(2);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        List<PlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(player.getPlayerGamePerformances());
        playerGamePerformanceList.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        List<Double> fieldingDoubles = new ArrayList<>();
        //Collections.sort(playerGamePerformanceList, Comparator.comparing(MLBPlayerGamePerformance::getDate).reversed());
        int games = 0;
        for(int i = 0; i<playerGamePerformanceList.size(); i++){
            //for(MLBPlayerGamePerformance mlbPlayerGamePerformance : mlbPlayer.getMLBPlayerGamePerformances() ){
            if(games>=N){
                break;
            }
            //if(playerGamePerformanceList.get(i).getDate().before(beforeDate) && playerGamePerformanceList.get(i).getFieldingpercentage() > 0){
                fieldingDoubles.add((double) playerGamePerformanceList.get(i).getPoints());
                games++;
            //}
        }
        //DoubleSummaryStatistics doubleSummaryStatistics = fieldingDoubles.stream().mapToDouble((x) -> x).summaryStatistics();
        SimpleRegression simpleRegression = new SimpleRegression();
        for(int x = 0; x < fieldingDoubles.size(); x++){
            simpleRegression.addData(x,fieldingDoubles.get(x));
        }
        //System.out.println(playerGamePerformanceList.size() + " took " + (System.currentTimeMillis() - start)  + " ms");
        return simpleRegression.getSlope();
    }

    public double getBlocksRateOfChange(int N, Player player, Date date){
        //long start = System.currentTimeMillis();
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(2);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        List<PlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(player.getPlayerGamePerformances());
        playerGamePerformanceList.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        List<Double> fieldingDoubles = new ArrayList<>();
        //Collections.sort(playerGamePerformanceList, Comparator.comparing(MLBPlayerGamePerformance::getDate).reversed());
        int games = 0;
        for(int i = 0; i<playerGamePerformanceList.size(); i++){
            //for(MLBPlayerGamePerformance mlbPlayerGamePerformance : mlbPlayer.getMLBPlayerGamePerformances() ){
            if(games>=N){
                break;
            }
            //if(playerGamePerformanceList.get(i).getDate().before(beforeDate) && playerGamePerformanceList.get(i).getFieldingpercentage() > 0){
            fieldingDoubles.add((double) playerGamePerformanceList.get(i).getBlocks());
            games++;
            //}
        }
        //DoubleSummaryStatistics doubleSummaryStatistics = fieldingDoubles.stream().mapToDouble((x) -> x).summaryStatistics();
        SimpleRegression simpleRegression = new SimpleRegression();
        for(int x = 0; x < fieldingDoubles.size(); x++){
            simpleRegression.addData(x,fieldingDoubles.get(x));
        }
        //System.out.println(playerGamePerformanceList.size() + " took " + (System.currentTimeMillis() - start)  + " ms");
        return simpleRegression.getSlope();
    }
    public double getReboundsRateOfChange(int N, Player player, Date date){
        //long start = System.currentTimeMillis();
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(2);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        List<PlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(player.getPlayerGamePerformances());
        playerGamePerformanceList.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        List<Double> fieldingDoubles = new ArrayList<>();
        //Collections.sort(playerGamePerformanceList, Comparator.comparing(MLBPlayerGamePerformance::getDate).reversed());
        int games = 0;
        for(int i = 0; i<playerGamePerformanceList.size(); i++){
            //for(MLBPlayerGamePerformance mlbPlayerGamePerformance : mlbPlayer.getMLBPlayerGamePerformances() ){
            if(games>=N){
                break;
            }
            //if(playerGamePerformanceList.get(i).getDate().before(beforeDate) && playerGamePerformanceList.get(i).getFieldingpercentage() > 0){
            fieldingDoubles.add((double) playerGamePerformanceList.get(i).getRebounds());
            games++;
            //}
        }
        //DoubleSummaryStatistics doubleSummaryStatistics = fieldingDoubles.stream().mapToDouble((x) -> x).summaryStatistics();
        SimpleRegression simpleRegression = new SimpleRegression();
        for(int x = 0; x < fieldingDoubles.size(); x++){
            simpleRegression.addData(x,fieldingDoubles.get(x));
        }
        //System.out.println(playerGamePerformanceList.size() + " took " + (System.currentTimeMillis() - start)  + " ms");
        return simpleRegression.getSlope();
    }
    public double getStealsRateOfChange(int N, Player player, Date date){
        //long start = System.currentTimeMillis();
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(2);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        List<PlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(player.getPlayerGamePerformances());
        playerGamePerformanceList.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        List<Double> fieldingDoubles = new ArrayList<>();
        //Collections.sort(playerGamePerformanceList, Comparator.comparing(MLBPlayerGamePerformance::getDate).reversed());
        int games = 0;
        for(int i = 0; i<playerGamePerformanceList.size(); i++){
            //for(MLBPlayerGamePerformance mlbPlayerGamePerformance : mlbPlayer.getMLBPlayerGamePerformances() ){
            if(games>=N){
                break;
            }
            //if(playerGamePerformanceList.get(i).getDate().before(beforeDate) && playerGamePerformanceList.get(i).getFieldingpercentage() > 0){
            fieldingDoubles.add((double) playerGamePerformanceList.get(i).getSteals());
            games++;
            //}
        }
        //DoubleSummaryStatistics doubleSummaryStatistics = fieldingDoubles.stream().mapToDouble((x) -> x).summaryStatistics();
        SimpleRegression simpleRegression = new SimpleRegression();
        for(int x = 0; x < fieldingDoubles.size(); x++){
            simpleRegression.addData(x,fieldingDoubles.get(x));
        }
        //System.out.println(playerGamePerformanceList.size() + " took " + (System.currentTimeMillis() - start)  + " ms");
        return simpleRegression.getSlope();
    }
    public double getTurnoversRateOfChange(int N, Player player, Date date){
        //long start = System.currentTimeMillis();
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(2);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        List<PlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(player.getPlayerGamePerformances());
        playerGamePerformanceList.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        List<Double> fieldingDoubles = new ArrayList<>();
        //Collections.sort(playerGamePerformanceList, Comparator.comparing(MLBPlayerGamePerformance::getDate).reversed());
        int games = 0;
        for(int i = 0; i<playerGamePerformanceList.size(); i++){
            //for(MLBPlayerGamePerformance mlbPlayerGamePerformance : mlbPlayer.getMLBPlayerGamePerformances() ){
            if(games>=N){
                break;
            }
            //if(playerGamePerformanceList.get(i).getDate().before(beforeDate) && playerGamePerformanceList.get(i).getFieldingpercentage() > 0){
            fieldingDoubles.add((double) playerGamePerformanceList.get(i).getTurnovers());
            games++;
            //}
        }
        //DoubleSummaryStatistics doubleSummaryStatistics = fieldingDoubles.stream().mapToDouble((x) -> x).summaryStatistics();
        SimpleRegression simpleRegression = new SimpleRegression();
        for(int x = 0; x < fieldingDoubles.size(); x++){
            simpleRegression.addData(x,fieldingDoubles.get(x));
        }
        //System.out.println(playerGamePerformanceList.size() + " took " + (System.currentTimeMillis() - start)  + " ms");
        return simpleRegression.getSlope();
    }
    public double getFoulsRateOfChange(int N, Player player, Date date){
        //long start = System.currentTimeMillis();
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(2);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        List<PlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(player.getPlayerGamePerformances());
        playerGamePerformanceList.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        List<Double> fieldingDoubles = new ArrayList<>();
        //Collections.sort(playerGamePerformanceList, Comparator.comparing(MLBPlayerGamePerformance::getDate).reversed());
        int games = 0;
        for(int i = 0; i<playerGamePerformanceList.size(); i++){
            //for(MLBPlayerGamePerformance mlbPlayerGamePerformance : mlbPlayer.getMLBPlayerGamePerformances() ){
            if(games>=N){
                break;
            }
            //if(playerGamePerformanceList.get(i).getDate().before(beforeDate) && playerGamePerformanceList.get(i).getFieldingpercentage() > 0){
            fieldingDoubles.add((double) playerGamePerformanceList.get(i).getFouls());
            games++;
            //}
        }
        //DoubleSummaryStatistics doubleSummaryStatistics = fieldingDoubles.stream().mapToDouble((x) -> x).summaryStatistics();
        SimpleRegression simpleRegression = new SimpleRegression();
        for(int x = 0; x < fieldingDoubles.size(); x++){
            simpleRegression.addData(x,fieldingDoubles.get(x));
        }
        //System.out.println(playerGamePerformanceList.size() + " took " + (System.currentTimeMillis() - start)  + " ms");
        return simpleRegression.getSlope();
    }
    public double getFreeThrowsRateOfChange(int N, Player player, Date date){
        //long start = System.currentTimeMillis();
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(2);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        List<PlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(player.getPlayerGamePerformances());
        playerGamePerformanceList.sort(Comparator.comparing(PlayerGamePerformance::getDate).reversed());
        List<Double> fieldingDoubles = new ArrayList<>();
        //Collections.sort(playerGamePerformanceList, Comparator.comparing(MLBPlayerGamePerformance::getDate).reversed());
        int games = 0;
        for(int i = 0; i<playerGamePerformanceList.size(); i++){
            //for(MLBPlayerGamePerformance mlbPlayerGamePerformance : mlbPlayer.getMLBPlayerGamePerformances() ){
            if(games>=N){
                break;
            }
            //if(playerGamePerformanceList.get(i).getDate().before(beforeDate) && playerGamePerformanceList.get(i).getFieldingpercentage() > 0){
            fieldingDoubles.add((double) playerGamePerformanceList.get(i).getFreeThrowsAttempted());
            games++;
            //}
        }
        //DoubleSummaryStatistics doubleSummaryStatistics = fieldingDoubles.stream().mapToDouble((x) -> x).summaryStatistics();
        SimpleRegression simpleRegression = new SimpleRegression();
        for(int x = 0; x < fieldingDoubles.size(); x++){
            simpleRegression.addData(x,fieldingDoubles.get(x));
        }
        //System.out.println(playerGamePerformanceList.size() + " took " + (System.currentTimeMillis() - start)  + " ms");
        return simpleRegression.getSlope();
    }
}
