package com.leaguetor.net;

import com.leaguetor.entity.*;
import com.leaguetor.StringUtil;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;



import com.esportplace.android.SportModel;
import com.esportplace.android.Tracer;

class SportCache implements SportModel {


    SportModel mImpl;


    HashMap<String, Object> mCache = new HashMap<String, Object>();
    HashMap<String, LeagueInfo> mLgCache = new HashMap();
    HashMap<Integer, LeagueInfo> mLgidCache = new HashMap();

    HashMap<String, TourInfo> mTrCache = new HashMap();
    HashMap<Integer, TourInfo> mTridCache = new HashMap();


    void cacheLeague(LeagueInfo li) {
        mLgCache.put(li.uname, li);
        mLgidCache.put(li.id, li);
    }

    void clearLeague(LeagueInfo li) {
        mLgCache.remove(li.uname);
        mLgidCache.remove(li.id);
    }

    void cacheTour(TourInfo li) {
        mTrCache.put(li.uname, li);
        mTridCache.put(li.id, li);
    }

    void clearTour(TourInfo li) {
        mTrCache.remove(li.uname);
        mTridCache.remove(li.id);
    }



    public void setImpl(SportModel model) {
        mImpl = model;
    }


    public List<Sport> getSportList() {
        List<Sport> ret = (List<Sport>)mCache.get("sport");
        Tracer.log("Sports from cache " + ret);
        if (ret == null) {
            ret = mImpl.getSportList();
            mCache.put("sport", ret);
            if (ret != null) {
                for (Sport sp : ret) {
                    mCache.put("sport-" + sp.name, sp);
                }
            }
        }
        return ret;
    }



    public List<LeagueInfo> getUserLeagues(String usr) {
        String key = "lgs-" + usr;
        List<LeagueInfo> ret =  (List<LeagueInfo>)mCache.get(key);
        if (ret == null) {
            ret = mImpl.getUserLeagues(usr);
            mCache.put(key, ret);
            if (ret != null) {
                for (LeagueInfo li : ret) {
                    li.ownername = usr;
                    cacheLeague(li);
                }
            }
        } else {
            Tracer.log("Got user leagues from cache for " + usr);
        }
        return ret;
    }


    public LeagueInfo getLeagueInfo(String lgname) {
        LeagueInfo ret = (LeagueInfo)mLgCache.get(lgname);
        if (ret == null || ret.sportId == 0) {
            ret = mImpl.getLeagueInfo(lgname);
            cacheLeague(ret);
        }
        return ret;
    }

    public LeagueInfo getLeagueInfo(int id) {
        LeagueInfo ret = (LeagueInfo)mLgidCache.get(id);
        if (ret == null || ret.sportId == 0) {
            ret = mImpl.getLeagueInfo(id);
            cacheLeague(ret);
        }
        return ret;

    }




    public List<Team> getTeams(String league) {
        String key = "ltms-" + league;
        List<Team> lt = (List<Team>)mCache.get(key);
        if (lt == null) {
            lt = mImpl.getTeams(league);
            if (lt != null) {
                Tracer.log("Got " + lt.size() + " teams from server");
                for (Team t : lt) {
                    Tracer.log("Put team to cache " + t);
                    mCache.put("team-" + t.id, t);
                    mCache.put("team-" + t.uname, t);
                }
            }
            mCache.put(key, lt);
        } else {
            Tracer.log("Got " + lt.size() + " teams from cache");
        }
        return lt;
    }

    public List<DivInfo> getDivs(String league) {
        String key = "divs-" + league;
        List<DivInfo> lt = (List<DivInfo>)mCache.get(key);
        if (lt == null) {
            lt = mImpl.getDivs(league);
            if (lt != null) {
                for (DivInfo d : lt) {
                    mCache.put("di-" + d.id, d);
                    if (d.id > 0)
                        mCache.put("div-" + d.uname, d);
                }
            }
            mCache.put(key, lt);
        }
        return lt;
    }

