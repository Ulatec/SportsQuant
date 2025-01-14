package BaseballQuant.Model;

public class ScoreModel implements Cloneable{

    private double homeHighPoints;
    private double homeLowPoints;
    private double homeHighStolenBases;
    private double homeLowStolenBases;
    private double awayHighPoints;
    private double awayLowPoints;
//    private double awayRGPGHigh;
//    private double awayRGPGLow;
//    private double homeRGPGHigh;
//    private double homeRGPGLow;
    private double awayModelRunsGivenUpPerGameHigh;
    private double awayModelRunsGivenUpPerGameLow;
    private double homeModelRunsGivenUpPerGameHigh;
    private double homeModelRunsGivenUpPerGameLow;
    private double awayHighStolenBases;
    private double awayLowStolenBases;
    private float homeRunsGivenUpHigh;
    private float homeRunsGivenUpLow;
    private float awayRunsGivenUpHigh;
    private float awayRunsGivenUpLow;
    private double homeFieldingHigh;
    private double homeFieldingLow;
    private double awayFieldingHigh;
    private double awayFieldingLow;
    private double homeFieldingModelHigh;
    private double homeFieldingMoelLow;
    private float homePitchingModelHigh;
    private float homePitchingModelLow;
    private float awayPitchingModelHigh;
    private float awayPitchingModelLow;
    private double awayFieldingModelHigh;
    private double awayFieldingModelLow;
    private boolean insufficientPitcherData;
    private double homeAdvantage;
    private double totalHigh;
    private double totalLow;
    private double homeRunsGivenUpPerGameRoc;
    private double awayRunsGivenUpPerGameRoc;
    private double homeRunsScoredPerGameRoc;
    private double awayRunsScoredPerGameRoc;
    private double homeFieldingRoc;
    private double awayFieldingRoc;
    private double homeStolenBasesRoc;
    private double awayStolenBasesRoc;

    private double homeTestCorrelation;
    private double homeRunCorrelation;
    private double homeGivenUpCorrelation;
    private double homeFieldingCorrelation;

    private double homeRunsScoredPerGame;
    private double awayRunsScoredPerGame;
    private double homeRunsGivenUpPerGame;
    private double awayRunsGivenUpPerGame;

    private double homeTestCorrelationRoc;
    private double homeRunCorrelationRoc;
    private double homeGivenUpCorrelationRoc;
    private double homeFieldingCorrelationRoc;

    private double awayTestCorrelation;
    private double awayRunCorrelation;
    private double awayGivenUpCorrelation;
    private double awayFieldingCorrelation;

    private double awayTestCorrelationRoc;
    private double awayRunCorrelationRoc;
    private double awayGivenUpCorrelationRoc;
    private double awayFieldingCorrelationRoc;

    private boolean correlationsComplete;
    private boolean correlationRocsComplete;
    private boolean correlationRocBoolsComplete;

    private float homeWalkRoc;
    private float awayWalkRoc;

    public boolean testAwayBoolean;
    public boolean testHomeBoolean;
    public boolean runAwayBoolean;
    public boolean runHomeBoolean;

    public double homeRunsScoredVol;
    public double awayRunsScoredVol;
    public double homeRunsGivenUpVol;
    public double awayRunsGivenUpVol;

    public double homeRunsScoredVolRoc;
    public double awayRunsScoredVolRoc;
    public double homeRunsGivenUpVolRoc;
    public double awayRunsGivenUpVolRoc;

    public double homeAveragePriorPlayers;
    public double awayAveragePriorPlayers;
    public double homeAveragePriorPlayersRoc;
    public double awayAveragePriorPlayersRoc;

    public boolean awaySufficientPitcherGamers;
    public boolean homeSufficientPitcherGamers;

    public ScoreModel(){
        homeHighPoints = 0.0;
        homeLowPoints = 0.0;
    }
    public double getHomeHighPoints() {
        return homeHighPoints;
    }

