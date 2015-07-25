package com.bduhbsoft.BigSixDominoes;

import java.util.ArrayList;

/*
* Class Dominoe
*
* Define a Dominoe
*/

public class Dominoe {
    private int mSide1;
    private int mSide2;
    private boolean mIsDouble;
    private Orientation mOrientation;

    public enum Orientation {
        SIDE1_NORTH,
        SIDE1_SOUTH,
        SIDE1_EAST,
        SIDE1_WEST
    }

    public enum SetType {
        DOUBLE_SIX
    }

    //Public helpers
    public static int NUM_DLB_SIX_DOMINOES = 28;

    public static ArrayList<Dominoe> getDominoeSet(SetType setType) {
        //TODO: case statement
        return getDouleSixSet();
    }

    private static ArrayList<Dominoe> getDouleSixSet() {
        ArrayList<Dominoe> set = new ArrayList<Dominoe>();

        //Create double six set
        set.add(new Dominoe(0, 0));
        set.add(new Dominoe(0, 1));
        set.add(new Dominoe(0, 2));
        set.add(new Dominoe(0, 3));
        set.add(new Dominoe(0, 4));
        set.add(new Dominoe(0, 5));
        set.add(new Dominoe(0, 6));

        set.add(new Dominoe(1, 1));
        set.add(new Dominoe(1, 2));
        set.add(new Dominoe(1, 3));
        set.add(new Dominoe(1, 4));
        set.add(new Dominoe(1, 5));
        set.add(new Dominoe(1, 6));

        set.add(new Dominoe(2, 2));
        set.add(new Dominoe(2, 3));
        set.add(new Dominoe(2, 4));
        set.add(new Dominoe(2, 5));
        set.add(new Dominoe(2, 6));

        set.add(new Dominoe(3, 3));
        set.add(new Dominoe(3, 4));
        set.add(new Dominoe(3, 5));
        set.add(new Dominoe(3, 6));

        set.add(new Dominoe(4, 4));
        set.add(new Dominoe(4, 5));
        set.add(new Dominoe(4, 6));

        set.add(new Dominoe(5, 5));
        set.add(new Dominoe(5, 6));

        set.add(new Dominoe(6, 6));

        return set;
    }

    public Dominoe(int side1, int side2) {
        mSide1 = side1;
        mSide2 = side2;
        mIsDouble = mSide1 == mSide2;
        mOrientation = Orientation.SIDE1_NORTH;
    }

    public int getSide1() {
        return mSide1;
    }

    public int getSide2() {
        return mSide2;
    }

    public boolean isDouble() {
        return mIsDouble;
    }

    public Orientation getOrientation() {
        return mOrientation;
    }

    public void setOrientation(Orientation orientation) {
        mOrientation = orientation;
    }

    @Override
    public String toString() {
        return mSide1 + "|" + mSide2;
    }

    public boolean equals(Dominoe rhs) {
        return
            ( (this.mSide1 == rhs.getSide1()) &&
              (this.mSide2 == rhs.getSide2())   ) ||

            ( (this.mSide1 == rhs.getSide2()) &&
              (this.mSide2 == rhs.getSide1())   );
    }

}
