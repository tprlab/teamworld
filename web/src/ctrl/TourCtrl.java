package com.leaguetor.web.ctrl;


import javax.servlet.http.HttpServletRequest;

import com.leaguetor.web.UrlPath;
import com.leaguetor.DBAccessor;
import com.leaguetor.web.ErrorFactory;

import com.leaguetor.entity.*;
import com.leaguetor.StringUtil;
import com.leaguetor.ThriftUtil;
import com.leaguetor.SportACL;
import com.leaguetor.acl.ACLConstants;
import com.leaguetor.acl.Perm;


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
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Collection;
import com.leaguetor.entity.*;

@Controller
public class TourCtrl extends BaseCtrl{
    
    @RequestMapping(value = UrlPath.MANAGE_LEAGUE_TOURS, method = RequestMethod.GET)
    public String showTours(@RequestParam(value = "name", required = true) String uname, 
            ModelMap model, Principal principal){
        String user = principal == null ? null : principal.getName();

        logger.debug("Getting league " + uname + " by " + user);
        League lg = DBAccessor.getLeague(uname, true);
        if (lg == null)
            return UrlPath.ERROR;

        Perm perm = SportACL.getPerm(user, "league", lg.id);
        //int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_LEAGUE);
        if (perm == null || perm.perm == 0) {
            return UrlPath.ACCESS_DENIED;
        }

        int p_lg = perm.perm & ACLConstants.MANAGE_LEAGUE;
        int p_gm = perm.perm & ACLConstants.MANAGE_GAMES;

        if (p_lg == 0 && p_gm == 0) {
            return UrlPath.ACCESS_DENIED;
        }


        if (lg.history != null) {
            for (TourInfo ht : lg.history) {
                ht.strStart = StringUtil.formatDate(new Date(ht.started), "MMM YY");                
                ht.strEnd = StringUtil.formatDate(new Date(ht.finished), "MMM YY");
            }
        }

        TourInfo tr = new TourInfo();
        model.addAttribute("tr", tr);
        model.addAttribute("league", lg);
        addPermAttr(perm.perm, model);
        return "manage_league_tours";
    }


    @RequestMapping(value = UrlPath.DO_CREATE_TOUR, method = RequestMethod.POST)
    public String createTour(@ModelAttribute("new_tour") TourInfo tour,
        BindingResult errors, ModelMap model, RedirectAttributes redirectAttributes, Principal principal){
        String user = principal.getName();
        logger.debug("Creating tour " + tour + " by " + user);

        LeagueInfo lg = DBAccessor.getLeagueInfo(tour.leagueName);
        if (lg == null)
            return UrlPath.ERROR;

        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_LEAGUE);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }

        redirectAttributes.addAttribute("name", lg.uname);

        if (tour.name == null || tour.name.length() < 3) {
            redirectAttributes.addFlashAttribute("error", "empty.name");
            return "redirect:" + UrlPath.MANAGE_LEAGUE_TOURS;
        }


        tour.league = lg;
        SportDB.Iface db = DBAccessor.getDB();
        try { 
            String tr_id = db.createTour(tour);
            redirectAttributes.addAttribute("name", tr_id);
        } catch(DbError t) {
            logger.error("Cannot create tour " + tour + ": " + t.getMessage());
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_CREATE_TOUR, t.code, t.message);
            redirectAttributes.addFlashAttribute("error", err);

            return "redirect:" + UrlPath.MANAGE_LEAGUE_TOURS;
        } catch(Throwable t) {
            logger.error("Cannot create tour " + tour, t);
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_CREATE_TOUR, LeaguetorConstants.ERROR_UNEXPECTED, null);
            redirectAttributes.addFlashAttribute("error", err);
            return "redirect:" + UrlPath.MANAGE_LEAGUE_TOURS;
        }

        return "redirect:" + UrlPath.MANAGE_TOUR;
    }

    @RequestMapping(value = UrlPath.MANAGE_TOUR, method = RequestMethod.GET)
    public String manageTour(@RequestParam(value = "name", required = true) String uname, 
            ModelMap model, Principal principal){
        String user = principal == null ? null : principal.getName();

        logger.debug("Managing tour " + uname + " by " + user);

        Tour tr = DBAccessor.getTour(uname, -1);
        if (tr == null) {
            logger.warn("No tour " + uname);
            return UrlPath.ERROR;
        }
        LeagueInfo lg = DBAccessor.getLeagueInfo(tr.league.uname);
        if (lg == null) {
            logger.warn("No league " + tr.league.uname);
            return UrlPath.ERROR;
        }

        model.addAttribute("tr", tr);
        model.addAttribute("league", lg);

        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_LEAGUE | ACLConstants.MANAGE_GAMES);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }
        Perm uperm = SportACL.getPerm(user, "league", lg.id);
        addPermAttr(uperm.perm, model);
        List<Team> lteams = DBAccessor.getTeams(lg.id);

