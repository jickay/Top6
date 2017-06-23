package com.example.jickay.top6;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ViJack on 6/21/2017.
 */

public class TaskAdapter extends ArrayAdapter
{
    int LEADING_DAYS = 10;

    public TaskAdapter(Context context, ArrayList<Task> tasks) {
        super(context,0,tasks);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Task task = (Task) getItem(position);

        if (convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_list_item, parent, false);
        }

        //Get task list item views
        final TextView taskNum = (TextView) convertView.findViewById(R.id.task_num);
        TextView taskDate = (TextView) convertView.findViewById(R.id.task_date);
        TextView taskTitle = (TextView) convertView.findViewById(R.id.task_title);
        ProgressBar bar = (ProgressBar) convertView.findViewById(R.id.task_urgency);

        //Set text for views
        taskNum.setText(Integer.toString(position+1));
        taskDate.setText(task.getDate());
        taskTitle.setText(task.getTitle());

        //Toggle completion when number is clicked
        taskNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskNum.getText() != "OK") {
                    taskNum.setText("OK");
                } else {
                    taskNum.setText(Integer.toString(position+1));
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

}
