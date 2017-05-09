package com.sneakairs.android.adaptors;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sneakairs.android.App;
import com.sneakairs.android.R;
import com.sneakairs.android.models.ReminderGeoPoint;
import com.sneakairs.android.utils.LocationUtils;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by sumodkulkarni on 09/05/17.
 */

public class RemindersListAdaptor extends RecyclerView.Adapter<RemindersListAdaptor.ReminderViewHolder> {

    private List<ReminderGeoPoint> list;
    private Context context;

    public RemindersListAdaptor(List<ReminderGeoPoint> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder {

        ImageView deleteButton;
        TextView message, distance;

        public ReminderViewHolder(View view) {
            super(view);
            deleteButton = (ImageView) view.findViewById(R.id.button_delete_reminder);
            message = (TextView) view.findViewById(R.id.reminder_message);
            distance = (TextView) view.findViewById(R.id.reminder_distance);
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

        String distance = String.valueOf(LocationUtils.calculateDistanceFromUser(App.userGeoPoint, reminderGeoPoint.getLatitude(), reminderGeoPoint.getLongitude())) + "km";
        holder.distance.setText(distance);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.remove(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
