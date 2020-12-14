package com.leaguetor.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.leaguetor.acl.*;
import com.leaguetor.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.ArrayList;


public class UserDetailsServiceImpl implements UserDetailsService {

    protected static final Log logger = LogFactory.getLog(UserDetailsServiceImpl.class);


    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("loadingUserByName.. " + username);

        User u = SportACL.findUser(username);
        if (u == null) {
            logger.warn("User " + username + " not found");
            throw new UsernameNotFoundException("User not found");
        }

        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));

        UserDetails ret = new org.springframework.security.core.userdetails.User(u.name, "***", grantedAuths);
        return ret;        
    }
}
