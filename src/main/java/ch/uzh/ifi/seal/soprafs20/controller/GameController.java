package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.constant.LobbyStatus;
import ch.uzh.ifi.seal.soprafs20.constant.LobbyType;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.LobbyRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GameGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import ch.uzh.ifi.seal.soprafs20.service.LobbyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GameController {

    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }


    //Get Game from Token
    @GetMapping("/game")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGame(@RequestParam String token) {

        //fetch game and return it as DTO
        Game game = gameService.getGameFromToken(token);

        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
    }


    //Set player in Game ready, called when unity is loaded
    @PutMapping("/game/ready")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO setPlayerUnityReady(@RequestParam String userToken, @RequestParam String gameToken) {

        gameService.setPlayerReady(gameToken,userToken);

        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(gameService.getGameFromToken(gameToken));
    }

}