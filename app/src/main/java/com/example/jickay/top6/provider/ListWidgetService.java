package com.example.jickay.top6.provider;

import android.app.Activity;
import android.app.LauncherActivity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.jickay.top6.MainActivity;
import com.example.jickay.top6.R;

import java.util.ArrayList;

/**
 * Created by ViJack on 8/21/2017.
 */

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(getApplicationContext(), intent);
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private int id;
    private Cursor cursor;

    private ArrayList<String> items = new ArrayList<String>();

//... include adapter-like methods here. See the StackView Widget sample.
    public ListRemoteViewsFactory (Context c, Intent i) {
        context = c;
        id = i.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        Uri uri = TaskProvider.CONTENT_URI;
        String where = "CAST(" + TaskProvider.COLUMN_COMPLETION_BEFORE + " as TEXT) =?";
        String[] filter = new String[] {"0"};
        String sortOrder = TaskProvider.COLUMN_DATE + " ASC, " + TaskProvider.COLUMN_IMPORTANCE + " DESC";
        cursor = new CursorLoader(context,uri,null,where,filter,sortOrder).loadInBackground();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getCount() {
        int count = cursor !=null ? cursor.getCount() : 0;
        return count;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public RemoteViews getViewAt(int i) {
        cursor.moveToPosition(i);
        String title = cursor.getString(cursor.getColumnIndex(TaskProvider.COLUMN_TITLE));
        String doneToday = Integer.toString(MainActivity.getDoneToday());
        String doneYesterday = Integer.toString(MainActivity.getDoneYesterday());

        int importance = cursor.getInt(cursor.getColumnIndex(TaskProvider.COLUMN_IMPORTANCE));
        int importanceColor = getImportanceColor(importance);

        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        view.setTextViewText(R.id.done_today,doneToday);
        view.setTextViewText(R.id.done_yesterday,doneYesterday);
        view.setInt(R.id.widget_task_num,"setBackgroundColor", ContextCompat.getColor(context,importanceColor));
        view.setTextViewText(R.id.widget_task_num,Integer.toString(i+1));
        view.setTextViewText(R.id.widget_title,title);
        Log.i("Widget","Text at position " + i + " is " + title);
        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private int getImportanceColor(int importance) {
        int color = -1;
        switch (importance) {
            // No value selected
            case -1: color = R.color.colorPrimaryMed; break;
            // Importance value selected
            case 1: color = R.color.importance_low; break;
            case 2: color = R.color.importance_med; break;
            case 3: color = R.color.importance_high; break;
        }
        return color;
    }
}