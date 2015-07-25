package com.bduhbsoft.BigSixDominoes;


import java.io.Serializable;
import java.util.ArrayList;

/*
* Class DominoeGameSession
*
* Contains the game session.  This includes the players of the game,
* the game board, the dominoe set and the rule settings for the that session
*/

public class DominoeGameSession {

    private ArrayList<DominoePlayer> mPlayers;
    private ArrayList<Dominoe> mBoneYard;
    private DominoeGameBoard mGameBoard;
    private DominoeGameOptions mOptions;
    private int mCurrentPlayer;

    public DominoeGameSession(DominoeGameOptions options) {
        mOptions = options;
        mBoneYard = new ArrayList<Dominoe>();
    }

    public boolean addPlayer(DominoePlayer player) {
        boolean success = false;
        if (mPlayers.size() < mOptions.getNumPlayers()) {
            mPlayers.add(player);
            success = true;
        }

        return success;
    }
}
