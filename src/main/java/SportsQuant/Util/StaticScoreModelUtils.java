package SportsQuant.Util;

import SportsQuant.Model.ScoreModel;

public class StaticScoreModelUtils {

    public static void convertAndAssignMadeBasketsFromPercentages(ScoreModel scoreModel){
        double homeHighNonThreePointers = scoreModel.getHomeHighFieldGoalAttempts() - scoreModel.getHomeHighThreePointAttempts();
        double homeLowNonThreePointers = scoreModel.getHomeLowFieldGoalAttempts() - scoreModel.getHomeLowThreePointAttempts();
        double awayHighNonThreePointers = scoreModel.getAwayHighFieldGoalAttempts() - scoreModel.getAwayHighThreePointAttempts();
        double awayLowNonThreePointers = scoreModel.getAwayLowFieldGoalAttempts() - scoreModel.getAwayLowThreePointAttempts();
        scoreModel.setHomeHighFieldGoalsForecasted(homeHighNonThreePointers);
        scoreModel.setHomeLowFieldGoalsForecasted(homeLowNonThreePointers);
        scoreModel.setAwayHighFieldGoalsForecasted(awayHighNonThreePointers);
        scoreModel.setAwayLowFieldGoalsForecasted(awayLowNonThreePointers);
        scoreModel.setHomeHighFreeThrowsForecasted(scoreModel.getHomeHighFreeThrowAttempts());
        scoreModel.setHomeLowFreeThrowsForecasted(scoreModel.getHomeLowFreeThrowAttempts());
        scoreModel.setAwayHighFreeThrowsForecasted(scoreModel.getAwayHighFreeThrowAttempts());
        scoreModel.setAwayLowFreeThrowsForecasted(scoreModel.getAwayLowFreeThrowAttempts());
        scoreModel.setHomeHighThreePointersForecasted(scoreModel.getHomeHighThreePointAttempts());
        scoreModel.setHomeLowThreePointersForecasted(scoreModel.getHomeLowThreePointAttempts());
        scoreModel.setAwayHighThreePointersForecasted(scoreModel.getAwayHighThreePointAttempts());
        scoreModel.setAwayLowThreePointersForecasted(scoreModel.getAwayLowThreePointAttempts());
    }

    public static void sumFieldGoalsAndFreeThrowsIntoPoints(ScoreModel scoreModel){
        double homeHighPoints = ((scoreModel.getHomeHighFieldGoalsForecasted() * scoreModel.getHomeHighFieldGoalPercentage())*2) +
                ((scoreModel.getHomeHighThreePointersForecasted() * scoreModel.getHomeHighThreePointPercentage())*3) +
                ((scoreModel.getHomeHighFreeThrowsForecasted() * scoreModel.getHomeHighFreeThrowPercentage())*1);
        double homeLowPoints = ((scoreModel.getHomeLowFieldGoalsForecasted() * scoreModel.getHomeLowFieldGoalPercentage())*2) +
                ((scoreModel.getHomeLowThreePointersForecasted() * scoreModel.getHomeLowThreePointPercentage())*3) +
                ((scoreModel.getHomeLowFreeThrowsForecasted() * scoreModel.getHomeLowFreeThrowPercentage())*1);
        double awayHighPoints = ((scoreModel.getAwayHighFieldGoalsForecasted() * scoreModel.getAwayHighFieldGoalPercentage())*2) +
                ((scoreModel.getAwayHighThreePointersForecasted() * scoreModel.getAwayHighThreePointPercentage())*3) +
                ((scoreModel.getAwayHighFreeThrowsForecasted() * scoreModel.getAwayHighFreeThrowPercentage())*1);
        double awayLowPoints = ((scoreModel.getAwayLowFieldGoalsForecasted() * scoreModel.getAwayLowFieldGoalPercentage())*2) +
                ((scoreModel.getAwayLowThreePointersForecasted() * scoreModel.getAwayLowThreePointPercentage())*3) +
                ((scoreModel.getAwayLowFreeThrowsForecasted() * scoreModel.getAwayLowFreeThrowPercentage())*1);
        scoreModel.setHomeHighPoints(homeHighPoints);
        scoreModel.setHomeLowPoints(homeLowPoints);
        scoreModel.setAwayHighPoints(awayHighPoints);
        scoreModel.setAwayLowPoints(awayLowPoints);
    }

