package com.bduhbsoft.BigSixDominoes;

import java.util.ArrayList;
import com.bduhbsoft.BigSixDominoes.Dominoe.Orientation;
import com.bduhbsoft.BigSixDominoes.Logging.LogLevel;

/*
* Class DominoeGameBoard
*
* Implements the game board the dominoes are played on.  It enforces
* if a dominoe can be played and determines if a given play results
* in a score
*/

public class DominoeGameBoard {

    private static final String TAG = "DominoeGameBoard";
    private static final int BAD_IDX = -1;

    //The board only consists of one row and one column.  ArrayList provides
    //enough flexability to conisder the start and end of the arrays as the
    //edged of the board.  The spinner denotes the intersection of the row
    //and column

    //For the row:      WEST        EAST
    //                 mRow[0] ... mRow[N]
    private ArrayList<Dominoe> mRow;

    //For the column:   NORTH           SOUTH
    //                 mColumn[0] ... mColumn[N]
    private ArrayList<Dominoe> mColumn;
    private Dominoe mSpinner;

    //Internal tracking variables
    private boolean mIsEmpty;

    public DominoeGameBoard() {
        mRow = mColumn = null;
        mIsEmpty = true;
        mSpinner = null;
        Logging.LogMsg(LogLevel.TRACE, TAG, "Constructor");
    }

    // Location assuming two dimentional north-down view of the game board and refering
    // to the edges of the dominoe layout on the board.  Example visualization:
    //
    //                   NORTH
    //                    ---
    //                   | 1 |
    //                    ---
    //                   | 5 |
    //                    ---
    //         EAST       ---        WEST
    //           --- --- | 5 | --- ---
    //          | 0 | 5 | --- | 5 | 2 |
    //           --- --- | 5 | --- ---
    //                    ---
    //                    ---
    //                   | 5 |
    //                    ---
    //                   | 3 |
    //                    ---
    //                   SOUTH
    //
    public enum EdgeLocation {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    private int getEastIdx() {
        if(mRow == null) {
            return BAD_IDX;
        }

        return mRow.size() - 1;
    }

    private int getWestIdx() {
        if(mRow == null) {
            return BAD_IDX;
        }

        return 0;
    }

    private int getNorthIdx() {
        if(mColumn == null) {
            return BAD_IDX;
        }

        return 0;
    }

    private int getSouthIdx() {
        if(mColumn == null) {
            return BAD_IDX;
        }

        return mColumn.size() - 1;
    }

    private void addDomino(Dominoe dom, ArrayList<Dominoe> curList, int idx) {
        if(idx == 0) {
            curList.add(idx, dom);
        } else {
            curList.add(dom);
        }
    }

    private void setSpinner(Dominoe theDominoe, int rowIdx) {
        Logging.LogMsg(LogLevel.TRACE, TAG, "setSpinner first double played");
        theDominoe.setOrientation(Orientation.SIDE1_NORTH);
        mColumn = new ArrayList<Dominoe>();
        mColumn.add(theDominoe);
        mSpinner = theDominoe;

        //Add to row
        addDomino(theDominoe, mRow, rowIdx);
    }

