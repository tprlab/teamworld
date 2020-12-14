package com.leaguetor.web.ctrl;


import javax.servlet.http.HttpServletRequest;

import com.leaguetor.web.UrlPath;
import com.leaguetor.DBAccessor;
import com.leaguetor.web.ErrorFactory;
import com.leaguetor.SportACL;
import com.leaguetor.entity.*;
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
import com.leaguetor.entity.*;

@Controller
public class ManageLeagueCtrl extends BaseCtrl{
    

    @RequestMapping(value = UrlPath.MANAGE_LEAGUE, method = RequestMethod.GET)
    public String manageLeague(@RequestParam(value = "name", required = true) String uname, 
            ModelMap model, Principal principal){
        String user = principal == null ? null : principal.getName();

        logger.debug("Getting league " + uname + " by " + user);
        League lg = DBAccessor.getLeague(uname, false);
        if (lg == null) {
            logger.debug("Get league returned null");
            return UrlPath.ERROR;
        }

        logger.debug("Lg divs: " + lg.divs);

        Perm uperm = SportACL.getPerm(user, "league", lg.id);

        //int acl_mgr = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_LEAGUE);
        if (uperm == null || uperm.perm == 0) {
            return UrlPath.ACCESS_DENIED;
        } 
        DivInfo div = new DivInfo();
        model.addAttribute("new_div", div);

        int p_lg = uperm.perm & ACLConstants.MANAGE_LEAGUE;
        int p_tm = uperm.perm & ACLConstants.MANAGE_TEAMS;
        int p_gm = uperm.perm & ACLConstants.MANAGE_GAMES;
        int p_pl = uperm.perm & ACLConstants.MANAGE_PLAYERS;


        addPermAttr(uperm.perm, model);
        String ret = "manage_league";
        if (p_lg != 0 || p_tm != 0)
            ret = "manage_league";
        else if (p_gm != 0)
            ret = "manage_league_tours";
        else
            ret = "manage_league_ppl";

        Team tm = new Team();
        model.addAttribute("team", tm);
        model.addAttribute("league", lg);
        return ret;
    }

    String _setTeamDiv(Team team, String user, TourInfo tr) {
        SportDB.Iface db = DBAccessor.getDB();

        try { 
            db.setTeamDiv(team.uname, team.divId, tr == null ? 0 : tr.id);
        } catch(DbError t) {
            logger.error("Cannot set div :"  + t.getMessage());
            return ErrorFactory.getError(LeaguetorConstants.ACTION_SET_DIV, t.code, t.message);
        } catch(Throwable t) {
            logger.error("Cannot set div ", t);
            return ErrorFactory.getError(LeaguetorConstants.ACTION_SET_DIV, LeaguetorConstants.ERROR_UNEXPECTED, null);
        }
        return null;
    }


    @RequestMapping(value = UrlPath.DO_SET_DIV, method = RequestMethod.POST)
    public String setTeamDiv(@ModelAttribute("new_team") Team team,
        @RequestParam(value = "ret", required = false) String ret, 
        BindingResult errors, ModelMap model, RedirectAttributes redirectAttributes, Principal principal){
        String user = principal.getName();
        logger.debug("Set div " + team + " by " + user);
        redirectAttributes.addAttribute("name", team.leagueName);
        LeagueInfo lg = DBAccessor.getLeagueInfo(team.leagueName);
        if (lg == null)
            return UrlPath.ERROR;

        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_TEAMS);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }

        String addr = ret != null && ret.equals("div") ? UrlPath.MANAGE_LEAGUE_DIVS : UrlPath.MANAGE_LEAGUE;

        String err = _setTeamDiv(team, user, null);
        if (err != null) {
            redirectAttributes.addFlashAttribute("error", err);
        }

        return "redirect:" + addr;
    }

    @RequestMapping(value = UrlPath.DO_SET_DIV_TR, method = RequestMethod.POST)
    public String setTeamDivTr(@ModelAttribute("new_team") Team team,
        BindingResult errors, ModelMap model, RedirectAttributes redirectAttributes, Principal principal){
        String user = principal.getName();
        logger.debug("Set trdiv " + team + " by " + user);

        LeagueInfo lg = DBAccessor.getLeagueInfo(team.leagueName);
        if (lg == null)
            return UrlPath.ERROR;

        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_TEAMS);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }

        TourInfo tr = DBAccessor.getTourInfo(team.tourName);
        if (tr == null) {
            logger.error("No tour for setting team " + team);
            return UrlPath.ERROR;
        }


        redirectAttributes.addAttribute("name", team.tourName);

        String err = _setTeamDiv(team, user, tr);
        if (err != null) {
            redirectAttributes.addFlashAttribute("error", err);
        }

        return "redirect:" + UrlPath.MANAGE_TOUR;
    }

}
