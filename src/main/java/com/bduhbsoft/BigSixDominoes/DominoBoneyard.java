package com.bduhbsoft.BigSixDominoes;

import java.util.ArrayList;

/*
* Class DominoBoneyard
*
* Implements the boneyward which holds the unused dominos
* The boneyard begins with all of the dominoes when playes
* have not received any.  As such it also washes (or shuffles)
* the dominoes before the players take them
*
*/

public class DominoBoneyard {

    private ArrayList<Dominoe> mDominoes;

    public DominoBoneyard(ArrayList<Dominoe> dominoes) {
        mDominoes = dominoes;
    }

    public Dominoe getDomino(int position) {
        if(position < mDominoes.size() ||
           position >= 0                 ) {
            return mDominoes.remove(position);
        }

        return null;
    }

    public ArrayList<Dominoe> getYard() {
        return mDominoes;
    }

    public void washYard() {
        //TODO: Generate unique random numbers between 0 - (n-1) and then
        //TODO: copy each value to the new index.  I.e. index 0 maps to the
        //TODO: first random index (i.e. first random number0, index 1 maps to the
        //TODO: second random index and so o
        //TODO: second random index and so onn  
    }
}
