package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Chat;
import ch.uzh.ifi.seal.soprafs20.entity.Message;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.ChatRepository;
import ch.uzh.ifi.seal.soprafs20.repository.MessageRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

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

    public void addMessageToChat(String lobbyToken, Message message) {
        Chat chat = chatRepository.findByLobbyToken(lobbyToken);
        if (chat.isActive()) {
            chat.addMessage(message);
            message.setUsername(userRepository.findByToken(message.getUserToken()).getUsername());
            messageRepository.save(message);
            messageRepository.flush();
        }
    }

    public void userJoined(String lobbyToken, String userToken) {
        Message message = new Message();
        message.setUsername("EVENTEVENTEVENT");
        message.setMessage(userRepository.findByToken(userToken).getUsername() + " joined Chat.");
        messageRepository.save(message);
        messageRepository.flush();
        chatRepository.findByLobbyToken(lobbyToken).addMessage(message);
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
