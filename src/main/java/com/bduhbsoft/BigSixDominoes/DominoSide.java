package com.bduhbsoft.BigSixDominoes;

/*
* Class DominoSide
*
* Defines a Domino side.  Intent of this class is to allow more generic
* processing of possible domino boards that stack dominos together based
* on multiple sides.
*
* A board could contain a grid of sides to more easily process spatial
* locations of whole dominos
*/


public class DominoSide {

    public enum SideOrientation {
        North,
        South,
        East,
        West
    }

    private final int mValue;
    private SideOrientation mOrientation;

    /**
    * Constructs new side with orientation and value
    *
    * @param val Numeric value of the side
    * @param orn Orientation of the side
    */ 
    public DominoSide(int val, SideOrientation orn) {
        mValue = val;
        mOrientation = orn;
    }

    /**
    * Constructs new side with default north orientation
    *
    * @param val Numeric value of the side
    */ 
    public DominoSide(int val) {
        mValue = val;
        mOrientation = SideOrientation.North;
    }

    /**
    * Sets new orientation of the side
    *
    * @param orn Orientation of the side
    */ 
    public void setOrientation(SideOrientation orn) {
        mOrientation = orn;
    }

    /**
    * Gets numeric value
    *
    * @return Numeric value of the side
    */ 
    public int getValue() {
        return mValue;
    }

    /**
    * Gets orientation
    *
    * @return Orientation of the side
    */ 
    public SideOrientation getOrientation() {
        return mOrientation;
    }

    public boolean equals(DominoSide rhs) {
        return mValue == rhs.mValue;
    }
}
