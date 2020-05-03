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
    private final BotService botService;
    private final LobbyService lobbyService;
    private final ChatService chatService;

    private final Logger log = LoggerFactory.getLogger(GameService.class);
    private final GameRepository gameRepository;


    @Autowired
    public GameService(UserService userService, ChatService chatService,BotService botService, LobbyService lobbyService, @Qualifier("gameRepository") GameRepository gameRespository) {
        this.gameRepository = gameRespository;
        this.userService = userService;
        this.lobbyService = lobbyService;
        this.botService = botService;
        this.chatService = chatService;
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

        List<String> clueList = new ArrayList<>();
        List<String> checkList = new ArrayList<>();
        List<Integer> voteList = new ArrayList<>();
        for(int a = 0; a<5; a++){
            voteList.add(0);
        }

        newGame.setBotList(botList);
        newGame.setBotsClueGiven(false);
        newGame.setBotsVoted(false);
        newGame.setPlayerList(userList);
        newGame.setToken(lobby.getLobbyToken());
        newGame.setCurrentRound(0);
        newGame.setVersion(1);
        newGame.setClueList(clueList);
        newGame.setVoteList(voteList);
        newGame.setCheckList(checkList);
        newGame.setGuessGiven(false);
        //todo:make guesser random again
        newGame.setGuesser(0);
        newGame.setMysteryWords(WordFileHandler.getMysteryWords());

        // saves the given entity but data is only persisted in the database once flush() is called
        newGame = gameRepository.save(newGame);
        gameRepository.flush();

        //update game of user and add initial position
        Integer position = 0;
        for(User user : userList){
            user.setGame(newGame);
            user.setCurrentPosition(position);
            position +=1;
            System.out.println("added User to game;");
            System.out.println(user.getId());
        }

        //update game of bot
        position = 0;
        for(Bot bot : botList){
            bot.setGame(newGame);
            bot.setCurrentPosition(position);
            position +=1;
            System.out.println("added vot to game;");
            System.out.println(bot.getId());
        }



        log.debug("Created Information for Lobby: {}", newGame);
        return newGame;
    }


    //vote for topic, adds one to index of topic voted for
