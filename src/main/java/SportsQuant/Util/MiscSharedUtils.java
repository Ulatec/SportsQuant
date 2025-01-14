package SportsQuant.Util;

import SportsQuant.Model.BackTestIngestObject;
import SportsQuant.Model.BackTestResult;

public class MiscSharedUtils {

    public static BackTestResult buildBackTestResult(BackTestIngestObject backTestIngestObject,int correctPredictions,
                                              int incorrectPredictions,double startingMoney, double totalActualPoints, double totalPredictedPoints,
                                              int exactMatch){
        BackTestResult backTestResult = new BackTestResult();
        backTestResult.setPlayerGameLookBack(backTestIngestObject.getPlayerGameLookBack());
        backTestResult.setDoubleSquareRoot(backTestIngestObject.isDoubleSquareRoot());
        backTestResult.setLowerBlockPointFactor(backTestIngestObject.getLowerBlockPointFactor());
        backTestResult.setPointsReducedPerBlock(backTestIngestObject.getPointsReducedPerBlock());
        backTestResult.setPointsReducedPerRebound(backTestIngestObject.getPointsReducedPerRebound());
        backTestResult.setPointsReducedPerFoul(backTestIngestObject.getPointsReducedPerFoul());
        backTestResult.setCorrectPercent(((double) correctPredictions / (double) (correctPredictions + incorrectPredictions)) * 100);
        backTestResult.setPredictCorrect(correctPredictions);
        backTestResult.setPredictIncorrect(incorrectPredictions);
        backTestResult.setActualPoints(totalActualPoints);
        backTestResult.setExactResults(exactMatch);
        backTestResult.setEstimatedPoints(totalPredictedPoints);
        backTestResult.setBlockExponent(backTestIngestObject.getBlockExponent());
        backTestResult.setModelOpponentBlocks(backTestIngestObject.isModelOpponentBlocks());
        backTestResult.setFactorPostBlock(backTestIngestObject.isFactorPostBlocks());
        backTestResult.setPointsReducedPerSteal(backTestIngestObject.getPointReductionPerSteal());
        backTestResult.setModelOpponentSteals(backTestIngestObject.isModelOpponentSteals());
        backTestResult.setGameTimeThreshold(backTestIngestObject.getGameTimeThreshold());
        backTestResult.setPointsReducedPerTurnover(backTestIngestObject.getPointsReducedPerTurnover());
        backTestResult.setDayLookbackCap(backTestIngestObject.getDayLookbackCap());
        backTestResult.setBetType(backTestIngestObject.getBetType());
        backTestResult.setHighBlockPointFactor(backTestIngestObject.getHighBlockPointFactor());
        backTestResult.setHighStealPointFactor(backTestIngestObject.getHighStealPointFactor());
        backTestResult.setLowerStealPointFactor(backTestIngestObject.getLowerStealPointFactor());
        backTestResult.setHighReboundPointFactor(backTestIngestObject.getHighReboundPointFactor());
        backTestResult.setLowerReboundPointFactor(backTestIngestObject.getLowerReboundPointFactor());
        backTestResult.setHighTurnoverPointFactor(backTestIngestObject.getHighTurnoverPointFactor());
        backTestResult.setLowerTurnoverPointFactor(backTestIngestObject.getLowerTurnoverPointFactor());
        backTestResult.setHighFoulPointFactor(backTestIngestObject.getHighFoulPointFactor());
        backTestResult.setLowerFoulPointFactor(backTestIngestObject.getLowerFoulPointFactor());
        backTestResult.setPointThreshold(backTestIngestObject.getPointThreshold());
        backTestResult.setAllowBelowZero(backTestIngestObject.isAllowBelowZero());
        backTestResult.setSquareRootTotal(backTestIngestObject.isSquareRootTotalPoints());
        backTestResult.setStartDate(backTestIngestObject.getStartDate());
        backTestResult.setGamesToTest(backTestIngestObject.getGamesToTest());
        backTestResult.setHomeTeamAdvantage(backTestIngestObject.getHomeTeamAdvantage());
        backTestResult.setEndingMoney(startingMoney);
        backTestResult.setFractalWindow(backTestIngestObject.getFractalWindow());
        backTestResult.setPointvolweight(backTestIngestObject.getPointvolweight());
        return backTestResult;
    }





}
