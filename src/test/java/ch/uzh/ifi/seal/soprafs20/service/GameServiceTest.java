package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.Difficulty;
import ch.uzh.ifi.seal.soprafs20.constant.LobbyStatus;
import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Bot;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.helpers.WordFileHandler;
import ch.uzh.ifi.seal.soprafs20.repository.BotRepository;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


class GameServiceTest {

    @InjectMocks
    GameService gameService;

    @Mock
    private GameRepository gameRepository;
    @Mock
    private BotRepository botRepository;
    @Mock
    private UserService userService;
    @Mock
    private LobbyService lobbyService;
    @Mock
    private ChatService chatService;
    @Mock
    private BotService botService;

    private Game testGame;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        // given
        testGame = new Game();
        testUser = new User();


        testGame.setId(1L);
        testGame.setVersion(0);
        testGame.setToken("Token_Aa0Bb1");
        testGame.setTopic("Topic");
        testGame.setCurrentRound(0);
        testGame.setGuesser(0);
        testGame.getPlayerList().add(testUser);
        testGame.setBotsClueGiven(false);
        testGame.setBotsVoted(false);
        testGame.setVoteList(new ArrayList<>(Collections.nCopies(5, 0)));

        testUser.setUsername("UserName");
        testUser.setPassword("PassWord");
        testUser.setStatus(UserStatus.ONLINE);
        testUser.setToken("UserToken");
        testUser.setId(2L);
        testUser.setGame(testGame);
        testUser.setVoted(false);
        testUser.setTotalClues(0);
        testUser.setTotalScore(0);
        testUser.setGuessesCorrect(0);
        testUser.setGuessesMade(0);
        testUser.setInvalidClues(0);
        testUser.setGuessesMadeLife(0);
        testUser.setTotalCluesLife(0);
        testUser.setInvalidCluesLife(0);
        testUser.setGuessesCorrectLife(0);

        when(gameRepository.save(Mockito.any())).thenReturn(testGame);
        when(gameRepository.findByToken(testGame.getToken())).thenReturn(testGame);
        when(gameRepository.findByToken("INVALID_TOKEN")).thenReturn(null);
        when(userService.getUserFromToken(testUser.getToken())).thenReturn(testUser);
        when(botService.botClue(Difficulty.NEUTRAL, testGame.getTopic())).thenReturn("BOT_CLUE");

        Mockito.doCallRealMethod().when(userService).leaveGame(testUser);
        Mockito.doCallRealMethod().when(userService).leaveLobby(testUser);
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

        gameService.setPlayerReady(testGame.getToken(), testUser.getToken());

