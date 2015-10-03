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

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getUserName() {
        return mUserName;
    }

    public boolean isMyTurn() {
        return mIsMyTurn;
    }

    public int getScore() {
        return mScore;
    }

    public void setMyTurn(boolean myTurn) {
        mIsMyTurn = myTurn;
    }

    public void setScore(int newScore) {
        mScore = newScore;
    }
}
