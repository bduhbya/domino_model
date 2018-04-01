package com.bduhbsoft.BigSixDominoes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import com.bduhbsoft.BigSixDominoes.Domino.Orientation;
import com.bduhbsoft.BigSixDominoes.Logging.LogLevel;

/**
* Class DominoGameBoardDoubleSix
*
* Implements the game board for double six domino game variant.  It enforces
* if a dominoe can be played and determines if a given play results
* in a score.
*/

public class DominoGameBoardDoubleSix extends DominoMultiPlayerGameBoard {

    private static final String TAG = CHILD_TAG = "DominoGameBoardDoubleSix";
    private static final int BAD_IDX = -1;
    private static final int NO_DOMINO = -1;
    private static final int MAX_DOM_COUNT = 7; //Max of domino types
    private static final int MIN_DOM_TYPE = 0; //Min domino types

    //Dominoes on the board are modelled by row and column array.  The
    //spinner is the interection of the arrays

    //For the row:      WEST        EAST
    //                 mRow[0] ... mRow[N]
    private ArrayList<Domino> mRow;

    //For the column:   NORTH           SOUTH
    //                 mColumn[0] ... mColumn[N]
    private ArrayList<Domino> mColumn;
    private Domino mSpinner;

    //Internal tracking variables
    private boolean  mSpinnerFlanked;
    private int[]    mTypeCount; //Tracks number of each domino type played

    //**************************Public Interface*****************************

    //Functions implemented from super class are no documented since they are
    //documented in the super class
    public DominoGameBoardDoubleSix() {
        super();
        mRow = mColumn = null;
        mSpinner = null;
        mSpinnerFlanked = false;
        initTypeCounts();
        Logging.LogMsg(LogLevel.TRACE, TAG, "Constructor");
    }

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

    public void commitBoardState() {
        if(mLastDom != null) {
            //Update count of domino types and check if board is now locked
            //A double is only counted once
            mTypeCount[mLastDom.getSide1()] += 1;
            if (!mLastDom.isDouble()) mTypeCount[mLastDom.getSide2()] += 1;
            updateBoardLocked();
        }
        mBoardOpen = true;
        mLastDom = null;
    }

