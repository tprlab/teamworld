package com.esportplace.android;

import android.os.Bundle;

import com.leaguetor.entity.*;
import com.leaguetor.net.ModelAccessor;

import java.util.List;


public class TourInfoActivity extends BaseActivity  {

    String mTourName;
    TourInfo mTour;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lginfo);
        mTourName = getStringParam("tour");
    }

    protected void asyncInit() {
        if (mTourName != null)
            mTour = ModelAccessor.model().getTourInfo(mTourName);
    }

    protected void onSaveInstanceState (Bundle outState)
    {
        if (mTour != null)
            outState.putString("tour", mTour.uname);
    }


}


