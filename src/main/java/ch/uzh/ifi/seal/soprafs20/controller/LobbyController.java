package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.constant.LobbyType;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import ch.uzh.ifi.seal.soprafs20.service.LobbyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LobbyController {

    private final LobbyService lobbyService;
    private final GameService gameService;

    LobbyController(LobbyService lobbyService, GameService gameService) {
        this.lobbyService = lobbyService;
        this.gameService = gameService;
    }


    //Get All Public Lobbies
    @GetMapping(value = "/lobbies")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<LobbyGetDTO> getAllLobbies() {
        // fetch all users in the internal representation
        List<Lobby> lobbies = lobbyService.getLobbies();
        List<LobbyGetDTO> lobbyGetDTOS = new ArrayList<>();

        // convert each user to the API representation
        for (Lobby lobby : lobbies) {
            if (lobby.getLobbyType() == LobbyType.PUBLIC) {

                lobbyGetDTOS.add(DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby));
            }
        }
        return lobbyGetDTOS;
    }

    //Get Lobby with token
    @GetMapping(value = "/lobby", params = "lobbyToken")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyGetDTO getLobby(@RequestParam String lobbyToken) {

        //get Lobby with token
        Lobby lobby = lobbyService.getLobbyFromToken(lobbyToken);

        // convert internal representation of Lobby back to API
        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);
    }


    //create new Lobby
    @PostMapping("/lobby")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LobbyGetDTO createLobby(@RequestBody LobbyPostDTO lobbyPostDTO) {
        // convert API user to internal representation
        Lobby lobbyInput = DTOMapper.INSTANCE.convertLobbyPostDTOtoEntity(lobbyPostDTO);

        // create lobby
        Lobby createdLobby = lobbyService.createLobby(lobbyInput, lobbyPostDTO.getAdminToken());

        // convert internal representation of Lobby back to API
        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(createdLobby);
    }


    //start game
    @PutMapping("/lobby/{token}/game")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public String startGame(@PathVariable String token) {

        Lobby lobby = lobbyService.getLobbyFromToken(token);

        Game game = gameService.createGame(lobby, token);

        return game.getToken();

    }


    //add user to Lobby
    @PutMapping("/lobby")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LobbyGetDTO joinLobby(@RequestParam String joinToken, @RequestParam String userToken) {

        //add user to lobby
        Lobby lobby = lobbyService.joinLobby(joinToken, userToken);

        // convert internal representation of lobby back to API
        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);
    }

    //add bot to Lobby
    @PutMapping(value = "/lobby", params = {"lobbyToken", "difficulty"})
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LobbyGetDTO addBot(@RequestParam String lobbyToken, @RequestParam String difficulty) {

        //add bot to lobby
        Lobby lobby = lobbyService.addBot(lobbyToken, difficulty);

        // convert internal representation of lobby back to API
        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);
    }

    //remove user from Lobby
    @DeleteMapping("/lobby")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void leaveLobby(@RequestParam String lobbyToken, @RequestParam String userToken) {

        //remove user from lobby
        lobbyService.leaveLobby(lobbyToken, userToken);


    }

    //remove bot from Lobby
    @DeleteMapping(value = "/lobby", params = {"lobbyToken", "botToken"})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyGetDTO removeBot(@RequestParam String lobbyToken, @RequestParam String botToken) {

        //add user to lobby
        Lobby lobby = lobbyService.removeBot(lobbyToken, botToken);

        // convert internal representation of lobby back to API
        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);
    }


    //Set player in Lobby ready, called when he pressed ready button
    @PutMapping("/lobby/ready")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyGetDTO setPlayerLobbyReady(@RequestParam String userToken, @RequestParam String lobbyToken) {

        gameService.setPlayerReady(lobbyToken, userToken);

        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobbyService.getLobbyFromToken(lobbyToken));
    }
}
