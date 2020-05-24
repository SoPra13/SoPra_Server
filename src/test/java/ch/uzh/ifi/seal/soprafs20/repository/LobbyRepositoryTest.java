package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.constant.LobbyStatus;
import ch.uzh.ifi.seal.soprafs20.constant.LobbyType;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LobbyRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Qualifier("lobbyRepository")
    @Autowired
    private LobbyRepository lobbyRepository;

    private Lobby lobby;

    @BeforeEach
    void setup() {
        // given
        lobby = new Lobby();
        lobby.setLobbyName("LobbyName");
        lobby.setLobbyToken("TOKEN");
        lobby.setLobbyState(LobbyStatus.OPEN);
        lobby.setNumberOfPlayers(1);
        lobby.setAdminToken("ADMIN_TOKEN");
        lobby.setLobbyType(LobbyType.PUBLIC);
        lobby.setJoinToken("JOIN_TOKEN");

        entityManager.persist(lobby);
        entityManager.flush();
    }

    @Test
    void findByToken_success() {
        // when
        Lobby found = lobbyRepository.findByLobbyToken(lobby.getLobbyToken());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getLobbyName(), lobby.getLobbyName());
        assertEquals(found.getLobbyToken(), lobby.getLobbyToken());
        assertEquals(found.getLobbyState(), lobby.getLobbyState());
        assertEquals(found.getNumberOfPlayers(), lobby.getNumberOfPlayers());
        assertEquals(found.getAdminToken(), lobby.getAdminToken());
        assertEquals(found.getLobbyType(), lobby.getLobbyType());
    }

    @Test
    void findByToken_failed() {
        // when
        Lobby found = lobbyRepository.findByLobbyToken("invalid");

        // then
        assertNull(found);
    }

    @Test
    void findByJoinToken_success() {
        // when
        Lobby found = lobbyRepository.findByJoinToken(lobby.getJoinToken());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getLobbyName(), lobby.getLobbyName());
        assertEquals(found.getLobbyToken(), lobby.getLobbyToken());
        assertEquals(found.getLobbyState(), lobby.getLobbyState());
        assertEquals(found.getNumberOfPlayers(), lobby.getNumberOfPlayers());
        assertEquals(found.getAdminToken(), lobby.getAdminToken());
        assertEquals(found.getLobbyType(), lobby.getLobbyType());
    }

    @Test
    void findByJoinToken_failed() {
        // when
        Lobby found = lobbyRepository.findByJoinToken("invalid");

        // then
        assertNull(found);
    }
}