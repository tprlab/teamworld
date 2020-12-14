package com.leaguetor.web;

import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;

import org.springframework.security.core.userdetails.UserDetails;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TokenService extends PersistentTokenBasedRememberMeServices {

    protected final Log logger = LogFactory.getLog(getClass());

    protected UserDetails processAutoLoginCookie(String[] cookieTokens,
                                 javax.servlet.http.HttpServletRequest request,
                                 javax.servlet.http.HttpServletResponse response) {

        for (String ck : cookieTokens) {
            logger.debug("Process cookies "  + ck);
        }

        return super.processAutoLoginCookie(cookieTokens, request, response); 
    }
}