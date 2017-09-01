package com.example.jickay.top6.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.jickay.top6.provider.TaskProvider;

import java.util.Calendar;

/**
 * Created by User on 8/24/2017.
 */

public class ReminderManager {

    private ReminderManager() {}

    public static void setReminder(Context context, String type, long taskId, String title, Calendar time) {

        Log.i("ReminderManager","Alarm set to notify at " + time.getTime());

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, OnAlarmReceiver.class);
        switch (type) {
            case "task":
                // Set early warning notification alarm
                intent.setAction("Warning");
                intent.putExtra(TaskProvider.COLUMN_TASKID, taskId);
                intent.putExtra(TaskProvider.COLUMN_TITLE, title);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                alarmManager.set(AlarmManager.RTC, time.getTimeInMillis(), pendingIntent);

                // Set overdue notification alarm
                intent.setAction("Overdue");
                pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                time.set(Calendar.DAY_OF_YEAR, time.get(Calendar.DAY_OF_YEAR) + 1); // Change time to day after set deadline
                alarmManager.set(AlarmManager.RTC, time.getTimeInMillis(), pendingIntent);
                break;
            case "emtpy":
                intent.setAction("Empty");
                intent.putExtra(TaskProvider.COLUMN_TASKID, taskId);
                intent.putExtra(TaskProvider.COLUMN_TITLE, title);

                // Set time of notification to next day after last use
                time.set(Calendar.DAY_OF_YEAR,time.get(Calendar.DAY_OF_YEAR)+1);
                time.set(Calendar.HOUR_OF_DAY,0);
                time.set(Calendar.MINUTE,0);
                time.set(Calendar.SECOND,0);

                pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                alarmManager.set(AlarmManager.RTC, time.getTimeInMillis(), pendingIntent);
        }
    }
}
