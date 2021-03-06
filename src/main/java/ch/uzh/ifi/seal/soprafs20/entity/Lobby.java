package ch.uzh.ifi.seal.soprafs20.entity;

import ch.uzh.ifi.seal.soprafs20.constant.LobbyStatus;
import ch.uzh.ifi.seal.soprafs20.constant.LobbyType;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "LOBBY")
public class Lobby implements Serializable {

    public static final int MAX_PLAYER = 7;

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String lobbyName;

    @Column(nullable = false, unique = true)
    private String lobbyToken;

    @Column(nullable = false, unique = true)
    private String joinToken;

    @Column(nullable = false)
    private LobbyStatus lobbyState;

    @Column(nullable = false)
    private Integer numberOfPlayers;

    @Column(nullable = false)
    private String adminToken;

    @Column(nullable = false)
    private LobbyType lobbyType;

    @OneToMany(mappedBy = "lobby")
    @JsonBackReference
    private List<User> playerList = new ArrayList<>();

    @OneToMany(mappedBy = "lobby")
    @JsonBackReference
    private List<Bot> botList = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdminToken() {
        return adminToken;
    }

    public void setAdminToken(String adminToken) {
        this.adminToken = adminToken;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public String getLobbyToken() {
        return lobbyToken;
    }

    public void setLobbyToken(String lobbyToken) {
        this.lobbyToken = lobbyToken;
    }

    public LobbyStatus getLobbyState() {
        return lobbyState;
    }

    public void setLobbyState(LobbyStatus status) {
        this.lobbyState = status;
    }

    public LobbyType getLobbyType() {
        return lobbyType;
    }

    public void setLobbyType(LobbyType type) {
        this.lobbyType = type;
    }

    public Integer getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(Integer numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public List<User> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<User> playerList) {
        this.playerList = playerList;
    }

    public void removePlayer(User user) {
        playerList.remove(user);
    }

    public List<Bot> getBotList() {
        return botList;
    }

    public void setBotList(List<Bot> botList) {
        this.botList = botList;
    }

    public void removeBot(Bot bot) {
        botList.remove(bot);
    }

    public String getJoinToken() {
        return joinToken;
    }

    public void setJoinToken(String joinToken) {
        this.joinToken = joinToken;
    }

}
