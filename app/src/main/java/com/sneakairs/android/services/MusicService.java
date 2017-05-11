package com.sneakairs.android.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.LoginFilter;
import android.util.Log;

import com.sneakairs.android.App;
import com.sneakairs.android.R;
import com.sneakairs.android.utils.Constants;

/**
 * Created by sumodkulkarni on 10/05/17.
 */

public class MusicService extends Service {

    private static final String TAG = "MusicService.java";

    private MediaPlayer mediaPlayer;
    BroadcastReceiver musicBroadcastReceiver;
    boolean shouldPlayMusic = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        App.isMusicServiceRunning = true;

        mediaPlayer = new MediaPlayer().create(App.context, R.raw.song);

        if (musicBroadcastReceiver == null) {
            musicBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    Log.d(TAG, "isMediaPlayer playing at the beginning = " + mediaPlayer.isPlaying());

                    if (intent.hasExtra("bluetooth-service")) {
                        if (App.wirelessControl) toggleMusic();
                        else return;
                    } else toggleMusic();

                    Log.d(TAG, "isMediaPlayer playing at the end = " + mediaPlayer.isPlaying());
                }
            };
        }

        registerReceiver(musicBroadcastReceiver, new IntentFilter(Constants.MUSIC_UPDATE_INTENT_FILTER));

        return START_NOT_STICKY;
    }

    private void toggleMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            App.isMusicPlaying = false;
        } else {
            mediaPlayer.start();
            mediaPlayer.isLooping();
            App.isMusicPlaying = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(musicBroadcastReceiver);
        App.isMusicServiceRunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
