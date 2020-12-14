package com.esportplace.android;

import android.os.Bundle;

import com.leaguetor.entity.*;
import com.leaguetor.net.ModelAccessor;
import com.leaguetor.StringUtil;

import java.util.List;
import java.util.ArrayList;


public class TourActivity extends BaseActivity  {

    String mTourName;
    Tour mTour;
    Division mDiv;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mTourName = getStringParam("tour");
        Tracer.log("Getting tour " + mTourName);
    }

    protected void asyncInit() {
        if (mTourName != null)
            mTour = ModelAccessor.model().getTour(mTourName);
    }

    protected void onSaveInstanceState (Bundle outState)
    {
        if (mTour != null)
            outState.putString("tour", mTour.uname);
    }

    public List<Game> getDivGames() {
        if (mDiv == null)
            return null;
        List<Game> games = mDiv.games;
        Tracer.log("Div " + mDiv.id + " has " + StringUtil.list_size(mDiv.games) + " games");
        if (mDiv.id == 0) {
            games = new ArrayList<Game>();
            for (Division d : mTour.divList) {
                Tracer.log("Div " + d.id + " has " + StringUtil.list_size(d.games) + " games");
                if (d.games != null)
                    games.addAll(d.games);
            }
        }
        return games;
    }



}


