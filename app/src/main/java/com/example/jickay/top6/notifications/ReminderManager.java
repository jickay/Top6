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

    private static int ALARM_DAYS_BEFORE = 3;

    private static Calendar thisCalendar;

    private ReminderManager() {}

    public static void setReminder(Context context, String type,
                                   long taskId, String title, Calendar taskTime,
                                   int color) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        thisCalendar = (Calendar) taskTime.clone();
        int difference = getDifference();
        Log.i("ReminderManager","Difference is " + difference);

        Intent intent = new Intent(context, OnAlarmReceiver.class);
        PendingIntent pendingIntent;

        switch (type) {
            case "warning":
                if (difference >= 0) {
                    // Set early warning notification alarm
                    intent.setAction("Warning");
                    intent.putExtra(TaskProvider.COLUMN_TASKID, taskId);
                    intent.putExtra(TaskProvider.COLUMN_TITLE, title);
                    intent.putExtra(TaskProvider.COLUMN_IMPORTANCE, color);
                    if (0 <= difference && difference <= ALARM_DAYS_BEFORE) {
                        intent.putExtra("AlarmDaysBefore", difference);
                        thisCalendar.set(Calendar.DAY_OF_MONTH, thisCalendar.get(Calendar.DAY_OF_MONTH) - difference);
                    } else if (difference > ALARM_DAYS_BEFORE) {
                        intent.putExtra("AlarmDaysBefore", ALARM_DAYS_BEFORE);
                        thisCalendar.set(Calendar.DAY_OF_MONTH, thisCalendar.get(Calendar.DAY_OF_MONTH) - ALARM_DAYS_BEFORE);
                    }

                    pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    alarmManager.set(AlarmManager.RTC, thisCalendar.getTimeInMillis(), pendingIntent);

                    Log.i("SetAlarm", "Warning alarm set to " + thisCalendar.getTime().toString());
                }
                break;
            case "overdue":
                if (difference >= 0) {
                    // Set overdue notification alarm
                    intent.setAction("Overdue");
                    intent.putExtra(TaskProvider.COLUMN_IMPORTANCE,color);
                    pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                    thisCalendar.set(Calendar.DAY_OF_YEAR, thisCalendar.get(Calendar.DAY_OF_YEAR) + 1); // Change thisCalendar to day after set deadline
                    alarmManager.set(AlarmManager.RTC, thisCalendar.getTimeInMillis(), pendingIntent);

                    Log.i("SetAlarm", "Overdue alarm set to " + thisCalendar.getTime().toString());
                }
                break;
            case "emtpy":
                intent.setAction("Empty");
                intent.putExtra(TaskProvider.COLUMN_TASKID, taskId);
                intent.putExtra(TaskProvider.COLUMN_TITLE, title);
                intent.putExtra(TaskProvider.COLUMN_IMPORTANCE,color);

                // Set thisCalendar of notification to next day after last use
                thisCalendar.set(Calendar.DAY_OF_YEAR,thisCalendar.get(Calendar.DAY_OF_YEAR)+1);
                thisCalendar.set(Calendar.HOUR_OF_DAY,0);
                thisCalendar.set(Calendar.MINUTE,0);
                thisCalendar.set(Calendar.SECOND,0);

                pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                alarmManager.set(AlarmManager.RTC, thisCalendar.getTimeInMillis(), pendingIntent);

                Log.i("SetAlarm","Empty alarm set to " + thisCalendar.getTime().toString());
                break;
        }
    }

    private static int getDifference() {
        Calendar c = Calendar.getInstance();
        int today = c.get(Calendar.DAY_OF_YEAR);
        int taskDay = thisCalendar.get(Calendar.DAY_OF_YEAR);

        return taskDay - today;
    }
}
