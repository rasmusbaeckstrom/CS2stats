package org.rb.cs2stats.entity;

import jakarta.persistence.*;

@Entity
public class PlayerMatchStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Player player;

    @ManyToOne
    private GameMatch gameMatch;

    private String team; // Ny kolumn för att lagra laget

    // Getters och Setters
    public Long getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public GameMatch getGameMatch() {
        return gameMatch;
    }

    public void setGameMatch(GameMatch gameMatch) {
        this.gameMatch = gameMatch;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }
}

