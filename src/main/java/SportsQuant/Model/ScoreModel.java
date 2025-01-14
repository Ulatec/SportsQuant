package SportsQuant.Model;

public class ScoreModel implements Cloneable {
    private double homePointsPerGameRoc;
    private double homeBlocksPerGameRoc;
    private double homeReboundsPerGameRoc;
    private double homeStealsPerGameRoc;
    private double homeTurnoversPerGameRoc;
    private double homeFoulsPerGameRoc;
    private double awayFoulsPerGameRoc;
    private double homeFreeThrowAttemptsPerGameRoc;
    private double awayFreeThrowAttemptsPerGameRoc;
    private double homePointsScoredVol;
    private double awayPointsScoredVol;
    private double homePointsScoredVolRoc;
    private double awayPointsScoredVolRoc;
    private double homeOverUnderRoc;
    private double awayOverUnderRoc;
    private double homeWinProbRoc;
    private double awayWinProbRoc;


    private double homeHighThreePointersForecasted;
    private double homeLowThreePointersForecasted;
    private double homeHighFreeThrowsForecasted;
    private double homeLowFreeThrowsForecasted;
    private double homeHighFieldGoalsForecasted;
    private double homeLowFieldGoalsForecasted;
    private double homeHighFieldGoalPercentage;
    private double homeLowFieldGoalPercentage;
    private double homeHighThreePointPercentage;
    private double homeLowThreePointPercentage;
    private double homeHighFreeThrowPercentage;
    private double homeLowFreeThrowPercentage;
    private double homeHighFieldGoalAttempts;
    private double homeLowFieldGoalAttempts;
    private double homeHighThreePointAttempts;
    private double homeLowThreePointAttempts;
    private double homeHighFreeThrowAttempts;
    private double homeLowFreeThrowAttempts;
    private double homeHighDefensiveRebounds;
    private double homeLowDefensiveRebounds;
    private double homeHighOffensiveRebounds;
    private double homeLowOffensiveRebounds;
    private double homePointsPerGame;
    private double homeBlockPerGame;
    private double homeStealPerGame;
    private double homeTurnoverPerGame;
    private double homeReboundPerGame;
    private double homeFoulPerGame;
    private double homeHighPoints;
    private double homeLowPoints;
    private double homeHighSteals;
    private double homeLowSteals;
    private double homeHighBlocks;
    private double homeLowBlocks;
    private double homeHighTurnovers;
    private double homeLowTurnovers;
    private double homeHighRebounds;
    private double homeLowRebounds;
    private double homeHighFouls;
    private double homeLowFouls;

    private double awayPointsPerGameRoc;
    private double awayBlocksPerGameRoc;
    private double awayReboundsPerGameRoc;
    private double awayStealsPerGameRoc;
    private double awayTurnoversPerGameRoc;
    private double awayHighThreePointersForecasted;
    private double awayLowThreePointersForecasted;
    private double awayHighFreeThrowsForecasted;
    private double awayLowFreeThrowsForecasted;
    private double awayHighFieldGoalsForecasted;
    private double awayLowFieldGoalsForecasted;
    private double awayHighFieldGoalPercentage;
    private double awayLowFieldGoalPercentage;
    private double awayHighThreePointPercentage;
    private double awayLowThreePointPercentage;
    private double awayHighFreeThrowPercentage;
    private double awayLowFreeThrowPercentage;
    private double awayHighFieldGoalAttempts;
    private double awayLowFieldGoalAttempts;
    private double awayHighThreePointAttempts;
    private double awayLowThreePointAttempts;
    private double awayHighFreeThrowAttempts;
    private double awayLowFreeThrowAttempts;
    private double awayHighDefensiveRebounds;
    private double awayLowDefensiveRebounds;
    private double awayHighOffensiveRebounds;
    private double awayLowOffensiveRebounds;
    private double awayPointsPerGame;
    private double awayBlockPerGame;
    private double awayStealPerGame;
    private double awayTurnoverPerGame;
    private double awayReboundPerGame;
    private double awayFoulPerGame;
    private double awayHighPoints;
    private double awayLowPoints;
    private double awayHighBlocks;
    private double awayLowBlocks;
    private double awayHighSteals;
    private double awayLowSteals;
    private double awayHighTurnovers;
    private double awayLowTurnovers;
    private double awayHighRebounds;
    private double awayLowRebounds;
    private double awayHighFouls;
    private double awayLowFouls;
    private double awayFreeThrowsPerGame;

    private double awayBlockScoringModelHigh;
    private double awayBlockScoringModelLow;
    private double awayStaticBlockScoringModel;
    private double awayStealScoringModelHigh;
    private double awayStealScoringModelLow;
    private double awayStaticStealScoringModel;
    private double homeBlockScoringModelHigh;
    private double homeBlockScoringModelLow;
    private double homeStaticBlockScoringModel;
    private double homeStealScoringModelHigh;
    private double homeStealScoringModelLow;
    private double homeStaticStealScoringModel;
    private double homeTurnoverScoringModelHigh;
    private double homeTurnoverScoringModelLow;
    private double homeStaticTurnoverScoringModel;
    private double awayTurnoverScoringModelHigh;
    private double awayTurnoverScoringModelLow;
    private double awayStaticTurnoverScoringModel;
    private double homeOffensiveReboundScoringModelHigh;
    private double homeOffensiveReboundScoringModelLow;
    private double homeDefensiveReboundScoringModelHigh;
    private double homeDefensiveReboundScoringModelLow;
    private double awayOffensiveReboundScoringModelHigh;
    private double awayOffensiveReboundScoringModelLow;
    private double awayDefensiveReboundScoringModelHigh;
    private double awayDefensiveReboundScoringModelLow;
    private double homeFoulScoringModelHigh;
    private double homeFoulScoringModelLow;
    private double homeStaticFoulScoringModel;
    private double awayFoulScoringModelHigh;
    private double awayFoulScoringModelLow;
    private double awayStaticFoulScoringModel;
    private double homeScoringBPG;
    private double awayScoringBPG;
    private double homeFreeThrowsPerGame;

