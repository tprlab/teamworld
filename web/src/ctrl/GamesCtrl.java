package com.leaguetor.web.ctrl;


import javax.servlet.http.HttpServletRequest;

import com.leaguetor.web.UrlPath;
import com.leaguetor.DBAccessor;
import com.leaguetor.web.ErrorFactory;
import com.leaguetor.web.WebSportUtil;
import com.leaguetor.entity.*;
import com.leaguetor.StringUtil;
import com.leaguetor.SportACL;
import com.leaguetor.acl.ACLConstants;


import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;
import com.leaguetor.entity.*;

@Controller
public class GamesCtrl extends BaseCtrl{

    @RequestMapping(value = UrlPath.MANAGE_GAMES, method = RequestMethod.GET)    
    public String manageTour(@RequestParam(value = "name", required = true) String uname, 
            ModelMap model, Principal principal){
        String user = principal == null ? null : principal.getName();

        logger.debug("Managing games in tour " + uname + " by " + user);
        Tour tr = DBAccessor.getTour(uname, 1);
        if (tr == null) {
            logger.warn("No tour " + uname);
            return UrlPath.ERROR;
        }
        tr.league.sport.fieldName = WebSportUtil.getFieldName(tr.league.sport.fieldType);

        int acl = SportACL.isGranted(user, null, "league", tr.league.id, ACLConstants.MANAGE_GAMES);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }

/*
        LeagueInfo lg = DBAccessor.getLeagueInfo(tr.leagueName);
        tr.league = lg;
*/
        boolean done = tr.status == LeaguetorConstants.TOUR_STATUS_FINISHED;
        

        model.addAttribute("tr", tr);
        if (tr.divList != null) {
            for (Division d : tr.divList) {
                model.addAttribute("dv-" + d.id, d);
                if (d.games == null)
                    continue;
                DBAccessor.prepareGames(d.games);
            }
        }
        if (tr.main != null) {
            model.addAttribute("dv-0", tr.main);
            if (tr.main.games != null)
                DBAccessor.prepareGames(tr.main.games);
        }


        List<Venue> vens = DBAccessor.getVenues(tr.league.uname);
        if (vens != null) {
            HashMap<Integer, Venue> vmap = new HashMap<>();
            ArrayList<Venue> vlist = new ArrayList<>();
            for (Venue v : vens) {
                vmap.put(v.id, v);
                if (v.courts > 1) {
                    for (int i = 0; i < v.courts; i++) {
                        Venue vv = new Venue(v);
                        vv.courts = i + 1;
                        vlist.add(vv);
                    }
                } else {
                    v.courts = 1;
                    vlist.add(v);
                }

            }

            model.addAttribute("venMap", vmap);
            model.addAttribute("venlist", vlist);
        }

