package BaseballQuant.Util;

import BaseballQuant.Model.MLBGame;
import BaseballQuant.Model.ScoreModel;

public class StaticScoreModelUtils {

    public static void squareRootTotal(ScoreModel safeScoreModel){
        float awayAmountToAddBack = (float)Math.sqrt(((safeScoreModel.getAwayHighPoints() + safeScoreModel.getAwayLowPoints()) / 2) - safeScoreModel.getAwayLowPoints());
        safeScoreModel.setAwayHighPoints(safeScoreModel.getAwayHighPoints() -  awayAmountToAddBack);
        safeScoreModel.setAwayLowPoints(safeScoreModel.getAwayLowPoints() + awayAmountToAddBack);
        float homeAmountToAddBack = (float)Math.sqrt(((safeScoreModel.getHomeHighPoints() + safeScoreModel.getHomeLowPoints()) / 2) - safeScoreModel.getHomeLowPoints());
        safeScoreModel.setHomeHighPoints(safeScoreModel.getHomeHighPoints() - homeAmountToAddBack);
        safeScoreModel.setHomeLowPoints(safeScoreModel.getHomeLowPoints() + homeAmountToAddBack);
    }

    public static void adjustForShortenedGame(ScoreModel scoreModel){
        scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() * (7.0/9.0));
        scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() * (7.0/9.0));
        scoreModel.setAwayHighPoints(scoreModel.getAwayHighPoints() * (7.0/9.0));
        scoreModel.setAwayLowPoints(scoreModel.getAwayLowPoints() * (7.0/9.0));
        scoreModel.setHomePitchingModelHigh(scoreModel.getHomePitchingModelHigh() * (float)(7.0/9.0));
        scoreModel.setHomePitchingModelLow(scoreModel.getHomePitchingModelLow() * (float)(7.0/9.0));
        scoreModel.setAwayPitchingModelHigh(scoreModel.getAwayPitchingModelHigh() * (float)(7.0/9.0));
        scoreModel.setAwayPitchingModelLow(scoreModel.getAwayPitchingModelLow() * (float)(7.0/9.0));
        scoreModel.setHomeRunsGivenUpHigh(scoreModel.getHomeRunsGivenUpHigh() * (float)(7.0/9.0));
        scoreModel.setHomeRunsGivenUpLow(scoreModel.getHomeRunsGivenUpLow() * (float)(7.0/9.0));
        scoreModel.setAwayRunsGivenUpHigh(scoreModel.getAwayRunsGivenUpHigh() * (float)(7.0/9.0));
        scoreModel.setAwayRunsGivenUpLow(scoreModel.getAwayRunsGivenUpLow() * (float)(7.0/9.0));
        scoreModel.setHomeFieldingModelHigh(scoreModel.getHomeFieldingModelHigh() * (7.0/9.0));
        scoreModel.setHomeFieldingMoelLow(scoreModel.getHomeFieldingMoelLow() * (7.0/9.0));
        scoreModel.setAwayFieldingModelHigh(scoreModel.getAwayFieldingModelHigh() * (7.0/9.0));
        scoreModel.setAwayFieldingModelLow(scoreModel.getAwayFieldingModelLow() * (7.0/9.0));
        scoreModel.setHomeFieldingHigh(scoreModel.getHomeFieldingHigh() * (7.0/9.0));
        scoreModel.setHomeFieldingLow(scoreModel.getHomeFieldingLow() * (7.0/9.0));
        scoreModel.setAwayFieldingHigh(scoreModel.getAwayFieldingHigh() * (7.0/9.0));
        scoreModel.setAwayFieldingLow(scoreModel.getAwayFieldingLow() * (7.0/9.0));
        scoreModel.setAwayHighStolenBases(scoreModel.getAwayHighStolenBases() * (7.0/9.0));
        scoreModel.setAwayLowStolenBases(scoreModel.getAwayLowStolenBases() * (7.0/9.0));
        scoreModel.setHomeHighStolenBases(scoreModel.getHomeHighStolenBases() * (7.0/9.0));
        scoreModel.setHomeLowStolenBases(scoreModel.getHomeLowStolenBases() * (7.0/9.0));
//        scoreModel.setHomePPG(scoreModel.getHomePPG() * (float)(7.0/9.0));
//        scoreModel.setAwayPPG(scoreModel.getAwayPPG() * (float)(7.0/9.0));
    }

