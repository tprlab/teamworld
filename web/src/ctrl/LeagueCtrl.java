package com.leaguetor.web.ctrl;


import javax.servlet.http.HttpServletRequest;

import com.leaguetor.web.UrlPath;
import com.leaguetor.DBAccessor;
import com.leaguetor.web.ErrorFactory;
import com.leaguetor.entity.*;
import com.leaguetor.SportACL;
import com.leaguetor.acl.*;
import com.leaguetor.StringUtil;



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
import java.util.Date;
import com.leaguetor.entity.*;

@Controller
public class LeagueCtrl extends BaseCtrl{
    
    @RequestMapping(value = UrlPath.CREATE_LEAGUE, method = RequestMethod.GET)
    public String getCreateForm(@ModelAttribute("league") LeagueInfo lg, ModelMap model) {
        List<Sport> sportList = DBAccessor.getSportList();
        if (sportList == null)
            return UrlPath.ERROR;
        logger.debug("have league " + lg);
        model.addAttribute("sportList", sportList);
        if (lg == null)
            lg = new LeagueInfo();
        model.addAttribute("league", lg);

        return "createlg";
    }

    @RequestMapping(value = UrlPath.DO_CREATE_LEAGUE, method = RequestMethod.POST)
    public String handleCreate(@ModelAttribute("league") LeagueInfo lg, BindingResult errors, HttpServletRequest request, 
        ModelMap model, RedirectAttributes redirectAttributes, Principal principal){

        SportDB.Iface db = DBAccessor.getDB();
        int lgid = 0;
        String user = principal.getName();

        logger.debug("Creating league " + lg + " by " + user);
        User owner = SportACL.findUser(user);

        if (owner == null) {
            logger.error("User not found to create league " + lg);
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_CREATE_LEAGUE, LeaguetorConstants.ERROR_UNEXPECTED, null);
            redirectAttributes.addFlashAttribute("error", err);
            return "redirect:" + UrlPath.CREATE_LEAGUE;
        }

        lg.owner = owner.id;
        lg.ownername = owner.name;

        if (lg.name == null || lg.name.length() < 3) {
            redirectAttributes.addFlashAttribute("error", "empty.name");
            redirectAttributes.addFlashAttribute("league", lg);
            return "redirect:" + UrlPath.CREATE_LEAGUE;
        }

