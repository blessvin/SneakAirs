package com.sneakairs.android.utils;

/**
 * Created by sumodkulkarni on 04/05/17.
 */

public class Constants {

    public static final String KEY_REMINDER_GEO_POINTS = "reminder-geo-points";
    public static final String KEY_NAVIGATION_GEO_POINTS = "navigation-geo-points";

    public static final String REMINDER_UPDATE_INTENT_FILTER = "reminder_update";
    public static final String MUSIC_UPDATE_INTENT_FILTER = "music-update";
    public static final String NAVIGATION_UPDATE_INTENT_FILTER = "navigation-update";

    public static final String shouldPlayMusic = "should-play-music";
    public static final String overRideMusicPlayback = "override-music";

    public static final String bluetooth_send_message = "bluetooth_send_message";

    /**
     * Messages to be sent via bluetooth in different events:
     */

    public static final String MESSAGE_EVENT_REMINGER = "";
    public static final String MESSAGE_EVENT_TURN_LEFT = "";
    public static final String MESSAGE_EVENT_TURN_RIGHT = "";
    public static final String MESSAGE_NAVIGATION_ENDED = "";


    /**
     * Some important constants
     */

    //Default range for navigation point alert in metre
    public static final int DEFAULT_NAVIGATION_RANGE = 15;

}