public Game addVote(String gameToken, String userToken, int vote){
        if (vote > 5)throw new ResponseStatusException(HttpStatus.NOT_FOUND, "int has to be <5");

        Game game = getGameFromToken(gameToken);

        User user = userService.getUserFromToken(userToken);
        game.setGuessCorrect(null);
        List voteList = game.getVoteList();

        if(!game.getBotsVoted()){
            game.setBotsVoted(true);
            for(Bot bot: game.getBotList()){
                int botVote = new Random().nextInt(4);
                Integer votes = (Integer) voteList.get(botVote);
                voteList.set(botVote,votes+1);
            }
        }

        Integer votes = (Integer) voteList.get(vote);
        voteList.set(vote,votes+=1);
        game.setVoteList(voteList);
        user.setVoted(true);
        return game;

    }

    //set Topic of game after Voting
    public Game setTopic(String gameToken, String topic){
        Game game = getGameFromToken(gameToken);

        game.setTopic(topic.toLowerCase());
        return game;
    }

    //remove user during game
    public void removeUser(String userToken, String gameToken){

        Game game = gameRepository.findByToken(gameToken);
        User user = userService.getUserFromToken(userToken);
        Lobby lobby = lobbyService.getLobbyFromToken(gameToken);
        List oldUser = game.getPlayerList();
        chatService.leaveChat(gameToken,userToken);
        lobby.removePlayer(user);
        game.removePlayer(user);
        userService.leaveGame(user);
        userService.leaveLobby(user);

        if(game.getPlayerList().size()==0){
            for(Bot bot : game.getBotList()){
                botService.deleteBot(bot.getToken());
            }
            gameRepository.delete(game);
            lobbyService.deleteLobby(lobby);
            return;
        }


    }

    //add clue given by player
    public synchronized Game addClue(String userToken, String gameToken, String Aclue){


        String clue = Aclue.toLowerCase();
        System.out.println(clue);
        System.out.println("");
        boolean valid = WordService.isValidWord(clue);
        Game game = gameRepository.findByToken(gameToken);
        User user = userService.getUserFromToken(userToken);
        List checklist = game.getChecklist();


       /* if (!game.getBotsClueGiven()){
            game.setBotsClueGiven(true);

            for(Bot bot : game.getBotList()){
                String botClue = botService.botclue(bot.getDifficulty(),game.getTopic());
                checklist.add(botClue);
            }
        }*/
        if(!user.getGaveClue()) {
            if(valid){
                if(!clue.equals(game.getTopic().toLowerCase())){
                    System.out.println("valid");
                    System.out.println("");
                    checklist.add(clue);
                    System.out.println(checklist);
                    System.out.println("");
                }else{
                    checklist.add("CENSORED");
                }
            }else {
                checklist.add("CENSORED");
            }
        }else{
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "user already gave clue");
        }


        user.setGaveClue(true);
        //if all players gave clues, remove duplicates
        if(checklist.size()==(game.getPlayerList().size()-1)+game.getBotList().size()){
            System.out.println("ALL CLUES RECEIVED");
            System.out.println("");
            checklist.add(game.getTopic());
           boolean[] duplicates = WordService.checkSimilarityInArray((String[]) checklist.toArray(new String[checklist.size()]));
           //remove duplicates from end to bottom because of indexes removed would break code
            for(int i = checklist.size()-1; i>=0;i--){
                if(duplicates[i]){
                    checklist.set(i,"CENSORED");
                }
            }
            System.out.println(checklist);
            System.out.println("");

            //set ClueList to valid clues
            List clueList = game.getClueList();
            clueList.clear();
            clueList.addAll(checklist);
            game.setClueList(clueList);
            System.out.println(clueList);
            System.out.println("");
        }

        return game;
    }

    public Game makeGuess(String gameToken, String guess){
        System.out.println(guess);
        System.out.println("");

        Game game = gameRepository.findByToken(gameToken);
        game.setGuessGiven(true);
        game.setGuessCorrect(game.getTopic().equals(guess.toLowerCase()));
        System.out.println(game.getTopic().equals(guess));
        System.out.println("");
        System.out.println(game.getGuessCorrect());
        System.out.println("");
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
        if(game.getGuessCorrect()|| game.getGuessCorrect() == null) {
            game.setCurrentRound(round + 1);
        }else{
            game.setCurrentRound(round + 2);
        }
        game.setGuessCorrect(null);
        game.setGuesser((guesser+1) % game.getPlayerList().size());
        game.setTopic(null);
        game.setVoteList(voteList);
        game.setClueList(clueList);
        game.setCheckList(checkList);
        game.setGuessGiven(false);
        game.setBotsClueGiven(false);
        game.setBotsVoted(false);


        for (User user : game.getPlayerList()) {
            user.setUnityReady(false);
            user.setVoted(false);
            user.setGaveClue(false);
    }
        return game;
    }

    public void endGame(String gameToken, String userToken, String score){

        Game game = gameRepository.findByToken(gameToken);
        User user = userService.getUserFromToken(userToken);
        List oldUser = game.getPlayerList();
        game.removePlayer(user);
        userService.leaveGame(user);



        if(game.getPlayerList().size()==0){
            for(Bot bot : game.getBotList()){
                game.removeBot(bot);
                botService.leaveGame(bot);
            }
            gameRepository.delete(game);
            chatService.endMessage(score,userToken,gameToken);
            return;
        }

    }

    public int numberOfCluesGiven(Game game){
        int count = 0;

        for(User user : game.getPlayerList()){
            if(user.getGaveClue()){
                count += 1;
            }
        }
        return count;
    }
}
