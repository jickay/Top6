package com.example.jickay.top6;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ViJack on 6/21/2017.
 */

public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder>
{
    int LEADING_DAYS = 10;

    Context context;
    ArrayList<Task> tasks;

    CardView card;
    Task thisTask;

    public TaskRecyclerAdapter(Context c, ArrayList<Task> t) {
        context = c;
        tasks = t;
    };

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView task_title;
        TextView task_date;
        TextView task_num;
        TextView task_desc;
        ProgressBar bar;

        public ViewHolder(CardView card){
            super(card);
            cardView = card;

            task_title = (TextView) cardView.findViewById(R.id.task_title);
            task_date = (TextView) cardView.findViewById(R.id.task_date);
            task_num = (TextView) cardView.findViewById(R.id.task_num);
            task_desc = (TextView) cardView.findViewById(R.id.task_description);
            bar = (ProgressBar) cardView.findViewById(R.id.task_urgency);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        card = (CardView) LayoutInflater.from(context)
                .inflate(R.layout.task_card,parent,false);
        thisTask = tasks.get(i);
        return new ViewHolder(card);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.task_title.setText(tasks.get(i).getTitle());
        viewHolder.task_date.setText(tasks.get(i).getDate());
        viewHolder.task_num.setText(Integer.toString(i + 1));
        viewHolder.task_desc.setText(tasks.get(i).getDescription());

        viewHolder.task_num.setBackgroundColor(ContextCompat.getColor(context,getImportanceColor(i)));
        viewHolder.task_date.setBackgroundColor(ContextCompat.getColor(context,getImportanceColor(i)));

        viewHolder.bar.setProgress(tasks.get(i).getUrgency());

        onCardLongClick(card,i);
    }

    @Override
    public int getItemCount() {
        return MainActivity.getIncompleteTasks().size();
    }

    private void onCardLongClick(final CardView cardView,
                                   final int i) {
        // Listener for clicking a task in the list
        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(context,EditTask.class);
                Task task = tasks.get(i);

                Bundle b = new Bundle();
                b.putInt("num",i);
                b.putString("title", task.getTitle());
                b.putString("date", task.getDate());
                b.putString("desc", task.getDescription());
                intent.putExtra("taskData", b);

                ((Activity)cardView.getContext()).startActivityForResult(intent, 1);
                return true;
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



}
