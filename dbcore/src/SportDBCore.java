package com.leaguetor.db;

import com.leaguetor.entity.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;


class SportDBCore {
    
    HibernateWrapper mORM;
    Log logger = LogFactory.getLog(getClass());

    public void setORM(HibernateWrapper hw) {
        mORM = hw;
    }

    public List<Sport> getSportList() throws Exception {
        return mORM.getAll(Sport.class);
    }

    public int createLeague(LeagueInfo lg) throws Exception {
        
        mORM.put(lg);
        return lg.id;
    }

    public LeagueInfo getLeagueInfo(int id) throws Exception {
        return (LeagueInfo)mORM.get(LeagueInfo.class, id);
    }

    public Sport getSport(int id) throws Exception {
        return (Sport)mORM.get(Sport.class, id);
    }

    public LeagueInfo findLeagueInfo(String uname) throws Exception {
        return (LeagueInfo)mORM.getOne(LeagueInfo.class, mORM.createParams("uname", uname));
    }

    League prepareLeague(League lg) throws Exception {
        if (lg == null)
            return null;
        if (lg.divList == null)
            return lg;
        for (Division d : lg.divList)
            lg.putToDivs(d.id, d);
        return lg;
    }

    public League findLeague(String uname) throws Exception {
        League ret = (League)mORM.getOne(League.class, mORM.createParams("uname", uname), mORM.createList("noDiv"));
        return prepareLeague(ret);
    }

    public League getLeague(int id) throws Exception {
        League lg = (League)mORM.get(League.class, id);
        return prepareLeague(lg);
    }


    public List<TourInfo> getLeagueTours(int id, int status) throws Exception {
        return (List<TourInfo>)mORM.getAll(TourInfo.class, 0, 100, 
            mORM.createParams("leagueId", id, "status", status), "started", false);
    }



    public void saveLeague(LeagueInfo lg) throws Exception {
        mORM.put(lg);
    }

    public void saveTeam(Team tm) throws Exception {
        mORM.put(tm);
    }

    public void saveDiv(DivInfo div) throws Exception {
        mORM.put(div);
    }

    public void saveTour(TourInfo tr) throws Exception {
        mORM.put(tr);
    }




    public List<LeagueInfo> getLeagues(int page, int size) throws Exception {
        return (List<LeagueInfo>)mORM.getAll(LeagueInfo.class, page * size, size, 
            mORM.createParams("status", 0), "id", true);
    }

    public int addTeam(Team t) throws Exception {
        mORM.put(t);
        return t.id;
    }

    public List<Team> getTeamList(int league) throws Exception {
        return (List<Team>)mORM.getAll(Team.class, 0, 100, mORM.createParams("leagueId", league), "created", true);
    }

    public List<Team> getTeamListDiv(int league, int div) throws Exception {
        return (List<Team>)mORM.getAll(Team.class, 0, 100, mORM.createParams("leagueId", league, "divId", div), "created", true);
    }


    public Team findTeam(String name) throws Exception {
        return (Team)mORM.getOne(Team.class, mORM.createParams("uname", name));
    }

    public Team findTeamEx(String name) throws Exception {
        Team ret = findTeam(name);
        if (ret == null)
            return ret;
        ret.league = getLeagueInfo(ret.leagueId);
        Integer games = (Integer)mORM.executeNamedSelectOne("countTeamGames", mORM.createParams("team", ret.id));
        //Integer tours = (Integer)mORM.executeNamedSelectOne("countTeamTours", mORM.createParams("team", ret.id));
        ret.games = games == null ? 0 : games;
        //ret.tours = tours == null ? 0 : tours;
        return ret;
    }


    public Team getTeam(int id) throws Exception {
        return (Team)mORM.get(Team.class, id);
    }

    /////////////////////////
    public int addDiv(DivInfo d) throws Exception {
        int rank = getDivRank(d.leagueId);
        d.rank = rank + 1;
        mORM.put(d);
        return d.id;
    }

    public List<DivInfo> getDivList(int league) throws Exception {
        return (List<DivInfo>)mORM.getAll(DivInfo.class, 0, 100, mORM.createParams("leagueId", league), "rank", true);
    }

