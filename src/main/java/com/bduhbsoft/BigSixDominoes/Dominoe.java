package com.bduhbsoft.BigSixDominoes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/*
* Class Dominoe
*
* Defines a Dominoe
*/

public class Dominoe implements Serializable {
    private DominoSide mSide1;
    private DominoSide mSide2;
    private boolean mIsDouble;

    private static HashMap<DominoSide.SideOrientation, Orientation> sOrientationMap = new HashMap<>();

    static {
        sOrientationMap.put(DominoSide.SideOrientation.North, Orientation.SIDE1_NORTH);
        sOrientationMap.put(DominoSide.SideOrientation.South, Orientation.SIDE1_SOUTH);
        sOrientationMap.put(DominoSide.SideOrientation.East, Orientation.SIDE1_EAST);
        sOrientationMap.put(DominoSide.SideOrientation.West, Orientation.SIDE1_WEST);
    }

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
        return getDoubleSixSet();
    }

    private static ArrayList<Dominoe> getDoubleSixSet() {
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
        mSide1 = new DominoSide(side1);
        mSide2 = new DominoSide(side2);
        mIsDouble = mSide1.equals(mSide2);
        setSidesOrientaion(Orientation.SIDE1_NORTH);
    }

    public int getSide1() {
        return mSide1.getValue();
    }

    public int getSide2() {
        return mSide2.getValue();
    }

    public DominoSide getDomSide1() {
        return mSide1;
    }

    public DominoSide getDomSide2() {
        return mSide2;
    }

    public boolean isDouble() {
        return mIsDouble;
    }

    public Orientation getOrientation() {
        return sOrientationMap.get(mSide1.getOrientation());
    }

    public void setOrientation(Orientation orientation) {
        setSidesOrientaion(orientation);
    }

    @Override
    public String toString() {
        return mSide1.getValue() + "|" + mSide2.getValue();
    }

    public boolean equals(Dominoe rhs) {
        return
            ( (this.mSide1.equals(rhs.getDomSide1())) &&
              (this.mSide2.equals(rhs.getDomSide2()))   ) ||

            ( (this.mSide1.equals(rhs.getDomSide2())) &&
              (this.mSide2.equals(rhs.getDomSide1()))   );
    }

    private void setSidesOrientaion(Orientation newOrn) {
        switch(newOrn) {
            case SIDE1_NORTH:
                mSide1.setOrientation(DominoSide.SideOrientation.North);
                mSide2.setOrientation(DominoSide.SideOrientation.South);
                break;
            case SIDE1_SOUTH:
                mSide1.setOrientation(DominoSide.SideOrientation.South);
                mSide2.setOrientation(DominoSide.SideOrientation.North);
                break;
            case SIDE1_EAST:
                mSide1.setOrientation(DominoSide.SideOrientation.East);
                mSide2.setOrientation(DominoSide.SideOrientation.West);
                break;
            case SIDE1_WEST:
                mSide1.setOrientation(DominoSide.SideOrientation.West);
                mSide2.setOrientation(DominoSide.SideOrientation.East);
                break;
        }
    }
}
