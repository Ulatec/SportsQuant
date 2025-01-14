package BaseballQuant.Model;

import java.time.LocalDate;

public class BackTestResult {

    //settings
    private int playerGameLookBack;
    private double runsGivenUpDifferential;
    private int bullpenGameLookback;
    private boolean modelStolenBases;
    private boolean doubleSquareRoot;
    private boolean squareRootTotalPoints;
    private boolean allowLowEndBelowZero;
    private String betType;
    private double betSize;

    private LocalDate startDate;
    private double gamesToTest;
    private double dailyVol;
  //  private boolean modelOpponentPitching;
    private boolean modelOpponentFielding;
    private boolean modelOpponentSteals;
    private double endingMoney;
    private int pitcherGameLookback;
    private int exactResults;
    private int exactLosses;
    private double pointThreshold;
    //results
    private int predictOver;
    private int predictUnder;
    private int predictCorrect;
    private int predictIncorrect;
    private int actualOver;
    private int actualUnder;
    private double estimatedPoints;
    private double actualPoints;
    private double correctPercent;
    private boolean homeRunsGivenUpRocFlip;
    private boolean AwayRunsGivenUpRocFlip;
    private boolean homeRunsScoredRocFlip;
    private boolean AwayRunsScoredocFlip;
    private boolean homeFieldingRocFlip;
    private boolean awayFieldingRocFlip;
    private boolean homeStolenBasesRocFlip;
    private boolean awayStolenBasesRocFlip;
    private boolean enable1;
    private boolean enable2;
    private boolean enable3;
    private boolean enable4;
    private boolean enable5;
    private boolean enable6;
    private boolean enable7;
    private boolean enable8;
    private boolean enable9;
    private boolean enable10;
    private boolean enable11;
    private boolean enable12;
    private boolean enable13;
    private boolean enable14;
    private boolean enable15;

    private boolean flip1;
    private boolean flip2;
    private boolean flip3;
    private boolean flip4;
    private boolean flip5;
    private boolean flip6;

    private double d1;
    private double d2;
    private double d3;
    private double d4;
    private double d5;
    private double d6;
    private double d7;
    private double d8;
    private double d9;
    private double d10;
    private double d11;
    private double d12;
    private double deez;
    private double rSquared;
    //private double pitchersGuessedPct;


    public int getPlayerGameLookBack() {
        return playerGameLookBack;
    }

