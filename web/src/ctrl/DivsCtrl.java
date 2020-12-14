package com.leaguetor.web.ctrl;


import javax.servlet.http.HttpServletRequest;

import com.leaguetor.web.UrlPath;
import com.leaguetor.DBAccessor;
import com.leaguetor.web.ErrorFactory;
import com.leaguetor.SportACL;
import com.leaguetor.acl.ACLConstants;
import com.leaguetor.entity.*;


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
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;
import com.leaguetor.entity.*;

@Controller
public class DivsCtrl extends BaseCtrl{
    
    @RequestMapping(value = UrlPath.MANAGE_LEAGUE_DIVS, method = RequestMethod.GET)
    public String manageTeams(@RequestParam(value = "name", required = true) String lg_name, ModelMap model, Principal principal) {
        String user = principal == null ? null : principal.getName();
        League lg = DBAccessor.getLeague(lg_name, false);
        if (lg == null)
            return UrlPath.ERROR;


        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_TEAMS);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }

        model.addAttribute("league", lg);
        model.addAttribute("new_team", new Team());
        model.addAttribute("dv", new DivInfo());

        logger.debug("League " + lg_name + " has divs " + lg.getDivsSize());
        return "manage_league_divs";
    }

    @RequestMapping(value = UrlPath.DO_CREATE_DIV, method = RequestMethod.POST)
    public String createDiv(@ModelAttribute("new_div") DivInfo div,
        BindingResult errors, ModelMap model, RedirectAttributes redirectAttributes, Principal principal){
        String user = principal.getName();
        logger.debug("Creating div " + div + " by " + user);
        redirectAttributes.addAttribute("name", div.leagueName);

        if (div.name == null || div.name.length() < 3) {
            redirectAttributes.addFlashAttribute("error", "empty.name");
            return "redirect:" + UrlPath.MANAGE_LEAGUE;
        }


        LeagueInfo lg = DBAccessor.getLeagueInfo(div.leagueName);
        div.league = lg;
        SportDB.Iface db = DBAccessor.getDB();

        try { 
            int div_id = db.addDiv(div);
        } catch(DbError t) {
            logger.error("Cannot add div " + div+ ": " + t.getMessage());
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_CREATE_DIV, t.code, t.message);
            redirectAttributes.addFlashAttribute("error", err);

            return "redirect:" + UrlPath.MANAGE_LEAGUE;
        } catch(Throwable t) {
            logger.error("Cannot add div " + div, t);
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_CREATE_DIV, LeaguetorConstants.ERROR_UNEXPECTED, null);
            redirectAttributes.addFlashAttribute("error", err);
            return "redirect:" + UrlPath.MANAGE_LEAGUE;
        }

        return "redirect:" + UrlPath.MANAGE_LEAGUE;
    }

    public boolean getDiv(String uname, String tour, ModelMap model, int gm ){
        Division div = DBAccessor.getDiv(tour, uname, gm);
        TourInfo tr = DBAccessor.getTourInfo(tour);
        if (div == null || tr == null)
            return false;
        DBAccessor.prepareGames(div.games);
        Counting cnt = DBAccessor.getCounting(tr.id);
        model.addAttribute("dv", div);
        model.addAttribute("counting", cnt);
        model.addAttribute("tr", tr);
        model.addAttribute("league", tr.league);
        return true;
    }


    @RequestMapping(value = UrlPath.MANAGE_DIV, method = RequestMethod.GET)
    @PreAuthorize("hasPermission(tour, 'manage')")
    public String manageDiv(@RequestParam(value = "name", required = true) String uname, 
        @RequestParam(value = "tour", required = false) String tour, 
            ModelMap model, Principal principal){
        String user = principal == null ? null : principal.getName();

        logger.debug("Managing div " + uname + " in " + tour + " by " + user);
        if (!getDiv(uname, tour, model, 1))
            return UrlPath.ERROR;
        TourInfo tr = (TourInfo)model.get("tr");

        int acl = SportACL.isGranted(user, null, "league", tr.league.id, ACLConstants.MANAGE_TEAMS);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }

        model.addAttribute("ng", new Game());
        model.addAttribute("minutes", DBAccessor.MINUTES);

        return tr.status == LeaguetorConstants.TOUR_STATUS_ACTIVE ? "manage_div" : "view_div";
    }

    @RequestMapping(value = UrlPath.VIEW_DIV, method = RequestMethod.GET)
    public String viewDiv(@RequestParam(value = "name", required = true) String uname, 
        @RequestParam(value = "tour", required = false) String tour, 
            ModelMap model, Principal principal){

        if (!getDiv(uname, tour, model, -1))
            return UrlPath.ERROR;
        return "view_div";
    }

    @RequestMapping(value = UrlPath.DO_REMOVE_DIV, method = RequestMethod.POST)
    public String removeDiv(@RequestParam(value = "div", required = true) String div, 
        @RequestParam(value = "league", required = true) String league, 
        ModelMap model, RedirectAttributes redirectAttributes, Principal principal){
        String user = principal.getName();
        logger.debug("Removing div " + div + " by " + user);
        redirectAttributes.addAttribute("name", league);
        LeagueInfo lg = DBAccessor.getLeagueInfo(league);
        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_TEAMS);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }


        SportDB.Iface db = DBAccessor.getDB();
        String err = null;
        try { 
            boolean ok = db.deleteDiv(div);
            if (!ok) {
                logger.error("Div deletion rejected for " + div);
                err = ErrorFactory.getError(LeaguetorConstants.ACTION_DELETE_DIV, LeaguetorConstants.ERROR_UNEXPECTED, null);
            }
        } catch(DbError t) {
            logger.error("Cannot delete div " + div+ ": " + t.getMessage());
            err = ErrorFactory.getError(LeaguetorConstants.ACTION_DELETE_DIV, t.code, t.message);

        } catch(Throwable t) {
            logger.error("Cannot delete div " + div, t);
            err = ErrorFactory.getError(LeaguetorConstants.ACTION_DELETE_DIV, LeaguetorConstants.ERROR_UNEXPECTED, null);
        }
        redirectAttributes.addFlashAttribute("error", err);

        return "redirect:" + UrlPath.MANAGE_LEAGUE;
    }


    @RequestMapping(value = UrlPath.CREATE_DIV, method = RequestMethod.GET)
    public String preCreateDiv(@RequestParam(value = "league", required = true) String league, 
            ModelMap model, Principal principal) {
        String user = principal == null ? null : principal.getName();
        LeagueInfo lg = DBAccessor.getLeagueInfo(league);
        if (lg == null)
            return UrlPath.ERROR;

        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_TEAMS);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }

        DivInfo div = new DivInfo();
        model.addAttribute("dv", div);
        model.addAttribute("league", lg);

        return "create_div";
    }

    @RequestMapping(value = UrlPath.DO_RENAME_DIV, method = RequestMethod.POST)
    public String saveDiv(@RequestParam(value = "name", required = true) String div, 
        @RequestParam(value = "newname", required = true) String name, 
        ModelMap model, RedirectAttributes redirectAttributes, Principal principal){
        String user = principal.getName();
        logger.debug("Saving div " + div + " by " + user);
        DivInfo dv = DBAccessor.getDivInfoEx(div);
        if (dv == null){ 
            return "redirect:" + UrlPath.ERROR;
        }

        LeagueInfo lg = dv.league;
        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_TEAMS);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }


        SportDB.Iface db = DBAccessor.getDB();
        String err = null;
        String msg = null;
        dv.name = name;

        try { 
            if (db.saveDiv(dv)) {
                msg= "div.saved"; 
            } else {
                err ="div.already.exist";
            }
        
        } catch(DbError t) {
            logger.error("Cannot rename div " + div+ ": " + t.getMessage());
            err = ErrorFactory.getError(LeaguetorConstants.ACTION_SAVE_DIV, t.code, t.message);

        } catch(Throwable t) {
            logger.error("Cannot rename div " + div, t);
            err = ErrorFactory.getError(LeaguetorConstants.ACTION_SAVE_DIV, LeaguetorConstants.ERROR_UNEXPECTED, null);
        }

        redirectAttributes.addFlashAttribute("error", err);
        redirectAttributes.addFlashAttribute("umsg", msg);
        redirectAttributes.addAttribute("name", dv.uname);

        return "redirect:" + UrlPath.MANAGE_LEAGUE_DIV;
    }

    @RequestMapping(value = UrlPath.MANAGE_LEAGUE_DIV, method = RequestMethod.GET)
    public String manageLgDiv(@RequestParam(value = "name", required = true) String uname, 
        @RequestParam(value = "tour", required = false) String tour, 
            ModelMap model, Principal principal){
        String user = principal == null ? null : principal.getName();

        logger.debug("Managing lg div " + uname);

        DivInfo dv = DBAccessor.getDivInfoEx(uname);
        if (dv == null) {
            return UrlPath.ERROR;
        }

        int acl = SportACL.isGranted(user, null, "league", dv.league.id, ACLConstants.MANAGE_TEAMS);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }

        model.addAttribute("dv", dv);
        model.addAttribute("league", dv.league);


        return "manage_lg_div";
    }

    @RequestMapping(value = UrlPath.CHANGE_RANK, method = RequestMethod.POST)
    public String changeRank(@RequestParam(value = "name", required = true) String div, 
        @RequestParam(value = "league", required = true) String lgname, 
        ModelMap model, RedirectAttributes redirectAttributes, Principal principal){

        String user = principal == null ? null : principal.getName();

        logger.debug("Change div rank " + div);

        DivInfo dv = DBAccessor.getDivInfo(div);
        if (dv == null) {
            return "redirect:" + UrlPath.ERROR;
        }

        LeagueInfo lg  = DBAccessor.getLeagueInfo(lgname);
        if (lg == null) {
            return "redirect:" + UrlPath.ERROR;
        }


        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_TEAMS);
        if (acl == 0) {
            return "redirect:" + UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return "redirect:" + UrlPath.ERROR;
        }

        SportDB.Iface db = DBAccessor.getDB();
        String err = null;

        try { 
            db.incRank(div);
        } catch(DbError t) {
            logger.error("Cannot increase div rank " + div+ ": " + t.getMessage());
            err = ErrorFactory.getError(LeaguetorConstants.ACTION_SAVE_DIV, t.code, t.message);
        } catch(Throwable t) {
            logger.error("Cannot increase div rank " + div, t);
            err = ErrorFactory.getError(LeaguetorConstants.ACTION_SAVE_DIV, LeaguetorConstants.ERROR_UNEXPECTED, null);
        }

        redirectAttributes.addFlashAttribute("error", err);
        redirectAttributes.addAttribute("name", lg.uname);

        return "redirect:" + UrlPath.MANAGE_LEAGUE;

    }

}
