package com.bduhbsoft.BigSixDominoes;

import java.io.Serializable;
/**
* Class DominoPlayer
*
* Defines a DominoPlayer
*/

public class DominoPlayer implements Serializable {
    String mDisplayName;
    String mUserName;
    boolean mIsMyTurn;
    int mScore;
    PlayerType mType;

    /**
    * Enumerates the player type.  Player is either on the server
    * or available via direct bluetooth or wifi connection or is an
    * AI player
    */ 
    public enum PlayerType {
        /**
        * Server player turn is sent to the server for processing
        */ 
        Server,

        /**
        * AI players are procesed locally
        */ 
        AI,

        /**
        * Bluetooth player turn is sent via bluetooth connection directly to the player.
        */ 
        BlueTooth,

        /**
        * Wifi player turn is sent via wifi connection directly to the player.
        */ 
        Wifi
    }

    /**
    * Constructs new player
    *
    * @param displayName Name displayed during the game
    *
    * @param userName Players official username
    */ 
    public DominoPlayer(String displayName, String userName, PlayerType type) {
        mUserName = userName;
        mDisplayName = displayName;
        mIsMyTurn = false;
        mScore = 0;
        mType = type;
    }

    /**
    * Returns the display name of the player
    *
    * @return The display name
    */
    public String getDisplayName() {
        return mDisplayName;
    }

    /**
    * Returns the user name of the player.
    * <p/>
    * User name is guaranteed unique
    *
    * @return The user name
    */
    public String getUserName() {
        return mUserName;
    }

    /**
    * Returns the current player's turn status
    *
    * @return User's turn status
    */
    public boolean isMyTurn() {
        return mIsMyTurn;
    }

    /**
    * Returns the current player's score
    *
    * @return User's current score
    */
    public int getScore() {
        return mScore;
    }

    /**
    * Set's the current player's turn status
    * <p/>
    * Is not checked.  Containing structure repsonsible for managing
    *
    * @param myTurn User's turn status
    */
    public void setMyTurn(boolean myTurn) {
        mIsMyTurn = myTurn;
    }

    /**
    * Set's the current player's score
    * <p/>
    * Is not checked.  Containing structure repsonsible for managing
    *
    * @param newScore User's score
    */
    public void setScore(int newScore) {
        mScore = newScore;
    }
}
