package com.esportplace.android;

import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import com.leaguetor.net.ModelAccessor;

import com.leaguetor.entity.*;
import com.leaguetor.Scheduler;
import com.leaguetor.StringUtil;


import java.util.List;
import java.util.ArrayList;
import android.app.Fragment;


public abstract class ScheduleFragment extends BaseFragment {

    List<Game> mGames;
    Tour mTour;
    int mDivId;

    public void setTour(Tour t) {
        mTour = t;
    }

    public void setDiv(int id) {
        mDivId = id;
    }

    public List<Game> getGames() {
        return mGames; 
    }
}

