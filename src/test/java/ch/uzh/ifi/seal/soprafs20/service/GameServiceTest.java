package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Bot;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class GameServiceTest {

    @Mock
    private GameRepository gameRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    GameService gameService;

    private Game testGame;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        // given
        testGame = new Game();
        testGame.setId(1L);
        testGame.setVersion(0);
        testGame.setToken("Token_Aa0Bb1");
        testGame.setRound(0);
        testGame.setGuesser(0);

        testUser = new User();
        testUser.setUsername("UserName");
        testUser.setPassword("PassWord");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("UserToken");
        testUser.setId(1L);
        testUser.setGame(testGame);


//        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);
        Mockito.when(gameRepository.findByToken(testGame.getToken())).thenReturn(testGame);
        Mockito.when(gameRepository.findByToken("INVALID_TOKEN")).thenReturn(null);

    }


    @Test
    void getGames() {
        Mockito.doReturn(Collections.singletonList(testGame)).when(gameRepository).findAll();

        List<Game> gameList = gameService.getGames();

        assertTrue(gameList.contains(testGame));
    }

    @Test
    void getGameFromToken_success() {

        Game fetched_game = gameService.getGameFromToken(testGame.getToken());

        assertEquals(testGame.getId(), fetched_game.getId());
        assertEquals(testGame.getToken(), fetched_game.getToken());
    }

    @Test
    void getGameFromToken_invalid_token() {
        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.getGameFromToken("INVALID_TOKEN"));

        assertEquals("404 NOT_FOUND \"No matching Game found\"", exception.getMessage());
    }

    @Test
    void setPlayerReady_success() {
        Mockito.when(userService.getUserFromToken(testUser.getToken())).thenReturn(testUser);

        gameService.setPlayerReady(testGame.getToken(), testUser.getToken());

        assertTrue(testUser.isUnityReady());
    }

    @Test
    void setPlayerReady_invalid_game_token() {
        Mockito.when(userService.getUserFromToken(testUser.getToken())).thenReturn(testUser);

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.setPlayerReady("INVALID_TOKEN", testUser.getToken()));

        assertEquals("404 NOT_FOUND \"Could not set player ready\"", exception.getMessage());
    }

    @Test
    void setPlayerReady_invalid_user_token() {
        Mockito.when(userService.getUserFromToken("INVALID_USER_TOKEN"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no User with requested Token"));

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.setPlayerReady(testGame.getToken(), "INVALID_USER_TOKEN"));

        assertEquals("404 NOT_FOUND \"There is no User with requested Token\"", exception.getMessage());
    }

    @Test
    void setPlayerReady_game_user_mismatch() {
        Game otherGame = new Game();
        otherGame.setToken("OTHER_TOKEN");
        testUser.setGame(otherGame);
        Mockito.when(userService.getUserFromToken(testUser.getToken())).thenReturn(testUser);

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.setPlayerReady(testGame.getToken(), testUser.getToken()));

        assertEquals("404 NOT_FOUND \"Could not set player ready\"", exception.getMessage());
    }

    @Test
    void createGame_success() {
        Game dummyGame = new Game(); // Mockito.mock(Game.class);
        Bot testBot = new Bot();
        Lobby testLobby = new Lobby();
        testLobby.setLobbyToken("LOBBY_TOKEN");
        testLobby.getPlayerList().add(testUser);
        testLobby.getBotList().add(testBot);
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(dummyGame);


        Game newGame = gameService.createGame(testLobby, "ANY_TOKEN");

        assertTrue(newGame.getBotList().containsAll(testLobby.getBotList()));
        assertTrue(newGame.getPlayerList().containsAll(testLobby.getPlayerList()));
        assertEquals(testLobby.getLobbyToken(), newGame.getToken());
        assertTrue(newGame.getVoteList().stream().allMatch(i -> i == 0));
        assertEquals(65, newGame.getMysteryWords().size());
        assertEquals(newGame, testBot.getGame());
        assertEquals(newGame, testUser.getGame());
    }


    @Test
    void createGame_without_bots() {
        Game dummyGame = new Game(); // Mockito.mock(Game.class);
        Lobby testLobby = new Lobby();
        testLobby.setLobbyToken("LOBBY_TOKEN");
        testLobby.getPlayerList().add(testUser);
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(dummyGame);


        Game newGame = gameService.createGame(testLobby, "ANY_TOKEN");

        assertTrue(newGame.getBotList().isEmpty());
        assertTrue(newGame.getPlayerList().containsAll(testLobby.getPlayerList()));
        assertEquals(testLobby.getLobbyToken(), newGame.getToken());
        assertTrue(newGame.getVoteList().stream().allMatch(i -> i == 0));
        assertEquals(65, newGame.getMysteryWords().size());
        assertEquals(newGame, testUser.getGame());
    }

    @Test
    void createGame_without_players() {
        Game dummyGame = new Game(); // Mockito.mock(Game.class);
        Bot testBot = new Bot();
        Lobby testLobby = new Lobby();
        testLobby.setLobbyToken("LOBBY_TOKEN");
        testLobby.getBotList().add(testBot);
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(dummyGame);

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> gameService.createGame(testLobby, "ANY_TOKEN"));

        assertEquals("bound must be positive", exception.getMessage());
        Mockito.verify(gameRepository, Mockito.times(0)).flush();
    }

    @Test
    void addVote_success() {
        Game dummyGame = new Game();
        Lobby testLobby = new Lobby();
        testLobby.setLobbyToken("LOBBY_TOKEN");
        testLobby.getPlayerList().add(testUser);
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(dummyGame);
        Game newGame = gameService.createGame(testLobby, "ANY_TOKEN");
        Mockito.when(gameRepository.findByToken(newGame.getToken())).thenReturn(dummyGame);

        gameService.addVote(newGame.getToken(), 1);
        gameService.addVote(newGame.getToken(), 1);
        gameService.addVote(newGame.getToken(), 5);

        assertEquals(1, newGame.getVoteList().get(4));
        assertEquals(2, newGame.getVoteList().get(0));
    }

    @Test
    void addVote_invalid_game_token() {
        Game dummyGame = new Game();
        Lobby testLobby = new Lobby();
        testLobby.setLobbyToken("LOBBY_TOKEN");
        testLobby.getPlayerList().add(testUser);
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(dummyGame);
        Game newGame = gameService.createGame(testLobby, "ANY_TOKEN");
        Mockito.when(gameRepository.findByToken(newGame.getToken())).thenReturn(dummyGame);

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.addVote("INVALID_TOKEN", 1));

        assertEquals("404 NOT_FOUND \"No matching Game found\"", exception.getMessage());
        assertEquals(0, newGame.getVoteList().get(0));
    }

    @Test
    void addVote_invalid_vote() {
        Game dummyGame = new Game();
        Lobby testLobby = new Lobby();
        testLobby.setLobbyToken("LOBBY_TOKEN");
        testLobby.getPlayerList().add(testUser);
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(dummyGame);
        Game newGame = gameService.createGame(testLobby, "ANY_TOKEN");
        Mockito.when(gameRepository.findByToken(newGame.getToken())).thenReturn(dummyGame);

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.addVote(newGame.getToken(), 0));

        assertEquals("404 NOT_FOUND \"int has to be [1:5]\"", exception.getMessage());
        assertEquals(0, newGame.getVoteList().get(0));
    }
}