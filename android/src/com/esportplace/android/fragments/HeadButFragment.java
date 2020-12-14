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


public abstract class HeadButFragment extends SWTableFragment {

    public abstract int getStrId();

    public boolean showSize() {
        return true;
    }

    public String getHeaderStr() {
        String ret = getString(getStrId());
        if (showSize() && mData != null && mData.size() > 0) {
            ret += ": " + mData.size();
        }
        return ret;
    }

    public boolean getInitState() {
        return false;
    }

    public boolean useAllTable() {
        return false;
    }

    protected void afterFill(boolean more, boolean less) {
        if (!more && !less)
            mBtn.setVisibility(View.GONE);
    }


}
