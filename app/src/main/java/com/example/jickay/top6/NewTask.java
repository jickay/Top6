package com.example.jickay.top6;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.jickay.top6.fragment.DatePickerFragment;

import java.util.Calendar;

public class NewTask extends AppCompatActivity {

    int LEADING_DAYS = 10;

    EditText dateField;
    int importanceValue = -1;

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
        final RadioGroup importance = (RadioGroup) findViewById(R.id.importance_group);

        FloatingActionButton save = (FloatingActionButton) findViewById(R.id.save_new_task);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int urgency = getProgress(Integer.parseInt(date.getText().toString().split(" ")[1]));
                Bundle b = saveTask(title,date,desc,urgency);

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

        importance.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                importanceValue = onRadioButtonClicked(radioGroup.findViewById(i));
            }
        });
    }

    protected Bundle saveTask(EditText title,
                              EditText date,
                              EditText desc,
                              int urgency) {
        Bundle b = new Bundle();

        b.putString("title",title.getText().toString());
        b.putString("date",date.getText().toString());
        b.putString("description",desc.getText().toString());
        b.putInt("importance",importanceValue);
        b.putInt("urgency",urgency);

        return b;
    }

    public void showDatePickerDialog(View v) {
        DialogFragment df = new DatePickerFragment();
        df.show(getFragmentManager(), "datePicker");
    }

    public int onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        int value = -1;

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.importance_low:
                if (checked)
                    value = 1;
                    break;
            case R.id.importance_med:
                if (checked)
                    value = 2;
                    break;
            case R.id.importance_high:
                if (checked)
                    value = 3;
                break;
        }

        return value;
    }

    private int getProgress(int taskDay) {
        //Calculate value for progress bar
        final Calendar c = Calendar.getInstance();
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

}


