package com.example.admin.friendconnection.schedule;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.admin.friendconnection.R;

public class ScheduleReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("TIMETIME", "Đến r");
        String scheduleItem = intent.getStringExtra(AddScheludeActivity.DATA);
        Log.e("RECIVER", scheduleItem);
        if (scheduleItem != null) {
            Intent ii = new Intent(context, NotiScheduleActivity.class);
            ii.putExtra(AddScheludeActivity.DATA, scheduleItem);
            PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), ii,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Notification n = new Notification.Builder(context)
                    .setContentTitle("Friend Connection")
                    .setContentText("You are scheduled now")
                    .setSmallIcon(R.drawable.ic_marker_icon)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true).build();

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, n);
        } else {
            Log.e("ReCIVEr", "bị null mất tiêu chứ");
        }
    }
}
