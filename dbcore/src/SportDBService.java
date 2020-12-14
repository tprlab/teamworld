package com.leaguetor.db;

import com.leaguetor.entity.*;
import com.leaguetor.SportTable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.leaguetor.Scheduler;
import com.leaguetor.StringUtil;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;


class SportDBService implements SportDB.Iface {

    SportDBCore mDB;
    Log logger = LogFactory.getLog(getClass());
    public static String DATE_MASK = "dd MMM yy";

    long getTime(String tz) {
        return mDB.getTime(tz);
    }

    public void setDB(SportDBCore db) {
        mDB = db;
    }

    void throwError(Throwable t) throws DbError {
        throw new DbError(LeaguetorConstants.ERROR_DATABASE_ERROR, t.getMessage());
    }

    void throwError(int code, String msg) throws DbError {
        throw new DbError(code, msg);
    }


    public List<Sport> getSportList() throws DbError, org.apache.thrift.TException {

        try {
            List<Sport> ret = mDB.getSportList();
            for (int i = 0; i < ret.size(); i++) {
                Sport sp = ret.get(i);
                if (sp.getCountingsSize() < 1)
                    ret.remove(i--);
            }
            return ret;
        } catch(Throwable t) {
            logger.error("Cannot get sport list", t);
            throwError(t);
        }
        return null;
    }


    public int createLeague(LeagueInfo lg) throws DbError, org.apache.thrift.TException {
        if (lg.sport == null && lg.sportId == 0)
            throwError(LeaguetorConstants.ERROR_VALUE_NOT_SET, "sport");

        if (lg.sport == null) {
            Sport sp = null;
            try {
                sp = mDB.getSport(lg.sportId);
            } catch(Throwable t) {
                logger.error("Cannot get sport: " + lg.sportId, t);
                throwError(t);
            }
            if (sp == null)
                throwError(LeaguetorConstants.ERROR_INVALID_VALUE, "sport");
            lg.sport = sp;
        }

        lg.uname = getLeagueUname(lg.name);
        lg.created = getTime(lg.timezone);
        logger.debug("Generated uname " + lg.uname);

        try {
            return mDB.createLeague(lg);
        } catch(Throwable t) {
            logger.error("Cannot create league", t);
            throwError(t);
        }
        return -1;
    }

