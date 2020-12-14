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
public class VenuesCtrl extends BaseCtrl{
    
    @RequestMapping(value = UrlPath.MANAGE_VENUES, method = RequestMethod.GET)
    public String manageTeams(@RequestParam(value = "name", required = true) String lg_name, ModelMap model, Principal principal) {
        String user = principal == null ? null : principal.getName();
        LeagueInfo lg = DBAccessor.getLeagueInfo(lg_name);
        if (lg == null)
            return UrlPath.ERROR;


        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }

        List<Venue> vens = DBAccessor.getVenues(lg.uname);

        model.addAttribute("league", lg);
        model.addAttribute("nv", new Venue());
        model.addAttribute("vens", vens);

        logger.debug("League " + lg_name + " has venues " + (vens == null ? 0 : vens.size()));
        return "manage_league_venues";
    }

    @RequestMapping(value = UrlPath.DO_SAVE_VENUE, method = RequestMethod.POST)
    public String createDiv(@ModelAttribute("nv") Venue ven,
        @RequestParam(value = "league", required = true) String lgname, 
        @RequestParam(value = "active", required = false) String active, 
        @RequestParam(value = "delete", required = false) String delete, 
        ModelMap model, RedirectAttributes redirectAttributes, Principal principal){
        String user = principal.getName();
        logger.debug("Changing venue " + ven + " active " + active + " delete " + delete);
        redirectAttributes.addAttribute("name", lgname);

        if (ven.name == null || ven.name.length() < 3) {
            redirectAttributes.addFlashAttribute("error", "empty.name");
            return "redirect:" + UrlPath.MANAGE_VENUES;
        }

        LeagueInfo lg = DBAccessor.getLeagueInfo(lgname);
        if (lg == null)
            return UrlPath.ERROR;


        int acl = SportACL.isGranted(user, null, "league", lg.id, ACLConstants.MANAGE);
        if (acl == 0) {
            return UrlPath.ACCESS_DENIED;
        } else if (acl == -1) {
            return UrlPath.ERROR;
        }


        if (ven.courts < 1)
            ven.courts = 1;

        SportDB.Iface db = DBAccessor.getDB();
        String err = null;
        ven.active = active != null;
        ven.status = ven.active ? 1 : -1;

        try { 
            if (delete != null)
                db.deleteVenue(ven.id);
            else
                db.saveVenue(lg.uname, ven);
        } catch(DbError t) {
            logger.error("Cannot save venue " + ven + ": " + t.getMessage());
            err = ErrorFactory.getError(LeaguetorConstants.ACTION_SAVE_DIV, t.code, t.message);
        } catch(Throwable t) {
            logger.error("Cannot save venue " + ven, t);
            err = ErrorFactory.getError(LeaguetorConstants.ACTION_SAVE_DIV, LeaguetorConstants.ERROR_UNEXPECTED, null);
        }


        redirectAttributes.addFlashAttribute("error", err);
        return "redirect:" + UrlPath.MANAGE_VENUES;
    }

}
