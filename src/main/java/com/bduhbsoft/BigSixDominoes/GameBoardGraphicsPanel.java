package com.bduhbsoft.BigSixDominoes;

import java.util.ArrayList;
import javax.swing.JFrame;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.Set;

import com.bduhbsoft.BigSixDominoes.Logging.LogLevel;
import com.bduhbsoft.BigSixDominoes.Dominoe.SetType;
import com.bduhbsoft.BigSixDominoes.Dominoe.Orientation;
import com.bduhbsoft.BigSixDominoes.DominoeGameBoard.EdgeLocation;
import com.bduhbsoft.BigSixDominoes.ScoreCardHouse;
import com.bduhbsoft.BigSixDominoes.DominoGameScoreboard;

/*
* Class GameBoardGraphicsPanel
*
* Implements a graphic representation of the
* the gameboard
*
*/

class GameBoardGraphicsPanel extends JPanel {

    //****************** Domino Drawing Data **********************************

    private static final int mFaceW = 30;
    private static final int mFaceH = 30;
    private static final int mFaceW_2 = mFaceW / 2;
    private static final int mFaceH_2 = mFaceH / 2;
    private static final int mDivSize = 4;
    private static final int mOutlineW = 2;
    private static final int mArcW = 7;
    private static final int mArcH = 7;
    //Domino faces are square.  Length or width of a face are interchangeable
    private static final int mDomLength = (mFaceW * 2) + mDivSize + (mOutlineW * 2);
    private static final int mDomLength_2 = mDomLength / 2;
    private static final int mDomWidth = mFaceW + (mOutlineW * 2);
    private static final int mDomWidth_2 = mDomWidth / 2;
    private static final int mSeperationW = 3;
    private static final int mPipW = 7;
    private static final int mPipH = 7;
    private static final int mPipW_2 = mPipW / 2;
    private static final int mPipH_2 = mPipH / 2;
    private static final int mPipGap = 3;
    private static final String TAG = "GameBoardGraphicsPanel";
    private static final int XY_IDX = 0;
    private static final int SPN_XY_IDX = 1;
    private static final int RET_SIZE = 2;
    private static final int INVALID_CORD = -1;
    private static final int SCORE_X = 25;
    private static final int SCORE_Y = 25;
    private static final int SCORE_SIZE = 20;
    private static final int TITLE_X = 100;
    private static final int TITLE_Y = 35;
    private static final int TITLE_SIZE = 15;
    private static final Color SCORE_COLOR = Color.BLACK;
    private static final Color TITLE_COLOR = Color.BLUE;
    private static final Font TITLE_FONT = new Font("Monospaced", Font.PLAIN, TITLE_SIZE);
    private static final Font SCORE_FONT = new Font("Monospaced", Font.PLAIN, SCORE_SIZE);
    private ArrayList<Dominoe> mRow;
    private ArrayList<Dominoe> mCol;
    private Dominoe mSpinner;
    private int mPoints;

