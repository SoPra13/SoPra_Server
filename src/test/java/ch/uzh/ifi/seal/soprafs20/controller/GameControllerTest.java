package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;


    private Game newTestGame() {
        Game game = new Game();
        game.setId(1L);
        game.setVersion(0);
        game.setToken("Token_Aa0Bb1");
        game.setCurrentRound(0);
        game.setGuesser(0);
        return game;
    }

    @Test
    public void get_getGame_correct() throws Exception {
        Game game = newTestGame();

        given(gameService.getGameFromToken("Token_Aa0Bb1")).willReturn(game);

        MockHttpServletRequestBuilder getRequest = get("/game?token=Token_Aa0Bb1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(game.getId().intValue())))
                .andExpect(jsonPath("$.version", is(game.getVersion())))
                .andExpect(jsonPath("$.token", is(game.getToken())))
                .andExpect(jsonPath("$.currentRound", is(game.getCurrentRound())))
                .andExpect(jsonPath("$.guesser", is(game.getGuesser())));
    }

    @Test
    public void get_getGame_invalid_GameToken() throws Exception {

        given(gameService.getGameFromToken("INVALID_Token_Aa0Bb1"))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No matching Game found"));

        MockHttpServletRequestBuilder getRequest = get("/game?token=INVALID_Token_Aa0Bb1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isNotFound())
                .andExpect(status().reason("No matching Game found"));
    }

    @Test
    public void put_ready_correct() throws Exception {
        Game game = newTestGame();


        given(gameService.getGameFromToken("Token_Aa0Bb1")).willReturn(game);

        MockHttpServletRequestBuilder putRequest =
                put("/game/ready?userToken=UserToken&gameToken=Token_Aa0Bb1")
                        .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(game.getId().intValue())))
                .andExpect(jsonPath("$.version", is(game.getVersion())))
                .andExpect(jsonPath("$.token", is(game.getToken())))
                .andExpect(jsonPath("$.currentRound", is(game.getCurrentRound())))
                .andExpect(jsonPath("$.guesser", is(game.getGuesser())));
        verify(gameService).setPlayerReady("Token_Aa0Bb1", "UserToken");
    }

    @Test
    public void put_ready_invalid_UserToken() throws Exception {

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no User with requested Token"))
                .when(gameService).setPlayerReady("Token_Aa0Bb1", "INVALID_UserToken");

        MockHttpServletRequestBuilder putRequest =
                put("/game/ready?userToken=INVALID_UserToken&gameToken=Token_Aa0Bb1")
                        .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(putRequest).andExpect(status().isNotFound())
                .andExpect(status().reason("There is no User with requested Token"));
    }

    @Test
    public void put_ready_invalid_GameToken() throws Exception {

        given(gameService.getGameFromToken("INVALID_Token_Aa0Bb1"))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No matching Game found"));

        MockHttpServletRequestBuilder putRequest =
                put("/game/ready?userToken=UserToken&gameToken=INVALID_Token_Aa0Bb1")
                        .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isNotFound())
                .andExpect(status().reason("No matching Game found"));
        verify(gameService).setPlayerReady("INVALID_Token_Aa0Bb1", "UserToken");
    }

    @Test
    public void put_vote_correct() throws Exception {
        Game game = newTestGame();

        given(gameService.addVote("Token_Aa0Bb1", "Token_User", 1)).willReturn(game);

        MockHttpServletRequestBuilder putRequest =
                put("/game/vote?gameToken=Token_Aa0Bb1&userToken=Token_User&topic=1")
                        .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(putRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(game.getId().intValue())))
                .andExpect(jsonPath("$.version", is(game.getVersion())))
                .andExpect(jsonPath("$.token", is(game.getToken())))
                .andExpect(jsonPath("$.currentRound", is(game.getCurrentRound())))
                .andExpect(jsonPath("$.guesser", is(game.getGuesser())));
    }

    @Test
    public void put_vote_invalid_userToken() throws Exception {

        given(gameService.addVote("Token_Aa0Bb1", "INVALID_Token_User", 0))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "\"There is no User with requested Token\""));

        MockHttpServletRequestBuilder putRequest =
                put("/game/vote?gameToken=Token_Aa0Bb1&userToken=INVALID_Token_User&topic=0")
                        .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isNotFound())
                .andExpect(status().reason("\"There is no User with requested Token\""));
    }

    @Test
    public void put_vote_invalid_token() throws Exception {

        given(gameService.addVote("INVALID_Token_Aa0Bb1", "Token_User", 3))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No matching Game found"));

        MockHttpServletRequestBuilder putRequest =
                put("/game/vote?gameToken=INVALID_Token_Aa0Bb1&userToken=Token_User&topic=3")
                        .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isNotFound())
                .andExpect(status().reason("No matching Game found"));
    }

    @Test
    public void put_topic_success() throws Exception {
        Game game = newTestGame();

        given(gameService.setTopic("Token_Aa0Bb1", "Topic"))
                .willReturn(game);

        MockHttpServletRequestBuilder putRequest =
                put("/game/topic?gameToken=Token_Aa0Bb1&topic=Topic")
                        .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(game.getId().intValue())))
                .andExpect(jsonPath("$.version", is(game.getVersion())))
                .andExpect(jsonPath("$.token", is(game.getToken())))
                .andExpect(jsonPath("$.currentRound", is(game.getCurrentRound())))
                .andExpect(jsonPath("$.guesser", is(game.getGuesser())));
    }

    @Test
    public void put_topic_invalid_token() throws Exception {

        given(gameService.setTopic("INVALID_Token_Aa0Bb1", "Topic"))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No matching Game found"));

        MockHttpServletRequestBuilder putRequest =
                put("/game/topic?gameToken=INVALID_Token_Aa0Bb1&topic=Topic")
                        .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isNotFound())
                .andExpect(status().reason("No matching Game found"));
    }

    @Test
    public void put_removePlayer_success() throws Exception {

        Mockito.doNothing().when(gameService).removeUser(Mockito.anyString(), Mockito.anyString());

        MockHttpServletRequestBuilder delRequest =
                delete("/game?userToken=USER_TOKEN&gameToken=Token_Aa0Bb1")
                        .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(delRequest).andExpect(status().isOk());
        Mockito.verify(gameService, times(1)).removeUser(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void put_addClue_success() throws Exception {
        Game game = newTestGame();

        given(gameService.addClue("USER_TOKEN", "Token_Aa0Bb1", "Clue"))
                .willReturn(game);

        MockHttpServletRequestBuilder putRequest =
                put("/game/clue?gameToken=Token_Aa0Bb1&userToken=USER_TOKEN&clue=Clue")
                        .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(game.getId().intValue())))
                .andExpect(jsonPath("$.version", is(game.getVersion())))
                .andExpect(jsonPath("$.token", is(game.getToken())))
                .andExpect(jsonPath("$.currentRound", is(game.getCurrentRound())))
                .andExpect(jsonPath("$.guesser", is(game.getGuesser())));
    }

    @Test
    public void put_makeGuess_success() throws Exception {
        Game game = newTestGame();

        given(gameService.makeGuess("Token_Aa0Bb1", "Guess"))
                .willReturn(game);

        MockHttpServletRequestBuilder putRequest =
                put("/game/guess?gameToken=Token_Aa0Bb1&guess=Guess")
                        .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(game.getId().intValue())))
                .andExpect(jsonPath("$.version", is(game.getVersion())))
                .andExpect(jsonPath("$.token", is(game.getToken())))
                .andExpect(jsonPath("$.currentRound", is(game.getCurrentRound())))
                .andExpect(jsonPath("$.guesser", is(game.getGuesser())));
    }

    @Test
    public void put_nextRound_success() throws Exception {
        Game game = newTestGame();

        given(gameService.nextRound("Token_Aa0Bb1"))
                .willReturn(game);

        MockHttpServletRequestBuilder putRequest =
                put("/game/round?gameToken=Token_Aa0Bb1")
                        .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(game.getId().intValue())))
                .andExpect(jsonPath("$.version", is(game.getVersion())))
                .andExpect(jsonPath("$.token", is(game.getToken())))
                .andExpect(jsonPath("$.currentRound", is(game.getCurrentRound())))
                .andExpect(jsonPath("$.guesser", is(game.getGuesser())));
    }

}