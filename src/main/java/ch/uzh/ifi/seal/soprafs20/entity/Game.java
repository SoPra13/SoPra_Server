package ch.uzh.ifi.seal.soprafs20.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "GAME")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Integer currentRound;

    @Column(nullable = false)
    private Integer guesser;

    @Column
    private String topic;

    @Column
    private Boolean guessCorrect;

    @Column
    private Boolean guessGiven;

    @ElementCollection
    private List<String> mysteryWords = new ArrayList<String>();

    @ElementCollection
    private List<Integer> voteList = new ArrayList<>();

    @ElementCollection
    private List<String> clueList = new ArrayList<String>();

    @ElementCollection
    private List<String> checkList = new ArrayList<String>();

    @OneToMany(mappedBy = "game")
    @JsonBackReference
    private List<User> playerList = new ArrayList<User>();

    @OneToMany(mappedBy = "game")
    @JsonBackReference
    private List<Bot> botList = new ArrayList<Bot>();


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

    public void setClueList(List clueList) {
       this.clueList = clueList;
    }

    public List getChecklist() { return checkList; }

    public void setCheckList(List checkList) {
        this.checkList = checkList;
    }

    public List getVoteList() { return voteList;}

    public void setVoteList(List voteList) {
        this.voteList = voteList;
    }

     public List getMysteryWords() { return mysteryWords; }

     public void setMysteryWords(List mysteryWords) {
     this.mysteryWords = mysteryWords;
     }

     public List<User> getPlayerList() {
        return playerList;
     }

     public void removePlayer(User user){playerList.remove(user);}

     public void setPlayerList(List playerList) {
         this.playerList = playerList;
     }

     public List<Bot> getBotList() {
        return botList;
     }

     public void setBotList(List botList) {
        this.botList = botList;
     }

    public void removePlayer(Bot bot){botList.remove(bot);}

    public Boolean getGuessCorrect() {return guessCorrect;}

    public void setGuessCorrect(Boolean guessCorrect) {this.guessCorrect = guessCorrect;}

    public Boolean getGuessGiven() {
        return guessGiven;
    }

    public void setGuessGiven(Boolean guessGiven) {
        this.guessGiven = guessGiven;
    }

}
