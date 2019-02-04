package com.mobileapp.polimi.maprojectg4;


import com.mobileapp.polimi.maprojectg4.model.Board;
import com.mobileapp.polimi.maprojectg4.testUtils.TestUtils;
import com.mobileapp.polimi.maprojectg4.model.pieces.Dragon;
import com.mobileapp.polimi.maprojectg4.model.FrozenPieces;
import com.mobileapp.polimi.maprojectg4.model.pieces.Giant;
import com.mobileapp.polimi.maprojectg4.model.pieces.Mage;
import com.mobileapp.polimi.maprojectg4.model.Match;
import com.mobileapp.polimi.maprojectg4.model.Piece;
import com.mobileapp.polimi.maprojectg4.model.Position;
import com.mobileapp.polimi.maprojectg4.model.pieces.Squire;

import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MatchTest {

    @Test
    public void updateUnusedSpellsTest() {
        Match match = new Match();

        //test the white freeze
        match.updateUnusedSpells(Match.Spell.FREEZE);
        String expectedSpells = "0HRTFHRT";
        String returnedUnesdSpellsAsString = new String(TestUtils.getUnusedSpellAsString(match));
        assertEquals(expectedSpells, returnedUnesdSpellsAsString);

        //test the white heal
        match.updateUnusedSpells(Match.Spell.HEAL);
        String expectedSpells1 = "00RTFHRT";
        String returnedUnesdSpellsAsString1 = new String(TestUtils.getUnusedSpellAsString(match));
        assertEquals(expectedSpells1, returnedUnesdSpellsAsString1);

        //test the white revive
        match.updateUnusedSpells(Match.Spell.REVIVE);
        String expectedSpells2 = "000TFHRT";
        String returnedUnesdSpellsAsString2 = new String(TestUtils.getUnusedSpellAsString(match));
        assertEquals(expectedSpells2, returnedUnesdSpellsAsString2);


        //test the white transport
        match.updateUnusedSpells(Match.Spell.TELEPORT);
        String expectedSpells3 = "0000FHRT";
        String returnedUnesdSpellsAsString3 = new String(TestUtils.getUnusedSpellAsString(match));
        assertEquals(expectedSpells3, returnedUnesdSpellsAsString3);


        //test the black freeze
        match.updateCurrentTeam();
        match.updateUnusedSpells(Match.Spell.FREEZE);
        String expectedSpells4 = "00000HRT";
        String returnedUnesdSpellsAsString4 = new String(TestUtils.getUnusedSpellAsString(match));
        assertEquals(expectedSpells4, returnedUnesdSpellsAsString4);


        //test the white heal
        match.updateUnusedSpells(Match.Spell.HEAL);
        String expectedSpells5 = "000000RT";
        String returnedUnesdSpellsAsString5 = new String(TestUtils.getUnusedSpellAsString(match));
        assertEquals(expectedSpells5, returnedUnesdSpellsAsString5);

        //test the white revive
        match.updateUnusedSpells(Match.Spell.REVIVE);
        String expectedSpell6 = "0000000T";
        String returnedUnesdSpellsAsString6 = new String(TestUtils.getUnusedSpellAsString(match));
        assertEquals(expectedSpell6, returnedUnesdSpellsAsString6);
        //test the white teleport
        match.updateUnusedSpells(Match.Spell.TELEPORT);
        String expectedSpell7 = "00000000";
        String returnedUnesdSpellsAsString7 = new String(TestUtils.getUnusedSpellAsString(match));
        assertEquals(expectedSpell7, returnedUnesdSpellsAsString7);
    }

    @Test
    public void movePieceTest() {
        Match match = new Match();
        String myBoardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String myVitalityString = "5675434334345765";
        TestUtils.updateBoard(myBoardString, myVitalityString, match);


        //Test of moving Giant from (2,1) to (1,1) on the initial board setting
        String expectedMyBoardString = "G00000" + "0K00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String expectedMyVitalityString = "5675434334345765";
        Position posIn = new Position(2, 1);
        Position posFin = new Position(1, 1);
        match.movePiece(posIn, posFin);

        assertEquals(TestUtils.boardToString(match), expectedMyBoardString);
        assertEquals(TestUtils.vitalityToString(match), expectedMyVitalityString);


        //Test of moving White Giant from (3,3) to (3,4) occupied by Black Knight on board setting form slide 11, engage a combat
        //and both of the pieces start with full vitality
        String boardString = "000000" + "00Ks0a" + "DSGk0m" + "MKs00d" + "AS00kg" + "000000";
        String vitalityString = "6753434533445765";
        TestUtils.updateBoard(boardString, vitalityString, match);
        String expectedBoardString = "000000" + "00Ks0a" + "DS0G0m" + "MKs00d" + "AS00kg" + "000000";
        String expectedVitalityString = "6753434333457650";
        Position posIn1 = new Position(3, 3);
        Position posFin1 = new Position(3, 4);

        match.movePiece(posIn1, posFin1);

        assertEquals(TestUtils.boardToString(match), expectedBoardString);
        //assertEquals(match.getBoard().boardToString(),expectedVitalityString); //da rimettere quando aggiorno engageCombat()

        //try to move the white giant on a cell occupied by a black knight frozen
        String myBoardString2 = "000000" + "00K0sa" + "DSGk0m" + "MK00sd" + "AS00kg" + "000000";
        String vitalityString2 = "6753434543345765";
        TestUtils.updateBoard(myBoardString2, vitalityString2, match);
        FrozenPieces frozenPieces = new FrozenPieces();
        Position posFrozen = new Position(3, 4);
        Position posFinal = new Position(0, 0);
        match.castSpell(Match.Spell.FREEZE, posFrozen, posFinal);
        Position pInit = new Position(3, 3);
        Position pFinal = new Position(3, 4);
        match.movePiece(pInit, pFinal);
        String expectedBoardString2 = "000000" + "00K0sa" + "DS0G0m" + "MK00sd" + "AS00kg" + "000000";
        String expectedVitalityString2 = "6753434533457650";
        assertEquals(TestUtils.boardToString(match), expectedBoardString2);
        assertEquals(TestUtils.vitalityToString(match), expectedVitalityString2);

    }

    @Test(expected = IllegalArgumentException.class)
    public void movePieceException1Test() {
        //Test of moving Giant from (2,1) to (2,2) on the initial board setting, we expect an exception, since the position is occupied by a piece of the same team
        Match match = new Match();
        String myBoardString1 = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String myVitalityString1 = "5675434334345765";
        TestUtils.updateBoard(myBoardString1, myVitalityString1, match);
        Position posIn = new Position(2, 1);
        Position posFin = new Position(2, 2);
        match.movePiece(posIn, posFin);

    }

    @Test(expected = IllegalArgumentException.class)
    public void movePieceException2Test() {
        //Test of moving Giant from (2,1) to (8,9) on the initial board setting, we expect an exception, since the position is out of board
        Match match = new Match();
        String myBoardString1 = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String myVitalityString1 = "5675434334345765";
        TestUtils.updateBoard(myBoardString1, myVitalityString1, match);
        Position posIn = new Position(2, 1);
        Position posFin = new Position(8, 9);
        match.movePiece(posIn, posFin);

    }

    @Test(expected = IllegalArgumentException.class)
    public void movePieceException3Test() {
        //Test of moving Giant from (2,1) to (-1,0) on the initial board setting, we expect an exception, since the position is out of board
        Match match = new Match();
        String myBoardString1 = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String myVitalityString1 = "5675434334345765";
        TestUtils.updateBoard(myBoardString1, myVitalityString1, match);
        Position posIn = new Position(2, 1);
        Position posFin = new Position(-1, 0);
        match.movePiece(posIn, posFin);

    }

    @Test(expected = IllegalArgumentException.class)
    public void movePieceException4Test() {
        //test of moving a frozen piece, we expect an exception
        Match match = new Match();
        FrozenPieces frozenPieces = new FrozenPieces();
        Position pos = new Position(2, 1);
        Position posFin = new Position(0, 0);
        match.castSpell(Match.Spell.FREEZE, pos, posFin);
        Position pInit = new Position(2, 1);
        Position pFinal = new Position(1, 1);
        match.movePiece(pInit, pFinal);

    }

    @Test
    public void attackTest() {
        Match match = new Match();
        String myBoardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "0S0Akg" + "000000";
        String myVitalityString = "5674343534345765";
        TestUtils.updateBoard(myBoardString, myVitalityString, match);
        String expectedVitalityString = "5674343534325765";
        //white Archer attacks the black Knight
        Position pAttack = new Position(5, 4);
        Position pDefend = new Position(5, 5);
        match.attack(pAttack, pDefend);
        assertEquals(TestUtils.vitalityToString(match), expectedVitalityString);

    }

    //test of slide 12
    @Test
    public void attack2Test() {
        Match match = new Match();
        String myBoardString = "000000" + "0KGs0a" + "0S0k0m" + "MKA0sd" + "0Sk000" + "0Dg000";
        String myVitalityString = "7434365545343575";
        TestUtils.updateBoard(myBoardString, myVitalityString, match);
        String expectedVitalityString = "7434365545341575";
        Position pAttack = new Position(4, 3);
        Position pDefend = new Position(4, 5);
        match.attack(pAttack, pDefend);
        assertEquals(TestUtils.vitalityToString(match), expectedVitalityString);

    }

    //test of slide 13
    @Test
    public void attack3Test() {
        Match match = new Match();
        String myBoardString = "000000" + "0KGsSa" + "00Kk0m" + "M0A0sd" + "0S000g" + "0Dg000";
        String myVitalityString = "7436545534335765";
        TestUtils.updateBoard(myBoardString, myVitalityString, match);
        String expectedVitalityString = "7436543534335765";
        Position pAttack = new Position(3, 4);
        Position pDefend = new Position(4, 3);
        match.updateCurrentTeam();
        match.attack(pAttack, pDefend);
        assertEquals(TestUtils.vitalityToString(match), expectedVitalityString);

    }


    @Test(expected = IllegalArgumentException.class)
    public void attackException1Test() {
        Match match = new Match();
        String myBoardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String myVitalityString = "5675434334345765";
        TestUtils.updateBoard(myBoardString, myVitalityString, match);
        //white Giant attacks white Knight, we expect an exception since the defender is a piece of the same team
        Position pAttack = new Position(2, 1);
        Position pDefend = new Position(2, 2);
        match.attack(pAttack, pDefend);


    }

    @Test(expected = IllegalArgumentException.class)
    public void attackException2Test() {
        Match match = new Match();
        String myBoardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String myVitalityString = "5675434334345765";
        TestUtils.updateBoard(myBoardString, myVitalityString, match);
        //white Giant attacks the position (1,1), we expect an exception since the attacked position is free
        Position pAttack = new Position(2, 1);
        Position pDefend = new Position(1, 1);
        match.attack(pAttack, pDefend);


    }

    @Test(expected = IllegalArgumentException.class)
    public void attackException3Test() {
        Match match = new Match();
        String myBoardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String myVitalityString = "5675434334345765";
        TestUtils.updateBoard(myBoardString, myVitalityString, match);
        //white Giant attacks the position (1,1), we expect an exception since the attacked position is free and out of range
        Position pAttack = new Position(2, 1);
        Position pDefend = new Position(3, 3);
        match.attack(pAttack, pDefend);
    }

    //check that a frozen piece cannot attack
    @Test(expected = IllegalArgumentException.class)
    public void attackException4Test() {
        Match match = new Match();
        String myBoardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String myVitalityString = "5675434334345765";
        match.updateCurrentTeam();
        Position pos = new Position(2, 1);
        Position posFin = new Position(0, 0);
        match.castSpell(Match.Spell.FREEZE, pos, posFin);
        match.updateCurrentTeam();
        TestUtils.updateBoard(myBoardString, myVitalityString, match);
        //white Giant attacks the position (1,1), we expect an exception since the attacked position is free and out of range
        Position pAttack = new Position(2, 1);
        Position pDefend = new Position(5, 5);
        match.attack(pAttack, pDefend);
    }

    //Test the engageCombat with 2 inputs
    @Test
    public void engageCombat1Test() {
        Match match = new Match();
        String myBoardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String vitalityString = "5675434334345765";
        TestUtils.updateBoard(myBoardString, vitalityString, match);
        Position attacker = new Position(2, 1);
        Position defender = new Position(3, 6);
        Position[] pos = match.engageCombat(attacker, defender);
        assertEquals(TestUtils.vitalityToString(match), "1675434334345065");

        String myBoardString1 = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String vitalityString1 = "5675434334345765";
        TestUtils.updateBoard(myBoardString1, vitalityString1, match);
        Position attacker1 = new Position(2, 2);
        Position defender1 = new Position(3, 5);
        Position[] pos1 = match.engageCombat(attacker1, defender1);
        assertEquals(TestUtils.vitalityToString(match), "5675034330345765");
    }

    //Test the engageCombat with 4 inputs
    @Test
    public void engageCombat2Test() {
        Match match = new Match();
        String myBoardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String vitalityString = "5675434334345765";
        TestUtils.updateBoard(myBoardString, vitalityString, match);

        int[] vitalityOfFighters = match.engageCombat(new Giant(Piece.Team.WHITE), new Mage(Piece.Team.BLACK), 5, 7);
        assertEquals(vitalityOfFighters[0], 1);
        assertEquals(vitalityOfFighters[1], 0);

        int[] vitalityOfFighters2 = match.engageCombat(new Squire(Piece.Team.WHITE), new Squire(Piece.Team.BLACK), 2, 2);
        assertEquals(vitalityOfFighters2[0], 0);
        assertEquals(vitalityOfFighters2[1], 0);
    }

    @Test
    public void engageCombat3Test() {
        //case of combat in which one of the two piece is frozen
        FrozenPieces frozenPieces = new FrozenPieces();
        Match match = new Match();
        String myBoardString = "000000" + "00K0sa" + "DSGk0m" + "MK00sd" + "AS00kg" + "000000";
        String vitalityString = "6753434543345765";
        TestUtils.updateBoard(myBoardString, vitalityString, match);
        Position pos = new Position(3, 4);
        Position posFin = new Position(0, 0);
        match.castSpell(Match.Spell.FREEZE, pos, posFin);
        Position pAttack = new Position(3, 3);
        Position pDefend = new Position(3, 4);
        Position[] posOfDeadPieces = match.engageCombat(pAttack, pDefend);
        assertEquals(TestUtils.vitalityToString(match), "6753434503345765");

    }

    @Test
    public void healSpellTest() {
        Match match = new Match();
        String myBoardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String vitalityString = "1675434334345765";
        TestUtils.updateBoard(myBoardString, vitalityString, match);
        Position pos = new Position(2, 1);
        Position posFin = new Position(0, 0);
        match.castSpell(Match.Spell.HEAL, pos, posFin);
        assertEquals(match.getBoard().getVitalityAt(new Position(2, 1)), 5);
    }

    @Test
    public void teleportSpellTest() {
        Match match = new Match();
        String myBoardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String vitalityString = "5675434334345765";
        TestUtils.updateBoard(myBoardString, vitalityString, match);
        Position pos = new Position(2, 1);
        Position posFin = new Position(1, 3);
        match.castSpell(Match.Spell.TELEPORT, pos, posFin);
        String expectedBoardString = "00G000" + "0K00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String expectedVitality = "6754343534345765";
        assertEquals(expectedBoardString, TestUtils.boardToString(match));
        assertEquals(TestUtils.vitalityToString(match), expectedVitality);

        Match match1 = new Match();
        String myBoardString1 = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String vitalityString1 = "5675434334345765";
        TestUtils.updateBoard(myBoardString1, vitalityString1, match1);
        Position pos1 = new Position(2, 1);
        Position posFin1 = new Position(2, 6);
        match1.castSpell(Match.Spell.TELEPORT, pos1, posFin1);
        String expectedBoardString1 = "000000" + "0K00sG" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String expectedVitality1 = "6754343343417650";
        assertEquals(expectedBoardString1, TestUtils.boardToString(match1));
        assertEquals(expectedVitality1, TestUtils.vitalityToString(match1));
    }

    @Test
    public void reviveSpellTest() {
        Match match = new Match();
        String myBoardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String vitalityString = "5675434334345765";
        TestUtils.updateBoard(myBoardString, vitalityString, match);
        Position pos = new Position(4, 6);
        Position posFin = new Position(0, 0);
        Piece pieceToRevive = match.getBoard().getPieceAtPosition(pos);
        match.killPiece(pos);
        assertTrue(match.getListOfDeadBlackPieces().contains(pieceToRevive));
        match.updateCurrentTeam();
        match.castSpell(Match.Spell.REVIVE, pos, posFin);
        assertEquals(TestUtils.boardToString(match), myBoardString);
        assertEquals(TestUtils.vitalityToString(match), vitalityString);
        assertFalse(match.getListOfDeadBlackPieces().contains(pieceToRevive));

        Match match1 = new Match();

        String myBoardString1 = "000000" + "GK00sa" + "kS00km" + "MK00sd" + "AS000g" + "000000";
        String vitalityString1 = "5475434334357650";
        TestUtils.updateBoard(myBoardString1, vitalityString1, match1);
        Position pos1 = new Position(3, 1);
        Position posFin1 = new Position(0, 0);
        match1.castSpell(Match.Spell.REVIVE, pos1, posFin1);
        assertEquals(match1.getBoard().getVitalityAt(pos1), 2);
        assertTrue(match1.getBoard().getPieceAtPosition(pos1) instanceof Dragon);
        Piece pieceToRevive1 = match1.getBoard().getPieceAtPosition(pos1);
        assertFalse(match1.getListOfDeadWhitePieces().contains(pieceToRevive1));
    }

    //A piece revive on a frozen Piece
    @Test
    public void reviveSpell2Test() {
        Match match = new Match();
        Position pos = new Position(4, 6);
        Position posFin = new Position(0, 0);
        match.killPiece(pos);
        Squire squire = new Squire(Piece.Team.WHITE);
        squire.setCurrentVitality(27);
        match.getBoard().setPositionInTable(pos, squire);
        match.killPiece(new Position(5, 2));
        match.updateCurrentTeam();
        match.castSpell(Match.Spell.FREEZE, pos, posFin);
        match.castSpell(Match.Spell.REVIVE, pos, posFin);
        String expectedBoardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "A000kg" + "000000";
        String expectedVitalityString = "5675434343457650";
        assertEquals(expectedBoardString, TestUtils.boardToString(match));
        assertEquals(expectedVitalityString, TestUtils.vitalityToString(match));

    }

    @Test(expected = IllegalStateException.class)
    public void reviveSpellExceptionTest() {
        Match match = new Match();
        String myBoardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String vitalityString = "5675434334345765";
        TestUtils.updateBoard(myBoardString, vitalityString, match);
        Position pos = new Position(2, 1);
        Position finalPos = new Position(1, 1);
        Position posFin = new Position(0, 0);
        match.movePiece(pos, finalPos);
        match.castSpell(Match.Spell.REVIVE, pos, posFin);
    }

    @Test
    public void FreezeTest() {
        Match match = new Match();
        Position targetPosition = new Position(5, 5);
        Position finalPosition = new Position(0, 0);
        match.castSpell(Match.Spell.FREEZE, targetPosition, finalPosition);
        assertEquals(match.getFrozenPieces().getFrozenBlack().getRow(), 5);
        assertEquals(match.getFrozenPieces().getFrozenBlack().getColumn(), 5);
        assertEquals(match.getFrozenPieces().getTurnLeftBlack(), 3);
        assertEquals(match.getFrozenPieces().isBlackFrozen(), true);
        match.updateCurrentTeam();
        Position targetPosition1 = new Position(2, 1);
        Position finalPosition1 = new Position(0, 0);
        match.castSpell(Match.Spell.FREEZE, targetPosition1, finalPosition1);
        assertEquals(match.getFrozenPieces().getFrozenWhite().getRow(), 2);
        assertEquals(match.getFrozenPieces().getFrozenWhite().getColumn(), 1);
        assertEquals(match.getFrozenPieces().getTurnLeftWhite(), 3);
        assertEquals(match.getFrozenPieces().isWhiteFrozen(), true);

    }

    @Test
    public void decideWinnerTest() {
        Match match = new Match();

        // Winner:  white
        // Condition: it occupies 4 out of 4 special positions
        String myBoardString = "S00K00" + "000000" + "00000m" + "000000" + "AK000g" + "00G00S";
        TestUtils.updateBoard(myBoardString, match);
        Match.Winner winner = match.decideWinner();
        assertEquals(Match.Winner.WHITE, winner);

        // Winner:  white
        // Condition: it occupies 3 out of 4 special positions
        myBoardString = "000K00" + "000000" + "00000m" + "000000" + "AK000g" + "00G00S";
        TestUtils.updateBoard(myBoardString, match);
        Match.Winner winner1 = match.decideWinner();
        assertEquals(Match.Winner.WHITE, winner1);

        // Winner: white
        // Condition: all the black pieces are frozen and the white has no frozen pieces
        myBoardString = "000000" + "000000" + "00000M" + "000000" + "00000G" + "00g000";
        TestUtils.updateBoard(myBoardString, match);
        match.getFrozenPieces().setCounterBlackPiecesFrozen(1);
        Match.Winner winner2 = match.decideWinner();
        assertEquals(Match.Winner.WHITE, winner2);

        // Winner:  black
        // Condition:  all the white pieces are frozen and the black has no frozen pieces (First)
        myBoardString = "000000" + "000000" + "00000m" + "000000" + "00000g" + "00G000";
        TestUtils.updateBoard(myBoardString, match);
        match.getFrozenPieces().setCounterWhitePiecesFrozen(1);
        Match.Winner winner3 = match.decideWinner();
        assertEquals(Match.Winner.BLACK, winner3);

        // Winner: black
        // Condition: all the white pieces are frozen and the black has no frozen pieces (Second)
        match = new Match();
        myBoardString = "000000" + "00d000" + "00000G" + "0g0000" + "00k000" + "000000";
        TestUtils.updateBoard(myBoardString, match);
        match.getFrozenPieces().setCounterWhitePiecesFrozen(1);
        Match.Winner winner4 = match.decideWinner();
        assertEquals(Match.Winner.BLACK, winner4);

        // Winner: black
        // Condition: the black has two pieces non frozen and is left just a white piece which is frozen
        myBoardString = "000000" + "00d000" + "00000G" + "0g0000" + "00k000" + "000000";
        TestUtils.updateBoard(myBoardString, match);
        match.getFrozenPieces().setCounterWhitePiecesFrozen(1);
        match.getFrozenPieces().setCounterBlackPiecesFrozen(1);
        Match.Winner winner5 = match.decideWinner();
        assertEquals(Match.Winner.BLACK, winner5);

        // Winner: black
        // Condition: configuration taken from the slide 19
        myBoardString = "000000" + "000000" + "000k00" + "000000" + "000000" + "000000";
        TestUtils.updateBoard(myBoardString, match);
        match.getFrozenPieces().setCounterBlackPiecesFrozen(0);
        Match.Winner winner6 = match.decideWinner();
        assertEquals(Match.Winner.BLACK, winner6);

        // Winner: draw
        // Condition: all of the pieces are frozen both for black and white
        myBoardString = "000000" + "00d000" + "00000G" + "000000" + "000000" + "000000";
        TestUtils.updateBoard(myBoardString, match);
        match.getFrozenPieces().setCounterWhitePiecesFrozen(1);
        match.getFrozenPieces().setCounterBlackPiecesFrozen(1);
        Match.Winner winner7 = match.decideWinner();
        assertEquals(Match.Winner.DRAW, winner7);

        // Winner: draw
        // Condition: configuration taken from the slide 18
        myBoardString = "000000" + "000000" + "000k00" + "000000" + "000000" + "000000";
        TestUtils.updateBoard(myBoardString, match);
        match.getFrozenPieces().setCounterBlackPiecesFrozen(1);
        Match.Winner winner8 = match.decideWinner();
        assertEquals(Match.Winner.DRAW, winner8);

        // Winner: noWinner
        // Condition: no winner conditions are true so the game will continue until there will be a winner
        myBoardString = "0k0000" + "00d0a0" + "0A000G" + "0s0000" + "00K000" + "00000S";
        TestUtils.updateBoard(myBoardString, match);
        match.getFrozenPieces().setCounterWhitePiecesFrozen(1);
        match.getFrozenPieces().setCounterBlackPiecesFrozen(1);
        Match.Winner winner9 = match.decideWinner();
        assertEquals(Match.Winner.NOWINNER, winner9);

        //
    }

    @Test
    public void isPieceAtPositionFrozenTest() {
        Match match = new Match();
        String myBoardString = "000000" + "GK00sa" + "DS00km" + "MK00sd" + "AS00kg" + "000000";
        String vitalityString = "5675434334345765";
        TestUtils.updateBoard(myBoardString, vitalityString, match);
        Position pos = new Position(5, 5);
        Position posFin = new Position(0, 0);
        match.castSpell(Match.Spell.FREEZE, pos, posFin);
        match.getFrozenPieces().isPieceAtPositionFrozen(pos);
    }

    @Test(expected = IllegalArgumentException.class)
    public void castSpellExceptionTest() { //white mage is dead
        Match match = new Match();
        String myBoardString = "000000" + "GK00sa" + "DS00km" + "0K00sd" + "AS00kg" + "000000";
        String vitalityString = "5654343343457650";
        TestUtils.updateBoard(myBoardString, vitalityString, match);
        Position pos = new Position(3, 5);
        Position posFin = new Position(0, 0);
        match.castSpell(Match.Spell.FREEZE, pos, posFin);

    }

    @Test(expected = IllegalArgumentException.class)
    public void castSpellExceptionTest2() { //black mage is dead
        Match match = new Match();
        String myBoardString = "000000" + "GK00sa" + "DS00k0" + "MK00sd" + "AS00kg" + "000000";
        String vitalityString = "5675434334345650";
        TestUtils.updateBoard(myBoardString, vitalityString, match);
        match.updateCurrentTeam();
        Position pos = new Position(2, 1);
        Position posFin = new Position(0, 0);
        match.castSpell(Match.Spell.FREEZE, pos, posFin);


    }

    @Test
    public void killPieceTest(){
        Match match = new Match();
        Position killPos = new Position(3,1);
        Piece piece = match.getBoard().getPieceAtPosition(killPos);
        match.killPiece(killPos);
        assertTrue(match.getListOfDeadWhitePieces().contains(piece));




        Position killPos1 = new Position(3,6);
        Piece p = match.getBoard().getPieceAtPosition(killPos1);
        match.killPiece(killPos1);
        assertTrue(match.getListOfDeadBlackPieces().contains(p));

    }

    @Test
    public void killPieceTest1(){
        Match match = new Match();
        Position killPos = new Position(2,1);
        Piece p = match.getBoard().getPieceAtPosition(killPos);
        Position posFin = new Position(0,0);
        match.killPiece(killPos);
        assertTrue(match.getListOfDeadWhitePieces().contains(p));
        match.castSpell(Match.Spell.REVIVE,killPos,posFin);
        assertFalse(match.getListOfDeadWhitePieces().contains(p));


    }


    @Test
    public void initialPosOfPieceTest() {
        Piece piece = new Giant(Piece.Team.WHITE);
        Match match = new Match();
        String myBoardString = "000000" + "00K0sa" + "DSGk0m" + "MK00sd" + "AS00kg" + "000000";
        String vitalityString = "6753434543345765";
        TestUtils.updateBoard(myBoardString, vitalityString, match);
        Vector<Position> expectedPosition = new Vector<Position>();
        expectedPosition.add(new Position(2,1));

        assertEquals(expectedPosition.elementAt(0).getRow(),match.initialPosOfPiece(piece).elementAt(0).getRow());
        assertEquals(expectedPosition.elementAt(0).getColumn(),match.initialPosOfPiece(piece).elementAt(0).getColumn());
    }

    @Test
    public void initialPosOfPieceTest2() {
        Piece piece = new Squire(Piece.Team.BLACK);
        Match match = new Match();
        String myBoardString = "000000" + "00K0sa" + "DSGk0m" + "MK00sd" + "AS00kg" + "000000";
        String vitalityString = "6753434543345765";
        TestUtils.updateBoard(myBoardString, vitalityString, match);
        Vector<Position> expectedPosition = new Vector<Position>();
        expectedPosition.add(new Position(2,5));
        expectedPosition.add(new Position(4,5));
        match.updateCurrentTeam();
        assertEquals(expectedPosition.elementAt(0).getRow(),match.initialPosOfPiece(piece).elementAt(0).getRow());
        assertEquals(expectedPosition.elementAt(0).getColumn(),match.initialPosOfPiece(piece).elementAt(0).getColumn());
        assertEquals(expectedPosition.elementAt(1).getRow(),match.initialPosOfPiece(piece).elementAt(1).getRow());
        assertEquals(expectedPosition.elementAt(1).getColumn(),match.initialPosOfPiece(piece).elementAt(1).getColumn());
    }

    @Test
    public void removePiecesFromDeadListsTest(){
        Match match = new Match();
        Position pos = new Position(2,1);
        Piece giant = match.getBoard().getPieceAtPosition(pos);
        match.killPiece(pos);
        assertTrue(match.getListOfDeadWhitePieces().contains(giant));
        match.removePiecesFromDeadLists(giant);
        assertFalse(match.getListOfDeadWhitePieces().contains(giant));
    }

    @Test
    public void removePiecesFromDeadListsTest1(){
        Match match = new Match();
        Position pos = new Position(2,5); //blackSquire
        Position pos2 = new Position(3,5); //blackKnight
        Position pos3 = new Position(2,6); //blackArcher
        Position posFin = new Position(0,0);
        Piece squire = match.getBoard().getPieceAtPosition(pos);
        Piece knight = match.getBoard().getPieceAtPosition(pos2);
        Piece archer = match.getBoard().getPieceAtPosition(pos3);
        match.killPiece(pos);
        match.killPiece(pos2);
        match.killPiece(pos3);
        assertTrue(match.getListOfDeadBlackPieces().contains(squire));
        assertTrue(match.getListOfDeadBlackPieces().contains(knight));
        assertTrue(match.getListOfDeadBlackPieces().contains(archer));
        match.removePiecesFromDeadLists(knight);
        assertFalse(match.getListOfDeadWhitePieces().contains(knight));
        match.updateCurrentTeam();
        match.castSpell(Match.Spell.REVIVE,pos3,posFin);
        assertFalse(match.getListOfDeadWhitePieces().contains(archer));

    }

}