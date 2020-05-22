package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.LeaderboardBy;
import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
@Transactional
class LeaderboardServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    LeaderboardService leaderboardService;
    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("Name");
        testUser.setPassword("Pwd");
        testUser.setToken("TOKEN");
        testUser.setStatus(UserStatus.ONLINE);
        userService.createUser(testUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void getJsonLeaderboardByAttribute() {

        String res = leaderboardService.getJsonLeaderboardByAttribute(LeaderboardBy.GAMESPLAYED);


        assertNotNull(res);
    }

    @Test
    void getComparator() {
        for (LeaderboardBy by : LeaderboardBy.values()) {
            if (by.equals(LeaderboardBy.INVALID_TESTING)) continue;
            assertNotNull(leaderboardService.getComparator(by));
        }
        Exception exception = assertThrows(IllegalStateException.class,
                () -> leaderboardService.getComparator(LeaderboardBy.INVALID_TESTING));

        assertEquals("Unexpected value: " + LeaderboardBy.INVALID_TESTING, exception.getMessage());
    }

    @Test
    void getScore() {
        for (LeaderboardBy by : LeaderboardBy.values()) {
            if (by.equals(LeaderboardBy.INVALID_TESTING)) continue;
            System.out.println(by);
            assertNotNull(leaderboardService.getScore(testUser, by));
        }
        Exception exception = assertThrows(IllegalStateException.class,
                () -> leaderboardService.getScore(testUser, LeaderboardBy.INVALID_TESTING));

        assertEquals("Unexpected value: " + LeaderboardBy.INVALID_TESTING, exception.getMessage());
    }
}