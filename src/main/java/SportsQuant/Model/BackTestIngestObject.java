package SportsQuant.Model;

import java.time.LocalDate;

public class BackTestIngestObject implements Cloneable{
    private int gamesToTest;
    private int playerGameLookBack;
    private double pointsReducedPerBlock;
    private double pointsReducedPerTurnover;
    private double pointReductionPerSteal;
    private double pointsReducedPerRebound;
    private double pointsReducedPerFoul;
    private double gameTimeThreshold;
    private double lowerBlockPointFactor;
    private double highBlockPointFactor;
    private double lowerStealPointFactor;
    private double highStealPointFactor;
    private double highTurnoverPointFactor;
    private double lowerTurnoverPointFactor;
    private double highReboundPointFactor;
    private double lowerReboundPointFactor;
    private double highFoulPointFactor;
    private double lowerFoulPointFactor;
    private boolean doubleSquareRoot;
    private boolean allowBelowZero;
    private boolean replaceLowBlocksWithBPG;
    private double blockExponent;
    private boolean modelOpponentBlocks;
    private boolean squareRootTotalPoints;
    private boolean factorPostBlocks;
    private boolean modelOpponentSteals;
    private boolean modelOpponentTurnovers;
    private double pointThreshold;
    private double homeTeamAdvantage;
    private double numHigh;
    private double numLow;
    private TestingPair testingPair;
    private LocalDate startDate;
    private String betType;
    private int dayLookbackCap;
    private BackTestResult originalResult;
    private boolean started;
    private boolean finished;
    private Integer fractalWindow;
    private double pointvolweight;

