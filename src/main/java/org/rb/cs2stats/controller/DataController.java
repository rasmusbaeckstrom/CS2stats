//package org.rb.cs2stats.controller;
//
//import org.rb.cs2stats.entity.GameMatch;
//import org.rb.cs2stats.entity.Player;
//import org.rb.cs2stats.repository.GameMatchRepository;
//import org.rb.cs2stats.repository.PlayerRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api")
//public class DataController {
//
//    @Autowired
//    private GameMatchRepository gameMatchRepository;
//
//    @Autowired
//    private PlayerRepository playerRepository;
//
//    @GetMapping("/matches")
//    public List<GameMatch> getAllMatches() {
//        return gameMatchRepository.findAll();
//    }
//
//    @GetMapping("/players")
//    public List<Player> getAllPlayers() {
//        return playerRepository.findAll();
//    }
//}