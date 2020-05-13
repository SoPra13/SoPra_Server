package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.entity.Chat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ChatRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Qualifier("chatRepository")
    @Autowired
    private ChatRepository chatRepository;

    private Chat testChat;

    @BeforeEach
    void setup(){
        Chat testChat = new Chat();
        testChat.setLobbyToken("lobbyToken");
        testChat.setActive(true);

        entityManager.persist(testChat);
        entityManager.flush();
    }

    @Test
    void findByLobbyToken_success() {

        Chat found = chatRepository.findByLobbyToken("lobbyToken");

        assertEquals("lobbyToken", found.getLobbyToken());
        assertTrue(found.isActive());
    }

    @Test
    void findByLobbyToken_failed() {

        Chat found = chatRepository.findByLobbyToken("invalid_lobbyToken");

        assertNull(found);
    }
}