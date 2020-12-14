package com.esportplace.android;

import android.os.Bundle;
    
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.leaguetor.entity.*;
import com.leaguetor.StringUtil;
import com.leaguetor.Scheduler;
import com.leaguetor.MathUtil;
import com.leaguetor.net.ModelAccessor;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;

import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import android.content.DialogInterface;
import android.app.AlertDialog;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;


public class ScheduleActivity extends TourActivity {

    int mDivIdx;
    List<String> mDivs = new ArrayList<String>();
    int mGamesNum;
    Spinner mDivSpinner;
    SpinnerSelector mDSS = new DivSpinnerSelector();

    String mDateMask = "dd MMM HH:mm";

    Spinner mKindSpinner;
    SpinnerSelector mKSS = new KindSpinnerSelector();
    List<String> mKinds = new ArrayList<String>();

    Spinner mModeSpinner;
    SpinnerSelector mMSS = new ModeSpinnerSelector();
    List<String> mModes = new ArrayList<String>();
    boolean mGamesShown;
    List<Game> mNewGames;
    boolean mResult;

    List<String> mStages;
    Spinner mStageSpinner;
    SpinnerSelector mSSS = new StageSpinnerSelector();




    class DivSpinnerSelector extends SpinnerSelector {

        public void onSelectElem(int pos) {
            Division div = mTour.divList.get(pos);
            Tracer.log("Selected item  " + pos + ": " + div);
            if (div == null)
                return;
            mDivIdx = pos;
            mDiv = div;
            fill();
        }

    }

    class KindSpinnerSelector extends SpinnerSelector {

        public void onSelectElem(int pos) {
            View v_rr = findViewById(R.id.round_robin);
            View v_po = findViewById(R.id.play_off);
            v_rr.setVisibility( pos == 0 ? View.VISIBLE : View.GONE);
            v_po.setVisibility( pos == 1 ? View.VISIBLE : View.GONE);
        }
    }

    class ModeSpinnerSelector extends SpinnerSelector {

        public void onSelectElem(int pos) {
        }
    }

    class StageSpinnerSelector extends SpinnerSelector {

