package com.example.jickay.top6.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.jickay.top6.R;
import com.example.jickay.top6.provider.TaskProvider;

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
        // Get db data
        cursor.moveToPosition(i);
        String title = cursor.getString(cursor.getColumnIndex(TaskProvider.COLUMN_TITLE));
        int completion = cursor.getInt(cursor.getColumnIndex(TaskProvider.COLUMN_COMPLETION_TODAY));
        int overdue = cursor.getInt(cursor.getColumnIndex(TaskProvider.COLUMN_OVERDUE));
        // Store into local list
        items.add(title);
        // Set widget item colors and text
        RemoteViews widgetItems = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        if (completion == 1) {
            widgetItems.setInt(R.id.widget_task_num, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorAccent));
            widgetItems.setInt(R.id.widget_title, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorAccent));
            widgetItems.setInt(R.id.widget_title, "setTextColor", ContextCompat.getColor(context, R.color.cardview_light_background));
        } else if (overdue == 0) {
            int importance = cursor.getInt(cursor.getColumnIndex(TaskProvider.COLUMN_IMPORTANCE));
            int importanceColor = getImportanceColor(importance);
            widgetItems.setInt(R.id.widget_task_num, "setBackgroundColor", ContextCompat.getColor(context, importanceColor));
            widgetItems.setInt(R.id.widget_title, "setBackgroundColor", ContextCompat.getColor(context, R.color.cardview_light_background));
            widgetItems.setInt(R.id.widget_title, "setTextColor", ContextCompat.getColor(context, R.color.cardview_dark_background));
        } else {
            widgetItems.setInt(R.id.widget_task_num, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorOverdue));
            widgetItems.setInt(R.id.widget_title, "setBackgroundColor", ContextCompat.getColor(context, R.color.colorOverdue));
            widgetItems.setInt(R.id.widget_title, "setTextColor", ContextCompat.getColor(context, R.color.cardview_light_background));
        }
        widgetItems.setTextViewText(R.id.widget_task_num,Integer.toString(i+1));
        widgetItems.setTextViewText(R.id.widget_title,items.get(i));
        Log.i("Widget","Text at position " + i + " is " + title);
        return widgetItems;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public void onDataSetChanged() {
        Log.i("Widget","Data set changed initiated");
        cursor.moveToFirst();
        items.clear();
        do {
            String title = cursor.getString(cursor.getColumnIndex(TaskProvider.COLUMN_TITLE));
            items.add(title);
        } while (cursor.moveToNext());
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
            case 1: color = R.color.importance_3; break;
            case 2: color = R.color.importance_2; break;
            case 3: color = R.color.importance_1; break;
        }
        return color;
    }
}
