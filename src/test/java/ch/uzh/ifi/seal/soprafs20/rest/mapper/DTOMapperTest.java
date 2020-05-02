package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.constant.LobbyStatus;
import ch.uzh.ifi.seal.soprafs20.constant.LobbyType;
import ch.uzh.ifi.seal.soprafs20.constant.MessageType;
import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.Message;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation works.
 */
public class DTOMapperTest {
    @Test
    public void testGetChat_fromMessage_toChatGetDTO() {
        // create Message
        Message message = new Message();
        message.setUsername("username");
        message.setMessage("message");
        message.setMessageType(MessageType.NORMAL);

        // MAP -> Create user
        ChatGetDTO chat = DTOMapper.INSTANCE.convertEntitytoChatGetDTO(message);

        // check content
        assertEquals(message.getUsername(), chat.getUsername());
        assertEquals(message.getMessage(), chat.getMessage());
        assertEquals(message.getMessageType(), chat.getMessageType());
    }

    @Test
    public void testMessage_fromChatPostDTO_toMessage() {
        // create ChatPostDTO
        ChatPostDTO chat = new ChatPostDTO();
        chat.setMessage("message");

        // MAP -> Create user
        Message message = DTOMapper.INSTANCE.convertChatPostDTOtoEntity(chat);

        // check content
        assertEquals(chat.getMessage(), message.getMessage());
    }


    @Test
    public void testCreateUser_fromUserPostDTO_toUser_success() {
        // create UserPostDTO
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("password");
        userPostDTO.setUsername("username");

        // MAP -> Create user
        User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // check content
        assertEquals(userPostDTO.getPassword(), user.getPassword());
        assertEquals(userPostDTO.getUsername(), user.getUsername());
    }

    @Test
    public void testCreateUser_fromUserPutDTO_toUser_success() {
        // create UserPostDTO
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setPassword("password");
        userPutDTO.setUsername("username");
        userPutDTO.setColor(Color.getColor("red"));
        userPutDTO.setDarkMode(true);

        // MAP -> Create user
        User user = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);

        // check content
        assertEquals(userPutDTO.getPassword(), user.getPassword());
        assertEquals(userPutDTO.getUsername(), user.getUsername());
        assertEquals(userPutDTO.getColor(), user.getColor());
        assertEquals(userPutDTO.isDarkMode(), user.isDarkMode());
    }

    @Test
    public void testGetUser_fromUser_toUserGetDTO_success() {
        // create User
        User user = new User();
        user.setPassword("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1");

        // MAP -> Create UserGetDTO
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

        // check content
        assertEquals(user.getId(), userGetDTO.getId());
        assertEquals(user.getUsername(), userGetDTO.getUsername());
        assertEquals(user.getStatus(), userGetDTO.getStatus());
    }

    @Test
    public void testCreateLobby_fromLobbyPostDTO_toLobby_success() {
        // create LobbyPostDTO
        LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
        lobbyPostDTO.setLobbyname("lobbyname");

      // MAP -> Create lobby
        Lobby lobby = DTOMapper.INSTANCE.convertLobbyPostDTOtoEntity(lobbyPostDTO);

        // check content
        assertEquals(lobbyPostDTO.getLobbyname(), lobby.getLobbyname());
    }

    @Test
    public void testGetLobby_fromLobby_toLobbyGetDTO_success() {
        // create Lobby and User
        User testAdmin = new User();
        testAdmin.setToken("ADMIN_TOKEN");
        Lobby lobby = new Lobby();
        lobby.setId(0L);
        lobby.setLobbyname("lobbyname");
        lobby.setLobbyToken("lobbyToken");
        lobby.setLobbyState(LobbyStatus.OPEN);
        lobby.setLobbyType(LobbyType.PRIVATE);
        lobby.setNumberOfPlayers(1);
        lobby.setPlayerList(Collections.singletonList(testAdmin));
        lobby.setAdminToken(testAdmin.getToken());

        // MAP -> Create LobbyGetDTO
        LobbyGetDTO lobbyGetDTO = DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);

        // check content
        assertEquals(lobby.getId(), lobbyGetDTO.getId());
        assertEquals(lobby.getLobbyname(), lobbyGetDTO.getLobbyname());
        assertEquals(lobby.getLobbyToken(), lobbyGetDTO.getLobbyToken());
        assertEquals(lobby.getLobbyState(), lobbyGetDTO.getLobbyState());
        assertEquals(lobby.getLobbyType(), lobbyGetDTO.getLobbyType());
        assertEquals(lobby.getNumberOfPlayers(), lobbyGetDTO.getNumberOfPlayers());
        assertEquals(lobby.getPlayerList(), lobbyGetDTO.getPlayerList());
        assertEquals(lobby.getAdminToken(), lobbyGetDTO.getAdminToken());
    }

    @Test
    public void testGetGame_fromGame_toGameGetDTO_success() {

        Game game = new Game();
        game.setId(0L);
        game.setVersion(0);
        game.setToken("token");
        game.setCurrentRound(0);
        game.setGuesser(0);
        game.setVoteList(new ArrayList<>(Collections.nCopies(5, 0)));

        // MAP -> Create GameGetDTO
        GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);

        // check content
        assertEquals(game.getId(), gameGetDTO.getId());
        assertEquals(game.getVersion(), gameGetDTO.getVersion());
        assertEquals(game.getToken(), gameGetDTO.getToken());
        assertEquals(game.getCurrentRound(), gameGetDTO.getCurrentRound());
        assertEquals(game.getGuesser(), gameGetDTO.getGuesser());
        assertEquals(game.getVoteList(), gameGetDTO.getVoteList());
    }
}
