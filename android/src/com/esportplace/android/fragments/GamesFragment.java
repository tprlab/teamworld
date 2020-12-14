package com.esportplace.android;

import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import com.leaguetor.entity.*;
import com.leaguetor.net.ModelUtil;
import com.leaguetor.net.ModelAccessor;
import com.leaguetor.StringUtil;

import java.util.List;
import java.util.Date;



public class GamesFragment extends HeadButFragment {

    String mDateMask = "dd MMM yy - hh:mm a";
    boolean mRemoveRS;

    public int getStrId() {
        return R.string.games;
    }

    public int getHeaderId() {
        return R.id.games_txt;
    }

    public int getFragmentId() {
        return R.layout.games_fragment;
    }
    public int getBtnId() {
        return R.id.more_games;
    }
    public int getTableId() {
        return R.id.game_table;
    }
    public int getRowId() {
        return R.layout.gamerow;
    }

    public static String getStage(int stage) {
        if (stage == -1)
            return "F";
        else if (stage == -2)
            return "S";
        else if (stage == -4)
            return "Q";
        return "R" + (-stage);
    }


    class GameFiller implements STableFragment.TableFiller {

        public void fillTable(int idx, Object o, TableRow row, TableLayout tbl) {
            Game g = (Game)o;
            TextView gdesc = (TextView)row.findViewById(R.id.gamedesc);
            String stage = "";
            if (g.stage < 0) {
                stage = getStage(g.stage);
                TextView gidx = (TextView)row.findViewById(R.id.gameidx);
                gidx.setText(stage);
            }
            gdesc.setText(g.team1.name + " - " + g.team2.name);
            if (g.status ==  LeaguetorConstants.GAME_STATUS_SCORED) {
                String score = "" + g.score1 + ":" + g.score2;
                TextView tsc = (TextView)row.findViewById(R.id.score);
                tsc.setText(score);
                //tsc.setBackgroundResource(R.drawable.score_rect);
            }
            if (g.scheduled != 0) {
                g.shownDate = StringUtil.formatDate(new Date(g.scheduled), mDateMask);
                //mActivity.addSeparator(mTable, 1, R.color.gray);                
                TextView small = (TextView)mActivity.getLayoutInflater().inflate(R.layout.small_row, null);
                small.setText(g.shownDate);
                mTable.addView(small);
            } else {
                row.setBackgroundColor(getResources().getColor(R.color.pink));
            }

            row.setId(g.id);
            row.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onManageGame(v);
                }    
            });

            row.setOnTouchListener(new MotionHelper(new MotionHelper.SweepListener() {
                public void onSweep(View v) {
                    Tracer.log("Game " + v.getId() + " sweeped");
                    removeGame(v);
                }    
            }));
        }
    }

    public TableFiller getFiller() {
        return new GameFiller();
    }

    public int getFoldBtnId() {
        return R.id.less_games;
    }

    public void onManageGame(View v){
        final int gid = v.getId();
        mActivity.executeAsyncEx(new BaseActivity.RunnableEx() {
            Game g;
            TourInfo ti;
            public void run() {
                g = ModelAccessor.model().getGame(gid);
                if (g == null) {
                    Tracer.log("No game " + gid);
                    return;
                }
                ti = ModelAccessor.model().getTourInfo(g.tourId);
                if (ti == null) {
                    Tracer.log("No tour " + g.tourId);
                    return;
                }
            }
            public void done() {
                if (g != null && ti != null)
                    mActivity.renderView(GameActivity.class, mActivity.createMap("tour", ti.uname, "game", g.id));
            }
        });
    }

    void removeGame(View v) {
        final int id = v.getId();
        v.setVisibility(View.GONE);
        
        mActivity.executeAsyncNW(new Runnable() {
            public void run() {
                mRemoveRS = ModelAccessor.model().removeGame(id);
            }
        }, null);
    }
}
