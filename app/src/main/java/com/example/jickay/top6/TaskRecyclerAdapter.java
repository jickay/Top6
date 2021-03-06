package com.example.jickay.top6;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jickay.top6.fragment.TaskFragment;
import com.example.jickay.top6.notifications.ReminderManager;
import com.example.jickay.top6.provider.TaskProvider;
import com.example.jickay.top6.settings.SettingsActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by ViJack on 6/21/2017.
 */

public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder>
{
    private static int LEADING_DAYS = 10;

    private int WHITE_BACKGROUND = R.color.cardview_light_background;
    private int RED_BACKGROUND = R.color.colorOverdue;
    private int GREEN_BACKGROUND = R.color.colorAccent;
    private int WHITE_TEXT = R.color.cardview_light_background;
    private int BLACK_TEXT = R.color.cardview_dark_background;

    private Context context;
    private TaskFragment fragment;
    private Cursor cursor;
    private int currentId;
    private String listType;

    private ViewGroup parent;
    private MainActivity mainActivity;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor prefEditor;

    public TaskRecyclerAdapter(TaskFragment f, Context c, String type) {
        fragment = f;
        context = c;
        listType = type;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout cardView;
        RelativeLayout textLayout;
        TextView task_title;
        TextView task_date;
        TextView task_num;
        TextView task_desc;
        ProgressBar bar;
        ImageView edit_button;
        ImageView delay_button;
        ImageView complete_button;

        private ViewHolder(RelativeLayout card){
            super(card);
            cardView = card;
            textLayout = (RelativeLayout) cardView.findViewById(R.id.textLayout);

            task_title = (TextView) cardView.findViewById(R.id.task_title);
            task_date = (TextView) cardView.findViewById(R.id.task_date);
            task_num = (TextView) cardView.findViewById(R.id.task_num);
            task_desc = (TextView) cardView.findViewById(R.id.task_description);
            bar = (ProgressBar) cardView.findViewById(R.id.progress_bar);

            edit_button = (ImageView) cardView.findViewById(R.id.edit_button);
            delay_button = (ImageView) cardView.findViewById(R.id.delay_button);
            complete_button = (ImageView) cardView.findViewById(R.id.complete_button);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        RelativeLayout card = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.task_card,parent,false);
        this.parent = parent;
        mainActivity = new MainActivity();

        return new ViewHolder(card);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        cursor.moveToPosition(i);

        currentId = cursor.getInt(cursor.getColumnIndex(TaskProvider.COLUMN_TASKID));
        String title = cursor.getString(cursor.getColumnIndex(TaskProvider.COLUMN_TITLE));
        String date = cursor.getString(cursor.getColumnIndex(TaskProvider.COLUMN_DATE));
        String description = cursor.getString(cursor.getColumnIndex(TaskProvider.COLUMN_DESCRIPTION));
        int importance = cursor.getInt(cursor.getColumnIndex(TaskProvider.COLUMN_IMPORTANCE));
        int completion_today = cursor.getInt(cursor.getColumnIndex(TaskProvider.COLUMN_COMPLETION_TODAY));
        int completion_before = cursor.getInt(cursor.getColumnIndex(TaskProvider.COLUMN_COMPLETION_BEFORE));

        Log.i("Database", "Pos: " + (i + 1) + " Loading task: " + currentId + ","
                + title + "," + date + "," + description + "," + importance + "," + completion_today + "," + completion_before);

        // Set variables
        TextView taskNumView = viewHolder.task_num;
        String taskPos = Integer.toString(i + 1);
        String dateString = formatDate(date);
        int importanceColor = getImportanceColor(importance);

        // Set text for views
        viewHolder.task_title.setText(title);
        viewHolder.task_date.setText(dateString);
        viewHolder.task_num.setText(taskPos);
        viewHolder.task_desc.setText(description);

        // Set initial completion for each card
        setInitialCompletion(viewHolder,completion_today,completion_before,taskPos,date,title,importanceColor);

        // Set button listener to edit task card
        setEditCardListener(viewHolder.edit_button, i);

        // Set delay listener to push back task date
        if (fragment != null) {
            setDelayCardListener(viewHolder, viewHolder.delay_button, i, currentId);
        } else {
            viewHolder.delay_button.setVisibility(View.GONE);
            viewHolder.delay_button.setEnabled(false);
        }

        // Set completion listener for task_num view
        setCompletionListener(parent, viewHolder, viewHolder.complete_button, taskNumView, date, taskPos, title, importanceColor, i);
    }

    @Override
    public int getItemCount() {
        int count = cursor !=null ? cursor.getCount() : 0;
        return count;
    }

    private void setInitialCompletion(ViewHolder viewHolder,
                                      int completion_today, int completion_before,
                                      String taskPos, String dateString, String title,
                                      int importanceColor) {
        int completion_check = 0;
        switch (listType) {
            // For today's list only check current completion
            case "current":
                completion_check = completion_today;
                break;
            // All other cases check current and past completion
            default:
                if ((completion_today | completion_before) == 1) {
                    completion_check = 1;
                }
                break;
        }

        switch (completion_check) {
            case 1:
                setCardColors(viewHolder, "\u2714", GREEN_BACKGROUND, GREEN_BACKGROUND, WHITE_TEXT, GREEN_BACKGROUND);
                break;
            default:
                setCardColors(viewHolder, taskPos, importanceColor, WHITE_BACKGROUND, BLACK_TEXT, importanceColor);
                checkOverdue(viewHolder, dateString, taskPos, title);
                break;
        }
    }

    private void setEditCardListener(final ImageView edit_button,
                                     final int i) {
        // Listener for clicking a task in the list
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursor.moveToPosition(i);
                currentId = cursor.getInt(cursor.getColumnIndex(TaskProvider.COLUMN_TASKID));
                // Create intent
                Intent intent = new Intent(context,CreateEditTask.class);
                intent.putExtra("Intent","edit");
                // Get ID from database
                intent.putExtra("ID",currentId);
                // Start activity
                ((Activity)edit_button.getContext()).startActivityForResult(intent, 1);
            }
        });
    }

    private void setDelayCardListener(final ViewHolder viewHolder, final ImageView delay_button, final int i, final int currentId) {
        delay_button.setOnClickListener(new View.OnClickListener() {
            int year; int month; int day; String dateData;
            @Override
            public void onClick(View view) {
                // Build and inflate view with picker
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
                LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View pickerView = li.inflate(R.layout.number_picker, null);
                final NumberPicker picker = (NumberPicker) pickerView.findViewById(R.id.day_picker);
                picker.setMinValue(1);
                picker.setMaxValue(31);

                // Move to task in db and get id
                cursor.moveToPosition(i);
                // Split parts of date into appropriate integers
                dateData = cursor.getString(cursor.getColumnIndex(TaskProvider.COLUMN_DATE));
                Log.i("DelayTask","Current date of task in db is " + dateData);
                year = Integer.parseInt(dateData.split("-")[0]);
                month = Integer.parseInt(dateData.split("-")[1]);
                day = Integer.parseInt(dateData.split("-")[2]);

                // Set the dialog attributes
                builder.setTitle(R.string.delay_task)
                        // Specify the list array, the items to be selected by default (null for none),
                        // and the listener through which to receive callbacks when items are selected
                        .setView(pickerView)
                        // Set the action buttons
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                delayTaskFunction(viewHolder,picker,year,month,day,dateData,currentId);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                builder.create().show();
            }
        });
    }

    private void delayTaskFunction(ViewHolder viewHolder, NumberPicker picker,
                                   int year, int month, int day,
                                   String dateData, long currentId) {
        // Get value from number picker
        int daysToDelay = picker.getValue();
        // Check max day of month
        Calendar c = new GregorianCalendar(year,month,day);
        int daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        // Set new day value and adjust if exceeds current month
        int finalDay = day + daysToDelay;
        if (finalDay > daysInMonth) {
            finalDay -= daysInMonth;
            month += 1;
        }
        // Change values back to string
        String dayString = finalDay < 10 ? "0" + Integer.toString(finalDay) : Integer.toString(finalDay);
        String newDateData = Integer.toString(year) + "-" +
                Integer.toString(month) + "-" +
                dayString;
        Log.i("DelayTask","Old date is " + dateData + ", New date is " + newDateData);

        // Store new date string into db
        Uri delayDate = ContentUris.withAppendedId(TaskProvider.CONTENT_URI, currentId);
        ContentValues values = new ContentValues();
        values.put(TaskProvider.COLUMN_DATE, newDateData);
        context.getContentResolver().update(delayDate, values, null, null);

        // Update notifications
//        cursor.moveToPosition(currentId);
//        String titleString = cursor.getString(cursor.getColumnIndex(TaskProvider.COLUMN_TITLE));
//        int importanceColor = getImportanceColor(cursor.getInt(cursor.getColumnIndex(TaskProvider.COLUMN_IMPORTANCE)));
//        ReminderManager.setReminder(context,"warning",currentId,titleString, c, importanceColor);
//        ReminderManager.setReminder(context,"overdue",currentId,titleString, c, R.color.colorOverdue);

        // Display new date on card
        viewHolder.task_date.setText(formatDate(newDateData));
        fragment.refreshCursor();

        // Snackbar notification of delay changes
        Activity activity = (Activity) parent.getContext();
        Snackbar.make(activity.findViewById(R.id.main_activity),"Task has been delayed by " + daysToDelay + " days",Snackbar.LENGTH_SHORT).show();
    }

    private void setCompletionListener(final ViewGroup parent, final ViewHolder viewHolder,
                                       final ImageView complete_button, final TextView textView,
                                       final String date, final String taskPos, final String title,
                                       final int importanceColor, final int i) {
        // Toggle completion when number is clicked
        complete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateString = formatDate(date);
                // Set cursor to appropriate row
                cursor.moveToPosition(i);
                currentId = cursor.getInt(cursor.getColumnIndex(TaskProvider.COLUMN_TASKID));

                int doneToday = MainActivity.getDoneToday();
                if (textView.getText() != "\u2714") {
                    setCardColors(viewHolder,"\u2714",GREEN_BACKGROUND,GREEN_BACKGROUND,WHITE_TEXT,GREEN_BACKGROUND);
                    viewHolder.task_title.setText(title);

                    // Update row in database
                    setCompletionValues(currentId,true);

                    // Add to tasks done today
                    if (listType.matches("current")) {
                        MainActivity.setDoneToday(doneToday + 1);
                        MainActivity.updateDoneToday();
                    }

                    // Clear notifications
                    Intent myIntent = new Intent(context, AlarmManager.class);
                    PendingIntent.getActivity(context, currentId, myIntent, PendingIntent.FLAG_UPDATE_CURRENT).cancel();
                    NotificationManager mgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mgr.cancel(currentId);
                    Log.i("Completion","Notification for task " + currentId + " is cancelled");

                    completeSnackbar(parent,viewHolder,currentId,dateString,taskPos,title,importanceColor);
                } else {
                    setCardColors(viewHolder,taskPos,importanceColor,WHITE_BACKGROUND,BLACK_TEXT,importanceColor);
                    checkOverdue(viewHolder, date, taskPos, title);

                    // Update row in database
                    setCompletionValues(currentId,false);

                    // Subtract from tasks done today
                    if (listType.matches("current")) {
                        MainActivity.setDoneToday(doneToday - 1);
                        MainActivity.updateDoneToday();
                    }

                    Snackbar.make(parent, R.string.task_incomplete, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void completeSnackbar(final ViewGroup parent,
                                  final ViewHolder viewHolder, final int currentId,
                                  final String dateString, final String taskPos,  final String title,
                                  final int importanceColor) {
        Snackbar mySnackbar = Snackbar.make(parent, R.string.task_completed, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setCompletionValues(currentId,false);
                        MainActivity.setDoneToday(MainActivity.getDoneToday()-1);
                        MainActivity.updateDoneToday();
                        setCardColors(viewHolder,taskPos,importanceColor,WHITE_BACKGROUND,BLACK_TEXT,BLACK_TEXT);
                        checkOverdue(viewHolder, dateString, taskPos, title);
                        Snackbar.make(parent, R.string.task_incomplete, Snackbar.LENGTH_SHORT).show();
                    }
                });
        mySnackbar.show();
    }

    public void setCompletionValues(int currentId, boolean completed) {
        ContentValues values = new ContentValues();
        switch (listType) {
            case "current":
                if (completed) {
                    values.put(TaskProvider.COLUMN_COMPLETION_TODAY, 1);
                    Log.i("CompletionValue", "ID of " + currentId + " set to 1");
                } else {
                    values.put(TaskProvider.COLUMN_COMPLETION_TODAY, 0);
                    Log.i("CompletionValue", "ID of " + currentId + " set to 0");
                }
                break;
            default:
                if (completed) {
                    values.put(TaskProvider.COLUMN_COMPLETION_BEFORE, 1);
                    Log.i("CompletionValue", "ID of " + currentId + " set to 1");
                } else {
                    values.put(TaskProvider.COLUMN_COMPLETION_BEFORE, 0);
                    Log.i("CompletionValue", "ID of " + currentId + " set to 0");
                }
        }
        Uri uri = ContentUris.withAppendedId(TaskProvider.CONTENT_URI,currentId);
        parent.getContext().getContentResolver().update(uri,values,null,null);
    }

    private void setCardColors(ViewHolder viewHolder, String text,
                               int colorSide, int colorMain, int textColor, int progressColor) {
        // Set to checkmark character
        viewHolder.task_num.setText(text);

        // Set color to completion color
        viewHolder.task_num.setBackgroundColor(ContextCompat.getColor(context,colorSide));
        viewHolder.task_date.setBackgroundColor(ContextCompat.getColor(context,colorSide));
        viewHolder.bar.setBackgroundColor(ContextCompat.getColor(context,progressColor));
        viewHolder.bar.setProgress(ContextCompat.getColor(context,progressColor));

        viewHolder.textLayout.setBackgroundColor(ContextCompat.getColor(context,colorMain));
        viewHolder.task_title.setTextColor(ContextCompat.getColor(context,textColor));
        viewHolder.task_desc.setTextColor(ContextCompat.getColor(context,textColor));
    }

    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        this.cursor.moveToFirst();
        notifyDataSetChanged();
    }

    private int getImportanceColor(int importance) {
        int color = -1;
        switch (importance) {
            // No value selected
            case -1: color = R.color.colorPrimaryMed; break;
            // Importance value selected
            case 3: color = getColorPreference(context,"high"); break;
            case 2: color = getColorPreference(context,"med"); break;
            case 1: color = getColorPreference(context,"low"); break;
        }
        return color;
    }

    public static int getColorPreference(Context context, String importanceLevel) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        String colorString;
        // Get pref value for given level
        switch (importanceLevel) {
            case "high": colorString = pref.getString(SettingsActivity.COLOR_HIGH,""); break;
            case "med": colorString = pref.getString(SettingsActivity.COLOR_MED,""); break;
            case "low": colorString = pref.getString(SettingsActivity.COLOR_LOW,""); break;
            default: colorString = ""; break;
        }

        // Get color resource for pref value
        int color;
        switch (colorString) {
            case "1": color = R.color.importance_1; break;
            case "2": color = R.color.importance_2; break;
            case "3": color = R.color.importance_3; break;
            case "4": color = R.color.importance_4; break;
            case "5": color = R.color.importance_5; break;
            case "6": color = R.color.importance_6; break;
            case "7": color = R.color.importance_7; break;
            default: color = R.color.colorPrimaryMed; break;
        }

        return color;
    }

    private void checkOverdue(ViewHolder viewHolder, String dateString, String taskPos, String title) {
        // Set urgency bar and check if overdue
        int progress = getUrgency(dateString);
        if (progress >= 0) {
            viewHolder.bar.setProgress(progress);
            viewHolder.task_title.setText(title);
            // Update db to mark task as not overdue
            Uri overdue = ContentUris.withAppendedId(TaskProvider.CONTENT_URI, currentId);
            ContentValues values = new ContentValues();
            values.put(TaskProvider.COLUMN_OVERDUE, 0);
            context.getContentResolver().update(overdue, values, null, null);
        } else {
            viewHolder.bar.setProgress(100);
            setCardColors(viewHolder, taskPos, RED_BACKGROUND, RED_BACKGROUND, WHITE_TEXT, RED_BACKGROUND);
            viewHolder.task_title.setText("OVERDUE: " + title);
            // Update db to mark task as overdue
            Uri overdue = ContentUris.withAppendedId(TaskProvider.CONTENT_URI, currentId);
            ContentValues values = new ContentValues();
            values.put(TaskProvider.COLUMN_OVERDUE, 1);
            context.getContentResolver().update(overdue, values, null, null);
        }
    }

    public int getUrgency(String date) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        LEADING_DAYS = Integer.parseInt(pref.getString("progress_bar","10"));

        int progressValue = 0;
        if (!date.isEmpty()) {
            // Set up calendar objects for task and current date
            Calendar taskCalendar = Calendar.getInstance();
            taskCalendar.set(Calendar.YEAR,Integer.parseInt(date.split("-")[0]));
            taskCalendar.set(Calendar.MONTH,Integer.parseInt(date.split("-")[1]));
            taskCalendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(date.split("-")[2]));
            int taskDayOfYear = taskCalendar.get(Calendar.DAY_OF_YEAR);

            Calendar c = Calendar.getInstance();
            int currentDayOfYear = c.get(Calendar.DAY_OF_YEAR);

            // Calculate different between task day and current day
            int difference = taskDayOfYear - currentDayOfYear;

            //Set progress bar length
            if (0 <= difference && difference <= LEADING_DAYS) {
                progressValue = 100 / LEADING_DAYS * (LEADING_DAYS - difference);
            } else if (difference > LEADING_DAYS){
                progressValue = 0;
            } else {
                progressValue = -1;
            }
        }

        return progressValue;
    }

    public static String formatDate(String date) {

        int month = Integer.parseInt(date.split("-")[1]);
        String day = date.split("-")[2];

        String monthString = "";
        switch (month) {
            case 0: monthString = "Jan"; break;
            case 1: monthString = "Feb"; break;
            case 2: monthString = "Mar"; break;
            case 3: monthString = "Apr"; break;
            case 4: monthString = "May"; break;
            case 5: monthString = "Jun"; break;
            case 6: monthString = "Jul"; break;
            case 7: monthString = "Aug"; break;
            case 8: monthString = "Sep"; break;
            case 9: monthString = "Oct"; break;
            case 10: monthString = "Nov"; break;
            case 11: monthString = "Dec"; break;
        }

        return monthString + " " + day;
    }

}
