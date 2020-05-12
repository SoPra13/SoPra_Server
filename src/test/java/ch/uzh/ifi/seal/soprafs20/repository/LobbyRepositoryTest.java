package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.constant.LobbyStatus;
import ch.uzh.ifi.seal.soprafs20.constant.LobbyType;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
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

    @Test
    public void findByToken_success() {
        // given
        Lobby lobby = new Lobby();
        lobby.setLobbyName("LobbyName");
        lobby.setLobbyToken("TOKEN");
        lobby.setLobbyState(LobbyStatus.OPEN);
        lobby.setNumberOfPlayers(1);
        lobby.setAdminToken("ADMIN_TOKEN");
        lobby.setLobbyType(LobbyType.PUBLIC);
        lobby.setJoinToken("JOIN_TOKEN");

        entityManager.persist(lobby);
        entityManager.flush();

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
    public void findByToken_failed() {
        // given
        Lobby lobby = new Lobby();
        lobby.setLobbyName("LobbyName");
        lobby.setLobbyToken("TOKEN");
        lobby.setLobbyState(LobbyStatus.OPEN);
        lobby.setNumberOfPlayers(1);
        lobby.setAdminToken("ADMIN_TOKEN");
        lobby.setLobbyType(LobbyType.PUBLIC);
        lobby.setJoinToken("JOIN_TOKEN");

        entityManager.persist(lobby);
        entityManager.flush();

        // when
        Lobby found = lobbyRepository.findByLobbyToken("invalid");

        // then
        assertNull(found);
    }
}