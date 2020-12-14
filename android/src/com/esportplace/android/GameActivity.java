package com.esportplace.android;

import android.os.Bundle;

import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.View;
import android.widget.EditText;

import android.support.v4.app.DialogFragment;


import java.util.List;
import java.util.Date;

import com.leaguetor.entity.*;
import com.leaguetor.net.ModelAccessor;
import com.leaguetor.StringUtil;

import android.app.TimePickerDialog;
import android.widget.TimePicker;
import android.app.DatePickerDialog;
import android.widget.DatePicker;



public class GameActivity extends TourInfoActivity  {

    Game mGame;
    int mGameId;
    boolean mResult;
    static final String DATE_MASK = "dd MMM";
    Date mDate;
    Date mTime;
    ScoreCtrl mScore1;
    ScoreCtrl mScore2;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        mGameId = getIntParam("game", true);
        View v = findViewById(R.id.game_main);
        mScore1 = new ScoreCtrl(v, R.id.inc1, R.id.dec1, R.id.score1);
        mScore2 = new ScoreCtrl(v, R.id.inc2, R.id.dec2, R.id.score2);
    }

    protected void asyncInit() {
        mGame = ModelAccessor.model().getGame(mGameId);
    }

    protected void refresh() {
        if (mGame == null) {
            Tracer.log("No game for setscore");
            finish();
        }
        String title = mGame.team1.name + " - " + mGame.team2.name;
        setTitle(title);

        Tracer.log("Game", mGame);
        setText(R.id.team1, mGame.team1.name);
        setText(R.id.team2, mGame.team2.name);
        String str_dt = "";
        String str_tm = "";

        if (mGame.scheduled > 0) {
            Date dt = new Date(mGame.scheduled);
            str_dt = StringUtil.formatDate(dt, DATE_MASK);
            str_tm = StringUtil.formatTime(dt);
        } else {
            str_dt = getString(R.string.no_date);
            str_tm = getString(R.string.no_time);
        }

        setText(R.id.date, str_dt);
        setText(R.id.time, str_tm);

        if (mGame.status != 0) {
            setText(R.id.score1, "" + mGame.score1);
            setText(R.id.score2, "" + mGame.score2);
        }
    }

    public void onSaveGame(View view) {
        int s1 = mScore1.getScore(-1);
        int s2 = mScore2.getScore(-1);

        boolean scored = s1 != -1 || s2 != -1;
        if (scored) {
            mGame.score1 = s1 == -1 ? 0 : s1;
            mGame.score2 = s2 == -1 ? 0 : s2;
            mGame.status = 1;
        }

        if (mDate != null || mTime != null) {
            Date dt = mDate;
            if (dt == null)
                dt = mGame.scheduled > 0 ? new Date(mGame.scheduled) : new Date();
            Date tm = mTime != null ? mTime : mGame.scheduled > 0 ? new Date(mGame.scheduled) : null;
            Date d = StringUtil.combineDate(dt, tm);
            Tracer.log("Combining date", dt, tm, d);
            mGame.scheduled = d.getTime();
        }

        executeAsync(
            new Runnable() {
              public void run() {
                mResult = ModelAccessor.model().saveGame(mGame);
              }        
            }, 
            new Runnable() {
              public void run() {
                if (mResult)
                    finish();
                else
                    showTip(getString(R.string.unex_err));
              }
            }        
        );
    }


    public void changeTime(View v) {
        CustomTimePicker d = new CustomTimePicker();

        d.setListener(new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hour, int minute) {

                Tracer.log("Picked time ", hour, minute);
                Date dt = new Date(StringUtil.buildTime(hour, minute));
                String str = StringUtil.formatTime(dt);
                setText(R.id.time, str);
                mTime = dt;
            }
        });
    
        d.show(getSupportFragmentManager(), "select time");
    }

    public void changeDate(View v) {
        CustomDatePicker d = new CustomDatePicker();

        d.setListener(new DatePickerDialog.OnDateSetListener () {
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Tracer.log("Picked date ", year, month, day);
                Date dt = StringUtil.buildDate(year, month, day);
                String str = StringUtil.formatDate(dt, DATE_MASK);
                setText(R.id.date, str);
                mDate = dt;
            }
        });

        d.show(getSupportFragmentManager(), "select date");
    }




}


