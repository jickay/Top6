package com.example.jickay.top6;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import java.util.ArrayList;

public class CompletedTasks extends AppCompatActivity {

    private static ArrayList<Task> completedTasks = MainActivity.getCompletedTasks();

    private ExpandableListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_tasks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Assign listview
        listView = (ExpandableListView) findViewById(R.id.task_list);

        // Listener for clicking a task in the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CompletedTasks.this, CreateEditTask.class);
                Task currentTask = completedTasks.get(position);

                Bundle b = new Bundle();
                b.putInt("num",position);
                b.putString("title", currentTask.getTitle());
                b.putString("date", currentTask.getDate());
                b.putString("desc", currentTask.getDescription());
                intent.putExtra("taskData",b);

                startActivityForResult(intent,1);
            }
        });

        TaskAdapter taskAdapter = new TaskAdapter(this, completedTasks);
        listView.setAdapter(taskAdapter);
    }



}
