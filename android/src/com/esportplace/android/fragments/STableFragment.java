package com.esportplace.android;

import android.os.Bundle;

import android.view.View;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageButton;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.content.res.Resources;

import com.leaguetor.net.ModelAccessor;
import com.leaguetor.entity.*;

import java.util.List;


public abstract class STableFragment extends BaseFragment {
    boolean mAll;
    List mData;
    TableLayout mTable;
    int mLimit; 
    TableFiller mFiller;
    TextView mHdr;
    boolean mHideHeader;
   

    public static interface TableFiller {
        void fillTable(int idx, Object data, TableRow row, TableLayout tbl);
    }


    public abstract TableFiller getFiller();

    public abstract int getHeaderId();
    public abstract String getHeaderStr();
    public abstract int getTableId();
    public abstract int getRowId();
    public boolean getInitState() {
        return true;
    }

    public void hideHeader(boolean m) {
        mHideHeader = m;
    }

    public boolean useAllTable() {
        return true;
    }

    protected boolean needSeparator() {
        return true;
    }

    protected void afterFill(boolean more, boolean less) {
    }

//    public void init(int btn_id, int table_id, int row_id) {
    protected void init(View v){
        mView = v;
        mFiller = getFiller();
        mTable = (TableLayout)v.findViewById(getTableId());
        mHdr = (TextView)mView.findViewById(getHeaderId());
    }

    public void setData(List data, int lim) {
        mData = data;
        mLimit = lim;
    }


    public void fill() {
        mAll = getInitState();
        mHdr.setText(getHeaderStr());
        fillTable(mAll);
    }

    protected void fillTableHeader(TableLayout tl){}

    protected void refill() {
        if (useAllTable()) {
            if (mTable.getVisibility() == View.VISIBLE)
                mTable.setVisibility(View.GONE);
            else
                mTable.setVisibility(View.VISIBLE);
        } else {
            fillTable(mAll);
        }
    }

    void fillTable(boolean all) {
        if (mData == null)
            return;
        if (mHideHeader)
            mHdr.setVisibility(View.GONE);

        BaseActivity act = getAct();
        mTable.removeAllViews();
        fillTableHeader(mTable);
        int M = mLimit;

        int G = mData.size();
        int GM = G;

        boolean more = false;
        boolean less = false;
        if (G > M)
        {
            if (all) {
                less = true;
            } else {
                more = true;
                GM = M;
            }
        }

        boolean sep = needSeparator();
        act.addSeparator(mTable, sep && GM > 0);

        for (int i = 0; i < GM; i++) {
            TableRow row = act.createRow(getRowId(), mTable);
            mFiller.fillTable(i, mData.get(i), row, mTable);
            act.addSeparator(mTable, sep && (i < GM - 1 || more));
        }

        afterFill(more, less);

    }
}