    public List<TourInfo> getTours(String league) {
        String key = "tours-" + league;
        List<TourInfo> lt = (List<TourInfo>)mCache.get(key);
        if (lt == null) {
            lt = mImpl.getTours(league);
            if (lt != null) {
                for (TourInfo t : lt) {
                    Tracer.log("Put tour to cache " + t);
                    t.leagueName = league;
                    cacheTour(t);
                }
            }
            mCache.put(key, lt);
        }
        return lt;
    }

    public List<Game> getTourGames(String tr) {
        String key = "games-" + tr;
        List<Game> lt = (List<Game>)mCache.get(key);
        if (lt == null) {
            lt = mImpl.getTourGames(tr);
            mCache.put(key, lt);
            if (lt != null) {
                TourInfo tour = getTourInfo(tr);
                if (tour == null) {
                    Tracer.log("No tour for games " + tr);
                } else {
                    List<Team> tms = getTeams(tour.leagueName);
                    Tracer.log("Have " + (tms == null ? 0 : tms.size()) + " teams for games " + tr);
                }
                for (Game g : lt) {
                    g.team1 = getTeam(g.teamId1);
                    g.team2 = getTeam(g.teamId2);
                    Tracer.log("Filled teams " + g.team1 + " " + g.team2);
                    mCache.put("game-" + g.id, g);
                }
            }
        }
        return lt;
    }

    public List<Division> getTourDivs(String tr) {
        TourInfo tour = getTourInfo(tr);
        if (tour == null)
            return null;

        List<DivInfo> dis = getDivs(tour.leagueName);

        String key = "tdivs-" + tr;
        List<Division> lt = (List<Division>)mCache.get(key);
        if (lt == null) {
            lt = mImpl.getTourDivs(tr);
            if (lt != null) {
                for (Division d : lt) {
                    DivInfo di = getDivInfo(d.id);
                    if (di != null) {
                        d.name = di.name;
                        d.uname = di.uname;
                    }
                    mCache.put("div-" + d.id, d);
                }
            }
            mCache.put(key, lt);
        }
        return lt;
    }


    public DivInfo getDivInfo(int id) {
        return (DivInfo)mCache.get("di-" + id);
    }

    public TourInfo getTourInfo(String uname) {
        TourInfo tr = (TourInfo)mTrCache.get(uname);
        if (tr == null) {
            tr = mImpl.getTourInfo(uname);
            if (tr != null) {
                cacheTour(tr);
            }
        }
        return tr;
    }

    public Team getTeam(int id) {
        return (Team)mCache.get("team-" + id);
    }

    public Division getDiv(int id) {
        return (Division)mCache.get("div-" + id);
    }


    public List<TableRecord> getTable(String tr, int div) {

        TourInfo tour = getTourInfo(tr);
        if (tour == null)
            return null;

        List<Team> tms = getTeams(tour.leagueName);

        Division d = getDiv(div);
        if (d == null)
            return null;
        
        if (d.table == null) {
            d.table = mImpl.getTable(tr, div);
            if (d.table != null) {
                for (TableRecord t : d.table) {
                    t.team = getTeam(t.teamId);
                }
            }
        }
        return d.table;
    }

    public Counting getCounting(String tr) {
        Counting ret = (Counting)mCache.get("cnt-" + tr);
        if (ret == null) {
            ret = mImpl.getCounting(tr);
            mCache.put("cnt-" +tr, ret);
        }
        return ret;            
    }

    public String createLeague(LeagueInfo lg) {
        String ret = mImpl.createLeague(lg);
        if (ret != null) {
            mCache.remove("lgs-" + lg.ownername);
        }
        return ret;
    }
    public Sport findSport(String name) {
        return (Sport)mCache.get("sport-" + name);
    }

