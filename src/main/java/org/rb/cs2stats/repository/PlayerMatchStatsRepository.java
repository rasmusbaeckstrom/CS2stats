package org.rb.cs2stats.repository;

import org.rb.cs2stats.entity.PlayerMatchStats;
import org.springframework.data.repository.CrudRepository;

public interface PlayerMatchStatsRepository extends CrudRepository<PlayerMatchStats, Long> {
}
