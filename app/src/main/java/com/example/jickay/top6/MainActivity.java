package com.example.jickay.top6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static ArrayList<Task> incompleteTasks = new ArrayList<>();
    private static ArrayList<Task> completedTasks = new ArrayList<>();
    private static ArrayList<Task> deletedTasks = new ArrayList<>();

    private int lastDateUsed;

    private RecyclerView recyclerView;
    private TaskRecyclerAdapter taskAdapter;
    private ExpandableListView listView;
    private DrawerLayout drawer;
    private ConstraintLayout emptyText;

    // Getter methods
    public static ArrayList<Task> getIncompleteTasks() { return incompleteTasks; }
    public static ArrayList<Task> getCompletedTasks() { return completedTasks; }
    public static ArrayList<Task> getDeletedTasks() { return deletedTasks; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find view by id
//        recyclerView = (RecyclerView) findViewById(R.id.recycler);
//        emptyText = (ConstraintLayout) findViewById(R.id.empty_layout);

        // Add task button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_task_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Tag","New task clicked");
                Intent intent = new Intent(MainActivity.this, NewTask.class);
                startActivityForResult(intent,0);
            }
        });

        // Navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Daily startup methods

        // Clean up completed tasks from last day
//        boolean isNewDay = checkIfNewDay();
//        dailyTaskCleanup(isNewDay);

        if (incompleteTasks.size()>6) {
            Snackbar.make(findViewById(R.id.main_activity), R.string.starting_message, Snackbar.LENGTH_LONG).show();
        } else if (incompleteTasks.size()>0 && incompleteTasks.size()<6) {
//            listView.setVisibility(View.VISIBLE);
//            emptyText.setVisibility(View.GONE);
            Snackbar.make(findViewById(R.id.main_activity), R.string.notfull_message, Snackbar.LENGTH_LONG).show();
        } else {
//            emptyText.setVisibility(View.GONE);
        }
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        Calendar c = Calendar.getInstance();
//        lastDateUsed = c.get(Calendar.DATE);
//    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        if (incompleteTasks.isEmpty()) {
//            listView.setVisibility(View.GONE);
//            emptyText.setVisibility(View.VISIBLE);
//        } else {
//            listView.setVisibility(View.VISIBLE);
//            emptyText.setVisibility(View.GONE);
//        }
//    }

    /*
        Navigation drawer menu methods
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all_tasks) {
            Intent intent = new Intent(MainActivity.this,AllTasks.class);
            startActivity(intent);
        } else if (id == R.id.nav_completed_tasks) {
            Intent intent = new Intent(MainActivity.this,CompletedTasks.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this,Settings.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
    Method for new task and edit task activities
    Activates after called activity returns a result to MainActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent intent) {
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
//                listView.setAdapter(taskAdapter);
                Snackbar.make(findViewById(R.id.main_activity), R.string.new_task_added, Snackbar.LENGTH_SHORT).show();

            // New task cancelled
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(findViewById(R.id.main_activity), R.string.new_task_cancelled, Snackbar.LENGTH_SHORT).show();
            }
        // For Edit Task activity
        } else if (requestCode == 1) {
            // Task edits to be saved
            if (resultCode == Activity.RESULT_OK) {
                String action = intent.getStringExtra("Action");
                if (action.matches("delete")) {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.main_activity), R.string.task_deleted, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Undo recent task delete
                                    Task lastDeletedTask = deletedTasks.get(deletedTasks.size()-1);
                                    MainActivity.getIncompleteTasks().add(intent.getExtras().getInt("UndoPos"),lastDeletedTask);
                                    Snackbar.make(findViewById(R.id.main_activity), R.string.task_restored, Snackbar.LENGTH_SHORT).show();
//                                    listView.setAdapter(taskAdapter);
//
//                                    listView.setVisibility(View.VISIBLE);
//                                    emptyText.setVisibility(View.INVISIBLE);
                                }
                            });
                    mySnackbar.show();
                } else if (action.matches("edit")) {
                    Snackbar.make(findViewById(R.id.main_activity), R.string.task_edits_saved, Snackbar.LENGTH_SHORT).show();
                }
//                listView.setAdapter(taskAdapter);
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
//                listView.setAdapter(taskAdapter);
            }
        }
    }

//    protected boolean checkIfNewDay() {
//        // Get today's date
//        Calendar c = Calendar.getInstance();
//        int today = c.get(Calendar.DATE);
//
//        return today != lastDateUsed;
//    }
//
//    protected void dailyTaskCleanup(boolean isNewDay) {
//        // If today different than last day used move completed Task objects to completedTasks list
//        if (isNewDay) {
//            for (Task task : incompleteTasks) {
//                if (task.getCompletion()) {
//                    incompleteTasks.remove(task);
//                    completedTasks.add(task);
//                }
//            }
//            Toast.makeText(getApplicationContext(), R.string.completed_cleanup, Toast.LENGTH_SHORT).show();
//        }
//    }
}
