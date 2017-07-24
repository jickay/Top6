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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jickay.top6.provider.TaskProvider;

import org.w3c.dom.Text;

import java.util.Calendar;

/**
 * Created by ViJack on 6/21/2017.
 */

public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder>
{
    private static int LEADING_DAYS = 10;
    private int PER_PAGE = 3;

    private Context context;
    private Cursor c;
    private int currentId;

    private ViewGroup parent;

    public TaskRecyclerAdapter(Context c) {
        context = c;
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
//        if (i <= PER_PAGE+10) {
            String taskPos = Integer.toString(i + 1);
            c.moveToPosition(i);

            currentId = c.getInt(c.getColumnIndex(TaskProvider.COLUMN_TASKID));
            String title = c.getString(c.getColumnIndex(TaskProvider.COLUMN_TITLE));
            String date = c.getString(c.getColumnIndex(TaskProvider.COLUMN_DATE));
            String description = c.getString(c.getColumnIndex(TaskProvider.COLUMN_DESCRIPTION));
            int importance = c.getInt(c.getColumnIndex(TaskProvider.COLUMN_IMPORTANCE));
            int completion = c.getInt(c.getColumnIndex(TaskProvider.COLUMN_COMPLETION));

            Log.i("Database", "Pos: " + (i + 1) + " Loading task: " + currentId + ","
                    + title + "," + date + "," + description + "," + importance + "," + completion);

            // Set text for views
            TextView textView = viewHolder.task_num;

            viewHolder.task_title.setText(title);
            viewHolder.task_date.setText(date);
            viewHolder.task_num.setText(taskPos);
            viewHolder.task_desc.setText(description);

            if (completion == 1) {
                viewHolder.task_num.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                viewHolder.task_date.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                viewHolder.bar.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                textView.setText("\u2714");
            } else {
                viewHolder.task_num.setBackgroundColor(ContextCompat.getColor(context, getImportanceColor(importance)));
                viewHolder.task_date.setBackgroundColor(ContextCompat.getColor(context, getImportanceColor(importance)));
                viewHolder.bar.setBackgroundColor(ContextCompat.getColor(context, getImportanceColor(importance)));
                textView.setText(Integer.toString(i+1));
            }

            int progress = getUrgency(date);
            viewHolder.bar.setProgress(progress);

            setCompletionListener(parent, viewHolder, textView, taskPos, getImportanceColor(importance), i);

            editCardListener(viewHolder.fab, i);
//        }
    }

    @Override
    public int getItemCount() {
        int count = c!=null ? c.getCount() : 0;
        return count;
    }

    private void editCardListener(final FloatingActionButton fab,
                                  final int i) {
        // Listener for clicking a task in the list
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.moveToPosition(i);
                currentId = c.getInt(c.getColumnIndex(TaskProvider.COLUMN_TASKID));
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
                c.moveToPosition(i);
                currentId = c.getInt(c.getColumnIndex(TaskProvider.COLUMN_TASKID));

                if (textView.getText() != "\u2714") {
                    // Set to checkmark character
                    textView.setText("\u2714");

                    // Set color to completion color
                    viewHolder.task_num.setBackgroundColor(ContextCompat.getColor(context,R.color.colorAccent));
                    viewHolder.task_date.setBackgroundColor(ContextCompat.getColor(context,R.color.colorAccent));
                    viewHolder.bar.setBackgroundColor(ContextCompat.getColor(context,R.color.colorAccent));

                    // Update row in database
                    setCompletion(currentId,true);

                    completeSnackbar(parent,viewHolder,currentId,textView,taskPos,color);
                } else {
                    // Set to original rank number
                    textView.setText(taskPos);

                    // Set to original importance color
                    viewHolder.task_num.setBackgroundColor(ContextCompat.getColor(context,color));
                    viewHolder.task_date.setBackgroundColor(ContextCompat.getColor(context,color));
                    viewHolder.bar.setBackgroundColor(ContextCompat.getColor(context,color));

                    // Update row in database
                    setCompletion(currentId,false);

                    Snackbar.make(parent, R.string.task_incomplete, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void completeSnackbar(final ViewGroup parent,
                                  final ViewHolder viewHolder, final int currentId,
                                  final TextView taskNum, final String taskPos, final int color) {
        Snackbar mySnackbar = Snackbar.make(parent, R.string.task_completed, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setCompletion(currentId,false);
                        taskNum.setText(taskPos);
                        viewHolder.task_num.setBackgroundColor(ContextCompat.getColor(context,color));
                        viewHolder.task_date.setBackgroundColor(ContextCompat.getColor(context,color));
                        Snackbar.make(parent, R.string.task_incomplete, Snackbar.LENGTH_SHORT).show();
                    }
                });
        mySnackbar.show();
    }

    private void setCompletion(int currentId, boolean completed) {
        ContentValues values = new ContentValues();
        if (completed) {
            values.put(TaskProvider.COLUMN_COMPLETION, 1);
            Log.i("CompletionValue","ID of "+currentId+" set to 1");
        } else {
            values.put(TaskProvider.COLUMN_COMPLETION, 0);
            Log.i("CompletionValue","ID of "+currentId+" set to 0");
        }
        Uri uri = ContentUris.withAppendedId(TaskProvider.CONTENT_URI,currentId);
        parent.getContext().getContentResolver().update(uri,values,null,null);
    }

    public void swapCursor(Cursor cursor) {
        c = cursor;
        c.moveToFirst();
        notifyDataSetChanged();
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

}
