package org.rb.cs2stats.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GameMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mapName;
    private String serverName;
    private String winningTeam;

    private LocalDateTime createdAt;

    private Integer scoreCT;
    private Integer scoreT;

    @OneToMany(mappedBy = "gameMatch", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PlayerMatchStats> playerStats = new ArrayList<>();

    // Constructors
    public GameMatch() {}

    // Getters and Setters
    public String getWinningTeam() {
        return winningTeam;
    }

    public void setWinningTeam(String winningTeam) {
        this.winningTeam = winningTeam;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getScoreCT() {
        return scoreCT;
    }

    public void setScoreCT(Integer scoreCT) {
        this.scoreCT = scoreCT;
    }

    public Integer getScoreT() {
        return scoreT;
    }

    public void setScoreT(Integer scoreT) {
        this.scoreT = scoreT;
    }

    public List<PlayerMatchStats> getPlayerStats() {
        return playerStats;
    }

    public void setPlayerStats(List<PlayerMatchStats> playerStats) {
        this.playerStats = playerStats;
    }

    // Utility methods
    public void addPlayerStats(PlayerMatchStats stats) {
        playerStats.add(stats);
        stats.setGameMatch(this);
    }

    public void removePlayerStats(PlayerMatchStats stats) {
        playerStats.remove(stats);
        stats.setGameMatch(null);
    }

    @Override
    public String toString() {
        return "GameMatch{" +
                "id=" + id +
                ", mapName='" + mapName + '\'' +
                ", serverName='" + serverName + '\'' +
                ", createdAt=" + createdAt +
                ", scoreCT=" + scoreCT +
                ", scoreT=" + scoreT +
                '}';
    }
}
