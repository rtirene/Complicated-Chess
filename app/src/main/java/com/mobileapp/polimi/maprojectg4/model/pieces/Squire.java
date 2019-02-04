package com.mobileapp.polimi.maprojectg4.model.pieces;


import com.mobileapp.polimi.maprojectg4.model.Piece;

public class Squire extends Piece {

    public Squire(Team myTeam){
        this.myTeam = myTeam;
        this.maxVitality = 3;
        this.currentVitality = this.maxVitality;
        this.moveRange = 1;
        this.moveDirection = Direction.STRAIGHT;
        this.moveType = Movement.WALK;
        this.attackRange = 0;
        this.strength = 1;
        this.attackDirection = Direction.EMPTY;
        this.canUseSpells = false;
        this.canAttack=false;

    }


}

