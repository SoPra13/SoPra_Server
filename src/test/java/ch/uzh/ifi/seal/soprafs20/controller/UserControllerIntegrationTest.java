package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private User testUser;


    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setUsername("User");
        testUser.setPassword("PWD");
        testUser.setToken("USER_TOKEN");
        testUser.setStatus(UserStatus.ONLINE);
        userService.createUser(testUser);
        testUser = userRepository.findByUsername("User");

    }

    @AfterEach
    void reset() {
        userRepository.deleteAll();
    }

    @Test
    void getAllUsers() throws Exception {
        User testUser2 = new User();
        testUser2.setUsername("testUser2");
        testUser2.setPassword("PWD2");
        testUser2.setToken("TOKEN2");
        testUser2.setStatus(UserStatus.ONLINE);
        userService.createUser(testUser2);
        testUser2 = userRepository.findByUsername("testUser2");

        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username", is(testUser.getUsername())))
                .andExpect(jsonPath("$[1].username", is(testUser2.getUsername())));
    }

    @Test
    void updateInGame() throws Exception {


        MockHttpServletRequestBuilder putRequest = put("/user/updateingametab?userToken=" +
                testUser.getToken()).contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isOk());

        testUser = userRepository.findByToken(testUser.getToken());
        assertEquals(1L, testUser.getIsInGameTabCycle());
    }

    @Test
    void updateUser() throws Exception {
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("newUsername");
        String userPostDTOString = new ObjectMapper().writeValueAsString(userPostDTO);

        MockHttpServletRequestBuilder putRequest = put("/user?token=" + testUser.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(userPostDTOString);
        mockMvc.perform(putRequest).andExpect(status().isNoContent());

        testUser = userRepository.findByToken(testUser.getToken());
        assertEquals("newUsername", testUser.getUsername());
    }

//    @Test
//    void addScore() throws Exception {
//        System.out.println(testUser.getToken());
//        MockHttpServletRequestBuilder putRequest = put("/user/score?userToken=" +
//                testUser.getToken() + "&score=100").contentType(MediaType.APPLICATION_JSON);
//        mockMvc.perform(putRequest).andExpect(status().isOk());
//
//        testUser = userRepository.findByToken(testUser.getToken());
//        assertEquals(100, testUser.getTotalScore());
//    }
}