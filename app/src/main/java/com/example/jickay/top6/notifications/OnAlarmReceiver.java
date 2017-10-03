package com.example.jickay.top6.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.jickay.top6.MainActivity;
import com.example.jickay.top6.R;
import com.example.jickay.top6.provider.TaskProvider;

/**
 * Created by User on 8/24/2017.
 */

public class OnAlarmReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        int id = (int) intent.getLongExtra(TaskProvider.COLUMN_TASKID,0);
        String title = intent.getStringExtra(TaskProvider.COLUMN_TITLE);
        int color = intent.getIntExtra(TaskProvider.COLUMN_IMPORTANCE,R.color.cardview_dark_background);
        int daysBefore = intent.getIntExtra("AlarmDaysBefore",0);

        NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent toMainActivity = new Intent (context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, toMainActivity, PendingIntent.FLAG_ONE_SHOT);

        // Default notification for early warning
        String message;
        int icon;
        // Change icon and message for overdue and empty conditions
        if (action.matches("Overdue")) {
            icon = R.drawable.ic_error_black_24dp;
            message = "TASK IS OVERDUE!";
        } else if (action.matches("Empty")) {
            icon = R.drawable.ic_format_list_bulleted_black_24dp;
            message = "Sweet! You're done all your tasks!";
            title = "Go add some more when you're ready";
        } else {
            if (daysBefore == 0) {
                message = "Task due today:";
            } else if (daysBefore == 1) {
                message = "Task due in 1 day:";
            } else {
                message = "Task due in " + Integer.toString(daysBefore) + " days:";
            }
            icon = R.drawable.ic_access_time_black_24dp;
        }

        // Build actual notification
        Notification note = new Notification.Builder(context)
                .setDefaults(0)
                .setContentTitle(message)
                .setContentText(title)
                .setSmallIcon(icon)
                .setColor(ContextCompat.getColor(context,color))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[] { 0, 100, 100, 50, 100, 50, 100, 50 })
                .setLights(Color.RED,1000,1000)
                .build();

        // Make overdue notifications sticky
        if (action.matches("Overdue")) {
            note.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        }

        nMgr.notify(id,note);
    }
}
