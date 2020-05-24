package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.constant.LobbyStatus;
import ch.uzh.ifi.seal.soprafs20.constant.LobbyType;
import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import ch.uzh.ifi.seal.soprafs20.service.LobbyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * LobbyControllerTest
 * This is a WebMvcTest which allows to test the LobbyController i.e. GET/POST request without actually sending them over the network.
 * This tests if the LobbyController works.
 */
@WebMvcTest(LobbyController.class)
class LobbyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LobbyService lobbyService;

    @MockBean
    private GameService gameService;

    private Lobby dummyLobby() {
        Lobby lobby = new Lobby();
        lobby.setLobbyName("testlobbyname");
//        lobby.setPassword("testpassword");
        lobby.setLobbyState(LobbyStatus.OPEN);
//        lobby.setToken("testtoken");
        lobby.setAdminToken("admin");
        lobby.setId(1L);
        return lobby;
    }

    private User dummyUser() {
        User user = new User();
        user.setUsername("testusername");
        user.setPassword("testpassword");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("testtokenUser");
        user.setId(1L);
        return user;
    }

    @Test
    void post_create_lobby_success() throws Exception {
        Lobby lobby = dummyLobby();
        User user = dummyUser();
        lobby.setAdminToken(user.getToken());

        given(lobbyService.createLobby(Mockito.any(), Mockito.any())).willReturn(lobby);

        LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
        lobbyPostDTO.setAdminToken(user.getToken());
//        lobbyPostDTO.setPassword("password");
        lobbyPostDTO.setLobbyName("lobbyName");
        lobbyPostDTO.setLobbyType(LobbyType.PUBLIC);

        MockHttpServletRequestBuilder postRequest = post("/lobby").contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPostDTO));
        mockMvc.perform(postRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$.lobbyName", is(lobby.getLobbyName())))
                .andExpect(jsonPath("$.id", is(lobby.getId().intValue())))
//                .andExpect(jsonPath("$.password", is(lobby.getPassword())))
//                .andExpect(jsonPath("$.token", is(lobby.getToken())))
                .andExpect(jsonPath("$.adminToken", is(user.getToken())));
    }

    @Test
    void post_create_lobby_failed() throws Exception {
        User user = dummyUser();

        LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
        lobbyPostDTO.setAdminToken(user.getToken());


        given(lobbyService.createLobby(Mockito.any(), Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND,
                "There is no User with requested Token"));


        MockHttpServletRequestBuilder postRequest = post("/lobby").contentType(MediaType.APPLICATION_JSON).content(asJsonString(lobbyPostDTO));
        mockMvc.perform(postRequest).andExpect(status().isNotFound())
                .andExpect(status().reason("There is no User with requested Token"));
    }

    @Test
    void get_lobbies_success() throws Exception {
        Lobby lobby1 = dummyLobby();
        Lobby lobby2 = dummyLobby();
        lobby2.setId(7L);
        List<Lobby> lobbies = new ArrayList<>();
        lobby1.setLobbyType(LobbyType.PRIVATE);
        lobby2.setLobbyType(LobbyType.PUBLIC);
        lobbies.add(lobby1);
        lobbies.add(lobby2);

        List<LobbyGetDTO> result = new ArrayList<>();
        result.add(DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby2));


        given(lobbyService.getLobbies()).willReturn(lobbies);

        MockHttpServletRequestBuilder getRequest = get("/lobbies").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(7)))
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void get_lobbies_empty_success() throws Exception {
        Lobby lobby1 = dummyLobby();
        List<Lobby> lobbies = new ArrayList<>();
        lobby1.setLobbyType(LobbyType.PRIVATE);
        lobbies.add(lobby1);

        given(lobbyService.getLobbies()).willReturn(lobbies);

        MockHttpServletRequestBuilder getRequest = get("/lobbies").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }

    @Test
    void get_lobbies_failed() throws Exception {

        given(lobbyService.getLobbies()).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No  Lobbies Found"));

        MockHttpServletRequestBuilder getRequest = get("/lobbies").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }

    @Test
     void get_lobbyByToken_correct() throws Exception {
        Lobby lobby = dummyLobby();

        given(lobbyService.getLobbyFromToken("testtoken")).willReturn(lobby);


        MockHttpServletRequestBuilder getRequest = get("/lobby?lobbyToken=testtoken")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.lobbyName", is(lobby.getLobbyName())))
                .andExpect(jsonPath("$.id", is(lobby.getId().intValue())))
                .andExpect(jsonPath("$.lobbyState", is(lobby.getLobbyState().toString())));
    }

    @Test
    void get_lobbyByToken_failed() throws Exception {
        Lobby lobby = dummyLobby();

        given(lobbyService.getLobbyFromToken(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No matching Lobby found"));

        MockHttpServletRequestBuilder getRequest = get("/lobby?lobbyToken=invalid").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isNotFound())
                .andExpect(status().reason("No matching Lobby found"));
    }

    @Test
    void put_joinLobby_success() throws Exception {
        Lobby lobby = dummyLobby();
        User user = dummyUser();
        lobby.getPlayerList().add(user);
        lobby.setNumberOfPlayers(lobby.getPlayerList().size());

        given(lobbyService.joinLobby("testtoken", "testtokenUser"))
                .willReturn(lobby);

        MockHttpServletRequestBuilder putRequest = put("/lobby?joinToken=testtoken&userToken=testtokenUser").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$.lobbyName", is(lobby.getLobbyName())))
                .andExpect(jsonPath("$.numberOfPlayers", is(1)))
                .andExpect(jsonPath("$.id", is(lobby.getId().intValue())));
    }

    @Test
    void put_joinLobby_overfull() throws Exception {
        Lobby lobby = dummyLobby();
        User user = dummyUser();
        lobby.setNumberOfPlayers(7);

        given(lobbyService.joinLobby("testtoken", "testtokenUser"))
                .willThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Lobby is full"));

        MockHttpServletRequestBuilder putRequest = put("/lobby?joinToken=testtoken&userToken=testtokenUser").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isForbidden())
                .andExpect(status().reason("Lobby is full"));
    }

    @Test
    void put_joinLobby_duplicate() throws Exception {
        Lobby lobby = dummyLobby();
        User user = dummyUser();
        lobby.getPlayerList().add(user);
        lobby.setNumberOfPlayers(lobby.getPlayerList().size());

        given(lobbyService.joinLobby("testtoken", "testtokenUser"))
                .willThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "User is already in Lobby"));

        MockHttpServletRequestBuilder putRequest = put("/lobby?joinToken=testtoken&userToken=testtokenUser").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isForbidden())
                .andExpect(status().reason("User is already in Lobby"));
    }

    @Test
    void put_joinLobby_invalidToken() throws Exception {
        Lobby lobby = dummyLobby();
        User user = dummyUser();

        given(lobbyService.joinLobby("invalid_token", "testtokenUser"))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby not found"));

        MockHttpServletRequestBuilder putRequest = put("/lobby?joinToken=invalid_token&userToken=testtokenUser").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isNotFound())
                .andExpect(status().reason("Lobby not found"));
    }

    @Test
    void delete_removePlayer_success() throws Exception {
        Lobby lobby = dummyLobby();
        User user = dummyUser();
        lobby.getPlayerList().add(user);
        lobby.setNumberOfPlayers(lobby.getPlayerList().size());


        MockHttpServletRequestBuilder delRequest = delete("/lobby?lobbyToken=testtoken&userToken=testtokenUser").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(delRequest).andExpect(status().isOk());
    }


    @Test
    void delete_removePlayer_invalidLobby() throws Exception {
        Lobby lobby = dummyLobby();
        User user = dummyUser();
        lobby.getPlayerList().add(user);
        lobby.setNumberOfPlayers(lobby.getPlayerList().size());

        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby not found")).
                when(lobbyService).leaveLobby("invalid_testtoken", "testtokenUser");

        MockHttpServletRequestBuilder delRequest = delete("/lobby?lobbyToken=invalid_testtoken&userToken=testtokenUser").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(delRequest).andExpect(status().isNotFound())
                .andExpect(status().reason("Lobby not found"));
    }

    @Test
    void delete_removePlayer_invalidPlayer() throws Exception {
        Lobby lobby = dummyLobby();
        User user = dummyUser();
        lobby.getPlayerList().add(user);
        lobby.setNumberOfPlayers(lobby.getPlayerList().size());

        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no User with requested Token")).
                when(lobbyService).leaveLobby("testtoken", "invalid_testtokenUser");

        MockHttpServletRequestBuilder delRequest = delete("/lobby?lobbyToken=testtoken&userToken=invalid_testtokenUser").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(delRequest).andExpect(status().isNotFound())
                .andExpect(status().reason("There is no User with requested Token"));
    }


    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     *
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }
}