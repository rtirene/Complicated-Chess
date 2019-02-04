package com.mobileapp.polimi.maprojectg4.model;
import com.mobileapp.polimi.maprojectg4.model.pieces.Knight;
import com.mobileapp.polimi.maprojectg4.model.pieces.Mage;
import com.mobileapp.polimi.maprojectg4.model.pieces.Squire;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static com.mobileapp.polimi.maprojectg4.model.Match.Spell.HEAL;

/**
 * Match is the engine of the game,it initializes all the other classes and contains the methods that manage movements,
 * attacks an the spells.
 */
public class Match implements Serializable {

    private Board board;
    private int maxPieces;
    private Piece.Team currentTeam;
    private Spell[] unusedSpellsWhite = {Spell.FREEZE, HEAL,Spell.REVIVE,Spell.TELEPORT};
    private Spell[] unusedSpellsBlack  = {Spell.FREEZE, HEAL,Spell.REVIVE,Spell.TELEPORT};
    private FrozenPieces frozenPieces;
    private Position[] specialPositions = new Position[4];
    public enum Winner {WHITE,BLACK, DRAW, NOWINNER}

    public List<Piece> listOfDeadWhitePieces;
    public List<Piece> listOfDeadBlackPieces;

    public Match() {
        this.board = new Board();
        this.maxPieces = 16;
        this.frozenPieces = new FrozenPieces();
        this.currentTeam = Piece.Team.WHITE; //the white starts
        //initialize the special positions
        specialPositions[0]=new Position(1,1);
        specialPositions[1]=new Position(1,4);
        specialPositions[2]=new Position(6,3);
        specialPositions[3]=new Position(6,6);
        this.listOfDeadWhitePieces = new ArrayList<Piece>();
        this.listOfDeadBlackPieces = new ArrayList<Piece>();

    }


    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setCurrentTeam(Piece.Team currentTeam) {
        this.currentTeam = currentTeam;
    }

    public Spell[] getUnusedSpellsWhite() {
        return unusedSpellsWhite;
    }

    public Spell[] getUnusedSpellsBlack() {
        return unusedSpellsBlack;
    }

    public void setUnusedSpellsWhite(Spell[] unusedSpellsWhite) {
        this.unusedSpellsWhite = unusedSpellsWhite;
    }

    public void setUnusedSpellsBlack(Spell[] unusedSpellsBlack) {
        this.unusedSpellsBlack = unusedSpellsBlack;
    }

    public Position[] getSpecialPositions() {
        return specialPositions;
    }

    public int getMaxPieces() {
        return maxPieces;
    }

    public FrozenPieces getFrozenPieces() {
        return frozenPieces;
    }

    public void setFrozenPieces(FrozenPieces frozenPieces) {
        this.frozenPieces = frozenPieces;
    }

    public Piece.Team getCurrentTeam() {
        return currentTeam;
    }

    public enum Spell{
        HEAL,REVIVE,TELEPORT,FREEZE,NOSPELL
    }

    /**
     * Updates the current Team, i.e. switch from black to white and viceversa.
     * Updates the turns left for a piece to be unfrozen.
     */
    public void updateCurrentTeam(){
        frozenPieces.updateTurnLeft(currentTeam);

        if(currentTeam == Piece.Team.WHITE){
            currentTeam = Piece.Team.BLACK;
        }
        else if (currentTeam == Piece.Team.BLACK){
            currentTeam = Piece.Team.WHITE;
        }

    }

    /**
     * Checks if a movement is allowed
     * @param pInit
     * @param pFinal
     * @return
     */
    public boolean canMovePiece(Position pInit ,Position pFinal){
        //check if there is a piece at pInit
        if (board.getPieceAtPosition(pInit)==null){
            return false;
        }
        //check if the piece to be moved is a player's piece
        if (board.whichTeam(pInit)!=currentTeam){
            return false;
        }
        //check if the piece in the initial position is frozen
        if(getFrozenPieces().isPieceAtPositionFrozen(pInit)){
            return false;
        }
        //check if the piece in the final position is of your team
        if (board.whichTeam(pFinal)==currentTeam){
            return false;
        }

        //calling the possibleDirections method of Class Piece, a vector of allowed positions is computed
        //given the initial position of the piece.
        Vector<Position> listOfPossiblePositions;
        listOfPossiblePositions = board.getPieceAtPosition(pInit).possibleDirections(pInit, board, board.whichTeam(pInit));

        boolean found = false;
        for (Position pos : listOfPossiblePositions) {
            //if the final position is in the list of allowed position
            if (pos.getColumn() == pFinal.getColumn() && pos.getRow() == pFinal.getRow()) {
                found=true;
            }
        }
        //return true if the pieces was found, or false if it was not found
        return found;
    }

