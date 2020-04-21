package ch.uzh.ifi.seal.soprafs20.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Message implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String message;

    private String userToken;

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
