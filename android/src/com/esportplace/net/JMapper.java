package com.leaguetor.rest;

import java.util.*;

import com.leaguetor.*;
import com.leaguetor.entity.*;



public class JMapper {

    public static Map<String, Object> createMap() {
        return new HashMap<String, Object>();
    }

    public static List createList() {
        return new ArrayList();
    }


    public static Map<String, Object> mapLeague(LeagueInfo lg, boolean owner) {
        Map<String, Object> ret = createMap();
        if (lg != null) {
            ret.put("name", lg.name);          
            ret.put("id", lg.id);
            ret.put("sport", lg.sport.id);
            ret.put("loc", lg.location);
            if (owner)
                ret.put("owner", lg.ownername);
        }
        return ret;
    }

    public static Map<String, Object> mapTour(TourInfo tr) {
        Map<String, Object> m = createMap();
        m.put("name", tr.name);          
        m.put("id", tr.id);
        m.put("uname", tr.uname);
        m.put("start", tr.started);
        if (tr.finished != 0)
            m.put("end", tr.finished);
        m.put("status", tr.status);
        return m;
    }




    public static Map<String, Object> mapTeam(Team tm) {
        Map<String, Object> m = createMap();
        m.put("name", tm.name);          
        m.put("id", tm.id);
        m.put("uname", tm.uname);
        m.put("div", tm.divId);
        return m;
    }

    public static Map<String, Object> mapDiv(DivInfo div) {
        Map<String, Object> m = createMap();
        m.put("name", div.name);          
        m.put("id", div.id);
        m.put("uname", div.uname);
        return m;
    }

    public static Map<String, Object> mapDiv(Division div) {
        Map<String, Object> m = createMap();
        m.put("name", div.name);          
        m.put("id", div.id);
        m.put("uname", div.uname);
        return m;
    }


    public static Map<String, Object> mapGame(Game g) {
        Map<String, Object> m = createMap();
        if (g.id > 0)
            m.put("id", g.id);
        m.put("team1", g.team1.id);
        m.put("team2", g.team2.id);

        if (g.status != 0) {
            m.put("status", g.status);
            m.put("score1", g.score1);
            m.put("score2", g.score2);
        }
        if (g.scheduled != 0)
            m.put("date", g.scheduled / 1000);     
        if (g.venueId > 0) {
            m.put("venue", g.venueId);
        }

        if (g.court > 0)
            m.put("court", g.court);

        if (g.divId > 0)
            m.put("divId", g.divId);
        if (g.stage > 0)
            m.put("stage", g.stage);
        return m;
    }

    public static Map<String, Object> mapGameSch(Game g) {
        Map<String, Object> m = createMap();
        m.put("id", g.id);
        m.put("date", g.scheduled / 1000);     
        if (g.court > 0)
            m.put("court", g.court);
        return m;
    }



    public static Map<String, Object> mapTableRecord(TableRecord tr, Counting cnt) {
        Map<String, Object> m = createMap();
        m.put("team", tr.team.id);          
        m.put("pts", tr.points);
        m.put("g", tr.games);
        if (cnt.resultOnly) {
            m.put("w", tr.wins);
            m.put("l", tr.losses);
        }

        if (cnt.tieAllowed) {
            m.put("t", tr.ties);
        }

        if (cnt.otAllowed) {
            m.put("lot", tr.lot);
            m.put("wot", tr.wot);
        }

        m.put("ga", tr.ga);
        m.put("gf", tr.gf);

        return m;
    }




    
}
