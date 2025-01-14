package SportsQuant.Model;

import java.time.LocalDate;

public class BackTestResult {
    private int gamesToTest;
    private LocalDate startDate;
    //settings
    private int playerGameLookBack;
    private double pointsReducedPerBlock;
    private double pointsReducedPerSteal;
    private double pointsReducedPerTurnover;
    private double pointsReducedPerRebound;
    private double pointsReducedPerFoul;
    private int dayLookbackCap;
    private double lowerBlockPointFactor;
    private double highBlockPointFactor;
    private double lowerStealPointFactor;
    private double highStealPointFactor;
    private double lowerTurnoverPointFactor;
    private double highTurnoverPointFactor;
    private double lowerReboundPointFactor;
    private double highReboundPointFactor;
    private double lowerFoulPointFactor;
    private double highFoulPointFactor;
    private boolean doubleSquareRoot;
    private double blockExponent;
    private boolean modelOpponentBlocks;
    private boolean modelOpponentSteals;
    private double lowerPointAdjustmentFactor;
    private boolean factorPostBlock;
    private double gameTimeThreshold;
    private double homeTeamAdvantage;
    private boolean modelOpponentTurnovers;
    private String betType;
    private int exactResults;
    private int exactLosses;
    private int ppgCorrect;
    private int ppgIncorrect;
    private double pointThreshold;
    private boolean allowBelowZero;
    private boolean squareRootTotal;
    private TestingPair testingPair;
    private BackTestResult originalResult;
    private Integer fractalWindow;
    //results
    private int predictOver;
    private int predictUnder;
    private int predictCorrect;
    private int predictIncorrect;
    private int actualOver;
    private int actualUnder;
    private double estimatedPoints;
    private double actualPoints;
    private double pointDelta;
    private double correctPercent;
    private double endingMoney;
    private double dailyVol;

    private double pointvolweight;

    public double getHomeTeamAdvantage() {
        return homeTeamAdvantage;
    }

    public void setHomeTeamAdvantage(double homeTeamAdvantage) {
        this.homeTeamAdvantage = homeTeamAdvantage;
    }

    public int getPlayerGameLookBack() {
        return playerGameLookBack;
    }

    public void setPlayerGameLookBack(int playerGameLookBack) {
        this.playerGameLookBack = playerGameLookBack;
    }

    public double getPointsReducedPerBlock() {
        return pointsReducedPerBlock;
    }

    public void setPointsReducedPerBlock(double pointsReducedPerBlock) {
        this.pointsReducedPerBlock = pointsReducedPerBlock;
    }

    public double getLowerBlockPointFactor() {
        return lowerBlockPointFactor;
    }

    public void setLowerBlockPointFactor(double lowerBlockPointFactor) {
        this.lowerBlockPointFactor = lowerBlockPointFactor;
    }

    public int getPredictOver() {
        return predictOver;
    }

    public void setPredictOver(int predictOver) {
        this.predictOver = predictOver;
    }

    public int getPredictUnder() {
        return predictUnder;
    }

    public void setPredictUnder(int predictUnder) {
        this.predictUnder = predictUnder;
    }

    public int getPredictCorrect() {
        return predictCorrect;
    }

    public void setPredictCorrect(int predictCorrect) {
        this.predictCorrect = predictCorrect;
    }

    public int getPredictIncorrect() {
        return predictIncorrect;
    }

    public void setPredictIncorrect(int predictIncorrect) {
        this.predictIncorrect = predictIncorrect;
    }

    public double getCorrectPercent() {
        return correctPercent;
    }

    public void setCorrectPercent(double correctPercent) {
        this.correctPercent = correctPercent;
    }

    public int getActualOver() {
        return actualOver;
    }

    public void setActualOver(int actualOver) {
        this.actualOver = actualOver;
    }

    public int getActualUnder() {
        return actualUnder;
    }

    public void setActualUnder(int actualUnder) {
        this.actualUnder = actualUnder;
    }

    public double getEstimatedPoints() {
        return estimatedPoints;
    }

    public void setEstimatedPoints(double estimatedPoints) {
        this.estimatedPoints = estimatedPoints;
    }

    public double getActualPoints() {
        return actualPoints;
    }

    public void setActualPoints(double actualPoints) {
        this.actualPoints = actualPoints;
    }

    public boolean isDoubleSquareRoot() {
        return doubleSquareRoot;
    }

    public void setDoubleSquareRoot(boolean doubleSquareRoot) {
        this.doubleSquareRoot = doubleSquareRoot;
    }



    public double getBlockExponent() {
        return blockExponent;
    }

    public void setBlockExponent(double blockExponent) {
        this.blockExponent = blockExponent;
    }

    public boolean isModelOpponentBlocks() {
        return modelOpponentBlocks;
    }

    public void setModelOpponentBlocks(boolean modelOpponentBlocks) {
        this.modelOpponentBlocks = modelOpponentBlocks;
    }

    public double getLowerPointAdjustmentFactor() {
        return lowerPointAdjustmentFactor;
    }

