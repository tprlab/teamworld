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



public class Dashboard extends DashActivity  {


    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash);
        mUser = getStringParam("user");
        setTitle(getString(R.string.dashboard));
    }

    public void onGames(View v) {
    }

    public void onTours(View v) {
    }

    public void onLeagues(View v) {
    }


    public void refresh() {

        if (mInfo == null)
            return;
  
        int M = 2;

        GamesFragment gf = (GamesFragment)getSupportFragmentManager().findFragmentById(R.id.games_fragment);
        gf.setData(mInfo.games, M);
        gf.fill();

        ToursFragment tf = (ToursFragment)getSupportFragmentManager().findFragmentById(R.id.trs_fragment);
        tf.setData(mInfo.tours, M);
        tf.fill();

        LeaguesFragment lf = (LeaguesFragment)getSupportFragmentManager().findFragmentById(R.id.lgs_fragment);
        lf.setData(mInfo.leagues, M);
        lf.fill();

    }


    public void onCreateLeague(View v){
        renderView(LeagueInfoView.class, createMap("user", mUser, "create", 1));
    }

}


