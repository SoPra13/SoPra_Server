package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameService gameService;

    @AfterEach
    void reset() {
        userRepository.deleteAll();
    }

    @Test
    void put_addScore() throws Exception {
        User testUser = new User();
        testUser.setUsername("User");
        testUser.setPassword("PWD");
        testUser.setToken("USER_TOKEN");
        testUser.setStatus(UserStatus.ONLINE);
        userService.createUser(testUser);
        testUser = userRepository.findByUsername("User");
        String userToken = testUser.getToken();

        MockHttpServletRequestBuilder putRequest =
                put("/game/score?userToken=" + userToken + "&score=100")
                        .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isOk());

        assertEquals(100, userRepository.findByUsername("User").getTotalScore());
    }

    @Test
    void put_addScore_invalid_userToken() throws Exception {
        User testUser = new User();
        testUser.setUsername("User");
        testUser.setPassword("PWD");
        testUser.setToken("USER_TOKEN");
        testUser.setStatus(UserStatus.ONLINE);
        userService.createUser(testUser);
        testUser = userRepository.findByUsername("User");
        String userToken = testUser.getToken();

        MockHttpServletRequestBuilder putRequest =
                put("/game/score?userToken=INVALID_TOKEN&score=100")
                        .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(putRequest).andExpect(status().isNotFound());

        assertEquals(0, userRepository.findByUsername("User").getTotalScore());
    }
}