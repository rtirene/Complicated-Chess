package com.mobileapp.polimi.maprojectg4;



import com.mobileapp.polimi.maprojectg4.model.pieces.Knight;
import com.mobileapp.polimi.maprojectg4.testUtils.TestUtils;
import com.mobileapp.polimi.maprojectg4.model.pieces.Archer;
import com.mobileapp.polimi.maprojectg4.model.pieces.Dragon;
import com.mobileapp.polimi.maprojectg4.model.pieces.Giant;
import com.mobileapp.polimi.maprojectg4.model.pieces.Mage;
import com.mobileapp.polimi.maprojectg4.model.Match;
import com.mobileapp.polimi.maprojectg4.model.Piece;
import com.mobileapp.polimi.maprojectg4.model.Position;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Vector;

public class PieceTest {


    @Test
    public void possibleAttacksTestUI(){
        Match match = new Match();
        String boardString = "000000" + "G0K0sa" + "DS0k0m" + "MK00sd" + "AS00kg" + "000000";
        TestUtils.updateBoard(boardString,match);
        Piece.Team currentTeam = Piece.Team.BLACK;
        Vector enemiesForBlackKnight = new Vector();
        Vector enemies = new Vector();

        Knight blackKnight = new Knight(currentTeam);
        Position blackKnightPOs = new Position(3,4);
        Position whiteKnight = new Position(2,3);

        enemies.add(enemies.size(),whiteKnight);

        enemiesForBlackKnight = blackKnight.possibleAttacks(blackKnightPOs,match.getBoard(),currentTeam);

        Position getPos1 = (Position) enemiesForBlackKnight.elementAt(0);

        assertEquals(getPos1.getRow(),whiteKnight.getRow());
        assertEquals(getPos1.getColumn(),whiteKnight.getColumn());

        assertEquals(enemies.size(),enemiesForBlackKnight.size());


    }
    @Test
    public void possibleAttacksTest(){

        Match match = new Match();
        String boardString = "000000" + "00Ks0a" + "DSGk0m" + "MKs00d" + "AS00kg" + "000000"; //From Exemple of attack - Giant (slide)
        TestUtils.updateBoard(boardString,match);
        Piece.Team currentTeam = Piece.Team.WHITE;
        Vector enemiesForWhiteGiant = new Vector();
        Vector enemies = new Vector();


        // Test white Giant initial position of the board
        Giant whiteGiant = new Giant(currentTeam);
        Position whiteGiantPosition = new Position(3,3);
        Position blackKnight = new Position(3,4);
        Position blackSquire = new Position(4,3);

        enemies.add(enemies.size(),blackKnight);
        enemies.add(enemies.size(),blackSquire);

        enemiesForWhiteGiant = whiteGiant.possibleAttacks(whiteGiantPosition,match.getBoard(),currentTeam);

        Position getPos1 = (Position) enemiesForWhiteGiant.elementAt(0);
        Position getPos2 = (Position) enemiesForWhiteGiant.elementAt(1);

        assertEquals(getPos1.getRow(),blackKnight.getRow());
        assertEquals(getPos1.getColumn(),blackKnight.getColumn());
        assertEquals(getPos2.getRow(),blackSquire.getRow());
        assertEquals(getPos2.getColumn(),blackSquire.getColumn());

        // Test white Archer initial position of the board
        match = new Match();
        boardString = "000000" + "0KGs0a" + "0S0k0m" + "MKA0sd" + "0Sk000" + "0Dg000"; //From Exemple of attack - Archer (slide)
        TestUtils.updateBoard(boardString,match);
        Vector enemiesForWhiteArcher = new Vector();
        Vector enemies1 = new Vector();

        Archer whiteArcher = new Archer(currentTeam);
        Position whiteArcherPosition = new Position(4,3);
        Position blackKnight1 = new Position(5,3);
        Position blackSquire1 = new Position(4,5);

        enemies1.add(enemies1.size(),blackKnight1);
        enemies1.add(enemies1.size(),blackSquire1);

        enemiesForWhiteArcher = whiteArcher.possibleAttacks(whiteArcherPosition,match.getBoard(),currentTeam);

        Position getPos11 = (Position) enemiesForWhiteArcher.elementAt(0);
        Position getPos21 = (Position) enemiesForWhiteArcher.elementAt(1);

        assertEquals(getPos21.getRow(),blackKnight1.getRow());
        assertEquals(getPos21.getColumn(),blackKnight1.getColumn());
        assertEquals(getPos11.getRow(),blackSquire1.getRow());
        assertEquals(getPos11.getColumn(),blackSquire1.getColumn());

    }


