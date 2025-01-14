package BaseballQuant;

import BaseballQuant.Model.*;
import BaseballQuant.Repository.*;
import BaseballQuant.Threads.TodaysGamesThread;
import BaseballQuant.Util.CSVExporter;
import BaseballQuant.Util.CSVImporter;
import BaseballQuant.Util.GameFinder;
import SportsQuant.Model.GameResult;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
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
    private List<MLBGame> allGames;
    //private List<MLBGameOdds> allOdds;
    private Parent rootNode;
    private GameRepository gameRepository;
    private MLBGameOddsRepository gameOddsRepository;
    private PitcherRepository pitcherRepository;
    List<MLBGame> gameList;
    @FXML private TextField ou_thread_count_input;
    @FXML private TextField ou_player_lookback;
    @FXML private TextField ou_pitcher_lookback;
    @FXML private TextField ou_bullpen_lookback;
    @FXML private TextField ou_high_run_factor;
    @FXML private TextField ou_low_run_factor;
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mlbView.fxml"));
        loader.setControllerFactory(mainContext::getBean);
        loader.setController(this);
        rootNode = loader.load();
        this.gameRepository = mainContext.getBean(GameRepository.class);
        this.gameOddsRepository = mainContext.getBean(MLBGameOddsRepository.class);
        this.pitcherRepository = mainContext.getBean(PitcherRepository.class);
        //System.out.println(playerRepository.findAll());

