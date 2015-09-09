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
    private int mCurGameBoardTest;
    private int[] mPassFailCtr;
    private boolean mTstClassSuccess;

    public FunctionalTesting() {
        mCurGameBoardTest = -1;
        mPassFailCtr = new int[PASS_FAIL_CNDS];
    }

    public interface iGameBoardTest { 
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
        }

        title += ": " + (success ? "SUCCESS" : "FAILED");
        refreshDisplay(board.getRow(), board.getColumn(), board.getSpinner(), curTtl, title);

        return success;
    }

    private boolean checkClassSuccess(boolean classScs, boolean caseScs) {
        return (classScs && caseScs);
    }

    public boolean runAutomatedTests() {
        boolean success;

        success = testDomino();
        success = testBoneYard();

        return success;
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
        tstClassSuccess = checkClassSuccess(tstClassSuccess, success);;
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
        tstClassSuccess = checkClassSuccess(tstClassSuccess, success);;
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
        tstClassSuccess = checkClassSuccess(tstClassSuccess, success);;
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

        logSummary(TEST_CLASS, tstClassSuccess, passFailCtr);
        return success;
    }

    public boolean testDomino() {
        boolean success = true, tstClassSuccess = true;
        int[] passFailCtr = new int[PASS_FAIL_CNDS];
        Dominoe tempDom;
        ArrayList<String> messages = new ArrayList<String>();
        int dblSide1 = 0, dblSide2 = 0;
        int regSide1 = 2, regSide2 = 3;
        ArrayList<Dominoe> domSet;
        final String TEST_CLASS = "Domino class testing";

        Logging.LogMsg(LogLevel.INFO, TAG, "");
        Logging.LogMsg(LogLevel.INFO, TAG, "Running Domnoe Class Tests");
        //Case create new double
        tempDom = new Dominoe(dblSide1, dblSide2);
        messages.add("Testing double: " + tempDom);
        if(!tempDom.isDouble()) {
            success = false;
            messages.add("isDouble returned false");
        }

        if(tempDom.getSide1() != dblSide1 || tempDom.getSide2() != dblSide2) {
            success = false;
            messages.add("Sides do not match created values.  Created: "
                         + dblSide1 + "|" + dblSide2 + ", found: " + tempDom);
        }

        logSuccess(success, "Dominoe: Create new double", messages, passFailCtr);

        //Case create non-double
        tstClassSuccess = checkClassSuccess(tstClassSuccess, success);;
        success = true;
        messages.clear();
        tempDom = new Dominoe(regSide1, regSide2);
        messages.add("Testing non-double: " + tempDom);
        if(tempDom.isDouble()) {
            success = false;
            messages.add("isDouble returned true");
        }

        if(tempDom.getSide1() != regSide1 || tempDom.getSide2() != regSide2) {
            success = false;
            messages.add("Sides do not match created values.  Created: "
                         + regSide1 + "|" + regSide2 + ", found: " + tempDom);
        }

        logSuccess(success, "Dominoe: Create new non-double", messages, passFailCtr);

        //Case set and get orientation
        tstClassSuccess = checkClassSuccess(tstClassSuccess, success);;
        success = true;
        messages.clear();
        ArrayList<Orientation> testOrtns = new ArrayList<Dominoe.Orientation>();
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

        logSuccess(success, "Dominoe: Set and get orientation", messages, passFailCtr);

        //Case get dominoe set
        tempDom = new Dominoe(dblSide1, dblSide2);
        messages.clear();
        tstClassSuccess = checkClassSuccess(tstClassSuccess, success);;
        success = true;
        domSet = Dominoe.getDominoeSet(SetType.DOUBLE_SIX);

        if(domSet == null) {
            success = false;
            messages.add("Function getDominoeSet returned null");
        }

        //TODO: Lookup clever java way to validate all dominoes are present in set
        for(Dominoe dom : domSet) {
            messages.add("Dominoe: " + dom);
        }

        logSuccess(success, "Dominoe: Get set of type " + SetType.DOUBLE_SIX, messages, passFailCtr);

        logSummary(TEST_CLASS, tstClassSuccess, passFailCtr);
        return success;
    }

    public static iGameBoardTest[] mGameBoardTests = {

        new iGameBoardTest() { @Override public boolean runTest(FunctionalTesting test) {
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

        new iGameBoardTest() { @Override public boolean runTest(FunctionalTesting test) {
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

        new iGameBoardTest() { @Override public boolean runTest(FunctionalTesting test) {
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

        new iGameBoardTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(3, 2), new Dominoe(6, 3), new Dominoe(2, 2)};
            int expectedTtl[]          = new int[]          {                5,                 8,                10};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.WEST, EdgeLocation.EAST};
            boolean expectedSuc[]      = new boolean[]      {             true,              true,              true};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Create Single Row With Spinner Played NOT First at East";

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

        new iGameBoardTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(3, 2), new Dominoe(6, 3), new Dominoe(2, 2)};
            int expectedTtl[]          = new int[]          {                5,                 8,                10};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.WEST, EdgeLocation.EAST};
            boolean expectedSuc[]      = new boolean[]      {             true,              true,              true};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Create Single Row With Spinner Played NOT First at East";

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

        new iGameBoardTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(6, 4), new Dominoe(4, 3), new Dominoe(6, 6)};
            int expectedTtl[]          = new int[]          {               10,                 9,                15};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.EAST, EdgeLocation.WEST};
            boolean expectedSuc[]      = new boolean[]      {             true,              true,              true};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Create Single Row With Spinner Played NOT First at West";

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

        new iGameBoardTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(6, 6), new Dominoe(6, 3), new Dominoe(5, 6),  new Dominoe(4, 6)};
            int expectedTtl[]          = new int[]          {               12,                15,                 8,                 12};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.EAST, EdgeLocation.WEST, EdgeLocation.NORTH};
            boolean expectedSuc[]      = new boolean[]      {             true,              true,              true,               true};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Create column with North Domino Only";

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

        new iGameBoardTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(6, 6), new Dominoe(6, 3), new Dominoe(5, 6),  new Dominoe(4, 6)};
            int expectedTtl[]          = new int[]          {               12,                15,                 8,                 12};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.EAST, EdgeLocation.WEST, EdgeLocation.SOUTH};
            boolean expectedSuc[]      = new boolean[]      {             true,              true,              true,               true};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Create column with South Domino Only";

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

        new iGameBoardTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(5, 5), new Dominoe(5, 0),  new Dominoe(5, 1)};
            int expectedTtl[]          = new int[]          {               10,                10,                 10};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.EAST, EdgeLocation.NORTH};
            boolean expectedSuc[]      = new boolean[]      {             true,              true,              false};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Add bad domino to north side with a east half-flanked spinner";

            Logging.LogMsg(LogLevel.INFO, TAG, "");
            Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

            //Case: Test playing a matchng domino to the NORTH side with a east half-flanked spinner
            success = test.testAddDominoes(curDom, expectedTtl, expectedSuc, board, addLocation, messages, TEST_NAME);

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new iGameBoardTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(5, 5), new Dominoe(5, 0),  new Dominoe(5, 1)};
            int expectedTtl[]          = new int[]          {               10,                10,                 10};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.EAST, EdgeLocation.SOUTH};
            boolean expectedSuc[]      = new boolean[]      {             true,              true,              false};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Add bad domino to south side with a east half-flanked spinner";

            Logging.LogMsg(LogLevel.INFO, TAG, "");
            Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

            //Case: Test playing a matchng domino to the SOUTH side with a east half-flanked spinner
            success = test.testAddDominoes(curDom, expectedTtl, expectedSuc, board, addLocation, messages, TEST_NAME);

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new iGameBoardTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(5, 5), new Dominoe(5, 0), new Dominoe(5, 1),  new Dominoe(6, 1)};
            int expectedTtl[]          = new int[]          {               10,                10,                 1,                  1};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.EAST, EdgeLocation.WEST, EdgeLocation.NORTH};
            boolean expectedSuc[]      = new boolean[]      {             true,              true,              true,              false};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Add bad domino to north side with a full-flanked spinner";

            Logging.LogMsg(LogLevel.INFO, TAG, "");
            Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

            //Case: Test playing a non-matchng domino to the NORTH side with a full-flanked spinner
            success = test.testAddDominoes(curDom, expectedTtl, expectedSuc, board, addLocation, messages, TEST_NAME);

            logSuccess(success, TEST_NAME, messages, test.mPassFailCtr);

            return success;
        }},

        new iGameBoardTest() { @Override public boolean runTest(FunctionalTesting test) {
            boolean success = true, tempResult = false;
            DominoeGameBoard board = new DominoeGameBoard();
            Dominoe curDom[]           = new Dominoe[]      {new Dominoe(5, 5), new Dominoe(5, 0), new Dominoe(5, 1),  new Dominoe(6, 1)};
            int expectedTtl[]          = new int[]          {               10,                10,                 1,                  1};
            EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.EAST, EdgeLocation.WEST, EdgeLocation.SOUTH};
            boolean expectedSuc[]      = new boolean[]      {             true,              true,              true,              false};
            ArrayList<String> messages = new ArrayList<String>();
            final String TEST_NAME = "DominoeGameBoard: Add bad domino to south side with a full-flanked spinner";

            Logging.LogMsg(LogLevel.INFO, TAG, "");
            Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

            //Case: Test playing a non-matchng domino to the SOUTH side with a full-flanked spinner
            success = test.testAddDominoes(curDom, expectedTtl, expectedSuc, board, addLocation, messages, TEST_NAME);

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
            mTest.mCurGameBoardTest++;

            if(mCurGameBoardTest == 0) {
                mTest.resetTestMetrics();
            }

            mTest.mNextGameBrdTestBtn.setEnabled(false);
            if(mCurGameBoardTest < FunctionalTesting.mGameBoardTests.length) {
                mTest.mTstClassSuccess = checkClassSuccess(mTstClassSuccess, FunctionalTesting.mGameBoardTests[mTest.mCurGameBoardTest].runTest(mTest));
                mTest.mNextGameBrdTestBtn.setEnabled(true);
            } else {
                logSummary(TEST_BOARD_CLASS, mTest.mTstClassSuccess, mTest.mPassFailCtr);
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

        Logging.LogMsg(LogLevel.INFO, TAG, "Functional testing start");

        //Run all automated tests
        success = test.runAutomatedTests();

        test.initializeGUI();

        //Test game board
        //TODO: Determine if cmd line or build switch should automate
        //game baord tests that are currently run manually and visually
        //inspected.  These tests can be run without visual inspection.
//        success = test.testGameBoard();
    }
}
