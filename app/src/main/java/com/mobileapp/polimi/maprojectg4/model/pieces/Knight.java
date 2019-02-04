package com.mobileapp.polimi.maprojectg4.model.pieces;


import com.mobileapp.polimi.maprojectg4.model.Piece;

public class Knight extends Piece {

    public Knight(Team myTeam){
        this.myTeam = myTeam;
        this.maxVitality = 4;
        this.currentVitality = this.maxVitality;
        this.moveRange = 1;
        this.moveDirection = Direction.ANY;
        this.moveType = Movement.WALK;
        this.attackRange = 1;
        this.strength = 2;
        this.attackDirection = Direction.DIAGONAL;
        this.canUseSpells = false;
        this.canAttack=true;

    }
}
