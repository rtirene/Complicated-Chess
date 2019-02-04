package com.mobileapp.polimi.maprojectg4.testUtils;

import com.mobileapp.polimi.maprojectg4.model.Match;
import com.mobileapp.polimi.maprojectg4.model.Piece;
import com.mobileapp.polimi.maprojectg4.model.Position;

public class TurnUtils {

    static final int boardSize = 36;
    static final int firstBoard = 1;
    static final int lastBoard = boardSize;
    static final int firstVitality = lastBoard +1;
    static final int lastVitality = firstVitality + 15;
    static final int firstFrozen = lastVitality +1;
    static final int lastFrozen = firstFrozen + 5;
    static final int firstUnusedSpells = lastFrozen +1;
    static final int lastUnusedSpells = firstUnusedSpells + 7;


    public static int getLastUnusedSpells() {
        return lastUnusedSpells;
    }

    /** Executes a turn, updating the attributes of the class Match
     * @param turn String of length with the information of a turn as explained on the slide
     * @throws IllegalStateException if you try to make other moves when the game ended for a Draw or Victory
     */
    public static void playTurn(String turn, Match match){

        //check if the game is finished (draw or someone wins)
        if(match.decideWinner() != Match.Winner.NOWINNER) {
            if (match.decideWinner() == Match.Winner.DRAW)
                throw new IllegalStateException("You cannot continue to play, the game ended in a" + match.decideWinner());
            else
                throw new IllegalStateException("You cannot continue to play, the game ended, " + match.decideWinner() + "won");
        }

        int cFirstPosition = Character.getNumericValue(turn.charAt(1));
        int rFirstPosition = Character.getNumericValue(turn.charAt(2));
        int cSecondPosition = Character.getNumericValue(turn.charAt(3));
        int rSecondPosition = Character.getNumericValue(turn.charAt(4));

        Position firstPosition = new Position(rFirstPosition, cFirstPosition);
        Position secondPosition = new Position(rSecondPosition, cSecondPosition);

        switch (turn.charAt(0)){

            case 'M' :
                match.movePiece(firstPosition,secondPosition);
                break;
            case 'A' :
                match.attack(firstPosition,secondPosition);
                break;
            case 'H' :
                match.castSpell(Match.Spell.HEAL,firstPosition,secondPosition);
                break;
            case 'T' :
                match.castSpell(Match.Spell.TELEPORT,firstPosition,secondPosition);
                break;
            case 'R' :
                match.castSpell(Match.Spell.REVIVE,firstPosition,secondPosition);
                break;
            case 'F' :
                match.castSpell(Match.Spell.FREEZE,firstPosition,secondPosition);
                break;
            default:
                throw new IllegalArgumentException("The string that you inserted doesn't not follow the specifics" +
                        "\n Please insert a valid string!");
        }

        match.updateCurrentTeam();
        match.decideWinner();
    }

    /** This function convert all the information of the actual game configurations into a string.
     * It is used to do the test conforming with the slide.
     * @return A string with the actual configurations of the game.
     */
    public static String turnToString(Match match){
        String turnString = "";
        if(match.getCurrentTeam() == Piece.Team.WHITE)
            turnString = turnString + "W";
        if(match.getCurrentTeam() == Piece.Team.BLACK)
            turnString = turnString + "B";
        turnString = turnString + TestUtils.boardToString(match) + TestUtils.vitalityToString(match) + TestUtils.getFrozenPiecesAsString(match) +
                TestUtils.getUnusedSpellAsString(match);

        return turnString;
    }

    /** The function set all the attributes of the class Match retrieving the information from the passed parameter
     * @param turnString String that contains the information about the match configuration
     */
    public static void setTurn(String turnString, Match match){

        //set the initial team
        if(turnString.startsWith("W"))
            match.setCurrentTeam(Piece.Team.WHITE);
        if(turnString.startsWith("B"))
            match.setCurrentTeam(Piece.Team.BLACK);

        StringBuilder turnStringSB= new StringBuilder(turnString);

        TestUtils.updateBoard(turnStringSB.substring(firstBoard,lastBoard+1),match);
        TestUtils.updateVitality(turnStringSB.substring(firstVitality,lastVitality+1),match);
        TestUtils.setFrozenFromString(turnStringSB.substring(firstFrozen,lastFrozen+1),match);
        StringBuilder spellString = new StringBuilder(turnStringSB.substring(firstUnusedSpells,lastUnusedSpells+1));

        //set unusedSpell White
        StringBuilder spellStringWhite = new StringBuilder(spellString.substring(0,4));
        Match.Spell[] arrayWhite = new Match.Spell[4];
        for (int i=0; i<spellStringWhite.length(); i++){
            switch (spellStringWhite.charAt(i)){
                case 'F': arrayWhite[0]= Match.Spell.FREEZE;
                    break;
                case 'H': arrayWhite[1]= Match.Spell.HEAL;
                    break;
                case 'R': arrayWhite[2]= Match.Spell.REVIVE;
                    break;
                case 'T': arrayWhite[3]= Match.Spell.TELEPORT;
                    break;
                default: arrayWhite[i]= Match.Spell.NOSPELL;
            }
        }
        match.setUnusedSpellsWhite(arrayWhite);

        //set unusedSpell Black
        StringBuilder spellStringBlack = new StringBuilder(spellString.substring(4,8));
        Match.Spell[] arrayBlack = new Match.Spell[4];
        for (int i=0; i<spellStringBlack.length(); i++){
            switch (spellStringBlack.charAt(i)){
                case 'F': arrayBlack[0]= Match.Spell.FREEZE;
                    break;
                case 'H': arrayBlack[1]= Match.Spell.HEAL;
                    break;
                case 'R': arrayBlack[2]= Match.Spell.REVIVE;
                    break;
                case 'T': arrayBlack[3]= Match.Spell.TELEPORT;
                    break;
                default: arrayBlack[i]= Match.Spell.NOSPELL;
            }
        }
        match.setUnusedSpellsBlack(arrayBlack);
    }

    /**
     * Returns a string that is the state of the game, as explained in slide 31 or
     * a string of with the explanation of what went wrong
     * @param arg the state (67 character) + the turn(s)
     * @return a string of 67 character + the winner if there was one (or DRAW)
     */
    public static String turnTest(String arg){
        Match match = new Match();
        StringBuilder inputString = new StringBuilder(arg);
        setTurn(inputString.substring(0,lastUnusedSpells+1), match); //
        String turns = arg.substring(lastUnusedSpells+1);
        int sizeTurns = turns.length();


        try{
            for(int i = 0 ; i < sizeTurns; i=i+5){
                playTurn(turns.substring(i,i+5),match);
            }
            StringBuilder returnedString = new StringBuilder(turnToString(match));

            Match.Winner winner = match.decideWinner();
            if(winner != Match.Winner.NOWINNER)
                returnedString.append(winner.toString());

            return returnedString.toString();

        }catch (Exception e){
            return "ERROR: " + e.getMessage();
        }
    }


}