    /**
     * Manages the movement of the pieces on the board
     * and call the engageCombat method in case the final position is occupied
     * @param pInit initial position
     * @param pFinal final position
     * @return
     */
    public void movePiece(Position pInit ,Position pFinal){
        //check if there is a piece at pInit
        if (board.getPieceAtPosition(pInit)==null){
            throw new IllegalArgumentException("There is no piece on pInit");
        }
        //check if the piece to be moved is a player's piece
        if (board.whichTeam(pInit)!=currentTeam){
            throw new IllegalArgumentException("You can't move an opponent piece");
        }
        //check if the piece in the initial position is frozen
        if(getFrozenPieces().isPieceAtPositionFrozen(pInit)){
            throw new IllegalArgumentException("The piece can't be moved because it's frozen");
        }

        //calling the possibleDirections method of Class Piece, a vector of allowed positions is computed
        //given the initial position of the piece.
        Vector<Position> listOfPossiblePositions;
        listOfPossiblePositions = board.getPieceAtPosition(pInit).possibleDirections(pInit, board, board.whichTeam(pInit));

        boolean found = false;
        for (Position pos : listOfPossiblePositions) {
            //if the final position is in the list of allowed position
            if (pos.getColumn() == pFinal.getColumn() && pos.getRow() == pFinal.getRow()) {
                //if the final position is free
                if (board.getPieceAtPosition(pFinal) == null) {
                    board.movePieceInTable(pInit, pFinal);
                } else {
                    //if there's a enemy on the final position
                    Position[] positionsOfDeadPieces = engageCombat(pInit, pFinal);
                    if (positionsOfDeadPieces[0] == null) { //the attacker wins, the defender is dead
                        killPiece(pFinal); //clear the position of the defender
                        board.movePieceInTable(pInit, pFinal); //move the attacker
                    } else if (positionsOfDeadPieces[1] == null) { //the defender wins, the attacker is dead
                        killPiece(pInit); //clear the position of the attacker
                        //the life of the defender is already updated by engageCombat
                    } else { //they are both dead
                        killPiece(pFinal); //clear the position of the defender
                        killPiece(pInit); //clear the position of the attacker
                    }
                }
                found = true;
            }
        }
        //if the final position it has not been found in the list of allowed position
        if (!found) {
            throw new IllegalArgumentException("The desired movement is not allowed");
        }
    }
    /**
     * Manages the changes in the vitality after the attacks, clear the position in case the piece is killed.
     * @param pAttack the position of the piece who attack
     * @param pDefend the position of the piece which has to defend
     * @throws IllegalArgumentException
     *
     */
    public void attack(Position pAttack,Position pDefend){
        //check if we are attacking with a piece of our team
        if (board.whichTeam(pAttack)!=currentTeam){
            throw new IllegalArgumentException("You cannot attack with an opponent's piece");
        }
        //check if the attacking piece is frozen
        if(getFrozenPieces().isPieceAtPositionFrozen(pAttack)){
            throw new IllegalArgumentException("The piece can't attack because it's frozen");
        }
        //calling the possibleAttacks method of Class Piece, a vector of allowed attack positions is computed
        //given the initial position of the piece.
        Vector<Position> listOfPossibleAttacks = board.getPieceAtPosition(pAttack).possibleAttacks(pAttack,board,board.whichTeam(pAttack));
        boolean found=false;
        for (Position pos : listOfPossibleAttacks) {
            //if the desired attack position is in the list of allowed attack positions
            if (pos.getColumn() == pDefend.getColumn() && pos.getRow() == pDefend.getRow()) {
                found=true;
            }
        }
        if(!found){
            //if the desired attack position is not in the list of allowed attack positions, throws an exception
            throw new IllegalArgumentException("The attack position is not allowed");
        }
        //Otherwise get the new vitality of the defender
        // after the attack which is equal to : Vitality of the defender - Strength of the attacker
        int newVitalityOfthePiece = board.getVitalityAt(pDefend) - board.getPieceAtPosition(pAttack).getStrength();
        if (newVitalityOfthePiece >0) //the defender is not dead
            board.getPieceAtPosition(pDefend).setCurrentVitality(newVitalityOfthePiece);
        else { //If the vitality of the defender is zero, i.e. the defender is dead, his position is cleared
            killPiece(pDefend);
        }
    }

