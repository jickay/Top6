package com.example.jickay.top6.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import com.example.jickay.top6.MainActivity;
import com.example.jickay.top6.R;

/**
 * Created by ViJack on 8/21/2017.
 */

public class AppWidget extends AppWidgetProvider {
    public static final String ONCLICK_COMPLETE = "com.example.jickay.Top6.TOAST_ACTION";
    public static final String EXTRA_ITEM = "com.example.jickay.Top6.EXTRA_ITEM";

    Cursor cursor;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.i("AppWidget","onUpdate called");

        for (int i = 0; i < appWidgetIds.length; i++) {

            Intent intent = new Intent(context, ListWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.app_widget);

            rv.setRemoteAdapter(R.id.widget_listview, intent);
            rv.setEmptyView(R.id.widget_listview, R.id.widget_empty);

//            Intent clickComplete = new Intent(context,ListWidgetService.class);
//            clickComplete.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
//            clickComplete.setAction(ONCLICK_COMPLETE);
//
//            PendingIntent clickCompletePendingIntent = PendingIntent.getBroadcast(context, 0, clickComplete, PendingIntent.FLAG_UPDATE_CURRENT);
//            rv.setOnClickPendingIntent(R.id.widget_complete_button, clickCompletePendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,R.id.widget_listview);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.i("AppWidget","onReceive called");

        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE")) {
            Log.i("AppWidget","Intent received");
            RemoteViews remoteView = new RemoteViews(context.getPackageName(),R.layout.app_widget);
            ComponentName component = new ComponentName(context,ListWidgetService.class);
            int appWidgetIds[] = mgr.getAppWidgetIds(new ComponentName(context, ListWidgetService.class));
            mgr.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview);
            mgr.updateAppWidget(component, remoteView);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);


    }
}
