package org.rb.cs2stats.entity;

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
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "gameMatch", cascade = CascadeType.ALL)
    private List<Round> rounds = new ArrayList<>();

    public GameMatch() {}

    public GameMatch(String mapName, String serverName) {
        this.mapName = mapName;
        this.serverName = serverName;
    }

    // Getters och setters
    public Long getId() { return id; }
    public String getMapName() { return mapName; }
    public void setMapName(String mapName) { this.mapName = mapName; }

    public String getServerName() { return serverName; }
    public void setServerName(String serverName) { this.serverName = serverName; }

    public List<Round> getRounds() { return rounds; }
    public void setRounds(List<Round> rounds) { this.rounds = rounds; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void setStartTime(LocalDateTime now) {
    }
}
