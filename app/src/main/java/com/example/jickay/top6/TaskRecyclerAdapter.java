package com.example.jickay.top6;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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

        public ViewHolder(CardView card){
            super(card);
            cardView = card;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        CardView card = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_task,parent,false);
        return new ViewHolder(card);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.task_title.setText(tasks.get(i).getTitle());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

}
