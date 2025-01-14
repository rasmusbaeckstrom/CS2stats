package org.rb.cs2stats.service;

import org.rb.cs2stats.entity.*;
import org.rb.cs2stats.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LogParserService {

    @Autowired
    private GameMatchRepository gameMatchRepository;

    @Autowired
    private PlayerRepository playerRepository;

    private static final Logger logger = Logger.getLogger(LogParserService.class.getName());
    private static final int MAX_ROUNDS_TO_WIN = 13;

    private boolean isHalftime = false;  // Spårar om sidbyte har inträffat
    private int roundCounter = 0;

    public void parseLogFile(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());
        GameMatch gameMatch = new GameMatch();
        Round currentRound = null;

        for (String line : lines) {
            if (line.contains("Match_Start")) {
                resetMatchState(gameMatch);
                continue;
            }

            if (ignoreWarmupOrEmptyLines(line)) continue;

            handleMapAndServerInfo(line, gameMatch);
            handlePlayerConnectionAndEvents(line);

            if (line.contains("Round_Start")) {
                roundCounter++;
                currentRound = startNewRound(gameMatch);
                logger.info("New round started: " + roundCounter);
            }

            if (line.contains("Round_End") && currentRound != null) {
                endRound(currentRound, gameMatch);
            }

            if (line.contains("MatchStatus: Score:")) {
                int scoreT = safeParseInt(extractField(line, "Score: (\\d+):"), 0);
                int scoreCT = safeParseInt(extractField(line, "Score: \\d+:(\\d+)"), 0);

                if (isHalftime) {
                    logger.info("Applying side switch logic to MatchStatus scores.");
                    int temp = scoreCT;
                    scoreCT = scoreT;
                    scoreT = temp;
                }

                if (currentRound != null) {
                    currentRound.setScoreT(scoreT);
                    currentRound.setScoreCT(scoreCT);
                    logger.info("Updated round score from MatchStatus: T-" + scoreT + ", CT-" + scoreCT);
                }
            }

            if (line.contains("Halftime")) {
                isHalftime = true;
                logger.info("Halftime reached - teams switched sides.");
            }

            if (line.contains("Game Over")) {
                saveMatchIfValid(gameMatch);
                break;
            }
        }

        saveMatchIfValid(gameMatch);
    }

    private void resetMatchState(GameMatch gameMatch) {
        if (!gameMatch.getRounds().isEmpty()) {
            saveMatchIfValid(gameMatch);
        }
        isHalftime = false;
        roundCounter = 0;
        logger.info("New match started.");
    }

    private boolean ignoreWarmupOrEmptyLines(String line) {
        return line.contains("Warmup") || line.trim().isEmpty() || line.contains("Round_Restart");
    }

    private void handleMapAndServerInfo(String line, GameMatch gameMatch) {
        if (line.contains("\"map\"")) {
            gameMatch.setMapName(extractField(line, "\"map\"\\s*:\\s*\"(.*?)\""));
        }
        if (line.contains("server_cvar")) {
            gameMatch.setServerName(extractField(line, "server_cvar:\\s*\"(.*?)\""));
        }
    }

    private void handlePlayerConnectionAndEvents(String line) {
        if (line.contains("SourceTV") || line.contains("<Spectator>")) {
            logger.info("Ignoring spectator or SourceTV connection.");
            return;
        }

        String steamId = extractField(line, "\\[U:1:(\\d+)]");
        String playerName = extractField(line, "\"(.+?)<\\d+><\\[U");

        if (steamId != null && playerName != null) {
            if (!playerRepository.existsById(steamId)) {
                playerRepository.save(new Player(steamId, playerName));
                logger.info("New player saved: " + playerName + " [" + steamId + "]");
            }
        }
    }

    private Round startNewRound(GameMatch gameMatch) {
        Round round = new Round();
        round.setRoundNumber(roundCounter);
        round.setGameMatch(gameMatch);
        return round;
    }

    private void endRound(Round round, GameMatch gameMatch) {
        gameMatch.getRounds().add(round);
        logger.info("Round " + round.getRoundNumber() + " ended with score: T-" + round.getScoreT() + ", CT-" + round.getScoreCT());

        if (isMatchOver(round.getScoreT(), round.getScoreCT())) {
            logger.info("Match over detected at round " + round.getRoundNumber());
            saveMatchIfValid(gameMatch);
        }
    }

    private void saveMatchIfValid(GameMatch gameMatch) {
        if (!gameMatch.getRounds().isEmpty()) {
            gameMatchRepository.save(gameMatch);
            logger.info("Match saved with " + gameMatch.getRounds().size() + " rounds.");
        } else {
            logger.warning("No rounds found for match.");
        }
    }

    private String extractField(String line, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        return matcher.find() ? matcher.group(1) : null;
    }

    private int safeParseInt(String str, int defaultValue) {
        try {
            return str != null ? Integer.parseInt(str) : defaultValue;
        } catch (NumberFormatException e) {
            logger.warning("Failed to parse int from string: " + str);
            return defaultValue;
        }
    }

    private boolean isMatchOver(int scoreT, int scoreCT) {
        return scoreT >= MAX_ROUNDS_TO_WIN || scoreCT >= MAX_ROUNDS_TO_WIN;
    }
}
