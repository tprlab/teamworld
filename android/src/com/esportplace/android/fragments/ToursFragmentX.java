package com.esportplace.android;

import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import com.leaguetor.entity.*;

import java.util.List;



public class ToursFragmentX extends ToursFragment {

    View mTourCreator;
    EditText mTourName;
    boolean mTourMode = false;
    boolean mAllTours = false;
    TextView mTrHdr;



    public int getFragmentId() {
        return R.layout.tr_fragment_ex;
    }


    public void onTours(View v) {
        mTourMode = !mTourMode;
        if (mTourMode) {
            mTourCreator.setVisibility(View.VISIBLE);
            mTrHdr.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up_float, 0);
        } else {
            mTourCreator.setVisibility(View.GONE);
            mTrHdr.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.plus, 0);
        }
    }

    protected void init(View v){
        super.init(v);
        mTourCreator = v.findViewById(R.id.create_bar_tr);
        mTourCreator.setVisibility(View.GONE);
        mTourName = (EditText)mTourCreator.findViewById(R.id.tr_name);
        mTrHdr = (TextView)v.findViewById(R.id.tours_txt);

        mTrHdr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onTours(v);
            }
        });

        Button confirm_btn = (Button)v.findViewById(R.id.tourConfirm);
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createTour(v);
            }
        });
    }

    public void createTour(View v) {
    }

    public boolean showSize() {
        return false;
    }
}


