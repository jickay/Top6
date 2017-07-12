package com.example.jickay.top6;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import java.util.ArrayList;

public class AllTasks extends AppCompatActivity {

    private static ArrayList<Task> incompleteTasks = MainActivity.getIncompleteTasks();

    private ExpandableListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Assign listview
        listView = (ExpandableListView) findViewById(R.id.task_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_task_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllTasks.this, NewTask.class);
                startActivityForResult(intent,0);
            }
        });


        // Listener for clicking a task in the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AllTasks.this, CreateEditTask.class);
                Task currentTask = incompleteTasks.get(position);

                Bundle b = new Bundle();
                b.putInt("num",position);
                b.putString("title", currentTask.getTitle());
                b.putString("date", currentTask.getDate());
                b.putString("desc", currentTask.getDescription());
                intent.putExtra("taskData",b);

                startActivityForResult(intent,1);
            }
        });

        TaskAdapter taskAdapter = new TaskAdapter(this, incompleteTasks);
        listView.setAdapter(taskAdapter);
    }



}
