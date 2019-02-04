package com.mobileapp.polimi.maprojectg4.model;

import android.content.Context;
import com.mobileapp.polimi.maprojectg4.R;
import com.mobileapp.polimi.maprojectg4.testUtils.TestUtils;
import java.util.ArrayList;
import java.util.Vector;


public class ArtificialIntelligence {


    /**
     * Plays  a random action, chosen between all the legal actions.
     * @param match the match that we play
     */
    public static void random(Match match){
        //search for the legal actions
        ArrayList<String> arrayMoves = ArtificialIntelligence.possibleActions(match,true);
        //choose and play a random action
        int randomIndex = (int)Math.round(Math.random()*(arrayMoves.size()-1));
        String action =  arrayMoves.get(randomIndex);
        playCpu(action,match);
    }


    /**
     * Plays an action looking at the result of the action in the board and choosing the best action with the function getBestAction.
     * @param match the match to play
     */
    public static void firstLevelAI(Match match){
        String action =  getBestAction(match);
        playCpu(action,match);
    }

    /**
     * plays an action looking at the result of the action in the board and choosing the best action with the function getBestActionNegamax
     * @param match the match to play
     */
    public static String secondLevelAI (Match match,Boolean teleportActivated, int negaMaxDepth,Context context){
        String action =  getBestActionNegamax(match,teleportActivated,negaMaxDepth);
        return playCpuString(action,match,context);
    }


    /**
     * Evaluates the current state of the match. The evaluation function is simmetric, e.g. 400 for the white team, mean -400 for the black team.
     * Evaluates the vitality of the pieces (+- vitality*10), the number of pieces (#pieces*100) and the number of pieces in special cells (#piecesInSpecial*100).
     * Multiplies everything by 100 to avoid the use of double, that degrades the performances
     * @param match
     * @return an integer that evaluate the "goodness" of a match. A positive number will be a good match, a negative number will be a bad match
     */
    public static int evaluationFunction(Match match){
        ArrayList<Position> myTeamPieces = new ArrayList<>();
        ArrayList<Position> opponentPieces = new ArrayList<>();

        for (int r = 1; r <= match.getBoard().getSize(); r++) {  //select the row
            for (int c = 1; c <= match.getBoard().getSize(); c++) {  //select the columns
                Position currentPos = new Position(r, c);

                // My Team Vector
                if (match.getBoard().getPieceAtPosition(new Position(r, c)) != null && match.getBoard().getPieceAtPosition(new Position(r, c)).getMyTeam() == match.getCurrentTeam()) {
                    myTeamPieces.add(currentPos);
                }

                // Opponent Piece Vector
                if (match.getBoard().getPieceAtPosition(currentPos) != null && match.getBoard().getPieceAtPosition(currentPos).getMyTeam() != match.getCurrentTeam()) {
                    opponentPieces.add(currentPos);
                }
            }
        }
        int indexOfVictory = 0;
        int importanceOfBeingAlive = 100;
        int importanceOfSpecialCells = 300;
        int normalizationOfLife = 10;

        //add the vitality of the ally pieces
        for (Position pos:myTeamPieces) {
            int vitalityOfPiece = match.getBoard().getPieceAtPosition(pos).getCurrentVitality();
            indexOfVictory = indexOfVictory + vitalityOfPiece*normalizationOfLife; //add normalized vitality of pieces
        }
        //subtract the vitality of enemy pieces
        for (Position pos:opponentPieces){
            int vitalityOfPiece = match.getBoard().getPieceAtPosition(pos).getCurrentVitality();
            indexOfVictory = indexOfVictory - vitalityOfPiece*normalizationOfLife; //subtract normalized vitality of pieces
        }
        //add/subtract for each alive piece
        indexOfVictory= indexOfVictory+ myTeamPieces.size()*importanceOfBeingAlive - opponentPieces.size()*importanceOfBeingAlive;

        //add point if there are ally pieces on special cell
        for (Position pos:myTeamPieces) {
            for (Position specialPos : match.getSpecialPositions()) {
                //check if we are targeting a special position
                if (pos.getColumn() == specialPos.getColumn() && pos.getRow() == specialPos.getRow()) {
                    indexOfVictory = indexOfVictory + importanceOfSpecialCells;
                }
            }
        }

        //subtract point if there are enemy pieces on special cell
        for (Position pos:opponentPieces) {
            for (Position specialPos : match.getSpecialPositions()) {
                //check if we are targeting a special position
                if (pos.getColumn() == specialPos.getColumn() && pos.getRow() == specialPos.getRow()) {
                    indexOfVictory = indexOfVictory - importanceOfSpecialCells;
                }
            }
        }


        return indexOfVictory;
    }

