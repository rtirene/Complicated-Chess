package com.mobileapp.polimi.maprojectg4.controller;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileapp.polimi.maprojectg4.R;
import com.mobileapp.polimi.maprojectg4.repositoryDatabase.SQLiteGameRepository;
import com.mobileapp.polimi.maprojectg4.view.ViewUtils;

import java.util.List;
import java.util.Vector;


public class LoadActivity extends AppCompatActivity {
    private Button loadButton;
    private Vector<String> listGames;
    private SQLiteGameRepository db;
    private String nameItemClicked;
    private int maxTurn;
    private Context context;
    private View lastItemClicked;

    /** Overridden methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeWithBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        //set the title bar
        setTitle(R.string.load_game);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this,R.color.specialCell)));

        context = this;
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Buttons
        loadButton = (Button) findViewById(R.id.loadButton);
        loadButton.setEnabled(false);

        // Create list
        db = new SQLiteGameRepository(this);
        listGames = db.findAllLoad();
        updateGraphics();
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
                stopService(new Intent(this, SoundService.class));
            }
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoadActivity.this,PlayActivity.class));
    }

    /**
     * Describes the functionality of the load button. It is linked with the load button using onClick in the xml.
     * @param view is the linked button
     */
    public void launchSelectedMatch(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Activity","Load Activity");
        intent.putExtra("Match Name",nameItemClicked);
        intent.putExtra("Max Turn",maxTurn);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);

    }

    /**
     * Shows a list containing: match name, match winner and type match(vs CPU or vs Human). It also manages the appearance of the load button
     */
    public void updateGraphics(){
        LinearLayout bigList = (LinearLayout)findViewById(R.id.big_list);

        for(int i =  0; i<listGames.size(); i++){

            // singleItem
            LinearLayout singleItem = new LinearLayout(context);
            singleItem.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            int margin = ViewUtils.dipToPixels(context,16);
            lp.setMargins(0,0,0,margin);
            singleItem.setLayoutParams(lp);

            // Match Name
            TextView matchName = new TextView(context);
            singleItem.addView(matchName);
            final String matchNameString = listGames.elementAt(i);
            matchName.setText(matchNameString);
            matchName.setTextColor(R.color.primaryTextDark);
            matchName.setTextSize(22);
            matchName.setTypeface(null, Typeface.BOLD);

            // horizontal layout
            LinearLayout horizontal = new LinearLayout(context);
            horizontal.setOrientation(LinearLayout.HORIZONTAL);
            horizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            // add horizontal to singleItem
            singleItem.addView(horizontal);

            // Winner Name
            LinearLayout containerWinner = new LinearLayout(context);
            containerWinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,0.5f));
            TextView winnerName = new TextView(context);
            winnerName.setText(db.getWinner(matchNameString));
            winnerName.setTextColor(R.color.primaryTextDark);
            containerWinner.addView(winnerName);
            winnerName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            horizontal.addView(containerWinner);

            // Type Name
            LinearLayout containerType = new LinearLayout(context);
            containerType.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,0.5f));
            TextView typeName = new TextView(context);
            typeName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            if(db.isCpuGame(matchNameString)) { typeName.setText("Vs AI");}
            else{typeName.setText("Vs Human");}
            typeName.setTextColor(R.color.primaryTextDark);
            containerType.addView(typeName);
            horizontal.addView(containerType);
            typeName.setGravity(Gravity.RIGHT);

            // add singleItem to listView
            bigList.addView(singleItem);

            singleItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    maxTurn = db.getMaxTurn(matchNameString);
                    loadButton.setEnabled(true);
                    if(lastItemClicked!=null) {
                        lastItemClicked.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimary));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            lastItemClicked.setElevation(0);
                        }
                    }

                    if(lastItemClicked != view) {
                        view.setBackgroundColor(ContextCompat.getColor(context, R.color.highlightCell));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            view.setElevation(10);
                        }
                    }

                    if(lastItemClicked == view) {
                        lastItemClicked = null;
                        loadButton.setEnabled(false);
                    }
                    else {
                        lastItemClicked = view;
                        nameItemClicked = matchNameString;
                    }
                }
            });
        }
    }
}