    public DivInfo findDivInfo(String name) throws Exception {
        return (DivInfo)mORM.getOne(DivInfo.class, mORM.createParams("uname", name));
    }

    public DivInfo findDivInfoEx(String name) throws Exception {
        DivInfo div = findDivInfo(name);
        if (div == null)
            return div;
        div.league = getLeagueInfo(div.leagueId);
        div.leagueName = div.league.name;
        Integer teams = (Integer)mORM.executeNamedSelectOne("countDivTeams", mORM.createParams("dv", div.id));
        Integer tours  = (Integer)mORM.executeNamedSelectOne("countDivTours", mORM.createParams("dv", div.id));
        div.teams = teams == null ? 0 : teams;
        div.tours = tours == null ? 0 : tours;
        return div;
    }


    public DivInfo getDiv(int id) throws Exception {
        return (DivInfo)mORM.get(DivInfo.class, id);
    }

    public void setTeamDiv(String team, int dv, int tr) throws Exception {
        Team tm = findTeam(team);
        logger.debug("Setting team div " + team + " div " + dv + " tour " + tr);
        if (tm == null) {
            logger.warn("Cannot set div " + dv + " for unknown team " + team);
            return;
        }
        if (tr != 0) {
            TableRecord tmr = new TableRecord();
            tmr.teamId = tm.id;
            tmr.tourId = tr;
            tmr.divId = dv;
            tmr.leagueId = tm.leagueId;
            mORM.put(tmr);
        } else {
            tm.divId = dv;
            mORM.put(tm);
        }
    }

    public String addTour(TourInfo t) throws Exception {
        mORM.put(t);
        return t.id > 0 ? t.uname : null;
    }

    public TourInfo findTourInfo(String name) throws Exception {
        return (TourInfo)mORM.getOne(TourInfo.class, mORM.createParams("uname", name));
    }

    public Tour getTour(int id)  throws Exception {
        return (Tour)mORM.get(Tour.class, id);
    }

    public TourInfo getTourInfo(int id)  throws Exception {
        return (TourInfo)mORM.get(TourInfo.class, id);
    }


    public List<Game> loadGames(int tr) throws Exception {
        return loadGames(tr, true);
    }

    public List<Game> loadGames(int tr, boolean asc) throws Exception {
        return (List<Game>)mORM.executeNamedSelect(asc ? "loadGamesAsc" : "loadGamesDesc", mORM.createParams("tr", tr));
    }


    public List<Game> loadGames(int tr, int div) throws Exception {
        return loadGames(tr, div, true);
    }

    public List<Game> loadGames(int tr, int div, boolean asc) throws Exception {
        return (List<Game>)mORM.executeNamedSelect(asc ? "loadDivGamesAsc" : "loadDivGamesDesc", mORM.createParams("tourId", tr, "divId", div));
    }

    public List<Integer> getTourDivs(int tr) throws Exception {
        return (List<Integer>)mORM.executeNamedSelect("getTourDivs", mORM.createParams("tour", tr));
    }

    public Tour findTour(String name, int gm) throws Exception {
        Tour tr = (Tour)mORM.getOne(Tour.class, mORM.createParams("uname", name));
        if (tr == null)
            return tr;
        League lg = getLeague(tr.leagueId);
        List<Integer> ld = getTourDivs(tr.id);
        Set<Integer> divSet = new TreeSet(ld);

        if (lg.divList != null) {
          for (Division d : lg.divList) {
              if (!divSet.contains(d.id))
                  continue;
              if (d.getTeamsSize() < 1)
                  continue;
              tr.putToDivs(d.id, d);
              tr.addToDivList(d);
          }
        }

        if (tr.getDivsSize() < 1)
            tr.main = new Division();

        if (gm == 0)
            return tr;

        List<Game> games = loadGames(tr.id, gm == 1);
        if (games == null)
            return tr;
        logger.debug("Loaded " + games.size() + " games for tour " + name + " with divs " + tr.getDivsSize());
        for (Game g : games) {

            if (g.divId == 0) {
                if (tr.main == null)
                    tr.main = new Division();
                tr.main.addToGames(g);
                continue;
            }

            
            Division d = tr.divs.get(g.divId);
            if (d == null) {
                logger.warn("No div for game " + g);
                continue;
            }
            d.addToGames(g);
        }
        return tr;    
    }


