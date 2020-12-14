package com.leaguetor.web.ctrl;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
public class FollowCtrl extends BaseCtrl{

   

    @RequestMapping(value = UrlPath.DO_FOLLOW, method = RequestMethod.POST)
    public String handleCreate(@RequestParam("subj") String subj, @RequestParam("ftype") String type, 
        @RequestParam(value="undo", required = false) boolean undo,
        ModelMap model, RedirectAttributes redirectAttributes, Principal principal) {

        if (principal == null) {
            logger.debug("Anon attempt follow: "  + type + " - " + subj + " " + undo);
            return UrlPath.ERROR;
        }

        String uname = principal.getName();
        logger.debug("Follow " + uname + " type " + type + " - " + subj + " " + undo);
        User user = SportACL.findUser(uname);
        if (user == null) {
            logger.error("User " + uname + " not found");
            return UrlPath.ERROR;
        }

        int ftype = StringUtil.parseInt(type);
        int subid = 0;
        if (ftype == 0) {
            LeagueInfo li = DBAccessor.getLeagueInfo(subj);
            if (li == null) {
                logger.error("League " + subj + " not found to follow " + undo);
                return UrlPath.ERROR;
            }
            subid = li.id;
        } else if (ftype == 1) {
            Team tm = DBAccessor.getTeam(subj);
            if (tm == null) {
                logger.error("Team " + subj + " not found to follow " + undo);
                return UrlPath.ERROR;
            }
            subid = tm.id;
        }

        Fan f = new Fan();
        f.subj = subid;
        f.type = ftype;
        f.usr = user.id;

        boolean ok = undo ? DBAccessor.removeFan(f) : DBAccessor.addFan(f);
        String action = undo ? "removed" : "added";
        logger.debug("Fan " + f + " " + action + " with result " + ok);
    
        String url = ftype == 0 ? UrlPath.LEAGUE : UrlPath.TEAM;
        redirectAttributes.addAttribute("name", subj);

        return "redirect:" + url;
    }


}
