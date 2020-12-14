package com.leaguetor.net;

import com.leaguetor.entity.*;
import com.leaguetor.StringUtil;

import com.esportplace.android.Tracer;
import com.esportplace.android.SportModel;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;


public class ModelUtil {

    public static List<Division> makeDivs(List<DivInfo> divs, List<Team> teams) {
        ArrayList<Division> ret = new ArrayList<Division>();
        HashMap<Integer, Division> dmap = new HashMap<Integer, Division>();
        for (DivInfo dv : divs) {
            Division d = new Division();
            d.id = dv.id;
            d.name = dv.name;
            d.uname = dv.uname;
            ret.add(d);
            dmap.put(d.id, d);
        }

        if (dmap.get(0) == null) {
            Division d = new Division();
            d.id = 0;
            // supposed to be 0-div
            ret.add(0, d);
            dmap.put(d.id, d);
        }

        for (Team t : teams) {
            Division d = dmap.get(t.divId);
            d.addToTeams(t);
        }

        return ret;
    }


}