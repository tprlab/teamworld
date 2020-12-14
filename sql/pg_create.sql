

CREATE TABLE usr(id SERIAL PRIMARY KEY, 
    name VARCHAR(32) UNIQUE, 
    email VARCHAR(32) UNIQUE, 
    regged BIGINT DEFAULT 0, 
    status INT NOT NULL DEFAULT 1,
    enabled INT NOT NULL DEFAULT 1 ,
    pwd VARCHAR(512)
);

CREATE TABLE uperm(id SERIAL PRIMARY KEY, 
    otype VARCHAR(12),
    oid INT NOT NULL,
    perms INT DEFAULT 0,
    usr VARCHAR(32) NOT NULL,
    CONSTRAINT fk_perm_usr FOREIGN KEY (usr) REFERENCES usr(name)
);
  
CREATE TABLE user_roles (id SERIAL PRIMARY KEY,
  usr VARCHAR(32) NOT NULL,
  role VARCHAR(32) NOT NULL,
  UNIQUE (role, usr),

  CONSTRAINT fk_role_usr FOREIGN KEY (usr) REFERENCES usr (name)
);

create index usr_idx on usr(name);
create index usr_email_idx on usr(email);
create index usr_role_idx on user_roles(usr);


CREATE TABLE sport(id INTEGER PRIMARY KEY, 
    name TEXT, 
    popul INTEGER DEFAULT 0, 
    opp_mode INTEGER DEFAULT 0
);

CREATE TABLE counting(id INTEGER PRIMARY KEY, 
    name TEXT, 
    wins INTEGER DEFAULT 0,
    wot INTEGER DEFAULT 0,
    loss INTEGER DEFAULT 0,
    lot INTEGER DEFAULT 0,
    tie INTEGER DEFAULT 0,
    otAllowed INTEGER DEFAULT 0,
    tieAllowed INTEGER DEFAULT 0,
    resultOnly INTEGER DEFAULT 1
);

CREATE TABLE sport_counting(sport INT NOT NULL,
    counting INT NOT NULL,
    CONSTRAINT fk_sc_sp FOREIGN KEY(sport) REFERENCES sport(id) ON DELETE CASCADE,
    CONSTRAINT fk_sc_cnt FOREIGN KEY(counting) REFERENCES counting(id) ON DELETE CASCADE
);
    

CREATE TABLE league(id SERIAL PRIMARY KEY, 
    name VARCHAR(64), 
    uname VARCHAR(64) UNIQUE NOT NULL, 
    location VARCHAR(256),
    sport INTEGER NOT NULL,
    owner INTEGER NOT NULL,
    ownername VARCHAR(32) NOT NULL,
    created BIGINT DEFAULT 0, 
    CONSTRAINT fk_lg_sport FOREIGN KEY(sport) REFERENCES sport(id) ON DELETE CASCADE,
    CONSTRAINT fk_lg_owner FOREIGN KEY(owner) REFERENCES usr(id) ON DELETE CASCADE
) ;


CREATE TABLE team(id SERIAL PRIMARY KEY, 
    name VARCHAR(64), 
    uname VARCHAR(64) UNIQUE NOT NULL, 
    status INT DEFAULT 0,
    div_id INT DEFAULT 0,
    league INT NOT NULL,
    created BIGINT DEFAULT 0, 
    CONSTRAINT fk_tm_lg FOREIGN KEY(league) references league(id)
) ;

CREATE TABLE division(id SERIAL PRIMARY KEY, 
    name VARCHAR(64), 
    uname VARCHAR(64) UNIQUE NOT NULL, 
    status INT DEFAULT 0,
    league INT NOT NULL,
    created BIGINT DEFAULT 0, 
    stage INT DEFAULT 0,
    CONSTRAINT fk_div_lg FOREIGN KEY(league) references league(id)
) ;


CREATE TABLE tour(id SERIAL PRIMARY KEY, 
    name VARCHAR(64), 
    uname VARCHAR(64) UNIQUE NOT NULL, 
    status INT DEFAULT 0,
    league INT NOT NULL,
    started BIGINT DEFAULT 0, 
    finished BIGINT DEFAULT 0, 
    stage INT DEFAULT 0,
    created BIGINT DEFAULT 0, 
    CONSTRAINT fk_tr_lg FOREIGN KEY(league) references league(id)
) ;


CREATE TABLE game(id SERIAL PRIMARY KEY, 
        tour INTEGER NOT NULL,
        team1 INTEGER NOT NULL,
        team2 INTEGER NOT NULL,
        status INTEGER DEFAULT 0,
        scheduled BIGINT DEFAULT 0,
        score1 INTEGER DEFAULT 0,
        score2 INTEGER DEFAULT 0,
        details TEXT,
        overtime INTEGER DEFAULT 0,
        league INTEGER NOT NULL,
        division INTEGER DEFAULT 0,
        stage INTEGER DEFAULT 0,

        FOREIGN KEY(tour) REFERENCES tour(id) ON DELETE CASCADE,
        FOREIGN KEY(team1) REFERENCES team(id) ON DELETE CASCADE,
        FOREIGN KEY(team2) REFERENCES team(id) ON DELETE CASCADE,
        FOREIGN KEY(league) REFERENCES league(id) ON DELETE CASCADE
) ;


CREATE TABLE team_record(team INT NOT NULL,
    league INT NOT NULL,
    tour INT NOT NULL,
    div_id INT DEFAULT 0,
    points INT DEFAULT 0,
    games INT DEFAULT 0,
    wins INT DEFAULT 0,
    losses INT DEFAULT 0,
    wot INT DEFAULT 0,
    lot INT DEFAULT 0,
    ties INT DEFAULT 0,
    goal_for INT DEFAULT 0, 
    goal_against INT DEFAULT 0,
    goal_diff INT DEFAULT 0,
    place INT DEFAULT 0,
    version INT DEFAULT 0,
    FOREIGN KEY(tour) REFERENCES tour(id) ON DELETE CASCADE,
    FOREIGN KEY(team) REFERENCES team(id) ON DELETE CASCADE,
    FOREIGN KEY(league) REFERENCES league(id) ON DELETE CASCADE
) ;

CREATE SEQUENCE league_seq;
CREATE SEQUENCE team_seq;
CREATE SEQUENCE div_seq;
CREATE SEQUENCE tour_seq;
CREATE SEQUENCE game_seq;
CREATE SEQUENCE perm_seq;
CREATE SEQUENCE user_seq START 100;
CREATE SEQUENCE role_seq START 100;

