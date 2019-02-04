package com.mobileapp.polimi.maprojectg4.model.pieces;

import com.mobileapp.polimi.maprojectg4.model.Piece;

public class Archer extends Piece {

    public Archer(Team myTeam){
        this.myTeam = myTeam;
        this.maxVitality = 5;
        this.currentVitality = this.maxVitality;
        this.moveRange = 2;
        this.moveDirection = Direction.ANY;
        this.moveType = Movement.WALK;
        this.attackRange = 3;
        this.strength = 2;
        this.attackDirection = Direction.STRAIGHT;
        this.canUseSpells = false;
        this.canAttack=true;



    }

}

