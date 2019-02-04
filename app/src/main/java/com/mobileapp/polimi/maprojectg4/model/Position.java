package com.mobileapp.polimi.maprojectg4.model;

import java.io.Serializable;

/**
 * The class Position identify the position on the board
 */

public class Position implements Serializable{
    private int row;
    private int column;

    public Position(int row, int column){
        this.row=row;
        this.column=column;
    }

    /**
     * Moves the current position in a specified direction by one step.
     * Even numbers are straight directions, odd numbers are diagonal directions.
     * @param direction 0(right),1(up-right),2(up),3(up-left),4(left),5(down-left),6(down),7(down-right)
     */
    public void movePosition(int direction) {  //direction = {0,1,2,3,4,5,6,7}
        switch (direction) {
            case (0):
                column++;
                break;
            case(1):
                row--;
                column++;
                break;
            case(2):
                row--;
                break;
            case(3):
                row--;
                column--;
                break;
            case(4):
                column--;
                break;
            case(5):
                column--;
                row++;
                break;
            case(6):
                row++;
                break;
            case(7):
                column++;
                row++;
                break;
            default:
                throw new IllegalArgumentException("The input direction (i) is not a possible direction");
        }
    }

    /**
     * Moves the current position in a specified direction by a specific numbers of step.
     * Even numbers are straight directions, odd numbers are diagonal directions.
     * @param direction 0(right),1(up-right),2(up),3(up-left),4(left),5(down-left),6(down),7(down-right)
     * @param steps the number of steps
     */
    public void movePosition(int direction, int steps) {
        if(steps<0) {
            throw new IllegalArgumentException("the inserted number of steps is not valid (<0)");
        }
        for(int i=1; i<=steps; i++ ){
            movePosition(direction);
        }
    }


    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

}