    public void saveGames(List<Game> games) throws Exception {
        mORM.putAll(games);
    }

    public Game getGame(int id) throws Exception {
        return (Game)mORM.get(Game.class, id);
    }

    public Division findDiv(String tour, String divName, int gm) throws Exception {
        Division div = (Division)mORM.getOne(Division.class, mORM.createParams("uname", divName));
        if (div == null || gm == 0)
            return div;
        TourInfo tr = findTourInfo(tour);
        if (tr == null)
            return div;

        List<Game> games = loadGames(tr.id, div.id, gm == 1);
        if (games == null)
            return div;
        div.games = games;
        div.table = getTable(tr.id, div.id);
        return div;    
    }

    public List<TableRecord> getTable(int tour, int div)  throws Exception {
        /*
        return div > 0 ? (List<TableRecord>)mORM.getAll(TableRecord.class, 0, 100, mORM.createParams("tourId", tour, "divId", div), "points", false) :
         (List<TableRecord>)mORM.getAll(TableRecord.class, 0, 100, mORM.createParams("tourId", tour), "points", false);
        */
        return div > 0 ? (List<TableRecord>)mORM.executeNamedSelect("getDivTable", mORM.createParams("tourId", tour, "divId", div)) :
         (List<TableRecord>)mORM.executeNamedSelect("getTourTable", mORM.createParams("tourId", tour));
    }

    public void writeTable(List<TableRecord> lt)   throws Exception {
        mORM.putAll(lt);
    }

    public void clearTable(int tour, int div)   throws Exception  {
        mORM.executeQuery("clearTable", mORM.createParams("tour", tour, "dv", div));
    }

    public void removeGames(List<Game> g) throws Exception {
        mORM.deleteAll(g);
    }

    public int getPlayOffStage(int tour, int div) throws Exception {
        Integer ret = (Integer)mORM.executeNamedSelectOne("getPlayOffStage", mORM.createParams("tour", tour, "dv", div));
        return ret == null ? 0 : ret;
    }

    public int getPlayOffFirstStage(int tour, int div) throws Exception {
        Integer ret = (Integer)mORM.executeNamedSelectOne("getPlayOffFirstStage", mORM.createParams("tour", tour, "dv", div));
        return ret == null ? 0 : ret;
    }

    public List<Game> getPlayOffHistory(int tour, int div) throws Exception {
        return (List<Game>)mORM.executeNamedSelect("getPlayOffHistory", mORM.createParams("tour", tour, "dv", div));
    }

    long getTime(String tzname) {
        TimeZone tz = tzname == null ? TimeZone.getDefault() : TimeZone.getTimeZone(tzname);

        return System.currentTimeMillis() + tz.getRawOffset();
    }

    void finishTour(int tour, long tm) throws Exception {
        TourInfo tr = getTourInfo(tour);
        LeagueInfo lg = getLeagueInfo(tr.leagueId);
        tr.status = LeaguetorConstants.TOUR_STATUS_FINISHED;
        if (tm == 0)
            tm = getTime(lg.timezone);
        tr.finished = tm;
        mORM.put(tr);
    }

    void cancelTour(int tour) throws Exception {
        TourInfo tr = getTourInfo(tour);
        if (tr != null)
            mORM.delete(tr);
    }

    boolean deleteTeam(String uname) throws Exception {
        Team tm = findTeamEx(uname);
        if (tm == null) {
            logger.warn("No team to delete " + uname);
            return false;
        }
        if (tm.games > 0) {
            logger.warn("Cannot delete team " + tm);
            return false;
        }
        mORM.delete(tm);
        return true;
    }

    boolean deleteDiv(String dname) throws Exception {
        DivInfo div = findDivInfoEx(dname);
        if (div == null) {
            logger.error("Div " + dname + " not found to be deleted");
            return false;
        }

        if (div.tours > 0 || div.teams > 0)
            return false;

        Integer gn = (Integer)mORM.executeNamedSelectOne("countDivGames", mORM.createParams("dv", div.id));
        int n = gn == null ? 0 : gn;
        if (n == 0) {
            mORM.delete(div);
            return true;
        }
        logger.warn("Cannot delete div " + dname + " porque has " + n + " games");  
        return false;
    }

