package com.bduhbsoft.BigSixDominoes;


/**
* Class MultiPlayerGameBoardFactory
*
* Provides concrete game board matching the request game type
*/
public class MultiPlayerGameBoardFactory {

    public enum GameType {
        BigSix,
        MexicanTrain,
        Spinner,
        ChickenFoot,
        FortyTwo
    }

    //TODO: Throw exception
    public static DominoMultiPlayerGameBoard getGameBoard(GameType type) {
        switch(type) {
            case BigSix:
                return new DominoGameBoardDoubleSix();
            case MexicanTrain:
            case Spinner:
            case ChickenFoot:
            case FortyTwo:
            default:
                return null;

        }
    }
}
