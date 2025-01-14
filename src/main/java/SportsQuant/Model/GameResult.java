package SportsQuant.Model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class GameResult {
    private SimpleIntegerProperty gameId;
    private SimpleDoubleProperty awayPredictedPoints;
    private SimpleDoubleProperty homePredictedPoints;
    private SimpleDoubleProperty totalPredictedPoints;
    private SimpleDoubleProperty overUnder;
    private SimpleStringProperty awayTeamName;
    private SimpleStringProperty homeTeamName;
    private SimpleStringProperty ou_result;
    private SimpleStringProperty spread_result;
    private SimpleStringProperty ml_result;
    private SimpleStringProperty ml_away_pct;
    private SimpleStringProperty date;
    private SimpleBooleanProperty incompleteModel;
    private SimpleBooleanProperty shortenedGame;
    public GameResult() {
        this.gameId = new SimpleIntegerProperty();
        this.awayPredictedPoints = new SimpleDoubleProperty();
        this.homePredictedPoints = new SimpleDoubleProperty();
        this.totalPredictedPoints = new SimpleDoubleProperty();
        this.overUnder = new SimpleDoubleProperty();
        this.awayTeamName = new SimpleStringProperty();
        this.homeTeamName = new SimpleStringProperty();
        this.ou_result = new SimpleStringProperty();
        this.date = new SimpleStringProperty();
        this.incompleteModel = new SimpleBooleanProperty();
        this.shortenedGame = new SimpleBooleanProperty();
        this.ml_result = new SimpleStringProperty();
        this.spread_result = new SimpleStringProperty();
        this.ml_away_pct = new SimpleStringProperty();
    }

    public String getAwayTeamName() {
        return awayTeamName.get();
    }

    public SimpleStringProperty awayTeamNameProperty() {
        return awayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName.set(awayTeamName);
    }

    public String getHomeTeamName() {
        return homeTeamName.get();
    }

    public SimpleStringProperty homeTeamNameProperty() {
        return homeTeamName;
    }

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName.set(homeTeamName);
    }

    public int getGameId() {
        return gameId.get();
    }

    public SimpleIntegerProperty gameIdProperty() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId.set(gameId);
    }

    public double getAwayPredictedPoints() {
        return awayPredictedPoints.get();
    }

    public SimpleDoubleProperty awayPredictedPointsProperty() {
        return awayPredictedPoints;
    }

    public void setAwayPredictedPoints(double awayPredictedPoints) {
        this.awayPredictedPoints.set(awayPredictedPoints);
    }

    public double getHomePredictedPoints() {
        return homePredictedPoints.get();
    }

    public SimpleDoubleProperty homePredictedPointsProperty() {
        return homePredictedPoints;
    }

    public void setHomePredictedPoints(double homePredictedPoints) {
        this.homePredictedPoints.set(homePredictedPoints);
    }

    public double getTotalPredictedPoints() {
        return totalPredictedPoints.get();
    }

    public SimpleDoubleProperty totalPredictedPointsProperty() {
        return totalPredictedPoints;
    }

    public void setTotalPredictedPoints(double totalPredictedPoints) {
        this.totalPredictedPoints.set(totalPredictedPoints);
    }

    public double getOverUnder() {
        return overUnder.get();
    }

    public SimpleDoubleProperty overUnderProperty() {
        return overUnder;
    }

    public void setOverUnder(double overUnder) {
        this.overUnder.set(overUnder);
    }

    public String getOu_result() {
        return ou_result.get();
    }

    public SimpleStringProperty ou_resultProperty() {
        return ou_result;
    }

    public void setOu_result(String ou_result) {
        this.ou_result.set(ou_result);
    }

    public String getSpread_result() {
        return spread_result.get();
    }

    public SimpleStringProperty spread_resultProperty() {
        return spread_result;
    }

    public void setSpread_result(String spread_result) {
        this.spread_result.set(spread_result);
    }

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public boolean isIncompleteModel() {
        return incompleteModel.get();
    }

    public SimpleBooleanProperty incompleteModelProperty() {
        return incompleteModel;
    }

    public void setIncompleteModel(boolean incompleteModel) {
        this.incompleteModel.set(incompleteModel);
    }

    public boolean isShortenedGame() {
        return shortenedGame.get();
    }

    public SimpleBooleanProperty shortenedGameProperty() {
        return shortenedGame;
    }

    public void setShortenedGame(boolean shortenedGame) {
        this.shortenedGame.set(shortenedGame);
    }

    public String getMl_result() {
        return ml_result.get();
    }

    public SimpleStringProperty ml_resultProperty() {
        return ml_result;
    }

    public void setMl_result(String ml_result) {
        this.ml_result.set(ml_result);
    }

    public String getMl_away_pct() {
        return ml_away_pct.get();
    }

    public SimpleStringProperty ml_away_pctProperty() {
        return ml_away_pct;
    }

    public void setMl_away_pct(String ml_away_pct) {
        this.ml_away_pct.set(ml_away_pct);
    }
}