    /** Checks if the attack is legal.
     * @param pAttack
     * @param pDefend
     * @return
     */
    public boolean canAttack(Position pAttack, Position pDefend){
        //check if we are attacking with a piece of our team
        if (board.whichTeam(pAttack)!=currentTeam) return false;
        //check if the attacking piece is frozen
        if(getFrozenPieces().isPieceAtPositionFrozen(pAttack)) return false;
        //calling the possibleAttacks method of Class Piece, a vector of allowed attack positions is computed
        //given the initial position of the piece.
        Vector<Position> listOfPossibleAttacks = board.getPieceAtPosition(pAttack).possibleAttacks(pAttack,board,board.whichTeam(pAttack));
        boolean found=false;
        for (Position pos : listOfPossibleAttacks) {
            //if the desired attack position is in the list of allowed attack positions
            if (pos.getColumn() == pDefend.getColumn() && pos.getRow() == pDefend.getRow()) {
                found=true;
            }
        }
        return found;

    }

    /**
     * Used when two pieces of different teams are going to be in the same position.
     * Updates the respective vitalities after the combat.
     * They inflict each other their strength until someone (or both of them) die.
     * If the defender is frozen, the defender die and the attacker suffers no damage.
     * @param pAttack the position of the piece that is going to move on another piece
     * @param pDefend the position of the piece whose place is going to be occupied by another piece
     * @return an array of Positions. The first element of the array is the position of the attacker if it died (otherwise is null),
     * The second element is the position of the defender if it died (otherwise is null)
     */
    public Position[] engageCombat(Position pAttack,Position pDefend){
        int attackerVitality=board.getVitalityAt(pAttack); //retrieve the vitality of the attacker
        int defenderVitality=board.getVitalityAt(pDefend); //retrieve the vitality of the defender
        //the array to return
        Position[] positionsOfDeadPieces = new Position[2];
        //check if in the attacked position there is a frozen piece: in that case it is killed
        // and the attacker wins the combat without losing vitality
        if(getFrozenPieces().isPieceAtPositionFrozen(pDefend)){
            positionsOfDeadPieces[1]=pDefend;
            board.getPieceAtPosition(pDefend).setCurrentVitality(0);
            board.getPieceAtPosition(pAttack).setCurrentVitality(attackerVitality);
            return positionsOfDeadPieces;
        }
        //Otherwise given the two pieces it calls the combat
        Piece attacker = board.getPieceAtPosition(pAttack);
        Piece defender = board.getPieceAtPosition(pDefend);
        int[] vitalityOfFighters= engageCombat(attacker,defender,attackerVitality,defenderVitality);
        //update the vitalities of attacker and defender after the combat
        board.getPieceAtPosition(pAttack).setCurrentVitality(vitalityOfFighters[0]);
        board.getPieceAtPosition(pDefend).setCurrentVitality(vitalityOfFighters[1]);
        if (vitalityOfFighters[0]==0) { //the attacker is dead
            positionsOfDeadPieces[0]=pAttack;
        }
        if (vitalityOfFighters[1]==0){ //the defender is dead
            positionsOfDeadPieces[1]=pDefend;
        }
        return positionsOfDeadPieces;
    }

