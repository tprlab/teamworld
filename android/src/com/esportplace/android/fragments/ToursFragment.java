package com.esportplace.android;

import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import com.leaguetor.entity.*;
import com.leaguetor.net.ModelAccessor;

import java.util.List;



public class ToursFragment extends HeadButFragment {


    public int getHeaderId() {
        return R.id.tours_txt;
    }

    public int getStrId() {
        return R.string.tours;
    }

    public int getFragmentId() {
        return R.layout.tr_fragment;
    }
    public int getBtnId() {
        return R.id.more_tours;
    }
    public int getTableId() {
        return R.id.tr_table;
    }
    public int getRowId() {
        return R.layout.tourrow;
    }

    public void onManageTour(View v){
        TourInfo ti = ModelAccessor.model().getTourInfo(v.getId());
        if (ti == null) {
            Tracer.log("No tour " + v.getId());
            return;
        }
        mActivity.renderView(TourDash.class, mActivity.createMap("tour", ti.uname));
    }


    class TourFiller implements STableFragment.TableFiller {


        public void fillTable(int idx, Object o, TableRow row, TableLayout tbl) {
            TourInfo tr = (TourInfo)o;
            TextView tv = (TextView)row.findViewById(R.id.name);
            tv.setText(tr.name);
            row.setId(tr.id);
            row.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    onManageTour(v);
                }
            });
        }
    }

    public TableFiller getFiller() {
        return new TourFiller();
    }


}