    @Test(expected = IllegalArgumentException.class)
    public void exceptionPossibleAttacksTest(){
        // Test black Mage initial position of the board
        Match match = new Match();
        Piece.Team currentTeam = Piece.Team.BLACK;
        String boardString = "000000" + "0KGs0a" + "0S0k0m" + "MKA0sd" + "0Sk000" + "0Dg000";
        TestUtils.updateBoard(boardString,match);
        Mage blackMage = new Mage(currentTeam);
        Vector enemiesForBlackMage = new Vector();
        Position blackMagePosition = new Position(3,6);

        enemiesForBlackMage = blackMage.possibleAttacks(blackMagePosition,match.getBoard(),currentTeam);
    }

    /**
     * Test that the function possibleDirections return the allowed position in which a specific piece can move on,
     * given a particular configuration of the board. For the pieces that walk, e.g. Squire
     */
    @Test
    public void possibleDirectionsRecursiveTest(){

        Match match = new Match();
        String boardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        TestUtils.updateBoard(boardString,match);
        Piece.Team currentTeam = Piece.Team.WHITE;

        // Test White Giant in the initial position of the board
        Giant whiteGiant = new Giant(currentTeam);
        Vector<Position> getPositionsForWhiteGiant;
        Position myPosition = new Position(2,1);
        Position pos1 = new Position(1,1);
        Position pos2 = new Position(1,2);
        getPositionsForWhiteGiant = whiteGiant.possibleDirections(myPosition,match.getBoard(),currentTeam);
        Position getPos1 = getPositionsForWhiteGiant.elementAt(0);
        Position getPos2 = getPositionsForWhiteGiant.elementAt(1);
        assertEquals(pos1.getRow(),getPos1.getRow());
        assertEquals(pos1.getColumn(),getPos1.getColumn());
        assertEquals(pos2.getRow(),getPos2.getRow());
        assertEquals(pos2.getColumn(),getPos2.getColumn());

        // Test white Archer in position(3,3) with the following board configuration
        match = new Match();
        boardString = "0000s0" + "0g0000" + "00ASS0" + "0K0G00" + "000000" + "000000";
        TestUtils.updateBoard(boardString,match);
        Archer whiteArcher = new Archer(currentTeam);
        myPosition.setColumn(3);
        myPosition.setRow(3);
        Vector<Position> getPositionsForWhiteArcher;

        pos1 = new Position(2,4);
        pos2 = new Position(2,5);
        Position pos3 = new Position(1,5);
        Position pos4 = new Position(1,4);
        Position pos5 = new Position(1,3);
        Position pos6 = new Position(2,3);
        Position pos7 = new Position(1,2);
        Position pos8 = new Position(2,2);
        Position pos9 = new Position(3,2);
        Position pos10 = new Position(2,1);
        Position pos11 = new Position(3,1);
        Position pos12 = new Position(4,1);
        Position pos13 = new Position(4,3);
        Position pos14 = new Position(5,2);
        Position pos15 = new Position(5,3);
        Position pos16 = new Position(5,4);

        getPositionsForWhiteArcher = whiteArcher.possibleDirections(myPosition,match.getBoard(),currentTeam);

        getPos1 = getPositionsForWhiteArcher.elementAt(0);
        getPos2 = getPositionsForWhiteArcher.elementAt(1);
        Position getPos3 = getPositionsForWhiteArcher.elementAt(2);
        Position getPos4 = getPositionsForWhiteArcher.elementAt(3);
        Position getPos5 = getPositionsForWhiteArcher.elementAt(4);
        Position getPos6 = getPositionsForWhiteArcher.elementAt(5);
        Position getPos7 = getPositionsForWhiteArcher.elementAt(6);
        Position getPos8 = getPositionsForWhiteArcher.elementAt(7);
        Position getPos9 = getPositionsForWhiteArcher.elementAt(8);
        Position getPos10 = getPositionsForWhiteArcher.elementAt(9);
        Position getPos11 = getPositionsForWhiteArcher.elementAt(10);
        Position getPos12 = getPositionsForWhiteArcher.elementAt(11);
        Position getPos13 = getPositionsForWhiteArcher.elementAt(12);
        Position getPos14 = getPositionsForWhiteArcher.elementAt(13);
        Position getPos15 = getPositionsForWhiteArcher.elementAt(14);
        Position getPos16 = getPositionsForWhiteArcher.elementAt(15);

        assertEquals(pos1.getRow(),getPos1.getRow());
        assertEquals(pos1.getColumn(),getPos1.getColumn());
        assertEquals(pos2.getRow(),getPos2.getRow());
        assertEquals(pos2.getColumn(),getPos2.getColumn());
        assertEquals(pos3.getRow(),getPos3.getRow());
        assertEquals(pos3.getColumn(),getPos3.getColumn());
        assertEquals(pos4.getRow(),getPos4.getRow());
        assertEquals(pos4.getColumn(),getPos4.getColumn());
        assertEquals(pos5.getRow(),getPos5.getRow());
        assertEquals(pos5.getColumn(),getPos5.getColumn());
        assertEquals(pos6.getRow(),getPos6.getRow());
        assertEquals(pos6.getColumn(),getPos6.getColumn());
        assertEquals(pos7.getRow(),getPos7.getRow());
        assertEquals(pos7.getColumn(),getPos7.getColumn());
        assertEquals(getPos8.getRow(),pos8.getRow());
        assertEquals(getPos8.getColumn(),pos8.getColumn());
        assertEquals(getPos9.getRow(),pos9.getRow());
        assertEquals(getPos9.getColumn(),pos9.getColumn());
        assertEquals(getPos10.getRow(),pos10.getRow());
        assertEquals(getPos10.getColumn(),pos10.getColumn());
        assertEquals(getPos11.getRow(),pos11.getRow());
        assertEquals(getPos11.getColumn(),pos11.getColumn());
        assertEquals(getPos12.getRow(),pos12.getRow());
        assertEquals(getPos12.getColumn(),pos12.getColumn());
        assertEquals(getPos13.getRow(),pos13.getRow());
        assertEquals(getPos13.getColumn(),pos13.getColumn());
        assertEquals(getPos14.getRow(),pos14.getRow());
        assertEquals(getPos14.getColumn(),pos14.getColumn());
        assertEquals(getPos15.getRow(),pos15.getRow());
        assertEquals(getPos15.getColumn(),pos15.getColumn());
        assertEquals(getPos16.getRow(),pos16.getRow());
        assertEquals(getPos16.getColumn(),pos16.getColumn());

        // Test configuration board taken from slide 7
        match = new Match();
        Vector<Position> getPositionsForWhiteGiant2;
        Giant whitegiant2 = new Giant(currentTeam);
        myPosition = new Position(3,3);
        boardString = "000000" + "00K0sa" + "DSGk0m" + "MK00sd" + "AS00kg" + "000000";
        TestUtils.updateBoard(boardString,match);

        pos1 = new Position(3,4);
        pos2 = new Position(4,3);
        pos3 = new Position(4,4);
        pos4 = new Position(5,3);

        getPositionsForWhiteGiant2 = whitegiant2.possibleDirections(myPosition,match.getBoard(),currentTeam);

        getPos1 = getPositionsForWhiteGiant2.elementAt(0);
        getPos2 = getPositionsForWhiteGiant2.elementAt(1);
        getPos3 = getPositionsForWhiteGiant2.elementAt(2);
        getPos4 = getPositionsForWhiteGiant2.elementAt(3);

        assertEquals(pos1.getRow(),getPos1.getRow());
        assertEquals(pos1.getColumn(),getPos1.getColumn());
        assertEquals(pos2.getRow(),getPos2.getRow());
        assertEquals(pos2.getColumn(),getPos2.getColumn());
        assertEquals(pos3.getRow(),getPos3.getRow());
        assertEquals(pos3.getColumn(),getPos3.getColumn());
        assertEquals(pos4.getRow(),getPos4.getRow());
        assertEquals(pos4.getColumn(),getPos4.getColumn());

        // Test configuration board taken from slide 9
        match = new Match();
        Vector<Position> getPositionsForWhiteArcher2;
        Archer whiteArcher2 = new Archer(currentTeam);
        myPosition = new Position(5,3);
        boardString = "000000" + "GK00sa" + "DSk00m" + "M0Ks0d" + "0SAk00" + "0000g0";
        TestUtils.updateBoard(boardString,match);

        pos1 = new Position(5,4);
        pos2 = new Position(4,4);
        pos3 = new Position(4,2);
        pos4 = new Position(3,3);
        pos5 = new Position(5,1);
        pos6 = new Position(6,2);
        pos7 = new Position(6,3);
        pos8 = new Position(6,1);
        pos9 = new Position(6,4);
        pos10 = new Position(6,5);
        pos11 = new Position(5,5);

        getPositionsForWhiteArcher2 = whiteArcher2.possibleDirections(myPosition,match.getBoard(),currentTeam);

        getPos1 = getPositionsForWhiteArcher2.elementAt(0);
        getPos2 = getPositionsForWhiteArcher2.elementAt(1);
        getPos3 = getPositionsForWhiteArcher2.elementAt(2);
        getPos4 = getPositionsForWhiteArcher2.elementAt(3);
        getPos5 = getPositionsForWhiteArcher2.elementAt(4);
        getPos6 = getPositionsForWhiteArcher2.elementAt(5);
        getPos7 = getPositionsForWhiteArcher2.elementAt(6);
        getPos8 = getPositionsForWhiteArcher2.elementAt(7);
        getPos9 = getPositionsForWhiteArcher2.elementAt(8);
        getPos10 = getPositionsForWhiteArcher2.elementAt(9);
        getPos11 = getPositionsForWhiteArcher2.elementAt(10);

        assertEquals(pos1.getRow(),getPos1.getRow());
        assertEquals(pos1.getColumn(),getPos1.getColumn());
        assertEquals(pos2.getRow(),getPos2.getRow());
        assertEquals(pos2.getColumn(),getPos2.getColumn());
        assertEquals(pos3.getRow(),getPos3.getRow());
        assertEquals(pos3.getColumn(),getPos3.getColumn());
        assertEquals(pos4.getRow(),getPos4.getRow());
        assertEquals(pos4.getColumn(),getPos4.getColumn());
        assertEquals(pos5.getRow(),getPos5.getRow());
        assertEquals(pos5.getColumn(),getPos5.getColumn());
        assertEquals(pos6.getRow(),getPos6.getRow());
        assertEquals(pos6.getColumn(),getPos6.getColumn());
        assertEquals(pos7.getRow(),getPos7.getRow());
        assertEquals(pos7.getColumn(),getPos7.getColumn());
        assertEquals(pos8.getRow(),getPos8.getRow());
        assertEquals(pos8.getColumn(),getPos8.getColumn());
        assertEquals(pos9.getRow(),getPos9.getRow());
        assertEquals(pos9.getColumn(),getPos9.getColumn());
        assertEquals(pos10.getRow(),getPos10.getRow());
        assertEquals(pos10.getColumn(),getPos10.getColumn());
        assertEquals(pos11.getRow(),getPos11.getRow());
        assertEquals(pos11.getColumn(),getPos11.getColumn());


    }

