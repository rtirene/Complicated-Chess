package com.mobileapp.polimi.maprojectg4.model;

import com.mobileapp.polimi.maprojectg4.model.pieces.Archer;
import com.mobileapp.polimi.maprojectg4.model.pieces.Dragon;
import com.mobileapp.polimi.maprojectg4.model.pieces.Giant;
import com.mobileapp.polimi.maprojectg4.model.pieces.Knight;
import com.mobileapp.polimi.maprojectg4.model.pieces.Mage;
import com.mobileapp.polimi.maprojectg4.model.pieces.Squire;

import java.io.Serializable;

/**
 * Board is a matrix size*size that contains the references to the pieces
 */
public class Board implements Serializable{

    private Piece[][] table;
    private int size;
    /**
     * Builds a square board 6x6 with pieces in the position defined in the slides
     */
    public Board(){
        this.size= 6;
        this.table = new Piece[this.size][this.size];

        // white
        table[1][0] = new Giant(Piece.Team.WHITE);
        table[1][1] = new Knight(Piece.Team.WHITE);
        table[2][0] = new Dragon(Piece.Team.WHITE);
        table[2][1] = new Squire(Piece.Team.WHITE);
        table[3][0] = new Mage(Piece.Team.WHITE);
        table[3][1] = new Knight(Piece.Team.WHITE);
        table[4][0] = new Archer(Piece.Team.WHITE);
        table[4][1] = new Squire(Piece.Team.WHITE);

        // black
        table[1][4] = new Squire(Piece.Team.BLACK);
        table[1][5] = new Archer(Piece.Team.BLACK);
        table[2][4] = new Knight(Piece.Team.BLACK);
        table[2][5] = new Mage(Piece.Team.BLACK);
        table[3][4] = new Squire(Piece.Team.BLACK);
        table[3][5] = new Dragon(Piece.Team.BLACK);
        table[4][4] = new Knight(Piece.Team.BLACK);
        table[4][5] = new Giant(Piece.Team.BLACK);
    }

    public Piece[][] getTable() {
        return table;
    }
    /**
     * Puts a piece in the board's table specifying a desired position.
     * @param pos the position desired where insert the piece
     * @param piece the desired piece to insert in the board's table
     */
    public void setPositionInTable(Position pos,Piece piece){
        table[pos.getRow()-1][pos.getColumn()-1] = piece;
    }

    /**
     * Returns the piece's vitality situated in a specified position.
     * @param pos The position in the board
     * @return The vitality of the piece at the desired position
     */
    public int getVitalityAt(Position pos){
        if (getPieceAtPosition(pos) == null)
            throw new IllegalArgumentException("The selected position is empty. No piece found");
        if (pos.getRow()<1 || pos.getRow()>size || pos.getColumn()<1 || pos.getRow()>size)
            throw new IllegalArgumentException("the selected position is outside the board");
        return (table[pos.getRow()-1][pos.getColumn()-1]).getCurrentVitality();
    }

    /**
     * Returns the Team of a piece in a given position.
     * @param pos the position is considered from 1 to size
     * @return the piece's team. It is a predefined constant of the enum type Team
     */
    public Piece.Team whichTeam(Position pos) {
        if (getPieceAtPosition(pos) == null)
            return Piece.Team.FREE;
        return table[pos.getRow()-1][pos.getColumn()-1].getMyTeam();
    }
    /**
     * Returns the piece in the specified position.
     * @param pos the position where is located the piece
     * @return the piece in the desired position
     */
    public Piece getPieceAtPosition(Position pos) {
        Piece piece;
        if(isOutOfBoard(pos)) throw new IllegalArgumentException("The position is outside the board");
        if(table[pos.getRow()-1][pos.getColumn()-1] == null)
            return null;
        return table[pos.getRow()-1][pos.getColumn()-1];
    }

    /**
     * Checks if the candidate position is inside the board.
     * @param pos the position checked
     * @return true if the piece is out of the board, false if it is inside
     */
    public boolean isOutOfBoard(Position pos) {
        //check if pos is outside the board
        return pos.getRow() < 1 || pos.getRow() > size || pos.getColumn() < 1 || pos.getColumn() > size;
    }

    /**
     * Moves a piece from initialPosition to finalPosition.
     * @param initialPosition The position where is located the piece that needs to move
     * @param finalPosition The destination position where the piece will move
     */
    public void movePieceInTable(Position initialPosition, Position finalPosition){

        if(getPieceAtPosition(initialPosition) == null )
            throw new IllegalArgumentException("The initial position is empty");
        if( getPieceAtPosition(finalPosition) != null)
            throw new IllegalArgumentException("Is not possible to move to an occupied position");

        setPositionInTable(finalPosition,getPieceAtPosition(initialPosition));
        setPositionInTable(initialPosition,null);
    }

    /** Getter */
    public int getSize() {
        return size;
    }


}
