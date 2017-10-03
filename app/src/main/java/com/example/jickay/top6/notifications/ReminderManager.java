package com.example.jickay.top6.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.jickay.top6.provider.TaskProvider;

import java.util.Calendar;

/**
 * Created by User on 8/24/2017.
 */

public class ReminderManager {

    private static int alarmDays = 3;
    private static String ALARM_DAYS_DEFAULT = "3";

    private static Calendar thisCalendar;
    private static Context c;
    private ReminderManager() {}

    public static void setReminder(Context context, String type,
                                   long taskId, String title, Calendar taskTime,
                                   int color) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        thisCalendar = (Calendar) taskTime.clone();
        int difference = getDifference();
        Log.i("ReminderManager","Difference is " + difference);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        alarmDays = Integer.parseInt(pref.getString("warning_days",ALARM_DAYS_DEFAULT));
        boolean warningOn = pref.getBoolean("warning_switch",true);
        boolean overdueOn = pref.getBoolean("overdue_switch",true);

        Intent intent = getIntent(type, difference, warningOn, overdueOn,
                context, taskId, title, color);

        if (type.matches("overdue")) {
            // Change thisCalendar to day after set deadline
            thisCalendar.set(Calendar.DAY_OF_YEAR, thisCalendar.get(Calendar.DAY_OF_YEAR) + 1);
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)taskId, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.set(AlarmManager.RTC, thisCalendar.getTimeInMillis(), pendingIntent);
    }

    private static Intent getIntent(String type, int difference, boolean warningOn, boolean overdueOn,
                                     Context context, long taskId, String title, int color) {
        Intent intent = null;
        switch (type) {
            case "warning":
                if (difference >= 0 && warningOn) {
                    // Set early warning notification alarm
                    intent = new Intent(context, OnAlarmReceiver.class);
                    intent.setAction("Warning");
                    intent.putExtra(TaskProvider.COLUMN_TASKID, taskId);
                    intent.putExtra(TaskProvider.COLUMN_TITLE, title);
                    intent.putExtra(TaskProvider.COLUMN_IMPORTANCE, color);
                    if (0 <= difference && difference <= alarmDays) {
                        intent.putExtra("AlarmDaysBefore", difference);
                        thisCalendar.set(Calendar.DAY_OF_MONTH, thisCalendar.get(Calendar.DAY_OF_MONTH) - difference);
                    } else if (difference > alarmDays) {
                        intent.putExtra("AlarmDaysBefore", alarmDays);
                        thisCalendar.set(Calendar.DAY_OF_MONTH, thisCalendar.get(Calendar.DAY_OF_MONTH) - alarmDays);
                    }

                    Log.i("Notification","Warning title is " + intent.getStringExtra(TaskProvider.COLUMN_TITLE));
                    Log.i("SetAlarm", "Warning alarm set to " + thisCalendar.getTime().toString());
                }
                break;
            case "overdue":
                if (overdueOn) {
                    // Set overdue notification alarm
                    intent = new Intent(context, OnAlarmReceiver.class);
                    intent.setAction("Overdue");
                    intent.putExtra(TaskProvider.COLUMN_TASKID, taskId);
                    intent.putExtra(TaskProvider.COLUMN_TITLE, title);
                    intent.putExtra(TaskProvider.COLUMN_IMPORTANCE, color);

                    Log.i("SetAlarm", "Overdue alarm set to " + thisCalendar.getTime().toString());
                }
                break;
            case "emtpy":
                intent = new Intent(context, OnAlarmReceiver.class);
                intent.setAction("Empty");
                intent.putExtra(TaskProvider.COLUMN_TASKID, taskId);
                intent.putExtra(TaskProvider.COLUMN_TITLE, title);
                intent.putExtra(TaskProvider.COLUMN_IMPORTANCE,color);

                // Set thisCalendar of notification to next day after last use
                thisCalendar.set(Calendar.DAY_OF_YEAR,thisCalendar.get(Calendar.DAY_OF_YEAR)+1);
                thisCalendar.set(Calendar.HOUR_OF_DAY,0);
                thisCalendar.set(Calendar.MINUTE,0);
                thisCalendar.set(Calendar.SECOND,0);

                Log.i("SetAlarm","Empty alarm set to " + thisCalendar.getTime().toString());
                break;
        }
        return intent;
    }

    private static int getDifference() {
        Calendar c = Calendar.getInstance();
        int today = c.get(Calendar.DAY_OF_YEAR);
        int taskDay = thisCalendar.get(Calendar.DAY_OF_YEAR);

        return taskDay - today;
    }
}
