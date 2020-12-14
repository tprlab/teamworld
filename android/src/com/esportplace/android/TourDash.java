package com.esportplace.android;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.leaguetor.entity.*;
import com.leaguetor.net.ModelAccessor;

import java.util.List;
import android.widget.LinearLayout;
import android.content.DialogInterface;


public class TourDash extends BaseActivity  {

    String mTourName;
    Tour mTour;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tour_dash);
        mTourName = getStringParam("tour");
    }

    protected void asyncInit() {
        if (mTourName == null)
            return;
        mTour = ModelAccessor.model().getTour(mTourName);
    }

    protected void onSaveInstanceState (Bundle outState)
    {
        if (mTour != null)
            outState.putString("tour", mTour.uname);
    }


    public void refresh() {

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setId(12345);

        FragmentManager fm = getSupportFragmentManager();
        LinearLayout dash = (LinearLayout)findViewById(R.id.tour_dash);
        FragmentTransaction ft = fm.beginTransaction();
        if (mTour.divList != null)        {
           for (Division d : mTour.divList) {
               String tag = "div-" + d.id;
               Fragment f = fm.findFragmentByTag(tag);
               if (f != null)
                   ft.remove(f);
               StandingsFragment sf = new StandingsFragment();
               sf.initData(mTour, d);

               if (d.id == 0) {
                   d.name = getString(R.string.play_off);
               }

               Tracer.log("Creating fragment " + tag);
               ft.add(ll.getId(), sf, tag);

               String gtag = "g" + tag;
               GamesFragment gf = new GamesFragment();
               gf.hideHeader(true);
               int L = d.id == 0 ? 3 : 0;
               gf.setData(d.games, L); 
               f = fm.findFragmentByTag(gtag);
               if (f != null)
                   ft.remove(f);

               ft.add(ll.getId(), gf, gtag);
               //sf.fill();
           }
        }
        ft.commit();
        dash.addView(ll);

    }

    protected int getMenuId() {
        return R.menu.tour;
    }

    protected boolean handleMenu(int m) {
        if (m == R.id.schedule) {
            schedule();
        } else if (m == R.id.finish) {
            finishTour();
        } else if (m == R.id.delete) {
            deleteTour();
        } else {
            return false;
        }
        return true;
    }

    void deleteTour() {
        showConfirm(getString(R.string.tourdel_confirm), R.string.delete, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
            Tracer.log("Deleting tour " + mTour.id + " " + mTour.name);                
            //getSportDB().deleteTour(mTour.id);
            //finish();
            //renderView(SportWorldMain.class, null);
            }
         });
    }

    void finishTour() {
        showConfirm(getString(R.string.tourfin_confirm), R.string.finish, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
            Tracer.log("Finishing tour " + mTour.id + " " + mTour.name);                
            //getSportDB().deleteTour(mTour.id);
            //finish();
            //renderView(SportWorldMain.class, null);
            }
         });

    }

    void schedule() {
        renderView(ScheduleActivity.class, createMap("tour", mTour.uname));
        //renderView(MainActivity.class, createMap("tour", mTour.uname));
    }

}


