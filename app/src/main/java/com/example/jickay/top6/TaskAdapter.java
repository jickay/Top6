package com.example.jickay.top6;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ViJack on 6/21/2017.
 */

public class TaskAdapter extends ArrayAdapter
{
    public TaskAdapter(Context context, ArrayList<Task> tasks) {
        super(context,0,tasks);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Task task = (Task) getItem(position);

        if (convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_list_item, parent, false);
        }

        final TextView taskNum = (TextView) convertView.findViewById(R.id.task_num);
        TextView taskDate = (TextView) convertView.findViewById(R.id.task_date);
        TextView taskTitle = (TextView) convertView.findViewById(R.id.task_title);

        taskNum.setText(Integer.toString(position+1));
        taskDate.setText(task.getDate());
        taskTitle.setText(task.getTitle());

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

        return convertView;
    }

}
