package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;

public class UserGetDTO {

    private Long id;
    private String username;
    private String token;
    private UserStatus status;
    private Lobby lobby;
    private Game game;
    private Boolean unityReady;
    private Boolean lobbyReady;
    private Boolean voted;
    private Boolean gaveClue;
    private Integer avatar;
    private Integer gamesPlayed;
    private Integer guessesMade;
    private Integer guessesCorrect;
    private Integer invalidClues;
    private Integer totalClues;
    private Integer duplicateClues;
    private Integer totalScore;


    public void setAvatar(Integer avatar) {
        this.avatar = avatar;
    }

    public Integer getAvatar(){return avatar;}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setUnityReady(boolean unityReady) {
        this.unityReady = unityReady;
    }

    public Boolean isUnityReady() {
        return unityReady;
    }

    public void setLobbyReady(boolean lobbyReady) {
        this.lobbyReady = lobbyReady;
    }

    public Boolean isLobbyReady() {
        return lobbyReady;
    }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }

    public Boolean getVoted() {
        return voted;
    }

    public void setGaveClue(boolean gaveClue) {
        this.gaveClue = gaveClue;
    }

    public Boolean getGaveClue() {
        return gaveClue;
    }

    public Integer getGamesPlayed() {return  gamesPlayed;}

    public void setGamesPlayed(Integer gamesPlayed){ this.gamesPlayed = gamesPlayed;}

    public Integer getTotalClues() {return  totalClues;}

    public void setTotalClues(Integer totalClues){ this.totalClues = totalClues;}

    public Integer getGuessesMade() {return  guessesMade;}

    public void setGuessesMade(Integer guessesMade){ this.guessesMade = guessesMade;}

    public Integer getGuessesCorrect() {return  guessesCorrect;}

    public void setGuessesCorrect(Integer guessesCorrect){ this.guessesCorrect = guessesCorrect;}

    public Integer getInvalidClues() {return  invalidClues;}

    public void setInvalidClues(Integer invalidClues){ this.invalidClues = invalidClues;}

    public Integer getDuplicateClues() {return  duplicateClues;}

    public void setDuplicateClues(Integer duplicateClues){ this.duplicateClues = duplicateClues;}

    public Integer getTotalScore() {return  totalScore;}

    public void setTotalScore(Integer totalScore){ this.totalScore = totalScore;}
}
