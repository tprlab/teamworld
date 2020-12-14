package com.leaguetor;

import com.leaguetor.entity.*;


import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class PlayOff {

    public static List<Game> createList() {
        return new ArrayList<Game>();
    }

    public static Game createGame(Team t1, Team t2, int stage) {
        Game g = new Game();
        g.team1 = t1;       
        g.team2 = t2;
        g.stage = stage;
        return g;
    }


    public static List<Game> getGamesForStage(List<Game> hist, int stage) {
        List<Game> ret = createList();
        if (hist != null) {
            for (Game g : hist) {
                if (g.stage == stage)
                    ret.add(g);
            }
        }
        return ret;        
    }
    

    public static List<Game> schedule(List<TableRecord> table, List<Game> hist, int stage) {
        int prev_stage = stage * 2;
        List<Game> prev = getGamesForStage(hist, prev_stage);
        boolean init_pl = prev.size() == 0;
        return init_pl ? initPlayOff(table, stage) : schedulePlayOff(table, prev, stage);
    }

    public static List<Game> initPlayOff(List<TableRecord> table, int stage) {
        int have_teams = table.size();
        int max_teams = -stage * 2;
        int GN = -stage;
        if (GN < 1)
            return null;

        List ret = createList();
        if (have_teams >= max_teams) {
            for (int i = 0; i < GN; i++) {
                TableRecord tr1 = table.get(i);
                TableRecord tr2 = table.get(max_teams - i - 1);
                Game g = createGame(tr1.team, tr2.team, stage);
                ret.add(g);
            }
        } else {
            int diff = max_teams - have_teams;
            for (int i = diff; i < GN; i++) {
                TableRecord tr1 = table.get(i);
                TableRecord tr2 = table.get(max_teams - i - 1);
                Game g = createGame(tr1.team, tr2.team, stage);
                ret.add(g);
            }
        }

        return ret;
    }

    public static List<Game> schedulePlayOff(List<TableRecord> table, List<Game> hist, int stage) {

        List<TableRecord> str = getStageWinners(table, hist);
        int n = str.size() / 2;
        int gn = -stage;
        int df = gn * 2 - str.size();
        Tracer.log("Fill poff from begin " + df + " for stage " + stage + " with " + str.size());
        for (int i = 0; i < df; i++)
            str.add(0, table.get(df - i - 1));

        List ret = createList();
        for (int i = 0; i < gn; i++) {
            TableRecord tr1 = str.get(i);
            TableRecord tr2 = str.get(2 * gn - i - 1);
            Game g = createGame(tr1.team, tr2.team, stage);
            ret.add(g);
        }
        return ret;
    }


    public static List<TableRecord> getStageWinners(List<TableRecord> table, List<Game> games) {
        HashMap<Integer, Integer> wins = new HashMap<Integer, Integer>();
        int maxwin = 0;
        for (Game g : games) {
            int w = g.score1 > g.score2 ? g.team1.id : g.team2.id;
            if (!wins.containsKey(w))
                wins.put(w, 1);
            else
                wins.put(w, wins.get(w) + 1);
            int n = wins.get(w);
            if (n > maxwin)
                maxwin = n;
        }
        List<TableRecord> ret = new ArrayList<TableRecord>();
        for (TableRecord r : table) {
            Tracer.log("TableRecord " + r);

            if (!wins.containsKey(r.team.id))
                continue;
            int w = wins.get(r.team.id);
            if (w != maxwin)
                continue;
            ret.add(r);
        }
        return ret;
    }

    public static boolean checkTeamDup(Map<Integer,Integer> m, Team t) {
        Integer v = m.get(t.id);
        if (v != null)
            return false;
        m.put(t.id, 1);
        return true;
    }

    public static Team checkDups(List<Game> games) {
        Map<Integer, Integer> m = new HashMap<Integer, Integer>();
        for (Game g : games) {
            if (!checkTeamDup(m, g.team1))
                return g.team1;
            if (!checkTeamDup(m, g.team2))
                return g.team2;
        }
        return null;
    }
}
