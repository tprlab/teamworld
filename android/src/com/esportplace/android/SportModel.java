package com.esportplace.android;

import com.leaguetor.entity.*;
import java.util.List;

public interface SportModel {
    public List<LeagueInfo> getUserLeagues(String user);
    public List<Sport> getSportList();

    public LeagueInfo getLeagueInfo(String uname);
    public LeagueInfo getLeagueInfo(int id);

    public List<Team> getTeams(String league);

    public List<DivInfo> getDivs(String league);

    public List<TourInfo> getTours(String league);

    public List<Game> getTourGames(String tr);
    public List<Division> getTourDivs(String tr);

    public TourInfo getTourInfo(String uname);
    public TourInfo getTourInfo(int id);
    public Tour getTour(String uname);

    public List<TableRecord> getTable(String tr, int div);
    public Counting getCounting(String tr);

    public String createLeague(LeagueInfo lg);
    public String saveLeague(LeagueInfo lg);
    public boolean deleteLeague(LeagueInfo lg);
    public Sport findSport(String name);

    public String saveTeam(Team tm);
    public String createTeam(Team tm);
    public boolean deleteTeam(Team tm);
    public Team getTeam(String name);

    public String saveDiv(DivInfo div);
    public String createDiv(DivInfo div);
    public boolean deleteDiv(DivInfo div);
    public DivInfo getDiv(String name);

    public Game getGame(int id);
    public boolean saveGame(Game g);
    public boolean saveGames(String tour, List<Game> g);

    public DashInfo getActivities(String uname);

    public boolean resetDates(String tour, int div, List<Game> games);

    public boolean removeGame(int id);

}
