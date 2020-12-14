package com.leaguetor.web;

import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.web.context.request.NativeWebRequest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.User;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.ArrayList;



public class SocialSignInAdapter implements SignInAdapter {
    protected static final Log logger = LogFactory.getLog(SocialSignInAdapter.class);
    
    @Autowired
    TokenBasedRememberMeServices tokenBasedRememberMeServices;

    public String signIn(String localUserId, Connection<?> connection, NativeWebRequest request) {

        ConnectionKey ck = connection.getKey();
        logger.debug("Logged in " + localUserId + " from " + connection.getDisplayName() + " " + ck.toString());

        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));

        User u = new User(localUserId, "***", grantedAuths);
        Authentication auth = new UsernamePasswordAuthenticationToken(u, null, grantedAuths);
        SecurityContextHolder.getContext().setAuthentication(auth);

        tokenBasedRememberMeServices.onLoginSuccess((HttpServletRequest) request.getNativeRequest(),
                (HttpServletResponse) request.getNativeResponse(), auth);

        return null;
    }



}