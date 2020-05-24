package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.entity.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class GameRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    private Game game;

    @BeforeEach
    void setup() {
        // given
        game = new Game();
        game.setVersion(0);
        game.setToken("1");
        game.setCurrentRound(0);
        game.setGuesser(0);

        entityManager.persist(game);
        entityManager.flush();
    }

    @Test
    void findByToken_success() {
        // when
        Game found = gameRepository.findByToken(game.getToken());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getVersion(), game.getVersion());
        assertEquals(found.getToken(), game.getToken());
        assertEquals(found.getCurrentRound(), game.getCurrentRound());
        assertEquals(found.getGuesser(), game.getGuesser());
    }

    @Test
    void findByToken_failed() {
        // when
        Game found = gameRepository.findByToken("invalid");

        // then
        assertNull(found);
    }
}