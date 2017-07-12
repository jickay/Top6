package com.example.jickay.top6;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.jickay.top6.fragment.DatePickerFragment;

import java.util.Calendar;


public class CreateEditTask extends AppCompatActivity {

    int LEADING_DAYS = 10;

    EditText title;
    EditText date;
    EditText desc;
    EditText dateField;

    RadioGroup importance;
    int importanceValue = -1;

    FloatingActionButton save;
    FloatingActionButton cancelDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get views by id
        title = (EditText) findViewById(R.id.title);
        date = (EditText) findViewById(R.id.date);
        desc = (EditText) findViewById(R.id.description);
        dateField = (EditText) findViewById(R.id.date);
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
        dateField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    showDatePickerDialog(v,dateField);
                }
            }
        });

        // Set character limit for title and description


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
                    Bundle b = saveNewTask(title, date, desc);

                    Intent intent = new Intent();
                    intent.putExtra("NewTask", b);
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
        final Bundle b = getIntent().getBundleExtra("taskData");
        final Task currentTask = MainActivity.getIncompleteTasks().get(b.getInt("num"));
        fillTask(currentTask, title, date, desc, importance);

        // Delete existing tasks
        cancelDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Popup dialog to confirm cancellation of task
                new AlertDialog.Builder(CreateEditTask.this)
                        .setTitle(R.string.delete_question)
                        .setMessage(currentTask.getTitle())
                        .setCancelable(true)
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(R.string.delete,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface di, int i) {
                                        int taskPos = deleteTask(currentTask);

                                        Intent intent = new Intent();
                                        intent.putExtra("Action", "delete");
                                        intent.putExtra("UndoPos", taskPos);
                                        setResult(Activity.RESULT_OK, intent);
                                        finish();
                                    }
                                })
                        .show();
            }
        });

        // Saving existing task edits
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (intent.matches("edit")) {
                    // Save edits in Task object
                    saveTaskEdit(currentTask, title, date, desc);
                    // Pass result back to MainActivity
                    Intent intent = new Intent();
                    intent.putExtra("Action", "edit");
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
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

    private Bundle saveNewTask(EditText title,
                              EditText date,
                              EditText desc) {
        Bundle b = new Bundle();

        b.putString("title",title.getText().toString());
        b.putString("date",date.getText().toString());
        b.putString("description",desc.getText().toString());
        b.putInt("importance",importanceValue);

        return b;
    }

    private void fillTask(Task currentTask,
                            EditText title,
                            EditText date,
                            EditText desc,
                            RadioGroup importance) {
        title.setText(currentTask.getTitle());
        date.setText(currentTask.getDate());
        desc.setText(currentTask.getDescription());
        setCheckedRadio(currentTask,importance);
    }

    private void saveTaskEdit(Task currentTask,
                                EditText title,
                                EditText date,
                                EditText desc) {
        currentTask.setTitle(title.getText().toString());
        currentTask.setDate(date.getText().toString());
        currentTask.setDescription(desc.getText().toString());
        currentTask.setImportance(importanceValue);
        MainActivity.getUrgency(currentTask);
    }

    private int deleteTask(Task currentTask) {
        // Get index of currentTask
        int taskPos = MainActivity.getIncompleteTasks().indexOf(currentTask);
        // Move to deletedTasks arraylist if deletion confirmed
        MainActivity.getIncompleteTasks().remove(currentTask);
        MainActivity.getDeletedTasks().add(currentTask);

        return taskPos;
    }

    private void showDatePickerDialog(View v, EditText field) {
        DialogFragment df = new DatePickerFragment();
        df.show(getFragmentManager(), "datePicker");
    }

    private int setRadioValue(View view) {
        // Is the button now checked?
        boolean checked = false;
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
                case R.id.importance4:
                    if (checked)
                        value = 4;
                    break;
                case R.id.importance5:
                    if (checked)
                        value = 5;
                    break;
                default: value = -1; break;
            }
        }

        return value;
    }

    private void setCheckedRadio(Task currentTask, RadioGroup radioGroup) {
        switch (currentTask.getImportance()) {
            case 1: ((RadioButton)radioGroup.findViewById(R.id.importance1)).setChecked(true);
                importanceValue = 1; break;
            case 2: ((RadioButton)radioGroup.findViewById(R.id.importance2)).setChecked(true);
                importanceValue = 2; break;
            case 3: ((RadioButton)radioGroup.findViewById(R.id.importance3)).setChecked(true);
                importanceValue = 3; break;
            case 4: ((RadioButton)radioGroup.findViewById(R.id.importance4)).setChecked(true);
                importanceValue = 4; break;
            case 5: ((RadioButton)radioGroup.findViewById(R.id.importance5)).setChecked(true);
                importanceValue = 5; break;
        }
    }

    private int getProgress(int taskDay) {
        //Calculate value for progress bar
        Calendar c = Calendar.getInstance();
        int currentDay = c.get(Calendar.DAY_OF_MONTH);
        int daysInMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        int difference;
        if (taskDay >= currentDay) {
            difference = taskDay - currentDay;
        } else {
            int remainingDays = daysInMonth - currentDay;
            difference = taskDay + remainingDays;
        }

        int daysRemaining = LEADING_DAYS-difference;
        int progressValue = 0;

        //Set progress bar length
        if (difference <= LEADING_DAYS) {
            progressValue = 100/LEADING_DAYS * daysRemaining;
        }

        return progressValue;
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
