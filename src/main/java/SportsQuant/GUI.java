package SportsQuant;


import SportsQuant.Model.*;
import SportsQuant.Repository.*;
import SportsQuant.Threads.TodaysGamesThread;
import SportsQuant.Util.GameFinder;
import SportsQuant.Util.StaticOddsFetcher;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
//import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@SpringBootApplication
public class GUI extends Application {
    private ConfigurableApplicationContext mainContext;
    private List<Game> allGames;

    private Parent rootNode;
    private GameRepository gameRepository;
    private GameOddsRepository gameOddsRepository;
    private HashMap<Integer, GameOdds> gameOddsHashMap;
    List<Game> gameList;
    @FXML private TextField ou_thread_count_input;
    @FXML private TextField ou_player_lookback;
    @FXML private TextField ou_pitcher_lookback;
    @FXML private TextField ou_bullpen_lookback;
    @FXML private TextField ou_high_block_factor;
    @FXML private TextField ou_low_block_factor;
    @FXML private TextField ou_high_field_factor;
    @FXML private TextField ou_low_field_factor;
    @FXML private TextField ou_high_steal_factor;
    @FXML private TextField ou_low_steal_factor;
    @FXML private CheckBox ou_double_square_root_true;
    @FXML private CheckBox ou_total_square_root_true;
    @FXML private TableColumn<GameResult, SimpleIntegerProperty> game_id;
    @FXML private TableColumn<GameResult, SimpleStringProperty> home_team_name;
    @FXML private TableColumn<GameResult, SimpleStringProperty> away_team_name;
    @FXML private TableColumn<GameResult, SimpleStringProperty> date;
    @FXML private TableColumn<GameResult, SimpleDoubleProperty> total_points;
    @FXML private TableColumn<GameResult, SimpleDoubleProperty> home_points;
    @FXML private TableColumn<GameResult, SimpleDoubleProperty> away_points;
    @FXML private TableColumn<GameResult, SimpleStringProperty> ou_result;
    @FXML private TableColumn<GameResult, SimpleStringProperty> spread_result;
    @FXML private TableColumn<GameResult, SimpleStringProperty> ml_result;
    @FXML private TableColumn<GameResult, SimpleStringProperty> ml_away_pct;
    @FXML private TableColumn<GameResult, SimpleStringProperty> over_under;
    @FXML private TableColumn<GameResult, SimpleBooleanProperty> incomplete;
    @FXML private TableColumn<GameResult, SimpleBooleanProperty> shortenedGame;
    @FXML private TableView<GameResult> todays_games_list;