    public void clearSchedule(int tour, int div)   throws Exception  {
        if (div != 0)
            mORM.executeQuery("clearSchedule", mORM.createParams("tour", tour, "dv", div));
        else
            mORM.executeQuery("clearScheduleAll", mORM.createParams("tour", tour));
    }

    public League getHashLeague(Map<Integer, League> lgs, int id) throws Exception {
        League lg = lgs.get(id);
        if (lg != null)
            return lg;
        lg = getLeague(id);
        lgs.put(lg.id, lg);
        if (lg.divs != null) {
            Collection<Division> dvs = lg.divs.values();
            for (Division d : dvs) {
                DivInfo di = new DivInfo();
                di.id = d.id;
                di.name = d.name;
                di.uname = d.uname;
                lg.putToDivMap(di.id, di);
            }
        }
        LeagueInfo li = new LeagueInfo();
        li.id = lg.id;
        li.name = lg.name;
        li.uname = lg.uname;
        li.sport = lg.sport;
        lg.info = li;

//        logger.debug("Loaded hash league " + lg);
        return lg;
    }

    public TourInfo getHashTour(Map<Integer, League> lgs, int lgid, int id) throws Exception {
        League lg = lgs.get(lgid);
        if (lg == null)
            return null;
        TourInfo ret = null;
        if (lg.tourMap != null)
            ret = lg.tourMap.get(id);
        if (ret != null)
            return ret;
        ret = getTourInfo(id);
        lg.putToTourMap(ret.id, ret);
        return ret;
    }


    public List<Game> fullGames(List<Game> games) throws Exception {
        HashMap<Integer, League> lgs = new HashMap<>();

        for (Game g : games) {
            League lg = getHashLeague(lgs, g.leagueId);
            g.league = lg.info;
//            logger.debug("Set hash league for game " + g.id + ": "  + lg.info);
            if (g.divId > 0)
                g.division = lg.divMap.get(g.divId);
            g.tour = getHashTour(lgs, lg.id,  g.tourId);
        }
        return games;
    }

    public List<Game> getGames(int league, int tour, int limit, int status, boolean asc, boolean full)  throws Exception {

        Map<String, Object> params = mORM.createParams();
        if (tour > 0)
            params.put("tourId", tour);
        if (status != -1)
            params.put("status", status);

        Boolean dir = new Boolean(asc);

        List<Game> ret = (List<Game>)mORM.getAll(Game.class, 0, limit, params,
            mORM.createList("id", "scheduled"), mORM.createList(dir , dir));

        if (!full || ret == null)
            return ret;

        return fullGames(ret);
    }


    public List<LeagueInfo> getLeaguesList(Collection<Integer> lgs) throws Exception {
        List<LeagueInfo> a = (List<LeagueInfo>)mORM.getAll(LeagueInfo.class, lgs);
        if (a == null || a.size() < 1)
            return a;
        List<LeagueInfo> ret = mORM.createList();
        for (LeagueInfo li : a) {
            if (li.status < 0)
                continue;
            ret.add(li);
        }
        return ret;
    }

    public List<TourInfo> getToursList(Collection<Integer> trs) throws Exception {
        List<TourInfo> a = (List<TourInfo>)mORM.getAll(TourInfo.class, trs);
        return a;
    }


    public int getDivRank(int league) throws Exception {
        Integer rank = (Integer)mORM.executeNamedSelectOne("getDivRank", mORM.createParams("league", league));
        return rank == null ? -1 : rank;
    }

