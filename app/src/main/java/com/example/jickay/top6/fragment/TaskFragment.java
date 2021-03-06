package com.example.jickay.top6.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.jickay.top6.R;
import com.example.jickay.top6.TaskRecyclerAdapter;
import com.example.jickay.top6.notifications.ReminderManager;
import com.example.jickay.top6.widget.ListWidgetService;
import com.example.jickay.top6.provider.TaskProvider;

import java.util.Calendar;

/**
 * Created by user on 7/2/2017.
 */

public class TaskFragment extends Fragment {

    RecyclerView recView;
    TaskRecyclerAdapter adapter;
    Cursor cursor;
    TextView empty;

    AppWidgetManager widgetManager;

    public TaskFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TaskRecyclerAdapter(this, getActivity(),"current");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_main, container, false);
        recView = (RecyclerView) v.findViewById(R.id.recycler);
        empty = (TextView) v.findViewById(R.id.empty_message);

        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        setFullscreen(getActivity());

        // Load new cursor to refresh view; Does not display any rows completed before
        Intent intent = new Intent(getContext(), ListWidgetService.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
        Log.i("Fragment","Broadcast intent to widget provider");
        refreshCursor();

        // Show starting instructions if no tasks
        if (!cursor.moveToFirst()) {
            recView.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        // Show normal list of tasks in recycler view
        } else {
            recView.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);

            // Set adapter to recycler view to fill content
            recView.setAdapter(adapter);
            recView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recView.setLayoutManager(layoutManager);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!cursor.moveToFirst()) {
            ReminderManager.setReminder(getActivity(),"empty",0,getString(R.string.empty_notification_message),Calendar.getInstance(),R.color.colorAccent);
        }
    }

    public void refreshCursor() {
        Uri uri = TaskProvider.CONTENT_URI;
        String where = "CAST(" + TaskProvider.COLUMN_COMPLETION_BEFORE + " as TEXT) =?";
        String[] filter = new String[] {"0"};
        String sortOrder = TaskProvider.COLUMN_DATE + " ASC, " + TaskProvider.COLUMN_IMPORTANCE + " DESC";
        cursor = new CursorLoader(getActivity(),uri,null,where,filter,sortOrder).loadInBackground();
        cursor.moveToFirst();
        adapter.swapCursor(cursor);
    }

    public static void setFullscreen(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= 19) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else if (Build.VERSION.SDK_INT >= 16) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    public static void setFullscreenWithNavigation(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= 19) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else if (Build.VERSION.SDK_INT >= 16) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

}
