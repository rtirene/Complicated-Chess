package com.mobileapp.polimi.maprojectg4;

import com.mobileapp.polimi.maprojectg4.testUtils.TestUtils;
import com.mobileapp.polimi.maprojectg4.model.Match;
import com.mobileapp.polimi.maprojectg4.model.Piece;
import com.mobileapp.polimi.maprojectg4.testUtils.TurnUtils;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;


public class TurnTest {

    @Test
    public void playTurnsTest(){
        String argTest = "W" +
                "000000" + // board (row 1)
                "GK00sa" + // board (row 2)
                "DS00km" + // board (row 3)
                "MK00sd" + // board (row 4)
                "AS00kg" + // board (row 5)
                "000000" + // board (row 6)
                "5675434334345765" + // vitality
                "000000" + // frozen pieces
                "FHRTFHRT" + // unused spells
                "M1343" + // turn: white moves dragon to (4,3)
                "M5242" + // turn: black moves a squire to (4,2)
                "A4353" + // turn: white dragon attacks knight (5,3)
                "M4243";  // turn: black squire moves to (4,3), thus engaging in a combat with dragon"


        String expectedArg= "W" // moving player c'è da capire se e quando aggiorna il turno
                + "000000" // board (row 1)
                + "GK000a" // board (row 2)
                + "0S0Dkm" // board (row 3)
                + "MK00sd" // board (row 4)
                + "AS00kg" // board (row 5)
                + "000000" // board (row 6)
                + "5754343513457650" // vitality
                + "000000" // frozen pieces
                + "FHRTFHRT"; // unused spells

        String outString = TurnUtils.turnTest(argTest);
        assertEquals(expectedArg,outString);
    }

    @Test
    public void playTurnsTest1() {
        String argTest = "W" +
                "000000" + // board (row 1)
                "GK00sa" + // board (row 2)
                "DS00km" + // board (row 3)
                "MK00sd" + // board (row 4)
                "AS00kg" + // board (row 5)
                "000000" + // board (row 6)
                "5675434334345765" + // vitality
                "000000" + // frozen pieces
                "FHRTFHRT" + // unused spells
                "M1343" + // turn: white moves dragon to (4,3)
                "M5242" + // turn: black moves a squire to (4,2)
                "A4353" + // turn: white dragon attacks knight (5,3)
                "M4243" +  // turn: black squire moves to (4,3), thus engaging in a combat with dragon"
                "F6400"; // turn: white mage freezes black dragon


        String expectedArg= "B" // moving player c'è da capire se e quando aggiorna il turno
                + "000000" // board (row 1)
                + "GK000a" // board (row 2)
                + "0S0Dkm" // board (row 3)
                + "MK00sd" // board (row 4)
                + "AS00kg" // board (row 5)
                + "000000" // board (row 6)
                + "5754343513457650" // vitality
                + "000643" // frozen pieces
                + "0HRTFHRT"; // unused spells

        String returnedString = TurnUtils.turnTest(argTest);
        assertEquals(expectedArg,returnedString);
    }

    @Test
    public void playTurnTest2(){
        String argTest = "W" +
                "000000" + // board (row 1)
                "GK00sa" + // board (row 2)
                "DS00km" + // board (row 3)
                "MK00sd" + // board (row 4)
                "AS00kg" + // board (row 5)
                "000000" + // board (row 6)
                "5675434334345765" + // vitality
                "000000" + // frozen pieces
                "FHRTFHRT" + // unused spells
                "M1343" + // turn: white moves dragon to (4,3)
                "M5242" + // turn: black moves a squire to (4,2)
                "A4353" + // turn: white dragon attacks knight (5,3) KnightVitality = 1
                "M4243" + // turn: black squire moves to (4,3), thus engaging in a combat with dragon" SquireVita = 0; DragonVita = 5
                "M1526" + // turn: white moves archer to (2,6)
                "H5300" +  // turn: black heals knight
                "T1461" +  // turn: white teleports mage to (6,1):WE EXPECT AN EXCEPTION
                "R5200" + // turn: black revives squire
                "H2600" + //turn:white heal to waste a turn
                "A6261";  // turn: black archer attaks mage; MageVita = 5

        String expectedArg= "W"
                + "00000M" // board (row 1)
                + "GK00sa" // board (row 2)
                + "0S0Dkm" // board (row 3)
                + "0K00sd" // board (row 4)
                + "0S00kg" // board (row 5)
                + "0A0000" // board (row 6)
                + "5434355343455765" // vitality
                + "000000" // frozen pieces
                + "F0R0F00T"; // unused spells

        TurnUtils TurnUtils = new TurnUtils();
        String returnedString = com.mobileapp.polimi.maprojectg4.testUtils.TurnUtils.turnTest(argTest);
        assertEquals("ERROR: The spell can't be cast on a position with a mage",returnedString);

    }

