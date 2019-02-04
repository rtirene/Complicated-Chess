package com.mobileapp.polimi.maprojectg4.controller;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileapp.polimi.maprojectg4.R;
import com.mobileapp.polimi.maprojectg4.model.ArtificialIntelligence;
import com.mobileapp.polimi.maprojectg4.model.Board;
import com.mobileapp.polimi.maprojectg4.model.FrozenPieces;
import com.mobileapp.polimi.maprojectg4.model.Match;
import com.mobileapp.polimi.maprojectg4.model.Piece;
import com.mobileapp.polimi.maprojectg4.model.Position;
import com.mobileapp.polimi.maprojectg4.repositoryDatabase.SQLiteGameRepository;
import com.mobileapp.polimi.maprojectg4.repositoryDatabase.SerializeObjects;
import com.mobileapp.polimi.maprojectg4.testUtils.TestUtils;
import com.mobileapp.polimi.maprojectg4.view.BoardAdapter;
import com.mobileapp.polimi.maprojectg4.view.ViewUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import static android.support.v7.widget.ListPopupWindow.WRAP_CONTENT;
import static android.widget.Toast.makeText;


public class GameActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String CURRENT_BOARD = "current boardString" ;
    private static final String FROZEN_PIECES = "current frozenPieces";
    private static final String UNUSED_SPELLS = "current unusedSpell";
    private static final String CURRENT_TEAM = "the current team";
    private static final String CIMITERIES = "the white and black cimiteries";
    private static final String VITALITY = "current vitality";
    private Match match;
    private TextView turnView;
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
    private Calendar c = Calendar.getInstance();
    private String nameMatchSaved;
    private SQLiteGameRepository myDb;
    private int maxTurn;
    private Match.Winner winner;
    private String callingActivity;
    private int isCpuGame;
    private String message;
    private Toast toast;
    private ProgressDialog progDialog;
    private Date date;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;
    private boolean isThinking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //lock the screen to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        date = new Date();
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        timeFormatter = new SimpleDateFormat("HH:mm:ss");
        myDb = new SQLiteGameRepository(this);
        super.onCreate(savedInstanceState);
        toast = new Toast(this);
        toast = makeText(this, "Action", Toast.LENGTH_LONG);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.match = new Match();
        context = this;
        winner = Match.Winner.NOWINNER;
        setContentView(R.layout.activity_game);
        cellSelected = new View[2];
        selectedPosition = new Position[2];
        turnView = (TextView) findViewById(R.id.turnView);

        //retrieve saved information on the game
        if (savedInstanceState != null) {
            TestUtils.updateBoard(savedInstanceState.getString(CURRENT_BOARD), savedInstanceState.getString(VITALITY), match);
            TestUtils.setFrozenFromString(savedInstanceState.getString(FROZEN_PIECES), match);
            TestUtils.setUnusedSpellFromString(savedInstanceState.getString(UNUSED_SPELLS), match);
            TestUtils.setCurrentTeamFromString(savedInstanceState.getString(CURRENT_TEAM), match);
            TestUtils.setCemeteriesFromString(savedInstanceState.getString(CIMITERIES), match);

            if (savedInstanceState.getString(CURRENT_TEAM) == "BLACK")
                changeTurnText();

        }


        callingActivity = getIntent().getExtras().getString("Activity");
        String savedTeam = null;

        if (savedInstanceState == null){
            if (callingActivity.equals("Play Activity")) {
                // Start a new game storing the default initial configuration in the database

                try {
                    firstStoreMatch();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (callingActivity.equals("Load Activity")) {
                // Retrieve all the values from a database in order to initialize the board with the values of the previously saved match

                nameMatchSaved = getIntent().getExtras().getString("Match Name");
                maxTurn = getIntent().getExtras().getInt("Max Turn");
                myDb.setTurns(maxTurn + 1);
                Cursor cursor = myDb.retrieveMatch(nameMatchSaved, true, 0);

                // Retrieve Board
                try {
                    match.setBoard((Board) SerializeObjects.getObject(cursor, "BOARD"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Retrieve Frozen Pieces
                try {
                    match.setFrozenPieces((FrozenPieces) SerializeObjects.getObject(cursor, "FROZEN_PIECES"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Retrieve Unused Spells
                String savedUnusedSpells = null;
                try {
                    if (cursor.moveToFirst()) {
                        do {
                            savedUnusedSpells = cursor.getString(cursor.getColumnIndex("UNUSED_SPELLS"));
                        } while (cursor.moveToNext());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                TestUtils.setUnusedSpellFromString(savedUnusedSpells, match);

                // Retrieve Current Team
                if (cursor.moveToFirst()) {
                    do {
                        savedTeam = cursor.getString(cursor.getColumnIndex("CURRENT_TEAM"));
                    } while (cursor.moveToNext());
                }
                if (savedTeam.equals("BLACK")) {
                    TestUtils.setCurrentTeamFromString("BLACK", match);
                    changeTurnText();
                } else if (savedTeam.equals("WHITE")) {
                    TestUtils.setCurrentTeamFromString("WHITE", match);
                }

                // Retrieve Dead Pieces
                List<Piece> savedWhiteDeadPieces = null;
                List<Piece> savedBlackDeadPieces = null;
                try {
                    savedWhiteDeadPieces = (List<Piece>) SerializeObjects.getObject(cursor, "DEAD_PIECES_WHITE");
                    savedBlackDeadPieces = (List<Piece>) SerializeObjects.getObject(cursor, "DEAD_PIECES_BLACK");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    match.setListOfDeadWhitePieces(savedWhiteDeadPieces);
                    match.setListOfDeadBlackPieces(savedBlackDeadPieces);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (callingActivity.equals("AI Activity")) {
                try {
                    firstStoreMatch();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                AlertDialog.Builder dialogTeam = new AlertDialog.Builder(this);

                dialogTeam.setPositiveButton(R.string.Black, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //The default best move to avoid useless computations
                        String action = "M1343";
                        message = ArtificialIntelligence.playCpuString(action, match, context);
                        adapter.notifyDataSetChanged();
                        chessboard.setAdapter(adapter);
                        if (!checkWinner()) {
                            changeTurnText();
                            match.updateCurrentTeam();

                        }
                        try {
                            storeCurrentMatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        toast.cancel();
                        toast = toast.makeText(context, message, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });

                dialogTeam.setNegativeButton(R.string.White, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                dialogTeam.setTitle(R.string.ChooseTeam);
                dialogTeam.setMessage(R.string.ChooseYourTeam);
                dialogTeam.create();
                dialogTeam.show();
            }
        }

        //initialize the board
        chessBoardGridView = (GridView) findViewById(R.id.chessboard);
        adapter = new BoardAdapter(context, match.getBoard(), match.getFrozenPieces());
        chessBoardGridView.setAdapter(adapter);
        chessBoardGridView.setOnItemClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            chessBoardGridView.setElevation(200);
        //set the correct dimension of the board
        LayoutParams layoutParams = chessBoardGridView.getLayoutParams();
        int size = ViewUtils.getSizeOfBoard(context);
        layoutParams.height = size;
        layoutParams.width = size;
        this.chessboard = chessBoardGridView;
        chessboard.setLongClickable(true);
        taskLayout = (LinearLayout) findViewById(R.id.taskBar);
        showMePieceInformation();
        FloatingActionButton soundButton = (FloatingActionButton) findViewById(R.id.sound);
        soundButton.setImageResource(R.drawable.stop_sound);

        if(isSoundServiceRunning(SoundService.class)){
            soundButton.setImageResource(R.drawable.start_sound);
        }
        else{
            soundButton.setImageResource(R.drawable.stop_sound);
        }
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
        if (!isThinking) {
            callingActivity = getIntent().getExtras().getString("Activity");

            if (callingActivity.equals("Play Activity") || callingActivity.equals("AI Activity")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                SeeMatchActivity.setCurrentTurn(0);
                builder.setTitle(R.string.Exit);
                builder.setMessage(R.string.saveBeforeQuit);

                builder.setPositiveButton(R.string.Save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            namingMatch();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();

                    }
                });

                builder.setNegativeButton(R.string.Quit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDb.delete(nameMatchSaved);
                        startActivity(new Intent(GameActivity.this, MainActivity.class));
                        dialog.dismiss();
                        overridePendingTransition(R.anim.slide_in2, R.anim.slide_out2);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else if (callingActivity.equals("Load Activity")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                SeeMatchActivity.setCurrentTurn(0);
                builder.setTitle(getString(R.string.Exit));
                String question = getString(R.string.sureToQuit);
                String text = question + getString(R.string.theGame) + nameMatchSaved + getString(R.string.beenSaved);
                SpannableString spannableString = new SpannableString(text);
                spannableString.setSpan(new RelativeSizeSpan(1f), question.length(), text.length(), 0);
                builder.setMessage(spannableString);

                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(GameActivity.this, MainActivity.class));
                        dialog.dismiss();
                        overridePendingTransition(R.anim.slide_in2, R.anim.slide_out2);
                    }
                });

                builder.setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else if (callingActivity.equals("AI Activity")) {
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_in2, R.anim.slide_out2);
            }
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
    public void onItemClick(final AdapterView<?> parent, View view, int indexOfPosition, long id) {

        Position pos = new Position(indexOfPosition / match.getBoard().getSize() + 1, indexOfPosition % match.getBoard().getSize() + 1);
        showMePieceInformation();


        /** selecting the piece */
        if (cellSelected[0] == null && !match.getFrozenPieces().isPieceAtPositionFrozen(pos)) {
            toast.cancel();
            // Shows information popUp clicking on a piece
            if (!isTeleportButtonActivated) {
                if (match.getBoard().getPieceAtPosition(pos) == null) return;

                if (match.getBoard().getPieceAtPosition(pos).getMyTeam() != match.getCurrentTeam())
                    return;

                cellSelected[0] = view;
                selectedPosition[0] = pos;

                view.setBackgroundColor(ContextCompat.getColor(this, R.color.selectedPiece));

                Piece p = match.getBoard().getPieceAtPosition(pos);

                //move by default
                if(p.possibleDirections(selectedPosition[0], match.getBoard(), match.getCurrentTeam()).size() != 0 && (!p.isCanAttack()|| p.possibleAttacks(selectedPosition[0], match.getBoard(), match.getCurrentTeam()).size() ==0) && !p.isCanUseSpells()){
                    //initialize the layoutParameters for tips and button
                    final LinearLayout.LayoutParams lp;
                    if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                        lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                    else lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

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

                if (match.getBoard().getPieceAtPosition(pos).getMyTeam() != match.getCurrentTeam())
                    return;

                cellSelected[0] = view;
                selectedPosition[0] = pos;
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.teleportSelectedPiece));
                //update the tips
                TextView tips = (TextView) findViewById(R.id.tips);
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
                    if(match.getBoard().getPieceAtPosition(selectedPosition[1]) == null) {
                        message = TestUtils.teamToLowerCase(match.getCurrentTeam()) +" " + getString(R.string.moved) +" "+ TestUtils.getPieceName(selectedPosition[0], match);
                        if(!callingActivity.equals("AI Actvity")) {
                            toast = makeText(this, message, toast.LENGTH_SHORT);
                            toast.show();
                        }
                        match.movePiece(selectedPosition[0], selectedPosition[1]);
                    }else{
                        String pieceToMove = TestUtils.getPieceName(selectedPosition[0], match);
                        String finalPiece = TestUtils.getPieceName(selectedPosition[1],match);
                        match.movePiece(selectedPosition[0],selectedPosition[1]);
                        if(match.getBoard().getPieceAtPosition(selectedPosition[1])!= null) {
                            if (match.getBoard().getPieceAtPosition(selectedPosition[1]).getMyTeam() == match.getCurrentTeam()) {
                                message = TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + pieceToMove + " "+getString(R.string.defeated)+" " +
                                        TestUtils.teamToLowerCase(match.getOpponentTeam()) + " " + finalPiece;
                                if(!callingActivity.equals("AI Actvity")) {
                                    toast = makeText(this, message, toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            } else if (match.getBoard().getPieceAtPosition(selectedPosition[1]).getMyTeam() == match.getOpponentTeam()) {
                                message = TestUtils.teamToLowerCase(match.getOpponentTeam()) + " " + finalPiece + " "+getString(R.string.defeated)+" " +
                                        TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + pieceToMove;
                                if(!callingActivity.equals("AI Actvity")) {
                                    toast = makeText(this, message, toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        }else {
                            message = getString(R.string.NoPieceSurvived);
                            if(!callingActivity.equals("AI Actvity")) {
                                toast = makeText(this, message, toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }

                    }

                        updateGraphics();


                } else {

                    //adapter.notifyDataSetChanged();
                    chessboard.setAdapter(adapter);
                    cellSelected[0] = null;
                    cellSelected[1] = null;
                    toast = makeText(this, R.string.illegalMove, toast.LENGTH_SHORT);
                    toast.show();
                    taskLayout.removeAllViews();
                    return;
                }

            } else if (isAttackButtonActivated) {
                isAttackButtonActivated = false;
                cellSelected[1] = view;
                selectedPosition[1] = pos;
                if (match.canAttack(selectedPosition[0], selectedPosition[1])) {
                    String attacker = TestUtils.getPieceName(selectedPosition[0], match);
                    String attackedPiece = TestUtils.getPieceName(selectedPosition[1],match);
                    match.attack(selectedPosition[0], selectedPosition[1]);
                    if(match.getBoard().getPieceAtPosition(selectedPosition[1])!=null){
                        message = TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + TestUtils.getPieceName(selectedPosition[0],match)
                                + " "+getString(R.string.attacks)+" " + TestUtils.teamToLowerCase(match.getOpponentTeam()) + " " +  TestUtils.getPieceName(selectedPosition[1],match);
                        if(!callingActivity.equals("AI Actvity")) {
                            toast = makeText(this, message, toast.LENGTH_SHORT);
                            toast.show();
                        }

                    }
                    else{

                        message = TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + attacker
                                + " "+getString(R.string.attacks)+" " + TestUtils.teamToLowerCase(match.getOpponentTeam()) + " " +  attackedPiece+
                                " "+getString(R.string.killedAfterAttack);
                        if(!callingActivity.equals("AI Actvity")) {
                            toast = makeText(this, message, toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                    updateGraphics();

                } else {
                    chessboard.setAdapter(adapter);
                    cellSelected[0] = null;
                    cellSelected[1] = null;
                    toast = makeText(this, R.string.illegalAttack, toast.LENGTH_SHORT);
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
                    message = TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + TestUtils.getPieceName(selectedPosition[0],match)
                            +" "+ getString(R.string.freezes)+" " + TestUtils.teamToLowerCase(match.getOpponentTeam()) + " " +  TestUtils.getPieceName(selectedPosition[1],match);
                    if(!callingActivity.equals("AI Actvity")) {
                        toast = makeText(this, message, toast.LENGTH_SHORT);
                        toast.show();
                    }
                    match.castSpell(Match.Spell.FREEZE, selectedPosition[1], new Position(0, 0));
                    updateGraphics();
                } else {
                    chessboard.setAdapter(adapter);
                    cellSelected[0] = null;
                    cellSelected[1] = null;
                    if(!callingActivity.equals("AI Actvity")) {
                        toast = makeText(this, R.string.illegalFreeze, toast.LENGTH_SHORT);
                        toast.show();
                    }
                    taskLayout.removeAllViews();


                    return;
                }
            } else if (isReviveButtonActivated) {
                //isSpellButtonActivated=false;
                isReviveButtonActivated = false;
                cellSelected[1] = view;
                selectedPosition[1] = pos;
                if (match.isSpellPermitted(selectedPosition[1], new Position(0, 0), Match.Spell.REVIVE)) {
                    if(match.getBoard().getPieceAtPosition(selectedPosition[1]) == null) {
                        match.castSpell(Match.Spell.REVIVE, selectedPosition[1], new Position(0,0));
                        message = TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + TestUtils.getPieceName(selectedPosition[0],match)
                                + " "+ getString(R.string.revives)+" " + TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " +  TestUtils.getPieceName(selectedPosition[1],match);
                        if(!callingActivity.equals("AI Actvity")) {
                            toast = makeText(this, message, toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }else {

                        match.castSpell(Match.Spell.REVIVE, selectedPosition[1], new Position(0,0));
                        if(match.getBoard().getPieceAtPosition(selectedPosition[1])!= null){
                            if(match.getBoard().getPieceAtPosition(selectedPosition[1]).getMyTeam() == match.getCurrentTeam()){
                                message = TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + TestUtils.getPieceName(selectedPosition[0],match)
                                        +  " "+ getString(R.string.revives)+" " + TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " +  TestUtils.getPieceName(selectedPosition[1],match)+" "+
                                        getString(R.string.combatWon);
                                if(!callingActivity.equals("AI Actvity")) {
                                    toast = makeText(this, message, toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }else if(match.getBoard().getPieceAtPosition(selectedPosition[1]).getMyTeam() == match.getOpponentTeam()){
                                message = getString(R.string.reviveDied) ;
                                if(!callingActivity.equals("AI Actvity")) {
                                    toast = makeText(this, message, toast.LENGTH_SHORT);
                                    toast.show();
                                }

                            }
                        }
                        else {
                            message = getString(R.string.reviveBothDied);
                            if(!callingActivity.equals("AI Actvity")) {
                                toast = makeText(this, message, toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    }

                    updateGraphics();


                } else {
                    chessboard.setAdapter(adapter);
                    cellSelected[0] = null;
                    cellSelected[1] = null;
                    toast = makeText(this, R.string.illegalRevive, toast.LENGTH_SHORT);
                    toast.show();
                    taskLayout.removeAllViews();
                    return;
                }
            } else if (isHealButtonActivated) {
                //isSpellButtonActivated=false;
                isHealButtonActivated = false;
                cellSelected[1] = view;
                selectedPosition[1] = pos;
                if (match.isSpellPermitted(selectedPosition[1], new Position(0, 0), Match.Spell.HEAL)) {
                    message = TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + TestUtils.getPieceName(selectedPosition[0],match)
                            + " "+getString(R.string.heals)+" " + TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " +  TestUtils.getPieceName(selectedPosition[1],match);
                    if(!callingActivity.equals("AI Actvity")) {
                        toast = makeText(this, message, toast.LENGTH_SHORT);
                        toast.show();
                    }
                    match.castSpell(Match.Spell.HEAL, selectedPosition[1], new Position(0, 0));
                    updateGraphics();
                } else {
                    chessboard.setAdapter(adapter);
                    cellSelected[0] = null;
                    cellSelected[1] = null;
                    toast = makeText(this, R.string.illegalHeal, toast.LENGTH_SHORT);
                    toast.show();
                    taskLayout.removeAllViews();
                    return;
                }
            } else if (isTeleportButtonActivated) {
                //isSpellButtonActivated=false;
                isTeleportButtonActivated = false;
                cellSelected[1] = view;
                selectedPosition[1] = pos;

                if (match.isSpellPermitted(selectedPosition[0], selectedPosition[1], Match.Spell.TELEPORT)) {
                    if(match.getBoard().getPieceAtPosition(selectedPosition[1]) == null) {
                        message = TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + getString(R.string.Mage)+" "
                                + getString(R.string.teleports) +" "+ TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + TestUtils.getPieceName(selectedPosition[0],match);
                        if(!callingActivity.equals("AI Actvity")) {
                            toast = makeText(this, message, toast.LENGTH_SHORT);
                            toast.show();
                        }
                        match.castSpell(Match.Spell.TELEPORT, selectedPosition[0], selectedPosition[1]);

                    }else {
                        String pieceToTeleport= TestUtils.getPieceName(selectedPosition[0], match);
                        String finalPiece = TestUtils.getPieceName(selectedPosition[1],match);
                        match.castSpell(Match.Spell.TELEPORT, selectedPosition[0], selectedPosition[1]);
                        if(match.getBoard().getPieceAtPosition(selectedPosition[1])!= null){
                            if(match.getBoard().getPieceAtPosition(selectedPosition[1]).getMyTeam() == match.getCurrentTeam()){
                                message = TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + getString(R.string.Mage) + " "
                                        + getString(R.string.teleported) +" "+ TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + pieceToTeleport + " " + getString(R.string.combatWon)+" "+
                                        getString(R.string.against)+" "+ TestUtils.teamToLowerCase(match.getOpponentTeam())+ " "+finalPiece ;
                                if(!callingActivity.equals("AI Actvity")) {
                                    toast = makeText(this, message, toast.LENGTH_SHORT);
                                    toast.show();
                                }

                            }else if(match.getBoard().getPieceAtPosition(selectedPosition[1]).getMyTeam() == match.getOpponentTeam()){
                                 message = TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + getString(R.string.Mage) + " "
                                        +getString(R.string.teleported) +" "+ TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + pieceToTeleport + " " + getString(R.string.combatNotWon)+ " " +
                                        " " + getString(R.string.against)+" "+ TestUtils.teamToLowerCase(match.getOpponentTeam())+ " "+finalPiece ;
                                if(!callingActivity.equals("AI Actvity")) {
                                    toast = makeText(this, message, toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        }
                        else {
                            message = TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + getString(R.string.Mage) + " "
                                    +getString(R.string.teleported) +" "+  TestUtils.teamToLowerCase(match.getCurrentTeam()) + " " + pieceToTeleport + " " + getString(R.string.duringCombat)
                                    +" "+TestUtils.teamToLowerCase(match.getOpponentTeam())+ " "+finalPiece+ " "+getString(R.string.noOneSurvived);
                            if(!callingActivity.equals("AI Actvity")) {
                                toast = makeText(this, message, toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    }

                   updateGraphics();


                } else {

                    chessboard.setAdapter(adapter);
                    cellSelected[0] = null;
                    cellSelected[1] = null;
                    toast = makeText(this, R.string.illegalTeleport, toast.LENGTH_SHORT);
                    toast.show();
                    taskLayout.removeAllViews();
                    return;
                }
            }

            if (cellSelected[0]!=null)
                cellSelected[0].setBackgroundColor(resetBackground(selectedPosition[0]));
            cellSelected[0] = null;
            cellSelected[1] = null;
            taskLayout.removeAllViews();
        }

    }

    private boolean checkWinner(){
        boolean win = false;
        winner = match.decideWinner();
        if (winner != Match.Winner.NOWINNER) {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            finish();
                            if(callingActivity.equals("Play Activity") || callingActivity.equals("Load Activity")) {
                                Intent intent = new Intent(GameActivity.this, GameActivity.class);
                                intent.putExtra("Activity", "Play Activity");
                                startActivity(intent);
                            }else if(callingActivity.equals("AI Activity")){
                                Intent intent = new Intent(GameActivity.this, GameActivity.class);
                                intent.putExtra("Activity", "AI Activity");
                                startActivity(intent);
                            }
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            startActivity(new Intent(GameActivity.this, MainActivity.class));
                            finish();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if(winner != Match.Winner.DRAW){
                builder.setMessage(winner +" "+ getString(R.string.winWantToPlayAgain)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(getString(R.string.No), dialogClickListener).show();
            }else {
                builder.setMessage(winner +" " +getString(R.string.wantToPlayAgain)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(getString(R.string.No), dialogClickListener).show();
            }
            win = true;
        }
        return win;
    }

    private void changeTurnText() {
        if (turnView.getText().toString().compareTo(getResources().getString(R.string.white_turn)) == 0) {
            turnView.setText(getResources().getString(R.string.black_turn));
        }
        else {
            turnView.setText(getResources().getString(R.string.white_turn));
        }
    }

    private void moveButton(final Piece piece, final Position position) {
        //initialize the layoutParameters for tips and button
        final LinearLayout.LayoutParams lp;
        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        else lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
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
            lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        else lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
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
            lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        else lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
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
            lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        else lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
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
            lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        else lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
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
            lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        else lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
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
            lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        else lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
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
            lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        else lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
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
                cellSelected[0] = null;
                cellSelected[1] = null;
                chessboard.setAdapter(adapter);
                taskLayout.removeAllViews();
                isTeleportButtonActivated = false;
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
            lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        else lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
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


    public void goToRules(View view){
        Intent intent = new Intent(this, RulesActivity.class);
        intent.putExtra("Activity","Game Activity");
        intent.putExtra("Match Name",nameMatchSaved);
        int maxTurn = myDb.getMaxTurn(nameMatchSaved);
        intent.putExtra("Max Turn",maxTurn);
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
     * Stores a match's turn pushing a new record in the database. It is called repeatedly at the end of each turn.
     * @throws Exception
     */
    private void storeCurrentMatch() throws Exception {

        // Serialize objects
        final byte[] boardSerialized = SerializeObjects.convertToByteStream(match.getBoard());
        final byte[] frozenPiecesSerialized = SerializeObjects.convertToByteStream(match.getFrozenPieces());
        final byte[] deadWhitePiecesSerialized = SerializeObjects.convertToByteStream(match.getListOfDeadWhitePieces());
        final byte[] deadBlackPiecesSerialized = SerializeObjects.convertToByteStream(match.getListOfDeadBlackPieces());

        // Strings
        final String unusedSpells = TestUtils.getUnusedSpellAsString(match);
        final String winnerString = winner.toString();
        String dateAsString = dateFormatter.format(date);
        String timeAsString = timeFormatter.format(date);

        // Insert in DB
        myDb.insertData(nameMatchSaved,boardSerialized,frozenPiecesSerialized,unusedSpells,match.getCurrentTeam().toString(), deadWhitePiecesSerialized, deadBlackPiecesSerialized,dateAsString,timeAsString,winnerString,isCpuGame,message);
    }

    /**
     * Allows the user to use a custom name for the match. It also manages the alert dialog and the appearance of the confirm button depending on what the user types.
     * @throws Exception
     */
    private void namingMatch() throws Exception {
        final String[] newName = {null};

        // Strings
        String dateAsString = dateFormatter.format(date);
        String timeAsString = timeFormatter.format(date);

        // Alert Dialog
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(GameActivity.this);
        alertDialog.setTitle(R.string.nameRequest);
        alertDialog.setMessage("");

        final EditText input = new EditText(GameActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        input.setText(getString(R.string.Match) + dateAsString + " " + timeAsString);
        alertDialog.setView(input);

        DialogInterface.OnClickListener confirmNaming = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                newName[0] = String.valueOf(input.getText());
                myDb.updateData(String.valueOf(input.getText()),nameMatchSaved);
                dialogInterface.dismiss();
                nameMatchSaved = String.valueOf(input.getText());
                startActivity(new Intent(GameActivity.this, MainActivity.class));
                finish();
            }
        };

        DialogInterface.OnClickListener discardNaming = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        };

        alertDialog.setPositiveButton(R.string.saveMatch, confirmNaming);
        alertDialog.setNegativeButton(R.string.backToGame, discardNaming);
        alertDialog.setCancelable(false);
        final AlertDialog dialog =  alertDialog.create();


        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                newName[0] = String.valueOf(input.getText());

                if(checkDataInDb(SQLiteGameRepository.getTableName(),String.valueOf(input.getText()))) {
                    dialog.setMessage(getString(R.string.MatchAlreadyExist));
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
                else {
                    dialog.setMessage("");
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                }

                if(String.valueOf(input.getText()).isEmpty()) {
                    String mex = getString(R.string.validName);
                    SpannableString span = new SpannableString(mex);
                    span.setSpan(new ForegroundColorSpan(R.color.redColor),0,mex.length(),0);
                    dialog.setMessage(getString(R.string.validName));
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }

                if(String.valueOf(input.getText()).length()>=26){
                    dialog.setMessage(getString(R.string.shorterName));
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        };

        input.addTextChangedListener(watcher);
        dialog.show();
    }

    /**
     * Checks if already exists a match with the same name into the database. Duplicates names are not allowed.
     * @param tableName the name of the table we want to check
     * @param fieldValue the match name we want to check
     * @return true if already exists a match called fieldValue otherwise false
     */
    public boolean checkDataInDb(String tableName, String fieldValue) {
        String query = "SELECT DISTINCT NAME FROM " + tableName;

        Cursor cursor = myDb.getWritableDatabase().rawQuery(query,null);
        while(cursor.moveToNext()){
            String element = cursor.getString(cursor.getColumnIndex("NAME"));
            if(element!=null)
                if(element.equals(fieldValue)) {
                    cursor.close();
                    return true;
                }
        }
        cursor.close();
        return false;
    }

    /**
     * Stores the match at the beginning (turn 0). It is used a match name default well-formatted: "dd/MM/yyyy - HH:mm:ss".
     * @throws Exception
     */
    private void firstStoreMatch() throws Exception {

        // Serialize objects
        final byte[] boardSerialized = SerializeObjects.convertToByteStream(match.getBoard());
        final byte[] frozenPiecesSerialized = SerializeObjects.convertToByteStream(match.getFrozenPieces());
        final byte[] deadWhitePiecesSerialized = SerializeObjects.convertToByteStream(match.getListOfDeadWhitePieces());
        final byte[] deadBlackPiecesSerialized = SerializeObjects.convertToByteStream(match.getListOfDeadBlackPieces());

        // Strings
        final String unusedSpells = TestUtils.getUnusedSpellAsString(match);
        String dateAsString = dateFormatter.format(date);
        String timeAsString = timeFormatter.format(date);
        final String winnerString = winner.toString();

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(GameActivity.this);
        alertDialog.setTitle(getString(R.string.nameRequest));
        alertDialog.setMessage("");

        final EditText input = new EditText(GameActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        nameMatchSaved =  getString(R.string.Match) + dateAsString + " " + timeAsString;

        if(callingActivity.equals("Game Activity")){
            isCpuGame = 0;
        }else if(callingActivity.equals("AI Activity")){
            isCpuGame = 1;
        }
        myDb.insertData(nameMatchSaved,boardSerialized,frozenPiecesSerialized,unusedSpells,match.getCurrentTeam().toString(), deadWhitePiecesSerialized, deadBlackPiecesSerialized,dateAsString,timeAsString,winnerString,isCpuGame,"");
    }

    public void setDatabaseMessage(String message) {
        this.message = message;
    }


    private void updateGraphics(){
        adapter.notifyDataSetChanged();
        chessboard.setAdapter(adapter);

        //check winner
        if (!checkWinner()){
            changeTurnText();
            match.updateCurrentTeam();
        }else{
            try {
                storeCurrentMatch();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            storeCurrentMatch();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(callingActivity.equals("AI Activity") || myDb.isCpuGame(nameMatchSaved)) {
            new BackgroundTask().execute();
        }



    }

    /**
     * A separate AsyncTask to call te ai without freezing the game
     */
    private class BackgroundTask extends AsyncTask<Void, Integer, String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDialog = new ProgressDialog(context);
            progDialog.setMessage(getString(R.string.AIThinking));
            progDialog.setIndeterminate(false);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setCancelable(true);
            progDialog.show();
            isThinking = true;
            progDialog.setCancelable(false);
        }


        @Override
        protected String doInBackground(Void... arg0)
        {
            //we need to perform some semplifications, otherwise it will take too much time
            //now if the teleport is used, call negamax with depth 1, otherwise call negamax with depth 2
            if(match.getUnusedSpells()[3]== Match.Spell.TELEPORT)
                return ArtificialIntelligence.secondLevelAI(match,true,1,context); //call the negamax with depth 1 and with teleport considered
            else if (match.getUnusedSpells()[3]== Match.Spell.NOSPELL)
                return ArtificialIntelligence.secondLevelAI(match,true,2,context); //call the negamax with depth 2 and no teleport considered
            else return ""; //to make an exception if the control is not correct
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String toastMessage)
        {
            super.onPostExecute(toastMessage);
            adapter.notifyDataSetChanged();
            chessboard.setAdapter(adapter);

            setDatabaseMessage(toastMessage);

            toast.cancel();
            toast = makeText(context,toastMessage,Toast.LENGTH_SHORT);
            toast.show();
            //check winner
            if (!checkWinner()) {
                changeTurnText();
                match.updateCurrentTeam();

            }
            try {
                storeCurrentMatch();
            } catch (Exception e) {
                e.printStackTrace();
            }

            progDialog.dismiss();
            isThinking=false;
        }

    }


}