        try { 
            lgid = db.createLeague(lg);
            SportACL.grant(user, "league", lgid, ACLConstants.MANAGE);
        } catch(DbError t) {
            logger.error("Cannot save league " + lg + ": " + t.getMessage());
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_CREATE_LEAGUE, t.code, t.message);
            redirectAttributes.addFlashAttribute("error", err);
            redirectAttributes.addFlashAttribute("league", lg);
            return "redirect:" + UrlPath.CREATE_LEAGUE;
        } catch(Throwable t) {
            logger.error("Cannot save league " + lg, t);
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_CREATE_LEAGUE, LeaguetorConstants.ERROR_UNEXPECTED, null);
            redirectAttributes.addFlashAttribute("error", err);
            return "redirect:" + UrlPath.CREATE_LEAGUE;
        }
        redirectAttributes.addAttribute("name", lg.uname);

        return "redirect:" + UrlPath.MANAGE_LEAGUE;
    }

    @RequestMapping(value = UrlPath.LEAGUE, method = RequestMethod.GET)
    public String getLeague(@RequestParam(value = "name", required = true) String uname, 
            ModelMap model, Principal principal){
        String user = principal == null ? null : principal.getName();
        logger.debug("Getting league " + uname + " by " + user);
        SportDB.Iface db = DBAccessor.getDB();
        League lg = DBAccessor.getLeague(uname, true);
        if (lg == null)
            return UrlPath.ERROR;
        model.addAttribute("league", lg);
        TourInfo tr = null;
        if (lg.getToursSize() > 0) {
            tr = lg.tours.get(0);            
        } else if (lg.getHistorySize() > 0) {
            tr = lg.history.get(0);
        }

        if (tr != null) {
            Tour trx = DBAccessor.getTour(tr.uname, 0);
            if (trx.divList != null) {
                for (Division d : trx.divList) {
                    d.table = DBAccessor.getTable(trx.id, d.id);
                }
            }
            if (trx.main != null)
                trx.main.table = DBAccessor.getTable(trx.id, 0);

            Counting cnt = DBAccessor.getCounting(trx.id);
            model.addAttribute("counting", cnt);
            model.addAttribute("tr", trx);
        }

        if (principal == null) {
            model.addAttribute("follow", 0);            
        } else {
            User u = SportACL.findUser(user);
            if (u == null) {
                model.addAttribute("follow", 0);            
            } else {
                Fan fan = DBAccessor.getFan(u.id, LeaguetorConstants.FAN_LEAGUE, lg.id);
                model.addAttribute("follow", fan == null ? 1 : -1);            
            }
        }

/*        
        if (lg.ownername.equals(user))
            model.addAttribute("manage", true);
*/
        return UrlPath.LEAGUE;
    }

    @RequestMapping(value = UrlPath.LEAGUE_HISTORY, method = RequestMethod.GET)
    public String getHistory(@RequestParam(value = "name", required = true) String uname, 
            ModelMap model, Principal principal){
        String user = principal == null ? null : principal.getName();
        logger.debug("Getting league " + uname + " by " + user);
        SportDB.Iface db = DBAccessor.getDB();
        League lg = DBAccessor.getLeague(uname, true);
        if (lg == null)
            return UrlPath.ERROR;
        if (lg.history != null) {
            for (TourInfo ht : lg.history) {
                ht.strStart = StringUtil.formatDate(new Date(ht.started), "MMM YY");                
                ht.strEnd = StringUtil.formatDate(new Date(ht.finished), "MMM YY");
            }
        }

        model.addAttribute("league", lg);
        return "lg_history";
    }




    @RequestMapping(value = UrlPath.MANAGE_LEAGUE_INFO, method = RequestMethod.GET)
    public String manageLeague(@RequestParam(value = "name", required = true) String uname, 
            ModelMap model, Principal principal){
        String user = principal == null ? null : principal.getName();

        logger.debug("Getting league " + uname + " by " + user);
        LeagueInfo lg = DBAccessor.getLeagueInfo(uname);
        if (lg == null)
            return UrlPath.ERROR;
        if (!lg.ownername.equalsIgnoreCase(user)) {
            return UrlPath.ACCESS_DENIED;
        }

        List<Sport> sportList = DBAccessor.getSportList();
        model.addAttribute("league", lg);
        model.addAttribute("sportList", sportList);
        return "manage_league_info";
    }

    @RequestMapping(value = UrlPath.DO_SAVE_LEAGUE, method = RequestMethod.POST)
    public String handleSave(@ModelAttribute("league") LeagueInfo lg, BindingResult errors, HttpServletRequest request, 
        ModelMap model, RedirectAttributes redirectAttributes, Principal principal){

        SportDB.Iface db = DBAccessor.getDB();
        String user = principal.getName();
        logger.debug("Saving league " + lg + " by " + user);

        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_LEAGUE);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }


        try { 
            db.saveLeague(lg);
            redirectAttributes.addFlashAttribute("umsg", "league.saved");
        } catch(DbError t) {
            logger.error("Cannot save league " + lg + ": " + t.getMessage());
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_CREATE_LEAGUE, t.code, t.message);
            redirectAttributes.addFlashAttribute("error", err);
        } catch(Throwable t) {
            logger.error("Cannot save league " + lg, t);
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_CREATE_LEAGUE, LeaguetorConstants.ERROR_UNEXPECTED, null);
            redirectAttributes.addFlashAttribute("error", err);
        }

        redirectAttributes.addAttribute("name", lg.uname);
        return "redirect:" + UrlPath.MANAGE_LEAGUE_INFO;
    }

    @RequestMapping(value = UrlPath.MANAGE_LEAGUE_PPL, method = RequestMethod.GET)
    public String manageLeaguePpl(@RequestParam(value = "name", required = true) String uname, 
            ModelMap model, Principal principal){
        String user = principal == null ? null : principal.getName();

        logger.debug("Getting league fans " + uname + " by " + user);
        LeagueInfo lg = DBAccessor.getLeagueInfo(uname);
        if (lg == null)
            return UrlPath.ERROR;

        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_PLAYERS);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }

        List<Player> fans = DBAccessor.getFans(lg.id, LeaguetorConstants.FAN_LEAGUE);
        model.addAttribute("league", lg);
        model.addAttribute("fans", fans);
        return "manage_league_ppl";
    }

}
