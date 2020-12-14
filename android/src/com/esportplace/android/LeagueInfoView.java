package com.esportplace.android;

import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.TableLayout;
import android.widget.TableRow;

import com.leaguetor.net.ModelAccessor;
import com.leaguetor.entity.*;

import android.content.DialogInterface;

import java.util.List;


public class LeagueInfoView extends LeagueActivity  {

    List<String> mSports = createList();
    XSpinner mSSpinner = new XSpinner(this);
    //SpinnerSelector mSS = new SpinnerSelector();
    //Button mBSpinner;

    List<Sport> mSportList;

    boolean mCreating;

    int fillSports(int id) {
        if (mSportList == null)
            return -1;
        int idx = 0;
        for (Sport sp : mSportList) {
            if (sp.id == id)
                idx = mSports.size();

            mSports.add(sp.name);
        }
        return idx;
    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lginfo);
        int creating = getIntParam("create", false);
        mCreating = creating != 0;
        mSSpinner.initId(R.id.sport);
    }

    protected void asyncInit() {
        super.asyncInit();
        mSportList = ModelAccessor.model().getSportList();
    }

    protected void refresh() {
        mSports.clear();
        Tracer.log("Refreshing lg info, " + mCreating + ", sports " + 
            (mSportList == null ? 0 : mSportList.size()) + ", user " + mUser);


        if (!mCreating) {
            ((Button)findViewById(R.id.create)).setText(getString(R.string.save));
        } else {
            //findViewById(R.id.delete).setVisibility(View.GONE);
        }



        if (mLeague == null && !mCreating)
            return;

        int sel = fillSports(mLeague == null ? 0 : mLeague.sportId);
        

//        mBSpinner = (Button)findViewById(R.id.sport);
/*
        int sel = 0;
        if (mSportList != null) {
            for (int i = 0; i < mSportList.size(); i++) {
                Sport sp = mSportList.get(i);
                mSports.add(sp.name);
                if (!mCreating && mLeague.sportId == sp.id)
                    sel = i;
            }
            linkSpinner(R.id.sport, mSports, mSS);
            setSpinnerSelection(R.id.sport, sel);
        } else {
            findViewById(R.id.sport).setVisibility(View.GONE);
        }
*/

        if (!mCreating) {
            setTextField(R.id.name, mLeague.name);
            setTextField(R.id.location, mLeague.location);
        }
        mSSpinner.initData(mSports);
        mSSpinner.setSel(sel);

    }

    class CreateLeagueTask implements RunnableEx {
        LeagueInfo li;
        String lgid;
        public CreateLeagueTask(LeagueInfo l) {
            li = l;
        }
    
        public void run() {

            lgid = mCreating ? ModelAccessor.model().createLeague(li) : 
                ModelAccessor.model().saveLeague(li);
                    

        }
        public void done() {
            if (lgid == null || lgid.isEmpty()) {
                showTip(R.string.unex_err);
                return;
            }
/*
            if (mCreating)
                renderView(LeagueView.class, createMap("user", mUser, "league", lgid));
            else
                showTip(R.string.info_saved);
*/
        }
    }

    public void onCreateLeague(View v) {
        String name = getTextField(R.id.name);
        String location = getTextField(R.id.location);
        Sport sport = mSportList.get(mSSpinner.getSel());
        if (name.isEmpty()) {
            showTip(R.string.name_empty);
            return;
        }

        if (name.length() < 3) {
            showTip(R.string.name_min_len);
            return;
        }

        LeagueInfo li = mLeague;
        if (mCreating) {
            Tracer.log("Creating league " + name + ", " + location + ", " + sport);
            li = new LeagueInfo();
        } 
        li.name = name;
        li.location = location;
        li.sport = sport;
        li.ownername = mUser;
        executeAsyncEx(new CreateLeagueTask(li));
    }

    public void onDeleteLeague(View v) {    
/*
        showConfirm(getString(R.string.lg_del_confirm), R.string.delete, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int did) {
                Tracer.log("Deleting league " + mLeague);                

            if (ModelAccessor.model().deleteLeague(mLeague)) {
                renderView(UserProfile.class, createMap("user", mUser));
                finish();
            } else {
                showTip(R.string.unex_err);
            }
           }
         });
*/
    }

    public void onSpinner(View v) {
        mSSpinner.show(v.getId());
    }
}


