package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GameGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

        gameService.checkAllPlayersAreConnected(token);

        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
    }


    //Set player in Game ready, called when unity is loaded
    @PutMapping("/game/ready")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO setPlayerUnityReady(@RequestParam String userToken, @RequestParam String gameToken) {

        gameService.setPlayerReady(gameToken, userToken);

        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(gameService.getGameFromToken(gameToken));
    }


    //vote for topic
    @PutMapping("/game/vote")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO voteForTopic(@RequestParam String gameToken, @RequestParam String userToken, @RequestParam Integer topic) {

        Game game = gameService.addVote(gameToken, userToken, topic);

        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
    }

    //Set voted Topic as word
    @PutMapping(value = "/game/topic", params = {"gameToken", "topic"})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO setTopic(@RequestParam String gameToken, @RequestParam String topic) {

        //set Topic chosen from voting in unity
        Game game = gameService.setTopic(gameToken, topic);

        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
    }

    //player leave game
    @DeleteMapping("/game")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void removePlayer(@RequestParam String userToken, @RequestParam String gameToken) {

        gameService.removeUser(userToken, gameToken);

    }

    //Set voted Topic as word
    @PutMapping(value = "/game/clue", params = {"gameToken", "userToken", "clue"})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO addClue(@RequestParam String gameToken, @RequestParam String userToken, @RequestParam String clue) {

        //set Topic chosen from voting in unity
        Game game = gameService.addClue(userToken, gameToken, clue);

        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
    }

    //make a guess
    @PutMapping(value = "/game/guess", params = {"gameToken", "userToken", "guess"})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO makeGuess(@RequestParam String gameToken, @RequestParam String userToken, @RequestParam String guess) {

        //set Topic chosen from voting in unity
        Game game = gameService.makeGuess(gameToken, userToken, guess);

        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
    }

    //Set voted Topic as word
    @PutMapping(value = "/game/round", params = {"gameToken"})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO nextRound(@RequestParam String gameToken) {

        //enter next round
        Game game = gameService.nextRound(gameToken);

        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
    }

    @PutMapping(value = "/game/leave", params = {"gameToken", "userToken"})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void endGame(@RequestParam String gameToken, @RequestParam String userToken) {

        //end game
        gameService.endGame(gameToken, userToken);

    }


    @PutMapping(value = "/game/score", params = {"score", "score"})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void addScore(@RequestParam String userToken, @RequestParam Integer score) {

        gameService.addScore(userToken,score);

    }
}