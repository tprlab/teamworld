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
import java.util.Map;
import java.util.ArrayList;
import com.leaguetor.entity.*;
import com.leaguetor.acl.*;

@Controller
public class AssignCtrl extends BaseCtrl{
    

    @RequestMapping(value = UrlPath.ASSIGN_PPL, method = RequestMethod.GET)
    public String assignLeague(@RequestParam(value = "league", required = true) String lgname, 
        @RequestParam(value = "user", required = true) int uid, 
            ModelMap model, Principal principal){
        String user = principal == null ? null : principal.getName();

        logger.debug("Assigning in league " + lgname + " by " + user);
        LeagueInfo lg = DBAccessor.getLeagueInfo(lgname);
        if (lg == null) {
            logger.debug("Get league returned null: " + lgname);
            return UrlPath.ERROR;
        }

        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_PLAYERS);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }

        User pl = SportACL.getUser(uid);
        if (pl == null) {
            logger.debug("Player with id " + uid + " not found");
            return UrlPath.ERROR;
        }

        Perm UP = SportACL.getPerm(pl.name, "league", lg.id);
        int perms = UP == null ? 0 : UP.perm;
        logger.debug("Got perms " + UP);

        List<Perm> lperms = new ArrayList<>();
        Perm up = new Perm();
        up.perm = ACLConstants.MANAGE_TEAMS;
        up.desc="manage_teams";
        up.state = perms & up.perm;
        lperms.add(up);

        up = new Perm();
        up.perm = ACLConstants.MANAGE_LEAGUE;
        up.desc="manage_league";
        up.state = perms & up.perm;
        lperms.add(up);

        up = new Perm();
        up.perm = ACLConstants.MANAGE_GAMES;
        up.desc="manage_games";
        up.state = perms & up.perm;
        lperms.add(up);

        up = new Perm();
        up.perm = ACLConstants.MANAGE_PLAYERS;
        up.desc="manage_players";
        up.state = perms & up.perm;
        lperms.add(up);



        List<Team> teams = DBAccessor.getTeams(lg.id);

        model.addAttribute("league", lg);
        model.addAttribute("player", pl);
        model.addAttribute("teams", teams);
        model.addAttribute("lperms", lperms);

        return "assign";
    }

    @RequestMapping(value = UrlPath.DO_ASSIGN, method = RequestMethod.POST)
    public String handleCreate(@RequestParam("oid") int oid, @RequestParam("uid") int uid,
        @RequestParam("type") String type,
        HttpServletRequest request, 
        ModelMap model, RedirectAttributes redirectAttributes, Principal principal){

        String user = principal == null ? null : principal.getName();

        LeagueInfo lg = null;
        Team tm = null;
        String target = UrlPath.ASSIGN_PPL;
        if ("league".equals(type)) {
            logger.debug("Do assign in league " + oid + " by " + user);
            lg = DBAccessor.getLeagueInfo(oid);
            if (lg == null) {
                logger.debug("Get league returned null: " + oid);
                return UrlPath.ERROR;
            }
        } else if ("team".equals(type)){
            target = UrlPath.TEAM_ASSIGN_PPL;
            tm = DBAccessor.getTeam(oid);
            if (tm == null) {
                logger.debug("Get team returned null: " + oid);
                return UrlPath.ERROR;
            }
            lg = DBAccessor.getLeagueInfo(tm.leagueId);
            if (lg == null) {
                logger.debug("Get league returned null: " + oid);
                return UrlPath.ERROR;
            }
            redirectAttributes.addAttribute("team", tm.uname);
        } else {
            logger.debug("Unknown assign type " + type);
            return UrlPath.ERROR;
        }


        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_PLAYERS);
        if (acl == 0 && tm != null)
            acl = SportACL.isGranted(user, null, "team", tm.id, ACLConstants.MANAGE_PLAYERS);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }



        User pl = SportACL.getUser(uid);
        if (pl == null) {
            logger.debug("Player with id " + uid + " not found");
            return UrlPath.ERROR;
        }

        Perm perm = new Perm();
        perm.perm = 0;
        perm.oid = oid;
        perm.scope = lg.id;
        perm.type = type;
        perm.user = pl.name;
        redirectAttributes.addAttribute("league", lg.uname);

        String perm_name = type.equals("league") ? "lperm" : "tperm";

        for (int i = 1; i <= ACLConstants.MANAGE_PLAYERS; i *= 2)
        {
            String p = request.getParameter(perm_name + i);
            if (p == null)
                continue;

            logger.debug("Perm " + i + " is on");
            perm.perm |= i;
        }

        SportACL.savePerm(perm);
        redirectAttributes.addAttribute("user", uid);

        return  "redirect:" + target;
    }

    @RequestMapping(value = UrlPath.TEAM_ASSIGN_PPL, method = RequestMethod.GET)
    public String assignTeam(@RequestParam(value = "team", required = true) String tmname, 
        @RequestParam(value = "user", required = true) int uid, 
            ModelMap model, Principal principal){
        String user = principal == null ? null : principal.getName();

        logger.debug("Assigning in team " + tmname + " by " + user);
        Team team = DBAccessor.getTeam(tmname);
        if (team == null) {
            logger.debug("Team not found " + tmname);
            return UrlPath.ERROR;
        }

        LeagueInfo lg = DBAccessor.getLeagueInfo(team.leagueId);
        if (lg == null) {
            logger.debug("League not found " + team.leagueId);
            return UrlPath.ERROR;
        }

        Perm lp = SportACL.getPerm(user, "league", lg.id);
        Perm tp = SportACL.getPerm(user, "team", team.id);
        int lperms = lp == null ? 0 : lp.perm;
        int tperms = tp == null ? 0 : tp.perm;
        int perms = lperms | tperms;
        logger.debug("Got perms " + perms);
        if (perms == 0) {
            return UrlPath.ACCESS_DENIED;
        }


        User pl = SportACL.getUser(uid);
        if (pl == null) {
            logger.debug("Player with id " + uid + " not found");
            return UrlPath.ERROR;
        }

        Perm upp = SportACL.getPerm(pl.name, "team", team.id);
        int uperms = upp == null ? 0 : upp.perm;


        List<Perm> plist = new ArrayList<>();

        Perm up = new Perm();
        up.perm = ACLConstants.MANAGE_GAMES;
        up.desc="set_scores";
        up.state = uperms & up.perm;
        plist.add(up);

        up = new Perm();
        up.perm = ACLConstants.MANAGE_PLAYERS;
        up.desc="manage_players";
        up.state = uperms & up.perm;
        plist.add(up);



        model.addAttribute("league", lg);
        model.addAttribute("player", pl);
        model.addAttribute("team", team);
        model.addAttribute("perms", plist);

        return "assign_team";
    }

}
