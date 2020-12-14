package com.esportplace.android;

import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TableLayout;
import android.widget.TableRow;

import android.content.res.Resources;

import com.leaguetor.net.ModelAccessor;
import com.leaguetor.net.ModelUtil;
import com.leaguetor.StringUtil;
import com.leaguetor.ThriftUtil;
import com.leaguetor.entity.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


public class LeagueDash extends LeagueActivity implements XSpinner.Selector {

    List<DivInfo> mDivs;
    List<Team> mTeams;
    List<Division> mFullDivs;
    List<TourInfo> mTours;
    List<String> mDivNames = new ArrayList();
    Map<Integer, Team> mTeamMap = new HashMap();
    int mMode = 0;  
    View mCreator;
    EditText mNewName;
    Runnable mRefresh = new Runnable() {
        public void run() {
            prepareDivs();
            refresh();
        }
    };
    String mMsg;

    TextView mTeamHdr;

    TextView mDivHdr;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lgteams);
        mCreator = findViewById(R.id.create_bar);
        mCreator.setVisibility(View.GONE);
        mNewName = (EditText)mCreator.findViewById(R.id.newname);

        mTeamHdr = (TextView)findViewById(R.id.team_hdr);
        mDivHdr = (TextView)findViewById(R.id.div_hdr);
    
    }



    void prepareDivs() {
        mFullDivs = ModelUtil.makeDivs(mDivs, mTeams);
        if (mFullDivs != null)
            Tracer.log("League has " + mFullDivs.size() + " divs");

        mDivNames.clear();
        if (mFullDivs != null) {
            for (Division d : mFullDivs) {
                if (d.id == 0)
                    d.name = getString(R.string.undiv);

                mDivNames.add(d.name);
            }
        }

        Tracer.log("DivNames: " + mDivNames);

    }

    protected void asyncInit() {
        super.asyncInit();
        if (mLeague == null)
            return;
        mTeams = ModelAccessor.model().getTeams(mLeague.uname);
        mDivs = ModelAccessor.model().getDivs(mLeague.uname);
        for (Team t : mTeams)
            mTeamMap.put(t.id, t);
        prepareDivs();
        mTours = ModelAccessor.model().getTours(mLeague.uname);
    }

/*
    class TeamClickListener implements View.OnClickListener {
        String team;

        TeamClickListener(String t) {
            team = t;
        } 

        public void onClick(View v){
            renderView(AddTeam.class, createMap("team", team, "league", mLeague.uname));
        }
    }

    class DivClickListener implements View.OnClickListener {
        String div;

        DivClickListener(String t) {
            div = t;
        } 

        public void onClick(View v){
            renderView(CreateDiv.class, createMap("div", div, "league", mLeague.uname));
        }
    }
*/


    protected void refresh() {
        
        setTitle(getString(R.string.league) + "[" + mLeague.name + "]");

        if (mMsg != null) {
            showTip(mMsg);
            mMsg = null;
        }

        ToursFragment tf = (ToursFragment)getSupportFragmentManager().findFragmentById(R.id.trs_fragment);
        tf.setData(mTours, 2);
        tf.fill();

        Tracer.log("Refresh teams");
        TableLayout ll = (TableLayout)findViewById(R.id.table);
        ll.removeAllViews();

        //for (Team tm : lt) {
        for (Division d : mFullDivs) {


            //TableRow drow = createRow(R.layout.divrow, ll);
            //((TextView)drow.findViewById(R.id.name)).setText(d.name);
            //drow.findViewById(R.id.show).setVisibility(View.GONE);

            TextView dhdr = (TextView)getLayoutInflater().inflate(R.layout.theader, null);
            dhdr.setText(d.name);
            ll.addView(dhdr);

            //addSeparator(ll, 3, R.color.black);

            /*
            if (d.id > 0)
                drow.setOnClickListener(new DivClickListener(d.uname));
            */

            if (d.teams != null) {
                for (int i = 0; i < d.getTeamsSize(); i++) {
                    Team t = d.teams.get(i);
                    TableRow trow = createRow(R.layout.teamrow, ll);
                    trow.setId(t.id);
                    ((TextView)trow.findViewById(R.id.team_name)).setText(t.name);
                    //trow.setOnClickListener(new TeamClickListener(t.uname));
                    Button btn = (Button)trow.findViewById(R.id.tmdiv);
                    XSpinner spinner = new XSpinner(this);
                    spinner.initCtrl(btn);
                    spinner.initData(mDivNames);
                    spinner.setSel(d.name);
                    spinner.setId(t.id);
                    //btn.setId(t.id);
                    spinner.setListener(this);


                    if (mFullDivs.size() < 2)
                        btn.setVisibility(View.GONE);


                    addSeparator(ll, i < d.getTeamsSize() - 1);
                    
                }
            }
        }
    }

    public void onCreateTeam(View v) {
        //renderView(AddTeam.class, createMap("user", mUser, "league", mLeagueId));
        if (mMode != 1) {
            mMode = 1;
            mCreator.setVisibility(View.VISIBLE);
            mNewName.setHint(getString(R.string.team_name));
            //mTrHdr.setDrawable(R.drawable.arrow_up_float);
        } else {
            mMode = 0;
            mCreator.setVisibility(View.GONE);
            //mTrHdr.setDrawable(R.drawable.plus);
        }
    }

    public void onCreateDiv(View v) {
        //renderView(CreateDiv.class, createMap("user", mUser, "league", mLeagueId));
        if (mMode != 2) {
            mMode = 2;
            mCreator.setVisibility(View.VISIBLE);
            mNewName.setHint(getString(R.string.div_name));
            //mDivHdr.setDrawable(R.drawable.arrow_up_float);
        } else {
            mMode = 0;
            mCreator.setVisibility(View.GONE);
            //mTrHdr.setDrawable(R.drawable.plus);
        }

    }