    public void setHomeHighPoints(double homeHighPoints) {
        this.homeHighPoints = homeHighPoints;
    }





    public double getHomeLowPoints() {
        return homeLowPoints;
    }

    public void setHomeLowPoints(double homeLowPoints) {
        this.homeLowPoints = homeLowPoints;
    }



    public double getAwayHighPoints() {
        return awayHighPoints;
    }

    public void setAwayHighPoints(double awayHighPoints) {
        this.awayHighPoints = awayHighPoints;
    }

    public double getAwayLowPoints() {
        return awayLowPoints;
    }

    public void setAwayLowPoints(double awayLowPoints) {
        this.awayLowPoints = awayLowPoints;
    }




    public double getTotalHigh() {
        return totalHigh;
    }

    public void setTotalHigh(double totalHigh) {
        this.totalHigh = totalHigh;
    }

    public double getTotalLow() {
        return totalLow;
    }

    public void setTotalLow(double totalLow) {
        this.totalLow = totalLow;
    }

    public float getHomePitchingModelHigh() {
        return homePitchingModelHigh;
    }

    public void setHomePitchingModelHigh(float homePitchingModelHigh) {
        this.homePitchingModelHigh = homePitchingModelHigh;
    }

    public float getHomePitchingModelLow() {
        return homePitchingModelLow;
    }

    public void setHomePitchingModelLow(float homePitchingModelLow) {
        this.homePitchingModelLow = homePitchingModelLow;
    }

    public float getAwayPitchingModelHigh() {
        return awayPitchingModelHigh;
    }

    public void setAwayPitchingModelHigh(float awayPitchingModelHigh) {
        this.awayPitchingModelHigh = awayPitchingModelHigh;
    }

    public float getAwayPitchingModelLow() {
        return awayPitchingModelLow;
    }

    public void setAwayPitchingModelLow(float awayPitchingModelLow) {
        this.awayPitchingModelLow = awayPitchingModelLow;
    }

    public float getHomeRunsGivenUpHigh() {
        return homeRunsGivenUpHigh;
    }

    public void setHomeRunsGivenUpHigh(float homeRunsGivenUpHigh) {
        this.homeRunsGivenUpHigh = homeRunsGivenUpHigh;
    }

    public float getHomeRunsGivenUpLow() {
        return homeRunsGivenUpLow;
    }

    public void setHomeRunsGivenUpLow(float homeRunsGivenUpLow) {
        this.homeRunsGivenUpLow = homeRunsGivenUpLow;
    }

    public float getAwayRunsGivenUpHigh() {
        return awayRunsGivenUpHigh;
    }

    public void setAwayRunsGivenUpHigh(float awayRunsGivenUpHigh) {
        this.awayRunsGivenUpHigh = awayRunsGivenUpHigh;
    }

    public float getAwayRunsGivenUpLow() {
        return awayRunsGivenUpLow;
    }

    public void setAwayRunsGivenUpLow(float awayRunsGivenUpLow) {
        this.awayRunsGivenUpLow = awayRunsGivenUpLow;
    }

    public double getHomeFieldingModelHigh() {
        return homeFieldingModelHigh;
    }

    public void setHomeFieldingModelHigh(double homeFieldingModelHigh) {
        this.homeFieldingModelHigh = homeFieldingModelHigh;
    }

    public double getHomeFieldingMoelLow() {
        return homeFieldingMoelLow;
    }

    public void setHomeFieldingMoelLow(double homeFieldingMoelLow) {
        this.homeFieldingMoelLow = homeFieldingMoelLow;
    }

    public double getAwayFieldingModelHigh() {
        return awayFieldingModelHigh;
    }

    public void setAwayFieldingModelHigh(double awayFieldingModelHigh) {
        this.awayFieldingModelHigh = awayFieldingModelHigh;
    }

    public double getAwayFieldingModelLow() {
        return awayFieldingModelLow;
    }

