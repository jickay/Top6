package com.example.jickay.top6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import static android.support.v4.app.ActivityCompat.startActivity;

public class MainActivity extends AppCompatActivity {

    private static ArrayList<Task> incompleteTasks = new ArrayList<>();
    private static ArrayList<Task> completedTasks = new ArrayList<>();

    private int lastDateUsed;

    private TaskAdapter taskAdapter;
    private ListView listView;

    // Getter methods
    public static ArrayList<Task> getIncompleteTasks() { return incompleteTasks; }
    public static ArrayList<Task> getCompletedTasks() { return completedTasks; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Assign listview
        listView = (ListView) findViewById(R.id.task_list);

        // Add task button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_task_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewTask.class);
                startActivityForResult(intent,0);
            }
        });

        // Listener for clicking a task in the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditTask.class);

                Bundle b = new Bundle();
                b.putInt("num",position);
                b.putString("title", incompleteTasks.get(position).getTitle());
                b.putString("date", incompleteTasks.get(position).getDate());
                b.putString("desc", incompleteTasks.get(position).getDescription());
                intent.putExtra("taskData",b);

                startActivityForResult(intent,1);
            }
        });

        // Daily startup methods

            // Clean up completed tasks from last day
        boolean isNewDay = checkIfNewDay();
        dailyTaskCleanup(isNewDay);
        Toast.makeText(getApplicationContext(), R.string.starting_message, Toast.LENGTH_LONG).show();
            // Show latest incomplete tasks

        taskAdapter = new TaskAdapter(this, incompleteTasks);
        listView.setAdapter(taskAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Calendar c = Calendar.getInstance();
        lastDateUsed = c.get(Calendar.DATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_all_tasks) {
            Intent toAllTasks = new Intent(MainActivity.this, AllTasks.class);
            startActivity(toAllTasks);
            return true;
        }

        if (id == R.id.action_completed_tasks) {
            Intent toCompletedTasks = new Intent(MainActivity.this, CompletedTasks.class);
            startActivity(toCompletedTasks);
            return true;
        }

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Method for new task and edit task activities
    Activates after called activity returns a result to MainActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // For New Task activity
        if (requestCode == 0) {
            // New task to be added
            if (resultCode == Activity.RESULT_OK) {
                // Get date from intent
                Bundle newData = intent.getBundleExtra("NewTask");
                // Construct new Task object using data
                Task task = new Task(newData.getString("title"),
                        newData.getString("date"),
                        newData.getString("description"));
                incompleteTasks.add(task);
                // Refresh list to show new tasks
                listView.setAdapter(taskAdapter);
                Toast.makeText(getApplicationContext(), R.string.new_task_added, Toast.LENGTH_SHORT).show();

            // New task cancelled
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), R.string.new_task_cancelled, Toast.LENGTH_SHORT).show();
            }
        // For Edit Task activity
        } else if (requestCode == 1) {
            // Task edits to be saved
            if (resultCode == Activity.RESULT_OK) {
                listView.setAdapter(taskAdapter);
                Toast.makeText(getApplicationContext(), R.string.task_edits_saved, Toast.LENGTH_SHORT).show();
            }
//            else if (resultCode == Activity.RESULT_CANCELED) {
//                listView.setAdapter(taskAdapter);
//            }
        }
    }

    protected boolean checkIfNewDay() {
        // Get today's date
        Calendar c = Calendar.getInstance();
        int today = c.get(Calendar.DATE);

        return today != lastDateUsed;
    }

    protected void dailyTaskCleanup(boolean isNewDay) {
        // If today different than last day used move completed Task objects to completedTasks list
        if (isNewDay) {
            for (Task task : incompleteTasks) {
                if (task.getCompletion()) {
                    incompleteTasks.remove(task);
                    completedTasks.add(task);
                }
            }
        }
    }
}
