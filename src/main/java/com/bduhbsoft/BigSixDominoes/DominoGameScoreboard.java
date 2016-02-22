package com.bduhbsoft.BigSixDominoes;

import java.util.Map;
import java.util.Set;
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
    private int mScoreThreshold;

    /**
    * Constructs score board.  Note, this should use the player username in case
    * display name changes
    *
    * @param players ArryList of player names for the scoreboard
    *
    * @param threshold Maximum score to win the game
    */ 
    public DominoGameScoreboard(ArrayList<String> players, int threshold) {
        mPlayerScoreCards = new HashMap<String, ArrayList<ScoreCardHouse>>();
        mScoreMultiple = ScoreCardHouse.DEFAULT_MULTIPLE;
        mScoreThreshold = threshold;

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
    * @param threshold Maximum score to win the game
    * 
    * @param scoreMultiple Non default multiple for scoring
    */ 
    public DominoGameScoreboard(ArrayList<String> players, int threshold, int scoreMultiple) {
        mPlayerScoreCards = new HashMap<String, ArrayList<ScoreCardHouse>>();
        mScoreMultiple = scoreMultiple;
        mScoreThreshold = threshold;

        for(String player : players) {
            ArrayList<ScoreCardHouse> scoreCard = new ArrayList<ScoreCardHouse>();
            scoreCard.add(new ScoreCardHouse(mScoreMultiple));
            mPlayerScoreCards.put(player, scoreCard);
        }
    }

    /**
    * Adds specified number of points to a player's score.
    *
    * @param player Name of player that scored. MUST match player used in 
    * the constructor
    *
    * @param points Points added for the given player
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

        //TODO: Consider adding callback for valid score
        return found;
    }

    /**
    * Retrieves points for given player.
    *
    * @return Points for that player or -1 if player is not found
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

    /**
    * Returns set of players
    *
    * @return Set of players
    */
    public Set<String> getPlayers() {
        return mPlayerScoreCards.keySet();
    }

    private int getPoints(String player) {
        ArrayList<ScoreCardHouse> scoreCard = getScoreCardHouses(player);
        int points = 0;

        for(ScoreCardHouse house : scoreCard) {
            points += house.getPoints();
        }

        return points;
    }

    /**
    * Returns the scoring multiple
    *
    * @return Scoring multiple to make a valid score
    */
    public int getScoringMultiple() {
        return mScoreMultiple;
    }

    /**
    * Returns the scoring threshold
    *
    * @return Scoring threshold to win the game
    */
    public int getScoringThreshold() {
        return mScoreThreshold;
    }

    /**
    * Returns the maximum expected houses given the scoring threshold
    *
    * @return Maximum number of houses expected given the scoring threshold
    */
    public int getMaximumHouses() {
        int pointsPerHouse = mScoreMultiple * ScoreCardHouse.NUM_HOUSE_ELEMENTS;
        return (mScoreThreshold / pointsPerHouse) + 1;
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
