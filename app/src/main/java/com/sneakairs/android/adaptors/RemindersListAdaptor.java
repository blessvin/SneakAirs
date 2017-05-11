package com.sneakairs.android.adaptors;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.sneakairs.android.App;
import com.sneakairs.android.R;
import com.sneakairs.android.models.ReminderGeoPoint;
import com.sneakairs.android.utils.Constants;
import com.sneakairs.android.utils.LocationUtils;

import java.util.List;
import java.util.StringTokenizer;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by sumodkulkarni on 09/05/17.
 */

public class RemindersListAdaptor extends RecyclerView.Adapter<RemindersListAdaptor.ReminderViewHolder> {

    private List<ReminderGeoPoint> list;
    private Context context;
    private Realm realm;

    public RemindersListAdaptor(List<ReminderGeoPoint> list, Context context) {
        this.list = list;
        this.context = context;
        this.realm = Realm.getDefaultInstance();
    }

    public void setList(List<ReminderGeoPoint> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder {

        ImageView deleteButton;
        TextView message;

        public ReminderViewHolder(View view) {
            super(view);
            deleteButton = (ImageView) view.findViewById(R.id.button_delete_reminder);
            message = (TextView) view.findViewById(R.id.reminder_message);
        }
    }

    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminders_list_item, parent, false);

        return new ReminderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ReminderViewHolder holder, int position) {

        final ReminderGeoPoint reminderGeoPoint = list.get(position);

        holder.message.setText(reminderGeoPoint.getMessage());

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ReminderGeoPoint reminder = realm.where(ReminderGeoPoint.class).equalTo("id", reminderGeoPoint.getId()).findFirst();

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        reminder.setDeleted(true);
                    }
                });

                for (ReminderGeoPoint buzzReminder : App.buzzRemindersList) {
                    if (buzzReminder.getId() == reminderGeoPoint.getId())
                        App.buzzRemindersList.remove(buzzReminder);
                }

                App.updateRemindersList();
                context.sendBroadcast(new Intent(Constants.REMINDERS_LIST_UPDATED_INTENT_FILTER));
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
