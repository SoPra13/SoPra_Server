package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.constant.LobbyType;

public class LobbyPostDTO {

    private String lobbyname;

    private String adminToken;

    private LobbyType lobbyType;


    public String getLobbyname() {
        return lobbyname;
    }

    public void setLobbyname(String lobbyname) {
        this.lobbyname = lobbyname;
    }

    public String getAdminToken() {
        return adminToken;
    }

    public void setAdminToken(String adminToken) {
        this.adminToken = adminToken;
    }

    public LobbyType getLobbyType() { return lobbyType;}

    public void setLobbyType(LobbyType lobbyType) {
        this.lobbyType = lobbyType;
    }
}