    public static void factorStealsIntoScoringModel(ScoreModel scoreModel,
                                                    double pointsReducedPerSteal, double lowerStealFactor, double upperStealFactor){
        double homeStealDifferentialHigh = scoreModel.getAwayHighSteals() - scoreModel.getHomeStealScoringModelHigh();
        double homeStealDifferentialLow = scoreModel.getAwayLowSteals() - scoreModel.getHomeStealScoringModelLow();
        double awayStealDifferentialHigh = scoreModel.getHomeHighSteals() - scoreModel.getAwayStealScoringModelHigh();
        double awayStealDifferentialLow = scoreModel.getHomeLowSteals() - scoreModel.getAwayStealScoringModelLow();
        double homeHighThreePointMix = scoreModel.getHomeHighThreePointAttempts() / (scoreModel.getHomeHighFieldGoalAttempts());
        double homeLowThreePointMix = scoreModel.getHomeLowThreePointAttempts() / (scoreModel.getHomeLowFieldGoalAttempts());
        double awayHighThreePointMix = scoreModel.getAwayHighThreePointAttempts() / (scoreModel.getAwayHighFieldGoalAttempts());
        double awayLowThreePointMix = scoreModel.getAwayLowThreePointAttempts() / (scoreModel.getAwayLowFieldGoalAttempts());
//        scoreModel.setAwayHighPoints(scoreModel.getAwayHighPoints() - ((awayStealDifferentialLow * lowerStealFactor)* pointsReducedPerSteal));
//        scoreModel.setAwayLowPoints(scoreModel.getAwayLowPoints() - ((awayStealDifferentialHigh * upperStealFactor)* pointsReducedPerSteal));
//        scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() - ((homeStealDifferentialLow * lowerStealFactor)* pointsReducedPerSteal));
//        scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() - ((homeStealDifferentialHigh * upperStealFactor) * pointsReducedPerSteal));

        //TAKE POSSESSIONS AWAY
        scoreModel.setHomeHighThreePointersForecasted(scoreModel.getHomeHighThreePointersForecasted() - (homeStealDifferentialLow * homeHighThreePointMix));
        scoreModel.setHomeHighFieldGoalsForecasted(scoreModel.getHomeHighFieldGoalsForecasted() - (homeStealDifferentialLow * (1-homeHighThreePointMix)));
        scoreModel.setHomeLowThreePointersForecasted(scoreModel.getHomeLowThreePointersForecasted() - (homeStealDifferentialHigh * homeLowThreePointMix));
        scoreModel.setHomeLowFieldGoalsForecasted(scoreModel.getHomeLowFieldGoalsForecasted() - (homeStealDifferentialHigh * (1-homeLowThreePointMix)));

        scoreModel.setAwayHighThreePointersForecasted(scoreModel.getAwayHighThreePointersForecasted() - (awayStealDifferentialLow * awayHighThreePointMix));
        scoreModel.setAwayHighFieldGoalsForecasted(scoreModel.getAwayHighFieldGoalsForecasted() - (awayStealDifferentialLow * (1-awayHighThreePointMix)));
        scoreModel.setAwayLowThreePointersForecasted(scoreModel.getAwayLowThreePointersForecasted() - (awayStealDifferentialHigh * awayLowThreePointMix));
        scoreModel.setAwayLowFieldGoalsForecasted(scoreModel.getAwayLowFieldGoalsForecasted() - (awayStealDifferentialHigh * (1-awayLowThreePointMix)));


        //ADD POSSESSIONS BACK TO TEAM THAT STOLE BALL.
        scoreModel.setHomeHighThreePointersForecasted(scoreModel.getHomeHighThreePointersForecasted() + (awayStealDifferentialHigh * homeHighThreePointMix));
        scoreModel.setHomeHighFieldGoalsForecasted(scoreModel.getHomeHighFieldGoalsForecasted() + (awayStealDifferentialHigh * (1-homeHighThreePointMix)));
        scoreModel.setHomeLowThreePointersForecasted(scoreModel.getHomeLowThreePointersForecasted() + (awayStealDifferentialLow * homeLowThreePointMix));
        scoreModel.setHomeLowFieldGoalsForecasted(scoreModel.getHomeLowFieldGoalsForecasted() + (awayStealDifferentialLow * (1-homeLowThreePointMix)));

        scoreModel.setAwayHighThreePointersForecasted(scoreModel.getAwayHighThreePointersForecasted() + (homeStealDifferentialHigh * awayHighThreePointMix));
        scoreModel.setAwayHighFieldGoalsForecasted(scoreModel.getAwayHighFieldGoalsForecasted() + (homeStealDifferentialHigh * (1-awayHighThreePointMix)));
        scoreModel.setAwayLowThreePointersForecasted(scoreModel.getAwayLowThreePointersForecasted() + (homeStealDifferentialLow * awayLowThreePointMix));
        scoreModel.setAwayLowFieldGoalsForecasted(scoreModel.getAwayLowFieldGoalsForecasted() + (homeStealDifferentialLow * (1-awayLowThreePointMix)));

    }