        model.addAttribute("ng", new Game());
        model.addAttribute("minutes", DBAccessor.MINUTES);
        return done ? "view_tour" : "manage_tour_games";
    }

    @RequestMapping(value = UrlPath.DO_SAVE_GAME, method = RequestMethod.POST)
    public String saveGame(Game g, @RequestParam(value = "tourName") String tour,
        @RequestParam(value = "divName", required = false) String div,
        @RequestParam(value = "return", required = false) String ret,
        @RequestParam(value = "hour", required = false) String hour,
        @RequestParam(value = "minute", required = false) String minute,
        @RequestParam(value = "ampm", required = false) String ampm,
        @RequestParam(value = "gmven", required = false) String strVenue,
        ModelMap model, RedirectAttributes redirectAttributes, Principal principal){

        logger.debug("saving game " + g + ", ret " + ret + ", div " + div + " venue " + strVenue);
        String user = principal == null ? null : principal.getName();

        TourInfo tr = DBAccessor.getTourInfo(tour);
        if (tr == null) {
            return UrlPath.ERROR;
        }

        Counting cnt = DBAccessor.getCounting(tr.id);
        if (cnt == null) {
            logger.error("No counting for tour " + tr);
            return "redirect:" + UrlPath.ERROR;
        }


        int acl = SportACL.isGranted(user, null, "league", tr.league.id, ACLConstants.MANAGE_GAMES);
        if (acl == 0) {
            return "redirect:" + UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return "redirect:" + UrlPath.ERROR;
        }

        if (strVenue != null) {
            int[] vv = StringUtil.parseScore(strVenue);
            if (vv != null && vv.length > 1) {
                g.venueId = vv[0];
                g.court = vv[1];
            }
        }

        long tm = StringUtil.buildTime(hour, minute, ampm);
        logger.debug("Built time " + tm + " from " + hour + ":" + minute + " " + ampm);
        String err = handleGame(g, cnt, tm);
        List<Game> lg = new ArrayList<>();
        if (err != null) {
            redirectAttributes.addFlashAttribute("error", err);
            lg = null;
        } else {
            lg.add(g);
        }

        return submitGames(tour, div, lg, ret, model, redirectAttributes);        
    }

    String submitGames(String tour, String div, List<Game> games, String ret, ModelMap model, RedirectAttributes redirectAttributes) {

        boolean return_div = ret != null && ret.equalsIgnoreCase("div") && div != null;
        String ret_addr = UrlPath.MANAGE_GAMES;
        if (return_div)
        {
            redirectAttributes.addAttribute("name", div);
            redirectAttributes.addAttribute("tour", tour);
            ret_addr = UrlPath.MANAGE_DIV;
        } else {
            redirectAttributes.addAttribute("name", tour);
        }
        if (games != null) {
            SportDB.Iface db = DBAccessor.getDB();
            try { 
                db.saveGames(games);
            } catch(DbError t) {
                logger.error("Cannot save games : " + t.getMessage());
                String err = ErrorFactory.getError(LeaguetorConstants.ACTION_SAVE_GAMES, t.code, t.message);
                redirectAttributes.addFlashAttribute("error", err);
            } catch(Throwable t) {
                logger.error("Cannot save games", t);
                String err = ErrorFactory.getError(LeaguetorConstants.ACTION_SAVE_GAMES, LeaguetorConstants.ERROR_UNEXPECTED, null);
                redirectAttributes.addFlashAttribute("error", err);
            }
        }

        return "redirect:" + ret_addr;
    }


    String handleGame(Game g, Counting cnt, long tm) {
        Date d = StringUtil.parseDate(g.strDate, DBAccessor.DATE_MASK);
        //Date t = StringUtil.parseTime(g.strTime);
        boolean haveScore1 = StringUtil.emptyOrNull(g.strScore1) == false;
        boolean haveScore2 = StringUtil.emptyOrNull(g.strScore2) == false;
        /*
        if ((g.strScore != null && g.strScore.length() > 0) && score == null) {
            redirectAttributes.addFlashAttribute("error", "score.wrong");
        }
        */
        if (d != null) {
            //long tt = t == null ? 0: t.getTime();
            //long tm = d.getTime() + tt;
            g.scheduled = d.getTime() + tm;
            logger.debug("Game " + g.id + " have date " + new Date(g.scheduled) + "(" + g.strDate + " " + g.strTime + ")");
            logger.debug("Date " + d + ", time " + tm);
        }
        if (haveScore1 || haveScore2) {
            g.score1 = haveScore1 ? StringUtil.parseInt(g.strScore1) : 0;
            g.score2 = haveScore2 ? StringUtil.parseInt(g.strScore2) : 0;
            if (g.score1 < 0 || g.score2 < 0) {
                return "score.wrong";
            } 

            if (g.score1 == g.score2 && cnt.tieAllowed == false)
                return "no.tie";
            g.status = LeaguetorConstants.GAME_STATUS_SCORED;
            logger.debug("Score set for game " + g.id);
        }
        return null;
    }
