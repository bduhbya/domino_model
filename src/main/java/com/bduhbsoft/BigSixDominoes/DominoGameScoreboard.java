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
 *
 * The score board only rejects points if the player is not found
 * to be on the score board.  If the score does not match the configured
 * multiple, then the points are ignored.  Looking up a player in the
 * score card is constant time.  This means the user of this class can
 * blindly add points after every turn to simplify the game logic.
 * */

public class DominoGameScoreboard {

    private Map<String, ArrayList<ScoreCardHouse>> mPlayerScoreCards;
    private static final int PLAYER_NOT_FOUND_POINTS = -1;
    private int mScoreMultiple;

    /**
    * Constructs score board.  Note, this should use the player username in case
    * display name changes
    *
    * @param players ArryList of player names for the scoreboard
    */ 
    public DominoGameScoreboard(ArrayList<String> players) {
        mPlayerScoreCards = new HashMap<String, ArrayList<ScoreCardHouse>>();
        mScoreMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;

        for(String player : players) {
            ArrayList<ScoreCardHouse> scoreCard = new ArrayList<ScoreCardHouse>();
            scoreCard.add(new ScoreCardHouse(mScoreMultiple));
            mPlayerScoreCards.put(player, scoreCard);
        }
    }

    /**
    * Constructs score board.  Note, this should use the player username in case
    * display name changes
    *
    * @param players ArryList of player names for the scoreboard
    * 
    * @param scoreMultiple Non default multiple for scoring
    */ 
    public DominoGameScoreboard(ArrayList<String> players, int scoreMultiple) {
        mPlayerScoreCards = new HashMap<String, ArrayList<ScoreCardHouse>>();
        mScoreMultiple = scoreMultiple;

        for(String player : players) {
            ArrayList<ScoreCardHouse> scoreCard = new ArrayList<ScoreCardHouse>();
            scoreCard.add(new ScoreCardHouse(mScoreMultiple));
            mPlayerScoreCards.put(player, scoreCard);
        }
    }

    /**
    * Adds specified number of points to players score.
    *
    * @param player name of player that scored. MUST match player used in 
    * the constructor
    *
    * @return True if player found and points attempted to be added, false otherwise
    */
    public boolean addPoints(String player, int points) {
        boolean found = false;
        boolean validScore = false;
        if(checkPlayer(player)) {
            validScore = addPlayerPoints(player, points);
            found = true;
        }

        //TODO: Add callback for valid score
        return found;
    }

    /**
    * Retrieves points for given player.
    *
    * @return -1 if player is not found
    */ 
    public int getPlayerPoints(String player) {
        if(checkPlayer(player)) {
            return getPoints(player);
        }

        return PLAYER_NOT_FOUND_POINTS;
    }

    /**
    * Returns the score card houses of a player.
    *
    * @return List of houses for a given player
    */
    public ArrayList<ScoreCardHouse> getPlayerScoreCardHouses(String player) {
        if(checkPlayer(player)) {
            return getScoreCardHouses(player);
        }

        return null;
    }

    /**
    * Returns the number of players on the score card
    *
    * @return Number of players on the score card
    */
    public int getNumPlayers() {
        return mPlayerScoreCards.size();
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

    private boolean addPlayerPoints(String player, int points) {
        boolean validScore = (points % 5 == 0) && (points > 0);

        if(validScore) {
            ArrayList<ScoreCardHouse> scoreCard = getScoreCardHouses(player);
            int lastScoreCardHouse = scoreCard.size() - 1;
            int leftOver = scoreCard.get(lastScoreCardHouse).addPoints(points);

            while(leftOver > 0) {
                ScoreCardHouse newScoreCardHouse = new ScoreCardHouse(mScoreMultiple);
                leftOver = newScoreCardHouse.addPoints(leftOver);
                scoreCard.add(newScoreCardHouse);
            }
        }

        return validScore;
    }

    private boolean checkPlayer(String player) {
        if(!mPlayerScoreCards.containsKey(player)) {
            return false;
        }

        return true;
    }
}
