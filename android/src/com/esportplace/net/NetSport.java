
package com.leaguetor.net;

import com.leaguetor.entity.*;
import com.leaguetor.StringUtil;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import org.json.JSONObject;
import org.json.JSONArray;

import com.esportplace.android.SportModel;
import com.esportplace.android.Tracer;

import com.leaguetor.rest.JMapper;

class NetSport implements SportModel {

    //String mServer = "192.168.1.40/rest";
    String mServer = "sportlanding.com/rest";
    String mProto = "https://";

    String getUrl(String ...args) {
        StringBuilder b = new StringBuilder();
        b.append(mProto).append(mServer);
        for (String a : args) {
            b.append("/").append(a);
        }
        return b.toString();
    }


    public List<Sport> getSportList() {
        String url = getUrl("sport");
        JSONArray js = NetClient.getArray(url);
        return JFactory.getSports(js);
        
    }

    public List<LeagueInfo> getUserLeagues(String usr) {
        String url = getUrl("user", usr, "leagues");
        JSONArray js = NetClient.getArray(url);
        return JFactory.getLeagues(js);
    }

    public LeagueInfo getLeagueInfo(String lgname) {
        String url = getUrl("league", lgname);
        JSONObject js = NetClient.getJSON(url);
        LeagueInfo ret = JFactory.getLeagueInfo(js);
        return ret;
    }


    public List<Team> getTeams(String league) {
        String url = getUrl("league", league, "teams");
        JSONArray js = NetClient.getArray(url);
        return JFactory.getTeams(js);
    }

    public List<DivInfo> getDivs(String league) {
        String url = getUrl("league", league, "divs");
        JSONArray js = NetClient.getArray(url);
        return JFactory.getDivs(js);
    }

    public List<TourInfo> getTours(String league) {
        String url = getUrl("league", league, "tours");
        JSONArray js = NetClient.getArray(url);
        return JFactory.getTours(js);
    }

    public List<Game> getTourGames(String tr) {
        String url = getUrl("tour", tr, "games");
        JSONArray js = NetClient.getArray(url);
        return JFactory.getGames(js);
    }

    public List<Division> getTourDivs(String tr){
        String url = getUrl("tour", tr, "divs");
        JSONArray js = NetClient.getArray(url);
        return JFactory.getFullDivs(js);
    }


    public List<TableRecord> getTable(String tr, int div) {
        String url = getUrl("tour", tr, "table", "" + div);
        JSONArray js = NetClient.getArray(url);
        return JFactory.getTable(js);
    }


    public Counting getCounting(String tr) {
        String url = getUrl("tour", tr, "counting");
        JSONObject js = NetClient.getJSON(url);
        return JFactory.getCounting(js);
    }

    public String createLeague(LeagueInfo lg) {
        Map<String, Object> m = JMapper.mapLeague(lg, true);
        String url = getUrl("league", "create");
        return postForId(url, m, "create league");
    }
    public Sport findSport(String name) {
        return null;
    }

    protected String postForId(String url, Map<String, Object> data, String desc) {
        JSONObject js = NetClient.post(url, data);
        if (js == null)
            return null;
        int code = js.optInt("code");
        String ret = null;
        if (code != 0) {
            String err = js.optString("error");
            Tracer.log(desc + " error: " + err);
            return null;
        }
        return js.optString("id");
    }

    protected boolean postForCode(String url, Map<String, Object> data, String desc) {
        JSONObject js = NetClient.post(url, data);
        if (js == null)
            return false;
        int code = js.optInt("code");
        if (code != 0) {
            String err = js.optString("error");
            Tracer.log(desc + " error: " + err);
            return false;
        }
        return true;
    }


    public String saveLeague(LeagueInfo lg) {
        Map<String, Object> m = JMapper.mapLeague(lg, true);
        String url = getUrl("league", lg.uname, "update");
        return postForId(url, m, "save league");
    }

