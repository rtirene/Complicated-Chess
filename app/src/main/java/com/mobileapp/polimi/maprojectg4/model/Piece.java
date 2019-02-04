package com.mobileapp.polimi.maprojectg4.model;

import com.mobileapp.polimi.maprojectg4.model.Board;
import com.mobileapp.polimi.maprojectg4.model.Position;
import com.mobileapp.polimi.maprojectg4.model.pieces.Archer;

import java.io.Serializable;
import java.util.Vector;

/**
 * Created by Group04.
 */

public abstract class Piece implements Serializable{
    protected int maxVitality;
    protected int moveRange;
    protected Direction moveDirection;
    protected Movement moveType;
    protected int attackRange;
    protected int strength;
    protected Direction attackDirection;
    protected boolean canAttack;
    protected boolean canUseSpells;
    protected Team myTeam;
    protected int currentVitality;

    public boolean isCanAttack() {
        return canAttack;
    }

    public int getMaxVitality() {
        return maxVitality;
    }

    public int getmoveRange() {
        return moveRange;
    }

    public int getCurrentVitality() {
        return currentVitality;
    }

    public void setCurrentVitality(int currentVitality) {
        this.currentVitality = currentVitality;
    }

    public enum Direction {
        STRAIGHT, ANY, DIAGONAL, EMPTY
    }
    public enum Team {
        WHITE, BLACK, FREE
    }
    public enum Movement {
        WALK, FLY
    }

    public Team getMyTeam() {
        return myTeam;
    }

    public boolean isCanUseSpells() {
        return canUseSpells;
    }

    public int getStrength() {
        return strength;
    }

    public Movement getMoveType() { return moveType; }

    public Direction getMoveDirection() { return moveDirection; }

    public Direction getAttackDirection() { return attackDirection; }

    public int getAttackRange() { return attackRange; }

    /**
     * Computes all the cells where the selected piece can attack. Return a vector of Position.
     * @param initialPosition the position of the piece that calls the method
     * @param board the actual board configuration of the game
     * @param currentTeam the current team that is playing
     * @return a vector containing the allowed positions to which the piece in currentPosition can attacks
     * @throws throw IllegalArgumentException if the selected piece cannot attack
     */
    public Vector<Position> possibleAttacks(Position initialPosition, Board board, Team currentTeam) {

        //check if the selected piece can attack
        if (!canAttack)
            throw new IllegalArgumentException("The piece in the initial position cannot attack");

        Vector possibleAttack = new Vector();
        Position currentPosition;
        int steps;
        int direction;

        //set the starting direction
        if (attackDirection==Direction.DIAGONAL)
            direction = 1; //diagonal right-up
        else direction = 0; //right

        while (direction<8) {
            //create a new position that correspond to the initial position
            currentPosition = new Position(initialPosition.getRow(),initialPosition.getColumn());
            //reset the steps
            steps = 0;

            boolean exit = false; //condition for exit from the inner cicle
            while (!exit && steps < attackRange){
                //move in the selected direction
                currentPosition.movePosition(direction);
                //check if we are inside the board
                if(!board.isOutOfBoard(currentPosition)) {
                    if (board.getPieceAtPosition(currentPosition) != null) {
                        //if there is an ally
                        if (board.getPieceAtPosition(currentPosition).getMyTeam() == currentTeam) {
                            exit = true; //because we found a piece
                        }
                        //if there is an enemy
                        if (board.getPieceAtPosition(currentPosition).getMyTeam() != currentTeam) {
                            possibleAttack.add(currentPosition);
                            exit = true; //because we found a piece
                        }
                    }
                }
                else exit=true; //because we are out of board
                steps++;
            }
            //depending on the attack direction, set the next direction
            if (attackDirection == Direction.ANY) direction += 1;
            else direction += 2;
        }
        return possibleAttack;
    }

    /**
     * Computes a possible directions vector of the piece selected by the user. It calls possibleDirectionRecursiveWalk or possibleDirectionsFly based on the selected piece's move type.
     * @param initialPosition position of the piece selected by the user
     * @param board current board setting
     * @param currentTeam team of the piece in currentPosition
     * @return a vector filled with the allowed positions
     */
    public Vector<Position> possibleDirections(Position initialPosition, Board board, Team currentTeam) {

        int checkElement[][] = {{0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}};
        Vector<Position> allowedPositions = new Vector<Position>();

        if (moveType == Movement.WALK) {
            possibleDirectionsRecursiveWalk(initialPosition, board, currentTeam, allowedPositions, moveRange, checkElement);
        }

        if (moveType == Movement.FLY) {
            possibleDirectionsRecursiveFly(initialPosition, board, currentTeam, allowedPositions, moveRange, checkElement);
        }

        return allowedPositions;

    }