    public static void factorBlocksIntoScoringModel(ScoreModel scoreModel,  double pointsReducedPerBlock,
                                                    double lowerBlockFactor, double upperBlockFactor){

        double homeBlockDifferentialHigh = scoreModel.getAwayHighBlocks() - scoreModel.getHomeBlockScoringModelHigh() ;
        double homeBlockDifferentialLow =  scoreModel.getAwayLowBlocks() - scoreModel.getHomeBlockScoringModelLow()  ;
        double awayBlockDifferentialHigh = scoreModel.getHomeHighBlocks() - scoreModel.getAwayBlockScoringModelHigh() ;
        double awayBlockDifferentialLow = scoreModel.getHomeLowBlocks() - scoreModel.getAwayBlockScoringModelLow() ;

        double homeHighThreePointMix = scoreModel.getHomeHighThreePointAttempts() / (scoreModel.getHomeHighFieldGoalAttempts());
        double homeLowThreePointMix = scoreModel.getHomeLowThreePointAttempts() / (scoreModel.getHomeLowFieldGoalAttempts());
        double awayHighThreePointMix = scoreModel.getAwayHighThreePointAttempts() / (scoreModel.getAwayHighFieldGoalAttempts());
        double awayLowThreePointMix = scoreModel.getAwayLowThreePointAttempts() / (scoreModel.getAwayLowFieldGoalAttempts());

//        scoreModel.setAwayHighPoints(scoreModel.getAwayHighPoints() - ((awayBlockDifferentialLow * lowerBlockFactor) * pointsReducedPerBlock));
//        scoreModel.setAwayLowPoints(scoreModel.getAwayLowPoints() - ((awayBlockDifferentialHigh * upperBlockFactor) * pointsReducedPerBlock));
//        scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() - ((homeBlockDifferentialLow * lowerBlockFactor) * pointsReducedPerBlock));
//        scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() - ((homeBlockDifferentialHigh * upperBlockFactor) * pointsReducedPerBlock));
//        scoreModel.setAwayHighFieldGoalsForecasted(scoreModel.getAwayHighFieldGoalsForecasted() - ((awayBlockDifferentialLow * lowerBlockFactor)));
//        scoreModel.setAwayLowFieldGoalAttempts(scoreModel.getAwayLowFieldGoalsForecasted() - ((awayBlockDifferentialHigh * upperBlockFactor)));
//        scoreModel.setHomeHighFieldGoalAttempts(scoreModel.getHomeHighFieldGoalsForecasted() - ((homeBlockDifferentialLow * lowerBlockFactor)));
//        scoreModel.setHomeLowFieldGoalAttempts(scoreModel.getHomeLowFieldGoalsForecasted() - ((homeBlockDifferentialHigh * upperBlockFactor)));
        scoreModel.setHomeHighThreePointersForecasted(scoreModel.getHomeHighThreePointersForecasted() - (homeBlockDifferentialLow * homeHighThreePointMix));
        scoreModel.setHomeHighFieldGoalsForecasted(scoreModel.getHomeHighFieldGoalsForecasted() - (homeBlockDifferentialLow * (1-homeHighThreePointMix)));
        scoreModel.setHomeLowThreePointersForecasted(scoreModel.getHomeLowThreePointersForecasted() - (homeBlockDifferentialHigh * homeLowThreePointMix));
        scoreModel.setHomeLowFieldGoalsForecasted(scoreModel.getHomeLowFieldGoalsForecasted() - (homeBlockDifferentialHigh * (1-homeLowThreePointMix)));

        scoreModel.setAwayHighThreePointersForecasted(scoreModel.getAwayHighThreePointersForecasted() - (awayBlockDifferentialLow * awayHighThreePointMix));
        scoreModel.setAwayHighFieldGoalsForecasted(scoreModel.getAwayHighFieldGoalsForecasted() - (awayBlockDifferentialLow * (1-awayHighThreePointMix)));
        scoreModel.setAwayLowThreePointersForecasted(scoreModel.getAwayLowThreePointersForecasted() - (awayBlockDifferentialHigh * awayLowThreePointMix));
        scoreModel.setAwayLowFieldGoalsForecasted(scoreModel.getAwayLowFieldGoalsForecasted() - (awayBlockDifferentialHigh * (1-awayLowThreePointMix)));

    }


