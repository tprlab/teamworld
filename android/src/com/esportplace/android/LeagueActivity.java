package com.esportplace.android;

import android.os.Bundle;

import com.leaguetor.entity.*;

import java.util.List;
import com.leaguetor.net.ModelAccessor;


public class LeagueActivity extends BaseActivity  {

    String mUser;
    String mLeagueId;
    LeagueInfo mLeague;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lginfo);
        mUser = getStringParam("user");
        mLeagueId = getStringParam("league");
    }

    protected void asyncInit() {
        if (mLeagueId != null) {
            mLeague = ModelAccessor.model().getLeagueInfo(mLeagueId);
            if (mLeague != null) {
                mLeague.ownername = mUser;
            }
        }
    }

}


