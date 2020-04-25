package ch.uzh.ifi.seal.soprafs20.rest.dto;
import ch.uzh.ifi.seal.soprafs20.entity.Bot;
import ch.uzh.ifi.seal.soprafs20.entity.User;

import java.util.ArrayList;
import java.util.List;

public class GameGetDTO {

    private Long id;
    private Integer version;
    private String token;
    private Integer currentRound;
    private Integer guesser;
    private String topic;
    private Boolean guessCorrect;
    private List<String> clueList = new ArrayList<String>();
    private List<String> mysteryWords = new ArrayList<String>();
    private List<Integer> voteList = new ArrayList<>();
    private List<User> playerList = new ArrayList<User>();
    private List<Bot> botList = new ArrayList<>();

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

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getCurrentRound() {return currentRound;}
    public void setCurrentRound(Integer currentRound) {this.currentRound = currentRound;}

    public List getClueList() { return clueList; }
    public void setClueList(ArrayList clueList) { this.clueList = clueList; }

    public List getVoteList() { return voteList; }
    public void setVoteList(ArrayList voteList) { this.voteList = voteList; }

    public List getMysteryWords() { return mysteryWords; }
    public void setMysteryWords(ArrayList mysteryWords) { this.mysteryWords = mysteryWords; }

    public List getPlayerList() {
        return playerList;
    }

    public void setPlayerList(ArrayList playerList) {
        this.playerList = playerList;
    }

    public List<Bot> getBotList() {
        return botList;
    }

    public void setBotList(List botList) {
        this.botList = botList;
    }

    public Boolean getGuessCorrect() {return guessCorrect;}

    public void setGuessCorrect(Boolean guessCorrect) {this.guessCorrect = guessCorrect;}

}