    public static void factorFoulsIntoScoringModel(ScoreModel scoreModel, double pointsReducedPerFoul, double lowerFoulFactor,double upperFoulFactor){

        double homeFoulDifferentialHigh = scoreModel.getHomeFoulScoringModelHigh() - scoreModel.getAwayHighFouls();
        double homeFoulDifferentialLow = scoreModel.getHomeFoulScoringModelLow() - scoreModel.getAwayLowFouls();
        double awayFoulDifferentialHigh = scoreModel.getAwayFoulScoringModelHigh() - scoreModel.getHomeHighFouls();
        double awayFoulDifferentialLow = scoreModel.getAwayFoulScoringModelLow() - scoreModel.getHomeLowFouls();



        scoreModel.setAwayHighFreeThrowsForecasted(scoreModel.getAwayHighFreeThrowsForecasted() - ((awayFoulDifferentialHigh)) * upperFoulFactor);
        scoreModel.setAwayLowFreeThrowsForecasted(scoreModel.getAwayLowFreeThrowsForecasted() - ((awayFoulDifferentialLow)) * lowerFoulFactor);
        scoreModel.setHomeHighFreeThrowsForecasted(scoreModel.getHomeHighFreeThrowsForecasted() - ((homeFoulDifferentialHigh) * upperFoulFactor));
        scoreModel.setHomeLowFreeThrowsForecasted(scoreModel.getHomeLowFreeThrowsForecasted() - ((homeFoulDifferentialLow)) * lowerFoulFactor);


    }