    /**
     * Evaluates all the legal actions from a starting match and return the best move looking only at the single action.
     * @param match the initial match where we will evaluate all the actions
     * @return the best action as a string
     */
    public static String getBestAction(Match match){
        String bestAction = "";
        int pointfOfBestAction=-10000;
        ArrayList<String> possibleActions = ArtificialIntelligence.possibleActions(match,true);
        for (String action: possibleActions){
            //create a temporary match equal to the current match
            Match temporaryMatch=new Match();
            TestUtils.updateBoard(TestUtils.boardToString(match),TestUtils.vitalityToString(match),temporaryMatch);
            TestUtils.setCemeteriesFromString(TestUtils.getCemeteriesAsString(match),temporaryMatch);
            TestUtils.setCurrentTeamFromString(match.getCurrentTeam().toString(),temporaryMatch);
            TestUtils.setUnusedSpellFromString(TestUtils.getUnusedSpellAsString(match),temporaryMatch);
            TestUtils.setFrozenFromString(TestUtils.getFrozenPiecesAsString(match),temporaryMatch);

            //test an action and get the evaluation function for that action, save the best among every action
            playCpu(action,temporaryMatch);
            if (evaluationFunction(temporaryMatch)>pointfOfBestAction){
                pointfOfBestAction=evaluationFunction(temporaryMatch);
                bestAction = action;
            }
        }
        return bestAction;

    }


    /**
     * Evaluates all the legal actions from a starting match and return the best move using a Negamax algorithm with alpha-beta optimization.
     * @param match
     * @param teleportActivated chooses if we are considering the teleport actions. Disable it to boost the performances
     * @param negaMaxDepth chooses the depth of the recursion of the negamax. 1 will look at your action and your opponent action, 2 will look at your action, your opponent and again your action.
     * @return the best action as a string
     */
    public static String getBestActionNegamax(Match match,Boolean teleportActivated,int negaMaxDepth){
        int alpha = -300;
        int beta = 1200;
        int bestscore= -10000;
        ArrayList<String> possibleActions = ArtificialIntelligence.possibleActions(match,teleportActivated);
        String bestAction=possibleActions.get(0);
        Match temporaryMatch=new Match();
        for (String action: possibleActions){
            //create a temporary match equal to the current match
            TestUtils.updateBoard(TestUtils.boardToString(match),TestUtils.vitalityToString(match),temporaryMatch);
            TestUtils.setCemeteriesFromString(TestUtils.getCemeteriesAsString(match),temporaryMatch);
            TestUtils.setCurrentTeamFromString(match.getCurrentTeam().toString(),temporaryMatch);
            TestUtils.setUnusedSpellFromString(TestUtils.getUnusedSpellAsString(match),temporaryMatch);
            TestUtils.setFrozenFromString(TestUtils.getFrozenPiecesAsString(match),temporaryMatch);
            //test an action
            playCpu(action,temporaryMatch);
            int score = negamax(temporaryMatch, alpha,beta,negaMaxDepth,teleportActivated);
            if (score >= beta) {
                bestAction = action;
                break;
            }
            if (score >bestscore) {
                bestscore = score;
                bestAction = action;
                if (score > alpha) {
                    alpha = score;
                }
            }
        }
        return bestAction;
    }

    /**
     * Searchs the possible action tree with a recursion.
     * @param match
     * @param alpha sets the lower bound for tree pruning
     * @param beta sets the higher bound for tree pruning
     * @param depth sets the number of recursion before to stop
     * @param teleportActivated sets if we are considering the teleport actions
     * @return
     */
    public static int negamax(Match match,int alpha, int beta, int depth,Boolean teleportActivated){
        int bestscore = -10000;
        if(depth == 0){
            return evaluationFunction(match);
        }
        else{
            match.updateCurrentTeam();
            ArrayList<String> possibleActions = ArtificialIntelligence.possibleActions(match,teleportActivated);
            Match temporaryMatch=new Match();
            for (String action:possibleActions) {
                TestUtils.updateBoard(TestUtils.boardToString(match),TestUtils.vitalityToString(match),temporaryMatch);
                TestUtils.setCemeteriesFromString(TestUtils.getCemeteriesAsString(match),temporaryMatch);
                TestUtils.setCurrentTeamFromString(match.getCurrentTeam().toString(),temporaryMatch);
                TestUtils.setUnusedSpellFromString(TestUtils.getUnusedSpellAsString(match),temporaryMatch);
                TestUtils.setFrozenFromString(TestUtils.getFrozenPiecesAsString(match),temporaryMatch);
                playCpu(action,temporaryMatch);
                int score = -negamax(temporaryMatch,-beta,-alpha,depth-1,teleportActivated);
                if (score >= beta) return score;
                if (score >bestscore) {
                    bestscore = score;
                    if (score > alpha) alpha = score;
                }
            }
            return bestscore;
        }
    }


