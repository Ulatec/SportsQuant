package BaseballQuant;

import BaseballQuant.Model.*;
import BaseballQuant.Repository.*;
import BaseballQuant.Threads.BackTestThread;
import BaseballQuant.Threads.ThreadMonitor;
import BaseballQuant.Threads.TodaysGamesThread;
import BaseballQuant.Util.GameFinder;
import BaseballQuant.Util.ListSplitter;
import SportsQuant.Model.GameResult;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

@Component
public class BackTest implements ApplicationRunner {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private MLBGameOddsRepository gameOddsRepository;
    @Autowired
    private PitcherRepository pitcherRepository;


    @Override
    public void run(ApplicationArguments args) throws Exception{
        System.out.println("max memory:" + Runtime.getRuntime().maxMemory());
        System.out.println("total memory:" +Runtime.getRuntime().totalMemory());
        System.out.println("free memory:" + Runtime.getRuntime().freeMemory());
        List<MLBGameOdds> mlbGameOdds = (List<MLBGameOdds>) gameOddsRepository.findAll();

        List<MLBGame> games = (List<MLBGame>) gameRepository.findAll();
        HashMap<Integer, MLBGameOdds> mlbGameOddsHashMap = new HashMap<>();
        for(MLBGameOdds mlbGameOdds1 : mlbGameOdds){
            mlbGameOddsHashMap.put(mlbGameOdds1.getGameId(), mlbGameOdds1);
        }
        games.sort(Comparator.comparing(MLBGame::getDate).reversed());


        boolean todaysGames = false;
        boolean useStaticParams = false;

        //LocalDate startDate = LocalDate.of(2017, 6,7);
        LocalDate startDate = LocalDate.of(2024, 9,24);
        int gamesToTest = 2500;
        int threads = 12;
        double[] betSize = new double[]{0.06};
        boolean accuracyTesting = false;
        boolean forward = false;
        String spreadMode = "moneyline";
        boolean modelStolenBases = true;
        boolean modelOpposingFielding = true;
       // boolean[] modelOpponentPitchingToTest = new boolean[]{true};
        boolean[] doubleSquareRootToTest = new boolean[]{false};
        boolean[] squareRootTotalPointsToTest = new boolean[]{false};
        boolean[] allowLowEndBelowZero = new boolean[] {false};
//        //TODO::MONEYLINE TESTS
        int[] gameCountsToTest = new int[]{2,3,4,5};
        int[] pitcherGameLookbackToTest = new int[]{2,3,4,5};
        int[] bullpenGameLookbackToTest = new int[]{65};
        double[] d1List = new double[]{0.25};
        double[] d2List = new double[]{0.25};
        double[] d3List = new double[]{0.25};
        double[] d4List = new double[]{0.25};
        double[] d5List = new double[]{0.25};
        double[] d6List = new double[]{0.25};
        double[] d7List = new double[]{0.25};
        double[] d8List = new double[]{0.5};
        double[] d9List = new double[]{0.5};
        double[] d10List = new double[]{0.5};
        double[] d11List = new double[]{0.5};
        double[] d12List = new double[]{0.5};
        double[] deez = new double[] {20};
        double[] highRunFactor = new double[]{0.5};
        double[] lowRunFactor = new double[]{0.9};
        double[] highFieldingFactor = new double[]{-0.85};
        double[] lowFieldingFactor = new double[]{0.6};
        double[] highStolenBaseFactor = new double[]{0.6};
        double[] lowStolenBaseFactor = new double[]{1.5};
        double[] homeAdvantageHigh = new double[]{-0.1};
        double[] homeAdvantageLow = new double[]{0};
        double[] homeRunsGivenUpRocThreshold = new double[]{1};
        double[] AwayRunsGivenUpRocThreshold = new double[]{1};
        double[] homeRunsScoredRocThreshold = new double[]{1};
        double[] AwayRunsScoredocThreshold = new double[]{1};
        double[] homeFieldRocThreshold = new double[]{1};
        double[] awayFieldRocThreshold = new double[]{1};
        double[] homeStolenBasesRocThreshold = new double[]{-0.000001};
        double[] awayStolenBasesRocThreshold = new double[]{-0.000001};
        double[] pointThresholds = new double[]{0};
        boolean[] homeRunsGivenUpRocFlip = new boolean[]{true,};
        boolean[] awayRunsGivenUpRocFlip = new boolean[]{false,true,};
        boolean[] homeRunsScoredRocFlip = new boolean[]{false,true,};
        boolean[] awayRunsScoredRocFlip = new boolean[]{false,true,};
        boolean[] homeFieldingRocFlip = new boolean[]{false,};
        boolean[] awayFieldingRocFlip = new boolean[]{false,true,};
        boolean[] homeStolenBasesRocFlip = new boolean[]{false,};
        boolean[] awayStolenBasesRocFlip = new boolean[]{false,};
        boolean[] enable1 = new boolean[]{true};
        boolean[] enable2 = new boolean[]{true,};
        boolean[] enable3 = new boolean[]{true,};
        boolean[] enable4 = new boolean[]{true,};
        boolean[] enable5 = new boolean[]{true,};
        boolean[] enable6 = new boolean[]{true,};
        boolean[] enable7 = new boolean[]{true,};
        boolean[] enable8 = new boolean[]{true,};
        boolean[] enable9 = new boolean[]{true,};
        boolean[] enable10 = new boolean[]{true,};
        boolean[] enable11 = new boolean[]{true,};
        boolean[] enable12 = new boolean[]{true,};
        boolean[] enable13 = new boolean[]{false};
        boolean[] enable14 = new boolean[]{false,};
        boolean[] enable15 = new boolean[]{false,};
        boolean[] flip1 = new boolean[]{false,};
        boolean[] flip2 = new boolean[]{false,true,};
        boolean[] flip3 = new boolean[]{false,true,};
        boolean[] flip4 = new boolean[]{false,};
        boolean[] flip5 = new boolean[]{false,};
        boolean[] flip6 = new boolean[]{false,};

        //TODO: SPREAD
//        int[] gameCountsToTest = new int[]{7};
//        int[] pitcherGameLookbackToTest = new int[]{13};
//        int[] bullpenGameLookbackToTest = new int[]{65};
//        double[] d1List = new double[]{1};
//        double[] d2List = new double[]{1};
//        double[] d3List = new double[]{0};
//        double[] d4List = new double[]{0};
//        double[] d5List = new double[]{0};
//        double[] d6List = new double[]{1};
//        double[] d7List = new double[]{0.5};
//        double[] d8List = new double[]{0.5};
//        double[] d9List = new double[]{0};
//        double[] d10List = new double[]{1};
//        double[] d11List = new double[]{0.5};
//        double[] d12List = new double[]{0};
//        double[] deez = new double[] {4};
//        double[] highRunFactor = new double[]{0.5};
//        double[] lowRunFactor = new double[]{0.9};
//        double[] highFieldingFactor = new double[]{-0.85};
//        double[] lowFieldingFactor = new double[]{0.6};
//        double[] highStolenBaseFactor = new double[]{0.6};
//        double[] lowStolenBaseFactor = new double[]{1.5};
//        double[] homeAdvantageHigh = new double[]{-0.1};
//        double[] homeAdvantageLow = new double[]{0};
//        double[] homeRunsGivenUpRocThreshold = new double[]{1};
//        double[] AwayRunsGivenUpRocThreshold = new double[]{1};
//        double[] homeRunsScoredRocThreshold = new double[]{1};
//        double[] AwayRunsScoredocThreshold = new double[]{1};
//        double[] homeFieldRocThreshold = new double[]{1};
//        double[] awayFieldRocThreshold = new double[]{1};
//        double[] homeStolenBasesRocThreshold = new double[]{-0.000001};
//        double[] awayStolenBasesRocThreshold = new double[]{-0.000001};
//        double[] pointThresholds = new double[]{0};
//        boolean[] homeRunsGivenUpRocFlip = new boolean[]{true};
//        boolean[] awayRunsGivenUpRocFlip = new boolean[]{false};
//        boolean[] homeRunsScoredRocFlip = new boolean[]{false};
//        boolean[] awayRunsScoredRocFlip = new boolean[]{false};
//        boolean[] homeFieldingRocFlip = new boolean[]{false};
//        boolean[] awayFieldingRocFlip = new boolean[]{false};
//        boolean[] homeStolenBasesRocFlip = new boolean[]{false,};
//        boolean[] awayStolenBasesRocFlip = new boolean[]{false};
//        boolean[] enable1 = new boolean[]{true};
//        boolean[] enable2 = new boolean[]{true,};
//        boolean[] enable3 = new boolean[]{true,};
//        boolean[] enable4 = new boolean[]{true,};
//        boolean[] enable5 = new boolean[]{true,};
//        boolean[] enable6 = new boolean[]{true};
//        boolean[] enable7 = new boolean[]{true};
//        boolean[] enable8 = new boolean[]{true,};
//        boolean[] enable9 = new boolean[]{true,};
//        boolean[] enable10 = new boolean[]{true,};
//        boolean[] enable11 = new boolean[]{true,};
//        boolean[] enable12 = new boolean[]{true,};
//        boolean[] enable13 = new boolean[]{true};
//        boolean[] enable14 = new boolean[]{true};
//        boolean[] enable15 = new boolean[]{true,};
//        boolean[] flip1 = new boolean[]{false,};
//        boolean[] flip2 = new boolean[]{false,};
//        boolean[] flip3 = new boolean[]{false,};
//        boolean[] flip4 = new boolean[]{false,};
//        boolean[] flip5 = new boolean[]{false};
//        boolean[] flip6 = new boolean[]{false};


        //TODO::OVERUNDER TESTS
//        int[] gameCountsToTest = new int[]{7};
//        int[] pitcherGameLookbackToTest = new int[]{13};
//        int[] bullpenGameLookbackToTest = new int[]{65};
//        double[] d1List = new double[]{0};
//        double[] d2List = new double[]{0};
//        double[] d3List = new double[]{0};
//        double[] d4List = new double[]{0};
//        double[] d5List = new double[]{0};
//        double[] d6List = new double[]{0};
//        double[] d7List = new double[]{0};
//        double[] d8List = new double[]{0,};
//        double[] d9List = new double[]{1};
//        double[] d10List = new double[]{1};
//        double[] d11List = new double[]{1};
//        double[] d12List = new double[]{1};
//        double[] deez = new double[] {3};
//        double[] highRunFactor = new double[]{0.5};
//        double[] lowRunFactor = new double[]{0.9};
//        double[] highFieldingFactor = new double[]{-0.85};
//        double[] lowFieldingFactor = new double[]{0.6};
//        double[] highStolenBaseFactor = new double[]{0.6};
//        double[] lowStolenBaseFactor = new double[]{1.5};
//        double[] homeAdvantageHigh = new double[]{-0.1};
//        double[] homeAdvantageLow = new double[]{0};
//        double[] homeRunsGivenUpRocThreshold = new double[]{1};
//        double[] AwayRunsGivenUpRocThreshold = new double[]{1};
//        double[] homeRunsScoredRocThreshold = new double[]{1};
//        double[] AwayRunsScoredocThreshold = new double[]{1};
//        double[] homeFieldRocThreshold = new double[]{1};
//        double[] awayFieldRocThreshold = new double[]{1};
//        double[] homeStolenBasesRocThreshold = new double[]{-0.000001};
//        double[] awayStolenBasesRocThreshold = new double[]{-0.000001};
//        double[] pointThresholds = new double[]{0};
//        boolean[] homeRunsGivenUpRocFlip = new boolean[]{false};
//        boolean[] awayRunsGivenUpRocFlip = new boolean[]{false};
//        boolean[] homeRunsScoredRocFlip = new boolean[]{false};
//        boolean[] awayRunsScoredRocFlip = new boolean[]{false};
//        boolean[] homeFieldingRocFlip = new boolean[]{false};
//        boolean[] awayFieldingRocFlip = new boolean[]{false};
//        boolean[] homeStolenBasesRocFlip = new boolean[]{false,};
//        boolean[] awayStolenBasesRocFlip = new boolean[]{false};
//        boolean[] enable1 = new boolean[]{true};
//        boolean[] enable2 = new boolean[]{true,};
//        boolean[] enable3 = new boolean[]{false,};
//        boolean[] enable4 = new boolean[]{true,};
//        boolean[] enable5 = new boolean[]{true,};
//        boolean[] enable6 = new boolean[]{false};
//        boolean[] enable7 = new boolean[]{true,};
//        boolean[] enable8 = new boolean[]{false,};
//        boolean[] enable9 = new boolean[]{true,};
//        boolean[] enable10 = new boolean[]{true,};
//        boolean[] enable11 = new boolean[]{true,};
//        boolean[] enable12 = new boolean[]{true,};
//        boolean[] enable13 = new boolean[]{true};
//        boolean[] enable14 = new boolean[]{true};
//        boolean[] enable15 = new boolean[]{true,};
////
//        boolean[] flip1 = new boolean[]{false};
//        boolean[] flip2 = new boolean[]{true};
//        boolean[] flip3 = new boolean[]{true};
//        boolean[] flip4 = new boolean[]{true};
//        boolean[] flip5 = new boolean[]{true};
//        boolean[] flip6 = new boolean[]{true};





        List<BackTestIngestObject> scoreModelIngestObjects = new ArrayList<>();
                List<BackTestIngestObject> backTestIngestObjects = new ArrayList<>();
        for(int gameCounts : gameCountsToTest) {
            for (boolean doubleSquareRoot : doubleSquareRootToTest) {
                for(double size: betSize){
                   // for (int pitchGameLookBack : pitcherGameLookbackToTest) {
                            for (boolean squareRootTotalPoints : squareRootTotalPointsToTest) {
                                for (int bullpenGameLookback : bullpenGameLookbackToTest) {
                                    for (double highRun : highRunFactor) {
                                        for (double lowRun : lowRunFactor) {
                                            for (double highFielding : highFieldingFactor) {
                                                for (double lowFielding : lowFieldingFactor) {
                                                    for (double highSteal : highStolenBaseFactor) {
                                                        for (double lowSteal : lowStolenBaseFactor) {
                                                            for(boolean allowBelowZero : allowLowEndBelowZero) {
                                                                for(double homeHigh : homeAdvantageHigh) {
                                                                    for(double homeLow : homeAdvantageLow) {
                                                                        for(double pointThreshold : pointThresholds) {
                                                                            for (double homeRunsGivenUp : homeRunsGivenUpRocThreshold) {
                                                                                for(double awayRunsGivenUp : AwayRunsGivenUpRocThreshold) {
                                                                                    for(double homeRunsScoredRoc : homeRunsScoredRocThreshold) {
                                                                                        for(double awayRunsScoredRoc : AwayRunsScoredocThreshold) {
                                                                                            for(double  homeFieldingRoc : homeFieldRocThreshold) {
                                                                                                for(double  awayFieldingRoc : awayFieldRocThreshold) {
                                                                                                    for(double homeBasesStolen : homeStolenBasesRocThreshold){
                                                                                                        for(double awayBasesStolen : awayStolenBasesRocThreshold) {
                                                                                                            for(boolean homeRunsGiveUpFlip : homeRunsGivenUpRocFlip) {
                                                                                                                for(boolean awayRunsGivenUpFlip : awayRunsGivenUpRocFlip) {
                                                                                                                    for(boolean homeRunsScoreFlip : homeRunsScoredRocFlip) {
                                                                                                                        for(boolean awayRunsScoreFlip : awayRunsScoredRocFlip) {
                                                                                                                            for(boolean homeFieldingFlip : homeFieldingRocFlip) {
                                                                                                                                for(boolean awayFieldingFlip : awayFieldingRocFlip) {
                                                                                                                                    for(boolean homeStolenBaseFlip : homeStolenBasesRocFlip) {
                                                                                                                                        for(boolean awayStolenBaseFlip : awayStolenBasesRocFlip) {
                                                                                                                                            for(boolean one : enable1) {
                                                                                                                                                for(boolean two : enable2) {
                                                                                                                                                    for (boolean three : enable3) {
                                                                                                                                                        for (boolean four : enable4) {
                                                                                                                                                            for (boolean five : enable5) {
                                                                                                                                                                for (boolean six : enable6) {
                                                                                                                                                                    for(boolean seven : enable7) {
                                                                                                                                                                        for(boolean eight : enable8) {
                                                                                                                                                                            for(boolean nine : enable9) {
                                                                                                                                                                                for (boolean ten : enable10) {
                                                                                                                                                                                    for (boolean eleven : enable11) {
                                                                                                                                                                                        for (boolean twelve : enable12) {
                                                                                                                                                                                            for (boolean thirteen : enable13) {
                                                                                                                                                                                                for (boolean fourteen : enable14) {
                                                                                                                                                                                                    for (boolean fifteen : enable15) {

                                                                                                                                                                                                for (boolean f1 : flip1) {
                                                                                                                                                                                                    for (boolean f2 : flip2) {
                                                                                                                                                                                                        for (boolean f3 : flip3) {
                                                                                                                                                                                                            for (boolean f4 : flip4) {
                                                                                                                                                                                                                for (boolean f5 : flip5) {
                                                                                                                                                                                                                    for (boolean f6 : flip6) {
                                                                                                                                                                                                                        for(double d1 : d1List){
                                                                                                                                                                                                                            for(double d2 : d2List) {
                                                                                                                                                                                                                                for (double d3 : d3List) {
                                                                                                                                                                                                                                    for (double d4 : d4List) {
                                                                                                                                                                                                                                        for (double d5 : d5List) {
                                                                                                                                                                                                                                            for (double d6 : d6List) {
                                                                                                                                                                                                                                                for (double d7 : d7List) {
                                                                                                                                                                                                                                                    for (double d8 : d8List) {
                                                                                                                                                                                                                                                        for (double d9 : d9List) {
                                                                                                                                                                                                                                                            for (double d10 : d10List) {
                                                                                                                                                                                                                                                                for (double d11 : d11List) {
                                                                                                                                                                                                                                                                    for (double d12 : d12List) {
                                                                                                                                                                                                                                                                        for(double dee : deez){
                                                                                                                                                                                                                                                                    if (!one && homeRunsGiveUpFlip) {
                                                                                                                                                                                                                                                                    } else if (!two && awayRunsGivenUpFlip) {
                                                                                                                                                                                                                                                                    } else if (!three && homeRunsScoreFlip) {
                                                                                                                                                                                                                                                                    } else if (!four && awayRunsScoreFlip) {
                                                                                                                                                                                                                                                                    } else if (!five && homeFieldingFlip) {
                                                                                                                                                                                                                                                                    } else if (!six && awayFieldingFlip) {
                                                                                                                                                                                                                                                                    } else if (!seven && homeStolenBaseFlip) {
                                                                                                                                                                                                                                                                    } else if (!eight && awayStolenBaseFlip) {
                                                                                                                                                                                                                                                                    } else if (!nine && allowBelowZero) {
                                                                                                                                                                                                                                                                    } else if (!ten && f1) {
                                                                                                                                                                                                                                                                    } else if (!eleven && f2) {
                                                                                                                                                                                                                                                                    } else if (!twelve && f3) {
                                                                                                                                                                                                                                                                    } else if (!thirteen && f4) {
                                                                                                                                                                                                                                                                    } else if (!fourteen && f5) {
                                                                                                                                                                                                                                                                    } else if (!fifteen && f6) {
                                                                                                                                                                                                                                                                    } else {
                                                                                                                                                                                                                                                                        BackTestIngestObject backTestIngestObject = new BackTestIngestObject();
                                                                                                                                                                                                                                                                        backTestIngestObject.setGameCount(gameCounts);
                                                                                                                                                                                                                                                                        backTestIngestObject.setDoubleSquareRoot(doubleSquareRoot);
                                                                                                                                                                                                                                                                        backTestIngestObject.setPitcherGameLookback(gameCounts);
                                                                                                                                                                                                                                                                        backTestIngestObject.setSquareRootTotalPoints(squareRootTotalPoints);
                                                                                                                                                                                                                                                                        backTestIngestObject.setBullpenGameCount(bullpenGameLookback);
                                                                                                                                                                                                                                                                        backTestIngestObject.setModelOpposingFielding(modelOpposingFielding);
                                                                                                                                                                                                                                                                        backTestIngestObject.setModelStolenBases(modelStolenBases);
                                                                                                                                                                                                                                                                        backTestIngestObject.setBetType(spreadMode);
                                                                                                                                                                                                                                                                        backTestIngestObject.setAllowLowEndBelowZero(allowBelowZero);
                                                                                                                                                                                                                                                                        backTestIngestObject.setStartDate(startDate);
                                                                                                                                                                                                                                                                        backTestIngestObject.setGamesToTest(gamesToTest);

                                                                                                                                                                                                                                                                        backTestIngestObject.setHomeRunsGivenUpRocFlip(homeRunsGiveUpFlip);
                                                                                                                                                                                                                                                                        backTestIngestObject.setAwayRunsGivenUpRocFlip(awayRunsGivenUpFlip);
                                                                                                                                                                                                                                                                        backTestIngestObject.setHomeRunsScoredRocFlip(homeRunsScoreFlip);
                                                                                                                                                                                                                                                                        backTestIngestObject.setAwayRunsScoredocFlip(awayRunsScoreFlip);
                                                                                                                                                                                                                                                                        backTestIngestObject.setHomeFieldingRocFlip(homeFieldingFlip);
                                                                                                                                                                                                                                                                        backTestIngestObject.setAwayFieldingRocFlip(awayFieldingFlip);
                                                                                                                                                                                                                                                                        backTestIngestObject.setHomeStolenBasesRocFlip(homeStolenBaseFlip);
                                                                                                                                                                                                                                                                        backTestIngestObject.setAwayStolenBasesRocFlip(awayStolenBaseFlip);

                                                                                                                                                                                                                                                                        backTestIngestObject.setEnable1(one);
                                                                                                                                                                                                                                                                        backTestIngestObject.setEnable2(two);
                                                                                                                                                                                                                                                                        backTestIngestObject.setEnable3(three);
                                                                                                                                                                                                                                                                        backTestIngestObject.setEnable4(four);
                                                                                                                                                                                                                                                                        backTestIngestObject.setEnable5(five);
                                                                                                                                                                                                                                                                        backTestIngestObject.setEnable6(six);
                                                                                                                                                                                                                                                                        backTestIngestObject.setEnable7(seven);
                                                                                                                                                                                                                                                                        backTestIngestObject.setEnable8(eight);
                                                                                                                                                                                                                                                                        backTestIngestObject.setEnable9(nine);
                                                                                                                                                                                                                                                                        backTestIngestObject.setEnable10(ten);
                                                                                                                                                                                                                                                                        backTestIngestObject.setEnable11(eleven);
                                                                                                                                                                                                                                                                        backTestIngestObject.setEnable12(twelve);
                                                                                                                                                                                                                                                                        backTestIngestObject.setEnable13(thirteen);
                                                                                                                                                                                                                                                                        backTestIngestObject.setEnable14(fourteen);
                                                                                                                                                                                                                                                                        backTestIngestObject.setEnable15(fifteen);


                                                                                                                                                                                                                                                                        backTestIngestObject.setFlip1(f1);
                                                                                                                                                                                                                                                                        backTestIngestObject.setFlip2(f2);
                                                                                                                                                                                                                                                                        backTestIngestObject.setFlip3(f3);
                                                                                                                                                                                                                                                                        backTestIngestObject.setFlip4(f4);
                                                                                                                                                                                                                                                                        backTestIngestObject.setFlip5(f5);
                                                                                                                                                                                                                                                                        backTestIngestObject.setFlip6(f6);

                                                                                                                                                                                                                                                                        backTestIngestObject.setD1(d1);
                                                                                                                                                                                                                                                                        backTestIngestObject.setD2(d2);
                                                                                                                                                                                                                                                                        backTestIngestObject.setD3(d3);
                                                                                                                                                                                                                                                                        backTestIngestObject.setD4(d4);
                                                                                                                                                                                                                                                                        backTestIngestObject.setD5(d5);
                                                                                                                                                                                                                                                                        backTestIngestObject.setD6(d6);
                                                                                                                                                                                                                                                                        backTestIngestObject.setD7(d7);
                                                                                                                                                                                                                                                                        backTestIngestObject.setD8(d8);
                                                                                                                                                                                                                                                                        backTestIngestObject.setD9(d9);
                                                                                                                                                                                                                                                                        backTestIngestObject.setD10(d10);
                                                                                                                                                                                                                                                                        backTestIngestObject.setD11(d11);
                                                                                                                                                                                                                                                                        backTestIngestObject.setD12(d12);
                                                                                                                                                                                                                                                                        backTestIngestObject.setDeez(dee);
                                                                                                                                                                                                                                                                        backTestIngestObject.setBetSize(size);
                                                                                                                                                                                                                                                                        backTestIngestObjects.add(backTestIngestObject);
                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                }
                                                                                                                                                                                                                            }
                                                                                                                                                                                                                        }
                                                                                                                                                                                                                    }
                                                                                                                                                                                                                }
                                                                                                                                                                                                            }
                                                                                                                                                                                                        }
                                                                                                                                                                                                            }
                                                                                                                                                                                                        }
                                                                                                                                                                                                    }
                                                                                                                                                                                                }
                                                                                                                                                                                            }
                                                                                                                                                                                        }
                                                                                                                                                                                    }
                                                                                                                                                                                }
                                                                                                                                                                            }
                                                                                                                                                                        }
                                                                                                                                                                    }
                                                                                                                                                                }
                                                                                                                                                            }
                                                                                                                                                        }
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                        }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                    }

            }
        }

//            if(!backTestIngestObject.isEnable9() && backTestIngestObject.isAllowLowEndBelowZero()){
//                backTestIngestObjectIterator.remove();
//            }
//        }

        for(Integer gameCount : gameCountsToTest){
            for(Integer pitcherGameCount : pitcherGameLookbackToTest){
                for(Integer bullpenGameCount : bullpenGameLookbackToTest){
                    for(boolean belowZero : allowLowEndBelowZero){
                        for(boolean dblSqRt : doubleSquareRootToTest) {
                            BackTestIngestObject scoreModelIngestObject = new BackTestIngestObject();
                            scoreModelIngestObject.setGameCount(gameCount);
                            scoreModelIngestObject.setPitcherGameLookback(pitcherGameCount);
                            scoreModelIngestObject.setBullpenGameCount(bullpenGameCount);
                            scoreModelIngestObject.setDoubleSquareRoot(dblSqRt);
                            scoreModelIngestObject.setAllowLowEndBelowZero(belowZero);
                            scoreModelIngestObject.setStartDate(startDate);
                            scoreModelIngestObject.setGamesToTest(gamesToTest);
                            scoreModelIngestObject.setBetType(spreadMode);
                            scoreModelIngestObjects.add(scoreModelIngestObject);
                        }
                    }
                }
            }
        }
//            GameFinder gameFinder = new GameFinder();
//            gameFinder.setPitcherRepository(pitcherRepository);
//            List<MLBGame> gameList =  gameFinder.findTodaysGames(LocalDate.now());
//            List<CaesarsObject> caesarsObjectList = findGameOdds(LocalDate.now());
//            System.out.println(caesarsObjectList);
//            attemptToPairCaesarsObjectsToMLBGames(gameList, caesarsObjectList);
//            //System.out.println(gameList);
//            List<GameResult> gameResults = new ArrayList<>();
//            for(MLBGame game : gameList){
//                GameResult gameResult = new GameResult();
//                gameResult.setGameId(game.getGameId());
//                gameResult.setAwayTeamName(game.getAwayTeamName());
//                gameResult.setHomeTeamName(game.getHomeTeamName());
//                gameResult.setDate(game.getDate().toString());
//                gameResults.add(gameResult);
//            }
//            TodaysGamesThread todaysGamesThread = new TodaysGamesThread(gameResults, games, gameList);
//            todaysGamesThread.setBackTestIngestObjects(backTestIngestObjects);
//            todaysGamesThread.setMlbGameOdds(mlbGameOdds);
//            todaysGamesThread.start();
//            List<List<BackTestIngestObject>> listOfScoreModelLists = ListSplitter.split(scoreModelIngestObjects, threads);
//
//            ThreadMonitor scoreModelMonitor = new ThreadMonitor(threads);
//            scoreModelMonitor.setCacheBuilding(true);
//
//            for (int i = 0; i < listOfScoreModelLists.size(); i++) {
//                //System.out.println("creating thread "+ i);
//                BackTestThread backTestThread = new BackTestThread(listOfScoreModelLists.get(i), mlbGameOddsHashMap, games, scoreModelMonitor);
//                backTestThread.setThreadNum(i);
//                backTestThread.setCaching(true);
//                backTestThread.setForward(forward);
//                //backTestThread.setScrapingProxy((ScrapingProxy)proxies.toArray()[i]);
//                backTestThread.start();
//            }
//            scoreModelMonitor.start();
//
//            while(scoreModelMonitor.getGameCache() == null){
//
//            }
//            HashMap<CacheSettingsObject, HashMap<Integer, ScoreModel>> gameCache = scoreModelMonitor.getGameCache();


            System.out.println("Tests to Run: " + backTestIngestObjects.size());
            if(useStaticParams){
                threads = 1;
            }
            ThreadMonitor threadMonitor = new ThreadMonitor(threads);
            threadMonitor.setCacheBuilding(false);
            List<List<BackTestIngestObject>> listOfLists = ListSplitter.split(backTestIngestObjects, threads);

//            for(List<BackTestIngestObject> list : listOfLists){
//                System.out.println(list.size());
//                System.out.println(list);
//            }

            int size = listOfLists.size();
            if(useStaticParams){
                size = 1;
            }
            if(todaysGames) {
                GameFinder gameFinder = new GameFinder();
                gameFinder.setPitcherRepository(pitcherRepository);
                List<MLBGame> gameList = gameFinder.findTodaysGames(LocalDate.now());
                List<CaesarsObject> caesarsObjectList = findGameOdds(LocalDate.now());
                System.out.println(caesarsObjectList);
                attemptToPairCaesarsObjectsToMLBGames(gameList, caesarsObjectList);
            //    games.addAll(gameList);
            }

            for (int i = 0; i < size; i++) {
                //System.out.println("creating thread "+ i);
                BackTestThread backTestThread = new BackTestThread(listOfLists.get(i), mlbGameOddsHashMap, games, threadMonitor);
                backTestThread.setUseStaticParams(useStaticParams);
                //backTestThread.setGameCache(gameCache);
                backTestThread.setThreadNum(i);
                backTestThread.setForward(forward);
                //backTestThread.setScrapingProxy((ScrapingProxy)proxies.toArray()[i]);
                backTestThread.start();
            }
            threadMonitor.start();

    }

    public static List<ScrapingProxy> getProxies(){
        List<ScrapingProxy> proxies = new ArrayList<>();
        proxies.add(new ScrapingProxy("107.175.43.68", 1163));
        proxies.add(new ScrapingProxy("107.173.72.103", 1284));
        proxies.add(new ScrapingProxy("64.44.18.189", 5117));
        proxies.add(new ScrapingProxy("194.76.139.97", 1581));
        proxies.add(new ScrapingProxy("67.227.35.152", 6909));
        return proxies;
    }
    public List<CaesarsObject> findGameOdds(LocalDate localDate) throws IOException {
        List<CaesarsObject> caesarsObjectList = new ArrayList<>();
        Connection.Response response = Jsoup.connect("https://www.sportsbookreview.com/betting-odds/" +
                        "mlb-baseball/pointspread/full-game/?date=" +convertLocalDate(localDate))
                .method(Connection.Method.GET)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 OPR/78.0.4093.153")
                .header("Connection", "keep-alive")
                .header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjkwMDk1YmM2ZGM2ZDY3NzkxZDdkYTFlZWIxYTU1OWEzZDViMmM0ODYiLCJ0eXAiOiJKV1QifQ.eyJ0ZW5hbnQiOiJjbG91ZGJldCIsInV1aWQiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20va3ViZXJzaG11YmVyLXByb2QtY2ItYXV0aCIsImF1ZCI6Imt1YmVyc2htdWJlci1wcm9kLWNiLWF1dGgiLCJhdXRoX3RpbWUiOjE2MzAwMzIwMDYsInVzZXJfaWQiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJzdWIiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJpYXQiOjE2MzAwNzI3ODksImV4cCI6MTYzMDA3NjM4OSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6e30sInNpZ25faW5fcHJvdmlkZXIiOiJjdXN0b20ifX0.D9tWV403lYk9EElG09UW3NZTENUmMkquTf_8y2-eTqqsS3bHcrs3tMHXiXVy2EtPY0zfukc__3pMIQHYrga6TtSdkQjIB3qTf-Biemk9T0vtIhyZ_ClEiFTi3OoQ8FWPBN66jc_5DsRdm0FAQ23TmEwUB7oqTDKRZN6yC_xcc3ksAFoUIRB_9KrDi-ofXAr0VqejB4xKLrtpypxvXgHVs_rrxpA3hWg6FEPhVxQhI3LFEmUOZsB8Dg6j-UAKXiS8rON4f_-MCKk0vJemkBgYFhcbjI1rf_hdmoBB0G8cpXTjJmHAaXzF8xtsl4sYyeOI1GJeiFxuHJK19hMwacfkuw")
                .header("Content-Type", "application/json")
                .header("x-player-timezone", "America/Chicago")
                .ignoreContentType(true)
                .maxBodySize(0)
                .execute();
        Document document = (Document) response.parse();
        Element element = document.getElementById("section-mlb").getElementById("tbody-mlb");
        Elements gameElements = element.select("div[class=d-flex flex-row flex-nowrap border position-relative mt-0 GameRows_eventMarketGridContainer__GuplK GameRows_neverWrap__gnQNO]");
        for(Element element1 : gameElements){
            CaesarsObject caesarsObject = new CaesarsObject();
            Elements test = element1.getElementsByClass("d-flex align-items-center overflow-hidden fs-9 GameRows_gradientContainer__ZajIf");
            String tricode1;
            String awayPitcher = "";
            Element teamElement = test.get(0);
            if(teamElement.childrenSize() > 1){
                tricode1 = test.get(0).getElementsByTag("a").get(0).getElementsByTag("b").get(0).text();
                awayPitcher = test.get(0).child(2).text().replace(" (R)", "").replace(" (L)", "");
            }else{
                tricode1 = test.get(0).getElementsByTag("a").text();
            }

            String tricode2;
            Element teamElement2 = test.get(1);
            String homePitcher = "";
            if(teamElement2.childrenSize() > 1){
                tricode2 = test.get(1).getElementsByTag("a").get(0).getElementsByTag("b").get(0).text();
                homePitcher = test.get(1).child(2).text().replace(" (R)", "").replace(" (L)", "");
            }else{
                tricode2 = test.get(1).getElementsByTag("a").text();
            }
            if(tricode1.equals("Washington")){
                tricode1 = "WAS";
            }
            if(tricode2.equals("Washington")){
                tricode2 = "WAS";
            }
            caesarsObject.setAwayTeamName(convertTriCode(tricode1));
            caesarsObject.setHomeTeamName(convertTriCode(tricode2));
            caesarsObject.setAwayPitcherName(awayPitcher);
            caesarsObject.setHomePitcherName(homePitcher);

            //get odds
            Elements test2 = element1.getElementsByClass("d-flex align-items-center text-center OddsCells_oddsNumber__u3rsp OddsCells_compact__cawia");

            //away odds
            String unParsedAwaySpread = test2.get(4).getElementsByTag("span").get(1).text();
            if(unParsedAwaySpread.equals("-") || unParsedAwaySpread.equals("")){
                unParsedAwaySpread = test2.get(6).getElementsByTag("span").get(1).text();
            }
            String unParsedAwayOdds = test2.get(4).getElementsByTag("span").get(2).text();
            if(unParsedAwayOdds.equals("-") || unParsedAwayOdds.equals("")){
                unParsedAwayOdds = test2.get(6).getElementsByTag("span").get(2).text();
            }
            String unParsedHomeSpread = test2.get(5).getElementsByTag("span").get(1).text();
            if(unParsedHomeSpread.equals("-") || unParsedHomeSpread.equals("")){
                unParsedHomeSpread = test2.get(7).getElementsByTag("span").get(1).text();
            }
            String unParsedHomeOdds = test2.get(5).getElementsByTag("span").get(2).text();
            if(unParsedHomeOdds.equals("-") || unParsedHomeOdds.equals("")){
                unParsedHomeOdds = test2.get(7).getElementsByTag("span").get(2).text();
            }
            if(!unParsedHomeSpread.equals("") && !unParsedAwaySpread.equals("")) {
                caesarsObject.setAwayTeamSpread(Double.parseDouble(unParsedAwaySpread));
                caesarsObject.setAwayTeamSpreadOdds(Double.parseDouble(unParsedAwayOdds));
                caesarsObject.setHomeTeamSpread(Double.parseDouble(unParsedHomeSpread));
                caesarsObject.setHomeTeamSpreadOdds(Double.parseDouble(unParsedHomeOdds));
                System.out.println(tricode1);


                caesarsObjectList.add(caesarsObject);
            }
        }

        //MONEYLINE
        Connection.Response responseML = Jsoup.connect("https://www.sportsbookreview.com/betting-odds/" +
                        "mlb-baseball/?date=" +convertLocalDate(localDate))
                .method(Connection.Method.GET)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 OPR/78.0.4093.153")
                .header("Connection", "keep-alive")
                .header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjkwMDk1YmM2ZGM2ZDY3NzkxZDdkYTFlZWIxYTU1OWEzZDViMmM0ODYiLCJ0eXAiOiJKV1QifQ.eyJ0ZW5hbnQiOiJjbG91ZGJldCIsInV1aWQiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20va3ViZXJzaG11YmVyLXByb2QtY2ItYXV0aCIsImF1ZCI6Imt1YmVyc2htdWJlci1wcm9kLWNiLWF1dGgiLCJhdXRoX3RpbWUiOjE2MzAwMzIwMDYsInVzZXJfaWQiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJzdWIiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJpYXQiOjE2MzAwNzI3ODksImV4cCI6MTYzMDA3NjM4OSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6e30sInNpZ25faW5fcHJvdmlkZXIiOiJjdXN0b20ifX0.D9tWV403lYk9EElG09UW3NZTENUmMkquTf_8y2-eTqqsS3bHcrs3tMHXiXVy2EtPY0zfukc__3pMIQHYrga6TtSdkQjIB3qTf-Biemk9T0vtIhyZ_ClEiFTi3OoQ8FWPBN66jc_5DsRdm0FAQ23TmEwUB7oqTDKRZN6yC_xcc3ksAFoUIRB_9KrDi-ofXAr0VqejB4xKLrtpypxvXgHVs_rrxpA3hWg6FEPhVxQhI3LFEmUOZsB8Dg6j-UAKXiS8rON4f_-MCKk0vJemkBgYFhcbjI1rf_hdmoBB0G8cpXTjJmHAaXzF8xtsl4sYyeOI1GJeiFxuHJK19hMwacfkuw")
                .header("Content-Type", "application/json")
                .header("x-player-timezone", "America/Chicago")
                .ignoreContentType(true)
                .maxBodySize(0)
                .execute();
        Document documentML = (Document) responseML.parse();
        Element elementML = documentML.getElementById("section-mlb").getElementById("tbody-mlb");
        Elements gameElementsML = elementML.select("div[class=d-flex flex-row flex-nowrap border position-relative mt-0 GameRows_eventMarketGridContainer__GuplK GameRows_neverWrap__gnQNO]");
        for(Element element1ML : gameElementsML){
            Elements test = element1ML.getElementsByClass("d-flex align-items-center overflow-hidden fs-9 GameRows_gradientContainer__ZajIf");
            String tricode1;
            String awayPitcher = "";
            Element teamElement = test.get(0);
            if(teamElement.childrenSize() > 1){
                tricode1 = test.get(0).getElementsByTag("a").get(0).getElementsByTag("b").get(0).text();
                awayPitcher = test.get(0).child(2).text().replace(" (R)", "").replace(" (L)", "");
            }else{
                tricode1 = test.get(0).getElementsByTag("a").text();
            }

            String tricode2;
            Element teamElement2 = test.get(1);
            String homePitcher = "";
            if(teamElement2.childrenSize() > 1){
                tricode2 = test.get(1).getElementsByTag("a").get(0).getElementsByTag("b").get(0).text();
                homePitcher = test.get(1).child(2).text().replace(" (R)", "").replace(" (L)", "");
            }else{
                tricode2 = test.get(1).getElementsByTag("a").text();
            }
            if(tricode1.equals("Washington")){
                tricode1 = "WAS";
            }
            if(tricode2.equals("Washington")){
                tricode2 = "WAS";
            }
            Elements test2 = element1ML.getElementsByClass("d-flex align-items-center text-center OddsCells_oddsNumber__u3rsp OddsCells_compact__cawia");

            //away odds
            String unParsedAwayMoneyLine = test2.get(4).getElementsByTag("span").get(2).text();
            if(unParsedAwayMoneyLine.equals("-") || unParsedAwayMoneyLine.equals("")){
                unParsedAwayMoneyLine = test2.get(6).getElementsByTag("span").get(2).text();
            }
            // String unParsedAwayOdds = test2.get(0).getElementsByTag("span").get(2).text();

            String unParsedHomeMoneyLine = test2.get(5).getElementsByTag("span").get(2).text();
            if(unParsedHomeMoneyLine.equals("-") || unParsedHomeMoneyLine.equals("")){
                unParsedHomeMoneyLine = test2.get(7).getElementsByTag("span").get(2).text();
            }
            System.out.println(unParsedHomeMoneyLine);

            for(CaesarsObject caesarsObject : caesarsObjectList){
                if(caesarsObject.getHomeTeamName().equals(convertTriCode(tricode2)) && caesarsObject.getHomePitcherName().equals(homePitcher)){
                    caesarsObject.setAwayTeamMoneyLine(Double.parseDouble(unParsedAwayMoneyLine));
                    caesarsObject.setHomeTeamMoneyLine(Double.parseDouble(unParsedHomeMoneyLine));
                }
            }
            //String unParsedHomeOdds = test2.get(1).getElementsByTag("span").get(2).text();
        }
        Connection.Response responseOU = Jsoup.connect("https://www.sportsbookreview.com/betting-odds/" +
                        "mlb-baseball/totals/full-game/?date=" +convertLocalDate(localDate))
                .method(Connection.Method.GET)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 OPR/78.0.4093.153")
                .header("Connection", "keep-alive")
                .header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjkwMDk1YmM2ZGM2ZDY3NzkxZDdkYTFlZWIxYTU1OWEzZDViMmM0ODYiLCJ0eXAiOiJKV1QifQ.eyJ0ZW5hbnQiOiJjbG91ZGJldCIsInV1aWQiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20va3ViZXJzaG11YmVyLXByb2QtY2ItYXV0aCIsImF1ZCI6Imt1YmVyc2htdWJlci1wcm9kLWNiLWF1dGgiLCJhdXRoX3RpbWUiOjE2MzAwMzIwMDYsInVzZXJfaWQiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJzdWIiOiJhZjBiYTQ5NS1iYmM3LTQyMmItOTViYi0xZDc3MzQxMzM3YmYiLCJpYXQiOjE2MzAwNzI3ODksImV4cCI6MTYzMDA3NjM4OSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6e30sInNpZ25faW5fcHJvdmlkZXIiOiJjdXN0b20ifX0.D9tWV403lYk9EElG09UW3NZTENUmMkquTf_8y2-eTqqsS3bHcrs3tMHXiXVy2EtPY0zfukc__3pMIQHYrga6TtSdkQjIB3qTf-Biemk9T0vtIhyZ_ClEiFTi3OoQ8FWPBN66jc_5DsRdm0FAQ23TmEwUB7oqTDKRZN6yC_xcc3ksAFoUIRB_9KrDi-ofXAr0VqejB4xKLrtpypxvXgHVs_rrxpA3hWg6FEPhVxQhI3LFEmUOZsB8Dg6j-UAKXiS8rON4f_-MCKk0vJemkBgYFhcbjI1rf_hdmoBB0G8cpXTjJmHAaXzF8xtsl4sYyeOI1GJeiFxuHJK19hMwacfkuw")
                .header("Content-Type", "application/json")
                .header("x-player-timezone", "America/Chicago")
                .ignoreContentType(true)
                .maxBodySize(0)
                .execute();
        Document documentOU = (Document) responseOU.parse();
        Element elementOU = documentOU.getElementById("section-mlb").getElementById("tbody-mlb");
        Elements gameElementsOU = elementOU.select("div[class=d-flex flex-row flex-nowrap border position-relative mt-0 GameRows_eventMarketGridContainer__GuplK GameRows_neverWrap__gnQNO]");
        for(Element element1OU : gameElementsOU){
            Elements test = element1OU.getElementsByClass("d-flex align-items-center overflow-hidden fs-9 GameRows_gradientContainer__ZajIf");
            String tricode1;
            String awayPitcher = "";
            Element teamElement = test.get(0);
            if(teamElement.childrenSize() > 1){
                tricode1 = test.get(0).getElementsByTag("a").get(0).getElementsByTag("b").get(0).text();
                awayPitcher = test.get(0).child(2).text().replace(" (R)", "").replace(" (L)", "");
            }else{
                tricode1 = test.get(0).getElementsByTag("a").text();
            }

            String tricode2;
            Element teamElement2 = test.get(1);
            String homePitcher = "";
            if(teamElement2.childrenSize() > 1){
                tricode2 = test.get(1).getElementsByTag("a").get(0).getElementsByTag("b").get(0).text();
                homePitcher = test.get(1).child(2).text().replace(" (R)", "").replace(" (L)", "");
            }else{
                tricode2 = test.get(1).getElementsByTag("a").text();
            }
            if(tricode1.equals("Washington")){
                tricode1 = "WAS";
            }
            if(tricode2.equals("Washington")){
                tricode2 = "WAS";
            }
            Elements test2 = element1OU.getElementsByClass("d-flex align-items-center text-center OddsCells_oddsNumber__u3rsp OddsCells_compact__cawia");

            //away odds
            String unParseOverUnder = test2.get(4).getElementsByTag("span").get(1).text();
            if(unParseOverUnder.equals("-")|| unParseOverUnder.equals("")){
                unParseOverUnder = test2.get(6).getElementsByTag("span").get(1).text();
            }
            String unParsedOverOdds = test2.get(4).getElementsByTag("span").get(2).text();
            if(unParsedOverOdds.equals("-")|| unParsedOverOdds.equals("")){
                unParsedOverOdds = test2.get(6).getElementsByTag("span").get(2).text();
            }
            String unParsedUnderOdds = test2.get(5).getElementsByTag("span").get(2).text();
            if(unParsedUnderOdds.equals("-") || unParsedUnderOdds.equals("")){
                unParsedUnderOdds = test2.get(7).getElementsByTag("span").get(2).text();
            }
            //  String unParsedHomeMoneyLine = test2.get(1).getElementsByTag("span").get(1).text();
            System.out.println(unParsedUnderOdds);

            for(CaesarsObject caesarsObject : caesarsObjectList){
                if(caesarsObject.getHomeTeamName().equals(convertTriCode(tricode2)) && caesarsObject.getHomePitcherName().equals(homePitcher)){
                    caesarsObject.setOverUnder(Double.parseDouble(unParseOverUnder));
                    caesarsObject.setOddsOver(Double.parseDouble(unParsedOverOdds));
                    caesarsObject.setOddsUnder(Double.parseDouble(unParsedUnderOdds));
                }
            }
            //String unParsedHomeOdds = test2.get(1).getElementsByTag("span").get(2).text();
        }
        return caesarsObjectList;
    }
    public String convertTriCode(String tricode){
        return switch (tricode) {
            case "LAA" -> "Angels";
            case "LA Angels" -> "Angels";
            case "CWS" -> "White Sox";
            case "CHW" -> "White Sox";
            case "HOU" -> "Astros";
            case "Houston" -> "Astros";
            case "OAK" -> "Athletics";
            case "Oakland" -> "Athletics";
            case "MIN" -> "Twins";
            case "Minnesota" -> "Twins";
            case "MIL" -> "Brewers";
            case "Milwaukee" -> "Brewers";
            case "TB" -> "Rays";
            case "Tampa Bay" -> "Rays";
            case "MIA" -> "Marlins";
            case "Miami" -> "Marlins";
            case "SF" -> "Giants";
            case "San Francisco" -> "Giants";
            case "SEA" -> "Mariners";
            case "Seattle" -> "Mariners";
            case "LAD" -> "Dodgers";
            case "LA Dodgers" -> "Dodgers";
            case "AZ" -> "D-backs";
            case "ARI" -> "D-backs";
            case "Arizona" -> "D-backs";
            case "SD" -> "Padres";
            case "San Diego" -> "Padres";
            case "BAL" -> "Orioles";
            case "Baltimore" -> "Orioles";
            case "BOS" -> "Red Sox";
            case "Boston" -> "Red Sox";
            case "PIT" -> "Pirates";
            case "PittsBurgh" -> "Pirates";
            case "Pittsburgh" -> "Pirates";
            case "CHC" -> "Cubs";
            case "Chi. Cubs" -> "Cubs";
            case "ATL" -> "Braves";
            case "Atlanta" -> "Braves";
            case "PHI" -> "Phillies";
            case "Philadelphia" -> "Phillies";
            case "STL" -> "Cardinals";
            case "CIN" -> "Reds";
            case "Cincinnati" -> "Reds";
            case "TOR" -> "Blue Jays";
            case "Toronto" -> "Blue Jays";
            case "NYY" -> "Yankees";
            case "NY Yankees" -> "Yankees";
            case "CLE" -> "Guardians";
            case "Cleveland" -> "Guardians";
            case "DET" -> "Tigers";
            case "Detroit" -> "Tigers";
            case "TEX" -> "Rangers";
            case "Texas" -> "Rangers";
            case "KC" -> "Royals";
            case "COL" -> "Rockies";
            case "Colorado" -> "Rockies";
            case "NYM" -> "Mets";
            case "WSH" -> "Nationals";
            case "WAS" -> "Nationals";
            case "Washington" -> "Nationals";
            default -> null;
        };

    }

    public String convertLocalDate(LocalDate localDate){
        String dateString = "";
        int day = localDate.getDayOfMonth();
        int month = localDate.getMonthValue();
        dateString = dateString + localDate.getYear() + "-";
        String monthString = "";
        if (month < 10) {
            monthString = "0" + month;
        } else {
            monthString = String.valueOf(month);
        }
        dateString = dateString + monthString + "-";
        String dayString = "";
        if (day < 10) {
            dayString = "0" + day;
        } else {
            dayString = String.valueOf(day);
        }
        dateString = dateString + dayString;
        return dateString;
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
                       // String cleanCaesarsTitle = StringUtils.stripAccents(caesarsObject.getEventTitle()).toLowerCase();

                        if(mlbGame.getAwayTeamName().toLowerCase().contains(caesarsObject.getAwayTeamName().toLowerCase())
                                && mlbGame.getHomeTeamName().toLowerCase().contains(caesarsObject.getHomeTeamName().toLowerCase()) ){
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
}
