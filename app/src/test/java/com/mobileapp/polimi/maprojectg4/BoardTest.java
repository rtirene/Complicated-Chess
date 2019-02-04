package com.mobileapp.polimi.maprojectg4;


import com.mobileapp.polimi.maprojectg4.testUtils.TestUtils;
import com.mobileapp.polimi.maprojectg4.model.Board;
import com.mobileapp.polimi.maprojectg4.model.pieces.Giant;
import com.mobileapp.polimi.maprojectg4.model.pieces.Knight;
import com.mobileapp.polimi.maprojectg4.model.Match;
import com.mobileapp.polimi.maprojectg4.model.Piece;
import com.mobileapp.polimi.maprojectg4.model.Position;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoardTest {

    @Test
    public void constructorTest() {
        Board board = new Board();
        assertEquals(board.getSize(),6);
    }

    @Test
    public void getPieceAtPositionTest() {
        Match match = new Match();
        String myBoardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String myVitalityString= "5675434334345765";
        TestUtils.updateBoard(myBoardString,myVitalityString, match);

        Position pos1 = new Position(2, 1);
        Position pos2 = new Position(3, 5);
        Position pos3 = new Position(1, 1);

        assertTrue(match.getBoard().getPieceAtPosition(pos1) instanceof Giant);
        assertTrue(match.getBoard().getPieceAtPosition(pos2) instanceof Knight);
        assertTrue(match.getBoard().getPieceAtPosition(pos3) ==  null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void getPieceAtPositionExceptionTest() {
        Match match = new Match();
        String myBoardString = "000000" + "YK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String myVitalityString= "5675434334345765";
        TestUtils.updateBoard(myBoardString,myVitalityString,match);

        Position pos1 = new Position(2, 1);
        Piece piece = match.getBoard().getPieceAtPosition(pos1);
    }



    //check if whichTeam give the correct team
    @Test
    public void whichTeamTest() {
        Match match = new Match();
        String boardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        TestUtils.updateBoard(boardString,match);
        Position pos1 = new Position(3,1);
        Position pos2 = new Position(4,3);
        Position pos3 = new Position(5,5);
        assertEquals(match.getBoard().whichTeam(pos1), Piece.Team.WHITE);
        assertEquals(match.getBoard().whichTeam(pos2), Piece.Team.FREE);
        assertEquals(match.getBoard().whichTeam(pos3), Piece.Team.BLACK);
    }

    @Test
    public void movePieceInTableTest(){
        Match match = new Match();
        String boardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String vitalityString= "5675434334345765";
        TestUtils.updateBoard(boardString,match);
        Position pInit = new Position(4,2);
        Position pFinal = new Position(5,4);
        match.getBoard().movePieceInTable(pInit,pFinal);
        assertEquals(TestUtils.pieceToCharacter(match.getBoard().getPieceAtPosition(pFinal)),'K');
        assertEquals(TestUtils.pieceToCharacter(match.getBoard().getPieceAtPosition(pInit)),'0');
    }

    @Test(expected = IllegalArgumentException.class)
    public void movePieceInTableExceptionTest(){
        Match match = new Match();
        String boardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String vitalityString= "5675434334345765";
        TestUtils.updateBoard(boardString,match);
        Position pInit = new Position(4,2);
        Position pFinal = new Position(2,1);
        match.getBoard().movePieceInTable(pInit,pFinal);
    }

    @Test
    public void getVitalityAt() {
        Match match = new Match();
        String boardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String vitalityString= "5675434334345765";
        TestUtils.updateBoard(boardString,match);
        Position position = new Position(3,1);

        assertEquals(match.getBoard().getVitalityAt(position), 6);
    }
}