/*
    public void onSpinner(View v) {
        int vid = v.getId();
        Tracer.log("Active spinner " + vid);
        
        XSpinner sp = mSpinners.get(vid);
        sp.show(vid);
    }
*/

    public void onSelected(int id, int sel, String val) {
        Division div = mFullDivs.get(sel);
        final Team t = mTeamMap.get(id);
        Tracer.log("Selected div " + div.name + " for team " + t.id + "(" + t.divId + ")");
        if (t.divId != div.id) {

            t.divId = div.id;
            executeAsync(new Runnable() {
                public void run() {
                    ModelAccessor.model().saveTeam(t);
                }}, mRefresh
            );
        }
    }

    public void onCreateConfirmed(View v) {
        String name = mNewName.getText().toString();

        if (mMode == 1) {
            final Team tm = new Team();
            tm.name = name;
            tm.leagueName = mLeagueId;
            Tracer.log("Creating team " + tm);
            executeAsync(new Runnable() {
                public void run() {
                    String tname = ModelAccessor.model().createTeam(tm);
                    boolean fail = StringUtil.emptyOrNull(tname);
                    mMsg = getString(fail ? R.string.unex_err : R.string.info_saved);
                    if (!fail)
                        mTeams = ModelAccessor.model().getTeams(mLeague.uname);
                }}, mRefresh
            );
        } else if (mMode == 2) {
            final DivInfo div = new DivInfo();
            div.name = name;
            div.leagueName = mLeagueId;
            Tracer.log("Creating div " + div);
            executeAsync(new Runnable() {
                public void run() {
                    String dname = ModelAccessor.model().createDiv(div);
                    boolean fail = StringUtil.emptyOrNull(dname);
                    mMsg = getString(fail ? R.string.unex_err : R.string.info_saved);
                    if (!fail)
                        mDivs = ModelAccessor.model().getDivs(mLeague.uname);
                }}, mRefresh
            );
        }
        hideKeyboard(mNewName);
        mNewName.setText("");
        mCreator.setVisibility(View.GONE);
    }


    public void onManageTour(View v) {
        TourInfo tr = ThriftUtil.findList(mTours, v.getId());
        if (tr != null)
            renderView(TourDash.class, createMap("user", mUser, "tour", tr.uname));
    }

}