    private double totalHigh;
    private double totalLow;

    public ScoreModel(){
        homeHighPoints = 0.0;
        homeLowPoints = 0.0;
        homeHighBlocks = 0.0;
        homeLowBlocks = 0.0;
    }
    public double getHomeHighPoints() {
        return homeHighPoints;
    }

    public void setHomeHighPoints(double homeHighPoints) {
        this.homeHighPoints = homeHighPoints;
    }


    public double getHomeHighBlocks() {
        return homeHighBlocks;
    }

    public void setHomeHighBlocks(double homeHighBlocks) {
        this.homeHighBlocks = homeHighBlocks;
    }

    public double getLowBlocks() {
        return homeLowBlocks;
    }

    public void setLowBlocks(double lowBlocks) {
        this.homeLowBlocks = lowBlocks;
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


    public double getHomeLowBlocks() {
        return homeLowBlocks;
    }

    public void setHomeLowBlocks(double homeLowBlocks) {
        this.homeLowBlocks = homeLowBlocks;
    }

    public double getAwayHighBlocks() {
        return awayHighBlocks;
    }

    public void setAwayHighBlocks(double awayHighBlocks) {
        this.awayHighBlocks = awayHighBlocks;
    }

    public double getAwayLowBlocks() {
        return awayLowBlocks;
    }

    public void setAwayLowBlocks(double awayLowBlocks) {
        this.awayLowBlocks = awayLowBlocks;
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

    public double getHomeHighSteals() {
        return homeHighSteals;
    }

    public void setHomeHighSteals(double homeHighSteals) {
        this.homeHighSteals = homeHighSteals;
    }

    public double getHomeLowSteals() {
        return homeLowSteals;
    }

    public void setHomeLowSteals(double homeLowSteals) {
        this.homeLowSteals = homeLowSteals;
    }

    public double getAwayHighSteals() {
        return awayHighSteals;
    }

    public void setAwayHighSteals(double awayHighSteals) {
        this.awayHighSteals = awayHighSteals;
    }

    public double getAwayLowSteals() {
        return awayLowSteals;
    }

    public void setAwayLowSteals(double awayLowSteals) {
        this.awayLowSteals = awayLowSteals;
    }

    public double getAwayStealScoringModelHigh() {
        return awayStealScoringModelHigh;
    }

    public void setAwayStealScoringModelHigh(double awayStealScoringModelHigh) {
        this.awayStealScoringModelHigh = awayStealScoringModelHigh;
    }

    public double getHomeStealScoringModelHigh() {
        return homeStealScoringModelHigh;
    }

    public void setHomeStealScoringModelHigh(double homeStealScoringModelHigh) {
        this.homeStealScoringModelHigh = homeStealScoringModelHigh;
    }

    public double getAwayBlockScoringModelHigh() {
        return awayBlockScoringModelHigh;
    }

    public void setAwayBlockScoringModelHigh(double awayBlockScoringModelHigh) {
        this.awayBlockScoringModelHigh = awayBlockScoringModelHigh;
    }

    public double getAwayBlockScoringModelLow() {
        return awayBlockScoringModelLow;
    }

    public void setAwayBlockScoringModelLow(double awayBlockScoringModelLow) {
        this.awayBlockScoringModelLow = awayBlockScoringModelLow;
    }

    public double getHomeBlockScoringModelHigh() {
        return homeBlockScoringModelHigh;
    }

    public void setHomeBlockScoringModelHigh(double homeBlockScoringModelHigh) {
        this.homeBlockScoringModelHigh = homeBlockScoringModelHigh;
    }

    public double getHomeBlockScoringModelLow() {
        return homeBlockScoringModelLow;
    }

    public void setHomeBlockScoringModelLow(double homeBlockScoringModelLow) {
        this.homeBlockScoringModelLow = homeBlockScoringModelLow;
    }

    public double getHomeScoringBPG() {
        return homeScoringBPG;
    }

    public void setHomeScoringBPG(double homeScoringBPG) {
        this.homeScoringBPG = homeScoringBPG;
    }

    public double getAwayScoringBPG() {
        return awayScoringBPG;
    }

    public void setAwayScoringBPG(double awayScoringBPG) {
        this.awayScoringBPG = awayScoringBPG;
    }


    public double getAwayStealScoringModelLow() {
        return awayStealScoringModelLow;
    }

    public void setAwayStealScoringModelLow(double awayStealScoringModelLow) {
        this.awayStealScoringModelLow = awayStealScoringModelLow;
    }

    public double getHomeStealScoringModelLow() {
        return homeStealScoringModelLow;
    }

    public void setHomeStealScoringModelLow(double homeStealScoringModelLow) {
        this.homeStealScoringModelLow = homeStealScoringModelLow;
    }

    public double getHomeHighTurnovers() {
        return homeHighTurnovers;
    }

    public void setHomeHighTurnovers(double homeHighTurnovers) {
        this.homeHighTurnovers = homeHighTurnovers;
    }

    public double getHomeLowTurnovers() {
        return homeLowTurnovers;
    }

    public void setHomeLowTurnovers(double homeLowTurnovers) {
        this.homeLowTurnovers = homeLowTurnovers;
    }

    public double getAwayHighTurnovers() {
        return awayHighTurnovers;
    }

    public void setAwayHighTurnovers(double awayHighTurnovers) {
        this.awayHighTurnovers = awayHighTurnovers;
    }

    public double getAwayLowTurnovers() {
        return awayLowTurnovers;
    }

    public void setAwayLowTurnovers(double awayLowTurnovers) {
        this.awayLowTurnovers = awayLowTurnovers;
    }

    public double getHomeTurnoverScoringModelHigh() {
        return homeTurnoverScoringModelHigh;
    }

    public void setHomeTurnoverScoringModelHigh(double homeTurnoverScoringModelHigh) {
        this.homeTurnoverScoringModelHigh = homeTurnoverScoringModelHigh;
    }

    public double getHomeTurnoverScoringModelLow() {
        return homeTurnoverScoringModelLow;
    }

    public void setHomeTurnoverScoringModelLow(double homeTurnoverScoringModelLow) {
        this.homeTurnoverScoringModelLow = homeTurnoverScoringModelLow;
    }

    public double getAwayTurnoverScoringModelHigh() {
        return awayTurnoverScoringModelHigh;
    }

    public void setAwayTurnoverScoringModelHigh(double awayTurnoverScoringModelHigh) {
        this.awayTurnoverScoringModelHigh = awayTurnoverScoringModelHigh;
    }

    public double getAwayTurnoverScoringModelLow() {
        return awayTurnoverScoringModelLow;
    }

    public void setAwayTurnoverScoringModelLow(double awayTurnoverScoringModelLow) {
        this.awayTurnoverScoringModelLow = awayTurnoverScoringModelLow;
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

    public double getHomeHighRebounds() {
        return homeHighRebounds;
    }

    public void setHomeHighRebounds(double homeHighRebounds) {
        this.homeHighRebounds = homeHighRebounds;
    }

    public double getHomeLowRebounds() {
        return homeLowRebounds;
    }

    public void setHomeLowRebounds(double homeLowRebounds) {
        this.homeLowRebounds = homeLowRebounds;
    }

    public double getAwayHighRebounds() {
        return awayHighRebounds;
    }

    public void setAwayHighRebounds(double awayHighRebounds) {
        this.awayHighRebounds = awayHighRebounds;
    }

    public double getAwayLowRebounds() {
        return awayLowRebounds;
    }

    public void setAwayLowRebounds(double awayLowRebounds) {
        this.awayLowRebounds = awayLowRebounds;
    }

    public double getHomeOffensiveReboundScoringModelHigh() {
        return homeOffensiveReboundScoringModelHigh;
    }

    public void setHomeOffensiveReboundScoringModelHigh(double homeOffensiveReboundScoringModelHigh) {
        this.homeOffensiveReboundScoringModelHigh = homeOffensiveReboundScoringModelHigh;
    }

    public double getHomeOffensiveReboundScoringModelLow() {
        return homeOffensiveReboundScoringModelLow;
    }

    public void setHomeOffensiveReboundScoringModelLow(double homeOffensiveReboundScoringModelLow) {
        this.homeOffensiveReboundScoringModelLow = homeOffensiveReboundScoringModelLow;
    }

    public double getHomeHighFouls() {
        return homeHighFouls;
    }

    public void setHomeHighFouls(double homeHighFouls) {
        this.homeHighFouls = homeHighFouls;
    }

    public double getHomeLowFouls() {
        return homeLowFouls;
    }

    public void setHomeLowFouls(double homeLowFouls) {
        this.homeLowFouls = homeLowFouls;
    }

    public double getAwayHighFouls() {
        return awayHighFouls;
    }

    public void setAwayHighFouls(double awayHighFouls) {
        this.awayHighFouls = awayHighFouls;
    }

    public double getAwayLowFouls() {
        return awayLowFouls;
    }

    public void setAwayLowFouls(double awayLowFouls) {
        this.awayLowFouls = awayLowFouls;
    }

    public double getHomeFoulScoringModelHigh() {
        return homeFoulScoringModelHigh;
    }

    public void setHomeFoulScoringModelHigh(double homeFoulScoringModelHigh) {
        this.homeFoulScoringModelHigh = homeFoulScoringModelHigh;
    }

    public double getHomeFoulScoringModelLow() {
        return homeFoulScoringModelLow;
    }

    public void setHomeFoulScoringModelLow(double homeFoulScoringModelLow) {
        this.homeFoulScoringModelLow = homeFoulScoringModelLow;
    }

    public double getAwayFoulScoringModelHigh() {
        return awayFoulScoringModelHigh;
    }

    public void setAwayFoulScoringModelHigh(double awayFoulScoringModelHigh) {
        this.awayFoulScoringModelHigh = awayFoulScoringModelHigh;
    }

    public double getAwayFoulScoringModelLow() {
        return awayFoulScoringModelLow;
    }

    public void setAwayFoulScoringModelLow(double awayFoulScoringModelLow) {
        this.awayFoulScoringModelLow = awayFoulScoringModelLow;
    }

    public double getAwayStaticBlockScoringModel() {
        return awayStaticBlockScoringModel;
    }

    public void setAwayStaticBlockScoringModel(double awayStaticBlockScoringModel) {
        this.awayStaticBlockScoringModel = awayStaticBlockScoringModel;
    }

    public double getAwayStaticStealScoringModel() {
        return awayStaticStealScoringModel;
    }

    public void setAwayStaticStealScoringModel(double awayStaticStealScoringModel) {
        this.awayStaticStealScoringModel = awayStaticStealScoringModel;
    }

    public double getHomeStaticBlockScoringModel() {
        return homeStaticBlockScoringModel;
    }

    public void setHomeStaticBlockScoringModel(double homeStaticBlockScoringModel) {
        this.homeStaticBlockScoringModel = homeStaticBlockScoringModel;
    }

    public double getHomeStaticStealScoringModel() {
        return homeStaticStealScoringModel;
    }

    public void setHomeStaticStealScoringModel(double homeStaticStealScoringModel) {
        this.homeStaticStealScoringModel = homeStaticStealScoringModel;
    }

    public double getHomeStaticTurnoverScoringModel() {
        return homeStaticTurnoverScoringModel;
    }

    public void setHomeStaticTurnoverScoringModel(double homeStaticTurnoverScoringModel) {
        this.homeStaticTurnoverScoringModel = homeStaticTurnoverScoringModel;
    }

    public double getAwayStaticTurnoverScoringModel() {
        return awayStaticTurnoverScoringModel;
    }

    public void setAwayStaticTurnoverScoringModel(double awayStaticTurnoverScoringModel) {
        this.awayStaticTurnoverScoringModel = awayStaticTurnoverScoringModel;
    }
    public double getHomeStaticFoulScoringModel() {
        return homeStaticFoulScoringModel;
    }

    public void setHomeStaticFoulScoringModel(double homeStaticFoulScoringModel) {
        this.homeStaticFoulScoringModel = homeStaticFoulScoringModel;
    }
    public double getAwayStaticFoulScoringModel() {
        return awayStaticFoulScoringModel;
    }

    public void setAwayStaticFoulScoringModel(double awayStaticFoulScoringModel) {
        this.awayStaticFoulScoringModel = awayStaticFoulScoringModel;
    }
    public double getHomeBlockPerGame() {
        return homeBlockPerGame;
    }
    public void setHomeBlockPerGame(double homeBlockPerGame) {
        this.homeBlockPerGame = homeBlockPerGame;
    }
    public double getHomeStealPerGame() {
        return homeStealPerGame;
    }
    public void setHomeStealPerGame(double homeStealPerGame) {
        this.homeStealPerGame = homeStealPerGame;
    }
    public double getHomeTurnoverPerGame() {
        return homeTurnoverPerGame;
    }
    public void setHomeTurnoverPerGame(double homeTurnoverPerGame) {
        this.homeTurnoverPerGame = homeTurnoverPerGame;
    }
    public double getHomeReboundPerGame() {
        return homeReboundPerGame;
    }
    public void setHomeReboundPerGame(double homeReboundPerGame) {
        this.homeReboundPerGame = homeReboundPerGame;
    }
    public double getHomeFoulPerGame() {
        return homeFoulPerGame;
    }
    public void setHomeFoulPerGame(double homeFoulPerGame) {
        this.homeFoulPerGame = homeFoulPerGame;
    }
    public double getAwayBlockPerGame() {
        return awayBlockPerGame;
    }
    public void setAwayBlockPerGame(double awayBlockPerGame) {
        this.awayBlockPerGame = awayBlockPerGame;
    }
    public double getAwayStealPerGame() {
        return awayStealPerGame;
    }
    public void setAwayStealPerGame(double awayStealPerGame) {
        this.awayStealPerGame = awayStealPerGame;
    }
    public double getAwayTurnoverPerGame() {
        return awayTurnoverPerGame;
    }
    public void setAwayTurnoverPerGame(double awayTurnoverPerGame) {
        this.awayTurnoverPerGame = awayTurnoverPerGame;
    }
    public double getAwayReboundPerGame() {
        return awayReboundPerGame;
    }
    public void setAwayReboundPerGame(double awayReboundPerGame) {
        this.awayReboundPerGame = awayReboundPerGame;
    }
    public double getAwayFoulPerGame() {
        return awayFoulPerGame;
    }
    public void setAwayFoulPerGame(double awayFoulPerGame) {
        this.awayFoulPerGame = awayFoulPerGame;
    }
    public double getHomeHighFieldGoalAttempts() {
        return homeHighFieldGoalAttempts;
    }
    public void setHomeHighFieldGoalAttempts(double homeHighFieldGoalAttempts) {
        this.homeHighFieldGoalAttempts = homeHighFieldGoalAttempts;
    }
    public double getHomeLowFieldGoalAttempts() {
        return homeLowFieldGoalAttempts;
    }
    public void setHomeLowFieldGoalAttempts(double homeLowFieldGoalAttempts) {
        this.homeLowFieldGoalAttempts = homeLowFieldGoalAttempts;
    }

    public double getHomeHighThreePointAttempts() {
        return homeHighThreePointAttempts;
    }

    public void setHomeHighThreePointAttempts(double homeHighThreePointAttempts) {
        this.homeHighThreePointAttempts = homeHighThreePointAttempts;
    }

    public double getHomeLowThreePointAttempts() {
        return homeLowThreePointAttempts;
    }

    public void setHomeLowThreePointAttempts(double homeLowThreePointAttempts) {
        this.homeLowThreePointAttempts = homeLowThreePointAttempts;
    }

    public double getHomeHighFreeThrowAttempts() {
        return homeHighFreeThrowAttempts;
    }

    public void setHomeHighFreeThrowAttempts(double homeHighFreeThrowAttempts) {
        this.homeHighFreeThrowAttempts = homeHighFreeThrowAttempts;
    }

    public double getHomeLowFreeThrowAttempts() {
        return homeLowFreeThrowAttempts;
    }

    public void setHomeLowFreeThrowAttempts(double homeLowFreeThrowAttempts) {
        this.homeLowFreeThrowAttempts = homeLowFreeThrowAttempts;
    }

    public double getHomeHighDefensiveRebounds() {
        return homeHighDefensiveRebounds;
    }

    public void setHomeHighDefensiveRebounds(double homeHighDefensiveRebounds) {
        this.homeHighDefensiveRebounds = homeHighDefensiveRebounds;
    }

    public double getHomeLowDefensiveRebounds() {
        return homeLowDefensiveRebounds;
    }

    public void setHomeLowDefensiveRebounds(double homeLowDefensiveRebounds) {
        this.homeLowDefensiveRebounds = homeLowDefensiveRebounds;
    }

    public double getHomeHighOffensiveRebounds() {
        return homeHighOffensiveRebounds;
    }

    public void setHomeHighOffensiveRebounds(double homeHighOffensiveRebounds) {
        this.homeHighOffensiveRebounds = homeHighOffensiveRebounds;
    }

    public double getHomeLowOffensiveRebounds() {
        return homeLowOffensiveRebounds;
    }

    public void setHomeLowOffensiveRebounds(double homeLowOffensiveRebounds) {
        this.homeLowOffensiveRebounds = homeLowOffensiveRebounds;
    }


    public double getAwayHighFieldGoalAttempts() {
        return awayHighFieldGoalAttempts;
    }

    public void setAwayHighFieldGoalAttempts(double awayHighFieldGoalAttempts) {
        this.awayHighFieldGoalAttempts = awayHighFieldGoalAttempts;
    }

    public double getAwayLowFieldGoalAttempts() {
        return awayLowFieldGoalAttempts;
    }

    public void setAwayLowFieldGoalAttempts(double awayLowFieldGoalAttempts) {
        this.awayLowFieldGoalAttempts = awayLowFieldGoalAttempts;
    }

    public double getAwayHighThreePointAttempts() {
        return awayHighThreePointAttempts;
    }

    public void setAwayHighThreePointAttempts(double awayHighThreePointAttempts) {
        this.awayHighThreePointAttempts = awayHighThreePointAttempts;
    }

    public double getAwayLowThreePointAttempts() {
        return awayLowThreePointAttempts;
    }

    public void setAwayLowThreePointAttempts(double awayLowThreePointAttempts) {
        this.awayLowThreePointAttempts = awayLowThreePointAttempts;
    }

    public double getAwayHighFreeThrowAttempts() {
        return awayHighFreeThrowAttempts;
    }

    public void setAwayHighFreeThrowAttempts(double awayHighFreeThrowAttempts) {
        this.awayHighFreeThrowAttempts = awayHighFreeThrowAttempts;
    }

    public double getAwayLowFreeThrowAttempts() {
        return awayLowFreeThrowAttempts;
    }

    public void setAwayLowFreeThrowAttempts(double awayLowFreeThrowAttempts) {
        this.awayLowFreeThrowAttempts = awayLowFreeThrowAttempts;
    }

    public double getAwayHighDefensiveRebounds() {
        return awayHighDefensiveRebounds;
    }

    public void setAwayHighDefensiveRebounds(double awayHighDefensiveRebounds) {
        this.awayHighDefensiveRebounds = awayHighDefensiveRebounds;
    }

    public double getAwayLowDefensiveRebounds() {
        return awayLowDefensiveRebounds;
    }

    public void setAwayLowDefensiveRebounds(double awayLowDefensiveRebounds) {
        this.awayLowDefensiveRebounds = awayLowDefensiveRebounds;
    }

    public double getAwayHighOffensiveRebounds() {
        return awayHighOffensiveRebounds;
    }

    public void setAwayHighOffensiveRebounds(double awayHighOffensiveRebounds) {
        this.awayHighOffensiveRebounds = awayHighOffensiveRebounds;
    }

    public double getAwayLowOffensiveRebounds() {
        return awayLowOffensiveRebounds;
    }

    public void setAwayLowOffensiveRebounds(double awayLowOffensiveRebounds) {
        this.awayLowOffensiveRebounds = awayLowOffensiveRebounds;
    }

    public double getHomeHighFreeThrowPercentage() {
        return homeHighFreeThrowPercentage;
    }

    public void setHomeHighFreeThrowPercentage(double homeHighFreeThrowPercentage) {
        this.homeHighFreeThrowPercentage = homeHighFreeThrowPercentage;
    }

    public double getHomeLowFreeThrowPercentage() {
        return homeLowFreeThrowPercentage;
    }

    public void setHomeLowFreeThrowPercentage(double homeLowFreeThrowPercentage) {
        this.homeLowFreeThrowPercentage = homeLowFreeThrowPercentage;
    }

    public double getAwayHighFreeThrowPercentage() {
        return awayHighFreeThrowPercentage;
    }

    public void setAwayHighFreeThrowPercentage(double awayHighFreeThrowPercentage) {
        this.awayHighFreeThrowPercentage = awayHighFreeThrowPercentage;
    }

    public double getAwayLowFreeThrowPercentage() {
        return awayLowFreeThrowPercentage;
    }

    public void setAwayLowFreeThrowPercentage(double awayLowFreeThrowPercentage) {
        this.awayLowFreeThrowPercentage = awayLowFreeThrowPercentage;
    }

    public double getHomeHighThreePointPercentage() {
        return homeHighThreePointPercentage;
    }

    public void setHomeHighThreePointPercentage(double homeHighThreePointPercentage) {
        this.homeHighThreePointPercentage = homeHighThreePointPercentage;
    }

    public double getHomeLowThreePointPercentage() {
        return homeLowThreePointPercentage;
    }

    public void setHomeLowThreePointPercentage(double homeLowThreePointPercentage) {
        this.homeLowThreePointPercentage = homeLowThreePointPercentage;
    }

    public double getAwayHighThreePointPercentage() {
        return awayHighThreePointPercentage;
    }

    public void setAwayHighThreePointPercentage(double awayHighThreePointPercentage) {
        this.awayHighThreePointPercentage = awayHighThreePointPercentage;
    }

    public double getAwayLowThreePointPercentage() {
        return awayLowThreePointPercentage;
    }

    public void setAwayLowThreePointPercentage(double awayLowThreePointPercentage) {
        this.awayLowThreePointPercentage = awayLowThreePointPercentage;
    }

    public double getHomeHighFieldGoalPercentage() {
        return homeHighFieldGoalPercentage;
    }

    public void setHomeHighFieldGoalPercentage(double homeHighFieldGoalPercentage) {
        this.homeHighFieldGoalPercentage = homeHighFieldGoalPercentage;
    }

    public double getHomeLowFieldGoalPercentage() {
        return homeLowFieldGoalPercentage;
    }

    public void setHomeLowFieldGoalPercentage(double homeLowFieldGoalPercentage) {
        this.homeLowFieldGoalPercentage = homeLowFieldGoalPercentage;
    }

    public double getAwayHighFieldGoalPercentage() {
        return awayHighFieldGoalPercentage;
    }

    public void setAwayHighFieldGoalPercentage(double awayHighFieldGoalPercentage) {
        this.awayHighFieldGoalPercentage = awayHighFieldGoalPercentage;
    }

    public double getAwayLowFieldGoalPercentage() {
        return awayLowFieldGoalPercentage;
    }

    public void setAwayLowFieldGoalPercentage(double awayLowFieldGoalPercentage) {
        this.awayLowFieldGoalPercentage = awayLowFieldGoalPercentage;
    }

    public double getHomeHighFieldGoalsForecasted() {
        return homeHighFieldGoalsForecasted;
    }

    public void setHomeHighFieldGoalsForecasted(double homeHighFieldGoalsForecasted) {
        this.homeHighFieldGoalsForecasted = homeHighFieldGoalsForecasted;
    }

    public double getHomeLowFieldGoalsForecasted() {
        return homeLowFieldGoalsForecasted;
    }

    public void setHomeLowFieldGoalsForecasted(double homeLowFieldGoalsForecasted) {
        this.homeLowFieldGoalsForecasted = homeLowFieldGoalsForecasted;
    }

    public double getAwayHighFieldGoalsForecasted() {
        return awayHighFieldGoalsForecasted;
    }

    public void setAwayHighFieldGoalsForecasted(double awayHighFieldGoalsForecasted) {
        this.awayHighFieldGoalsForecasted = awayHighFieldGoalsForecasted;
    }

    public double getAwayLowFieldGoalsForecasted() {
        return awayLowFieldGoalsForecasted;
    }

    public void setAwayLowFieldGoalsForecasted(double awayLowFieldGoalsForecasted) {
        this.awayLowFieldGoalsForecasted = awayLowFieldGoalsForecasted;
    }

    public double getHomeHighFreeThrowsForecasted() {
        return homeHighFreeThrowsForecasted;
    }

    public void setHomeHighFreeThrowsForecasted(double homeHighFreeThrowsForecasted) {
        this.homeHighFreeThrowsForecasted = homeHighFreeThrowsForecasted;
    }

    public double getHomeLowFreeThrowsForecasted() {
        return homeLowFreeThrowsForecasted;
    }

    public void setHomeLowFreeThrowsForecasted(double homeLowFreeThrowsForecasted) {
        this.homeLowFreeThrowsForecasted = homeLowFreeThrowsForecasted;
    }

    public double getAwayHighFreeThrowsForecasted() {
        return awayHighFreeThrowsForecasted;
    }

    public void setAwayHighFreeThrowsForecasted(double awayHighFreeThrowsForecasted) {
        this.awayHighFreeThrowsForecasted = awayHighFreeThrowsForecasted;
    }

    public double getAwayLowFreeThrowsForecasted() {
        return awayLowFreeThrowsForecasted;
    }

    public void setAwayLowFreeThrowsForecasted(double awayLowFreeThrowsForecasted) {
        this.awayLowFreeThrowsForecasted = awayLowFreeThrowsForecasted;
    }

    public double getHomeHighThreePointersForecasted() {
        return homeHighThreePointersForecasted;
    }

    public void setHomeHighThreePointersForecasted(double homeHighThreePointersForecasted) {
        this.homeHighThreePointersForecasted = homeHighThreePointersForecasted;
    }

    public double getHomeLowThreePointersForecasted() {
        return homeLowThreePointersForecasted;
    }

    public void setHomeLowThreePointersForecasted(double homeLowThreePointersForecasted) {
        this.homeLowThreePointersForecasted = homeLowThreePointersForecasted;
    }

    public double getAwayHighThreePointersForecasted() {
        return awayHighThreePointersForecasted;
    }

    public void setAwayHighThreePointersForecasted(double awayHighThreePointersForecasted) {
        this.awayHighThreePointersForecasted = awayHighThreePointersForecasted;
    }

    public double getAwayLowThreePointersForecasted() {
        return awayLowThreePointersForecasted;
    }

    public void setAwayLowThreePointersForecasted(double awayLowThreePointersForecasted) {
        this.awayLowThreePointersForecasted = awayLowThreePointersForecasted;
    }

    public double getHomeDefensiveReboundScoringModelHigh() {
        return homeDefensiveReboundScoringModelHigh;
    }

    public void setHomeDefensiveReboundScoringModelHigh(double homeDefensiveReboundScoringModelHigh) {
        this.homeDefensiveReboundScoringModelHigh = homeDefensiveReboundScoringModelHigh;
    }

    public double getHomeDefensiveReboundScoringModelLow() {
        return homeDefensiveReboundScoringModelLow;
    }

    public void setHomeDefensiveReboundScoringModelLow(double homeDefensiveReboundScoringModelLow) {
        this.homeDefensiveReboundScoringModelLow = homeDefensiveReboundScoringModelLow;
    }

    public double getAwayOffensiveReboundScoringModelHigh() {
        return awayOffensiveReboundScoringModelHigh;
    }

    public void setAwayOffensiveReboundScoringModelHigh(double awayOffensiveReboundScoringModelHigh) {
        this.awayOffensiveReboundScoringModelHigh = awayOffensiveReboundScoringModelHigh;
    }

    public double getAwayOffensiveReboundScoringModelLow() {
        return awayOffensiveReboundScoringModelLow;
    }

    public void setAwayOffensiveReboundScoringModelLow(double awayOffensiveReboundScoringModelLow) {
        this.awayOffensiveReboundScoringModelLow = awayOffensiveReboundScoringModelLow;
    }

    public double getAwayDefensiveReboundScoringModelHigh() {
        return awayDefensiveReboundScoringModelHigh;
    }

    public void setAwayDefensiveReboundScoringModelHigh(double awayDefensiveReboundScoringModelHigh) {
        this.awayDefensiveReboundScoringModelHigh = awayDefensiveReboundScoringModelHigh;
    }

    public double getAwayDefensiveReboundScoringModelLow() {
        return awayDefensiveReboundScoringModelLow;
    }

    public void setAwayDefensiveReboundScoringModelLow(double awayDefensiveReboundScoringModelLow) {
        this.awayDefensiveReboundScoringModelLow = awayDefensiveReboundScoringModelLow;
    }

    public double getHomePointsPerGame() {
        return homePointsPerGame;
    }

    public void setHomePointsPerGame(double homePointsPerGame) {
        this.homePointsPerGame = homePointsPerGame;
    }

    public double getAwayPointsPerGame() {
        return awayPointsPerGame;
    }

    public void setAwayPointsPerGame(double awayPointsPerGame) {
        this.awayPointsPerGame = awayPointsPerGame;
    }

    public double getHomePointsPerGameRoc() {
        return homePointsPerGameRoc;
    }

    public void setHomePointsPerGameRoc(double homePointsPerGameRoc) {
        this.homePointsPerGameRoc = homePointsPerGameRoc;
    }

    public double getAwayPointsPerGameRoc() {
        return awayPointsPerGameRoc;
    }

    public void setAwayPointsPerGameRoc(double awayPointsPerGameRoc) {
        this.awayPointsPerGameRoc = awayPointsPerGameRoc;
    }

    public double getHomeBlocksPerGameRoc() {
        return homeBlocksPerGameRoc;
    }

    public void setHomeBlocksPerGameRoc(double homeBlocksPerGameRoc) {
        this.homeBlocksPerGameRoc = homeBlocksPerGameRoc;
    }

    public double getAwayBlocksPerGameRoc() {
        return awayBlocksPerGameRoc;
    }

    public void setAwayBlocksPerGameRoc(double awayBlocksPerGameRoc) {
        this.awayBlocksPerGameRoc = awayBlocksPerGameRoc;
    }

    public double getHomeReboundsPerGameRoc() {
        return homeReboundsPerGameRoc;
    }

    public void setHomeReboundsPerGameRoc(double homeReboundsPerGameRoc) {
        this.homeReboundsPerGameRoc = homeReboundsPerGameRoc;
    }

    public double getAwayReboundsPerGameRoc() {
        return awayReboundsPerGameRoc;
    }

    public void setAwayReboundsPerGameRoc(double awayReboundsPerGameRoc) {
        this.awayReboundsPerGameRoc = awayReboundsPerGameRoc;
    }

    public double getHomeStealsPerGameRoc() {
        return homeStealsPerGameRoc;
    }

    public void setHomeStealsPerGameRoc(double homeStealsPerGameRoc) {
        this.homeStealsPerGameRoc = homeStealsPerGameRoc;
    }

    public double getAwayStealsPerGameRoc() {
        return awayStealsPerGameRoc;
    }

    public void setAwayStealsPerGameRoc(double awayStealsPerGameRoc) {
        this.awayStealsPerGameRoc = awayStealsPerGameRoc;
    }

    public double getHomeTurnoversPerGameRoc() {
        return homeTurnoversPerGameRoc;
    }

    public void setHomeTurnoversPerGameRoc(double homeTurnoversPerGameRoc) {
        this.homeTurnoversPerGameRoc = homeTurnoversPerGameRoc;
    }

    public double getAwayTurnoversPerGameRoc() {
        return awayTurnoversPerGameRoc;
    }

    public void setAwayTurnoversPerGameRoc(double awayTurnoversPerGameRoc) {
        this.awayTurnoversPerGameRoc = awayTurnoversPerGameRoc;
    }

    public double getHomeFoulsPerGameRoc() {
        return homeFoulsPerGameRoc;
    }

    public void setHomeFoulsPerGameRoc(double homeFoulsPerGameRoc) {
        this.homeFoulsPerGameRoc = homeFoulsPerGameRoc;
    }

    public double getAwayFoulsPerGameRoc() {
        return awayFoulsPerGameRoc;
    }

    public void setAwayFoulsPerGameRoc(double awayFoulsPerGameRoc) {
        this.awayFoulsPerGameRoc = awayFoulsPerGameRoc;
    }

    public double getHomeFreeThrowAttemptsPerGameRoc() {
        return homeFreeThrowAttemptsPerGameRoc;
    }

    public void setHomeFreeThrowAttemptsPerGameRoc(double homeFreeThrowAttemptsPerGameRoc) {
        this.homeFreeThrowAttemptsPerGameRoc = homeFreeThrowAttemptsPerGameRoc;
    }

    public double getAwayFreeThrowAttemptsPerGameRoc() {
        return awayFreeThrowAttemptsPerGameRoc;
    }

    public void setAwayFreeThrowAttemptsPerGameRoc(double awayFreeThrowAttemptsPerGameRoc) {
        this.awayFreeThrowAttemptsPerGameRoc = awayFreeThrowAttemptsPerGameRoc;
    }

    public double getAwayFreeThrowsPerGame() {
        return awayFreeThrowsPerGame;
    }

    public void setAwayFreeThrowsPerGame(double awayFreeThrowsPerGame) {
        this.awayFreeThrowsPerGame = awayFreeThrowsPerGame;
    }

    public double getHomeFreeThrowsPerGame() {
        return homeFreeThrowsPerGame;
    }

    public void setHomeFreeThrowsPerGame(double homeFreeThrowsPerGame) {
        this.homeFreeThrowsPerGame = homeFreeThrowsPerGame;
    }

    public double getHomePointsScoredVol() {
        return homePointsScoredVol;
    }

    public void setHomePointsScoredVol(double homePointsScoredVol) {
        this.homePointsScoredVol = homePointsScoredVol;
    }

    public double getAwayPointsScoredVol() {
        return awayPointsScoredVol;
    }

    public void setAwayPointsScoredVol(double awayPointsScoredVol) {
        this.awayPointsScoredVol = awayPointsScoredVol;
    }

    public double getHomePointsScoredVolRoc() {
        return homePointsScoredVolRoc;
    }

    public void setHomePointsScoredVolRoc(double homePointsScoredVolRoc) {
        this.homePointsScoredVolRoc = homePointsScoredVolRoc;
    }

    public double getAwayPointsScoredVolRoc() {
        return awayPointsScoredVolRoc;
    }

    public void setAwayPointsScoredVolRoc(double awayPointsScoredVolRoc) {
        this.awayPointsScoredVolRoc = awayPointsScoredVolRoc;
    }

    public double getHomeOverUnderRoc() {
        return homeOverUnderRoc;
    }

    public void setHomeOverUnderRoc(double homeOverUnderRoc) {
        this.homeOverUnderRoc = homeOverUnderRoc;
    }

    public double getAwayOverUnderRoc() {
        return awayOverUnderRoc;
    }

    public void setAwayOverUnderRoc(double awayOverUnderRoc) {
        this.awayOverUnderRoc = awayOverUnderRoc;
    }

    public double getHomeWinProbRoc() {
        return homeWinProbRoc;
    }

    public void setHomeWinProbRoc(double homeWinProbRoc) {
        this.homeWinProbRoc = homeWinProbRoc;
    }

    public double getAwayWinProbRoc() {
        return awayWinProbRoc;
    }

    public void setAwayWinProbRoc(double awayWinProbRoc) {
        this.awayWinProbRoc = awayWinProbRoc;
    }
}
