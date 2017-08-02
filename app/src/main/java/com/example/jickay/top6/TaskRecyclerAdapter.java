package com.example.jickay.top6;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jickay.top6.provider.TaskProvider;

import java.util.Calendar;

/**
 * Created by ViJack on 6/21/2017.
 */

public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder>
{
    private static int LEADING_DAYS = 10;
    private int PER_PAGE = 3;

    private Context context;
    private Cursor cursor;
    private int currentId;
    private String listType;

    private ViewGroup parent;

    public TaskRecyclerAdapter(Context c, String type) {
        context = c;
        listType = type;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout cardView;
        TextView task_title;
        TextView task_date;
        TextView task_num;
        TextView task_desc;
        ProgressBar bar;
        FloatingActionButton fab;

        private ViewHolder(RelativeLayout card){
            super(card);
            cardView = card;

            task_title = (TextView) cardView.findViewById(R.id.task_title);
            task_date = (TextView) cardView.findViewById(R.id.task_date);
            task_num = (TextView) cardView.findViewById(R.id.task_num);
            task_desc = (TextView) cardView.findViewById(R.id.task_description);
            bar = (ProgressBar) cardView.findViewById(R.id.progress_circle);
            fab = (FloatingActionButton) cardView.findViewById(R.id.edit_fab);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        RelativeLayout card = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.task_card,parent,false);
        this.parent = parent;

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

        // Set text for views
        TextView taskNumView = viewHolder.task_num;
        String taskPos = Integer.toString(i + 1);
        String dateString = formatDate(date);
        int importanceColor = getImportanceColor(importance);

        viewHolder.task_title.setText(title);
        viewHolder.task_date.setText(dateString);
        viewHolder.task_num.setText(taskPos);
        viewHolder.task_desc.setText(description);

        int completion_check = 0;
        switch (listType) {
            case "current":
                completion_check = completion_today;
                break;
            default:
                if ((completion_today | completion_before) == 1) {
                    completion_check = 1;
                }
                break;
        }

        if (completion_check == 1) {
            setTaskNumView(taskNumView, viewHolder, "\u2714", R.color.colorAccent);
        } else {
            setTaskNumView(taskNumView, viewHolder, taskPos, importanceColor);
        }

        // Set urgency bar
        int progress = getUrgency(dateString);
        viewHolder.bar.setProgress(progress);

        // Set completion listener for task_num view
        setCompletionListener(parent, viewHolder, taskNumView, taskPos, importanceColor, i);

        // Set button listener to edit task card
        editCardListener(viewHolder.fab, i);
    }

    @Override
    public int getItemCount() {
        int count = cursor !=null ? cursor.getCount() : 0;
        return count;
    }

    private void editCardListener(final FloatingActionButton fab,
                                  final int i) {
        // Listener for clicking a task in the list
        fab.setOnClickListener(new View.OnClickListener() {
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
                ((Activity)fab.getContext()).startActivityForResult(intent, 1);
            }
        });
    }

    private void setCompletionListener(final ViewGroup parent,
                                       final ViewHolder viewHolder,
                                       final TextView textView,
                                       final String taskPos,
                                       final int color,
                                       final int i) {
        // Toggle completion when number is clicked
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set cursor to appropriate row
                cursor.moveToPosition(i);
                currentId = cursor.getInt(cursor.getColumnIndex(TaskProvider.COLUMN_TASKID));

                int doneToday = MainActivity.getDoneToday();
                if (textView.getText() != "\u2714") {
                    setTaskNumView(textView,viewHolder,"\u2714",R.color.colorAccent);

                    // Update row in database
                    setCompletion(currentId,true);

                    // Add to tasks done today
                    if (listType.matches("current")) {
                        MainActivity.setDoneToday(doneToday + 1);
                        MainActivity.updateDoneToday();
                    }

                    completeSnackbar(parent,viewHolder,currentId,textView,taskPos,color);
                } else {
                    setTaskNumView(textView,viewHolder,taskPos,color);

                    // Update row in database
                    setCompletion(currentId,false);

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
                                  final TextView textView, final String taskPos, final int color) {
        Snackbar mySnackbar = Snackbar.make(parent, R.string.task_completed, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setCompletion(currentId,false);
                        MainActivity.setDoneToday(MainActivity.getDoneToday()-1);
                        MainActivity.updateDoneToday();
                        setTaskNumView(textView,viewHolder,taskPos,color);
                        Snackbar.make(parent, R.string.task_incomplete, Snackbar.LENGTH_SHORT).show();
                    }
                });
        mySnackbar.show();
    }

    private void setCompletion(int currentId, boolean completed) {
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

    public void setTaskNumView (TextView textView, ViewHolder viewHolder,
                               String text, int color) {
        // Set to checkmark character
        textView.setText(text);

        // Set color to completion color
        viewHolder.task_num.setBackgroundColor(ContextCompat.getColor(context,color));
        viewHolder.task_date.setBackgroundColor(ContextCompat.getColor(context,color));
        viewHolder.bar.setBackgroundColor(ContextCompat.getColor(context,color));
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
            case 1: color = R.color.importance_low; break;
            case 2: color = R.color.importance_med; break;
            case 3: color = R.color.importance_high; break;
        }
        return color;
    }

    public static int getUrgency(String date) {
        int progressValue = 0;

        if (!date.isEmpty()) {
            int taskDay = Integer.parseInt(date.split(" ")[1]);
            //Calculate value for progress bar
            Calendar c = Calendar.getInstance();
            int currentDay = c.get(Calendar.DAY_OF_MONTH);
            int daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);

            int difference;
            if (taskDay >= currentDay) {
                difference = taskDay - currentDay;
            } else {
                int remainingDays = daysInMonth - currentDay;
                difference = taskDay + remainingDays;
            }

            int daysRemaining = LEADING_DAYS - difference;

            //Set progress bar length
            if (difference <= LEADING_DAYS) {
                progressValue = 100 / LEADING_DAYS * daysRemaining;
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
