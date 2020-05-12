package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.Difficulty;
import ch.uzh.ifi.seal.soprafs20.constant.LobbyStatus;
import ch.uzh.ifi.seal.soprafs20.constant.LobbyType;
import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Bot;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.LobbyRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyServiceTest {

    @Mock
    private LobbyRepository lobbyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private BotService botService;
    @Mock
    private ChatService chatService;

    @InjectMocks
    private LobbyService lobbyService;

    private Lobby testLobby;
    private User testAdmin;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        // given
        testAdmin = new User();
        testAdmin.setUsername("ADMIN");
        testAdmin.setPassword("12345;P");
        testAdmin.setToken("ADMIN_TOKEN");
        testAdmin.setStatus(UserStatus.ONLINE);

        testLobby = new Lobby();
        testLobby.setId(1L);
        testLobby.setLobbyName("NAME");
        testLobby.setLobbyToken("TOKEN");
        testLobby.setJoinToken("JOINTOKEN");
        testLobby.setLobbyState(LobbyStatus.OPEN);
        testLobby.setNumberOfPlayers(1);
        testLobby.setAdminToken(testAdmin.getToken());
        testLobby.setLobbyType(LobbyType.PUBLIC);

        testLobby.getPlayerList().add(testAdmin);
        testAdmin.setLobby(testLobby);

        Mockito.when(lobbyRepository.save(Mockito.any())).thenReturn(testLobby);
        Mockito.when(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken())).thenReturn(testLobby);
        Mockito.when(lobbyRepository.findByJoinToken(testLobby.getJoinToken())).thenReturn(testLobby);
        Mockito.doReturn(Collections.singletonList(testLobby)).when(lobbyRepository).findAll();

    }

    private Bot newBot() {
        Bot testBot = new Bot();
        testBot.setId(0L);
        testBot.setBotname("BOT");
        testBot.setToken("BOT_TOKEN");
        testBot.setDifficulty(Difficulty.NEUTRAL);
        return testBot;
    }


    @Test
    void getLobbies() {
        List<Lobby> get_all_lobbys = lobbyService.getLobbies();
        assertTrue(get_all_lobbys.contains(testLobby));
    }

    @Test
    void getLobbyFromToken_success() {
        Lobby fetched_Lobby = lobbyService.getLobbyFromToken("TOKEN");

        assertEquals(testLobby.getId(), fetched_Lobby.getId());
        assertEquals(testLobby.getLobbyToken(), fetched_Lobby.getLobbyToken());
        assertEquals(testLobby.getJoinToken(), fetched_Lobby.getJoinToken());
        assertEquals(testLobby.getAdminToken(), fetched_Lobby.getAdminToken());
    }

    @Test
    void getLobbyFromToken_invalid_token() {
        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.getLobbyFromToken("INVALID_TOKEN"));
        assertEquals("404 NOT_FOUND \"No matching Lobby found\"", exception.getMessage());
    }


    @Test
    void createLobby_success() {
        testLobby.setLobbyState(null);
        testLobby.setNumberOfPlayers(null);
        testLobby.setAdminToken(null);
        String test = testLobby.getLobbyToken();
        Mockito.when(lobbyRepository.findByJoinToken(String.valueOf(1729))).thenReturn(testLobby);
        Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(testAdmin);
        Mockito.when(userService.getUserFromToken(Mockito.any())).thenReturn(testAdmin);
        Mockito.doNothing().when(chatService).createChat(testLobby.getLobbyToken());

        Lobby newLobby = lobbyService.createLobby(testLobby, "ADMIN_TOKEN");

        assertEquals("ADMIN_TOKEN", newLobby.getAdminToken());
        assertEquals(1, newLobby.getNumberOfPlayers());
        assertEquals(LobbyStatus.OPEN, newLobby.getLobbyState());
        assertTrue(newLobby.getPlayerList().contains(testAdmin));
        Mockito.verify(lobbyRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(lobbyRepository, Mockito.times(1)).flush();
    }

    @Test
    void createLobby_invalid_ADMIN_TOKEN() {
        testLobby.setLobbyState(null);
        testLobby.setNumberOfPlayers(null);
        testLobby.setAdminToken(null);
        Mockito.when(userRepository.findByToken("INVALID_ADMIN_TOKEN")).thenReturn(null);
        Mockito.when(userService.getUserFromToken("INVALID_ADMIN_TOKEN"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no User with requested Token"));

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.createLobby(testLobby, "INVALID_ADMIN_TOKEN"));

        assertEquals("404 NOT_FOUND \"There is no User with requested Token\"", exception.getMessage());
    }

    @Test
    void joinLobby_success() {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setToken("NEW_TOKEN");

        Mockito.when(userService.getUserFromToken(newUser.getToken())).thenReturn(newUser);
        Mockito.when(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken())).thenReturn(testLobby);

        Lobby newLobby = lobbyService.joinLobby(testLobby.getJoinToken(), newUser.getToken());

        assertTrue(newLobby.getPlayerList().contains(newUser));
        assertEquals(2, newLobby.getNumberOfPlayers());

    }

    @Test
    void joinLobby_invalid_LOBBY_TOKEN() {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setToken("NEW_TOKEN");

        Mockito.when(lobbyRepository.findByLobbyToken("INVALID_TOKEN")).thenReturn(null);

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.joinLobby("INVALID_TOKEN", "NEW_TOKEN"));

        assertEquals("404 NOT_FOUND \"No matching Lobby found\"", exception.getMessage());

    }

    @Test
    void joinLobby_lobby_overfull() {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setToken("NEW_TOKEN");
        testLobby.setNumberOfPlayers(testLobby.maxPlayer);

        Mockito.when(userService.getUserFromToken(newUser.getToken())).thenReturn(newUser);
        Mockito.when(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken())).thenReturn(testLobby);

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.joinLobby("JOINTOKEN", "NEW_TOKEN"));

        assertEquals("403 FORBIDDEN \"Lobby is full\"", exception.getMessage());

    }

    @Test
    void joinLobby_duplicate_join() {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setToken("NEW_TOKEN");

        Mockito.when(userService.getUserFromToken(newUser.getToken())).thenReturn(newUser);
        Mockito.when(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken())).thenReturn(testLobby);

        Lobby givenLobby = lobbyService.joinLobby(testLobby.getJoinToken(), newUser.getToken());


        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.joinLobby(givenLobby.getJoinToken(), newUser.getToken()));

        assertEquals("403 FORBIDDEN \"User is already in Lobby\"", exception.getMessage());

    }

    @Test
    void joinLobby_invalid_USER_TOKEN() {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setToken("NEW_TOKEN");

        Mockito.when(userRepository.findByToken("INVALID_NEW_TOKEN")).thenReturn(null);
        Mockito.when(userService.getUserFromToken("INVALID_NEW_TOKEN"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no User with requested Token"));
        Mockito.when(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken())).thenReturn(testLobby);

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.joinLobby(testLobby.getJoinToken(), "INVALID_NEW_TOKEN"));

        assertEquals("404 NOT_FOUND \"There is no User with requested Token\"", exception.getMessage());

    }


    @Test
    void leaveLobby() {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setToken("NEW_TOKEN");

        Mockito.when(userService.getUserFromToken(newUser.getToken())).thenReturn(newUser);
        Mockito.when(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken())).thenReturn(testLobby);

        Lobby givenLobby = lobbyService.joinLobby(testLobby.getJoinToken(), newUser.getToken());

        lobbyService.leaveLobby(givenLobby.getLobbyToken(), newUser.getToken());

        assertFalse(testLobby.getPlayerList().contains(newUser));
    }

    @Test
    void leaveLobby_invalid_lobby_token() {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setToken("NEW_TOKEN");

        Mockito.when(userService.getUserFromToken(newUser.getToken())).thenReturn(newUser);
        Mockito.when(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken())).thenReturn(testLobby);
        Lobby givenLobby = lobbyService.joinLobby(testLobby.getJoinToken(), newUser.getToken());
        Mockito.when(lobbyRepository.findByLobbyToken("INVALID_TOKEN")).thenReturn(null);

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.leaveLobby("INVALID_TOKEN", "NEW_TOKEN"));

        assertEquals("404 NOT_FOUND \"Lobby not found\"", exception.getMessage());
        assertTrue(givenLobby.getPlayerList().contains(newUser));
    }

    @Test
    void leaveLobby_invalid_user_token() {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setToken("NEW_TOKEN");

        Mockito.when(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken())).thenReturn(testLobby);
        Mockito.when(userService.getUserFromToken(newUser.getToken())).thenReturn(newUser);
        Lobby givenLobby = lobbyService.joinLobby(testLobby.getJoinToken(), newUser.getToken());

        Mockito.when(userRepository.findByToken("INVALID_NEW_TOKEN")).thenReturn(null);
        Mockito.when(userService.getUserFromToken("INVALID_NEW_TOKEN"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no User with requested Token"));


        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.leaveLobby(testLobby.getLobbyToken(), "INVALID_NEW_TOKEN"));

        assertEquals("404 NOT_FOUND \"There is no User with requested Token\"", exception.getMessage());
        assertTrue(givenLobby.getPlayerList().contains(newUser));
    }

    @Test
    void leaveLobby_lastUser() {

        Mockito.when(userService.getUserFromToken("ADMIN_TOKEN")).thenReturn(testAdmin);
        Mockito.when(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken())).thenReturn(testLobby);

        lobbyService.leaveLobby(testLobby.getLobbyToken(), "ADMIN_TOKEN");

        Mockito.verify(lobbyRepository, Mockito.times(1)).delete(testLobby);
    }

    @Test
    void addBot_success() {
        Bot testBot = newBot();


        Mockito.when(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken())).thenReturn(testLobby);
        Mockito.when(botService.createBot(testBot.getDifficulty().toString())).thenReturn(testBot);

        Lobby newLobby = lobbyService.addBot(testLobby.getLobbyToken(), testBot.getDifficulty().toString());

        assertTrue(newLobby.getBotList().contains(testBot));
        assertEquals(2, newLobby.getNumberOfPlayers());
        assertEquals(testLobby, testBot.getLobby());

    }

    @Test
    void addBot_invalid_lobby_token() {
        Bot testBot = newBot();

        Mockito.when(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken())).thenReturn(testLobby);
        Mockito.when(botService.createBot(testBot.getDifficulty().toString())).thenReturn(testBot);

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.addBot("INVALID_TOKEN", "NEUTRAL"));


        assertEquals("404 NOT_FOUND \"Lobby not found\"", exception.getMessage());
        assertFalse(testLobby.getBotList().contains(testBot));
        assertNull(testBot.getLobby());
    }

    @Test
    void addBot_lobby_overfull() {
        Bot testBot = newBot();
        testLobby.setNumberOfPlayers(testLobby.maxPlayer);

        Mockito.when(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken())).thenReturn(testLobby);
        Mockito.when(botService.createBot(testBot.getDifficulty().toString())).thenReturn(testBot);

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.addBot(testLobby.getLobbyToken(), "NEUTRAL"));


        assertEquals("403 FORBIDDEN \"Lobby is full\"", exception.getMessage());
        assertFalse(testLobby.getBotList().contains(testBot));
        assertNull(testBot.getLobby());

    }

    @Test
    void addBot_invalid_difficulty() {
        Bot testBot = newBot();

        Mockito.when(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken())).thenReturn(testLobby);
        Mockito.when(botService.createBot("INVALID_DIFFICULTY"))
                .thenThrow(new IllegalArgumentException("ILLEGAL VALUE ENCOUNTERED"));

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                lobbyService.addBot(testLobby.getLobbyToken(), "INVALID_DIFFICULTY"));


        assertEquals("ILLEGAL VALUE ENCOUNTERED", exception.getMessage());
        assertFalse(testLobby.getBotList().contains(testBot));
        assertNull(testBot.getLobby());

    }


    @Test
    void removeBot() {
        Bot testBot = newBot();

        Mockito.when(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken())).thenReturn(testLobby);
        Mockito.when(botService.createBot(testBot.getDifficulty().toString())).thenReturn(testBot);
        Mockito.when(botService.getBotFromToken(testBot.getToken())).thenReturn(testBot);
        lobbyService.addBot(testLobby.getLobbyToken(), testBot.getDifficulty().toString());

        testLobby = lobbyService.removeBot(testLobby.getLobbyToken(), testBot.getToken());

        assertFalse(testLobby.getBotList().contains(testBot));
        assertEquals(1, testLobby.getNumberOfPlayers());
        assertNull(testBot.getLobby());
    }

    @Test
    void removeBot_invalid_lobby_token() {
        Bot testBot = newBot();

        Mockito.when(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken())).thenReturn(testLobby);
        Mockito.when(botService.createBot(testBot.getDifficulty().toString())).thenReturn(testBot);
        Mockito.when(botService.getBotFromToken(testBot.getToken())).thenReturn(testBot);
        lobbyService.addBot(testLobby.getLobbyToken(), testBot.getDifficulty().toString());


        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.removeBot("INVALID_TOKEN", testBot.getToken()));


        assertEquals("404 NOT_FOUND \"Lobby not found\"", exception.getMessage());
        assertTrue(testLobby.getBotList().contains(testBot));
        assertEquals(testLobby, testBot.getLobby());
        assertEquals(2, testLobby.getNumberOfPlayers());
    }

    @Test
    void removeBot_invalid_bot_token() {
        Bot testBot = newBot();

        Mockito.when(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken())).thenReturn(testLobby);
        Mockito.when(botService.createBot(testBot.getDifficulty().toString())).thenReturn(testBot);
        Mockito.when(botService.getBotFromToken(Mockito.anyString()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no Bot with requested Token"));
        lobbyService.addBot(testLobby.getLobbyToken(), testBot.getDifficulty().toString());


        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.removeBot(testLobby.getLobbyToken(), "INVALID_BOT_TOKEN"));


        assertEquals("404 NOT_FOUND \"There is no Bot with requested Token\"", exception.getMessage());
        assertTrue(testLobby.getBotList().contains(testBot));
        assertEquals(testLobby, testBot.getLobby());
        assertEquals(2, testLobby.getNumberOfPlayers());
    }

    @Test
    void removeBot_Bot_not_in_lobby() {
        Bot testBot = newBot();
        Bot otherBot = new Bot();
        otherBot.setId(1L);
        otherBot.setToken("OTHER_TOKEN");

        Mockito.when(lobbyRepository.findByLobbyToken(testLobby.getLobbyToken())).thenReturn(testLobby);
        Mockito.when(botService.createBot(testBot.getDifficulty().toString())).thenReturn(testBot);
        Mockito.when(botService.getBotFromToken(testBot.getToken())).thenReturn(testBot);
        Mockito.when(botService.getBotFromToken(otherBot.getToken())).thenReturn(otherBot);
        lobbyService.addBot(testLobby.getLobbyToken(), testBot.getDifficulty().toString());

        lobbyService.removeBot(testLobby.getLobbyToken(), otherBot.getToken());

//        assertEquals("404 NOT_FOUND \"There is no Bot with requested Token in the lobby\"", exception.getMessage());
        assertFalse(testLobby.getBotList().contains(otherBot));
        assertEquals(2, testLobby.getNumberOfPlayers());
        assertNotNull(testBot.getLobby());
        assertNull(otherBot.getLobby());
    }

    @Test
    void setPlayerReady_success() {
        Mockito.when(userService.getUserFromToken(testAdmin.getToken())).thenReturn(testAdmin);

        lobbyService.setPlayerReady(testLobby.getLobbyToken(), testAdmin.getToken());

        assertTrue(testAdmin.isLobbyReady());
    }

    @Test
    void setPlayerReady_invalid_lobby_token() {
        testAdmin.setLobbyReady(false);
        Mockito.when(userService.getUserFromToken(testAdmin.getToken())).thenReturn(testAdmin);

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.setPlayerReady("INVALID_LOBBY_TOKEN", testAdmin.getToken()));

        assertEquals("404 NOT_FOUND \"Could not set player ready\"", exception.getMessage());
        assertFalse(testAdmin.isLobbyReady());
    }

    @Test
    void setPlayerReady_invalid_bot_token() {
        testAdmin.setLobbyReady(false);
        Mockito.when(userService.getUserFromToken("INVALID_BOT_TOKEN"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no User with requested Token"));

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> lobbyService.setPlayerReady(testLobby.getLobbyToken(), "INVALID_BOT_TOKEN"));

        assertEquals("404 NOT_FOUND \"There is no User with requested Token\"", exception.getMessage());
        assertFalse(testAdmin.isLobbyReady());
    }

    @Test
    void deleteLobby() {

        lobbyService.deleteLobby(testLobby);

        Mockito.verify(lobbyRepository, Mockito.times(1)).delete(testLobby);
    }
}




