package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Bot;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.helpers.WordFileHandler;
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


class GameServiceTest {


    @Mock
    private GameRepository gameRepository;
    @Mock
    private UserService userService;
    @Mock
    private LobbyService lobbyService;
    @Mock
    private ChatService chatService;
    @Mock
    private BotService botService;


    @InjectMocks
    GameService gameService;

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
        testGame.setCurrentRound(0);
        testGame.setGuesser(0);
        testGame.getPlayerList().add(testUser);
        testGame.setBotsClueGiven(false);
        testGame.setBotsVoted(false);

        testUser.setUsername("UserName");
        testUser.setPassword("PassWord");
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setToken("UserToken");
        testUser.setId(1L);
        testUser.setGame(testGame);
        testUser.setVoted(false);


//        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);
        Mockito.when(gameRepository.findByToken(testGame.getToken())).thenReturn(testGame);
        Mockito.when(gameRepository.findByToken("INVALID_TOKEN")).thenReturn(null);
        Mockito.when(userService.getUserFromToken(testUser.getToken())).thenReturn(testUser);
        Mockito.when(botService.botclue(Mockito.any(), Mockito.anyString())).thenReturn("BOT_CLUE");

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

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.setPlayerReady(testGame.getToken(), testUser.getToken()));

        assertEquals("404 NOT_FOUND \"Could not set player ready\"", exception.getMessage());
    }

    Game createGame_helper(Lobby lobby){
        //this method need to be the same as the beginning of the GameService.createGame
        Game newGame = new Game();

        List<User> userList = new ArrayList<>();
        userList.addAll(lobby.getPlayerList());
        List<Bot> botList = new ArrayList<>();
        botList.addAll(lobby.getBotList());
        List<String> clueList = new ArrayList<>();
        List<String> checkList = new ArrayList<>();
        List<Integer> voteList = new ArrayList<>();
        for(int a = 0; a<5; a++){
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
        Lobby testLobby = new Lobby();
        testLobby.setLobbyToken("LOBBY_TOKEN");
        testLobby.getPlayerList().add(testUser);
        testLobby.getBotList().add(testBot);
        Game dummyGame = createGame_helper(testLobby); // Mockito.mock(Game.class);
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(dummyGame);


        Game newGame = gameService.createGame(testLobby, "ANY_TOKEN");

        assertTrue(newGame.getBotList().containsAll(testLobby.getBotList()));
        assertTrue(newGame.getPlayerList().containsAll(testLobby.getPlayerList()));
        assertEquals(testLobby.getLobbyToken(), newGame.getToken());
        assertTrue(newGame.getVoteList().stream().allMatch(i -> i == 0));
        assertEquals(65, newGame.getMysteryWords().size());
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
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(dummyGame);


        Game newGame = gameService.createGame(testLobby, "ANY_TOKEN");

        assertTrue(newGame.getBotList().isEmpty());
        assertTrue(newGame.getPlayerList().containsAll(testLobby.getPlayerList()));
        assertEquals(testLobby.getLobbyToken(), newGame.getToken());
        assertTrue(newGame.getVoteList().stream().allMatch(i -> i == 0));
//        assertEquals(65, newGame.getMysteryWords().size());       //TODO why does wordservice return 67 in some cases
        assertEquals(newGame, testUser.getGame());
        assertNotNull(newGame.getClueList());
        assertNotNull(newGame.getChecklist());
    }

    //todo:make guesser random again
   /* @Test
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
    }*/

    @Test
    void addVote_success() {
        testGame.setVoteList(new ArrayList<Integer>(Collections.nCopies(5,0)));

        Mockito.when(gameRepository.findByToken(testGame.getToken())).thenReturn(testGame);

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
        testGame.setVoteList(new ArrayList<Integer>(Collections.nCopies(5,0)));

        Mockito.when(gameRepository.findByToken(testGame.getToken())).thenReturn(testGame);

        gameService.addVote(testGame.getToken(), testUser.getToken(), 0);
        gameService.addVote(testGame.getToken(), testUser.getToken(), 0);
        gameService.addVote(testGame.getToken(), testUser.getToken(), 4);

        assertEquals(1, testGame.getVoteList().get(4));
        assertEquals(2, testGame.getVoteList().get(0));
        assertEquals(4, testGame.getVoteList().stream().mapToInt(Integer::intValue).sum());
        assertTrue(testUser.getVoted());
    }

    @Test
    void addVote_invalid_game_token() {
        testGame.setVoteList(new ArrayList<Integer>(Collections.nCopies(5,0)));

//        Mockito.when(gameRepository.findByToken("INVALID_TOKEN")).thenReturn(testGame);

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.addVote("INVALID_TOKEN", testUser.getToken(), 1));

        assertEquals("404 NOT_FOUND \"No matching Game found\"", exception.getMessage());
        assertEquals(0, testGame.getVoteList().get(0));
        assertFalse(testUser.getVoted());
    }

    @Test
    void addVote_invalid_user_token() {
        testGame.setVoteList(new ArrayList<Integer>(Collections.nCopies(5,0)));

        Mockito.when(gameRepository.findByToken(testGame.getToken())).thenReturn(testGame);
        Mockito.when(userService.getUserFromToken("INVALID_USER_TOKEN"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no User with requested Token"));

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.addVote(testGame.getToken(), "INVALID_USER_TOKEN", 0));

        assertEquals("404 NOT_FOUND \"There is no User with requested Token\"", exception.getMessage());
        assertEquals(0, testGame.getVoteList().get(0));
        assertFalse(testUser.getVoted());
    }

    @Test
    void addVote_invalid_vote() {
        testGame.setVoteList(new ArrayList<Integer>(Collections.nCopies(5,0)));

        Mockito.when(gameRepository.findByToken(testGame.getToken())).thenReturn(testGame);

        int invalid_index = -1;
        Exception exception = Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> gameService.addVote(testGame.getToken(), testUser.getToken(), invalid_index));

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
        assertNull(testGame.getTopic());
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


        Mockito.when(userService.getUserFromToken(userToKick.getToken())).thenReturn(userToKick);
        Mockito.when(lobbyService.getLobbyFromToken(testLobby.getLobbyToken())).thenReturn(testLobby);
        Mockito.doNothing().when(chatService).leaveChat(testGame.getToken(), userToKick.getToken());
        Mockito.doCallRealMethod().when(userService).leaveGame(userToKick);
        Mockito.doCallRealMethod().when(userService).leaveLobby(userToKick);
        gameService.removeUser(userToKick.getToken(), testGame.getToken());

        assertFalse(testGame.getPlayerList().contains(userToKick));
        assertNull(userToKick.getGame());
        assertNull(userToKick.getLobby());
    }

    @Test
    void removeUser_last_user() {
        Lobby testLobby = new Lobby();
        Bot testBot = new Bot();
        testGame.getBotList().add(testBot);
        testLobby.setLobbyToken(testGame.getToken());       // in createGame game token is defined same as lobby token

        Mockito.when(lobbyService.getLobbyFromToken(testLobby.getLobbyToken())).thenReturn(testLobby);

        gameService.removeUser(testUser.getToken(), testGame.getToken());

        assertFalse(testGame.getPlayerList().contains(testUser));
        assertNull(testUser.getGame());
        assertNull(testUser.getLobby());
        Mockito.verify(gameRepository, Mockito.times(1)).delete(testGame);
        Mockito.verify(lobbyService, Mockito.times(1)).deleteLobby(testLobby);
        Mockito.verify(gameRepository, Mockito.times(1)).delete(testGame);
    }

    //TODO if we check if game and user token are valid we need two more test.

  /*  @Test
    void addClue_success() {
        String clue = "valid";
        testUser.setGaveClue(false);
        testGame.setTopic("Topic");
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
    }
*/
 /*   @Test
    void addClue_with_bot_success() {
        String clue = "valid";
        testUser.setGaveClue(false);
        testGame.setTopic("Topic");
        User otherUser = new User();
        otherUser.setGaveClue(false);
        testGame.getPlayerList().add(otherUser);
        User guesser = new User();      // guesser is needed to make sure w dont eval clue to early
        guesser.setGaveClue(false);
        testGame.getPlayerList().add(guesser);
        Bot testBot = new Bot();
        testBot.setDifficulty(Difficulty.NEUTRAL);
        testGame.getBotList().add(testBot);
        Mockito.when(botService.botclue(Mockito.any(), Mockito.anyString())).thenReturn("BOT_CLUE");

        Game newGame = gameService.addClue(testUser.getToken(), testGame.getToken(), clue);

        assertTrue(testUser.getGaveClue());
        assertTrue(newGame.getChecklist().contains(clue));
        assertTrue(newGame.getChecklist().contains("BOT_CLUE"));
        assertFalse(newGame.getClueList().contains(clue));
    }
*/
 /*   @Test
    void addClue_invalid_clue() {
        String clue = "invalid_clue";
        testUser.setGaveClue(false);
        testGame.setTopic("Topic");
        User guesser = new User();      // guesser is needed to make sure w dont eval clue to early
        guesser.setGaveClue(false);
        testGame.getPlayerList().add(guesser);

        Game newGame = gameService.addClue(testUser.getToken(), testGame.getToken(), clue);

        assertTrue(testUser.getGaveClue());
        assertFalse(testGame.getChecklist().contains(clue));
        assertTrue(testGame.getChecklist().contains("CENSORED"));
    }

  */
