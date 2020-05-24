package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setup() {
        // given
        user = new User();
        user.setPassword("password");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1");

        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    void findByName_success() {
        // when
        User found = userRepository.findByUsername(user.getUsername());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getPassword(), user.getPassword());
        assertEquals(found.getUsername(), user.getUsername());
        assertEquals(found.getToken(), user.getToken());
        assertEquals(found.getStatus(), user.getStatus());
    }

    @Test
    void findByName_failed() {
        // when
        User found = userRepository.findByUsername("invalid");

        // then
        assertNull(found);
    }

    @Test
    void findByPasswprd_success() {
        // when
        User found = userRepository.findByPassword(user.getPassword());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getPassword(), user.getPassword());
        assertEquals(found.getUsername(), user.getUsername());
        assertEquals(found.getToken(), user.getToken());
        assertEquals(found.getStatus(), user.getStatus());
    }

    @Test
    void findByPasswprd_failed() {
        // when
        User found = userRepository.findByPassword("invalid");

        // then
        assertNull(found);
    }

    @Test
    void findByToken_success() {
        // when
        User found = userRepository.findByToken(user.getToken());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getPassword(), user.getPassword());
        assertEquals(found.getUsername(), user.getUsername());
        assertEquals(found.getToken(), user.getToken());
        assertEquals(found.getStatus(), user.getStatus());
    }

    @Test
    void findByToken_failed() {
        // when
        User found = userRepository.findByToken("invalid");

        // then
        assertNull(found);
    }
}
