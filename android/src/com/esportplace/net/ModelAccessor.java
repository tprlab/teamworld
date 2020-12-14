package com.leaguetor.net;

import com.leaguetor.entity.*;
import com.leaguetor.StringUtil;

import com.esportplace.android.Tracer;
import com.esportplace.android.SportModel;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;


public class ModelAccessor {
    static ModelAccessor cInst = new ModelAccessor();
    SportModel mNet = new NetSport();
    SportCache mModel = new SportCache();
    public static String DATE_MASK = "dd MMM yy";

    public ModelAccessor() {
        mModel.setImpl(mNet);
    }


    public static SportModel model() {
        return cInst.mModel;
    }

}