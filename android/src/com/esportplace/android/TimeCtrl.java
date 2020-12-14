package com.esportplace.android;

import android.view.View;
import android.widget.TextView;

import android.support.v4.app.DialogFragment;

import java.util.Date;
import java.util.Calendar;

import android.app.TimePickerDialog;
import android.widget.TimePicker;

import com.leaguetor.StringUtil;


public class TimeCtrl extends DateCtrl {


    protected String formatDate() {
        return StringUtil.formatTime(mDate);
    }

    public void changeDate(View v) {
        CustomTimePicker d = new CustomTimePicker();

        d.setListener(new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hour, int minute) {

                Tracer.log("Picked time ", hour, minute);
                mDate = new Date(StringUtil.buildTime(hour, minute));
                Tracer.log("Built time " + mDate);            
                setDate(mDate);
            }
        });

        int H = StringUtil.getHour(mDate.getTime());
        int M = StringUtil.getMinute(mDate.getTime());
        Tracer.log("Has date " + mDate + " = " + H + ":" + M);
        d.init(H, M);

        d.show(mAct.getSupportFragmentManager(), mAct.getString(R.string.select_time));
    }
}
