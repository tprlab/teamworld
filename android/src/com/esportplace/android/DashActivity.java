package com.esportplace.android;

import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.content.res.Resources;

import com.leaguetor.net.ModelAccessor;
import com.leaguetor.entity.*;

import java.util.List;

public class DashActivity extends BaseActivity  {

    DashInfo mInfo;
    String mUser;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mUser = getStringParam("user");
    }

    protected void asyncInit() {
        mInfo = ModelAccessor.model().getActivities(mUser);
        if (mInfo == null) {
            Tracer.log("Cannot load user info " + mUser);
            return;
        }
        Tracer.log("User " + mUser + " has " + mInfo.getLeaguesSize() + " leagues, " +
            mInfo.getToursSize() + " tours, " + mInfo.getGamesSize() + " games");
    }

    public void onManageLeague(View v){
        LeagueInfo li = ModelAccessor.model().getLeagueInfo(v.getId());
        if (li == null)
            return;
        renderView(LeagueDash.class, createMap("user", mUser, "league", li.uname));
    }


}
