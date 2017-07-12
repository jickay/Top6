package com.example.jickay.top6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    static int LEADING_DAYS = 10;

    private static ArrayList<Task> incompleteTasks = new ArrayList<>();
    private static ArrayList<Task> completedTasks = new ArrayList<>();
    private static ArrayList<Task> deletedTasks = new ArrayList<>();

    private FileWriter fileWriter;

    // Getter methods
    public static ArrayList<Task> getIncompleteTasks() { return incompleteTasks; }
    public static ArrayList<Task> getCompletedTasks() { return completedTasks; }
    public static ArrayList<Task> getDeletedTasks() { return deletedTasks; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load from file
        fileWriter = new FileWriter();
        fileWriter.onLoad(this);

        // Calculate urgency for each task
        for (Task task : incompleteTasks) {
            getUrgency(task);
        }

        // Sort tasks with priority algorithm


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add task button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_task_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateEditTask.class);
                intent.putExtra("Intent","new");
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

        // Starting message
        if (incompleteTasks.size()>6) {
            Snackbar.make(findViewById(R.id.main_activity), R.string.starting_message, Snackbar.LENGTH_LONG).show();
        } else if (incompleteTasks.size()>0 && incompleteTasks.size()<6) {
            Snackbar.make(findViewById(R.id.main_activity), R.string.notfull_message, Snackbar.LENGTH_LONG).show();
        }
    }

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
                        newData.getString("description"),
                        newData.getInt("importance"));
                getUrgency(task);
                incompleteTasks.add(task);
                saveTaskFile();
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
                    saveTaskFile();
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.main_activity), R.string.task_deleted, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Undo recent task delete
                                    Task lastDeletedTask = deletedTasks.get(deletedTasks.size()-1);
                                    MainActivity.getIncompleteTasks().add(intent.getExtras().getInt("UndoPos"),lastDeletedTask);
                                    saveTaskFile();
                                    Snackbar.make(findViewById(R.id.main_activity), R.string.task_restored, Snackbar.LENGTH_SHORT).show();
                                }
                            });
                    mySnackbar.show();
                } else if (action.matches("edit")) {
                    saveTaskFile();
                    Snackbar.make(findViewById(R.id.main_activity), R.string.task_edits_saved, Snackbar.LENGTH_SHORT).show();
                }
//                listView.setAdapter(taskAdapter);
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
//                listView.setAdapter(taskAdapter);
            }
        }
    }

    private void saveTaskFile() {
        fileWriter.onSave(this);
    }

    private void loadTaskFile() {
        fileWriter.onLoad(this);
    }

    public static void getUrgency(Task thisTask) {
        int taskDay = Integer.parseInt(thisTask.getDate().split(" ")[1]);
        //Calculate value for progress bar
        Calendar c = Calendar.getInstance();
        int currentDay = c.get(Calendar.DAY_OF_MONTH);
        int daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        int difference;
        if (taskDay >= currentDay) {
            difference = taskDay - currentDay;
        } else {
            int remainingDays = daysInMonth - currentDay;
            difference = taskDay + remainingDays;
        }

        int daysRemaining = LEADING_DAYS-difference;
        int progressValue = 0;

        //Set progress bar length
        if (difference <= LEADING_DAYS) {
            progressValue = 100/LEADING_DAYS * daysRemaining;
        }

        thisTask.setUrgency(progressValue);
    }

    private void sortByPriority() {
        int buffer = 10;
        int i = 0;
        while (i<buffer && i<incompleteTasks.size()) {
        }
    }


}
