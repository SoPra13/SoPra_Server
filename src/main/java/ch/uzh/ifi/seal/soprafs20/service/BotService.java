package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.Difficulty;
import ch.uzh.ifi.seal.soprafs20.entity.Bot;
import ch.uzh.ifi.seal.soprafs20.repository.BotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */

@Service
@Transactional
public class BotService {
    private static final int BOT_AVATARS = 3;

    private final Logger log = LoggerFactory.getLogger(BotService.class);

    private final BotRepository botRepository;


    @Autowired
    public BotService(@Qualifier("botRepository") BotRepository botRepository) {
        this.botRepository = botRepository;

    }


    public Bot createBot(String difficulty) {
        String msg;
        List<String> names;
        try {
            names = Files.readAllLines(Paths.get("src/Bot_Names.txt"));
        }
        catch (IOException e) {
            msg = e.getMessage();
            log.error(msg);
            names = Collections.singletonList("Fritz");
        }

        var bot = new Bot();
        bot.setToken(UUID.randomUUID().toString());
        bot.setBotName(String.valueOf(names.get(new SecureRandom().nextInt(names.size()))));
        msg = "bot name found: " + bot.getBotName();
        log.info(msg);
        Difficulty actualDifficulty;
        try {
            actualDifficulty = Difficulty.valueOf(difficulty);
        }
        catch (IllegalArgumentException e) {
            msg = e.getMessage();
            log.error(msg);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "difficulty not found");
        }
        bot.setAvatar(BOT_AVATARS);
        bot.setDifficulty(actualDifficulty);

        // saves the given entity but data is only persisted in the database once flush() is called
        botRepository.save(bot);
        botRepository.flush();

        return bot;
    }


    //get bot from token
    public Bot getBotFromToken(String token) {
        Bot botByToken = botRepository.findByToken(token);

        String baseErrorMessage = "There is no Bot with requested Token";
        if (botByToken == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, baseErrorMessage);
        }
        return botByToken;
    }

    public void deleteBot(String token) {
        Bot botByToken = botRepository.findByToken(token);
        botRepository.delete(botByToken);
    }

    public String botClue(Difficulty difficulty, String topic) {
        String msg;

        log.info("topic for datamuse:");
        log.info(topic);
        msg = difficulty.toString();
        log.info(msg);
        log.info("type");
        String clue;
        if (difficulty == Difficulty.FRIEND || (difficulty == Difficulty.NEUTRAL && new SecureRandom().nextBoolean())) {
            clue = WordService.getGoodWord(topic);
            msg = "good clue: " + clue;
            log.debug("test");
            log.info(msg);
        }
        else {
            clue = WordService.getBadWord(topic);
            msg = "bad clue: " + clue;
            log.info(msg);
        }
        return clue;
    }

    public void leaveGame(Bot bot) {
        bot.setGame(null);
    }

}
