package org.rb.cs2stats.repository;

import org.rb.cs2stats.entity.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, String> {
}
