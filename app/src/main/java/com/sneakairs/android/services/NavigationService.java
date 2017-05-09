package com.sneakairs.android.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by sumodkulkarni on 09/05/17.
 */

public class NavigationService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