    public boolean putDominoe(Dominoe theDominoe, EdgeLocation location) {
        boolean success = false;
        int idx = 0;
        ArrayList<Dominoe> curList = null;
        Orientation targetOrtn = null;

        Logging.LogMsg(LogLevel.TRACE, TAG, "putDominoe");
        //Board is empty, set east and west pointers at least.  If double, then
        //also process as the first double.  If empty, board location is implied
        if (mIsEmpty) {
            Logging.LogMsg(LogLevel.TRACE, TAG, "putDominoe, empty board, adding domino: " + theDominoe);
            mIsEmpty = false;
            success = true;
            mRow = new ArrayList<Dominoe>();

            if (theDominoe.isDouble()) {
                setSpinner(theDominoe, getWestIdx());
            } else {
                Logging.LogMsg(LogLevel.TRACE, TAG, "putDominoe, added domino to row as only domino");
                theDominoe.setOrientation(Orientation.SIDE1_WEST);
                mRow.add(theDominoe);
            }

            return success;
        }

        //Setup variables for common processing below.
        switch(location) {
            case NORTH:
                idx = getNorthIdx();
                curList = mColumn;
                targetOrtn = Orientation.SIDE1_NORTH;
                break;

            case SOUTH:
                idx = getSouthIdx();
                curList = mColumn;
                targetOrtn = Orientation.SIDE1_SOUTH;
                break;

            case EAST:
                idx = getEastIdx();
                curList = mRow;
                targetOrtn = Orientation.SIDE1_EAST;
                break;

            case WEST:
                idx = getWestIdx();
                curList = mRow;
                targetOrtn = Orientation.SIDE1_WEST;
                break;
        }

        Logging.LogMsg(LogLevel.TRACE, TAG, "putDominoe, board not empty, adding dominoe to board area: " + location
                       + ", index: " + idx);

        if(idx != BAD_IDX) {
            if(checkMatch(theDominoe, curList.get(idx), targetOrtn)) {
                String domList = "";
                if(mSpinner == null && theDominoe.isDouble()) {
                    setSpinner(theDominoe, idx);
                } else {
                    addDomino(theDominoe, curList, idx);
                }

                success = true;
                Logging.LogMsg(LogLevel.TRACE, TAG, "putDominoe, added domino to list, new list:");
                for(Dominoe dom : curList) {
                    if(dom.getOrientation() == Orientation.SIDE1_NORTH || 
                       dom.getOrientation() == Orientation.SIDE1_WEST    ) {
                        domList += "" + dom.getSide1() + "|" + dom.getSide2() + "  ";
                    } else if(dom.getOrientation() == Orientation.SIDE1_SOUTH || 
                              dom.getOrientation() == Orientation.SIDE1_EAST    ) {
                        domList += "" + dom.getSide2() + "|" + dom.getSide1() + "  ";
                    }
                }
                Logging.LogMsg(LogLevel.TRACE, TAG, "    " + domList);
            } else {
                Logging.LogMsg(LogLevel.TRACE, TAG, "putDominoe, rejected domino: " + theDominoe);
            }
        }

        return success;
    }

    boolean checkMatch(Dominoe newDom, Dominoe boardDom, Orientation targetOrtn) {
        boolean success = false;
        int curSide = 0;
        boolean matchSd1 = false;
        Orientation newOrtn = null;

        Logging.LogMsg(LogLevel.TRACE, TAG, "checkMatch, checking match between " + newDom + " and " + boardDom);
        //The target orientation is used for efficient processing.  Instead of using
        //a switch statement, check if the board dominoe matches the target orientation.
        //If so, check against side 1, if not just check side 2.  This allows us to NOT
        //have special processing for doubles
        if(boardDom.getOrientation() == targetOrtn) {
            curSide = boardDom.getSide1();
        } else {
            curSide = boardDom.getSide2();
        }

        Logging.LogMsg(LogLevel.TRACE, TAG, "checkMatch, checking domino against side: " + curSide);
        matchSd1 = newDom.getSide1() == curSide;

        success = (newDom.getSide1() == curSide ||
                   newDom.getSide2() == curSide   );

//        Logging.LogMsg(LogLevel.TRACE, TAG, "checkMatch, matchSd1 (of new domino): " + matchSd1 + ", success (matches either side): " + success);

        if(success) {
            //If side one matches the target, turn the domino the opposite way.  Doubles
            //go sideways
//            Logging.LogMsg(LogLevel.TRACE, TAG, "checkMatch, target orientation: " + targetOrtn);
            switch(targetOrtn) {
                case SIDE1_NORTH:

                    if(newDom.isDouble()) {
//                        Logging.LogMsg(LogLevel.TRACE, TAG, "checkMatch, new domino is double");
                        newOrtn = Orientation.SIDE1_EAST;
                    } else {
                        newOrtn = matchSd1 ? Orientation.SIDE1_SOUTH :
                                  Orientation.SIDE1_NORTH;
                    }
                    break;

                case SIDE1_SOUTH:
                    if(newDom.isDouble()) {
//                        Logging.LogMsg(LogLevel.TRACE, TAG, "checkMatch, new domino is double");
                        newOrtn = Orientation.SIDE1_EAST;
                    } else {
                        newOrtn = matchSd1 ? Orientation.SIDE1_NORTH :
                                  Orientation.SIDE1_SOUTH;
                    }
                    break;

                case SIDE1_EAST:
                    if(newDom.isDouble()) {
//                        Logging.LogMsg(LogLevel.TRACE, TAG, "checkMatch, new domino is double");
                        newOrtn = Orientation.SIDE1_NORTH;
                    } else {
                        newOrtn = matchSd1 ? Orientation.SIDE1_WEST :
                                  Orientation.SIDE1_EAST;
                    }
                    break;

                case SIDE1_WEST:
                    if(newDom.isDouble()) {
//                        Logging.LogMsg(LogLevel.TRACE, TAG, "checkMatch, new domino is double");
                        newOrtn = Orientation.SIDE1_NORTH;
                    } else {
                        newOrtn = matchSd1 ? Orientation.SIDE1_EAST :
                                  Orientation.SIDE1_WEST;
                    }

                    break;

            }

            Logging.LogMsg(LogLevel.TRACE, TAG, "checkMatch, set domino orientation: " + newOrtn);
            newDom.setOrientation(newOrtn);
        }

        return success;
    }

