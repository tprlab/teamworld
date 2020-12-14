package com.leaguetor.web;

import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ConnRep implements UsersConnectionRepository {
    JdbcUsersConnectionRepository mDB;
    protected static final Log logger = LogFactory.getLog(ConnRep.class);

    ConnRep(JdbcUsersConnectionRepository jdb) {
        mDB  = jdb;
    }

    public ConnectionRepository   createConnectionRepository(String userId)     {
        logger.debug("Created rep for " + userId);
        return mDB.createConnectionRepository(userId); 
    }

          
    public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
        logger.debug("Find user for " + providerId);
        for (String s : providerUserIds) {
            logger.debug("User " + s);
        }
        HashSet<String> hs = new HashSet<String>();
        hs.add("2");
        return hs;
    }

    public List<String>   findUserIdsWithConnection(Connection<?> connection) {
        ConnectionKey ck = connection.getKey();
        logger.debug("Find users for " + connection.getDisplayName() + " " + ck.toString());
        List<String> ret = new ArrayList<String>();
        ret.add("1");
        return ret;
    }
}
 