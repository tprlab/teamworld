package com.leaguetor.web;


public class WebSportUtil {

    static String[] cFieldNames = new String[]{"field", "court", "table"};

    public static String getFieldName(int type) {
        return cFieldNames[type];
    }
}