    //******************** Scorecard Drawing Data ****************************
    private DominoGameScoreboard mScoreboard;
    private static final int HOUSE_ELEMENT_THICKNESS = 2; //Line thickness
    private static final int CIRCLE_RADIUS = 10; //For circle elements
    private static final int CIRCLE_DIAMETER = CIRCLE_RADIUS * 2;
    private static final int CROSS_WIDTH = CIRCLE_DIAMETER; //Width of cross element
    private static final int HOUSE_ELEMENT_SPACE = 3; //Space between elements in a house and the hor/ver base lines
    private static final int HOUSE_BASE_THICKNESS = 3; //Thickness of base lines of the house
    private static final int HOUSE_BASE_THICKNESS_2 = HOUSE_BASE_THICKNESS / 2; //Thickness of base lines of the house
    private static final int HOUSE_BASELINE_LENGTH = ((HOUSE_ELEMENT_SPACE * 2 + CROSS_WIDTH) * 2);
    private static final int HOUSE_BASELINE_LENGTH_2 = HOUSE_BASELINE_LENGTH / 2;
    private static final int HOUSE_SPACING = 4; //Spaces between houses in the card
    private static final int MIN_LANE_SPACING = 4; //Minimum space between the side of a house and the column of that player's lane and player's name and next lane
    private static final int MIN_LANE_WIDTH = MIN_LANE_SPACING * 2 + HOUSE_BASELINE_LENGTH; //Minimum width of a lane.  Player name could make width larger
    private static final int HOUSE_DRAW_LENGTH = HOUSE_BASELINE_LENGTH + HOUSE_SPACING;
    private static final int HOUSE_DRAW_LENGTH_2 = HOUSE_DRAW_LENGTH / 2;
    private static final int PLAYER_FONT_OVER_DRAW = 2;
    private static final int PLAYER_FONT_SIZE = 9;
    private static final Color PLAYER_FONT_COLOR = Color.BLACK;
    private static final Font PLAYER_FONT = new Font("Monospaced", Font.PLAIN, PLAYER_FONT_SIZE);
    private static final int SCORECARD_LINE_WIDTH = 3;

    /*
      Player1 | Player2
     ---------|---------
              |
       X | O  |    |
      ---|--- | ---|---
       \ |    |    |
    */ 

    //******************** General Data ***************************************

    private String mTitle;
    private DrawingElement mDraw;
    private int mCurLaneWidth;
    private Dimension mDim;

    enum DrawingElement {
        DrawingGameBoard,
        DrawingScorecard,
        DrawingGameBoardAndScorecard,
        DrawingNone
    }

    /**
    * GameBoardGraphicsPanel constructor.  Basic setup.
    */
    public GameBoardGraphicsPanel()
    {
        super();
        setBackground(Color.GRAY);
        mRow = null;
        mCol = null;
        mSpinner = null;
        mDraw = DrawingElement.DrawingNone;
    }

    /**
    * Draws currently configured visuals
    */
    @Override
    public void paintComponent(Graphics g)
    {
        int width = getWidth();
        int height = getHeight();
        mDim = new Dimension();
        getSize(mDim);

        super.paintComponent(g);

        switch(mDraw) {
            case DrawingGameBoard:
                drawDomBoard(g);
                break;

            case DrawingScorecard:
                drawScorecard(g);
                drawPointTotal(g);
                break;

            default:
                Logging.LogMsg(LogLevel.TRACE, TAG, "paintComponent, no-op.  mDraw: " + mDraw);
        }
        drawTitle(g);
    }

