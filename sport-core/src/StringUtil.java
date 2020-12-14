package com.leaguetor;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.Locale;
import java.util.GregorianCalendar;

public class StringUtil {

    static SimpleDateFormat createFormatter(String mask) {
        SimpleDateFormat ret = new SimpleDateFormat(mask);
        ret.setTimeZone(TimeZone.getTimeZone("GMT"));
        //ret.setTimeZone(TimeZone.getDefault());
        return ret;
    }

    static SimpleDateFormat createUTCFormatter(String mask) {
        SimpleDateFormat ret = new SimpleDateFormat(mask);
        ret.setTimeZone(TimeZone.getDefault());
        //ret.setTimeZone(TimeZone.getTimeZone("GMT"));
        return ret;
    }


    static SimpleDateFormat cTimeFormatU = createUTCFormatter("hh:mm a");
    static SimpleDateFormat cTimeFormat = createFormatter("hh:mm a");
    static SimpleDateFormat cDtFormat = createFormatter("dd MMM - hh:mm a");

    public static long DAY_MSEC = 3600 * 1000 * 24;


    public static boolean emptyOrNull(String str) {
        return str == null || str.length() < 1;
    }

    public static boolean emptyOrNull(Collection cc) {
        return cc == null || cc.size() < 1;
    }

    public static int parseInt(String str) {
        if (str == null)
            return 0;
        try {
            return Integer.parseInt(str);
        } catch(Throwable t) {
            return 0;
        }
    }

    public static long parseLong(String str) {
        if (str == null)
            return 0;
        try {
            return Long.parseLong(str);
        } catch(Throwable t) {
            return 0;
        }
    }

    public static double parseDouble(String str) {
        if (str == null)
            return 0;
        try {
            return Double.parseDouble(str);
        } catch(Throwable t) {
            return 0;
        }
    }

    public static String[] args(Object ...a) {
        List<String> ls = new ArrayList<String>();
        for (Object o : a) {
            ls.add(o.toString());            
        }
        String[] ret = new String[ls.size()];
        return ls.toArray(ret);
    }

    public static Date parseDate(String str, String mask) {
        if (emptyOrNull(str))
            return null;

        SimpleDateFormat df = createFormatter(mask);
        try {
            Date ret = df.parse(str);
            Tracer.log("Date " + str + " parsed to " + ret);
            return ret;
        } catch(Exception e) {
            Tracer.err("Parse date error " + str + " on " + df.toPattern(), e);
        }
        return null;
    }

    public static Date parseTime(String str) {
        if (emptyOrNull(str))
            return null;

        try {
            return cTimeFormat.parse(str);
        } catch(Exception e) {                   
            Tracer.err("Parse time error " + str + " on " + cTimeFormat.toPattern(), e);
        }
        return null;
    }

    public static String formatDate(Date d) {
        return cDtFormat.format(d);
    }

    public static String formatDate(Date d, String mask) {
        SimpleDateFormat df = createFormatter(mask);
        return df.format(d);
    }

    public static String formatTime(Date d) {
        return cTimeFormat.format(d);
    }

    public static String formatTimeU(Date d) {
        return cTimeFormatU.format(d);
    }



    public static int[] parseScore(String str) {
        if (emptyOrNull(str))
            return null;
        str = str.replaceAll("\\s+","");
        String parts[] = str.split(":");
        if (parts == null || parts.length != 2) {
            Tracer.log("Invalid score " + str);
            return null;
        }
        int s1 = 0;
        int s2 = 0;
        try {
            s1 = Integer.parseInt(parts[0]);
            s2 = Integer.parseInt(parts[1]);
        } catch(Exception e) {
            Tracer.err("Cannot parse score " +str, e);
            return null;
        }
        return new int[]{s1, s2};
    }

    public static long getNearestDay(Date dt, int day) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
        c.setTime(dt);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        Tracer.log("Date " + dt + " is " + dayOfWeek + " with first day " + c.getFirstDayOfWeek() + ", need " + day);
        dayOfWeek--;
        if (dayOfWeek > day)
            day += 7;

        long diff = day - dayOfWeek;
        long ntime = dt.getTime() / DAY_MSEC * DAY_MSEC;
        return ntime + diff * DAY_MSEC;
    }

    public static long getNextWeek(long dt) {
        return dt + 7 * DAY_MSEC;
    }

    public static long buildTime(int h, int m, int a) {
        return ((h + a * 12) * 60 + m) * 60000;
    }

    public static long buildTime(int h, int m) {
        return (h * 60 + m) * 60000 ;
    }

    public static long buildTime(String hour, String minute, String ampm) {
        int h = parseInt(hour);
        int m = parseInt(minute);
        int a = parseInt(ampm);
        return buildTime(h, m, a);
    }

    public static String buildTimeString(String hour, String minute, String ampm) {
        if (emptyOrNull(hour) || emptyOrNull(minute) || emptyOrNull(ampm))
            return null;

        int a = parseInt(ampm);
        String h = hour.length() < 2 ? "0" + hour : hour;
        String m = minute.length() < 2 ? "0" + minute : minute;
        String ap = a == 0 ? "AM" : "PM";
        return h + ":" + m + " " + ap;
    }

    public static Date buildDate(int year, int month, int day) {
        Calendar c = new GregorianCalendar();
        c.set(year, month, day);
        return c.getTime();
    }

    public static Date combineDate(Date dt, Date tm) {
        if (dt == null)
            dt = new Date();
        long t = dt.getTime();
        t /= DAY_MSEC;
        t *= DAY_MSEC;
        if (tm != null)
            t += (tm.getTime() % DAY_MSEC);
        return new Date(t);
    }

    public static int list_size(Collection c) {
        return c == null ? 0 : c.size();
    }

    public static long fromLocal(long t) {
        long offset = TimeZone.getDefault().getOffset(System.currentTimeMillis());
        return t - offset;  
    }

    public static int getHour(long t) {
        long dh = t % DAY_MSEC;
        dh /= 60000;
        return (int)(dh / 60);
    }

    public static int getMinute(long t) {
        long dh = t % DAY_MSEC;
        dh /= 60000;
        return (int)(dh % 60);
    }


    public static long now() {
        long utc = System.currentTimeMillis();
        long offset = TimeZone.getDefault().getOffset(utc);
        return utc + offset;
    }

}