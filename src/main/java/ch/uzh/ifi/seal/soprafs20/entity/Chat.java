package ch.uzh.ifi.seal.soprafs20.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Chat {

    @Id
    private String lobbyToken;

    @Column
    private boolean active;

    @OneToMany()
    private List<Message> messages = new LinkedList<Message>();

    @OneToMany()
    private List<User> loggedInUsers = new LinkedList<User>();

    public List<User> getUserLoggedIn() {
        return loggedInUsers;
    }

    public void addUser(User user) {
        this.loggedInUsers.add(user);
    }

    public void removeUser(User user) {
        this.loggedInUsers.remove(user);
    }

    public void addMessage(Message message){
        this.messages.add(message);
    }

    public List<Message> getMessages() {
        return messages;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setLobbyToken(String lobbyToken) {
        this.lobbyToken = lobbyToken;
    }

    public String getLobbyToken() {
        return lobbyToken;
    }
}
