package com.leaguetor.web.ctrl;


import javax.servlet.http.HttpServletRequest;

import com.leaguetor.web.UrlPath;
import com.leaguetor.DBAccessor;
import com.leaguetor.web.ErrorFactory;
import com.leaguetor.SportACL;
import com.leaguetor.acl.ACLConstants;
import com.leaguetor.entity.*;
import com.leaguetor.acl.*;
import com.leaguetor.mail.*;
import com.leaguetor.ThriftUtil;


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

@Controller
public class ProfileCtrl extends BaseCtrl{
    
    @RequestMapping(value = UrlPath.PROFILE, method = RequestMethod.GET)
    public String showProfile(ModelMap model, Principal principal) {
        String usr = principal.getName();
        User u = SportACL.findUser(usr);
        if (u == null) {
            model.addAttribute("error", true);
            return "error";
        }

        List<Integer> lgs = SportACL.getUserLeagues(usr);
        if (lgs != null) {
            List<LeagueInfo> lis = DBAccessor.getUserLeagues(lgs);
            if (lis != null && lis.size() > 0)
                model.addAttribute("leagues", lis);
        }
        model.addAttribute("usr", u);

        List<LeagueInfo> lf = DBAccessor.getFanLeagues(u.id);
        logger.debug("LFans " + lf);

        if (lgs != null && lf != null) {
            for (Integer lgid : lgs) {
                LeagueInfo li = ThriftUtil.findList(lf, lgid);
                if (li != null)
                    lf.remove(li);
            }
        }
        model.addAttribute("fleagues", lf);        

        List<Integer> ptids = SportACL.getPermTeams(usr);
        if (ptids != null && ptids.size() > 0) {
            List<Team> pteams = DBAccessor.getTeamListById(ptids);
            model.addAttribute("pteams", pteams);
        }

        return "profile";
    }

/*
    @RequestMapping(value = UrlPath.DO_SAVE_PROFILE, method = RequestMethod.POST)
    public String schedule(@ModelAttribute("usr") User u,
        BindingResult errors, ModelMap model, RedirectAttributes redirectAttributes, Principal principal){
        SportDB.Iface db = DBAccessor.getDB();
    }
*/


}
