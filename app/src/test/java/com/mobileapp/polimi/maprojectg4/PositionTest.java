package com.mobileapp.polimi.maprojectg4;

import com.mobileapp.polimi.maprojectg4.model.Position;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;



public class PositionTest {

    @Test
    public void movePositionTest(){
        //move right
        Position pos=new Position(3,3);
        pos.movePosition(0);
        assertEquals(pos.getRow(),3);
        assertEquals(pos.getColumn(),4);
        //move up-right
        pos = new Position(3, 3);
        pos.movePosition(1);
        assertEquals(pos.getRow(),2);
        assertEquals(pos.getColumn(),4);
        //move up
        pos = new Position(3, 3);
        pos.movePosition(2);
        assertEquals(pos.getRow(),2);
        assertEquals(pos.getColumn(),3);
        //move up-left
        pos = new Position(3, 3);
        pos.movePosition(3);
        assertEquals(pos.getRow(),2);
        assertEquals(pos.getColumn(),2);
        //move left
        pos = new Position(3, 3);
        pos.movePosition(4);
        assertEquals(pos.getRow(),3);
        assertEquals(pos.getColumn(),2);
        //move left-down
        pos = new Position(3, 3);
        pos.movePosition(5);
        assertEquals(pos.getRow(),4);
        assertEquals(pos.getColumn(),2);
        //move down
        pos = new Position(3, 3);
        pos.movePosition(6);
        assertEquals(pos.getRow(),4);
        assertEquals(pos.getColumn(),3);
        //move down-right
        pos = new Position(3, 3);
        pos.movePosition(7);
        assertEquals(pos.getRow(),4);
        assertEquals(pos.getColumn(),4);
        //move right by 3 steps
        pos=new Position(3,3);
        pos.movePosition(0,3);
        assertEquals(pos.getRow(),3);
        assertEquals(pos.getColumn(),6);
        //move up-right by 1 steps
        pos = new Position(3, 3);
        pos.movePosition(1,1);
        assertEquals(pos.getRow(),2);
        assertEquals(pos.getColumn(),4);
        //move down by 6 steps
        pos = new Position(3, 3);
        pos.movePosition(6,6);
        assertEquals(pos.getRow(),9);
        assertEquals(pos.getColumn(),3);
    }


}
