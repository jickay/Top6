package com.example.jickay.top6.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.jickay.top6.CreateEditTask;
import com.example.jickay.top6.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ViJack on 6/21/2017.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{
    EditText dateField;
    EditText descField;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        dateField = (EditText) getActivity().findViewById(R.id.date);
        descField = (EditText) getActivity().findViewById(R.id.description);

        return new DatePickerDialog(getActivity(),this,year,month,day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        String monthString = "";
        switch (month) {
            case 0: monthString = "Jan"; break;
            case 1: monthString = "Feb"; break;
            case 2: monthString = "Mar"; break;
            case 3: monthString = "Apr"; break;
            case 4: monthString = "May"; break;
            case 5: monthString = "Jun"; break;
            case 6: monthString = "Jul"; break;
            case 7: monthString = "Aug"; break;
            case 8: monthString = "Sep"; break;
            case 9: monthString = "Oct"; break;
            case 10: monthString = "Nov"; break;
            case 11: monthString = "Dec"; break;
        }

        // Adding zero for single digit days for sorting
        String dayString = "";
        if (day < 10) {
            dayString = "0" + Integer.toString(day);
        } else {
            dayString = Integer.toString(day);
        }

        String dateString = monthString + " " + dayString;

        CreateEditTask.setDateData(Integer.toString(year) + "-" +
                                    Integer.toString(month) + "-" +
                                    dayString);

        dateField.setText(dateString);
        descField.requestFocus();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        dateField.clearFocus();
    }
}
