package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.entity.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("botRepository")
public interface BotRepository extends JpaRepository<Bot, Long> {
    Bot findByBotname(String botname);
    Bot findByToken(String token);
}