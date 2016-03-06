package com.bduhbsoft.BigSixDominoes;

import java.io.Serializable;
import java.util.ArrayList;
import com.bduhbsoft.BigSixDominoes.Dominoe.Orientation;
import com.bduhbsoft.BigSixDominoes.Logging.LogLevel;

/*
* Class DominoGameBoard
*
* Implements the game board the dominoes are played on.  It enforces
* if a dominoe can be played and determines if a given play results
* in a score
*/

public class DominoGameBoard implements Serializable {

    private static final String TAG = "DominoGameBoard";
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
    private boolean            mIsEmpty;
    private boolean            mSpinnerFlanked;
    //If the last domino was not committed or removed, the board is not open to accept
    //another domino
    private boolean            mBoardOpen;
    private Dominoe            mLastDom;

    //**************************Public Interface*****************************

    public DominoGameBoard() {
        mRow = mColumn = null;
        mIsEmpty = true;
        mSpinner = null;
        mSpinnerFlanked = false;
        mBoardOpen = true;
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
    //         WEST       ---        EAST
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

    void printList(ArrayList<Dominoe> list) {
        for(Dominoe dom : list) {
            Logging.LogMsg(LogLevel.TRACE, TAG, "    " + dom);
        }
    }

    /**
    * Removes last uncommitted domino from the game board
    */
    public void removeLast() {
        if(!mBoardOpen) {
            if(mRow != null) {
                Logging.LogMsg(LogLevel.TRACE, TAG, "removeLast, mLostDom: " + mLastDom + ", row size before: " + mRow.size());
                mRow.remove(mLastDom);
                Logging.LogMsg(LogLevel.TRACE, TAG, "removeLast, tried to remove domino, row size: " + mRow.size() + ", contents:");
                printList(mRow);
                if(mRow.isEmpty()) {
                    mRow = null;
                }
            }
            if(mColumn != null) {
                Logging.LogMsg(LogLevel.TRACE, TAG, "removeLast, mLostDom: " + mLastDom + ", col size before: " + mColumn.size());
                mColumn.remove(mLastDom);
                Logging.LogMsg(LogLevel.TRACE, TAG, "removeLast, tried to remove domino, col size: " + mColumn.size() + ", contents:");
                printList(mColumn);
                if(mColumn.isEmpty()) {
                    mColumn = null;
                }
            }

            if(mSpinner == mLastDom) {
                Logging.LogMsg(LogLevel.TRACE, TAG, "removeLast, last matched spinner, spinner set null");
                mSpinner = null;
            }

            mIsEmpty = (mRow == null && mColumn == null);
            Logging.LogMsg(LogLevel.TRACE, TAG, "removeLast, row is: " + ((mRow == null) ? "NULL" : "NOT NULL") +
                ", column is: " + ((mColumn == null) ? "NULL" : "NOT NULL") + ", mIsEmpty: " + mIsEmpty);
        }
    }

    /**
     * Commits the last domino played on the board.  Cannot be undone.
     */
    public void commitBoardState() {
        mBoardOpen = true;
        mLastDom = null;
    }

    /**
     * Attempts to add a domino to the game board, but does not commit the domino.
     * <p>
     * Returns true on successful result.
     * The game rules, board type and current configuration determine if adding the domino
     * is valid.
     * 
     * @param Dominoe     : The specific domino to add to the game board
     * @param EdgeLocation: Edge (or side) of the current configuration to add
     *                      the domino.  Note that when the board is empty, the specified
     *                      edge location will effect the domino orientation.
     * @return boolean    : True if the domino was added.  False if the domino could not be added
     */
    public boolean putDominoe(Dominoe theDominoe, EdgeLocation location) {
        boolean success = false;
        int idx = 0, addIdx = 0;;
        ArrayList<Dominoe> curList = null;
        Orientation targetOrtn = null;

        Logging.LogMsg(LogLevel.TRACE, TAG, "putDominoe");
        if(!mBoardOpen) {
            Logging.LogMsg(LogLevel.TRACE, TAG, "putDominoe, board not open because last domino not committed or removed");
            return success;
        }

        //Board is empty, set east and west pointers at least.  If double, then
        //also process as the first double.  If empty, board location is implied
        if (mIsEmpty) {
            Logging.LogMsg(LogLevel.TRACE, TAG, "putDominoe, empty board, adding domino: " + theDominoe);
            mIsEmpty = false;
            success = true;
            mRow = new ArrayList<Dominoe>();

            if (theDominoe.isDouble()) {
                setSpinner(theDominoe, getWestAddIdx());
            } else {
                Logging.LogMsg(LogLevel.TRACE, TAG, "putDominoe, added domino to row as only domino");
                targetOrtn = (location == EdgeLocation.EAST) ? Orientation.SIDE1_EAST : Orientation.SIDE1_WEST;
                theDominoe.setOrientation(targetOrtn);
                addDomino(theDominoe, mRow, 0);
            }

            return success;
        }

        //If trying to put a domino NORTH or SOUTH without the spinner already flanked
        //by the row dominoes, it fails automatically
        if(!mSpinnerFlanked && (location == EdgeLocation.NORTH || location == EdgeLocation.SOUTH)) {
            Logging.LogMsg(LogLevel.TRACE, TAG, "putDominoe, rejecting domino to board area: " +
                           location + ", because spinner flaked is: " + mSpinnerFlanked);
            return false;
        }

        //Setup variables for common processing below.
        switch(location) {
            case NORTH:
                idx = getNorthIdx();
                addIdx = getNorthAddIdx();
                curList = mColumn;
                targetOrtn = Orientation.SIDE1_NORTH;
                break;

            case SOUTH:
                idx = getSouthIdx();
                addIdx = getSouthAddIdx();
                curList = mColumn;
                targetOrtn = Orientation.SIDE1_SOUTH;
                break;

            case EAST:
                idx = getEastIdx();
                addIdx = getEastAddIdx();
                curList = mRow;
                targetOrtn = Orientation.SIDE1_EAST;
                break;

            case WEST:
                idx = getWestIdx();
                addIdx = getWestAddIdx();
                curList = mRow;
                targetOrtn = Orientation.SIDE1_WEST;
                break;
        }

        Logging.LogMsg(LogLevel.TRACE, TAG, "putDominoe, board not empty, adding domino to board area: " + location
                       + ", index: " + idx);

        if(idx != BAD_IDX) {
            if(checkMatch(theDominoe, curList.get(idx), targetOrtn)) {
                String domList = "";
                if(mSpinner == null && theDominoe.isDouble()) {
                    setSpinner(theDominoe, addIdx);
                } else {
                    addDomino(theDominoe, curList, addIdx);
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

        //Update the status of the spinner
        updatedSpinnerFlanked();
        return success;
    }

    /**
     * Returns the current board row.
     * <p>
     * @return ArrayList<Dominoe> : Row of the game board
     */
    public ArrayList<Dominoe> getRow() {
        return mRow;
    }

    /**
     * Returns the current board column.
     * <p>
     * @return ArrayList<Dominoe> : Column of the game board
     */
    public ArrayList<Dominoe> getColumn() {
        return mColumn;
    }

    private int getEastAddIdx() {
        if(mRow == null) {
            return BAD_IDX;
        }

        return mRow.size();
    }

    /**
     * Returns the current spinner
     * <p>
     * @return Dominoe : Board spinner
     */
    public Dominoe getSpinner() {
        return mSpinner;
    }


    //TODO: Consider making private
    public int getSpinnerRow() {
        return getSpinnerIdx(mRow);
    }

    //TODO: Consider making private
    public int getSpinnerColumn() {
        return getSpinnerIdx(mColumn);
    }

    /**
     * Returns board empty status
     * <p>
     * @return boolean : Status of board being empty
     */
    public boolean isEmpty() {
        return mIsEmpty;
    }

    /**
     * Returns board perimeter value
     * <p>
     * @return int : Current perimeter value
     */
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

    //********************************Private Functions*********************************
    private int getWestAddIdx() {
        if(mRow == null) {
            return BAD_IDX;
        }

        return 0;
    }

    private int getNorthAddIdx() {
        if(mColumn == null) {
            return BAD_IDX;
        }

        return 0;
    }

    private int getSouthAddIdx() {
        if(mColumn == null) {
            return BAD_IDX;
        }

        return mColumn.size();
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

    private void updatedSpinnerFlanked() {
        int spinnerRowIdx = getSpinnerRow();

        mSpinnerFlanked =
            (spinnerRowIdx > 0 && spinnerRowIdx < getEastIdx());

        Logging.LogMsg(LogLevel.DEBUG, TAG, "updatedSpinnerFlanked, spinner is flanked: " + mSpinnerFlanked);
    }

    private void addDomino(Dominoe dom, ArrayList<Dominoe> curList, int idx) {
        mBoardOpen = false;
        mLastDom = dom;
        curList.add(idx, dom);
    }

    private void setSpinner(Dominoe theDominoe, int rowIdx) {
        Logging.LogMsg(LogLevel.TRACE, TAG, "setSpinner first double played");
        theDominoe.setOrientation(Orientation.SIDE1_NORTH);
        mColumn = new ArrayList<Dominoe>();
        addDomino(theDominoe, mColumn, 0);
        mSpinner = theDominoe;

        //Add to row
        addDomino(theDominoe, mRow, rowIdx);
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

    private int getSpinnerIdx(ArrayList<Dominoe> list) {
        if(list == null || mSpinner == null) {
            return BAD_IDX;
        }

        return list.indexOf(mSpinner);
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

}
