package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.LobbyStatus;
import ch.uzh.ifi.seal.soprafs20.constant.LobbyType;
import ch.uzh.ifi.seal.soprafs20.constant.MessageType;
import ch.uzh.ifi.seal.soprafs20.entity.Chat;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.Message;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.ChatRepository;
import ch.uzh.ifi.seal.soprafs20.repository.LobbyRepository;
import ch.uzh.ifi.seal.soprafs20.repository.MessageRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
@Transactional
class ChatServiceIntegrationTest {

    @Qualifier("chatRepository")
    @Autowired
    private ChatRepository chatRepository;

    @Qualifier("messageRepository")
    @Autowired
    private MessageRepository messageRepository;

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Qualifier("lobbyRepository")
    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @Autowired
    private LobbyService lobbyService;

    private Chat testChat;
    private Lobby testLobby;
    private User testUser;
    private Message msg;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("userName");
        testUser.setPassword("userPwd");
        userService.createUser(testUser);

        testLobby = new Lobby();
        testLobby.setLobbyName("lobbyName");
        testLobby.setLobbyToken("Token");
        testLobby.setJoinToken("joinToken");
        testLobby.setLobbyState(LobbyStatus.OPEN);
        testLobby.setNumberOfPlayers(1);
        testLobby.setAdminToken(testUser.getToken());
        testLobby.setLobbyType(LobbyType.PUBLIC);

        msg = new Message();
        msg.setMessageBody("dummyMessage");
        msg.setMessageType(MessageType.ACTION);
        msg.setUsername("dummyName");

        lobbyService.createLobby(testLobby, testUser.getToken());
        chatService.createChat(testLobby.getLobbyToken());
        testChat = chatRepository.findByLobbyToken(testLobby.getLobbyToken());
    }

    @AfterEach
    void reset() {
        userRepository.deleteAll();
        messageRepository.deleteAll();
        chatRepository.deleteAll();
        lobbyRepository.deleteAll();
    }

    @Test
    void createChat() {
//          CreateChat is move to the @BeforeEach setup
//        chatService.createChat(testLobby.getLobbyToken());
        Chat newChat = chatRepository.findByLobbyToken(testLobby.getLobbyToken());

        assertNotNull(newChat);
        assertTrue(newChat.isActive());
        assertEquals(testLobby.getLobbyToken(), testChat.getLobbyToken());
    }

    @Test
    void addMessageToChat_success() {

        chatService.addMessageToChat(testLobby.getLobbyToken(), testUser.getToken(), msg);

        assertTrue(testChat.getMessages().contains(msg));
        assertEquals(testUser.getUsername(), msg.getUsername());
        assertEquals(MessageType.NORMAL, msg.getMessageType());
    }

    @Test
    void addMessageToChat_inActive() {

        testChat.setActive(false);

        chatService.addMessageToChat(testLobby.getLobbyToken(), testUser.getToken(), msg);

        assertFalse(testChat.getMessages().contains(msg));
        assertNotEquals(testUser.getUsername(), msg.getUsername());
        assertNotEquals(MessageType.NORMAL, msg.getMessageType());
    }

    @Test
    void userJoined_success() {

        chatService.userJoined(testLobby.getLobbyToken(), testUser.getToken());

        assertTrue(testChat.getUserLoggedIn().contains(testUser));
        assertTrue(testChat.getMessages().stream()
                .anyMatch(o -> o.getMessageBody().equals(testUser.getUsername() + " joined Chat.")));
    }

    @Test
    void userJoined_userAlreadyLoggedIn() {

        chatService.userJoined(testLobby.getLobbyToken(), testUser.getToken());
        chatService.userJoined(testLobby.getLobbyToken(), testUser.getToken());

        assertEquals(1, testChat.getUserLoggedIn().stream().filter(o -> o.getToken().equals(testUser.getToken())).count());
        assertEquals(1, testChat.getMessages().stream()
                .filter(o -> o.getMessageBody().equals(testUser.getUsername() + " joined Chat.")).count());
    }

    @Test
    void leaveChat() {
        chatService.userJoined(testLobby.getLobbyToken(), testUser.getToken());

        chatService.leaveChat(testLobby.getLobbyToken(), testUser.getToken());

        assertFalse(testChat.getUserLoggedIn().contains(testUser));
    }

    @Test
    void getAllMessagesFromChat_success() {
        chatService.addMessageToChat(testLobby.getLobbyToken(), testUser.getToken(), msg);

        List<Message> allMsg = chatService.getAllMessagesFromChat(testLobby.getLobbyToken());

        assertTrue(allMsg.contains(msg));
    }

    @Test
    void getAllMessagesFromChat_noMessages() {

        List<Message> allMsg = chatService.getAllMessagesFromChat(testLobby.getLobbyToken());

        assertEquals(0, allMsg.size());
    }

    @Test
    void setChatActivity_active() {

        chatService.setChatActivity(testLobby.getLobbyToken(), true);

        assertTrue(testChat.isActive());
    }

    @Test
    void setChatActivity_inActive() {

        chatService.setChatActivity(testLobby.getLobbyToken(), false);

        assertFalse(testChat.isActive());
    }

    @Test
    void isChatActive() {

        Boolean status = chatService.isChatActive(testLobby.getLobbyToken());

        assertEquals(testChat.isActive(), status);
    }
}