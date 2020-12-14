package com.leaguetor.net;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esportplace.android.Tracer;
import com.leaguetor.entity.*;

import java.util.List;
import java.util.ArrayList;


public class JFactory {

    static List createList() {
        return new ArrayList();
    }

    static interface Mapper {
        public Object mapObject(JSONObject o);
    }

    static class LeagueInfoMapper implements Mapper {
        public Object mapObject(JSONObject o) {
            return JFactory.getLeagueInfo(o);
        }
    }

    static class TourInfoMapper implements Mapper {
        public Object mapObject(JSONObject o) {
            return JFactory.getTour(o);
        }
    }

    static class SportMapper implements Mapper {
        public Object mapObject(JSONObject o) {
            return JFactory.getSport(o);
        }
    }

    static class TeamMapper implements Mapper {
        public Object mapObject(JSONObject o) {
            return JFactory.getTeam(o);
        }
    }

    static class DivInfoMapper implements Mapper {
        public Object mapObject(JSONObject o) {
            return JFactory.getDiv(o);
        }
    }

    static class DivisionMapper implements Mapper {
        public Object mapObject(JSONObject o) {
            return JFactory.getFullDiv(o);
        }
    }

    static class GameMapper implements Mapper {
        public Object mapObject(JSONObject o) {
            return JFactory.getGame(o);
        }
    }

    static class TableRecordMapper implements Mapper {
        public Object mapObject(JSONObject o) {
            return JFactory.getTableRecord(o);
        }
    }


    static Mapper cLegueMapper = new LeagueInfoMapper();
    static Mapper cTourMapper = new TourInfoMapper();
    static Mapper cSportMapper = new SportMapper();
    static Mapper cGameMapper = new GameMapper();
    static Mapper cTeamMapper = new TeamMapper();
    static Mapper cDivMapper = new DivInfoMapper();
    static Mapper cFullDivMapper = new DivisionMapper();
    static Mapper cTableRecordMapper = new TableRecordMapper();


    public static List mapList(JSONArray a, Mapper m) {
        List ret = createList();
        if (a == null)
            return ret;
        for (int i = 0; i < a.length(); i++) {
            JSONObject j = a.optJSONObject(i);
            Object o = m.mapObject(j);
            if (o != null)
                ret.add(o);
        }
        return ret;
    }



    public static List<LeagueInfo> getLeagues(JSONArray a) {
        return mapList(a, cLegueMapper);
    }

    public static LeagueInfo getLeagueInfo(JSONObject js) {
        LeagueInfo li = new LeagueInfo();
        try {
            li.id = js.optInt("id");
            li.name = js.optString("name");
            li.uname = js.optString("uname");
            li.location = js.optString("loc");
            li.sportId = js.optInt("sport");
        } catch(Throwable t) {
            Tracer.err("Cannot fill league info from " + js, t);
            return null;
        }
        return li;
    }

    public static List<Sport> getSports(JSONArray a) {
        return mapList(a, cSportMapper);
    }

    public static Sport getSport(JSONObject js) {
        Sport sp = new Sport();
        sp.id = js.optInt("id");
        sp.name = js.optString("name");
        return sp;
    }

    public static List<Team> getTeams(JSONArray a) {
        return mapList(a, cTeamMapper);
    }

    public static Team getTeam(JSONObject js) {
        Team t = new Team();
        t.id = js.optInt("id");
        t.name = js.optString("name");
        t.uname = js.optString("uname");
        t.divId = js.optInt("div");
        return t;
    }

    public static List<DivInfo> getDivs(JSONArray a) {
        return mapList(a, cDivMapper);
    }

    public static DivInfo getDiv(JSONObject js) {
        DivInfo t = new DivInfo();
        t.id = js.optInt("id");
        t.name = js.optString("name");
        t.uname = js.optString("uname");
        return t;
    }

    public static List<TourInfo> getTours(JSONArray a) {
        return mapList(a, cTourMapper);
    }

    public static TourInfo getTour(JSONObject js) {
        if (js == null)
            return null;
        TourInfo t = new TourInfo();
        t.id = js.optInt("id");
        t.name = js.optString("name");
        t.uname = js.optString("uname");
        t.started = js.optLong("start");
        t.finished = js.optLong("end", 0);
        t.status = js.optInt("status", 0);
        t.leagueId = js.optInt("lg");
        return t;
    }

    public static List<Game> getGames(JSONArray a) {
        return mapList(a, cGameMapper);
    }

    public static Game getGame(JSONObject js) {
        if (js == null)
            return null;
        Game g = new Game();
        g.id = js.optInt("id");
        g.status = js.optInt("status");
        g.stage = js.optInt("stage");
        if (g.status != 0) {
            g.score1 = js.optInt("score1");
            g.score2 = js.optInt("score2");
        }

        g.teamId1 = js.optInt("team1");
        g.teamId2 = js.optInt("team2");

        g.scheduled = js.optLong("date", 0) * 1000;
        g.leagueId = js.optInt("lg");
        g.divId = js.optInt("div");
        g.tourId = js.optInt("tr");
        return g;
    }

    public static Division getFullDiv(JSONObject js) {
        Division d= new Division();
        d.id = js.optInt("id");
        d.name = js.optString("name");
        d.uname = js.optString("uname");
        JSONArray games = js.optJSONArray("games");
        JSONArray table = js.optJSONArray("table");
        d.games = getGames(games);
        d.table = getTable(table);
        return d;
    }

    public static List<Division> getFullDivs(JSONArray a) {
        return mapList(a, cFullDivMapper);
    }

    public static List<TableRecord> getTable(JSONArray a) {
        return mapList(a, cTableRecordMapper);
    }

    public static TableRecord getTableRecord(JSONObject js) {
        TableRecord t = new TableRecord();
        t.teamId = js.optInt("team");
        t.games = js.optInt("g");
        t.points = js.optInt("pts");
        return t;
    }

    public static Counting getCounting(JSONObject js) {
        if (js == null)
            return null;
        Counting c = new Counting();
        c.id = js.optInt("id");
        c.win = js.optInt("w");
        c.loss = js.optInt("l");
        c.tie = js.optInt("t");
        c.wot = js.optInt("wo");
        c.lot = js.optInt("lo");
        c.tieAllowed = js.optBoolean("ta");
        c.otAllowed = js.optBoolean("oa");
        c.resultOnly = js.optBoolean("ro");

        return c;
    }

    public static DashInfo getDash(JSONObject js) {
        if (js == null)
            return null;
        JSONArray lgs = js.optJSONArray("leagues");
        JSONArray trs = js.optJSONArray("tours");
        JSONArray gms = js.optJSONArray("games");
        DashInfo ret = new DashInfo();
        
        ret.leagues = getLeagues(lgs);
        ret.tours = getTours(trs);
        ret.games = getGames(gms);

        return ret;
    }

    public static Tour getTourX(JSONObject js) {
        if (js == null)
            return null;
        Tour t = new Tour();
        t.id = js.optInt("id");
        t.name = js.optString("name");
        t.uname = js.optString("uname");
        t.started = js.optLong("start");
        t.finished = js.optLong("end", 0);
        t.status = js.optInt("status", 0);
        t.leagueId = js.optInt("lg");

        JSONArray divs = js.optJSONArray("divs");
        JSONObject counting = js.optJSONObject("counting");

        t.divList = getFullDivs(divs);
        t.counting = getCounting(counting);
        
        return t;
    }




}