    /**
     * Used to manage the vitalities of two pieces involved in a combat
     * They inflict each other their strength until someone (or both of them) die.
     * WARNING: this method does not update vitality
     * WARNING: this method does not check if the are frozen pieces
     * @param attacker the piece that wants engage the combat
     * @param defender the piece that defends himself
     * @param attackerVitality attacker current vitality
     * @param defenderVitality defender current vitality
     * @return an array of vitality. The first element of the array is the vitality of the attacker (0 if it died),
     * The second element is the vitality of the defender (0 if it died)
     */
    public int[] engageCombat(Piece attacker, Piece defender, int attackerVitality, int defenderVitality){
        int outputVitality[] = new int[2];
        Arrays.fill(outputVitality,0); //initialize the array to 0
        Boolean someoneIsDead=false;
        while(!someoneIsDead){  //they fight until someone is dead
            attackerVitality=attackerVitality-defender.getStrength(); //the vitality is decreased by the strength of the defender
            defenderVitality=defenderVitality-attacker.getStrength(); //the vitality is decreased by the strength of the attacker
            if (attackerVitality<1) { //attacker is dead
                someoneIsDead=true; //to exit from the cycle
                outputVitality[0]=0;
            }
            else { //if the attacker is not dead, update its vitality
                outputVitality[0]=attackerVitality; //update the life of attacker
            }
            if (defenderVitality<1) { //defender is dead
                someoneIsDead=true;
                outputVitality[1]=0;
            }
            else { //if the defender is not dead, update its vitality
                outputVitality[1]=defenderVitality; //update the life of the defender
            }
        }
        return outputVitality;
    }

    public Spell[] getUnusedSpells(){
        Spell[] returnedSpells = new Spell[0];
        if(currentTeam== Piece.Team.WHITE) returnedSpells = unusedSpellsWhite;
        else if(currentTeam== Piece.Team.BLACK) returnedSpells = unusedSpellsBlack;
        return returnedSpells;
    }

    /**
     * Checks is we are casting spells on legal cells
     * @param targetPosition
     * @param finalPosition
     * @param spell
     * @return
     */
    public boolean isSpellPermitted(Position targetPosition, Position finalPosition, Spell spell){
        //check is we are targeting a special position
        for (Position pos : specialPositions){
            //check if we are targeting a special position
            if (targetPosition.getColumn()== pos.getColumn() && targetPosition.getRow()==pos.getRow() || finalPosition.getColumn()==pos.getColumn() && finalPosition.getRow()==pos.getRow()){
                return false;
            }
        }
        //check if we are targeting a magician
        if (board.getPieceAtPosition(targetPosition) instanceof Mage){
            return false;
        }
        switch (spell) {
            case HEAL: {
                if (board.whichTeam(targetPosition) != currentTeam) {
                    return false;
                }
                break;
            }
            case TELEPORT: { //teleport
                //check if the piece in the final position is a mage
                if(board.getPieceAtPosition(finalPosition)instanceof Mage) return false;
                //if the target position is occupied by a enemy team piece
                else if (board.whichTeam(targetPosition)!=currentTeam) return false;
                //if the final position is occupied by a piece of the same team
                else if (board.whichTeam(finalPosition)==currentTeam) return false;
                break;
            }
            case REVIVE:{ //revive
                //we initialize a new board to retrieve the piece to be revived, given is initial position, that is the target
                //position of the revive spell
                Board initialBoard = new Board();
                Piece pieceToRevive = initialBoard.getPieceAtPosition(targetPosition);
                //check if the piece to be revived is of the same team
                if(pieceToRevive != null) {
                    if (pieceToRevive.getMyTeam() != currentTeam) {
                        return false;
                    }
                    //check if the piece is not dead
                    boolean found = false;
                    boolean foundFirst = false;
                    for (int r = 1; r <= board.getSize(); r++) {  //select the row
                        for (int c = 1; c <= board.getSize(); c++) {  //select the columns
                            if (board.getPieceAtPosition(new Position(r, c)) != null && board.getPieceAtPosition(new Position(r, c)).getMyTeam() == currentTeam) {
                                if (board.getPieceAtPosition(new Position(r, c)).getClass() == pieceToRevive.getClass()) {
                                    //in the case of Squire and Knight we have two pieces to check
                                    if (pieceToRevive.getClass() != Squire.class && pieceToRevive.getClass() != Knight.class)
                                        found = true;
                                    else if (foundFirst)
                                        found = true;
                                    else foundFirst = true;
                                }
                            }
                        }
                    }
                    if (found) {
                        return false;
                    }
                    if (board.whichTeam(targetPosition) == currentTeam) return false;
                }else return false;
                break;
            }
            case FREEZE:{ //freeze
                //check if the piece to be frozen is an opponent piece
                if(board.whichTeam(targetPosition)== currentTeam){
                    return false;
                }
                break;
            }
        }
        return true;
    }

