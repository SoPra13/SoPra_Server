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
    private Integer round;

    @Column(nullable = false)
    private Integer guesser;

    @ElementCollection
    private List<String> guessList = new ArrayList<>();

    @ElementCollection
    private List<String> mysteryWords = new ArrayList<>();

    @ElementCollection
    private List<Integer> voteList = new ArrayList<>();

    @OneToMany(mappedBy = "game")
    @JsonBackReference
    private List<User> playerList = new ArrayList<>();

    @OneToMany(mappedBy = "game")
    @JsonBackReference
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

    public Integer getRound() {return round;}

    public void setRound(Integer round) {this.round = round;}

    public List<String> getGuessList() { return guessList; }

    public void setGuessList(ArrayList<String> guessList) {
       this.guessList = guessList;
    }

    public List<Integer> getVoteList() { return voteList;}

    public void setVoteList(List<Integer> voteList) {
        this.voteList = voteList;
    }

     public List<String> getMysteryWords() { return mysteryWords; }

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


}
