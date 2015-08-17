package com.bduhbsoft.BigSixDominoes;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;
import java.util.Set;

import com.bduhbsoft.BigSixDominoes.Logging.LogLevel;

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

    private void putUniqueNum(int newNum, ArrayList<Integer> list) {
        boolean found = false;
    
        for(int curInt: list) {
            found = (curInt == newNum);

            if(found) break;
        }

        if(!found) list.add(newNum);

        return;
    }

    private ArrayList<Integer> getUniqueRandNumList(int min, int max) {
        Random rand = new Random();
        int randNum;
        ArrayList<Integer> unqList = new ArrayList<Integer>();

        while(unqList.size() <= max) {
            randNum = rand.nextInt((max - min) + 1) + min;
            putUniqueNum(randNum, unqList);

            Logging.LogMsg(LogLevel.TRACE, "getUniqueRandNumList", "Randon num: " + randNum + ", new list:");
            for(int curVal: unqList) {
                Logging.LogMsg(LogLevel.TRACE, "getUniqueRandNumList", "    " + curVal);
            }
        }

        return unqList;
    }

    public void washYard() {
        //TODO: Generate unique random numbers between 0 - (n-1) and then
        //TODO: copy each value to the new index.  I.e. index 0 maps to the
        //TODO: first random index (i.e. first random number0, index 1 maps to the
        //TODO: second random index and so o
        //TODO: second random index and so on
        ArrayList<Dominoe> newDoms = new ArrayList<Dominoe>();
        ArrayList<Integer> newIdxs = getUniqueRandNumList(0, mDominoes.size() - 1);

        for(int idx = 0; idx < mDominoes.size(); idx++) {
            newDoms.add(mDominoes.get(newIdxs.get(idx)));
        }

        mDominoes = newDoms;
        return;
    }
}