    public void setBoard(ArrayList<Dominoe> row, ArrayList<Dominoe> col, Dominoe spinner, int points) {
        mRow = row;
        mCol = col;
        mSpinner = spinner;
        mPoints = points;
        mDraw = DrawingElement.DrawingGameBoard;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setScorecard(DominoGameScoreboard scoreboard) {
        mScoreboard = scoreboard;
        mPoints = 0;
        mDraw = DrawingElement.DrawingScorecard;
    }

    private int[] getRowX(int width) {
        int rowLen = 0, spinLen = 0, idx = 0;
        int[] rowRet = new int[RET_SIZE];
        rowRet[XY_IDX] = INVALID_CORD;
        rowRet[SPN_XY_IDX] = INVALID_CORD;

        if(mRow != null) {
            //Doubles are drawn sideways
            for(Dominoe curDom : mRow) {
                if(!curDom.isDouble()) {
                    rowLen += mDomLength;
                } else {
                    rowLen += mDomWidth;
                }

                //Add in the seperation between dominos if there are multiple
                //and it is not the last one
                if(idx < (mRow.size()-1) ) {
                    rowLen += mSeperationW;
                }

                //If the domino is the spinner, record how far into the length
                //it occured.  If there is no spinner, then there is no column
                if(mSpinner != null && curDom.equals(mSpinner)) {
                    spinLen = rowLen - mDomWidth;
                    if(idx < (mRow.size()-1) ) {
                        spinLen -= mSeperationW;
                    }
                }

                idx++;
            }

            //Return the center line of the row's X-coordinate
            rowRet[XY_IDX] = (width / 2) - (rowLen / 2) + mDomWidth_2;
            if(spinLen > 0) {
                rowRet[SPN_XY_IDX] = rowRet[XY_IDX] + spinLen + mDomWidth_2;
            }
        }

        return rowRet;
    }

    private int[] getColY(int height) {
        int colLen = 0, spinLen = 0, idx = 0;
        int[] colRet = new int[RET_SIZE];
        colRet[XY_IDX] = INVALID_CORD;
        colRet[SPN_XY_IDX] = INVALID_CORD;

        if(mCol != null) {
            //Doubles are drawn sideways, but the spinner is not drawn sideways with repsect
            //to the column
            for(Dominoe curDom : mCol) {
                if(!curDom.isDouble() || curDom.equals(mSpinner)) {
                    colLen += mDomLength;
                } else {
                    colLen += mDomWidth;
                }

                //Add in the seperation between dominos if there are multiple
                //and it is not the last one
                if(idx < (mCol.size()-1) ) {
                    colLen += mSeperationW;
                }

                if(curDom.equals(mSpinner)) {
                    spinLen = colLen - mDomLength;
                    if(idx < (mCol.size()-1) ) {
                        spinLen -= mSeperationW;
                    }
                }

                idx++;
            }

            //Return the center line of the Column's y-coordinate
            colRet[XY_IDX] = (height / 2) - (colLen / 2) + mDomWidth_2;
            if(spinLen > 0) {
                //This SHOULD always happen on the column
                colRet[SPN_XY_IDX] = colRet[XY_IDX] + spinLen + mDomLength_2;
            }
        }

        return colRet;
    }

    private void drawPointTotal(Graphics g) {
        Color curCol = g.getColor();

        g.setColor(SCORE_COLOR);
        g.setFont(SCORE_FONT);
        g.drawString(Integer.toString(mPoints), SCORE_X, SCORE_Y);
        g.setColor(curCol);
    }

    private void drawTitle(Graphics g) {

        if(mTitle != null) {
            Color curCol = g.getColor();
            g.setColor(TITLE_COLOR);
            g.setFont(TITLE_FONT);
            g.drawString(mTitle, TITLE_X, TITLE_Y);
            g.setColor(curCol);
        }
    }

    private void drawHouseLine1(Graphics g, int centerX, int centerY, int quadrant) {
        int startX = 0, startY = 0, endX = 0, endY = 0;

        if(quadrant == 0) {
            startX = centerX - HOUSE_BASELINE_LENGTH_2 + HOUSE_ELEMENT_SPACE;
            startY = centerY - HOUSE_BASELINE_LENGTH_2 + HOUSE_ELEMENT_SPACE;
        } else if(quadrant == 1) {
            startX = centerX + HOUSE_ELEMENT_SPACE + HOUSE_BASE_THICKNESS_2;
            startY = centerY - HOUSE_BASELINE_LENGTH_2 + HOUSE_ELEMENT_SPACE;
        } else if(quadrant == 2) {
            startX = centerX - HOUSE_BASELINE_LENGTH_2 + HOUSE_ELEMENT_SPACE;
            startY = centerY + HOUSE_ELEMENT_SPACE + HOUSE_BASE_THICKNESS_2;
        } else if(quadrant == 3) {
            startX = centerX + HOUSE_ELEMENT_SPACE + HOUSE_BASE_THICKNESS_2;
            startY = centerY + HOUSE_ELEMENT_SPACE + HOUSE_BASE_THICKNESS_2;
        } else {
            Logging.LogMsg(LogLevel.TRACE, TAG, "drawhouseline, shouldn't be here, quadrant == " + quadrant);
        }
        endX = startX + CROSS_WIDTH;
        endY = startY + CROSS_WIDTH;

        g.drawLine(startX, startY, endX, endY);
    }

    private void drawHouseLine(Graphics g, int centerX, int centerY, int quadrant) {
        drawHouseLine1(g, centerX, centerY, quadrant);
    }

    private void drawHouseCross(Graphics g, int centerX, int centerY, int quadrant) {
        int startX = 0, startY = 0, endX = 0, endY = 0;

        drawHouseLine1(g, centerX, centerY, quadrant);

        if(quadrant == 0) {
            startX = centerX - HOUSE_ELEMENT_SPACE - HOUSE_BASE_THICKNESS_2;
            startY = centerY - HOUSE_BASELINE_LENGTH_2 + HOUSE_ELEMENT_SPACE;
        } else if(quadrant == 1) {
            startX = centerX + CROSS_WIDTH + HOUSE_ELEMENT_SPACE;
            startY = centerY - HOUSE_BASELINE_LENGTH_2 + HOUSE_ELEMENT_SPACE;
        } else if(quadrant == 2) {
            startX = centerX - HOUSE_ELEMENT_SPACE - HOUSE_BASE_THICKNESS_2;
            startY = centerY + HOUSE_ELEMENT_SPACE + HOUSE_BASE_THICKNESS_2;
        } else if(quadrant == 3) {
            startX = centerX + CROSS_WIDTH + HOUSE_ELEMENT_SPACE;
            startY = centerY + HOUSE_ELEMENT_SPACE + HOUSE_BASE_THICKNESS_2;
        } else {
            Logging.LogMsg(LogLevel.TRACE, TAG, "drawHouseCross, shouldn't be here, quadrant == " + quadrant);
        }
        endX = startX - CROSS_WIDTH;
        endY = startY + CROSS_WIDTH;

        g.drawLine(startX, startY, endX, endY);
    }

    private void drawHouseCircle(Graphics g, int centerX, int centerY, int quadrant) {
        int startX = 0, startY = 0;

        if(quadrant == 0) {
            startX = centerX - HOUSE_BASELINE_LENGTH_2 + HOUSE_ELEMENT_SPACE;
            startY = centerY - HOUSE_BASELINE_LENGTH_2 + HOUSE_ELEMENT_SPACE;
        } else if(quadrant == 1) {
            startX = centerX + HOUSE_ELEMENT_SPACE + HOUSE_BASE_THICKNESS_2;
            startY = centerY - HOUSE_BASELINE_LENGTH_2 + HOUSE_ELEMENT_SPACE;
        } else if(quadrant == 2) {
            startX = centerX - HOUSE_BASELINE_LENGTH_2 + HOUSE_ELEMENT_SPACE;
            startY = centerY + HOUSE_ELEMENT_SPACE + HOUSE_BASE_THICKNESS_2;
        } else if(quadrant == 3) {
            startX = centerX + HOUSE_ELEMENT_SPACE + HOUSE_BASE_THICKNESS_2;
            startY = centerY + HOUSE_ELEMENT_SPACE + HOUSE_BASE_THICKNESS_2;
        } else {
            Logging.LogMsg(LogLevel.TRACE, TAG, "drawHouseCircle, shouldn't be here, quadrant == " + quadrant);
        }

        g.drawOval(startX, startY, CIRCLE_DIAMETER, CIRCLE_DIAMETER);
    }

    private void drawHouse(Graphics g, int centerX, int centerY, ScoreCardHouse curHouse) {
        int horLineStartX = centerX - HOUSE_BASELINE_LENGTH_2, horLineStartY = centerY - (HOUSE_BASE_THICKNESS / 2);
        int verLineStartX = centerX - HOUSE_BASE_THICKNESS_2, verLineStartY = centerY - HOUSE_BASELINE_LENGTH_2;

        //Draw horizontial base line of house if present
        if(curHouse.getHorizontalBase()) {
            g.setColor(Color.BLACK);
            g.fillRect(horLineStartX, horLineStartY, HOUSE_BASELINE_LENGTH, HOUSE_ELEMENT_THICKNESS);
        }

        //Draw vertical base line of house if present
        if(curHouse.getVerticalBase()) {
            g.setColor(Color.BLACK);
            g.fillRect(verLineStartX, verLineStartY, HOUSE_ELEMENT_THICKNESS, HOUSE_BASELINE_LENGTH);
        }

        ScoreCardHouse.QuadState[] quads = curHouse.getQuads();
        if(quads != null) {
            for(int idx =0; idx < quads.length; idx++) {
                switch(quads[idx]) {
                    case Line:
                        drawHouseLine(g, centerX, centerY, idx);
                        break;
                    case Cross:
                        drawHouseCross(g, centerX, centerY, idx);
                        break;
                    case Circle:
                        drawHouseCircle(g, centerX, centerY, idx);
                        break;
                    case Empty:
                    default:
                        break;
                }
            }
        }
    }

    private int findLaneWidth(String[] playersArr, FontMetrics metrics) {
        int longestName = 0;
        int width = 0;
        for(int idx = 0; idx < playersArr.length; idx++) {
            int curWidth = metrics.stringWidth(playersArr[idx]);
            longestName = (curWidth > longestName ? curWidth : longestName);
            Logging.LogMsg(LogLevel.TRACE, TAG, "drawScorecard, longest name: " + longestName + ", cur player name: " + playersArr[idx]);
        }
        width = longestName + MIN_LANE_SPACING * 2;
        width = (width > MIN_LANE_WIDTH ? width : MIN_LANE_WIDTH);
        return width;
    }

    private void drawPlayerLane(Graphics g, String name, int baseX, int baseY, int laneWidth, FontMetrics metrics,
                                int maxHouses, ArrayList<ScoreCardHouse> houses, boolean drawDivder) {
        int fontHeight = metrics.getHeight();
        int fontStartX = baseX + (laneWidth/2) - metrics.stringWidth(name)/2; //Center font in the lane
        int fontStartY = baseY - HOUSE_ELEMENT_SPACE;

        //Draw underline
        g.setColor(PLAYER_FONT_COLOR);
        g.fillRect(baseX, baseY, laneWidth, SCORECARD_LINE_WIDTH);
        g.setFont(PLAYER_FONT);
        g.drawString(name, fontStartX, fontStartY);

        if(drawDivder) {
            int divY = baseY - (fontHeight + PLAYER_FONT_OVER_DRAW);
            int divX = baseX + laneWidth;
            int divLineHeight = fontHeight + PLAYER_FONT_OVER_DRAW + (maxHouses * HOUSE_DRAW_LENGTH);
            g.fillRect(divX, divY, SCORECARD_LINE_WIDTH, divLineHeight + SCORECARD_LINE_WIDTH);
        }

        if(houses != null) {
            int startHouseX = baseX + (laneWidth/2);
            int startHouseY = baseY + SCORECARD_LINE_WIDTH + HOUSE_DRAW_LENGTH_2;
            int idx = 1;
            for(ScoreCardHouse curHouse : houses) {
                drawHouse(g, startHouseX, startHouseY, curHouse);
                startHouseY += HOUSE_DRAW_LENGTH;
                idx++;
            }
        }
    }

    private void drawScorecard(Graphics g) {
        Logging.LogMsg(LogLevel.TRACE, TAG, "drawScorecard, panel size: " + mDim.width + "x" + mDim.height);

        if(mScoreboard != null && mScoreboard.getNumPlayers() > 0) {
            int desiredPlayer = 0;
            String playersArr[] = null;
            String curPlayer = null;
            playersArr = mScoreboard.getPlayers().toArray(new String[0]);
            // get metrics from the graphics
            FontMetrics metrics = g.getFontMetrics(PLAYER_FONT);
            int fontHeight = metrics.getHeight();
            int curLaneWidth = findLaneWidth(playersArr, metrics);
            int scorecardLength = curLaneWidth * playersArr.length;
            int curX = 0, curY = 0;
            int startX = mDim.width/2 - scorecardLength/2, startY = mDim.height/2;
            for(int curIdx = 0; curIdx < playersArr.length; ++curIdx) {
                curX = startX + ((curLaneWidth + SCORECARD_LINE_WIDTH) * curIdx);
                curY = startY;
                curPlayer = playersArr[curIdx];
                drawPlayerLane(g, curPlayer, curX, curY, curLaneWidth, metrics, mScoreboard.getMaximumHouses(),
                               mScoreboard.getPlayerScoreCardHouses(curPlayer), ((curIdx+1) < playersArr.length));
            }
        }
    }

    private void drawDomBoard(Graphics g) {
        int[] rowCord, colCord;
        int rowStartX = 0, rowStartY = 0;
        int colStartX = 0, colStartY = 0;

        Logging.LogMsg(LogLevel.TRACE, TAG, "drawDomBoard, panel size: " + mDim.width + "x" + mDim.height);

        rowCord = getRowX(mDim.width);
        colCord = getColY(mDim.height);

        drawPointTotal(g);

        //The row starts at row x, spinner y
        //The column starts at column y and spinner
        //If the column came back empty, just center on the Y value

        if(rowCord[XY_IDX] != INVALID_CORD) {
            rowStartX = rowCord[XY_IDX];
        } else {
            Logging.LogMsg(LogLevel.TRACE, TAG, "drawDomBoard, no row to draw");
            return;
        }

        Logging.LogMsg(LogLevel.TRACE, TAG, "drawDomBoard, row startX coordinate: " + rowStartX);

        if(colCord[SPN_XY_IDX] != INVALID_CORD) {
            rowStartY = colCord[SPN_XY_IDX];
        } else {
            rowStartY = (mDim.height / 2) - (mDomWidth_2);
        }

        Logging.LogMsg(LogLevel.TRACE, TAG, "drawDomBoard, spinner/row startY coordinate: " + rowStartY);
        drawRow(g, rowStartX, rowStartY);

        //If the column size is 1, it only contains the spinner and does not need drawing
        //If the column does not exist, it obviously also, does not need drawing
        if(mCol == null || mCol.size() == 1 ) {
            Logging.LogMsg(LogLevel.TRACE, TAG, "drawDomBoard, column only contains spinner, or does not exist");
            return;
        }

        if(colCord[XY_IDX] != INVALID_CORD) {
            colStartY = colCord[XY_IDX];
        } else {
            Logging.LogMsg(LogLevel.TRACE, TAG, "drawDomBoard, no col to draw");
            return;
        }

        Logging.LogMsg(LogLevel.TRACE, TAG, "drawDomBoard, col startY coordinate: " + colStartY);

        if(rowCord[SPN_XY_IDX] != INVALID_CORD) {
            //This SHOULD always happen.  If not, there is an error in the code
            colStartX = rowCord[SPN_XY_IDX];
        } else {
            Logging.LogMsg(LogLevel.ERROR, TAG, "drawDomBoard, no col to draw");
            return;
        }

        Logging.LogMsg(LogLevel.TRACE, TAG, "drawDomBoard, spinner/column startX coordinate: " + colStartX);
        drawCol(g, colStartX, colStartY);

        return;
    }

    private void drawRow(Graphics g, int startX, int startY) {
        int curX = startX, curY = 0;
        int regY = startY - mDomWidth_2, dblY = startY - mDomLength_2;

        Logging.LogMsg(LogLevel.TRACE, TAG, "drawRow, starting row at: " + curX + "x" + curY);
        //Row y is the center line, so move half the size based on domino orintation
        for(Dominoe dom : mRow) {
            if(dom.isDouble()) {
                curY = dblY;
            } else {
                curY = regY;
            }

            Logging.LogMsg(LogLevel.TRACE, TAG, "drawRow, drawing domino at : " + curX + "x" + curY);
            drawDominoe(g, curX, curY, dom);

            if(dom.isDouble()) {
                curX += mDomWidth + mSeperationW;
            } else {
                curX += mDomLength + mSeperationW;
            }
        }
    }

    private void drawCol(Graphics g, int startX, int startY) {
        int curX = 0, curY = startY;
        int regX = startX - mDomWidth_2, dblX = startX - mDomLength_2;

        Logging.LogMsg(LogLevel.TRACE, TAG, "drawCol, starting col at: " + curX + "x" + curY);
        //Col x is the center line, so move half the size based on domoino orientation
        for(Dominoe dom : mCol) {
            if(dom.isDouble() && !dom.equals(mSpinner)) {
                curX = dblX;
            } else {
                curX = regX;
            }

            //The spinner is drawn by the row
            if(!dom.equals(mSpinner)) {
                Logging.LogMsg(LogLevel.TRACE, TAG, "drawCol, drawing domino at : " + curX + "x" + curY);
                drawDominoe(g, curX, curY, dom);
            }

            if(dom.isDouble() && !dom.equals(mSpinner)) {
                curY += mDomWidth + mSeperationW;
            } else {
                curY += mDomLength + mSeperationW;
            }
        }
    }

    private void drawPipCenter(Graphics g, int startX, int startY) {
        int x = startX + mFaceW_2 - mPipW_2;
        int y = startY + mFaceH_2 - mPipH_2;
        g.fillOval(x, y, mPipW, mPipH);
    }

    private void drawPipNorthEast(Graphics g, int startX, int startY) {
        int x = startX + mFaceW - mPipGap - mPipW;
        int y = startY + mPipGap;
        g.fillOval(x, y, mPipW, mPipH);
    }

    private void drawPipNorthWest(Graphics g, int startX, int startY) {
        int x = startX + mPipGap;
        int y = startY + mPipGap;
        g.fillOval(x, y, mPipW, mPipH);
    }

    private void drawPipSouthWest(Graphics g, int startX, int startY) {
        int x = startX + mPipGap;
        int y = startY + mFaceH - mPipGap - mPipH;
        g.fillOval(x, y, mPipW, mPipH);
    }

    private void drawPipSouthEast(Graphics g, int startX, int startY) {
        int x = startX + mFaceW - mPipGap - mPipW;
        int y = startY + mFaceH - mPipGap - mPipH;
        g.fillOval(x, y, mPipW, mPipH);
    }

    private void drawPipCenterEast(Graphics g, int startX, int startY) {
        int x = startX + mPipGap;
        int y = startY + mFaceH_2 - mPipH_2;
        g.fillOval(x, y, mPipW, mPipH);
    }

    private void drawPipCenterWest(Graphics g, int startX, int startY) {
        int x = startX + mFaceW - mPipGap - mPipW;
        int y = startY + mFaceH_2 - mPipH_2;
        g.fillOval(x, y, mPipW, mPipH);
    }

    private void drawPipCenterSouth(Graphics g, int startX, int startY) {
        int x = startX + mFaceW_2 - mPipW_2;
        int y = startY + mFaceH - mPipGap - mPipH;
        g.fillOval(x, y, mPipW, mPipH);
    }

    private void drawPipCenterNorth(Graphics g, int startX, int startY) {
        int x = startX + mFaceW_2 - mPipW_2;
        int y = startY + mPipGap;
        g.fillOval(x, y, mPipW, mPipH);
    }

    private void drawPips(Graphics g, int startX, int startY, int numPips, Orientation ortn) {
        g.setColor(Color.BLACK);

        if(numPips == 0) {
            return;
        } else if(numPips == 1) {
            drawPipCenter(g, startX, startY);
            return;
        } else if(numPips == 4) {
            drawPipNorthEast(g, startX, startY);
            drawPipNorthWest(g, startX, startY);
            drawPipSouthWest(g, startX, startY);
            drawPipSouthEast(g, startX, startY);
            return;
        } else if(numPips == 5) {
            drawPipNorthEast(g, startX, startY);
            drawPipNorthWest(g, startX, startY);
            drawPipSouthWest(g, startX, startY);
            drawPipSouthEast(g, startX, startY);
            drawPipCenter(g, startX, startY);
            return;
        }

        switch (ortn) {
            case SIDE1_NORTH:
            case SIDE1_SOUTH:
                if(numPips == 2) {
                    drawPipNorthEast(g, startX, startY);
                    drawPipSouthWest(g, startX, startY);
                } else if(numPips == 3) {
                    drawPipNorthEast(g, startX, startY);
                    drawPipCenter(g, startX, startY);
                    drawPipSouthWest(g, startX, startY);
                } else if(numPips == 6) {
                    drawPipNorthEast(g, startX, startY);
                    drawPipNorthWest(g, startX, startY);
                    drawPipSouthWest(g, startX, startY);
                    drawPipSouthEast(g, startX, startY);
                    drawPipCenterEast(g, startX, startY);
                    drawPipCenterWest(g, startX, startY);
                }
                break;

            case SIDE1_EAST:
            case SIDE1_WEST:
                if(numPips == 2) {
                    drawPipNorthWest(g, startX, startY);
                    drawPipSouthEast(g, startX, startY);
                } else if(numPips == 3) {
                    drawPipNorthWest(g, startX, startY);
                    drawPipCenter(g, startX, startY);
                    drawPipSouthEast(g, startX, startY);
                } else if(numPips == 6) {
                    drawPipNorthEast(g, startX, startY);
                    drawPipNorthWest(g, startX, startY);
                    drawPipSouthWest(g, startX, startY);
                    drawPipSouthEast(g, startX, startY);
                    drawPipCenterNorth(g, startX, startY);
                    drawPipCenterSouth(g, startX, startY);
                }
                break;
        }

        return;
    }

    private void drawDominoe(Graphics g, int startX, int startY, Dominoe dom) {
        int curX = startX, curY = startY;
        int adjStartX = startX + mOutlineW, adjStartY = startY + mOutlineW;

        if(dom.getOrientation() == Orientation.SIDE1_NORTH ||
           dom.getOrientation() == Orientation.SIDE1_SOUTH   ) {
            //Draw body
            g.setColor(Color.WHITE);
            g.fillRoundRect(curX, curY, mDomWidth, mDomLength, mArcW, mArcH);

            g.setColor(Color.BLACK);
            g.drawRoundRect(curX, curY, mDomWidth, mDomLength, mArcW, mArcH);

            //Draw dividing line
            curY += mFaceH + mOutlineW;
            g.setColor(Color.BLACK);
            g.fillRect(curX, curY, mDomWidth, mDivSize);
            curY += mDivSize;
            curX += mOutlineW;
        } else {
            //Draw body
            g.setColor(Color.WHITE);
            g.fillRoundRect(curX, curY, mDomLength, mDomWidth, mArcW, mArcH);

            g.setColor(Color.BLACK);
            g.drawRoundRect(curX, curY, mDomLength, mDomWidth, mArcW, mArcH);

            //Draw dividing line
            curX += mFaceW + mOutlineW;
            g.setColor(Color.BLACK);
            g.fillRect(curX, curY, mDivSize, mDomWidth);
            curX += mDivSize;
            curY += mOutlineW;
        }


        //The curX|Y variables end up pointing to the second domino face starting point
        if(dom.getOrientation() == Orientation.SIDE1_NORTH ||
           dom.getOrientation() == Orientation.SIDE1_WEST     ) {
            drawPips(g, adjStartX, adjStartY, dom.getSide1(), dom.getOrientation());
            drawPips(g, curX, curY, dom.getSide2(), dom.getOrientation());
        } else {
            drawPips(g, adjStartX, adjStartY, dom.getSide2(), dom.getOrientation());
            drawPips(g, curX, curY, dom.getSide1(), dom.getOrientation());
        }
    }
}