    /** Manages the spells casted by the magicians
     * @param spellType the spell type can be one of the constants value of the enum Spell: HEAL, TELEPORT, REVIVE, FREEZE
     * @param targetPosition the position on which the player wants call the spell
     * @param finalPosition the position where a piece is teleported. It's just used in TELEPORT
     */
    public void castSpell(Spell spellType,Position targetPosition,Position finalPosition){
        //check if there's a magician character in the current team
        boolean magicianFound = false;
        for (int r=1; r<=board.getSize(); r++){  //select the row
            for (int c=1; c<=board.getSize(); c++){  //select the columns
                if (board.getPieceAtPosition(new Position(r,c)) != null &&  board.getPieceAtPosition(new Position(r,c)).getMyTeam()==currentTeam){
                    if (board.getPieceAtPosition(new Position(r,c)).isCanUseSpells()){
                        magicianFound = true;
                    }
                }
            }
        }
        if (!magicianFound){
                throw new IllegalArgumentException("All the magician piece of your team are dead, the spell can't be casted");
        }

        //check if we are targeting a magician
        if (board.getPieceAtPosition(targetPosition) instanceof Mage){
            throw new IllegalArgumentException("The spell can't be cast on a position with a mage");
        }
        //check if we are targeting a special position
        for (Position pos : specialPositions){
            if (targetPosition.getColumn()== pos.getColumn() && targetPosition.getRow()==pos.getRow() || finalPosition.getColumn()==pos.getColumn() && finalPosition.getRow()==pos.getRow()){
                throw new IllegalArgumentException("The spell can't be cast on special cells");
            }
        }
        switch (spellType){
            case HEAL: { //heal
                //check if the heal spell has been used yet by a mage, if not, update unused spells
                updateUnusedSpells(HEAL);
                //if the target position is occupied by a enemy team piece
                if (board.whichTeam(targetPosition)!=currentTeam){
                    throw new IllegalArgumentException("It is not possible to heal an enemy piece");
                }
                //The Heal spell set the vitality of the piece at his initial value
                int maxVitality= board.getPieceAtPosition(targetPosition).getMaxVitality();
                board.getPieceAtPosition(targetPosition).setCurrentVitality(maxVitality);
                break;
            }

            case TELEPORT: { //teleport
                //check if the teleport spell has been used yet by a mage, if not, update unused spells
                updateUnusedSpells(Spell.TELEPORT);
                //check if the piece in the final position is a mage
                if(board.getPieceAtPosition(finalPosition)instanceof Mage){
                    throw new IllegalArgumentException("The spell can't be cast on a position with a mage");
                }
                //if the target position is occupied by a enemy team piece
                if (board.whichTeam(targetPosition)!=currentTeam){
                    throw new IllegalArgumentException("It is not possible to teleport an enemy piece");
                }
                //if final position is free
                if (board.getPieceAtPosition(finalPosition)==null) {
                    board.movePieceInTable(targetPosition,finalPosition);
                }
                //if final position is occupied by an enemy the two pieces are involved in a combat
                else if (board.whichTeam(finalPosition)!= currentTeam){
                    Position[] positionsOfDeadPieces = engageCombat(targetPosition, finalPosition);
                    if (positionsOfDeadPieces[0] == null) { //the attacker wins, the defender is dead
                        killPiece(finalPosition); //clear the position of the defender
                        board.movePieceInTable(targetPosition,finalPosition); //move the attacker
                        //the life of the attacker is already updated by engageCombat
                    } else if (positionsOfDeadPieces[1] == null) { //the defender wins, the attacker is dead
                        killPiece(targetPosition); //clear the position of the attacker
                        //the vitality of the defender is already set by engageCombat
                    } else { //they are both dead
                        killPiece(targetPosition);
                        killPiece(finalPosition);
                    }
                }
                else {
                    //if the final position is occupied by a piece of the same team
                    throw new IllegalArgumentException("It is not possible to teleport a piece on a piece of your team");
                }
                break;
            }
            case REVIVE:{ //revive
                //check if the revive spell has been used yet by a mage, if not, update unused spells
                updateUnusedSpells(Spell.REVIVE);
                //if the position is free, revive the piece

                //we initialize a new board to retrieve the piece to be revived, given is initial position, that is the target
                //position of the revive spell
                Board initialBoard = new Board();
                Piece pieceToRevive = initialBoard.getPieceAtPosition(targetPosition);
                //check if the piece to be revived is of the same team
                if(pieceToRevive.getMyTeam()!= currentTeam){
                    throw new IllegalArgumentException("You can't revive an opponent team");
                }
                //check if the piece is not dead, otherwise throw an exception
                boolean found=false;
                boolean foundFirst=false;
                for (int r=1; r<=board.getSize(); r++){  //select the row
                    for (int c=1; c<=board.getSize(); c++){  //select the columns
                        if (board.getPieceAtPosition(new Position(r,c))!= null && board.getPieceAtPosition(new Position(r,c)).getMyTeam()==currentTeam) {
                            if (board.getPieceAtPosition(new Position(r, c)).getClass() == pieceToRevive.getClass()) {
                                //in the case of Squire and Knight we have two pieces to check
                                if (pieceToRevive.getClass() != Squire.class && pieceToRevive.getClass() != Knight.class)
                                    found = true;
                                else if (foundFirst)
                                    found = true;
                                else foundFirst = true;
                            }
                        }
                    }
                }
                if (found){
                    throw new IllegalStateException("The piece you want to revive is not dead");
                }
                //if the position is free, revive the piece (checking the initial board)
                if (board.getPieceAtPosition(targetPosition)== null){
                    board.setPositionInTable(targetPosition,pieceToRevive);
                    //delete the piece from the list of dead pieces
                    removePiecesFromDeadLists(pieceToRevive);
                }
                else if (board.whichTeam(targetPosition)!=currentTeam){ //if there is an enemy
                    int[] vitalityOfFighters = new int[2];
                    //check if the piece in target position is frozen
                    if(getFrozenPieces().isPieceAtPositionFrozen(targetPosition)){
                        vitalityOfFighters[1]=0; //the piece is killed
                        vitalityOfFighters[0]=pieceToRevive.getMaxVitality();
                    }
                    else {
                        vitalityOfFighters = engageCombat(pieceToRevive, board.getPieceAtPosition(targetPosition), pieceToRevive.getMaxVitality(), board.getVitalityAt(targetPosition));
                    }

                    //update the board
                    if (vitalityOfFighters[0] == 0 && vitalityOfFighters[1]!= 0) { //the piece to revive is dead after the combat
                        // update the vitality of the piece in target position
                       board.getPieceAtPosition(targetPosition).setCurrentVitality(vitalityOfFighters[1]);

                    } else if (vitalityOfFighters[1] == 0 && vitalityOfFighters[0]!=0) { //the defender is dead, revive the piece
                        killPiece(targetPosition);
                        board.setPositionInTable(targetPosition,pieceToRevive);
                        removePiecesFromDeadLists(pieceToRevive);
                        pieceToRevive.setCurrentVitality(vitalityOfFighters[0]);



                        } else { //they are both dead
                        killPiece(targetPosition); //clear the position
                    }



                }



                else {
                    //if the target position is currently occupied by a piece of the same team
                    throw new IllegalArgumentException("if the target position is occupied by a piece of the same team, the piece cannot be revived there");
                }
                break;
            }
            case FREEZE:{ //freeze
                //check if the freeze spell has been used yet by a mage, if not, update unused spells
                updateUnusedSpells(Spell.FREEZE);
                //check if the piece to be frozen is an opponent piece
                if(board.whichTeam(targetPosition)!= currentTeam){
                    if (currentTeam == Piece.Team.WHITE) {
                        //freeze a black piece
                        frozenPieces.setFrozenBlack(targetPosition);
                    }

                    else {
                        //freeze the white piece
                        frozenPieces.setFrozenWhite(targetPosition);
                    }
                }

                else{
                    throw new IllegalArgumentException("It is not possible to freeze a piece of the same team");
                }
                break;
            }
        }
    }

