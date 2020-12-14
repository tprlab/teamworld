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


public abstract class SWTableFragment extends STableFragment {
    View mBtn;
    View mFoldBtn;

    public abstract int getBtnId();

    public int getFoldBtnId() {
        return 0;
    }

    public boolean isBtnImage() {
        return true;
    }

    protected int getFoldId() {
        return R.drawable.arrow_up_float;
    }

    protected int getUnFoldId() {
        return R.drawable.arrow_down_float;
    }


    protected void init(View v){
        super.init(v);
        mBtn = v.findViewById(getBtnId());
        boolean all = getInitState();
        int foldBtnId = getFoldBtnId();
        if (foldBtnId != 0) {
            mFoldBtn = v.findViewById(foldBtnId);
        }

        String hstr = getHeaderStr();
        Tracer.log("Fragment " + hstr + " state " + all);

        if (mBtn != null) {            
            mBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v){
                    onSwitch(v);
                }
            });
        }

        if (mFoldBtn != null) {
            mFoldBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v){
                    onSwitch(v);
                }
            });

            mFoldBtn.setVisibility(all ? View.VISIBLE : View.GONE);
            mBtn.setVisibility(all ? View.GONE : View.VISIBLE);

        }
    }

    protected void onSwitch(View v) {
        mAll = !mAll;
        if (mFoldBtn == null) {
            int fold = getFoldId();
            int unfold = getUnFoldId();
            int img = mAll ? fold : unfold;

            if (isBtnImage()) {
                mBtn.setBackgroundResource(img);
            } else {
                TextView tv = (TextView)mBtn;
                tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, img, 0);
            }
        } else {
            mFoldBtn.setVisibility(mAll ? View.VISIBLE : View.GONE);
            mBtn.setVisibility(mAll ? View.GONE : View.VISIBLE);
        }
        refill();
    }

    protected void afterFill(boolean more, boolean less) {
        super.afterFill(more, less);


        if (mFoldBtn != null) {
            mFoldBtn.setVisibility(less ? View.VISIBLE : View.GONE);
            mBtn.setVisibility(more ? View.GONE : View.VISIBLE);
        }
    }
}