    public boolean deleteLeague(LeagueInfo li) {
        String url = getUrl("league", li.uname, "delete");
        return postForCode(url, null, "delete league");
    }


    public String saveTeam(Team tm) {
        String url = getUrl("team", tm.uname, "update");
        Map<String, Object> m = JMapper.mapTeam(tm);
        return postForId(url, m, "save team");
    }

    public String createTeam(Team tm) {
        String url = getUrl("league", tm.leagueName, "team", "create");
        Map<String, Object> m = JMapper.mapTeam(tm);
        return postForId(url, m, "create team");
    }

    public Team getTeam(String uname) {
        String url = getUrl("team", uname);

        JSONObject js = NetClient.getJSON(url);
        return JFactory.getTeam(js);
    }

    public boolean deleteTeam(Team tm) {
        String url = getUrl("team", tm.uname, "delete");
        return postForCode(url, null, "delete team");
    }

    public String saveDiv(DivInfo div) {
        String url = getUrl("div", div.uname, "update");
        Map<String, Object> m = JMapper.mapDiv(div);
        Tracer.log("Posting save div: " + m);
        return postForId(url, m, "save div");
    }

    public String createDiv(DivInfo div) {
        String url = getUrl("league", div.leagueName, "div", "create");
        Map<String, Object> m = JMapper.mapDiv(div);
        return postForId(url, m, "create div");
    }

    public DivInfo getDiv(String uname) {
        String url = getUrl("div", uname);

        JSONObject js = NetClient.getJSON(url);
        return JFactory.getDiv(js);
    }

    public boolean deleteDiv(DivInfo div) {
        String url = getUrl("div", div.uname, "delete");
        return postForCode(url, null, "delete div");
    }

    public Game getGame(int id) {
        String url = getUrl("game", "" + id);

        JSONObject js = NetClient.getJSON(url);
        return JFactory.getGame(js);
    }

    public boolean saveGame(Game g) {
        String url = getUrl("game", "" + g.id, "update");
        Map<String, Object> m = JMapper.mapGame(g);
        return postForCode(url, m, "save game");
    }

    public DashInfo getActivities(String uname) {
        String url = getUrl("user", uname, "activity");
        JSONObject js = NetClient.getJSON(url);
        return JFactory.getDash(js);
    }

    public Tour getTour(String uname) {
        
        String url = getUrl("tour", uname);
        JSONObject js = NetClient.getJSON(url);
        return JFactory.getTourX(js);

    }

    public LeagueInfo getLeagueInfo(int id) {
        String url = getUrl("league", "" + id);
        JSONObject js = NetClient.getJSON(url);
        LeagueInfo ret = JFactory.getLeagueInfo(js);
        return ret;

    }

    public TourInfo getTourInfo(int id) {
        return null;
    }

    public TourInfo getTourInfo(String uname) {
        return null;
    }

    public boolean saveGames(String tour, List<Game> games) {
        String url = getUrl("tour", tour, "save_games");
        List l = JMapper.createList();
        int tourId = 0;
        int league = 0;
        for (Game g : games) {
            Map<String, Object> m = g.id >  0 ? JMapper.mapGameSch(g) : JMapper.mapGame(g);
            l.add(m);
            if (g.tourId > 0)
                tourId = g.tourId;
            if (g.leagueId > 0)
                league = g.leagueId;
        }
        Map<String, Object> data = JMapper.createMap();
        data.put("games", l);
        data.put("tour", tourId);
        data.put("league", league);
        Tracer.log("Saving games " + data);
        return postForCode(url, data, "save games");
    }

    public boolean resetDates(String tour, int div, List<Game> games) {
        String url = getUrl("tour", "" + tour, "reset_dates");
        Map<String, Object> m = JMapper.createMap();
        m.put("divId", div);
        return postForCode(url, m, "reset dates");
    }

    public boolean removeGame(int id) {
        String url = getUrl("game", "" + id, "delete");
        return postForCode(url, null, "remove game");
    }
}
