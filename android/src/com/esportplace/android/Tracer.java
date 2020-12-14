package com.esportplace.android;

import java.util.Date;

//import org.apache.log4j.Logger; 

import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tracer
{
    private static final String LOG_TAG = "SportWorld";    
    private static final Logger logger = LoggerFactory.getLogger("asport");

    public static String getString(Object ...args) {
        if (args == null)
            return "";
        StringBuilder sb = new StringBuilder();
        for (Object a : args)
            sb.append(a).append(" ");

        return sb.toString();
    }
    public static void log(Object ...args) 
    { 
        if (args == null)
            return;
        logger.info(getString(args));
//        System.out.println(s);
/*
        Date d = new Date();
        String sl = "[" + d.toString() + "] ";
        sl += s == null ? "" : s.toString();
        Log.i(LOG_TAG, sl);
*/

    }

    public static void err(String msg, Throwable t) 
    { 
//        t.printStackTrace();
        logger.error(msg, t);
//        System.out.println(t);
//        Log.e(LOG_TAG, msg + (t == null ? "" : t.toString()));
    }

}