/*
    @Test
    void addClue_clue_equals_topic() {
        testUser.setGaveClue(false);
        testGame.setTopic("Topic");
        String clue = testGame.getTopic();
        User guesser = new User();      // guesser is needed to make sure w dont eval clue to early
        guesser.setGaveClue(false);
        testGame.getPlayerList().add(guesser);

        Game newGame = gameService.addClue(testUser.getToken(), testGame.getToken(), clue);

        assertTrue(testUser.getGaveClue());
        assertFalse(testGame.getChecklist().contains(true));
        assertTrue(testGame.getChecklist().contains("CENSORED"));
    }
*/
    @Test
    void addClue_has_already_given_clue() {
        String clue = "clue";
        testUser.setGaveClue(true);
        testGame.setTopic("Topic");
        User guesser = new User();      // guesser is needed to make sure w dont eval clue to early
        guesser.setGaveClue(false);
        testGame.getPlayerList().add(guesser);

        Exception exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> gameService.addClue(testUser.getToken(), testGame.getToken(), clue));

        assertTrue(testUser.getGaveClue());
        assertFalse(testGame.getChecklist().contains(clue));
        assertFalse(testGame.getChecklist().contains("CENSORED"));
        assertEquals("403 FORBIDDEN \"user already gave clue\"", exception.getMessage());
    }