    public void setAwayFieldingModelLow(double awayFieldingModelLow) {
        this.awayFieldingModelLow = awayFieldingModelLow;
    }

    public double getHomeFieldingHigh() {
        return homeFieldingHigh;
    }

    public void setHomeFieldingHigh(double homeFieldingHigh) {
        this.homeFieldingHigh = homeFieldingHigh;
    }

    public double getHomeFieldingLow() {
        return homeFieldingLow;
    }

    public void setHomeFieldingLow(double homeFieldingLow) {
        this.homeFieldingLow = homeFieldingLow;
    }

    public double getAwayFieldingHigh() {
        return awayFieldingHigh;
    }

    public void setAwayFieldingHigh(double awayFieldingHigh) {
        this.awayFieldingHigh = awayFieldingHigh;
    }

    public double getAwayFieldingLow() {
        return awayFieldingLow;
    }

    public void setAwayFieldingLow(double awayFieldingLow) {
        this.awayFieldingLow = awayFieldingLow;
    }


    public double getHomeHighStolenBases() {
        return homeHighStolenBases;
    }

    public void setHomeHighStolenBases(double homeHighStolenBases) {
        this.homeHighStolenBases = homeHighStolenBases;
    }

    public double getHomeLowStolenBases() {
        return homeLowStolenBases;
    }

    public void setHomeLowStolenBases(double homeLowStolenBases) {
        this.homeLowStolenBases = homeLowStolenBases;
    }

    public double getAwayHighStolenBases() {
        return awayHighStolenBases;
    }

    public void setAwayHighStolenBases(double awayHighStolenBases) {
        this.awayHighStolenBases = awayHighStolenBases;
    }

    public double getAwayLowStolenBases() {
        return awayLowStolenBases;
    }

    public void setAwayLowStolenBases(double awayLowStolenBases) {
        this.awayLowStolenBases = awayLowStolenBases;
    }

