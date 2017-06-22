package com.example.jickay.top6;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import static com.example.jickay.top6.MainActivity.allTasks;

public class EditTask extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final EditText title = (EditText) findViewById(R.id.edit_title);
        final EditText date = (EditText) findViewById(R.id.edit_date);
        final EditText desc = (EditText) findViewById(R.id.edit_description);

        Bundle b = getIntent().getBundleExtra("taskData");
        final Task currentTask = allTasks.get(b.getInt("num"));
        inflateTask(currentTask,title,date,desc);

        FloatingActionButton save = (FloatingActionButton) findViewById(R.id.save_task);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTask(currentTask,title,date,desc);

                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        FloatingActionButton delete = (FloatingActionButton) findViewById(R.id.delete_task);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTask(currentTask);

                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
            }
        });

        EditText dateField = (EditText) findViewById(R.id.edit_date);
        dateField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickerDialog(v);
                }
            }
        });
    }

    protected void inflateTask(Task currentTask,
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

    protected void deleteTask(Task currentTask) {
        allTasks.remove(currentTask);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment df = new DatePickerFragment();
        df.show(getFragmentManager(), "datePicker");
    }

}