    public ArrayList<Dominoe> getRow() {
        return mRow;
    }

    public ArrayList<Dominoe> getColumn() {
        return mColumn;
    }

    private int getSpinnerIdx(ArrayList<Dominoe> list) {
        if(list == null || mSpinner == null) {
            return -1;
        }

        return list.indexOf(mSpinner);
    }

    public Dominoe getSpinner() {
        return mSpinner;
    }

    public int getSpinnerRow() {
        return getSpinnerIdx(mRow);
    }

    public int getSpinnerColumn() {
        return getSpinnerIdx(mColumn);
    }

    public boolean isEmpty() {
        return mIsEmpty;
    }

    private int getEdgeVal(Dominoe curDom, Orientation ortn, EdgeLocation edge) {
        int retVal = 0;
        if(curDom.isDouble()) {
            return curDom.getSide1() + curDom.getSide2();
        }

        switch(edge) {
            case NORTH:
                if(ortn == Orientation.SIDE1_NORTH) {
                    retVal = curDom.getSide1();
                } else {
                    retVal = curDom.getSide2();
                }
                break;

            case SOUTH:
                if(ortn == Orientation.SIDE1_SOUTH) {
                    retVal = curDom.getSide1();
                } else {
                    retVal = curDom.getSide2();
                }
                break;

            case EAST:
                if(ortn == Orientation.SIDE1_EAST) {
                    retVal = curDom.getSide1();
                } else {
                    retVal = curDom.getSide2();
                }
                break;

            case WEST:
                if(ortn == Orientation.SIDE1_WEST) {
                    retVal = curDom.getSide1();
                } else {
                    retVal = curDom.getSide2();
                }
                break;
        }

        return retVal;
    }

    public int getPerimTotal() {
        int eastVal = 0, westVal = 0, northVal = 0, southVal = 0;
        int eastIdx, westIdx, northIdx, southIdx;
        Dominoe curDom;

        eastIdx = getEastIdx();
        //THe row is ALWAYS created and if it doesn't exist, the board should be empty
        if(eastIdx == BAD_IDX) {
            return 0;
        }

        //If there is a valid domino in the east location, just use it
        curDom = mRow.get(eastIdx);
        Logging.LogMsg(LogLevel.TRACE, TAG, "getPerimTotal, getting value for east domino: " + curDom + ", orientation: " +
                       curDom.getOrientation() + ", index: " + eastIdx);
        eastVal = getEdgeVal(curDom, curDom.getOrientation(), EdgeLocation.EAST);
        Logging.LogMsg(LogLevel.TRACE, TAG, "getPerimTotal, got east value: " + eastVal);

        //If there is only one domino and it is NOT a double, we need the other side to add
        //to the total.  If there is more than one, add it as normal
        westIdx = getWestIdx();
        if((westIdx == eastIdx && !curDom.isDouble()) ||
           (westIdx != eastIdx                      )   ) {
            curDom = mRow.get(westIdx);
            westVal = getEdgeVal(curDom, curDom.getOrientation(), EdgeLocation.WEST);
            Logging.LogMsg(LogLevel.TRACE, TAG, "getPerimTotal, getting value for west domino: " + curDom + ", orientation: " +
                           curDom.getOrientation() + ", index: " + westIdx);
            Logging.LogMsg(LogLevel.TRACE, TAG, "getPerimTotal, got west value: " + westVal);
        }

        //The spinner itself doesn't count towards the total from the column
        //The column never has one domino that is used for both north and south (unlike the row)
        //So it is not needed to handle the column the same way as the row
        northIdx = getNorthIdx();
        if(northIdx != BAD_IDX) {
            curDom = mColumn.get(northIdx);
            if(curDom != mSpinner) {
                northVal = getEdgeVal(curDom, curDom.getOrientation(), EdgeLocation.NORTH);
                Logging.LogMsg(LogLevel.TRACE, TAG, "getPerimTotal, got north value: " + northVal);
            }
        }

        southIdx = getSouthIdx();
        if(southIdx != BAD_IDX) {
            curDom = mColumn.get(southIdx);
            if(curDom != mSpinner) {
                southVal = getEdgeVal(curDom, curDom.getOrientation(), EdgeLocation.SOUTH);
                Logging.LogMsg(LogLevel.TRACE, TAG, "getPerimTotal, got south value: " + southVal);
            }
        }

        return (eastVal + westVal + northVal + southVal);
    }
}
