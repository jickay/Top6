package com.example.jickay.top6;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ViJack on 6/21/2017.
 */

public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder>
{
    private static int LEADING_DAYS = 10;

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
            bar = (ProgressBar) cardView.findViewById(R.id.task_urgency);
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
        String taskPos = Integer.toString(i+1);
        c.moveToPosition(i);

        currentId = c.getInt(c.getColumnIndex(TaskProvider.COLUMN_TASKID));
        String title = c.getString(c.getColumnIndex(TaskProvider.COLUMN_TITLE));
        String date = c.getString(c.getColumnIndex(TaskProvider.COLUMN_DATE));
        String description = c.getString(c.getColumnIndex(TaskProvider.COLUMN_DESCRIPTION));
        int importance = c.getInt(c.getColumnIndex(TaskProvider.COLUMN_IMPORTANCE));

        Log.i("Database","Pos: "+(i+1)+" Loading task: "+currentId+","+title+","+date+","+description+","+importance);

        // Set text for views
        viewHolder.task_title.setText(title);
        viewHolder.task_date.setText(date);
        viewHolder.task_num.setText(taskPos);
        viewHolder.task_desc.setText(description);

        viewHolder.task_num.setBackgroundColor(ContextCompat.getColor(context,getImportanceColor(importance)));
        viewHolder.task_date.setBackgroundColor(ContextCompat.getColor(context,getImportanceColor(importance)));

        viewHolder.bar.setProgress(getUrgency(date));

        setCompletionListener(parent,viewHolder,viewHolder.task_num,taskPos,getImportanceColor(i));

        editCardListener(viewHolder.fab,i);
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

    private int getImportanceColor(int importance) {
        int color = -1;
        switch (importance) {
            // No value selected
            case -1: color = R.color.colorPrimaryMed; break;
            // Importance value selected
            case 1: color = R.color.importance1; break;
            case 2: color = R.color.importance2; break;
            case 3: color = R.color.importance3; break;
            case 4: color = R.color.importance4; break;
            case 5: color = R.color.importance5; break;
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

    private void setCompletionListener(final ViewGroup parent,
                                       final ViewHolder viewHolder,
                                       final TextView textView,
                                       final String taskPos,
                                       final int color) {
        //Toggle completion when number is clicked
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView.getText() != "\u2714") {
                    textView.setText("\u2714");
//                    task.setCompletion(true);

                    viewHolder.task_num.setBackgroundColor(ContextCompat.getColor(context,R.color.colorAccent));
                    viewHolder.task_date.setBackgroundColor(ContextCompat.getColor(context,R.color.colorAccent));

                    completeSnackbar(parent,viewHolder,textView,taskPos,color);
                } else {
                    textView.setText(taskPos);
//                    task.setCompletion(false);

                    viewHolder.task_num.setBackgroundColor(ContextCompat.getColor(context,color));
                    viewHolder.task_date.setBackgroundColor(ContextCompat.getColor(context,color));

                    Snackbar.make(parent, R.string.task_incomplete, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void completeSnackbar(final ViewGroup parent,
                                  final ViewHolder viewHolder,
                                  final TextView taskNum,
                                  final String taskPos, final int color) {
        Snackbar mySnackbar = Snackbar.make(parent, R.string.task_completed, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        task.setCompletion(false);
                        taskNum.setText(taskPos);
                        viewHolder.task_num.setBackgroundColor(ContextCompat.getColor(context,color));
                        viewHolder.task_date.setBackgroundColor(ContextCompat.getColor(context,color));
                        Snackbar.make(parent, R.string.task_incomplete, Snackbar.LENGTH_SHORT).show();
                    }
                });
        mySnackbar.show();
    }

    public void swapCursor(Cursor cursor) {
        c = cursor;
        c.moveToFirst();
        notifyDataSetChanged();
    }

}
