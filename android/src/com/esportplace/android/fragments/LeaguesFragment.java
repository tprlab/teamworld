package com.esportplace.android;

import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import com.leaguetor.entity.*;

import java.util.List;


public class LeaguesFragment extends HeadButFragment {

    public int getHeaderId() {
        return R.id.leagues_txt;
    }

    public int getStrId() {
        return R.string.leagues;
    }

    public int getFragmentId() {
        return R.layout.lg_fragment;
    }
    public int getBtnId() {
        return R.id.more_lgs;
    }
    public int getTableId() {
        return R.id.lg_table;
    }
    public int getRowId() {
        return R.layout.lgrow;
    }

    class LeagueFiller implements STableFragment.TableFiller {

        public void fillTable(int idx, Object o, TableRow row, TableLayout tbl) {
            LeagueInfo lg = (LeagueInfo)o;
            TextView tv = (TextView)row.findViewById(R.id.name);
            tv.setText(lg.name);
            tv.setId(lg.id);
            row.setId(lg.id);
        }
    }


    public TableFiller getFiller() {
        return new LeagueFiller();
    }
}