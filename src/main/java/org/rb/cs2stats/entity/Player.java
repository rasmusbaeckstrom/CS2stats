package org.rb.cs2stats.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Objects;

@Entity
public class Player {
    @Id
    private String steamId;  // SteamID som primärnyckel
    private String name;

    public Player() {}

    public Player(String steamId, String name) {
        this.steamId = steamId;
        this.name = name;
    }

    // Getters, setters och equals/hashCode
    public String getSteamId() { return steamId; }
    public void setSteamId(String steamId) { this.steamId = steamId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(steamId, player.steamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(steamId);
    }
}
