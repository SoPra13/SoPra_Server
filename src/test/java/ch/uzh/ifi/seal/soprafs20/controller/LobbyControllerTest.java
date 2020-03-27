package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserPostDTO;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(LobbyController.class)
public class LobbyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LobbyService lobbyService;

    @Test
    public void get_lobby_correct() throws Exception {

        //Userservice need to be implemented


        MockHttpServletRequestBuilder getRequest = get("/lobby").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isOk());
    }

    @Test
    public void get_lobby_failed() throws Exception {

        //Userservice need to be implemented

        MockHttpServletRequestBuilder getRequest = get("/lobby").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isNotFound()).andExpect(content().string("Error"));
    }

    @Test
    public void put_lobbyjoin_correct() throws Exception {

        //Userservice need to be implemented

        MockHttpServletRequestBuilder getRequest = put("/1/1").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isAccepted());
    }

    @Test
    public void put_lobbyjoin_forbidden() throws Exception {

        //Userservice need to be implemented

        MockHttpServletRequestBuilder getRequest = put("/1/1").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isForbidden()).andExpect(content().string("Error"));
    }

    @Test
    public void put_lobbyjoin_notfound() throws Exception {

        //Userservice need to be implemented

        MockHttpServletRequestBuilder getRequest = put("/1/1").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isNotFound()).andExpect(content().string("Error"));
    }

    @Test
    public void delete_lobbyleft_correct() throws Exception {

        //Userservice need to be implemented

        MockHttpServletRequestBuilder deleteRequest = delete("/1/1").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(deleteRequest).andExpect(status().isOk());
    }
    @Test
    public void delete_lobbyjoin_notfound() throws Exception {

        //Userservice need to be implemented

        MockHttpServletRequestBuilder deleteRequest = delete("/1/1").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(deleteRequest).andExpect(status().isNotFound()).andExpect(content().string("Error"));
    }

    @Test
    public void put_lobbyjoinbot_correct() throws Exception {

        //Userservice need to be implemented

        MockHttpServletRequestBuilder putRequest = put("/1/bot").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isAccepted());
    }

    @Test
    public void put_lobbyjoinbot_forbidden() throws Exception {

        //Userservice need to be implemented

        MockHttpServletRequestBuilder putRequest = put("/1/bot").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isForbidden()).andExpect(content().string("Error"));
    }

    @Test
    public void put_lobbyjoinbot_notfound() throws Exception {

        //Userservice need to be implemented

        MockHttpServletRequestBuilder putRequest = put("/1/bot").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isNotFound()).andExpect(content().string("Error"));
    }

    @Test
    public void put_lobbyleftbot_correct() throws Exception {

        //Userservice need to be implemented

        MockHttpServletRequestBuilder deleteRequest = delete("/1/bot").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(deleteRequest).andExpect(status().isAccepted());
    }

    @Test
    public void put_lobbyleftbot_forbidden() throws Exception {

        //Userservice need to be implemented

        MockHttpServletRequestBuilder deleteRequest = delete("/1/bot").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(deleteRequest).andExpect(status().isForbidden()).andExpect(content().string("Error"));
    }

    @Test
    public void put_lobbyleftbot_notfound() throws Exception {

        //Userservice need to be implemented

        MockHttpServletRequestBuilder deleteRequest = delete("/1/bot").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(deleteRequest).andExpect(status().isNotFound()).andExpect(content().string("Error"));









        private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }
}