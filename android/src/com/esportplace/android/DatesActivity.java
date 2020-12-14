package com.esportplace.android;

import android.os.Bundle;

import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.support.v4.app.DialogFragment;


import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.leaguetor.entity.*;
import com.leaguetor.net.ModelAccessor;
import com.leaguetor.StringUtil;

import android.app.TimePickerDialog;
import android.widget.TimePicker;
import android.app.DatePickerDialog;
import android.widget.DatePicker;


import java.text.DateFormatSymbols;

import android.content.DialogInterface;
import android.app.AlertDialog;


public class DatesActivity extends TourActivity  {

    DateCtrl mDate = new DateCtrl();
    TimeCtrl mTime = new TimeCtrl();
    TimeCtrl mTimeE = new TimeCtrl();
    SpinnerSelector mSS = new SpinnerSelector();
    Spinner mSpinner;
    List<Game> mGames;
    boolean mResult;

    int mDivId;


    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mDivId = getIntParam("div", false);
        setContentView(R.layout.dates);
        View l = findViewById(R.id.dates_layout);

        mDate.init(this, l, R.id.start_date);
        mTime.init(this, l, R.id.start_time);
        mTimeE.init(this, l, R.id.end_time);
        DateFormatSymbols dfs = new DateFormatSymbols();

        Button spinner_btn = (Button)findViewById(R.id.day_week);
        mSS.setBtn(spinner_btn);

        mSpinner = (Spinner) findViewById(R.id.spinner_day_week);
        List<String> days = Arrays.asList(dfs.getWeekdays());
        days.set(0, getString(R.string.select_day));
        linkSpinner(mSpinner, days, mSS);
    }

    protected void asyncInit() {
        super.asyncInit();
        if (mTour == null) {
            Tracer.log("No tour");
            return;
        }
        if (mTour.divs != null)
            mDiv =  mTour.divs.get(mDivId);
        Tracer.log("Selected div " + mDiv);
    }

    protected void refresh() {
        long tm = StringUtil.buildTime(23, 00);
        Tracer.log("Endtime is " + tm);
        mTimeE.setDate(new Date(tm));
        mGames = getDivGames();
        int cnt = 0;
        int size = 0;
        if (mGames != null) {
            size = mGames.size();
            for (Game g : mGames) {
                if (g.scheduled == 0)
                    cnt++;
            }
        }
        TextView unsch = (TextView)findViewById(R.id.unsch_games);
        unsch.setText(getString(R.string.unsch_games, cnt, size));
    }

    public void onReset(View v){
        if (mGames == null)
            return;

        for (Game g : mGames)
            g.scheduled = 0;


        executeAsync(
            new Runnable() {
              public void run() {
                ModelAccessor.model().resetDates(mTour.uname, mDiv.id, mGames);
              }
            }, 
            new Runnable() {
              public void run() {
                refresh();
              }
            }        
        );
    }

    public void onDay(View w) {
        mSpinner.performClick();
    }

    

    public void onSaveDates(View v) {
        int day = mSS.pos;
        Date dt = mDate.getDate();
        Date tm = mTime.getDate();
        Date tmE = mTimeE.getDate();
        String len_str = getTextField(R.id.game_length);
        String pnum_str = getTextField(R.id.court_num);
        int len = StringUtil.parseInt(len_str);
        int pnum = StringUtil.parseInt(pnum_str);
        if (len < 1)
        {
            showTip(R.string.game_length_empty);
            findViewById(R.id.game_length).requestFocus();
            return;
        }

        if (pnum < 1)
        {
            showTip(R.string.court_num_empty);
            findViewById(R.id.court_num).requestFocus();
            return;
        }

        long t0 = tm.getTime() % StringUtil.DAY_MSEC;
        Tracer.log("Start time " + t0 + "(" + tm.getTime() + ")");
        int C = pnum;
        long T = t0;
        long E = tmE.getTime() % StringUtil.DAY_MSEC;
        Tracer.log("End time " + E + "(" + tmE.getTime() + ")");
        long M = len * 60000;
        long DD = day > 0 ? 7 * StringUtil.DAY_MSEC : StringUtil.DAY_MSEC;
        if (E < 1)
            E = StringUtil.DAY_MSEC;
 
        long D = StringUtil.getNearestDay(dt, day);
        String[] array = new String[mGames.size()];
        int idx = 0;
        for (Game g : mGames) {
            long gt = D + T;
            g.scheduled = gt;
            g.court = pnum - C + 1;
            C--;
            if (C == 0)
            {
                C = pnum;
                T += M;
            }
            if (T + M >= E)
            {
                D += DD;
                T = t0;
                C = pnum;
            }
            String str_dt = StringUtil.formatDate(new Date(g.scheduled));
            Tracer.log("Time for " + g.id + " is " + str_dt + "(" + g.scheduled + ") " + " on court " + g.court);
            String gstr = str_dt + " (" + g.court + ")\n" + g.team1.name + " - " + g.team2.name;
            array[idx++] = gstr;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.schedule)).setItems(array, null);
        builder.setNegativeButton("cancel", null).setPositiveButton(R.string.schedule, 
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    saveGames();
                }
            }
        );

        AlertDialog d = builder.create();
        d.show();


        Button pbtn = (Button)d.getButton(DialogInterface.BUTTON_POSITIVE);
        pbtn.setTextColor(getResources().getColor(R.color.white));
        pbtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bbutton));            

    }

    void saveGames() {
        executeAsync(new Runnable() {
                public void run() {
                    mResult = ModelAccessor.model().saveGames(mTour.uname, mGames);
                }
            },
            new Runnable(){
                public void run() {
                    refresh();
                }
            }
        );
    }


}


