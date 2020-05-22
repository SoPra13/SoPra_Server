package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.Difficulty;
import ch.uzh.ifi.seal.soprafs20.entity.Bot;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.repository.BotRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
@Transactional
 class BotServiceIntegrationTest {

    @Qualifier("botRepository")
    @Autowired
    private BotRepository botRepository;

    @Autowired
    private BotService botService;

    private Bot testBot;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        // given
        testBot = botService.createBot("NEUTRAL");
    }

    @AfterEach
    void clear() {
        botRepository.deleteAll();
    }

    @Test
    void createBot_success() {
        Bot neutralBot = botService.createBot("NEUTRAL");
        Bot friendBot = botService.createBot("FRIEND");
        Bot hostileBot = botService.createBot("HOSTILE");

        assertEquals(Difficulty.NEUTRAL, neutralBot.getDifficulty());
        assertEquals(Difficulty.FRIEND, friendBot.getDifficulty());
        assertEquals(Difficulty.HOSTILE, hostileBot.getDifficulty());
    }

    @Test
    void createBot_invalid_String_difficulty() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                botService.createBot("INVALID_DIFFICULTY"));
    }

    @Test
    void createBot_invalid_partial_difficulty() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                botService.createBot("HOST"));
    }

    @Test
    void getBotFromToken_success() {
        Bot fetchedBot = botService.getBotFromToken(testBot.getToken());

        assertEquals(fetchedBot.getId(), testBot.getId());
        assertEquals(fetchedBot.getToken(), testBot.getToken());
    }

    @Test
    void getBotFromToken_invalid_Token() {
        Exception exception = Assertions.assertThrows(ResponseStatusException.class, () ->
                botService.getBotFromToken("INVALID_TOKEN"));

        assertEquals("404 NOT_FOUND \"There is no Bot with requested Token\"", exception.getMessage());
    }

    @Test
    void deleteBot() {
        botService.deleteBot(testBot.getToken());

        assertNull(botRepository.findByToken(testBot.getToken()));
    }

    @Test
    void botClue_FRIEND() {

        String clue = botService.botClue(Difficulty.FRIEND, "topic");

        assertNotNull(clue);
    }

    @Test
    void botClue_HOSTILE() {

        String clue = botService.botClue(Difficulty.HOSTILE, "topic");

        assertNotNull(clue);
    }

    @Test
    void leaveGame() {
        testBot.setGame(new Game());

        botService.leaveGame(testBot);

        assertNull(testBot.getGame());
    }

    @Test
     void getAvatar() {

        int avatar = testBot.getAvatar();

        assertNotNull(avatar);
    }
}











