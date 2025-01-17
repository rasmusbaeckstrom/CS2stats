package org.rb.cs2stats.service;

import org.rb.cs2stats.entity.GameMatch;
import org.rb.cs2stats.entity.PlayerMatchStats;
import org.rb.cs2stats.entity.Player;
import org.rb.cs2stats.repository.GameMatchRepository;
import org.rb.cs2stats.repository.PlayerMatchStatsRepository;
import org.rb.cs2stats.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LogParserService {

    @Autowired
    private GameMatchRepository gameMatchRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerMatchStatsRepository playerMatchStatsRepository;

    private static final Pattern PLAYER_JOIN_PATTERN = Pattern.compile("\"(.*?)<\\d+><\\[U:1:(\\d+)]><>\" entered the game");
    private static final Pattern GAME_OVER_PATTERN = Pattern.compile("Game Over: .* score (\\d+):(\\d+) after .*");
    private static final Pattern MATCH_STATUS_PATTERN = Pattern.compile("MatchStatus: Score: (\\d+):(\\d+) on map \\\"(.*?)\\\"");
    private static final Pattern SERVER_NAME_PATTERN = Pattern.compile("\"server\" : \"(.*?)\"");
    private static final Pattern LOG_START_PATTERN = Pattern.compile("Log file started \\(file \\\"logs/(\\d{4}_\\d{2}_\\d{2}_\\d{6})_\\d+\\.log\\\"");
    private static final Pattern TEAM_ASSIGNMENT_PATTERN = Pattern.compile("\".*?<\\d+><\\[U:1:(\\d+)]><(TERRORIST|CT)>\"");

    private GameMatch currentMatch;
    private Set<String> currentPlayers = new HashSet<>();
    private Map<String, String> playerTeamMap = new HashMap<>();
    private LocalDateTime logStartDate;

    @Transactional
    public void parseLogFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isLogStartProcessed = false;

            while ((line = br.readLine()) != null) {
                if (!isLogStartProcessed && line.contains("Log file started")) {
                    extractLogStartDate(line);
                    isLogStartProcessed = true;
                }
                processLine(line, filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extractLogStartDate(String line) {
        Matcher matcher = LOG_START_PATTERN.matcher(line);
        if (matcher.find()) {
            String logFileTimestamp = matcher.group(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HHmmss");
            try {
                logStartDate = LocalDateTime.parse(logFileTimestamp, formatter);
            } catch (Exception e) {
                e.printStackTrace();
                logStartDate = LocalDateTime.now(); // Fallback to current time
            }
        } else {
            logStartDate = LocalDateTime.now(); // Fallback
        }
    }

    private void processLine(String line, String filePath) {
        if (line.contains("entered the game")) {
            processPlayerJoin(line);
        } else if (line.contains("MatchStatus: Score:")) {
            processMatchStatus(line);
        } else if (line.contains("Game Over")) {
            processGameOver(line, filePath);
        } else {
            updatePlayerTeam(line);
        }
    }

    private void updatePlayerTeam(String line) {
        Matcher matcher = TEAM_ASSIGNMENT_PATTERN.matcher(line);
        if (matcher.find()) {
            String steamId = matcher.group(1);
            String team = matcher.group(2);
            playerTeamMap.put(steamId, team);
        }
    }

    private void processPlayerJoin(String line) {
        Matcher matcher = PLAYER_JOIN_PATTERN.matcher(line);
        if (matcher.find()) {
            String playerName = matcher.group(1);
            String steamId = matcher.group(2);

            Player player = playerRepository.findById(steamId)
                    .orElseGet(() -> {
                        Player newPlayer = new Player();
                        newPlayer.setSteamId(steamId);
                        newPlayer.setName(playerName);
                        playerRepository.save(newPlayer);
                        return newPlayer;
                    });

            if (!player.getName().equals(playerName)) {
                player.setName(playerName);
                playerRepository.save(player);
            }

            currentPlayers.add(steamId);
        }
    }

    private void processMatchStatus(String line) {
        Matcher matcher = MATCH_STATUS_PATTERN.matcher(line);
        if (matcher.find()) {
            int scoreCT = Integer.parseInt(matcher.group(1));
            int scoreT = Integer.parseInt(matcher.group(2));
            String mapName = matcher.group(3);

            if (currentMatch == null) {
                currentMatch = new GameMatch();
                currentMatch.setCreatedAt(logStartDate != null ? logStartDate : LocalDateTime.now());
            }

            currentMatch.setMapName(mapName);
            currentMatch.setScoreCT(scoreCT);
            currentMatch.setScoreT(scoreT);
        }
    }

    private void processGameOver(String line, String filePath) {
        Matcher matcher = GAME_OVER_PATTERN.matcher(line);
        if (matcher.find() && currentMatch != null) {
            int finalScoreCT = Integer.parseInt(matcher.group(1));
            int finalScoreT = Integer.parseInt(matcher.group(2));

            currentMatch.setScoreCT(finalScoreCT);
            currentMatch.setScoreT(finalScoreT);
            currentMatch.setServerName(extractServerName(filePath));
            currentMatch.setCreatedAt(logStartDate != null ? logStartDate : LocalDateTime.now());

            String winningTeam = (finalScoreT > finalScoreCT) ? "TERRORIST" : "CT";
            currentMatch.setWinningTeam(winningTeam);

            gameMatchRepository.save(currentMatch);

            assignTeamsAndResults(winningTeam);

            currentMatch = null;
            currentPlayers.clear();
        }
    }

    private void assignTeamsAndResults(String winningTeam) {
        for (String steamId : currentPlayers) {
            Player player = playerRepository.findById(steamId)
                    .orElseThrow(() -> new IllegalArgumentException("Player with SteamID " + steamId + " not found"));

            PlayerMatchStats matchStats = new PlayerMatchStats();
            matchStats.setGameMatch(currentMatch);
            matchStats.setPlayer(player);

            String playerTeam = playerTeamMap.getOrDefault(steamId, "UNKNOWN");
            matchStats.setTeam(playerTeam.equals(winningTeam) ? "WINNING" : "LOSING");

            playerMatchStatsRepository.save(matchStats);
        }
    }

    private String extractServerName(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = SERVER_NAME_PATTERN.matcher(line);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown Server";
    }
}
