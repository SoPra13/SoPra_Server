package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Message;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ChatGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ChatPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GameGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.ChatService;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ChatController {

    private final ChatService chatService;

    ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    //Get Game from Token
    @GetMapping("/chat")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ChatGetDTO> getMessages(@RequestParam String lobbyToken) {
        List<Message> messages = chatService.getAllMessagesFromChat(lobbyToken);
        List<ChatGetDTO> chatGetDTOs = new ArrayList<>();
        for (Message message : messages) {
            chatGetDTOs.add(DTOMapper.INSTANCE.convertEntitytoChatGetDTO(message));
        }
        return chatGetDTOs;
    }

    @GetMapping("/chat/active")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public boolean isChatActive(@RequestParam String lobbyToken) {
        return chatService.isChatActive(lobbyToken);
    }

    @PostMapping("/chat")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public void addMessage(@RequestParam String lobbyToken, @RequestParam String userToken, @RequestBody ChatPostDTO chatPostDTO) {
        chatService.addMessageToChat(lobbyToken, userToken, DTOMapper.INSTANCE.convertChatPostDTOtoEntity(chatPostDTO));
    }

    @PostMapping("/chat/join")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public void userJoined(@RequestParam String lobbyToken, @RequestParam String userToken) {
        chatService.userJoined(lobbyToken, userToken);
    }

    @PostMapping("/chat/toggle")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public void toggleChat(@RequestParam String lobbyToken) {
        chatService.setChatActivity(lobbyToken, !chatService.isChatActive(lobbyToken));
    }

}