package com.example.jickay.top6;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jickay.top6.fragment.TaskFragment;
import com.example.jickay.top6.provider.TaskProvider;
import com.example.jickay.top6.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private int LEADING_DAYS = 10;

    private static int doneToday = 0;
    private static int doneYesterday = 0;

    private static SharedPreferences sharedPref;
    private SharedPreferences.Editor prefEditor;

    private static TextView done_today;
    private TextView done_yesterday;

    private static ArrayList<Task> incompleteTasks = new ArrayList<>();
    private static ArrayList<Task> completedTasks = new ArrayList<>();
    private static ArrayList<Task> deletedTasks = new ArrayList<>();

    // Getter methods
    public static int getDoneToday() { return doneToday; }
    public static int getDoneYesterday() { return doneYesterday; }
    public static ArrayList<Task> getIncompleteTasks() { return incompleteTasks; }
    public static ArrayList<Task> getCompletedTasks() { return completedTasks; }
    public static ArrayList<Task> getDeletedTasks() { return deletedTasks; }

    // Setter methods
    public static void setDoneToday(int number) { doneToday = number; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Sort tasks with priority algorithm
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Preferences editor
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        prefEditor = sharedPref.edit();

        getCompletedToday(this);

        // Add task button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_task_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateEditTask.class);
                intent.putExtra("Intent","new");
                startActivityForResult(intent,0);
                Log.i("New task","New task started");
            }
        });

        // Navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set completion count for today and yesterday
        done_today = (TextView) findViewById(R.id.done_today);
        done_yesterday = (TextView) findViewById(R.id.done_yesterday);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Store last day used
        Calendar c = Calendar.getInstance();
        int lastDay = c.get(Calendar.DAY_OF_YEAR);
        Log.i("NewDayDetection", "Last day is " + lastDay);

        prefEditor.putInt(getString(R.string.date_yesterday), lastDay);
        prefEditor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get count of currently completed tasks from db and set to local variable
        getCompletedToday(this);

        // Get today's date
        Calendar c = Calendar.getInstance();
        int thisDay = c.get(Calendar.DAY_OF_YEAR);
        int lastDay = sharedPref.getInt(getString(R.string.date_yesterday), 0);
        Log.i("NewDayDetection","ThisDay: " + Integer.toString(thisDay) + ", LastDay: " + Integer.toString(lastDay));

        // Clear completed if new day
        if (thisDay > lastDay) {
            Log.i("NewDayDetection",Integer.toString(thisDay) + " is later than " + Integer.toString(lastDay));
            // Store number done before clearing into yesterday preference; zero out today preference
            moveTodayCountToYesterday();
            // Set number done yesterday, zero out done today
            updateDoneYesterday();
            updateDoneToday();
            // Clear complete today to not show on recycler
            clearCompleted();
        } else {
            updateDoneToday();
            updateDoneYesterday();
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
            Intent intent = new Intent(MainActivity.this, AltTaskLists.class);
            intent.putExtra("ListType","all");
            startActivity(intent);
        } else if (id == R.id.nav_completed_tasks) {
            Intent intent = new Intent(MainActivity.this, AltTaskLists.class);
            intent.putExtra("ListType","completed");
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
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
                            // Undo recent task delete
                            .setAction(R.string.undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Task lastDeletedTask = deletedTasks.get(deletedTasks.size()-1);
                                    MainActivity.getIncompleteTasks().add(intent.getExtras().getInt("UndoPos"),lastDeletedTask);
                                    Snackbar.make(findViewById(R.id.main_activity), R.string.task_restored, Snackbar.LENGTH_SHORT).show();
                                }
                            });
                    mySnackbar.show();
                } else if (action.matches("edit")) {
                    Snackbar.make(findViewById(R.id.main_activity), R.string.task_edits_saved, Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void getCompletedToday(Context context) {
        // Get all tasks complete today
        Uri allCompleted = TaskProvider.CONTENT_URI;
        String where = "CAST(" + TaskProvider.COLUMN_COMPLETION_TODAY + " as TEXT) =?";
        String[] filter = new String[]{"1"};
        String sortOrder = TaskProvider.COLUMN_DATE + " ASC";
        Cursor cursor = new CursorLoader(context, allCompleted, null, where, filter, sortOrder).loadInBackground();

        // Count number in list
        int count = 0;
        if (cursor.moveToFirst()) {
            do {
                count++;
            } while (cursor.moveToNext());
        }

        prefEditor.putInt(getString(R.string.done_today), count);
        prefEditor.commit();

        doneToday = count;
    }

    private void clearCompleted() {
        // Get all tasks complete today
        Uri allCompleted = TaskProvider.CONTENT_URI;
        String where = "CAST(" + TaskProvider.COLUMN_COMPLETION_TODAY + " as TEXT) =?";
        String[] filter = new String[]{"1"};
        String sortOrder = TaskProvider.COLUMN_DATE + " ASC";
        Cursor cursor = new CursorLoader(this, allCompleted, null, where, filter, sortOrder).loadInBackground();

        // Change value as complete before to prevent loading on main activity
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(TaskProvider.COLUMN_TASKID));
                Uri eachCompleted = ContentUris.withAppendedId(TaskProvider.CONTENT_URI, id);
                ContentValues values = new ContentValues();
                values.put(TaskProvider.COLUMN_COMPLETION_TODAY, 0);
                values.put(TaskProvider.COLUMN_COMPLETION_BEFORE, 1);
                getContentResolver().update(eachCompleted, values, null, null);
            } while (cursor.moveToNext());
        }
    }

    public static void updateDoneToday() {
        Log.i("DoneToday","Current count is " + doneToday);
        int number = doneToday;
        if (number < 0) { number = 0; }
        String count = Integer.toString(number);
        done_today.setText(count);
    }

    public void updateDoneYesterday() {
        Log.i("DoneYesterday","Current count is " + doneToday);
        int number = sharedPref.getInt(getString(R.string.done_yesterday), 0);
        if (number < 0) { number = 0; }
        String count = Integer.toString(number);
        done_yesterday.setText(count);
    }

    private void moveTodayCountToYesterday() {
        int number = sharedPref.getInt(getString(R.string.done_today), 0);
        doneYesterday = number;
        prefEditor.putInt(getString(R.string.done_yesterday), number);
        prefEditor.putInt(getString(R.string.done_today),0);
        prefEditor.commit();
    }

}
