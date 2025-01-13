CREATE TABLE Matches (
                         MatchID INT AUTO_INCREMENT PRIMARY KEY,
                         Map VARCHAR(50) NOT NULL
);

CREATE TABLE Players (
                         PlayerID INT AUTO_INCREMENT PRIMARY KEY,
                         SteamID VARCHAR(50) NOT NULL UNIQUE,
                         PlayerName VARCHAR(100) NOT NULL
);

CREATE TABLE PlayerStats (
                             StatID INT AUTO_INCREMENT PRIMARY KEY,
                             PlayerID INT NOT NULL,
                             MatchID INT NOT NULL,
                             Kills INT DEFAULT 0,
                             Deaths INT DEFAULT 0,
                             Assists INT DEFAULT 0,
                             Headshots INT DEFAULT 0,
                             MoneySpent INT DEFAULT 0,
                             FOREIGN KEY (PlayerID) REFERENCES Players(PlayerID),
                             FOREIGN KEY (MatchID) REFERENCES Matches(MatchID)
);

CREATE TABLE Events (
                        EventID INT AUTO_INCREMENT PRIMARY KEY,
                        MatchID INT NOT NULL,
                        PlayerID INT NOT NULL,
                        EventType VARCHAR(50),
                        Weapon VARCHAR(50),
                        TargetID INT,
                        EventTime DATETIME,
                        FOREIGN KEY (MatchID) REFERENCES Matches(MatchID),
                        FOREIGN KEY (PlayerID) REFERENCES Players(PlayerID),
                        FOREIGN KEY (TargetID) REFERENCES Players(PlayerID)
);