    public static void factorDefensiveReboundsIntoScoringModel(ScoreModel scoreModel, double pointsReducedPerRebound, double lowerReboundFactor,double upperReboundFactor, boolean moreInfo){

        if(moreInfo){
            System.out.println("Home Three Point Attempt Range Before DefReb: " + scoreModel.getHomeHighThreePointAttempts() + " || " + scoreModel.getHomeLowThreePointAttempts());
            System.out.println("Home Field Goal Attempt Range Before DefReb: " + scoreModel.getHomeHighFieldGoalAttempts() + " || " + scoreModel.getHomeLowFieldGoalAttempts());
            System.out.println("Away Three Point Attempt Range Before DefReb: " + scoreModel.getAwayHighThreePointAttempts() + " || " + scoreModel.getAwayLowThreePointAttempts());
            System.out.println("Away Field Goal Attempt Range Before DefReb: " + scoreModel.getAwayHighFieldGoalAttempts() + " || " + scoreModel.getAwayLowFieldGoalAttempts());
        }
        double homeReboundDifferentialHigh = scoreModel.getAwayHighDefensiveRebounds() - scoreModel.getHomeDefensiveReboundScoringModelHigh();
        double homeReboundDifferentialLow = scoreModel.getAwayLowDefensiveRebounds() - scoreModel.getHomeDefensiveReboundScoringModelLow();

        double awayReboundDifferentialHigh = scoreModel.getHomeHighDefensiveRebounds() - scoreModel.getAwayDefensiveReboundScoringModelHigh();
        double awayReboundDifferentialLow = scoreModel.getHomeLowDefensiveRebounds() - scoreModel.getAwayDefensiveReboundScoringModelLow();

        double homeHighThreePointMix = scoreModel.getHomeHighThreePointAttempts() / (scoreModel.getHomeHighFieldGoalAttempts());
        double homeLowThreePointMix = scoreModel.getHomeLowThreePointAttempts() / (scoreModel.getHomeLowFieldGoalAttempts());
        double awayHighThreePointMix = scoreModel.getAwayHighThreePointAttempts() / (scoreModel.getAwayHighFieldGoalAttempts());
        double awayLowThreePointMix = scoreModel.getAwayLowThreePointAttempts() / (scoreModel.getAwayLowFieldGoalAttempts());


//        scoreModel.setAwayHighPoints(scoreModel.getAwayHighPoints() - ((awayTurnoverDifferentialHigh * lowerReboundFactor) * pointsReducedPerRebound));
//        scoreModel.setAwayLowPoints(scoreModel.getAwayLowPoints() - ((awayTurnoverDifferentialLow * upperReboundFactor) * pointsReducedPerRebound));
//        scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() - ((homeTurnoverDifferentialHigh * lowerReboundFactor) * pointsReducedPerRebound));
//        scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() - ((homeTurnoverDifferentialLow * upperReboundFactor) * pointsReducedPerRebound));

        scoreModel.setHomeHighThreePointersForecasted(scoreModel.getHomeHighThreePointersForecasted() - (homeReboundDifferentialLow * homeHighThreePointMix));
        scoreModel.setHomeHighFieldGoalsForecasted(scoreModel.getHomeHighFieldGoalsForecasted() - (homeReboundDifferentialLow * (1-homeHighThreePointMix)));
        scoreModel.setHomeLowThreePointersForecasted(scoreModel.getHomeLowThreePointersForecasted() - (homeReboundDifferentialHigh * homeLowThreePointMix));
        scoreModel.setHomeLowFieldGoalsForecasted(scoreModel.getHomeLowFieldGoalsForecasted() - (homeReboundDifferentialHigh * (1-homeLowThreePointMix)));

        scoreModel.setAwayHighThreePointersForecasted(scoreModel.getAwayHighThreePointersForecasted() - (awayReboundDifferentialLow * awayHighThreePointMix));
        scoreModel.setAwayHighFieldGoalsForecasted(scoreModel.getAwayHighFieldGoalsForecasted() - (awayReboundDifferentialLow * (1-awayHighThreePointMix)));
        scoreModel.setAwayLowThreePointersForecasted(scoreModel.getAwayLowThreePointersForecasted() - (awayReboundDifferentialHigh * awayLowThreePointMix));
        scoreModel.setAwayLowFieldGoalsForecasted(scoreModel.getAwayLowFieldGoalsForecasted() - (awayReboundDifferentialHigh * (1-awayLowThreePointMix)));
        if(moreInfo){
            System.out.println("Home Three Point Attempt Range After DefReb: " + scoreModel.getHomeHighThreePointAttempts() + " || " + scoreModel.getHomeLowThreePointAttempts());
            System.out.println("Home Field Goal Attempt Range After DefReb: " + scoreModel.getHomeHighFieldGoalAttempts() + " || " + scoreModel.getHomeLowFieldGoalAttempts());
            System.out.println("Away Three Point Attempt Range After DefReb: " + scoreModel.getAwayHighThreePointAttempts() + " || " + scoreModel.getAwayLowThreePointAttempts());
            System.out.println("Away Field Goal Attempt Range After DefReb: " + scoreModel.getAwayHighFieldGoalAttempts() + " || " + scoreModel.getAwayLowFieldGoalAttempts());
        }
    }


