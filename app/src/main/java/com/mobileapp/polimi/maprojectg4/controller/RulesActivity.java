package com.mobileapp.polimi.maprojectg4.controller;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.WindowManager;
import android.widget.TextView;

import com.mobileapp.polimi.maprojectg4.R;

import java.util.List;

public class RulesActivity extends AppCompatActivity {

    private String activity;
    private String nameMatch;
    private int maxTurn;

    /** Overridden methods*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        TextView mBox = (TextView) findViewById(R.id.textView);
        String rulesString = getResources().getString(R.string.completeRules);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            mBox.setText(Html.fromHtml(rulesString, Html.FROM_HTML_MODE_LEGACY));
        } else {
            mBox.setText(Html.fromHtml(rulesString));
        }
        activity = getIntent().getExtras().getString("Activity");
        nameMatch = getIntent().getExtras().getString("Match Name");
        maxTurn = getIntent().getExtras().getInt("Max Turn");

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
    public void onBackPressed(){
        //if the rules are acessed from the Main Activity
        if(activity.equals("Main Activity")){
            startActivity(new Intent(this,MainActivity.class));
        }
        //if the rules are accessed from the match human vs.human or human vs. cpu
        else if(activity.equals("Game Activity")){
            Intent intent = new Intent(this,GameActivity.class);
            intent.putExtra("Activity","Load Activity");
            intent.putExtra("Match Name",nameMatch);
            intent.putExtra("Max Turn",maxTurn);
            startActivity(intent);

        }
        //if the rules are acessed from an online game
        else if(activity.equals("Online Game")){
            super.onBackPressed();
        }
    }

}
