package com.example.jickay.top6;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class NewTask extends AppCompatActivity {
    static EditText dateField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get components by id
        final EditText title = (EditText) findViewById(R.id.title);
        final EditText date = (EditText) findViewById(R.id.date);
        final EditText desc = (EditText) findViewById(R.id.description);

        FloatingActionButton save = (FloatingActionButton) findViewById(R.id.save_new_task);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = saveTask(title,date,desc);

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

        dateField = (EditText) findViewById(R.id.date);
        dateField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickerDialog(v);
                }
            }
        });
    }

    protected Bundle saveTask(EditText title,
                              EditText date,
                              EditText desc) {
        Bundle b = new Bundle();

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


