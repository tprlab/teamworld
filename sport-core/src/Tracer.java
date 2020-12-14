package com.leaguetor;

import java.util.Date;
/*
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
*/

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Tracer
{
    //protected static Logger logger = LoggerFactory.getLogger("copperage");
    protected static Log logger = LogFactory.getLog(Tracer.class);
    public static void log(Object s) 
    { 
        System.out.println(s == null ? "" : s.toString());
        logger.debug(s == null ? "" : s.toString());
    }

    public static void err(String s, Throwable t) 
    {
        System.out.println("[Error]:" + s + ": " + t);
        logger.error(s, t);
    }

    public static void err(Throwable t) 
    { 
        err("", t);
    }

}
