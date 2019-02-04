package com.mobileapp.polimi.maprojectg4.model.pieces;


import com.mobileapp.polimi.maprojectg4.model.Piece;

public class Giant extends Piece {

    public Giant(Team myTeam){
        this.myTeam = myTeam;
        this.maxVitality = 5;
        this.currentVitality = this.maxVitality;
        this.moveRange = 2;
        this.moveDirection = Direction.STRAIGHT;
        this.moveType = Movement.WALK;
        this.attackRange = 1;
        this.strength = 4;
        this.attackDirection = Direction.STRAIGHT;
        this.canAttack=true;
        this.canUseSpells = false;

    }
}