    /**
     * Creates a list with all the possible moves from an initial board. The actions are ordered in this way: spells, movements, attacks.
     * @param match
     * @param teleportActivated is used to set if we want to generate the moves from teleport
     * @return
     */
    public static ArrayList<String> possibleActions(Match match, Boolean teleportActivated){
        ArrayList<String> actions = new ArrayList<>();
        ArrayList<Position> myTeamPieces = new ArrayList<>();
        ArrayList<Position> opponentPieces = new ArrayList<>();
        ArrayList<Piece> deadPieces = new ArrayList<>();
        ArrayList<Position> allPositions = new ArrayList<>();


        for (int r = 1; r <= match.getBoard().getSize(); r++) {  //select the row
            for (int c = 1; c <= match.getBoard().getSize(); c++) {  //select the columns
                Position currentPos = new Position(r, c);

                // My Team Vector
                if (match.getBoard().getPieceAtPosition(new Position(r, c)) != null && match.getBoard().getPieceAtPosition(new Position(r, c)).getMyTeam() == match.getCurrentTeam()) {
                    myTeamPieces.add(currentPos);
                }

                // Opponent Piece Vector
                if (match.getBoard().getPieceAtPosition(currentPos) != null && match.getBoard().getPieceAtPosition(currentPos).getMyTeam() != match.getCurrentTeam()) {
                    opponentPieces.add(currentPos);
                }

                allPositions.add(currentPos);
                // Dead Pieces Vector
                deadPieces = (ArrayList<Piece>) match.getListOfDeadPieces();
            }
        }
        for(Position currentPos: myTeamPieces){
            Piece piece = match.getBoard().getPieceAtPosition(currentPos);
            Vector<Position> possibleDirections = piece.possibleDirections(currentPos,match.getBoard(),match.getCurrentTeam());
            Vector<Position> possibleAttacks = new Vector<>();
            if(piece.isCanAttack()) {
                possibleAttacks = piece.possibleAttacks(currentPos, match.getBoard(), match.getCurrentTeam());
            }
            Match.Spell[] possibleSpells = new Match.Spell[0];
            if(piece.isCanUseSpells()) {
                possibleSpells = match.getUnusedSpells();
            }

            // Compute spells

            for(Match.Spell spell: possibleSpells){
                switch (spell){
                    case REVIVE:
                        for(Piece deadPiece:deadPieces){
                            Vector<Position> initialPositions = match.initialPosOfPiece(deadPiece);
                            for(Position pos:initialPositions){
                                if(match.isSpellPermitted(pos,new Position(0,0), Match.Spell.REVIVE)){
                                    actions.add("R" + pos.getColumn() + pos.getRow() +"00");
                                }
                            }
                        }
                        break;
                    case TELEPORT:
                        if (teleportActivated) {
                            for (Position myTeamPosition : myTeamPieces) {
                                for (Position finalPos : allPositions) {
                                    if (match.isSpellPermitted(myTeamPosition, finalPos, Match.Spell.TELEPORT)) {
                                        actions.add("T" + myTeamPosition.getColumn() + myTeamPosition.getRow() + finalPos.getColumn() + finalPos.getRow());
                                    }
                                }
                            }
                        }
                    break;
                    case FREEZE:
                        for(Position opponentPosition:opponentPieces){
                            if(match.isSpellPermitted(opponentPosition,new Position(0,0), Match.Spell.FREEZE)) {
                                actions.add("F" + opponentPosition.getColumn() + opponentPosition.getRow() + "00");
                            }
                        }
                        break;
                    case HEAL:
                        for(Position myTeamPosition:myTeamPieces){
                            if(match.isSpellPermitted(myTeamPosition,new Position(0,0), Match.Spell.HEAL)) {
                                actions.add("H" + myTeamPosition.getColumn() + myTeamPosition.getRow() + "00");
                            }
                        }
                        break;
                }
            }


            // Compute move
            for(Position finalPos:possibleDirections){
                if(match.canMovePiece(currentPos,finalPos)) {
                    actions.add("M" + currentPos.getColumn() + currentPos.getRow() + finalPos.getColumn() + finalPos.getRow());
                }
            }

            // Compute attack
            for(Position finalPos:possibleAttacks){
                if(match.canAttack(currentPos,finalPos)) {
                    actions.add("A" + currentPos.getColumn() + currentPos.getRow() + finalPos.getColumn() + finalPos.getRow());
                }
            }
        }
        return actions;
    }


