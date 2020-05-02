package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.Message;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ChatPostDTO;
import ch.uzh.ifi.seal.soprafs20.service.ChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void get_getMessages() throws Exception {
        Message msg1 = new Message();
        msg1.setMessage("MSG1");
        Message msg2 = new Message();
        msg2.setMessage("MSG2");
        ArrayList<Message> allMsg = new ArrayList<Message>();
        allMsg.add(msg1);
        allMsg.add(msg2);

        given(chatService.getAllMessagesFromChat(Mockito.anyString())).willReturn(allMsg);

        MockHttpServletRequestBuilder getRequest = get("/chat?lobbyToken=Token_Aa0Bb1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message", is(msg1.getMessage())))
                .andExpect(jsonPath("$[1].message", is(msg2.getMessage())));
    }

    @Test
    void get_isChatActive() throws Exception {
        given(chatService.isChatActive(Mockito.anyString())).willReturn(true);

        MockHttpServletRequestBuilder getRequest = get("/chat/active?lobbyToken=Token_Aa0Bb1")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));
    }

    @Test
    void post_addMessage() throws Exception {
        ChatPostDTO chatPostDTO = new ChatPostDTO();
        chatPostDTO.setMessage("MSG");

        MockHttpServletRequestBuilder postRequest = post("/chat?lobbyToken=LToken&userToken=UToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(chatPostDTO));
        mockMvc.perform(postRequest).andExpect(status().isAccepted());
        Mockito.verify(chatService, times(1)).addMessageToChat(any(),any(),any());
    }

    @Test
    void userJoined()  throws Exception {
        MockHttpServletRequestBuilder postRequest = post("/chat/join?lobbyToken=LToken&userToken=UToken")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(postRequest).andExpect(status().isAccepted());
        Mockito.verify(chatService, times(1)).userJoined(any(),any());
    }

    @Test
    void toggleChat()  throws Exception {

        MockHttpServletRequestBuilder postRequest = post("/chat/toggle?lobbyToken=LToken&userToken=UToken")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(postRequest).andExpect(status().isAccepted());
        Mockito.verify(chatService, times(1)).setChatActivity(any(),any());
    }

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }
}