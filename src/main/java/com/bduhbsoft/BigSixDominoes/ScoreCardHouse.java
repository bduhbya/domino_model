package com.bduhbsoft.BigSixDominoes;

import com.bduhbsoft.BigSixDominoes.Logging.LogLevel;

/**
* Tracks the state of each house of a classic domino game score board.
*
* </p>
*
* Each quadrant holds up to 10 points and the base lines of the house
* are worth 5 points each.  A house is worth 50 points when full.  The
* quadrants of the house are described the same way the quadrants of
* a Cartesian graph are described.
*
* </p>
*
* Each house fills up as points are added to
* a players score.  If a line is already drawn in a quadrant, then it
* can only transition to a cross.  If a quadrant is empty it can take
* either a line or a circle.
*/
public class ScoreCardHouse {
    private QuadState[] mQuads; //State of each quadrant
    private boolean mHorBase; //Is horizontal base established?
    private boolean mVertBase; //Is vertical base established?
    private int mPoints; //Numeric value of points
    private int mRemaining; //Remaining points in the house before full
    private int mCurQuad; //Current quadrant

    public static final int MULTIPLE = 5; //Built with multiples of 5
    public static final int DBL_MULTIPLE = MULTIPLE * 2; //House quadrants hold at most twice the multiple
    private static final int HOUSE_FULL = 50; //50 points per house
    private static final String TAG = "ScoreCardHouse";

    /**
    * State of each house quadrant.
    *
    * </p>
    *
    * Each quadrant of a house can be either a single line (5 points),
    * a cross/x (10 points) or a circle (10 points)
    */ 
    public enum QuadState {
        Empty,
        Line,
        Cross,
        Circle
    }

    /**
    * Constructs empty house.
    */
    public ScoreCardHouse() {
        mHorBase = false;
        mVertBase = false;
        mQuads = new QuadState[4];
        for(int idx = 0; idx < mQuads.length; idx++) {
            mQuads[idx] = QuadState.Empty;
        }
        mPoints = 0;
        mRemaining = HOUSE_FULL;
        mCurQuad = 0;
    }

    /**
    * Adds points to the house.  If the house cannot take all of the points, it
    * returns the left over points.
    *
    * @return Number of left over points
    */
    public int addPoints(int points) {
        if((points % MULTIPLE) != 0) {
            Logging.LogMsg(LogLevel.ERROR, TAG, "ScoreCardHouse::addPoints, point value: " + points + " not multiple of: " + MULTIPLE);
            return points;
        }

        int leftOver = 0;
        if(points > mRemaining) {
            leftOver = points - mRemaining;
            points = mRemaining;
        }

        buildHouse(points);
        mPoints += points;

        return leftOver;
    }

    /**
    * Gets current points in the house.
    *
    * @return Points in this house
    */
    public int getPoints() {
        return mPoints;
    }

    /**
    * Gets horizontal base status
    *
    * @return True if horizontal base is active
    */
    public boolean getHorizontalBase() {
        return mHorBase;
    }

    /**
    * Gets vertical base status
    *
    * @return True if vertical base is active
    */
    public boolean getVerticalBase() {
        return mVertBase;
    }

    /**
    * Gets quadrants of the house.
    *
    * @return Quadrants of the house
    */
    public QuadState[] getQuads() {
        return mQuads;
    }

    /**
    * Returns true if the house is full.
    *
    * @return True if house is full and false otherwise
    */
    public boolean isFull() {
        return mPoints == HOUSE_FULL;
    }

    private void buildHouse(int points) {
        if(!mHorBase) {
            mHorBase = true;
            points -= MULTIPLE;
        }

        if(!mVertBase && points > 0) {
            mVertBase = true;
            points -= MULTIPLE;
        }

        while(points > 0) {
            switch(mQuads[mCurQuad]) {
                case Empty:
                    if(points >= DBL_MULTIPLE) {
                        mQuads[mCurQuad] = QuadState.Circle;
                        points -= DBL_MULTIPLE;
                        mCurQuad++;
                    } else {
                        mQuads[mCurQuad] = QuadState.Line;
                        points -= MULTIPLE;
                    }
                    break;
                case Line:
                    mQuads[mCurQuad] = QuadState.Cross;
                    points -= MULTIPLE;
                    mCurQuad++;
                    break;
                //Should never reach here
                case Cross:
                case Circle:
                default:
                    //TODO: Error processing, maybe throw exception
                    break;
            }
        }
    }
}
