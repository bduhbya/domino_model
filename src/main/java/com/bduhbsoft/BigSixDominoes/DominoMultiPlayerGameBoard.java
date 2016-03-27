package com.bduhbsoft.BigSixDominoes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.bduhbsoft.BigSixDominoes.Domino.Orientation;
import com.bduhbsoft.BigSixDominoes.Logging.LogLevel;

/**
* Abstract Class DominoMultiPlayerGameBoard
*
* Defines generic domino board for multi-player games.
*/

public abstract class DominoMultiPlayerGameBoard implements Serializable {

    //Internal tracking variables
    protected boolean           mIsEmpty;
    //If the last domino was not committed or removed, the board is not open to accept
    //another domino
    protected boolean           mBoardOpen;
    protected Domino            mLastDom;

    protected static String CHILD_TAG; //Set by extending class so that shared logging is easliy understood

    //**************************Public Interface*****************************

    public DominoMultiPlayerGameBoard() {
        mIsEmpty = true;
        mBoardOpen = true;
        Domino mLastDom = null;
    }

    // Location assuming two dimentional north-up view of the game board and refering
    // to the edges of the dominoe layout on the board.  The edge location is generalized
    // for standard domino games, Mexican Train, Chicken Foot and Spinner.  Example visualization:
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
    //                      NORTH_WEST   NORTH      NORTH_EAST
    //                                       
    //                            / \     ---     / \
    //                           \ 9 \   | 1 |   / 8 /
    //                            \ / \   ---   / \ /
    //                             \ 5 \ | 5 | / 5 /
    //                     WEST     \ /   ---   \ /   EAST
    //                         --- ---  --- ---  --- ---
    //                        | 0 | 5 || 5 | 5 || 5 | 2 |
    //                         --- ---  --- ---  --- ---
    //                                    ---
    //                                   | 5 |     
    //                                    ---
    //                                   | 3 |     
    //                                    --- 
    //                   SOUTH_WEST      SOUTH       SOUTH_EAST
    //
    public enum EdgeLocation {
        NORTH,
        SOUTH,
        EAST,
        WEST,
        NORTH_EAST,
        NORTH_WEST,
        SOUTH_EAST,
        SOUTH_WEST
    }

    protected void printList(ArrayList<Domino> list) {
        for(Domino dom : list) {
            Logging.LogMsg(LogLevel.TRACE, CHILD_TAG, "    " + dom);
        }
    }

    /**
    * Removes last uncommitted domino from the game board
    */
    public abstract void removeLast();

    /**
     * Commits the last domino played on the board.  Cannot be undone.
     */
    public abstract void commitBoardState();

    /**
     * Attempts to add a domino to the game board, but does not commit the domino.
     * <p>
     * Returns true on successful result.
     * The game rules, board type and current configuration determine if adding the domino
     * is valid.
     * 
     * @param theDomino The specific domino to add to the game board
     * @param location Edge (or side) of the current configuration to add
     *                     the domino.  Note that when the board is empty, the specified
     *                     edge location will effect the domino orientation.
     * @return  True if the domino was added.  False if the domino could not be added
     */
    public abstract boolean putDomino(Domino theDomino, EdgeLocation location);

    /**
     * Returns the current board row for the request edge location.
     * <p>
     * 
     * @param location Edge location to get list of dominoes
     * @return List of dominoes for the corresponding edge location
     */
    public abstract List<Domino> getDomList(EdgeLocation location);

    /**
     * Returns the current pivot domino for the board.
     * <p>
     * The specific meaning of the pivot domino is game specific
     * @return Pivot domino for the board
     */
    public abstract Domino getPivotDom();

    /**
     * Returns board empty status
     * <p>
     * @return Status of board being empty
     */
    public boolean isEmpty() {
        return mIsEmpty;
    }

    /**
     * Returns board perimeter value
     * <p>
     * @return Current perimeter value
     */
    public abstract int getPerimTotal();
}
