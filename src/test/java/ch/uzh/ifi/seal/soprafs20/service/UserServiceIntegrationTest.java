package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private User testUser;

    @BeforeEach
    public void setup() {
    testUser = new User();
    testUser.setPassword("testPassword");
    testUser.setUsername("testUsername");
    testUser = userService.createUser(testUser);

    }

    @AfterEach
    public void clear() {
        userRepository.deleteAll();
    }

    @Test
    public void createUser_validInputs_success() {
        userRepository.deleteAll();
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");

        // when
        User createdUser = userService.createUser(testUser);

        // then
        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getPassword(), createdUser.getPassword());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getToken());
        assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
    }

    @Test
    public void getAll(){
        List<User> all = userService.getUsers();

        assertNotNull(all);
        assertTrue(all.contains(testUser));
    }

    @Test
    public void createUser_duplicateUsername_throwsException() {

        // attempt to create second user with same username
        User testUser2 = new User();

        // change the name but forget about the username
        testUser2.setPassword("testPassword2");
        testUser2.setUsername("testUsername");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
    }

    @Test
    public void loginUser_success(){

        userService.loginUser(testUser);

        assertEquals(UserStatus.ONLINE, userRepository.findByToken(testUser.getToken()).getStatus());
    }

    @Test
    public void loginUser_failed(){

        assertThrows(ResponseStatusException.class, () -> userService.loginUser(new User()));
    }

    @Test
    public void logoutUser_success(){

        userService.logoutUser(testUser.getToken());

        assertEquals(UserStatus.OFFLINE, userRepository.findByToken(testUser.getToken()).getStatus());
    }

    @Test
    public void logoutUser_failed(){

        assertThrows(ResponseStatusException.class, () -> userService.logoutUser("wrongToken"));
    }

    @Test
    public void updateUser_success(){
        testUser.setUsername("newUserName");

        userService.updateUser(testUser);

        assertEquals(testUser.getUsername(), userRepository.findByToken(testUser.getToken()).getUsername());
    }

    @Test
    public void updateUser_failed(){
        testUser.setToken("wrongToken");

        assertThrows(ResponseStatusException.class, () -> userService.updateUser(new User()));
    }

    @Test
    public void leaveLobby_success(){

        userService.leaveLobby(testUser);

        assertNull(testUser.getLobby());
        assertFalse(testUser.isLobbyReady());
    }

    @Test
    public void setUserInGameTab_success(){

        userService.setUserInGameTab(testUser.getToken(), true);

        assertTrue(userRepository.findByToken(testUser.getToken()).isInGameTab());
    }

    @Test
    public void updateIsInGameTab_success(){

        userService.updateIsInGameTab(testUser.getToken());

        assertTrue(userRepository.findByToken(testUser.getToken()).isInGameTab());
    }

    @Test
    public void updateIsInGameTab_failed(){
        testUser.setIsInGameTabCycle(2L);
        userService.updateIsInGameTab(testUser.getToken());
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                testUser.setIsInGameTabCycle(0L);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User cant be updated anymore.");
            }
        }).start();

        assertTrue(userRepository.findByToken(testUser.getToken()).isInGameTab());
    }
}