//    public static void factorStaticOpponentPitchingIntoScoringModel(ScoreModel scoreModel, MLBGame game, double lowRunFactor, double highRunFactor, boolean moreInfo){
//        float homeBlockDifferentialHigh = (float) (scoreModel.getHomeModelRunsGivenUpPerGameHigh() - scoreModel.getAwayRGPGHigh());
//        float homeBlockDifferentialLow = (float) (scoreModel.getHomeModelRunsGivenUpPerGameLow() - scoreModel.getAwayRGPGLow());
//
//        double awayBlockDifferentialHigh = scoreModel.getAwayModelRunsGivenUpPerGameHigh() - scoreModel.getHomeRGPGHigh();
//        double awayBlockDifferentialLow = scoreModel.getAwayModelRunsGivenUpPerGameLow() - scoreModel.getHomeRGPGLow();
//
//        if(moreInfo) {
//            StringBuilder stringBuilder = new StringBuilder();
//            System.out.println("Before pitching Differential Range : " + (scoreModel.getHomeHighPoints() + scoreModel.getAwayHighPoints()) + " || " + (scoreModel.getHomeLowPoints() + scoreModel.getAwayLowPoints()));
//            //BLOCKS FORECASTED BY HOME TEAM
//            System.out.println("Home Pitching Range: " + scoreModel.getHomeRunsGivenUpHigh() + " || " + scoreModel.getHomeRunsGivenUpLow());
//            System.out.println("This compares to Away Team's (" + game.getAwayTeamName() + ") prior scoring against pitching range of: " + scoreModel.getAwayPitchingModelHigh() + " || " + scoreModel.getAwayPitchingModelLow());
//            //BLOCKS FORECASTED BY AWAY TEAM
//            //double awayBlockMidPoint = (scoreModel.getAwayHighBlocks() + scoreModel.getAwayLowBlocks())/2;
//            System.out.println("Away Pitching Range: " + scoreModel.getAwayRunsGivenUpHigh() + " || " + scoreModel.getAwayRunsGivenUpLow());
//            System.out.println("This compares to Home Team's (" + game.getHomeTeamName() + ")prior scoring against pitching range of: " + scoreModel.getHomePitchingModelHigh() + " || " + scoreModel.getHomePitchingModelLow());
//            stringBuilder.append("Home Team expects to play against a pitching range differential of: ").append(homeBlockDifferentialHigh).append(" || ").append(homeBlockDifferentialLow);
//            stringBuilder.append("Away Team expects to play against a pitching range differential of: ").append(awayBlockDifferentialHigh).append(" || ").append(awayBlockDifferentialLow);
//        }
//        scoreModel.setAwayHighPoints(scoreModel.getAwayHighPoints() - ((homeBlockDifferentialHigh * 1) * highRunFactor));
//        scoreModel.setAwayLowPoints(scoreModel.getAwayLowPoints() - ((homeBlockDifferentialLow * 1) * lowRunFactor));
//        scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() - ((awayBlockDifferentialHigh * 1) * highRunFactor));
//        scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() - ((awayBlockDifferentialLow * 1) * lowRunFactor));
//    }

    public static void factorOpponentPitchingIntoScoringModel(ScoreModel scoreModel, MLBGame game, double runDifferentialFactor, double highRunFactor, double lowRunFactor, boolean moreInfo){
        //FORECASTED DIFFERENCE IN BLOCKS BY HOME TEAM
        float homeBlockDifferentialHigh = scoreModel.getHomePitchingModelHigh() - scoreModel.getAwayRunsGivenUpHigh();
        float homeBlockDifferentialLow = scoreModel.getHomePitchingModelLow() - scoreModel.getAwayRunsGivenUpLow();

        double awayBlockDifferentialHigh = scoreModel.getAwayPitchingModelHigh() - scoreModel.getHomeRunsGivenUpHigh();
        double awayBlockDifferentialLow = scoreModel.getAwayPitchingModelLow() - scoreModel.getHomeRunsGivenUpLow();

        if(moreInfo) {
            StringBuilder stringBuilder = new StringBuilder();
            System.out.println("Before pitching Differential Range : " + (scoreModel.getHomeHighPoints() + scoreModel.getAwayHighPoints()) + " || " + (scoreModel.getHomeLowPoints() + scoreModel.getAwayLowPoints()));
            //BLOCKS FORECASTED BY HOME TEAM
            System.out.println("Home Pitching Range: " + scoreModel.getHomeRunsGivenUpHigh() + " || " + scoreModel.getHomeRunsGivenUpLow());
            System.out.println("This compares to Away Team's (" + game.getAwayTeamName() + ") prior scoring against pitching range of: " + scoreModel.getAwayPitchingModelHigh() + " || " + scoreModel.getAwayPitchingModelLow());
            //BLOCKS FORECASTED BY AWAY TEAM
            //double awayBlockMidPoint = (scoreModel.getAwayHighBlocks() + scoreModel.getAwayLowBlocks())/2;
            System.out.println("Away Pitching Range: " + scoreModel.getAwayRunsGivenUpHigh() + " || " + scoreModel.getAwayRunsGivenUpLow());
            System.out.println("This compares to Home Team's (" + game.getHomeTeamName() + ")prior scoring against pitching range of: " + scoreModel.getHomePitchingModelHigh() + " || " + scoreModel.getHomePitchingModelLow());
            stringBuilder.append("Home Team expects to play against a pitching range differential of: ").append(homeBlockDifferentialHigh).append(" || ").append(homeBlockDifferentialLow);
            stringBuilder.append("Away Team expects to play against a pitching range differential of: ").append(awayBlockDifferentialHigh).append(" || ").append(awayBlockDifferentialLow);
        }
        scoreModel.setAwayHighPoints(scoreModel.getAwayHighPoints() - ((homeBlockDifferentialHigh * runDifferentialFactor) * highRunFactor));
        scoreModel.setAwayLowPoints(scoreModel.getAwayLowPoints() - ((homeBlockDifferentialLow * runDifferentialFactor) * lowRunFactor));
        scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() - ((awayBlockDifferentialHigh * runDifferentialFactor) * highRunFactor));
        scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() - ((awayBlockDifferentialLow * runDifferentialFactor) * lowRunFactor));
    }

    public static void addHomeTeamAdvantage(ScoreModel scoreModel, double homeTeamAdvantageLow, double homeTeamAdvantageHigh, boolean shortGame){
        if(shortGame){
            scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() + (homeTeamAdvantageHigh * (7.0/9.0)));
            scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() + (homeTeamAdvantageLow * (7.0/9.0)));
        }else{
            scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() + (homeTeamAdvantageHigh));
            scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() + (homeTeamAdvantageLow));
        }
    }

    public static void addDynamicHomeTeamAdvantage(ScoreModel scoreModel,double homeTeamAdvantageHigh, boolean shortGame){
        if(shortGame){
            scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() + (homeTeamAdvantageHigh * (7.0/9.0)));

        }else{
            scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() + (homeTeamAdvantageHigh));
        }
    }
    public static void factorOpponentFieldingIntoScoringModel(ScoreModel scoreModel, MLBGame game, double highFieldingFactor, double lowFieldingFactor, boolean moreInfo){
        //FORECASTED DIFFERENCE IN BLOCKS BY HOME TEAM
        double homeFieldingDifferentialHigh = scoreModel.getHomeFieldingModelHigh() - scoreModel.getAwayFieldingHigh();
        double homeFieldingDifferentialLow = scoreModel.getHomeFieldingMoelLow() - scoreModel.getAwayFieldingLow();
        if(moreInfo) {
            StringBuilder stringBuilder = new StringBuilder();
            System.out.println("Before fielding Differential Range : " + (scoreModel.getHomeHighPoints() + scoreModel.getAwayHighPoints()) + " || " + (scoreModel.getHomeLowPoints() + scoreModel.getAwayLowPoints()));
            System.out.println("Before Away Fielding Differential Range : " + (scoreModel.getAwayHighPoints()) + " || " + (scoreModel.getAwayLowPoints()));
            //BLOCKS FORECASTED BY HOME TEAM
            System.out.println("Home Fielding Range: " + scoreModel.getHomeRunsGivenUpHigh() + " || " + scoreModel.getHomeRunsGivenUpLow());
            System.out.println("This compares to Away Team's (" + game.getAwayTeamName() + ") prior scoring against fielding range of: " + scoreModel.getAwayPitchingModelHigh() + " || " + scoreModel.getAwayPitchingModelLow());
            //BLOCKS FORECASTED BY AWAY TEAM
            //double awayBlockMidPoint = (scoreModel.getAwayHighBlocks() + scoreModel.getAwayLowBlocks())/2;
            System.out.println("Away Fielding Range: " + scoreModel.getAwayRunsGivenUpHigh() + " || " + scoreModel.getAwayRunsGivenUpLow());
            System.out.println("This compares to Home Team's (" + game.getHomeTeamName() + ")prior scoring against fielding range of: " + scoreModel.getHomePitchingModelHigh() + " || " + scoreModel.getHomePitchingModelLow());
            stringBuilder.append("Home Team expects to play against a fielding range differential of: ").append(homeFieldingDifferentialHigh).append(" || ").append(homeFieldingDifferentialLow).append("\n");
            stringBuilder.append("Away Team expects to play against a fielding range differential of: ").append(scoreModel.getAwayFieldingModelHigh() - scoreModel.getHomeFieldingHigh()).append(" || ").append(scoreModel.getAwayFieldingModelLow() - scoreModel.getHomeFieldingLow()).append("\n");
        }
        scoreModel.setAwayHighPoints(scoreModel.getAwayHighPoints() - (homeFieldingDifferentialLow * lowFieldingFactor));
        scoreModel.setAwayLowPoints(scoreModel.getAwayLowPoints() - ((homeFieldingDifferentialHigh * highFieldingFactor)));
        scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() - (((scoreModel.getAwayFieldingModelLow() - scoreModel.getHomeFieldingLow()) * lowFieldingFactor)));
        scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() - (((scoreModel.getAwayFieldingModelHigh() - scoreModel.getHomeFieldingHigh()) * highFieldingFactor)));
    }

    public static void addStolenBasesToModel(ScoreModel scoreModel,  double highStolenBaseFactor, double lowStolenBaseFactor){
        scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() + (scoreModel.getHomeHighStolenBases() * highStolenBaseFactor));
        scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() + (scoreModel.getHomeLowStolenBases() * lowStolenBaseFactor));
        scoreModel.setAwayHighPoints(scoreModel.getAwayHighPoints() + (scoreModel.getAwayHighStolenBases() * highStolenBaseFactor));
        scoreModel.setAwayLowPoints(scoreModel.getAwayLowPoints() + (scoreModel.getAwayLowStolenBases() * lowStolenBaseFactor));
    }
}
