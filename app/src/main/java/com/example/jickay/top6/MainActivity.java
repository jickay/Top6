package com.example.jickay.top6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private int LEADING_DAYS = 10;

    private GestureDetectorCompat gesture;
    private RecyclerView rv;

    private static ArrayList<Task> incompleteTasks = new ArrayList<>();
    private static ArrayList<Task> completedTasks = new ArrayList<>();
    private static ArrayList<Task> deletedTasks = new ArrayList<>();

    // Getter methods
    public static ArrayList<Task> getIncompleteTasks() { return incompleteTasks; }
    public static ArrayList<Task> getCompletedTasks() { return completedTasks; }
    public static ArrayList<Task> getDeletedTasks() { return deletedTasks; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                Log.i("New task","New task started");
            }
        });

        // Set touch listener for task list
        gesture = new GestureDetectorCompat(this, new MyGestureListener());

        rv = (RecyclerView)findViewById(R.id.recycler);
        rv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gesture.onTouchEvent(event);
                return false;
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
        Snackbar.make(findViewById(R.id.main_activity), R.string.notfull_message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onTouchEvent (MotionEvent event){
        gesture.onTouchEvent(event);
        return super.onTouchEvent(event);
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


    class MyGestureListener implements GestureDetector.OnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            if (distanceY > 0) {
//                Log.i("Scroll",Float.toString(e1.getY()));
//                rv.smoothScrollToPosition(Float.floatToIntBits(e1.getY())+200);
//            } else {
//                Log.i("Scroll",Float.toString(e1.getY()));
//                rv.smoothScrollToPosition(Float.floatToIntBits(e1.getY())-200);
//            }
            return false;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
//            if (velocityY < 0) {
//                Log.i("Scroll",Float.toString(velocityY)+" "+rv.computeVerticalScrollOffset());
//                rv.smoothScrollToPosition(rv.computeVerticalScrollOffset()+50);
//            } else {
//                Log.i("Scroll",Float.toString(velocityY)+" "+rv.computeVerticalScrollOffset());
//                rv.smoothScrollToPosition(rv.computeVerticalScrollOffset()-50);
//            }
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public void onLongPress(MotionEvent e) {

        }
    }

}
