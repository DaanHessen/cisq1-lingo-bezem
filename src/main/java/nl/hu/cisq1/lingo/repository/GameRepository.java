package nl.hu.cisq1.lingo.repository;

import nl.hu.cisq1.lingo.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {
    List<Game> findTop10ByOrderByScoreDesc();
}
