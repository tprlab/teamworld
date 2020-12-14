package com.leaguetor.web.ctrl;


import javax.servlet.http.HttpServletRequest;

import com.leaguetor.web.UrlPath;
import com.leaguetor.DBAccessor;
import com.leaguetor.web.ErrorFactory;
import com.leaguetor.entity.*;
import com.leaguetor.StringUtil;
import com.leaguetor.Scheduler;
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
import java.util.Map;
import java.util.HashMap;
import com.leaguetor.entity.*;
import com.leaguetor.PlayOff;

@Controller
public class ScheduleCtrl extends BaseCtrl{

    @RequestMapping(value = UrlPath.SCHEDULE_TOUR, method = RequestMethod.GET)    
    public String getSchedule(@RequestParam(value = "name", required = true) String uname, 
            @RequestParam(value = "div", required = false) String div, 
            @RequestParam(value = "mode", required = false) Integer imode,
            @RequestParam(value = "stage", required = false) Integer istage,
            @RequestParam(value = "all", required = false) Boolean ball,
            ModelMap model, Principal principal){
        String user = principal == null ? null : principal.getName();
        int mode = imode == null ? 0 : imode;
        int stage = istage == null ? 0 : istage;
        boolean all = ball == null ? false : ball;

        logger.debug("Scheduling tour " + uname + " by " + user + " div " + div + " mode " + mode + " stage " + stage);
        Tour tr = DBAccessor.getTour(uname, 0);
        if (tr == null) {
            logger.warn("No tour " + uname);
            return UrlPath.ERROR;
        }
        League lg = DBAccessor.getLeague(tr.league.uname, false);
        if (lg == null) {
            logger.warn("No league " + tr.league.uname);
            return UrlPath.ERROR;
        }

        int acl = SportACL.isGranted(user, null, "league", tr.league.id, ACLConstants.MANAGE_GAMES);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }

        DivInfo dv = null;
        if (div != null)
        {
            dv = DBAccessor.getDivInfo(div);
            model.addAttribute("dv", dv);
        }
        ScheduleParams sch = new ScheduleParams();
        if (mode == LeaguetorConstants.PLAY_OFF)
            sch = schedulePlayOff(tr, dv, stage, all);
        if (sch != null)
            sch.mode = mode;