    @Test
    public void playTurnTest3() {

        String argTest = "W" +
                "000000" + // board (row 1)
                "000000" + // board (row 2)
                "000k0m" + // board (row 3)
                "0000M0" + // board (row 4)
                "000000" + // board (row 5)
                "000000" + // board (row 6)
                "1110000000000000" + // vitality
                "000431" + // frozen pieces
                "0HRTFHRT" + // unused spells
                "M5463"; // turn: white mage moves over black mage

        String expectedArg = "B" +
                "000000" + // board (row 1)
                "000000" + // board (row 2)
                "000k00" + // board (row 3)
                "000000" + // board (row 4)
                "000000" + // board (row 5)
                "000000" + // board (row 6)
                "1000000000000000" + // vitallity
                "000431" + // frozen pieces
                "0HRTFHRT"+
                "DRAW";

        Match.Winner expectedWinner = Match.Winner.DRAW;
        String returnedString = TurnUtils.turnTest(argTest);
        assertEquals(expectedArg, returnedString);
    }

    @Test
    public void playTurnTest4(){

        String argTest = "B" +
                "000000" + // board (row 1)
                "000000" + // board (row 2)
                "000k0m" + // board (row 3)
                "0000M0" + // board (row 4)
                "000000" + // board (row 5)
                "000000" + // board (row 6)
                "1110000000000000" + // vitality
                "000431" + // frozen pieces
                "0HRTFHRT" + // unused spells
                "M0054" + // turn: black mage moves over white mage
                "A4355";

        String expectedArg = "W" +
                "000000" + // board (row 1)
                "000000" + // board (row 2)
                "000k00" + // board (row 3)
                "000000" + // board (row 4)
                "000000" + // board (row 5)
                "000000" + // board (row 6)
                "1000000000000000" + // vitallity
                "000000" + // frozen pieces
                "0HRTFHRT" + //unused spell
                "BLACK";  //winner

        Match.Winner expectedWinner = Match.Winner.BLACK;
        String returnedString = TurnUtils.turnTest(argTest);
        assertEquals("ERROR: The position is outside the board", returnedString);
    }

    @Test
    public void playTurnTest5() {
        String argTest = "W" +
                "000000" + // board (row 1)
                "GK00sa" + // board (row 2)
                "DS00km" + // board (row 3)
                "MK00sd" + // board (row 4)
                "AS00kg" + // board (row 5)
                "000000" + // board (row 6)
                "5675434334345765" + // vitality
                "000000" + // frozen pieces
                "FHRTFHRT" + // unused spells
                "M2434" + // turn: white knight moves to (4,3)
                "M6444" + // turn: black dragon moves to (4,4)
                "M3444" + //turn: white knight move on the  black dragon. They engage combat. White knight dye, Black dragon healt:2
                "H4400" +  //heal of the black dragon
                "T2556" +  //teleport the white squire on (6,5)
                "M4424" +  //move the black dragon from (4,4) to (4,2)
                "F2400" +  //white freeze the black dragon
                "M6251" + //move the black archer in (1,5)
                "R2400" +  //revive the white knight that kill the frozen dragon taking no damage
                "M5141" + //the black archer moves to (1,4)
                "M1211" + //the white giant moves to (1,1)
                "F1300" + //black freeze the white dragon
                "M1536" + //white move archer to (6,3)
                "M5343" + //move the black knight to (3,4)
                "M5666";  //move the white squire to (6,6) -> white wins

        String expectedArg= "B" // moving player
                + "G00a00" // board (row 1)
                + "0K00s0" // board (row 2)
                + "DS0k0m" // board (row 3)
                + "MK00s0" // board (row 4)
                + "0000kg" // board (row 5)
                + "00A00S" // board (row 6)
                + "5674345543347530" // vitality
                + "131000" // frozen pieces
                + "0H0000RT"+
                "WHITE"; // unused spells

        String returnedString = TurnUtils.turnTest(argTest);
        assertEquals(expectedArg,returnedString);
    }

