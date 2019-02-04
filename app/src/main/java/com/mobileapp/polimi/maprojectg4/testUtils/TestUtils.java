package com.mobileapp.polimi.maprojectg4.testUtils;


import com.mobileapp.polimi.maprojectg4.model.pieces.Archer;
import com.mobileapp.polimi.maprojectg4.model.pieces.Dragon;
import com.mobileapp.polimi.maprojectg4.model.pieces.Giant;
import com.mobileapp.polimi.maprojectg4.model.pieces.Knight;
import com.mobileapp.polimi.maprojectg4.model.pieces.Mage;
import com.mobileapp.polimi.maprojectg4.model.Match;
import com.mobileapp.polimi.maprojectg4.model.Piece;
import com.mobileapp.polimi.maprojectg4.model.Position;
import com.mobileapp.polimi.maprojectg4.model.pieces.Squire;

import java.util.ArrayList;
import java.util.List;

import static com.mobileapp.polimi.maprojectg4.model.Match.Spell.FREEZE;
import static com.mobileapp.polimi.maprojectg4.model.Match.Spell.HEAL;
import static com.mobileapp.polimi.maprojectg4.model.Match.Spell.REVIVE;
import static com.mobileapp.polimi.maprojectg4.model.Match.Spell.TELEPORT;


/**
 * A class that contains method useful to test the model
 */
public class TestUtils {


    /**
     * Given a character, characterToPiece return the Piece that correspond to the character.
     * The Uppercase character are for White pieces, lowercase character are for Black pieces.
     * @param characterOfPiece: the character can be A,a (archer),G,g (giant),D,d (dragon),S,s (squire),K,k (knight),M,m (mage)
     * @return an instance of the class Giant, Mage, Archer, Dragon, Squire or Knight
     */
    public static Piece characterToPiece(char characterOfPiece){
        Piece piece;
        switch (characterOfPiece){
            case 'A':
                piece = new Archer(Piece.Team.WHITE);
                break;
            case 'K':
                piece = new Knight(Piece.Team.WHITE);
                break;
            case 'D':
                piece = new Dragon(Piece.Team.WHITE);
                break;
            case 'M':
                piece =new Mage(Piece.Team.WHITE);
                break;
            case 'S':
                piece =new Squire(Piece.Team.WHITE);
                break;
            case 'G':
                piece = new Giant(Piece.Team.WHITE);
                break;
            case 'a':
                piece = new Archer(Piece.Team.BLACK);
                break;
            case 'k':
                piece = new Knight(Piece.Team.BLACK);
                break;
            case 'd':
                piece = new Dragon(Piece.Team.BLACK);
                break;
            case 'm':
                piece =new Mage(Piece.Team.BLACK);
                break;
            case 's':
                piece =new Squire(Piece.Team.BLACK);
                break;
            case 'g':
                piece = new Giant(Piece.Team.BLACK);
                break;
            case '0' :
                piece = null;
                break;
            default:
                throw new IllegalArgumentException("The input char is not permitted");

        }
        return piece;
    }

    /**
     * Given an instance of an implementation of Piece, pieceToCharacter return a character
     * @param piece an instance of the class Giant, Mage, Archer, Dragon, Squire or Knight
     * @return a char: A,a (archer),G,g (giant),D,d (dragon),S,s (squire),K,k (knight),M,m (mage)
     */
    public static char pieceToCharacter(Piece piece){
        char character = '0';
        if (piece==null) character = '0';
        else if(piece.getMyTeam()==Piece.Team.WHITE) {
            if (piece.getClass() == Knight.class) character = 'K';
            else if (piece.getClass() == Archer.class) character = 'A';
            else if (piece.getClass() == Giant.class) character = 'G';
            else if (piece.getClass() == Mage.class) character = 'M';
            else if (piece.getClass() == Squire.class) character = 'S';
            else if (piece.getClass() == Dragon.class) character = 'D';
        }
        else if(piece.getMyTeam()==Piece.Team.BLACK) {
            if (piece.getClass() == Knight.class) character = 'k';
            else if (piece.getClass() == Archer.class) character = 'a';
            else if (piece.getClass() == Giant.class) character = 'g';
            else if (piece.getClass() == Mage.class) character = 'm';
            else if (piece.getClass() == Squire.class) character = 's';
            else if (piece.getClass() == Dragon.class) character = 'd';
        }
        return character;
    }

    /**
     *UpdateBoard update the board creating new object with a certain vitality in the right position
     * @param boardString is a string of dimension size*size. The chars can be: G (giant), M (mage), S (squire), K (knight), D (dragon), A (archer), 0 (empty)
     * The letters are UPPERCASE for white and lowercase for black
     * @param vitalityString a string of dimension maxPiece (16) that represent the vitality of the pieces
     * @param match the match that we are considering
     */
    public static void updateBoard(String boardString,String vitalityString,Match match){
        updateBoard(boardString,match);
        updateVitality(vitalityString, match);
    }

