package com.mobileapp.polimi.maprojectg4.controller;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.mobileapp.polimi.maprojectg4.R;
import com.mobileapp.polimi.maprojectg4.model.OnlineGame;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class
WaitOnlineGameActivity extends AppCompatActivity {

    private Context context;
    private OnlineGame game = new OnlineGame();
    private boolean isBackPressed;

    /** Overridden methods */

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
        startActivity(new Intent(WaitOnlineGameActivity.this,PlayActivity.class));
        isBackPressed = true;
        //finish();
        if(game.getConnection() != null)
            game.getConnection().disconnect();
        overridePendingTransition(R.anim.slide_in2,R.anim.slide_out2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_online_game);
        //lock the screen to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        context = this;
        isBackPressed = false;

        if(savedInstanceState == null){
            new Thread(){
                public void run(){

                    boolean net = checkInternetConnection();
                    System.out.println("bool: " + net);
                    if(!net){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, " No Internet Connection ", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(WaitOnlineGameActivity.this, PlayActivity.class));
                            }
                        });
                    } else
                        createMatch();
                }
            }.start();
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /** Class methods*/

    /**
     * Create an online game by doing an http GET request using the method getMatch.
     * If the match is created open the OnlineGameActivity corresponding to the match created,
     * otherwise it asks the player if he wants to try again or leave.
     */
    private void createMatch(){

        String[] matchResp = null;
        matchResp = game.getMatch();

        if (matchResp[0].equals("200")) {
            Intent goOnline = new Intent(WaitOnlineGameActivity.this, OnlineGameActivity.class);
            goOnline.putExtra("url", matchResp[1]);
            goOnline.putExtra("color", matchResp[2]);
            startActivity(goOnline);
        } else {

            if(!isBackPressed ){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("There is no player");
                        builder.setMessage("Do you want to try again?");

                        builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(WaitOnlineGameActivity.this, PlayActivity.class));
                                dialog.dismiss();
                            }
                        });

                        builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(WaitOnlineGameActivity.this, WaitOnlineGameActivity.class));
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
            }
        }
    }

    /**
     * Controls if the internet connection is available
     * @return true if it is, false if it isn't.
     */
    private boolean checkInternetConnection() {
        try {

            HttpURLConnection connection = null;
            URL url = new URL("http://www.google.com");
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }
}



