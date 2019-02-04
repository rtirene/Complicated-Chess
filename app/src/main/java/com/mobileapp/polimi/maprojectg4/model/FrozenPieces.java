package com.mobileapp.polimi.maprojectg4.model;

import java.io.Serializable;

/**
 * Frozen pieces class manages the frozen pieces in the board
 */
public class FrozenPieces implements Serializable{

    private Position frozenWhite;
    private Position frozenBlack;
    private boolean isWhiteFrozen;
    private boolean isBlackFrozen;
    private int turnLeftWhite;
    private int turnLeftBlack;
    private int counterWhitePiecesFrozen; //number of white frozen pieces (for future extensions)
    private int counterBlackPiecesFrozen; // number of black frozen pieces(for future extensions)

    public FrozenPieces(){
        this.frozenBlack = new Position(0,0);
        this.frozenWhite = new Position(0,0);
        this.isBlackFrozen = false;
        this.isWhiteFrozen = false;
        this.turnLeftBlack = 0;
        this.turnLeftWhite = 0;
        this.counterWhitePiecesFrozen = 0;
        this.counterBlackPiecesFrozen = 0;
    }

    /**
     * Given the target position of the Freeze spell sets the piece in that position frozen
     * sets the number of turns in which the piece must remain frozen to 3
     * updates the number of white frozen pieces in the board, used to decide the winner.
     * @param pos the position of the white piece to set frozen
     */
    public void setFrozenWhite(Position pos){
        isWhiteFrozen = true;
        frozenWhite.setRow(pos.getRow());
        frozenWhite.setColumn(pos.getColumn());
        turnLeftWhite=3;
        counterWhitePiecesFrozen++;
    }

    /**
     * Given the target position of the Freeze spell sets the piece in that position frozen
     * sets the number of turns in which the piece must remain frozen to 3
     * updates the number of black frozen pieces in the board, used to decide the winner.
     * @param pos the position of the black piece to set frozen
     */
    public void setFrozenBlack(Position pos){
        isBlackFrozen = true;
        frozenBlack.setRow(pos.getRow());
        frozenBlack.setColumn(pos.getColumn());
        turnLeftBlack=3;
        counterBlackPiecesFrozen++;
    }
    /**
     * Clears the frozen posidion of a killed frozen piece. Then sets to zero the number of turns left to be unfrozen
     * and the number of white frozen pieces in the board is decreased
     */
    public void unfreezeWhite(){
        isWhiteFrozen = false;
        frozenWhite.setRow(0);
        frozenWhite.setColumn(0);
        turnLeftWhite=0;
        counterWhitePiecesFrozen--;
    }


    /**
     * Clears the frozen position of a killed frozen piece. Then sets to zero the number of turns left to be unfrozen
     * and the number of black frozen pieces in the board is decreased.
     */
    public void unfreezeBlack(){
        isBlackFrozen = false;
        frozenBlack.setRow(0);
        frozenBlack.setColumn(0);
        turnLeftBlack=0;
        counterBlackPiecesFrozen--;
    }

    public Position getFrozenWhite() {
        return frozenWhite;
    }

    public Position getFrozenBlack() {
        return frozenBlack;
    }

    public boolean isWhiteFrozen() {
        return isWhiteFrozen;
    }

    public boolean isBlackFrozen() {
        return isBlackFrozen;
    }


    /**
     * this method update the number of turn left to the white and black pieces to be unfrozen
     */
    public void updateTurnLeft(Piece.Team currentTeam){

        if (currentTeam == Piece.Team.WHITE){
            if(turnLeftWhite != 0){ //there is a white frozen pieces
                if(turnLeftWhite > 1){
                    turnLeftWhite--;
                }
                else{
                    //if the turn left is 1, at the next turn the piece is unfrozen, the frozen position
                    //is cleared and the number of white frozen pieces is decreased
                    isWhiteFrozen=false;
                    frozenWhite.setRow(0);
                    frozenWhite.setColumn(0);
                    turnLeftWhite =0;
                    counterWhitePiecesFrozen--;

                }
            }
        }

        else if(currentTeam == Piece.Team.BLACK){
            if(turnLeftBlack != 0){ //there is a black frozen pieces
                if(turnLeftBlack > 1){
                    turnLeftBlack--;
                }
                else{
                    //if the turn left is 1, at the next turn the piece is unfrozen, the frozen position
                    //is cleared and the number of black frozen pieces is decreased
                    isBlackFrozen=false;
                    frozenBlack.setRow(0);
                    frozenBlack.setColumn(0);
                    turnLeftBlack =0;
                    counterBlackPiecesFrozen--;
                }
            }
        }
    }

    public boolean isPieceAtPositionFrozen(Position pos){
        return (pos.getColumn() == frozenWhite.getColumn() && pos.getRow() == frozenWhite.getRow()) ||
                (pos.getColumn() == frozenBlack.getColumn() && pos.getRow() == frozenBlack.getRow());
    }

    public void setWhiteFrozen(boolean whiteFrozen) {
        isWhiteFrozen = whiteFrozen;
    }

    public void setBlackFrozen(boolean blackFrozen) {
        isBlackFrozen = blackFrozen;
    }

    public int getTurnLeftWhite() {
        return turnLeftWhite;
    }

    public int getTurnLeftBlack() {
        return turnLeftBlack;
    }

    public void setTurnLeftWhite(int turnLeftWhite) {
        this.turnLeftWhite = turnLeftWhite;
    }

    public void setTurnLeftBlack(int turnLeftBlack) {
        this.turnLeftBlack = turnLeftBlack;
    }

    public int getCounterWhitePiecesFrozen() {
        return counterWhitePiecesFrozen;
    }

    public int getCounterBlackPiecesFrozen() {
        return counterBlackPiecesFrozen;
    }



    public void setCounterWhitePiecesFrozen(int counterWhitePiecesFrozen) {
        this.counterWhitePiecesFrozen = counterWhitePiecesFrozen;
    }

    public void setCounterBlackPiecesFrozen(int counterBlackPiecesFrozen) {
        this.counterBlackPiecesFrozen = counterBlackPiecesFrozen;
    }
}