    public void setLowerPointAdjustmentFactor(double lowerPointAdjustmentFactor) {
        this.lowerPointAdjustmentFactor = lowerPointAdjustmentFactor;
    }

    public boolean isFactorPostBlock() {
        return factorPostBlock;
    }

    public void setFactorPostBlock(boolean factorPostBlock) {
        this.factorPostBlock = factorPostBlock;
    }

    public int getExactLosses() {
        return exactLosses;
    }

    public void setExactLosses(int exactLosses) {
        this.exactLosses = exactLosses;
    }

    public int getExactResults() {
        return exactResults;
    }

    public void setExactResults(int exactResults) {
        this.exactResults = exactResults;
    }

    public double getPointsReducedPerSteal() {
        return pointsReducedPerSteal;
    }

    public void setPointsReducedPerSteal(double pointsReducedPerSteal) {
        this.pointsReducedPerSteal = pointsReducedPerSteal;
    }

    public boolean isModelOpponentSteals() {
        return modelOpponentSteals;
    }

    public void setModelOpponentSteals(boolean modelOpponentSteals) {
        this.modelOpponentSteals = modelOpponentSteals;
    }

    public int getPpgCorrect() {
        return ppgCorrect;
    }

    public void setPpgCorrect(int ppgCorrect) {
        this.ppgCorrect = ppgCorrect;
    }

    public int getPpgIncorrect() {
        return ppgIncorrect;
    }

    public void setPpgIncorrect(int ppgIncorrect) {
        this.ppgIncorrect = ppgIncorrect;
    }

    @Override
    public String toString() {
        return "BackTestResult{" +
                "gamesToTest=" + gamesToTest +
                ", startDate=" + startDate +
                ", playerGameLookBack=" + playerGameLookBack +
                ", pointsReducedPerBlock=" + pointsReducedPerBlock +
                ", pointsReducedPerSteal=" + pointsReducedPerSteal +
                ", pointsReducedPerTurnover=" + pointsReducedPerTurnover +
                ", pointsReducedPerRebound=" + pointsReducedPerRebound +
                ", pointsReducedPerFoul=" + pointsReducedPerFoul +
                ", dayLookbackCap=" + dayLookbackCap +
                ", lowerBlockPointFactor=" + lowerBlockPointFactor +
                ", highBlockPointFactor=" + highBlockPointFactor +
                ", lowerStealPointFactor=" + lowerStealPointFactor +
                ", highStealPointFactor=" + highStealPointFactor +
                ", lowerTurnoverPointFactor=" + lowerTurnoverPointFactor +
                ", highTurnoverPointFactor=" + highTurnoverPointFactor +
                ", lowerReboundPointFactor=" + lowerReboundPointFactor +
                ", highReboundPointFactor=" + highReboundPointFactor +
                ", lowerFoulPointFactor=" + lowerFoulPointFactor +
                ", highFoulPointFactor=" + highFoulPointFactor +
                ", doubleSquareRoot=" + doubleSquareRoot +
                ", blockExponent=" + blockExponent +
                ", modelOpponentBlocks=" + modelOpponentBlocks +
                ", modelOpponentSteals=" + modelOpponentSteals +
                ", lowerPointAdjustmentFactor=" + lowerPointAdjustmentFactor +
                ", factorPostBlock=" + factorPostBlock +
                ", gameTimeThreshold=" + gameTimeThreshold +
                ", homeTeamAdvantage=" + homeTeamAdvantage +
                ", modelOpponentTurnovers=" + modelOpponentTurnovers +
                ", betType='" + betType + '\'' +
                ", exactResults=" + exactResults +
                ", exactLosses=" + exactLosses +
                ", ppgCorrect=" + ppgCorrect +
                ", ppgIncorrect=" + ppgIncorrect +
                ", pointThreshold=" + pointThreshold +
                ", allowBelowZero=" + allowBelowZero +
                ", squareRootTotal=" + squareRootTotal +
                ", testingPair=" + testingPair +
                ", predictOver=" + predictOver +
                ", predictUnder=" + predictUnder +
                ", predictCorrect=" + predictCorrect +
                ", predictIncorrect=" + predictIncorrect +
                ", actualOver=" + actualOver +
                ", actualUnder=" + actualUnder +
                ", estimatedPoints=" + estimatedPoints +
                ", actualPoints=" + actualPoints +
                ", pointDelta=" + pointDelta +
                ", correctPercent=" + correctPercent +
                ", endingMoney=" + endingMoney +
                ", dailyVol=" + dailyVol +
                '}';
    }

    public double getGameTimeThreshold() {
        return gameTimeThreshold;
    }

    public void setGameTimeThreshold(double gameTimeThreshold) {
        this.gameTimeThreshold = gameTimeThreshold;
    }

    public double getPointsReducedPerTurnover() {
        return pointsReducedPerTurnover;
    }

    public void setPointsReducedPerTurnover(double pointsReducedPerTurnover) {
        this.pointsReducedPerTurnover = pointsReducedPerTurnover;
    }

    public boolean isModelOpponentTurnovers() {
        return modelOpponentTurnovers;
    }

