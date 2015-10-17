package com.bduhbsoft.BigSixDominoes;

/**
* Class DominoeGameOptions
*
* Contains the options for a given game.  Things like the number of players,
* colors of the board, skin for dominoes, scoring threshhold and so on
*/

public class DominoeGameOptions {

    //Default options
    public static final int DEFAULT_PLAYERS = 2;
    public static final int SCORE_THRESHOLD = 150;
    public static final int MIN_START_SCORE = 0;
    public static final int DOM_PER_HAND = 9;

    //Number of players for the current game
    final int mNumPlayers;
    //Score needed to win a game
    final int mScoreThreshold;
    //Minimum score needed to get on the board and start scoring, typlically 0 or 10
    final int mMinStartingScore;
    //Dominoes each player gets at the start of hand, typically 7 or 9
    final int mNumDomPerHand;

    /**
    * Constructor expects all options
    */
    public DominoeGameOptions(int numPlayers, int scoreThreshold, int startScore, int domPerHand) {
        mNumPlayers = numPlayers;
        mScoreThreshold = scoreThreshold;
        mMinStartingScore = startScore;
        mNumDomPerHand = domPerHand;
    }

    /**
    * Returns number of players
    *
    * @return int : Number of players in the game
    */
    public int getNumPlayers() { return mNumPlayers; }

    /**
    * Returns scoring threshold
    *
    * @return int : Score needed to win the game
    */
    public int getScoreThreshold() { return mScoreThreshold; }

    /**
    * Returns minimum starting score
    *
    * @return int : minimum starting score
    */
    public int getMinStartingScore() { return mMinStartingScore; }

    /**
    * Returns number of dominoes per player per hand
    *
    * @return int : score multiple
    */
    public int getNumDomPerHand() { return mNumDomPerHand; }
}
