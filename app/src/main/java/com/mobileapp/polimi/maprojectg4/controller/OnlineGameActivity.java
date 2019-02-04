package com.mobileapp.polimi.maprojectg4.controller;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileapp.polimi.maprojectg4.R;
import com.mobileapp.polimi.maprojectg4.model.ArtificialIntelligence;
import com.mobileapp.polimi.maprojectg4.model.Match;
import com.mobileapp.polimi.maprojectg4.model.OnlineGame;
import com.mobileapp.polimi.maprojectg4.model.Piece;
import com.mobileapp.polimi.maprojectg4.model.Position;
import com.mobileapp.polimi.maprojectg4.testUtils.TestUtils;
import com.mobileapp.polimi.maprojectg4.view.BoardAdapter;
import com.mobileapp.polimi.maprojectg4.view.ViewUtils;

import java.util.List;
import java.util.Vector;

import static android.support.v7.widget.ListPopupWindow.WRAP_CONTENT;
import static android.widget.Toast.makeText;

public class OnlineGameActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String CURRENT_BOARD = "current boardString";
    private static final String FROZEN_PIECES = "current frozenPieces";
    private static final String UNUSED_SPELLS = "current unusedSpell";
    private static final String CURRENT_TEAM = "the current team";
    private static final String CIMITERIES = "the white and black cimiteries";
    private static final String VITALITY = "current vitality";
    private Match match;
    private TextView turnView;
    private TextView teamView;
    private GridView chessboard;
    private BoardAdapter adapter;
    private Context context;
    private View[] cellSelected;
    private Position[] selectedPosition;
    private GridView chessBoardGridView;
    private LinearLayout taskLayout;
    private boolean isMoveButtonActivated;
    private boolean isAttackButtonActivated;
    private boolean isSpellButtonActivated;
    private boolean isFreezeButtonActivated;
    private boolean isHealButtonActivated;
    private boolean isReviveButtonActivated;
    private boolean isTeleportButtonActivated;
    private Match.Winner winner;

    /** Online game attributes*/
    private OnlineGame game = new OnlineGame();
    private String endMess;
    char[] move = new char[5];
    private boolean isGameEnded;
    private boolean thereIsAMove;
    private Toast toast;
    private boolean isBackPressed;
    private Thread Online;

    /**Used to stop/start the music in background and to change the image representing the sound button.
     * It is linked to it using the onClick in the xml
     * @param view
     */
    public void soundOnClick(View view) {
        if(isSoundServiceRunning(SoundService.class)){
            FloatingActionButton soundButton = (FloatingActionButton) findViewById(R.id.sound);
            stopService(new Intent(this, SoundService.class));
            soundButton.setImageResource(R.drawable.stop_sound);
        }
        else{
            FloatingActionButton soundButton = (FloatingActionButton) findViewById(R.id.sound);
            startService(new Intent(this, SoundService.class));
            soundButton.setImageResource(R.drawable.start_sound);
        }
    }

    /**
     * Manage the online game using a different thread, it is called after the match is created.
     * Post the first move for the white team and start the game loop.
     * Does the calls to the server with the functions postMove and getMove.
     */
    private void onlineGame() {

        Online = new Thread() {

            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (game.getYourTeam().toString().toLowerCase().equals("black"))
                            teamView.setText(getResources().getString(R.string.black));
                    }
                });

                //if i'm first, i.e. i'm white, post the first move.
                String firstMove;
                if (game.getYourTeam().toString().equals(Piece.Team.WHITE.toString())) {
                    //send move
                    int sec = 0;
                    while (!thereIsAMove && sec<30) {
                        try {
                            Thread.sleep(1000);
                            sec++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(sec > 29){

                        isGameEnded = true;
                        endMess = "timeout";

                    } else {

                        String m = String.valueOf(game.getClickMove());
                        firstMove = game.postMove(m);

                        if(firstMove.equals("200")){

                            thereIsAMove = false;

                        } else{

                            isGameEnded = true;
                            endMess = firstMove;
                        }
                    }
                }

                //start the game loop
                while (!isGameEnded ) {

                    if (!game.getYourTeam().toString().equals(match.getCurrentTeam().toString())) {


                        String[] moveGet = game.getMove();

                        if (moveGet[0].equals("200")) {
                            final String toastMessage = ArtificialIntelligence.playCpuString(moveGet[1], match, context);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateGraphics();
                                    toast.cancel();
                                    toast = makeText(context,toastMessage,Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                        } else {
                            isGameEnded = true;
                            endMess = moveGet[0];
                        }
                    }

                    String winner = match.decideWinner().toString().toLowerCase();
                    if (winner.equals(Match.Winner.NOWINNER.toString().toLowerCase())){
                        int sec = 0;
                        while (!thereIsAMove && sec<30) {
                            try {
                                sec++;
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if(sec > 29){

                            isGameEnded = true;
                            endMess = "timeout";

                        } else {

                            String m = String.valueOf(game.getClickMove());
                            String movePost = game.postMove(m);

                            if (movePost.equals("200")) {
                                thereIsAMove = false;
                                winner = match.decideWinner().toString().toLowerCase();
                                if(winner.toLowerCase().equals(game.getYourTeam().toString().toLowerCase())){
                                    isGameEnded = true;
                                    endMess = winner+" wins";
                                } else if(winner.toLowerCase().equals("draw")){
                                    isGameEnded = true;
                                    endMess = "Draw";
                                }
                            } else {

                                isGameEnded = true;
                                endMess = movePost;
                            }
                        }
                    } else {
                        isGameEnded = true;
                        if(winner.equals("draw"))
                            endMess = winner;
                        else endMess = winner+" wins";
                    }

                }


                if(this.isAlive() && !isBackPressed){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            SeeMatchActivity.setCurrentTurn(0);
                            builder.setTitle("Game ended: " + endMess);
                            builder.setMessage("Do you want to play again?");

                            builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(OnlineGameActivity.this, PlayActivity.class));
                                    finish();
                                    dialog.dismiss();
                                    //mediaPlayer.stop();
                                }
                            });

                            builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(OnlineGameActivity.this, WaitOnlineGameActivity.class));
                                    finish();
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });
                }
            }
        };
        Online.start();
    }

    /** Overridden methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //lock the screen to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onlinegame);
        thereIsAMove = false;
        isGameEnded = false;
        isBackPressed = false;

        // eliminate the status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        toast = new Toast(this);
        this.match = new Match();
        context = this;
        winner = Match.Winner.NOWINNER;
        cellSelected = new View[2];
        selectedPosition = new Position[2];
        turnView = (TextView) findViewById(R.id.turnView);

        if (savedInstanceState != null) {
            TestUtils.updateBoard(savedInstanceState.getString(CURRENT_BOARD),savedInstanceState.getString(VITALITY), match);
            TestUtils.setFrozenFromString(savedInstanceState.getString(FROZEN_PIECES), match);
            TestUtils.setUnusedSpellFromString(savedInstanceState.getString(UNUSED_SPELLS), match);
            TestUtils.setCurrentTeamFromString(savedInstanceState.getString(CURRENT_TEAM), match);
            TestUtils.setCemeteriesFromString(savedInstanceState.getString(CIMITERIES), match);

            if (savedInstanceState.getString(CURRENT_TEAM).equals("BLACK"))
                changeTurnText();
        }

        //initialize the board
        chessBoardGridView = (GridView) findViewById(R.id.chessboard);
        adapter = new BoardAdapter(context, match.getBoard(), match.getFrozenPieces());
        chessBoardGridView.setAdapter(adapter);
        chessBoardGridView.setOnItemClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            chessBoardGridView.setElevation(200);
        //set the correct dimension of the board
        ViewGroup.LayoutParams layoutParams = chessBoardGridView.getLayoutParams();
        int size = ViewUtils.getSizeOfBoard(context);
        layoutParams.height = size;
        layoutParams.width = size;
        this.chessboard = chessBoardGridView;
        chessboard.setLongClickable(true);
        taskLayout = (LinearLayout) findViewById(R.id.taskBar);
        teamView = (TextView) findViewById(R.id.yourTeam);

        //set the game parameters
        game.setGameUrl(getIntent().getExtras().getString("url"));
        if(getIntent().getExtras().getString("color").equals("white"))
            game.setYourTeam(Piece.Team.WHITE);
        else game.setYourTeam(Piece.Team.BLACK);


        //showMePieceInformation();

        onlineGame();

        FloatingActionButton soundButton = (FloatingActionButton) findViewById(R.id.sound);
        if(isSoundServiceRunning(SoundService.class)){
            soundButton.setImageResource(R.drawable.start_sound);
        }
        else{
            soundButton.setImageResource(R.drawable.stop_sound);

        }

    }

    /**Used to retrive if the music in background is active
     * @param soundService
     * @return true if the sound service is running, false if not
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean isSoundServiceRunning(Class<SoundService> soundService) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (soundService.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @Override
    protected void onPause() {
        Context context = getApplicationContext();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        if (!taskInfo.isEmpty()) {
            ComponentName topActivity = taskInfo.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                FloatingActionButton soundButton = (FloatingActionButton) findViewById(R.id.sound);
                stopService(new Intent(this, SoundService.class));
                soundButton.setImageResource(R.drawable.stop_sound);
            }
        }
        super.onPause();
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        // Save the user's current game state
        savedInstanceState.putString(CURRENT_BOARD, TestUtils.boardToString(match));
        savedInstanceState.putString(FROZEN_PIECES, TestUtils.getFrozenPiecesAsString(match));
        savedInstanceState.putString(UNUSED_SPELLS, TestUtils.getUnusedSpellAsString(match));
        savedInstanceState.putString(CURRENT_TEAM, match.getCurrentTeam().toString());
        savedInstanceState.putString(CIMITERIES, TestUtils.getCemeteriesAsString(match));
        savedInstanceState.putString(VITALITY, TestUtils.vitalityToString(match));

        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        SeeMatchActivity.setCurrentTurn(0);
        builder.setTitle("Exit");
        builder.setMessage("Do you want to quit?");

        builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(OnlineGameActivity.this, PlayActivity.class));
                if(game.getConnection() != null)
                    game.getConnection().disconnect();
                if(game.getConnectionMove() != null)
                    game.getConnectionMove().disconnect();
                isBackPressed = true;
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();


    }
    @Override
    public void onItemClick(final AdapterView<?> parent, View view, int indexOfPosition, long id) {

        if (game.getYourTeam().toString().equals(match.getCurrentTeam().toString())) {
            Position pos = new Position(indexOfPosition / match.getBoard().getSize() + 1, indexOfPosition % match.getBoard().getSize() + 1);
            showMePieceInformation();

            /** selecting the piece */
            if (cellSelected[0] == null && !match.getFrozenPieces().isPieceAtPositionFrozen(pos)) {
                // Shows information popUp clicking on a piece
                if (!isTeleportButtonActivated) {
                    if (match.getBoard().getPieceAtPosition(pos) == null) return;

                    if (match.getBoard().getPieceAtPosition(pos).getMyTeam() != match.getCurrentTeam())
                        return;

                    cellSelected[0] = view;
                    selectedPosition[0] = pos;

                    //inserisco la prima posizione
                    move[1] = ((char) ('0'+pos.getColumn()));
                    move[2] = ((char) ('0'+pos.getRow()));

                    view.setBackgroundColor(ContextCompat.getColor(this, R.color.selectedPiece));

                    Piece p = match.getBoard().getPieceAtPosition(pos);
                    //move by default
                    if(p.possibleDirections(selectedPosition[0], match.getBoard(), match.getCurrentTeam()).size() != 0 && (!p.isCanAttack()|| p.possibleAttacks(selectedPosition[0], match.getBoard(), match.getCurrentTeam()).size() ==0) && !p.isCanUseSpells()){
                        //initialize the layoutParameters for tips and button
                        final LinearLayout.LayoutParams lp;
                        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        else lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        isMoveButtonActivated = true;
                        Vector<Position> possibleMove = p.possibleDirections(pos,match.getBoard(),match.getCurrentTeam());
                        for(Position position1:possibleMove){
                            int i = ((position1.getRow()-1)*match.getBoard().getSize())+position1.getColumn()-1;
                            LayerDrawable layerDrawable = ViewUtils.possibleDirectionCellDraw(context, resetBackground(position1),ContextCompat.getColor(context,R.color.possibleDirectionCell));
                            chessboard.getChildAt(i).setBackground(layerDrawable);
                        }
                        taskLayout.removeAllViews();
                        //initialize undo button
                        undoButton();
                        //initialize the Tips
                        ViewUtils.initializeTips(context,lp, taskLayout, R.string.moveTips);

                    }
                    //normal choice
                    else {
                        //create undo button
                        undoButton();
                        //create move button
                        if (p.possibleDirections(selectedPosition[0], match.getBoard(), match.getCurrentTeam()).size() != 0)
                            moveButton(p, pos);
                        //create attack button
                        if (p.isCanAttack() && p.possibleAttacks(selectedPosition[0], match.getBoard(), match.getCurrentTeam()).size() != 0)
                            attackButton(p, pos);
                        //create spell button
                        if (p.isCanUseSpells()) spellButton();
                    }

                } else if (isTeleportButtonActivated) { //select the position of the piece to teleport
                    if (match.getBoard().getPieceAtPosition(pos) == null) return;

                    if (match.getBoard().getPieceAtPosition(pos).getMyTeam() != match.getCurrentTeam()) return;

                    cellSelected[0] = view;
                    selectedPosition[0] = pos;

                    // inserisco la prima posizione
                    move[1] = ((char) ('0'+pos.getColumn()));
                    move[2] = ((char) ('0'+pos.getRow()));

                    view.setBackgroundColor(ContextCompat.getColor(this, R.color.teleportSelectedPiece));
                    //update the tips
                    TextView tips = (TextView) findViewById(R.id.tips);
                    if(tips!=null)
                        tips.setText(R.string.teleportTips2);
                }
            }

            /** selecting a position to ... */
            else {

                if (isMoveButtonActivated) { //move
                    isMoveButtonActivated = false;
                    cellSelected[1] = view;
                    selectedPosition[1] = pos;

                    if (match.canMovePiece(selectedPosition[0], selectedPosition[1])) {

                        match.movePiece(selectedPosition[0], selectedPosition[1]);
                        updateGraphics();

                        // inserisco la seconda posizione e l'identificativo della mossa
                        move[0] = 'M';
                        move[3] = ((char) ('0'+pos.getColumn()));
                        move[4] = ((char) ('0'+pos.getRow()));
                        game.setClickMove(move);
                        thereIsAMove = true;
                        System.out.println("thereIsAMove: " + thereIsAMove);
                        //notify();


                    } else {

                        //adapter.notifyDataSetChanged();
                        chessboard.setAdapter(adapter);
                        cellSelected[0] = null;
                        cellSelected[1] = null;
                        Toast toast = makeText(this, "Illegal Move", Toast.LENGTH_SHORT);
                        toast.show();
                        taskLayout.removeAllViews();
                        return;
                    }

                } else if (isAttackButtonActivated) {
                    isAttackButtonActivated = false;
                    cellSelected[1] = view;
                    selectedPosition[1] = pos;

                    if (match.canAttack(selectedPosition[0], selectedPosition[1])) {
                        match.attack(selectedPosition[0], selectedPosition[1]);

                        updateGraphics();

                        // inserisco la seconda posizione e l'identificativo della mossa
                        move[0] = 'A';
                        move[3] = ((char) ('0'+pos.getColumn()));
                        move[4] = ((char) ('0'+pos.getRow()));
                        game.setClickMove(move);
                        thereIsAMove = true;


                    } else {
                        chessboard.setAdapter(adapter);
                        cellSelected[0] = null;
                        cellSelected[1] = null;
                        Toast toast = makeText(this, "Illegal Attack", Toast.LENGTH_SHORT);
                        toast.show();
                        taskLayout.removeAllViews();
                        return;
                    }
                } else if (isFreezeButtonActivated) {
                    //isSpellButtonActivated=false;
                    isFreezeButtonActivated = false;
                    cellSelected[1] = view;
                    selectedPosition[1] = pos;
                    if (match.isSpellPermitted(selectedPosition[1], new Position(0, 0), Match.Spell.FREEZE)) {
                        match.castSpell(Match.Spell.FREEZE, selectedPosition[1], new Position(0, 0));

                        updateGraphics();

                        // inserisco la seconda posizione e l'identificativo della mossa
                        move[0] = 'F';
                        move[1] = ((char) ('0'+pos.getColumn()));
                        move[2] = ((char) ('0'+pos.getRow()));
                        move[3] = ((char) ('0'+0));
                        move[4] = ((char) ('0'+0));
                        game.setClickMove(move);
                        thereIsAMove = true;


                    } else {
                        chessboard.setAdapter(adapter);
                        cellSelected[0] = null;
                        cellSelected[1] = null;
                        Toast toast = makeText(this, "Illegal Freeze", Toast.LENGTH_SHORT);
                        taskLayout.removeAllViews();
                        toast.show();

                        return;
                    }
                } else if (isReviveButtonActivated) {
                    //isSpellButtonActivated=false;
                    isReviveButtonActivated = false;
                    cellSelected[1] = view;
                    selectedPosition[1] = pos;
                    if (match.isSpellPermitted(selectedPosition[1], new Position(0, 0), Match.Spell.REVIVE)) {
                        match.castSpell(Match.Spell.REVIVE, selectedPosition[1], new Position(0, 0));
                        updateGraphics();

                        // inserisco la seconda posizione e l'identificativo della mossa
                        move[0] = 'R';
                        move[1] = ((char) ('0'+pos.getColumn()));
                        move[2] = ((char) ('0'+pos.getRow()));
                        move[3] = ((char) ('0'+0));
                        move[4] = ((char) ('0'+0));
                        game.setClickMove(move);
                        thereIsAMove = true;

                    } else {
                        chessboard.setAdapter(adapter);
                        cellSelected[0] = null;
                        cellSelected[1] = null;
                        Toast toast = makeText(this, "Illegal Revive", Toast.LENGTH_SHORT);
                        taskLayout.removeAllViews();
                        toast.show();
                        return;
                    }
                } else if (isHealButtonActivated) {
                    //isSpellButtonActivated=false;
                    isHealButtonActivated = false;
                    cellSelected[1] = view;
                    selectedPosition[1] = pos;
                    if (match.isSpellPermitted(selectedPosition[1], new Position(0, 0), Match.Spell.HEAL)) {
                        match.castSpell(Match.Spell.HEAL, selectedPosition[1], new Position(0, 0));

                        updateGraphics();

                        // inserisco la seconda posizione e l'identificativo della mossa
                        move[0] = 'H';
                        move[1] = ((char) ('0'+pos.getColumn()));
                        move[2] = ((char) ('0'+pos.getRow()));
                        move[3] = ((char) ('0'+0));
                        move[4] = ((char) ('0'+0));
                        game.setClickMove(move);
                        thereIsAMove = true;


                    } else {
                        chessboard.setAdapter(adapter);
                        cellSelected[0] = null;
                        cellSelected[1] = null;
                        Toast toast = makeText(this, "Illegal Heal", Toast.LENGTH_SHORT);
                        taskLayout.removeAllViews();
                        toast.show();
                        return;
                    }
                } else if (isTeleportButtonActivated) {
                    //isSpellButtonActivated=false;
                    isTeleportButtonActivated = false;
                    cellSelected[1] = view;
                    selectedPosition[1] = pos;

                    if (match.isSpellPermitted(selectedPosition[0], selectedPosition[1], Match.Spell.TELEPORT)) {
                        match.castSpell(Match.Spell.TELEPORT, selectedPosition[0], selectedPosition[1]);
                        updateGraphics();

                        // inserisco la seconda posizione e l'identificativo della mossa
                        move[0] = 'T';
                        move[3] = ((char) ('0'+pos.getColumn()));
                        move[4] = ((char) ('0'+pos.getRow()));
                        game.setClickMove(move);
                        thereIsAMove = true;


                    } else {

                        chessboard.setAdapter(adapter);
                        cellSelected[0] = null;
                        cellSelected[1] = null;
                        Toast toast = makeText(this, "Illegal Teleport", Toast.LENGTH_SHORT);
                        taskLayout.removeAllViews();
                        toast.show();
                        return;
                    }
                }

                if (cellSelected[0] != null)
                    cellSelected[0].setBackgroundColor(resetBackground(selectedPosition[0]));
                cellSelected[0] = null;
                cellSelected[1] = null;
                taskLayout.removeAllViews();

            }
        }
    }

    /** Methods for creating the buttons*/
    private void moveButton(final Piece piece, final Position position) {
        //initialize the layoutParameters for tips and button
        final LinearLayout.LayoutParams lp;
        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        else lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //initialize the button
        final Button moveButton = new Button(context);
        moveButton.setText(R.string.move);
        moveButton.setLayoutParams(lp);
        taskLayout = (LinearLayout)findViewById(R.id.taskBar);
        taskLayout.addView(moveButton);

        moveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View argo) {
                isMoveButtonActivated = true;
                Vector<Position> possibleMove = piece.possibleDirections(position,match.getBoard(),match.getCurrentTeam());
                for(Position position1:possibleMove){
                    int i = ((position1.getRow()-1)*match.getBoard().getSize())+position1.getColumn()-1;
                    LayerDrawable layerDrawable = ViewUtils.possibleDirectionCellDraw(context, resetBackground(position1),ContextCompat.getColor(context,R.color.possibleDirectionCell));
                    chessboard.getChildAt(i).setBackground(layerDrawable);
                }
                taskLayout.removeAllViews();
                //initialize undo button
                undoButton();
                //initialize the Tips
                ViewUtils.initializeTips(context,lp, taskLayout, R.string.moveTips);


            }
        });
    }

    private void attackButton(final Piece piece, final Position position) {
        //initialize the layoutParameters for tips and button
        final LinearLayout.LayoutParams lp;
        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        else lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //initialize the button
        final Button attackButton = new Button(this);
        taskLayout = (LinearLayout)findViewById(R.id.taskBar);
        attackButton.setText(R.string.attack);
        attackButton.setLayoutParams(lp);
        taskLayout.addView(attackButton);

        attackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View argo) {
                isAttackButtonActivated = true;
                Vector<Position> possibleAttack = piece.possibleAttacks(position,match.getBoard(),match.getCurrentTeam());
                for(Position position1:possibleAttack){
                    int i = ((position1.getRow()-1)*match.getBoard().getSize())+position1.getColumn()-1;
                    chessboard.getChildAt(i).setBackgroundColor(ContextCompat.getColor(context,R.color.possibleAttackCell));
                }
                taskLayout.removeAllViews();
                //initialize undo button
                undoButton();
                //initialize the Tips
                ViewUtils.initializeTips(context,lp, taskLayout, R.string.attackTips);


            }
        });
    }

    private void spellButton() {
        //initialize the layoutParameters for tips and button
        LinearLayout.LayoutParams lp;
        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        else lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //initialize the button
        final Button spellButton = new Button(this);
        spellButton.setText(R.string.spell);
        spellButton.setLayoutParams(lp);
        taskLayout = (LinearLayout)findViewById(R.id.taskBar);
        taskLayout.addView(spellButton);

        spellButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View argo) {
                taskLayout.removeAllViews();
                isSpellButtonActivated = true;
                //initialize undo button
                undoButton();
                //display only the unused spells
                for(Match.Spell s:match.getUnusedSpells()){
                    switch (s){
                        case FREEZE:
                            freezeButton();
                            break;
                        case HEAL:
                            healButton();
                            break;
                        case TELEPORT:
                            teleportButton();
                            break;
                        case REVIVE:
                            reviveButton();
                            break;
                    }
                }
            }
        });
    }

    private void freezeButton() {
        //initialize the layoutParameters for tips and button
        final LinearLayout.LayoutParams lp;
        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        else lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //initialize the button
        final Button freezeButton = new Button(this);
        freezeButton.setText(R.string.freeze);
        freezeButton.setLayoutParams(lp);
        taskLayout = (LinearLayout)findViewById(R.id.taskBar);
        taskLayout.addView(freezeButton);

        freezeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View argo) {
                isFreezeButtonActivated = true;
                taskLayout.removeAllViews();
                //initialize undo button
                undoButton();
                //initialize the Tips
                ViewUtils.initializeTips(context,lp, taskLayout, R.string.freezeTips);
            }
        });
    }

    private void healButton() {
        //initialize the layoutParameters for tips and button
        final LinearLayout.LayoutParams lp;
        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        else lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //initialize the button
        final Button healButton = new Button(this);
        taskLayout = (LinearLayout)findViewById(R.id.taskBar);
        healButton.setText(R.string.heal);
        healButton.setLayoutParams(lp);
        taskLayout.addView(healButton);

        healButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View argo) {
                isHealButtonActivated = true;
                taskLayout.removeAllViews();
                //initialize undo button
                undoButton();
                //initialize the Tips
                ViewUtils.initializeTips(context,lp, taskLayout, R.string.healTips);
            }
        });
    }

    private void teleportButton() {
        //initialize the layoutParameters for tips and button
        final LinearLayout.LayoutParams lp;
        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        else lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //initialize the button
        final Button teleportButton = new Button(this);
        taskLayout = (LinearLayout)findViewById(R.id.taskBar);
        teleportButton.setText(R.string.teleport);
        teleportButton.setLayoutParams(lp);
        taskLayout.addView(teleportButton);

        teleportButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View argo) {
                cellSelected[0].setBackgroundColor(resetBackground(selectedPosition[0]));
                cellSelected[0] = null;
                isTeleportButtonActivated = true;
                taskLayout.removeAllViews();
                //initialize undo button
                undoButton();
                //initialize the Tips
                ViewUtils.initializeTips(context,lp, taskLayout, R.string.teleportTips1);
            }
        });
    }

    private void reviveButton() {
        //initialize the layoutParameters for tips and button
        final LinearLayout.LayoutParams lp;
        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        else lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //initialize the button
        final Button reviveButton = new Button(this);
        reviveButton.setText(R.string.revive);
        reviveButton.setLayoutParams(lp);
        taskLayout = (LinearLayout)findViewById(R.id.taskBar);
        taskLayout.addView(reviveButton);

        reviveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View argo) {
                chessboard.setAdapter(adapter);
                taskLayout.removeAllViews();
                //initialize undo button
                undoButton();
                //initialize the dead pieces
                showDeadPieces();
            }
        });
    }

    private void undoButton() {
        //initialize the layoutParameters for tips and button
        LinearLayout.LayoutParams lp;
        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        else lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //initialize the button
        final ImageButton undoButton = new ImageButton(this);
        undoButton.setImageResource(R.drawable.ic_undo);
        undoButton.setLayoutParams(lp);
        taskLayout = (LinearLayout)findViewById(R.id.taskBar);
        taskLayout.addView(undoButton);

        undoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View argo) {
                if (cellSelected[0]!=null)
                    cellSelected[0].setBackgroundColor(resetBackground(selectedPosition[0]));
                isTeleportButtonActivated = false;
                cellSelected[0] = null;
                cellSelected[1] = null;
                chessboard.setAdapter(adapter);
                taskLayout.removeAllViews();
            }
        });
    }

    /**
     * Shows the current piece information. It is called when the user press long click on a piece
     */
    private void showMePieceInformation() {
        final LayoutInflater[] layoutInflater = new LayoutInflater[1];

        chessboard.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long id) {
                ImageView child = (ImageView) chessboard.getChildAt(index);
                Position pos = new Position(index/match.getBoard().getSize() +1, index%match.getBoard().getSize()+1);
                Piece piece = match.getBoard().getPieceAtPosition(pos);

                if (child.getDrawable() != null) {
                    layoutInflater[0] = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    ViewGroup container = (ViewGroup) layoutInflater[0].inflate(R.layout.activity_popup, null);

                    final PopupWindow popupWindow = new PopupWindow(container, WRAP_CONTENT, WRAP_CONTENT,true);
                    ((TextView)popupWindow.getContentView().findViewById(R.id.description)).append(piece.getClass().getSimpleName());
                    ((TextView)popupWindow.getContentView().findViewById(R.id.vitality)).append(" " + String.valueOf(piece.getCurrentVitality()));
                    ((TextView)popupWindow.getContentView().findViewById(R.id.moverange)).append(" "+String.valueOf(piece.getmoveRange()));
                    ((TextView)popupWindow.getContentView().findViewById(R.id.movedirection)).append(ViewUtils.moveDirectionAsString(piece,context));
                    ((TextView)popupWindow.getContentView().findViewById(R.id.movetype)).append(ViewUtils.moveTypeAsString(piece, context));

                    if(piece.isCanAttack()) {
                        ((TextView) popupWindow.getContentView().findViewById(R.id.attackrange)).append(" "+String.valueOf(piece.getAttackRange()));
                        ((TextView) popupWindow.getContentView().findViewById(R.id.attacktype)).append(ViewUtils.attackDirectionAsString(piece,context));
                        ((TextView) popupWindow.getContentView().findViewById(R.id.strenght)).append(" " +String.valueOf(piece.getStrength()));
                    }
                    else {
                        ((TextView) popupWindow.getContentView().findViewById(R.id.attackrange)).setVisibility(View.GONE);
                        ((TextView) popupWindow.getContentView().findViewById(R.id.attacktype)).setVisibility(View.GONE);
                        ((TextView) popupWindow.getContentView().findViewById(R.id.strenght)).append(String.valueOf(piece.getStrength()));
                    }

                    if(piece.isCanUseSpells()) {
                        ((TextView) popupWindow.getContentView().findViewById(R.id.spells)).append(ViewUtils.getUnusedSpellAsString(piece,match,context));
                    }
                    else{
                        ((TextView) popupWindow.getContentView().findViewById(R.id.spells)).setVisibility(View.GONE);

                    }

                    popupWindow.setAnimationStyle(-1);

                    //change the behavior if the screen is flipped, otherwise the popup goes out of the screen
                    if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                        popupWindow.showAsDropDown(view,0,0);
                    else popupWindow.showAtLocation(view, Gravity.CENTER_HORIZONTAL,ViewUtils.dipToPixels(context,16),ViewUtils.dipToPixels(context,16));

                    // Remove the information popUp clicking on any place of the screen
                    container.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            popupWindow.dismiss();
                            return true;
                        }
                    });
                }
                return true;
            }
        });

    }

    /**
     * When click to revive, display the dead pieces on the task bar, then let you decide where to revive a piece
     */
    private void showDeadPieces(){
        final LinearLayout.LayoutParams lp;
        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        else lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //check if there are dead pieces
        if (match.getListOfDeadPieces().size()==0)
            ViewUtils.initializeTips(context,lp, taskLayout, R.string.reviveTips1);
        else for(final Piece p:match.getListOfDeadPieces()) {
            //initialize view
            final ImageView deadPiece = new ImageView(context);
            int imageResource = context.getResources().getIdentifier(p.toString()+"_ic", "drawable", context.getPackageName());
            Drawable res = ContextCompat.getDrawable(context,imageResource);
            deadPiece.setImageDrawable(res);
            deadPiece.setLayoutParams(lp);
            taskLayout.addView(deadPiece);

            deadPiece.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View argo) {
                    isReviveButtonActivated = true;
                    Vector<Position> initialPositions = match.initialPosOfPiece(p);
                    for(Position position1:initialPositions){
                        int i = ((position1.getRow()-1)*match.getBoard().getSize())+position1.getColumn()-1;
                        chessboard.getChildAt(i).setBackgroundColor(ContextCompat.getColor(context,R.color.possibleAttackCell));
                    }
                    taskLayout.removeAllViews();
                    //initialize undo button
                    undoButton();
                    //initialize the Tips
                    ViewUtils.initializeTips(context,lp, taskLayout, R.string.reviveTips2);
                }
            });
        }
    }

    /**
     * Create the intent to the RulesActivity and start the activity.
     * @param view
     */
    public void goToRules(View view){
        Intent intent = new Intent(this, RulesActivity.class);
        intent.putExtra("Activity","Online Game");
        startActivity(intent);
    }

    /**
     * Retrieve the original background color of a cell in the board
     * @param position the cell to reset
     * @return the original color id
     */
    private int resetBackground(Position position) {
        int pos = (position.getRow()*match.getBoard().getSize())-(match.getBoard().getSize()-position.getColumn())-1;

        //special cells
        if (pos == 0 || pos == 3 || pos == 32 || pos == 35)
            return ContextCompat.getColor(context,R.color.specialCell);

        //background depending of the position
        int col = pos/6 %2;
        if (col == 0)
        {
            if (pos%2 == 0)
                return ContextCompat.getColor(context,R.color.board1);
            else
                return ContextCompat.getColor(context,R.color.board2);
        }
        else
        {
            if (pos%2 == 0)
                return ContextCompat.getColor(context,R.color.board2);
            else
                return ContextCompat.getColor(context,R.color.board1);
        }
    }

    /**
     * Update graphic of the board calling the adapter;
     * call changeTurnText and update the current team.
     */
    private void updateGraphics(){
        adapter.notifyDataSetChanged();
        chessboard.setAdapter(adapter);

        changeTurnText();
        match.updateCurrentTeam();
    }

    /**
     * Set the turnView to the right text.
     */
    private void changeTurnText() {
        if (turnView.getText().toString().compareTo(getResources().getString(R.string.white_turn)) == 0) {
            turnView.setText(getResources().getString(R.string.black_turn));
        }
        else {
            turnView.setText(getResources().getString(R.string.white_turn));
        }
    }


}



