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
    private int mMultiple; //Configured multiple for a single score
    private int mDoubleMultiple; //One quadrant holds twice the multiple value
    private int mHouseFull; //Maximum points for a house

    public static final int DEFAULT_MULTIPLE = 5; //Built with multiples of 5
    public static final int NUM_HOUSE_ELEMENTS = 10;
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
        mMultiple = DEFAULT_MULTIPLE;
        initHouse();
    }

    /**
    * Constructs empty house with non-default multiple.
    */
    public ScoreCardHouse(int multiple) {
        mMultiple = multiple;
        initHouse();
    }

    private void initHouse() {
        mHorBase = false;
        mVertBase = false;
        mQuads = new QuadState[4];
        for(int idx = 0; idx < mQuads.length; idx++) {
            mQuads[idx] = QuadState.Empty;
        }
        mHouseFull = mMultiple * NUM_HOUSE_ELEMENTS;
        mPoints = 0;
        mCurQuad = 0;
        mDoubleMultiple = mMultiple * 2;
        mRemaining = mHouseFull;
    }

    /**
    * Adds points to the house.  If the house cannot take all of the points, it
    * returns the left over points.
    *
    * @param points Number of points to add.
    *
    * @return Number of left over points
    */
    public int addPoints(int points) {
        Logging.LogMsg(LogLevel.TRACE, TAG, "ScoreCardHouse::addPoints, adding points: " + points + ".");
        if((points % mMultiple) != 0) {
            Logging.LogMsg(LogLevel.ERROR, TAG, "ScoreCardHouse::addPoints, point value: " + points + " not multiple of: " + mMultiple);
            return points;
        }

        int leftOver = 0;
        if(points > mRemaining) {
            Logging.LogMsg(LogLevel.TRACE, TAG, "ScoreCardHouse::addPoints, points: " + points + ", greater than remaining: " + mRemaining);
            leftOver = points - mRemaining;
            points = mRemaining;
        }

        buildHouse(points);
        mPoints += points;
        mRemaining -= points;

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
    * Gets configured multiple
    *
    * @return The configured multiple of the house
    */
    public int getMultiple() {
        return mMultiple;
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
        return mPoints == mHouseFull;
    }

    /**
    * Returns maximum points house can hold.
    *
    * @return The number of points this can hold in total
    */
    public int getMaxPoints() {
        return mHouseFull;
    }

    /**
    * Determines if one house is equal to another house in terms
    * of line/circle/cross patterns
    *
    * @param house Other house to check
    *
    * @return True if other house is equal this house, false otherwise
    */
    public boolean equals(ScoreCardHouse house) {
        for(int idx = 0; idx < mQuads.length; idx++) {
            if(this.mQuads[idx] != house.mQuads[idx])
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getState();
    }

    private void buildHouse(int points) {
        Logging.LogMsg(LogLevel.TRACE, TAG, "ScoreCardHouse::buildHouse");
        if(!mHorBase) {
            mHorBase = true;
            points -= mMultiple;
            Logging.LogMsg(LogLevel.TRACE, TAG, "ScoreCardHouse::buildHouse, built horizontal line");
        }

        if(!mVertBase && points > 0) {
            mVertBase = true;
            points -= mMultiple;
            Logging.LogMsg(LogLevel.TRACE, TAG, "ScoreCardHouse::buildHouse, built vertical line");
        }

        while(points > 0) {
            Logging.LogMsg(LogLevel.TRACE, TAG, "ScoreCardHouse::buildHouse, before processing " + points + " points, current state " + getState());
            switch(mQuads[mCurQuad]) {
                case Empty:
                    if(points >= mDoubleMultiple) {
                        Logging.LogMsg(LogLevel.TRACE, TAG, "ScoreCardHouse::buildHouse, case Q" + mCurQuad + ": EMPTY, points: " + points + " >= " + mDoubleMultiple);
                        mQuads[mCurQuad] = QuadState.Circle;
                        points -= mDoubleMultiple;
                        mCurQuad++;
                    } else {
                        Logging.LogMsg(LogLevel.TRACE, TAG, "ScoreCardHouse::buildHouse, case Q" + mCurQuad + ": EMPTY, points: " + points + " >= " + mMultiple);
                        mQuads[mCurQuad] = QuadState.Line;
                        points -= mMultiple;
                    }
                    break;
                case Line:
                    Logging.LogMsg(LogLevel.TRACE, TAG, "ScoreCardHouse::buildHouse, case Q" + mCurQuad + ": Line, points: " + points + " >= " + mMultiple);
                    mQuads[mCurQuad] = QuadState.Cross;
                    points -= mMultiple;
                    mCurQuad++;
                    break;
                //Should never reach here
                case Cross:
                case Circle:
                default:
                    //TODO: Error processing, maybe throw exception
                    break;
            }
            Logging.LogMsg(LogLevel.TRACE, TAG, "ScoreCardHouse::buildHouse, after processing, current state " + getState());
            Logging.LogMsg(LogLevel.TRACE, TAG, "");
        }
        Logging.LogMsg(LogLevel.TRACE, TAG, "");
    }

    private String getState() {
        String state = "Points: " + mPoints;

        state += " - H>" + mHorBase + " - V>" + mVertBase;
        for(int idx = 0; idx < mQuads.length; idx++) {
            state += " - Q" + idx + "[" + mQuads[idx] + "]";
        }

        return state;
    }
}
