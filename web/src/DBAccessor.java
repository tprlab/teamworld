package com.leaguetor.web;

import com.leaguetor.entity.*;
import com.leaguetor.StringUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;


public class DBAccessor {
    static DBAccessor cInst;
    SportDB.Iface mDB;
    public static String DATE_MASK = "dd MMM yy EEE";
    public static String[] MINUTES = new String[]{"00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"};
    protected static final Log logger = LogFactory.getLog(DBAccessor.class);

    public DBAccessor() {
        cInst = this;
    }

    public void setDB(SportDB.Iface sp) {
        mDB = sp;
    }

    public static SportDB.Iface getDB() {
        return cInst.mDB;
    }

    public static LeagueInfo getLeagueInfo(String uname) {
        try {
            return cInst.mDB.getLeagueInfo(uname);
        } catch(Throwable t) {
            logger.error("Cannot get league info " + uname, t);
        }
        return null;
    }

    public static League getLeague(String uname, boolean tours) {
        try {
            return cInst.mDB.getLeague(uname, tours);
        } catch(Throwable t) {
            logger.error("Cannot get league " + uname, t);
        }
        return null;
    }

    public static Tour getTour(String uname, int games) {
        try {
            return cInst.mDB.findTour(uname, games);
        } catch(Throwable t) {
            logger.error("Cannot get tour " + uname, t);
        }
        return null;
    }



    public static List<Sport> getSportList() {
        try {
            return cInst.mDB.getSportList();
        } catch(Throwable t) {
            logger.error("Cannot get sport list", t);
        }
        return null;
    }

    public static Team getTeam(String name) {
        try {
            return cInst.mDB.findTeamEx(name);
        } catch(Throwable t) {
            logger.error("Cannot get team " + name, t);
        }
        return null;
    }


    public static List<Team> getTeams(int league) {
        try {
            return cInst.mDB.getTeamList(league);
        } catch(Throwable t) {
            logger.error("Cannot get teams for league " + league, t);
        }
        return null;
    }

   public static List<DivInfo> getDivs(int league) {
        try {
            return cInst.mDB.getDivList(league);
        } catch(Throwable t) {
            logger.error("Cannot get divs for league " + league, t);
        }
        return null;
    }

    public static DivInfo getDivInfo(String name) {
        try {
            return cInst.mDB.findDivInfo(name);
        } catch(Throwable t) {
            logger.error("Cannot get div info " + name, t);
        }
        return null;
    }

    public static DivInfo getDivInfoEx(String name) {
        try {
            return cInst.mDB.findDivInfoEx(name);
        } catch(Throwable t) {
            logger.error("Cannot get div exinfo " + name, t);
        }
        return null;
    }


    public static TourInfo getTourInfo(String name) {
        try {
            return cInst.mDB.findTourInfo(name);
        } catch(Throwable t) {
            logger.error("Cannot get tour info " + name, t);
        }
        return null;
    }

    public static Division getDiv(String tour, String name, int games) {
        try {
            return cInst.mDB.findDiv(tour, name, games);
        } catch(Throwable t) {
            logger.error("Cannot get div " + tour + ": " + name, t);
        }
        return null;
    }

    public static void prepareGames(List<Game> games) {
        if (games == null)
            return;
        for (Game g : games) {
            if (g.stage < 0)
                g.stage = -g.stage;

            if (g.status == LeaguetorConstants.GAME_STATUS_SCORED) {
                g.strScore1 = "" + g.score1;
                g.strScore2 = "" + g.score2;
            }
            if (g.scheduled != 0) {
                Date dt =  new Date(g.scheduled);
                logger.debug("Game " + g.id + " has date " + dt);
                g.strDate = StringUtil.formatDate(dt, DATE_MASK);
//                g.shownDate = StringUtil.formatDate(dt, DATE_MASK_SH);
                g.strTime = StringUtil.formatTime(dt);
            }
        }
    }

   public static void prepareGames(List<Game> games, int league) {
        prepareGames(games);
        List<DivInfo> ld = getDivs(league);
        Map<Integer, DivInfo> md = new HashMap<Integer, DivInfo>();
        if (ld == null || games == null)
            return;

        for (DivInfo dv : ld) {
            md.put(dv.id, dv);
        }

        for (Game g : games) {
            g.division = md.get(g.divId);
         }
    }

    public static Counting getCounting(int tour) {
        try {
            return cInst.mDB.getCounting(tour);
        } catch(Throwable t) {
            logger.error("Cannot get counting for " + tour, t);
        }
        return null;
    }

    public static List<TableRecord> getTable(int tour, int div) {
        try {
            return cInst.mDB.getTable(tour, div);
        } catch(Throwable t) {
            logger.error("Cannot get table for " + tour + ":" + div, t);
        }
        return null;
    }

    public static List<Game> getGames(int league, int tour, int size, int status, boolean asc, boolean full) {
        try {
            return cInst.mDB.getGames(league, tour, size, status, asc, full);
        } catch(Throwable t) {
            logger.error("Cannot get last results " + tour, t);
        }
        return null;
    }

   public static List<Game> getTeamGames(String team, String tour) {
        try {
            return cInst.mDB.getTeamGames(team, tour);
        } catch(Throwable t) {
            logger.error("Cannot get team games for  " + team + " in " + tour, t);
        }
        return null;
    }


 
   public static List<LeagueInfo> getUserLeagues(List<Integer> lgs) {
        try {
            return cInst.mDB.getLeaguesList(lgs);
        } catch(Throwable t) {
            logger.error("Cannot get leagues for user, size  " + lgs.size(), t);
        }
        return null;
    }

    public static List<Venue> getVenues(String lg) {
        try {
            return cInst.mDB.getVenues(lg);
        } catch(Throwable t) {
            logger.error("Cannot get venues for league  " + lg, t);
        }
        return null;
    }


 
}