package com.esportplace.android;

import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.content.res.Resources;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.leaguetor.net.ModelAccessor;

import com.leaguetor.entity.*;

import java.util.List;
import android.support.v4.app.Fragment;


public abstract class BaseFragment extends Fragment {

    BaseActivity mActivity;
    View mView;

    public void onCreate (Bundle state) {
        super.onCreate(state);
    }

    protected void init(View v){}

    protected abstract int getFragmentId();

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle state) {
        View ret = inflater.inflate(getFragmentId(), container, false);
        mView = ret;
        mActivity = getAct();
        init(mView);
        return ret;
    }

    public void onPause(){
        super.onPause();
    }

    public void onResume(){
        super.onResume();

        if (needInit()) {
            mActivity.executeAsync(new Runnable() {
                public void run() {
                    asyncInit();
                }}, new Runnable() {
                public void run() {
                    fill();
                }});
        } else {
            fill();
        }
    }

    public void fill(){}

    protected boolean needInit() {
        return false;
    }

    protected BaseActivity getAct() {
        return (BaseActivity)getActivity(); 
    }

    protected void asyncInit() {}




}