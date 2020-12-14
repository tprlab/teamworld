package com.esportplace.android;

import android.view.View;
import android.widget.TextView;

import android.support.v4.app.DialogFragment;

import java.util.Date;

import android.app.DatePickerDialog;
import android.widget.DatePicker;

import com.leaguetor.StringUtil;

public class DateCtrl {

    Date mDate;
    TextView mCtrl;
    static final String DATE_MASK = "dd MMM";
    Date mDefault;
    BaseActivity mAct;

    public void setDefault(Date dt) {
        mDefault = dt;
    }

    public Date getDate() {
        return mDate;
    }

    protected String formatDate() {
        return StringUtil.formatDate(mDate, DATE_MASK);
    }

    void setDate(Date dt) {
        mDate = dt;
        String str = formatDate();
        mCtrl.setText(str);
    }

    public void init(BaseActivity act, View v, int id) {
        mAct = act;
        mCtrl = (TextView)v.findViewById(id);
        mCtrl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeDate(v);
            }
        });

        if (mDefault == null)
            mDefault = new Date(StringUtil.now());
        Tracer.log("Def date " + mDefault);
        setDate(mDefault);  
    }

    public void changeDate(View v) {
        CustomDatePicker d = new CustomDatePicker();

        d.setListener(new DatePickerDialog.OnDateSetListener () {
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Tracer.log("Picked date ", year, month, day);
                mDate = StringUtil.buildDate(year, month, day);
                setDate(mDate);
            }
        });

        d.show(mAct.getSupportFragmentManager(), mAct.getString(R.string.select_date));
    }
}
