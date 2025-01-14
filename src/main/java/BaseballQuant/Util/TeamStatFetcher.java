package BaseballQuant.Util;

import BaseballQuant.Model.*;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class TeamStatFetcher {
    List<MLBGame> gameList;
    public TeamStatFetcher(){

    }

    public List<MLBGame> getGameList() {
        return gameList;
    }

    public void setGameList(List<MLBGame> gameList) {
        this.gameList = gameList;
    }
    public int startingPitcherRunsGivenUpLastNGames(int gameCount, MLBTeam mlbTeam, MLBGame game){
        String teamName = mlbTeam.getTeamName();
        int runsGivenUp = 0;
        int gamesCounted = 0;
        List<MLBGame> listOfRecentGames = getTeamsLastNGames(gameCount, mlbTeam, game);
        for(MLBGame game3: listOfRecentGames){
            if(game3.getAwayTeamName().equals(teamName)){
                MLBPitcher startingPitcher = game.getAwayStartingPitcher();
                for(MLBPitcherPerformance mlbPitcherPerformance : startingPitcher.getMLBPitcherGamePerformances()){
                    if(mlbPitcherPerformance.getGameId() == game3.getGameId()){
                        runsGivenUp = runsGivenUp + mlbPitcherPerformance.getRunsGivenUp();
                    }
                }
            }else {
                MLBPitcher startingPitcher = game.getHomeStartingPitcher();
                for(MLBPitcherPerformance mlbPitcherPerformance : startingPitcher.getMLBPitcherGamePerformances()){
                    if(mlbPitcherPerformance.getGameId() == game3.getGameId()){
                        runsGivenUp = runsGivenUp + mlbPitcherPerformance.getRunsGivenUp();
                    }
                }
            }
        }
        return  runsGivenUp;
    }
//    public int

    public int runsGivenUpLastNGames(int gameCount, MLBTeam mlbTeam, MLBGame game){
        String teamName = mlbTeam.getTeamName();
       // List<Integer> runs = new ArrayList<>();
        int runsGivenUp = 0;
        //int gamesCounted = 0;
        List<MLBGame> listOfRecentGames = getTeamsLastNGames(gameCount, mlbTeam, game);
        for(int i = 0; i< listOfRecentGames.size(); i++){
            MLBGame game3 = listOfRecentGames.get(i);
        //for(MLBGame game3: listOfRecentGames){
            if(game3.getAwayTeamName().equals(teamName)){
                runsGivenUp = runsGivenUp + game3.getHomePoints();
                //runs.add(game3.getHomePoints());
            }else {
                runsGivenUp = runsGivenUp + game3.getAwayPoints();
                //runs.add(game3.getAwayPoints());
            }
        }
//        System.out.println("[runsGivenUpLastNGames]" + mlbTeam.getTeamName() + " gave up " + runsGivenUp + " over last " + gameCount + " games.");
//        System.out.println("[runsGivenUpLastNGames]" + runs);
        return  runsGivenUp;
    }
    public float runsGivenUpLastNGamesRateOfChange(int gameCount, MLBTeam mlbTeam, MLBGame game){
        String teamName = mlbTeam.getTeamName();
         List<Integer> valueList = new ArrayList<>();
        int runsGivenUp = 0;
        //int gamesCounted = 0;
        List<MLBGame> listOfRecentGames = getTeamsLastNGames(gameCount, mlbTeam, game);
        for(int i = 0; i< listOfRecentGames.size(); i++){
            MLBGame game3 = listOfRecentGames.get(i);
            //for(MLBGame game3: listOfRecentGames){
            if(game3.getAwayTeamName().equals(teamName)){
                runsGivenUp = runsGivenUp + game3.getHomePoints();
                valueList.add( game3.getHomePoints());
                //runs.add(game3.getHomePoints());
            }else {
                runsGivenUp = runsGivenUp + game3.getAwayPoints();
                valueList.add( game3.getAwayPoints());
                //runs.add(game3.getAwayPoints());
            }
        }


        SimpleRegression simpleRegression = new SimpleRegression();
        for(int x = 0; x < valueList.size(); x++){
            simpleRegression.addData(x,valueList.get(x));
        }

        //System.out.println("Points Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints/N);
        return (float) simpleRegression.getSlope();
    }

    public double runsScoredLastNGamesRateOfChange(int gameCount, MLBTeam mlbTeam, MLBGame game){
        String teamName = mlbTeam.getTeamName();
        List<Integer> valueList = new ArrayList<>();
        int runsScored = 0;
        //int gamesCounted = 0;
        List<MLBGame> listOfRecentGames = getTeamsLastNGames(gameCount, mlbTeam, game);
        for(int i = 0; i< listOfRecentGames.size(); i++){
            MLBGame game3 = listOfRecentGames.get(i);
            //for(MLBGame game3: listOfRecentGames){
            if(game3.getAwayTeamName().equals(teamName)){
                runsScored = runsScored + game3.getAwayPoints();
                valueList.add( game3.getAwayPoints());
                //runs.add(game3.getHomePoints());
            }else {
                runsScored = runsScored + game3.getHomePoints();
                valueList.add( game3.getHomePoints());
                //runs.add(game3.getAwayPoints());
            }
        }


        SimpleRegression simpleRegression = new SimpleRegression();
        for(int x = 0; x < valueList.size(); x++){
            simpleRegression.addData(x,valueList.get(x));
        }

        //System.out.println("Points Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints/N);
        return (float) simpleRegression.getSlope();
    }

    public List<MLBGame> getTeamsLastNHomeGames(int gameCount, MLBTeam mlbTeam, MLBGame game){
        String teamName = mlbTeam.getTeamName();

        //System.out.println("searching teamName: " + teamName);
        //List<MLBGame> GamesIn2021 = gameRepository.findAllByAwayTeamNameOrHomeTeamName(teamName, teamName);
        List<MLBGame> GamesIn2021 = new ArrayList<>();
        for(int i = 0; i<gameList.size();i++){
            //for(MLBGame gameInList : gameList){
            try {
                if (gameList.get(i).getHomeTeamName().equals(teamName)) {
                    GamesIn2021.add(gameList.get(i));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        GamesIn2021.sort(Comparator.comparing(MLBGame::getDate).reversed());
        int gamesCounted = 0;
        List<MLBGame> listOfMostRecentGames = new ArrayList<>();
        for(int i = 0; i<GamesIn2021.size(); i++){
//        for(MLBGame game3: GamesIn2021){
            if(gamesCounted>=gameCount){
                break;
            }else{
                if(GamesIn2021.get(i).getDate().before(game.getDate())){
                    listOfMostRecentGames.add(GamesIn2021.get(i));
                    gamesCounted = gamesCounted + 1;
                }
            }
        }
        return listOfMostRecentGames;
    }
    public List<MLBGame> getTeamsLastNGames(int gameCount, MLBTeam mlbTeam, MLBGame game){
        String teamName = mlbTeam.getTeamName();
        long time = game.getDate().getTime();
        //System.out.println("searching teamName: " + teamName);
        //List<MLBGame> GamesIn2021 = gameRepository.findAllByAwayTeamNameOrHomeTeamName(teamName, teamName);
        List<MLBGame> GamesIn2021 = new ArrayList<>();
        for(int i = 0; i<gameList.size();i++){
        //for(MLBGame gameInList : gameList){
            try {
                if (gameList.get(i).getAwayTeamName().equals(teamName) || gameList.get(i).getHomeTeamName().equals(teamName)) {
                    GamesIn2021.add(gameList.get(i));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        GamesIn2021.sort(Comparator.comparing(MLBGame::getDate).reversed());
        int gamesCounted = 0;
        List<MLBGame> listOfMostRecentGames = new ArrayList<>();
        for(int i = 0; i<GamesIn2021.size(); i++){
//        for(MLBGame game3: GamesIn2021){
            if(gamesCounted>=gameCount){
                break;
            }else{
                if(GamesIn2021.get(i).getDate().getTime() < time){
                    listOfMostRecentGames.add(GamesIn2021.get(i));
                    gamesCounted = gamesCounted + 1;
                }
            }
        }
        return listOfMostRecentGames;
    }
    public List<MLBGame> getTeamsLastNGamesClean(int gameCount, MLBTeam mlbTeam, MLBGame game){
        String teamName = mlbTeam.getTeamName();

        //System.out.println("searching teamName: " + teamName);
        //List<MLBGame> GamesIn2021 = gameRepository.findAllByAwayTeamNameOrHomeTeamName(teamName, teamName);
        List<MLBGame> GamesIn2021 = new ArrayList<>();
        for(int i = 0; i<gameList.size();i++){
            //for(MLBGame gameInList : gameList){
            try {
                if (gameList.get(i).getAwayTeamName().equals(teamName) || gameList.get(i).getHomeTeamName().equals(teamName)) {
                    GamesIn2021.add(gameList.get(i));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        GamesIn2021.sort(Comparator.comparing(MLBGame::getDate).reversed());
        int gamesCounted = 0;
        List<MLBGame> listOfMostRecentGames = new ArrayList<>();
        for(int i = 0; i<GamesIn2021.size(); i++){
//        for(MLBGame game3: GamesIn2021){
            if(gamesCounted>=gameCount){
                break;
            }else{
                if(GamesIn2021.get(i).getDate().before(game.getDate()) && GamesIn2021.get(i).getAwayStartingPitcher() != null && GamesIn2021.get(i).getHomeStartingPitcher() != null){
                    listOfMostRecentGames.add(GamesIn2021.get(i));
                    gamesCounted = gamesCounted + 1;
                }
            }
        }
        return listOfMostRecentGames;
    }

    public int getBullpenRunsGivenUp(MLBTeam mlbTeam, MLBGame mlbGame){
        int runsGivenUp = 0;
        if(mlbGame.getAwayTeamName().equals(mlbTeam.getTeamName())) {
            MLBPitcher startingPitcher = mlbGame.getAwayStartingPitcher();
            //Set<MLBPitcher> pitchersSet = (Set<MLBPitcher>) mlbGame.getAwayMLBTeam().getPitchingPlayers().clone();

            List<MLBPitcher> pitchers = new ArrayList<>(mlbGame.getAwayMLBTeam().getPitchingPlayers());
            if (startingPitcher != null) {
                List<MLBPitcher> safeList = new ArrayList();
                for(int i = 0; i<pitchers.size(); i++){
                //for (MLBPitcher pitcher : pitchers) {
                    if (pitchers.get(i).getPlayerID() != startingPitcher.getPlayerID()) {
                        safeList.add(pitchers.get(i));
                    }
                }
                for (MLBPitcher bullpenPitcher : safeList) {
                    for (MLBPitcherPerformance mlbPitcherPerformance : bullpenPitcher.getMLBPitcherGamePerformances()) {
                        if (mlbPitcherPerformance.getGameId() == mlbGame.getGameId()) {
                            runsGivenUp = runsGivenUp + mlbPitcherPerformance.getRunsGivenUp();
                            //System.out.println(bullpenPitcher.getFullName() + " gave up " + mlbPitcherPerformance.getRunsGivenUp() + " in " + mlbPitcherPerformance.getInningsPitched() + " innings");
                        }
                    }
                }
            }


        }else{
            MLBPitcher startingPitcher = mlbGame.getHomeStartingPitcher();
            Set<MLBPitcher> pitchers = mlbGame.getHomeMLBTeam().getPitchingPlayers();
            List<MLBPitcher> pitcherList = new ArrayList<>(pitchers);
            if (startingPitcher != null) {
                List<MLBPitcher> safeList = new ArrayList();
                for (MLBPitcher pitcher : pitcherList) {
                    if (pitcher.getPlayerID() != startingPitcher.getPlayerID()) {
                        safeList.add(pitcher);
                    }
                }
                for (MLBPitcher bullpenPitcher : safeList) {
                    for (MLBPitcherPerformance mlbPitcherPerformance : bullpenPitcher.getMLBPitcherGamePerformances()) {
                        if (mlbPitcherPerformance.getGameId() == mlbGame.getGameId()) {
                            runsGivenUp = runsGivenUp + mlbPitcherPerformance.getRunsGivenUp();
                            //System.out.println(bullpenPitcher.getFullName() + " gave up " + mlbPitcherPerformance.getRunsGivenUp() + " in " + mlbPitcherPerformance.getInningsPitched() + " innings");
                        }
                    }
                }
            }
        }
        return  runsGivenUp;
    }

    public double getBullpenRunsGivenUpLastNGames(int N, MLBTeam mlbTeam, MLBGame mlbGame){
        List<MLBGame> lastNGames = getTeamsLastNGames(N, mlbTeam, mlbGame);
        int totalRunsGivenUp = 0;

        for(MLBGame game : lastNGames){
            totalRunsGivenUp = totalRunsGivenUp + getBullpenRunsGivenUp((MLBTeam) mlbTeam.clone(), (MLBGame) game.clone());
        }

        return  totalRunsGivenUp;
    }


    public float getBullpenRunsGivenUpLastNGamesStdDev(int N, MLBTeam mlbTeam, MLBGame mlbGame){
        double totalRunsGivenUp = getBullpenRunsGivenUpLastNGames(N,mlbTeam, mlbGame);
        //System.out.println(mlbTeam.getTeamName() + " bullpen gave up " + totalRunsGivenUp + " in the last " + N + " games. " + (totalRunsGivenUp/(double)N));
        List<MLBGame> mostRecentGames = getTeamsLastNGames(N,mlbTeam,mlbGame);
        List<Double> temp = new ArrayList<>();
        for(MLBGame recentGame : mostRecentGames){
            //if(playerGamePerformance.getDate().before(date) && playerGamePerformance.getMinutes()> 0.5) {
            temp.add((double) getBullpenRunsGivenUp(mlbTeam, recentGame));
            //}
        }
        //System.out.println(temp);
        double[] data = temp.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();

        float variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += (float)Math.pow(data[i] - totalRunsGivenUp/(double)N, 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + mlbTeam.getTeamName() + ": " + Math.sqrt(variance));
        return (float)Math.sqrt(variance);
    }
    public float getRunsGivenUpLastNGamesStdDev(int N, MLBTeam mlbTeam, MLBGame mlbGame){
        double totalRunsGivenUp = runsGivenUpLastNGames(N,mlbTeam, mlbGame);
        //System.out.println(mlbTeam.getTeamName() + " bullpen gave up " + totalRunsGivenUp + " in the last " + N + " games. " + (totalRunsGivenUp/(double)N));
        List<MLBGame> mostRecentGames = getTeamsLastNGames(N,mlbTeam,mlbGame);
        List<Double> temp = new ArrayList<>();
        for(MLBGame recentGame : mostRecentGames){
            //if(playerGamePerformance.getDate().before(date) && playerGamePerformance.getMinutes()> 0.5) {
            if(recentGame.getAwayTeamMlbId() == mlbTeam.getMlbId()){
                temp.add((double) recentGame.getAwayPoints());
            }else{
                temp.add((double) recentGame.getHomePoints());
            }
            ;
            //}
        }
        //System.out.println(temp);
        double[] data = temp.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();

        float variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += (float)Math.pow(data[i] - totalRunsGivenUp/(double)N, 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + mlbTeam.getTeamName() + ": " + Math.sqrt(variance));
        return (float)Math.sqrt(variance);
    }

    public List<Double> modelOpposingTeamPitchingForPlayer(int gameCount, MLBTeam mlbTeam, MLBPlayer mlbPlayer, MLBGame mlbGame, BackTestIngestObject backTestIngestObject){
        List<Double> highRangeValues = new ArrayList<>();
        List<Double> lowRangeValues = new ArrayList<>();

        List<MLBGame> mlbGames = getTeamsLastNGames(gameCount,mlbTeam, mlbGame);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        LocalDateTime localDateTime = mlbGame.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.minusHours(2);
        Date beforeDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        //System.out.println("searching for most recent games before " + date + " for player: " + player.getFirstName() + " " + player.getLastName());
        int games = 0;
        Set<MLBPlayerGamePerformance> playerGamePerformanceSet = mlbPlayer.getPlayerGamePerformances();
        List<MLBPlayerGamePerformance> playerGamePerformanceList = new ArrayList<>(playerGamePerformanceSet);
        playerGamePerformanceList.sort(Comparator.comparing(MLBPlayerGamePerformance::getDate).reversed());
        List<Integer> GameIDs = new ArrayList<>();
        for(MLBPlayerGamePerformance playerGamePerformance : mlbPlayer.getPlayerGamePerformances()){
            if(games>=gameCount){
                break;
            }
            if(playerGamePerformance.getDate().before(beforeDate) && playerGamePerformance.getAtbats()> 0){
                GameIDs.add(playerGamePerformance.getGameId());
//                System.out.println("[BlockModel] GameDate: " + simpleDateFormat.format(playerGamePerformance.getDate()) +
//                        " PGPID: " + playerGamePerformance.getId() + " Pts: " + playerGamePerformance.getPoints() + " gameID: " + playerGamePerformance.getGameID());
                games++;
            }else{
                //System.out.println(playerGamePerformance.getDate() + " is not before " + beforeDate);
            }
        }



        for(MLBGame pastGame : mlbGames){
            MLBTeam selectedTeam;
            if(pastGame.getAwayTeamMlbId() == mlbTeam.getMlbId()){
                selectedTeam = pastGame.getAwayMLBTeam();
            }else{
                selectedTeam = pastGame.getHomeMLBTeam();
            }
            double runsGivenUpPerGame = runsGivenUpLastNGames(gameCount, selectedTeam,pastGame)/(double) gameCount;
            double runsGivenUpStdDev;
            if(backTestIngestObject.isDoubleSquareRoot()){
                runsGivenUpStdDev = Math.sqrt(getRunsGivenUpLastNGamesStdDev(gameCount, selectedTeam, pastGame));
            }else{
                runsGivenUpStdDev = getRunsGivenUpLastNGamesStdDev(gameCount, selectedTeam, pastGame);
            }
            double highRunsGivenUp = 0.0;
            double lowRunsGivenUp = 0.0;

            if(!Double.isNaN(runsGivenUpStdDev)) {
                highRunsGivenUp = highRunsGivenUp + (runsGivenUpPerGame + runsGivenUpStdDev);
                //if ((runsGivenUpPerGame - runsGivenUpStdDev) > 0) {
                lowRunsGivenUp = lowRunsGivenUp + ((runsGivenUpPerGame - runsGivenUpStdDev));
                //}
                highRangeValues.add(highRunsGivenUp);
                lowRangeValues.add(lowRunsGivenUp);
            }
        }
        DoubleSummaryStatistics highSummaryStatistics = highRangeValues.stream().mapToDouble((x) -> x).summaryStatistics();
        DoubleSummaryStatistics lowSummaryStatistics = lowRangeValues.stream().mapToDouble((x) -> x).summaryStatistics();
        List<Double> doubleList = new ArrayList<>();
        doubleList.add(highSummaryStatistics.getAverage());
        doubleList.add(lowSummaryStatistics.getAverage());
        return doubleList;

    }

    public Double getTeamRunsGivenUpPerGame(int gameCount, MLBTeam mlbTeam, MLBGame mlbGame){
        List<MLBGame> mlbGames = getTeamsLastNGames(gameCount,mlbTeam, mlbGame);
        List<Double> doubleList = new ArrayList<>();
        for(MLBGame pastGame : mlbGames){
            MLBTeam selectedTeam;
            if(pastGame.getAwayTeamMlbId() == mlbTeam.getMlbId()){
                selectedTeam = pastGame.getAwayMLBTeam();
            }else{
                selectedTeam = pastGame.getHomeMLBTeam();
            }
            double runsGivenUpPerGame = runsGivenUpLastNGames(gameCount, selectedTeam,pastGame)/(double) gameCount;
            doubleList.add(runsGivenUpPerGame);

        }
        DoubleSummaryStatistics stats = doubleList.stream().mapToDouble((x) -> x).summaryStatistics();
        return stats.getAverage();
    }

    public double runsScoredPerGame(int gameCount, MLBTeam mlbTeam, MLBGame game){
        String teamName = mlbTeam.getTeamName();
        List<Integer> valueList = new ArrayList<>();
        int runsScored = 0;
        //int gamesCounted = 0;
        List<MLBGame> listOfRecentGames = getTeamsLastNGames(gameCount, mlbTeam, game);
        for(int i = 0; i< listOfRecentGames.size(); i++){
            MLBGame game3 = listOfRecentGames.get(i);
            //for(MLBGame game3: listOfRecentGames){
            if(game3.getAwayTeamName().equals(teamName)){
                runsScored = runsScored + game3.getAwayPoints();
                valueList.add( game3.getAwayPoints());
                //runs.add(game3.getHomePoints());
            }else {
                runsScored = runsScored + game3.getHomePoints();
                valueList.add( game3.getHomePoints());
                //runs.add(game3.getAwayPoints());
            }
        }


        SimpleRegression simpleRegression = new SimpleRegression();
        for(int x = 0; x < valueList.size(); x++){
            simpleRegression.addData(x,valueList.get(x));
        }

        //System.out.println("Points Per Game For " + player.getFirstName() + " " + player.getLastName() + ": " + totalPoints/N);
        return (float) runsScored/listOfRecentGames.size();
    }

    public float getTeamRunsGivenUpPerGameStdDev(int gameCount, MLBTeam mlbTeam, MLBGame mlbGame){
        double totalRunsGivenUp = getTeamRunsGivenUpPerGame(gameCount,mlbTeam, mlbGame);
        List<MLBGame> mostRecentGames = getTeamsLastNGames(gameCount,mlbTeam,mlbGame);
        List<Double> temp = new ArrayList<>();
        for(MLBGame recentGame : mostRecentGames) {
            if (recentGame.getAwayTeamMlbId() == mlbTeam.getMlbId()) {
                temp.add((double) recentGame.getAwayPoints());
            } else {
                temp.add((double) recentGame.getHomePoints());
            }
        }
        double[] data = temp.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();

        float variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += (float)Math.pow(data[i] - totalRunsGivenUp/(double)gameCount, 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + mlbTeam.getTeamName() + ": " + Math.sqrt(variance));
        return (float)Math.sqrt(variance);
    }

    public List<Double> getOpposingTeamRunsGivenUpPerGame(int gameCount, MLBTeam mlbTeam, MLBGame mlbGame){
        List<MLBGame> mlbGames = getTeamsLastNGames(gameCount,mlbTeam, mlbGame);
        List<Double> doubleList = new ArrayList<>();
        for(MLBGame pastGame : mlbGames){
            MLBTeam selectedTeam;
            if(pastGame.getAwayTeamMlbId() == mlbTeam.getMlbId()){
                selectedTeam = pastGame.getHomeMLBTeam();
            }else{
                selectedTeam = pastGame.getAwayMLBTeam();
            }
            double runsGivenUpPerGame = runsGivenUpLastNGames(gameCount, selectedTeam,pastGame)/(double) gameCount;
            doubleList.add(runsGivenUpPerGame);

        }

        return doubleList;

    }
    public Double getHomeAdvantage(int gameCount, MLBTeam mlbTeam, MLBGame mlbGame){
        List<MLBGame> mlbGames = getTeamsLastNHomeGames((gameCount*2),mlbTeam, mlbGame);
        int homePoints = 0;
        int awayPoints = 0;
        for(MLBGame pastGame : mlbGames){
            awayPoints = awayPoints + pastGame.getAwayPoints();
            homePoints = homePoints + pastGame.getHomePoints();
        }
        double temp = (double)(homePoints - awayPoints)/mlbGames.size();
        return temp;
    }

    public float genericStdDev(List<Double> doubles){
        DoubleSummaryStatistics stats = doubles.stream().mapToDouble((x) -> x).summaryStatistics();
        double[] data = doubles.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();

        float variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += (float)Math.pow(data[i] - stats.getAverage(), 2);
        }
        variance /= data.length;
        //System.out.println("Std Dev Per Game For " + mlbTeam.getTeamName() + ": " + Math.sqrt(variance));
        return (float)Math.sqrt(variance);
    }

}
