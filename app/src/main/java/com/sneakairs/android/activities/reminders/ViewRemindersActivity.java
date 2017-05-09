package com.sneakairs.android.activities.reminders;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.sneakairs.android.R;
import com.sneakairs.android.adaptors.RemindersListAdaptor;
import com.sneakairs.android.models.ReminderGeoPoint;
import com.sneakairs.android.models.ReminderGeoPointList;
import com.sneakairs.android.utils.CacheUtils;
import com.sneakairs.android.utils.Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EActivity(R.layout.activity_view_reminders)
public class ViewRemindersActivity extends AppCompatActivity {

    ArrayList<ReminderGeoPoint> reminderGeoPointsList = new ArrayList<>();
    @ViewById(R.id.recycler_view_reminders) RecyclerView recyclerView;
    RemindersListAdaptor remindersListAdaptor;
    Gson gson;

    @AfterViews
    protected void afterViews() {
        gson = new Gson();

        reminderGeoPointsList = gson.fromJson(CacheUtils.get(Constants.KEY_REMINDER_GEO_POINTS), ReminderGeoPointList.class);





    }

}
