package com.example.jickay.top6.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ViJack on 7/14/2017.
 */

public class TaskProvider extends ContentProvider {

    SQLiteDatabase db;

    // Database columns
    public static final String COLUMN_TASKID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IMPORTANCE = "importance";
    public static final String COLUMN_COMPLETION_TODAY = "completion_today";
    public static final String COLUMN_COMPLETION_BEFORE = "completion_before";

    // Database constants
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "data";
    public static final String DATABASE_TABLE = "tasks";

    // Content URI and authority
    public static final String AUTHORITY = "com.example.jickay.top6.provider.TaskProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/task");

    // MIME types for db lookup
    private static final String TASKS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.example.jickay.top6.tasks";
    private static final String TASK_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.example.jickay.top6.task";

    private static final int LIST_TASK = 0;
    private static final int ITEM_TASK = 1;
    private static final UriMatcher URI_MATCHER = buildUriMatcher();


    @Override
    public boolean onCreate() {
        db = new DatabaseHelper(getContext()).getWritableDatabase();
        return true;
    }

    // Helper class
    protected static class DatabaseHelper extends SQLiteOpenHelper {
        static final String DATABASE_CREATE = "create table " + DATABASE_TABLE + " (" +
                COLUMN_TASKID + " integer primary key autoincrement, " +
                COLUMN_TITLE + " text not null, " +
                COLUMN_DATE + " text not null, " +
                COLUMN_DESCRIPTION + " text not null, " +
                COLUMN_IMPORTANCE + " integer not null, " +
                COLUMN_COMPLETION_TODAY + " integer not null, " +
                COLUMN_COMPLETION_BEFORE + " integer not null);";

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int ii) {
            throw new UnsupportedOperationException();
        }
    }

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, "task", LIST_TASK);
        matcher.addURI(AUTHORITY, "task/#", ITEM_TASK);
        return matcher;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case LIST_TASK: return TASKS_MIME_TYPE;
            case ITEM_TASK: return TASK_MIME_TYPE;
            default: throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    // CRUD methods

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long id = db.insertOrThrow(DATABASE_TABLE, null, contentValues);
        Log.i("ContentValues",contentValues.getAsString("title"));
        Log.i("ContentValues",contentValues.getAsString("date"));
        Log.i("Database","Insert attempted");

        if (id > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, id);
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int count = db.delete(
                DATABASE_TABLE,
                COLUMN_TASKID + "=?",
                new String[] {Long.toString(ContentUris.parseId(uri))}
        );

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        if (contentValues.containsKey(COLUMN_TASKID)) throw new UnsupportedOperationException();

        int count = db.update(
                DATABASE_TABLE,
                contentValues,
                COLUMN_TASKID + "=?",
                new String[] {Long.toString(ContentUris.parseId(uri))}
        );

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        String[] projection = new String[] {
                COLUMN_TASKID,
                COLUMN_TITLE,
                COLUMN_DATE,
                COLUMN_DESCRIPTION,
                COLUMN_IMPORTANCE,
                COLUMN_COMPLETION_TODAY,
                COLUMN_COMPLETION_BEFORE
        };

        Cursor c;
        switch (URI_MATCHER.match(uri)) {
            case LIST_TASK:
                c = db.query(DATABASE_TABLE,
                        projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case ITEM_TASK:
                c = db.query(DATABASE_TABLE,
                        projection,
                        COLUMN_TASKID + "=?",
                        new String[] {Long.toString(ContentUris.parseId(uri))},
                        null, null, null, null);
                if (c.getCount() > 0) {
                    c.moveToFirst();
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

}
