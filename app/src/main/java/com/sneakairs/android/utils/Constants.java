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

    public static final String MESSAGE_EVENT_REMINGER = "h";
    public static final String MESSAGE_EVENT_TURN_LEFT = "l";
    public static final String MESSAGE_EVENT_TURN_RIGHT = "r";
    public static final String MESSAGE_NAVIGATION_ENDED = "e";
    public static final String MESSAGE_MUSIC_TOGGLE = "m";

    public static final String KEY_MUSIC_INTENT_DATA = "music-toggle-data";

    /**
     * Some important constants
     */

    //Default range for navigation point alert in metre
    public static final int DEFAULT_NAVIGATION_RANGE = 15;

    public static final String REMINDERS_LIST_UPDATED_INTENT_FILTER = "reminders-list-updated";

}
