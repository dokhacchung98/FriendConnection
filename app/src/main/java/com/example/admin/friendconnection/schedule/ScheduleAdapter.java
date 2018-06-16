package com.example.admin.friendconnection.schedule;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.admin.friendconnection.R;

import java.util.ArrayList;

public class ScheduleAdapter extends ArrayAdapter<ScheduleItem> {
    private ArrayList<ScheduleItem> scheduleItems;
    private LayoutInflater layoutInflater;
    private TextView txtTitle;
    private TextView txtTime;
    private TextView txtCalendar;
    private TextView txtLocation;
    private TextView txtValue;

    public ScheduleAdapter(@NonNull Context context, @NonNull ArrayList<ScheduleItem> scheduleItems) {
        super(context, android.R.layout.simple_list_item_1, scheduleItems);
        this.scheduleItems = scheduleItems;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.item_schedule, parent, false);
        txtTitle = convertView.findViewById(R.id.txtTitle);
        txtTime = convertView.findViewById(R.id.txtTime);
        txtCalendar = convertView.findViewById(R.id.txtCalendar);
        txtLocation = convertView.findViewById(R.id.txtLocation);
        txtValue = convertView.findViewById(R.id.txtValue);

        txtTitle.setText(scheduleItems.get(position).getTitle());
        txtTime.setText(scheduleItems.get(position).getTime());
        txtCalendar.setText(scheduleItems.get(position).getCalendar());
        txtLocation.setText(scheduleItems.get(position).getLocation());
        txtValue.setText(scheduleItems.get(position).getValue());

        return convertView;
    }
}
