package com.esportplace.android;

import android.os.Bundle;

import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.View;
import android.widget.EditText;

import android.support.v4.app.DialogFragment;
import com.leaguetor.StringUtil;

import java.util.List;

public class ScoreCtrl {

    View mView;
    EditText mScore;

    public ScoreCtrl(View v, int inc, int dec, int score){
        mView = v;
        View v_inc = mView.findViewById(inc);
        View v_dec = mView.findViewById(dec);
        if (v_inc != null)  {
            v_inc.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onInc(v);                    
                }
            });
        }

        if (v_dec != null)  {
            v_dec.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onDec(v);                    
                }
            });
        }

        mScore = (EditText)mView.findViewById(score);
    }


    public void onInc(View view) {
        int score = getScore();
        mScore.setText("" + (++score));
    }

    public void onDec(View view) {
        int score = getScore();
        if (score > 0)
            mScore.setText("" + (--score));
    }

    public int getScore() {
        String ss = mScore.getText().toString();
        return StringUtil.emptyOrNull(ss) ? 0 : StringUtil.parseInt(ss);
    }

    public int getScore(int def) {
        String ss = mScore.getText().toString();
        return StringUtil.emptyOrNull(ss) ? def : StringUtil.parseInt(ss);
    }

}


