package com.sneakairs.android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.clans.fab.FloatingActionMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_start)
public class StartActivity extends AppCompatActivity {

    @ViewById(R.id.fab_menu) FloatingActionMenu fabMenu;

    @AfterViews
    protected void afterViews() {

    }

    @Click(R.id.fab_directions)
    protected void goToDirectionsActivity() {
        Intent intent = new Intent(this, DirectionsActivity_.class);
        startActivity(intent);
        fabMenu.close(true);
    }

    @Click(R.id.fab_reminder)
    protected void goToReminderActivity() {
        Intent intent = new Intent(this, ReminderActivity_.class);
        startActivity(intent);
        fabMenu.close(true);
    }
}
