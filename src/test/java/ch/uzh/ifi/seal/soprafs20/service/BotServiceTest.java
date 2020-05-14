package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.Difficulty;
import ch.uzh.ifi.seal.soprafs20.entity.Bot;
import ch.uzh.ifi.seal.soprafs20.repository.BotRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BotServiceTest {

    @Mock
    private BotRepository botRepository;

    @InjectMocks
    private BotService botService;

    private Bot testBot;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        // given
        testBot = new Bot();
        testBot.setId(1L);
        testBot.setBotName("NAME");
        testBot.setToken("TOKEN");
        testBot.setDifficulty(Difficulty.NEUTRAL);

        Mockito.when(botRepository.save(Mockito.any())).thenReturn(testBot);
        Mockito.when(botRepository.findByToken("TOKEN")).thenReturn(testBot);


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

        assertEquals(exception.getMessage(), "404 NOT_FOUND \"There is no Bot with requested Token\"");
    }
}











