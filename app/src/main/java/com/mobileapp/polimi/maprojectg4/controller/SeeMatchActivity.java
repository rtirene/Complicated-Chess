package com.mobileapp.polimi.maprojectg4.controller;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileapp.polimi.maprojectg4.R;
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

import java.util.Calendar;
import java.util.List;

import static android.support.v7.widget.ListPopupWindow.WRAP_CONTENT;

public class SeeMatchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String CURRENT_BOARD = "current boardString";
    private static final String FROZEN_PIECES = "current frozenPieces";
    private static final String UNUSED_SPELLS = "current unusedSpell";
    private static final String CURRENT_TEAM = "the current team";
    private static final String CIMITERIES = "the white and black cimiteries";
    private static final String VITALITY = "current vitality";
    private static final String CURRENT_TURN = "current turn";
    private static final String WAS_ROTATED = "";
    private Match match;
    private TextView turnView;
    private GridView chessboard;
    private BoardAdapter adapter;
    private Context context;
    private View[] cellSelected;
    private Position[] selectedPosition;
    private GridView chessBoardGridView;
    private Calendar c = Calendar.getInstance();
    private String nameMatchSaved;
    private SQLiteGameRepository myDb;
    private int maxTurn;
    private ImageButton forwardButton;
    private ImageButton backwardButton;
    private static int currentTurn;
    private String winner;
    private Toast toast;

    /** Overridden methods*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context=this;
        myDb = new SQLiteGameRepository(this);
        super.onCreate(savedInstanceState);
        toast = new Toast(this);
        toast= Toast.makeText(this,"hello",Toast.LENGTH_LONG);
        // eliminate the status bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setTheme(R.style.AppThemeWithBar);
            //set the bar
            setTitle(getString(R.string.replay_doing));
            ActionBar bar = getSupportActionBar();
            bar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.board1)));
        }


        this.match = new Match();
        context = this;
        setContentView(R.layout.activity_see_match);
        cellSelected = new View[2];
        selectedPosition = new Position[2];
        turnView = (TextView) findViewById(R.id.turnView);
        forwardButton = (ImageButton)findViewById(R.id.forwardButton);
        backwardButton = (ImageButton)findViewById(R.id.backwardButton);

        // Check if the game is new or previously saved.
        String callingButton = getIntent().getExtras().getString("Calling Button");
        String savedTeam = null;


        if(savedInstanceState == null){
            if(callingButton!=null) {
                if (callingButton.equals("Forward")) {
                    currentTurn++;
                } else if (callingButton.equals("Backward")) {
                    currentTurn--;
                }
            }
        }

        //retrieve saved information on the game
//        if (savedInstanceState != null) {
//            TestUtils.updateBoard(savedInstanceState.getString(CURRENT_BOARD),savedInstanceState.getString(VITALITY), match);
//            TestUtils.setFrozenFromString(savedInstanceState.getString(FROZEN_PIECES), match);
//            TestUtils.setUnusedSpellFromString(savedInstanceState.getString(UNUSED_SPELLS), match);
//            TestUtils.setCurrentTeamFromString(savedInstanceState.getString(CURRENT_TEAM), match);
//            TestUtils.setCemeteriesFromString(savedInstanceState.getString(CIMITERIES), match);
//            currentTurn = savedInstanceState.getInt(CURRENT_TURN);
//
//            if (savedInstanceState.getString(CURRENT_TEAM) == "BLACK")
//                changeTurnText();
//        }
//        else {


            winner = getIntent().getExtras().getString("Winner");
            nameMatchSaved = getIntent().getExtras().getString("Match Name");
            maxTurn = myDb.getMaxTurn(nameMatchSaved);
            Cursor cursor = myDb.retrieveMatch(nameMatchSaved, false, currentTurn);
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
//        }

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

        ImageButton backward = (ImageButton) findViewById(R.id.backwardButton);
        ImageButton forward = (ImageButton) findViewById(R.id.forwardButton);
        if (currentTurn==0) {
            backward.setEnabled(false);
            backward.setVisibility(View.GONE);
        }

        String message = myDb.getAction(nameMatchSaved,currentTurn);
        if(currentTurn>0 && savedInstanceState==null) {
            toast = Toast.makeText(this, message, toast.LENGTH_SHORT);
            toast.show();
        }

        if (currentTurn==maxTurn){
            if (winner!=null && winner.equals("NOWINNER")) {
                forward.setImageResource(R.drawable.ic_play);
                toast.cancel();
                toast = Toast.makeText(this, R.string.playAgain, Toast.LENGTH_SHORT);
                toast.show();
            }
            else{
                forward.setEnabled(false);
                forward.setVisibility(View.GONE);
            }
        }

        showMePieceInformation();

        FloatingActionButton soundButton = (FloatingActionButton) findViewById(R.id.sound);
        if(isSoundServiceRunning(SoundService.class)){
            soundButton.setImageResource(R.drawable.start_sound);
        }
        else{
            soundButton.setImageResource(R.drawable.stop_sound);
        }

    }

    @Override
    protected void onPause() {
        Context context = getApplicationContext();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        //stops the music in background only when the activity is interrupted because the user has left the applicaion
        //pressing the home button
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

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onBackPressed() {
        currentTurn = 0;
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in2,R.anim.slide_out2);
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

    /** Class methods*/

    private void changeTurnText() {
        if (turnView.getText().toString().compareTo(getResources().getString(R.string.white_turn)) == 0) {
            turnView.setText(getResources().getString(R.string.black_turn));
        }
        else {
            turnView.setText(getResources().getString(R.string.white_turn));
        }
    }

    /**
     * Describes the functionality of the forward button. It is linked with the forward button using onClick in the xml.
     * @param view the linked button
     */
    public void goToNextTurn(View view) {
        int maxTurn = myDb.getMaxTurn(nameMatchSaved);
        if(toast != null){
            toast.cancel();
        }
        if(currentTurn<maxTurn) {
            Intent intent = new Intent(this, SeeMatchActivity.class);
            intent.putExtra("Calling Button", "Forward");
            intent.putExtra("Match Name", nameMatchSaved);
            intent.putExtra("Winner",winner);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
        else{
                goToMatch();
        }
    }

    /**
     * Describes the functionality of the backward button. It is linked with the backward button using onClick in the xml.
     * @param view the linked button
     */
    public void goToPreviousTurn(View view) {
        if(toast !=null){
            toast.cancel();
        }
            Intent intent = new Intent(this, SeeMatchActivity.class);
            intent.putExtra("Calling Button","Backward");
            intent.putExtra("Match Name",nameMatchSaved);
            intent.putExtra("Winner",winner);
            startActivity(intent);

        overridePendingTransition(0, 0);
    }

    /**
     * Describes the functionality of the forward button in the last turn. It change functionality allowing the user to continue the game.
     */
    public void goToMatch() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Activity","Load Activity");
        intent.putExtra("Match Name",nameMatchSaved);
        intent.putExtra("Max Turn",maxTurn);
        toast.cancel();
        startActivity(intent);

    }

    /**
     * Updates the current turn. It is called every time the user click on backward or forward buttons.
     * @param currentTurn is the value of the current turn
     */
    public static void setCurrentTurn(int currentTurn) {
        SeeMatchActivity.currentTurn = currentTurn;
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

}