package com.leaguetor.web.ctrl;


import javax.servlet.http.HttpServletRequest;

import com.leaguetor.web.UrlPath;
import com.leaguetor.DBAccessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import com.leaguetor.entity.*;

@Controller
public class MainCtrl extends BaseCtrl {
    
    @RequestMapping(value = UrlPath.MAIN, method = RequestMethod.GET)
    public String getMainViewPage(HttpServletRequest request, ModelMap model) {
        SportDB.Iface db = DBAccessor.getDB();

        try {
            List<LeagueInfo> ll = db.getLeagues(0, 20);
            model.addAttribute("leagues", ll);
            logger.debug("Loaded " + (ll == null ? 0 : ll.size()) + " leagues for main");

            List<Game> results = DBAccessor.getGames(0, 0, 10, 1, false, true);
            DBAccessor.prepareGames(results);
            model.addAttribute("results", results);
            int nr = results == null ? 0 : results.size();

            List<Game> games = DBAccessor.getGames(0, 0, 10, 0, true, true);
            DBAccessor.prepareGames(games);
            model.addAttribute("cgames", games);
            int ng = games == null ? 0 : games.size();

            logger.debug("Loaded " + nr + " results, and " + ng + " games");

        } catch(Throwable t) {
            logger.error("Cannot get leagues list", t);
        }

        return "main";
    }

    @RequestMapping(value = UrlPath.ERROR, method = RequestMethod.GET)
    public String getErrorPage(HttpServletRequest request, ModelMap model) {
        return "error";
    }

    @RequestMapping(value = UrlPath.ABOUT, method = RequestMethod.GET)
    public String getAbout(ModelMap model) {
        return "about";
    }
}