    public static void factorOffensiveReboundsIntoScoringModel(ScoreModel scoreModel,double offensiveReboundFactor){

        double homeReboundDifferentialHigh = scoreModel.getAwayHighOffensiveRebounds() - scoreModel.getHomeOffensiveReboundScoringModelHigh();
        double homeReboundDifferentialLow = scoreModel.getAwayLowOffensiveRebounds() - scoreModel.getHomeOffensiveReboundScoringModelLow();

        double awayReboundDifferentialHigh = scoreModel.getHomeHighOffensiveRebounds() - scoreModel.getAwayOffensiveReboundScoringModelHigh();
        double awayReboundDifferentialLow = scoreModel.getHomeLowOffensiveRebounds() - scoreModel.getAwayOffensiveReboundScoringModelLow();

        double homeHighThreePointMix = scoreModel.getHomeHighThreePointAttempts() / (scoreModel.getHomeHighFieldGoalAttempts());
        double homeLowThreePointMix = scoreModel.getHomeLowThreePointAttempts() / (scoreModel.getHomeLowFieldGoalAttempts());
        double awayHighThreePointMix = scoreModel.getAwayHighThreePointAttempts() / (scoreModel.getAwayHighFieldGoalAttempts());
        double awayLowThreePointMix = scoreModel.getAwayLowThreePointAttempts() / (scoreModel.getAwayLowFieldGoalAttempts());


//        scoreModel.setAwayHighPoints(scoreModel.getAwayHighPoints() - ((awayTurnoverDifferentialHigh * lowerReboundFactor) * pointsReducedPerRebound));
//        scoreModel.setAwayLowPoints(scoreModel.getAwayLowPoints() - ((awayTurnoverDifferentialLow * upperReboundFactor) * pointsReducedPerRebound));
//        scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() - ((homeTurnoverDifferentialHigh * lowerReboundFactor) * pointsReducedPerRebound));
//        scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() - ((homeTurnoverDifferentialLow * upperReboundFactor) * pointsReducedPerRebound));

        scoreModel.setHomeHighThreePointersForecasted(scoreModel.getHomeHighThreePointersForecasted() - ((homeReboundDifferentialLow * homeHighThreePointMix) * offensiveReboundFactor));
        scoreModel.setHomeHighFieldGoalsForecasted(scoreModel.getHomeHighFieldGoalsForecasted() - ((homeReboundDifferentialLow * (1-homeHighThreePointMix)) * offensiveReboundFactor));
        scoreModel.setHomeLowThreePointersForecasted(scoreModel.getHomeLowThreePointersForecasted() - ((homeReboundDifferentialHigh * homeLowThreePointMix) * offensiveReboundFactor));
        scoreModel.setHomeLowFieldGoalsForecasted(scoreModel.getHomeLowFieldGoalsForecasted() - ((homeReboundDifferentialHigh * (1-homeLowThreePointMix)) * offensiveReboundFactor));

        scoreModel.setAwayHighThreePointersForecasted(scoreModel.getAwayHighThreePointersForecasted() - ((awayReboundDifferentialLow * awayHighThreePointMix) * offensiveReboundFactor));
        scoreModel.setAwayHighFieldGoalsForecasted(scoreModel.getAwayHighFieldGoalsForecasted() - ((awayReboundDifferentialLow * (1-awayHighThreePointMix)) * offensiveReboundFactor));
        scoreModel.setAwayLowThreePointersForecasted(scoreModel.getAwayLowThreePointersForecasted() - ((awayReboundDifferentialHigh * awayLowThreePointMix) * offensiveReboundFactor));
        scoreModel.setAwayLowFieldGoalsForecasted(scoreModel.getAwayLowFieldGoalsForecasted() - ((awayReboundDifferentialHigh * (1-awayLowThreePointMix)) * offensiveReboundFactor));

    }




