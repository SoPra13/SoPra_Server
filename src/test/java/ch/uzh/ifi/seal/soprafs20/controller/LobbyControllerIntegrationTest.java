package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.constant.LobbyStatus;
import ch.uzh.ifi.seal.soprafs20.constant.LobbyType;
import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Bot;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.BotRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.LobbyRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs20.service.BotService;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import ch.uzh.ifi.seal.soprafs20.service.LobbyService;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LobbyControllerIntegrationTest {

    @Qualifier("lobbyRepository")
    @Autowired
    LobbyRepository lobbyRepository;

    @Qualifier("gameRepository")
    @Autowired
    GameRepository gameRepository;

    @Qualifier("userRepository")
    @Autowired
    UserRepository userRepository;

    @Qualifier("botRepository")
    @Autowired
    BotRepository botRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    GameService gameService;

    @Autowired
    UserService userService;

    @Autowired
    BotService botService;

    @Autowired
    LobbyService lobbyService;


    private User testUser;
    private Bot testBot;
    private Game testGame;
    private Lobby testLobby;

    @BeforeEach
    void setup(){
        botRepository.deleteAll();
        userRepository.deleteAll();
        lobbyRepository.deleteAll();
        gameRepository.deleteAll();

        testBot = botService.createBot("FRIEND");
        testUser = new User();
        testLobby = new Lobby();
        testGame = new Game();

        testUser.setUsername("User");
        testUser.setPassword("PWD");
        testUser.setToken("ADMIN_TOKEN");       // this will be reset ramdom when created in repository
        testUser.setStatus(UserStatus.ONLINE);
        userService.createUser(testUser);
        testUser = userRepository.findByUsername("User");

        testLobby.setLobbyName("testLobby");
        testLobby.setLobbyToken("LOBBY_TOKEN");
        testLobby.setJoinToken("1729");
        testLobby.setLobbyState(LobbyStatus.OPEN);
        testLobby.setNumberOfPlayers(1);
        testLobby.setAdminToken(testUser.getToken());
        testLobby.setLobbyType(LobbyType.PUBLIC);
        testLobby = lobbyService.createLobby(testLobby, testUser.getToken());
    }

    @AfterEach
    void reset() {
        botRepository.deleteAll();
        userRepository.deleteAll();
        lobbyRepository.deleteAll();
        gameRepository.deleteAll();
    }

    @Test
    void put_startGame() throws Exception {


        MockHttpServletRequestBuilder putRequest = put("/lobby/" + testLobby.getLobbyToken() + "/game")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isCreated());

        assertTrue(gameRepository.findByToken(testLobby.getLobbyToken()).getPlayerList().contains(testUser));
    }

    @Test
    void put_startGame_invalid_lobbyToken_path() throws Exception {


        MockHttpServletRequestBuilder putRequest = put("/lobby/INVALID_TOKEN/game")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isNotFound());
    }

    @Test
    void put_addBot() throws Exception {


        MockHttpServletRequestBuilder putRequest = put("/lobby?lobbyToken=" + testLobby.getLobbyToken() +
                "&difficulty=FRIEND").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isCreated());

        assertFalse(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken()).getBotList().isEmpty());
    }

    @Test
    void put_addBot_invalid_lobbyToken() throws Exception {


        MockHttpServletRequestBuilder putRequest = put("/lobby?lobbyToken=INVALID_TOKEN" +
                "&difficulty=FRIEND").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isNotFound());

        assertTrue(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken()).getBotList().isEmpty());
    }

    @Test
    void put_addBot_lobby_full() throws Exception {
        testLobby.setNumberOfPlayers(7);

        MockHttpServletRequestBuilder putRequest = put("/lobby?lobbyToken=" + testLobby.getLobbyToken() +
                "&difficulty=FRIEND").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isForbidden());

        assertTrue(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken()).getBotList().isEmpty());
    }

    @Test
    void put_addBot_invalid_difficulty() throws Exception {


        MockHttpServletRequestBuilder putRequest = put("/lobby?lobbyToken=" + testLobby.getLobbyToken() +
                "&difficulty=INVALID_DIFFICULTY").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isNotFound());

        assertTrue(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken()).getBotList().isEmpty());
    }

    @Test
    void put_removeBot() throws Exception {

        MockHttpServletRequestBuilder putRequest = put("/lobby?lobbyToken=" + testLobby.getLobbyToken() +
                "&difficulty=FRIEND").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isCreated());
        testBot = lobbyRepository.findByLobbyToken(testLobby.getLobbyToken()).getBotList().get(0);
        MockHttpServletRequestBuilder delRequest = delete("/lobby?lobbyToken=" + testLobby.getLobbyToken() +
                "&botToken=" + testBot.getToken()).contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(delRequest).andExpect(status().isOk());

        assertTrue(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken()).getBotList().isEmpty());
    }

    @Test
    void put_removeBot_invalid_lobbyToken() throws Exception {

        MockHttpServletRequestBuilder putRequest = put("/lobby?lobbyToken=" + testLobby.getLobbyToken() +
                "&difficulty=FRIEND").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isCreated());
        testBot = lobbyRepository.findByLobbyToken(testLobby.getLobbyToken()).getBotList().get(0);
        MockHttpServletRequestBuilder delRequest = delete("/lobby?lobbyToken=INVALID_TOKEN" +
                "&botToken=" + testBot.getToken()).contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(delRequest).andExpect(status().isNotFound());

        assertFalse(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken()).getBotList().isEmpty());
    }

    @Test
    void put_removeBot_invalid_botToken() throws Exception {

        MockHttpServletRequestBuilder putRequest = put("/lobby?lobbyToken=" + testLobby.getLobbyToken() +
                "&difficulty=FRIEND").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isCreated());
        testBot = lobbyRepository.findByLobbyToken(testLobby.getLobbyToken()).getBotList().get(0);
        MockHttpServletRequestBuilder delRequest = delete("/lobby?lobbyToken=" + testLobby.getLobbyToken() +
                "&botToken=INVALID_TOKEN").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(delRequest).andExpect(status().isNotFound());

        assertFalse(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken()).getBotList().isEmpty());
    }

    @Test
    void put_setPlayerLobbyReady() throws Exception {
        MockHttpServletRequestBuilder putGame = put("/lobby/" + testLobby.getLobbyToken() + "/game")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putGame).andExpect(status().isCreated());

        MockHttpServletRequestBuilder putRequest = put("/lobby/ready?userToken=" + testUser.getToken() +
                "&lobbyToken=" + testLobby.getLobbyToken())
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isOk());

        assertTrue(testUser.isUnityReady());
    }

    @Test
    void put_setPlayerLobbyReady_invalid_userToken() throws Exception {
        MockHttpServletRequestBuilder putGame = put("/lobby/" + testLobby.getLobbyToken() + "/game")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putGame).andExpect(status().isCreated());

        MockHttpServletRequestBuilder putRequest = put("/lobby/ready?userToken=INVALID_TOKEN" +
                "&lobbyToken=" + testLobby.getLobbyToken())
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isNotFound());
    }

    @Test
    void put_setPlayerLobbyReady_invalid_gameToken() throws Exception {
        MockHttpServletRequestBuilder putGame = put("/lobby/" + testLobby.getLobbyToken() + "/game")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putGame).andExpect(status().isCreated());

        MockHttpServletRequestBuilder putRequest = put("/lobby/ready?userToken=" + testUser.getToken() +
                "&lobbyToken=INVALID_TOKEN")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isNotFound());
    }
}