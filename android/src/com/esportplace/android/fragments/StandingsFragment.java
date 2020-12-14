package com.esportplace.android;

import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Button;

import android.widget.TableRow;
import android.widget.TableLayout;

import android.graphics.Typeface;

import com.leaguetor.net.ModelAccessor;
import com.leaguetor.net.ModelUtil;
import com.leaguetor.entity.*;

import java.util.List;
import java.util.Set;
import java.util.HashSet;


public class StandingsFragment extends SWTableFragment  {

    Tour mTour;
    Division mDiv;

    public int getFragmentId() {
        return R.layout.standings_fragment;
    }

    public void initData(Tour t, Division d) {
        mTour = t;
        mDiv = d;
        setData(d.table, 0);
    }

    public int getHeaderId() {
        return R.id.div_hdr;
    }
    public String getHeaderStr() {
        return mDiv.name;
    }
    public int getTableId() {
        return R.id.table;
    }
    public int getRowId() {
        return R.layout.tablerow;
    }

    public int getBtnId() {
        return R.id.div_hdr;
    }

    public boolean isBtnImage() {
        return false;
    }

    protected boolean needSeparator() {
        return false;
    }


    public void fillTableHeader(TableLayout tl) {
        if (mData.size() < 1) {
            return;
        }
        TableRow rrow = mActivity.createRow(R.layout.tablerow, tl);
        ((TextView)rrow.findViewById(R.id.games)).setText(getString(R.string.G));
        ((TextView)rrow.findViewById(R.id.points)).setText(getString(R.string.P));
        ((TextView)rrow.findViewById(R.id.team)).setText(getString(R.string.team));

        ((TextView)rrow.findViewById(R.id.games)).setTypeface(null, Typeface.BOLD);
        ((TextView)rrow.findViewById(R.id.points)).setTypeface(null, Typeface.BOLD);
        ((TextView)rrow.findViewById(R.id.team)).setTypeface(null, Typeface.BOLD);
        mActivity.addSeparator(mTable, 1, R.color.darkgray);
    }

    class RTableFiller implements STableFragment.TableFiller {

        public void fillTable(int idx, Object o, TableRow row, TableLayout tbl) {
            TableRecord r = (TableRecord)o;
            ((TextView)row.findViewById(R.id.games)).setText("" + r.games);
            ((TextView)row.findViewById(R.id.points)).setText("" + r.points);
            ((TextView)row.findViewById(R.id.team)).setText(r.team.name);
        }
    }


    public TableFiller getFiller() {
        return new RTableFiller();
    }

    protected void afterFill(boolean more, boolean less) {
        super.afterFill(more, less);
        mActivity.addSeparator(mTable, 1, R.color.darkgray);
    }

}


