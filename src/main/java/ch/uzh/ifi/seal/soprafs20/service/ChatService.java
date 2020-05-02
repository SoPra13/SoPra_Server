package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.MessageType;
import ch.uzh.ifi.seal.soprafs20.entity.Chat;
import ch.uzh.ifi.seal.soprafs20.entity.Message;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.ChatRepository;
import ch.uzh.ifi.seal.soprafs20.repository.MessageRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class ChatService {


    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChatService(@Qualifier("chatRepository") ChatRepository chatRepository, @Qualifier("messageRepository") MessageRepository messageRepository, @Qualifier("userRepository") UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public void createChat(String lobbyToken) {
        Chat chat = new Chat();
        chat.setLobbyToken(lobbyToken);
        chat.setActive(true);
        chatRepository.save(chat);
        chatRepository.flush();
    }

    public void addMessageToChat(String lobbyToken, String userToken, Message message) {
        Chat chat = chatRepository.findByLobbyToken(lobbyToken);
        if (chat.isActive()) {
            chat.addMessage(message);
            message.setUsername(userRepository.findByToken(userToken).getUsername());
            message.setMessageType(MessageType.NORMAL);
            messageRepository.saveAndFlush(message);
        }
    }

    public void endMessage(String score, String userToken, String lobbyToken){
        Chat chat = chatRepository.findByLobbyToken(lobbyToken);
        Message msg = new Message();
        msg.setMessage("You have scored " + score + " points!");
        msg.setMessageType(MessageType.ACTION);
        msg.setUsername("");
        messageRepository.saveAndFlush(msg);
        chat.addMessage(msg);
        this.addMessageToChat(lobbyToken,userToken, msg);
    }

    public void userJoined(String lobbyToken, String userToken) {
        Chat chat = chatRepository.findByLobbyToken(lobbyToken);
        User user = userRepository.findByToken(userToken);
        if (!chat.getUserLoggedIn().contains(user)) {
            chat.addUser(user);
            Message message = new Message();
            message.setMessage(user.getUsername() + " joined Chat.");
            message.setMessageType(MessageType.ACTION);
            messageRepository.saveAndFlush(message);
            chat.addMessage(message);
        }
    }

    public void leaveChat(String lobbyToken, String userToken){
        Chat chat = chatRepository.findByLobbyToken(lobbyToken);
        User user = userRepository.findByToken(userToken);
        chat.removeUser(user);
    }

    public List<Message> getAllMessagesFromChat(String lobbyToken) {
        return chatRepository.findByLobbyToken(lobbyToken).getMessages();
    }

    public void setChatActivity(String lobbyToken, Boolean active) {
        chatRepository.findByLobbyToken(lobbyToken).setActive(active);
    }

    public boolean isChatActive(String lobbyToken) {
        return chatRepository.findByLobbyToken(lobbyToken).isActive();
    }

}
