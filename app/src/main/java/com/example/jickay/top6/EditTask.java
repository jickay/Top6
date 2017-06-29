package com.example.jickay.top6;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class EditTask extends AppCompatActivity {

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

        // Get info from Task object to fill EditTexts
        final Bundle b = getIntent().getBundleExtra("taskData");
        final Task currentTask = MainActivity.getIncompleteTasks().get(b.getInt("num"));
        fillTask(currentTask,title,date,desc);

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
    }

    protected void fillTask(Task currentTask,
                               EditText title,
                               EditText date,
                               EditText desc) {
        title.setText(currentTask.getTitle());
        date.setText(currentTask.getDate());
        desc.setText(currentTask.getDescription());
    }

    protected void saveTask(Task currentTask,
                            EditText title,
                            EditText date,
                            EditText desc) {
        currentTask.setTitle(title.getText().toString());
        currentTask.setDate(date.getText().toString());
        currentTask.setDescription(desc.getText().toString());
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

}
