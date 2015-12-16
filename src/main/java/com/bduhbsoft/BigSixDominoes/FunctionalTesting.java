package com.bduhbsoft.BigSixDominoes;

import javax.swing.JFrame;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.util.concurrent.TimeUnit;

import com.bduhbsoft.BigSixDominoes.Logging.LogLevel;
import com.bduhbsoft.BigSixDominoes.Dominoe.SetType;
import com.bduhbsoft.BigSixDominoes.Dominoe.Orientation;
import com.bduhbsoft.BigSixDominoes.DominoeGameBoard.EdgeLocation;
import com.bduhbsoft.BigSixDominoes.ScoreCardHouse;
import com.bduhbsoft.BigSixDominoes.ScoreCardHouse.QuadState;

/*
* Class FunctionalTesting
*
* Implements functional testing of the various models
*
*/

public class FunctionalTesting {
    private final static String TAG = "FunctionalTesting";
    private final static String FAILED_MSG = "FAIL";
    private final static String PASSED_MSG = "PASS";
    private final static String TEST_BOARD_CLASS = "Dominoe Board Class Test";
    private final static int PASS_CTR = 0;
    private final static int FAIL_CTR = 1;
    private final static int PASS_FAIL_CNDS = 2;
    private final static int SCREEN_WIDTH = 1280;
    private final static int SCREEN_HEIGHT = 720;
    private final static int DOM_PLAY_SLEEP_TIME_MS = 1000;
    private final static String RUN_GUI_TESTS = "-g"; //Run GUI tests instead of automating
    private final static String LOG_OPT = "--log";
    private final static String LOG_LEVEL_TRACE = LOG_OPT + "=trace";
    private final static String LOG_LEVEL_DEBUG = LOG_OPT + "=debug";
    private final static String LOG_LEVEL_WARN = LOG_OPT + "=warn";
    private final static String LOG_LEVEL_INFO = LOG_OPT + "=info";
    private final static String LOG_LEVEL_ERROR = LOG_OPT + "=error";
    private final static Map<String, LogLevel> LOG_MAP = new HashMap<>();
    private static ArrayList<IFunctionalTest[]> mGuiTests = new ArrayList<>();
    public final static String BAD_PLAYER = "Bad player name!";

    private JFrame mApplication;
    private GameBoardGraphicsPanel mPanel;
    private JButton mNextVisualTestBtn;
    private VisualTestBtnActionListener mNextVisualTestListener;
    private int mCurEventBasedFunctionalTestGroup;
    private int mCurEventBasedFunctionalTest;
    private int[] mPassFailCtr;
    private boolean mTstClassSuccess;
    private boolean mRunGuiTests;

    public FunctionalTesting() {
        mCurEventBasedFunctionalTestGroup = 0;
        mCurEventBasedFunctionalTest = 0;
        mPassFailCtr = new int[PASS_FAIL_CNDS];
        mRunGuiTests = false;

        LOG_MAP.put(LOG_LEVEL_TRACE, LogLevel.TRACE);
        LOG_MAP.put(LOG_LEVEL_DEBUG, LogLevel.DEBUG);
        LOG_MAP.put(LOG_LEVEL_WARN, LogLevel.WARN);
        LOG_MAP.put(LOG_LEVEL_INFO, LogLevel.INFO);
        LOG_MAP.put(LOG_LEVEL_ERROR, LogLevel.ERROR);
    }

    public interface IFunctionalTest { 
        //boolean initTest();
        boolean runTest(FunctionalTesting test);
    }

    public static void logSuccess(boolean success, String testCase, ArrayList<String> messages, int[] passFailCtr) {
        String passFail;

        if(success) {
            passFail = PASSED_MSG;
            passFailCtr[PASS_CTR]++;
        } else {
            passFail = FAILED_MSG;
            passFailCtr[FAIL_CTR]++;
        }

        Logging.LogMsg(LogLevel.INFO, TAG, "Test \"" + testCase + "\": " + passFail);

        for(String msg : messages) {
            Logging.LogMsg(LogLevel.INFO, TAG, "    " + passFail + ": " + msg);
        }
    }

    public static void logSummary(String testClass, boolean success, int[] passFailCtr) {
        Logging.LogMsg(LogLevel.INFO, TAG, "Test class: \"" + testClass +"\" Overall: " + 
                      (success ? PASSED_MSG : FAILED_MSG) +
                      ", passed: " + passFailCtr[PASS_CTR] + " - failed: " + passFailCtr[FAIL_CTR]);
    }

    private void refreshDisplay(ArrayList<Dominoe> row, ArrayList<Dominoe> col, Dominoe spinner, int points, String testName) {

        if(mRunGuiTests) {
            mPanel.setBoard(row, col, spinner, points);
            mPanel.setTitle(testName);
            mPanel.revalidate();
            mPanel.repaint();
        }

        return;
    }

    public void resetTestMetrics() {
        for(int idx = 0; idx < PASS_FAIL_CNDS; idx++) {
            mPassFailCtr[idx] = 0;
        }

        mTstClassSuccess = true;
    }

    private boolean testAddDominoes(Dominoe[] dom, int[] expectedTtl, boolean[] expectedSuccess,
                                    DominoeGameBoard board, EdgeLocation[] addLoc, ArrayList<String> messages, String testName) {
        boolean success = true, tempResult = false;
        int curTtl = 0;
        String title = testName;

        if(title == null) {
            title = "No Test Name";
        }

        for(int idx = 0; idx < dom.length; idx++) {
            tempResult = board.putDominoe(dom[idx], addLoc[idx]);

            if(tempResult != expectedSuccess[idx]) {
                success = false;
                messages.add("Board " + (tempResult ? "accepcted" : "rejected") + " domino: " +
                             dom[idx] + ", expected board to " + (expectedSuccess[idx] ? "accecpt" : "reject") + " domino");
            }

            curTtl = board.getPerimTotal();
            if(curTtl != expectedTtl[idx]) {
                success = false;
                messages.add("Perimeter total mismatch.  Got: " + curTtl + ", expected: " + expectedTtl[idx]);
            }
            board.commitBoardState();
        }

        title += ": " + (success ? "SUCCESS" : "FAILED");
        refreshDisplay(board.getRow(), board.getColumn(), board.getSpinner(), curTtl, title);

        return success;
    }

    private boolean testAddHousePoints(ScoreCardHouse house, int[] addPoints, int[] expectedTtl, int[] expectedLeftOver,
                                       boolean[] expectedFull, int[] quadLocation, ScoreCardHouse.QuadState[] expectedState,
                                       boolean[] expectedHorizontal, boolean[] expectedVertical, int expectedMultiple, ArrayList<String> messages, String testName) {
        boolean success = true, isFull = false, horVert;
        int leftOver, total;
        QuadState[] curState;
        String title = testName;

        if(title == null) {
            title = "No Test Name";
        }

        if(expectedMultiple != house.getMultiple()) {
            success = false;
            messages.add("Actual multiple: " + house.getMultiple() + ", expected: " + expectedMultiple);
        }

        for(int idx = 0; idx < addPoints.length; idx++) {
            leftOver = house.addPoints(addPoints[idx]);
            if(leftOver != expectedLeftOver[idx]) {
                success = false;
                messages.add("Left over points: " + leftOver + " expected: " + expectedLeftOver[idx]);
            }

            isFull = house.isFull();
            if(isFull != expectedFull[idx]) {
                success = false;
                messages.add("House full: " + isFull + " expected: " + expectedFull[idx]);
            }

            horVert = house.getHorizontalBase();
            if(horVert != expectedHorizontal[idx]) {
                success = false;
                messages.add("Horizontal: " + horVert + ", expected: " + expectedHorizontal[idx]);
            }

            horVert = house.getVerticalBase();
            if(horVert != expectedVertical[idx]) {
                success = false;
                messages.add("Vertical: " + horVert + ", expected: " + expectedVertical[idx]);
            }

            curState = house.getQuads();
            if(curState[quadLocation[idx]] != expectedState[idx]) {
                success = false;
                messages.add("Quadrant " + quadLocation[idx] + ": " + curState[quadLocation[idx]] + ", expected: " + expectedState[idx]);
            }

            total = house.getPoints();
            if(total != expectedTtl[idx]) {
                success = false;
                messages.add("Point total: " + total + ", expected: " + expectedTtl[idx]);
            }
        }

        title += ": " + (success ? "SUCCESS" : "FAILED");
        return success;
    }

