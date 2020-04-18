package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.Difficulty;
import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Bot;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.BotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    private final Logger log = LoggerFactory.getLogger(BotService.class);

    private final BotRepository botRepository;

    @Autowired
    public BotService(@Qualifier("botRepository") BotRepository botRepository) {
        this.botRepository = botRepository;
    }


    public Bot createBot(String difficulty) {
        var bot = new Bot();
        bot.setToken(UUID.randomUUID().toString());
        bot.setBotname("abc");
        Difficulty actualDifficulty = Difficulty.valueOf(difficulty);
        bot.setColor(null);
        bot.setDifficulty(actualDifficulty);

        // saves the given entity but data is only persisted in the database once flush() is called
        botRepository.save(bot);
        botRepository.flush();

        log.debug("Created Information for Bot: {}", bot);
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

}