package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
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

    // most test are covered by Unit test written at the beginning
    // here are only functions tested written after the main testing has been done.
/*    @Test
    void getGame() {
    }

    @Test
    void setPlayerUnityReady() {
    }

    @Test
    void voteForTopic() {
    }

    @Test
    void setTopic() {
    }

    @Test
    void removePlayer() {
    }

    @Test
    void addClue() {
    }

    @Test
    void makeGuess() {
    }

    @Test
    void nextRound() {
    }

    @Test
    void endGame() {
    }*/

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
}