/*
        HashMap<Integer, Team> htm = new HashMap<>();
        if (lteams != null) {
            for (Team t : lteams) 
                htm.put(t.id, t);
        }
*/

        List<DivInfo> ld = DBAccessor.getDivs(lg.id);

        if (tr.divList != null) {
            for (Division d : tr.divList) {
                d.table = DBAccessor.getTable(tr.id, d.id);
                for (TableRecord rc : d.table) {
                    Team tm = ThriftUtil.findList(lteams, rc.team.id);
                    if (tm != null)
                        lteams.remove(tm);
                }
            }
        }
        Collection<Team> ct = lteams;
        logger.debug("Added " + ct.size() + " undivisioned teams");
       
    

        if (tr.status == LeaguetorConstants.TOUR_STATUS_FINISHED) {
            return viewTour(uname, model);
        }
        Team tm = new Team();
        model.addAttribute("new_team", tm);
        model.addAttribute("teams", ct);
        model.addAttribute("divs", ld);

        return "manage_tour";
    }

    @RequestMapping(value = UrlPath.MANAGE_STANDINGS, method = RequestMethod.GET)
    public String showStandings(@RequestParam(value = "name", required = true) String uname, 
            ModelMap model, Principal principal){
        String user = principal == null ? null : principal.getName();

        logger.debug("Managing tour standings " + uname + " by " + user);
        Tour tr = DBAccessor.getTour(uname, 0);
        boolean done = tr.status == LeaguetorConstants.TOUR_STATUS_FINISHED;
        if (tr == null) {
            logger.warn("No tour " + uname);
            return UrlPath.ERROR;
        }

        int acl = SportACL.isGranted(user, null, "league", tr.league.id, 
            ACLConstants.MANAGE_LEAGUE | ACLConstants.MANAGE_TEAMS | ACLConstants.MANAGE_GAMES);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }

        if (tr.divList != null) {
            for (Division d : tr.divList) {
                d.table = DBAccessor.getTable(tr.id, d.id);
            }
        }
        if (tr.main != null) {
            tr.main.table = DBAccessor.getTable(tr.id, 0);
//            logger.debug("Main table " + tr.main.table);
        }

        Perm uperm = SportACL.getPerm(user, "league", tr.league.id);
        addPermAttr(uperm.perm, model);

        Counting cnt = DBAccessor.getCounting(tr.id);
        model.addAttribute("counting", cnt);
        model.addAttribute("tr", tr);
        return done ? "view_tour" : "manage_standings";
    }

    @RequestMapping(value = UrlPath.TOUR_SETTINGS, method = RequestMethod.GET)
    public String tourSettings(@RequestParam(value = "name", required = true) String uname, 
            ModelMap model, Principal principal){
        String user = principal == null ? null : principal.getName();

        logger.debug("tour settings" + uname + " by " + user);
        if (!getTourInfo(uname, model))
        {
            return UrlPath.ERROR;
        }


        League lg = (League)model.get("league");        
        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_LEAGUE);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }
