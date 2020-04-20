package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserPostDTO;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.constraints.br.TituloEleitoral;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ExecutionException;


import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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
        game.setRound(0);
        game.setGuesser(0);
        return game;
    }

    private User newTestUser() {
        User user = new User();
        user.setUsername("UserName");
        user.setPassword("PassWord");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("UserToken");
        user.setId(1L);
        return user;
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
                .andExpect(jsonPath("$.round", is(game.getRound())))
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
                .andExpect(jsonPath("$.round", is(game.getRound())))
                .andExpect(jsonPath("$.guesser", is(game.getGuesser())));
        verify(gameService).setPlayerReady("Token_Aa0Bb1", "UserToken");
    }

    @Test
    public void put_ready_invalid_UserToken() throws Exception {
        Game game = newTestGame();
        User user = newTestUser();

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no User with requested Token"))
                .when(gameService).setPlayerReady("Token_Aa0Bb1","INVALID_UserToken");

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

        given(gameService.addVote("Token_Aa0Bb1", 1)).willReturn(game);

        MockHttpServletRequestBuilder putRequest = put("/game/vote?gameToken=Token_Aa0Bb1&topic=1")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(putRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(game.getId().intValue())))
                .andExpect(jsonPath("$.version", is(game.getVersion())))
                .andExpect(jsonPath("$.token", is(game.getToken())))
                .andExpect(jsonPath("$.round", is(game.getRound())))
                .andExpect(jsonPath("$.guesser", is(game.getGuesser())));
    }

    @Test
    public void put_vote_invalid_topic() throws Exception {

        given(gameService.addVote("Token_Aa0Bb1", 6))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "int has to be [1:5]"));

        MockHttpServletRequestBuilder putRequest = put("/game/vote?gameToken=Token_Aa0Bb1&topic=6")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isNotFound())
                .andExpect(status().reason("int has to be [1:5]"));
    }

    @Test
    public void put_vote_invalid_token() throws Exception {

        given(gameService.addVote("INVALID_Token_Aa0Bb1", 3))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No matching Game found"));

        MockHttpServletRequestBuilder putRequest = put("/game/vote?gameToken=INVALID_Token_Aa0Bb1&topic=3")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isNotFound())
                .andExpect(status().reason("No matching Game found"));
    }


}