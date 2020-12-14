package com.leaguetor;

import com.leaguetor.entity.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Scheduler
{
    public static List<Game> scheduleRoundRobin(List<Team> teams, int rounds) {

        if (rounds < 1)
            rounds = 1;
        if (rounds > 8)
            rounds = 8;
        
        int N = teams == null ? 0 : teams.size();
        if (N < 2) {
            System.out.println("no teams");
            return null;
        }
        
        List<int[]> tt = RoundRobin.schedule(N);
        if (tt == null) {
            System.out.println("schedule failed");
            return null;
        }
        int TN = tt.size();
        int[] perm = MathUtil.getPerm(TN);
        List<Game> ret = new ArrayList<Game>();
        int[] hosts = new int[N];
        int[] guests = new int[N];
        Random rnd = new Random();

        for (int i = 0; i < N; i++) {
            hosts[i] = 0;
            guests[i] = 0;
        }
        

        for (int n = 0; n < rounds; n++) {
            for (int i = 0; i < TN; i++) {
                int tix = perm[i];
                int[] gms = tt.get(tix);
                int gn = gms.length / 2;

                for (int j = 0; j < gn; j++) {
                    int t1 = gms[2 * j] - 1;
                    int t2 = gms[2 * j + 1] - 1;
//                    System.out.println("teams " + t1 + "(" + hosts[t1] + ") " +                     t2 +  "(" + hosts[t2] + ")");
                    Team tm1 = null;
                    Team tm2 = null;
                    int th = -1;
                    int tg = -1;

                    if (n % 2 == 0)
                    {
                        int h1 = hosts[t1];
                        int h2 = hosts[t2];
                        boolean hp = h1 == h2 ? 
                            guests[t1] == guests[t2] ? rnd.nextBoolean()
                            : guests[t1] > guests[t2] ? true : false  
                             : h1 > h2 ? false : true;
                        th = hp ? t1 : t2;
                        tg = hp ? t2 : t1;
                        tm1 = teams.get(th);
                        tm2 = teams.get(tg);
                    } else {
                        int gidx = ret.size() - gn * TN;
                        Game gp = ret.get(gidx);
                        tm1 = gp.team2;
                        tm2 = gp.team1;
                        th = tm1.id == teams.get(t1).id ? t1 : t2;
                        tg = th == t1 ? t2 : t1;
//                        System.out.println("Got game " + gidx + " " + tm1.name + " " + tm2.name);
                    }
                    hosts[th]++;
                    guests[tg]++;
//                    System.out.println("R" + n + " - " + ret.size() +  ". Hosts " + tm1.name + " " + hosts[th] + " vs " + tm2.name + " " + hosts[tg]);
                    Game g = new Game();
                    g.setTeam1(tm1);
                    g.setTeam2(tm2);
                    ret.add(g);
                }
            }
        }

        return ret;
    }

    public static List<Game> schedulePlayOff(List<TableRecord> table, List<Game> hist, int stage) {
        return PlayOff.schedule(table, hist, stage);
    }

    public static int applyLimit(List<Game> lg, int limit) {
        int ret = 0;
        Map<Team, Integer> cnt = new HashMap<Team, Integer>();
        for (int i = 0; i < lg.size(); i++) {
            Game g = lg.get(i);
            Integer t1 = cnt.get(g.team1);
            Integer t2 = cnt.get(g.team2);
            int tn1 = t1 == null ? 0 : t1;
            int tn2 = t2 == null ? 0 : t2;
            if (tn1 == limit || tn2 == limit) {
                lg.remove(i--);
                ret++;
                continue;
            }
            cnt.put(g.team1, tn1 + 1);
            cnt.put(g.team2, tn2 + 1);
        }
        return ret;
    }

    public static int[] offerPlayOffStage(int size) {
        int reg = MathUtil.getLowerBin((short)size) / 2;
        int max = MathUtil.isBin(size) ? reg : reg * 2;
        return new int[]{reg, max};
    }
/*
    public int scheduleRoundRobin(ScheduleParams sch) {
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
*/
    public static List<Game> scheduleRoundRobin(int league, int tour, int div, List<Team> teams, int n, int mode) {
        int rounds = n;
        if (mode != LeaguetorConstants.SCH_MODE_ROUNDS) {
            int rg = teams.size() - 1;
            rounds = n % rg == 0 ? n / rg : n / rg + 1;
        }
        List<Game> dgames = scheduleRoundRobin(teams, rounds);
        if (dgames == null)
            return null;
        if (mode != LeaguetorConstants.SCH_MODE_ROUNDS) {
            applyLimit(dgames, n);
        }
        for (Game g : dgames) {
            g.divId = div;
            g.tourId = tour;
            g.leagueId = league;
        }
        return dgames;
    }


}