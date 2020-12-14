namespace java com.leaguetor.entity

const i32 ERROR_UNEXPECTED = 99;
const i32 ERROR_DATABASE_ERROR = 100
const i32 ERROR_VALUE_NOT_SET = 101;
const i32 ERROR_ALREADY_EXIST = 102;
const i32 ERROR_INVALID_VALUE = 103;
const i32 ERROR_EMPTY_NAME = 104;

const i32 ACTION_GET_SPORT_LIST = 1000;
const i32 ACTION_CREATE_LEAGUE = 1001;
const i32 ACTION_GET_LEAGUE = 1002;
const i32 ACTION_CREATE_TEAM = 1003;
const i32 ACTION_CREATE_DIV = 1004;
const i32 ACTION_SET_DIV = 1005;
const i32 ACTION_CREATE_TOUR = 1006;
const i32 ACTION_SCHEDULE = 1007;
const i32 ACTION_SAVE_GAMES = 1008;
const i32 ACTION_REFRESH_TABLE = 1009;
const i32 ACTION_FINISH_TOUR = 1010;
const i32 ACTION_CANCEL_TOUR = 1011;
const i32 ACTION_DELETE_DIV = 1012;
const i32 ACTION_CLEAR_SCHEDULE = 1013;
const i32 ACTION_COPY_STRUCTURE = 1014;
const i32 ACTION_DELETE_TEAM = 1015;
const i32 ACTION_SAVE_TEAM = 1016;
const i32 ACTION_SAVE_DIV = 1017;
const i32 ACTION_SAVE_TOUR = 1018;
const i32 ACTION_SAVE_LEAGUE = 1019;

const i32 ROUND_ROBIN = 0;
const i32 PLAY_OFF = 1;

const i32 GAME_STATUS_DEFAULT = 0;
const i32 GAME_STATUS_SCORED = 1;

const i32 SCH_MODE_ROUNDS = 0;
const i32 SCH_MODE_LIMIT = 1;

const i32 TOUR_STATUS_ACTIVE = 0;
const i32 TOUR_STATUS_FINISHED = 1;
const i32 TOUR_STATUS_CANCELED = -1;

const i32 MODE_NONE = 0;
const i32 MODE_ASC  = 1;
const i32 MODE_DESC = -1;

const i32 FAN_LEAGUE = 0;
const i32 FAN_TEAM = 1;


struct Counting
{
    1: i32 id;
    2: i32 win;
    3: i32 loss;
    4: i32 tie;
    5: i32 wot;
    6: i32 lot;
    7: bool tieAllowed;
    8: bool otAllowed;
    9: bool resultOnly;
    10: string name;
}

struct Sport
{
    1: i32 id;
    2: string name;
    3: i32 popul;
    4: i32 oppnum;
    5: list<Counting> countings;
    6: i32 fieldType;
    7: string fieldName;
}

struct LeagueInfo
{
    1: i32 id;
    2: string name;
    3: string uname;
    4: string location;
    5: i32 sportId;
    6: Sport sport;
    7: i32 owner;
    8: string ownername;
    9: i64 created;
    10: string timezone;
    11: i32 status;
}



struct DivInfo
{
    1: i32 id;
    2: string name;
    3: string uname;
    4: i32 leagueId;
    5: LeagueInfo league;
    6: i32 status;
    7: i64 created;
    8: string leagueName;
    9: i32 teams;
    10: i32 tours;
    11: i32 rank;
}

struct Team
{
    1: i32 id;
    2: string name;
    3: string uname;
    4: i32 leagueId;
    5: LeagueInfo league;
    6: i32 divId;
    7: i32 status;
    8: i64 created;
    9: string leagueName;
    10: string tourName;
    11: i32 games;
    12: i32 tours;
}




struct TourInfo
{
    1: i32 id;
    2: string name;
    3: i32 status;
    4: i32 stage;
    5: i64 started;
    6: i64 finished;
    7: i32 leagueId;
    8: string leagueName;
    9: LeagueInfo league;
    10: string uname;
    11: i64 created;
    20: string strStart;
    21: string strEnd;
}

