package com.mobileapp.polimi.maprojectg4.controller;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.mobileapp.polimi.maprojectg4.R;

import java.util.List;

public class PlayActivity extends AppCompatActivity {

    /** Overridden methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        FloatingActionButton soundButton = (FloatingActionButton) findViewById(R.id.sound);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(isSoundServiceRunning(SoundService.class)){
            soundButton.setImageResource(R.drawable.start_sound);
        }
        else{
            soundButton.setImageResource(R.drawable.stop_sound);
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PlayActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in2,R.anim.slide_out2);

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

    /**Used to start a new match in human vs.human mode, it is linked to the New Game button using onClick in the xml
     * @param view
     */
    public void newGame(View view){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Activity", "Play Activity");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);


    }

    /**Used to access to the list of saved matches, it is linked to the Saved Game button using onClick in the xml
     * @param view
     */
    public void savedGame(View view){
        Intent intent = new Intent(this, LoadActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);

    }

    /**Used to start a new match in human vs. CPU mode, it is linked to the AI button using onClick in the xml
     * @param view
     */
    public void AI(View view){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Activity","AI Activity");
        startActivity(intent);

    }

    /**Used to start a new match online, it is linked to the Online Game button using the onClick in the xml
     * @param view
     */
    public void onlineGame(View view){
        Intent intent = new Intent(this, WaitOnlineGameActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);

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
