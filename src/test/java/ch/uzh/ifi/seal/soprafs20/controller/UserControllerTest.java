package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserPostDTO;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User dummyUser(){
        User user = new User();
        user.setUsername("testusername");
        user.setPassword("testpassword");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("testtoken");
        user.setId(1L);
        return user;
    }

    @Test
    public void post_register_correct() throws Exception {
        User user = dummyUser();

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testusername");
        userPostDTO.setPassword("testpassword");

        given(userService.createUser(Mockito.any())).willReturn(user);


        MockHttpServletRequestBuilder postRequest = post("/register").contentType(MediaType.APPLICATION_JSON).content(asJsonString(userPostDTO));
        mockMvc.perform(postRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.password", is(user.getPassword())))
                .andExpect(jsonPath("$.token", is(user.getToken())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    @Test
    public void post_register_failed() throws Exception {
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("test");
        userPostDTO.setPassword("test");

        given(userService.createUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT,
                "The username provided is not unique. Therefore, the user could not be created!"));


        MockHttpServletRequestBuilder postRequest = post("/register").contentType(MediaType.APPLICATION_JSON).content(asJsonString(userPostDTO));
        mockMvc.perform(postRequest).andExpect(status().isConflict())
                .andExpect(status().reason("The username provided is not unique. Therefore, the user could not be created!"));
    }

    @Test
    public void get_user_correct() throws Exception {
        User user = dummyUser();


        given(userService.getUserFromToken("testtoken")).willReturn(user);


        MockHttpServletRequestBuilder getRequest = get("/user?token=testtoken").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.password", is(user.getPassword())))
                .andExpect(jsonPath("$.token", is(user.getToken())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    @Test
    public void get_user_failed() throws Exception {
        given(userService.getUserFromToken("testtoken"))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no User with requested Token"));

        MockHttpServletRequestBuilder getRequest = get("/user?token=testtoken").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isNotFound())
                .andExpect(status().reason("There is no User with requested Token"));
    }


    @Test
    public void post_login_correct() throws Exception {
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testusername");
        userPostDTO.setPassword("testpassword");


        given(userService.LoginUser(Mockito.any())).willReturn("testtoken");


        MockHttpServletRequestBuilder postRequest = post("/login").contentType(MediaType.APPLICATION_JSON).content(asJsonString(userPostDTO));
        mockMvc.perform(postRequest).andExpect(status().isOk()).andExpect(content().string("testtoken"));
    }

    @Test
    public void post_login_failed() throws Exception {
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("test");
        userPostDTO.setPassword("test");

        given(userService.LoginUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login unsuccessful."));


        MockHttpServletRequestBuilder postRequest = post("/login").contentType(MediaType.APPLICATION_JSON).content(asJsonString(userPostDTO));
        mockMvc.perform(postRequest).andExpect(status().isUnauthorized()).andExpect(status().reason("Login unsuccessful."));
    }


    @Test
    public void put_logout_correct() throws Exception {
        MockHttpServletRequestBuilder getRequest = put("/logout?token=testtoken").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isOk());
    }

    @Test
    public void put_logout_failed() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "logout unsuccessful.")).when(userService).LogoutUser("testtoken");

        MockHttpServletRequestBuilder getRequest = put("/logout?token=testtoken").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isNotFound()).andExpect(status().reason("logout unsuccessful."));
    }


    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     *
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }
}