    /**
     * Test that the function possibleDirections return the allowed position in which a specific piece can move on,
     * given a particular configuration of the board. For the pieces that flight, i.e. Dragon
     */
    @Test
    public void possibleDirectionsRecursiveFlyTest(){
        Match match = new Match();
        String boardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        TestUtils.updateBoard(boardString,match);

        // Test black Dragon with initial board setting
        boardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        TestUtils.updateBoard(boardString,match);
        Piece.Team currentTeam = Piece.Team.BLACK;

        Dragon blackDragon = new Dragon(currentTeam);
        Position myPosition = new Position(4,6);
        Vector<Position> allowedPositionForBlackDragon;

        Position pos1 = new Position(1,6);
        Position pos2 = new Position(3,4);
        Position pos3 = new Position(4,4);
        Position pos4 = new Position(4,3);
        Position pos5 = new Position(5,4);
        Position pos6 = new Position(6,5);
        Position pos7 = new Position(6,6);

        allowedPositionForBlackDragon = blackDragon.possibleDirections(myPosition,match.getBoard(),currentTeam);

        Position getPos1 = allowedPositionForBlackDragon.elementAt(0);
        Position getPos2 = allowedPositionForBlackDragon.elementAt(1);
        Position getPos3 = allowedPositionForBlackDragon.elementAt(2);
        Position getPos4 = allowedPositionForBlackDragon.elementAt(3);
        Position getPos5 = allowedPositionForBlackDragon.elementAt(4);
        Position getPos6 = allowedPositionForBlackDragon.elementAt(5);
        Position getPos7 = allowedPositionForBlackDragon.elementAt(6);

        assertEquals(pos1.getRow(),getPos1.getRow());
        assertEquals(pos1.getColumn(),getPos1.getColumn());
        assertEquals(pos2.getRow(),getPos2.getRow());
        assertEquals(pos2.getColumn(),getPos2.getColumn());
        assertEquals(pos3.getRow(),getPos3.getRow());
        assertEquals(pos3.getColumn(),getPos3.getColumn());
        assertEquals(pos4.getRow(),getPos4.getRow());
        assertEquals(pos4.getColumn(),getPos4.getColumn());
        assertEquals(pos5.getRow(),getPos5.getRow());
        assertEquals(pos5.getColumn(),getPos5.getColumn());
        assertEquals(pos6.getRow(),getPos6.getRow());
        assertEquals(pos6.getColumn(),getPos6.getColumn());
        assertEquals(pos7.getRow(),getPos7.getRow());
        assertEquals(pos7.getColumn(),getPos7.getColumn());



        // Test the following board configuration on the white Dragon
        match = new Match();
        boardString = "00s000" + "000m00" + "0sDgkM" + "00K000" + "0s0000" + "000000";
        TestUtils.updateBoard(boardString,match);
        Dragon whiteDragon = new Dragon(currentTeam);
        currentTeam = Piece.Team.WHITE;

        myPosition = new Position(3,3);
        Vector<Position> allowedPositionForWhiteDragon;

        pos1 = new Position(3,4);
        pos2 = new Position(3,5);
        pos3 = new Position(2,5);
        pos4 = new Position(4,5);
        pos5 = new Position(2,4);
        pos6 = new Position(1,4);
        pos7 = new Position(2,3);
        Position  pos8 = new Position(3,2);
        Position  pos9 = new Position(4,4);
        Position pos10 = new Position(5,4);
        Position pos11 = new Position(1,3);
        Position pos12 = new Position(1,2);
        Position pos13 = new Position(2,2);
        Position pos14 = new Position(2,1);
        Position pos15 = new Position(3,1);
        Position pos16 = new Position(4,1);
        Position pos17 = new Position(4,2);
        Position pos18 = new Position(5,2);
        Position pos19 = new Position(5,3);
        Position pos20 = new Position(6,3);

        allowedPositionForWhiteDragon = whiteDragon.possibleDirections(myPosition,match.getBoard(),currentTeam);

        getPos1 = allowedPositionForWhiteDragon.elementAt(0);
        getPos2 = allowedPositionForWhiteDragon.elementAt(1);
        getPos3 = allowedPositionForWhiteDragon.elementAt(2);
        getPos4 = allowedPositionForWhiteDragon.elementAt(3);
        getPos5 = allowedPositionForWhiteDragon.elementAt(4);
        getPos6 = allowedPositionForWhiteDragon.elementAt(5);
        getPos7 = allowedPositionForWhiteDragon.elementAt(6);
        Position getPos8 = allowedPositionForWhiteDragon.elementAt(7);
        Position getPos9 = allowedPositionForWhiteDragon.elementAt(8);
        Position getPos10 = allowedPositionForWhiteDragon.elementAt(9);
        Position getPos11 = allowedPositionForWhiteDragon.elementAt(10);
        Position getPos12 = allowedPositionForWhiteDragon.elementAt(11);
        Position getPos13 = allowedPositionForWhiteDragon.elementAt(12);
        Position getPos14 = allowedPositionForWhiteDragon.elementAt(13);
        Position getPos15 = allowedPositionForWhiteDragon.elementAt(14);
        Position getPos16 = allowedPositionForWhiteDragon.elementAt(15);
        Position getPos17 = allowedPositionForWhiteDragon.elementAt(16);
        Position getPos18 = allowedPositionForWhiteDragon.elementAt(17);
        Position getPos19 = allowedPositionForWhiteDragon.elementAt(18);
        Position getPos20 = allowedPositionForWhiteDragon.elementAt(19);


        assertEquals(pos1.getRow(),getPos1.getRow());
        assertEquals(pos1.getColumn(),getPos1.getColumn());
        assertEquals(pos2.getRow(),getPos2.getRow());
        assertEquals(pos2.getColumn(),getPos2.getColumn());
        assertEquals(pos3.getRow(),getPos3.getRow());
        assertEquals(pos3.getColumn(),getPos3.getColumn());
        assertEquals(pos4.getRow(),getPos4.getRow());
        assertEquals(pos4.getColumn(),getPos4.getColumn());
        assertEquals(pos5.getRow(),getPos5.getRow());
        assertEquals(pos5.getColumn(),getPos5.getColumn());
        assertEquals(pos6.getRow(),getPos6.getRow());
        assertEquals(pos6.getColumn(),getPos6.getColumn());
        assertEquals(pos7.getRow(),getPos7.getRow());
        assertEquals(pos7.getColumn(),getPos7.getColumn());
        assertEquals(pos8.getRow(),getPos8.getRow());
        assertEquals(pos8.getColumn(),getPos8.getColumn());
        assertEquals(pos9.getRow(),getPos9.getRow());
        assertEquals(pos9.getColumn(),getPos9.getColumn());
        assertEquals(pos10.getRow(),getPos10.getRow());
        assertEquals(pos10.getColumn(),getPos10.getColumn());
        assertEquals(pos11.getRow(),getPos11.getRow());
        assertEquals(pos11.getColumn(),getPos11.getColumn());
        assertEquals(pos12.getRow(),getPos12.getRow());
        assertEquals(pos12.getColumn(),getPos12.getColumn());
        assertEquals(pos13.getRow(),getPos13.getRow());
        assertEquals(pos13.getColumn(),getPos13.getColumn());
        assertEquals(pos14.getRow(),getPos14.getRow());
        assertEquals(pos14.getColumn(),getPos14.getColumn());
        assertEquals(pos15.getRow(),getPos15.getRow());
        assertEquals(pos15.getColumn(),getPos15.getColumn());
        assertEquals(pos16.getRow(),getPos16.getRow());
        assertEquals(pos16.getColumn(),getPos16.getColumn());
        assertEquals(pos17.getRow(),getPos17.getRow());
        assertEquals(pos17.getColumn(),getPos17.getColumn());
        assertEquals(pos18.getRow(),getPos18.getRow());
        assertEquals(pos18.getColumn(),getPos18.getColumn());
        assertEquals(pos19.getRow(),getPos19.getRow());
        assertEquals(pos19.getColumn(),getPos19.getColumn());
        assertEquals(pos20.getRow(),getPos20.getRow());
        assertEquals(pos20.getColumn(),getPos20 .getColumn());

        // Test configuration taken from slide 8
        match = new Match();
        boardString = "000000" + "GK00sa" + "DSk00m" + "MK00sd" + "AS00kg" + "000000";
        TestUtils.updateBoard(boardString,match);
        Dragon whiteDragon2 = new Dragon(currentTeam);
        currentTeam = Piece.Team.WHITE;


        myPosition = new Position(3,1);
        Vector<Position> allowedPositionForWhiteDragon2;

        pos1 = new Position(3,3);
        pos2 = new Position(3,4);
        pos3 = new Position(2,3);
        pos4 = new Position(4,3);
        pos5 = new Position(1,2);
        pos6 = new Position(1,1);
        pos7 = new Position(6,1);

        allowedPositionForWhiteDragon2 = whiteDragon2.possibleDirections(myPosition,match.getBoard(),currentTeam);

        getPos1 = allowedPositionForWhiteDragon2.elementAt(0);
        getPos2 = allowedPositionForWhiteDragon2.elementAt(1);
        getPos3 = allowedPositionForWhiteDragon2.elementAt(2);
        getPos4 = allowedPositionForWhiteDragon2.elementAt(3);
        getPos5 = allowedPositionForWhiteDragon2.elementAt(4);
        getPos6 = allowedPositionForWhiteDragon2.elementAt(5);
        getPos7 = allowedPositionForWhiteDragon2.elementAt(6);

        assertEquals(pos1.getRow(),getPos1.getRow());
        assertEquals(pos1.getColumn(),getPos1.getColumn());
        assertEquals(pos2.getRow(),getPos2.getRow());
        assertEquals(pos2.getColumn(),getPos2.getColumn());
        assertEquals(pos3.getRow(),getPos3.getRow());
        assertEquals(pos3.getColumn(),getPos3.getColumn());
        assertEquals(pos4.getRow(),getPos4.getRow());
        assertEquals(pos4.getColumn(),getPos4.getColumn());
        assertEquals(pos5.getRow(),getPos5.getRow());
        assertEquals(pos5.getColumn(),getPos5.getColumn());
        assertEquals(pos6.getRow(),getPos6.getRow());
        assertEquals(pos6.getColumn(),getPos6.getColumn());
        assertEquals(pos7.getRow(),getPos7.getRow());
        assertEquals(pos7.getColumn(),getPos7.getColumn());
    }

}
