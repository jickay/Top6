package com.example.jickay.top6;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.jickay.top6.fragment.TaskFragment;
import com.example.jickay.top6.provider.TaskProvider;

public class AltTaskLists extends AppCompatActivity {

    private RecyclerView recView;
    private TaskRecyclerAdapter adapter;
    private TextView empty;

    private String listType;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternative_tasks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get list type
        listType = getIntent().getStringExtra("ListType");

        // Set adapter for recycler view
        adapter = new TaskRecyclerAdapter(this,listType);
        recView = (RecyclerView) findViewById(R.id.task_list_alt);
        empty = (TextView) findViewById(R.id.empty_message);

        // Load new cursor to refresh view; Does not display any rows completed before

        setCursorForList(listType);

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

    private void setCursorForList(String listType) {
        Uri uri = TaskProvider.CONTENT_URI;
        String where = "";
        String[] valueFilter = {""};
        String sortOrder = "";

        switch (listType) {
            // All columns sorted by default
            case "all":
                this.setTitle("All Tasks");
                where = null;
                valueFilter = null;
                sortOrder = TaskProvider.COLUMN_DATE + " DESC, " + TaskProvider.COLUMN_IMPORTANCE + " DESC";
                break;
            // All in completed column where value is 1
            case "completed":
                this.setTitle("Completed Tasks");
                where = "CAST(" + TaskProvider.COLUMN_COMPLETION_BEFORE + " as TEXT) =?";
                valueFilter = new String[]{"1"};
                sortOrder = TaskProvider.COLUMN_DATE + " DESC, " + TaskProvider.COLUMN_IMPORTANCE + " DESC";
                break;
            // All in date column that is before today's date (Needs work!)
            case "past_due":
                this.setTitle("Past Due");
                where = TaskProvider.COLUMN_DATE + " =?";
                valueFilter = new String[]{"1"};
                sortOrder = TaskProvider.COLUMN_DATE + " ASC, " + TaskProvider.COLUMN_IMPORTANCE + " DESC";
                break;
        }

        cursor = new CursorLoader(this, uri, null, where, valueFilter, sortOrder).loadInBackground();
    }

}