//        allOdds =  mongoTemplate.findAll(MLBGameOdds.class, "mlbGameOdds");
//        allGames =  mongoTemplate.findAll(MLBGame.class, "mlbGame");
        allGames = (List<MLBGame>) gameRepository.findAll();
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
    public void runPointSpread(){

    }

    @FXML
    public void runOverUnder(){
        BackTestIngestObject overUnderObject = new BackTestIngestObject();
        overUnderObject.setGameCount(Integer.parseInt(ou_player_lookback.getText()));
        overUnderObject.setPitcherGameLookback(Integer.parseInt(ou_pitcher_lookback.getText()));
        overUnderObject.setDoubleSquareRoot(ou_double_square_root_true.isSelected());
        overUnderObject.setSquareRootTotalPoints(ou_total_square_root_true.isSelected());
        overUnderObject.setBullpenGameCount(Integer.parseInt(ou_bullpen_lookback.getText()));
//        overUnderObject.setHighRunFactor(Double.parseDouble(ou_high_run_factor.getText()));
//        overUnderObject.setLowRunFactor(Double.parseDouble(ou_low_run_factor.getText()));
//        overUnderObject.setHighfieldingFactor(Double.parseDouble(ou_high_field_factor.getText()));
//        overUnderObject.setLowfieldingFactor(Double.parseDouble(ou_low_field_factor.getText()));
//        overUnderObject.setHighStolenBaseFactor(Double.parseDouble(ou_high_steal_factor.getText()));
//        overUnderObject.setLowStolenBaseFactor(Double.parseDouble(ou_low_steal_factor.getText()));
        overUnderObject.setAllowLowEndBelowZero(((CheckBox) rootNode.lookup("#ou_low_end_below_zero")).isSelected());
        overUnderObject.setModelOpposingFielding(true);
       // overUnderObject.setModelOpposingPitching(true);
        overUnderObject.setModelStolenBases(true);
        overUnderObject.setBetType("overunder");

        BackTestIngestObject pointSpreadObject = new BackTestIngestObject();
        pointSpreadObject.setGameCount(Integer.parseInt(((TextField) rootNode.lookup("#spread_player_lookback")).getText()));
        pointSpreadObject.setPitcherGameLookback(Integer.parseInt(((TextField) rootNode.lookup("#spread_pitcher_lookback")).getText()));
        pointSpreadObject.setDoubleSquareRoot(((CheckBox) rootNode.lookup("#spread_double_square_root")).isSelected());
        pointSpreadObject.setSquareRootTotalPoints(((CheckBox) rootNode.lookup("#spread_square_root_total")).isSelected());
        pointSpreadObject.setAllowLowEndBelowZero(((CheckBox) rootNode.lookup("#spread_low_end_below_zero")).isSelected());
        pointSpreadObject.setBullpenGameCount(Integer.parseInt(((TextField) rootNode.lookup("#spread_bullpen_lookback")).getText()));
//        pointSpreadObject.setHighRunFactor(Double.parseDouble(((TextField) rootNode.lookup("#spread_high_run_factor")).getText()));
//        pointSpreadObject.setLowRunFactor(Double.parseDouble(((TextField) rootNode.lookup("#spread_low_run_factor")).getText()));
//        pointSpreadObject.setHighfieldingFactor(Double.parseDouble(((TextField) rootNode.lookup("#spread_high_field_factor")).getText()));
//        pointSpreadObject.setLowfieldingFactor(Double.parseDouble(((TextField) rootNode.lookup("#spread_low_field_factor")).getText()));
//        pointSpreadObject.setHighStolenBaseFactor(Double.parseDouble(((TextField) rootNode.lookup("#spread_high_steal_factor")).getText()));
//        pointSpreadObject.setLowStolenBaseFactor(Double.parseDouble(((TextField) rootNode.lookup("#spread_low_steal_factor")).getText()));
        pointSpreadObject.setModelOpposingFielding(true);
     //   pointSpreadObject.setModelOpposingPitching(true);
        pointSpreadObject.setModelStolenBases(true);
        pointSpreadObject.setBetType("spread");

        BackTestIngestObject moneyLineObject = new BackTestIngestObject();
        moneyLineObject.setGameCount(Integer.parseInt(((TextField) rootNode.lookup("#ml_player_lookback")).getText()));
        moneyLineObject.setPitcherGameLookback(Integer.parseInt(((TextField) rootNode.lookup("#ml_pitcher_lookback")).getText()));
        moneyLineObject.setDoubleSquareRoot(((CheckBox) rootNode.lookup("#ml_double_square_root")).isSelected());
        moneyLineObject.setSquareRootTotalPoints(((CheckBox) rootNode.lookup("#ml_square_root_total")).isSelected());
        moneyLineObject.setAllowLowEndBelowZero(((CheckBox) rootNode.lookup("#ml_low_end_below_zero")).isSelected());
        moneyLineObject.setBullpenGameCount(Integer.parseInt(((TextField) rootNode.lookup("#ml_bullpen_lookback")).getText()));
//        moneyLineObject.setHighRunFactor(Double.parseDouble(((TextField) rootNode.lookup("#ml_high_run_factor")).getText()));
//        moneyLineObject.setLowRunFactor(Double.parseDouble(((TextField) rootNode.lookup("#ml_low_run_factor")).getText()));
//        moneyLineObject.setHighfieldingFactor(Double.parseDouble(((TextField) rootNode.lookup("#ml_high_field_factor")).getText()));
//        moneyLineObject.setLowfieldingFactor(Double.parseDouble(((TextField) rootNode.lookup("#ml_low_field_factor")).getText()));
//        moneyLineObject.setHighStolenBaseFactor(Double.parseDouble(((TextField) rootNode.lookup("#ml_high_steal_factor")).getText()));
//        moneyLineObject.setLowStolenBaseFactor(Double.parseDouble(((TextField) rootNode.lookup("#ml_low_steal_factor")).getText()));
//        moneyLineObject.setHomeAdvantageHigh(Double.parseDouble(((TextField) rootNode.lookup("#ml_home_high_adv")).getText()));
//        moneyLineObject.setHomeAdvantageLow(Double.parseDouble(((TextField) rootNode.lookup("#ml_home_low_adv")).getText()));
        moneyLineObject.setModelOpposingFielding(true);
     //   moneyLineObject.setModelOpposingPitching(true);
        moneyLineObject.setModelStolenBases(true);
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
        //JSONObject jsonObject = gameFinder.getMarketsCloudBet();
//        System.out.println("williamHill: " + jsonObject);
        //List<CaesarsObject> caesarsObjectList = parseCloudBetJSON(jsonObject);
        JSONObject jsonObject = gameFinder.getOverUnderWilliamHill(localDate);
        System.out.println("williamHill: " + jsonObject);
        List<CaesarsObject> caesarsObjectList = parseWilliamHillJSON(jsonObject);
        System.out.println(caesarsObjectList);
        gameFinder.setPitcherRepository(pitcherRepository);
        gameList =  gameFinder.findTodaysGames(localDate);
        attemptToPairCaesarsObjectsToMLBGames(gameList, caesarsObjectList);
        //System.out.println(gameList);
        List<GameResult> gameResults = new ArrayList<>();
        for(MLBGame game : gameList){
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
        todaysGamesThread.setMlbGameOdds((List<MLBGameOdds>) gameOddsRepository.findAll());
        todaysGamesThread.start();
    }


    @FXML
    public void loadSettings() {
        JSONObject jsonObject = attemptToRestoreSettings();
        System.out.println(jsonObject);
        if(jsonObject != null){
            if(jsonObject.has("gameLookbackList")) {
                ou_player_lookback.setText(jsonObject.getString("gameLookbackList"));
            }
            if(jsonObject.has("pointPerBlockList")) {
                ou_pitcher_lookback.setText(jsonObject.getString("pointPerBlockList"));
            }
            if(jsonObject.has("bullpenLookback")) {
                ou_bullpen_lookback.setText(jsonObject.getString("bullpenLookback"));
            }
            if(jsonObject.has("highRunFactor")) {
                ou_high_run_factor.setText(jsonObject.getString("highRunFactor"));
            }
            if(jsonObject.has("lowRunFactor")) {
                ou_low_run_factor.setText(jsonObject.getString("lowRunFactor"));
            }
            if(jsonObject.has("highFieldFactor")) {
                ou_high_field_factor.setText(jsonObject.getString("highFieldFactor"));
            }
            if(jsonObject.has("lowFieldFactor")) {
                ou_low_field_factor.setText(jsonObject.getString("lowFieldFactor"));
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
            if(jsonObject.has("spread_gameLookbackList")) {
                ((TextField) rootNode.lookup("#spread_player_lookback")).setText(jsonObject.getString("spread_gameLookbackList"));
            }
            if(jsonObject.has("spread_pointPerBlockList")) {
                ((TextField) rootNode.lookup("#spread_pitcher_lookback")).setText(jsonObject.getString("spread_pointPerBlockList"));
            }
            if(jsonObject.has("spread_bullpenLookback")) {
                ((TextField) rootNode.lookup("#spread_bullpen_lookback")).setText(jsonObject.getString("spread_bullpenLookback"));
            }
            if(jsonObject.has("spread_highRunFactor")) {
                ((TextField) rootNode.lookup("#spread_high_run_factor")).setText(jsonObject.getString("spread_highRunFactor"));
            }
            if(jsonObject.has("spread_lowRunFactor")) {
                ((TextField) rootNode.lookup("#spread_low_run_factor")).setText(jsonObject.getString("spread_lowRunFactor"));
            }
            if(jsonObject.has("spread_highFieldFactor")) {
                ((TextField) rootNode.lookup("#spread_high_field_factor")).setText(jsonObject.getString("spread_highFieldFactor"));
            }
            if(jsonObject.has("spread_lowFieldFactor")) {
                ((TextField) rootNode.lookup("#spread_low_field_factor")).setText(jsonObject.getString("spread_lowFieldFactor"));
            }
            if(jsonObject.has("spread_highStealFactor")) {
                ((TextField) rootNode.lookup("#spread_high_steal_factor")).setText(jsonObject.getString("spread_highStealFactor"));
            }
            if(jsonObject.has("spread_lowStealFactor")) {
                ((TextField) rootNode.lookup("#spread_low_steal_factor")).setText(jsonObject.getString("spread_lowStealFactor"));
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
            if(jsonObject.has("ml_gameLookbackList")) {
                ((TextField) rootNode.lookup("#ml_player_lookback")).setText(jsonObject.getString("ml_gameLookbackList"));
            }
            if(jsonObject.has("ml_pointPerBlockList")) {
                ((TextField) rootNode.lookup("#ml_pitcher_lookback")).setText(jsonObject.getString("ml_pointPerBlockList"));
            }
            if(jsonObject.has("ml_bullpenLookback")) {
                ((TextField) rootNode.lookup("#ml_bullpen_lookback")).setText(jsonObject.getString("ml_bullpenLookback"));
            }
            if(jsonObject.has("ml_highRunFactor")) {
                ((TextField) rootNode.lookup("#ml_high_run_factor")).setText(jsonObject.getString("ml_highRunFactor"));
            }
            if(jsonObject.has("ml_lowRunFactor")) {
                ((TextField) rootNode.lookup("#ml_low_run_factor")).setText(jsonObject.getString("ml_lowRunFactor"));
            }
            if(jsonObject.has("ml_highFieldFactor")) {
                ((TextField) rootNode.lookup("#ml_high_field_factor")).setText(jsonObject.getString("ml_highFieldFactor"));
            }
            if(jsonObject.has("ml_lowFieldFactor")) {
                ((TextField) rootNode.lookup("#ml_low_field_factor")).setText(jsonObject.getString("ml_lowFieldFactor"));
            }
            if(jsonObject.has("ml_highStealFactor")) {
                ((TextField) rootNode.lookup("#ml_high_steal_factor")).setText(jsonObject.getString("ml_highStealFactor"));
            }
            if(jsonObject.has("ml_lowStealFactor")) {
                ((TextField) rootNode.lookup("#ml_low_steal_factor")).setText(jsonObject.getString("ml_lowStealFactor"));
            }
            if(jsonObject.has("ml_homeHighAdvantage")) {
                ((TextField) rootNode.lookup("#ml_home_high_adv")).setText(jsonObject.getString("ml_homeHighAdvantage"));
            }
            if(jsonObject.has("ml_homeLowAdvantage")) {
                ((TextField) rootNode.lookup("#ml_home_low_adv")).setText(jsonObject.getString("ml_homeLowAdvantage"));
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
        }
    }
    @FXML
    public void exportGameOdds(){
        CSVExporter csvExporter = new CSVExporter();
        csvExporter.writeAllGameOddsToCsv((List<MLBGameOdds>) gameOddsRepository.findAll());
    }
    @FXML
    public void readOdds(){
        CSVImporter csvImporter = new CSVImporter();
        List<CSVGameObject> gameObjects = csvImporter.readGameOddsExport();
        for(CSVGameObject csvGameObject : gameObjects){
            MLBGameOdds mlbGameOdds = new MLBGameOdds();
            mlbGameOdds.setGameId(csvGameObject.getGameId());
            mlbGameOdds.setAwayTeamMoneyLine(csvGameObject.getAwayTeamMoneyLine());
            mlbGameOdds.setAwayTeamSpreadOdds(csvGameObject.getAwayTeamSpreadOdds());
            mlbGameOdds.setAwayTeamSpread(csvGameObject.getAwayTeamSpread());
            mlbGameOdds.setHomeTeamMoneyLine(csvGameObject.getHomeTeamMoneyLine());
            mlbGameOdds.setHomeTeamSpreadOdds(csvGameObject.getHomeTeamSpreadOdds());
            mlbGameOdds.setHomeTeamSpread(csvGameObject.getHomeTeamSpread());
            mlbGameOdds.setHomeTeamName(csvGameObject.getHomeTeamName());
            mlbGameOdds.setAwayTeamName(csvGameObject.getAwayTeamName());
        }
    }

    @FXML
    public void importFromFile(){
        CSVImporter csvImporter = new CSVImporter();
        List<CSVGameObject> csvGames = csvImporter.readGameDataFromCSV();
        attemptToMatchCSVToMLBGame(csvGames, allGames);
    }

    public List<Double> returnDoublesFromString(String string){
        List<Double> doubleList = new ArrayList<>();
        String[] items = string.split(",");
        for(String item : items){
            doubleList.add(Double.parseDouble(item));
        }
        return doubleList;
    }
    public List<Integer> returnIntegersFromString(String string){
        List<Integer> integerList = new ArrayList<>();
        String[] items = string.split(",");
        for(String item : items){
            integerList.add(Integer.parseInt(item));
        }
        return integerList;
    }
    @FXML
    public void saveSettings(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("gameLookbackList", ou_player_lookback.getText() );
        jsonObject.put("pointPerBlockList", ou_pitcher_lookback.getText());
        jsonObject.put("bullpenLookback", ou_bullpen_lookback.getText());
        jsonObject.put("highRunFactor", ou_high_run_factor.getText());
        jsonObject.put("lowRunFactor", ou_low_run_factor.getText());
        jsonObject.put("highFieldFactor", ou_high_field_factor.getText());
        jsonObject.put("lowFieldFactor", ou_low_field_factor.getText());
        jsonObject.put("highStealFactor", ou_high_steal_factor.getText());
        jsonObject.put("lowStealFactor", ou_low_steal_factor.getText());
        jsonObject.put("doublesqrt", ou_double_square_root_true.isSelected());
        jsonObject.put("sqrtTotal", ou_total_square_root_true.isSelected());
        jsonObject.put("ou_belowZero", ((CheckBox) rootNode.lookup("#ou_low_end_below_zero")).isSelected());
        jsonObject.put("spread_gameLookbackList", ((TextField) rootNode.lookup("#spread_player_lookback")).getText());
        jsonObject.put("spread_pointPerBlockList", ((TextField) rootNode.lookup("#spread_pitcher_lookback")).getText());
        jsonObject.put("spread_bullpenLookback", ((TextField) rootNode.lookup("#spread_bullpen_lookback")).getText());
        jsonObject.put("spread_highRunFactor", ((TextField) rootNode.lookup("#spread_high_run_factor")).getText());
        jsonObject.put("spread_lowRunFactor", ((TextField) rootNode.lookup("#spread_low_run_factor")).getText());
        jsonObject.put("spread_highFieldFactor", ((TextField) rootNode.lookup("#spread_high_field_factor")).getText());
        jsonObject.put("spread_lowFieldFactor", ((TextField) rootNode.lookup("#spread_low_field_factor")).getText());
        jsonObject.put("spread_highStealFactor", ((TextField) rootNode.lookup("#spread_high_steal_factor")).getText());
        jsonObject.put("spread_lowStealFactor", ((TextField) rootNode.lookup("#spread_low_steal_factor")).getText());
        jsonObject.put("spread_doublesqrt", ((CheckBox) rootNode.lookup("#spread_double_square_root")).isSelected());
        jsonObject.put("spread_sqrtTotal",((CheckBox) rootNode.lookup("#spread_square_root_total")).isSelected());
        jsonObject.put("spread_belowZero", ((CheckBox) rootNode.lookup("#spread_low_end_below_zero")).isSelected());
        jsonObject.put("ml_gameLookbackList", ((TextField) rootNode.lookup("#ml_player_lookback")).getText());
        jsonObject.put("ml_pointPerBlockList", ((TextField) rootNode.lookup("#ml_pitcher_lookback")).getText());
        jsonObject.put("ml_bullpenLookback", ((TextField) rootNode.lookup("#ml_bullpen_lookback")).getText());
        jsonObject.put("ml_highRunFactor", ((TextField) rootNode.lookup("#ml_high_run_factor")).getText());
        jsonObject.put("ml_lowRunFactor", ((TextField) rootNode.lookup("#ml_low_run_factor")).getText());
        jsonObject.put("ml_highFieldFactor", ((TextField) rootNode.lookup("#ml_high_field_factor")).getText());
        jsonObject.put("ml_lowFieldFactor", ((TextField) rootNode.lookup("#ml_low_field_factor")).getText());
        jsonObject.put("ml_highStealFactor", ((TextField) rootNode.lookup("#ml_high_steal_factor")).getText());
        jsonObject.put("ml_lowStealFactor", ((TextField) rootNode.lookup("#ml_low_steal_factor")).getText());
        jsonObject.put("ml_homeLowAdvantage", ((TextField) rootNode.lookup("#ml_home_low_adv")).getText());
        jsonObject.put("ml_homeHighAdvantage", ((TextField) rootNode.lookup("#ml_home_high_adv")).getText());
        jsonObject.put("ml_doublesqrt", ((CheckBox) rootNode.lookup("#ml_double_square_root")).isSelected());
        jsonObject.put("ml_sqrtTotal",((CheckBox) rootNode.lookup("#ml_square_root_total")).isSelected());
        jsonObject.put("ml_belowZero", ((CheckBox) rootNode.lookup("#ml_low_end_below_zero")).isSelected());
        saveSettingsToJsonFile(jsonObject);
    }




    public void saveSettingsToJsonFile(JSONObject jsonObject){
        ClassLoader classLoader = getClass().getClassLoader();
        System.out.println(classLoader.getClass().getResource("/"));
        try {
            PrintWriter writer =
                    new PrintWriter(
                            new File("C:/Users/Mark/Documents/saved_settings.json"));
            writer.print(jsonObject.toString());
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void attemptToMatchCSVToMLBGame(List<CSVGameObject> csvGameObjects, List<MLBGame> allGames){
        List<MLBGameOdds> assignedGameOdds = new ArrayList<>();
        int totalAssigned = 0;
        for(CSVGameObject csvGameObject : csvGameObjects) {
            LocalDateTime localDate = csvGameObject.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusHours(8);
            LocalDateTime localDateBefore = localDate.minusDays(1).minusHours(2);
            LocalDateTime localDateAfter = localDate.plusDays(1).plusHours(2);
            String csvAwayTriCode = csvGameObject.getAwayTeamTricode().toLowerCase();
            String csvHomeTriCode = csvGameObject.getHomeTeamTricode().toLowerCase();
            String csvAwayPitcher = StringUtils.stripAccents(csvGameObject.getAwayPitcherName().toLowerCase());
            String csvHomePitcher = StringUtils.stripAccents(csvGameObject.getHomePitcherName().toLowerCase());
            boolean found = false;
            for (MLBGame mlbGame : allGames) {
                if (mlbGame.getAwayStartingPitcher() != null && mlbGame.getHomeStartingPitcher() != null) {
                    //System.out.println(mlbGame);
                    String awayTriCode = mlbGame.getAwayTeamTricode().toLowerCase();
                    String homeTriCode = mlbGame.getHomeTeamTricode().toLowerCase();
                    String awayPitcher = mlbGame.getAwayStartingPitcher().getFullName().toLowerCase().replace(" ", "");
                    String homePitcher = mlbGame.getHomeStartingPitcher().getFullName().toLowerCase().replace(" ", "");
                    LocalDateTime gameDateTime = mlbGame.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    if (gameDateTime.isAfter(localDateBefore)) {
                        if (gameDateTime.isBefore(localDateAfter)) {
                            if (awayTriCode.equals(csvAwayTriCode)) {
                                if (homeTriCode.equals(csvHomeTriCode)) {
                                    if (awayPitcher.contains(csvAwayPitcher)) {
                                        if (homePitcher.contains(csvHomePitcher)) {
                                            System.out.println("Assigning " + csvGameObject + " to Game Id: " + mlbGame.getGameId());
                                            MLBGameOdds mlbGameOdds = new MLBGameOdds();
                                            mlbGameOdds.setGameId(mlbGame.getGameId());
                                            mlbGameOdds.setHomeTeamName(mlbGame.getHomeTeamName());
                                            mlbGameOdds.setAwayTeamName(mlbGame.getAwayTeamName());
                                            mlbGameOdds.setDate(mlbGame.getDate());
                                            mlbGameOdds.setAwayTeamSpread(csvGameObject.getAwayTeamSpread());
                                            mlbGameOdds.setHomeTeamSpread(csvGameObject.getHomeTeamSpread());
                                            mlbGameOdds.setAwayTeamSpreadOdds(csvGameObject.getAwayTeamSpreadOdds());
                                            mlbGameOdds.setHomeTeamSpreadOdds(csvGameObject.getHomeTeamSpreadOdds());
                                            mlbGameOdds.setAwayTeamMoneyLine(csvGameObject.getAwayTeamMoneyLine());
                                            mlbGameOdds.setHomeTeamMoneyLine(csvGameObject.getHomeTeamMoneyLine());
                                            mlbGameOdds.setOverUnder(csvGameObject.getOverUnder());
                                            assignedGameOdds.add(mlbGameOdds);
                                            totalAssigned++;
                                            found = true;
                                        } else {
                                            System.out.println(mlbGame + " incorrect home Pitcher.");
                                        }
                                    } else {
                                        System.out.println(mlbGame + " incorrect away Pitcher.");
                                    }
                                } else {
                                    System.out.println(mlbGame + " incorrect home triCode.");
                                }
                            }
                        }
                    }
                }
            }
            if (!found) {
                System.out.println("stop here.");
            }
        }
        System.out.println(totalAssigned + "/" + csvGameObjects.size());
        int savedGames = 0;
        for(MLBGameOdds mlbGameOdds : assignedGameOdds){
            if(gameOddsRepository.findByGameId(mlbGameOdds.getGameId()).isEmpty()){
                gameOddsRepository.save(mlbGameOdds);
                savedGames++;
            }else{
                if(gameOddsRepository.findByGameId(mlbGameOdds.getGameId()).get().getAwayTeamMoneyLine() == 0.0){
                    MLBGameOdds tempOdds = gameOddsRepository.findByGameId(mlbGameOdds.getGameId()).get();
                    tempOdds.setAwayTeamMoneyLine(mlbGameOdds.getAwayTeamMoneyLine());
                    tempOdds.setHomeTeamMoneyLine(mlbGameOdds.getHomeTeamMoneyLine());
                    gameOddsRepository.save(tempOdds);
                }
            }
        }
        System.out.println(" saved games: " + savedGames);
    }

    public static JSONObject attemptToRestoreSettings(){
        try{
            if(new File("C:/Users/Mark/Documents/saved_settings.json").isFile()){
                List<String> string = Files.readAllLines(Paths.get("C:/Users/Mark/Documents/saved_settings.json"));

                return new JSONObject(string.get(0));


            }else{
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void attemptToPairCaesarsObjectsToMLBGames(List<MLBGame> games, List<CaesarsObject> caesarsObjectList){
        for(MLBGame mlbGame : games){
            String homeTeamName = mlbGame.getHomeTeamName();
            if(homeTeamName.equals("D-backs")){
                homeTeamName = "Diamondbacks";
            }
            String awayTeamName = mlbGame.getAwayTeamName();
            if(awayTeamName.equals("D-backs")){
                awayTeamName = "Diamondbacks";
            }
            if(mlbGame.getHomeStartingPitcher() != null) {
                String homeTeamPitcher = mlbGame.getHomeStartingPitcher().getFullName();
                List<String> nameStrings = Arrays.asList(StringUtils.stripAccents(homeTeamPitcher).replace("Jr.", "").split(" "));
                String pitcherLastName = nameStrings.get(nameStrings.size() - 1).replace(".", "");
                System.out.println("pitcher lastName: " + pitcherLastName);
                for (CaesarsObject caesarsObject : caesarsObjectList) {
                    if (caesarsObject.getHomeTeamName() != null && caesarsObject.getHomePitcherName() != null){
                        if(caesarsObject.getHomePitcherName().toLowerCase(Locale.ROOT).contains("l m jr")){
                            caesarsObject.setHomePitcherName("Lance McCullers Jr");
                        }
                        String cleanPitcherName = StringUtils.stripAccents(pitcherLastName).replace(".", "").toLowerCase().replace("jr.", "");;
                        String cleanCaesarsPitcherName = StringUtils.stripAccents(caesarsObject.getHomePitcherName()).toLowerCase().replace(".", "").replace("jr.", "");
                        if (cleanCaesarsPitcherName.contains(cleanPitcherName.toLowerCase()) && caesarsObject.getHomeTeamName().toLowerCase().contains(homeTeamName.toLowerCase())){
                            MLBGameOdds mlbGameOdds = new MLBGameOdds();
                            mlbGameOdds.setGameId(mlbGame.getGameId());
                            mlbGameOdds.setDate(mlbGame.getDate());
                            mlbGameOdds.setOverUnder(caesarsObject.getOverUnder());
                            mlbGameOdds.setAwayTeamSpread(caesarsObject.getAwayTeamSpread());
                            mlbGameOdds.setHomeTeamSpread(caesarsObject.getHomeTeamSpread());
                            mlbGameOdds.setAwayTeamSpreadOdds(caesarsObject.getAwayTeamSpreadOdds());
                            mlbGameOdds.setHomeTeamSpreadOdds(caesarsObject.getHomeTeamSpreadOdds());
                            mlbGameOdds.setHomeTeamName(mlbGame.getHomeTeamName());
                            mlbGameOdds.setAwayTeamName(mlbGame.getAwayTeamName());
                            mlbGameOdds.setHomeTeamMoneyLine(caesarsObject.getHomeTeamMoneyLine());
                            mlbGameOdds.setAwayTeamMoneyLine(caesarsObject.getAwayTeamMoneyLine());
                            if (gameOddsRepository.findByGameId(mlbGameOdds.getGameId()).isEmpty()) {
                                System.out.println("saving odds.");
                                gameOddsRepository.save(mlbGameOdds);
                            }else{
                                MLBGameOdds mlbGameOdds1 = gameOddsRepository.findByGameId(mlbGameOdds.getGameId()).get();
                                mlbGameOdds1.setDate(mlbGame.getDate());
                                mlbGameOdds1.setOverUnder(caesarsObject.getOverUnder());
                                mlbGameOdds1.setAwayTeamSpread(caesarsObject.getAwayTeamSpread());
                                mlbGameOdds1.setHomeTeamSpread(caesarsObject.getHomeTeamSpread());
                                mlbGameOdds1.setAwayTeamSpreadOdds(caesarsObject.getAwayTeamSpreadOdds());
                                mlbGameOdds1.setHomeTeamSpreadOdds(caesarsObject.getHomeTeamSpreadOdds());
                                mlbGameOdds1.setHomeTeamName(mlbGame.getHomeTeamName());
                                mlbGameOdds1.setAwayTeamName(mlbGame.getAwayTeamName());
                                mlbGameOdds1.setHomeTeamMoneyLine(caesarsObject.getHomeTeamMoneyLine());
                                mlbGameOdds1.setAwayTeamMoneyLine(caesarsObject.getAwayTeamMoneyLine());
                                gameOddsRepository.save(mlbGameOdds1);
                                break;
                            }
                        }
                    }else{
//                        String cleanCaesarsTitle = StringUtils.stripAccents(caesarsObject.getEventTitle()).toLowerCase();
//                        if(cleanCaesarsTitle.contains(mlbGame.getHomeTeamName().toLowerCase()) && cleanCaesarsTitle.contains(mlbGame.getAwayTeamName().toLowerCase()) ){
//                            MLBGameOdds mlbGameOdds = new MLBGameOdds();
//                            mlbGameOdds.setGameId(mlbGame.getGameId());
//                            mlbGameOdds.setDate(mlbGame.getDate());
//                            mlbGameOdds.setOverUnder(caesarsObject.getOverUnder());
//                            mlbGameOdds.setAwayTeamSpread(caesarsObject.getAwayTeamSpread());
//                            mlbGameOdds.setHomeTeamSpread(caesarsObject.getHomeTeamSpread());
//                            mlbGameOdds.setAwayTeamSpreadOdds(caesarsObject.getAwayTeamSpreadOdds());
//                            mlbGameOdds.setHomeTeamSpreadOdds(caesarsObject.getHomeTeamSpreadOdds());
//                            mlbGameOdds.setHomeTeamName(mlbGame.getHomeTeamName());
//                            mlbGameOdds.setAwayTeamName(mlbGame.getAwayTeamName());
//                            if (mlbGameOddsRepository.findByGameId(mlbGameOdds.getGameId()).isEmpty()) {
//                                System.out.println("saving odds.");
//                                mlbGameOddsRepository.save(mlbGameOdds);
//                                break;
//                            }else{
//                                MLBGameOdds mlbGameOdds1 = mlbGameOddsRepository.findByGameId(mlbGameOdds.getGameId()).get();
//                                mlbGameOdds1.setDate(mlbGame.getDate());
//                                mlbGameOdds1.setOverUnder(caesarsObject.getOverUnder());
//                                mlbGameOdds1.setAwayTeamSpread(caesarsObject.getAwayTeamSpread());
//                                mlbGameOdds1.setHomeTeamSpread(caesarsObject.getHomeTeamSpread());
//                                mlbGameOdds1.setAwayTeamSpreadOdds(caesarsObject.getAwayTeamSpreadOdds());
//                                mlbGameOdds1.setHomeTeamSpreadOdds(caesarsObject.getHomeTeamSpreadOdds());
//                                mlbGameOdds1.setHomeTeamName(mlbGame.getHomeTeamName());
//                                mlbGameOdds1.setAwayTeamName(mlbGame.getAwayTeamName());
//                                mlbGameOddsRepository.save(mlbGameOdds1);
//                                break;
//                            }
//                        }
                    }
                }
            }else if(mlbGame.getAwayStartingPitcher() != null){
                String awayTeamPitcher = mlbGame.getAwayStartingPitcher().getFullName();
                List<String> nameStrings = Arrays.asList(StringUtils.stripAccents(awayTeamPitcher).split(" "));
                String pitcherLastName = nameStrings.get(nameStrings.size() - 1);
                System.out.println("pitcher lastName: " + pitcherLastName);
                for (CaesarsObject caesarsObject : caesarsObjectList) {
                    if (caesarsObject.getAwayPitcherName() != null && caesarsObject.getAwayTeamName() != null) {
                        if(caesarsObject.getHomePitcherName().toLowerCase(Locale.ROOT).contains("l m jr")){
                            caesarsObject.setAwayPitcherName("Lance McCullers Jr");
                        }
                        String cleanPitcherName = StringUtils.stripAccents(pitcherLastName).replace(".", "").toLowerCase().replace("jr.", "");;
                        String cleanCaesarsPitcherName = StringUtils.stripAccents(caesarsObject.getAwayPitcherName()).toLowerCase().replace(".", "").replace("jr.", "");
                        if (cleanCaesarsPitcherName.contains(cleanPitcherName.toLowerCase()) && caesarsObject.getAwayTeamName().toLowerCase().contains(awayTeamName.toLowerCase())) {
                            MLBGameOdds mlbGameOdds = new MLBGameOdds();
                            mlbGameOdds.setGameId(mlbGame.getGameId());
                            mlbGameOdds.setDate(mlbGame.getDate());
                            mlbGameOdds.setOverUnder(caesarsObject.getOverUnder());
                            mlbGameOdds.setAwayTeamSpread(caesarsObject.getAwayTeamSpread());
                            mlbGameOdds.setHomeTeamSpread(caesarsObject.getHomeTeamSpread());
                            mlbGameOdds.setAwayTeamSpreadOdds(caesarsObject.getAwayTeamSpreadOdds());
                            mlbGameOdds.setHomeTeamSpreadOdds(caesarsObject.getHomeTeamSpreadOdds());
                            mlbGameOdds.setHomeTeamName(mlbGame.getHomeTeamName());
                            mlbGameOdds.setAwayTeamName(mlbGame.getAwayTeamName());
                            mlbGameOdds.setHomeTeamMoneyLine(caesarsObject.getHomeTeamMoneyLine());
                            mlbGameOdds.setAwayTeamMoneyLine(caesarsObject.getAwayTeamMoneyLine());
                            if (gameOddsRepository.findByGameId(mlbGameOdds.getGameId()).isEmpty()) {
                                System.out.println("saving odds.");
                                gameOddsRepository.save(mlbGameOdds);
                                break;
                            }else{
                                MLBGameOdds mlbGameOdds1 = gameOddsRepository.findByGameId(mlbGameOdds.getGameId()).get();
                                mlbGameOdds1.setDate(mlbGame.getDate());
                                mlbGameOdds1.setOverUnder(caesarsObject.getOverUnder());
                                mlbGameOdds1.setAwayTeamSpread(caesarsObject.getAwayTeamSpread());
                                mlbGameOdds1.setHomeTeamSpread(caesarsObject.getHomeTeamSpread());
                                mlbGameOdds1.setAwayTeamSpreadOdds(caesarsObject.getAwayTeamSpreadOdds());
                                mlbGameOdds1.setHomeTeamSpreadOdds(caesarsObject.getHomeTeamSpreadOdds());
                                mlbGameOdds1.setHomeTeamName(mlbGame.getHomeTeamName());
                                mlbGameOdds1.setAwayTeamName(mlbGame.getAwayTeamName());
                                mlbGameOdds1.setHomeTeamMoneyLine(caesarsObject.getHomeTeamMoneyLine());
                                mlbGameOdds1.setAwayTeamMoneyLine(caesarsObject.getAwayTeamMoneyLine());
                                gameOddsRepository.save(mlbGameOdds1);
                                break;
                            }
                        }
                    }else{
                        String cleanCaesarsTitle = StringUtils.stripAccents(caesarsObject.getEventTitle()).toLowerCase();
                        if(cleanCaesarsTitle.contains(mlbGame.getHomeTeamName().toLowerCase()) && cleanCaesarsTitle.contains(mlbGame.getAwayTeamName().toLowerCase()) ){
                            MLBGameOdds mlbGameOdds = new MLBGameOdds();
                            mlbGameOdds.setGameId(mlbGame.getGameId());
                            mlbGameOdds.setDate(mlbGame.getDate());
                            mlbGameOdds.setOverUnder(caesarsObject.getOverUnder());
                            mlbGameOdds.setAwayTeamSpread(caesarsObject.getAwayTeamSpread());
                            mlbGameOdds.setHomeTeamSpread(caesarsObject.getHomeTeamSpread());
                            mlbGameOdds.setAwayTeamSpreadOdds(caesarsObject.getAwayTeamSpreadOdds());
                            mlbGameOdds.setHomeTeamSpreadOdds(caesarsObject.getHomeTeamSpreadOdds());
                            mlbGameOdds.setHomeTeamName(mlbGame.getHomeTeamName());
                            mlbGameOdds.setAwayTeamName(mlbGame.getAwayTeamName());
                            mlbGameOdds.setHomeTeamMoneyLine(caesarsObject.getHomeTeamMoneyLine());
                            mlbGameOdds.setAwayTeamMoneyLine(caesarsObject.getAwayTeamMoneyLine());
                            if (gameOddsRepository.findByGameId(mlbGameOdds.getGameId()).isEmpty()) {
                                System.out.println("saving odds.");
                                gameOddsRepository.save(mlbGameOdds);
                                break;
                            }else{
                                MLBGameOdds mlbGameOdds1 = gameOddsRepository.findByGameId(mlbGameOdds.getGameId()).get();
                                mlbGameOdds1.setDate(mlbGame.getDate());
                                mlbGameOdds1.setOverUnder(caesarsObject.getOverUnder());
                                mlbGameOdds1.setAwayTeamSpread(caesarsObject.getAwayTeamSpread());
                                mlbGameOdds1.setHomeTeamSpread(caesarsObject.getHomeTeamSpread());
                                mlbGameOdds1.setAwayTeamSpreadOdds(caesarsObject.getAwayTeamSpreadOdds());
                                mlbGameOdds1.setHomeTeamSpreadOdds(caesarsObject.getHomeTeamSpreadOdds());
                                mlbGameOdds1.setHomeTeamName(mlbGame.getHomeTeamName());
                                mlbGameOdds1.setAwayTeamName(mlbGame.getAwayTeamName());
                                mlbGameOdds1.setHomeTeamMoneyLine(caesarsObject.getHomeTeamMoneyLine());
                                mlbGameOdds1.setAwayTeamMoneyLine(caesarsObject.getAwayTeamMoneyLine());
                                gameOddsRepository.save(mlbGameOdds1);
                                break;
                            }
                        }
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
                }
                if(eventJsonObject.has("home")){
                    caesarsObject.setHomeTeamName(eventJsonObject.getJSONObject("home").getString("name"));
                }
                for(String playerKey : eventJsonObject.getJSONObject("players").keySet()){
                    JSONObject playerJson = eventJsonObject.getJSONObject("players").getJSONObject(playerKey);
                    if(playerJson.getJSONObject("position").getString("name").equals("Starting Pitcher")){
                        if(playerJson.getString("team").equals("HOME")){
                            caesarsObject.setHomePitcherName(playerJson.getString("name"));
                        }else if(playerJson.getString("team").equals("AWAY")){
                            caesarsObject.setAwayPitcherName(playerJson.getString("name"));
                        }
                    }
                }
                if(eventJsonObject.getJSONObject("markets").has("baseball.moneyline") &&
                        eventJsonObject.getJSONObject("markets").has("baseball.run_line") &&
                        eventJsonObject.getJSONObject("markets").has("baseball.totals")) {
                    JSONObject moneyLineObject = eventJsonObject.getJSONObject("markets").getJSONObject("baseball.moneyline").getJSONObject("submarkets").getJSONObject("period=ot&period=ft");
                    for (Object selection : moneyLineObject.getJSONArray("selections")) {
                        JSONObject selectionJson = (JSONObject) selection;
                        if (selectionJson.getString("outcome").equals("home")) {
                            caesarsObject.setHomeTeamMoneyLine(selectionJson.getDouble("price"));
                        } else if (selectionJson.getString("outcome").equals("away")) {
                            caesarsObject.setAwayTeamMoneyLine(selectionJson.getDouble("price"));
                        }
                    }
                    JSONObject runLineObject = eventJsonObject.getJSONObject("markets").getJSONObject("baseball.run_line").getJSONObject("submarkets").getJSONObject("period=ot&period=ft");
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
                    caesarsObject.setHomeTeamSpreadOdds(homeRunLine.getDouble("price"));
                    caesarsObject.setAwayTeamSpreadOdds(awayRunLine.getDouble("price"));

                    JSONObject overUnderObject = eventJsonObject.getJSONObject("markets").getJSONObject("baseball.totals").getJSONObject("submarkets").getJSONObject("period=ot&period=ft");
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
                    caesarsObject.setOddsOver(overLine.getDouble("price"));
                    caesarsObject.setOddsUnder(underLine.getDouble("price"));
                    caesarsObjectList.add(caesarsObject);
                }

            }
        }
        return caesarsObjectList;
    }


    public List<CaesarsObject> parseWilliamHillJSON(JSONObject jsonObject){
        List<CaesarsObject> caesarsObjectList = new ArrayList<>();
        for(Object category : jsonObject.getJSONArray("competitions")){
            JSONObject categoryJsonObject = (JSONObject) category;
            if(( categoryJsonObject.getString("groupName").equals("MLB"))){
                for(Object event : categoryJsonObject.getJSONArray("events")){
                    JSONObject eventJsonObject = (JSONObject) event;
                    if(!eventJsonObject.getBoolean("started")){
                        CaesarsObject caesarsObject = new CaesarsObject();
                        caesarsObject.setEventTitle(eventJsonObject.getString("name").replace("|", ""));
                        String[] strings = eventJsonObject.getString("name").split(" \\|at\\| ");
                        String awayTeamName = strings[0].replace("|", "");

                        String homeTeamName = strings[1].replace("|", "");

                        System.out.println(Arrays.toString(strings));
                        if(eventJsonObject.has("teamMeta")){
                            for (Object team : eventJsonObject.getJSONArray("teamMeta")) {
                                JSONObject teamJsonObject = (JSONObject) team;
                                if (teamJsonObject.getString("team").contains(awayTeamName)) {
                                    caesarsObject.setAwayTeamName(teamJsonObject.getString("team"));
                                    caesarsObject.setAwayPitcherName(teamJsonObject.getString("pitcher"));
                                } else {
                                    caesarsObject.setHomeTeamName(teamJsonObject.getString("team"));
                                    caesarsObject.setHomePitcherName(teamJsonObject.getString("pitcher"));

                                }
                            }
                        }else{
                            caesarsObject.setAwayTeamName(awayTeamName);
                            caesarsObject.setHomeTeamName(homeTeamName);
                        }

                        for(Object market : eventJsonObject.getJSONArray("markets")){
                            JSONObject marketJsonObject = (JSONObject) market;
                            if(marketJsonObject.getString("displayName").equals("Total Runs")){
                                if(marketJsonObject.has("line")) {
                                    caesarsObject.setOverUnder(marketJsonObject.getDouble("line"));
                                }
                            }
                            if(marketJsonObject.getString("displayName").equals("Run Line")){
                                for(Object selection : marketJsonObject.getJSONArray("selections")){
                                    JSONObject selectionJsonObject = (JSONObject) selection;
                                    if(selectionJsonObject.getString("type").equals("away")){
                                        caesarsObject.setAwayTeamSpread(marketJsonObject.getDouble("line") * -1);
                                        caesarsObject.setAwayTeamSpreadOdds(((JSONObject) selection).getJSONObject("price").getDouble("a"));
                                    }else{
                                        caesarsObject.setHomeTeamSpread(marketJsonObject.getDouble("line"));
                                        caesarsObject.setHomeTeamSpreadOdds(((JSONObject) selection).getJSONObject("price").getDouble("a"));

                                    }
                                }
                            }
                            if(marketJsonObject.getString("displayName").equals("Money Line") && marketJsonObject.getBoolean("keyMarket")){
                                for(Object selection : marketJsonObject.getJSONArray("selections")){
                                    JSONObject selectionJsonObject = (JSONObject) selection;
                                    if(selectionJsonObject.getString("type").equals("away")){
                                        caesarsObject.setAwayTeamMoneyLine(((JSONObject) selection).getJSONObject("price").getDouble("a"));
                                    }else{
                                        caesarsObject.setHomeTeamMoneyLine(((JSONObject) selection).getJSONObject("price").getDouble("a"));
                                    }
                                }
                            }
                        }
                        caesarsObjectList.add(caesarsObject);
                    }
                }
            }
        }
        return caesarsObjectList;
    }



}
