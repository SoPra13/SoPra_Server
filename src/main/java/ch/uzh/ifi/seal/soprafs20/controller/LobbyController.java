package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.constant.LobbyStatus;
import ch.uzh.ifi.seal.soprafs20.constant.LobbyType;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.LobbyRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.LobbyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LobbyController {

    private final LobbyService lobbyService;
    LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }


    //Get All Public Lobbies
    @GetMapping("/lobbies")
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

    //Get All Public Lobbies
    @GetMapping("/lobby")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyGetDTO getLobby(@RequestParam String lobbyToken) {

        //get Lobby with token
        Lobby lobby = lobbyService.getLobbyFromToken(lobbyToken);

        // convert internal representation of Lobby back to API
        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);
    }


    @PostMapping("/lobby")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LobbyGetDTO createLobby(@RequestBody LobbyPostDTO lobbyPostDTO) {
        // convert API user to internal representation
        Lobby lobbyInput = DTOMapper.INSTANCE.convertLobbyPostDTOtoEntity(lobbyPostDTO);

        // create lobby
        Lobby createdLobby = lobbyService.createLobby(lobbyInput,lobbyPostDTO.getAdminToken());

        // convert internal representation of Lobby back to API
        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(createdLobby);
    }



    @PutMapping("/lobby")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public LobbyGetDTO joinLobby(@RequestParam String lobbyToken, @RequestParam String userToken) {

        //add user to lobby
        Lobby lobby = lobbyService.joinLobby(lobbyToken,userToken);

        // convert internal representation of lobby back to API
        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);
    }

    @DeleteMapping("/lobby")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyGetDTO leaveLobby(@RequestParam String lobbyToken, @RequestParam String userToken) {

        //add user to lobby
        Lobby lobby = lobbyService.leaveLobby(lobbyToken,userToken);

        // convert internal representation of lobby back to API
        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);
    }
}