    public LeagueInfo getLeagueInfo(String name) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.findLeagueInfo(name);
        } catch(Throwable t) {
            logger.error("Cannot get league", t);
            throwError(t);
        }
        return null;
    }

    public League getLeague(String name, boolean need_tours) throws DbError, org.apache.thrift.TException {
        try {
            League ret = mDB.findLeague(name);
            if (ret == null) {
                logger.warn("League " + name + " not found");
                return ret;
            }
            if (need_tours) {
                ret.setTours(mDB.getLeagueTours(ret.id, LeaguetorConstants.TOUR_STATUS_ACTIVE));
                ret.setHistory(mDB.getLeagueTours(ret.id, LeaguetorConstants.TOUR_STATUS_FINISHED));
            }
            return ret;
        } catch(Throwable t) {
            logger.error("Cannot get league", t);
            throwError(t);
        }
        return null;
    }


    String getTeamUname(String lgname, String name) throws DbError {
        String uname = name.toLowerCase().replaceAll("\\s+","-");
        try {
            String c_name = lgname + "-" + uname;
            //for (int i = 0; i < 10; i++) {
                if (mDB.findTeam(c_name) == null)
                    return c_name;
                //c_name = lgname + "-" + uname + "-" + (i + 1);
                return null;
            //}
        } catch(Throwable t) {
            logger.error("Cannot find team " + uname, t);
            throwError(t);
        }
        return null;
    }

    String getDivUname(String lgname, String name) throws DbError {
        String uname = name.toLowerCase().replaceAll("\\s+","-");
        try {
            String c_name = lgname + "-" + uname;
            //for (int i = 0; i < 10; i++) {
                if (mDB.findDivInfo(c_name) == null)
                    return c_name;
                //c_name = lgname + "-" + uname + "-" + (i + 1);
                return null;
            //}
        } catch(Throwable t) {
            logger.error("Cannot find team " + uname, t);
            throwError(t);
        }
        return null;
    }

    String getTourUname(String lgname, String name) throws DbError {
        String uname = name.toLowerCase().replaceAll("\\s+","-");
        try {
            String c_name = lgname + "-" + uname;
            //for (int i = 0; i < 10; i++) {
                if (mDB.findTourInfo(c_name) == null)
                    return c_name;
                //c_name = lgname + "-" + uname + "-" + (i + 1);
                return null;
            //}
        } catch(Throwable t) {
            logger.error("Cannot find tour " + uname, t);
            throwError(t);
        }
        return null;
    }

    String getLeagueUname(String name) throws DbError {
        try {
            String uname = name.toLowerCase().replaceAll("\\s+","-");
            String c_name = uname;
            for (int i = 0; i < 10; i++) {
                if (mDB.findLeague(c_name) == null)
                    return c_name;
                c_name = uname + "-" + (i + 1);
            }
        } catch(Throwable t) {
            logger.error("Cannot find league " + name, t);
            throwError(t);
        }
        return null;
    }


    public void saveLeague(LeagueInfo lg) throws DbError, org.apache.thrift.TException {

        logger.debug("Updating league " + lg);
        try {
            LeagueInfo old = mDB.getLeagueInfo(lg.id);

            if (!old.name.equals(lg.name)) {
                
                if (!old.name.equalsIgnoreCase(lg.name)) {
                    old.uname = getLeagueUname(lg.name);
                    logger.debug("Update league id " + old.uname);
                }
                old.name = lg.name;
            }
            if (lg.location != null)
                old.location = lg.location;
            if (lg.sport == null && lg.sportId != 0)
                lg.sport = mDB.getSport(lg.sportId);
            if (lg.sport != null)
                old.sport = lg.sport;

            mDB.saveLeague(old);
            lg.uname = old.uname;
        } catch(Throwable t) {
            logger.error("Cannot save league: " + lg, t);
            throwError(t);
        }
    }

    public List<LeagueInfo> getLeagues(int page, int size) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getLeagues(page, size);
        } catch(Throwable t) {
            logger.error("Cannot get leagues: " + page + ", " + size, t);
            throwError(t);
        }
        return null;
    }


    public int addTeam(Team t) throws DbError, org.apache.thrift.TException {
        if (t.league == null)
            throwError(LeaguetorConstants.ERROR_VALUE_NOT_SET, "league");

        if (t.leagueId == 0) {
            t.leagueId = t.league.id;
        }

        t.uname = getTeamUname(t.league.uname, t.name);
        t.created = getTime(t.league.timezone);
        logger.debug("Generated team uname " + t.uname);

        try {
            return mDB.addTeam(t);
        } catch(Throwable e) {
            logger.error("Cannot add team", e);
            throwError(e);
        }
        return -1;
    }

    public List<Team> getTeamList(int league) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getTeamList(league);
        } catch(Throwable t) {
            logger.error("Cannot get teams for: " +league, t);
            throwError(t);
        }
        return null;
    }

    public Team findTeam(String name) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.findTeam(name);
        } catch(Throwable t) {
            logger.error("Cannot find team " +name, t);
            throwError(t);
        }
        return null;
    }

    public Team findTeamEx(String name) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.findTeamEx(name);
        } catch(Throwable t) {
            logger.error("Cannot findex team " +name, t);
            throwError(t);
        }
        return null;
    }


    public Team getTeam(int id) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getTeam(id);
        } catch(Throwable t) {
            logger.error("Cannot get team " + id, t);
            throwError(t);
        }
        return null;
    }

    ///////////////////////////////
    public int addDiv(DivInfo d) throws DbError, org.apache.thrift.TException {
        if (d.league == null)
            throwError(LeaguetorConstants.ERROR_VALUE_NOT_SET, "league");

        if (d.leagueId == 0) {
            d.leagueId = d.league.id;
        }

        d.uname = getDivUname(d.league.uname, d.name);
        d.created = getTime(d.league.timezone);
        logger.debug("Generated div uname " + d.uname);

        try {
            return mDB.addDiv(d);
        } catch(Throwable e) {
            logger.error("Cannot add div " + d, e);
            throwError(e);
        }
        return -1;
    }

    public List<DivInfo> getDivList(int league) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getDivList(league);
        } catch(Throwable t) {
            logger.error("Cannot get divs for: " +league, t);
            throwError(t);
        }
        return null;
    }

    public DivInfo findDivInfo(String name) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.findDivInfo(name);
        } catch(Throwable t) {
            logger.error("Cannot find div " +name, t);
            throwError(t);
        }
        return null;
    }

    public DivInfo findDivInfoEx(String name) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.findDivInfoEx(name);
        } catch(Throwable t) {
            logger.error("Cannot findex div " +name, t);
            throwError(t);
        }
        return null;
    }


    public DivInfo getDiv(int id) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getDiv(id);
        } catch(Throwable t) {
            logger.error("Cannot get div " + id, t);
            throwError(t);
        }
        return null;
    }

    public void setTeamDiv(String team, int dv, int tr) throws DbError, org.apache.thrift.TException {
        try {
            mDB.setTeamDiv(team, dv, tr);
        } catch(Throwable t) {
            logger.error("Cannot set div " + dv + ", tr " + tr + " for team " + team, t);
            throwError(t);
        }
    }

    public String createTour(TourInfo t) throws DbError, org.apache.thrift.TException {
        if (t.league == null)
            throwError(LeaguetorConstants.ERROR_VALUE_NOT_SET, "league");

        if (t.leagueId == 0) {
            t.leagueId = t.league.id;
        }

        t.uname = getTourUname(t.league.uname, t.name);
        t.created = getTime(t.league.timezone);
        t.started = t.created;
        logger.debug("Generated tour uname " + t.uname);

        String ret = null;

        try {
            League lg = mDB.getLeague(t.leagueId);
            if (lg == null) {
                logger.error("No league for create tour " + t);
                throwError(LeaguetorConstants.ERROR_INVALID_VALUE, "league");
            }

            ret = mDB.addTour(t);
            if (ret == null) {
                logger.error("Cannot create tour " + t);
                throwError(LeaguetorConstants.ERROR_DATABASE_ERROR, "tour");
            }

            if (lg.getDivsSize() > 0) {
                for (Division d : lg.divs.values()) {
                    if (d.getTeamsSize() < 1)
                        continue;

                    List<TableRecord> tbl = createTable(lg.id, t.id, d.id, d.teams);
                    mDB.writeTable(tbl);
                }
            } else {
                List<TableRecord> tbl = createTable(lg.id, t.id, 0, lg.teams);
                mDB.writeTable(tbl);
            }
        } catch(Throwable e) {
            logger.error("Cannot add tour", e);
            throwError(e);
        }

        return ret;
    }

    public TourInfo findTourInfo(String id) throws DbError, org.apache.thrift.TException {
        try {
            TourInfo ret = mDB.findTourInfo(id);
            if (ret != null)
                ret.setLeague(mDB.getLeagueInfo(ret.leagueId));
            return ret;
        } catch(Throwable t) {
            logger.error("Cannot get tour info " + id, t);
            throwError(t);
        }
        return null;
    }
    public List<Game> scheduleRoundRobin(int league, int tour, int div, List<Team> teams, int n, int mode) {
        int rounds = n;
        if (mode != LeaguetorConstants.SCH_MODE_ROUNDS) {
            int rg = teams.size() - 1;
            rounds = n % rg == 0 ? n / rg : n / rg + 1;
        }
        List<Game> dgames = Scheduler.scheduleRoundRobin(teams, rounds);
        if (dgames == null)
            return null;
        for (Game g : dgames) {
            g.divId = div;
            g.tourId = tour;
            g.leagueId = league;
        }
        return dgames;
    }

    void setDates(List<Game> games, List<Team> teams, ScheduleParams sch)  {
        Date dt = StringUtil.parseDate(sch.startDate, DATE_MASK);
        if (dt == null)
            dt = new Date();

        Date btm = StringUtil.parseTime(sch.startTime);
        Date etm = StringUtil.parseTime(sch.endTime);
        logger.debug("Scheduling dates " + sch.day + " " +  dt + " " + btm + " " + etm + " "+ sch.gameLength + " "+ sch.gameNum);
        if (btm == null || etm == null)
            return;

/*
        if (!sch.schtime)
            return;
        Date dt = StringUtil.parseDate(sch.startDate, DATE_MASK);
        if (dt == null)
            dt = new Date();
        Date tm = StringUtil.parseTime(sch.startTime);
        long ttm = tm == null ? 0 : tm.getTime();
        long gdt = StringUtil.getNearestDay(dt, sch.day);
        String sdt = StringUtil.formatDate(new Date(gdt), DATE_MASK);
        
        int GR = games.size() / (teams.size() - 1);
        logger.debug("Start with date " + sdt + "after " + dt);
        int c = 0;
        for (Game g : games) {
            if (c < GR) {
                c++;                        
            } else {
                c = 1;
                gdt = StringUtil.getNextWeek(gdt);
            }
            g.scheduled = gdt + ttm;
        }
*/
        Scheduler.setDates(games, teams, sch.day, dt.getTime(), btm.getTime(), etm.getTime(), sch.gameLength, sch.gameNum);
    }

    public int scheduleRoundRobin(ScheduleParams sch) throws DbError, org.apache.thrift.TException {
        logger.debug("Scheduling RR " + sch);
        try {
            TourInfo tr = mDB.findTourInfo(sch.tourName);
            League lg = mDB.getLeague(tr.leagueId);
            DivInfo dv = mDB.findDivInfo(sch.divName);
            boolean divs = lg.getDivsSize() > 0;
            List<Game> games = new ArrayList<Game>();
            if (divs) {
                if (dv == null) {
                    for (Division d : lg.divs.values()) {
                        logger.debug("Scheduling div " + d.name + " with " + d.getTeamsSize() + " teams");
                        if (d.getTeamsSize() < 2)
                            continue;
                        List<Game> dgames = scheduleRoundRobin(lg.id, tr.id, d.id, d.teams, sch.rounds, sch.extmode);
                        setDates(dgames, d.teams, sch);
                        games.addAll(dgames);
                    }
                } else {
                    Division d = lg.divs.get(dv.id);
                    if (d != null) {
                        games = scheduleRoundRobin(lg.id, tr.id, d.id, d.teams, sch.rounds, sch.extmode);
                        setDates(games, d.teams, sch);
                    }
                }

            } else if (lg.getTeamsSize() > 2) {
                games = scheduleRoundRobin(lg.id, tr.id, 0, lg.teams, sch.rounds, sch.extmode);
                setDates(games, lg.teams, sch);
            }


            if (sch.extmode == LeaguetorConstants.SCH_MODE_LIMIT) {
                int rm = Scheduler.applyLimit(games, sch.rounds);
                logger.debug("Removed " + rm + " games after limit " + sch.rounds);
            }
            mDB.saveGames(games);
            return games.size();            
        } catch(Throwable t) {
            logger.error("Cannot schedule rr tour " + sch, t);
            throwError(t);
        }
        return 0;
    }

    public Tour findTour(String id, int games) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.findTour(id, games);
        } catch(Throwable t) {
            logger.error("Cannot get tour" + id, t);
            throwError(t);
        }
        return null;
    }

    public int saveGames(List<Game> games) throws DbError, org.apache.thrift.TException {
        List<Game> info = new ArrayList<Game>();
        List<Game> results = new ArrayList<Game>();
        List<Game> cancel = new ArrayList<Game>();
        List<Game> delete = new ArrayList<Game>();
        List<Game> add = new ArrayList<Game>();
        Map<Game, Game> mg = new HashMap<Game, Game>();        
        int tour = 0;
        int div = 0;
        int league = 0;
        try {
            for (Game g : games) {
                Game old = mDB.getGame(g.id);
                if (old == null) {
                    if (g.tourId > 0 && g.leagueId > 0)
                        add.add(g);
                    else
                        logger.warn("Decline incomplete game " +g);
                    continue;
                }
                mg.put(g, old);
                g.team1 = old.team1;
                g.team2 = old.team2;
                g.stage = old.stage;
                if (tour == 0) {
                    tour = old.tourId;
                    div = old.divId;
                    league = old.leagueId;
                }
            }
            logger.debug("" + games.size() + " for " + tour + ": " + div);
            SportTable.breakGames(mg, info, results, cancel, delete);
            logger.debug("Games " + games.size() + " broken to: info(" + info.size() + "), cancel( " + 
                cancel.size() + "), results " + results.size() + "), deleted " + delete.size() + " added " + add.size());

            mDB.saveGames(add);
            mDB.saveGames(info);
            mDB.saveGames(results);
            mDB.removeGames(delete);
            List<TableRecord> table = getTable(tour, div);
            if (table != null) {
                logger.debug("Have table with " + table.size() + " teams");
                SportTable.cancelGames(table, cancel);
                SportTable.scoreGames(table, results);
                
                Counting cnt = getCounting(tour);
                if (cnt != null) {
                    SportTable.countPoints(table, cnt, null);
                }
                mDB.writeTable(table);
            }

        } catch(Throwable t) {
            logger.error("Cannot save games", t);
            throwError(t);
        }
        return 0;
    }

    public Division findDiv(String tour, String id, int games) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.findDiv(tour, id, games);
        } catch(Throwable t) {
            logger.error("Cannot get div" + tour + ":" + id, t);
            throwError(t);
        }
        return null;
    }

    public List<TableRecord> getTable(int tour, int div)  throws DbError, org.apache.thrift.TException {
        logger.debug("Getting table for " + tour + ": " + div);
        try {
            List<TableRecord> table = mDB.getTable(tour, div);
            if (table.size() == 0) {
                TourInfo tr = mDB.getTourInfo(tour);
                if (tr == null) {
                    logger.debug("Tour " + tour + " not found");
                    return null;
                }
                List<Team> lt = mDB.getTeamListDiv(tr.leagueId, div);
                logger.debug("Got " + lt.size() + " teams for " + tour + ": " + div);
                return createTable(tr.leagueId, tour, div, lt);
            }
            return table;
        } catch(Throwable t) {
            logger.error("Cannot get table for " + tour + ":" + div, t);
            throwError(t);
        }
        return null;
    }

    TableRecord createRecord(int lg, int tr, int div, Team t) {
        TableRecord r = new TableRecord();
        r.team = t;
        r.teamId = t.id;
        r.tourId = tr;
        r.divId = div;
        r.leagueId = lg;
        return r;
    }

    List<TableRecord> createTable(int lg, int tr, int div, List<Team> teams) {
        List<TableRecord> lt = new ArrayList<TableRecord>();
        for (Team t : teams) {
            lt.add(createRecord(lg, tr, div, t));
        }
        return lt;
    }

    public void recalcTable(int tour, int div)  throws DbError, org.apache.thrift.TException {
        try {
            TourInfo tr = mDB.getTourInfo(tour);
            List<Team> teams = mDB.getTeamListDiv(tr.leagueId, div);
            mDB.clearTable(tour, div);
            List<TableRecord> lt = createTable(tr.leagueId, tr.id, div, teams);
            List<Game> games = mDB.loadGames(tour, div);
            SportTable.refreshTable(lt, games);
            Counting cnt = getCounting(tr);
            logger.debug("Refreshing for " + tr.uname + " is " + cnt + " on games " + games.size());
            if (cnt != null) {
                SportTable.countPoints(lt, cnt, null);
            }
            mDB.writeTable(lt);
        } catch(Throwable t) {
            logger.error("Cannot recalc table for " + tour + ":" + div, t);
            throwError(t);
        }
    }

    public Counting getCounting(TourInfo tr) throws DbError, org.apache.thrift.TException {
        try {                                 
            LeagueInfo lg = mDB.getLeagueInfo(tr.leagueId);
            return lg.sport.countings != null && lg.sport.countings.size() > 0 ? lg.sport.countings.get(0) : null;
        } catch(Throwable t) {
            logger.error("Cannot counting for tour " + tr,  t);
            throwError(t);
        }
        return null;
    }


    public Counting getCounting(int tour) throws DbError, org.apache.thrift.TException {
        TourInfo tr = null;
        try {
            tr = mDB.getTourInfo(tour);
        } catch(Throwable t) {
            logger.error("Cannot counting for tour " + tr,  t);
            throwError(t);
        }
        return getCounting(tr);
    }

    public int schedulePlayOff(ScheduleParams sch) throws DbError, org.apache.thrift.TException {
        logger.debug("Schedule play off " + sch);
        TourInfo tr = findTourInfo(sch.tourName);
        DivInfo div = findDivInfo(sch.divName);
        logger.debug("Using tour " + tr + " div " + div);

        for (int i = 0; i < sch.games.size();i++) {
            Game g = sch.games.get(i);
            if (g.removed) {
                sch.games.remove(i--);
                continue;
            }
            g.tourId = tr.id;
            g.leagueId = tr.leagueId;
            g.divId = div == null ? 0 : div.id;
            g.stage = -sch.stage;
        }

        try {
            mDB.saveGames(sch.games);
            return sch.games.size();            
        } catch(Throwable t) {
            logger.error("Cannot schedule playoff " + sch, t);
            throwError(t);
        }

        return 0;
    }

    public int getPlayOffStage(int tour, int div) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getPlayOffStage(tour, div);
        } catch(Throwable t) {
            logger.error("Cannot get playoff stage " + tour +":" + div , t);
            throwError(t);
        }
        return 0;        
    }

    public int getPlayOffFirstStage(int tour, int div) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getPlayOffFirstStage(tour, div);
        } catch(Throwable t) {
            logger.error("Cannot get playoff first stage " + tour +":" + div , t);
            throwError(t);
        }
        return 0;        
    }


    public ScheduleParams suggestPlayOff(int tour, int div, int stage, boolean all) throws DbError, org.apache.thrift.TException {

        List<TableRecord> table = getTable(tour, div);
        int[] stages = Scheduler.offerPlayOffStage(table.size());

        int last_stage =-getPlayOffStage(tour, div);
        int first_stage =-getPlayOffFirstStage(tour, div);

        if (stage == 0) {
            stage = last_stage == 0 ? stages[0] : last_stage == 1 ? 1 : last_stage >> 1;
        }

        logger.debug("Offered stage " + stage + " on table " + table.size() + " where last " + last_stage + ", first " + first_stage);
        if (stage == 0)
            return null;

        ScheduleParams sch = new ScheduleParams();
        sch.stage = stage;
        
        for (int s = stages[1]; s != 1; s /= 2)
            sch.addToStages(s);
        int mx_size = first_stage > 0 ? first_stage * 2 : stage * 2;
        sch.history = first_stage > 0 ? getPlayOffHistory(tour, div) : null;
        sch.games = Scheduler.schedulePlayOff(table, sch.history, -stage);
        sch.table = table;

        if (all == false) {
            while(table.size() > mx_size)
                table.remove(table.size() - 1);
        }
        return sch;
    }

    List<Game> getPlayOffHistory(int tour, int div) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getPlayOffHistory(tour, div);
        } catch(Throwable t) {
            logger.error("Cannot get playoff history " + tour +":" + div , t);
            throwError(t);
        }
        return null;
    }

    public void finishTour(TourInfo tr) throws DbError, org.apache.thrift.TException {
        try {
            mDB.finishTour(tr.id, tr.finished);
        } catch(Throwable t) {
            logger.error("Cannot finish tour " + tr, t);
            throwError(t);
        }
    }

    public void cancelTour(TourInfo tr)   throws DbError, org.apache.thrift.TException {
        try {
            mDB.cancelTour(tr.id);
        } catch(Throwable t) {
            logger.error("Cannot cancel tour " + tr, t);
            throwError(t);
        }
    }

    public boolean deleteDiv(String div)  throws DbError, org.apache.thrift.TException {
        try {
            return mDB.deleteDiv(div);
        } catch(Throwable t) {
            logger.error("Cannot delete div " + div, t);
            throwError(t);
        }
        return false;
    }

    public void clearSchedule(int tour, int div) throws DbError, org.apache.thrift.TException {
        try {
            mDB.clearSchedule(tour, div);
        } catch(Throwable t) {
            logger.error("Cannot clear schedule for " +  tour + " : " + div, t);
            throwError(t);
        }
    }

    public List<Game> getGames(int league, int tour, int limit, int status, boolean asc, boolean full)  throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getGames(league, tour, limit, status, asc, full);
        } catch(Throwable t) {
            logger.error("Cannot get last results for " +  league + "/ " + tour, t);
            throwError(t);
        }
        return null;
    }


    public List<LeagueInfo> getLeaguesList(List<Integer> lgs, String u) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getLeaguesList(lgs);
        } catch(Throwable t) {
            logger.error("Cannot get leagues for " +  lgs, t);
            throwError(t);
        }
        return null;
    }

    public void copyStructure(int tr) throws DbError, org.apache.thrift.TException {

        try {

            TourInfo tour = mDB.getTourInfo(tr);
            if (tour == null) {
                logger.warn("No tour " + tr);
                return;
            }

            List<Team> lt = mDB.getTeamList(tour.leagueId);
            List<TableRecord> table = mDB.getTable(tr, 0);
            if (lt == null || table == null)
                return;
            HashMap<Integer, TableRecord> ht = new HashMap<>();
            for (TableRecord r : table)
                ht.put(r.team.id, r);
            
            for (Team t : lt) {
                TableRecord r = ht.get(t.id);
                if (r == null) {
                    r = createRecord(tour.leagueId, tour.id, t.divId, t);
                    table.add(r);
                } else {
                    r.divId = t.divId;
                }
            }
            mDB.writeTable(table);
        } catch(Throwable t) {
            logger.error("Cannot copy structure for " +  tr, t);
            throwError(t);
        }
    }

    public boolean saveTeam(Team tm)throws DbError, org.apache.thrift.TException {
        try {
            Team t = mDB.findTeamEx(tm.uname);
            if (t == null) {
                logger.warn("Cannot save team " + tm);
                return false;
            }
            if (!t.name.equals(tm.name))
            {
                if (t.name.equalsIgnoreCase(tm.name))
                {
                    t.name = tm.name;
                } else {
                    String n_name = getTeamUname(t.league.uname, tm.name);
                    if (n_name == null)
                    {
                        logger.warn("Cannot generate uname for team " + tm);
                        return false;
                    }

                    t.name = tm.name;
                    t.uname = n_name;
                }
                mDB.saveTeam(t);
                tm.uname = t.uname;
            }
            if (tm.divId != t.divId) {
                setTeamDiv(t.uname, tm.divId, 0);
            }
        } catch(Throwable t) {
            logger.error("Cannot save team " +  tm + " : ", t);
            throwError(t);
        }
        return true;
    }

    public boolean deleteTeam(String uname)throws DbError, org.apache.thrift.TException {
        try {
            return mDB.deleteTeam(uname);

        } catch(Throwable t) {
            logger.error("Cannot delete team " + uname, t);
            throwError(t);
        }
        return false;
    }

    public boolean saveDiv(DivInfo div)throws DbError, org.apache.thrift.TException {
        logger.debug("Saving div " + div);
        try {
            DivInfo dv = mDB.findDivInfo(div.uname);
            if (dv == null) {
                logger.warn("Cannot find dv to save " + div);
                return false;
            }
            dv.league = mDB.getLeagueInfo(dv.leagueId);

            if (dv.name.equals(div.name))
            {
                return true;
            } else {
                if (dv.name.equalsIgnoreCase(div.name))
                {
                    dv.name = div.name;
                } else {
                    String n_name = getDivUname(dv.league.uname, div.name);
                    if (n_name == null)
                    {
                        logger.warn("Cannot generate uname for div " + div);
                        return false;
                    }

                    dv.name = div.name;
                    dv.uname = n_name;
                }
            }

            mDB.saveDiv(dv);
            div.uname = dv.uname;

        } catch(Throwable t) {
            logger.error("Cannot save div " + div , t);
            throwError(t);
        }
        return true;
    }

    public boolean saveTour(TourInfo tr)throws DbError, org.apache.thrift.TException {
        try {
            TourInfo t = mDB.findTourInfo(tr.uname);
            if (t == null) {
                logger.warn("Cannot find tour to save " + tr);
            }

            if (t.name.equals(tr.name))
            {
                return true;
            } else {
                if (t.name.equalsIgnoreCase(tr.name))
                {
                    t.name = tr.name;
                } else {
                    String n_name = getDivUname(tr.league.uname, tr.name);
                    if (n_name == null)
                    {
                        logger.warn("Cannot generate uname for tour " + tr);
                        return false;
                    }

                    t.name = tr.name;
                    t.uname = n_name;
                }
            }

            mDB.saveTour(t);                                         
            tr.uname = t.uname;

        } catch(Throwable e) {
            logger.error("Cannot save tour " + tr, e);
            throwError(e);
        }
        return true;
    }

    public boolean incRank(String div)throws DbError, org.apache.thrift.TException {

        try {

            return mDB.upRank(div);

        } catch(Throwable t) {
            logger.error("Cannot increase rank for " + div, t);
            throwError(t);
        }
        return true;
    }

    public List<Game> getTeamGames(String team, String tour) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getTeamGames(team, tour);
        } catch(Throwable t) {
            logger.error("Cannot get team games for " + team + " in "+ tour , t);
            throwError(t);
        }
        return null;
    }


    public int saveVenue(String league, Venue v) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.saveVenue(league, v);
        } catch(Throwable t) {
            logger.error("Cannot save venue " + v , t);
            throwError(t);
        }
        return 0;
    }

    public List<Venue> getVenues(String league) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getVenues(league);
        } catch(Throwable t) {
            logger.error("Cannot get venues for " + league , t);
            throwError(t);
        }
        return null;

    }

    public boolean deleteVenue(int id) throws DbError, org.apache.thrift.TException {
        logger.debug("Deleting venue " + id);
        try {
            return mDB.deleteVenue(id);
        } catch(Throwable t) {
            logger.error("Cannot delete venue " + id , t);
            throwError(t);
        }
        return false;
    }

    public boolean deleteLeague(String uname) throws DbError, org.apache.thrift.TException {
        logger.debug("Deleting league " + uname);
        try {
            return mDB.deleteLeague(uname);
        } catch(Throwable t) {
            logger.error("Cannot delete league " + uname , t);
            throwError(t);
        }
        return false;
    }

    public Game getGame(int id)  throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getGame(id);
        } catch(Throwable t) {
            logger.error("Cannot get game " +  id, t);
            throwError(t);
        }
        return null;
    }

    public List<Game> getActiveGames(List<Integer> lgids, List<LeagueInfo> lgs, List<TourInfo> tours) throws DbError, org.apache.thrift.TException {
        try {
            List<Game> ret = mDB.getLeaguesGames(lgids);
            if (ret == null)
                return ret;

            Set<Integer> lset = new HashSet<>();
            Set<Integer> tset = new HashSet<>();

            for (Game g : ret) {
                lset.add(g.leagueId);
                tset.add(g.tourId);
            }

            List<LeagueInfo> lg_set = mDB.getLeaguesList(lset);
            List<TourInfo> tr_set = mDB.getToursList(lset);
            if (lg_set != null)
                lgs.addAll(lg_set);
            if (tr_set != null) {
                tours.addAll(tr_set);
            }

            logger.debug("Gm Activity is " + ret.size() + " games, " + lgs.size() + " leagues, " + tours.size() + " tours");
            return ret;

        } catch(Throwable t) {
            logger.error("Cannot get active games " +  lgids, t);
            throwError(t);
        }
        return null;

    }

    public List<TourInfo> getActiveTours(List<Integer> lgids, List<LeagueInfo> lgs) throws DbError, org.apache.thrift.TException {
        try {
            List<TourInfo> ret = mDB.getLeaguesTours(lgids);
            if (ret == null)
                return ret;

            List<LeagueInfo> lgs2 = mDB.getLeagues(0, 10);
            Map<Integer, LeagueInfo> lmap = new HashMap<>();
            for (LeagueInfo lg : lgs2)
                lmap.put(lg.id, lg);

            lgs.addAll(lgs2);

            for (TourInfo t : ret) {
                t.league = lmap.get(t.leagueId);
            }

            //lgs.addAll(lmap.values());

            logger.debug("Tr Activity is " + ret.size() + " tours, " + lgs.size() + " leagues");
            return ret;

        } catch(Throwable t) {
            logger.error("Cannot get active tours " +  lgids, t);
            throwError(t);
        }
        return null;

    }


    public LeagueInfo getLeagueInfoById(int id) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getLeagueInfo(id);
        } catch(Throwable t) {
            logger.error("Cannot get league " +  id, t);
            throwError(t);
        }
        return null;
    }

    public TourInfo getTourInfo(int id) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getTourInfo(id);
        } catch(Throwable t) {
            logger.error("Cannot get tour " +  id, t);
            throwError(t);
        }
        return null;
    }

    public Team getTeamById(int id) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getTeam(id);
        } catch(Throwable t) {
            logger.error("Cannot get team " +  id, t);
            throwError(t);
        }
        return null;
    }

    public int resetDates(TourInfo ti, int divId) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.resetDates(ti.id, divId);
        } catch(Throwable t) {
            logger.error("Cannot reset dates for " +  ti + "(" + divId + ")", t);
            throwError(t);
        }
        return -1;
    }

    public boolean deleteGame(int id) throws DbError, org.apache.thrift.TException {
        try {
            mDB.deleteGame(id);
        } catch(Throwable t) {
            logger.error("Cannot delete game " +  id, t);
            throwError(t);
        }
        return true;
    }

    public Fan getFan(int usr, int type, int subj) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getFan(usr, type, subj);
        } catch(Throwable t) {
            logger.error("Cannot getFan for " +  usr + " " + type + " " + subj, t);
            throwError(t);
        }
        return null;
    }


    public boolean addFan(Fan f) throws DbError, org.apache.thrift.TException {
        logger.debug("Adding fan " + f);
        try {
            return mDB.addFan(f);
        } catch(Throwable t) {
            logger.error("Cannot addFan " + f, t);
            throwError(t);
        }
        return false;
    }

    public boolean removeFan(Fan f) throws DbError, org.apache.thrift.TException {
        logger.debug("Removing fan " + f);
        try {
            return mDB.removeFan(f);
        } catch(Throwable t) {
            logger.error("Cannot remove fan " + f, t);
            throwError(t);
        }
        return false;
    }

    public List<LeagueInfo> getFanLeagues(int user) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getFanLeagues(user);
        } catch(Throwable t) {
            logger.error("Cannot get league fans for  " + user, t);
            throwError(t);
        }
        return null;
    }

    public List<Team> getFanTeams(int user) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getFanTeams(user);
        } catch(Throwable t) {
            logger.error("Cannot get teams fans for  " + user, t);
            throwError(t);
        }
        return null;
    }


    public List<Player> getFans(int subj, int type) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getFans(subj, type);
        } catch(Throwable t) {
            logger.error("Cannot get fans for  " + subj + " " + type, t);
            throwError(t);
        }
        return null;
    }

    public List<Team> getTeamListById(List<Integer> ids) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getTeamListById(ids);
        } catch(Throwable t) {
            logger.error("Cannot get teams for  " + ids, t);
            throwError(t);
        }
        return null;
    }

    public Player getPlayer(int id) throws DbError, org.apache.thrift.TException {
        try {
            return mDB.getPlayer(id);
        } catch(Throwable t) {
            logger.error("Cannot get player  " + id, t);
            throwError(t);
        }
        return null;

    }


}
