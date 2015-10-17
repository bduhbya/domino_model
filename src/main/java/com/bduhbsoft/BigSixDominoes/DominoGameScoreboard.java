package com.bduhbsoft.BigSixDominoes;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.lang.IndexOutOfBoundsException;

import com.bduhbsoft.BigSixDominoes.Logging.LogLevel;

/**
 * Class DominoGameScoreboard
 *
 * Tracks the score for each player.
 *
 * </p>
 *
 * Also implements the classic line and cirlce score tracking
 * used when scoring domino games with pencil and paper which
 * avoids using tick marks or erasing or writing large columns of
 * numbers.
 * */

public class DominoGameScoreboard {

    /**
    * State of each house quadrant.
    *
    * </p>
    *
    * Each quadrant of a house can be either a single line (5 points),
    * a cross/x (10 points) or a circle (10 points)
    */ 
    private enum QuadState {
        Empty,
        Line,
        Cross,
        Circle
    }

    /**
    * Tracks the state of each house of a classic score board.
    *
    * </p>
    *
    * Each quadrant holds up to 10 points and the base lines of the house
    * are worth 5 points each.  A house is worth 50 points when full.  The
    * quadrants of the house are described the same way the quadrants of
    * a Cartesian graph are described.
    *
    * </p>
    *
    * Each house fills up as points are added to
    * a players score.  If a line is already drawn in a quadrant, then it
    * can only transition to a cross.  If a quadrant is empty it can take
    * either a line or a circle.
    */
    private class House {
        private QuadState mQuadI;
        private QuadState mQuadII;
        private QuadState mQuadIII;
        private QuadState mQuadIV;
        private boolean mHorBase;
        private boolean mVertBase;

        /**
        * Constructs empty house.
        */
        public House() {
            mHorBase = false;
            mVertBase = false;
            mQuadI   = QuadState.Empty;
            mQuadII  = QuadState.Empty;
            mQuadIII = QuadState.Empty;
            mQuadIV  = QuadState.Empty;
        }
    }

    private Map<String, Integer> mPlayerScores;
    private Map<String, ArrayList<House>> mPlayerScoreCards;

    /**
    * Constructs score board.  Note, this should use the player username in case
    * display name changes
    */ 
    public DominoGameScoreboard(ArrayList<String> players) {
        mPlayerScores = new HashMap<String, Integer>();

        for(String player : players) {
            mPlayerScores.put(player, 0);
        }
    }

    /**
    * Adds specified number of points to players score.
    *
    * @param player name of player that scored. MUST match player used in 
    * the constructor
    */
    public void addPoints(String player, int points) {
        //TODO: Throw exception if player is not found
        if(checkPlayer(player)) {
            mPlayerScores.put(player, points);
        }
    }

    /**
    * Retrieves points for given player.  If the player is not
    * in the board, throws exception
    */ 
    public int getPlayerPoints(String player) {
        //TODO: Throw excpetion if player is not found
        if(checkPlayer(player)) {
            return mPlayerScores.get(player);
        }

        return -1;
    }

    private boolean checkPlayer(String player) throws IndexOutOfBoundsException {
        if(!mPlayerScores.containsKey(player)) {
            throw new IndexOutOfBoundsException("Player not in list of configured players");
        }

        return true;
    }
}
