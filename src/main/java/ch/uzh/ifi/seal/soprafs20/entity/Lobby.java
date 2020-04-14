package ch.uzh.ifi.seal.soprafs20.entity;

import ch.uzh.ifi.seal.soprafs20.constant.LobbyStatus;
import ch.uzh.ifi.seal.soprafs20.constant.LobbyType;
import com.fasterxml.jackson.annotation.JsonBackReference;


import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


@Entity
@Table(name = "LOBBY")
public class Lobby implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String lobbyname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LobbyStatus lobbyState;

    @Column(nullable = false)
    private Integer numberOfPlayers ;

    @Column(nullable = false)
    private String adminToken;

    @Column(nullable = false)
    private LobbyType lobbyType;

    @OneToMany(mappedBy = "lobby")
    @JsonBackReference
    private List<User> playerList = new ArrayList<User>();

    @OneToMany(mappedBy = "lobby")
    @JsonBackReference
    private List<Bot> botList = new ArrayList<Bot>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdminToken() {
        return adminToken;
    }

    public void setAdminToken(String adminToken) {
        this.adminToken = adminToken;
    }

    public String getLobbyname() {
        return lobbyname;
    }

    public void setLobbyname(String lobbyname) {
        this.lobbyname = lobbyname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public List getPlayerList() { return playerList; }

    public void setPlayerList(List playerList) {
        this.playerList = playerList;
    }

    public List getBotList() {
        return botList;
    }

    public void setBotList(List botList) {
        this.botList = botList;
    }

}