        public void onSelectElem(int pos) {
        }
    }




    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sch);
        Button spinner_btn = (Button)findViewById(R.id.sch_div);
        mDSS.setBtn(spinner_btn);

        Button kspinner_btn = (Button)findViewById(R.id.sch_kind);
        mKSS.setBtn(kspinner_btn);

        Button mspinner_btn = (Button)findViewById(R.id.sch_mode);
        mMSS.setBtn(mspinner_btn);

        Button sspinner_btn = (Button)findViewById(R.id.stage);
        mSSS.setBtn(sspinner_btn);




        mDivSpinner = (Spinner) findViewById(R.id.spinner_div);
        mKindSpinner = (Spinner) findViewById(R.id.spinner_kind);
        mModeSpinner = (Spinner) findViewById(R.id.spinner_mode);
        mStageSpinner = (Spinner) findViewById(R.id.spinner_stage);

        mKinds.add(getString(R.string.round_robin));
        mKinds.add(getString(R.string.play_off));

        mModes.add(getString(R.string.rounds));
        mModes.add(getString(R.string.games));


        linkSpinner(mKindSpinner, mKinds, mKSS);
        linkSpinner(mModeSpinner, mModes, mMSS);

    }

    protected void asyncInit() {
        super.asyncInit();
        mDivs.clear();
        for (Division d : mTour.divList) {
            mDivs.add(d.name);
        }

        mDiv = mTour.divs.get(0);
        mDivIdx = 0;
    }

    protected void fill() {
        if (mDiv == null)
            return;

        mGamesNum = mDiv.getGamesSize();
        if (mDiv.id == 0) {
            for (Division d : mTour.divList) {
                if (d.id != 0)
                    mGamesNum += d.getGamesSize();
            }
        }        
        String str_games = String.format(getString(R.string.have_games), mGamesNum);
        setText(R.id.have_games, str_games);
        if (mTour != null) {
            String title = getString(R.string.schedule) + " " + mTour.name;
            if (mDiv.id != 0)
                title += "(" + mDiv.name + ")";
            setTitle(title);
        }

    }


    protected void refresh() {

        mDivs.set(0, getString(R.string.whole_tour));
        linkSpinner(mDivSpinner, mDivs, mDSS);
        if (mTour != null)
            setTitle(getString(R.string.schedule) + " " + mTour.name);

        mStages = getStages(mDiv.getGamesSize());
        linkSpinner(mStageSpinner, mStages, mSSS);
        

        fill();
    }

    public void showDivs(View v) {
        mDivSpinner.performClick();
    }

    public void showKind(View v) {
        mKindSpinner.performClick();
    }

    public void showGames(View v) {

        mGamesShown = !mGamesShown;

        TableLayout tl = (TableLayout)findViewById(R.id.game_table);
        tl.removeAllViews();
        if (!mGamesShown)
            return;
        List<Game> games = getDivGames();
/*
        if (mDiv.getGamesSize() < 1)
            return;
        List<Game> games = mDiv.games;
        if (mDiv.id == 0) {
            games = new ArrayList<Game>();
            for (Division d : mTour.divList) {
                if (d.games != null)
                    games.addAll(d.games);
            }
        }
*/

        for (Game g : games) {
            TableRow row = createRow(R.layout.gamerow, tl);
            TextView gdesc = (TextView)row.findViewById(R.id.gamedesc);
            String stage = "";
            if (g.stage < 0) {
                stage = GamesFragment.getStage(g.stage);
                TextView gidx = (TextView)row.findViewById(R.id.gameidx);
                gidx.setText(stage);
            }
            gdesc.setText(g.team1.name + " - " + g.team2.name);

            if (mDiv.id == 0 && g.divId != 0) {
                Division d = mTour.divs.get(g.divId);
                if (d != null) {
                    TextView tdiv = (TextView)row.findViewById(R.id.gamediv);
                    tdiv.setText(d.name);
                }
            }

            if (g.scheduled != 0) {
                TextView gdate = (TextView)row.findViewById(R.id.gamedate);
                String str_date = StringUtil.formatDate(new Date(g.scheduled), mDateMask);
                gdate.setText(str_date);
            } else {
               row.setBackgroundColor(getResources().getColor(R.color.pink));
            }

        }
        
    }

    String[] games2Text(List<Game> games) {
        String[] array = new String[games.size()];
        int idx = 0;
        for (Game g : games) {
            idx++;
            StringBuilder gdesc = new StringBuilder("" + idx + ". " + g.team1.name + " - " + g.team2.name);
            if (mDiv.id == 0) {
                Division gdiv = mTour.divs.get(g.divId);
                if (gdiv != null)
                    gdesc.append(" (").append(gdiv.name).append(")");
            }
            array[idx - 1] = gdesc.toString();
        }
        return array;
    }

    public void onScheduleRR(View v) {
        int mode = mMSS.pos;
        EditText e_rnd = (EditText)findViewById(R.id.rounds);
        int rounds = StringUtil.parseInt(e_rnd.getText().toString());
        int lg = 0;
        int tr = 0;
        int div = 0;
        Tracer.log("Schedule with mode " + mode +", rounds " + rounds);
        List<Game> games = null;
        if (mDiv.id != 0) {
            games = Scheduler.scheduleRoundRobin(mTour.league.id, mTour.id, mDiv.id, mDiv.teams, rounds, mode);
        } else {
            for (Division d : mTour.divList) {
                if (d.id == 0)
                    continue;
                List<Game> dgames = Scheduler.scheduleRoundRobin(mTour.league.id, mTour.id, d.id, d.teams, rounds, mode);
                Tracer.log("Generated games for div " + d.id + " " + dgames);
                if (games == null)
                    games = dgames;
                else
                    games.addAll(dgames);
            }
        }
        Tracer.log("Scheduled " + StringUtil.list_size(games) + " games");
        if (StringUtil.list_size(games) < 1)
            return;

        String[] array = games2Text(games);

        mNewGames = games;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
/*
        LayoutInflater inflater = getLayoutInflater();
        View dv = inflater.inflate(R.layout.sch_dlg, null);
        ListView lv = (ListView)dv.findViewById(R.id.lgames);
        ArrayAdapter ladapt = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array);
        lv.setAdapter(ladapt);
        
        builder.setTitle("Generated games").setView(dv).setPositiveButton(R.string.schedule, null);
*/

        builder.setTitle("Generated games").setItems(array, null);
        builder.setPositiveButton(R.string.schedule, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                    saveGames();
                }
            }
        )
        .setNegativeButton("Cancel", null);

        AlertDialog d = builder.create();
        d.show();


        Button pbtn = (Button)d.getButton(DialogInterface.BUTTON_POSITIVE);
        pbtn.setTextColor(getResources().getColor(R.color.white));

        /*
        ViewGroup.LayoutParams params = pbtn.getLayoutParams();
        //params.setMargins(10, 10, 10, 10);
        pbtn.setLayoutParams(params);
        */


        pbtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bbutton));            
    }


    public void showModes(View v) {
        mModeSpinner.performClick();
    }

    public void onSetDates(View v){
        renderView(DatesActivity.class, createMap("tour", mTour.uname, "div", mDiv.id));
    }

    void saveGames() {
        executeAsync(new Runnable() {
                public void run() {
                    mResult = ModelAccessor.model().saveGames(mTour.uname, mNewGames);
                }
            },
            new Runnable(){
                public void run() {
                    //refresh();
                    finish();
                }
            }
        );

    }

    public void showStages(View v) {
        mStageSpinner.performClick();
    }

    public void onSchedulePlayOff(View v) {
        int spos = mSSS.pos;
        int rpos = mStages.size() - spos - 1;
        int stage = 1;
        for (int i = 0; i < rpos; i++)
            stage *= 2;
        stage = -stage;
        Tracer.log("Scheduling play_off for stage " + stage + " on " + mDiv.table);
        
        List<Game> games = Scheduler.schedulePlayOff(mDiv.table, mDiv.games, stage);
        Tracer.log("Scheduled play_off games " + games);
        String[] array = games2Text(games);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Generated games").setItems(array, null);
        builder.setPositiveButton(R.string.schedule, null).setNegativeButton("Cancel", null);

        AlertDialog d = builder.create();
        d.show();

    }

    public List<String> getStages(int size) {
        int stage = MathUtil.getLowerBin((short)size);
        List<String> ret = new ArrayList();
        for (; stage > 1 ; stage /= 2) {
            if (stage == 2)
                ret.add(getString(R.string.sfin));
            else if (stage == 4)
                ret.add(getString(R.string.qfin));
            else
                ret.add(getString(R.string.round_of, stage * 2));

        }
        ret.add(getString(R.string.fin));
        return ret;
    }
}


