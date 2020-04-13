package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.entity.User;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

public class GameGetDTO {

    private Long id;
    private Integer version;
    private String token;
    private Integer round;
    private Integer guesser;
    private List<String> guessList = new ArrayList<String>();
    private List<String> mysteryWords = new ArrayList<String>();
    private List<User> playerList = new ArrayList<User>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getVersion() {return version;}
    public void setVersion(Integer version) {this.version = version;}

    public Integer getGuesser() {return guesser;}
    public void setGuesser(Integer guesser) {this.guesser = guesser;}

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public Integer getRound() {return round;}
    public void setRound(Integer round) {this.round = round;}

    public List getGuessList() { return guessList; }
    public void setGuessList(ArrayList guessList) { this.guessList = guessList; }

    public List getMysteryWords() { return mysteryWords; }
    public void setClueList(ArrayList mysteryWords) { this.mysteryWords = mysteryWords; }

    public List getPlayerList() {
        return playerList;
    }

    public void setPlayerList(ArrayList playerList) {
        this.playerList = playerList;
    }

}
