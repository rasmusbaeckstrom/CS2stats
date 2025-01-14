package org.rb.cs2stats.repository;

import org.rb.cs2stats.entity.RoundsPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoundsPlayerRepository extends JpaRepository<RoundsPlayer, Long> {}