   @Override
    public void init() throws Exception {
       mainContext = SpringApplication.run(GUI.class);
       FXMLLoader loader = new FXMLLoader(getClass().getResource("/nbaView.fxml"));
       loader.setControllerFactory(mainContext::getBean);
       loader.setController(this);
       rootNode = loader.load();
       this.gameRepository = mainContext.getBean(GameRepository.class);
       this.gameOddsRepository = mainContext.getBean(GameOddsRepository.class);
       //System.out.println(playerRepository.findAll());

//        allOdds =  mongoTemplate.findAll(MLBGameOdds.class, "mlbGameOdds");
//        allGames =  mongoTemplate.findAll(MLBGame.class, "mlbGame");
       allGames = (List<Game>) gameRepository.findAll();
       List<GameOdds> gameOdds = (List<GameOdds>) gameOddsRepository.findAll();
       HashMap<Integer, GameOdds> gameOddsHashMap = new HashMap<>();
       for(GameOdds gameOdds1 : gameOdds){
           gameOddsHashMap.put(gameOdds1.getGameId(), gameOdds1);
       }
       this.gameOddsHashMap = gameOddsHashMap;
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("BUNK");
        Scene scene = new Scene(rootNode, 1024, 800);
        scene.getStylesheets().add(getClass().getResource("/stylesheet.css").getFile());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    public void runOverUnder(){
        BackTestIngestObject overUnderObject = new BackTestIngestObject();
        overUnderObject.setPlayerGameLookBack(Integer.parseInt(ou_player_lookback.getText()));
        overUnderObject.setDoubleSquareRoot(ou_double_square_root_true.isSelected());
        overUnderObject.setSquareRootTotalPoints(ou_total_square_root_true.isSelected());
        overUnderObject.setHighBlockPointFactor(Double.parseDouble(ou_high_block_factor.getText()));
        overUnderObject.setLowerBlockPointFactor(Double.parseDouble(ou_low_block_factor.getText()));
        overUnderObject.setHighStealPointFactor(Double.parseDouble(ou_high_steal_factor.getText()));
        overUnderObject.setLowerStealPointFactor(Double.parseDouble(ou_low_steal_factor.getText()));
        overUnderObject.setAllowBelowZero(((CheckBox) rootNode.lookup("#ou_low_end_below_zero")).isSelected());
        overUnderObject.setPointThreshold(Double.parseDouble(((TextField) rootNode.lookup("#ou_point_threshold")).getText()));
        overUnderObject.setModelOpponentSteals(true);
        overUnderObject.setModelOpponentBlocks(true);
        overUnderObject.setModelOpponentTurnovers(false);
        overUnderObject.setPointsReducedPerBlock(2);
        overUnderObject.setPointReductionPerSteal(2);
        overUnderObject.setBetType("overunder");

        BackTestIngestObject pointSpreadObject = new BackTestIngestObject();
        pointSpreadObject.setPlayerGameLookBack(Integer.parseInt(((TextField) rootNode.lookup("#spread_player_lookback")).getText()));
        pointSpreadObject.setDoubleSquareRoot(((CheckBox) rootNode.lookup("#spread_double_square_root")).isSelected());
        pointSpreadObject.setSquareRootTotalPoints(((CheckBox) rootNode.lookup("#spread_square_root_total")).isSelected());
        pointSpreadObject.setAllowBelowZero(((CheckBox) rootNode.lookup("#spread_low_end_below_zero")).isSelected());
        pointSpreadObject.setHighBlockPointFactor(Double.parseDouble(((TextField) rootNode.lookup("#spread_high_block_factor")).getText()));
        pointSpreadObject.setLowerBlockPointFactor(Double.parseDouble(((TextField) rootNode.lookup("#spread_low_block_factor")).getText()));
        pointSpreadObject.setHighStealPointFactor(Double.parseDouble(((TextField) rootNode.lookup("#spread_high_steal_factor")).getText()));
        pointSpreadObject.setLowerStealPointFactor(Double.parseDouble(((TextField) rootNode.lookup("#spread_low_steal_factor")).getText()));
        pointSpreadObject.setHomeTeamAdvantage(Double.parseDouble(((TextField) rootNode.lookup("#spread_home_advantage")).getText()));
        pointSpreadObject.setPointThreshold(Double.parseDouble(((TextField) rootNode.lookup("#spread_point_threshold")).getText()));
        pointSpreadObject.setModelOpponentSteals(true);
        pointSpreadObject.setModelOpponentBlocks(true);
        pointSpreadObject.setModelOpponentTurnovers(false);
        pointSpreadObject.setPointsReducedPerBlock(2);
        pointSpreadObject.setPointReductionPerSteal(2);
        pointSpreadObject.setBetType("spread");

        BackTestIngestObject moneyLineObject = new BackTestIngestObject();
        moneyLineObject.setPlayerGameLookBack(Integer.parseInt(((TextField) rootNode.lookup("#ml_player_lookback")).getText()));
        moneyLineObject.setDoubleSquareRoot(((CheckBox) rootNode.lookup("#ml_double_square_root")).isSelected());
        moneyLineObject.setSquareRootTotalPoints(((CheckBox) rootNode.lookup("#ml_square_root_total")).isSelected());
        moneyLineObject.setAllowBelowZero(((CheckBox) rootNode.lookup("#ml_low_end_below_zero")).isSelected());
        moneyLineObject.setHighBlockPointFactor(Double.parseDouble(((TextField) rootNode.lookup("#ml_high_block_factor")).getText()));
        moneyLineObject.setLowerBlockPointFactor(Double.parseDouble(((TextField) rootNode.lookup("#ml_low_block_factor")).getText()));
        moneyLineObject.setHighStealPointFactor(Double.parseDouble(((TextField) rootNode.lookup("#ml_high_steal_factor")).getText()));
        moneyLineObject.setLowerStealPointFactor(Double.parseDouble(((TextField) rootNode.lookup("#ml_low_steal_factor")).getText()));
        moneyLineObject.setHomeTeamAdvantage(Double.parseDouble(((TextField) rootNode.lookup("#ml_home_advantage")).getText()));
        moneyLineObject.setPointThreshold(Double.parseDouble(((TextField) rootNode.lookup("#ml_point_threshold")).getText()));
        moneyLineObject.setModelOpponentSteals(true);
        moneyLineObject.setModelOpponentBlocks(true);
        moneyLineObject.setModelOpponentTurnovers(false);
        moneyLineObject.setPointsReducedPerBlock(2);
        moneyLineObject.setPointReductionPerSteal(2);
        moneyLineObject.setBetType("moneyline");

        List<BackTestIngestObject> list = new ArrayList<>();
        list.add(overUnderObject);
        list.add(pointSpreadObject);
        list.add(moneyLineObject);

        runTodaysGamesAction(list);

    }

    public void runTodaysGamesAction(List<BackTestIngestObject> backTestIngestObjectList){
        GameFinder gameFinder = new GameFinder();
        LocalDateTime localDateTime = LocalDate.now().atStartOfDay(ZoneId.of("US/Central")).toLocalDateTime();
        LocalDate localDate = localDateTime.toLocalDate();
        //List<CaesarsObject> caesarsObjectList = getCaesarsObjects(localDate, gameFinder);
        JSONObject jsonObject = StaticOddsFetcher.getMarketsCloudBet();
        System.out.println("williamHill: " + jsonObject);
        List<CaesarsObject> caesarsObjectList = parseCloudBetJSON(jsonObject);
        System.out.println(caesarsObjectList);
        //gameFinder.setPitcherRepository(pitcherRepository);
        gameList =  gameFinder.findGamesOlderThan2021(localDate);
        attemptToPairCaesarsObjectsToGames(gameList, caesarsObjectList);
        System.out.println(gameList);
        List<GameResult> gameResults = new ArrayList<>();
        for(Game game : gameList){
            GameResult gameResult = new GameResult();
            gameResult.setGameId(game.getGameId());
            gameResult.setAwayTeamName(game.getAwayTeamName());
            gameResult.setHomeTeamName(game.getHomeTeamName());
            gameResult.setDate(game.getDate().toString());
            gameResults.add(gameResult);
        }


        ObservableList<GameResult> data = FXCollections.observableArrayList(
                gameResults
        );

        game_id.setCellValueFactory(new PropertyValueFactory<>("gameId"));
        home_team_name.setCellValueFactory(new PropertyValueFactory<>("homeTeamName"));
        away_team_name.setCellValueFactory(new PropertyValueFactory<>("awayTeamName"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        total_points.setCellValueFactory(new PropertyValueFactory<>("totalPredictedPoints"));
        home_points.setCellValueFactory(new PropertyValueFactory<>("homePredictedPoints"));
        away_points.setCellValueFactory(new PropertyValueFactory<>("awayPredictedPoints"));
        over_under.setCellValueFactory(new PropertyValueFactory<>("overUnder"));
        ou_result.setCellValueFactory(new PropertyValueFactory<>("ou_result"));
        spread_result.setCellValueFactory(new PropertyValueFactory<>("spread_result"));
        ml_result.setCellValueFactory(new PropertyValueFactory<>("ml_result"));
        ml_away_pct.setCellValueFactory(new PropertyValueFactory<>("ml_away_pct"));
        shortenedGame.setCellValueFactory(new PropertyValueFactory<>("shortenedGame"));
        incomplete.setCellValueFactory(new PropertyValueFactory<>("incompleteModel"));
        todays_games_list.setItems(data);

        TodaysGamesThread todaysGamesThread = new TodaysGamesThread(gameResults, allGames, gameList);
        todaysGamesThread.setBackTestIngestObjects(backTestIngestObjectList);
        todaysGamesThread.setGameOddsHashMap(gameOddsHashMap);
        todaysGamesThread.start();
    }
    public void attemptToPairCaesarsObjectsToGames(List<Game> games, List<CaesarsObject> caesarsObjectList){
        for(Game game : games) {
            for (CaesarsObject caesarsObject : caesarsObjectList) {
                if (caesarsObject.getHomeTeamTriCode().contains(game.getHomeTeamTricode()) && caesarsObject.getAwayTeamTriCode().contains(game.getAwayTeamTricode())) {
                    GameOdds gameOdds = new GameOdds();
                    gameOdds.setGameId(game.getGameId());
                    gameOdds.setDate(game.getDate());
                    gameOdds.setOverUnder(caesarsObject.getOverUnder());
                    gameOdds.setAwayTeamSpread(caesarsObject.getAwayTeamSpread());
                    gameOdds.setHomeTeamSpread(caesarsObject.getHomeTeamSpread());
                    gameOdds.setAwayTeamSpreadOdds(caesarsObject.getAwayTeamSpreadOdds());
                    gameOdds.setHomeTeamSpreadOdds(caesarsObject.getHomeTeamSpreadOdds());
                    gameOdds.setHomeTeamTriCode(game.getHomeTeamTricode());
                    gameOdds.setAwayTeamTriCode(game.getAwayTeamTricode());
                    gameOdds.setHomeTeamMoneyLine(caesarsObject.getHomeTeamMoneyLine());
                    gameOdds.setAwayTeamMoneyLine(caesarsObject.getAwayTeamMoneyLine());
                    if (gameOddsRepository.findByGameId(gameOdds.getGameId()).isEmpty()) {
                        System.out.println("saving odds.");
                        gameOddsRepository.save(gameOdds);
                    } else {
                        GameOdds gameOdds1 = gameOddsRepository.findByGameId(gameOdds.getGameId()).get();
                        gameOdds1.setDate(gameOdds.getDate());
                        gameOdds1.setOverUnder(caesarsObject.getOverUnder());
                        gameOdds1.setAwayTeamSpread(caesarsObject.getAwayTeamSpread());
                        gameOdds1.setHomeTeamSpread(caesarsObject.getHomeTeamSpread());
                        gameOdds1.setAwayTeamSpreadOdds(caesarsObject.getAwayTeamSpreadOdds());
                        gameOdds1.setHomeTeamSpreadOdds(caesarsObject.getHomeTeamSpreadOdds());
                        gameOdds1.setHomeTeamTriCode(game.getHomeTeamTricode());
                        gameOdds1.setAwayTeamTriCode(game.getAwayTeamTricode());
                        gameOdds1.setHomeTeamMoneyLine(caesarsObject.getHomeTeamMoneyLine());
                        gameOdds1.setAwayTeamMoneyLine(caesarsObject.getAwayTeamMoneyLine());
                        System.out.println("saving odds.");
                        gameOddsRepository.save(gameOdds1);
                        break;
                    }
                }

            }
        }
    }

    public List<CaesarsObject> parseCloudBetJSON(JSONObject jsonObject){
        List<CaesarsObject> caesarsObjectList = new ArrayList<>();
        for(Object event : jsonObject.getJSONArray("events")){
            JSONObject eventJsonObject = (JSONObject) event;
            if(( eventJsonObject.getString("status").equals("TRADING"))){
                CaesarsObject caesarsObject = new CaesarsObject();
                caesarsObject.setEventTitle(eventJsonObject.getString("name"));
                if(eventJsonObject.has("away")){
                    caesarsObject.setAwayTeamName(eventJsonObject.getJSONObject("away").getString("name"));
                    caesarsObject.setAwayTeamTriCode(eventJsonObject.getJSONObject("away").getString("abbreviation"));
                }
                if(eventJsonObject.has("home")){
                    caesarsObject.setHomeTeamName(eventJsonObject.getJSONObject("home").getString("name"));
                    caesarsObject.setHomeTeamTriCode(eventJsonObject.getJSONObject("home").getString("abbreviation"));
                }

                if(eventJsonObject.getJSONObject("markets").has("basketball.moneyline") &&
                        eventJsonObject.getJSONObject("markets").has("basketball.handicap") &&
                        eventJsonObject.getJSONObject("markets").has("basketball.totals")) {

                    JSONObject moneyLineObject = eventJsonObject.getJSONObject("markets").getJSONObject("basketball.moneyline").getJSONObject("submarkets").getJSONObject("period=ot&period=ft");
                    for (Object selection : moneyLineObject.getJSONArray("selections")) {
                        JSONObject selectionJson = (JSONObject) selection;
                        if (selectionJson.getString("outcome").equals("home")) {
                            caesarsObject.setHomeTeamMoneyLine(convertDecimalToAmericanOdds(selectionJson.getDouble("price")));
                        } else if (selectionJson.getString("outcome").equals("away")) {
                            caesarsObject.setAwayTeamMoneyLine(convertDecimalToAmericanOdds(selectionJson.getDouble("price")));
                        }
                    }
                    JSONObject runLineObject = eventJsonObject.getJSONObject("markets").getJSONObject("basketball.handicap").getJSONObject("submarkets").getJSONObject("period=ot&period=ft");
                    JSONObject homeRunLine = (JSONObject) runLineObject.getJSONArray("selections").get(0);
                    JSONObject awayRunLine = (JSONObject) runLineObject.getJSONArray("selections").get(1);
                    String homeTrimmedString = homeRunLine.getString("params").substring(homeRunLine.getString("params").indexOf("handicap="));
                    if(homeTrimmedString.contains("&")) {
                        homeTrimmedString = homeTrimmedString.substring(0, homeTrimmedString.indexOf("&")).replace("handicap=", "");
                    }else{
                        homeTrimmedString = homeTrimmedString.replace("handicap=", "");
                    }
                    String awayTrimmedString = awayRunLine.getString("params").substring(awayRunLine.getString("params").indexOf("handicap="));
                    if(awayTrimmedString.contains("&")){
                        awayTrimmedString = awayTrimmedString.substring(0, awayTrimmedString.indexOf("&")).replace("handicap=", "");
                    }else{
                        awayTrimmedString = awayTrimmedString.replace("handicap=", "");
                    }

                    double homeLine = Double.parseDouble(homeTrimmedString);
                    double awayLine = Double.parseDouble(awayTrimmedString) * -1;

                    caesarsObject.setHomeTeamSpread(homeLine);
                    caesarsObject.setAwayTeamSpread(awayLine);
                    caesarsObject.setHomeTeamSpreadOdds(convertDecimalToAmericanOdds(homeRunLine.getDouble("price")));
                    caesarsObject.setAwayTeamSpreadOdds(convertDecimalToAmericanOdds(awayRunLine.getDouble("price")));

                    JSONObject overUnderObject = eventJsonObject.getJSONObject("markets").getJSONObject("basketball.totals").getJSONObject("submarkets").getJSONObject("period=ot&period=ft");
                    JSONObject overLine = (JSONObject) overUnderObject.getJSONArray("selections").get(0);
                    JSONObject underLine = (JSONObject) overUnderObject.getJSONArray("selections").get(1);
                    String trimmedOverString = overLine.getString("params").substring(overLine.getString("params").indexOf("total="));
                    if(trimmedOverString.contains("&")) {
                        trimmedOverString = trimmedOverString.substring(0, trimmedOverString.indexOf("&")).replace("total=", "");
                    }else{
                        trimmedOverString = trimmedOverString.replace("total=", "");
                    }
                    double overTotal = Double.parseDouble(trimmedOverString);

                    caesarsObject.setOverUnder(overTotal);
                    caesarsObject.setOddsOver(convertDecimalToAmericanOdds(overLine.getDouble("price")));
                    caesarsObject.setOddsUnder(convertDecimalToAmericanOdds(underLine.getDouble("price")));
                    caesarsObjectList.add(caesarsObject);
                }

            }
        }
        return caesarsObjectList;
    }



    @FXML
    public void loadSettings() {
        JSONObject jsonObject = attemptToRestoreSettings();
        System.out.println(jsonObject);
        if(jsonObject != null){
            if(jsonObject.has("gameLookbackList")) {
                ou_player_lookback.setText(jsonObject.getString("gameLookbackList"));
            }
            if(jsonObject.has("highBlockFactor")) {
                ou_high_block_factor.setText(jsonObject.getString("highBlockFactor"));
            }
            if(jsonObject.has("lowBlockFactor")) {
                ou_low_block_factor.setText(jsonObject.getString("lowBlockFactor"));
            }
            if(jsonObject.has("highStealFactor")) {
                ou_high_steal_factor.setText(jsonObject.getString("highStealFactor"));
            }
            if(jsonObject.has("lowStealFactor")) {
                ou_low_steal_factor.setText(jsonObject.getString("lowStealFactor"));
            }
            if(jsonObject.has("doublesqrt")){
                ou_double_square_root_true.setSelected(jsonObject.getBoolean("doublesqrt"));
            }
            if(jsonObject.has("sqrtTotal")){
                ou_total_square_root_true.setSelected(jsonObject.getBoolean("sqrtTotal"));
            }
            if(jsonObject.has("ou_belowZero")) {
                ((CheckBox) rootNode.lookup("#ou_low_end_below_zero")).setSelected(jsonObject.getBoolean("ou_belowZero"));
            }
            if(jsonObject.has("ou_point_threshold")) {
                ((TextField) rootNode.lookup("#ou_point_threshold")).setText(jsonObject.getString("ou_point_threshold"));
            }
            if(jsonObject.has("spread_gameLookbackList")) {
                ((TextField) rootNode.lookup("#spread_player_lookback")).setText(jsonObject.getString("spread_gameLookbackList"));
            }
            if(jsonObject.has("spread_highBlockFactor")) {
                ((TextField) rootNode.lookup("#spread_high_block_factor")).setText(jsonObject.getString("spread_highBlockFactor"));
            }
            if(jsonObject.has("spread_lowBlockFactor")) {
                ((TextField) rootNode.lookup("#spread_low_block_factor")).setText(jsonObject.getString("spread_lowBlockFactor"));
            }
            if(jsonObject.has("spread_highStealFactor")) {
                ((TextField) rootNode.lookup("#spread_high_steal_factor")).setText(jsonObject.getString("spread_highStealFactor"));
            }
            if(jsonObject.has("spread_lowStealFactor")) {
                ((TextField) rootNode.lookup("#spread_low_steal_factor")).setText(jsonObject.getString("spread_lowStealFactor"));
            }
            if(jsonObject.has("spread_homeAdvantage")) {
                ((TextField) rootNode.lookup("#spread_home_advantage")).setText(jsonObject.getString("spread_homeAdvantage"));
            }
            if(jsonObject.has("spread_doublesqrt")) {
                ((CheckBox) rootNode.lookup("#spread_double_square_root")).setSelected(jsonObject.getBoolean("spread_doublesqrt"));
            }
            if(jsonObject.has("spread_sqrtTotal")) {
                ((CheckBox) rootNode.lookup("#spread_square_root_total")).setSelected(jsonObject.getBoolean("spread_sqrtTotal"));
            }
            if(jsonObject.has("spread_belowZero")) {
                ((CheckBox) rootNode.lookup("#spread_low_end_below_zero")).setSelected(jsonObject.getBoolean("spread_belowZero"));
            }
            if(jsonObject.has("spread_point_threshold")) {
                ((TextField) rootNode.lookup("#spread_point_threshold")).setText(jsonObject.getString("spread_point_threshold"));
            }
            if(jsonObject.has("ml_gameLookbackList")) {
                ((TextField) rootNode.lookup("#ml_player_lookback")).setText(jsonObject.getString("ml_gameLookbackList"));
            }
            if(jsonObject.has("ml_highBlockFactor")) {
                ((TextField) rootNode.lookup("#ml_high_block_factor")).setText(jsonObject.getString("ml_highBlockFactor"));
            }
            if(jsonObject.has("ml_lowBlockFactor")) {
                ((TextField) rootNode.lookup("#ml_low_block_factor")).setText(jsonObject.getString("ml_lowBlockFactor"));
            }
            if(jsonObject.has("ml_highStealFactor")) {
                ((TextField) rootNode.lookup("#ml_high_steal_factor")).setText(jsonObject.getString("ml_highStealFactor"));
            }
            if(jsonObject.has("ml_lowStealFactor")) {
                ((TextField) rootNode.lookup("#ml_low_steal_factor")).setText(jsonObject.getString("ml_lowStealFactor"));
            }
            if(jsonObject.has("ml_homeAdvantage")) {
                ((TextField) rootNode.lookup("#ml_home_advantage")).setText(jsonObject.getString("ml_homeAdvantage"));
            }
            if(jsonObject.has("ml_doublesqrt")) {
                ((CheckBox) rootNode.lookup("#ml_double_square_root")).setSelected(jsonObject.getBoolean("ml_doublesqrt"));
            }
            if(jsonObject.has("ml_sqrtTotal")) {
                ((CheckBox) rootNode.lookup("#ml_square_root_total")).setSelected(jsonObject.getBoolean("ml_sqrtTotal"));
            }
            if(jsonObject.has("ml_belowZero")) {
                ((CheckBox) rootNode.lookup("#ml_low_end_below_zero")).setSelected(jsonObject.getBoolean("ml_belowZero"));
            }
            if(jsonObject.has("ml_point_threshold")) {
                ((TextField) rootNode.lookup("#ml_point_threshold")).setText(jsonObject.getString("ml_point_threshold"));
            }
        }
    }
//
//    public List<Double> returnDoublesFromString(String string){
//        List<Double> doubleList = new ArrayList<>();
//        String[] items = string.split(",");
//        for(String item : items){
//            doubleList.add(Double.parseDouble(item));
//        }
//        return doubleList;
//    }
//    public List<Integer> returnIntegersFromString(String string){
//        List<Integer> integerList = new ArrayList<>();
//        String[] items = string.split(",");
//        for(String item : items){
//            integerList.add(Integer.parseInt(item));
//        }
//        return integerList;
//    }
    @FXML
    public void saveSettings(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("gameLookbackList", ou_player_lookback.getText() );
        jsonObject.put("highBlockFactor", ou_high_block_factor.getText());
        jsonObject.put("lowBlockFactor", ou_low_block_factor.getText());
        jsonObject.put("highStealFactor", ou_high_steal_factor.getText());
        jsonObject.put("lowStealFactor", ou_low_steal_factor.getText());
        jsonObject.put("doublesqrt", ou_double_square_root_true.isSelected());
        jsonObject.put("sqrtTotal", ou_total_square_root_true.isSelected());
        jsonObject.put("ou_belowZero", ((CheckBox) rootNode.lookup("#ou_low_end_below_zero")).isSelected());
        jsonObject.put("ou_point_threshold", ((TextField) rootNode.lookup("#ou_point_threshold")).getText());
        jsonObject.put("spread_gameLookbackList", ((TextField) rootNode.lookup("#spread_player_lookback")).getText());
        jsonObject.put("spread_highBlockFactor", ((TextField) rootNode.lookup("#spread_high_block_factor")).getText());
        jsonObject.put("spread_lowBlockFactor", ((TextField) rootNode.lookup("#spread_low_block_factor")).getText());
        jsonObject.put("spread_highStealFactor", ((TextField) rootNode.lookup("#spread_high_steal_factor")).getText());
        jsonObject.put("spread_lowStealFactor", ((TextField) rootNode.lookup("#spread_low_steal_factor")).getText());
        jsonObject.put("spread_homeAdvantage", ((TextField) rootNode.lookup("#spread_home_advantage")).getText());
        jsonObject.put("spread_doublesqrt", ((CheckBox) rootNode.lookup("#spread_double_square_root")).isSelected());
        jsonObject.put("spread_sqrtTotal",((CheckBox) rootNode.lookup("#spread_square_root_total")).isSelected());
        jsonObject.put("spread_belowZero", ((CheckBox) rootNode.lookup("#spread_low_end_below_zero")).isSelected());
        jsonObject.put("spread_point_threshold", ((TextField) rootNode.lookup("#spread_point_threshold")).getText());
        jsonObject.put("ml_gameLookbackList", ((TextField) rootNode.lookup("#ml_player_lookback")).getText());
        jsonObject.put("ml_highBlockFactor", ((TextField) rootNode.lookup("#ml_high_block_factor")).getText());
        jsonObject.put("ml_lowBlockFactor", ((TextField) rootNode.lookup("#ml_low_block_factor")).getText());
        jsonObject.put("ml_highStealFactor", ((TextField) rootNode.lookup("#ml_high_steal_factor")).getText());
        jsonObject.put("ml_lowStealFactor", ((TextField) rootNode.lookup("#ml_low_steal_factor")).getText());
        jsonObject.put("ml_homeAdvantage", ((TextField) rootNode.lookup("#ml_home_advantage")).getText());
        jsonObject.put("ml_doublesqrt", ((CheckBox) rootNode.lookup("#ml_double_square_root")).isSelected());
        jsonObject.put("ml_sqrtTotal",((CheckBox) rootNode.lookup("#ml_square_root_total")).isSelected());
        jsonObject.put("ml_belowZero", ((CheckBox) rootNode.lookup("#ml_low_end_below_zero")).isSelected());
        jsonObject.put("ml_point_threshold", ((TextField) rootNode.lookup("#ml_point_threshold")).getText());
        saveSettingsToJsonFile(jsonObject);
    }
//
//
    public void saveSettingsToJsonFile(JSONObject jsonObject){
        ClassLoader classLoader = getClass().getClassLoader();
        System.out.println(classLoader.getClass().getResource("/"));
        try {
            PrintWriter writer =
                    new PrintWriter(
                            new File("C:/Users/Mark/Documents/nba_saved_settings.json"));
            writer.print(jsonObject.toString());
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    //TODO:: implement import odds from csv
    @FXML
    public void importFromFile(){

    }
    @FXML
    public void exportGameOdds(){

    }
    @FXML
    public void readOdds(){

    }
//
//
    public static JSONObject attemptToRestoreSettings(){
        try{
            if(new File("C:/Users/Mark/Documents/nba_saved_settings.json").isFile()){
                List<String> string = Files.readAllLines(Paths.get("C:/Users/Mark/Documents/nba_saved_settings.json"));

                return new JSONObject(string.get(0));


            }else{
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public double convertDecimalToAmericanOdds(double decimalOdds){
       if(decimalOdds - 1 > 1){
           return (decimalOdds -1)*100;
       }else{
           return -100/(decimalOdds - 1);
       }

    }
}