    @Test
    public void playTurnTest6() { // colonna,riga
        String argTest = "W" +
                "000000" + // board (row 1)
                "GK00sa" + // board (row 2)
                "DS00km" + // board (row 3)
                "MK00sd" + // board (row 4)
                "AS00kg" + // board (row 5)
                "000000" + // board (row 6)
                "5675434334345765" + // vitality
                "000000" + // frozen pieces
                "FHRTFHRT" + // unused spells
                //turns:
                "T1256" + //white teleports giant to (5,6)
                "T6514" + //black teleport giant on (4,1) , in which there is a mage WE EXPECT AN EXCEPTION
                "M1332" +
                "M5251" +
                "M2526" +
                "M5141" +
                "M2636" +
                "A1415" +
                "M2211" +
                "F5600" +
                "M3241" ;


        String expectedArg= "B" // moving player
                + "K00D00" // board (row 1)
                + "00000a" // board (row 2)
                + "0S00km" // board (row 3)
                + "gK00sd" // board (row 4)
                + "A000k0" // board (row 5)
                + "00S0G0" // board (row 6)
                + "4113435434557600" // vitality
                + "562000" // frozen pieces
                + "FHR00HR0" // unused spells
                + "WHITE";
        String returnedString = TurnUtils.turnTest(argTest);
        assertEquals("ERROR: The spell can't be cast on a position with a mage",returnedString);
    }

    @Test
    public void playTurnTest7() {

        String argTest = "W" +
                "000000" + // board (row 1)
                "000000" + // board (row 2)
                "000k0m" + // board (row 3)
                "0000M0" + // board (row 4)
                "000000" + // board (row 5)
                "000000" + // board (row 6)
                "1110000000000000" + // vitality
                "000431" + // frozen pieces
                "0HRTFHRT" + // unused spells
                "M5463"; // turn: white mage moves over black mage

        String expectedArg = "B" +
                "000000" + // board (row 1)
                "000000" + // board (row 2)
                "000k00" + // board (row 3)
                "000000" + // board (row 4)
                "000000" + // board (row 5)
                "000000" + // board (row 6)
                "1000000000000000" + // vitallity
                "000431" + // frozen pieces
                "0HRTFHRT" +
                "DRAW";

        assertEquals(TurnUtils.turnTest(argTest),expectedArg);
    }

    @Test
    public void playTurnsTest8() {
        String argTest = "W" +
                "000000" + // board (row 1)
                "GK00sa" + // board (row 2)
                "DS00km" + // board (row 3)
                "MK00sd" + // board (row 4)
                "AS00kg" + // board (row 5)
                "000000" + // board (row 6)
                "5675434334345765" + // vitality
                "000000" + // frozen pieces
                "FHRTFHRT" + // unused spells
                "M1343" + // turn: white moves dragon to (4,3)
                "M5242" + // turn: black moves a squire to (4,2)
                "A4353" + // turn: white dragon attacks knight (5,3)
                "M4243" +  // turn: black squire moves to (4,3), thus engaging in a combat with dragon"
                "F6400" +// turn: white mage freezes black dragon
                "F1200" + // black mage freezes white giant
                "F6200"; //white mage freezes another pieces, we expect and exception

        String returnedString = TurnUtils.turnTest(argTest);
        assertEquals("ERROR: The spell has been already used",returnedString);
    }

