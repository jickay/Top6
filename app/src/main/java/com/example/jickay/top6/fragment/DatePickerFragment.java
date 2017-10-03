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
        String monthName = "";
        switch (month) {
            case 0: monthName = "Jan"; break;
            case 1: monthName = "Feb"; break;
            case 2: monthName = "Mar"; break;
            case 3: monthName = "Apr"; break;
            case 4: monthName = "May"; break;
            case 5: monthName = "Jun"; break;
            case 6: monthName = "Jul"; break;
            case 7: monthName = "Aug"; break;
            case 8: monthName = "Sep"; break;
            case 9: monthName = "Oct"; break;
            case 10: monthName = "Nov"; break;
            case 11: monthName = "Dec"; break;
        }

        String monthString = addLeadingZero(month);
        String dayString = addLeadingZero(day);

        String dateString = monthName + " " + dayString;

        CreateEditTask.setDateData(Integer.toString(year) + "-" +
                                    monthString + "-" +
                                    dayString);
        CreateEditTask.setCalendar(year,month,day);

        dateField.setText(dateString);
        descField.requestFocus();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        dateField.clearFocus();
    }

    // Adding zero for single digit days for sorting
    private String addLeadingZero(int value) {
        String numString;
        if (value < 10) {
            numString = "0" + Integer.toString(value);
        } else {
            numString = Integer.toString(value);
        }
        return numString;
    }
}
