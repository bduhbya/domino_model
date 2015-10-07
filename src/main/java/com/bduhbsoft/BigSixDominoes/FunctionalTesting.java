package com.bduhbsoft.BigSixDominoes;

import javax.swing.JFrame;
import java.util.ArrayList;
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

    private JFrame mApplication;
    private GameBoardGraphicsPanel mPanel;
    private JButton mNextGameBrdTestBtn;
    private GameBoardBtnActionListener mNextGameBrdTestListener;
    private int mCurEventBasedFunctionalTest;
    private int[] mPassFailCtr;
    private boolean mTstClassSuccess;

    public FunctionalTesting() {
        mCurEventBasedFunctionalTest = 0;
        mPassFailCtr = new int[PASS_FAIL_CNDS];
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

        mPanel.setBoard(row, col, spinner, points);
        mPanel.setTitle(testName);
        mPanel.revalidate();
        mPanel.repaint();

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

    private boolean checkClassSuccess(boolean classScs, boolean caseScs) {
        return (classScs && caseScs);
    }

    public boolean runAutomatedTests() {
        boolean success = true;
        boolean tempSuccess;

        //TODO: Refactor to be like gameboard tests in array
        tempSuccess = testDomino(this);
        if(success) success = tempSuccess;
        tempSuccess = testBoneYard();
        if(success) success = tempSuccess;
        tempSuccess = testDominoePlayer();
        if(success) success = tempSuccess;

        return success;
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
        tstClassSuccess = checkClassSuccess(tstClassSuccess, success);
        success = true;
        messages.clear();

        player.setMyTurn(setTurn);

        if(player.isMyTurn() != setTurn) {
            success = false;
            messages.add("My turn mismatch.  Set value:  " + setTurn + ", got: " + player.isMyTurn());
        }

        logSuccess(success, "DominoePlayer: Set player turn status", messages, passFailCtr);

        //Case set setScore
        tstClassSuccess = checkClassSuccess(tstClassSuccess, success);
        success = true;
        messages.clear();

        player.setScore(setNewScore);

        if(player.getScore() != setNewScore) {
            success = false;
            messages.add("Score mismatch.  Expected initial value:  " + setNewScore + ", got: " + player.getScore());
        }

        logSuccess(success, "DominoePlayer: Set player score", messages, passFailCtr);

        tstClassSuccess = checkClassSuccess(tstClassSuccess, success);
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
        tstClassSuccess = checkClassSuccess(tstClassSuccess, success);
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
        tstClassSuccess = checkClassSuccess(tstClassSuccess, success);
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
        tstClassSuccess = checkClassSuccess(tstClassSuccess, success);
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

        tstClassSuccess = checkClassSuccess(tstClassSuccess, success);
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
             test.mTstClassSuccess = checkClassSuccess(test.mTstClassSuccess, curTest.runTest(test));
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
            ArrayList<Orientation> testOrtns = new ArrayList<Dominoe.Orientation>();
            tempDom = new Dominoe(0,0);
            //TODO: Look up how iterate through enums better in Java
            testOrtns.add(Orientation.SIDE1_NORTH);
            testOrtns.add(Orientation.SIDE1_SOUTH);
            testOrtns.add(Orientation.SIDE1_EAST);
            testOrtns.add(Orientation.SIDE1_WEST);
            for(Dominoe.Orientation curOrtn : testOrtns) {
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

            Logging.LogMsg(LogLevel.INFO, TAG, "");
            Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

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

            test.mTstClassSuccess = success;

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

            Logging.LogMsg(LogLevel.INFO, TAG, "");
            Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

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

            Logging.LogMsg(LogLevel.INFO, TAG, "");
            Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

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

            Logging.LogMsg(LogLevel.INFO, TAG, "");
            Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

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

            Logging.LogMsg(LogLevel.INFO, TAG, "");
            Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

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

            Logging.LogMsg(LogLevel.INFO, TAG, "");
            Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

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

            Logging.LogMsg(LogLevel.INFO, TAG, "");
            Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

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

            Logging.LogMsg(LogLevel.INFO, TAG, "");
            Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

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

            Logging.LogMsg(LogLevel.INFO, TAG, "");
            Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

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

            Logging.LogMsg(LogLevel.INFO, TAG, "");
            Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

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

            Logging.LogMsg(LogLevel.INFO, TAG, "");
            Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

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

            Logging.LogMsg(LogLevel.INFO, TAG, "");
            Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

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

            Logging.LogMsg(LogLevel.INFO, TAG, "");
            Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

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

            Logging.LogMsg(LogLevel.INFO, TAG, "");
            Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

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

            Logging.LogMsg(LogLevel.INFO, TAG, "");
            Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

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

            Logging.LogMsg(LogLevel.INFO, TAG, "");
            Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

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

            Logging.LogMsg(LogLevel.INFO, TAG, "");
            Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

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
    class GameBoardBtnActionListener implements ActionListener {
        FunctionalTesting mTest;

        GameBoardBtnActionListener(FunctionalTesting test) {
            mTest = test;
        }

        @Override
        public void actionPerformed(ActionEvent e) { 

            if(mTest.mCurEventBasedFunctionalTest == 0) {
                mTest.resetTestMetrics();
            }

            mTest.mTstClassSuccess = checkClassSuccess(mTest.mTstClassSuccess, FunctionalTesting.mGameBoardTests[mTest.mCurEventBasedFunctionalTest].runTest(mTest));
            mTest.mCurEventBasedFunctionalTest++;

            if(mTest.mCurEventBasedFunctionalTest == FunctionalTesting.mGameBoardTests.length) {
                mTest.mNextGameBrdTestBtn.setEnabled(false);
                logSummary(TEST_BOARD_CLASS, mTest.mTstClassSuccess, mTest.mPassFailCtr);
                mTest.mCurEventBasedFunctionalTest = 0;
            }
        }
    }

    public void initializeGUI() {
        this.mPanel = new GameBoardGraphicsPanel();
        this.mApplication = new JFrame();
        this.mNextGameBrdTestListener = new GameBoardBtnActionListener(this);
        this.mNextGameBrdTestBtn = new JButton("Next GameBoard Test");
        this.mNextGameBrdTestBtn.addActionListener(mNextGameBrdTestListener);

        this.mApplication.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.mApplication.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        this.mApplication.add(this.mPanel, BorderLayout.CENTER);
        this.mApplication.add(this.mNextGameBrdTestBtn, BorderLayout.SOUTH);
        this.mApplication.setVisible(true); 
    }

    public static void main(String[] args) {
        FunctionalTesting test = new FunctionalTesting();
        boolean success = false;

        Logging.LogMsg(LogLevel.INFO, TAG, "Functional testing start...");

        //Run all automated tests
        success = test.runAutomatedTests();

        Logging.LogMsg(LogLevel.INFO, TAG, "Functional testing overall result: " + ((success == true) ? "PASS" : "FAIL"));

        test.initializeGUI();

        //Test game board
        //TODO: Determine if cmd line or build switch should automate
        //game board tests that are currently run manually and visually
        //inspected.  These tests can be run without visual inspection.
//        success = test.testGameBoard();
    }
}
