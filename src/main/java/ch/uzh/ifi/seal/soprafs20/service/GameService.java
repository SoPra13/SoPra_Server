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
    public GameService(UserService userService, @Qualifier("gameRepository") GameRepository gameRespository) {
        this.gameRepository = gameRespository;
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


    //set player as ready, called when unity loaded
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

        List<User> userList = new ArrayList<>();
        userList.addAll(lobby.getPlayerList());
        List<Bot> botList = new ArrayList<>();
        botList.addAll(lobby.getBotList());
        List<Integer> voteList = new ArrayList<>();
        for(int a = 0; a<5; a++){
            voteList.add(0);
        }

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
        Integer position = 0;
        for(User user : userList){
            user.setGame(newGame);
            user.setCurrentPosition(position);
            position +=1;
        }

        //update game of bot
        for(Bot bot : botList){
            bot.setGame(newGame);
        }

        gameRepository.flush();

        log.debug("Created Information for Lobby: {}", newGame);
        return newGame;
    }


    //vote for topic, adds one to index of topic voted for
    public Game addVote(String gameToken, Integer vote){

        if (vote > 5)throw new ResponseStatusException(HttpStatus.NOT_FOUND, "int has to be <5");

        Game game = gameRepository.findByToken(gameToken);
        List voteList = game.getVoteList();
        Integer votes = (Integer) voteList.get(vote);
        voteList.set(vote,votes+=1);
        game.setVoteList(voteList);
        return game;

    }

}