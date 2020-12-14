package com.leaguetor.web;

import com.leaguetor.acl.*;
import com.leaguetor.StringUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;



public class SportACL {
    static SportACL cInst;
    AccessControl.Iface mACL;
    protected static final Log logger = LogFactory.getLog(DBAccessor.class);

    public SportACL() {
        cInst = this;
    }

    public void setACL(AccessControl.Iface acl) {
        mACL = acl;
    }

    public static AccessControl.Iface getACL() {
        return cInst.mACL;
    }


    public static int isGranted(String user, String role, String type, int id,int action) {
        try {
            boolean ok = cInst.mACL.isGranted(user, role, action, type, id);
            return ok ? 1 : 0;
        } catch (Throwable t) {
            logger.error("Cannot access ACL", t);
            return -1;
        }
    }

    public static boolean grant(String user, String type, int id, int action){
        try {
            cInst.mACL.grant(user, action, type, id);
            return true;
        } catch (Throwable t) {
            logger.error("Cannot access ACL", t);
            return false;
        }
    }

    public static boolean regUser(String name, String email) {
        try {
            return cInst.mACL.regUser(name, email);
        } catch (Throwable t) {
            logger.error("Cannot reg user", t);
            return false;
        }
    }

    public static User findUser(String name) {
        try {
            return cInst.mACL.findUser(name);
        } catch (Throwable t) {
            logger.error("Cannot find user " + name, t);
            return null;
        }
    }

    public static List<Integer> getUserLeagues(String name) {
        try {
            return cInst.mACL.getUserLeagues(name);
        } catch (Throwable t) {
            logger.error("Cannot get leagues for user " + name, t);
            return null;
        }
    }


}