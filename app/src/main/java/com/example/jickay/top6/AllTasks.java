package com.example.jickay.top6;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class AllTasks extends AppCompatActivity {

    private static ArrayList<Task> incompleteTasks = MainActivity.getIncompleteTasks();
    private static ArrayList<Task> completedTasks = MainActivity.getCompletedTasks();

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Assign listview
        listView = (ListView) findViewById(R.id.task_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_task_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllTasks.this, NewTask.class);
                startActivityForResult(intent,0);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Listener for clicking a task in the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AllTasks.this, EditTask.class);

                Bundle b = new Bundle();
                b.putInt("num",position);
                b.putString("title", incompleteTasks.get(position).getTitle());
                b.putString("date", incompleteTasks.get(position).getDate());
                b.putString("desc", incompleteTasks.get(position).getDescription());
                intent.putExtra("taskData",b);

                startActivityForResult(intent,1);
            }
        });

        TaskAdapter taskAdapter = new TaskAdapter(this, incompleteTasks);
        listView.setAdapter(taskAdapter);
    }



}
