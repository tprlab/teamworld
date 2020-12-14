package com.leaguetor.web.ctrl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.ModelMap;

import org.springframework.web.context.request.WebRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.connect.UserProfile;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.context.request.NativeWebRequest;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.User;

import org.springframework.beans.factory.annotation.Autowired;


import com.leaguetor.web.UrlPath;
import com.leaguetor.SportACL;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.ArrayList;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.Authentication;


@Controller
public class LoginCtrl extends BaseCtrl {
    @Autowired
    TokenBasedRememberMeServices tokenBasedRememberMeServices;

    @RequestMapping(value=UrlPath.LOGIN, method = RequestMethod.GET)
    public String getLoginPage(@RequestParam(value = "error", required = false) String error) {
        return "login";
    }

    @RequestMapping(value=UrlPath.SIGNIN, method = RequestMethod.GET)
    public String getRegPage(@RequestParam(value = "error", required = false) String error, ModelMap model) {
        if (error != null) {
            logger.debug("Login failed with error " + error);
            model.addAttribute("error", "error.unexpected");
        }
        return "signin";
    }

    @RequestMapping(value=UrlPath.SOC_SIGNUP, method=RequestMethod.GET)
    public String signupForm(WebRequest request, ModelMap model) {
        Connection<?> connection = ProviderSignInUtils.getConnectionFromSession(request);
        if (connection == null)
            return "error";

        ConnectionKey ck = connection.getKey();
        UserProfile up = connection.fetchUserProfile();
        logger.debug("Regging social " + ck + " - " + up.getUsername() + " " + up.getFirstName() + " " +
            up.getLastName() + " " + up.getEmail() + up.getName());
        model.addAttribute("uname", up.getUsername());
        model.addAttribute("email", up.getEmail());
        model.addAttribute("prov", ck.getProviderId());
        return "signup";
    }

    @RequestMapping(value="/signup", method=RequestMethod.POST)
    public String signup(@RequestParam(value = "name", required = true) String uname,
        @RequestParam(value = "email", required = false) String email, NativeWebRequest request) {
        if (!SportACL.regUser(uname, email)) {
            return UrlPath.ERROR;
        }

        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));

        User u = new User(uname, "***", grantedAuths);
        Authentication auth = new UsernamePasswordAuthenticationToken(u, null, grantedAuths);
        SecurityContextHolder.getContext().setAuthentication(auth);

        ProviderSignInUtils.handlePostSignUp(uname, request);
        tokenBasedRememberMeServices.onLoginSuccess((HttpServletRequest) request.getNativeRequest(),
                (HttpServletResponse) request.getNativeResponse(), auth);

        return "redirect:/profile";
     }
}
