package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.Bot;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.helpers.WordFileHandler;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    private final UserService userService;

    private final Logger log = LoggerFactory.getLogger(GameService.class);
    private final GameRepository gameRepository;


    @Autowired
    public GameService(UserService userService, @Qualifier("gameRepository") GameRepository gameRepository) {
        this.gameRepository = gameRepository;
        this.userService = userService;
    }


    //return all games, for testing only
    public List<Game> getGames() {
        return this.gameRepository.findAll();
    }

    //Get Game from Token
    public Game getGameFromToken(String token){

        String baseErrorMessage = "No matching Game found";
        //check if exists
        Game gameByToken = this.gameRepository.findByToken(token);
        if (gameByToken == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, baseErrorMessage);
        }
        return gameByToken;
    }


    //set player as ready
    public void setPlayerReady(String gameToken, String userToken){

        String baseErrorMessage = "Could not set player ready";
        User user = userService.getUserFromToken(userToken);

        if(user.getGame().getToken().equals(gameToken)){
            user.setUnityReady(true);
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, baseErrorMessage);
        }

    }


    //create new Game based on Lobby
    public Game createGame(Lobby lobby, String token) {
        Game newGame = new Game();
        // for testing purposes saving the game avoids having to mock the Game constructor
        newGame = gameRepository.save(newGame);

        List<User> userList = new ArrayList<>(lobby.getPlayerList());
        List<Bot> botList = new ArrayList<>(lobby.getBotList());
        // each card has 5 word so we need a vote list with 5 slots init with 0 votes
        List<Integer> voteList = new ArrayList<>(Collections.nCopies(5, 0));

        newGame.setBotList(botList);
        newGame.setPlayerList(userList);
        newGame.setToken(lobby.getLobbyToken());
        newGame.setRound(1);
        newGame.setVersion(1);
        newGame.setVoteList(voteList);
        newGame.setGuesser(new Random().nextInt(userList.size()));
        newGame.setMysteryWords(WordFileHandler.getMysteryWords());

        // saves the given entity but data is only persisted in the database once flush() is called
        newGame = gameRepository.save(newGame);

        //update game of user and add initial position
        int position = 0;
        for(User user : userList){
            user.setGame(newGame);
            user.setCurrentPosition(position);
            position +=1;
        }

        for(Bot bot : botList){
            bot.setGame(newGame);
        }

        gameRepository.flush();

        log.debug("Created Information for Lobby: {}", newGame);
        return newGame;
    }


    public Game addVote(String gameToken, Integer vote){
        // make sure that indexing of votes is the same everywhere
        if (vote < 1 || vote > 5)throw new ResponseStatusException(HttpStatus.NOT_FOUND, "int has to be [1:5]");

        Game game = gameRepository.findByToken(gameToken);
        if (game == null) {
            String baseErrorMessage = "No matching Game found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, baseErrorMessage);
        }

        game.getVoteList().set(vote-1, game.getVoteList().get(vote-1) + 1);
        return game;

    }

}