    /**
     * Plays a specified action.
     * @param turn the action to play
     * @param match the match where to play
     * */
    public static void playCpu(String turn,Match match){

        int cFirstPosition = Character.getNumericValue(turn.charAt(1));
        int rFirstPosition = Character.getNumericValue(turn.charAt(2));
        int cSecondPosition = Character.getNumericValue(turn.charAt(3));
        int rSecondPosition = Character.getNumericValue(turn.charAt(4));

        Position firstPosition = new Position(rFirstPosition, cFirstPosition);
        Position secondPosition = new Position(rSecondPosition, cSecondPosition);

        switch (turn.charAt(0)){

            case 'M' :
                match.movePiece(firstPosition,secondPosition);
                break;
            case 'A' :
                match.attack(firstPosition,secondPosition);
                break;
            case 'H' :
                match.castSpell(Match.Spell.HEAL,firstPosition,secondPosition);
                break;
            case 'T' :
                match.castSpell(Match.Spell.TELEPORT,firstPosition,secondPosition);
                break;
            case 'R' :
                match.castSpell(Match.Spell.REVIVE,firstPosition,secondPosition);
                break;
            case 'F' :
                match.castSpell(Match.Spell.FREEZE,firstPosition,secondPosition);
                break;
            default:
                throw new IllegalArgumentException("The string that you inserted doesn't not follow the specifics" +
                        "\n Please insert a valid string!");
        }

        match.decideWinner();
    }


