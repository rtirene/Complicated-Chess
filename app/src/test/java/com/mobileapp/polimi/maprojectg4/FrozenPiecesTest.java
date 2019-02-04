package com.mobileapp.polimi.maprojectg4;

import com.mobileapp.polimi.maprojectg4.model.FrozenPieces;
import com.mobileapp.polimi.maprojectg4.model.Match;
import com.mobileapp.polimi.maprojectg4.model.Position;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class FrozenPiecesTest {

    @Test
    public void setFrozenWhiteTest(){
        Position pos = new Position(2,2);
        FrozenPieces frozenPieces =new FrozenPieces();
        frozenPieces.setFrozenWhite(pos);
        assertEquals(frozenPieces.getFrozenWhite().getRow(),pos.getRow());
        assertEquals(frozenPieces.getFrozenWhite().getColumn(),pos.getColumn());
        assertEquals(frozenPieces.getTurnLeftWhite(),3);
        assertEquals(frozenPieces.isWhiteFrozen(),true);



    }
    @Test
    public void setFrozenBlackTest(){
        Position pos = new Position(2,5);
        FrozenPieces frozenPieces =new FrozenPieces();
        frozenPieces.setFrozenBlack(pos);
        assertEquals(frozenPieces.getFrozenBlack().getRow(),pos.getRow());
        assertEquals(frozenPieces.getFrozenBlack().getColumn(),pos.getColumn());
        assertEquals(frozenPieces.getTurnLeftBlack(),3);
        assertEquals(frozenPieces.isBlackFrozen(),true);


    }

    @Test
    public void updateTurnLeft(){

        Match match = new Match();
        Position pos = new Position(3,1);
        match.getFrozenPieces().setFrozenWhite(pos);
        match.getFrozenPieces().updateTurnLeft(match.getCurrentTeam());
        assertEquals(match.getFrozenPieces().getTurnLeftWhite(),2);
        match.getFrozenPieces().setTurnLeftWhite(1);
        match.getFrozenPieces().updateTurnLeft(match.getCurrentTeam());
        assertEquals(match.getFrozenPieces().getFrozenWhite().getRow(),0);
        assertEquals(match.getFrozenPieces().getFrozenWhite().getColumn(),0);
        assertEquals(match.getFrozenPieces().getTurnLeftWhite(),0);
        assertEquals(match.getFrozenPieces().isWhiteFrozen(),false);

        match.updateCurrentTeam();


        Position pos1 = new Position(2,6);
        match.getFrozenPieces().setFrozenBlack(pos1);
        match.getFrozenPieces().updateTurnLeft(match.getCurrentTeam());
        match.getFrozenPieces().setTurnLeftBlack(1);
        match.getFrozenPieces().updateTurnLeft(match.getCurrentTeam());
        assertEquals(match.getFrozenPieces().getFrozenBlack().getRow(),0);
        assertEquals(match.getFrozenPieces().getFrozenBlack().getColumn(),0);
        assertEquals(match.getFrozenPieces().getTurnLeftBlack(),0);
        assertEquals(match.getFrozenPieces().isWhiteFrozen(),false);

    }


}