    public void removePiecesFromDeadLists(Piece piece){
        if(currentTeam == Piece.Team.WHITE){
            for(Piece p : listOfDeadWhitePieces){
                if(p.toString().equals(piece.toString())){
                    listOfDeadWhitePieces.remove(p);
                    return;
                }
            }
        }
        if(currentTeam == Piece.Team.BLACK){
            for(Piece p: listOfDeadBlackPieces){
                if(p.toString().equals(piece.toString())){
                    listOfDeadBlackPieces.remove(p);
                    return;
                }
            }
        }
    }

    /**
     * Updates the UnusedSpellsWhite array or the UnusedSpellsBlack array given the team of
     * the mage that cast the spell.
     * If a spell is already been used it sets to NOSPELL the relative position in the unused spells arrays.
     * @param spell spell that are used
     */
    public void updateUnusedSpells(Spell spell){


        if(currentTeam == Piece.Team.WHITE){
            //check if the spell has already been used, in which case throws an exception
            boolean found = false;
            for (Spell i : unusedSpellsWhite){
                if (i == spell) found = true;
            }
            if (found != true) throw new IllegalStateException("The spell has been already used");
            //check the index of the used spell in the index
            int indexOfSpell = Arrays.asList(unusedSpellsWhite).indexOf(spell);
            //set the used spell to NOSPELL
            unusedSpellsWhite[indexOfSpell] = Spell.NOSPELL;

        }else if(currentTeam == Piece.Team.BLACK){
            boolean found = false;
            for (Spell i : unusedSpellsBlack){
                if (i == spell) found = true;
            }
            if (found != true) throw new IllegalStateException("The spell has been already used");
            //we initialize a new board to retrieve the piece to be revived
            int indexOfSpell = Arrays.asList(unusedSpellsBlack).indexOf(spell);
            //set the used spell to NOSPELL
            unusedSpellsBlack[indexOfSpell] = Spell.NOSPELL;
        }
    }