    /**
     * Plays a specified action and compute a textual description of that action.
     * @param turn the action to play
     * @param match
     * @param context a context to call the externalized strings
     * @return the textual description of the action played
     */
    public static String playCpuString(String turn,Match match,Context context){

        int cFirstPosition = Character.getNumericValue(turn.charAt(1));
        int rFirstPosition = Character.getNumericValue(turn.charAt(2));
        int cSecondPosition = Character.getNumericValue(turn.charAt(3));
        int rSecondPosition = Character.getNumericValue(turn.charAt(4));
        String message = "null";
        final Position firstPosition = new Position(rFirstPosition, cFirstPosition);
        final Position secondPosition = new Position(rSecondPosition, cSecondPosition);

        switch (turn.charAt(0)){

            case 'M' :
                if(match.getBoard().getPieceAtPosition(secondPosition) == null) {
                    message = TestUtils.teamToLowerCase(match.getCurrentTeam()) +" " + context.getString(R.string.moved) +" "+ TestUtils.getPieceName(firstPosition, match);
                    match.movePiece(firstPosition, secondPosition);
                }else {
                    String pieceToMove = TestUtils.getPieceName(firstPosition, match);
                    String finalPiece = TestUtils.getPieceName(secondPosition, match);
                    match.movePiece(firstPosition, secondPosition);
                    if (match.getBoard().getPieceAtPosition(secondPosition) != null) {
                        if (match.getBoard().getPieceAtPosition(secondPosition).getMyTeam() == match.getCurrentTeam()) {
                            message = TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + pieceToMove + " " + context.getString(R.string.defeated) + " " +
                                    TestUtils.teamToLowerCase(match.getOpponentTeam()) + " " + finalPiece;
                        } else if (match.getBoard().getPieceAtPosition(secondPosition).getMyTeam() == match.getOpponentTeam()) {
                            message = TestUtils.teamToLowerCase(match.getOpponentTeam()) + " " + finalPiece + " " + context.getString(R.string.defeated) + " " +
                                    TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + pieceToMove;
                        }
                    } else {
                        message = context.getString(R.string.NoPieceSurvived);
                    }
                }
                break;
            case 'A' :
                String attacker = TestUtils.getPieceName(firstPosition, match);
                String attackedPiece = TestUtils.getPieceName(secondPosition,match);
                match.attack(firstPosition,secondPosition);
                if(match.getBoard().getPieceAtPosition(secondPosition)!=null){
                    message = TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + attacker
                            + " "+context.getString(R.string.attacks)+" " + TestUtils.teamToLowerCase(match.getOpponentTeam()) + " " +  attackedPiece;

                }
                else{
                    message = TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + attacker
                            + " "+context.getString(R.string.attacks)+" " + TestUtils.teamToLowerCase(match.getOpponentTeam()) + " " +  attackedPiece+
                            " "+context.getString(R.string.killedAfterAttack);

                }


                break;
            case 'H' :
                message = TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + context.getString(R.string.Mage)
                        + " "+context.getString(R.string.heals)+" " + TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " +  TestUtils.getPieceName(firstPosition,match);
                match.castSpell(Match.Spell.HEAL,firstPosition,secondPosition);
                break;
            case 'T' :
                if(match.getBoard().getPieceAtPosition(secondPosition) == null) {
                    message = TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + context.getString(R.string.Mage)+" "
                            + context.getString(R.string.teleports) +" "+ TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + TestUtils.getPieceName(firstPosition,match);
                    match.castSpell(Match.Spell.TELEPORT, firstPosition, secondPosition);

                }else {
                    String pieceToTeleport = TestUtils.getPieceName(firstPosition, match);
                    String finalPiece = TestUtils.getPieceName(secondPosition, match);
                    match.castSpell(Match.Spell.TELEPORT, firstPosition, secondPosition);
                    if (match.getBoard().getPieceAtPosition(secondPosition) != null) {
                        if (match.getBoard().getPieceAtPosition(secondPosition).getMyTeam() == match.getCurrentTeam()) {
                            message = TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + context.getString(R.string.Mage) + " "
                                    + context.getString(R.string.teleported) + " " + TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + pieceToTeleport + " " + context.getString(R.string.combatWon) + " " +
                                    context.getString(R.string.against) + " " + TestUtils.teamToLowerCase(match.getOpponentTeam()) + " " + finalPiece;

                        } else if (match.getBoard().getPieceAtPosition(secondPosition).getMyTeam() == match.getOpponentTeam()) {
                            message = TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + context.getString(R.string.Mage) + " "
                                    + context.getString(R.string.teleported) + " " + TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + pieceToTeleport + " " + context.getString(R.string.combatNotWon) + " " +
                                    " " + context.getString(R.string.against) + " " + TestUtils.teamToLowerCase(match.getOpponentTeam()) + " " + finalPiece;
                        }
                    }
                }
                break;
            case 'R' :
                if(match.getBoard().getPieceAtPosition(firstPosition) == null) {
                    match.castSpell(Match.Spell.REVIVE, firstPosition, secondPosition);
                    message = TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + context.getString(R.string.Mage)
                            + " " + context.getString(R.string.revives) + " " + TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + TestUtils.getPieceName(firstPosition, match);
                }else {

                    match.castSpell(Match.Spell.REVIVE, firstPosition, secondPosition);
                    if (match.getBoard().getPieceAtPosition(firstPosition) != null) {
                        if (match.getBoard().getPieceAtPosition(firstPosition).getMyTeam() == match.getCurrentTeam()) {
                            message = TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + context.getString(R.string.Mage)
                                    + " " + context.getString(R.string.revives) + " " + TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + TestUtils.getPieceName(firstPosition, match) + " " +
                                    context.getString(R.string.combatWon);
                        } else if (match.getBoard().getPieceAtPosition(firstPosition).getMyTeam() == match.getOpponentTeam()) {
                            message = context.getString(R.string.reviveDied);
                        }
                    }
                }
                break;
            case 'F' :
                match.castSpell(Match.Spell.FREEZE,firstPosition,secondPosition);
                message = TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + context.getString(R.string.Mage)
                        +" "+ context.getString(R.string.freezes)+" " + TestUtils.teamToLowerCase(match.getOpponentTeam()) + " " +  TestUtils.getPieceName(firstPosition,match);
                break;
            default:
                throw new IllegalArgumentException("The string that you inserted doesn't not follow the specifics" +
                        "\n Please insert a valid string!");

        }

        match.decideWinner();
        return message;
    }
}
