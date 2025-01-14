CREATE TABLE game_matches (
                              matchid INT AUTO_INCREMENT PRIMARY KEY,
                              map VARCHAR(50) NOT NULL
);

CREATE TABLE players (
                         playerid INT AUTO_INCREMENT PRIMARY KEY,
                         steamid VARCHAR(50) NOT NULL UNIQUE,
                         playername VARCHAR(100) NOT NULL
);

CREATE TABLE player_stats (
                              statid INT AUTO_INCREMENT PRIMARY KEY,
                              playerid INT NOT NULL,
                              matchid INT NOT NULL,
                              kills INT DEFAULT 0,
                              deaths INT DEFAULT 0,
                              assists INT DEFAULT 0,
                              headshots INT DEFAULT 0,
                              moneyspent INT DEFAULT 0,
                              FOREIGN KEY (playerid) REFERENCES players(playerid),
                              FOREIGN KEY (matchid) REFERENCES game_matches(matchid)
);

CREATE TABLE events (
                        eventid INT AUTO_INCREMENT PRIMARY KEY,
                        matchid INT NOT NULL,
                        playerid INT NOT NULL,
                        eventtype VARCHAR(50),
                        weapon VARCHAR(50),
                        targetid INT,
                        eventtime DATETIME,
                        FOREIGN KEY (matchid) REFERENCES game_matches(matchid),
                        FOREIGN KEY (playerid) REFERENCES players(playerid),
                        FOREIGN KEY (targetid) REFERENCES players(playerid)
);