    /**
     * Returns the winner checking all the conditions.
     * @return black, white, draw if the winner is black, white or the game ended in a draw
     */
    public Winner decideWinner(){

        Winner winner = Winner.NOWINNER;
        int specialPositionsBlack = 0;
        int specialPositionsWhite = 0;
        int blackLeft = 0;
        int whiteLeft = 0;
        int whitePiecesFrozen = frozenPieces.getCounterWhitePiecesFrozen();
        int blackPiecesFrozen = frozenPieces.getCounterBlackPiecesFrozen();
        int whitePiecesNonFrozen;
        int blackPiecesNonFrozen;

        // Counts how many special positions are occupied by the black player and the white player
        for (Position p: specialPositions){
            if(board.whichTeam(p) == Piece.Team.BLACK)
                specialPositionsBlack++;
            if(board.whichTeam(p) == Piece.Team.WHITE)
                specialPositionsWhite++;
        }

        // Counts how many black pieces and white pieces are left in the game
        for (int r = 0; r <=5; r++)
            for(int c = 0; c<=5; c++){
            if(board.whichTeam(new Position(r+1,c+1)) == Piece.Team.WHITE )
                whiteLeft++;
            else if(board.whichTeam(new Position(r+1,c+1)) == Piece.Team.BLACK )
                blackLeft++;
        }

        // Compute how many black and white pieces left in the game are not frozen
        whitePiecesNonFrozen = whiteLeft-whitePiecesFrozen;
        blackPiecesNonFrozen = blackLeft-blackPiecesFrozen;

        /*  Check for the winner.
        1)  If a P player occupying at least 3 special positions
        2)  If a P's opponent has all the pieces frozen or dead and P has at least one piece and one piece not frozen
        If none of the conditions above are true for one of the players then set NOWINNER
          */
        if(specialPositionsBlack>=3 || ( (whiteLeft==0 || whiteLeft==whitePiecesFrozen) && blackLeft>0 && blackPiecesNonFrozen>=1 ))
            winner = Winner.BLACK;
        else if(specialPositionsWhite>=3 || (( blackLeft==0||blackLeft==blackPiecesFrozen) && whiteLeft>0 && whitePiecesNonFrozen>=1))
            winner = Winner.WHITE;
        else
            winner = Winner.NOWINNER;

        /* Check for the draw
           If a P player has all the pieces dead or all pieces frozen and before the P's opponent did not win then set DRAW
         */
        if((whiteLeft ==  0 || whiteLeft == whitePiecesFrozen) && winner != Winner.BLACK)
            winner = Winner.DRAW;

        if((blackLeft ==  0 || blackLeft == blackPiecesFrozen) && winner != Winner.WHITE)
            winner = Winner.DRAW;

        return winner;

    }