    @Test
    public void playTurnTest(){
        Match match = new Match();
//        TurnUtils TurnUtils = new TurnUtils();
        String argTest = "W" +
                "000000" + // board (row 1)
                "GK00sa" + // board (row 2)
                "DS00km" + // board (row 3)
                "MK00sd" + // board (row 4)
                "AS00kg" + // board (row 5)
                "000000" + // board (row 6)
                "5675434334345765" + // vitality
                "000000" + // frozen pieces
                "FHRTFHRT"; // unused spells

        TurnUtils.setTurn(argTest.substring(0,TurnUtils.getLastUnusedSpells()+1),match); //setting the initial game configurations

        TurnUtils.playTurn("T1256",match); //turn 1: white teleports  giant to (6,5)
        assertEquals("Board test failed:", "0000000K00saDS00kmMK00sdAS00kg0000G0", TestUtils.boardToString(match));
        assertEquals("Vitality test failed:", "6754343343455765", TestUtils.vitalityToString(match));
        assertEquals("Frozen pieces test failed:", "000000", TestUtils.getFrozenPiecesAsString(match));
        assertEquals("Unused Spells test failed:", "FHR0FHRT", TestUtils.getUnusedSpellAsString(match));
        assertEquals("Current Team test failed:", Piece.Team.BLACK, match.getCurrentTeam());
        assertEquals("Decide winner failed:", Match.Winner.NOWINNER, match.decideWinner());

        TurnUtils.playTurn("M6445",match); //turn 2: black moves dragon to (4,5)
        assertEquals("Board test failed:", "0000000K00saDS00kmMK00s0AS0dkg0000G0", TestUtils.boardToString(match));
        assertEquals("Vitality test failed:", "6754343634345575", TestUtils.vitalityToString(match));
        assertEquals("Frozen pieces test failed:", "000000", TestUtils.getFrozenPiecesAsString(match));
        assertEquals("Unused Spells test failed:", "FHR0FHRT",  TestUtils.getUnusedSpellAsString(match));
        assertEquals("Current Team test failed:", Piece.Team.WHITE, match.getCurrentTeam());
        assertEquals("Decide winner failed:", Match.Winner.NOWINNER, match.decideWinner());

        TurnUtils.playTurn("A5655",match); //turn 3: white attacks knight in (5,5) with giant in (5,6)
        assertEquals("Board test failed:", "0000000K00saDS00kmMK00s0AS0d0g0000G0", TestUtils.boardToString(match));
        assertEquals("Vitaity test failed:", "6754343634355750", TestUtils.vitalityToString(match));
        assertEquals("Frozen pieces test failed:", "000000", TestUtils.getFrozenPiecesAsString(match));
        assertEquals("Unused Spells test failed:", "FHR0FHRT",  TestUtils.getUnusedSpellAsString(match));
        assertEquals("Current Team test failed:", Piece.Team.BLACK, match.getCurrentTeam());
        assertEquals("Decide winner failed:", Match.Winner.NOWINNER, match.decideWinner());

        TurnUtils.playTurn("F5600",match); //turn 4: black freezes giant in (5,6)
        assertEquals("Board test failed:", "0000000K00saDS00kmMK00s0AS0d0g0000G0", TestUtils.boardToString(match));
        assertEquals("Vitaity test failed:", "6754343634355750",TestUtils.vitalityToString(match));
        assertEquals("Frozen pieces test failed:", "563000", TestUtils.getFrozenPiecesAsString(match));
        assertEquals("Unused Spells test failed:", "FHR00HRT",  TestUtils.getUnusedSpellAsString(match));
        assertEquals("Current Team test failed:", Piece.Team.WHITE, match.getCurrentTeam());
        assertEquals("Decide winner failed:", Match.Winner.NOWINNER, match.decideWinner());

        TurnUtils.playTurn("M2434",match); //turn 5: white moves knoght to (3,4)
        assertEquals("Board test failed:", "0000000K00saDS00kmM0K0s0AS0d0g0000G0", TestUtils.boardToString(match));
        assertEquals("Vitaity test failed:", "6754334634355750", TestUtils.vitalityToString(match));
        assertEquals("Frozen pieces test failed:", "562000", TestUtils.getFrozenPiecesAsString(match));
        assertEquals("Unused Spells test failed:", "FHR00HRT",  TestUtils.getUnusedSpellAsString(match));
        assertEquals("Current Team test failed:", Piece.Team.BLACK, match.getCurrentTeam());
        assertEquals("Decide winner failed:", Match.Winner.NOWINNER, match.decideWinner());

        TurnUtils.playTurn("M6566",match); //turn 6: black moves giant to (6,6)
        assertEquals("Board test failed:", "0000000K00saDS00kmM0K0s0AS0d000000Gg", TestUtils.boardToString(match));
        assertEquals("Vitaity test failed:", "6754334634355750", TestUtils.vitalityToString(match));
        assertEquals("Frozen pieces test failed:", "562000", TestUtils.getFrozenPiecesAsString(match));
        assertEquals("Unused Spells test failed:", "FHR00HRT",  TestUtils.getUnusedSpellAsString(match));
        assertEquals("Current Team test failed:", Piece.Team.WHITE, match.getCurrentTeam());
        assertEquals("Decide winner failed:", Match.Winner.NOWINNER, match.decideWinner());

        TurnUtils.playTurn("A3445",match); //turn 7: white attacks dragon in (4,5) with knight in (3,4)
        assertEquals("Board test failed:", "0000000K00saDS00kmM0K0s0AS0d000000Gg", TestUtils.boardToString(match));
        assertEquals("Vitaity test failed:", "6754334434355750", TestUtils.vitalityToString(match));
        assertEquals("Frozen pieces test failed:", "561000", TestUtils.getFrozenPiecesAsString(match));
        assertEquals("Unused Spells test failed:", "FHR00HRT",  TestUtils.getUnusedSpellAsString(match));
        assertEquals("Current Team test failed:", Piece.Team.BLACK, match.getCurrentTeam());
        assertEquals("Decide winner failed:", Match.Winner.NOWINNER, match.decideWinner());

        TurnUtils.playTurn("A6656",match); //turn 8: black attacks giant in (5,6) with giant in (6,6)
        assertEquals("Board test failed:", "0000000K00saDS00kmM0K0s0AS0d000000Gg",TestUtils.boardToString(match));
        assertEquals("Vitaity test failed:", "6754334434315750", TestUtils.vitalityToString(match));
        assertEquals("Frozen pieces test failed:", "561000", TestUtils.getFrozenPiecesAsString(match));
        assertEquals("Unused Spells test failed:", "FHR00HRT",  TestUtils.getUnusedSpellAsString(match));
        assertEquals("Current Team test failed:", Piece.Team.WHITE, match.getCurrentTeam());
        assertEquals("Decide winner failed:", Match.Winner.NOWINNER, match.decideWinner());

        TurnUtils.playTurn("A3445",match); //turn 9: white attacks dragon in (4,5) with knight in (3,4)
        assertEquals("Board test failed:", "0000000K00saDS00kmM0K0s0AS0d000000Gg", TestUtils.boardToString(match));
        assertEquals("Vitality test failed:", "6754334234315750", TestUtils.vitalityToString(match));
        assertEquals("Frozen pieces test failed:", "000000", TestUtils.getFrozenPiecesAsString(match));
        assertEquals("Unused Spells test failed:", "FHR00HRT",  TestUtils.getUnusedSpellAsString(match));
        assertEquals("Current Team test failed:", Piece.Team.BLACK, match.getCurrentTeam());
        assertEquals("Decide winner failed:", Match.Winner.NOWINNER, match.decideWinner());

        TurnUtils.playTurn("H4500",match); //turn 10: black mage use heal on dragon in (4,5)
        assertEquals("Board test failed:", "0000000K00saDS00kmM0K0s0AS0d000000Gg", TestUtils.boardToString(match));
        assertEquals("Vitality test failed:", "6754334634315750",TestUtils.vitalityToString(match));
        assertEquals("Frozen pieces test failed:", "000000",TestUtils.getFrozenPiecesAsString(match));
        assertEquals("Unused Spells test failed:", "FHR000RT", TestUtils.getUnusedSpellAsString(match));
        assertEquals("Current Team test failed:", Piece.Team.WHITE, match.getCurrentTeam());
        assertEquals("Decide winner failed:", Match.Winner.NOWINNER, match.decideWinner());

    }

    @Test
    public void playTurnsTest10() {
        String argTest = "B"+
        "00000g"+
        "S00KG0" +
        "k00s00"+
        "m00000"+
        "0000K0"+
        "000000"+
        "1131212400000000" +
        "000000"+
        "FHR00HR0"+
        "R5200";
        String returnedString = TurnUtils.turnTest(argTest);
        assertEquals(returnedString, "W00000gS00K00k00s00m000000000K00000001131224000000000000000FHR00H00");
    }

}