    public void setPlayerGameLookBack(int playerGameLookBack) {
        this.playerGameLookBack = playerGameLookBack;
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



//    public boolean isModelOpponentPitching() {
//        return modelOpponentPitching;
//    }
//
//    public void setModelOpponentPitching(boolean modelOpponentPitching) {
//        this.modelOpponentPitching = modelOpponentPitching;
//    }

    public int getPitcherGameLookback() {
        return pitcherGameLookback;
    }

    public void setPitcherGameLookback(int pitcherGameLookback) {
        this.pitcherGameLookback = pitcherGameLookback;
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


    public boolean isModelOpponentSteals() {
        return modelOpponentSteals;
    }

    public void setModelOpponentSteals(boolean modelOpponentSteals) {
        this.modelOpponentSteals = modelOpponentSteals;
    }


    @Override
    public String toString() {
        return "BackTestResult{" +
                "playerGameLookBack=" + playerGameLookBack +
                ", runsGivenUpDifferential=" + runsGivenUpDifferential +
                ", bullpenGameLookback=" + bullpenGameLookback +
                ", modelStolenBases=" + modelStolenBases +
                ", doubleSquareRoot=" + doubleSquareRoot +
                ", squareRootTotalPoints=" + squareRootTotalPoints +
                ", allowLowEndBelowZero=" + allowLowEndBelowZero +
                ", betType='" + betType + '\'' +
                ", startDate=" + startDate +
                ", gamesToTest=" + gamesToTest +
                ", dailyVol=" + dailyVol +
       //         ", modelOpponentPitching=" + modelOpponentPitching +
                ", modelOpponentFielding=" + modelOpponentFielding +
                ", modelOpponentSteals=" + modelOpponentSteals +
                ", endingMoney=" + endingMoney +
                ", pitcherGameLookback=" + pitcherGameLookback +
                ", exactResults=" + exactResults +
                ", exactLosses=" + exactLosses +
                ", pointThreshold=" + pointThreshold +
                ", predictOver=" + predictOver +
                ", predictUnder=" + predictUnder +
                ", predictCorrect=" + predictCorrect +
                ", predictIncorrect=" + predictIncorrect +
                ", actualOver=" + actualOver +
                ", actualUnder=" + actualUnder +
                ", estimatedPoints=" + estimatedPoints +
                ", actualPoints=" + actualPoints +
                ", correctPercent=" + correctPercent +
                '}';
    }

    public double getRunsGivenUpDifferential() {
        return runsGivenUpDifferential;
    }

    public void setRunsGivenUpDifferential(double runsGivenUpDifferential) {
        this.runsGivenUpDifferential = runsGivenUpDifferential;
    }


    public boolean isSquareRootTotalPoints() {
        return squareRootTotalPoints;
    }

    public void setSquareRootTotalPoints(boolean squareRootTotalPoints) {
        this.squareRootTotalPoints = squareRootTotalPoints;
    }

    public int getBullpenGameLookback() {
        return bullpenGameLookback;
    }

    public void setBullpenGameLookback(int bullpenGameLookback) {
        this.bullpenGameLookback = bullpenGameLookback;
    }


    public double getEndingMoney() {
        return endingMoney;
    }

    public void setEndingMoney(double endingMoney) {
        this.endingMoney = endingMoney;
    }



    public boolean isAllowLowEndBelowZero() {
        return allowLowEndBelowZero;
    }

    public void setAllowLowEndBelowZero(boolean allowLowEndBelowZero) {
        this.allowLowEndBelowZero = allowLowEndBelowZero;
    }


    public double getDailyVol() {
        return dailyVol;
    }

    public void setDailyVol(double dailyVol) {
        this.dailyVol = dailyVol;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public double getGamesToTest() {
        return gamesToTest;
    }

    public void setGamesToTest(double gamesToTest) {
        this.gamesToTest = gamesToTest;
    }

    public String getBetType() {
        return betType;
    }

    public void setBetType(String betType) {
        this.betType = betType;
    }

    public boolean isModelStolenBases() {
        return modelStolenBases;
    }

    public void setModelStolenBases(boolean modelStolenBases) {
        this.modelStolenBases = modelStolenBases;
    }

    public boolean isModelOpponentFielding() {
        return modelOpponentFielding;
    }

    public void setModelOpponentFielding(boolean modelOpponentFielding) {
        this.modelOpponentFielding = modelOpponentFielding;
    }

    public double getPointThreshold() {
        return pointThreshold;
    }

    public void setPointThreshold(double pointThreshold) {
        this.pointThreshold = pointThreshold;
    }


    public boolean isHomeRunsGivenUpRocFlip() {
        return homeRunsGivenUpRocFlip;
    }

    public void setHomeRunsGivenUpRocFlip(boolean homeRunsGivenUpRocFlip) {
        this.homeRunsGivenUpRocFlip = homeRunsGivenUpRocFlip;
    }

    public boolean isAwayRunsGivenUpRocFlip() {
        return AwayRunsGivenUpRocFlip;
    }

    public void setAwayRunsGivenUpRocFlip(boolean awayRunsGivenUpRocFlip) {
        AwayRunsGivenUpRocFlip = awayRunsGivenUpRocFlip;
    }

    public boolean isHomeRunsScoredRocFlip() {
        return homeRunsScoredRocFlip;
    }

    public void setHomeRunsScoredRocFlip(boolean homeRunsScoredRocFlip) {
        this.homeRunsScoredRocFlip = homeRunsScoredRocFlip;
    }

    public boolean isAwayRunsScoredocFlip() {
        return AwayRunsScoredocFlip;
    }

    public void setAwayRunsScoredocFlip(boolean awayRunsScoredocFlip) {
        AwayRunsScoredocFlip = awayRunsScoredocFlip;
    }

    public boolean isHomeFieldingRocFlip() {
        return homeFieldingRocFlip;
    }

    public void setHomeFieldingRocFlip(boolean homeFieldingRocFlip) {
        this.homeFieldingRocFlip = homeFieldingRocFlip;
    }

    public boolean isAwayFieldingRocFlip() {
        return awayFieldingRocFlip;
    }

    public void setAwayFieldingRocFlip(boolean awayFieldingRocFlip) {
        this.awayFieldingRocFlip = awayFieldingRocFlip;
    }

    public boolean isHomeStolenBasesRocFlip() {
        return homeStolenBasesRocFlip;
    }

    public void setHomeStolenBasesRocFlip(boolean homeStolenBasesRocFlip) {
        this.homeStolenBasesRocFlip = homeStolenBasesRocFlip;
    }

    public boolean isAwayStolenBasesRocFlip() {
        return awayStolenBasesRocFlip;
    }

    public void setAwayStolenBasesRocFlip(boolean awayStolenBasesRocFlip) {
        this.awayStolenBasesRocFlip = awayStolenBasesRocFlip;
    }

    public boolean isEnable1() {
        return enable1;
    }

    public void setEnable1(boolean enable1) {
        this.enable1 = enable1;
    }

    public boolean isEnable2() {
        return enable2;
    }

    public void setEnable2(boolean enable2) {
        this.enable2 = enable2;
    }

    public boolean isEnable3() {
        return enable3;
    }

    public void setEnable3(boolean enable3) {
        this.enable3 = enable3;
    }

    public boolean isEnable4() {
        return enable4;
    }

    public void setEnable4(boolean enable4) {
        this.enable4 = enable4;
    }

    public boolean isEnable5() {
        return enable5;
    }

    public void setEnable5(boolean enable5) {
        this.enable5 = enable5;
    }

    public boolean isEnable6() {
        return enable6;
    }

    public void setEnable6(boolean enable6) {
        this.enable6 = enable6;
    }

    public boolean isEnable7() {
        return enable7;
    }

    public void setEnable7(boolean enable7) {
        this.enable7 = enable7;
    }

    public boolean isEnable8() {
        return enable8;
    }

    public void setEnable8(boolean enable8) {
        this.enable8 = enable8;
    }

    public boolean isEnable9() {
        return enable9;
    }

    public void setEnable9(boolean enable9) {
        this.enable9 = enable9;
    }

    public boolean isEnable10() {
        return enable10;
    }

    public void setEnable10(boolean enable10) {
        this.enable10 = enable10;
    }

    public boolean isEnable11() {
        return enable11;
    }

    public void setEnable11(boolean enable11) {
        this.enable11 = enable11;
    }

    public boolean isEnable12() {
        return enable12;
    }

    public void setEnable12(boolean enable12) {
        this.enable12 = enable12;
    }

    public boolean isFlip1() {
        return flip1;
    }

    public void setFlip1(boolean flip1) {
        this.flip1 = flip1;
    }

    public boolean isFlip2() {
        return flip2;
    }

    public void setFlip2(boolean flip2) {
        this.flip2 = flip2;
    }

    public boolean isFlip3() {
        return flip3;
    }

    public void setFlip3(boolean flip3) {
        this.flip3 = flip3;
    }

    public boolean isFlip4() {
        return flip4;
    }

    public void setFlip4(boolean flip4) {
        this.flip4 = flip4;
    }

    public boolean isEnable13() {
        return enable13;
    }

    public void setEnable13(boolean enable13) {
        this.enable13 = enable13;
    }

    public boolean isEnable14() {
        return enable14;
    }

    public void setEnable14(boolean enable14) {
        this.enable14 = enable14;
    }

    public boolean isEnable15() {
        return enable15;
    }

    public void setEnable15(boolean enable15) {
        this.enable15 = enable15;
    }

    public boolean isFlip5() {
        return flip5;
    }

    public void setFlip5(boolean flip5) {
        this.flip5 = flip5;
    }

    public boolean isFlip6() {
        return flip6;
    }

    public void setFlip6(boolean flip6) {
        this.flip6 = flip6;
    }

    public double getrSquared() {
        return rSquared;
    }

    public void setrSquared(double rSquared) {
        this.rSquared = rSquared;
    }

    public double getD1() {
        return d1;
    }

    public void setD1(double d1) {
        this.d1 = d1;
    }

    public double getD2() {
        return d2;
    }

    public void setD2(double d2) {
        this.d2 = d2;
    }

    public double getD3() {
        return d3;
    }

    public void setD3(double d3) {
        this.d3 = d3;
    }

    public double getD4() {
        return d4;
    }

    public void setD4(double d4) {
        this.d4 = d4;
    }

    public double getD5() {
        return d5;
    }

    public void setD5(double d5) {
        this.d5 = d5;
    }

    public double getD6() {
        return d6;
    }

    public void setD6(double d6) {
        this.d6 = d6;
    }

    public double getD7() {
        return d7;
    }

    public void setD7(double d7) {
        this.d7 = d7;
    }

    public double getD8() {
        return d8;
    }

    public void setD8(double d8) {
        this.d8 = d8;
    }

    public double getD9() {
        return d9;
    }

    public void setD9(double d9) {
        this.d9 = d9;
    }

    public double getD10() {
        return d10;
    }

    public void setD10(double d10) {
        this.d10 = d10;
    }

    public double getD11() {
        return d11;
    }

    public void setD11(double d11) {
        this.d11 = d11;
    }
    public double getD12() {
        return d12;
    }

    public void setD12(double d12) {
        this.d12 = d12;
    }

    public double getDeez() {
        return deez;
    }

    public void setDeez(double deez) {
        this.deez = deez;
    }
    public double getBetSize() {
        return betSize;
    }

    public void setBetSize(double betSize) {
        this.betSize = betSize;
    }
}