    /**
     * Clears the position of a piece that has been killed
     * @param pos the position where is the piece to be killed
     */
    public void killPiece(Position pos){

        if (board.getPieceAtPosition(pos) == null) {
            throw new IllegalArgumentException("The selected position is already free");
        }

        //update the frozen pieces
        if (frozenPieces.isPieceAtPositionFrozen(pos)){
            //if the frozen piece is white
            if (board.whichTeam(pos)== Piece.Team.WHITE){
                frozenPieces.unfreezeWhite();
            }
            //if the frozen piece is black
            if (board.whichTeam(pos)== Piece.Team.BLACK){
                frozenPieces.unfreezeBlack();
            }
        }
        //add the killed piece to the list of dead pieces, given the team
        if(board.getPieceAtPosition(pos).getMyTeam()== Piece.Team.WHITE){
            listOfDeadWhitePieces.add(board.getPieceAtPosition(pos));

        }
        if (board.getPieceAtPosition(pos).getMyTeam()==Piece.Team.BLACK){
            listOfDeadBlackPieces.add(board.getPieceAtPosition(pos));
        }
        board.setPositionInTable(pos,null);
    }

    public List<Piece> getListOfDeadWhitePieces() {
        return listOfDeadWhitePieces;
    }

    public List<Piece> getListOfDeadBlackPieces() {
        return listOfDeadBlackPieces;
    }

    public void setListOfDeadWhitePieces(List<Piece> listOfDeadWhitePieces) {
        this.listOfDeadWhitePieces = listOfDeadWhitePieces;
    }

    public void setListOfDeadBlackPieces(List<Piece> listOfDeadBlackPieces) {
        this.listOfDeadBlackPieces = listOfDeadBlackPieces;
    }

    public List<Piece> getListOfDeadPieces() {
        if (currentTeam == Piece.Team.WHITE) return listOfDeadWhitePieces;
        else if (currentTeam == Piece.Team.BLACK) return listOfDeadBlackPieces;
        else throw new IllegalStateException("This is not the turn of black or white");
    }

    /**
     * @param p the piece in which we are interested
     * @return the initial position or the list of initial position of the piece
     */
    public Vector<Position> initialPosOfPiece(Piece p){
        Board firstBoard=new Board();
        Vector<Position> initialPositions = new Vector<Position>();
        for (int r=1; r <= firstBoard.getSize(); r++){  //select the row
            for (int c=1; c <= firstBoard.getSize(); c++) {  //select the columns
                Position pos = new Position(r,c);
                if (firstBoard.getPieceAtPosition(pos)!=null && firstBoard.getPieceAtPosition(pos).getMyTeam()==currentTeam){
                    if(firstBoard.getPieceAtPosition(pos).getClass() == p.getClass()){
                        initialPositions.add(pos);
                    }
                }
            }
        }
        return initialPositions;
    }

    public Piece.Team getOpponentTeam(){
        if(currentTeam == Piece.Team.WHITE)
            return Piece.Team.BLACK;
        else if(currentTeam == Piece.Team.BLACK)
            return Piece.Team.WHITE;
        else
            return Piece.Team.FREE;
    }
}