    public void setModelOpponentTurnovers(boolean modelOpponentTurnovers) {
        this.modelOpponentTurnovers = modelOpponentTurnovers;
    }

    public int getDayLookbackCap() {
        return dayLookbackCap;
    }

    public void setDayLookbackCap(int dayLookbackCap) {
        this.dayLookbackCap = dayLookbackCap;
    }

    public String getBetType() {
        return betType;
    }

    public void setBetType(String betType) {
        this.betType = betType;
    }

    public double getHighBlockPointFactor() {
        return highBlockPointFactor;
    }

    public void setHighBlockPointFactor(double highBlockPointFactor) {
        this.highBlockPointFactor = highBlockPointFactor;
    }

    public double getLowerStealPointFactor() {
        return lowerStealPointFactor;
    }

    public void setLowerStealPointFactor(double lowerStealPointFactor) {
        this.lowerStealPointFactor = lowerStealPointFactor;
    }

    public double getHighStealPointFactor() {
        return highStealPointFactor;
    }

    public void setHighStealPointFactor(double highStealPointFactor) {
        this.highStealPointFactor = highStealPointFactor;
    }

    public double getEndingMoney() {
        return endingMoney;
    }

    public void setEndingMoney(double endingMoney) {
        this.endingMoney = endingMoney;
    }

    public double getPointThreshold() {
        return pointThreshold;
    }

    public void setPointThreshold(double pointThreshold) {
        this.pointThreshold = pointThreshold;
    }

    public boolean isAllowBelowZero() {
        return allowBelowZero;
    }

    public void setAllowBelowZero(boolean allowBelowZero) {
        this.allowBelowZero = allowBelowZero;
    }

    public int getGamesToTest() {
        return gamesToTest;
    }

    public void setGamesToTest(int gamesToTest) {
        this.gamesToTest = gamesToTest;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public double getDailyVol() {
        return dailyVol;
    }

    public void setDailyVol(double dailyVol) {
        this.dailyVol = dailyVol;
    }

    public boolean isSquareRootTotal() {
        return squareRootTotal;
    }

    public void setSquareRootTotal(boolean squareRootTotal) {
        this.squareRootTotal = squareRootTotal;
    }

    public double getPointDelta() {
        return pointDelta;
    }

    public void setPointDelta(double pointDelta) {
        this.pointDelta = pointDelta;
    }

    public double getLowerTurnoverPointFactor() {
        return lowerTurnoverPointFactor;
    }

    public void setLowerTurnoverPointFactor(double lowerTurnoverPointFactor) {
        this.lowerTurnoverPointFactor = lowerTurnoverPointFactor;
    }

    public double getHighTurnoverPointFactor() {
        return highTurnoverPointFactor;
    }

    public void setHighTurnoverPointFactor(double highTurnoverPointFactor) {
        this.highTurnoverPointFactor = highTurnoverPointFactor;
    }

    public double getLowerReboundPointFactor() {
        return lowerReboundPointFactor;
    }

    public void setLowerReboundPointFactor(double lowerReboundPointFactor) {
        this.lowerReboundPointFactor = lowerReboundPointFactor;
    }

    public double getHighReboundPointFactor() {
        return highReboundPointFactor;
    }

    public void setHighReboundPointFactor(double highReboundPointFactor) {
        this.highReboundPointFactor = highReboundPointFactor;
    }

    public double getLowerFoulPointFactor() {
        return lowerFoulPointFactor;
    }

    public void setLowerFoulPointFactor(double lowerFoulPointFactor) {
        this.lowerFoulPointFactor = lowerFoulPointFactor;
    }

    public double getHighFoulPointFactor() {
        return highFoulPointFactor;
    }

    public void setHighFoulPointFactor(double highFoulPointFactor) {
        this.highFoulPointFactor = highFoulPointFactor;
    }

    public double getPointsReducedPerRebound() {
        return pointsReducedPerRebound;
    }

    public void setPointsReducedPerRebound(double pointsReducedPerRebound) {
        this.pointsReducedPerRebound = pointsReducedPerRebound;
    }

    public double getPointsReducedPerFoul() {
        return pointsReducedPerFoul;
    }

    public void setPointsReducedPerFoul(double pointsReducedPerFoul) {
        this.pointsReducedPerFoul = pointsReducedPerFoul;
    }

    public TestingPair getTestingPair() {
        return testingPair;
    }

    public void setTestingPair(TestingPair testingPair) {
        this.testingPair = testingPair;
    }

    public BackTestResult getOriginalResult() {
        return originalResult;
    }

    public void setOriginalResult(BackTestResult originalResult) {
        this.originalResult = originalResult;
    }

    public Integer getFractalWindow() {
        return fractalWindow;
    }

    public void setFractalWindow(Integer fractalWindow) {
        this.fractalWindow = fractalWindow;
    }

    public double getPointvolweight() {
        return pointvolweight;
    }

    public void setPointvolweight(double pointvolweight) {
        this.pointvolweight = pointvolweight;
    }
}

