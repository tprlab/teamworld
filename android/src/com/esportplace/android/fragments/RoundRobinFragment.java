package com.esportplace.android;

import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.content.res.Resources;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.leaguetor.net.ModelAccessor;

import com.leaguetor.entity.*;
import com.leaguetor.Scheduler;
import com.leaguetor.StringUtil;


import java.util.List;
import java.util.ArrayList;
import android.app.Fragment;
import android.widget.Spinner;


public class RoundRobinFragment extends ScheduleFragment {

    int mMode = 0;
    BaseActivity.SpinnerSelector mSS = new BaseActivity.SpinnerSelector();
    Button mDates;
    

    protected int getFragmentId() {
        return R.layout.round_robin;
    }

    protected void init(View v){
        super.init(v);        

        List<String> opts = new ArrayList();
        opts.add(getString(R.string.rounds));
        opts.add(getString(R.string.games));
        
/*
        Button btn = (Button)mView.findViewById(R.id.schkind);
        XSpinner spinner = new XSpinner(mActivity);
        spinner.initCtrl(btn);
        spinner.initData(opts);

        spinner.setListener(new XSpinner.Selector() {
            public void onSelected(int id, int sel, String val){
                mMode = sel;
            }
        });
*/

        Spinner spinner = (Spinner) mView.findViewById(R.id.schkind);
        mActivity.linkSpinner(spinner, opts, mSS);

        Button btn_sch = (Button)mView.findViewById(R.id.schedule);
        btn_sch.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                onSchedule();
            }            
        });

        mDates = (Button)mView.findViewById(R.id.setdate);
        if (mDates != null) {
            mDates.setVisibility(StringUtil.emptyOrNull(mGames) ? View.GONE : View.VISIBLE);
            mDates.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    onDates();
                }            
            });
        }


    }

    public void fill() {
        if (mGames == null)
            return;
        TableLayout ll = (TableLayout)mView.findViewById(R.id.gtable);
        ll.removeAllViews();
        for (Game g : mGames) {
            String gdesc = g.team1.name + " - " + g.team2.name;
            TableRow row = (TableRow)LayoutInflater.from(mActivity).inflate(R.layout.gamerow, null);
            ((TextView)row.findViewById(R.id.gamedesc)).setText(gdesc);
            ll.addView(row);
        }

        mDates.setVisibility(StringUtil.emptyOrNull(mGames) ? View.GONE : View.VISIBLE);

    }


    void onSchedule() {

        Division d = mTour.divs.get(mDivId);
        if (d == null) {
            Tracer.log("No div " + mDivId);
            return;
        }

        if (d.getTeamsSize() != d.getTableSize()) {
            d.teams = null;
            for (TableRecord t : d.table)
                d.addToTeams(t.team);
        }

        Tracer.log("Scheduling div " + d.name + " with teams " + d.getTeamsSize());

        EditText e_rnd = (EditText)mView.findViewById(R.id.rounds);
        int rounds = StringUtil.parseInt(e_rnd.getText().toString());
        int lg = 0;
        int tr = 0;
        int div = 0;
        List<Game> games = Scheduler.scheduleRoundRobin(mTour.league.id, mTour.id, mDivId, d.teams, rounds, mMode);
        mGames = games;
        Tracer.log("Scheduled " + StringUtil.list_size(mGames) + " games");
        fill();
        mActivity.hideKeyboard(mView);
    }

    void onDates(){
        mActivity.renderView(DatesActivity.class);
    }
}