struct Game
{   
    1: i32 id;
    2: Team team1; 
    3: Team team2;
    4: i32 status;
    5: i32 score1;
    6: i32 score2;
    7: i64 scheduled;
    8: string details;
    9: bool overtime;
    10: i32 stage;
    11: string strDate;
    12: string strTime;
    13: string location;
    14: bool removed;
    15: string strScore1;
    16: string strScore2;
    20: i32 leagueId;
    21: LeagueInfo league;
    22: i32 tourId;
    23: TourInfo tour;
    24: i32 divId;
    25: DivInfo division;
    26: string shownDate;
    27: i32 venueId;
    28: i32 court;
}



struct TableRecord
{
    1: Team team;
    2: i32 points;
    3: i32 games;
    4: i32 wins;
    5: i32 losses;
    6: i32 wot;
    7: i32 lot;
    8: i32 ties;
    9: i32 gf;
    10: i32 ga;
    11: i32 gdiff;
    12: i32 place;
    13: i32 tour;
    14: i32 version;
    20: i32 leagueId;
    21: i32 tourId;
    22: i32 divId;
    23: i32 teamId;
}


struct Division
{
    1: i32 id;
    2: string name;
    3: string uname;
    4: i32 leagueId;
    5: LeagueInfo league;
    6: i32 status;
    7: i64 created;
    8: string leagueName;
    9: string tourName;
    11: i32 rank;
    
    20: list<Team> teams;
    21: list<Game> games;
    22: list<TableRecord> table;
}


struct Tour
{
    1: i32 id;
    2: string name;
    3: i32 status;
    4: i32 stage;
    5: i64 started;
    6: i64 finished;
    7: i32 leagueId;
    8: string leagueName;
    9: LeagueInfo league;
    10: string uname;
    11: i64 created;

    20: map<i32, Division> divs;
    21: list<Team> teams;
    22: list<Division> divList;
    23: Counting counting;
    24: Division main;
    25: list<Game> games;
}




struct League
{
    1: i32 id;
    2: string name;
    3: string uname;
    4: string location;
    5: i32 sportId;
    6: Sport sport;
    7: i32 owner;
    8: string ownername;
    9: i64 created;
    10: string timezone;
    20: map<i32, Division> divs;
    21: list<Team> teams;
    22: list<TourInfo> tours;
    23: list<TourInfo> history;
    24: list<Division> divList;

    30: map<i32, DivInfo> divMap;
    31: map<i32, TourInfo> tourMap;
    32: LeagueInfo info;
}

struct ScheduleParams
{
    1: i32 mode;
    2: i32 rounds;
    3: i32 extmode;
    4: string leagueName;
    5: string tourName;
    6: string divName;
    7: i32 stage;
    8: list<i32> stages;
    9: list<Game> games;
    10: list<Game> history;
    11: list<TableRecord> table;
    12: bool schtime;
    13: i32 day;
    14: string startDate;
    15: string startTime;
    16: string endTime;
    17: i32 gameLength;
    18: i32 gameNum;
}

struct Venue
{
    1: i32 id;
    2: string name;
    3: string desc;
    4: string addr;
    5: string naddr;
    6: i32 courts;
    7: i32 leagueId;
    8: i32 status;
    9: bool active;
}

struct Fan
{
    1: i32 usr;
    2: i32 type;
    3: i32 subj;
}


struct Player
{
    1: i32 id;
    2: string name;
}

exception DbError
{
    1: i32 code;
    2: string message;
}

