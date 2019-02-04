package com.mobileapp.polimi.maprojectg4;

import com.mobileapp.polimi.maprojectg4.model.pieces.Dragon;
import com.mobileapp.polimi.maprojectg4.model.Match;
import com.mobileapp.polimi.maprojectg4.model.Position;
import com.mobileapp.polimi.maprojectg4.testUtils.TestUtils;
import com.mobileapp.polimi.maprojectg4.model.pieces.Squire;

import junit.framework.Assert;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;



public class TestUtilsTest {

    //check the correct behavior of updateBoard
    @Test
    public void updateBoardTest(){
        Match match= new Match();
        String boardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        TestUtils.updateBoard(boardString,match);
        Position pos1 = new Position(1,1);
        Position pos2 = new Position(6,6);
        Position pos3 = new Position(3,2);
        Position pos4 = new Position(4,6);
        assertNull(match.getBoard().getTable()[0][0]);
        assertNull(match.getBoard().getTable()[5][5]);
        assertTrue(match.getBoard().getTable()[2][1] instanceof Squire);
        assertTrue(match.getBoard().getTable()[3][5] instanceof Dragon);
    }

    //check if a too long string throw an exception
    @Test(expected = IllegalArgumentException.class)
    public void updateBoardExceptionTest(){
        Match match = new Match();
        String boardString = "000000";
        TestUtils.updateBoard(boardString,match);
    }

    //test the correct behaviour of updateBoard() with 2 parameters
    @Test
    public void updateBoardTest1() {
        Match match = new Match();
        String boardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String vitalityString= "5675434334345765";
        Position pos1 = new Position(1,1);
        Position pos2 = new Position(6,6);
        Position pos3 = new Position(3,2);
        Position pos4 = new Position(4,6);
        TestUtils.updateBoard(boardString,vitalityString,match);
        assertNull(match.getBoard().getPieceAtPosition(pos1));
        assertNull(match.getBoard().getPieceAtPosition(pos2));
        assertTrue(match.getBoard().getPieceAtPosition(pos3) instanceof Squire);
        assertEquals(match.getBoard().getVitalityAt(pos3),3);
        assertTrue(match.getBoard().getPieceAtPosition(pos4) instanceof Dragon);
        assertEquals(match.getBoard().getVitalityAt(pos4),6);
    }

    @Test
    public void boardToStringTest() {
        Match match = new Match();
        String boardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        TestUtils.updateBoard(boardString,match);
        assertEquals(TestUtils.boardToString(match), boardString);
    }

    @Test
    public void vitalityToStringTest(){
        Match match = new Match();
        String boardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String vitalityString= "5675434334345765";
        TestUtils.updateBoard(boardString,vitalityString,match);
        assertEquals(TestUtils.vitalityToString(match),vitalityString);

        String newVitalityString="2175434334345765";
        TestUtils.updateVitality(newVitalityString,match);
        assertEquals(TestUtils.vitalityToString(match),newVitalityString);
    }

    @Test
    public void getUnusedSpellFromStringTest(){
        Match match = new Match();
        String boardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String vitalityString= "5675434334345765";
        TestUtils.updateBoard(boardString,vitalityString,match);

        String spellString="F000000T";
        TestUtils.setUnusedSpellFromString(spellString,match);
        Assert.assertEquals(spellString,TestUtils.getUnusedSpellAsString(match));

    }

    @Test
    public void setCemeteriesFromStringTest(){
        Match match = new Match();
        String boardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String vitalityString= "5675434334345765";
        TestUtils.updateBoard(boardString,vitalityString,match);
        String deadPieces = "Gak";
        TestUtils.setCemeteriesFromString(deadPieces, match);
        assertEquals(deadPieces, TestUtils.getCemeteriesAsString(match));
    }

}
