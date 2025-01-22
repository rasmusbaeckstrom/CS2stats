package org.rb.cs2stats.repository;

import org.rb.cs2stats.entity.GameMatch;
import org.rb.cs2stats.entity.Player;
import org.rb.cs2stats.entity.PlayerStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerStatsRepository extends JpaRepository<PlayerStats, Long> {
    Optional<PlayerStats> findByPlayerAndGameMatch(Player player, GameMatch currentMatch);
}