    public static void factorTurnoversIntoScoringModel(ScoreModel scoreModel, double pointsReducedPerTurnover, double lowerTurnoverFactor,
                                                       double upperTurnoverFactor){

        double homeTurnoverDifferentialHigh = scoreModel.getHomeTurnoverScoringModelHigh() - scoreModel.getAwayHighTurnovers();
        double homeTurnoverDifferentialLow = scoreModel.getHomeTurnoverScoringModelLow() - scoreModel.getAwayLowTurnovers();
        double awayTurnoverDifferentialHigh = scoreModel.getAwayTurnoverScoringModelHigh() - scoreModel.getHomeHighTurnovers();
        double awayTurnoverDifferentialLow = scoreModel.getAwayTurnoverScoringModelLow() - scoreModel.getHomeLowTurnovers();


        double homeHighThreePointMix = scoreModel.getHomeHighThreePointAttempts() / (scoreModel.getHomeHighFieldGoalAttempts());
        double homeLowThreePointMix = scoreModel.getHomeLowThreePointAttempts() / (scoreModel.getHomeLowFieldGoalAttempts());
        double awayHighThreePointMix = scoreModel.getAwayHighThreePointAttempts() / (scoreModel.getAwayHighFieldGoalAttempts());
        double awayLowThreePointMix = scoreModel.getAwayLowThreePointAttempts() / (scoreModel.getAwayLowFieldGoalAttempts());


//        scoreModel.setAwayHighPoints(scoreModel.getAwayHighPoints() - ((awayTurnoverDifferentialLow * lowerTurnoverFactor) * pointsReducedPerTurnover));
//        scoreModel.setAwayLowPoints(scoreModel.getAwayLowPoints() - ((awayTurnoverDifferentialHigh * upperTurnoverFactor) * pointsReducedPerTurnover));
//        scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() - ((homeTurnoverDifferentialLow * lowerTurnoverFactor) * pointsReducedPerTurnover));
//        scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() - ((homeTurnoverDifferentialHigh * upperTurnoverFactor) * pointsReducedPerTurnover));



        scoreModel.setHomeHighThreePointersForecasted(scoreModel.getHomeHighThreePointersForecasted() - (homeTurnoverDifferentialLow * homeHighThreePointMix));
        scoreModel.setHomeHighFieldGoalsForecasted(scoreModel.getHomeHighFieldGoalsForecasted() - (homeTurnoverDifferentialLow * (1-homeHighThreePointMix)));
        scoreModel.setHomeLowThreePointersForecasted(scoreModel.getHomeLowThreePointersForecasted() - (homeTurnoverDifferentialHigh * homeLowThreePointMix));
        scoreModel.setHomeLowFieldGoalsForecasted(scoreModel.getHomeLowFieldGoalsForecasted() - (homeTurnoverDifferentialHigh * (1-homeLowThreePointMix)));

        scoreModel.setAwayHighThreePointersForecasted(scoreModel.getAwayHighThreePointersForecasted() - (awayTurnoverDifferentialLow * awayHighThreePointMix));
        scoreModel.setAwayHighFieldGoalsForecasted(scoreModel.getAwayHighFieldGoalsForecasted() - (awayTurnoverDifferentialLow * (1-awayHighThreePointMix)));
        scoreModel.setAwayLowThreePointersForecasted(scoreModel.getAwayLowThreePointersForecasted() - (awayTurnoverDifferentialHigh * awayLowThreePointMix));
        scoreModel.setAwayLowFieldGoalsForecasted(scoreModel.getAwayLowFieldGoalsForecasted() - (awayTurnoverDifferentialHigh * (1-awayLowThreePointMix)));

        //ADD POSSESSIONS BACK TO TEAM THAT STOLE BALL.
        scoreModel.setHomeHighThreePointersForecasted(scoreModel.getHomeHighThreePointersForecasted() + (awayTurnoverDifferentialHigh * homeHighThreePointMix));
        scoreModel.setHomeHighFieldGoalsForecasted(scoreModel.getHomeHighFieldGoalsForecasted() + (awayTurnoverDifferentialHigh * (1-homeHighThreePointMix)));
        scoreModel.setHomeLowThreePointersForecasted(scoreModel.getHomeLowThreePointersForecasted() + (awayTurnoverDifferentialLow * homeLowThreePointMix));
        scoreModel.setHomeLowFieldGoalsForecasted(scoreModel.getHomeLowFieldGoalsForecasted() + (awayTurnoverDifferentialLow * (1-homeLowThreePointMix)));

        scoreModel.setAwayHighThreePointersForecasted(scoreModel.getAwayHighThreePointersForecasted() + (homeTurnoverDifferentialHigh * awayHighThreePointMix));
        scoreModel.setAwayHighFieldGoalsForecasted(scoreModel.getAwayHighFieldGoalsForecasted() + (homeTurnoverDifferentialHigh * (1-awayHighThreePointMix)));
        scoreModel.setAwayLowThreePointersForecasted(scoreModel.getAwayLowThreePointersForecasted() + (homeTurnoverDifferentialLow * awayLowThreePointMix));
        scoreModel.setAwayLowFieldGoalsForecasted(scoreModel.getAwayLowFieldGoalsForecasted() + (homeTurnoverDifferentialLow * (1-awayLowThreePointMix)));


    }




    public static void addHomeTeamAdvantage(ScoreModel scoreModel, double homeTeamAdvantage ){


        scoreModel.setHomeHighPoints(scoreModel.getHomeHighPoints() + homeTeamAdvantage);
        scoreModel.setHomeLowPoints(scoreModel.getHomeLowPoints() + homeTeamAdvantage);
        //System.out.println("After turnovers Differential Range : " + (scoreModel.getHomeHighPoints() + scoreModel.getAwayHighPoints()) + " || " + (scoreModel.getHomeLowPoints() + scoreModel.getAwayLowPoints()));
    }
}
