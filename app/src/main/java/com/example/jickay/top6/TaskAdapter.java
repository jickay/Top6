package com.example.jickay.top6;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ViJack on 6/21/2017.
 */

public class TaskAdapter extends BaseExpandableListAdapter
{
    int LEADING_DAYS = 10;

    Context context;
    ArrayList<Task> tasks;

    public TaskAdapter(Context c, ArrayList<Task> t) {
        context = c;
        tasks = t;
    }

    @Override
    public View getChildView (int groupPosition, int childPosition,
                              boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView==null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_description, parent, false);
        }

        Task task = (Task) getGroup(groupPosition);
        String description = task.getDescription();

        TextView taskDesc = (TextView) convertView.findViewById(R.id.task_description);
        if (!description.isEmpty()) {
            taskDesc.setText(description);
        } else {
            taskDesc.setText("No description");
        }

        return convertView;
    }

    @Override
    public View getGroupView (int groupPosition, boolean isExpanded,
                              View convertView, final ViewGroup parent) {

        if (convertView==null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item, parent, false);
        }

        // Set variables
        final Task task = (Task) getGroup(groupPosition);
        final String taskPos = Integer.toString(groupPosition + 1);

        //Get task list item views
        final TextView taskNum = (TextView) convertView.findViewById(R.id.task_num);
        TextView taskDate = (TextView) convertView.findViewById(R.id.task_date);
        TextView taskTitle = (TextView) convertView.findViewById(R.id.task_title);
        ProgressBar bar = (ProgressBar) convertView.findViewById(R.id.task_urgency);

        //Set initial text for views
        if (task.getCompletion()==1) {
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
                    task.setCompletion(1);
                    completeSnackbar(parent,task,taskNum,taskPos);
                } else {
                    taskNum.setText(taskPos);
                    task.setCompletion(0);
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
                    task.setCompletion(0);
                    taskNum.setText(taskPos);
                    Snackbar.make(parent, R.string.task_incomplete, Snackbar.LENGTH_SHORT).show();
                }
            });
        mySnackbar.show();
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return MainActivity.getIncompleteTasks().get(groupPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return MainActivity.getIncompleteTasks().get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return MainActivity.getIncompleteTasks().size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

}