    public String saveLeague(LeagueInfo lg) {
        String key = lg.uname;
        String ret = mImpl.saveLeague(lg);
        if (ret != null) {
            clearLeague(lg);
        }
        return ret;
    }


    public boolean deleteLeague(LeagueInfo lg) {
        clearLeague(lg);
        mCache.remove("lgs-" + lg.ownername);
        return mImpl.deleteLeague(lg);
    }

    public String saveTeam(Team tm) {
        mCache.remove("ltms-" + tm.leagueName);
        mCache.remove("team-" + tm.uname);
        mCache.remove("team-" + tm.id);
        return mImpl.saveTeam(tm);
    }

    public String createTeam(Team tm) {
        mCache.remove("ltms-" + tm.leagueName);
        return mImpl.createTeam(tm);
    }

    public Team getTeam(String id) {
        Team ret = (Team)mCache.get("team-" + id);
        if (ret != null)
            return ret;
        ret = mImpl.getTeam(id);
        mCache.put("team-" + ret.id, ret);
        mCache.put("team-" + ret.uname, ret);
        return ret;
    }

    public boolean deleteTeam(Team tm) {
        boolean ret = mImpl.deleteTeam(tm);
        if (ret) {
            mCache.remove("ltms-" + tm.leagueName);
            mCache.remove("team-" + tm.id);
            mCache.remove("team-" + tm.uname);
        }
        return ret;
    }

    public String saveDiv(DivInfo div) {
        mCache.remove("divs-" + div.leagueName);
        mCache.remove("di-" + div.id);
        mCache.remove("div-" + div.uname);
        return mImpl.saveDiv(div);
    }
    public String createDiv(DivInfo div) {
        mCache.remove("divs-" + div.leagueName);
        return mImpl.createDiv(div);
    }
    public boolean deleteDiv(DivInfo div) {
        boolean ret = mImpl.deleteDiv(div);
        if (ret) {
            mCache.remove("divs-" + div.leagueName);
            mCache.remove("di-" + div.id);
            mCache.remove("div-" + div.uname);
        }
        return ret;
    }

    public DivInfo getDiv(String id) {
        DivInfo ret = (DivInfo)mCache.get("div-" + id);
        if (ret != null)
            return ret;
        ret = mImpl.getDiv(id);
        mCache.put("di-" + ret.id, ret);
        mCache.put("div-" + ret.uname, ret);
        return ret;
    }

    public Game getGame(int id) {
        Game ret = (Game)mCache.get("game-" + id);
        if (ret != null)
            return ret;
        ret = mImpl.getGame(id);
        if (ret != null) {
            ret.team1 = getTeam(ret.teamId1);
            ret.team2 = getTeam(ret.teamId2);
        }

        mCache.put("game-" + ret.id, ret);
        return ret;
    }

    public boolean saveGame(Game g) {
        if (!mImpl.saveGame(g))
            return false;
        mCache.remove("game-" + g.id);
        String dikey = "di-" + g.divId;
        DivInfo di = (DivInfo)mCache.get(dikey);
        mCache.remove(dikey);
        if (di != null)
            mCache.remove("div-" + di.uname);
        return true;
    }
/*
    boolean getDashTeams(int league, DashInfo dash) {
        LeagueInfo lg = dash.leagues.get(league);
        if (lg == null) {
            Tracer.log("No dash league " + league);
            return false;
        }

        List<Team> tms = getTeams(lg.uname);
        if (tms == null) {
            Tracer.log("No teams");
            return false;
        }
        for (Team t : tms)
            dash.putToTeamMap(t.id, t);
        return true;        
    }
*/

    void prepareTable(String league, List<TableRecord> table) {
        if (table == null)
            return;
        for (TableRecord t : table) {
            t.team = getTeam(t.teamId);
            if (t.team == null) {
                getTeams(league);
                t.team = getTeam(t.teamId);
            }
        }

    }

