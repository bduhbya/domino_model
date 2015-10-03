package com.bduhbsoft.BigSixDominoes;

/*
* Class DominoePlayer
*
* Defines a DominoePlayer
*/

public class DominoePlayer {
    String mDisplayName;
    String mUserName;
    boolean mIsMyTurn;
    int mScore;

    public DominoePlayer(String displayName, String userName) {
        mUserName = userName;
        mDisplayName = displayName;
        mIsMyTurn = false;
        mScore = 0;
    }

    /**
    * Returns the display name of the player
    *
    * @return String : the display name
    */
    public String getDisplayName() {
        return mDisplayName;
    }

    /**
    * Returns the user name of the player.
    * <p/>
    * User name is guaranteed unique
    *
    * @return String : the user name
    */
    public String getUserName() {
        return mUserName;
    }

    /**
    * Returns the current player's turn status
    *
    * @return boolean User's turn status
    */
    public boolean isMyTurn() {
        return mIsMyTurn;
    }

    /**
    * Returns the current player's score
    *
    * @return boolean User's turn status
    */
    public int getScore() {
        return mScore;
    }

    /**
    * Set's the current player's turn status
    * <p/>
    * Is not checked.  Containing structure repsonsible for managing
    *
    * @param boolean : User's turn status
    */
    public void setMyTurn(boolean myTurn) {
        mIsMyTurn = myTurn;
    }

    /**
    * Set's the current player's score
    * <p/>
    * Is not checked.  Containing structure repsonsible for managing
    *
    * @param int : User's score
    */
    public void setScore(int newScore) {
        mScore = newScore;
    }
}
