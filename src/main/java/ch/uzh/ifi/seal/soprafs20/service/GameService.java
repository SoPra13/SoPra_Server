package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.LobbyStatus;
import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Bot;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.helpers.WordFileHandler;
import ch.uzh.ifi.seal.soprafs20.repository.BotRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class GameService {

    private static final String SANITIZER = "[\n|\r|\t]";
    private static final String CENSORED = "CENSORED";

    private final UserService userService;
    private final BotService botService;
    private final LobbyService lobbyService;

    private final Logger log = LoggerFactory.getLogger(GameService.class);
    private final GameRepository gameRepository;
    private final BotRepository botRepository;


    @Autowired
    public GameService(UserService userService,
                       ChatService chatService,
                       BotService botService,
                       LobbyService lobbyService,
                       @Qualifier("gameRepository") GameRepository gameRepository,
                       @Qualifier("botRepository") BotRepository botRepository) {
        this.userService = userService;
        this.botService = botService;
        this.lobbyService = lobbyService;
        this.gameRepository = gameRepository;
        this.botRepository = botRepository;
    }


    //return all games, for testing only
    public List<Game> getGames() {
        return this.gameRepository.findAll();
    }

    //Get Game from Token
    public Game getGameFromToken(String token) {

        String baseErrorMessage = "No matching Game found";
        //check if exists
        Game gameByToken = this.gameRepository.findByToken(token);
        if (gameByToken == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, baseErrorMessage);
        }
        return gameByToken;
    }


    //set player as ready, called when unity loaded
    public void setPlayerReady(String gameToken, String userToken) {

        String baseErrorMessage = "Could not set player ready";
        User user = userService.getUserFromToken(userToken);

        if (user.getGame().getToken().equals(gameToken)) {
            user.setUnityReady(true);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, baseErrorMessage);
        }

    }


    //create new Game based on Lobby
    public Game createGame(Lobby lobby) {
        String msg;

        Game newGame = new Game();

        List<User> userList = new ArrayList<>(lobby.getPlayerList());
        List<Bot> botList = new ArrayList<>(lobby.getBotList());

        newGame.setBotList(botList);
        newGame.setBotsClueGiven(false);
        newGame.setBotsVoted(false);
        newGame.setPlayerList(userList);
        newGame.setToken(lobby.getLobbyToken());
        newGame.setCurrentRound(0);
        newGame.setVersion(1);
        newGame.setGuess(null);
        newGame.getClueList().clear();
        newGame.setVoteList(new ArrayList<>(Collections.nCopies(5, 0)));
        newGame.getChecklist().clear();
        newGame.setGuessGiven(null);
        newGame.setGuessCorrect(null);
        newGame.setGuesser(new SecureRandom().nextInt(newGame.getPlayerList().size()));
        newGame.setMysteryWords(WordFileHandler.getMysteryWords());

        // saves the given entity but data is only persisted in the database once flush() is called
        newGame = gameRepository.save(newGame);
        gameRepository.flush();

        //update game of user and add initial position
        for (User user : newGame.getPlayerList()) {
            user.setGame(newGame);
            log.info("added User to game;");
            msg = user.getId().toString();
            log.info(msg);
        }

        //update game of bot
        for (Bot bot : newGame.getBotList()) {
            bot.setGame(newGame);
            log.info("added vot to game;");
            msg = bot.getId().toString();
            log.info(msg);
        }


        log.debug("Created Information for Game: {}", newGame);
        return newGame;
    }


    //vote for topic, adds one to index of topic voted for
    public Game addVote(String gameToken, String userToken, int vote) {
        if (vote > 5) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "int has to be <=5");

        Game game = getGameFromToken(gameToken);

        User user = userService.getUserFromToken(userToken);
        game.setGuessCorrect(null);

        if (Boolean.FALSE.equals(game.getBotsVoted())) {
            game.setBotsVoted(true);
            for (int i = 0; i < game.getBotList().size(); i++) {
                int botVote = new SecureRandom().nextInt(4);
                game.getVoteList().set(botVote, game.getVoteList().get(botVote) + 1);
            }
        }
        //user votes for 5 if not voted in time, vote gets ignored
        if (vote != 5) {
            game.getVoteList().set(vote, game.getVoteList().get(vote) + 1);
        }
        user.setVoted(true);
        return game;
    }

    //set Topic of game after Voting
    public Game setTopic(String gameToken, String topic) {
        Game game = getGameFromToken(gameToken);
        game.setGuessCorrect(null);
        game.setTopic(topic.toLowerCase());
        return game;
    }

    public void checkAllPlayersAreConnected(String gameToken) {
        log.info("Checking if all players are connected:");
        Game game = getGameFromToken(gameToken);
        Lobby lobby = lobbyService.getLobbyFromToken(gameToken);
        if (game != null) {
            for (int i = 0; i<game.getPlayerList().size();i++) {
                User user = game.getPlayerList().get(i);
                if (!user.isInGameTab()) {
                    log.info("Found: {} has disconnected.", user.getUsername());
                    removeUser(user.getToken(), gameToken);
                    lobby.removePlayer(user);
                    userService.leaveLobby(user);
                    user.setStatus(UserStatus.OFFLINE);
                }
            }
            log.info("All players checked.");
        }
    }

    //remove user during game
    public void removeUser(String userToken, String gameToken) {

        Game game = gameRepository.findByToken(gameToken);
        User user = userService.getUserFromToken(userToken);
        Lobby lobby = lobbyService.getLobbyFromToken(gameToken);
        game.removePlayer(user);
        userService.leaveGame(user);


        if (game.getPlayerList().isEmpty()) {
            lobby.setLobbyState(LobbyStatus.OPEN);
            for (Bot bot : game.getBotList()) {
                botService.deleteBot(bot.getToken());
            }
            gameRepository.delete(game);
        }
    }

    //add clue given by player
    public synchronized Game addClue(String userToken, String gameToken, String aClue) {
        String clue = aClue.toLowerCase();
        String sanitizedMsg = clue.replaceAll(SANITIZER, "_");
        log.info(sanitizedMsg);
        boolean valid = WordService.isValidWord(clue);
        Game game = gameRepository.findByToken(gameToken);
        User user = userService.getUserFromToken(userToken);
        List<String> checklist = game.getChecklist();

        if (Boolean.FALSE.equals(game.getBotsClueGiven())) {
            game.setBotsClueGiven(true);

            for (Bot bot : game.getBotList()) {
                String botClue = botService.botClue(bot.getDifficulty(), game.getTopic());
                checklist.add(botClue);
            }
        }

        if (Boolean.FALSE.equals(user.getGaveClue())) {
            user.addTotalClues();
            user.addTotalCluesLife();
            if (valid && !(clue.equalsIgnoreCase(game.getTopic()) || clue.equals("empty"))) {
                log.info("valid");
                checklist.add(clue);
                String msg = checklist.toString();
                log.info(msg);
            }
            else {
                checklist.add(CENSORED);
                user.addInvalidCluesLife();
                user.addInvalidClues();
            }
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "user already gave clue");
        }


        user.setGaveClue(true);
        //if all players gave clues, remove duplicates
        removeDuplicates(game, checklist);
        return game;
    }

    public Game makeGuess(String gameToken, String userToken, String guess) {
        String msg;
        String sanitizedMsg = guess.replaceAll(SANITIZER, "_");
        log.info(sanitizedMsg);

        User user = userService.getUserFromToken(userToken);
        Game game = gameRepository.findByToken(gameToken);
        game.setGuessGiven(true);
        game.setGuess(guess);
        if (!guess.equals("null")) {
            user.addGuessesMade();
            user.addGuessesMadeLife();
            if (game.getTopic().equalsIgnoreCase(guess)) {
                game.setGuessCorrect(true);
                user.addGuessesCorrect();
                user.addGuessesCorrectLife();
                msg = guess + " was a correct guess.";
                log.info(msg);
            }
            else {
                game.setGuessCorrect(false);
            }
        }
        else {
            game.setGuess("no guess given");
        }
        sanitizedMsg = ("GuessCorrect: " + game.getGuessCorrect()).replaceAll(SANITIZER, "_");
        log.info(sanitizedMsg);
        return game;
    }

    public Game nextRound(String gameToken) {

        Game game = gameRepository.findByToken(gameToken);
        int round = game.getCurrentRound();
        int guesser = game.getGuesser();
        List<String> clueList = new ArrayList<>();
        List<String> checkList = new ArrayList<>();
        List<Integer> voteList = new ArrayList<>();
        for (int a = 0; a < 5; a++) {
            voteList.add(0);
        }

        //add round nr according to result of previous guess
        if (game.getGuessCorrect() == null || game.getGuessCorrect()) {
            game.setCurrentRound(round + 1);
        }
        else {
            game.setCurrentRound(round + 2);
        }
        game.setGuessCorrect(null);
        game.setGuesser((guesser + 1) % game.getPlayerList().size());
        game.setTopic(null);
        game.setVoteList(voteList);
        game.setClueList(clueList);
        game.setCheckList(checkList);
        game.setGuessGiven(null);
        game.setBotsClueGiven(false);
        game.setBotsVoted(false);

        for (User user : game.getPlayerList()) {
            user.setUnityReady(false);
            user.setVoted(false);
            user.setGaveClue(false);
        }
        return game;
    }

    public void endGame(String gameToken, String userToken) {

        Game game = gameRepository.findByToken(gameToken);
        Lobby lobby = lobbyService.getLobbyFromToken(gameToken);
        User user = userService.getUserFromToken(userToken);
        user.addGamesPlayed();
        game.removePlayer(user);
        userService.leaveGame(user);


        if (game.getPlayerList().isEmpty()) {
            lobby.setLobbyState(LobbyStatus.OPEN);
            //  use for loop with index iter or dont modifying concurrent objects
            for (Bot bot : game.getBotList()) {
                botRepository.delete(bot);
            }
            game.getBotList().clear();
            gameRepository.delete(game);
        }
    }

    void removeDuplicates(Game game, List<String> checklist) {
        String msg;
        if (checklist.size() == (game.getPlayerList().size() - 1) + game.getBotList().size()) {
            log.info("ALL CLUES RECEIVED");
            checklist.add(game.getTopic());
            boolean[] duplicates = WordService.checkSimilarityInArray(checklist.toArray(new String[0]));
            for (int i = checklist.size() - 1; i >= 0; i--) {
                if (duplicates[i]) {
                    checklist.set(i, CENSORED);
                }
            }
            msg = checklist.toString();
            log.info(msg);

            //set ClueList to valid clues
            game.getClueList().clear();
            game.getClueList().addAll(checklist);
            msg = game.getClueList().toString();
            log.info(msg);
        }
    }

    public void addScore(String userToken, Integer score){
        User user =userService.getUserFromToken(userToken);
        user.addTotalScore(score);
    }
//  this function (numberOfCluesGiven()) was not used but can
//  be fetch from following commit: 81a7cd97dfaa31a4c50e7ccb42a535b80c3fb941
}
