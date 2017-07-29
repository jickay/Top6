package com.example.jickay.top6;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.jickay.top6.fragment.TaskFragment;
import com.example.jickay.top6.provider.TaskProvider;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CompletedTasks extends AppCompatActivity {

    private RecyclerView recView;
    private TaskRecyclerAdapter adapter;
    private TextView empty;

    private Cursor cursor;

    private static ArrayList<Task> completedTasks = MainActivity.getCompletedTasks();

    private ExpandableListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_tasks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new TaskRecyclerAdapter(this);
        recView = (RecyclerView) findViewById(R.id.task_list_all);
        empty = (TextView) findViewById(R.id.empty_message);

        // Load new cursor to refresh view; Does not display any rows completed before
        Uri uri = TaskProvider.CONTENT_URI;
        String where = "CAST(" + TaskProvider.COLUMN_COMPLETION_BEFORE + " as TEXT) =?";
        String[] filter = new String[] {"1"};
        String sortOrder = TaskProvider.COLUMN_DATE + " ASC, " + TaskProvider.COLUMN_IMPORTANCE + " DESC";
        cursor = new CursorLoader(this,uri,null,where,filter,sortOrder).loadInBackground();

        if (cursor.moveToFirst()) {
            adapter.swapCursor(cursor);
            recView.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        } else {
            recView.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }

        // Set adapter to recycler view to fill content
        recView.setAdapter(adapter);
        recView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recView.setLayoutManager(layoutManager);

        // Set to full screen
        TaskFragment.setFullscreen(this);
    }



}
