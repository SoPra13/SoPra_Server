package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.constant.Difficulty;
import ch.uzh.ifi.seal.soprafs20.entity.Bot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BotRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Qualifier("botRepository")
    @Autowired
    private BotRepository botRepository;


    @Test
    public void findByBotname_success() {
        // given
        Bot bot = new Bot();
        bot.setBotname("BotName");
        bot.setToken("1");
        bot.setDifficulty(Difficulty.NEUTRAL);
        bot.setColor(Color.getColor("green"));

        entityManager.persist(bot);
        entityManager.flush();

        // when
        Bot found = botRepository.findByBotname(bot.getBotname());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getBotname(), bot.getBotname());
        assertEquals(found.getToken(), bot.getToken());
        assertEquals(found.getDifficulty(), bot.getDifficulty());
        assertEquals(found.getColor(), bot.getColor());
    }

    @Test
    public void findByBotname_failed() {
        // given
        Bot bot = new Bot();
        bot.setBotname("BotName");
        bot.setToken("1");
        bot.setDifficulty(Difficulty.NEUTRAL);
        bot.setColor(Color.getColor("green"));

        entityManager.persist(bot);
        entityManager.flush();

        // when
        Bot found = botRepository.findByBotname("invalid");

        // then
        assertNull(found);
    }

    @Test
    public void findByToken_success() {
        // given
        Bot bot = new Bot();
        bot.setBotname("BotName");
        bot.setToken("1");
        bot.setDifficulty(Difficulty.NEUTRAL);
        bot.setColor(Color.getColor("green"));

        entityManager.persist(bot);
        entityManager.flush();

        // when
        Bot found = botRepository.findByToken(bot.getToken());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getBotname(), bot.getBotname());
        assertEquals(found.getToken(), bot.getToken());
        assertEquals(found.getDifficulty(), bot.getDifficulty());
        assertEquals(found.getColor(), bot.getColor());
    }

    @Test
    public void findByToken_failed() {
        // given
        Bot bot = new Bot();
        bot.setBotname("BotName");
        bot.setToken("1");
        bot.setDifficulty(Difficulty.NEUTRAL);
        bot.setColor(Color.getColor("green"));

        entityManager.persist(bot);
        entityManager.flush();

        // when
        Bot found = botRepository.findByToken("invalid");

        // then
        assertNull(found);
    }
}