/*
    @Test
    void addClue_last_clue_success() {
        String clue = "valid";
        testUser.setGaveClue(false);
        testGame.setTopic("Topic");
        User guesser = new User();      // guesser is needed to make sure w dont eval clue to early
        guesser.setGaveClue(false);
        testGame.getPlayerList().add(guesser);

        Game newGame = gameService.addClue(testUser.getToken(), testGame.getToken(), clue);

        assertTrue(testUser.getGaveClue());
        assertTrue(testGame.getChecklist().contains(clue));
        assertTrue(testGame.getClueList().contains(clue));
    }
*//*
    @Test
    void addClue_remove_duplicate() {
        String clue = "valid";
        testUser.setGaveClue(false);
        testGame.setTopic("Topic");
        User otherUser = new User();
        otherUser.setToken("OTHER_TOKEN");
        otherUser.setGaveClue(false);
        testGame.getPlayerList().add(otherUser);
        User guesser = new User();      // guesser is needed to make sure w dont eval clue to early
        guesser.setGaveClue(false);
        testGame.getPlayerList().add(guesser);
        Mockito.when(userService.getUserFromToken(otherUser.getToken())).thenReturn(otherUser);

        gameService.addClue(otherUser.getToken(), testGame.getToken(), clue);
        Game newGame = gameService.addClue(testUser.getToken(), testGame.getToken(), clue);

        assertTrue(testUser.getGaveClue());
        assertFalse(testGame.getClueList().contains(clue));
        assertTrue(testGame.getClueList().containsAll(Arrays.asList("CENSORED", "CENSORED")));
    }
*//*
    @Test
    void makeGuess_success() {
        testGame.setTopic("Topic");

        Game newGame = gameService.makeGuess(testGame.getToken(),testUser.getToken(), testGame.getTopic());

        assertFalse(testGame.getGuessCorrect());
    }
    */
/*
    @Test
    void makeGuess_not_correct() {
        testGame.setTopic("Topic");

        Game newGame = gameService.makeGuess(testGame.getToken(), testUser.getToken() ,"other_topic");

        assertFalse(testGame.getGuessCorrect());
    }
*/
    @Test
    void nextRound_success() {
        testGame.setCurrentRound(0);
        testGame.setGuesser(0);
        testGame.setGuessCorrect(true);
        testGame.setTopic("Topic");
        testGame.getVoteList().add(1337);
        testGame.getClueList().add("clue");
        testGame.getChecklist().add("check");
        testGame.getPlayerList().add(new User());

        Game newGame = gameService.nextRound(testGame.getToken());

        assertEquals(1, newGame.getCurrentRound());
        assertEquals(1, newGame.getGuesser());
        assertNull(newGame.getGuessCorrect());
        assertNull(newGame.getTopic());
//        assertTrue(newGame.getVoteList().isEmpty());
        assertTrue(newGame.getVoteList().stream().allMatch(i -> i == 0));
        assertTrue(newGame.getClueList().isEmpty());
        assertTrue(newGame.getChecklist().isEmpty());
        assertFalse(testUser.isUnityReady());
        assertFalse(testUser.getVoted());
        assertFalse(testUser.getGaveClue());
    }

}



























