package org.rb.cs2stats.repository;

import org.rb.cs2stats.entity.GameMatch;
import org.springframework.data.repository.CrudRepository;

public interface GameMatchRepository extends CrudRepository<GameMatch, Long> {
}
