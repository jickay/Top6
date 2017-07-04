package com.example.jickay.top6;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    public TaskRecyclerAdapter(Context c, ArrayList<Task> t) {
        context = c;
        tasks = t;
    };

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView task_title;
        TextView task_date;
        TextView task_num;

        public ViewHolder(CardView card){
            super(card);
            cardView = card;

            task_title = (TextView) cardView.findViewById(R.id.task_title);
            task_date = (TextView) cardView.findViewById(R.id.task_date);
            task_num = (TextView) cardView.findViewById(R.id.task_num);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        CardView card = (CardView) LayoutInflater.from(context)
                .inflate(R.layout.task_card,parent,false);
        onCardLongClick(parent,card,i);
        return new ViewHolder(card);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.task_title.setText(tasks.get(i).getTitle());
        viewHolder.task_date.setText(tasks.get(i).getDate());
        viewHolder.task_num.setText(Integer.toString(i + 1));
    }

    @Override
    public int getItemCount() {
        return MainActivity.getIncompleteTasks().size();
    }

    protected void onCardLongClick(final ViewGroup parent,
                                   final CardView cardView,
                                   final int i) {
        // Listener for clicking a task in the list
        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(context,EditTask.class);
                Task task = tasks.get(i);

                Bundle b = new Bundle();
                b.putString("title", task.getTitle());
                b.putString("date", task.getDate());
                b.putString("desc", task.getDescription());
                intent.putExtra("taskData", b);

                startActivityForResult(intent, 1);
                return true;
            }
        });
    }

}
