package org.rb.cs2stats.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;

@Service
public class LogParserService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> parseLogFile(String filePath) throws IOException {
        List<Map<String, Object>> matches = new ArrayList<>();
        List<Map<String, Object>> players = new ArrayList<>();
        List<Map<String, Object>> events = new ArrayList<>();
        List<Map<String, Object>> playerStats = new ArrayList<>();

        Map<String, String> steamIdToName = new HashMap<>();
        int matchId = 1;
        String currentMap = null;

        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            // Match start och karta
            if (line.contains("World triggered \"Match_Start\"")) {
                Matcher mapMatcher = Pattern.compile("on \"([^\"]+)\"").matcher(line);
                if (mapMatcher.find()) {
                    currentMap = mapMatcher.group(1);
                    Map<String, Object> match = new HashMap<>();
                    match.put("MatchID", matchId++);
                    match.put("Map", currentMap);
                    matches.add(match);
                }
            }

            // Spelare kopplar upp sig
            if (line.contains("connected")) {
                Matcher steamIdMatcher = Pattern.compile("\\[U:1:(\\d+)]").matcher(line);
                Matcher nameMatcher = Pattern.compile("\"([^\"]+)<").matcher(line);
                if (steamIdMatcher.find() && nameMatcher.find()) {
                    String steamId = steamIdMatcher.group(1);
                    String playerName = nameMatcher.group(1);
                    steamIdToName.put(steamId, playerName);

                    Map<String, Object> player = new HashMap<>();
                    player.put("SteamID", steamId);
                    player.put("PlayerName", playerName);
                    players.add(player);
                }
            }

            // Kills, dödsfall och events
            if (line.contains("killed")) {
                Matcher killMatcher = Pattern.compile("\"([^\"]+)\".*killed \"([^\"]+)\".*with \"([^\"]+)\"").matcher(line);
                if (killMatcher.find()) {
                    String killer = killMatcher.group(1);
                    String victim = killMatcher.group(2);
                    String weapon = killMatcher.group(3);

                    Map<String, Object> event = new HashMap<>();
                    event.put("MatchID", matchId);
                    event.put("PlayerName", killer);
                    event.put("TargetName", victim);
                    event.put("Weapon", weapon);
                    event.put("EventType", "kill");
                    event.put("EventTime", line.substring(2, 21)); // Exempel: "07/06/2024 - 23:40:42"
                    events.add(event);
                }
            }

            // Player stats
            if (line.contains("round_stats") && line.contains("players")) {
                Matcher statsMatcher = Pattern.compile("\"player_\\d+\" : \"(.+)\"").matcher(line);
                if (statsMatcher.find()) {
                    String[] stats = statsMatcher.group(1).split(",");
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("PlayerID", stats[0].trim());
                    stat.put("Kills", Integer.parseInt(stats[3].trim()));
                    stat.put("Deaths", Integer.parseInt(stats[4].trim()));
                    stat.put("Assists", Integer.parseInt(stats[5].trim()));
                    stat.put("Headshots", Float.parseFloat(stats[7].trim()));
                    stat.put("MoneySpent", Integer.parseInt(stats[2].trim()));
                    playerStats.add(stat);
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("matches", matches);
        result.put("players", players);
        result.put("events", events);
        result.put("player_stats", playerStats);

        // Skriv JSON till fil
        objectMapper.writeValue(new File("cs2_parsed.json"), result);

        return result;
    }
}
