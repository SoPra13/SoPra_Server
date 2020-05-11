package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.LobbyStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Bot;
import ch.uzh.ifi.seal.soprafs20.repository.LobbyRepository;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class LobbyService {

    private final UserService userService;
    private  final BotService botService;
    private final ChatService chatService;
    private final Logger log = LoggerFactory.getLogger(LobbyService.class);
    private final LobbyRepository lobbyRepository;


    @Autowired
    public LobbyService(UserService userService, BotService botService, ChatService chatService, @Qualifier("lobbyRepository") LobbyRepository lobbyRepository) {
        this.userService = userService;
        this.botService = botService;
        this.chatService = chatService;
        this.lobbyRepository = lobbyRepository;
    }

    //get all Lobbies
    public List<Lobby> getLobbies() {
        return this.lobbyRepository.findAll();
    }

    //Get Lobby from Token
    public Lobby getLobbyFromToken(String token){

        String baseErrorMessage = "No matching Lobby found";
        //check if exists
        Lobby lobbyByToken = this.lobbyRepository.findByLobbyToken(token);
        if (lobbyByToken == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, baseErrorMessage);
        }

        return lobbyByToken;
    }

    //Get Lobby from Token
    public Lobby getLobbyFromJoinToken(String token){

        String baseErrorMessage = "No matching Lobby found";
        //check if exists
        Lobby lobbyByToken = this.lobbyRepository.findByJoinToken(token);
        if (lobbyByToken == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, baseErrorMessage);
        }

        return lobbyByToken;
    }

    //creates new Lobby
    public Lobby createLobby(Lobby newLobby, String token) {

        ArrayList<User> userList = new ArrayList<User>();
        User user = userService.getUserFromToken(token);
        userList.add(user);
        newLobby.setLobbyToken(UUID.randomUUID().toString());
        newLobby.setNumberOfPlayers(1);
        newLobby.setAdminToken(user.getToken());
        newLobby.setLobbyState(LobbyStatus.OPEN);
        newLobby.setPlayerList(userList);
        newLobby.setJoinToken(this.generateJoinToken());

        // saves the given entity but data is only persisted in the database once flush() is called
        newLobby = lobbyRepository.save(newLobby);
        user.setLobby(newLobby);
        lobbyRepository.flush();
        chatService.createChat(newLobby.getLobbyToken());

        log.debug("Created Information for Lobby: {}", newLobby);
        return newLobby;
    }


    //add player to Lobby
    public Lobby joinLobby(String joinToken, String uToken){

        Lobby lobbyToAdd = getLobbyFromJoinToken(joinToken);
        //checks
        checkLobbyExists(lobbyToAdd.getLobbyToken());
        checkLobbyFull(lobbyToAdd.getLobbyToken());
        checkLobbyTokens(lobbyToAdd.getLobbyToken(),uToken);

        //get lobby & user
        User userToAdd = userService.getUserFromToken(uToken);


        // add user to lobby
        List list = lobbyToAdd.getPlayerList();
        list.add(userToAdd);
        lobbyToAdd.setNumberOfPlayers(list.size()+lobbyToAdd.getBotList().size());
        userToAdd.setLobby(lobbyToAdd);
        return lobbyToAdd;
    }

    //remove player from Lobby
    public void leaveLobby(String lToken, String uToken){

        //checks
        checkLobbyExists(lToken);

        //get lobby & user
        User userToRemove = userService.getUserFromToken(uToken);
        Lobby lobbyToRemove = lobbyRepository.findByLobbyToken(lToken);

        // remove user from lobby
        lobbyToRemove.removePlayer(userToRemove);
        userService.leaveLobby(userToRemove);
        lobbyToRemove.setNumberOfPlayers(lobbyToRemove.getPlayerList().size()+lobbyToRemove.getBotList().size());

        if(lobbyToRemove.getPlayerList().size()==0){
            lobbyRepository.delete(lobbyToRemove);
        }
    }


    //add bot to Lobby
    public Lobby addBot(String lToken, String difficulty){

        //checks
        checkLobbyExists(lToken);
        checkLobbyFull(lToken);

        //get lobby
        Lobby lobbyToAdd = lobbyRepository.findByLobbyToken(lToken);

        // add bot to lobby
        List list = lobbyToAdd.getBotList();
        Bot bot = botService.createBot(difficulty);
        list.add(bot);
        lobbyToAdd.setNumberOfPlayers(list.size()+lobbyToAdd.getPlayerList().size());
        bot.setLobby(lobbyToAdd);
        return lobbyToAdd;
    }


    //remove bot from Lobby
    public Lobby removeBot(String lToken, String bToken){

        //checks
        checkLobbyExists(lToken);

        //get lobby & bot
        Bot botToRemove = botService.getBotFromToken(bToken);
        Lobby lobbyToRemove = lobbyRepository.findByLobbyToken(lToken);

        // remove bot from lobby
        List list = lobbyToRemove.getBotList();
        list.remove(botToRemove);
        lobbyToRemove.setBotList(list);
        lobbyToRemove.setNumberOfPlayers(list.size()+lobbyToRemove.getPlayerList().size());
        botToRemove.setLobby(null);
        return lobbyToRemove;
    }

    //check if user is already in Lobby
    private void checkLobbyTokens(String lToken, String uToken){
        String baseErrorMessage = "User is already in Lobby";

        Lobby lobbyToAdd = lobbyRepository.findByLobbyToken(lToken);

        List<User> users = lobbyToAdd.getPlayerList();

        for(User user : users){
            if(user.getToken().equals(uToken)){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, baseErrorMessage);
            }
        }
    }


    //check if Lobby is full
    private void checkLobbyFull(String lToken){

        String baseErrorMessage = "Lobby is full";
        Lobby lobbyToAdd = lobbyRepository.findByLobbyToken(lToken);

            if(lobbyToAdd.getNumberOfPlayers()>=7){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, baseErrorMessage);
        }
    }


// check if Lobby exists
    private void checkLobbyExists(String token){
        String baseErrorMessage = "Lobby not found";

        Lobby lobby = lobbyRepository.findByLobbyToken(token);

        if (lobby == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, baseErrorMessage);
        }
    }

    public void deleteLobby(Lobby lobby){
        lobbyRepository.delete(lobby);
    }


    //set player as ready
    public void setPlayerReady(String lobbyToken, String userToken){

        String baseErrorMessage = "Could not set player ready";
        User user = userService.getUserFromToken(userToken);

        if(user.getLobby().getLobbyToken().equals(lobbyToken)){
            user.setLobbyReady(true);
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, baseErrorMessage);
        }

    }

    /**
     * This is a helper method that will check the uniqueness criteria of the ???? and the ????
     * defined in the Lobby entity. The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @throws org.springframework.web.server.ResponseStatusException
     * @see Lobby
     */
    public String generateJoinToken(){
        Integer token = 1729;
//        Integer token = 1337;
        while (lobbyRepository.findByJoinToken(String.valueOf(token))!=null){
            token = new Random().nextInt(8999);
            token += 1000;
        }
        return String.valueOf(token);
    }
}