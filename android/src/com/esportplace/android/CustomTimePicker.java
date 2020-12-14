package com.esportplace.android;

import android.app.TimePickerDialog;
import android.support.v4.app.DialogFragment;
import android.app.Dialog;
import android.widget.TimePicker;

import java.util.Calendar;
import android.text.format.DateFormat;

import android.os.Bundle;


public class CustomTimePicker extends DialogFragment {

    TimePickerDialog.OnTimeSetListener mListener;

    public int mHour = -1;
    public int mMinute = -1;

    public void setListener(TimePickerDialog.OnTimeSetListener listener) {
        mListener = listener;
    }

    public void init(int h, int m) {
        mHour = h;
        mMinute = m;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = mHour > -1 ? mHour : c.get(Calendar.HOUR_OF_DAY);
        int minute = mMinute > -1 ? mMinute : c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), mListener, hour, minute, false);
                //DateFormat.is24HourFormat(getActivity()));
    }

}