/*
    @RequestMapping(value = UrlPath.DO_SAVE_GAMES, method = RequestMethod.POST)
    public String saveGames(Division div, @RequestParam(value = "tourName") String tour,
        @RequestParam(value = "return", required = false) String ret,
        BindingResult errors, HttpServletRequest request, 
        ModelMap model, RedirectAttributes redirectAttributes, Principal principal){
        logger.debug("saving games " + div);
        String user = principal == null ? null : principal.getName();

        TourInfo tr = DBAccessor.getTourInfo(tour);
        if (tr == null) {
            return UrlPath.ERROR;
        }

        Counting cnt = DBAccessor.getCounting(tr.id);
        if (cnt == null) {
            logger.error("No counting for tour " + tr);
            return "redirect:" + UrlPath.ERROR;
        }


        int acl = SportACL.isGranted(user, null, "league", tr.league.id, ACLConstants.MANAGE);
        if (acl == 0) {
            return "redirect:" + UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return "redirect:" + UrlPath.ERROR;
        }


        for (Game g : div.games) {
            String err = handleGame(g, cnt);
            if (err != null)
                redirectAttributes.addFlashAttribute("error", err);
        }

        return submitGames(tour, div.uname, div.games, ret, model, redirectAttributes);        
    }
*/
    @RequestMapping(value = UrlPath.LEAGUE_GAMES, method = RequestMethod.GET)    
    public String leagueGames(@RequestParam(value = "name", required = true) String uname, 
            ModelMap model, Principal principal){

        League lg = DBAccessor.getLeague(uname, true);
        if (lg == null) {
            logger.debug("[last_results] Get league returned null");
            return UrlPath.ERROR;
        }

        TourInfo tr = null;
        if (lg.getToursSize() > 0) {
            tr = lg.tours.get(0);            
        } else if (lg.getHistorySize() > 0) {
            tr = lg.history.get(0);
        }

        model.addAttribute("league", lg);
        if (tr != null) {
            model.addAttribute("tr", tr);
            List<Game> games = DBAccessor.getGames(lg.id, tr.id, 20, -1, false, false);
            logger.debug("League has " + (games == null ? 0 : games.size()) + " games");
            DBAccessor.prepareGames(games, lg.id);
            model.addAttribute("games", games);
        }

        return "league_games";
    }

    @RequestMapping(value = UrlPath.LAST_RESULTS, method = RequestMethod.GET)    
    public String getLastResults(ModelMap model){

        List<Game> games = DBAccessor.getGames(0, 0, 10, 1, false, true);
        DBAccessor.prepareGames(games);
        model.addAttribute("games", games);
        return "last_results";
    }

    @RequestMapping(value = UrlPath.COMING_GAMES, method = RequestMethod.GET)    
    public String leagueGames(ModelMap model){

        List<Game> games = DBAccessor.getGames(0, 0, 10, 0, false, true);
        DBAccessor.prepareGames(games);
        model.addAttribute("games", games);
        return "coming_games";
    }

    @RequestMapping(value = UrlPath.DO_ADD_GAME, method = RequestMethod.POST)
    public String addGame(@RequestParam(value = "tour", required = true) String tour,
        @RequestParam(value = "div", required = false) String div,
        @RequestParam(value = "team1", required = true) String t1,
        @RequestParam(value = "team2", required = true) String t2,
        ModelMap model, RedirectAttributes redirectAttributes, Principal principal){

        String user = principal == null ? null : principal.getName();

        TourInfo tr = DBAccessor.getTourInfo(tour);
        if (tr == null) {
            logger.error("Tour " + tour + " not found to add game");
            return "redirect:" + UrlPath.ERROR;
        }
        int acl = SportACL.isGranted(user, null, "league", tr.league.id, ACLConstants.MANAGE_GAMES);
        if (acl == 0) {
            return "redirect:" + UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return "redirect:" + UrlPath.ERROR;
        }

        Team tm1 = DBAccessor.getTeam(t1);
        if (tm1 == null) {
            logger.error("Team " + t1 + " not found");
            return "redirect:" + UrlPath.ERROR;
        }
        Team tm2 = DBAccessor.getTeam(t2);
        if (tm2 == null) {
            logger.error("Team " + t2 + " not found");
            return "redirect:" + UrlPath.ERROR;
        }

        if (tm1.id == tm2.id) {
            
            redirectAttributes.addAttribute("tour", tour);
            redirectAttributes.addAttribute("div", div);
            redirectAttributes.addFlashAttribute("error", "same.team");
            return "redirect:" + UrlPath.ADD_GAME;
        }

        Game g = new Game();
        g.team1 = tm1;
        g.team2 = tm2;
        g.tourId = tr.id;
        g.leagueId = tr.league.id;

        DivInfo dv = div == null ? null : DBAccessor.getDivInfo(div);
        g.divId = dv == null ? 0 : dv.id;

        
        List<Game> lg = new ArrayList<>();
        lg.add(g);
        String ret = null;

        return submitGames(tour, div, lg, ret, model, redirectAttributes);        
    }


    //@RequestMapping(value = UrlPath.TEAM_GAMES, method = RequestMethod.GET)    
    @RequestMapping(value = UrlPath.TEAM, method = RequestMethod.GET)    
    public String manageTour(@RequestParam(value = "name", required = true) String team, 
        @RequestParam(value = "tour", required = false) String tour, 
            ModelMap model){
        Team tm = DBAccessor.getTeam(team);
        TourInfo tr = DBAccessor.getTourInfo(tour);
        if (tm == null || tr == null)
            return UrlPath.ERROR;

        List<Game> games = DBAccessor.getTeamGames(team, tour);
        DBAccessor.prepareGames(games);

        model.addAttribute("games", games);
        model.addAttribute("team", tm);
        model.addAttribute("tr", tr);
        return "team_games";
    }


}
