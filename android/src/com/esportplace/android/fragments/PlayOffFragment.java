package com.esportplace.android;

import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.content.res.Resources;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.leaguetor.net.ModelAccessor;

import com.leaguetor.entity.*;

import java.util.List;
import android.app.Fragment;


public class PlayOffFragment extends ScheduleFragment {

    protected int getFragmentId() {
        return R.layout.play_off;
    }
}