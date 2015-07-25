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
    final String TEST_BOARD_CLASS = "Dominoe Class Test";
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

    public boolean testDominoe() {
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
        tstClassSuccess = success;
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
        tstClassSuccess = success;
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
        tstClassSuccess = success;
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

    public boolean testBoardFirstDouble() {
        boolean success = true, tempResult = false;
        DominoeGameBoard board = new DominoeGameBoard();
        ArrayList<Dominoe> row = null, col = null;
        Dominoe curDom;
        ArrayList<String> messages = new ArrayList<String>();
        int dblSide1 = 1, dblSide2 = 1;
        int regSide1 = 2, regSide2 = 3;
        final String TEST_NAME = "DominoeGameBoard: Add double to empty board";

        Logging.LogMsg(LogLevel.INFO, TAG, "");
        Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);
        resetTestMetrics();
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

        logSuccess(success, TEST_NAME, messages, mPassFailCtr);

        mTstClassSuccess = success;

        refreshDisplay(row, col, board.getSpinner(), board.getPerimTotal());

//        logSummary(TEST_CLASS, mTstClassSuccess, passFailCtr);
        return success;
    }

    private boolean testAddDominoes(Dominoe[] dom, int[] expectedTtl, boolean[] expectedSuccess,
                                    DominoeGameBoard board, EdgeLocation[] addLoc, ArrayList<String> messages) {
        boolean success = true, tempResult = false;
        int curTtl = 0;

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
            refreshDisplay(board.getRow(), board.getColumn(), board.getSpinner(), curTtl);

//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch(InterruptedException e) {
//                Logging.LogMsg(LogLevel.INFO, TAG, "testAddDominoes, caught exception: " + e);
//            }
//            try {
//                Thread.sleep(DOM_PLAY_SLEEP_TIME_MS);
//            } catch(InterruptedException ex) {
//                Thread.currentThread().interrupt();
//            }
        }

        return success;
    }

    public boolean testBoardRowOnlySpinner() {
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
        resetTestMetrics();
        //Case: Create a row of dominoes with a spinner, all dominoe succeed
        success = testAddDominoes(curDom, expectedTtl, expectedSuc, board, addLocation, messages);
        
        logSuccess(success, TEST_NAME, messages, mPassFailCtr);
//        refreshDisplay(board.getRow(), board.getColumn(), board.getSpinner());

        return success;
    }

    public boolean testBoardRowOnlyNoSpinner() {
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

        resetTestMetrics();
        //Case: Create a row of dominoes without a spinner, all dominoes succeed
        success = testAddDominoes(curDom, expectedTtl, expectedSuc, board, addLocation, messages);

        logSuccess(success, TEST_NAME, messages, mPassFailCtr);
//        refreshDisplay(board.getRow(), board.getColumn(), board.getSpinner());

        return success;
    }

    public boolean testBoardRowOnlySpinnerNotFirst() {
        boolean success = true, tempResult = false;
        DominoeGameBoard board = new DominoeGameBoard();
        Dominoe curDom[]           = new Dominoe[]      {new Dominoe(3, 2), new Dominoe(6, 3), new Dominoe(2, 2)};
        int expectedTtl[]          = new int[]          {                5,                 8,                10};
        EdgeLocation addLocation[] = new EdgeLocation[] {EdgeLocation.WEST, EdgeLocation.WEST, EdgeLocation.EAST};
        boolean expectedSuc[]      = new boolean[]      {             true,              true,              true};
        ArrayList<String> messages = new ArrayList<String>();
        final String TEST_NAME = "DominoeGameBoard: Create Single Row With Spinner Played NOT First";

        Logging.LogMsg(LogLevel.INFO, TAG, "");
        Logging.LogMsg(LogLevel.INFO, TAG, "Running: " + TEST_NAME);

        resetTestMetrics();
        //Case: Create a row of dominoes without a spinner, all dominoes succeed
        success = testAddDominoes(curDom, expectedTtl, expectedSuc, board, addLocation, messages);

        if(board.getSpinner() == null) {
            messages.add("Spinner is null.  Expected spinner to be present.");
            success = false;
        }

        logSuccess(success, TEST_NAME, messages, mPassFailCtr);
//        refreshDisplay(board.getRow(), board.getColumn(), board.getSpinner());

        return success;
    }

    private void refreshDisplay(ArrayList<Dominoe> row, ArrayList<Dominoe> col, Dominoe spinner, int points) {

        mPanel.setBoard(row, col, spinner, points);
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

    //Handler for clicking next game board test button
    class GameBoardBtnActionListener implements ActionListener {
        FunctionalTesting mTest;

        GameBoardBtnActionListener(FunctionalTesting test) {
            mTest = test;
        }

        @Override
        public void actionPerformed(ActionEvent e) { 
            mTest.mCurGameBoardTest++;

            mTest.mNextGameBrdTestBtn.setEnabled(false);
            if(mCurGameBoardTest == 0) {
                mTest.resetTestMetrics();
                mTstClassSuccess = mTest.testBoardFirstDouble();
                mTest.mNextGameBrdTestBtn.setEnabled(true);
            } else if(mCurGameBoardTest == 1) {
                mTstClassSuccess = mTest.testBoardRowOnlySpinner();
                mTest.mNextGameBrdTestBtn.setEnabled(true);
            } else if(mCurGameBoardTest == 2) {
                mTstClassSuccess = testBoardRowOnlyNoSpinner();
                mTest.mNextGameBrdTestBtn.setEnabled(true);
            } else if(mCurGameBoardTest == 3) {
                mTstClassSuccess = testBoardRowOnlySpinnerNotFirst();
                mTest.mNextGameBrdTestBtn.setEnabled(true);
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

        test.initializeGUI();

        Logging.LogMsg(LogLevel.INFO, TAG, "Functional testing start");

        //Test Dominoe class
        success = test.testDominoe();

        //Test game board
//        success = test.testGameBoard();
    }
}