service SportDB
{
    list<Sport> getSportList() throws (1:DbError e)

    i32 createLeague(1:LeagueInfo lg) throws (1:DbError e)
    LeagueInfo getLeagueInfo(1:string name) throws (1:DbError e)
    void saveLeague(1:LeagueInfo lg) throws (1:DbError e)
    list<LeagueInfo> getLeagues(1:i32 page, 2:i32 size) throws (1:DbError e)
    League getLeague(1:string name, 2:bool tours) throws (1:DbError e)
    bool deleteLeague(1:string name) throws (1:DbError e)

    i32 addTeam(1:Team t) throws (1:DbError e)
    list<Team> getTeamList(1:i32 league) throws (1:DbError e)
    Team findTeam(1:string name) throws (1:DbError e)
    Team findTeamEx(1:string name) throws (1:DbError e)


    i32 addDiv(1:DivInfo d) throws (1:DbError e)
    list<DivInfo> getDivList(1:i32 league) throws (1:DbError e)
    DivInfo findDivInfo(1:string name) throws (1:DbError e)
    DivInfo findDivInfoEx(1:string name) throws (1:DbError e)

    void setTeamDiv(1:string team, 2:i32 dv, 3:i32 tr) throws (1:DbError e)

    string createTour(1:TourInfo tour) throws (1:DbError e)
    TourInfo findTourInfo(1:string id) throws (1:DbError e)

    Tour findTour(1:string id, 2:i32 games) throws (1:DbError e)

    i32 scheduleRoundRobin(1:ScheduleParams sch) throws (1:DbError e)
    i32 schedulePlayOff(1:ScheduleParams sch) throws (1:DbError e)

    i32 saveGames(1:list<Game> games) throws (1:DbError e)

    Division findDiv(1:string tour, 2:string name, 3:i32 games) throws (1:DbError e)

    list<TableRecord> getTable(1:i32 tour, 2:i32 div) throws (1:DbError e)

    void recalcTable(1:i32 tour, 2:i32 div) throws (1:DbError e)

    Counting getCounting(1:i32 tour) throws (1:DbError e)
    i32 getPlayOffStage(1:i32 tour, 2:i32 div) throws (1:DbError e)

    ScheduleParams suggestPlayOff(1:i32 tour, 2:i32 div, 3:i32 stage, 4:bool all) throws (1:DbError e)

    void finishTour(1:TourInfo tr) throws (1:DbError e)
    void cancelTour(1:TourInfo tr)  throws (1:DbError e)

    bool deleteDiv(1:string div)   throws (1:DbError e)

    void clearSchedule(1:i32 tr, 2:i32 div)   throws (1:DbError e)

    list<Game> getGames(1:i32 league, 2:i32 tr, 3:i32 limit, 4:i32 status, 5:bool asc, 6:bool full) throws (1:DbError e)

    list<LeagueInfo> getLeaguesList(1:list<i32> lgs, 2:string usr) throws (1:DbError e)

    void copyStructure(1:i32 tour) throws (1:DbError e)

    bool saveTeam(1:Team tm) throws (1:DbError e)
    bool saveDiv(1:DivInfo div) throws (1:DbError e)
    bool saveTour(1:TourInfo tr) throws (1:DbError e)

    bool deleteTeam(1:string tm) throws (1:DbError e)

    bool incRank(1:string div) throws (1:DbError e)

    list<Game> getTeamGames(1:string team, 2:string tour) throws (1:DbError e)

    i32 saveVenue(1:string league, 2:Venue v) throws (1:DbError e)
    list<Venue> getVenues(1:string league) throws (1:DbError e)
    bool deleteVenue(1:i32 id) throws (1:DbError e)
    
    Game getGame(1:i32 id) throws (1:DbError e)

    list<Game> getActiveGames(1:list<i32> lgids, 2:list<LeagueInfo> lgs, 3:list<TourInfo> tours) throws (1:DbError e)

    list<TourInfo> getActiveTours(1:list<i32> lgids, 2:list<LeagueInfo> lgs) throws (1:DbError e)

    LeagueInfo getLeagueInfoById(1:i32 id) throws (1:DbError e)
    TourInfo getTourInfo(1:i32 id) throws (1:DbError e)
    Team getTeamById(1:i32 id) throws (1:DbError e)

    i32 resetDates(1:TourInfo ti, 2:i32 divId) throws (1:DbError e)

    bool deleteGame(1:i32 id) throws (1:DbError e)

    Fan getFan(1:i32 usr, 2:i32 type, 3:i32 subj) throws (1:DbError e)

    bool addFan(1:Fan f) throws (1:DbError e)
    bool removeFan(1:Fan f) throws (1:DbError e)

    list<LeagueInfo> getFanLeagues(1:i32 usr) throws (1:DbError e);

    list<Team> getFanTeams(1:i32 usr) throws (1:DbError e);
        
    list<Player> getFans(1:i32 subj, 2:i32 type) throws (1:DbError e);

    Player getPlayer(1:i32 id) throws (1:DbError e);

    list<Team> getTeamListById(1:list<i32> tids) throws (1:DbError e)
}