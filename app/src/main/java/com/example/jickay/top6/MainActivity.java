package com.example.jickay.top6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static ArrayList<Task> allTasks = new ArrayList<Task>();
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        taskAdapter = new TaskAdapter(this,allTasks);
        ListView listView = (ListView) findViewById(R.id.task_list);
        listView.setAdapter(taskAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_task_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewTask.class);
                startActivityForResult(intent,0);
            }
        });

        ListView list = (ListView) findViewById(R.id.task_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditTask.class);

                Bundle b = new Bundle();
                b.putInt("num",position);
                b.putString("title",allTasks.get(position).getTitle());
                b.putString("date",allTasks.get(position).getDate());
                b.putString("desc",allTasks.get(position).getDescription());
                intent.putExtra("taskData",b);

                startActivityForResult(intent,1);
            }
        });

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            Bundle newData = intent.getBundleExtra("NewTask");

            Task task = new Task(newData.getString("title"),
                    newData.getString("date"),
                    newData.getString("description"));
            allTasks.add(task);

            ListView listView = (ListView) findViewById(R.id.task_list);
            listView.setAdapter(taskAdapter);

            //taskAdapter.add(task);

            Toast.makeText(getApplicationContext(), "New task added", Toast.LENGTH_SHORT).show();
        } else if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            ListView listView = (ListView) findViewById(R.id.task_list);
            listView.setAdapter(taskAdapter);
            Toast.makeText(getApplicationContext(), "Task edits saved", Toast.LENGTH_SHORT).show();
        } else if (resultCode == Activity.RESULT_CANCELED){
            Toast.makeText(getApplicationContext(), "New task cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}