    /**
     * UpdateVitality update the vitality of the pieces on the board
     * @param vitalityString a string of dimension maxPiece (16) that represent the vitality of the pieces
     * @param match the match that we are considering
     */
    public static void updateVitality(String vitalityString, Match match){
        if (vitalityString.length() != 16){  //check if the length is correct
            throw new IllegalArgumentException("The length of the vitality string must be at least 16");
        }
        int stringPosition = 0;
        for (int c=0; c<match.getBoard().getSize(); c++){  //select the row
            for (int r=0; r<match.getBoard().getSize(); r++){  //select the columns
                if(match.getBoard().getTable()[r][c] != null) {
                    match.getBoard().getTable()[r][c].setCurrentVitality(Character.getNumericValue(vitalityString.charAt(stringPosition)));
                    stringPosition++;
                }
            }
        }
    }

    /**
     * UpdateBoard update the board creating new object with their max vitality in the right position
     * @param boardString is a string of dimension size*size. The chars can be: G (giant), M (mage), S (squire), K (knight), D (dragon), A (archer), 0 (empty)
     * The letters are UPPERCASE for white and lowercase for black
     * @param match the match that we are considering
     */
    public static void updateBoard(String boardString, Match match){
        if (boardString.length() != match.getBoard().getSize()*match.getBoard().getSize()){  //check if the length is correct
            throw new IllegalArgumentException("The length of the string must be size*size");
        }
        int stringPosition = 0;
        for (int r=0; r<match.getBoard().getSize(); r++){  //select the row
            for (int c=0; c<match.getBoard().getSize(); c++){  //select the columns
                match.getBoard().setPositionInTable(new Position(r+1,c+1),characterToPiece(boardString.charAt(stringPosition)));
                stringPosition++;
            }
        }
    }

    /**
     *VitalityToString return a string that describes the board
     * @return a string of the vitality of the pieces. The board is scanned by column from left to right. If there are less than 16 pieces, the string is zero-padded
     * @param match the match that we are considering
     */
    public static String vitalityToString(Match match) {
        StringBuilder outString= new StringBuilder("");
        for (int c=0; c<match.getBoard().getSize(); c++){  //select the row
            for (int r=0; r<match.getBoard().getSize(); r++){  //select the columns
                if(match.getBoard().getPieceAtPosition(new Position(r+1,c+1))!=null) {
                    outString.append(match.getBoard().getTable()[r][c].getCurrentVitality());
                }
            }
        }
        if(outString.length()<16){ //if a piece has died
            int lengthOutString = outString.length();
            for(int i=0;i<16-lengthOutString;i++){
                outString.append(0); //append a numbers of 0 equal to the number of dead pieces
            }
        }
        return outString.toString();
    }

    /**
     * BoardToString return a string of characters, scanning the table by row, from top to bottom, from left to right
     * @return a string of the pieces in the board
     */
    public static String boardToString(Match match){
        StringBuilder outString= new StringBuilder("");
        for (int r=0; r<match.getBoard().getSize(); r++){  //select the row
            for (int c=0; c<match.getBoard().getSize(); c++){  //select the columns
                outString.append(pieceToCharacter(match.getBoard().getTable()[r][c]));
            }
        }
        return outString.toString();
    }

    /**
     * SetFrozenFromString set the frozen Pieces in the board
     * @param frozenPiecesString: the string as explained in the slides
     * @param match: the match that we are considering
     */
    public static void setFrozenFromString(String frozenPiecesString, Match match){

        match.getFrozenPieces().getFrozenWhite().setRow((Integer.parseInt(String.valueOf(frozenPiecesString.charAt(1)))));
        match.getFrozenPieces().getFrozenWhite().setColumn((Integer.parseInt(String.valueOf(frozenPiecesString.charAt(0)))));
        match.getFrozenPieces().setTurnLeftWhite(Integer.parseInt(String.valueOf(frozenPiecesString.charAt(2))));
        if(match.getFrozenPieces().getTurnLeftWhite()!=0)
            match.getFrozenPieces().setCounterWhitePiecesFrozen(1);

        match.getFrozenPieces().getFrozenBlack().setRow(Integer.parseInt(String.valueOf(frozenPiecesString.charAt(4))));
        match.getFrozenPieces().getFrozenBlack().setColumn(Integer.parseInt(String.valueOf(frozenPiecesString.charAt(3))));
        match.getFrozenPieces().setTurnLeftBlack(Integer.parseInt(String.valueOf(frozenPiecesString.charAt(5))));
        if(match.getFrozenPieces().getTurnLeftBlack()!=0)
            match.getFrozenPieces().setCounterBlackPiecesFrozen(1);
    }

    /**GetFrozenPiecesAsString gives a representation of the frozen Pieces as a String
     * @param match the match that we are considering
     * @return the string of frozen Pieces, as explained in the slides
     */
    public static String getFrozenPiecesAsString(Match match) {

        String frozenPiecesString = Integer.toString(match.getFrozenPieces().getFrozenWhite().getColumn()) +
                Integer.toString(match.getFrozenPieces().getFrozenWhite().getRow()) +
                Integer.toString(match.getFrozenPieces().getTurnLeftWhite()) +
                Integer.toString(match.getFrozenPieces().getFrozenBlack().getColumn()) +
                Integer.toString(match.getFrozenPieces().getFrozenBlack().getRow()) +
                Integer.toString(match.getFrozenPieces().getTurnLeftBlack());


        return frozenPiecesString;
    }

