package com.example.jickay.top6;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ViJack on 6/21/2017.
 */

public class TaskAdapter extends ArrayAdapter
{
    int LEADING_DAYS = 10;

    public TaskAdapter(MainActivity context, ArrayList<Task> tasks) {
        super(context,0,tasks);
    }
    public TaskAdapter(AllTasks context, ArrayList<Task> tasks) { super(context,0,tasks); }
    public TaskAdapter(CompletedTasks context, ArrayList<Task> tasks) { super(context,0,tasks); }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Task task = (Task) getItem(position);
        final String taskPos = Integer.toString(position + 1);

        if (convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_list_item, parent, false);
        }

        //Get task list item views
        final TextView taskNum = (TextView) convertView.findViewById(R.id.task_num);
        TextView taskDate = (TextView) convertView.findViewById(R.id.task_date);
        TextView taskTitle = (TextView) convertView.findViewById(R.id.task_title);
        ProgressBar bar = (ProgressBar) convertView.findViewById(R.id.task_urgency);

        //Set initial text for views
        if (task.getCompletion()) {
            taskNum.setText("OK");
        } else {
            taskNum.setText(taskPos);
        }
        taskDate.setText(task.getDate());
        taskTitle.setText(task.getTitle());

        //Toggle completion when number is clicked
        taskNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskNum.getText() != "OK") {
                    taskNum.setText("OK");
                    task.setCompletion(true);
                    completeSnackbar(parent,task,taskNum,taskPos);
                } else {
                    taskNum.setText(taskPos);
                    task.setCompletion(false);
                    Snackbar.make(parent, R.string.task_incomplete, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        //Calculate value for progress bar
        final Calendar c = Calendar.getInstance();
        int currentDay = c.get(Calendar.DAY_OF_MONTH);
        int taskDay = Integer.parseInt(task.getDate().split(" ")[1]);
        int difference = taskDay - currentDay;

        int daysRemaining = LEADING_DAYS-difference;

        //Set progress bar length
        if (difference <= LEADING_DAYS) {
            bar.setProgress(100/LEADING_DAYS * daysRemaining);
        }

        return convertView;
    }

    protected void completeSnackbar(final ViewGroup parent, final Task task, final TextView taskNum, final String taskPos) {
        Snackbar mySnackbar = Snackbar.make(parent, R.string.task_completed, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    task.setCompletion(false);
                    taskNum.setText(taskPos);
                    Snackbar.make(parent, R.string.task_incomplete, Snackbar.LENGTH_SHORT).show();
                }
            });
        mySnackbar.show();
    }

}
