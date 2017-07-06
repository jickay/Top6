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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


public class EditTask extends AppCompatActivity {

    int importanceValue = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get views by id
        final EditText title = (EditText) findViewById(R.id.title);
        final EditText date = (EditText) findViewById(R.id.date);
        final EditText desc = (EditText) findViewById(R.id.description);
        RadioGroup importance = (RadioGroup) findViewById(R.id.importance_group);

        // Get info from Task object to fill EditTexts
        final Bundle b = getIntent().getBundleExtra("taskData");
        final Task currentTask = MainActivity.getIncompleteTasks().get(b.getInt("num"));
        fillTask(currentTask,title,date,desc,importance);

        FloatingActionButton save = (FloatingActionButton) findViewById(R.id.save_task);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Save edits in Task object
                saveTask(currentTask,title,date,desc);
                // Pass result back to MainActivity
                Intent intent = new Intent();
                intent.putExtra("Action","edit");
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        FloatingActionButton delete = (FloatingActionButton) findViewById(R.id.delete_task);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Popup dialog to confirm cancellation of task
                new AlertDialog.Builder(EditTask.this)
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
                                        intent.putExtra("Action","delete");
                                        intent.putExtra("UndoPos",taskPos);
                                        setResult(Activity.RESULT_OK, intent);
                                        finish();
                                    }
                                })
                        .show();
            }
        });

        final EditText dateField = (EditText) findViewById(R.id.date);
        dateField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickerDialog(v,dateField);
                }
            }
        });

        importance.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                importanceValue = onRadioButtonClicked(radioGroup.findViewById(i));
            }
        });
    }

    protected void fillTask(Task currentTask,
                            EditText title,
                            EditText date,
                            EditText desc,
                            RadioGroup importance) {
        title.setText(currentTask.getTitle());
        date.setText(currentTask.getDate());
        desc.setText(currentTask.getDescription());
        setCheckedRadio(currentTask,importance);
    }

    protected void saveTask(Task currentTask,
                            EditText title,
                            EditText date,
                            EditText desc) {
        currentTask.setTitle(title.getText().toString());
        currentTask.setDate(date.getText().toString());
        currentTask.setDescription(desc.getText().toString());
        currentTask.setImportance(importanceValue);
    }

    protected int deleteTask(Task currentTask) {
        // Get index of currentTask
        int taskPos = MainActivity.getIncompleteTasks().indexOf(currentTask);
        // Move to deletedTasks arraylist if deletion confirmed
        MainActivity.getIncompleteTasks().remove(currentTask);
        MainActivity.getDeletedTasks().add(currentTask);

        return taskPos;
    }

    public void showDatePickerDialog(View v, EditText field) {
        DialogFragment df = new DatePickerFragment();
        df.show(getFragmentManager(), "datePicker");
    }

    public int onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        int value = -1;

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
        }

        return value;
    }

    private void setCheckedRadio(Task currentTask, RadioGroup radioGroup) {
        switch (currentTask.getImportance()) {
            case 1: ((RadioButton)radioGroup.findViewById(R.id.importance1)).setChecked(true); break;
            case 2: ((RadioButton)radioGroup.findViewById(R.id.importance2)).setChecked(true); break;
            case 3: ((RadioButton)radioGroup.findViewById(R.id.importance3)).setChecked(true); break;
            case 4: ((RadioButton)radioGroup.findViewById(R.id.importance4)).setChecked(true); break;
            case 5: ((RadioButton)radioGroup.findViewById(R.id.importance5)).setChecked(true); break;
        }
    }

}