    public static String getUnusedSpellAsString(Match match){
        Match.Spell[] spellWhite = match.getUnusedSpellsWhite();
        Match.Spell[] spellBlack = match.getUnusedSpellsBlack();
        StringBuilder outString = new StringBuilder("");
        for(int i =0;i<4;i++){
            switch (spellWhite[i]){
                case FREEZE: outString.append("F");
                    break;
                case HEAL: outString.append("H");
                    break;
                case REVIVE: outString.append("R");
                    break;
                case TELEPORT: outString.append("T");
                    break;
                case NOSPELL : outString.append("0");
                    break;
            }
        }
        for(int i =0;i<4;i++){
            switch (spellBlack[i]){
                case FREEZE: outString.append("F");
                    break;
                case HEAL: outString.append("H");
                    break;
                case REVIVE: outString.append("R");
                    break;
                case TELEPORT: outString.append("T");
                    break;
                case NOSPELL: outString.append("0");
                    break;
            }
        }
        return outString.toString();
    }

    public static void setUnusedSpellFromString(String spellString, Match match){
        StringBuilder string = new StringBuilder(spellString);
        Match.Spell[] unusedSpellsWhite= {FREEZE,HEAL,REVIVE,TELEPORT};
        Match.Spell[] unusedSpellsBlack = {FREEZE,HEAL,REVIVE,TELEPORT};
        for(int i =0;i<4;i++){
            if (string.charAt(i)=='0')
                unusedSpellsWhite[i] = Match.Spell.NOSPELL;
        }
        for(int i =4;i<8;i++){
            if (string.charAt(i)=='0')
                unusedSpellsBlack[i-4] = Match.Spell.NOSPELL;
        }
        match.setUnusedSpellsWhite(unusedSpellsWhite);
        match.setUnusedSpellsBlack(unusedSpellsBlack);

    }

    public static void setCurrentTeamFromString(String currentTeam, Match match){
        if (currentTeam=="WHITE") match.setCurrentTeam(Piece.Team.WHITE);
        else if (currentTeam=="BLACK") match.setCurrentTeam(Piece.Team.BLACK);
        else throw new IllegalArgumentException("The input string is not valid");
    }

    public static String getCemeteriesAsString(Match match){
        StringBuilder string = new StringBuilder("");
        for(Piece piece:match.getListOfDeadWhitePieces())
            string.append(pieceToCharacter(piece));
        for(Piece piece:match.getListOfDeadBlackPieces())
            string.append(pieceToCharacter(piece));

        return string.toString();
    }

    public static void setCemeteriesFromString(String cemeteriesString,Match match){
        List<Piece> listOfDeadWhitePieces = new ArrayList<>();
        List<Piece> listOfDeadBlackPieces = new ArrayList<>();
        StringBuilder string = new StringBuilder(cemeteriesString);
        String currentTeamToSet = "white";
        for(int i=0; i<string.length();i++){
            Piece piece =characterToPiece(string.charAt(i));
            if (piece.getMyTeam()== Piece.Team.WHITE)
                listOfDeadWhitePieces.add(characterToPiece(string.charAt(i)));
            if (piece.getMyTeam()== Piece.Team.BLACK)
                listOfDeadBlackPieces.add(characterToPiece(string.charAt(i)));
        }
        match.setListOfDeadWhitePieces(listOfDeadWhitePieces);
        match.setListOfDeadBlackPieces(listOfDeadBlackPieces);
    }

    public static boolean isSpecialPosition(Position posToCheck,Match match){
        int rowToCheck = posToCheck.getRow();
        int colToCheck = posToCheck.getColumn();

        boolean firstPosition = (rowToCheck == match.getSpecialPositions()[0].getRow() && colToCheck == match.getSpecialPositions()[0].getColumn());
        boolean secondPosition = (rowToCheck == match.getSpecialPositions()[1].getRow() && colToCheck == match.getSpecialPositions()[1].getColumn());
        boolean thirdPosition = (rowToCheck == match.getSpecialPositions()[2].getRow() && colToCheck == match.getSpecialPositions()[2].getColumn());
        boolean fourthPosition = (rowToCheck == match.getSpecialPositions()[3].getRow() && colToCheck == match.getSpecialPositions()[3].getColumn());

        if(firstPosition && secondPosition && thirdPosition && fourthPosition){
            return true;
        }
        else{
            return false;
        }
    }

    public static String getPieceName(Position pos, Match match){
        return match.getBoard().getPieceAtPosition(pos).getClass().getSimpleName();
    }

    public static String positionToString(Position pos){
        String result = null;
        int row = pos.getRow();
        int col = pos.getColumn();
        result = "(" + row + "," + col + ")";
        return result;
    }

    public static String teamToLowerCase(Piece.Team team){

        if(team == Piece.Team.WHITE)
            return "White";
        else if(team == Piece.Team.BLACK)
            return "Black";
        else if(team == Piece.Team.FREE)
            return "Free";
        else
            return "";
    }

}
