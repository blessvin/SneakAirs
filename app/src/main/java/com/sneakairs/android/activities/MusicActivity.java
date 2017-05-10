package com.sneakairs.android.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.sneakairs.android.App;
import com.sneakairs.android.R;
import com.sneakairs.android.services.MusicService;
import com.sneakairs.android.utils.Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_music)
public class MusicActivity extends AppCompatActivity {

    private static final String TAG = "MusicActivity.java";

    @ViewById(R.id.button_play) ImageView playButton;
    @ViewById(R.id.button_skip_next) ImageView buttonSkipNext;
    @ViewById(R.id.button_skip_previous) ImageView buttonSkipPrevious;

    BroadcastReceiver musicBroadcastReceiver;

    @AfterViews
    protected void afterViews() {
        Log.d(TAG, "isMusicServiceRunning = " + App.isMusicServiceRunning);
        Log.d(TAG, "overRideMusicPlayback = " + App.overRideMusicPlayback);
        if (App.isMusicServiceRunning && App.overRideMusicPlayback) {
            Log.d(TAG, "play");
            playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pause));
        } else {
            playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
            Log.d(TAG, "pause");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (musicBroadcastReceiver == null) {
            musicBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (App.overRideMusicPlayback && intent.hasExtra(Constants.shouldPlayMusic)) {
                        setPlayButtonImage(intent.getBooleanExtra(Constants.shouldPlayMusic, false));

                    } else if (App.overRideMusicPlayback) setPlayButtonImage(true);
                    else setPlayButtonImage(false);
                }
            };
            registerReceiver(musicBroadcastReceiver, new IntentFilter(Constants.MUSIC_UPDATE_INTENT_FILTER));
        }
    }

    @Click(R.id.button_play)
    protected void toggleMusic() {

        App.overRideMusicPlayback = !App.overRideMusicPlayback;
        setPlayButtonImage(App.overRideMusicPlayback);

        Log.d(TAG, "App.override = " + App.overRideMusicPlayback);

        Intent intent = new Intent(Constants.MUSIC_UPDATE_INTENT_FILTER);
        sendBroadcast(intent);
    }

    private void setPlayButtonImage(boolean isPlaying) {
        if (isPlaying)
            playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pause));
        else playButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
    }

    @Click(R.id.button_skip_previous)
    protected void playNext() {

    }

    @Click(R.id.button_skip_next)
    protected void playPrevious() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (musicBroadcastReceiver != null)
            unregisterReceiver(musicBroadcastReceiver);
    }
}
