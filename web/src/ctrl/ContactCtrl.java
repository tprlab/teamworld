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
public class ContactCtrl extends BaseCtrl{
    
    @RequestMapping(value = UrlPath.CONTACT, method = RequestMethod.GET)
    public String showContact(ModelMap model, Principal principal) {
        User u = SportACL.findUser(principal.getName());
        if (u == null) {
            model.addAttribute("error", true);
            return "error";
        }

        model.addAttribute("email", u.email);

        return "contact";
    }

    @RequestMapping(value = UrlPath.DO_CONTACT, method = RequestMethod.POST)
    public String sendMsg(@RequestParam(value = "subj", required = true) String subj, 
            @RequestParam(value = "text", required = true) String text, 
            @RequestParam(value = "email", required = false) String email, 
        ModelMap model, RedirectAttributes redirectAttributes, Principal principal) {
        if (principal == null) {
            redirectAttributes.addAttribute("error", true);
            return "redirect:/contact";
        }

        logger.debug("Contact by " + principal + " subj [ " + subj + "], text:\n" + text);

        User u = SportACL.findUser(principal.getName());
        if (u == null) {
            redirectAttributes.addAttribute("error", true);
            return "redirect:/contact";
        }

        MailWrapper mail = MailWrapper.getInst();
        if (mail == null) {
            logger.error("Mail not configured");
            redirectAttributes.addFlashAttribute("error", "error");
            redirectAttributes.addFlashAttribute("text", text);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("subj", subj);
            return "redirect:/contact";
        }
        String from = principal.getName() + "(" + email + ")";
        boolean ok = mail.sendMailSafe(null, from, subj, text);
        if (!ok) {
            logger.error("Mail sending failed");
            redirectAttributes.addFlashAttribute("error", "error");
            redirectAttributes.addFlashAttribute("text", text);
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("subj", subj);
            return "redirect:/contact";
        }

        redirectAttributes.addFlashAttribute("done", true);
        return "redirect:/contact";
    }

}
