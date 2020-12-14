package com.leaguetor.web;

import com.leaguetor.entity.LeaguetorConstants;

public class ErrorFactory {
    public static String getError(int action, int code, String msg) {
        String ret = "error.unexpected";

        if (code == LeaguetorConstants.ERROR_UNEXPECTED) {
        }
        else if (code == LeaguetorConstants.ERROR_DATABASE_ERROR)
            ret = "error.db";
        else if (code == LeaguetorConstants.ERROR_VALUE_NOT_SET) {
            ret = "error.not_set";
            if (msg.equalsIgnoreCase("sport"))
                ret = "error.sport_not_set";
        }
        else if (code == LeaguetorConstants.ERROR_ALREADY_EXIST) {
            ret = "error.already_exist";
        }
        else if (code == LeaguetorConstants.ERROR_INVALID_VALUE) {
            ret = "error.invalid_value";
        }
        return ret; 
    }
}
