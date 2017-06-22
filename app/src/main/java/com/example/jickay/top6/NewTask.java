package com.example.jickay.top6;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.app.FragmentManager;
import android.widget.Toast;

import java.util.Calendar;

public class NewTask extends AppCompatActivity {
    static EditText dateField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton save = (FloatingActionButton) findViewById(R.id.save_new_task);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = saveTask();

                Intent intent = new Intent();
                intent.putExtra("NewTask", b);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        FloatingActionButton cancel = (FloatingActionButton) findViewById(R.id.cancel_new_task);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
            }
        });

        dateField = (EditText) findViewById(R.id.new_date);
        dateField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickerDialog(v);
                }
            }
        });
    }

    protected Bundle saveTask() {
        Bundle b = new Bundle();

        EditText title = (EditText) findViewById(R.id.new_title);
        EditText date = (EditText) findViewById(R.id.new_date);
        EditText desc = (EditText) findViewById(R.id.new_description);
        b.putString("title",title.getText().toString());
        b.putString("date",date.getText().toString());
        b.putString("description",desc.getText().toString());

        return b;
    }

    public void showDatePickerDialog(View v) {
        DialogFragment df = new DatePickerFragment();
        df.show(getFragmentManager(), "datePicker");
    }

}