    /**
     * Computes all the possible directions of a type walk piece selected by the user.
     * @param initialPosition the position of the piece that will call the method
     * @param board the actual board configuration
     * @param currentTeam the current team that is playing
     * @param allowedPositions the vector contains the allowed positions. It will be passed from the method possibleDirections that will return it after the computation
     * @param range dynamic range that decreases after each recursive call
     * @param checkElements vector used to avoid the same copy of the element in allowedPosition. This is possible because of the recursive approach.
     */
    public void possibleDirectionsRecursiveWalk(Position initialPosition, Board board, Team currentTeam, Vector<Position> allowedPositions, int range, int[][] checkElements) {

        Position currentPosition;
        int r = initialPosition.getRow();
        int c = initialPosition.getColumn();
        // At the beginning currentPosition is equal to initialPosition
        currentPosition = new Position(r, c);
        int directions = 0;

        // loop all the directions in the following order: right,right up, up, left up, left, left down, down, right down
        while (directions<8) {

            /*
             Update the current position accordingly to the movePosition method. The setRow and setColumn methods allow us to always call movePosition from the
             original position (r,c) for each directions.
            */
            currentPosition.setRow(r);
            currentPosition.setColumn(c);
            currentPosition.movePosition(directions);

            /*
             The isPermittedPositionWalk method check if we are in an allowed position. If the condition is true then we call the method addTheAllowedPosition
             that put a position in the vector after checking possible duplicates
             */
            if (isPermittedPositionWalk(initialPosition,currentPosition,board,currentTeam,range)) {
                addTheAllowedPosition(allowedPositions,currentPosition,checkElements);

                // Call the recursion just if we are in the piece's range and we don't have a piece in current position

                if (range > 1 && board.getPieceAtPosition(currentPosition) == null)
                    possibleDirectionsRecursiveWalk(currentPosition, board, currentTeam, allowedPositions, range - 1, checkElements);
            }

            /*
            The index variable directions increases depending on the move type of the piece. The 'any' type increase of a unit in order to use all the
            positions around the piece. If the piece moves accordingly to 'horizontal-vertical' the index increases of two units in order to skip the
            diagonal directions.
             */
            if(moveDirection == Direction.ANY) directions += 1;
            else if(moveDirection == Direction.STRAIGHT) directions += 2;
        }
    }


    /**
     * Returns true if are verified all the conditions that check if a position is possible for a piece's type 'walk'. It's  a private method used
     * just in possibleDirectionsRecursiveWalk.
     * @param initialPosition the piece's position that calls possibleDirectionsRecursiveWalk
     * @param currentPosition the position that we are checking in order to decide if it will insert in the vector allowedPosition in possibleDirectionsRecursiveWalk
     * @param board the actual configuration of the board
     * @param currentTeam the piece's team that calls possibleDirectionsRecursiveWalk
     * @param range the current range used in possibleDirectionsRecursiveWalk
     * @return a boolean that is true if there is not an ally in the current position and there is not a piece that is different from the one that calls the
     * possibleDirectionsRecursiveWalk method
     */
    private boolean isPermittedPositionWalk(Position initialPosition, Position currentPosition, Board board, Team currentTeam,int range){
        /*
         noAlly is true if in the currentPosition we don't have an ally.

         noPieceInThePassedPosition is true if there is a piece in the previous position. So we stop the research of the cells beyond that position.

         isMeInThePassedPosition is true if in the position there is the piece that calls possibleDirectionsRecursiveWalk. We need it
         because we want to stop the algorithm if there is a piece in the previous position but it mustn't be the piece that calls the function.
         In summary the condition noPiecedInThePassedPosition doesn't care if the piece that is in the previous position is the one that calls the method,
         so in order to know it,  we use isMeInThePassedPosition that is true when the range passed for every recursive calls is equal to the moveRange
         of the piece i.e it is the piece that is calling the method since in the first recursive call they are equal, then the range will decrease and
         we are moving from the position of the calling Piece.
         */
        boolean noAlly = false;
        boolean noPieceInThePassedPosition = false;
        boolean isMeInThePassedPosition = false;

        if(!board.isOutOfBoard(currentPosition)) {
            if (board.whichTeam(currentPosition) != currentTeam)
                noAlly = true;
            if (board.getPieceAtPosition(initialPosition) == null)
                noPieceInThePassedPosition = true;
            if (moveRange == range)
                isMeInThePassedPosition = true;
        }
        return (noAlly && (noPieceInThePassedPosition || isMeInThePassedPosition));

    }