        assertTrue(testUser.isUnityReady());
    }

    @Test
    void setPlayerReady_invalid_game_token() {

        String userToken = testUser.getToken();
        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.setPlayerReady("INVALID_TOKEN", userToken));

        assertEquals("404 NOT_FOUND \"Could not set player ready\"", exception.getMessage());
    }

    @Test
    void setPlayerReady_invalid_user_token() {
        when(userService.getUserFromToken("INVALID_USER_TOKEN"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no User with requested Token"));

        String gameToken = testGame.getToken();
        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.setPlayerReady(gameToken, "INVALID_USER_TOKEN"));

        assertEquals("404 NOT_FOUND \"There is no User with requested Token\"", exception.getMessage());
    }

    @Test
    void setPlayerReady_game_user_mismatch() {
        Game otherGame = new Game();
        otherGame.setToken("OTHER_TOKEN");
        testUser.setGame(otherGame);

        String gameToken = testGame.getToken();
        String userToken = testUser.getToken();
        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.setPlayerReady(gameToken, userToken));

        assertEquals("404 NOT_FOUND \"Could not set player ready\"", exception.getMessage());
    }

    Game createGame_helper(Lobby lobby) {
        //this method need to be the same as the beginning of the GameService.createGame
        Game newGame = new Game();

        List<User> userList = new ArrayList<>(lobby.getPlayerList());
        List<Bot> botList = new ArrayList<>(lobby.getBotList());
        List<String> clueList = new ArrayList<>();
        List<String> checkList = new ArrayList<>();
        List<Integer> voteList = new ArrayList<>();
        for (int a = 0; a < 5; a++) {
            voteList.add(0);
        }

        newGame.setBotList(botList);
        newGame.setPlayerList(userList);
        newGame.setToken(lobby.getLobbyToken());
        newGame.setCurrentRound(0);
        newGame.setVersion(1);
        newGame.setClueList(clueList);
        newGame.setVoteList(voteList);
        newGame.setCheckList(checkList);
        newGame.setGuessGiven(false);
        newGame.setGuesser(new Random().nextInt(userList.size()));
        newGame.setMysteryWords(WordFileHandler.getMysteryWords());

        return newGame;
    }

    @Test
    void createGame_success() {
        Bot testBot = new Bot();
        testBot.setId(1337L);
        Lobby testLobby = new Lobby();
        testLobby.setLobbyToken("LOBBY_TOKEN");
        testLobby.getPlayerList().add(testUser);
        testLobby.getBotList().add(testBot);
        Game dummyGame = createGame_helper(testLobby); // Mockito.mock(Game.class);
        when(gameRepository.save(Mockito.any())).thenReturn(dummyGame);


        Game newGame = gameService.createGame(testLobby);

        assertTrue(newGame.getBotList().containsAll(testLobby.getBotList()));
        assertTrue(newGame.getPlayerList().containsAll(testLobby.getPlayerList()));
        assertEquals(testLobby.getLobbyToken(), newGame.getToken());
        assertTrue(newGame.getVoteList().stream().allMatch(i -> i == 0));
        assertTrue(65 <= newGame.getMysteryWords().size());
        assertEquals(newGame, testBot.getGame());
        assertEquals(newGame, testUser.getGame());
        assertNotNull(newGame.getClueList());
        assertNotNull(newGame.getChecklist());
    }


    @Test
    void createGame_without_bots() {
        Lobby testLobby = new Lobby();
        testLobby.setLobbyToken("LOBBY_TOKEN");
        testLobby.getPlayerList().add(testUser);
        Game dummyGame = createGame_helper(testLobby); //new Game(); // Mockito.mock(Game.class);
        when(gameRepository.save(Mockito.any())).thenReturn(dummyGame);


        Game newGame = gameService.createGame(testLobby);

        assertTrue(newGame.getBotList().isEmpty());
        assertTrue(newGame.getPlayerList().containsAll(testLobby.getPlayerList()));
        assertEquals(testLobby.getLobbyToken(), newGame.getToken());
        assertTrue(newGame.getVoteList().stream().allMatch(i -> i == 0));
        assertEquals(65, newGame.getMysteryWords().size());
        assertEquals(newGame, testUser.getGame());
        assertNotNull(newGame.getClueList());
        assertNotNull(newGame.getChecklist());
    }

    @Test
    void createGame_without_players() {
        Game dummyGame = new Game(); // Mockito.mock(Game.class);
        Bot testBot = new Bot();
        Lobby testLobby = new Lobby();
        testLobby.setLobbyToken("LOBBY_TOKEN");
        testLobby.getBotList().add(testBot);
        when(gameRepository.save(Mockito.any())).thenReturn(dummyGame);

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> gameService.createGame(testLobby));

        assertEquals("bound must be positive", exception.getMessage());
        Mockito.verify(gameRepository, Mockito.times(0)).flush();
    }

    @Test
    void addVote_success() {


        gameService.addVote(testGame.getToken(), testUser.getToken(), 0);
        gameService.addVote(testGame.getToken(), testUser.getToken(), 0);
        gameService.addVote(testGame.getToken(), testUser.getToken(), 4);

        assertEquals(1, testGame.getVoteList().get(4));
        assertEquals(2, testGame.getVoteList().get(0));
        assertTrue(testUser.getVoted());
    }

    @Test
    void addVote_with_bot_success() {
        Bot testBot = new Bot();
        testGame.getBotList().add(testBot);

        gameService.addVote(testGame.getToken(), testUser.getToken(), 0);
        gameService.addVote(testGame.getToken(), testUser.getToken(), 0);
        gameService.addVote(testGame.getToken(), testUser.getToken(), 4);

        assertEquals(4, testGame.getVoteList().stream().mapToInt(Integer::intValue).sum());
        assertTrue(1 <= testGame.getVoteList().get(4));
        assertTrue(2 <= testGame.getVoteList().get(0));
        assertTrue(testUser.getVoted());
    }

    @Test
    void addVote_invalid_game_token() {

        String userToken = testUser.getToken();
        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.addVote("INVALID_TOKEN", userToken, 1));

        assertEquals("404 NOT_FOUND \"No matching Game found\"", exception.getMessage());
        assertEquals(0, testGame.getVoteList().get(0));
        assertFalse(testUser.getVoted());
    }

    @Test
    void addVote_invalid_user_token() {

        when(gameRepository.findByToken(testGame.getToken())).thenReturn(testGame);
        when(userService.getUserFromToken("INVALID_USER_TOKEN"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no User with requested Token"));

        String gameToken = testGame.getToken();
        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.addVote(gameToken, "INVALID_USER_TOKEN", 0));

        assertEquals("404 NOT_FOUND \"There is no User with requested Token\"", exception.getMessage());
        assertEquals(0, testGame.getVoteList().get(0));
        assertFalse(testUser.getVoted());
    }

    @Test
    void addVote_invalid_vote() {

        when(gameRepository.findByToken(testGame.getToken())).thenReturn(testGame);

        int invalid_index = -1;

        String gameToken = testGame.getToken();
        String userToken = testUser.getToken();
        Exception exception = Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> gameService.addVote(gameToken, userToken, invalid_index));

        assertEquals(String.format("Index %d out of bounds for length 5", invalid_index), exception.getMessage());
        assertEquals(0, testGame.getVoteList().get(0));
        assertFalse(testUser.getVoted());
    }

    @Test
    void setTopic_success() {

        testGame = gameService.setTopic(testGame.getToken(), "NEW_TOPIC");

        assertEquals("new_topic", testGame.getTopic());
    }

    @Test
    void setTopic_invalid_game_token() {

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.setTopic("INVALID_TOKEN", "NEW_TOPIC"));

        assertEquals("404 NOT_FOUND \"No matching Game found\"", exception.getMessage());
        assertNotEquals("NEW_TOPIC", testGame.getTopic());
    }

    @Test
    void checkAllPlayersAreConnected() {
        testGame.getPlayerList().get(0).setInGameTab(true);
        Lobby testLobby = new Lobby();
        testLobby.getPlayerList().add(testUser);

        when(lobbyService.getLobbyFromToken(testGame.getToken())).thenReturn(testLobby);

        gameService.checkAllPlayersAreConnected(testGame.getToken());

        assertEquals(UserStatus.ONLINE, testUser.getStatus());
        assertTrue(testLobby.getPlayerList().contains(testUser));
    }

    @Test
    void checkAllPlayersAreConnected_with_diconnected_player() {
        testGame.getPlayerList().get(0).setInGameTab(false);
        Lobby testLobby = new Lobby();
        testLobby.getPlayerList().add(testUser);

        when(lobbyService.getLobbyFromToken(testGame.getToken())).thenReturn(testLobby);

        gameService.checkAllPlayersAreConnected(testGame.getToken());

        assertEquals(UserStatus.OFFLINE, testUser.getStatus());
        assertFalse(testLobby.getPlayerList().contains(testUser));
    }

    @Test
    void removeUser_success() {
        Lobby testLobby = new Lobby();
        User userToKick = new User();
        userToKick.setToken("NEW_TOKEN");
        userToKick.setGame(testGame);
        userToKick.setLobby(testLobby);
        testLobby.setLobbyToken(testGame.getToken());       // in createGame game token is defined same as lobby token
        testLobby.getPlayerList().add(userToKick);
        testGame.getPlayerList().add(userToKick);


        when(userService.getUserFromToken(userToKick.getToken())).thenReturn(userToKick);
        when(lobbyService.getLobbyFromToken(testLobby.getLobbyToken())).thenReturn(testLobby);
        Mockito.doNothing().when(chatService).leaveChat(testGame.getToken(), userToKick.getToken());

        Mockito.doCallRealMethod().when(userService).leaveGame(userToKick);
        Mockito.doCallRealMethod().when(userService).leaveLobby(userToKick);
        gameService.removeUser(userToKick.getToken(), testGame.getToken());

        assertFalse(testGame.getPlayerList().contains(userToKick));
    }

    @Test
    void removeUser_last_user() {
        Lobby testLobby = new Lobby();
        Bot testBot = new Bot();
        testGame.getBotList().add(testBot);
        testLobby.setLobbyToken(testGame.getToken());       // in createGame game token is defined same as lobby token

        when(lobbyService.getLobbyFromToken(testLobby.getLobbyToken())).thenReturn(testLobby);

        gameService.removeUser(testUser.getToken(), testGame.getToken());

        assertFalse(testGame.getPlayerList().contains(testUser));
        Mockito.verify(gameRepository, Mockito.times(1)).delete(testGame);
    }

    @Test
    void addClue_success() {
        String clue = "valid";
        testUser.setGaveClue(false);
        User otherUser = new User();
        otherUser.setGaveClue(false);
        testGame.getPlayerList().add(otherUser);
        User guesser = new User();      // guesser is needed to make sure w dont eval clue to early
        guesser.setGaveClue(false);
        testGame.getPlayerList().add(guesser);

        Game newGame = gameService.addClue(testUser.getToken(), testGame.getToken(), clue);

        assertTrue(testUser.getGaveClue());
        assertTrue(newGame.getChecklist().contains(clue));
        assertFalse(newGame.getChecklist().contains("BOT_CLUE"));
        assertFalse(newGame.getClueList().contains(clue));
        assertEquals(1, testUser.getTotalClues());
    }


    @Test
    void addClue_with_bot_success() {
        String clue = "valid";
        testUser.setGaveClue(false);
        User otherUser = new User();
        otherUser.setGaveClue(false);
        testGame.getPlayerList().add(otherUser);
        User guesser = new User();      // guesser is needed to make sure w dont eval clue to early
        guesser.setGaveClue(false);
        testGame.getPlayerList().add(guesser);
        Bot testBot = new Bot();
        testBot.setDifficulty(Difficulty.NEUTRAL);
        testGame.getBotList().add(testBot);

        Game newGame = gameService.addClue(testUser.getToken(), testGame.getToken(), clue);

        assertTrue(testUser.getGaveClue());
        assertTrue(newGame.getChecklist().contains(clue));
        assertTrue(newGame.getChecklist().contains("BOT_CLUE"));
        assertFalse(newGame.getClueList().contains(clue));
    }

    @Test
    void addClue_invalid_clue() {
        String clue = "invalid_clue";
        testUser.setGaveClue(false);
        User guesser = new User();      // guesser is needed to make sure w dont eval clue to early
        guesser.setGaveClue(false);
        testGame.getPlayerList().add(guesser);

        gameService.addClue(testUser.getToken(), testGame.getToken(), clue);

        assertTrue(testUser.getGaveClue());
        assertFalse(testGame.getChecklist().contains(clue));
        assertTrue(testGame.getChecklist().contains("CENSORED"));
    }

    @Test
    void addClue_clue_equals_topic() {
        testUser.setGaveClue(false);
        String clue = testGame.getTopic();
        User guesser = new User();      // guesser is needed to make sure w dont eval clue to early
        guesser.setGaveClue(false);
        testGame.getPlayerList().add(guesser);

        gameService.addClue(testUser.getToken(), testGame.getToken(), clue);

        assertTrue(testUser.getGaveClue());
        assertTrue(testGame.getChecklist().contains("CENSORED"));
    }

    @Test
    void addClue_has_already_given_clue() {
        String clue = "clue";
        testUser.setGaveClue(true);
        User guesser = new User();      // guesser is needed to make sure w dont eval clue to early
        guesser.setGaveClue(false);
        testGame.getPlayerList().add(guesser);

        String gameToken = testGame.getToken();
        String userToken = testUser.getToken();
        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.addClue(userToken, gameToken, clue));

        assertTrue(testUser.getGaveClue());
        assertFalse(testGame.getChecklist().contains(clue));
        assertFalse(testGame.getChecklist().contains("CENSORED"));
        assertEquals("403 FORBIDDEN \"user already gave clue\"", exception.getMessage());
    }


    @Test
    void addClue_last_clue_success() {
        String clue = "valid";
        testUser.setGaveClue(false);
        User guesser = new User();      // guesser is needed to make sure w dont eval clue to early
        guesser.setGaveClue(false);
        testGame.getPlayerList().add(guesser);

        gameService.addClue(testUser.getToken(), testGame.getToken(), clue);

        assertTrue(testUser.getGaveClue());
        assertTrue(testGame.getChecklist().contains(clue));
        assertTrue(testGame.getClueList().contains(clue));
    }

    @Test
    void addClue_remove_duplicate() {
        String clue = "valid";
        testUser.setGaveClue(false);
        User otherUser = new User();
        otherUser.setToken("OTHER_TOKEN");
        otherUser.setGaveClue(false);
        otherUser.setTotalClues(0);
        otherUser.setTotalCluesLife(0);
        testGame.getPlayerList().add(otherUser);
        User guesser = new User();      // guesser is needed to make sure w dont eval clue to early
        guesser.setGaveClue(false);
        testGame.getPlayerList().add(guesser);
        when(userService.getUserFromToken(otherUser.getToken())).thenReturn(otherUser);

        gameService.addClue(otherUser.getToken(), testGame.getToken(), clue);
        gameService.addClue(testUser.getToken(), testGame.getToken(), clue);

        assertTrue(testUser.getGaveClue());
        assertFalse(testGame.getClueList().contains(clue));
        assertTrue(testGame.getClueList().containsAll(Arrays.asList("CENSORED", "CENSORED")));
    }

    @Test
    void makeGuess_success() {
        gameService.makeGuess(testGame.getToken(), testUser.getToken(), testGame.getTopic());

        assertTrue(testGame.getGuessCorrect());
    }

    @Test
    void makeGuess_not_correct() {
        gameService.makeGuess(testGame.getToken(), testUser.getToken(), "other_topic");

        assertFalse(testGame.getGuessCorrect());
    }

    @Test
    void makeGuess_skipped_guessing() {
        gameService.makeGuess(testGame.getToken(), testUser.getToken(), "null");

        assertEquals("no guess given", testGame.getGuess());
    }

    @Test
    void nextRound_success() {
        testGame.setCurrentRound(0);
        testGame.setGuesser(0);
        testGame.setGuessCorrect(true);
        testGame.getVoteList().add(1337);
        testGame.getClueList().add("clue");
        testGame.getChecklist().add("check");
        testGame.getPlayerList().add(new User());

        Game newGame = gameService.nextRound(testGame.getToken());

        assertEquals(1, newGame.getCurrentRound());
        assertEquals(1, newGame.getGuesser());
        assertNull(newGame.getGuessCorrect());
        assertNull(newGame.getTopic());
        assertTrue(newGame.getVoteList().stream().allMatch(i -> i == 0));
        assertTrue(newGame.getClueList().isEmpty());
        assertTrue(newGame.getChecklist().isEmpty());
        assertFalse(testUser.isUnityReady());
        assertFalse(testUser.getVoted());
        assertFalse(testUser.getGaveClue());
    }

    @Test
    void nextRound_false_guess() {
        testGame.setCurrentRound(0);
        testGame.setGuesser(0);
        testGame.setGuessCorrect(true);
        testGame.getVoteList().add(1337);
        testGame.getClueList().add("clue");
        testGame.getChecklist().add("check");
        testGame.getPlayerList().add(new User());
        testGame.setGuessCorrect(false);

        Game newGame = gameService.nextRound(testGame.getToken());

        assertEquals(2, newGame.getCurrentRound());
        assertEquals(1, newGame.getGuesser());
        assertNull(newGame.getGuessCorrect());
        assertNull(newGame.getTopic());
        assertTrue(newGame.getVoteList().stream().allMatch(i -> i == 0));
        assertTrue(newGame.getClueList().isEmpty());
        assertTrue(newGame.getChecklist().isEmpty());
        assertFalse(testUser.isUnityReady());
        assertFalse(testUser.getVoted());
        assertFalse(testUser.getGaveClue());
    }

    @Test
    void endGame_notLastPlayer() {
        testGame.getPlayerList().add(new User());
        testUser.setGamesPlayed(0);

        gameService.endGame(testGame.getToken(), testUser.getToken());

        assertEquals(1, testUser.getGamesPlayed());
        assertFalse(testGame.getPlayerList().contains(testUser));
    }

    @Test
    void endGame_lastPlayer() {
        Bot testBot = new Bot();
        testBot.setGame(testGame);
        testGame.getBotList().add(testBot);
        Lobby testLobby = new Lobby();
        testLobby.setLobbyState(LobbyStatus.INGAME);
        testUser.setGamesPlayed(0);
        when(lobbyService.getLobbyFromToken(testGame.getToken())).thenReturn(testLobby);
        Mockito.doNothing().when(botRepository).delete(testBot);

        gameService.endGame(testGame.getToken(), testUser.getToken());

        assertEquals(1, testUser.getGamesPlayed());
        assertFalse(testGame.getPlayerList().contains(testUser));
        assertEquals(LobbyStatus.OPEN, testLobby.getLobbyState());
        assertTrue(testGame.getBotList().isEmpty());
        Mockito.verify(botRepository, Mockito.times(1)).delete(testBot);
    }

}
