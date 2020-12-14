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
public class SearchCtrl extends BaseCtrl{
    
    @RequestMapping(value = UrlPath.VIEW_SEARCH, method = RequestMethod.GET)
    public String getCreateForm(@ModelAttribute("words") String words, ModelMap model) {
        return "search";
    }

    @RequestMapping(value = UrlPath.DO_SEARCH, method = RequestMethod.POST)
    public String handleCreate(@ModelAttribute("words") String words, BindingResult errors, HttpServletRequest request, 
        ModelMap model, RedirectAttributes redirectAttributes, Principal principal){

        HttpSession ses = request.getSession();
        String sid = ses == null ? null : ses.getId();

        logger.debug("Searching " + words + " by " + sid);
        String[] awords = words.split(" ,;");
        redirectAttributes.addAttribute("words", words);
        for (String w : awords) {
        }

        return "redirect:" + UrlPath.VIEW_SEARCH;
    }

}
