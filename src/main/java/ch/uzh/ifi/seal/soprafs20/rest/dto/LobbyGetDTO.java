package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.constant.LobbyStatus;
import ch.uzh.ifi.seal.soprafs20.constant.LobbyType;
import ch.uzh.ifi.seal.soprafs20.entity.Bot;
import ch.uzh.ifi.seal.soprafs20.entity.User;

import java.util.ArrayList;
import java.util.List;

public class LobbyGetDTO {

    private Long id;
    private String lobbyName;
    private String lobbyToken;
    private String joinToken;
    private LobbyStatus lobbyState;
    private LobbyType lobbyType;
    private Integer numberOfPlayers;
    private String adminToken;
    private List<User> playerList = new ArrayList<User>();
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

    public void setPlayerList(List playerList) {
        this.playerList = playerList;
    }

    public List getBotList() {
        return botList;
    }

    public void setBotList(List botList) {
        this.botList = botList;
    }

    public String getJoinToken() {
        return joinToken;
    }

    public void setJoinToken(String joinToken) {
        this.joinToken = joinToken;
    }


}