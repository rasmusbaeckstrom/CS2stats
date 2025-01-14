package org.rb.cs2stats.controller;

import org.rb.cs2stats.entity.GameMatch;
import org.rb.cs2stats.repository.GameMatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameMatchController {

    @Autowired
    private GameMatchRepository gameMatchRepository;

    @GetMapping
    public List<GameMatch> getAllMatches() {
        return gameMatchRepository.findAll();
    }
}