    public boolean putDomino(Domino theDomino, EdgeLocation location) {
        boolean success = false;
        int idx = 0, addIdx = 0;;
        ArrayList<Domino> curList = null;
        Orientation targetOrtn = null;

        //TODO: Throw exception for unsupported edge values

        Logging.LogMsg(LogLevel.TRACE, TAG, "putDominoe");
        if(!mBoardOpen) {
            Logging.LogMsg(LogLevel.TRACE, TAG, "putDominoe, board not open because last domino not committed or removed");
            return success;
        }

        //Board is empty, set east and west pointers at least.  If double, then
        //also process as the first double.  If empty, board location is implied
        if (mIsEmpty) {
            Logging.LogMsg(LogLevel.TRACE, TAG, "putDominoe, empty board, adding domino: " + theDomino);
            mIsEmpty = false;
            success = true;
            mRow = new ArrayList<Domino>();

            if (theDomino.isDouble()) {
                setSpinner(theDomino, getWestAddIdx());
            } else {
                Logging.LogMsg(LogLevel.TRACE, TAG, "putDominoe, added domino to row as only domino");
                targetOrtn = (location == EdgeLocation.EAST) ? Orientation.SIDE1_EAST : Orientation.SIDE1_WEST;
                theDomino.setOrientation(targetOrtn);
                addDomino(theDomino, mRow, 0);
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

            default:
                return false;
        }

        Logging.LogMsg(LogLevel.TRACE, TAG, "putDominoe, board not empty, adding domino to board area: " + location
                       + ", index: " + idx);

        if(idx != BAD_IDX) {
            if(checkMatch(theDomino, curList.get(idx), targetOrtn)) {
                String domList = "";
                if(mSpinner == null && theDomino.isDouble()) {
                    setSpinner(theDomino, addIdx);
                } else {
                    addDomino(theDomino, curList, addIdx);
                }

                success = true;
                Logging.LogMsg(LogLevel.TRACE, TAG, "putDominoe, added domino to list, new list:");
                for(Domino dom : curList) {
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
                Logging.LogMsg(LogLevel.TRACE, TAG, "putDominoe, rejected domino: " + theDomino);
            }
        }

        //Update the status of the spinner
        updatedSpinnerFlanked();
        return success;
    }

    public List<Domino> getDomList(EdgeLocation location) {
        switch(location) {
            case NORTH:
            case SOUTH:
                return mColumn;

            case EAST:
            case WEST:
                return mRow;

            default:
                throw new IllegalArgumentException("Unsupported edge location: " + location.name());
        }
    }

    public Domino getPivotDom() {
        return mSpinner;
    }

    public int getPerimTotal() {
        int eastVal = 0, westVal = 0, northVal = 0, southVal = 0;
        int eastIdx, westIdx, northIdx, southIdx;
        Domino curDom;

        eastIdx = getEastIdx();
        //The row is ALWAYS created and if it doesn't exist, the board should be empty
        if(eastIdx == BAD_IDX) {
            return 0;
        }

        //If there is a valid domino in the east location, just use it
        curDom = mRow.get(eastIdx);
        Logging.LogMsg(LogLevel.TRACE, TAG, "getPerimTotal, getting value for east domino: " + curDom + ", orientation: " +
                       curDom.getOrientation() + ", index: " + eastIdx);
        eastVal = getEdgeTotal(curDom, curDom.getOrientation(), EdgeLocation.EAST);
        Logging.LogMsg(LogLevel.TRACE, TAG, "getPerimTotal, got east value: " + eastVal);

        //If there is only one domino and it is NOT a double, we need the other side to add
        //to the total.  If there is more than one, add it as normal
        westIdx = getWestIdx();
        if((westIdx == eastIdx && !curDom.isDouble()) ||
           (westIdx != eastIdx                      )   ) {
            curDom = mRow.get(westIdx);
            westVal = getEdgeTotal(curDom, curDom.getOrientation(), EdgeLocation.WEST);
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
                northVal = getEdgeTotal(curDom, curDom.getOrientation(), EdgeLocation.NORTH);
                Logging.LogMsg(LogLevel.TRACE, TAG, "getPerimTotal, got north value: " + northVal);
            }
        }

        southIdx = getSouthIdx();
        if(southIdx != BAD_IDX) {
            curDom = mColumn.get(southIdx);
            if(curDom != mSpinner) {
                southVal = getEdgeTotal(curDom, curDom.getOrientation(), EdgeLocation.SOUTH);
                Logging.LogMsg(LogLevel.TRACE, TAG, "getPerimTotal, got south value: " + southVal);
            }
        }

        return (eastVal + westVal + northVal + southVal);
    }

    //********************************Private Functions*********************************
    private void updateBoardLocked() {
        //If the all the dominoes matching the edge values are on the board, it is locked
        int value;
        Set<Integer> edgeTypes = new HashSet<>();
        for(EdgeLocation loc : EdgeLocation.values()) {
            value = getEdgeVal(loc);
            if(value >= MIN_DOM_TYPE && value < MAX_DOM_COUNT)
                edgeTypes.add(value);
        }

        mIsLocked = true;
        for(int type : edgeTypes) {
            if(mTypeCount[type] < MAX_DOM_COUNT) {
                mIsLocked = false;
                break;
            }
        }
    }

    private void initTypeCounts() {
        mTypeCount = new int[MAX_DOM_COUNT];

        for(int curType = 0; curType < MAX_DOM_COUNT; curType++)
            mTypeCount[curType] = 0;
    }

    private int getSpinnerRow() {
        return getSpinnerIdx(mRow);
    }

    private int getSpinnerColumn() {
        return getSpinnerIdx(mColumn);
    }

    private int getEastAddIdx() {
        if(mRow == null) {
            return BAD_IDX;
        }

        return mRow.size();
    }

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

    //Get the value of domino at the specified edge location
    private int getEdgeVal(EdgeLocation loc) {
        int idx = BAD_IDX;
        Domino curDom;
        ArrayList<Domino> curList = null;
        switch(loc) {
            case NORTH:
                idx = getNorthIdx();
                curList = mColumn;
                break;
            case SOUTH:
                idx = getSouthIdx();
                curList = mColumn;
                break;
            case EAST:
                idx = getEastIdx();
                curList = mRow;
                break;
            case WEST:
                idx = getWestIdx();
                curList = mRow;
                break;
            default:
                idx = BAD_IDX;
        }

        if(idx == BAD_IDX)
            return NO_DOMINO;

        curDom = curList.get(idx);
        if(curDom.isDouble())
            return mSpinner.getSide1();

        //Get the total for the edge if not a double.  We use this to ensure we
        //return the correct side of the domino
        return getEdgeTotal(curDom, curDom.getOrientation(), loc);
    }

    private void updatedSpinnerFlanked() {
        int spinnerRowIdx = getSpinnerRow();

        mSpinnerFlanked =
            (spinnerRowIdx > 0 && spinnerRowIdx < getEastIdx());

        Logging.LogMsg(LogLevel.DEBUG, TAG, "updatedSpinnerFlanked, spinner is flanked: " + mSpinnerFlanked);
    }

    private void addDomino(Domino dom, ArrayList<Domino> curList, int idx) {
        mBoardOpen = false;
        mLastDom = dom;
        curList.add(idx, dom);
    }

    private void setSpinner(Domino theDomino, int rowIdx) {
        Logging.LogMsg(LogLevel.TRACE, TAG, "setSpinner first double played");
        theDomino.setOrientation(Orientation.SIDE1_NORTH);
        mColumn = new ArrayList<Domino>();
        addDomino(theDomino, mColumn, 0);
        mSpinner = theDomino;

        //Add to row
        addDomino(theDomino, mRow, rowIdx);
    }

    //Checks if the newDom matches the domino on the board location
    private boolean checkMatch(Domino newDom, Domino boardDom, Orientation targetOrtn) {
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

    private int getSpinnerIdx(ArrayList<Domino> list) {
        if(list == null || mSpinner == null) {
            return BAD_IDX;
        }

        return list.indexOf(mSpinner);
    }

    //Gets the total of a single edge.  Total may be the exposed side, or for the double the
    //the sum of the two sides
    private int getEdgeTotal(Domino curDom, Orientation ortn, EdgeLocation edge) {
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
