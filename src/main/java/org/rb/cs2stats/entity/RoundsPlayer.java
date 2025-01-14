package org.rb.cs2stats.entity;

import jakarta.persistence.*;

@Entity
public class RoundsPlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "round_id")
    private Round round;

    @ManyToOne
    @JoinColumn(name = "steam_id")
    private Player player;

    private String team; // T eller CT
    private int kills;
    private int deaths;
    private int assists;
    private int money;

    public RoundsPlayer() {}

    public RoundsPlayer(String team, int kills, int deaths, int assists, int money) {
        this.team = team;
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
        this.money = money;
    }

    // Getters och setters
    public Long getId() { return id; }
    public Round getRound() { return round; }
    public void setRound(Round round) { this.round = round; }

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }

    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }

    public int getKills() { return kills; }
    public void setKills(int kills) { this.kills = kills; }

    public int getDeaths() { return deaths; }
    public void setDeaths(int deaths) { this.deaths = deaths; }

    public int getAssists() { return assists; }
    public void setAssists(int assists) { this.assists = assists; }

    public int getMoney() { return money; }
    public void setMoney(int money) { this.money = money; }
}
