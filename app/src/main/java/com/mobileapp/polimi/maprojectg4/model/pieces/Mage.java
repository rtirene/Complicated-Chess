package com.mobileapp.polimi.maprojectg4.model.pieces;


import com.mobileapp.polimi.maprojectg4.model.Piece;

public class Mage extends Piece {

    public Mage(Team myTeam){
        this.myTeam = myTeam;
        this.maxVitality = 7;
        this.currentVitality = this.maxVitality;
        this.moveRange = 1;
        this.moveDirection = Direction.ANY;
        this.moveType = Movement.WALK;
        this.attackRange = 0;
        this.strength = 2;
        this.attackDirection = Direction.EMPTY;
        this.canUseSpells = true;
        this.canAttack=false;

    }
}
