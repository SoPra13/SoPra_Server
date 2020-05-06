package ch.uzh.ifi.seal.soprafs20.entity;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes the primary key
 */
@Entity
@Table(name = "USER")

public class User implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private UserStatus status;

    @Column
    private Boolean unityReady;

    @Column
    private Boolean lobbyReady;

    @Column
    private Boolean voted;

    @Column
    private Boolean gaveClue;

    @Column
    private Integer avatar;

    @Column
    private Integer gamesPlayed;

    @Column
    private Integer guessesMade;

    @Column
    private Integer guessesCorrect;

    @Column
    private Integer invalidClues;

    @Column
    private Integer totalClues;

    @Column
    private Integer duplicateClues;

    @Column
    private Integer totalScore;

    @ManyToOne
    @JsonManagedReference
    @JsonIgnore
    private Lobby lobby;

    @ManyToOne
    @JsonManagedReference
    @JsonIgnore
    private Game game;

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

    public void setGaveClue(boolean gaveClue) {this.gaveClue = gaveClue;}

    public Boolean getGaveClue() {return gaveClue; }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }

    public Boolean getVoted() {
        return voted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Integer getAvatar() {return  avatar;}
    public void setAvatar(Integer avatar){ this.avatar = avatar;}

    public Integer getGamesPlayed() {return  gamesPlayed;}
    public void setGamesPlayed(Integer gamesPlayed){ this.gamesPlayed = gamesPlayed;}
    public void addGamesPlayed() {gamesPlayed += 1;}

    public Integer getTotalClues() {return  totalClues;}
    public void setTotalClues(Integer totalClues){ this.totalClues = totalClues;}
    public void addTotalClues() {totalClues += 1;}

    public Integer getGuessesMade() {return  guessesMade;}
    public void setGuessesMade(Integer guessesMade){ this.guessesMade = guessesMade;}
    public void addGuessesMade() {guessesMade += 1;}

    public Integer getGuessesCorrect() {return  guessesCorrect;}
    public void setGuessesCorrect(Integer guessesCorrect){ this.guessesCorrect = guessesCorrect;}
    public void addGuessesCorrect() {guessesCorrect += 1;}

    public Integer getInvalidClues() {return  invalidClues;}
    public void setInvalidClues(Integer invalidClues){ this.invalidClues = invalidClues;}
    public void addInvalidClues() {invalidClues += 1;}

    public Integer getDuplicateClues() {return  duplicateClues;}
    public void setDuplicateClues(Integer duplicateClues){ this.duplicateClues = duplicateClues;}
    public void addDuplicateClues() {duplicateClues += 1;}

    public Integer getTotalScore() {return  totalScore;}
    public void setTotalScore(Integer totalScore){ this.totalScore = totalScore;}
    public void addTotalScore(Integer score) {totalScore += score;}

    @JsonIgnore
    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    @JsonIgnore
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

}
