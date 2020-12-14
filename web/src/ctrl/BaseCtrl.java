package com.leaguetor.web.ctrl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.leaguetor.acl.Perm;
import com.leaguetor.acl.ACLConstants;
import org.springframework.ui.ModelMap;


class BaseCtrl
{
    protected final Log logger = LogFactory.getLog(getClass());


    public static void addPermAttr(int perm, ModelMap model) {
        int p_lg = perm & ACLConstants.MANAGE_LEAGUE;
        int p_tm = perm & ACLConstants.MANAGE_TEAMS;
        int p_gm = perm & ACLConstants.MANAGE_GAMES;
        int p_pl = perm & ACLConstants.MANAGE_PLAYERS;


        model.addAttribute("mgr_lg", p_lg != 0);
        model.addAttribute("mgr_tm", p_tm != 0);
        model.addAttribute("mgr_gm", p_gm != 0);
        model.addAttribute("mgr_ppl", p_pl != 0);
    }

}