        model.addAttribute("tr", tr);
        model.addAttribute("league", lg);
        model.addAttribute("sch", sch);
        model.addAttribute("minutes", DBAccessor.MINUTES);
        return "schedule";
    }

    ScheduleParams schedulePlayOff(Tour tr, DivInfo div, int stage, boolean all) {

        int div_id = div == null ? 0 : div.id;
        SportDB.Iface db = DBAccessor.getDB();
        try { 
            return db.suggestPlayOff(tr.id, div_id, stage, all);
        } catch(Throwable t) {
            logger.error("Cannot suggest play off", t);
        }
        return null;
    }


    @RequestMapping(value = UrlPath.DO_CREATE_SCHEDULE, method = RequestMethod.POST)
    public String schedule(@ModelAttribute("sch") ScheduleParams sch,
        @RequestParam(value = "hour", required = false) String hour,
        @RequestParam(value = "minute", required = false) String minute,
        @RequestParam(value = "ampm", required = false) String ampm,
        @RequestParam(value = "ehour", required = false) String ehour,
        @RequestParam(value = "eminute", required = false) String eminute,
        @RequestParam(value = "eampm", required = false) String eampm,
        BindingResult errors, ModelMap model, RedirectAttributes redirectAttributes, Principal principal){
        SportDB.Iface db = DBAccessor.getDB();
        logger.debug("Scheduling " + sch);
        boolean nodiv = StringUtil.emptyOrNull(sch.divName);
        String user = principal == null ? null : principal.getName();

        LeagueInfo lg = DBAccessor.getLeagueInfo(sch.leagueName);
        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE_GAMES);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }

        sch.startTime = StringUtil.buildTimeString(hour, minute, ampm);
        sch.endTime = StringUtil.buildTimeString(ehour, eminute, eampm);

        logger.debug("Built time " + sch.startTime + " from " + hour + " " + minute + " " + ampm);
        logger.debug("Built end time " + sch.endTime + " from " + ehour + " " + eminute + " " + eampm);
        try { 
            if (sch.mode == 0)
            {
                int scn = db.scheduleRoundRobin(sch);
                logger.debug("Scheduled " + scn + " games");
            } else {
                Team dup = PlayOff.checkDups(sch.games);
                if (dup != null) {
                    logger.warn("Dup team for playoff " + dup);
                    redirectAttributes.addFlashAttribute("error", "dup.team");
                    redirectAttributes.addFlashAttribute("errext", dup.name);
                    redirectAttributes.addAttribute("name", sch.tourName);
                    redirectAttributes.addAttribute("div", sch.divName);
                    return "redirect:" + UrlPath.SCHEDULE_TOUR;
                }
                int scn = db.schedulePlayOff(sch);
                logger.debug("Scheduled " + scn + " playoff games");
            }
        } catch(DbError t) {
            logger.error("Cannot schedule " + sch + ": " + t.getMessage());
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_SCHEDULE, t.code, t.message);
            redirectAttributes.addFlashAttribute("error", err);
            redirectAttributes.addAttribute("name", sch.tourName);
            redirectAttributes.addAttribute("div", sch.divName);

            return "redirect:" + UrlPath.SCHEDULE_TOUR;
        } catch(Throwable t) {
            logger.error("Cannot schedule " + sch, t);
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_SCHEDULE, LeaguetorConstants.ERROR_UNEXPECTED, null);
            redirectAttributes.addFlashAttribute("error", err);
            redirectAttributes.addAttribute("name", sch.tourName);
            redirectAttributes.addAttribute("div", sch.divName);

            return "redirect:" + UrlPath.SCHEDULE_TOUR;
        }

        redirectAttributes.addAttribute("name", nodiv ? sch.tourName : sch.divName);
        if (!nodiv)
            redirectAttributes.addAttribute("tour", sch.tourName);

        return "redirect:" + (nodiv ? UrlPath.MANAGE_GAMES : UrlPath.MANAGE_DIV);
    }

    @RequestMapping(value = UrlPath.DO_REFRESH_TABLE, method = RequestMethod.POST)
    public String refreshTable(@RequestParam(value = "tour", required = true) String tour, 
        @RequestParam(value = "div", required = false) String div, 
        @RequestParam(value = "ret", required = false) String ret,
        ModelMap model, RedirectAttributes redirectAttributes, Principal principal){

        SportDB.Iface db = DBAccessor.getDB();
        logger.debug("Refreshing " + tour + ": " + div);

        TourInfo tr = DBAccessor.getTourInfo(tour);
        if (tr == null) {
            return UrlPath.ERROR;
        }

        String user = principal == null ? null : principal.getName();

        int acl = SportACL.isGranted(user, null, "league", tr.league.id, ACLConstants.MANAGE_GAMES);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }

        boolean nodiv = div == null;
        boolean ret_tour = ret == null;
        String ret_addr = ret_tour || nodiv ? UrlPath.MANAGE_STANDINGS : UrlPath.MANAGE_DIV;
        if (ret_tour || nodiv)        
            redirectAttributes.addAttribute("name", tr.uname);
        else {
            redirectAttributes.addAttribute("tour", tr.uname);
            redirectAttributes.addAttribute("name", div);
        }

        DivInfo dv = nodiv ? null : DBAccessor.getDivInfo(div);

        try { 
            db.recalcTable(tr.id, nodiv ?  0 : dv.id);
            logger.debug("Refreshed table for " + tr.uname + ": " + div);
        } catch(DbError t) {
            logger.error("Cannot refresh table for " + tr.uname + ": " + div, t);
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_REFRESH_TABLE, t.code, t.message);
            redirectAttributes.addFlashAttribute("error", err);
        } catch(Throwable t) {
            logger.error("Cannot refresh table for " + tr.uname + ": " + div, t);
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_REFRESH_TABLE, LeaguetorConstants.ERROR_UNEXPECTED, null);
            redirectAttributes.addFlashAttribute("error", err);
        }
        return "redirect:" + ret_addr;
    }

    @RequestMapping(value = UrlPath.DO_CLEAR_SCHEDULE, method = RequestMethod.POST)
    public String clearSchedule(@RequestParam(value = "tour", required = true) String tour,
        @RequestParam(value = "div", required = false) String div, 
        @RequestParam(value = "ret", required = false) String ret, 
        ModelMap model, RedirectAttributes redirectAttributes, Principal principal){

        SportDB.Iface db = DBAccessor.getDB();
        logger.debug("Clearing schedule " + tour + ": " + div);
        TourInfo tr = DBAccessor.getTourInfo(tour);
        if (tr == null) {
            return UrlPath.ERROR;                        
        }

        String user = principal == null ? null : principal.getName();

        int acl = SportACL.isGranted(user, null, "league", tr.league.id, ACLConstants.MANAGE_GAMES);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }

        
        boolean nodiv = StringUtil.emptyOrNull(div);
        boolean ret_tour = StringUtil.emptyOrNull(ret);
        String ret_addr = ret_tour || nodiv ? UrlPath.MANAGE_GAMES : UrlPath.MANAGE_DIV;
        if (ret_tour || nodiv)        
            redirectAttributes.addAttribute("name", tr.uname);
        else {
            redirectAttributes.addAttribute("tour", tr.uname);
            redirectAttributes.addAttribute("name", div);
        }

        DivInfo dv = nodiv ? null : DBAccessor.getDivInfo(div);

        try { 
            db.clearSchedule(tr.id, nodiv ?  0 : dv.id);
            logger.debug("Cleared schedule for " + tr.uname + ": " + div);
        } catch(DbError t) {
            logger.error("Cannot clear schedule for " + tr.uname + ": " + div, t);
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_CLEAR_SCHEDULE, t.code, t.message);
            redirectAttributes.addFlashAttribute("error", err);
        } catch(Throwable t) {
            logger.error("Cannot clear schedule for " + tr.uname + ": " + div, t);
            String err = ErrorFactory.getError(LeaguetorConstants.ACTION_CLEAR_SCHEDULE, LeaguetorConstants.ERROR_UNEXPECTED, null);
            redirectAttributes.addFlashAttribute("error", err);
        }
        return "redirect:" + ret_addr;
    }

    @RequestMapping(value = UrlPath.CLEAR_SCHEDULE, method = RequestMethod.GET)    
    public String getSchedule(@RequestParam(value = "tour", required = true) String tour, 
            @RequestParam(value = "div", required = false) String div, 
            @RequestParam(value = "ret", required = false) String ret, 
            ModelMap model, Principal principal){

            TourInfo tr = DBAccessor.getTourInfo(tour);
            if (tr == null) {
                logger.warn("No tour " + tour);
                return UrlPath.ERROR;
            }


            model.addAttribute("divName", div);
            model.addAttribute("ret", ret);
            model.addAttribute("tr", tr);

            return "clear_sch";
    }

    @RequestMapping(value = UrlPath.ADD_GAME, method = RequestMethod.GET)    
    public String addGame(@RequestParam(value = "tour", required = true) String tour, 
            @RequestParam(value = "div", required = false) String div, 
            ModelMap model, Principal principal){

        String user = principal == null ? null : principal.getName();

        logger.debug("Adding game " + tour + " by " + user + " div " + div);
        Tour tr = DBAccessor.getTour(tour, 0);
        if (tr == null) {
            logger.warn("No tour " + tour);
            return UrlPath.ERROR;
        }

        int acl = SportACL.isGranted(user, null, "league", tr.league.id, ACLConstants.MANAGE_GAMES);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }

        DivInfo dvi = div != null ? DBAccessor.getDivInfo(div) : null;

        Division dv = dvi != null && tr.divs != null ? tr.divs.get(dvi.id) : tr.main;
        if (dv.getTableSize() == 0) {
            dv.table = DBAccessor.getTable(tr.id, dv.id);
        }
        logger.debug("Adding game for for div " + dv);
        model.addAttribute("dv", dv);
        model.addAttribute("tr", tr);
        model.addAttribute("league", tr.league);

        return "add_game";
    }

}
