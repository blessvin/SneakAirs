package com.sneakairs.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.sneakairs.android.App;

import static com.sneakairs.android.App.context;


/**
 * Created by sumodkulkarni on 03/04/17.
 */

public class CacheUtils {
    static SharedPreferences sharedpreferences;

    public static final String MyPREFERENCES = "MyPrefs";

    static {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    public static void set(Context context, String key, String data) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, data);
        editor.apply();
    }


    public static String get(String key) {
        return sharedpreferences.getString(key, null);
    }

    public static void setBoolean(String key, boolean data) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(key, data);
        editor.apply();
    }
    public static boolean getBoolean(String key){
        return sharedpreferences.getBoolean(key, false);
    }

}
