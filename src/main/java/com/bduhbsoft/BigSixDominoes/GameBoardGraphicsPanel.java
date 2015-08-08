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

import com.bduhbsoft.BigSixDominoes.Logging.LogLevel;
import com.bduhbsoft.BigSixDominoes.Dominoe.SetType;
import com.bduhbsoft.BigSixDominoes.Dominoe.Orientation;
import com.bduhbsoft.BigSixDominoes.DominoeGameBoard.EdgeLocation;

/*
* Class GameBoardGraphicsPanel
*
* Implements a graphic representation of the
* the gameboard
*
*/

class GameBoardGraphicsPanel extends JPanel {

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

    private ArrayList<Dominoe> mRow;
    private ArrayList<Dominoe> mCol;
    private Dominoe mSpinner;
    private int mPoints;
    private String mTitle;

    public GameBoardGraphicsPanel()
    {
        super();
        setBackground(Color.GRAY);
        mRow = null;
        mCol = null;
        mSpinner = null;
    }

    public void paintComponent(Graphics g)
    {
        int width = getWidth();
        int height = getHeight();

        super.paintComponent(g);

        drawDomBoard(g);
    }

    public void setBoard(ArrayList<Dominoe> row, ArrayList<Dominoe> col, Dominoe spinner, int points) {
        mRow = row;
        mCol = col;
        mSpinner = spinner;
        mPoints = points;
    }

    public void setTitle(String title) {
        mTitle = title;
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
        g.setFont(new Font("Monospaced", Font.PLAIN, SCORE_SIZE));
        g.drawString(Integer.toString(mPoints), SCORE_X, SCORE_Y);
        g.setColor(curCol);
    }

    private void drawTitle(Graphics g) {

        if(mTitle != null) {
            Color curCol = g.getColor();
            g.setColor(TITLE_COLOR);
            g.setFont(new Font("Monospaced", Font.PLAIN, TITLE_SIZE));
            g.drawString(mTitle, TITLE_X, TITLE_Y);
            g.setColor(curCol);
        }
    }

    private void drawDomBoard(Graphics g) {
        Dimension dim = new Dimension();
        int[] rowCord, colCord;
        int rowStartX = 0, rowStartY = 0;
        int colStartX = 0, colStartY = 0;

        getSize(dim);
        Logging.LogMsg(LogLevel.TRACE, TAG, "drawDomBoard, panel size: " + dim.width + "x" + dim.height);

        rowCord = getRowX(dim.width);
        colCord = getColY(dim.height);

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
            rowStartY = (dim.height / 2) - (mDomWidth_2);
        }

        Logging.LogMsg(LogLevel.TRACE, TAG, "drawDomBoard, spinner/row startY coordinate: " + rowStartY);
        drawRow(g, rowStartX, rowStartY);

        drawPointTotal(g);
        drawTitle(g);

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
