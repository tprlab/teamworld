package com.leaguetor.web.ctrl;


import javax.servlet.http.HttpServletRequest;

import com.leaguetor.web.UrlPath;
import com.leaguetor.DBAccessor;
import com.leaguetor.web.ErrorFactory;
import com.leaguetor.entity.*;
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
import com.leaguetor.entity.*;

@Controller
public class TeamsCtrl extends BaseCtrl{
    
    @RequestMapping(value = UrlPath.MANAGE_LEAGUE_TEAMS, method = RequestMethod.GET)
    public String manageTeams(@RequestParam(value = "name", required = true) String lg_name, ModelMap model) {
        LeagueInfo lg = DBAccessor.getLeagueInfo(lg_name);
        List<Team> teams = DBAccessor.getTeams(lg.id);
        logger.debug("Getting manage team page for " + lg + "(" + lg_name + "), have teams " + (teams == null ?  0: teams.size()));
        Team t = new Team();
        model.addAttribute("league", lg);
        model.addAttribute("team", t);
        model.addAttribute("teams", teams);

        return "manage_league_teams";
    }

    @RequestMapping(value = UrlPath.DO_CREATE_TEAM, method = RequestMethod.POST)
    public String createTeam(@ModelAttribute("new_team") Team team,
        BindingResult errors, ModelMap model, RedirectAttributes redirectAttributes, Principal principal){
        String user = principal.getName();
        logger.debug("Creating team " + team + " by " + user);
        redirectAttributes.addAttribute("name", team.leagueName);

        if (team.name == null || team.name.length() < 3) {
            redirectAttributes.addFlashAttribute("error", "empty.name");
            return "redirect:" + UrlPath.MANAGE_LEAGUE;
        }


        LeagueInfo lg = DBAccessor.getLeagueInfo(team.leagueName);
        team.league = lg;
        SportDB.Iface db = DBAccessor.getDB();
        try { 
            int tm_id = db.addTeam(team);
        } catch(DbError t) {
            logger.error("Cannot add team " + team + ": " + t.getMessage());
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_CREATE_TEAM, t.code, t.message);
            redirectAttributes.addFlashAttribute("error", err);

            return "redirect:" + UrlPath.MANAGE_LEAGUE;
        } catch(Throwable t) {
            logger.error("Cannot add team " + team, t);
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_CREATE_TEAM, LeaguetorConstants.ERROR_UNEXPECTED, null);
            redirectAttributes.addFlashAttribute("error", err);
            return "redirect:" + UrlPath.MANAGE_LEAGUE;
        }

        return "redirect:" + UrlPath.MANAGE_LEAGUE;
    }

    @RequestMapping(value = UrlPath.CREATE_TEAM, method = RequestMethod.GET)
    public String preCreateTeam(@RequestParam(value = "league", required = true) String league, 
            ModelMap model, Principal principal) {
        String user = principal == null ? null : principal.getName();
        League lg = DBAccessor.getLeague(league, false);
        if (lg == null)
            return UrlPath.ERROR;

        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_TEAMS);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }

        Team tm = new Team();
        model.addAttribute("team", tm);
        model.addAttribute("league", lg);

        return "create_team";
    }



    @RequestMapping(value = UrlPath.DO_RENAME_TEAM, method = RequestMethod.POST)
    public String saveTeam(@RequestParam(value = "name", required = true) String team,
        @RequestParam(value = "newname", required = true) String name,
        ModelMap model, RedirectAttributes redirectAttributes, Principal principal){
        String user = principal.getName();
        logger.debug("Saving team " + team + " by " + user);
        Team tm = DBAccessor.getTeam(team);
        if (tm == null)
        {
            logger.warn("No team found for rename " + team);
            return "redirect:" + UrlPath.ERROR;
        }
        tm.name = name;
        
        LeagueInfo lg = tm.league;
        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_TEAMS);
        if (acl == 0) {
            return "redirect:" + UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            logger.warn("Cannot get acl for rename team " + team);
            return "redirect:" + UrlPath.ERROR;
        }

        SportDB.Iface db = DBAccessor.getDB();
        String err = null;
        String msg = null;

        try { 
            if (db.saveTeam(tm)) {
                msg = "team.saved";
            } else {
                err ="team.already.exist";
            }
        } catch(DbError t) {
            logger.error("Cannot rename team " + team + ": " + t.getMessage());
            err = ErrorFactory.getError(LeaguetorConstants.ACTION_SAVE_TEAM, t.code, t.message);

        } catch(Throwable t) {
            logger.error("Cannot rename team1 " + team, t);
            err = ErrorFactory.getError(LeaguetorConstants.ACTION_SAVE_TEAM, LeaguetorConstants.ERROR_UNEXPECTED, null);
        }

        redirectAttributes.addAttribute("name", tm.uname);
        redirectAttributes.addFlashAttribute("error", err);
        redirectAttributes.addFlashAttribute("umsg", msg);

        return "redirect:" + UrlPath.MANAGE_TEAM;
    }

    @RequestMapping(value = UrlPath.DO_REMOVE_TEAM, method = RequestMethod.POST)
    public String removeTeam(@RequestParam(value = "name", required = true) String team,
        ModelMap model, RedirectAttributes redirectAttributes, Principal principal){
        String user = principal.getName();
        logger.debug("Removing team " + team + " by " + user);
        Team tm = DBAccessor.getTeam(team);
        if (tm == null)
        {
            logger.warn("Cannot get team for delete " + team);
            return "redirect:" + UrlPath.ERROR;
        }

        redirectAttributes.addAttribute("name", tm.league.uname);
        LeagueInfo lg = tm.league;
        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_TEAMS);
        if (acl == 0) {
            return "redirect:" + UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            logger.warn("Cannot get acl for delete team " + team);
            return "redirect:" + UrlPath.ERROR;
        }


        SportDB.Iface db = DBAccessor.getDB();
        String err = null;

        try { 
            boolean rc = db.deleteTeam(team);
            if (rc == false) {
                logger.warn("Team delete failed " + team);
                err = "team.not.deleted";
            } else {
                redirectAttributes.addFlashAttribute("umsg", "team.deleted");
            }
        } catch(DbError t) {
            logger.error("Cannot remove team " + team + ": " + t.getMessage());
            err = ErrorFactory.getError(LeaguetorConstants.ACTION_DELETE_TEAM, t.code, t.message);

        } catch(Throwable t) {
            logger.error("Cannot remove team1 " + team, t);
            err = ErrorFactory.getError(LeaguetorConstants.ACTION_DELETE_TEAM, LeaguetorConstants.ERROR_UNEXPECTED, null);
        }

        redirectAttributes.addFlashAttribute("error", err);
        if (err != null)        {
            redirectAttributes.addAttribute("name", tm.uname);
            return "redirect:" + UrlPath.MANAGE_TEAM;
        }

        return "redirect:" + UrlPath.MANAGE_LEAGUE;
    }

    @RequestMapping(value = UrlPath.MANAGE_TEAM, method = RequestMethod.GET)
    public String manageTeams(@RequestParam(value = "name", required = true) String name, ModelMap model, 
            Principal principal) {
        String user = principal.getName();
        Team tm = DBAccessor.getTeam(name);
        if (tm == null)
            return UrlPath.ERROR;

        logger.debug("Managing team " + tm);
        model.addAttribute("team", tm);
        model.addAttribute("league", tm.league);

        return "manage_team";
    }


}
