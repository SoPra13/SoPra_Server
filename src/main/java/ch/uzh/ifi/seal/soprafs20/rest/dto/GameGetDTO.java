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
    private String guess;
    private Boolean guessCorrect;
    private Boolean guessGiven;
    private Boolean botsVoted;
    private Boolean botsClueGiven;
    private List<String> clueList = new ArrayList<>();
    private List<String> mysteryWords = new ArrayList<>();
    private List<Integer> voteList = new ArrayList<>();
    private List<User> playerList = new ArrayList<>();
    private List<Bot> botList = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getGuesser() {
        return guesser;
    }

    public void setGuesser(Integer guesser) {
        this.guesser = guesser;
    }

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

    public Integer getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(Integer currentRound) {
        this.currentRound = currentRound;
    }

    public List<String> getClueList() {
        return clueList;
    }

    public void setClueList(List<String> clueList) {
        this.clueList = clueList;
    }

    public List<Integer> getVoteList() {
        return voteList;
    }

    public void setVoteList(List<Integer> voteList) {
        this.voteList = voteList;
    }

    public String getGuess() {
        return guess;
    }

    public void setGuess(String guess) {
        this.guess = guess;
    }

    public List<String> getMysteryWords() {
        return mysteryWords;
    }

    public void setMysteryWords(List<String> mysteryWords) {
        this.mysteryWords = mysteryWords;
    }

    public List<User> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<User> playerList) {
        this.playerList = playerList;
    }

    public List<Bot> getBotList() {
        return botList;
    }

    public void setBotList(List<Bot> botList) {
        this.botList = botList;
    }

    public Boolean getGuessCorrect() {
        return guessCorrect;
    }

    public void setGuessCorrect(Boolean guessCorrect) {
        this.guessCorrect = guessCorrect;
    }

    public Boolean getGuessGiven() {
        return guessGiven;
    }

    public void setGuessGiven(Boolean guessGiven) {
        this.guessGiven = guessGiven;
    }

    public Boolean getBotsVoted() {
        return botsVoted;
    }

    public void setBotsVoted(Boolean botsVoted) {
        this.botsVoted = botsVoted;
    }

    public Boolean getBotsClueGiven() {
        return botsClueGiven;
    }

    public void setBotsClueGiven(Boolean botsClueGiven) {
        this.botsClueGiven = botsClueGiven;
    }
}
