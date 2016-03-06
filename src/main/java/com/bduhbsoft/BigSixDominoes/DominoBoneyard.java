package com.bduhbsoft.BigSixDominoes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;
import java.util.Set;

import com.bduhbsoft.BigSixDominoes.Logging.LogLevel;

/**
* Class DominoBoneyard
*
* Implements the boneyard which holds the unused dominoes
* The boneyard begins with all of the dominoes when players
* have not received any.  As such it also washes (or shuffles)
* the dominoes before the players take them
*
*/

public class DominoBoneyard implements Serializable {

    private ArrayList<Dominoe> mDominoes;

    public DominoBoneyard() {
        return;
    }

    //Sets bone yard contents
    public void setYard(ArrayList<Dominoe> dominoes) {
        mDominoes = dominoes;
    }

    //Removes domino from the board
    public Dominoe removeDomino() {
        if(mDominoes != null && mDominoes.size() > 0) {
            return mDominoes.remove(0);
        }

        return null;
    }

    //Utility function fot testing
    public ArrayList<Dominoe> getYard() {
        return mDominoes;
    }

    //Gets number of dominoes in the yard
    public int getYardSize() {
        if(mDominoes == null) {
            return 0;
        }
        return mDominoes.size();
    }

    private int putUniqueNum(int newNum, int[] list, TreeSet<Integer> unique, int idx) {
        //Tree search should be log(n) to search if the new number is already
        //in the array.  Uses more space, but runs faster.  Int array access
        //is obviously expected to be constant
        if(unique.add(newNum)) {
            list[idx] = newNum;
            idx++;
        }

        return idx;
    }

    private int[] getUniqueRandNumList(int min, int max) {
        Random rand = new Random();
        int randNum;
//        int temp;
        int[] retList = new int[max + 1];
        int idx = 0;
        TreeSet<Integer> unique = new TreeSet<Integer>();

        while(idx <= max) {
            randNum = rand.nextInt((max - min) + 1) + min;
            idx = putUniqueNum(randNum, retList, unique, idx);

            //TODO: Uncomment and add log level checking to loop when log level is implemented from build
//            Logging.LogMsg(LogLevel.TRACE, "getUniqueRandNumList", "Randon num: " + randNum + ", new list:");
//            temp = 0;
//            for(int curVal: retList) {
//                Logging.LogMsg(LogLevel.TRACE, "getUniqueRandNumList", "    retList[" + temp + "]: " + curVal);
//                temp++;
//                if(temp > idx) break;
//            }
        }

        return retList;
    }

    public void washYard() {
        //The algorithm works by generating a list of random indexes
        //The original list is traversed, and element zero is copied
        //to the new list at position randomIndexes[0].  Element 1 is
        //copied to the new list at position randomIndexes[1] and so on.
        //The array of random indexes serves as mapping from the original
        //list to the new list.  The new list is then saved as the member
        //list of the object.
        ArrayList<Dominoe> newDoms = new ArrayList<Dominoe>();
        int[] newIdxs = getUniqueRandNumList(0, mDominoes.size() - 1);

        for(int idx = 0; idx < mDominoes.size(); idx++) {
            newDoms.add(mDominoes.get(newIdxs[idx]));
        }

        mDominoes = newDoms;
        return;
    }
}
