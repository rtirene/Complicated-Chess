package com.mobileapp.polimi.maprojectg4.controller;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mobileapp.polimi.maprojectg4.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    /** Overridden methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set the button linked to sound
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
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        stopService(new Intent(this,SoundService.class));

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

    /**Used to start the Play Activity, it is linked to the Play Button using onClick in the xml
     * @param view
     */
    public void Play(View view){
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);



    }

    /**Used to access to the list of old games in Replay Activity, it is linked to Replay Button using onClick in the xml
     * @param view
     */
    public void Replay(View view){
        Intent intent = new Intent(this, ReplayActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);

    }

    /**Used to open the rules of the game,provided in Rules Activity,it is linked to the Rules Button using onClick in the xml
     * @param view
     */
    public void Rules(View view){
        Intent intent = new Intent(this, RulesActivity.class);
        intent.putExtra("Activity","Main Activity");
        startActivity(intent);
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
