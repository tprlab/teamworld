package com.leaguetor;

import com.leaguetor.entity.*;

import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.Map;


public class SportTable {

    public static final int GAME_SCORED = 1;
    public static final int GAME_CREATED = 0;

    public static final int TOUR_STARTED = 0;
    public static final int TOUR_FINISHED = 1;


    public static TableRecord findTeamRecord(List<TableRecord> table, int tm) {
        for (TableRecord tr : table) {
            if (tr.team.id == tm)
                return tr;
        }
        return null;
    }

    public static Set<Integer> scoreGames(List<TableRecord> table, List<Game> games) {
        return applyGames(true, table, games);
    }

    static Set<Integer> applyGames(boolean add, List<TableRecord> table, List<Game> games) {
        TreeSet<Integer> ret = new TreeSet<Integer>();

        for (Game g : games) {
            if (g.status != GAME_SCORED)
                continue;
            if (g.team1 == null || g.team2 == null)
                continue;
            TableRecord t1 = findTeamRecord(table, g.team1.id);
            TableRecord t2 = findTeamRecord(table, g.team2.id);
            if (t1 == null || t2 == null)
                continue;
            if (g.stage < 0)
                continue;
            int score1 = add ? g.score1 : -g.score1;
            int score2 = add ? g.score2 : -g.score2;
            int gn = add ? 1 : -1;
            t1.gf += score1;
            t1.ga += score2;

            t2.gf += score2;
            t2.ga += score1;

            t1.gdiff = t1.gf - t1.ga;
            t2.gdiff = t2.gf - t2.ga;
            t1.games += gn;
            t2.games += gn;

            ret.add(g.team1.id);
            ret.add(g.team2.id);

            if (g.score1 != g.score2) {
                boolean fst_more = g.score1 > g.score2;
                TableRecord tw = fst_more ? t1 : t2;
                TableRecord tl = fst_more ? t2 : t1;
                if (g.overtime) {
                    tw.wot += gn;
                    tl.lot += gn;
                } else {
                    tw.wins += gn;
                    tl.losses += gn;
                }
            } else {
                t1.ties += gn;
                t2.ties += gn;
            }
        }
        return ret;
    }

    public static Set<Integer> cancelGames(List<TableRecord> table, List<Game> games) { 
        return applyGames(false, table, games);
    }

    public static void refreshTable(List<TableRecord> table, List<Game> games) {
        for (TableRecord r : table) {
            r.points = r.games = r.wins = r.losses = r.wot = r.lot = r.ties = r.gf = r.ga = r.gdiff = 0;
        }

        scoreGames(table, games);
    }


    public static void countTableRecord(TableRecord r, Counting c) {
        if (!c.resultOnly) {
            r.points = r.gf;
            return;
        }

        r.points = r.wins * c.win + r.losses * c.loss;
        if (c.tieAllowed)
            r.points += r.ties * c.tie;
        if (c.otAllowed) 
            r.points += r.wot * c.wot + r.lot * c.lot;
    }

    public static void countPoints(List<TableRecord> table, Counting counting, Set<Integer> tms) {
        if (counting == null)
            return;
        for (TableRecord r : table) {
            if (tms != null && tms.contains(r.team.id) == false)
                continue;
            countTableRecord(r, counting);
        }
    }

    public static boolean gameChanged(Game g1, Game g2) {
        return g1.score1 != g2.score1 || g1.score2 != g2.score2 || g1.scheduled != g2.scheduled ||
            g1.location != g2.location || g1.details != g2.details;
            
    }

    public static void breakGames(Map<Game, Game> games, List<Game> info, List<Game> resuts, List<Game> cancel, List<Game> delete) {
        for (Map.Entry<Game, Game> kv : games.entrySet()) {
            Game ng = kv.getKey();
            Game old = kv.getValue();

            if (ng.removed) {
                delete.add(ng);
                ng.status = LeaguetorConstants.GAME_STATUS_DEFAULT;
            }


            if (ng.status == LeaguetorConstants.GAME_STATUS_SCORED && old.status == LeaguetorConstants.GAME_STATUS_SCORED) {
                if (ng.score1 != old.score1 || ng.score2 != old.score2) {
                    cancel.add(old);
                    resuts.add(ng);
                } else if (gameChanged(ng, old))
                    info.add(ng);
            } else if (ng.status == LeaguetorConstants.GAME_STATUS_DEFAULT && old.status == LeaguetorConstants.GAME_STATUS_SCORED) {
                cancel.add(old);
                if (!ng.removed)
                    info.add(ng);
            } else if (ng.status == LeaguetorConstants.GAME_STATUS_SCORED && old.status == LeaguetorConstants.GAME_STATUS_DEFAULT) {
                if (ng.scheduled == 0)
                    ng.scheduled = System.currentTimeMillis();

                resuts.add(ng);
            } else {
                if (!ng.removed && gameChanged(ng, old))
                    info.add(ng);
            }
        }
    }
}

