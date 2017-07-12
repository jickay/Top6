package com.example.jickay.top6;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ViJack on 6/21/2017.
 */

public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder>
{
    private Context context;
    private ArrayList<Task> tasks;

    private ViewGroup parent;

    public TaskRecyclerAdapter(Context c, ArrayList<Task> t) {
        context = c;
        tasks = t;
    };

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
        Task thisTask = tasks.get(i);
        String taskPos = Integer.toString(i+1);

        viewHolder.task_title.setText(thisTask.getTitle());
        viewHolder.task_date.setText(thisTask.getDate());
        viewHolder.task_num.setText(taskPos);
        viewHolder.task_desc.setText(thisTask.getDescription());

        viewHolder.task_num.setBackgroundColor(ContextCompat.getColor(context,getImportanceColor(i)));
        viewHolder.task_date.setBackgroundColor(ContextCompat.getColor(context,getImportanceColor(i)));

        viewHolder.bar.setProgress(thisTask.getUrgency());

        setCompletionListener(parent,viewHolder,viewHolder.task_num,thisTask,taskPos,getImportanceColor(i));

        editCardListener(viewHolder.fab,i);
    }

    @Override
    public int getItemCount() {
        return MainActivity.getIncompleteTasks().size();
    }

    private void editCardListener(final FloatingActionButton fab,
                                  final int i) {
        // Listener for clicking a task in the list
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent
                Intent intent = new Intent(context,CreateEditTask.class);
                intent.putExtra("Intent","edit");
                // Pull task data
                Task task = tasks.get(i);
                Bundle b = new Bundle();
                b.putInt("num",i);
                b.putString("title", task.getTitle());
                b.putString("date", task.getDate());
                b.putString("desc", task.getDescription());
                intent.putExtra("taskData", b);
                // Start activity
                ((Activity)fab.getContext()).startActivityForResult(intent, 1);
            }
        });
    }

    private int getImportanceColor(int i) {
        int importance = tasks.get(i).getImportance();
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

    private void setCompletionListener(final ViewGroup parent,
                                       final ViewHolder viewHolder,
                                       final TextView textView,
                                       final Task task,
                                       final String taskPos,
                                       final int color) {
        //Toggle completion when number is clicked
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView.getText() != "\u2714") {
                    textView.setText("\u2714");
                    task.setCompletion(true);

                    viewHolder.task_num.setBackgroundColor(ContextCompat.getColor(context,R.color.colorAccent));
                    viewHolder.task_date.setBackgroundColor(ContextCompat.getColor(context,R.color.colorAccent));

                    completeSnackbar(parent,viewHolder,task,textView,taskPos,color);
                } else {
                    textView.setText(taskPos);
                    task.setCompletion(false);

                    viewHolder.task_num.setBackgroundColor(ContextCompat.getColor(context,color));
                    viewHolder.task_date.setBackgroundColor(ContextCompat.getColor(context,color));

                    Snackbar.make(parent, R.string.task_incomplete, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void completeSnackbar(final ViewGroup parent,
                                  final ViewHolder viewHolder,
                                  final Task task, final TextView taskNum,
                                  final String taskPos, final int color) {
        Snackbar mySnackbar = Snackbar.make(parent, R.string.task_completed, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        task.setCompletion(false);
                        taskNum.setText(taskPos);
                        viewHolder.task_num.setBackgroundColor(ContextCompat.getColor(context,color));
                        viewHolder.task_date.setBackgroundColor(ContextCompat.getColor(context,color));
                        Snackbar.make(parent, R.string.task_incomplete, Snackbar.LENGTH_SHORT).show();
                    }
                });
        mySnackbar.show();
    }

}