    /**
     * Add a position that is allowed for the piece that calls this method. It checks if there is a duplicate, condition that is possible because of the
     * recursion. It use checkElements which at the beginning has all elements equals to zero. Then when a position is added the element corresponding to
     * that position will be one and the function knows that the position is already inserted in the vector.
     * @param allowedPositions the current vector of allowed positions
     * @param currentPosition the position that we want to add
     * @param checkElements the vector used in order to avoid the duplicates
     */
    private void addTheAllowedPosition(Vector<Position> allowedPositions,Position currentPosition, int[][] checkElements){

        /*
        Notice that we create a new position called accesiblePosition. Since currentPosition will change in possibleDirectionsRecursiveWalk we cannot insert it
        in the vector otherwise we will modify an already pushed position. So using a new position we ensure that there is no reference in the vector to a position
        used in the recursive method.
         */
        if (checkElements[currentPosition.getRow() - 1][currentPosition.getColumn() - 1] == 0) {
            Position accessiblePosition = new Position(currentPosition.getRow(), currentPosition.getColumn());
            allowedPositions.add(allowedPositions.size(), accessiblePosition);
            checkElements[currentPosition.getRow() - 1][currentPosition.getColumn() - 1] = 1;
        }
    }

    /**
     * Computes all the possible directions of a type fly piece selected by the user.
     * @param initialPosition the position of the piece that will call the method
     * @param board the actual board configuration
     * @param currentTeam the current team that is playing
     * @param allowedPositions the vector contains the allowed positions. It will be passed from the method possibleDirections that will return it after the computation
     * @param range dynamic range that decreases after each recursive call
     * @param checkElements vector used to avoid the same copy of the element in allowedPosition. This is possible because of the recursive approach.
     */
    public void possibleDirectionsRecursiveFly(Position initialPosition, Board board, Team currentTeam, Vector<Position> allowedPositions, int range, int[][] checkElements) {

        Position currentPosition;
        int r = initialPosition.getRow();
        int c = initialPosition.getColumn();
        currentPosition = new Position(r, c);
        int directions = 0;

        /*
        The method works similar to possibleDirectionsRecursiveWalk. The only difference now is that we use isPermittedPositionFly which allow to a piece
        to go beyond a piece.
         */
        while (directions<8) {
            currentPosition.setRow(r);
            currentPosition.setColumn(c);
            currentPosition.movePosition(directions);

            if (isPermittedPositionFly(currentPosition, board, currentTeam))
                addTheAllowedPosition(allowedPositions,currentPosition,checkElements);

            if (range > 1 && !board.isOutOfBoard(currentPosition))
                possibleDirectionsRecursiveFly(currentPosition, board, currentTeam, allowedPositions, range - 1, checkElements);

            if(moveDirection == Direction.ANY) directions += 1;
            else if(moveDirection == Direction.STRAIGHT) directions += 2;

        }
    }

    /**
     * Returns true if the piece is not out of the board or the piece is of the other team.
     * @param pos position to test
     * @param board the board containing our position
     * @param currentTeam the team that is moving
     * @return false if the position is outside the board or if there is a piece of the current team
     */
    private boolean isPermittedPositionFly(Position pos, Board board, Team currentTeam) {
        //check if pos is outside the board
        if (pos.getRow() < 1 || pos.getRow() > board.getSize() || pos.getColumn() < 1 || pos.getColumn() > board.getSize())
            return false;
            //check if in the position pos there is a piece of my team
        else return board.whichTeam(pos) != currentTeam;
    }

    /** This method is used to retrieve the name of the image within the resource folder related to the piece
     * @return the string containing the team following by the name of the piece
     */
    public String toString() {
        return (myTeam.toString() + getClass().getSimpleName()).toLowerCase();
    }

    public String nametoString() {
        return (getClass().getSimpleName()).toLowerCase();
    }





}