    private boolean testScoreCardAddPoints(DominoGameScoreboard scoreCard, int[] addPoints, String[] player, int expectedPlayerCount, int[] expectedTtl,
                                           boolean[] expectedSuccess, ArrayList<ScoreCardHouse> houses, ArrayList<String> messages, String testName) {
        boolean success = true, tempSuccess = true;
        int total;
        String title = testName;
        String lastPlayer = "";

        if(title == null) {
            title = "No Test Name";
        }

        for(int idx = 0; idx < addPoints.length; idx++) {
            lastPlayer = player[idx];
            tempSuccess = scoreCard.addPoints(lastPlayer, addPoints[idx]);
            if(tempSuccess != expectedSuccess[idx]) {
                success = false;
                messages.add(tempSuccess ? "ABLE" : "NOT ABLE" + " to add points " + addPoints[idx] + "for player: " + 
                             lastPlayer + ", expected: " + (expectedSuccess[idx] ? "SUCCESS" : "FAILURE"));
            }

            total = scoreCard.getPlayerPoints(lastPlayer);
            if(total != expectedTtl[idx]) {
                success = false;
                messages.add("Got " + total + " for player: " + lastPlayer + ", expected: " + expectedTtl[idx]);
            }
        }

        total = scoreCard.getNumPlayers();
        if(total != expectedPlayerCount) {
            success = false;
            messages.add("Found: " + total + ", players.  Expexted: " + expectedPlayerCount);
        }

        if(houses != null) {
            for(int idx = 0; idx < houses.size(); idx++) {
                if(!houses.get(idx).equals(scoreCard.getPlayerScoreCardHouses(lastPlayer).get(idx))) {
                    success = false;
                    messages.add("House " + idx + ", in score card shows: " + scoreCard.getPlayerScoreCardHouses(lastPlayer).get(idx) +
                                 ", expected: " + houses.get(idx));
                }
            }
        }

        //TODO: Draw score card on screen
        title += ": " + (success ? "SUCCESS" : "FAILED");
        return success;
    }

    private boolean checkGroupSuccess(boolean classScs, boolean caseScs) {
        return (classScs && caseScs);
    }

    public boolean runAutomatedTests() {
        boolean success = true;
        boolean tempSuccess;

        //TODO: Refactor to be like gameboard tests in array
        if(!mRunGuiTests) {
            tempSuccess = testDominoGameBoard(this);
            if(success) success = tempSuccess;
            tempSuccess = testDominoGameScoreboard(this);
            if(success) success = tempSuccess;
        }

        tempSuccess = testDomino(this);
        if(success) success = tempSuccess;
        tempSuccess = testBoneYard();
        if(success) success = tempSuccess;
        tempSuccess = testDominoePlayer();
        if(success) success = tempSuccess;
        tempSuccess = testDominoeGameOptions(this);
        if(success) success = tempSuccess;
        tempSuccess = testScoreCardHouse(this);
        if(success) success = tempSuccess;

        return success;
    }

    public boolean testDominoGameScoreboard(FunctionalTesting test) {
        boolean success = true;
        final String TEST_CLASS_NAME = "DominoGameScoreboard Class Tests";

        Logging.LogMsg(LogLevel.INFO, TAG, "");
        Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_CLASS_NAME);

        test.resetTestMetrics();

        for(IFunctionalTest curTest : mDominoGameScoreboardTests) {
             test.mTstClassSuccess = checkGroupSuccess(test.mTstClassSuccess, curTest.runTest(test));
        }

