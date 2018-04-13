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

    private ArrayList<DominoPlayer> mPlayers;
    private DominoPlayer mWinner;
    private DominoPlayer mPlayerResigned;
    private ArrayList<Domino> mBoneYard;
    private DominoMultiPlayerGameBoard mGameBoard;
    private DominoGameOptions mOptions;
    private int mCurrentPlayer;
    private HashMap<DominoPlayer, ArrayList<Domino>> mPlayerHands;
    private DominoGameScoreboard mScoreboard;
    private boolean mGameCompleted;
    private boolean mGameLocked;

    public DominoGameSession(DominoGameOptions options, ArrayList<DominoPlayer> players) {
        ArrayList<String> playerKeys = new ArrayList<>();
        mOptions = options;
        mPlayers = players;
//        mGameBoard = new DominoMultiPlayerGameBoard(); TODO: Factory to get correct type based on game type
        mBoneYard = new ArrayList<Domino>();
        mPlayerHands = new HashMap<>();
        for(DominoPlayer curPlayer : mPlayers) {
            playerKeys.add(curPlayer.getUserName());
        }
        mScoreboard = new DominoGameScoreboard(playerKeys, mOptions.getScoreThreshold(), mOptions.getScoreMultiple());
        mGameCompleted = false;
        mGameLocked = false;
    }

}