    public boolean upRank(String div)throws Exception {

        DivInfo dv = findDivInfo(div);
        if (dv == null) {
            logger.warn("Div " + div + " not found to increase rank");
            return false;
        }

        List<DivInfo> divs = getDivList(dv.leagueId);
        int i = 0;
        for (; i < divs.size(); i++) {
            DivInfo d = divs.get(i);
            if (d.id == dv.id)
                break;
        }
        if (i == divs.size()) {
            logger.warn("Div " + div + " not found to up rank???");
            return false;
        }

        if (i == 0) {
            logger.warn("Div " + div + " has highest rank already");
            return false;
        }

        DivInfo dp = divs.get(i - 1);
        int old = dv.rank;
        dv.rank = dp.rank;
        dp.rank = old;

        ArrayList<DivInfo> l = new ArrayList<>();
        l.add(dv);
        l.add(dp);

        mORM.putAll(l);
        return true;
    }

    public List<Game> getTeamGames(String team, String tour) throws Exception {
        Team tm = findTeam(team);
        TourInfo tr = findTourInfo(tour);
        if (tm == null || tr == null)
            return null;
        return (List<Game>)mORM.executeNamedSelect("getTeamGames", 
            mORM.createParams("tour", tr.id, "team", tm.id));
    }

    public int saveVenue(String league, Venue v) throws Exception {
        LeagueInfo lg = findLeagueInfo(league);
        if (lg == null)
            return 0;
        v.leagueId = lg.id;
        if (!v.active)
            v.status = -1;
        mORM.put(v);
        return v.id;
    }

    public List<Venue> getVenues(String league) throws Exception {
        LeagueInfo lg = findLeagueInfo(league);
        if (lg == null)
            return null;

        List<Venue> ret = (List<Venue>)mORM.getAll(Venue.class, 0, 100, mORM.createParams("leagueId", lg.id), "id", true);
        for (Venue v : ret) {
            v.active = v.status != -1;
        }
        return ret;
    }

    public boolean deleteVenue(int id) throws Exception {
        Venue v = (Venue)mORM.get(Venue.class, id);
        if (v == null)
            return false;
        mORM.delete(v);
        return true;
    }

    public boolean deleteLeague(String uname) throws Exception {
        LeagueInfo li = findLeagueInfo(uname);
        if (li == null) {   
            logger.warn("League " + uname + " not found for delete");
            return false;
        }
        li.status = -1;
        mORM.put(li);
        return true;
    }

    public List<Game> getLeaguesGames(List<Integer> lgs) throws Exception {
        return (List<Game>)mORM.executeNamedSelect("getLeaguesGames", mORM.createParams("ids", lgs));
    }

    public List<TourInfo> getLeaguesTours(List<Integer> lgs) throws Exception {
        return (List<TourInfo>)mORM.executeNamedSelect("getLeaguesTours", mORM.createParams("ids", lgs));
    }

    public int resetDates(int ti, int divId) throws Exception {
        return divId != 0 ? mORM.executeQuery("resetDivDates", mORM.createParams("tour", ti, "divId", divId)) :
            mORM.executeQuery("resetDates", mORM.createParams("tour", ti));
    }

    public void deleteGame(int id) throws Exception {
        Game g = new Game();
        g.id = id;
        mORM.delete(g);
    }

    public Fan getFan(int usr, int type, int subj) throws Exception {
        return (Fan)mORM.executeNamedSelectOne("getFan", mORM.createParams("usr", usr, "subj", subj, "ftype", type));
    }

    public boolean addFan(Fan f) throws Exception {
        mORM.put(f);
        return true;
    }

    public boolean removeFan(Fan f) throws Exception {
        mORM.delete(f);
        return true;
    }


    public List<LeagueInfo> getFanLeagues(int user) throws Exception {
        return (List<LeagueInfo>)mORM.executeNamedSelect("getFanLeagues", mORM.createParams("usr", user));
    }

    public List<Team> getFanTeams(int user) throws Exception {
        return (List<Team>)mORM.executeNamedSelect("getFanTeams", mORM.createParams("usr", user));
    }

    public List<Player> getFans(int subj, int type) throws DbError, org.apache.thrift.TException {
        return (List<Player>)mORM.executeNamedSelect("getFans", mORM.createParams("subj", subj, "ftype", type));

    }

    public List<Team> getTeamListById(List<Integer> ids) throws Exception {
        return (List<Team>)mORM.getAll(Team.class, ids);
    }

    public Player getPlayer(int id) throws Exception {
        return (Player)mORM.get(Player.class, id);
    }


}
