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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
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

    @Test
    public void post_register_correct() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPasseord("test");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("test");
        userPostDTO.setPassword("test");

        //Userservice need to be implemented


        MockHttpServletRequestBuilder postRequest = post("/register").contentType(MediaType.APPLICATION_JSON).content(asJsonString(userPostDTO));
        mockMvc.perform(postRequest).andExpect(status().isCreated());
    }

    @Test
    public void post_register_failed() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPasseord("test");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("test");
        userPostDTO.setPassword("test");

        //Userservice need to be implemented


        MockHttpServletRequestBuilder postRequest = post("/register").contentType(MediaType.APPLICATION_JSON).content(asJsonString(userPostDTO));
        mockMvc.perform(postRequest).andExpect(status().isConflict()).andExpect(content().string("Error"));
    }

    @Test
    public void get_user_correct() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setWord("testWord");


        //Userservice need to be implemented


        MockHttpServletRequestBuilder getRequest = get("/user?token=testtoken").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isOk()).andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.word", user.getWord()));
        .andExpect(jsonPath("$.word", user.getWord()));
    }

    @Test
    public void get_user_failed() throws Exception {

        //Userservice need to be implemented
        MockHttpServletRequestBuilder getRequest = get("/user?token=testtoken").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isNotFound()).andExpect(content().string("Error"));
    }

    @Test
    public void put_user_correct() throws Exception {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("testChange");
        userPutDTO.setPassword("testChange");

        //Userservice need to be implemented

        MockHttpServletRequestBuilder postRequest = put("/user/1").contentType(MediaType.APPLICATION_JSON).content(asJsonString(userPutDTO));
        mockMvc.perform(postRequest).andExpect(status().isNoContent());
    }

    @Test
    public void put_user_failed() throws Exception {
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("testChange");
        userPutDTO.setPassword("testChange");

        //Userservice need to be implemented

        MockHttpServletRequestBuilder postRequest = put("/user/1").contentType(MediaType.APPLICATION_JSON).content(asJsonString(userPutDTO));
        mockMvc.perform(postRequest).andExpect(status().isNotFound()).andExpect(content().string("Error"));
    }

    @Test
    public void post_login_correct() throws Exception {
        User user = new User();
        user.setToken("testToken");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("test");
        userPostDTO.setPassword("test");

        //Userservice need to be implemented


        MockHttpServletRequestBuilder postRequest = post("/login").contentType(MediaType.APPLICATION_JSON).content(asJsonString(userPostDTO));
        mockMvc.perform(postRequest).andExpect(status().isOk()).andExpect(jsonPath("$.token", is(user.getToken())));
    }

    @Test
    public void post_register_failed() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPasseord("test");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("test");
        userPostDTO.setPassword("test");

        //Userservice need to be implemented


        MockHttpServletRequestBuilder postRequest = post("/login").contentType(MediaType.APPLICATION_JSON).content(asJsonString(userPostDTO));
        mockMvc.perform(postRequest).andExpect(status().isUnauthorized()).andExpect(content().string("Error"));
    }

    @Test
    public void put_logout_correct() throws Exception {
        //Userservice need to be implemented


        MockHttpServletRequestBuilder getRequest = put("/user?token=testtoken").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isOk());
    }

    @Test
    public void put_logout_failed() throws Exception {

        //Userservice need to be implemented
        MockHttpServletRequestBuilder getRequest = put("/user?token=testtoken").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isNotFound()).andExpect(content().string("Error"));
    }

    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // given
        User user = new User();
        user.setName("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);

        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should return when getUsers() is called
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
    }


    @Test
    public void createUser_validInput_userCreated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("Test User");
        userPostDTO.setUsername("testUsername");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
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