    void prepareGames(List<Game> games) {
        if (games == null)
            return;

        for (int i = 0; i < games.size(); i++) {
            Game g = games.get(i);
            if (g.team1 == null) {
                g.team1 = getTeam(g.teamId1);
                if (g.team1 == null) {

                    LeagueInfo lg = getLeagueInfo(g.leagueId);
                    if (lg != null)
                        getTeams(lg.uname);
                    else
                        Tracer.log("No league for game " + g.id + "(" + g.leagueId + ")");
                        


                    g.team1 = getTeam(g.teamId1);
                }
            }

            if (g.team2 == null) {
                g.team2 = getTeam(g.teamId2);
            }

            if (g.team1 != null && g.team2 != null) {
                mCache.put("game-" + g.id, g);
                
            } else {
                games.remove(i--);
                Tracer.log("Skip dash game " + g);
            }
        }
    }

    public DashInfo getActivities(String uname) {
        
        String key = "dash-" + uname;
        DashInfo ret = (DashInfo)mCache.get(key);
        if (ret != null)
            return ret;
        ret = mImpl.getActivities(uname);
        mCache.put(key, ret);
        if (ret.getLeaguesSize() > 0) {
            for (LeagueInfo lg : ret.leagues) {
                cacheLeague(lg);
            }
        }

        if (ret.getToursSize() > 0) {
            for (TourInfo t : ret.tours)
                cacheTour(t);
        }
        if (ret.getGamesSize() > 0) {
            prepareGames(ret.games);
        }
        return ret;
    }

    public Tour getTour(String uname) {
        String key = "ftour-" + uname;
        Tour ret = (Tour)mCache.get(key);
        if (ret != null)
            return ret;
        ret = mImpl.getTour(uname);
        if (ret == null)
            return null;
        LeagueInfo lg = getLeagueInfo(ret.leagueId);
        ret.league = lg;
        ret.leagueName = lg.uname;
        if (ret.divList != null) {
            for (Division d : ret.divList) {
                prepareGames(d.games);
                Tracer.log("Preparing table for div " + d.id);
                if (d.table != null) {
                    prepareTable(lg.uname, d.table);
                    for (TableRecord r : d.table) {
                        if (r.team != null)
                            d.addToTeams(r.team);
                    }
                    
                }
                ret.putToDivs(d.id, d);
            }
        }
        mCache.put("ftour-" + uname, ret);
        return ret;
    }

    public TourInfo getTourInfo(int id) {
        return mTridCache.get(id);
    }

    void clearTourGames(int id) {
        TourInfo ti = getTourInfo(id);
        if (ti == null)
            return;
        mCache.remove("games-" + ti.uname);
    }

    public boolean saveGames(String tour, List<Game> lg) {
        for (Game g : lg) {
            mCache.remove("game-" + g.id);
        }
        TourInfo ti = getTourInfo(tour);
        if (ti != null)
            mCache.remove("ftour-" + ti.uname);
        return mImpl.saveGames(tour, lg);
    }

    public boolean resetDates(String tour, int div, List<Game> games) {
        for (Game g : games) {
            mCache.remove("game-" + g.id);
        }
        if(!mImpl.resetDates(tour, div, games))
            return false;
        return true;
        
    }

    public void removeGameA(Game g, Collection<Game> c) {
        if (c == null)
            return;

        c.remove(g);
    }

    public void removeGameT(Game g, Tour t) {
        if (t == null)
            return;
        for (Division d : t.divList) {
            if (d.id == g.divId) {
                removeGameA(g, d.games);
                return;
            }
        }
    }


    public boolean removeGame(int id) {
        if (!mImpl.removeGame(id))
            return false;
        String key = "game-" + id;
        Game g = (Game)mCache.get(key);
        mCache.remove(key);

        TourInfo ti = getTourInfo(g.tourId);
        if (ti == null)
            return true;
        Tour t = getTour(ti.uname);
        removeGameT(g, t);
        clearTourGames(g.tourId);
        return true;
    }

}
