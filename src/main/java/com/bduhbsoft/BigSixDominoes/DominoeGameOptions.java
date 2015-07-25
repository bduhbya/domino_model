package com.bduhbsoft.BigSixDominoes;

/*
* Class DominoeGameOptions
*
* Contains the options for a given game.  Things like the number of players,
* colors of the board, skin for dominoes, scoring threshhold and so on
*/

public class DominoeGameOptions {

    int mNumPlayers;
    
    public DominoeGameOptions(int numPlayers) {
        mNumPlayers = numPlayers;
    }

    public int getNumPlayers() { return mNumPlayers; }
}