    public BackTestIngestObject() {
        started = false;
        finished = false;
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

    public boolean isDoubleSquareRoot() {
        return doubleSquareRoot;
    }

    public void setDoubleSquareRoot(boolean doubleSquareRoot) {
        this.doubleSquareRoot = doubleSquareRoot;
    }

    public boolean isReplaceLowBlocksWithBPG() {
        return replaceLowBlocksWithBPG;
    }

    public void setReplaceLowBlocksWithBPG(boolean replaceLowBlocksWithBPG) {
        this.replaceLowBlocksWithBPG = replaceLowBlocksWithBPG;
    }

    public double getBlockExponent() {
        return blockExponent;
    }

    public void setBlockExponent(double blockExponent) {
        this.blockExponent = blockExponent;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isModelOpponentBlocks() {
        return modelOpponentBlocks;
    }

    public void setModelOpponentBlocks(boolean modelOpponentBlocks) {
        this.modelOpponentBlocks = modelOpponentBlocks;
    }

    @Override
    public String toString() {
        return "BackTestIngestObject{" +
                "gamesToTest=" + gamesToTest +
                ", playerGameLookBack=" + playerGameLookBack +
                ", pointsReducedPerBlock=" + pointsReducedPerBlock +
                ", pointsReducedPerTurnover=" + pointsReducedPerTurnover +
                ", pointReductionPerSteal=" + pointReductionPerSteal +
                ", pointsReducedPerRebound=" + pointsReducedPerRebound +
                ", gameTimeThreshold=" + gameTimeThreshold +
                ", lowerBlockPointFactor=" + lowerBlockPointFactor +
                ", highBlockPointFactor=" + highBlockPointFactor +
                ", lowerStealPointFactor=" + lowerStealPointFactor +
                ", highStealPointFactor=" + highStealPointFactor +
                ", highTurnoverPointFactor=" + highTurnoverPointFactor +
                ", lowerTurnoverPointFactor=" + lowerTurnoverPointFactor +
                ", highReboundPointFactor=" + highReboundPointFactor +
                ", lowerReboundPointFactor=" + lowerReboundPointFactor +
                ", doubleSquareRoot=" + doubleSquareRoot +
                ", allowBelowZero=" + allowBelowZero +
                ", replaceLowBlocksWithBPG=" + replaceLowBlocksWithBPG +
                ", blockExponent=" + blockExponent +
                ", modelOpponentBlocks=" + modelOpponentBlocks +
                ", squareRootTotalPoints=" + squareRootTotalPoints +
                ", factorPostBlocks=" + factorPostBlocks +
                ", modelOpponentSteals=" + modelOpponentSteals +
                ", modelOpponentTurnovers=" + modelOpponentTurnovers +
                ", pointThreshold=" + pointThreshold +
                ", homeTeamAdvantage=" + homeTeamAdvantage +
                ", startDate=" + startDate +
                ", betType='" + betType + '\'' +
                ", dayLookbackCap=" + dayLookbackCap +
                ", started=" + started +
                ", finished=" + finished +
                '}';
    }

    public boolean isFactorPostBlocks() {
        return factorPostBlocks;
    }

    public void setFactorPostBlocks(boolean factorPostBlocks) {
        this.factorPostBlocks = factorPostBlocks;
    }

    public boolean isModelOpponentSteals() {
        return modelOpponentSteals;
    }

    public void setModelOpponentSteals(boolean modeOpponentSteals) {
        this.modelOpponentSteals = modeOpponentSteals;
    }

    public double getPointReductionPerSteal() {
        return pointReductionPerSteal;
    }

    public void setPointReductionPerSteal(double pointReductionPerSteal) {
        this.pointReductionPerSteal = pointReductionPerSteal;
    }

    public double getGameTimeThreshold() {
        return gameTimeThreshold;
    }

    public void setGameTimeThreshold(double gameTimeThreshold) {
        this.gameTimeThreshold = gameTimeThreshold;
    }

    public boolean isModelOpponentTurnovers() {
        return modelOpponentTurnovers;
    }

    public void setModelOpponentTurnovers(boolean modelOpponentTurnovers) {
        this.modelOpponentTurnovers = modelOpponentTurnovers;
    }

    public double getPointsReducedPerTurnover() {
        return pointsReducedPerTurnover;
    }

    public void setPointsReducedPerTurnover(double pointsReducedPerTurnover) {
        this.pointsReducedPerTurnover = pointsReducedPerTurnover;
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

    public boolean isSquareRootTotalPoints() {
        return squareRootTotalPoints;
    }

    public void setSquareRootTotalPoints(boolean squareRootTotalPoints) {
        this.squareRootTotalPoints = squareRootTotalPoints;
    }

    public int getGamesToTest() {
        return gamesToTest;
    }

    public void setGamesToTest(int gamesToTest) {
        this.gamesToTest = gamesToTest;
    }

    public boolean isAllowBelowZero() {
        return allowBelowZero;
    }

    public void setAllowBelowZero(boolean allowBelowZero) {
        this.allowBelowZero = allowBelowZero;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public double getPointThreshold() {
        return pointThreshold;
    }

    public void setPointThreshold(double pointThreshold) {
        this.pointThreshold = pointThreshold;
    }

    public double getHomeTeamAdvantage() {
        return homeTeamAdvantage;
    }

    public void setHomeTeamAdvantage(double homeTeamAdvantage) {
        this.homeTeamAdvantage = homeTeamAdvantage;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new Error("Something impossible just happened");
        }
    }

    public double getHighTurnoverPointFactor() {
        return highTurnoverPointFactor;
    }

    public void setHighTurnoverPointFactor(double highTurnoverPointFactor) {
        this.highTurnoverPointFactor = highTurnoverPointFactor;
    }

    public double getLowerTurnoverPointFactor() {
        return lowerTurnoverPointFactor;
    }

    public void setLowerTurnoverPointFactor(double lowerTurnoverPointFactor) {
        this.lowerTurnoverPointFactor = lowerTurnoverPointFactor;
    }

    public double getPointsReducedPerRebound() {
        return pointsReducedPerRebound;
    }

    public void setPointsReducedPerRebound(double pointsReducedPerRebound) {
        this.pointsReducedPerRebound = pointsReducedPerRebound;
    }

    public double getHighReboundPointFactor() {
        return highReboundPointFactor;
    }

    public void setHighReboundPointFactor(double highReboundPointFactor) {
        this.highReboundPointFactor = highReboundPointFactor;
    }

    public double getLowerReboundPointFactor() {
        return lowerReboundPointFactor;
    }

    public void setLowerReboundPointFactor(double lowerReboundPointFactor) {
        this.lowerReboundPointFactor = lowerReboundPointFactor;
    }

    public double getHighFoulPointFactor() {
        return highFoulPointFactor;
    }

    public void setHighFoulPointFactor(double highFoulPointFactor) {
        this.highFoulPointFactor = highFoulPointFactor;
    }

    public double getLowerFoulPointFactor() {
        return lowerFoulPointFactor;
    }

    public void setLowerFoulPointFactor(double lowerFoulPointFactor) {
        this.lowerFoulPointFactor = lowerFoulPointFactor;
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

    public double getNumHigh() {
        return numHigh;
    }

    public void setNumHigh(double numHigh) {
        this.numHigh = numHigh;
    }

    public double getNumLow() {
        return numLow;
    }

    public void setNumLow(double numLow) {
        this.numLow = numLow;
    }

    public double getPointvolweight() {
        return pointvolweight;
    }

    public void setPointvolweight(double pointvolweight) {
        this.pointvolweight = pointvolweight;
    }
}