/*
        Perm uperm = SportACL.getPerm(user, "league", lg.id);
        addPermAttr(uperm.perm, model);
*/

        return "tour_settings";
    }


    @RequestMapping(value = UrlPath.FINISH_TOUR, method = RequestMethod.GET)
    public String preFinishTour(@RequestParam(value = "name", required = true) String uname, 
            ModelMap model, Principal principal){
        String user = principal == null ? null : principal.getName();

        logger.debug("Finishing tour " + uname + " by " + user);
        if (!getTourInfo(uname, model))
        {
            return UrlPath.ERROR;
        }

        return "tour_finish";
    }

    @RequestMapping(value = UrlPath.CANCEL_TOUR, method = RequestMethod.GET)
    public String preCancelTour(@RequestParam(value = "name", required = true) String uname, 
            ModelMap model, Principal principal){
        String user = principal == null ? null : principal.getName();

        logger.debug("Canceling tour " + uname + " by " + user);
        if (!getTourInfo(uname, model))
        {
            return UrlPath.ERROR;
        }

        return "tour_cancel";
    }

    @RequestMapping(value = UrlPath.DO_FINISH_TOUR, method = RequestMethod.POST)
    public String finishTour(@RequestParam(value="tour", required=true) String tour,
        ModelMap model, RedirectAttributes redirectAttributes, Principal principal){
        String user = principal.getName();
        logger.debug("Finished tour " + tour + " by " + user);
        redirectAttributes.addAttribute("name", tour);

        SportDB.Iface db = DBAccessor.getDB();
        TourInfo tr = DBAccessor.getTourInfo(tour);

        int acl = SportACL.isGranted(user, null, "league", tr.league.id, ACLConstants.MANAGE_LEAGUE);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }

        try { 
            db.finishTour(tr);
        } catch(DbError t) {
            logger.error("Cannot finish tour " + tour + ": " + t.getMessage());
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_FINISH_TOUR, t.code, t.message);
            redirectAttributes.addFlashAttribute("error", err);

            return "redirect:" + UrlPath.FINISH_TOUR;
        } catch(Throwable t) {
            logger.error("Cannot finish tour " + tour, t);
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_FINISH_TOUR, LeaguetorConstants.ERROR_UNEXPECTED, null);
            redirectAttributes.addFlashAttribute("error", err);
            return "redirect:" + UrlPath.FINISH_TOUR;
        }

        return "redirect:" + UrlPath.VIEW_TOUR;
    }


    @RequestMapping(value = UrlPath.DO_CANCEL_TOUR, method = RequestMethod.POST)
    public String cancelTour(@RequestParam(value="tour", required=true) String tour,
            @RequestParam(value="league", required=true) String league,
        ModelMap model, RedirectAttributes redirectAttributes, Principal principal){
        String user = principal.getName();
        logger.debug("Canceled tour " + tour + " by " + user);
        TourInfo tr = DBAccessor.getTourInfo(tour);

        int acl = SportACL.isGranted(user, null, "league", tr.league.id, ACLConstants.MANAGE_LEAGUE);
        if (acl == 0) {
            return "redirect:" + UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return "redirect:" + UrlPath.ERROR;
        }


        SportDB.Iface db = DBAccessor.getDB();

        try { 
            db.cancelTour(tr);
        } catch(DbError t) {
            logger.error("Cannot cancel tour " + tour + ": " + t.getMessage());
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_CANCEL_TOUR, t.code, t.message);
            redirectAttributes.addAttribute("name", tour);
            redirectAttributes.addFlashAttribute("error", err);
            return "redirect:" + UrlPath.CANCEL_TOUR;
        } catch(Throwable t) {
            logger.error("Cannot cancel tour " + tour, t);
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_CANCEL_TOUR, LeaguetorConstants.ERROR_UNEXPECTED, null);
            redirectAttributes.addAttribute("name", tour);
            redirectAttributes.addFlashAttribute("error", err);
            return "redirect:" + UrlPath.CANCEL_TOUR;
        }
        redirectAttributes.addAttribute("name", league);
        return "redirect:" + UrlPath.MANAGE_LEAGUE_TOURS;
    }


    public boolean getTourInfo(String tour, ModelMap model) {
        TourInfo tr = DBAccessor.getTourInfo(tour);
        if (tr == null) {
            logger.warn("No tour " + tour);
            return false;
        }
        League lg = DBAccessor.getLeague(tr.league.uname, false);
        if (lg == null) {
            logger.warn("No league " + tr.league.uname);
            return false;
        }

        model.addAttribute("tr", tr);
        model.addAttribute("league", lg);
        return true;
    }


    @RequestMapping(value = UrlPath.VIEW_TOUR, method = RequestMethod.GET)
    public String viewTour(@RequestParam(value = "name", required = true) String uname, 
            ModelMap model){
        Tour tr = DBAccessor.getTour(uname, -1);
        if (tr == null) {
            logger.warn("No tour " + uname);
            return UrlPath.ERROR;
        }

        if (tr.divList != null) {
            for (Division d : tr.divList) {
                DBAccessor.prepareGames(d.games);
                d.table = DBAccessor.getTable(tr.id, d.id);
            }
        }
        if (tr.main != null) {
            if (tr.getDivListSize() < 1)
                tr.main.table = DBAccessor.getTable(tr.id, 0);
            DBAccessor.prepareGames(tr.main.games);
        }

        Counting cnt = DBAccessor.getCounting(tr.id);
        model.addAttribute("counting", cnt);
        model.addAttribute("tr", tr);
        return "view_tour";
    }

    @RequestMapping(value = UrlPath.CREATE_TOUR, method = RequestMethod.GET)
    public String preCreateTour(@RequestParam(value = "league", required = true) String league, 
            ModelMap model, Principal principal) {
        String user = principal == null ? null : principal.getName();
        LeagueInfo lg = DBAccessor.getLeagueInfo(league);
        if (lg == null)
            return UrlPath.ERROR;

        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_LEAGUE);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }

        TourInfo tr = new TourInfo();
        model.addAttribute("tr", tr);
        model.addAttribute("league", lg);

        return "create_tour";
    }

    @RequestMapping(value = UrlPath.DO_COPY_TEAMS, method = RequestMethod.POST)
    public String copyStructure(@RequestParam(value = "tour", required = true) String tour, 
        ModelMap model, RedirectAttributes redirectAttributes, Principal principal){

        String user = principal.getName();
        logger.debug("Creating copying teams for " + tour + " by " + user);

        TourInfo tr = DBAccessor.getTourInfo(tour);

        if (tr == null)
            return "redirect:" + UrlPath.ERROR;

        int acl = SportACL.isGranted(user, null, "league", tr.league.id, ACLConstants.MANAGE_LEAGUE);
        if (acl == 0) {
            return "redirect:" + UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return "redirect:" + UrlPath.ERROR;
        }

        redirectAttributes.addAttribute("name", tr.uname);

        SportDB.Iface db = DBAccessor.getDB();
        try { 
            db.copyStructure(tr.id);
        } catch(DbError t) {
            logger.error("Cannot copy structure " + tr + ": " + t.getMessage());
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_COPY_STRUCTURE, t.code, t.message);
            redirectAttributes.addFlashAttribute("error", err);

            return "redirect:" + UrlPath.MANAGE_TOUR;
        } catch(Throwable t) {
            logger.error("Cannot copy structure " + tr, t);
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_COPY_STRUCTURE, LeaguetorConstants.ERROR_UNEXPECTED, null);
            redirectAttributes.addFlashAttribute("error", err);
            return "redirect:" + UrlPath.MANAGE_TOUR;
        }

        return "redirect:" + UrlPath.MANAGE_TOUR;
    }

    @RequestMapping(value = UrlPath.DO_RENAME_TOUR, method = RequestMethod.POST)
    public String saveTeam(@RequestParam(value = "name", required = true) String uname,
        @RequestParam(value = "newname", required = true) String name,
        ModelMap model, RedirectAttributes redirectAttributes, Principal principal){
        String user = principal.getName();
        logger.debug("Renaming tour " + uname + " by " + user);
        TourInfo tr = DBAccessor.getTourInfo(uname);
        if (tr == null)
        {
            logger.warn("No tour found for rename " + uname);
            return "redirect:" + UrlPath.ERROR;
        }
        tr.name = name;
        
        LeagueInfo lg = tr.league;
        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_LEAGUE);
        if (acl == 0) {
            return "redirect:" + UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            logger.warn("Cannot get acl for rename tour " + uname);
            return "redirect:" + UrlPath.ERROR;
        }

        SportDB.Iface db = DBAccessor.getDB();
        String err = null;
        String msg = null;

        try { 
            if (db.saveTour(tr)) {
                msg = "tour.saved";
            } else {
                err ="tour.already.exist";
            }
        } catch(DbError t) {
            logger.error("Cannot rename tour " + uname + ": " + t.getMessage());
            err = ErrorFactory.getError(LeaguetorConstants.ACTION_SAVE_TOUR, t.code, t.message);

        } catch(Throwable t) {
            logger.error("Cannot rename team1 " + uname, t);
            err = ErrorFactory.getError(LeaguetorConstants.ACTION_SAVE_TOUR, LeaguetorConstants.ERROR_UNEXPECTED, null);
        }

        redirectAttributes.addAttribute("name", tr.uname);
        redirectAttributes.addFlashAttribute("error", err);
        redirectAttributes.addFlashAttribute("umsg", msg);

        return "redirect:" + UrlPath.TOUR_SETTINGS;
    }



}
