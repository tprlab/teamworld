package com.esportplace.android;

import android.app.DatePickerDialog;
import android.support.v4.app.DialogFragment;
import android.app.Dialog;
import android.widget.DatePicker;

import java.util.Calendar;
import android.text.format.DateFormat;

import android.os.Bundle;



public class CustomDatePicker extends DialogFragment {

    public int mYear;
    public int mMonth;
    public int mDay;

    DatePickerDialog.OnDateSetListener mListener;

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        mListener = listener;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), mListener, year, month, day);
    }

}