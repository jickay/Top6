package com.example.jickay.top6;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by ViJack on 6/21/2017.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

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
        String dateString = monthString + " " + Integer.toString(day);

        EditText field = (EditText) getActivity().findViewById(R.id.date);
        field.setText(dateString);
    }
}