    @Override
    public String toString() {
        return "ScoreModel{" +
                ", homeHighPoints=" + homeHighPoints +
                ", homeLowPoints=" + homeLowPoints +
                ", homeHighStolenBases=" + homeHighStolenBases +
                ", homeLowStolenBases=" + homeLowStolenBases +
                ", awayHighPoints=" + awayHighPoints +
                ", awayLowPoints=" + awayLowPoints +
                ", awayHighStolenBases=" + awayHighStolenBases +
                ", awayLowStolenBases=" + awayLowStolenBases +
                ", homeRunsGivenUpHigh=" + homeRunsGivenUpHigh +
                ", homeRunsGivenUpLow=" + homeRunsGivenUpLow +
                ", awayRunsGivenUpHigh=" + awayRunsGivenUpHigh +
                ", awayRunsGivenUpLow=" + awayRunsGivenUpLow +
                ", homeFieldingHigh=" + homeFieldingHigh +
                ", homeFieldingLow=" + homeFieldingLow +
                ", awayFieldingHigh=" + awayFieldingHigh +
                ", awayFieldingLow=" + awayFieldingLow +
                ", homeFieldingModelHigh=" + homeFieldingModelHigh +
                ", homeFieldingMoelLow=" + homeFieldingMoelLow +
                ", homePitchingModelHigh=" + homePitchingModelHigh +
                ", homePitchingModelLow=" + homePitchingModelLow +
                ", awayPitchingModelHigh=" + awayPitchingModelHigh +
                ", awayPitchingModelLow=" + awayPitchingModelLow +
                ", awayFieldingModelHigh=" + awayFieldingModelHigh +
                ", awayFieldingModelLow=" + awayFieldingModelLow +
                ", totalHigh=" + totalHigh +
                ", totalLow=" + totalLow +
                '}';
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

    public double getAwayModelRunsGivenUpPerGameHigh() {
        return awayModelRunsGivenUpPerGameHigh;
    }

    public void setAwayModelRunsGivenUpPerGameHigh(double awayModelRunsGivenUpPerGameHigh) {
        this.awayModelRunsGivenUpPerGameHigh = awayModelRunsGivenUpPerGameHigh;
    }

    public double getHomeModelRunsGivenUpPerGame() {
        return homeModelRunsGivenUpPerGameHigh;
    }

    public void setHomeModelRunsGivenUpPerGame(double homeModelRunsGivenUpPerGameHigh) {
        this.homeModelRunsGivenUpPerGameHigh = homeModelRunsGivenUpPerGameHigh;
    }

    public double getAwayModelRunsGivenUpPerGameLow() {
        return awayModelRunsGivenUpPerGameLow;
    }

    public void setAwayModelRunsGivenUpPerGameLow(double awayModelRunsGivenUpPerGameLow) {
        this.awayModelRunsGivenUpPerGameLow = awayModelRunsGivenUpPerGameLow;
    }

    public double getHomeModelRunsGivenUpPerGameHigh() {
        return homeModelRunsGivenUpPerGameHigh;
    }

    public void setHomeModelRunsGivenUpPerGameHigh(double homeModelRunsGivenUpPerGameHigh) {
        this.homeModelRunsGivenUpPerGameHigh = homeModelRunsGivenUpPerGameHigh;
    }

    public double getHomeModelRunsGivenUpPerGameLow() {
        return homeModelRunsGivenUpPerGameLow;
    }

    public void setHomeModelRunsGivenUpPerGameLow(double homeModelRunsGivenUpPerGameLow) {
        this.homeModelRunsGivenUpPerGameLow = homeModelRunsGivenUpPerGameLow;
    }

    public double getHomeAdvantage() {
        return homeAdvantage;
    }

    public void setHomeAdvantage(double homeAdvantage) {
        this.homeAdvantage = homeAdvantage;
    }

    public boolean isInsufficientPitcherData() {
        return insufficientPitcherData;
    }

    public void setInsufficientPitcherData(boolean insufficientPitcherData) {
        this.insufficientPitcherData = insufficientPitcherData;
    }

    public double getHomeRunsGivenUpPerGameRoc() {
        return homeRunsGivenUpPerGameRoc;
    }

    public void setHomeRunsGivenUpPerGameRoc(double homeRunsGivenUpPerGameRoc) {
        this.homeRunsGivenUpPerGameRoc = homeRunsGivenUpPerGameRoc;
    }

    public double getAwayRunsGivenUpPerGameRoc() {
        return awayRunsGivenUpPerGameRoc;
    }

    public void setAwayRunsGivenUpPerGameRoc(double awayRunsGivenUpPerGameRoc) {
        this.awayRunsGivenUpPerGameRoc = awayRunsGivenUpPerGameRoc;
    }

    public double getHomeRunsScoredPerGameRoc() {
        return homeRunsScoredPerGameRoc;
    }

    public void setHomeRunsScoredPerGameRoc(double homeRunsScoredPerGameRoc) {
        this.homeRunsScoredPerGameRoc = homeRunsScoredPerGameRoc;
    }

    public double getAwayRunsScoredPerGameRoc() {
        return awayRunsScoredPerGameRoc;
    }

    public void setAwayRunsScoredPerGameRoc(double awayRunsScoredPerGameRoc) {
        this.awayRunsScoredPerGameRoc = awayRunsScoredPerGameRoc;
    }

    public double getHomeFieldingRoc() {
        return homeFieldingRoc;
    }

    public void setHomeFieldingRoc(double homeFieldingRoc) {
        this.homeFieldingRoc = homeFieldingRoc;
    }

    public double getAwayFieldingRoc() {
        return awayFieldingRoc;
    }

    public void setAwayFieldingRoc(double awayFieldingRoc) {
        this.awayFieldingRoc = awayFieldingRoc;
    }

    public double getHomeStolenBasesRoc() {
        return homeStolenBasesRoc;
    }

    public void setHomeStolenBasesRoc(double homeStolenBasesRoc) {
        this.homeStolenBasesRoc = homeStolenBasesRoc;
    }

    public double getAwayStolenBasesRoc() {
        return awayStolenBasesRoc;
    }

    public void setAwayStolenBasesRoc(double awayStolenBasesRoc) {
        this.awayStolenBasesRoc = awayStolenBasesRoc;
    }

    public double getHomeWalkRoc() {
        return homeWalkRoc;
    }

    public void setHomeWalkRoc(float homeWalkRoc) {
        this.homeWalkRoc = homeWalkRoc;
    }

    public double getAwayWalkRoc() {
        return awayWalkRoc;
    }

    public void setAwayWalkRoc(float awayWalkRoc) {
        this.awayWalkRoc = awayWalkRoc;
    }

    public double getHomeTestCorrelation() {
        return homeTestCorrelation;
    }

    public void setHomeTestCorrelation(double homeTestCorrelation) {
        this.homeTestCorrelation = homeTestCorrelation;
    }

    public double getHomeRunCorrelation() {
        return homeRunCorrelation;
    }

    public void setHomeRunCorrelation(double homeRunCorrelation) {
        this.homeRunCorrelation = homeRunCorrelation;
    }

    public double getHomeGivenUpCorrelation() {
        return homeGivenUpCorrelation;
    }

    public void setHomeGivenUpCorrelation(double homeGivenUpCorrelation) {
        this.homeGivenUpCorrelation = homeGivenUpCorrelation;
    }

    public double getHomeTestCorrelationRoc() {
        return homeTestCorrelationRoc;
    }

    public void setHomeTestCorrelationRoc(double homeTestCorrelationRoc) {
        this.homeTestCorrelationRoc = homeTestCorrelationRoc;
    }

    public double getHomeRunCorrelationRoc() {
        return homeRunCorrelationRoc;
    }

    public void setHomeRunCorrelationRoc(double homeRunCorrelationRoc) {
        this.homeRunCorrelationRoc = homeRunCorrelationRoc;
    }

    public double getHomeGivenUpCorrelationRoc() {
        return homeGivenUpCorrelationRoc;
    }

    public void setHomeGivenUpCorrelationRoc(double homeGivenUpCorrelationRoc) {
        this.homeGivenUpCorrelationRoc = homeGivenUpCorrelationRoc;
    }

    public double getAwayTestCorrelation() {
        return awayTestCorrelation;
    }

    public void setAwayTestCorrelation(double awayTestCorrelation) {
        this.awayTestCorrelation = awayTestCorrelation;
    }

    public double getAwayRunCorrelation() {
        return awayRunCorrelation;
    }

    public void setAwayRunCorrelation(double awayRunCorrelation) {
        this.awayRunCorrelation = awayRunCorrelation;
    }

    public double getAwayGivenUpCorrelation() {
        return awayGivenUpCorrelation;
    }

    public void setAwayGivenUpCorrelation(double awayGivenUpCorrelation) {
        this.awayGivenUpCorrelation = awayGivenUpCorrelation;
    }

    public double getAwayTestCorrelationRoc() {
        return awayTestCorrelationRoc;
    }

    public void setAwayTestCorrelationRoc(double awayTestCorrelationRoc) {
        this.awayTestCorrelationRoc = awayTestCorrelationRoc;
    }

    public double getAwayRunCorrelationRoc() {
        return awayRunCorrelationRoc;
    }

    public void setAwayRunCorrelationRoc(double awayRunCorrelationRoc) {
        this.awayRunCorrelationRoc = awayRunCorrelationRoc;
    }

    public double getAwayGivenUpCorrelationRoc() {
        return awayGivenUpCorrelationRoc;
    }

    public void setAwayGivenUpCorrelationRoc(double awayGivenUpCorrelationRoc) {
        this.awayGivenUpCorrelationRoc = awayGivenUpCorrelationRoc;
    }

    public double getHomeFieldingCorrelation() {
        return homeFieldingCorrelation;
    }

    public void setHomeFieldingCorrelation(double homeFieldingCorrelation) {
        this.homeFieldingCorrelation = homeFieldingCorrelation;
    }

    public double getHomeFieldingCorrelationRoc() {
        return homeFieldingCorrelationRoc;
    }

    public void setHomeFieldingCorrelationRoc(double homeFieldingCorrelationRoc) {
        this.homeFieldingCorrelationRoc = homeFieldingCorrelationRoc;
    }

    public double getAwayFieldingCorrelation() {
        return awayFieldingCorrelation;
    }

    public void setAwayFieldingCorrelation(double awayFieldingCorrelation) {
        this.awayFieldingCorrelation = awayFieldingCorrelation;
    }

    public double getAwayFieldingCorrelationRoc() {
        return awayFieldingCorrelationRoc;
    }

    public void setAwayFieldingCorrelationRoc(double awayFieldingCorrelationRoc) {
        this.awayFieldingCorrelationRoc = awayFieldingCorrelationRoc;
    }

    public double getHomeRunsScoredPerGame() {
        return homeRunsScoredPerGame;
    }

    public void setHomeRunsScoredPerGame(double homeRunsScoredPerGame) {
        this.homeRunsScoredPerGame = homeRunsScoredPerGame;
    }

    public double getAwayRunsScoredPerGame() {
        return awayRunsScoredPerGame;
    }

    public void setAwayRunsScoredPerGame(double awayRunsScoredPerGame) {
        this.awayRunsScoredPerGame = awayRunsScoredPerGame;
    }

    public double getHomeRunsGivenUpPerGame() {
        return homeRunsGivenUpPerGame;
    }

    public void setHomeRunsGivenUpPerGame(double homeRunsGivenUpPerGame) {
        this.homeRunsGivenUpPerGame = homeRunsGivenUpPerGame;
    }

    public double getAwayRunsGivenUpPerGame() {
        return awayRunsGivenUpPerGame;
    }

    public void setAwayRunsGivenUpPerGame(double awayRunsGivenUpPerGame) {
        this.awayRunsGivenUpPerGame = awayRunsGivenUpPerGame;
    }

    public boolean isCorrelationsComplete() {
        return correlationsComplete;
    }

    public void setCorrelationsComplete(boolean correlationsComplete) {
        this.correlationsComplete = correlationsComplete;
    }

    public boolean isCorrelationRocsComplete() {
        return correlationRocsComplete;
    }

    public void setCorrelationRocsComplete(boolean correlationRocsComplete) {
        this.correlationRocsComplete = correlationRocsComplete;
    }

    public boolean isTestAwayBoolean() {
        return testAwayBoolean;
    }

    public void setTestAwayBoolean(boolean testAwayBoolean) {
        this.testAwayBoolean = testAwayBoolean;
    }

    public boolean isTestHomeBoolean() {
        return testHomeBoolean;
    }

    public void setTestHomeBoolean(boolean testHomeBoolean) {
        this.testHomeBoolean = testHomeBoolean;
    }

    public boolean isCorrelationRocBoolsComplete() {
        return correlationRocBoolsComplete;
    }

    public void setCorrelationRocBoolsComplete(boolean correlationRocBoolsComplete) {
        this.correlationRocBoolsComplete = correlationRocBoolsComplete;
    }

    public boolean isRunAwayBoolean() {
        return runAwayBoolean;
    }

    public void setRunAwayBoolean(boolean runAwayBoolean) {
        this.runAwayBoolean = runAwayBoolean;
    }

    public boolean isRunHomeBoolean() {
        return runHomeBoolean;
    }

    public void setRunHomeBoolean(boolean runHomeBoolean) {
        this.runHomeBoolean = runHomeBoolean;
    }

    public double getHomeRunsScoredVol() {
        return homeRunsScoredVol;
    }

    public void setHomeRunsScoredVol(double homeRunsScoredVol) {
        this.homeRunsScoredVol = homeRunsScoredVol;
    }

    public double getAwayRunsScoredVol() {
        return awayRunsScoredVol;
    }

    public void setAwayRunsScoredVol(double awayRunsScoredVol) {
        this.awayRunsScoredVol = awayRunsScoredVol;
    }

    public double getHomeRunsScoredVolRoc() {
        return homeRunsScoredVolRoc;
    }

    public void setHomeRunsScoredVolRoc(double homeRunsScoredVolRoc) {
        this.homeRunsScoredVolRoc = homeRunsScoredVolRoc;
    }

    public double getAwayRunsScoredVolRoc() {
        return awayRunsScoredVolRoc;
    }

    public void setAwayRunsScoredVolRoc(double awayRunsScoredVolRoc) {
        this.awayRunsScoredVolRoc = awayRunsScoredVolRoc;
    }

    public double getHomeRunsGivenUpVol() {
        return homeRunsGivenUpVol;
    }

    public void setHomeRunsGivenUpVol(double homeRunsGivenUpVol) {
        this.homeRunsGivenUpVol = homeRunsGivenUpVol;
    }

    public double getAwayRunsGivenUpVol() {
        return awayRunsGivenUpVol;
    }

    public void setAwayRunsGivenUpVol(double awayRunsGivenUpVol) {
        this.awayRunsGivenUpVol = awayRunsGivenUpVol;
    }

    public double getHomeRunsGivenUpVolRoc() {
        return homeRunsGivenUpVolRoc;
    }

    public void setHomeRunsGivenUpVolRoc(double homeRunsGivenUpVolRoc) {
        this.homeRunsGivenUpVolRoc = homeRunsGivenUpVolRoc;
    }

    public double getAwayRunsGivenUpVolRoc() {
        return awayRunsGivenUpVolRoc;
    }

    public void setAwayRunsGivenUpVolRoc(double awayRunsGivenUpVolRoc) {
        this.awayRunsGivenUpVolRoc = awayRunsGivenUpVolRoc;
    }


    public double getHomeAveragePriorPlayers() {
        return homeAveragePriorPlayers;
    }

    public void setHomeAveragePriorPlayers(double homeAveragePriorPlayers) {
        this.homeAveragePriorPlayers = homeAveragePriorPlayers;
    }

    public double getAwayAveragePriorPlayers() {
        return awayAveragePriorPlayers;
    }

    public void setAwayAveragePriorPlayers(double awayAveragePriorPlayers) {
        this.awayAveragePriorPlayers = awayAveragePriorPlayers;
    }

    public double getHomeAveragePriorPlayersRoc() {
        return homeAveragePriorPlayersRoc;
    }

    public void setHomeAveragePriorPlayersRoc(double homeAveragePriorPlayersRoc) {
        this.homeAveragePriorPlayersRoc = homeAveragePriorPlayersRoc;
    }

    public double getAwayAveragePriorPlayersRoc() {
        return awayAveragePriorPlayersRoc;
    }

    public void setAwayAveragePriorPlayersRoc(double awayAveragePriorPlayersRoc) {
        this.awayAveragePriorPlayersRoc = awayAveragePriorPlayersRoc;
    }

    public boolean isAwaySufficientPitcherGamers() {
        return awaySufficientPitcherGamers;
    }

    public void setAwaySufficientPitcherGamers(boolean awaySufficientPitcherGamers) {
        this.awaySufficientPitcherGamers = awaySufficientPitcherGamers;
    }

    public boolean isHomeSufficientPitcherGamers() {
        return homeSufficientPitcherGamers;
    }

    public void setHomeSufficientPitcherGamers(boolean homeSufficientPitcherGamers) {
        this.homeSufficientPitcherGamers = homeSufficientPitcherGamers;
    }
}
