package org.rb.cs2stats.controller;

import org.rb.cs2stats.entity.GameMatch;
import org.rb.cs2stats.entity.Player;
import org.rb.cs2stats.entity.PlayerStats;
import org.rb.cs2stats.repository.GameMatchRepository;
import org.rb.cs2stats.repository.PlayerRepository;
import org.rb.cs2stats.repository.PlayerStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DataController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameMatchRepository gameMatchRepository;

    @Autowired
    private PlayerStatsRepository playerStatsRepository;

    @GetMapping("/players")
    public List<Player> getAllPlayers() {
        return (List<Player>) playerRepository.findAll();
    }

    @GetMapping("/matches")
    public List<GameMatch> getAllMatches() {
        return (List<GameMatch>) gameMatchRepository.findAll();
    }

    @GetMapping("/player-stats")
    public List<PlayerStats> getAllPlayerStats() {
        return playerStatsRepository.findAll();
    }
}