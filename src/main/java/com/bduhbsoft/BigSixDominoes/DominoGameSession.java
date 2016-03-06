package com.bduhbsoft.BigSixDominoes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.ArrayList;

/**
* Class DominoGameSession
*
* Contains the game session.  This includes the players of the game,
* the game board, the dominoe set and the rule settings for the that session
*/

public class DominoGameSession implements Serializable {

    private ArrayList<DominoePlayer> mPlayers;
    private DominoePlayer mWinner;
    private DominoePlayer mPlayerResigned;
    private ArrayList<Dominoe> mBoneYard;
    private DominoGameBoard mGameBoard;
    private DominoeGameOptions mOptions;
    private int mCurrentPlayer;
    private HashMap<DominoePlayer, ArrayList<Dominoe>> mPlayerHands;
    private DominoGameScoreboard mScoreboard;
    private boolean mGameCompleted;
    private boolean mGameLocked;

    public DominoGameSession(DominoeGameOptions options, ArrayList<DominoePlayer> players) {
        ArrayList<String> playerKeys = new ArrayList<>();
        mOptions = options;
        mPlayers = players;
        mGameBoard = new DominoGameBoard();
        mBoneYard = new ArrayList<Dominoe>();
        mPlayerHands = new HashMap<>();
        for(DominoePlayer curPlayer : mPlayers) {
            playerKeys.add(curPlayer.getUserName());
        }
        mScoreboard = new DominoGameScoreboard(playerKeys, mOptions.getScoreThreshold(), mOptions.getScoreMultiple());
        mGameCompleted = false;
        mGameLocked = false;
    }

}
