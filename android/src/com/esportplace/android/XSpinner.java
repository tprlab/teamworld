package com.esportplace.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.AsyncTask;
import android.app.ProgressDialog;

import android.content.DialogInterface;

import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import android.view.Window;

import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.View;
import android.widget.ListView;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;



import android.support.v4.app.FragmentActivity;

public class XSpinner {
    List<String> mLabels;

    int mListCtrl;
    BaseActivity mActivity;
    Button mBtn;
    int mSel = 0;
    int mId;

    Selector mListener;
    boolean mSearch = false;

    public static interface Selector {
        public void onSelected(int id, int sel, String val);
    }

    public void enableSearch(boolean b) {
        mSearch = b;
    }


    public void setListener(Selector s) {
        mListener = s;
    }

    public XSpinner(BaseActivity parent){
        mActivity = parent;
    }

    public void setId(int id) {     
        mId = id;
    }

    public void initId(int list) {
        mListCtrl = list;
        initCtrl((Button)mActivity.findViewById(list));
    }

    public void initCtrl(Button btn) {
        mBtn = btn;
        mBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                show(v.getId());
            }
        });
    }


    public void initData(List<String> labes) {
        mLabels = labes;
    }

    public void show(int id) {

        DialogFragment dlg = new ListDlg();
        dlg.setStyle(0, DialogFragment.STYLE_NO_TITLE);
        dlg.show(mActivity.getFragmentManager(), "dialog");
    }

    public String getSelLabel() {
        return mLabels.get(mSel);
    }

    public int getSel() {
        return mSel;
    }

    
    public void setSel(int sel) {
        if (sel < 0 || sel > mLabels.size() - 1)
            return;
        mSel = sel;
        mBtn.setText(mLabels.get(mSel));
    }

    public void setSel(String name) {
        mSel = 0;
        for (int i = 0;  i < mLabels.size(); i++) {
            if (name.equalsIgnoreCase(mLabels.get(i))) {
                mSel = i;
                break;
            }
        }
                
        mBtn.setText(mLabels.get(mSel));
    }



    public class ListDlg extends DialogFragment {


        class ListClickListener implements View.OnClickListener {
            
            int idx;

            public ListClickListener(int i) {
                idx = i;
            }
                
            public void onClick(View view) {
                mSel = idx;
                String val = mLabels.get(mSel);
                Tracer.log("New sel is " + mSel + ": " + val);
                dismiss();
                mBtn.setText(val);
 
                if (mListener != null) {
                    mListener.onSelected(mId, mSel, val);
                }
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.clist, container, false);
            EditText edit = (EditText)v.findViewById(R.id.search);
            if (!mSearch)
                edit.setVisibility(View.GONE);
            TableLayout lv = (TableLayout)v.findViewById(R.id.clist);

            for (int i = 0; i < mLabels.size(); i++) {
                TableRow row = mActivity.createRow(R.layout.listrow, lv);
                TextView tv = (TextView)row.findViewById(R.id.txt);
                tv.setText(mLabels.get(i));
                ListClickListener listener = new ListClickListener(i);
                tv.setOnClickListener(listener);
                row.setOnClickListener(listener);

                mActivity.addSeparator(lv, i < mLabels.size() - 1);
            }
            return v;
        }



    }


    
}

