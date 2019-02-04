package com.mobileapp.polimi.maprojectg4.model.pieces;


import com.mobileapp.polimi.maprojectg4.model.Match;
import com.mobileapp.polimi.maprojectg4.model.Piece;

public class Dragon extends Piece {

    public Dragon(Team myTeam){
        this.myTeam = myTeam;
        this.maxVitality = 6;
        this.currentVitality = maxVitality;
        this.moveRange = 3;
        this.moveDirection = Direction.STRAIGHT;
        this.moveType = Movement.FLY;
        this.attackRange = 2;
        this.strength = 3;
        this.attackDirection = Direction.STRAIGHT;
        this.canUseSpells = false;
        this.canAttack=true;


    }

}
