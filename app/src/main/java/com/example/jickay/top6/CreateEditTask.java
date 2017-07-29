package com.example.jickay.top6;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.jickay.top6.fragment.DatePickerFragment;
import com.example.jickay.top6.fragment.TaskFragment;
import com.example.jickay.top6.provider.TaskProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CreateEditTask extends AppCompatActivity {

    int LEADING_DAYS = 10;

    private Cursor c;

    private EditText title;
    private EditText date;
    private EditText desc;

    private String titleString;
    private String dateString;
    private String descString;

    private static String dateData;
    private RadioGroup importance;
    private int importanceValue = -1;

    private FloatingActionButton save;
    private FloatingActionButton cancelDelete;

    public static void setDateData(String data) { dateData = data; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaskFragment.setFullscreenWithNavigation(this);
        setContentView(R.layout.activity_edit_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get views by id
        title = (EditText) findViewById(R.id.title);
        date = (EditText) findViewById(R.id.date);
        desc = (EditText) findViewById(R.id.description);
        importance = (RadioGroup) findViewById(R.id.importance_group);

        save = (FloatingActionButton) findViewById(R.id.save_task);
        cancelDelete = (FloatingActionButton) findViewById(R.id.cancel_delete_task);

        // Get intent type
        final String intent = getIntent().getStringExtra("Intent");

        // For adding new tasks
        if (intent.matches("new")) {
            newTaskActivity();
        }
        // For editing existing tasks
        if (intent.matches("edit")) {
            editTaskActivity(intent);
        }

        // Open date picker
        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Open date picking dialog
                    showDatePickerDialog(v,date);
                }
            }
        });

        // Clear focus when hit enter
        setClearFocusOnEnter(title);
        setClearFocusOnEnter(desc);

        // Set value based on importance
        importance.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                importanceValue = setRadioValue(radioGroup.findViewById(i));
            }
        });
    }

    private void newTaskActivity() {
        // Set title and button icon
        this.setTitle(R.string.title_activity_new_task);
        cancelDelete.setImageDrawable(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_close_clear_cancel));

        // Save new tasks
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();

                if (inputValid()) {
                    long id = saveNewTask();

                    Intent intent = new Intent();
                    intent.putExtra("ID",id);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });

        // Cancel new tasks
        cancelDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    private void editTaskActivity(final String intent) {
        // Set title and button icon
        this.setTitle(R.string.title_activity_edit_task);
        cancelDelete.setImageDrawable(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_delete));

        // Get info from Task object to fill EditTexts
        final int id = getIntent().getIntExtra("ID",0);
        fillTask(id);

        // Saving existing task edits
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (intent.matches("edit")) {
                    // Save edits in Task object
                    saveTaskEdit(id);
                    // Pass result back to MainActivity
                    Intent intent = new Intent();
                    intent.putExtra("Action", "edit");
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });

        // Delete existing tasks
        cancelDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Popup dialog to confirm cancellation of task
                new AlertDialog.Builder(CreateEditTask.this)
                        .setTitle(R.string.delete_question)
                        .setMessage(title.getText().toString())
                        .setCancelable(true)
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(R.string.delete,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface di, int i) {
                                        deleteTask(id);

                                        Intent intent = new Intent();
                                        intent.putExtra("Action", "delete");
                                        intent.putExtra("UndoPos", id);
                                        setResult(Activity.RESULT_OK, intent);
                                        finish();
                                    }
                                })
                        .show();
            }
        });
    }

    private boolean inputValid() {
        boolean isValid = false;
        if (title.getText().toString().isEmpty()) {
            Snackbar.make(findViewById(R.id.edit_activity),"Your task is empty! Add something to do!",Snackbar.LENGTH_LONG).show();
            title.requestFocus();
        } else if (date.getText().toString().isEmpty()) {
            Snackbar.make(findViewById(R.id.edit_activity),"There's no date! Every task needs a deadline!",Snackbar.LENGTH_LONG).show();
        } else {
            isValid = true;
        }

        return isValid;
    }

    private long saveNewTask() {
        // Get text from EditTexts at save
        titleString = title.getText().toString();
        descString = desc.getText().toString();

        // Store values to insert
        ContentValues values = new ContentValues();
        values.put(TaskProvider.COLUMN_TITLE, titleString);
        values.put(TaskProvider.COLUMN_DATE, dateData);
        values.put(TaskProvider.COLUMN_DESCRIPTION, descString);
        values.put(TaskProvider.COLUMN_IMPORTANCE, importanceValue);
        values.put(TaskProvider.COLUMN_COMPLETION_TODAY, 0);
        values.put(TaskProvider.COLUMN_COMPLETION_BEFORE, 0);

        // Insert new task values into database
        Uri uri = getContentResolver().insert(TaskProvider.CONTENT_URI, values);
        long id = ContentUris.parseId(uri);
        Log.i("New task","New task inserted into db; id="+id);

        return id;
    }

    private void fillTask(int id) {
        Uri uri = ContentUris.withAppendedId(TaskProvider.CONTENT_URI,id);
        c = new CursorLoader(this,uri,null,null,null,null).loadInBackground();
        Log.i("FillTask","Cursor ID is "+id);

        // Get date data from database
        dateData = c.getString(c.getColumnIndex(TaskProvider.COLUMN_DATE));

        // Set strings to display
        title.setText(c.getString(c.getColumnIndex(TaskProvider.COLUMN_TITLE)));
        date.setText(TaskRecyclerAdapter.formatDate(
                c.getString(c.getColumnIndex(TaskProvider.COLUMN_DATE))));
        desc.setText(c.getString(c.getColumnIndex(TaskProvider.COLUMN_DESCRIPTION)));
        setCheckedRadio(importance,c.getInt(c.getColumnIndex(TaskProvider.COLUMN_IMPORTANCE)));
    }

    private void saveTaskEdit(int id) {
        // Get text from EditTexts at save
        titleString = title.getText().toString();
        descString = desc.getText().toString();

        // Store values to insert
        ContentValues values = new ContentValues();
        values.put(TaskProvider.COLUMN_TITLE, titleString);
        values.put(TaskProvider.COLUMN_DATE, dateData);
        values.put(TaskProvider.COLUMN_DESCRIPTION, descString);
        values.put(TaskProvider.COLUMN_IMPORTANCE, importanceValue);

        // Update row in database
        Uri uri = ContentUris.withAppendedId(TaskProvider.CONTENT_URI,id);
        int count = getContentResolver().update(uri,values,null,null);
        if (count != 1) {
            throw new IllegalStateException("Unable to update id "+id);
        }
    }

    private void deleteTask(int id) {
        // Delete row from database
        Uri uri = ContentUris.withAppendedId(TaskProvider.CONTENT_URI,id);
        getContentResolver().delete(uri,null,null);
        Log.i("DeleteTask","Entry deleted: ID "+id);
    }

    private void showDatePickerDialog(View v, EditText field) {
        DialogFragment df = new DatePickerFragment();
        df.show(getFragmentManager(), "datePicker");
    }

    private int setRadioValue(View view) {
        // Is the button now checked?
        boolean checked;
        int value = -1;

        if (view != null) {
            checked = ((RadioButton) view).isChecked();
            // Check which radio button was clicked
            switch(view.getId()) {
                case R.id.importance1:
                    if (checked)
                        value = 1;
                    break;
                case R.id.importance2:
                    if (checked)
                        value = 2;
                    break;
                case R.id.importance3:
                    if (checked)
                        value = 3;
                    break;
                default: value = -1; break;
            }
        }

        return value;
    }

    private void setCheckedRadio(RadioGroup radioGroup,int importance) {
        switch (importance) {
            case 1: ((RadioButton)radioGroup.findViewById(R.id.importance1)).setChecked(true);
                importanceValue = 1; break;
            case 2: ((RadioButton)radioGroup.findViewById(R.id.importance2)).setChecked(true);
                importanceValue = 2; break;
            case 3: ((RadioButton)radioGroup.findViewById(R.id.importance3)).setChecked(true);
                importanceValue = 3; break;
        }
    }

    private void hideKeyboard() {
        InputMethodManager input = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        input.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }

    private void setClearFocusOnEnter(View v) {
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction()==KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    getCurrentFocus().clearFocus();
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });
    }


}