        logSummary(TEST_CLASS_NAME, test.mTstClassSuccess, test.mPassFailCtr);
        return test.mTstClassSuccess;
    }

    public boolean testScoreCardHouse(FunctionalTesting test) {
        boolean success = true;
        final String TEST_CLASS_NAME = "ScoreCardHouse Class Tests";

        Logging.LogMsg(LogLevel.INFO, TAG, "");
        Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_CLASS_NAME);

        test.resetTestMetrics();

        for(IFunctionalTest curTest : mScoreCardHouseTests) {
             test.mTstClassSuccess = checkGroupSuccess(test.mTstClassSuccess, curTest.runTest(test));
        }

        logSummary(TEST_CLASS_NAME, test.mTstClassSuccess, test.mPassFailCtr);
        return test.mTstClassSuccess;
    }

    public boolean testDominoGameBoard(FunctionalTesting test) {
        boolean success = true;
        final String TEST_CLASS_NAME = "DominoGameBoard Class Tests";

        Logging.LogMsg(LogLevel.INFO, TAG, "");
        Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_CLASS_NAME);

        test.resetTestMetrics();

        for(IFunctionalTest curTest : mGameBoardTests) {
             test.mTstClassSuccess = checkGroupSuccess(test.mTstClassSuccess, curTest.runTest(test));
        }

        logSummary(TEST_CLASS_NAME, test.mTstClassSuccess, test.mPassFailCtr);
        return test.mTstClassSuccess;
    }

    public boolean testDominoeGameOptions(FunctionalTesting test) {
        boolean success = true;
        final String TEST_CLASS_NAME = "DominoGameBoardOptions Class Tests";

        Logging.LogMsg(LogLevel.INFO, TAG, "");
        Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_CLASS_NAME);

        test.resetTestMetrics();

        for(IFunctionalTest curTest : mGameOptionsTests) {
             test.mTstClassSuccess = checkGroupSuccess(test.mTstClassSuccess, curTest.runTest(test));
        }

        logSummary(TEST_CLASS_NAME, test.mTstClassSuccess, test.mPassFailCtr);
        return test.mTstClassSuccess;
    }

    public boolean testDominoePlayer() {
        boolean success = true, tstClassSuccess = true;
        int[] passFailCtr = new int[PASS_FAIL_CNDS];
        ArrayList<String> messages = new ArrayList<String>();
        final String name = "Test Player", userName = "TestUname";
        final boolean initialTurn = false, setTurn = true;
        final int initialScore = 0, setNewScore = 15;
        DominoePlayer player;
        final String TEST_CLASS = "DominoePlayer class testing";

        Logging.LogMsg(LogLevel.INFO, TAG, "");
        Logging.LogMsg(LogLevel.INFO, TAG, "Running DominoePlayer Class Tests");

        //Case new player with default values
        player = new DominoePlayer(name, userName);
        if(player.getDisplayName() != name) {
            success = false;
            messages.add("Display name mismatch.  Constructed with: " + name + ", got: " + player.getDisplayName());
        }

        if(player.getUserName() != userName) {
            success = false;
            messages.add("User name mismatch.  Constructed with: " + userName + ", got: " + player.getUserName());
        }

        if(player.isMyTurn() != initialTurn) {
            success = false;
            messages.add("My turn mismatch.  Expected initial value:  " + initialTurn + ", got: " + player.isMyTurn());
        }

        if(player.getScore() != initialScore) {
            success = false;
            messages.add("Score mismatch.  Expected initial value:  " + initialScore + ", got: " + player.getScore());
        }

        logSuccess(success, "DominoePlayer: New player object", messages, passFailCtr);

        //Case set isMyTurn
        tstClassSuccess = checkGroupSuccess(tstClassSuccess, success);
        success = true;
        messages.clear();

        player.setMyTurn(setTurn);

        if(player.isMyTurn() != setTurn) {
            success = false;
            messages.add("My turn mismatch.  Set value:  " + setTurn + ", got: " + player.isMyTurn());
        }

        logSuccess(success, "DominoePlayer: Set player turn status", messages, passFailCtr);

        //Case set setScore
        tstClassSuccess = checkGroupSuccess(tstClassSuccess, success);
        success = true;
        messages.clear();

        player.setScore(setNewScore);

        if(player.getScore() != setNewScore) {
            success = false;
            messages.add("Score mismatch.  Expected initial value:  " + setNewScore + ", got: " + player.getScore());
        }

        logSuccess(success, "DominoePlayer: Set player score", messages, passFailCtr);

        tstClassSuccess = checkGroupSuccess(tstClassSuccess, success);
        logSummary(TEST_CLASS, tstClassSuccess, passFailCtr);

        return tstClassSuccess;
        
    }

    public boolean testBoneYard() {
        boolean success = true, tstClassSuccess = true;
        int[] passFailCtr = new int[PASS_FAIL_CNDS];
        int yardSize, newYardSize;
        Dominoe tempDom1, tempDom2;
        ArrayList<Dominoe> domSet = Dominoe.getDominoeSet(SetType.DOUBLE_SIX);
        DominoBoneyard yard = new DominoBoneyard();
        ArrayList<Dominoe> copyList;
        ArrayList<String> messages = new ArrayList<String>();
        final String TEST_CLASS = "DominoBoneyard class testing";

        Logging.LogMsg(LogLevel.INFO, TAG, "");
        Logging.LogMsg(LogLevel.INFO, TAG, "Running DominoBoneyard Class Tests");

        //Case boneyard is empty after creation and returns zero size
        if(yard.getYardSize() != 0) {
            success = false;
            messages.add("getYardSize returned non-zero size after empty init: " + yard.getYardSize());
        }

        tempDom1 = yard.removeDomino();
        if(tempDom1 != null) {
            success = false;
            messages.add("removeDomino returned non-null domino when empty: " + tempDom1);
        }

        logSuccess(success, "DominoBoneyard: New empty boneyard", messages, passFailCtr);

        //Case set yard populates the bone yard
        tstClassSuccess = checkGroupSuccess(tstClassSuccess, success);
        success = true;
        messages.clear();

        yard.setYard(domSet);
        yardSize = yard.getYardSize();
        if(yardSize <= 0) {
            success = false;
            messages.add("setYard() failed, getYardSize returned bad value: " + yardSize);
        }

        logSuccess(success, "DominoBoneyard: Init boneyard", messages, passFailCtr);

        //If boneyard is empty, no point continuing
        if(!success) {
            return success;
        }

        //Case wash the bone yard
        tstClassSuccess = checkGroupSuccess(tstClassSuccess, success);
        success = false;
        messages.clear();

//      TODO: Figure out why this breaks compilation
//        @SuppressWarnings("unchecked")
        copyList = (ArrayList<Dominoe>)(yard.getYard().clone());
        yard.washYard();
        Logging.LogMsg(LogLevel.TRACE, "DominoBoneyard: Init boneyard", "copyList |  yard list");
        for(int idx = 0; idx < yardSize; idx++) {
            Logging.LogMsg(LogLevel.TRACE, "DominoBoneyard: Init boneyard", "  " + copyList.get(idx) + "    |     " + yard.getYard().get(idx));
            if(!yard.getYard().get(idx).equals(copyList.get(idx))) {
                success = true;
            }
        }

        if(!success) {
            messages.add("all dominoes still in same position, shuffle failed");
        }

        //Check for duplicates.  If there is a dup, it indicates a mistake in the shuffle
        for(int i = 0; i < (yardSize - 1); i++) {
            for(int j = (i + 1); j < yardSize; j++) {
                tempDom1 = yard.getYard().get(i);
                tempDom2 = yard.getYard().get(j);
                if(tempDom1.equals(tempDom2)) {
                    success = false;
                    messages.add("duplicate detected yard[" + i + "]: " + tempDom1 + " <-> yard[" + j + "]: " + tempDom2);
                }
            }
        }

        //Check that all original domioes are still present
        int origIdx = 0;
        boolean[] found = new boolean[copyList.size()];
        for(Dominoe origDom: copyList) {
            for(Dominoe curDom: yard.getYard()) {
                if(origDom.equals(curDom)) {
                    found[origIdx] = true;
                    break;
                }
            }
            if(!found[origIdx]) {
                success = false;
                messages.add("domino: " + origDom + ", not in washed list");
            }

            origIdx++;
        }

        logSuccess(success, "DominoBoneyard: wash the yard", messages, passFailCtr);

        //Case remove a domino from the yard
        tstClassSuccess = checkGroupSuccess(tstClassSuccess, success);
        success = true;
        messages.clear();

        tempDom1 = yard.removeDomino();
        newYardSize = yard.getYardSize();
        if(newYardSize != (yardSize - 1)) {
            success = false;
            messages.add("removeDomino removed one domino, expected size incorrect.  New size: " + newYardSize + ", prev size" + yardSize);
        }

        if(tempDom1 == null) {
            success = false;
            messages.add("removeDomino returned null, expected valid object");
        }

        logSuccess(success, "DominoBoneyard: remove domino from the yard", messages, passFailCtr);

        tstClassSuccess = checkGroupSuccess(tstClassSuccess, success);
        logSummary(TEST_CLASS, tstClassSuccess, passFailCtr);
        return tstClassSuccess;
    }

    public boolean testDomino(FunctionalTesting test) {
        boolean success = true;
        final String TEST_CLASS_NAME = "Domino Class Tests";

        Logging.LogMsg(LogLevel.INFO, TAG, "");
        Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_CLASS_NAME);

        test.resetTestMetrics();

        for(IFunctionalTest curTest : mDominoTests) {
             test.mTstClassSuccess = checkGroupSuccess(test.mTstClassSuccess, curTest.runTest(test));
        }

        logSummary(TEST_CLASS_NAME, test.mTstClassSuccess, test.mPassFailCtr);
        return test.mTstClassSuccess;
    }

    public static IFunctionalTest[] mDominoTests = {

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true;
            Dominoe tempDom;
            ArrayList<String> messages = new ArrayList<String>();
            int dblSide1 = 0, dblSide2 = 0;
            final String TEST_NAME = "Dominoe: Create new double";

            //Case create new double
            tempDom = new Dominoe(dblSide1, dblSide2);
            messages.add("Testing double: " + tempDom);
            if(!tempDom.isDouble()) {
                success = false;
                messages.add("isDouble returned false, expected true");
            }

            if(tempDom.getSide1() != dblSide1 || tempDom.getSide2() != dblSide2) {
                success = false;
                messages.add("Sides do not match created values.  Created: "
                             + dblSide1 + "|" + dblSide2 + ", found: " + tempDom);
            }

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true;
            Dominoe tempDom;
            ArrayList<String> messages = new ArrayList<String>();
            int regSide1 = 2, regSide2 = 3;
            final String TEST_NAME = "Dominoe: Create new non-double";

            //Case create non-double
            tempDom = new Dominoe(regSide1, regSide2);
            messages.add("Testing non-double: " + tempDom);
            if(tempDom.isDouble()) {
                success = false;
                messages.add("isDouble returned true, expected false");
            }

            if(tempDom.getSide1() != regSide1 || tempDom.getSide2() != regSide2) {
                success = false;
                messages.add("Sides do not match created values.  Created: "
                             + regSide1 + "|" + regSide2 + ", found: " + tempDom);
            }

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true;
            Dominoe tempDom;
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "Dominoe: Set and get orientation";

            //Case set and get orientation
            tempDom = new Dominoe(0,0);
            for(Dominoe.Orientation curOrtn : Dominoe.Orientation.values()) {
                tempDom.setOrientation(curOrtn);
                if(tempDom.getOrientation() != curOrtn) {
                    success = false;
                    messages.add("Calling setOrientation(" + curOrtn + ") doesn't match getOrientation(" +  tempDom.getOrientation());
                }
            }

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true;
            ArrayList<String> messages = new ArrayList<String>();
            ArrayList<Dominoe> domSet;
            final String TEST_NAME = "Dominoe: Get set of type " + SetType.DOUBLE_SIX;

            //Case get dominoe set
            domSet = Dominoe.getDominoeSet(SetType.DOUBLE_SIX);

            if(domSet == null) {
                success = false;
                messages.add("Function getDominoeSet returned null");
            }

            //TODO: Lookup clever java way to validate all dominoes are present in set
            for(Dominoe dom : domSet) {
                messages.add("Dominoe: " + dom);
            }

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);
            return success;
        }}
    };

    public static IFunctionalTest[] mGameOptionsTests = {
        
        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true;
            DominoeGameOptions options = null;
            final String TEST_NAME = "DominoGameBoardOptions: Create options object";
            ArrayList<String> messages = new ArrayList<>();

            options = new DominoeGameOptions(DominoeGameOptions.DEFAULT_PLAYERS,
                                             DominoeGameOptions.SCORE_THRESHOLD,
                                             DominoeGameOptions.MIN_START_SCORE,
                                             DominoeGameOptions.DOM_PER_HAND);

            if(options.getNumPlayers()       != DominoeGameOptions.DEFAULT_PLAYERS ||
               options.getScoreThreshold()   != DominoeGameOptions.SCORE_THRESHOLD ||
               options.getMinStartingScore() != DominoeGameOptions.MIN_START_SCORE ||
               options.getNumDomPerHand()    != DominoeGameOptions.DOM_PER_HAND       ) {
                messages.add("Options do not match created values, expected num players: " + DominoeGameOptions.DEFAULT_PLAYERS +
                             ", found: " + options.getNumPlayers() + ", expected threshold: " + DominoeGameOptions.SCORE_THRESHOLD +
                             ", found: " + options.getScoreThreshold() + ", expected starting score: " + DominoeGameOptions.MIN_START_SCORE +
                             ", found: " + options.getMinStartingScore() + ", expected dominoes per hand: " + DominoeGameOptions.DOM_PER_HAND +
                             ", found: " + options.getNumDomPerHand());
                success = false;
            }

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }}
    };

    //DominoGameScoreboard test bodies
    public static IFunctionalTest[] mDominoGameScoreboardTests = {

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            String player1 = "Player 1", player2 = "Player 2";
            ArrayList<String> gamePlayers = new ArrayList<>();
            gamePlayers.add(player1);
            gamePlayers.add(player2);
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            DominoGameScoreboard scoreBoard = new DominoGameScoreboard(gamePlayers);
            ArrayList<String> messages = new ArrayList<String>();
            int addPoints[]           = new int[]     {5      };
            String players[]          = new String[]  {player1};
            int expectedTtl[]         = new int[]     {5      };
            boolean expectedSuccess[] = new boolean[] {true   };
            ArrayList<ScoreCardHouse> expectedHouses = new ArrayList<>(); //Only checked after all plays made since house class tested seperately
            final String TEST_NAME = "DominoGameScoreboard: Add points for valid player";

            expectedHouses.add(new ScoreCardHouse(usingMultiple));
            expectedHouses.get(0).addPoints(addPoints[addPoints.length-1]);
            success = test.testScoreCardAddPoints(scoreBoard, addPoints, players, gamePlayers.size(), expectedTtl, expectedSuccess, expectedHouses, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            String player1 = "Player 1", player2 = "Player 2";
            ArrayList<String> gamePlayers = new ArrayList<>();
            gamePlayers.add(player1);
            gamePlayers.add(player2);
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            DominoGameScoreboard scoreBoard = new DominoGameScoreboard(gamePlayers);
            ArrayList<String> messages = new ArrayList<String>();
            int addPoints[]           = new int[]     {5         };
            String players[]          = new String[]  {BAD_PLAYER};
            int expectedTtl[]         = new int[]     {-1        };
            boolean expectedSuccess[] = new boolean[] {false     };
            ArrayList<ScoreCardHouse> expectedHouses = new ArrayList<>(); //Only checked after all plays made since house class tested seperately
            final String TEST_NAME = "DominoGameScoreboard: Add points for invalid player";

            expectedHouses = null;
            success = test.testScoreCardAddPoints(scoreBoard, addPoints, players, gamePlayers.size(), expectedTtl, expectedSuccess, expectedHouses, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            String player1 = "Player 1", player2 = "Player 2";
            ArrayList<String> gamePlayers = new ArrayList<>();
            gamePlayers.add(player1);
            gamePlayers.add(player2);
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            DominoGameScoreboard scoreBoard = new DominoGameScoreboard(gamePlayers);
            ArrayList<String> messages = new ArrayList<String>();
            int addPoints[]           = new int[]     {5      , 10     };
            String players[]          = new String[]  {player1, player2};
            int expectedTtl[]         = new int[]     {5      , 10     };
            boolean expectedSuccess[] = new boolean[] {true   , true   };
            ArrayList<ScoreCardHouse> expectedHouses = new ArrayList<>(); //Only checked after all plays made since house class tested seperately
            final String TEST_NAME = "DominoGameScoreboard: Add points for two valid players";

            expectedHouses.add(new ScoreCardHouse(usingMultiple));
            expectedHouses.get(0).addPoints(expectedTtl[expectedTtl.length-1]);
            success = test.testScoreCardAddPoints(scoreBoard, addPoints, players, gamePlayers.size(), expectedTtl, expectedSuccess, expectedHouses, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            String player1 = "Player 1", player2 = "Player 2";
            ArrayList<String> gamePlayers = new ArrayList<>();
            gamePlayers.add(player1);
            gamePlayers.add(player2);
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            DominoGameScoreboard scoreBoard = new DominoGameScoreboard(gamePlayers);
            ArrayList<String> messages = new ArrayList<String>();
            int addPoints[]           = new int[]     {usingMultiple+1, usingMultiple+2};
            String players[]          = new String[]  {player1        , player2        };
            int expectedTtl[]         = new int[]     {0              , 0              };
            boolean expectedSuccess[] = new boolean[] {true           , true           };
            ArrayList<ScoreCardHouse> expectedHouses = new ArrayList<>(); //Only checked after all plays made since house class tested seperately
            final String TEST_NAME = "DominoGameScoreboard: Add non-scoring points for two valid players";

            expectedHouses.add(new ScoreCardHouse(usingMultiple));
            expectedHouses.get(0).addPoints(expectedTtl[expectedTtl.length-1]);
            success = test.testScoreCardAddPoints(scoreBoard, addPoints, players, gamePlayers.size(), expectedTtl, expectedSuccess, expectedHouses, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            String player1 = "Player 1", player2 = "Player 2";
            ArrayList<String> gamePlayers = new ArrayList<>();
            gamePlayers.add(player1);
            gamePlayers.add(player2);
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            DominoGameScoreboard scoreBoard = new DominoGameScoreboard(gamePlayers);
            ArrayList<String> messages = new ArrayList<String>();
            int addPoints[]           = new int[]     {usingMultiple+1, usingMultiple*11};
            String players[]          = new String[]  {player1        , player2         };
            int expectedTtl[]         = new int[]     {0              , usingMultiple*11};
            boolean expectedSuccess[] = new boolean[] {true           , true            };
            ArrayList<ScoreCardHouse> expectedHouses = new ArrayList<>(); //Only checked after all plays made since house class tested seperately
            final String TEST_NAME = "DominoGameScoreboard: Add two-house score for one player in one play";

            int leftOver = expectedTtl[expectedTtl.length-1];
            int curHouse = 0;
            do {
                expectedHouses.add(new ScoreCardHouse(usingMultiple));
                leftOver = expectedHouses.get(curHouse).addPoints(leftOver);
                curHouse++;
            } while(leftOver > 0);
            success = test.testScoreCardAddPoints(scoreBoard, addPoints, players, gamePlayers.size(), expectedTtl, expectedSuccess, expectedHouses, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            String player1 = "Player 1", player2 = "Player 2";
            ArrayList<String> gamePlayers = new ArrayList<>();
            gamePlayers.add(player1);
            gamePlayers.add(player2);
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            DominoGameScoreboard scoreBoard = new DominoGameScoreboard(gamePlayers);
            ArrayList<String> messages = new ArrayList<String>();
            int addPoints[]           = new int[]     {usingMultiple+1, usingMultiple*21};
            String players[]          = new String[]  {player1        , player2         };
            int expectedTtl[]         = new int[]     {0              , usingMultiple*21};
            boolean expectedSuccess[] = new boolean[] {true           , true            };
            ArrayList<ScoreCardHouse> expectedHouses = new ArrayList<>(); //Only checked after all plays made since house class tested seperately
            final String TEST_NAME = "DominoGameScoreboard: Add three house score for one player in one play";

            int leftOver = expectedTtl[expectedTtl.length-1];
            int curHouse = 0;
            do {
                expectedHouses.add(new ScoreCardHouse(usingMultiple));
                leftOver = expectedHouses.get(curHouse).addPoints(leftOver);
                curHouse++;
            } while(leftOver > 0);
            success = test.testScoreCardAddPoints(scoreBoard, addPoints, players, gamePlayers.size(), expectedTtl, expectedSuccess, expectedHouses, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            String player1 = "Player 1", player2 = "Player 2";
            ArrayList<String> gamePlayers = new ArrayList<>();
            gamePlayers.add(player1);
            gamePlayers.add(player2);
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            DominoGameScoreboard scoreBoard = new DominoGameScoreboard(gamePlayers);
            ArrayList<String> messages = new ArrayList<String>();
            int addPoints[]           = new int[]     {usingMultiple, usingMultiple*5, usingMultiple*5 , usingMultiple*5 , usingMultiple*5 };
            String players[]          = new String[]  {player2      , player2        , player2         , player2         , player2         };
            int expectedTtl[]         = new int[]     {usingMultiple, usingMultiple*6, usingMultiple*11, usingMultiple*16, usingMultiple*21};
            boolean expectedSuccess[] = new boolean[] {true         , true           , true            , true            , true            };
            ArrayList<ScoreCardHouse> expectedHouses = new ArrayList<>(); //Only checked after all plays made since house class tested seperately
            final String TEST_NAME = "DominoGameScoreboard: Add three house score for one player in multiple plays";

            int leftOver = expectedTtl[expectedTtl.length-1];
            int curHouse = 0;
            do {
                expectedHouses.add(new ScoreCardHouse(usingMultiple));
                leftOver = expectedHouses.get(curHouse).addPoints(leftOver);
                curHouse++;
            } while(leftOver > 0);
            success = test.testScoreCardAddPoints(scoreBoard, addPoints, players, gamePlayers.size(), expectedTtl, expectedSuccess, expectedHouses, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }}
    };

    //ScoreCardHouse test bodies
    public static IFunctionalTest[] mScoreCardHouseTests = {

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE; //Use default ctor here and pass in multiple for remaining tests
            ScoreCardHouse house = new ScoreCardHouse();
            ArrayList<String> messages = new ArrayList<String>();
            int curPoints[]                          = new int[]                      {ScoreCardHouse.DEFAULT_MULTIPLE};
            int expectedTtl[]                        = new int[]                      {ScoreCardHouse.DEFAULT_MULTIPLE};
            int expectedLeftOver[]                   = new int[]                      {0                              };
            boolean expectedFull[]                   = new boolean[]                  {false                          };
            boolean expectedHoriz[]                  = new boolean[]                  {true                           };
            boolean expectedVert[]                   = new boolean[]                  {false                          };
            int quadLocation[]                       = new int[]                      {0                              };
            ScoreCardHouse.QuadState expectedState[] = new ScoreCardHouse.QuadState[] {ScoreCardHouse.QuadState.Empty };
            final String TEST_NAME = "ScoreCardHouse: Add " + ScoreCardHouse.DEFAULT_MULTIPLE + " points";
            String title;

            success = test.testAddHousePoints(house, curPoints, expectedTtl, expectedLeftOver, expectedFull,
                                              quadLocation, expectedState, expectedHoriz, expectedVert, usingMultiple, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            ScoreCardHouse house = new ScoreCardHouse(usingMultiple);
            ArrayList<String> messages = new ArrayList<String>();
            int curPoints[]                          = new int[]                      {usingMultiple*2               };
            int expectedTtl[]                        = new int[]                      {usingMultiple*2               };
            int expectedLeftOver[]                   = new int[]                      {0                             };
            boolean expectedFull[]                   = new boolean[]                  {false                         };
            boolean expectedHoriz[]                  = new boolean[]                  {true                          };
            boolean expectedVert[]                   = new boolean[]                  {true                          };
            int quadLocation[]                       = new int[]                      {0                             };
            ScoreCardHouse.QuadState expectedState[] = new ScoreCardHouse.QuadState[] {ScoreCardHouse.QuadState.Empty};
            final String TEST_NAME = "ScoreCardHouse: Add " + usingMultiple*2 + " points";
            String title;

            success = test.testAddHousePoints(house, curPoints, expectedTtl, expectedLeftOver, expectedFull,
                                              quadLocation, expectedState, expectedHoriz, expectedVert, usingMultiple, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            ScoreCardHouse house = new ScoreCardHouse(usingMultiple);
            ArrayList<String> messages = new ArrayList<String>();
            int curPoints[]                          = new int[]                      {usingMultiple*3              };
            int expectedTtl[]                        = new int[]                      {usingMultiple*3              };
            int expectedLeftOver[]                   = new int[]                      {0                            };
            boolean expectedFull[]                   = new boolean[]                  {false                        };
            boolean expectedHoriz[]                  = new boolean[]                  {true                         };
            boolean expectedVert[]                   = new boolean[]                  {true                         };
            int quadLocation[]                       = new int[]                      {0                            };
            ScoreCardHouse.QuadState expectedState[] = new ScoreCardHouse.QuadState[] {ScoreCardHouse.QuadState.Line};
            final String TEST_NAME = "ScoreCardHouse: Add " + usingMultiple*3 + " points";
            String title;

            success = test.testAddHousePoints(house, curPoints, expectedTtl, expectedLeftOver, expectedFull,
                                              quadLocation, expectedState, expectedHoriz, expectedVert, usingMultiple, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            ScoreCardHouse house = new ScoreCardHouse(usingMultiple);
            ArrayList<String> messages = new ArrayList<String>();
            int curPoints[]                          = new int[]                      {usingMultiple*4                };
            int expectedTtl[]                        = new int[]                      {usingMultiple*4                };
            int expectedLeftOver[]                   = new int[]                      {0                              };
            boolean expectedFull[]                   = new boolean[]                  {false                          };
            boolean expectedHoriz[]                  = new boolean[]                  {true                           };
            boolean expectedVert[]                   = new boolean[]                  {true                           };
            int quadLocation[]                       = new int[]                      {0                              };
            ScoreCardHouse.QuadState expectedState[] = new ScoreCardHouse.QuadState[] {ScoreCardHouse.QuadState.Circle};
            final String TEST_NAME = "ScoreCardHouse: Add " + usingMultiple*4 + " points";
            String title;

            success = test.testAddHousePoints(house, curPoints, expectedTtl, expectedLeftOver, expectedFull,
                                              quadLocation, expectedState, expectedHoriz, expectedVert, usingMultiple, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            ScoreCardHouse house = new ScoreCardHouse(usingMultiple);
            ArrayList<String> messages = new ArrayList<String>();
            int curPoints[]                          = new int[]                      {usingMultiple*3              , usingMultiple                 };
            int expectedTtl[]                        = new int[]                      {usingMultiple*3              , usingMultiple*4               };
            int expectedLeftOver[]                   = new int[]                      {0                            , 0                             };
            boolean expectedFull[]                   = new boolean[]                  {false                        , false                         };
            boolean expectedHoriz[]                  = new boolean[]                  {true                         , true                          };
            boolean expectedVert[]                   = new boolean[]                  {true                         , true                          };
            int quadLocation[]                       = new int[]                      {0                            , 0                             };
            ScoreCardHouse.QuadState expectedState[] = new ScoreCardHouse.QuadState[] {ScoreCardHouse.QuadState.Line, ScoreCardHouse.QuadState.Cross};
            final String TEST_NAME = "ScoreCardHouse: Add points twice, making cross at quad 0";
            String title;

            success = test.testAddHousePoints(house, curPoints, expectedTtl, expectedLeftOver, expectedFull,
                                              quadLocation, expectedState, expectedHoriz, expectedVert, usingMultiple, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            ScoreCardHouse house = new ScoreCardHouse(usingMultiple);
            ArrayList<String> messages = new ArrayList<String>();
            int curPoints[]                          = new int[]                      {usingMultiple                 , usingMultiple                 ,
                                                                                       usingMultiple                 , usingMultiple                 ,
                                                                                       usingMultiple                 , usingMultiple                 ,
                                                                                       usingMultiple                 , usingMultiple                 ,
                                                                                       usingMultiple                 , usingMultiple                  };

            int expectedTtl[]                        = new int[]                      {usingMultiple                 , usingMultiple*2               ,
                                                                                       usingMultiple*3               , usingMultiple*4               ,
                                                                                       usingMultiple*5               , usingMultiple*6               ,
                                                                                       usingMultiple*7               , usingMultiple*8               ,
                                                                                       usingMultiple*9               , usingMultiple*10                };

            int expectedLeftOver[]                   = new int[]                      {0                             , 0                             ,
                                                                                       0                             , 0                             ,
                                                                                       0                             , 0                             ,
                                                                                       0                             , 0                             ,
                                                                                       0                             , 0                             ,
                                                                                       0                             , 0                               };

            boolean expectedFull[]                   = new boolean[]                  {false                         , false                         ,
                                                                                       false                         , false                         ,
                                                                                       false                         , false                         ,
                                                                                       false                         , false                         ,
                                                                                       false                         , true                            };

            boolean expectedHoriz[]                  = new boolean[]                  {true                          , true                          ,
                                                                                       true                          , true                          ,
                                                                                       true                          , true                          ,
                                                                                       true                          , true                          ,
                                                                                       true                          , true                            };

            boolean expectedVert[]                   = new boolean[]                  {false                         , true                          ,
                                                                                       true                          , true                          ,
                                                                                       true                          , true                          ,
                                                                                       true                          , true                          ,
                                                                                       true                          , true                            };

            int quadLocation[]                       = new int[]                      {0                             , 0                             ,
                                                                                       0                             , 0                             ,
                                                                                       1                             , 1                             ,
                                                                                       2                             , 2                             ,
                                                                                       3                             , 3                               };

            ScoreCardHouse.QuadState expectedState[] = new ScoreCardHouse.QuadState[] {ScoreCardHouse.QuadState.Empty, ScoreCardHouse.QuadState.Empty,
                                                                                       ScoreCardHouse.QuadState.Line , ScoreCardHouse.QuadState.Cross,
                                                                                       ScoreCardHouse.QuadState.Line , ScoreCardHouse.QuadState.Cross,
                                                                                       ScoreCardHouse.QuadState.Line , ScoreCardHouse.QuadState.Cross,
                                                                                       ScoreCardHouse.QuadState.Line , ScoreCardHouse.QuadState.Cross  };
            final String TEST_NAME = "ScoreCardHouse: Add 5 points until full";
            String title;

            success = test.testAddHousePoints(house, curPoints, expectedTtl, expectedLeftOver, expectedFull,
                                              quadLocation, expectedState, expectedHoriz, expectedVert, usingMultiple, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            ScoreCardHouse house = new ScoreCardHouse(usingMultiple);
            ArrayList<String> messages = new ArrayList<String>();
            int curPoints[]                          = new int[]                      {usingMultiple*2               , usingMultiple*2               ,
                                                                                       usingMultiple*2               , usingMultiple*2               ,
                                                                                       usingMultiple*2               , usingMultiple*2                 };

            int expectedTtl[]                        = new int[]                      {usingMultiple*2               , usingMultiple*4               ,
                                                                                       usingMultiple*6               , usingMultiple*8               ,
                                                                                       usingMultiple*10              , usingMultiple*10                };

            int expectedLeftOver[]                   = new int[]                      {0                             , 0                             ,
                                                                                       0                             , 0                             ,
                                                                                       0                             , usingMultiple*2                 };

            boolean expectedFull[]                   = new boolean[]                  {false                         , false                         ,
                                                                                       false                         , false                         ,
                                                                                       true                          , true                            };

            boolean expectedHoriz[]                  = new boolean[]                  {true                          , true                          ,
                                                                                       true                          , true                          ,
                                                                                       true                          , true                            };

            boolean expectedVert[]                   = new boolean[]                  {true                          , true                          ,
                                                                                       true                          , true                          ,
                                                                                       true                          , true                            };

            int quadLocation[]                       = new int[]                      {0                             , 0                             ,
                                                                                       1                             , 2                             ,
                                                                                       3                             , 3                               };

            ScoreCardHouse.QuadState expectedState[] = new ScoreCardHouse.QuadState[] {ScoreCardHouse.QuadState.Empty , ScoreCardHouse.QuadState.Circle,
                                                                                       ScoreCardHouse.QuadState.Circle, ScoreCardHouse.QuadState.Circle,
                                                                                       ScoreCardHouse.QuadState.Circle, ScoreCardHouse.QuadState.Circle  };
            final String TEST_NAME = "ScoreCardHouse: Add " + usingMultiple*2 + " points until full";
            String title;

            success = test.testAddHousePoints(house, curPoints, expectedTtl, expectedLeftOver, expectedFull,
                                              quadLocation, expectedState, expectedHoriz, expectedVert, usingMultiple, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            int oneTimePoint = usingMultiple * 3;
            ScoreCardHouse house = new ScoreCardHouse(usingMultiple);
            ArrayList<String> messages = new ArrayList<String>();
            int curPoints[]                          = new int[]                      {oneTimePoint                 };
            int expectedTtl[]                        = new int[]                      {oneTimePoint                 };
            int expectedLeftOver[]                   = new int[]                      {0                            };
            boolean expectedFull[]                   = new boolean[]                  {false                        };
            boolean expectedHoriz[]                  = new boolean[]                  {true                         };
            boolean expectedVert[]                   = new boolean[]                  {true                         };
            int quadLocation[]                       = new int[]                      {0                            };
            ScoreCardHouse.QuadState expectedState[] = new ScoreCardHouse.QuadState[] {ScoreCardHouse.QuadState.Line};
            final String TEST_NAME = "ScoreCardHouse: Add " + oneTimePoint + " points";
            String title;

            success = test.testAddHousePoints(house, curPoints, expectedTtl, expectedLeftOver, expectedFull,
                                              quadLocation, expectedState, expectedHoriz, expectedVert, usingMultiple, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            int oneTimePoint = usingMultiple * 4;
            ScoreCardHouse house = new ScoreCardHouse(usingMultiple);
            ArrayList<String> messages = new ArrayList<String>();
            int curPoints[]                          = new int[]                      {oneTimePoint                   };
            int expectedTtl[]                        = new int[]                      {oneTimePoint                   };
            int expectedLeftOver[]                   = new int[]                      {0                              };
            boolean expectedFull[]                   = new boolean[]                  {false                          };
            boolean expectedHoriz[]                  = new boolean[]                  {true                           };
            boolean expectedVert[]                   = new boolean[]                  {true                           };
            int quadLocation[]                       = new int[]                      {0                              };
            ScoreCardHouse.QuadState expectedState[] = new ScoreCardHouse.QuadState[] {ScoreCardHouse.QuadState.Circle};
            final String TEST_NAME = "ScoreCardHouse: Add " + oneTimePoint + " points";
            String title;

            success = test.testAddHousePoints(house, curPoints, expectedTtl, expectedLeftOver, expectedFull,
                                              quadLocation, expectedState, expectedHoriz, expectedVert, usingMultiple, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            int oneTimePoint = usingMultiple * 5;
            ScoreCardHouse house = new ScoreCardHouse(usingMultiple);
            ArrayList<String> messages = new ArrayList<String>();
            int curPoints[]                          = new int[]                      {oneTimePoint                 };
            int expectedTtl[]                        = new int[]                      {oneTimePoint                 };
            int expectedLeftOver[]                   = new int[]                      {0                            };
            boolean expectedFull[]                   = new boolean[]                  {false                        };
            boolean expectedHoriz[]                  = new boolean[]                  {true                         };
            boolean expectedVert[]                   = new boolean[]                  {true                         };
            int quadLocation[]                       = new int[]                      {1                            };
            ScoreCardHouse.QuadState expectedState[] = new ScoreCardHouse.QuadState[] {ScoreCardHouse.QuadState.Line};
            final String TEST_NAME = "ScoreCardHouse: Add " + oneTimePoint + " points";
            String title;

            success = test.testAddHousePoints(house, curPoints, expectedTtl, expectedLeftOver, expectedFull,
                                              quadLocation, expectedState, expectedHoriz, expectedVert, usingMultiple, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            int oneTimePoint = usingMultiple * 6;
            ScoreCardHouse house = new ScoreCardHouse(usingMultiple);
            ArrayList<String> messages = new ArrayList<String>();
            int curPoints[]                          = new int[]                      {oneTimePoint                   };
            int expectedTtl[]                        = new int[]                      {oneTimePoint                   };
            int expectedLeftOver[]                   = new int[]                      {0                              };
            boolean expectedFull[]                   = new boolean[]                  {false                          };
            boolean expectedHoriz[]                  = new boolean[]                  {true                           };
            boolean expectedVert[]                   = new boolean[]                  {true                           };
            int quadLocation[]                       = new int[]                      {1                              };
            ScoreCardHouse.QuadState expectedState[] = new ScoreCardHouse.QuadState[] {ScoreCardHouse.QuadState.Circle};
            final String TEST_NAME = "ScoreCardHouse: Add " + oneTimePoint + " points";
            String title;

            success = test.testAddHousePoints(house, curPoints, expectedTtl, expectedLeftOver, expectedFull,
                                              quadLocation, expectedState, expectedHoriz, expectedVert, usingMultiple, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            int oneTimePoint = usingMultiple * 7;
            ScoreCardHouse house = new ScoreCardHouse(usingMultiple);
            ArrayList<String> messages = new ArrayList<String>();
            int curPoints[]                          = new int[]                      {oneTimePoint                 };
            int expectedTtl[]                        = new int[]                      {oneTimePoint                 };
            int expectedLeftOver[]                   = new int[]                      {0                            };
            boolean expectedFull[]                   = new boolean[]                  {false                        };
            boolean expectedHoriz[]                  = new boolean[]                  {true                         };
            boolean expectedVert[]                   = new boolean[]                  {true                         };
            int quadLocation[]                       = new int[]                      {2                            };
            ScoreCardHouse.QuadState expectedState[] = new ScoreCardHouse.QuadState[] {ScoreCardHouse.QuadState.Line};
            final String TEST_NAME = "ScoreCardHouse: Add " + oneTimePoint + " points";
            String title;

            success = test.testAddHousePoints(house, curPoints, expectedTtl, expectedLeftOver, expectedFull,
                                              quadLocation, expectedState, expectedHoriz, expectedVert, usingMultiple, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            int oneTimePoint = usingMultiple * 8;
            ScoreCardHouse house = new ScoreCardHouse(usingMultiple);
            ArrayList<String> messages = new ArrayList<String>();
            int curPoints[]                          = new int[]                      {oneTimePoint                   };
            int expectedTtl[]                        = new int[]                      {oneTimePoint                   };
            int expectedLeftOver[]                   = new int[]                      {0                              };
            boolean expectedFull[]                   = new boolean[]                  {false                          };
            boolean expectedHoriz[]                  = new boolean[]                  {true                           };
            boolean expectedVert[]                   = new boolean[]                  {true                           };
            int quadLocation[]                       = new int[]                      {2                              };
            ScoreCardHouse.QuadState expectedState[] = new ScoreCardHouse.QuadState[] {ScoreCardHouse.QuadState.Circle};
            final String TEST_NAME = "ScoreCardHouse: Add " + oneTimePoint + " points";
            String title;

            success = test.testAddHousePoints(house, curPoints, expectedTtl, expectedLeftOver, expectedFull,
                                              quadLocation, expectedState, expectedHoriz, expectedVert, usingMultiple, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            int oneTimePoint = usingMultiple * 9;
            ScoreCardHouse house = new ScoreCardHouse(usingMultiple);
            ArrayList<String> messages = new ArrayList<String>();
            int curPoints[]                          = new int[]                      {oneTimePoint                 };
            int expectedTtl[]                        = new int[]                      {oneTimePoint                 };
            int expectedLeftOver[]                   = new int[]                      {0                            };
            boolean expectedFull[]                   = new boolean[]                  {false                        };
            boolean expectedHoriz[]                  = new boolean[]                  {true                         };
            boolean expectedVert[]                   = new boolean[]                  {true                         };
            int quadLocation[]                       = new int[]                      {3                            };
            ScoreCardHouse.QuadState expectedState[] = new ScoreCardHouse.QuadState[] {ScoreCardHouse.QuadState.Line};
            final String TEST_NAME = "ScoreCardHouse: Add " + oneTimePoint + " points";
            String title;

            success = test.testAddHousePoints(house, curPoints, expectedTtl, expectedLeftOver, expectedFull,
                                              quadLocation, expectedState, expectedHoriz, expectedVert, usingMultiple, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            int oneTimePoint = usingMultiple * 10;
            ScoreCardHouse house = new ScoreCardHouse(usingMultiple);
            ArrayList<String> messages = new ArrayList<String>();
            int curPoints[]                          = new int[]                      {oneTimePoint                   };
            int expectedTtl[]                        = new int[]                      {oneTimePoint                   };
            int expectedLeftOver[]                   = new int[]                      {0                              };
            boolean expectedFull[]                   = new boolean[]                  {true                           };
            boolean expectedHoriz[]                  = new boolean[]                  {true                           };
            boolean expectedVert[]                   = new boolean[]                  {true                           };
            int quadLocation[]                       = new int[]                      {3                              };
            ScoreCardHouse.QuadState expectedState[] = new ScoreCardHouse.QuadState[] {ScoreCardHouse.QuadState.Circle};
            final String TEST_NAME = "ScoreCardHouse: Add " + oneTimePoint + " points";
            String title;

            success = test.testAddHousePoints(house, curPoints, expectedTtl, expectedLeftOver, expectedFull,
                                              quadLocation, expectedState, expectedHoriz, expectedVert, usingMultiple, messages, TEST_NAME);
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            ArrayList<String> messages = new ArrayList<String>();
            int points = usingMultiple * 3;
            ScoreCardHouse house = new ScoreCardHouse(usingMultiple), house2 = new ScoreCardHouse(usingMultiple);
            final String TEST_NAME = "ScoreCardHouse: Test equals function with equal houses";
            String title;

            house.addPoints(points);
            house2.addPoints(points);
            success = house.equals(house2);
            if(!success) {
                messages.add("Houses not equal, expected equal.  House1: " + house.toString() + ", house2: " + house2.toString());
            }
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            int usingMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
            ArrayList<String> messages = new ArrayList<String>();
            int points = usingMultiple * 3;
            ScoreCardHouse house = new ScoreCardHouse(usingMultiple), house2 = new ScoreCardHouse(usingMultiple);
            final String TEST_NAME = "ScoreCardHouse: Test equals function with non-equal houses";
            String title;

            house.addPoints(points);
            house2.addPoints(points + usingMultiple);
            success = !house.equals(house2);
            if(!success) {
                messages.add("Houses equal, expected enot qual.  House1: " + house.toString() + ", house2: " + house2.toString());
            }
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }}
    };

    //GameBoard test bodies
    public static IFunctionalTest[] mGameBoardTests = {

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            ArrayList<Dominoe> row = null, col = null;
            Dominoe curDom;
            ArrayList<String> messages = new ArrayList<String>();
            int dblSide1 = 1, dblSide2 = 1;
            int regSide1 = 2, regSide2 = 3;
            final String TEST_NAME = "DominoeGameBoard: Add double to empty board";
            String title;

            //Case: put double domino on empty board
            curDom = new Dominoe(dblSide1, dblSide2);
            tempResult = board.putDominoe(curDom, EdgeLocation.NORTH);

            //If board says it's empty, the test is failed or if board failed to put domino on empty board
            if(!tempResult) {
                messages.add("Failed to add domino to empty board");
                success = false;
            }

            if(board.isEmpty()) {
                messages.add("Board shows empty after adding domino");
                success = false;
            }

            //Check that the row and column have the dominoe and the spinner is correctly located
            row = board.getRow();
            col = board.getColumn();
            if(row == null) {
                messages.add("Row is null after adding first domino");
                success = false;
            }

            if(col == null) {
                messages.add("Column is null after adding first domino");
                success = false;
            }

            if(row != null && col != null) {
                if(!curDom.equals(row.get(0))) {
                    success = false;
                    messages.add("Domino added to board doens't match row domino, expected: " + curDom + " - actual: " + row.get(0));
                }

                if(!curDom.equals(col.get(0))) {
                    success = false;
                    messages.add("Domino added to board doens't match col domino, expected: " + curDom + " - actual: " + col.get(0));
                }
            }

            //Check spinner index
            if(board.getSpinnerRow() != 0) {
                success = false;
                messages.add("Spinner row incorrect.  Expected: 0 - Found: " + board.getSpinnerRow());
            }

            if(board.getSpinnerColumn() != 0) {
                success = false;
                messages.add("Spinner column incorrect.  Expected: 0 - Found: " + board.getSpinnerColumn());
            }

            //Check board total
            if(board.getPerimTotal() != (dblSide1 + dblSide2)) {
                success = false;
                messages.add("Perimeter total incorrect.  Expected: " + (dblSide1 + dblSide2) + " - Found: " + board.getPerimTotal());
            }

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            title = TEST_NAME + ": " + (success ? "SUCCESS" : "FAILED");
            test.refreshDisplay(row, col, board.getSpinner(), board.getPerimTotal(), title);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(6, 6), new Dominoe(6, 3), new Dominoe(3, 2), new Dominoe(6, 5), new Dominoe(2, 1)};
            int expectedTtl[]          = new int[]          {               12,                15,                14,                 7,                 6};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.WEST, EdgeLocation.WEST, EdgeLocation.EAST, EdgeLocation.WEST};
            boolean expectedSuc[]      = new boolean[]      {             true,              true,              true,              true,              true};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Create Single Row With Spinner";

            //Case: Create a row of dominoes with a spinner, all dominoes succeed
            success = test.testAddDominoes(curDom, expectedTtl, expectedSuc, board, addLocation, messages, TEST_NAME);
            
            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(3, 2), new Dominoe(6, 3), new Dominoe(2, 4)};
            int expectedTtl[]          = new int[]          {                5,                 8,                10};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.WEST, EdgeLocation.EAST};
            boolean expectedSuc[]      = new boolean[]      {             true,              true,              true};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Create Single Row With NO Spinner";

            //Case: Create a row of dominoes without a spinner, all dominoes succeed
            success = test.testAddDominoes(curDom, expectedTtl, expectedSuc, board, addLocation, messages, TEST_NAME);

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(3, 2), new Dominoe(6, 3), new Dominoe(2, 2)};
            int expectedTtl[]          = new int[]          {                5,                 8,                10};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.WEST, EdgeLocation.EAST};
            boolean expectedSuc[]      = new boolean[]      {             true,              true,              true};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Create Single Row With Spinner Played NOT First at EAST";

            //Case: Create a row of dominoes with the spinner the last dominoe on the east side
            success = test.testAddDominoes(curDom, expectedTtl, expectedSuc, board, addLocation, messages, TEST_NAME);

            if(board.getSpinner() == null) {
                messages.add("Spinner is null.  Expected spinner to be present.");
                success = false;
            }

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(3, 2), new Dominoe(6, 3), new Dominoe(2, 2)};
            int expectedTtl[]          = new int[]          {                5,                 8,                10};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.EAST, EdgeLocation.EAST, EdgeLocation.WEST};
            boolean expectedSuc[]      = new boolean[]      {             true,              true,              true};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Create Single Row With Spinner Played NOT First at WEST First Domino Played EAST";

            //Case: Create a row of dominoes with the spinner the last dominoe on the east side
            success = test.testAddDominoes(curDom, expectedTtl, expectedSuc, board, addLocation, messages, TEST_NAME);

            if(board.getSpinner() == null) {
                messages.add("Spinner is null.  Expected spinner to be present.");
                success = false;
            }

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(6, 4), new Dominoe(4, 3), new Dominoe(6, 6)};
            int expectedTtl[]          = new int[]          {               10,                 9,                15};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.EAST, EdgeLocation.WEST};
            boolean expectedSuc[]      = new boolean[]      {             true,              true,              true};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Create Single Row With Spinner Played NOT First at WEST";

            //Case: Create a row of dominoes with the spiiner as the last domoinoe on the west side
            success = test.testAddDominoes(curDom, expectedTtl, expectedSuc, board, addLocation, messages, TEST_NAME);

            if(board.getSpinner() == null) {
                messages.add("Spinner is null.  Expected spinner to be present.");
                success = false;
            }

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(6, 6), new Dominoe(6, 3), new Dominoe(5, 6),  new Dominoe(4, 6)};
            int expectedTtl[]          = new int[]          {               12,                15,                 8,                 12};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.EAST, EdgeLocation.WEST, EdgeLocation.NORTH};
            boolean expectedSuc[]      = new boolean[]      {             true,              true,              true,               true};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Create column with NORTH Domino Only";

            //Case: Test playing the spinner on the North side. This requires row dominoes flanking the spinner
            success = test.testAddDominoes(curDom, expectedTtl, expectedSuc, board, addLocation, messages, TEST_NAME);

            if(board.getSpinner() == null) {
                messages.add("Spinner is null.  Expected spinner to be present.");
                success = false;
            }

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(6, 6), new Dominoe(6, 3), new Dominoe(5, 6),  new Dominoe(4, 6)};
            int expectedTtl[]          = new int[]          {               12,                15,                 8,                 12};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.EAST, EdgeLocation.WEST, EdgeLocation.SOUTH};
            boolean expectedSuc[]      = new boolean[]      {             true,              true,              true,               true};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Create column with SOUTH Domino Only";

            //Case: Test playing the spinner on the South side. This requires row dominoes flanking the spinner
            success = test.testAddDominoes(curDom, expectedTtl, expectedSuc, board, addLocation, messages, TEST_NAME);

            if(board.getSpinner() == null) {
                messages.add("Spinner is null.  Expected spinner to be present.");
                success = false;
            }

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(5, 5), new Dominoe(5, 0),  new Dominoe(5, 1)};
            int expectedTtl[]          = new int[]          {               10,                10,                 10};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.EAST, EdgeLocation.NORTH};
            boolean expectedSuc[]      = new boolean[]      {             true,              true,              false};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Add bad domino to NORTH side with a east half-flanked spinner";

            //Case: Test playing a matchng domino to the NORTH side with a east half-flanked spinner
            success = test.testAddDominoes(curDom, expectedTtl, expectedSuc, board, addLocation, messages, TEST_NAME);

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(5, 5), new Dominoe(5, 0),  new Dominoe(5, 1)};
            int expectedTtl[]          = new int[]          {               10,                10,                 10};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.EAST, EdgeLocation.SOUTH};
            boolean expectedSuc[]      = new boolean[]      {             true,              true,              false};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Add bad domino to SOUTH side with a east half-flanked spinner";

            //Case: Test playing a matchng domino to the SOUTH side with a east half-flanked spinner
            success = test.testAddDominoes(curDom, expectedTtl, expectedSuc, board, addLocation, messages, TEST_NAME);

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(5, 5), new Dominoe(5, 0), new Dominoe(5, 1),  new Dominoe(6, 1)};
            int expectedTtl[]          = new int[]          {               10,                10,                 1,                  1};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.EAST, EdgeLocation.WEST, EdgeLocation.NORTH};
            boolean expectedSuc[]      = new boolean[]      {             true,              true,              true,              false};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Add bad domino to NORTH side with a full-flanked spinner";

            //Case: Test playing a non-matchng domino to the NORTH side with a full-flanked spinner
            success = test.testAddDominoes(curDom, expectedTtl, expectedSuc, board, addLocation, messages, TEST_NAME);

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(5, 5), new Dominoe(5, 0), new Dominoe(5, 1),  new Dominoe(6, 1)};
            int expectedTtl[]          = new int[]          {               10,                10,                 1,                  1};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.EAST, EdgeLocation.WEST, EdgeLocation.SOUTH};
            boolean expectedSuc[]      = new boolean[]      {             true,              true,              true,              false};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Add bad domino to SOUTH side with a full-flanked spinner";

            //Case: Test playing a non-matchng domino to the SOUTH side with a full-flanked spinner
            success = test.testAddDominoes(curDom, expectedTtl, expectedSuc, board, addLocation, messages, TEST_NAME);

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(3, 3), new Dominoe(3, 0)};
            int expectedTtl[]          = new int[]          {                6,                 6};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.EAST};
            boolean expectedSuc[]      = new boolean[]      {             true,              true};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Add domino without committing last domino";
            Dominoe tempDom;
            EdgeLocation tempLoc;

            //Case: Test playing a domino when last domino was not committed to the board
            success = test.testAddDominoes(curDom, expectedTtl, expectedSuc, board, addLocation, messages, TEST_NAME);

            tempDom = new Dominoe(2, 3);
            tempLoc = EdgeLocation.WEST;
            if(success && board.putDominoe(tempDom, tempLoc)) {
                tempDom = new Dominoe(2, 4);
                if(board.putDominoe(tempDom, tempLoc)) {
                    success = false;
                    messages.add("Able to add domino: " + tempDom + ", to edge: " + tempLoc + ", expected add to fail");
                }
            } else {
                messages.add("Failed to add domino: " + tempDom + ". to edge: " + tempLoc + ", expected add to succeed");
                success = false;
            }

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true;
            DominoeGameBoard board = new DominoeGameBoard();
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Remove spinner as first played domino";
            Dominoe tempDom;
            EdgeLocation tempLoc;

            tempDom = new Dominoe(3, 3);
            tempLoc = EdgeLocation.WEST;
            if(board.putDominoe(tempDom, tempLoc)) {
                //Expect board to be empty
                board.removeLast();
                ArrayList<Dominoe> row = board.getRow();
                ArrayList<Dominoe> col = board.getColumn();
                Dominoe spinner = board.getSpinner();
                if(row != null || col != null || spinner != null) {
                    success = false;
                    messages.add("Non-null detectd after removing spinner, row: " + ((row == null) ? "NULL" : "NOT NULL") +
                        ", col: " + ((col == null) ? "NULL" : "NOT NULL") + ", spinner: " + ((spinner == null) ? "NULL" : "NOT NULL"));
                }
                test.refreshDisplay(row, col, spinner, board.getPerimTotal(), TEST_NAME);
            }

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true;
            DominoeGameBoard board = new DominoeGameBoard();
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Remove non-spinner as first played domino";
            Dominoe tempDom;
            EdgeLocation tempLoc;

            tempDom = new Dominoe(2, 1);
            tempLoc = EdgeLocation.WEST;
            if(board.putDominoe(tempDom, tempLoc)) {
                //Expect board to be empty
                board.removeLast();
                ArrayList<Dominoe> row = board.getRow();
                ArrayList<Dominoe> col = board.getColumn();
                Dominoe spinner = board.getSpinner();
                if(row != null || col != null || spinner != null) {
                    success = false;
                    messages.add("Non-null detectd after removing spinner, row: " + ((row == null) ? "NULL" : "NOT NULL") +
                        ", col: " + ((col == null) ? "NULL" : "NOT NULL") + ", spinner: " + ((spinner == null) ? "NULL" : "NOT NULL"));
                }
                test.refreshDisplay(row, col, spinner, board.getPerimTotal(), TEST_NAME);
            }

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(3, 3)};
            int expectedTtl[]          = new int[]          {                6};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST};
            boolean expectedSuc[]      = new boolean[]      {             true};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Remove row domino from existing row";
            Dominoe tempDom;
            EdgeLocation tempLoc;

            //Case: Simply add the double, then play and remove one
            success = test.testAddDominoes(curDom, expectedTtl, expectedSuc, board, addLocation, messages, TEST_NAME);

            tempDom = new Dominoe(3, 0);
            tempLoc = EdgeLocation.WEST;
            int prevRowSize =  board.getRow().size();
            int prevTotal = expectedTtl[expectedTtl.length - 1];
            if(success && board.putDominoe(tempDom, tempLoc)) {
                board.removeLast();
                int curTotal = board.getPerimTotal();
                int curRowSize = board.getRow().size();
                if(curTotal != prevTotal || prevRowSize != curRowSize) {
                    messages.add("Total or row size mismatch. Total before add/remove: " + prevTotal + ", cur total: " + curTotal +
                        ", row count before add/remove: " + prevRowSize + ", cur count: " + curRowSize);
                    success = false;
                }
            } else {
                messages.add("Failed to add domino: " + tempDom + ". to edge: " + tempLoc + ", expected add to succeed");
                success = false;
            }

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new IFunctionalTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(5, 5), new Dominoe(5, 0), new Dominoe(5, 1),  new Dominoe(5, 2)};
            int expectedTtl[]          = new int[]          {               10,                10,                 1,                  3};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.EAST, EdgeLocation.WEST, EdgeLocation.SOUTH};
            boolean expectedSuc[]      = new boolean[]      {             true,              true,              true,               true};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Remove column domino from existing row";
            Dominoe tempDom;
            EdgeLocation tempLoc;

            //Case: Simply add the double, then play and remove one
            success = test.testAddDominoes(curDom, expectedTtl, expectedSuc, board, addLocation, messages, TEST_NAME);

            tempDom = new Dominoe(2, 1);
            tempLoc = EdgeLocation.SOUTH;
            int prevRowSize =  board.getRow().size();
            int prevColSize =  board.getColumn().size();
            int prevTotal = expectedTtl[expectedTtl.length - 1];
            if(success && board.putDominoe(tempDom, tempLoc)) {
                board.removeLast();
                int curTotal = board.getPerimTotal();
                int curRowSize = board.getRow().size();
                if(curTotal != prevTotal || prevRowSize != curRowSize) {
                    messages.add("Total or row size mismatch. Total before add/remove: " + prevTotal + ", cur total: " + curTotal +
                        ", row count before add/remove: " + prevRowSize + ", cur count: " + curRowSize);
                    success = false;
                }
            } else {
                messages.add("Failed to add domino: " + tempDom + ". to edge: " + tempLoc + ", expected add to succeed");
                success = false;
            }

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }}
    };

    //Handler for clicking next game board test button
    class VisualTestBtnActionListener implements ActionListener {
        FunctionalTesting mTest;

        VisualTestBtnActionListener(FunctionalTesting test) {
            mTest = test;
        }

        @Override
        public void actionPerformed(ActionEvent e) { 

            if(mTest.mCurEventBasedFunctionalTest == 0) {
                mTest.resetTestMetrics();
            }

            mTest.mTstClassSuccess = checkGroupSuccess(mTest.mTstClassSuccess,
                FunctionalTesting.mGuiTests.get(mCurEventBasedFunctionalTestGroup)[mTest.mCurEventBasedFunctionalTest].runTest(mTest));
//            mTest.mTstClassSuccess = checkGroupSuccess(mTest.mTstClassSuccess, FunctionalTesting.mGameBoardTests[mTest.mCurEventBasedFunctionalTest].runTest(mTest));
            mTest.mCurEventBasedFunctionalTest++;

            if(mTest.mCurEventBasedFunctionalTest == FunctionalTesting.mGameBoardTests.length) {
                mTest.mNextVisualTestBtn.setEnabled(false);
                logSummary(TEST_BOARD_CLASS, mTest.mTstClassSuccess, mTest.mPassFailCtr);
                mTest.mCurEventBasedFunctionalTest = 0;
            }
        }
    }

    public void initializeGUI() {
        if(mRunGuiTests) {
            this.mPanel = new GameBoardGraphicsPanel();
            this.mApplication = new JFrame();
            this.mNextVisualTestListener = new VisualTestBtnActionListener(this);
            this.mNextVisualTestBtn = new JButton("Next Visual Test");
            this.mNextVisualTestBtn.addActionListener(mNextVisualTestListener);

            this.mApplication.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.mApplication.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
            this.mApplication.add(this.mPanel, BorderLayout.CENTER);
            this.mApplication.add(this.mNextVisualTestBtn, BorderLayout.SOUTH);
            this.mApplication.setVisible(true);
        }
    }

    public void processArgs(String[] args) {

        Logging.LogMsg(LogLevel.INFO, TAG, "Received " + args.length + " command line args:");
        for(String curArg : args) {
            Logging.LogMsg(LogLevel.INFO, TAG, "    " + curArg);
            if(curArg.equals(RUN_GUI_TESTS)) {
                mRunGuiTests = true;
            } else if(curArg.contains(LOG_OPT)) {
                Logging.setLogLevel(LOG_MAP.get(curArg));
            }
        }
    }

    static {
        mGuiTests.add(mGameBoardTests);
        mGuiTests.add(mDominoGameScoreboardTests);
    }


    public static void main(String[] args) {
        FunctionalTesting test = new FunctionalTesting();
        boolean success = false;

        if(args.length > 0) test.processArgs(args);

        Logging.LogMsg(LogLevel.INFO, TAG, "Functional testing start...");

        //Run all automated tests
        success = test.runAutomatedTests();

        Logging.LogMsg(LogLevel.INFO, TAG, "Functional testing overall result: " + ((success == true) ? "PASS" : "FAIL"));

        test.initializeGUI();
    }
}
