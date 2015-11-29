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
 * Also implements the classic line and circle score tracking
 * used when scoring domino games with pencil and paper which
 * avoids using tick marks or erasing or writing large columns of
 * numbers.
 * */

public class DominoGameScoreboard {

    private Map<String, ArrayList<ScoreCardHouse>> mPlayerScoreCards;

    /**
    * Constructs score board.  Note, this should use the player username in case
    * display name changes
    */ 
    public DominoGameScoreboard(ArrayList<String> players) {
        mPlayerScoreCards = new HashMap<String, ArrayList<ScoreCardHouse>>();

        for(String player : players) {
            ArrayList<ScoreCardHouse> scoreCard = new ArrayList<ScoreCardHouse>();
            scoreCard.add(new ScoreCardHouse());
            mPlayerScoreCards.put(player, scoreCard);
        }
    }

    /**
    * Adds specified number of points to players score.
    *
    * @param player name of player that scored. MUST match player used in 
    * the constructor
    *
    * @return True if player found and points added, false otherwise
    */
    public boolean addPoints(String player, int points) {
        boolean found = false;
        if(checkPlayer(player)) {
            addPlayerPoints(player, points);
            found = true;
        }
        return found;
    }

    /**
    * Retrieves points for given player.  If the player is not
    * in the board, throws exception
    */ 
    public int getPlayerPoints(String player) {
        if(checkPlayer(player)) {
            return getPoints(player);
        }

        return -1;
    }

    /**
    * Returns the scorecard of a player.
    *
    * @return List of houses for a given player
    */
    public ArrayList<ScoreCardHouse> getPlayerScoreCardHouses(String player) {
        if(checkPlayer(player)) {
            return getScoreCardHouses(player);
        }

        return null;
    }

    private int getPoints(String player) {
        ArrayList<ScoreCardHouse> scoreCard = getScoreCardHouses(player);
        int points = 0;

        for(ScoreCardHouse house : scoreCard) {
            points += house.getPoints();
        }

        return points;
    }

    private ArrayList<ScoreCardHouse> getScoreCardHouses(String player) {
        return mPlayerScoreCards.get(player);
    }

    private void addPlayerPoints(String player, int points) {
        ArrayList<ScoreCardHouse> scoreCard = getScoreCardHouses(player);
        int lastScoreCardHouse = scoreCard.size() - 1;
        int leftOver = scoreCard.get(lastScoreCardHouse).addPoints(points);

        while(leftOver > 0) {
            ScoreCardHouse newScoreCardHouse = new ScoreCardHouse();
            leftOver = newScoreCardHouse.addPoints(leftOver);
            scoreCard.add(newScoreCardHouse);
        }
    }

    private boolean checkPlayer(String player) {
        if(!mPlayerScoreCards.containsKey(player)) {
            return false;
        }

        return true;
    }
}
