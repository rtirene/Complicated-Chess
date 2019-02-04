package com.mobileapp.polimi.maprojectg4.controller;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.mobileapp.polimi.maprojectg4.R;

/**
 * SoundService manages the music in background.
 */
public class SoundService extends Service implements MediaPlayer.OnPreparedListener {
    private static final String TAG = null;
    MediaPlayer mediaPlayerMain;
    private final IBinder mBinder = new ServiceBinder();
    private boolean isPlaying;

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public class ServiceBinder extends Binder {
        SoundService getService()
        {
            return SoundService.this;
        }
    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void onCreate() {
        super.onCreate();
        //create the sound track by keeping it from the resources
        mediaPlayerMain = MediaPlayer.create(this, R.raw.activitymaintheme);
        mediaPlayerMain.setLooping(true);
        mediaPlayerMain.setVolume(100,100);
        mediaPlayerMain.setOnPreparedListener( this);

    }

    public void onPrepared(MediaPlayer player) {
        player.start();
    }

    /**Starts the music in background when the startService(Intent)is called in an activity
     * @param intent
     * @param flags
     * @param startId
     * @return the START_NOT_STICKY mode
     */
    public int onStartCommand(Intent intent,int flags, int startId){
        mediaPlayerMain.start();
        isPlaying = true;
        return  START_NOT_STICKY;

    }

    public void onStop() {
        isPlaying = false;

    }
    public void onPause() {
    }

    @Override
    public void onDestroy() {
        mediaPlayerMain.stop();
        isPlaying = false;
    }
}
