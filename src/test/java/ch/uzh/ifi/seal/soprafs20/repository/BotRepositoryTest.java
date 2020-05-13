package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.constant.Difficulty;
import ch.uzh.ifi.seal.soprafs20.entity.Bot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
class BotRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Qualifier("botRepository")
    @Autowired
    private BotRepository botRepository;

    private Bot bot;

    @BeforeEach
    void setup(){
        bot = new Bot();
        bot.setBotName("BotName");
        bot.setToken("1");
        bot.setAvatar(0);
        bot.setDifficulty(Difficulty.NEUTRAL);

        entityManager.persist(bot);
        entityManager.flush();
    }


    @Test
    public void findByBotName_success() {
        // when
        Bot found = botRepository.findByBotname(bot.getBotName());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getBotName(), bot.getBotName());
        assertEquals(found.getToken(), bot.getToken());
        assertEquals(found.getDifficulty(), bot.getDifficulty());
    }
    @Test
    public void findByBotName_failed() {
        // when
        Bot found = botRepository.findByBotname("invalid");

        // then
        assertNull(found);
    }
    @Test
    public void findByToken_success() {
        // when
        Bot found = botRepository.findByToken(bot.getToken());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getBotName(), bot.getBotName());
        assertEquals(found.getToken(), bot.getToken());
        assertEquals(found.getDifficulty(), bot.getDifficulty());
    }
    @Test
    public void findByToken_failed() {
        // when
        Bot found = botRepository.findByToken("invalid");

        // then
        assertNull(found);
    }
}