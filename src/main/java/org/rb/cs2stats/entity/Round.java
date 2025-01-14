package org.rb.cs2stats.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int roundNumber;
    private int scoreT;
    private int scoreCT;

    @ManyToOne
    @JoinColumn(name = "game_match_id")
    private GameMatch gameMatch;

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL)
    private List<RoundsPlayer> roundsPlayers = new ArrayList<>();

    public Round() {}

    public Round(int roundNumber, int scoreT, int scoreCT) {
        this.roundNumber = roundNumber;
        this.scoreT = scoreT;
        this.scoreCT = scoreCT;
    }

    // Getters och setters
    public Long getId() { return id; }
    public int getRoundNumber() { return roundNumber; }
    public void setRoundNumber(int roundNumber) { this.roundNumber = roundNumber; }

    public int getScoreT() { return scoreT; }
    public void setScoreT(int scoreT) { this.scoreT = scoreT; }

    public int getScoreCT() { return scoreCT; }
    public void setScoreCT(int scoreCT) { this.scoreCT = scoreCT; }

    public GameMatch getGameMatch() { return gameMatch; }
    public void setGameMatch(GameMatch gameMatch) { this.gameMatch = gameMatch; }

    public List<RoundsPlayer> getRoundsPlayers() { return roundsPlayers; }
    public void setRoundsPlayers(List<RoundsPlayer> roundsPlayers) { this.roundsPlayers = roundsPlayers; }
}
